<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Agent workmap — Where to change what

Purpose: fast handoff for future agents. Use this first when a task says "update constants/config/content/flow".

## Quick start checklist (agent)

1. Read this file.
2. Read `DOCS/SUMMARY.md`, `DOCS/SCRATCHPAD.md`, `DOCS/SBOM.md`.
3. Confirm target area below before editing.
4. Update `DOCS/SCRATCHPAD.md` during work (not only at the end).

## Source-of-truth map

### Release notes & editions

- Release modal source of truth: `app/src/main/java/com/example/wordofday/data/release/AppReleaseCatalog.kt`
- Last-seen version DataStore: `app/src/main/java/com/example/wordofday/data/preferences/ReleaseNotesRepository.kt`
- Update modal UI: `app/src/main/java/com/example/wordofday/ui/update/UpdateModal.kt`
- Edition planning: `DOCS/EDITIONS_ROADMAP.md`
- Agent release-sync rules: `AGENTS.md`, `.cursor/rules/release-sync.mdc`

### App entry and navigation

- App entry point: `app/src/main/java/com/example/wordofday/MainActivity.kt`
- Root app composition + nav host: `app/src/main/java/com/example/wordofday/ui/WordOfDayApp.kt`
- Route constants: `app/src/main/java/com/example/wordofday/ui/navigation/AppDestinations.kt`

### Content model constants

- Grade enum, labels, and per-grade asset filename mapping:
  `app/src/main/java/com/example/wordofday/data/model/GradeLevel.kt`
- Category enum, labels, and MVP category subset (`MvpCategories`):
  `app/src/main/java/com/example/wordofday/data/model/Category.kt`
- Word schema model:
  `app/src/main/java/com/example/wordofday/data/model/WordEntry.kt`

### Data and fallback behavior

- JSON loader and in-memory per-grade cache:
  `app/src/main/java/com/example/wordofday/data/source/JsonWordDataSource.kt`
- Word selection, filtering, deterministic date indexing, adjacent-grade fallback:
  `app/src/main/java/com/example/wordofday/data/repository/WordRepository.kt`
- Grade spread order helper:
  `app/src/main/java/com/example/wordofday/data/repository/GradeSearchOrder.kt`

### Preferences and user defaults

- DataStore keys, CSV encoding/decoding, max category selection, default grade fallback:
  `app/src/main/java/com/example/wordofday/data/preferences/UserPreferencesRepository.kt`
- Preferences model and default/skip constructors:
  `app/src/main/java/com/example/wordofday/data/model/UserPreferences.kt`

### UI feature areas

- Home: `app/src/main/java/com/example/wordofday/ui/home/`
- Onboarding: `app/src/main/java/com/example/wordofday/ui/onboarding/`
- Settings: `app/src/main/java/com/example/wordofday/ui/settings/`
- Shared picker UI: `app/src/main/java/com/example/wordofday/ui/preferences/CategorySelection.kt`
- Theme/colors/typography: `app/src/main/java/com/example/wordofday/ui/theme/`

### Build and dependency constants

- Dependency/version source of truth:
  `gradle/libs.versions.toml`
- App module build config:
  `app/build.gradle.kts`
- Root build/plugin management:
  `build.gradle.kts`, `settings.gradle.kts`
- Gradle runtime properties:
  `gradle.properties`
- Gradle wrapper pin:
  `gradle/wrapper/gradle-wrapper.properties`

### Content assets and tooling

- Bundled words per grade: `app/src/main/assets/words/*.json`
- Inventory coverage script: `scripts/inventory_word_assets.py`
- Gap-fill corpus wave: `scripts/fill_word_gaps.py` + `scripts/corpus/lemma_banks.py`
- Category normalization helper script: `scripts/ensure_general_category.py`
- Coverage tracker doc: `DOCS/CONTENT_8D_PROGRESS.md`

### Android resources and policy text

- Android strings/theme XML:
  `app/src/main/res/values/`
- Manifest:
  `app/src/main/AndroidManifest.xml`

### CI and templates

- Android CI workflow:
  `.github/workflows/android-ci.yml`
- Issue/PR templates:
  `.github/ISSUE_TEMPLATE/`, `.github/PULL_REQUEST_TEMPLATE.md`

## Task routing guide

- "Add new route/screen": start in `ui/navigation/AppDestinations.kt` then `ui/WordOfDayApp.kt`.
- "Change grade list or labels": `data/model/GradeLevel.kt` and related assets in
  `assets/words/`.
- "Change category set/MVP categories": `data/model/Category.kt` then onboarding/settings
  UIs.
- "Change default preferences behavior": `data/model/UserPreferences.kt` and
  `data/preferences/UserPreferencesRepository.kt`.
- "Change daily pick logic/fallback": `data/repository/WordRepository.kt` and
  `data/repository/GradeSearchOrder.kt`.
- "Change dependency versions": `gradle/libs.versions.toml`, then update `DOCS/SBOM.md`.
- "Content volume progress": run script in `scripts/inventory_word_assets.py`, then update
  `DOCS/CONTENT_8D_PROGRESS.md` and `DOCS/SCRATCHPAD.md`.

## Agent guardrails for this repo

- Keep edits in scope; log out-of-scope findings in `DOCS/SCRATCHPAD.md`.
- Update status docs while working: at minimum `SCRATCHPAD` (+ `SBOM` if deps changed).
- Prefer updating enums/models before UI when changing taxonomy or defaults.
- Do not delete historical lines in `DOCS/*`; append with dated amendments.

## Known doc drift to watch

- Some historical roadmap lines still mention `assets/words.json`; use per-grade
  `assets/words/*.json` as current truth.
- AGP references may differ between old notes and current catalog; treat
  `gradle/libs.versions.toml` as source of truth.

`[2026-04-30]` Created as a fast handoff map to reduce agent search time and prevent
constant-location mistakes.
