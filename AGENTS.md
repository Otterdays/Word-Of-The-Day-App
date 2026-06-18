# AGENTS.md — Word of the Day App

Instructions for AI agents and contributors working in this repository.

## Init workflow

1. Read `DOCS/SUMMARY.md` → `DOCS/SBOM.md` → `DOCS/SCRATCHPAD.md` → `DOCS/STYLE_GUIDE.md`
2. For navigation/constants/data locations: read `DOCS/AGENT_WORKMAP.md`
3. For product edition planning: read `DOCS/EDITIONS_ROADMAP.md`
4. Update `DOCS/SCRATCHPAD.md` during work (not only at the end)

## Stack

- Android `:app` — Kotlin **2.3.20**, Jetpack Compose, Material 3, MVVM
- Version catalog: `gradle/libs.versions.toml`
- Words: `app/src/main/assets/words/*.json`

---

## URGENT — Release sync protocol (non-negotiable)

Any change that is **user-facing** or constitutes a **shippable release** MUST update **all** of the following in the **same task** (same PR / same agent session). Do not merge or hand off without completing every row.

| # | File | What to update |
| --- | --- | --- |
| 1 | `app/build.gradle.kts` | Increment `versionCode` (integer, always +1). Set `versionName` (SemVer, e.g. `0.2.0`). |
| 2 | `app/src/main/java/com/example/wordofday/data/release/AppReleaseCatalog.kt` | Add a new `AppRelease(...)` row at the **top** of `releases` with matching `versionCode`, `versionName`, `editionName`, and 3–6 `highlights` bullets users will see in the update modal. |
| 3 | `DOCS/CHANGELOG.md` | Move shipped items from `[Unreleased]` into a dated version heading (`## [0.2.0] - YYYY-MM-DD`) per Keep a Changelog. |
| 4 | `DOCS/EDITIONS_ROADMAP.md` | Update edition status table, checkboxes, and "Current app version" line. |
| 5 | `DOCS/SCRATCHPAD.md` | Append last action + active focus. |

### Update modal rules

- Source of truth for in-app "What's New": **`AppReleaseCatalog.kt`**
- Persistence: **`ReleaseNotesRepository`** (DataStore `last_seen_version_code`)
- UI: **`UpdateModal.kt`**, wired in **`WordOfDayApp.kt`**
- Settings re-open: **What's new** button → must show `latestRelease()` from catalog
- When adding features mid-edition: append to **current** `AppRelease.highlights` until next `versionCode` bump; on bump, **freeze** prior release text and start a new row

### versionCode / versionName rules

- `versionCode` — never reuse or decrement; Play Store requires monotonic integers
- `versionName` — SemVer `MAJOR.MINOR.PATCH`; edition bumps usually MINOR
- `BuildConfig.VERSION_CODE` drives modal gating — no hardcoded version in UI

### CHANGELOG rules

- `[Unreleased]` holds work-in-progress bullets
- On release: cut a new `## [x.y.z] - date` section; leave `[Unreleased]` empty or with placeholder
- Match highlight wording between CHANGELOG Added section and `AppReleaseCatalog` (same facts, changelog may be more technical)

### Failure mode

If an agent ships UI/features without updating the catalog + CHANGELOG + version bump, the **update modal will lie or stay silent** — treat as a **release blocker**.

---

## Code conventions

- MVVM: no business logic in composables
- Trace tag: `// [TRACE: DOCS/...]`
- Minimize scope; match existing patterns
- Run `./gradlew testDebugUnitTest assembleDebug` after substantive changes
- Content changes: run `python -X utf8 scripts/inventory_word_assets.py` and update `DOCS/CONTENT_8D_PROGRESS.md` when touching word JSON

## Docs preservation

Never delete content in `DOCS/` — append or annotate with `[AMENDED YYYY-MM-DD]:` only.

## Git

- Conventional Commits: `feat(scope):`, `fix(scope):`, etc.
- Do not commit unless the user asks
- Never force-push main

---

`[2026-06-17]` Created with urgent release-sync protocol for CHANGELOG + update modal parity.
