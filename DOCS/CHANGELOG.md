<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Changelog

All notable changes to this project documentation and codebase will be recorded here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Added

- `:app` Android module: `build.gradle.kts` with AGP **9.1.0**, Kotlin **2.3.20**, Compose BOM **2026.03.01**.
- Gradle version catalog (`gradle/libs.versions.toml`) — centralizes all dependency versions.
- `MainActivity` with edge-to-edge Compose shell.
- `HomeViewModel` + sealed `HomeUiState` (Loading / Success / Error).
- `HomeScreen` composable with staggered entrance animations, gradient background, Material 3 cards.
- `WordRepository` with 14 curated bundled words, deterministic day-of-year rotation.
- Material 3 theme: ink/gold/sage curated palette + dynamic color on Android 12+.
- Custom typography scale tuned for reading (large display for word, comfortable body for definitions).
- Adaptive launcher icon (gold background, cream "W" foreground).
- ProGuard rules for Kotlin serialization.
- Android resources: `strings.xml`, `themes.xml`.

### Changed

- `[2026-04-29]` **Adaptive layout + edge-to-edge insets:** Material3 **`calculateWindowSizeClass`** (`compose-material3-window-size` / `material3-window-size-class`) on **`HomeScreen`**; **`Scaffold(contentWindowInsets = WindowInsets.safeDrawing)`** on home/settings/onboarding; removed redundant **`safeDrawingPadding`** where scaffold applies insets; loading spinner uses **`safeDrawingPadding`**. [SBOM](../SBOM.md) updated. [CONTENT_8D_PROGRESS.md](./CONTENT_8D_PROGRESS.md), **`scripts/inventory_word_assets.py`** (matrix + gap sum), **`scripts/ensure_general_category.py`** (GENERAL tag helper); [ROADMAP.md](./ROADMAP.md) §8d checklist rows + snapshot table pointer; **Pre-K** sample TECH lemmas (**screen**, **plug**). checkboxes updated for **`WordOfDayApp`** navigation, **`JsonWordDataSource`** / per-grade assets, refresh/share, onboarding/settings/DataStore, partial tablet/wide layout; **`My_Thoughts.md`** documents **“today”** (device default zone + **`dayOfYear`**).
- `[2026-04-29]` Expanded bundled **`assets/words/*.json`** with multi-topic sample words per grade (Pre-K through Adult) aligned to MVP categories; appended six adult-tier topic samples.
- `[2026-04-29]` Bundled vocabulary split into **`assets/words/<grade>.json`** (15 files). `GradeLevel` defines each file base name; [`JsonWordDataSource`](../app/src/main/java/com/example/wordofday/data/source/JsonWordDataSource.kt) parses and caches per grade; [`WordRepository`](../app/src/main/java/com/example/wordofday/data/repository/WordRepository.kt) loads only the requested grade for counts and searches **outward by grade** when a grade file is empty. Removed monolithic `assets/words.json`.
- ROADMAP §8–10 fully rewritten: **per-grade granularity** (15 levels: Pre-K through Adult), 14 themed categories, JSON data store schema, content volume targets (~2,700 words MVP), content quality guidelines per grade band, onboarding UX flow, engagement features (history, favorites, quiz, streaks, sharing).
- Converted root project from single-module Java app to multi-module Android parent (`settings.gradle.kts` + `build.gradle.kts`).
- SBOM: documented **pinned Gradle 9.6 nightly** snapshot `9.6.0-20260330000154+0000` (wrapper `distributionUrl` on [distribution snapshots](https://services.gradle.org/distributions-snapshots/)); amended generic Gradle row with AGP-minimum vs. repo-pin note.

### Removed

- `org.jetbrains.kotlin.android` plugin from root and `:app` — AGP **9.1.0** handles Kotlin compilation internally; applying both caused Gradle sync conflicts.

### Added

- Initial `DOCS/` set: SBOM (with version-page links), SUMMARY, ROADMAP (checklist), ARCHITECTURE, STYLE_GUIDE, SCRATCHPAD, My_Thoughts, CHANGELOG.
- SBOM baseline: Kotlin **2.3.20**, AGP **9.1.0**, Compose via BOM; links to [JetBrains/kotlin releases](https://github.com/JetBrains/kotlin/releases) and AndroidX release note pages.
