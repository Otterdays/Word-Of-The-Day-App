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
