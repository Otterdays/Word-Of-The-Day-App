#!/usr/bin/env python3
"""Ensure each word row tags GENERAL when absent — boosts GENERAL-only picker coverage for §8d."""
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "app" / "src" / "main" / "assets" / "words"

STEMS = [
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


def main() -> None:
    for stem in STEMS:
        path = ASSETS / f"{stem}.json"
        data = json.loads(path.read_text(encoding="utf-8"))
        changed = False
        for item in data:
            cats = list(item.get("categories") or [])
            if "GENERAL" not in cats:
                cats.append("GENERAL")
                item["categories"] = cats
                changed = True
        if changed:
            path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
