<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Content sources & licensing

What the Word of the Day app can ship legally, and how extended lexicon packs work.

## What we **cannot** import wholesale

| Source | Why |
| --- | --- |
| Merriam-Webster, Oxford, Collins, etc. | Copyrighted — no bulk redistribution |
| Full Bible / Quran / Torah text dumps | License and store-policy constraints; we use **vocabulary + short PD examples** only |
| Proprietary thesauri (Roget's modern editions, etc.) | Copyrighted |

## What we **do** import (opt-in)

All supplemental content lives under `app/src/main/assets/lexicon/` and is **off by default**. Users enable packs in **Settings → Extended sources**.

| Pack | Path | License / basis | `ContentSource` |
| --- | --- | --- | --- |
| WordNet | `lexicon/wordnet/<grade>.json` | [Princeton WordNet License](https://wordnet.princeton.edu/license-and-commercial-use) | `WORDNET` |
| Myth & lore | `lexicon/packs/mythology.json` | Public-domain mythology summaries (Bulfinch-era framing) + curated editorial | `MYTHOLOGY` |
| Sacred reference | `lexicon/packs/sacred_reference.json` | Vocabulary + short examples from **King James Bible (1611, PD)** | `SACRED_TEXT` |
| Literary & historical | `lexicon/packs/literary_historical.json` | PD literature/history vocabulary; **teen/adult** ratings for grey topics | `LITERARY_HISTORICAL` |

Core curated corpus (`assets/words/*.json`) remains `ContentSource.CURATED`.

## Age gating

- Each `WordEntry` has `contentRating`: `ALL_AGES`, `TEEN`, or `ADULT`.
- `JsonWordDataSource.maxContentRatingForGrade()` caps what younger grades can see.
- Literary/historical pack includes mature historical terms (e.g. inquisition, colonialism) rated `ADULT`.

## Regenerating lexicon assets

```batch
pip install nltk
python -X utf8 scripts/import_open_lexicon.py
```

Then rebuild the app. Word counts are printed to stdout.

## Attribution in UI

Non-curated entries show **Source** on word detail cards (`WordDetailContent`) using `ContentSource.displayLabel` and `attribution`.

## Future (not shipped)

- Play Asset Delivery if WordNet bulk exceeds APK budget
- Per-faith sacred packs with community review
- User locale WordNet (OMW already downloaded by import script)

`[2026-06-17]` Initial policy for open lexicon import pipeline.

---

`[AMENDED 2026-11-04]:` **Mega lexicon (0.4.0).** `lexicon/wordnet/<grade>.json` is now built by
`scripts/build_mega_lexicon.py` (the old Princeton import is behind `--legacy-wordnet`). New
`lexicon/vintage/<grade>.json` pack. All sources are free; the only obligation is attribution,
shown in Settings → Extended sources and on each word's **Source** card.

| Source | Used for | License | Obligation |
| --- | --- | --- | --- |
| [Open English WordNet 2024](https://github.com/globalwordnet/english-wordnet) | Lemmas, definitions, synonyms, topic (`lexfile` → `Category`) | CC BY 4.0 (derived from Princeton WordNet) | Credit line |
| [wordfreq](https://github.com/rspeer/wordfreq) | **Build time only** — commonness (zipf) → grade level | MIT code / CC BY-SA data | None shipped |
| [Wiktionary via kaikki.org](https://kaikki.org/dictionary/English/) | Pronunciation (US enPR/IPA), short etymology, Wiktionary-authored examples (no quotations), vulgar/offensive tags | CC BY-SA 4.0 | Credit line; enriched fields stay CC BY-SA |
| [CMU Pronouncing Dictionary](https://github.com/cmusphinx/cmudict) | Pronunciation fallback (respelling from ARPABET) | BSD-2 | Keep license notice (DOCS/licenses/) |
| [Webster's Revised Unabridged 1913](https://www.gutenberg.org/ebooks/29765) | **Vintage words** pack (rare/obsolete words not in OEWN) | Public domain | None |

**Safety:** words whose first Wiktionary sense is tagged vulgar/offensive/slur/derogatory are dropped;
any other flagged sense or a mature-topic definition → `contentRating = ADULT` and grade ≥ 9th.
Webster entries with 1913-era racial language are excluded.

**Rebuild:** see the docstring in `scripts/build_mega_lexicon.py` (downloads ~580 MB once; not committed).
