#!/usr/bin/env python3
"""Inventory bundled word JSON for Roadmap §8d MVP targets (6 categories × 15 grades).

Run from repo root:
  python scripts/inventory_word_assets.py

Reads app/src/main/assets/words/*.json and prints coverage vs target words-per-combo (default 30).
"""
from __future__ import annotations

import json
import sys
from pathlib import Path

MVP_CATEGORIES = ("GENERAL", "TECH", "SPORTS", "FOOD", "SCIENCE", "ANIMALS")

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


def load_words(assets_dir: Path) -> tuple[dict[str, list[dict]], dict[str, set[str]]]:
    """Returns entries per file stem, and duplicate lemmas per stem (case-folded)."""
    by_file: dict[str, list[dict]] = {}
    dups: dict[str, set[str]] = {}
    for stem in FILE_ORDER:
        path = assets_dir / f"{stem}.json"
        if not path.exists():
            continue
        raw = json.loads(path.read_text(encoding="utf-8"))
        by_file[stem] = raw
        seen: dict[str, int] = {}
        dup_set: set[str] = set()
        for item in raw:
            w = str(item.get("word", "")).strip().casefold()
            if not w:
                continue
            seen[w] = seen.get(w, 0) + 1
            if seen[w] > 1:
                dup_set.add(w)
        if dup_set:
            dups[stem] = dup_set
    return by_file, dups


def coverage_matrix(by_file: dict[str, list[dict]]) -> dict[tuple[str, str], int]:
    counts: dict[tuple[str, str], int] = {}
    for stem in FILE_ORDER:
        rows = by_file.get(stem) or []
        for item in rows:
            cats = item.get("categories") or []
            # Normalize to strings (serialization names)
            cat_names = {str(c).upper() for c in cats}
            for mvp in MVP_CATEGORIES:
                if mvp in cat_names:
                    key = (stem, mvp)
                    counts[key] = counts.get(key, 0) + 1
    return counts


def main() -> int:
    root = Path(__file__).resolve().parents[1]
    assets = root / "app" / "src" / "main" / "assets" / "words"
    if not assets.is_dir():
        print(f"Missing assets dir: {assets}", file=sys.stderr)
        return 1

    by_file, dups = load_words(assets)
    matrix = coverage_matrix(by_file)

    target = 30
    total_words = sum(len(by_file.get(s, [])) for s in FILE_ORDER)

    print(f"# Section 8d inventory — MVP categories x grade files\n")
    print(f"- **Assets path:** `{assets.relative_to(root)}`")
    print(f"- **Total word rows (all grades):** {total_words}")
    print(f"- **Target per (grade file × MVP category):** {target}")
    print()

    if dups:
        print("## Duplicate lemmas within same grade file\n")
        for stem in sorted(dups.keys()):
            print(f"- `{stem}.json`: {', '.join(sorted(dups[stem]))}")
        print()

    # Markdown table: rows = grade files, cols = MVP categories
    header = "| Grade file | " + " | ".join(MVP_CATEGORIES) + " | Min | Max |"
    sep = "| --- |" + " --- |" * (len(MVP_CATEGORIES) + 2)
    print("## Coverage matrix (counts)\n")
    print(header)
    print(sep)

    gaps_total = 0
    for stem in FILE_ORDER:
        row = [f"`{stem}.json`"]
        row_counts = []
        for mvp in MVP_CATEGORIES:
            n = matrix.get((stem, mvp), 0)
            row.append(str(n))
            row_counts.append(n)
            gaps_total += max(0, target - n)
        row.append(str(min(row_counts) if row_counts else 0))
        row.append(str(max(row_counts) if row_counts else 0))
        print("| " + " | ".join(row) + " |")

    print()
    print(f"- **Sum of gaps to {target} per cell** (sum of max(0, {target}-count)): **{gaps_total}**")
    combos_below = sum(
        1 for stem in FILE_ORDER for mvp in MVP_CATEGORIES if matrix.get((stem, mvp), 0) < target
    )
    print(f"- **Combinations below target:** {combos_below} / {len(FILE_ORDER) * len(MVP_CATEGORIES)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
