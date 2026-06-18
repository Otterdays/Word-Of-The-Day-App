#!/usr/bin/env python3
"""Fill category gaps in per-grade word JSON from curated lemma banks.

Run from repo root:
  python -X utf8 scripts/fill_word_gaps.py
  python -X utf8 scripts/fill_word_gaps.py --target 60

Reads app/src/main/assets/words/*.json, merges tier-appropriate rows from
scripts/corpus/lemma_banks.py until each (grade stem × category) reaches
the target (default 60). Skips duplicate lemmas within a grade file.
"""
from __future__ import annotations

import argparse
import json
import os
import sys
from pathlib import Path

_SCRIPTS_DIR = Path(__file__).resolve().parent
if str(_SCRIPTS_DIR) not in sys.path:
    sys.path.insert(0, str(_SCRIPTS_DIR))

from corpus.lemma_banks import LEMMA_BANKS, MVP_FILL_CATEGORIES  # noqa: E402

FILE_ORDER = [
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

STEM_TO_TIER: dict[str, int] = {
    "pre_k": 0,
    "kindergarten": 0,
    "grade_1": 1,
    "grade_2": 1,
    "grade_3": 2,
    "grade_4": 2,
    "grade_5": 3,
    "grade_6": 3,
    "grade_7": 4,
    "grade_8": 4,
    "grade_9": 5,
    "grade_10": 5,
    "grade_11": 6,
    "grade_12": 6,
    "adult": 7,
}

DEFAULT_TARGET = int(os.environ.get("WOTD_FILL_TARGET", "60"))
TIER_SYNONYM_MIN = 4


def parse_args() -> argparse.Namespace:
    p = argparse.ArgumentParser(description="Fill word JSON gaps from lemma banks.")
    p.add_argument(
        "--target",
        type=int,
        default=DEFAULT_TARGET,
        help=f"Words per (grade file × category); default {DEFAULT_TARGET}",
    )
    return p.parse_args()


def load_json(path: Path) -> list[dict]:
    return json.loads(path.read_text(encoding="utf-8"))


def write_json(path: Path, rows: list[dict]) -> None:
    path.write_text(
        json.dumps(rows, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )


def category_counts(rows: list[dict]) -> dict[str, int]:
    counts = {cat: 0 for cat in MVP_FILL_CATEGORIES}
    for item in rows:
        cats = {str(c).upper() for c in (item.get("categories") or [])}
        for cat in MVP_FILL_CATEGORIES:
            if cat in cats:
                counts[cat] += 1
    return counts


def existing_lemmas(rows: list[dict]) -> set[str]:
    return {str(item.get("word", "")).strip().casefold() for item in rows if item.get("word")}


def build_entry(template: dict, category: str, tier: int) -> dict:
    entry: dict = {
        "word": template["word"],
        "partOfSpeech": template["partOfSpeech"],
        "pronunciation": template.get("pronunciation", ""),
        "definition": template["definition"],
        "example": template.get("example", ""),
        "categories": [category, "GENERAL"],
    }
    if tier >= TIER_SYNONYM_MIN:
        syns = template.get("synonyms") or []
        if syns:
            entry["synonyms"] = list(syns)
        note = template.get("usageNote", "")
        if note:
            entry["usageNote"] = note
        etym = template.get("etymology", "")
        if etym:
            entry["etymology"] = etym
    return entry


def fill_stem(
    stem: str,
    rows: list[dict],
    target: int,
) -> tuple[list[dict], int, list[str]]:
    tier = STEM_TO_TIER[stem]
    lemmas = existing_lemmas(rows)
    counts = category_counts(rows)
    added = 0
    errors: list[str] = []

    for category in MVP_FILL_CATEGORIES:
        need = max(0, target - counts.get(category, 0))
        if need == 0:
            continue

        pool = LEMMA_BANKS.get(category, {}).get(tier, [])
        if not pool:
            errors.append(f"{stem}.json / {category}: empty tier-{tier} bank")
            continue

        placed = 0
        idx = 0
        passes = 0
        max_passes = len(pool) + 2
        while placed < need and passes < max_passes:
            template = pool[idx % len(pool)]
            idx += 1
            if idx % len(pool) == 0:
                passes += 1
            word_key = str(template["word"]).strip().casefold()
            if not word_key or word_key in lemmas:
                continue
            rows.append(build_entry(template, category, tier))
            lemmas.add(word_key)
            counts[category] += 1
            placed += 1
            added += 1

        if placed < need:
            errors.append(
                f"{stem}.json / {category}: added {placed}, still short "
                f"{need - placed} (tier {tier} pool size {len(pool)})"
            )

    return rows, added, errors


def inventory_gap_sum(by_file: dict[str, list[dict]], target: int) -> int:
    total = 0
    for stem in FILE_ORDER:
        counts = category_counts(by_file.get(stem, []))
        for cat in MVP_FILL_CATEGORIES:
            total += max(0, target - counts.get(cat, 0))
    return total


def main() -> int:
    args = parse_args()
    target = args.target

    root = Path(__file__).resolve().parents[1]
    assets = root / "app" / "src" / "main" / "assets" / "words"
    if not assets.is_dir():
        print(f"Missing assets dir: {assets}", file=sys.stderr)
        return 1

    all_errors: list[str] = []
    added_by_file: dict[str, int] = {}
    by_file: dict[str, list[dict]] = {}

    print("# fill_word_gaps — category volume fill\n")
    print(f"- Assets: `{assets.relative_to(root)}`")
    print(f"- Target per (stem × category): {target}")
    print(f"- Categories ({len(MVP_FILL_CATEGORIES)}): {', '.join(MVP_FILL_CATEGORIES)}\n")

    for stem in FILE_ORDER:
        path = assets / f"{stem}.json"
        if not path.exists():
            all_errors.append(f"Missing file: {path.name}")
            continue
        rows = load_json(path)
        before = len(rows)
        rows, added, errs = fill_stem(stem, rows, target)
        if added:
            write_json(path, rows)
        added_by_file[stem] = added
        by_file[stem] = rows
        all_errors.extend(errs)
        print(f"- `{stem}.json`: +{added} rows (was {before}, now {len(rows)})")

    total_rows = sum(len(by_file.get(s, [])) for s in FILE_ORDER)
    gap_sum = inventory_gap_sum(by_file, target)

    print(f"\n## Totals\n")
    print(f"- Words added this run: {sum(added_by_file.values())}")
    print(f"- Final word rows (all grades): {total_rows}")
    print(f"- Gap sum vs {target} per cell: {gap_sum}")

    if all_errors:
        print(f"\n## Errors / warnings ({len(all_errors)})\n")
        for err in all_errors[:40]:
            print(f"- {err}")
        if len(all_errors) > 40:
            print(f"- ... and {len(all_errors) - 40} more")
        return 1 if gap_sum > 0 else 0

    if gap_sum > 0:
        print("\n## Warning\n")
        print(f"- Gap sum still {gap_sum}; expand lemma banks or resolve duplicates.")
        return 1

    print("\n- All category cells at or above target.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
