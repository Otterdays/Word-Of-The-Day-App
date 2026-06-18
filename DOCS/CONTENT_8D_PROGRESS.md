<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Content progress — Roadmap §8d (MVP volume)

Targets are defined in [ROADMAP.md](./ROADMAP.md) §**8d**: **~30 words per (grade file × MVP category)** across **15 grades × 6 MVP categories** (GENERAL, TECH, SPORTS, FOOD, SCIENCE, ANIMALS). Words may carry **multiple** categories; inventory **counts** each word once per MVP tag present.

## Tooling

| Script | Purpose |
| --- | --- |
| [scripts/inventory_word_assets.py](../scripts/inventory_word_assets.py) | Prints markdown-friendly coverage matrix + gap sum vs target **30** per cell; detects duplicate lemmas **within** the same grade file. |
| [scripts/ensure_general_category.py](../scripts/ensure_general_category.py) | One-time / maintenance helper: appends **GENERAL** when missing so GENERAL-only picker selections surface vocabulary rows. |
| [scripts/fill_word_gaps.py](../scripts/fill_word_gaps.py) | Fills each (grade × MVP category) to **30** from tiered **`scripts/corpus/lemma_banks.py`**; idempotent (skips duplicate lemmas). |
| [scripts/import_open_lexicon.py](../scripts/import_open_lexicon.py) | Builds **`assets/lexicon/`** from NLTK WordNet + **`scripts/corpus/import_packs_data.py`** (myth, sacred, literary packs). |

Run from repo root (UTF-8 console recommended on Windows: `python -X utf8 scripts/inventory_word_assets.py`).

## Snapshot — `[2026-06-17]` — **Open lexicon** (opt-in, `0.2.3`)

Supplemental vocabulary **merged at runtime** when enabled in **Settings → Extended sources**. Not counted in §8d MVP matrix (curated `assets/words/` only).

| Pack | Path | Rows | License / basis |
| --- | --- | --- | --- |
| WordNet | `assets/lexicon/wordnet/<grade>.json` | **10,351** | Princeton WordNet (NLTK import) |
| Myth & lore | `assets/lexicon/packs/mythology.json` | **62** | PD mythology + curated editorial |
| Sacred reference | `assets/lexicon/packs/sacred_reference.json` | **28** | PD scripture vocabulary (KJV-era examples) |
| Literary & historical | `assets/lexicon/packs/literary_historical.json` | **28** | PD literature/history; teen/adult ratings |

- **Regenerate:** `python -X utf8 scripts/import_open_lexicon.py` (requires `pip install nltk`)
- **Policy:** [CONTENT_SOURCES.md](./CONTENT_SOURCES.md)
- **Loader:** `JsonWordDataSource` + `LexiconPreferences` DataStore keys in `UserPreferencesRepository`

**Combined ceiling (all packs on, Adult grade):** ~**22,100+** distinct lemmas (11,667 curated + ~10.5k WordNet + packs; deduped by lemma at merge).

## Snapshot — `[2026-06-17]` — **Mega corpus** (60 × 13 × 15)

- **Total word rows:** **11,667** (**+9,449** vs **2,218** / **+9,449** vs **171** baseline)
- **Target:** **60** per (grade × category) for **13** non-GENERAL categories
- **Gap score:** **0**
- **Tooling:** `fill_word_gaps.py --target 60`, `lemma_banks.py`, `extended_lexicons.py`

## Snapshot — `[2026-06-17]` — MVP matrix **COMPLETE** (30 × 6)

- **Total word rows (all grade files):** **2,218** (**+2,047** vs prior **171**)
- **Gap score:** **0** (all **90** MVP cells at **30**)
- **Wave tooling:** `scripts/fill_word_gaps.py` + `scripts/corpus/lemma_banks.py`
- **Per-grade row counts:** ~141–152 rows/file (GENERAL column higher because every row tags GENERAL)

Run `python -X utf8 scripts/inventory_word_assets.py` to regenerate the matrix.

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
