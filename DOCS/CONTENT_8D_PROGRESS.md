<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Content progress — Roadmap §8d (MVP volume)

Targets are defined in [ROADMAP.md](./ROADMAP.md) §**8d**: **~30 words per (grade file × MVP category)** across **15 grades × 6 MVP categories** (GENERAL, TECH, SPORTS, FOOD, SCIENCE, ANIMALS). Words may carry **multiple** categories; inventory **counts** each word once per MVP tag present.

## Tooling

| Script | Purpose |
| --- | --- |
| [scripts/inventory_word_assets.py](../scripts/inventory_word_assets.py) | Prints markdown-friendly coverage matrix + gap sum vs target **30** per cell; detects duplicate lemmas **within** the same grade file. |
| [scripts/ensure_general_category.py](../scripts/ensure_general_category.py) | One-time / maintenance helper: appends **GENERAL** when missing so GENERAL-only picker selections surface vocabulary rows. |

Run from repo root (UTF-8 console recommended on Windows: `python -X utf8 scripts/inventory_word_assets.py`).

## Snapshot — `[2026-04-29]`

- **Total word rows (all grade files):** 89  
- **Gap score:** sum over all MVP cells of `max(0, 30 − count)` = **2509** (all **90** cells below target)  
- **Pre-K TECH gap closed:** added **screen**, **plug** (`pre_k.json`) so TECH column is no longer empty at earliest grade.

### Coverage matrix (counts)

| Grade file | GENERAL | TECH | SPORTS | FOOD | SCIENCE | ANIMALS | Min | Max |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `pre_k.json` | 6 | 2 | 1 | 1 | 1 | 1 | 1 | 6 |
| `kindergarten.json` | 5 | 1 | 1 | 1 | 1 | 1 | 1 | 5 |
| `grade_1.json` | 5 | 1 | 1 | 1 | 1 | 1 | 1 | 5 |
| `grade_2.json` | 5 | 1 | 1 | 1 | 1 | 1 | 1 | 5 |
| `grade_3.json` | 6 | 1 | 1 | 1 | 2 | 1 | 1 | 6 |
| `grade_4.json` | 5 | 1 | 1 | 1 | 2 | 1 | 1 | 5 |
| `grade_5.json` | 5 | 1 | 1 | 1 | 2 | 1 | 1 | 5 |
| `grade_6.json` | 5 | 1 | 2 | 1 | 4 | 1 | 1 | 5 |
| `grade_7.json` | 5 | 1 | 1 | 1 | 4 | 1 | 1 | 5 |
| `grade_8.json` | 5 | 1 | 1 | 1 | 3 | 1 | 1 | 5 |
| `grade_9.json` | 6 | 1 | 1 | 1 | 3 | 1 | 1 | 6 |
| `grade_10.json` | 5 | 1 | 1 | 1 | 1 | 1 | 1 | 5 |
| `grade_11.json` | 5 | 1 | 1 | 1 | 3 | 1 | 1 | 5 |
| `grade_12.json` | 5 | 1 | 1 | 1 | 2 | 1 | 1 | 5 |
| `adult.json` | 16 | 2 | 3 | 2 | 5 | 2 | 2 | 16 |

## Next waves (suggested)

1. **Balanced waves:** add lemmas grade-by-grade, rotating MVP tags toward **~30 per cell**; keep definitions age-aligned per §**8e**.  
2. **QA:** run inventory after each wave; fix duplicate lemmas within a grade file; spot-check reading level.  
3. **Optional:** automate CSV import / lint for schema → JSON when corpus editors prefer spreadsheets.
