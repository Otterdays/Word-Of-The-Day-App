#!/usr/bin/env python3
"""Generate optional lexicon assets: WordNet (dictionary + thesaurus) and curated packs.

Run from repo root:
  python -X utf8 scripts/import_open_lexicon.py

Requires: pip install nltk
First run downloads WordNet via NLTK (Princeton WordNet license).

Outputs:
  app/src/main/assets/lexicon/wordnet/<grade>.json
  app/src/main/assets/lexicon/packs/mythology.json
  app/src/main/assets/lexicon/packs/sacred_reference.json
  app/src/main/assets/lexicon/packs/literary_historical.json
"""
from __future__ import annotations

import json
import sys
from pathlib import Path

_SCRIPTS_DIR = Path(__file__).resolve().parent
if str(_SCRIPTS_DIR) not in sys.path:
    sys.path.insert(0, str(_SCRIPTS_DIR))

from corpus.import_packs_data import (  # noqa: E402
    LITERARY_HISTORICAL_PACK,
    LITERARY_LEMMAS,
    MYTH_LEMMAS,
    MYTHOLOGY_PACK,
    SACRED_LEMMAS,
    SACRED_REFERENCE_PACK,
)

REPO_ROOT = _SCRIPTS_DIR.parent
LEXICON_DIR = REPO_ROOT / "app" / "src" / "main" / "assets" / "lexicon"
WORDNET_DIR = LEXICON_DIR / "wordnet"
PACKS_DIR = LEXICON_DIR / "packs"

GRADE_FILES = [
    "pre_k",
    "kindergarten",
    "grade_1",
    "grade_2",
    "grade_3",
    "grade_4",
    "grade_5",
    "grade_6",
    "grade_7",
    "grade_8",
    "grade_9",
    "grade_10",
    "grade_11",
    "grade_12",
    "adult",
]

# Per-grade WordNet row caps (keeps APK reasonable; user opts in via Settings)
GRADE_LIMITS: dict[str, int] = {
    "pre_k": 200,
    "kindergarten": 200,
    "grade_1": 350,
    "grade_2": 350,
    "grade_3": 500,
    "grade_4": 500,
    "grade_5": 700,
    "grade_6": 700,
    "grade_7": 900,
    "grade_8": 900,
    "grade_9": 1100,
    "grade_10": 1100,
    "grade_11": 1300,
    "grade_12": 1300,
    "adult": 2000,
}

WN_POS = {
    "n": "noun",
    "v": "verb",
    "a": "adjective",
    "s": "adjective",
    "r": "adverb",
}

GRADE_ENUM = {
    "pre_k": "PRE_K",
    "kindergarten": "KINDERGARTEN",
    "grade_1": "GRADE_1",
    "grade_2": "GRADE_2",
    "grade_3": "GRADE_3",
    "grade_4": "GRADE_4",
    "grade_5": "GRADE_5",
    "grade_6": "GRADE_6",
    "grade_7": "GRADE_7",
    "grade_8": "GRADE_8",
    "grade_9": "GRADE_9",
    "grade_10": "GRADE_10",
    "grade_11": "GRADE_11",
    "grade_12": "GRADE_12",
    "adult": "ADULT",
}


def ensure_nltk_wordnet() -> None:
    try:
        import nltk  # noqa: PLC0415
        from nltk.corpus import wordnet as wn  # noqa: PLC0415
    except ImportError as exc:
        raise SystemExit(
            "NLTK is required. Install with: pip install nltk"
        ) from exc
    try:
        wn.synsets("dog")
    except LookupError:
        import nltk  # noqa: PLC0415

        nltk.download("wordnet", quiet=True)
        nltk.download("omw-1.4", quiet=True)


def complexity_score(word: str, definition: str) -> int:
    letters = len(word.replace(" ", ""))
    words_in_def = len(definition.split())
    return letters + words_in_def * 3


def grade_stem_for_score(score: int) -> str:
    if score < 14:
        return "pre_k"
    if score < 18:
        return "kindergarten"
    if score < 22:
        return "grade_1"
    if score < 26:
        return "grade_2"
    if score < 32:
        return "grade_3"
    if score < 38:
        return "grade_4"
    if score < 44:
        return "grade_5"
    if score < 50:
        return "grade_6"
    if score < 58:
        return "grade_7"
    if score < 66:
        return "grade_8"
    if score < 76:
        return "grade_9"
    if score < 88:
        return "grade_10"
    if score < 102:
        return "grade_11"
    if score < 120:
        return "grade_12"
    return "adult"


def content_rating_for_stem(stem: str) -> str:
    if stem in ("pre_k", "kindergarten", "grade_1", "grade_2"):
        return "ALL_AGES"
    if stem in ("grade_3", "grade_4", "grade_5", "grade_6", "grade_7", "grade_8"):
        return "TEEN"
    return "ADULT"


def capitalize_word(w: str) -> str:
    if not w:
        return w
    if w.isupper() and len(w) <= 4:
        return w
    return w[0].upper() + w[1:].lower() if len(w) > 1 else w.upper()


def build_wordnet_entries() -> dict[str, list[dict]]:
    from nltk.corpus import wordnet as wn  # noqa: PLC0415

    seen: set[str] = set()
    buckets: dict[str, list[tuple[int, dict]]] = {g: [] for g in GRADE_FILES}

    for synset in wn.all_synsets():
        pos = WN_POS.get(synset.pos())
        if not pos:
            continue
        definition = synset.definition().strip()
        if not definition or len(definition) > 220:
            continue
        for lemma in synset.lemmas():
            raw = lemma.name().replace("_", " ").lower()
            if not all(c.isalpha() or c == " " for c in raw):
                continue
            if raw in seen:
                continue
            if len(raw) > 32:
                continue
            seen.add(raw)
            score = complexity_score(raw, definition)
            stem = grade_stem_for_score(score)
            syns = sorted(
                {
                    l.name().replace("_", " ")
                    for l in synset.lemmas()
                    if l.name().replace("_", " ").lower() != raw
                }
            )[:6]
            entry = {
                "word": capitalize_word(raw),
                "partOfSpeech": pos,
                "definition": definition[0].upper() + definition[1:] if definition else definition,
                "example": "",
                "synonyms": [capitalize_word(s) for s in syns],
                "usageNote": "Imported from Princeton WordNet (opt-in).",
                "gradeLevel": GRADE_ENUM[stem],
                "categories": ["GENERAL"],
                "source": "WORDNET",
                "contentRating": content_rating_for_stem(stem),
            }
            buckets[stem].append((score, entry))

    out: dict[str, list[dict]] = {}
    for stem in GRADE_FILES:
        limit = GRADE_LIMITS[stem]
        rows = sorted(buckets[stem], key=lambda t: t[0])
        out[stem] = [e for _, e in rows[:limit]]
    return out


def pack_entry(
    word: str,
    pos: str,
    definition: str,
    *,
    example: str = "",
    categories: list[str] | None = None,
    grade_stem: str = "GRADE_6",
    content_rating: str = "TEEN",
    source: str,
    synonyms: list[str] | None = None,
    usage_note: str = "",
    etymology: str = "",
) -> dict:
    return {
        "word": word,
        "partOfSpeech": pos,
        "definition": definition,
        "example": example,
        "etymology": etymology,
        "synonyms": synonyms or [],
        "usageNote": usage_note,
        "gradeLevel": GRADE_ENUM.get(grade_stem, grade_stem),
        "categories": categories or ["GENERAL"],
        "source": source,
        "contentRating": content_rating,
    }


def expand_mythology_pack() -> list[dict]:
    rows = [dict(r) for r in MYTHOLOGY_PACK]
    for r in rows:
        r.setdefault("source", "MYTHOLOGY")
    existing = {r["word"].lower() for r in rows}
    for word, pos in MYTH_LEMMAS:
        key = word.lower()
        if key in existing:
            continue
        existing.add(key)
        rows.append(
            pack_entry(
                word,
                pos,
                f"A figure or concept from classical mythology and legend ({word}).",
                example=f"Scholars still debate the origins of {word} in ancient tales.",
                categories=["MYTHOLOGY", "HISTORY", "GENERAL"],
                grade_stem="GRADE_5",
                content_rating="TEEN",
                source="MYTHOLOGY",
                usage_note="Public-domain mythology reference; enable in Settings → Extended sources.",
            )
        )
    return rows


def expand_sacred_pack() -> list[dict]:
    rows = [dict(r) for r in SACRED_REFERENCE_PACK]
    for r in rows:
        r.setdefault("source", "SACRED_TEXT")
    existing = {r["word"].lower() for r in rows}
    for word, pos, gloss in SACRED_LEMMAS:
        key = word.lower()
        if key in existing:
            continue
        existing.add(key)
        rating = "ADULT" if word in ("blasphemy", "heresy") else "TEEN"
        rows.append(
            pack_entry(
                word.capitalize() if word.islower() else word,
                pos,
                gloss,
                example=f"The term {word} appears across many sacred and historical texts.",
                categories=["SACRED", "GENERAL"],
                grade_stem="GRADE_8",
                content_rating=rating,
                source="SACRED_TEXT",
                usage_note="Vocabulary from public-domain scripture references (KJV 1611).",
            )
        )
    return rows


def expand_literary_pack() -> list[dict]:
    rows = [dict(r) for r in LITERARY_HISTORICAL_PACK]
    for r in rows:
        r.setdefault("source", "LITERARY_HISTORICAL")
    existing = {r["word"].lower() for r in rows}
    grey_terms = {"inquisition", "colonialism", "imperialism", "nihilism", "revisionist"}
    for word, pos, gloss in LITERARY_LEMMAS:
        key = word.lower()
        if key in existing:
            continue
        existing.add(key)
        rating = "ADULT" if key in grey_terms else "TEEN"
        stem = "ADULT" if key in grey_terms else "GRADE_10"
        rows.append(
            pack_entry(
                word.capitalize() if word.islower() else word,
                pos,
                gloss,
                categories=["LITERATURE", "HISTORY", "PHILOSOPHY", "GENERAL"],
                grade_stem=stem,
                content_rating=rating,
                source="LITERARY_HISTORICAL",
                usage_note="Historical and literary vocabulary; mature themes may apply.",
            )
        )
    return rows


def write_json(path: Path, data: list[dict]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(
        json.dumps(data, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )


def main() -> None:
    ensure_nltk_wordnet()
    print("Building WordNet per-grade files…")
    wordnet = build_wordnet_entries()
    wn_total = 0
    for stem, rows in wordnet.items():
        write_json(WORDNET_DIR / f"{stem}.json", rows)
        wn_total += len(rows)
        print(f"  wordnet/{stem}.json — {len(rows)} rows")

    myth = expand_mythology_pack()
    sacred = expand_sacred_pack()
    literary = expand_literary_pack()
    write_json(PACKS_DIR / "mythology.json", myth)
    write_json(PACKS_DIR / "sacred_reference.json", sacred)
    write_json(PACKS_DIR / "literary_historical.json", literary)

    print(f"WordNet total: {wn_total}")
    print(f"mythology.json: {len(myth)}")
    print(f"sacred_reference.json: {len(sacred)}")
    print(f"literary_historical.json: {len(literary)}")
    print("Done — enable packs in Settings → Extended sources.")


if __name__ == "__main__":
    main()
