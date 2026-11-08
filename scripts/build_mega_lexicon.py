#!/usr/bin/env python3
"""Build the big opt-in lexicon from free/open sources.

[TRACE: DOCS/CONTENT_SOURCES.md]

Sources (all free; see DOCS/CONTENT_SOURCES.md for licenses / attribution):
  * Open English WordNet 2024 (CC BY 4.0)            -> words, definitions, synonyms, topics
  * wordfreq (MIT code, CC BY-SA data)               -> build-time only: grade level by commonness
  * Wiktionary via kaikki.org (CC BY-SA 4.0)         -> pronunciation, etymology, examples, safety tags
  * CMU Pronouncing Dictionary (BSD)                 -> pronunciation fallback
  * Webster's Revised Unabridged 1913 (public domain) -> "Vintage words" pack

Usage (from repo root):
  python3 -m venv .venv-lexicon && .venv-lexicon/bin/pip install wordfreq
  .venv-lexicon/bin/python -X utf8 scripts/build_mega_lexicon.py --sources <dir>

<dir> must contain (download once):
  oewn.xml.gz       https://github.com/globalwordnet/english-wordnet/releases/download/2024-edition/english-wordnet-2024.xml.gz
  kaikki.jsonl.gz   https://kaikki.org/dictionary/English/kaikki.org-dictionary-English.jsonl.gz  (~530 MB)
  cmudict.dict      https://raw.githubusercontent.com/cmusphinx/cmudict/master/cmudict.dict
  webster1913.txt   https://www.gutenberg.org/cache/epub/29765/pg29765.txt

Outputs:
  app/src/main/assets/lexicon/wordnet/<grade>.json   (Open dictionary toggle)
  app/src/main/assets/lexicon/vintage/<grade>.json   (Vintage words toggle)
"""
from __future__ import annotations

import argparse
import gzip
import json
import re
import sys
import xml.etree.ElementTree as ET
from collections import defaultdict
from pathlib import Path

from wordfreq import zipf_frequency

REPO = Path(__file__).resolve().parent.parent
ASSETS = REPO / "app" / "src" / "main" / "assets"
OUT_WORDNET = ASSETS / "lexicon" / "wordnet"
OUT_VINTAGE = ASSETS / "lexicon" / "vintage"
CURATED_DIR = ASSETS / "words"

GRADES = [
    "pre_k", "kindergarten", "grade_1", "grade_2", "grade_3", "grade_4", "grade_5",
    "grade_6", "grade_7", "grade_8", "grade_9", "grade_10", "grade_11", "grade_12", "adult",
]
GRADE_ENUM = {g: g.upper() for g in GRADES}
GRADE_ENUM["pre_k"] = "PRE_K"

# Commonness (zipf) -> grade. Common words go young, rare words go old.
ZIPF_STEPS = [
    (5.0, 0), (4.7, 1), (4.4, 2), (4.1, 3), (3.8, 4), (3.5, 5), (3.25, 6), (3.0, 7),
    (2.75, 8), (2.5, 9), (2.25, 10), (2.0, 11), (1.75, 12), (1.5, 13),
]
MATURE_MIN_GRADE = GRADES.index("grade_9")

POS = {"n": "noun", "v": "verb", "a": "adjective", "s": "adjective", "r": "adverb"}
KAIKKI_POS = {"noun": "noun", "verb": "verb", "adj": "adjective", "adv": "adverb"}

LEXFILE_CATEGORY = {
    "noun.animal": "ANIMALS", "noun.plant": "NATURE", "noun.food": "FOOD", "noun.body": "HEALTH",
    "noun.feeling": "EMOTIONS", "verb.emotion": "EMOTIONS", "adj.all": None,
    "noun.phenomenon": "SCIENCE", "noun.substance": "SCIENCE", "noun.shape": "MATH",
    "noun.quantity": "MATH", "noun.location": "WORLD", "noun.object": "NATURE",
    "noun.cognition": "PSYCHOLOGY", "noun.motive": "PSYCHOLOGY", "verb.cognition": "PSYCHOLOGY",
    "noun.group": "CULTURE", "verb.social": "CULTURE", "noun.possession": "FINANCE",
    "verb.possession": "FINANCE", "verb.weather": "WEATHER", "verb.consumption": "FOOD",
    "verb.body": "HEALTH", "verb.competition": "SPORTS", "verb.creation": "ART",
    "noun.communication": "LANGUAGES", "verb.communication": "LANGUAGES",
}

LEMMA_OK = re.compile(r"^[a-z]+(?:[ -][a-z]+)?$")
BORING_DEF = re.compile(
    r"^(?:a |an |any |the )?(?:genus|family|order|class|phylum|subfamily|suborder|division|subclass|"
    r"superfamily|tribe)\b"
    r"|^(?:a |the )?(?:city|town|river|port|capital|region|state|province|island|mountain|lake|county)"
    r" (?:in|of|on)\b"
    r"|^(?:a |the )?(?:former )?(?:unit|monetary unit) of\b",
)
MATURE = re.compile(
    r"\b(?:sex\w*|genital\w*|penis|vagin\w*|vulva|anus|anal|rectum|testic\w*|scrot\w*|breast\w*|nipple|"
    r"orgasm|erotic\w*|porn\w*|prostitut\w*|brothel|masturb\w*|ejacul\w*|semen|copulat\w*|intercourse|"
    r"rape\w*|incest\w*|fec\w*|faec\w*|excrement|urin\w*|buttock\w*|narcotic\w*|heroin|cocaine|"
    r"marijuana|suicid\w*|tortur\w*|murder\w*|kill\w*|execut\w*|slave\w*|nazi\w*|racis\w*|obscen\w*|"
    r"profan\w*|vulgar\w*|offensive\w*|disparag\w*|derogat\w*|contempt\w*|drunk\w*|intoxicat\w*|"
    r"lewd\w*|lust\w*|nude|naked|homosexual\w*|lesbian\w*|abortion\w*|gun|firearm\w*|weapon\w*|"
    r"corpse\w*|death|dead|die|dying)\b",
    re.I,
)
BLOCK_TAGS = {"vulgar", "offensive", "slur", "ethnic-slur", "ethnic", "derogatory", "pejorative", "obscene"}
VINTAGE_BLOCK = re.compile(r"\b(?:negro\w*|savage\w*|heathen\w*|mulatto\w*|hottentot\w*|inferior race\w*)\b", re.I)


def log(*a: object) -> None:
    print(*a, file=sys.stderr, flush=True)


def grade_for(word: str, definition: str, zipf: float) -> int:
    g = 14
    for threshold, idx in ZIPF_STEPS:
        if zipf >= threshold:
            g = idx
            break
    # Keep the youngest grades to short words with short definitions.
    if g < 4 and (len(word) > 8 or len(definition) > 90 or " " in word):
        g += 2
    return min(g, 14)


def base_rating(g: int) -> str:
    if g <= 3:
        return "ALL_AGES"
    if g <= 9:
        return "TEEN"
    return "ADULT"


def cap(s: str) -> str:
    s = s.strip()
    return s[:1].upper() + s[1:] if s else s


def sentence(s: str) -> str:
    s = cap(re.sub(r"\s+", " ", s))
    return s if s.endswith((".", "!", "?")) else s + "."


def load_curated() -> set[str]:
    out: set[str] = set()
    for f in CURATED_DIR.glob("*.json"):
        for row in json.loads(f.read_text(encoding="utf-8")):
            out.add(row["word"].strip().lower())
    return out


# ---------------------------------------------------------------- OEWN
def load_oewn(path: Path) -> dict[str, dict]:
    log("Parsing Open English WordNet…")
    entries: dict[str, list[tuple[str, list[str]]]] = defaultdict(list)  # lemma -> [(pos, [synsetIds])]
    entry_lemma: dict[str, str] = {}
    synsets: dict[str, dict] = {}
    with gzip.open(path, "rb") as fh:
        for _, el in ET.iterparse(fh, events=("end",)):
            tag = el.tag
            if tag == "LexicalEntry":
                lemma_el = el.find("Lemma")
                if lemma_el is not None:
                    form = lemma_el.get("writtenForm", "")
                    entry_lemma[el.get("id", "")] = form
                    senses = [s.get("synset", "") for s in el.findall("Sense")]
                    entries[form].append((lemma_el.get("partOfSpeech", ""), senses))
                el.clear()
            elif tag == "Synset":
                d = el.find("Definition")
                synsets[el.get("id", "")] = {
                    "def": (d.text or "").strip() if d is not None else "",
                    "ex": [(e.text or "").strip() for e in el.findall("Example")],
                    "members": el.get("members", "").split(),
                    "lexfile": el.get("lexfile", ""),
                }
                el.clear()
    out: dict[str, dict] = {}
    for form, alts in entries.items():
        if not LEMMA_OK.match(form) or not 3 <= len(form) <= 22:
            continue  # skips proper nouns (capitalised), symbols, long phrases
        pos, senses = max(alts, key=lambda t: len(t[1]))
        syn = next((synsets[s] for s in senses if s in synsets and synsets[s]["def"]), None)
        if not syn or len(syn["def"]) > 240 or BORING_DEF.search(syn["def"].lower()):
            continue
        synonyms = []
        for m in syn["members"]:
            other = entry_lemma.get(m, "")
            if other and other.lower() != form and LEMMA_OK.match(other.lower()):
                synonyms.append(cap(other))
        stem = form.split()[0][:5]
        example = next((e for e in syn["ex"] if stem in e.lower() and 10 <= len(e) <= 140), "")
        out[form] = {
            "pos": POS.get(pos, "noun"),
            "def": syn["def"],
            "ex": example,
            "syn": synonyms[:6],
            "cat": LEXFILE_CATEGORY.get(syn["lexfile"]),
            "senses": len(senses),
        }
    log(f"  {len(out):,} usable OEWN lemmas")
    return out


# ---------------------------------------------------------------- Wiktionary
def short_etymology(text: str) -> str:
    if not text:
        return ""
    lines = [ln.strip() for ln in text.split("\n") if ln.strip()]
    if lines and lines[0].lower().startswith("etymology tree"):
        prose = [ln for ln in lines if len(ln) > 40 or ln.startswith("From")]
        lines = prose[-1:] if prose else []
    t = " ".join(lines)
    t = re.sub(r"\s+", " ", t)
    parts = re.split(r"(?<=[.;])\s", t)
    out = ""
    for p in parts:
        if len(out) + len(p) > 220:
            break
        out = (out + " " + p).strip()
    return out if 12 <= len(out) <= 240 else ""


def load_kaikki(path: Path, wanted: set[str]) -> dict[str, dict]:
    log("Streaming Wiktionary (kaikki.org) — this takes a while…")
    info: dict[str, dict] = {}
    n = 0
    with gzip.open(path, "rt", encoding="utf-8") as fh:
        for line in fh:
            n += 1
            if n % 200_000 == 0:
                log(f"  {n:,} lines, {len(info):,} matched")
            # cheap prefilter before json parse
            m = re.search(r'"word": "([^"]{3,22})"', line)
            if not m or m.group(1).lower() not in wanted:
                continue
            try:
                d = json.loads(line)
            except ValueError:
                continue
            if d.get("lang_code") != "en":
                continue
            word = d["word"]
            if word != word.lower():
                continue
            rec = info.setdefault(word, {"ipa": "", "enpr": "", "ety": "", "ex": {}, "block": False, "mature": False})
            for s in d.get("sounds", []):
                tags = set(s.get("tags", []))
                if s.get("enpr") and not rec["enpr"]:
                    rec["enpr"] = s["enpr"]
                if s.get("ipa") and s["ipa"].startswith("/"):
                    if "US" in tags or "General-American" in tags or not rec["ipa"]:
                        if not rec["ipa"] or "US" in tags or "General-American" in tags:
                            rec["ipa"] = s["ipa"]
            if not rec["ety"]:
                rec["ety"] = short_etymology(d.get("etymology_text", ""))
            pos = KAIKKI_POS.get(d.get("pos", ""))
            senses = d.get("senses", [])
            for i, s in enumerate(senses):
                tags = set(s.get("tags", [])) | {t.lower() for t in s.get("raw_tags", [])}
                if tags & BLOCK_TAGS:
                    if i == 0:
                        rec["block"] = True
                    rec["mature"] = True
                if pos and pos not in rec["ex"]:
                    for e in s.get("examples", []):
                        txt = (e.get("text") or "").strip()
                        if e.get("type", "example") == "example" and 12 <= len(txt) <= 140 and word in txt.lower():
                            rec["ex"][pos] = txt
                            break
    log(f"  done: {n:,} lines, {len(info):,} words enriched")
    return info


# ---------------------------------------------------------------- CMU
ARPA = {
    "AA": "ah", "AE": "a", "AH": "uh", "AO": "aw", "AW": "ow", "AY": "y", "B": "b", "CH": "ch",
    "D": "d", "DH": "th", "EH": "e", "ER": "ur", "EY": "ay", "F": "f", "G": "g", "HH": "h",
    "IH": "i", "IY": "ee", "JH": "j", "K": "k", "L": "l", "M": "m", "N": "n", "NG": "ng",
    "OW": "oh", "OY": "oy", "P": "p", "R": "r", "S": "s", "SH": "sh", "T": "t", "TH": "th",
    "UH": "uu", "UW": "oo", "V": "v", "W": "w", "Y": "y", "Z": "z", "ZH": "zh",
}


def load_cmu(path: Path) -> dict[str, str]:
    """ARPABET -> simple stressed respelling, e.g. 'ser-un-DIP-i-tee'."""
    out: dict[str, str] = {}
    for line in path.read_text(encoding="latin-1").splitlines():
        parts = line.split("#")[0].split()
        if len(parts) < 2 or "(" in parts[0]:
            continue
        word, phones = parts[0], parts[1:]
        # Split into syllables: each vowel is a nucleus; between two vowels the last
        # consonant starts the next syllable (simple maximal-onset rule).
        units = [(ph.rstrip("012"), ph[-1] if ph[-1] in "012" else None) for ph in phones]
        vowels = [i for i, (_, st) in enumerate(units) if st is not None]
        if not vowels:
            continue
        bounds = [0]
        for a, b in zip(vowels, vowels[1:]):
            gap = b - a - 1
            bounds.append(b if gap == 0 else b - 1)
        bounds.append(len(units))
        chunks = []
        for si in range(len(vowels)):
            seg = units[bounds[si]:bounds[si + 1]]
            txt = "".join(ARPA.get(p, p.lower()) for p, _ in seg)
            chunks.append(txt.upper() if units[vowels[si]][1] == "1" else txt)
        out[word] = "-".join(chunks)
    return out


# ---------------------------------------------------------------- Webster 1913
W_POS = [(re.compile(r"(?:^|[\s,;])" + re.escape(k) + r"(?=[\s,;]|$)"), v) for k, v in [
    ("adv.", "adverb"), ("prep.", "preposition"), ("interj.", "interjection"), ("v. t.", "verb"),
    ("v. i.", "verb"), ("v.", "verb"), ("n.", "noun"), ("a.", "adjective"),
]]
W_SKIP = re.compile(r"^(?:See |Same as|Alt\.|Obs\. form|Plural of|imp\.|p\. p\.|pl\. of|of |An obsolete form)", re.I)
W_CITE = re.compile(r"(?:\s+(?:[A-Z][a-zA-Z]*\.?\s?){1,4}(?:[A-Z][a-z]+)\.)+\s*$")


def load_webster(path: Path) -> list[dict]:
    log("Parsing Webster 1913…")
    lines = path.read_text(encoding="utf-8", errors="replace").splitlines()
    start = next(i for i, ln in enumerate(lines) if ln.startswith("*** START"))
    end = next(i for i, ln in enumerate(lines) if ln.startswith("*** END"))
    lines = lines[start + 1:end]
    out: list[dict] = []
    seen: set[str] = set()
    i = 0
    head_re = re.compile(r"^[A-Z][A-Z\-']+$")
    while i < len(lines):
        ln = lines[i]
        if not head_re.match(ln) or i + 1 >= len(lines):
            i += 1
            continue
        word = ln.lower()
        j = i + 1
        header = lines[j]
        # collect block until next headword
        k = j + 1
        block: list[str] = []
        while k < len(lines) and not head_re.match(lines[k]):
            block.append(lines[k])
            k += 1
        i = k
        if word in seen or not LEMMA_OK.match(word) or not 4 <= len(word) <= 18:
            continue
        hdr = header
        pos = next((p for rx, p in W_POS if rx.search(hdr.split("Etym:")[0])), "")
        if not pos:
            continue
        ety = ""
        text = " ".join([hdr] + block)
        m = re.search(r"Etym: \[(.+?)\]", text)
        if m:
            ety = re.sub(r"\s+", " ", m.group(1)).strip()
        # paragraphs (after removing the possibly multi-line Etym: [...] span)
        body = re.sub(r"Etym: \[.*?\]", "", "\n".join([hdr] + block), flags=re.S)
        body_lines = body.split("\n")[1:]
        paras, cur = [], []
        for b in body_lines:
            if b.strip():
                cur.append(b.strip())
            elif cur:
                paras.append(" ".join(cur))
                cur = []
        if cur:
            paras.append(" ".join(cur))
        definition = ""
        obsolete = False
        for p in paras:
            p2 = re.sub(r"^(?:Defn:|\d+\.)\s*", "", p).strip()
            p2 = re.sub(r"^\([A-Z][a-z]+\.(?:\s*[A-Z][a-z]+\.)?\)\s*", "", p2)
            if not p2 or p2.startswith("Etym") or len(p2) < 15:
                continue
            obsolete = "[Obs.]" in p2 or "[R.]" in p2 or "[Archaic]" in p2
            p2 = re.sub(r"\[(?:Obs|R|Archaic|Colloq|Prov\. Eng|Poetic)\.?\]", "", p2)
            p2 = re.sub(r"\s+", " ", p2)
            if "]" in p2[:60] and "[" not in p2[:60]:
                continue  # leftover etymology fragment
            # Keep the first sentence only: drops trailing quotations, author citations, "See X".
            p2 = re.split(r"(?<=[a-z\)])\.\s+(?=[A-Z])", p2, maxsplit=1)[0].rstrip(".") + "."
            p2 = W_CITE.sub("", p2).strip()
            p2 = re.split(r'\s+["“]', p2)[0].strip()  # drop inline quotations
            p2 = re.sub(r"(?:\s*\.)+$", "", p2).rstrip(" ;:,") + "."
            p2 = p2.split(" -- ")[0].strip()
            if W_SKIP.match(p2):
                break
            definition = p2.rstrip(";:,")
            break
        if not 15 <= len(definition) <= 220:
            continue
        seen.add(word)
        out.append({"word": word, "pos": pos, "def": definition, "ety": ety[:200], "obs": obsolete})
    log(f"  {len(out):,} Webster headwords with clean definitions")
    return out


# ---------------------------------------------------------------- build
def compact(row: dict) -> dict:
    """Drop empty optional fields — the app's WordEntry has defaults for them."""
    return {k: v for k, v in row.items() if v not in ("", [], None)}


def write(dir_: Path, buckets: dict[str, list[dict]]) -> int:
    dir_.mkdir(parents=True, exist_ok=True)
    total = 0
    for g in GRADES:
        rows = sorted(buckets.get(g, []), key=lambda r: r["word"].lower())
        (dir_ / f"{g}.json").write_text(
            json.dumps(rows, ensure_ascii=False, separators=(",", ":")), encoding="utf-8",
        )
        total += len(rows)
        log(f"  {dir_.name}/{g}.json  {len(rows):>6,}")
    return total


def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--sources", type=Path, required=True)
    ap.add_argument("--vintage-limit", type=int, default=20000)
    args = ap.parse_args()
    src = args.sources

    curated = load_curated()
    oewn = load_oewn(src / "oewn.xml.gz")
    webster = load_webster(src / "webster1913.txt")
    wanted = set(oewn) | {w["word"] for w in webster}
    wik = load_kaikki(src / "kaikki.jsonl.gz", wanted)
    cmu = load_cmu(src / "cmudict.dict")

    def pronunciation(word: str) -> str:
        w = wik.get(word, {})
        if w.get("enpr"):
            return w["enpr"]
        if w.get("ipa"):
            return w["ipa"]
        return cmu.get(word, "")

    # ---- Open dictionary
    buckets: dict[str, list[dict]] = defaultdict(list)
    dropped = 0
    for word, o in oewn.items():
        if word in curated:
            continue
        w = wik.get(word, {})
        if w.get("block"):
            dropped += 1
            continue
        z = zipf_frequency(word, "en")
        if z == 0 and not w:
            continue  # truly obscure: not in frequency lists and no Wiktionary page
        g = grade_for(word, o["def"], z)
        mature = w.get("mature", False) or bool(MATURE.search(o["def"]))
        if mature:
            g = max(g, MATURE_MIN_GRADE)
        cats = ["GENERAL"] + ([o["cat"]] if o["cat"] else [])
        buckets[GRADES[g]].append(compact({
            "word": cap(word),
            "partOfSpeech": o["pos"],
            "pronunciation": pronunciation(word),
            "definition": sentence(o["def"]),
            "example": sentence(o["ex"] or w.get("ex", {}).get(o["pos"], "")) if (o["ex"] or w.get("ex", {}).get(o["pos"])) else "",
            "etymology": w.get("ety", ""),
            "synonyms": o["syn"],
            "gradeLevel": GRADE_ENUM[GRADES[g]],
            "categories": cats,
            "source": "WORDNET",
            "contentRating": "ADULT" if mature else base_rating(g),
        }))
    log(f"Open dictionary: dropped {dropped:,} vulgar/offensive words")
    n_dict = write(OUT_WORDNET, buckets)

    # ---- Vintage (Webster 1913): rarer words the modern dictionary doesn't already cover
    vb: dict[str, list[dict]] = defaultdict(list)
    scored = []
    for e in webster:
        word = e["word"]
        if word in oewn or word in curated:
            continue
        if VINTAGE_BLOCK.search(e["def"]) or wik.get(word, {}).get("block"):
            continue
        z = zipf_frequency(word, "en")
        if z > 3.0:
            continue
        # Prefer words with some modern trace (Wiktionary page or frequency), then obsolete charm.
        score = (1 if word in wik else 0) * 2 + (1 if z > 0 else 0) + (1 if e["obs"] else 0)
        scored.append((score, z, e))
    scored.sort(key=lambda t: (-t[0], -t[1], t[2]["word"]))
    for _, z, e in scored[: args.vintage_limit]:
        word = e["word"]
        g = 10 if z >= 2.0 else (12 if z >= 1.0 else 14)
        mature = bool(MATURE.search(e["def"])) or wik.get(word, {}).get("mature", False)
        vb[GRADES[g]].append(compact({
            "word": cap(word),
            "partOfSpeech": e["pos"],
            "pronunciation": pronunciation(word),
            "definition": sentence(e["def"]),
            "etymology": wik.get(word, {}).get("ety", "") or e["ety"],
            "usageNote": "Marked obsolete or rare in Webster's 1913." if e["obs"] else "From Webster's 1913 dictionary.",
            "gradeLevel": GRADE_ENUM[GRADES[g]],
            "categories": ["GENERAL", "HISTORY", "LANGUAGES"],
            "source": "VINTAGE",
            "contentRating": "ADULT" if mature else "TEEN",
        }))
    n_vin = write(OUT_VINTAGE, vb)
    log(f"\nTOTAL open dictionary: {n_dict:,}   vintage: {n_vin:,}")


if __name__ == "__main__":
    main()
