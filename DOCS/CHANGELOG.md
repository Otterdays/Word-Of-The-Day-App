<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Changelog

All notable changes to this project documentation and codebase will be recorded here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Added

### Changed

## [0.3.4] - 2026-06-18

### Fixed

- **More words refresh** no longer alternates between only two words when multiple interests are selected — refresh stays on the active topic and advances through its pool.
- **Selection spread** uses improved index mixing so consecutive refreshes explore more of the available vocabulary.

### Changed

- App version **`0.3.4`** (versionCode **11**).

## [0.3.3] - 2026-06-18

### Added

- **Theme toggle** on Home top-left — sun/moon button switches between light and dark mode.
- **`ThemePreferencesRepository`** — persists theme choice in DataStore (default: light).

### Changed

- **Default light theme** uses sharp pure-white surfaces and high-contrast ink text instead of warm off-white / dynamic Material You tints.
- App version **`0.3.3`** (versionCode **10**).

## [0.3.2] - 2026-06-18

### Changed

- **Home refresh flow:** repeated **More words** taps now advance through the available word pool for the user's selected grade and interests.
- **Smarter selection spread:** refresh picks use a deterministic hash and per-topic salt so more selected interests produce broader visible variety.
- **UI responsiveness:** Home keeps the current word visible while the next word loads instead of dropping to a full-screen spinner.
- **Small-screen resilience:** Home learning/action controls wrap across lines to avoid cramped rows and large-text overflow.
- **Refresh edge cases:** refresh failures now show inline on Home without replacing the full screen when a valid word is already displayed.
- App version **`0.3.2`** (versionCode **9**).

## [0.3.1] - 2026-06-17

### Added

- **53 interest topics** grouped in six sections with search on onboarding and settings (up to **8** selections).
- **Category content affinity** — new interests (Gaming, Podcasts, Ocean, etc.) match relevant corpus tags.
- **Word enrichment layer** — `assets/word_enrichment/overrides.json` + runtime patcher for thin auto-filled rows.
- **`InterestPicker`** shared component; **`AppSpacing`** compact layout tokens.

### Changed

- **Word cards** hide placeholder synonyms (`related term`) and surface real definitions for words like **stream**.
- **Home** action row consolidated; reduced padding on home, settings, onboarding, and detail cards.
- App version **`0.3.1`** (versionCode **8**).

## [0.3.0] - 2026-06-17

### Added

- **Explore screen** — full-text search across curated + opt-in lexicon (**22k+** rows).
- **Spaced review** — SM-2 flashcard deck with due counts on Home; **Review** screen.
- **Progress dashboard** — weekly quiz/review stats + **~20 achievements**.
- **Room database** — mastery state, user sentences, quiz attempt history.
- **Quiz modes** — **Reverse** (definition → word) and **Blitz** (15s timer).
- **Word notes** — save your own sentence + **Mark as hard** on word detail.
- **Library search** — filter history list and favorites; favorites sorted A–Z.

### Changed

- Settings + onboarding expose **all 14 categories** (up to **5** selections).
- Home **Learning hub** row: Explore · Review · Progress shortcuts.
- App version **`0.3.0`** (versionCode **7**).

## [0.2.4] - 2026-06-17

### Changed

- **Zen UI pass:** centered max-width content lanes on Home, Settings, Onboarding, Quiz, Library, and Word Detail screens.
- **Large-screen polish:** preference chips, quiz headings, empty states, and detail content no longer stretch edge-to-edge on tablets/foldables.
- App version **`0.2.4`** (versionCode **6**).

## [0.2.3] - 2026-06-17

### Added

- **Open lexicon pipeline:** WordNet (dictionary + synonyms), mythology, sacred reference, and literary/historical packs under **`assets/lexicon/`** (opt-in via Settings).
- **`scripts/import_open_lexicon.py`** + **`scripts/corpus/import_packs_data.py`** — regenerates supplemental JSON from NLTK WordNet and curated PD banks.
- **`ContentSource`**, **`ContentRating`**, **`LexiconPreferences`**; age gates in **`JsonWordDataSource`** / **`WordContentFilters`**.
- Settings **Extended sources** toggles; source attribution on **`WordDetailContent`**.
- **`DOCS/CONTENT_SOURCES.md`** — licensing and what cannot be bulk-imported.

### Changed

- App version **`0.2.3`** (versionCode **5**).

## [0.2.2] - 2026-06-17

### Added

- **Mega corpus wave:** **11,667** word rows (**+9,449**); **60** words per **13** topic categories × **15** grades (gap **0**).
- **8 new topic lexicons:** CARS, SPACE, MUSIC, HISTORY, MATH, HEALTH, WEATHER, EMOTIONS via **`scripts/corpus/extended_lexicons.py`**.
- **`fill_word_gaps.py --target 60`** default; tier pools expanded to **72** lemmas.

### Changed

- App version **`0.2.2`** (versionCode **4**).

## [0.2.1] - 2026-06-17

### Added

- **§8d MVP corpus complete:** **2,218** word rows across **15** grade files (**+2,047** this wave); **30** words per **TECH / SPORTS / FOOD / SCIENCE / ANIMALS** cell (gap sum **0**).
- Tooling: **`scripts/fill_word_gaps.py`** + **`scripts/corpus/lemma_banks.py`** (tiered definitions Pre-K → Adult; idempotent gap fill).

### Changed

- App version **`0.2.1`** (versionCode **3**).

## [0.2.0] - 2026-06-17

### Added

- **Edition 2 — Substance & Discovery:** **`QuizEngine`** + **`QuizScreen`**; **`LibraryScreen`** (calendar, list, favorites → **`WordDetailScreen`**); **`WordEntry.synonyms`** / **`usageNote`**; **`WordDetailContent`** shared cards.
- **Home engagement:** **`QuickSwitchSheet`**, easier/harder grade shift, category **`HorizontalPager`**, favorites, streak UI + milestone messages (7/30/100), pull-to-refresh, TTS, **`ShareFormatter`**, **`CategoryAccent`**.
- **Update modal:** **`AppReleaseCatalog`**, **`ReleaseNotesRepository`**, **`UpdateModal`** (auto on version bump; Settings → What's new).
- **Planning & agent rules:** **`DOCS/EDITIONS_ROADMAP.md`** (E1–E8 editions); root **`AGENTS.md`** + **`.cursor/rules/release-sync.mdc`** (urgent CHANGELOG/modal sync).
- **Content:** **`grade_5.json`** / **`grade_6.json`** → 15 rich entries each; **`grade_7.json`** / **`grade_8.json`** → 12 entries with synonyms/usage tips.
- JVM tests: **`GradeLevelOffsetTest`**, **`ShareFormatterTest`**, **`QuizEngineTest`**.

### Changed

- App version **`0.2.0`** (versionCode **2**); **`buildConfig`** enabled for modal gating.

---

<!-- Prior unreleased items shipped in 0.2.0 above. Historical Added entries below. -->

## [Unreleased] — archive pointer

`[NOTE 2026-06-17]:` Items below were logged under `[Unreleased]` before the **0.2.0** cut; see **0.2.0** section for the consolidated release.

### Added (pre-0.2.0 log)

- `[2026-06-17]` **Learning substance (Roadmap §10a/§10c + richer content):** **`QuizEngine`** + **`QuizScreen`** (5 multiple-choice questions, lifetime accuracy/best streak); **`LibraryScreen`** (90-day word history + browsable favorites → **`WordDetailScreen`**); **`WordEntry`** optional **`synonyms`** + **`usageNote`**; shared **`WordDetailContent`**; **`grade_5.json`** / **`grade_6.json`** expanded to 15 entries with full metadata; **`QuizEngineTest`**.

- `[2026-06-17]` **Home engagement wave (Roadmap §9c + §10 partial):** **`QuickSwitchSheet`** (grade/category without leaving home); **Easier / Harder** session grade shift; **HorizontalPager** category swipe; **`EngagementRepository`** (daily streak + favorites via DataStore); pull-to-refresh; TTS pronunciation button; **`ShareFormatter`** grade-tier share text; **`CategoryAccent`** chip colors; JVM tests **`GradeLevelOffsetTest`**, **`ShareFormatterTest`**.

- `[2026-04-30]` Added **`DOCS/AGENT_WORKMAP.md`**: a future-agent handoff index mapping exact edit locations for navigation routes, enum/constants ownership (`GradeLevel`, `Category`, `AppDestinations`), DataStore keys/defaults (`UserPreferencesRepository`), build/dependency pins (`gradle/libs.versions.toml`), content assets/scripts, and CI/template files.

- `[2026-04-29]` **Roadmap §0–8 engineering closure:** **`GradeSearchOrder.kt`** + **`GradeSearchOrderTest`** (JUnit **4.13.2**); **`HomeScreen`** lemma **`heading()`** semantics + **`LocalActivity`** (lint **`ContextCastToActivity`**); **`.github/workflows/android-ci.yml`** (**`testDebugUnitTest`**, **`lintDebug`**, **`assembleDebug`**). [ROADMAP.md](./ROADMAP.md) §§**4–6**, **8** checkboxes synced; [SBOM.md](./SBOM.md) **JUnit** row.

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

- `[2026-04-30]` `README.md` docs accuracy pass: replaced stale testing note ("no test sources") with current status and added `DOCS/AGENT_WORKMAP.md` under documentation links for faster onboarding.

- `[2026-04-29]` **Toolchain:** [SBOM](./SBOM.md) — **Android Gradle Plugin** **`9.2.0`** (**`gradle/libs.versions.toml`** **`agp`**); [AGP 9.2.0 release notes](https://developer.android.com/build/releases/agp-9-2-0-release-notes); Gradle compatibility (**≥ 9.4.1** for AGP 9.2.x), repo wrapper **9.6.x** nightly.

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
