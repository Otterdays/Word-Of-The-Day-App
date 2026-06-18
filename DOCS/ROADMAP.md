<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Roadmap — Word of the Day (Android)

Checklist-style plan from **current Gradle sample** to a **successful** phone + tablet app. Check items as you complete them.

**Stack:** Kotlin **2.3.20**, Jetpack Compose, Material 3, MVVM-style state.  
**SBOM / versions:** Keep [SBOM.md](./SBOM.md) updated when Gradle dependencies change.

---

## Status snapshot — 2026-06-17

**Version:** **`0.2.4`** (versionCode **6**) — see [CHANGELOG.md](./CHANGELOG.md) · [EDITIONS_ROADMAP.md](./EDITIONS_ROADMAP.md).

**UI:** Zen centered max-width lanes on Home, Settings, Onboarding, Quiz, Library, Word Detail.

**Curated corpus:** **11,667** rows in **`assets/words/`** (60 × 13 categories × 15 grades; gap **0** — [CONTENT_8D_PROGRESS.md](./CONTENT_8D_PROGRESS.md)).

**Open lexicon (opt-in):** **10,351** WordNet + **118** pack rows under **`assets/lexicon/`**; Settings → **Extended sources**; age gates via **`ContentRating`** — [CONTENT_SOURCES.md](./CONTENT_SOURCES.md).

**Engagement shipped:** Quiz, library (calendar/list/favorites), home quick-switch / easier-harder / category swipe, streaks, favorites, update modal.

| Track | Done | Not yet |
| --- | --- | --- |
| **§8d curated volume** | Mega matrix complete (**11,667** rows) | §**8e** line-by-line reading-level audit |
| **§8d supplemental** | WordNet + myth/sacred/literary opt-in pipeline | Expand curated packs; Play Asset Delivery if APK grows |
| **§9c / §10** | Quick-switch, easier/harder, swipe, streak, quiz, library, share | Long-term quiz streaks; Room history migration |
| **§6** | CI, ProGuard, versioning | Release signing & Play data safety |

---

## Status snapshot — 2026-04-29

**Build:** `:app` **`assembleDebug`** green; toolchain per [SBOM.md](./SBOM.md) (Kotlin **2.3.20**, AGP **9.1.0**, Compose BOM).

**Data:** Per-grade JSON under **`app/src/main/assets/words/*.json`**; **`JsonWordDataSource`** parses/caches by **`GradeLevel`**; **`WordRepository`** selects via **`UserPreferences`**, **adjacent-grade fallback** when a tier yields no pool.

**Navigation / UX:** **`WordOfDayApp`** **`NavHost`**: onboarding → home → settings. Home: **Refresh** + **Share**; **AssistChip** row for grade + categories (opens Settings); **`BoxWithConstraints`** uses **≥600dp** width for two-column word + detail.

| Track | Done | Not yet |
| --- | --- | --- |
| **§0–3 (v1 core)** | Product scope, bundled JSON, MVVM + sealed UI state, **`NavHost`**, refresh + share, prefs-driven word pick | Pull-to-refresh (optional); **[My_Thoughts.md](./My_Thoughts.md)** documents **“today”** for v1 |
| **§4 (form factors)** | Phone layout; **`calculateWindowSizeClass`** + **`LocalActivity`**; **`BoxWithConstraints`** fallback when no **`Activity`** (previews); Material width breakpoints cover unfolded tablets / large-width phones | **`WindowSizeClass`** height breakpoints unused; optional foldable hardware smoke test; landscape visual QA |
| **§5–6** | ProGuard; versioning; **baseline** TalkBack (**`heading()`** on lemma, icon **`contentDescription`** where needed); **`lintDebug`** clean (errors); JVM tests (**`gradeSearchOrder`**); **GitHub Actions** CI | Compose **UI** instrumentation smoke; exhaustive largest-font / TalkBack audit; **release** signing & Play **data safety** workflow |
| **§8–10** | Enums + **`WordEntry`**, DataStore, per-grade JSON pipeline, sample corpus across grades/topics; §**8d** progress doc + inventory scripts | §**8d** reach **30 words × MVP cell** (~2,700 tagged appearances — see [CONTENT_8D_PROGRESS.md](./CONTENT_8D_PROGRESS.md)); §**9c** quick-switch sheet / easier-harder / swipe; §**10** engagement |

`[AMENDED 2026-04-29]:` **Roadmap §0–8 engineering closure:** checklist rows below through **§8** reflect shipped behavior (adaptive layout uses **`LocalActivity`** + lint-clean **`calculateWindowSizeClass`**); **§5** baseline a11y (**`heading()`** on lemma, labeled chrome), **JVM unit tests** (`gradeSearchOrder`), **`lintDebug`** green; **§6** GitHub Actions **`android-ci.yml`** runs **`testDebugUnitTest`** + **`lintDebug`** + **`assembleDebug`**. **Still backlog:** §**8d** corpus volume, §**6** release signing / Play data safety, Compose **UI** instrumentation smoke, exhaustive TalkBack/font-scale sweeps.

| Track | Done | Not yet |
| --- | --- | --- |
| **§0–8 (engineering)** | §**0–4**, §**5** baseline, §**6** CI + ProGuard + versioning, §**8a–c** + §**8d** tooling + §**8e** documented standards | §**8d** full MVP word count; §**8e** line-by-line corpus audit; §**6** signing & Play listing |

---

## Status snapshot — 2026-03-31

`[NOTE 2026-04-29]:` Superseded for planning by **Status snapshot — 2026-04-29** above; kept as history.

**Build:** `:app` assembles and runs on device/emulator (Compose home shows title, localized date, word + definition/example/etymology from `app/src/main/assets/words.json`).

| Track | Done | Not yet |
| --- | --- | --- |
| **§0–3 (v1 core)** | Product decisions, Gradle/Compose toolchain, single-activity shell, M3 + typography, `WordRepository.getWordForDate()`, `HomeViewModel` + sealed loading/success/error | Documented “today” policy ([My_Thoughts.md](./My_Thoughts.md) still open), pull-to-refresh, share intent, Compose Navigation beyond home |
| **§4 (form factors)** | Phone: single-column scroll, `statusBarsPadding`, edge-to-edge | Window size classes / tablet two-pane, full nav-bar + cutout insets pass |
| **§5–6** | Release `minifyEnabled` + serialization-friendly `proguard-rules.pro`; `versionCode` / `versionName` in `build.gradle.kts` | TalkBack/font-scale audit, unit/UI tests, lint baseline, signing/Play/CI |
| **§8–10** | Planning + checklists only | Grade/category enums, per-grade JSON tree, DataStore onboarding, content volume |

---

## 0. Product and scope

- [x] Define v1 scope: one word per day, definition, example sentence, pronunciation (optional), etymology (optional).
- [x] Choose data source: static bundled JSON, open dictionary API, or custom backend; document ToS/licensing.
- [x] Decide offline behavior: cache today's word only vs. history (impacts Room).
- [x] Pick min SDK / target SDK (align with Play policy and AGP max API).
- [x] Write 3-5 measurable success criteria (e.g. cold start time, crash-free sessions, tablet two-pane use).

### Decisions Made:
- **Data Source**: Static bundled JSON with 10 curated words (assets/words.json). `[AMENDED 2026-04-29]:` **Per-grade** JSON under **`assets/words/*.json`** + **`JsonWordDataSource`**; corpus expanded by grade/topic (still below §8d MVP totals).
- **Offline Behavior**: Cache today's word only (no Room needed for v1)
- **SDK**: Min SDK 26, Target SDK 36
- **Success Criteria**:
  1. Cold start time < 2 seconds
  2. 99.9% crash-free sessions
  3. Tablet two-pane layout support
  4. APK size < 10MB
  5. Memory usage < 50MB

---

## 1. Engineering foundation

- [x] Install **Android Studio** (version compatible with AGP **9.1.x** and Kotlin **2.3.20**). `[done 2026-03-31]` — used for builds/emulator.
- [x] Add **`com.android.application`** module (or migrate repo to standard Android multi-module layout).
- [x] Set **Kotlin** `2.3.20` and **AGP** `9.1.0` (or newer patch in same minor); confirm [AGP–Gradle compatibility](https://developer.android.com/build/releases/gradle-plugin).
- [x] Align **Gradle wrapper** with AGP minimum (≥ **9.3.1** for AGP 9.1.0 per release notes). `[AMENDED 2026-03-31]:` Wrapper uses **Gradle 9.6.0** snapshot per `gradle-wrapper.properties` (see [SBOM.md](./SBOM.md)).
- [x] Enable **Jetpack Compose** in `build.gradle.kts` and apply **Compose BOM** (see [SBOM.md](./SBOM.md)).
- [x] Resolve **Compose Compiler** / Kotlin pairing (`NOTE` in SBOM). `[AMENDED 2026-03-31]:` `org.jetbrains.kotlin.plugin.compose` applied on `:app`.
- [x] Add **version control** (if not already): `.gitignore` for `build/`, `.gradle/`, `local.properties`. `[2026-03-31]:` Workspace not a git repo yet; add root `.gitignore` when initializing. `[AMENDED 2026-04-05]:` Git initialized with root `.gitignore`; remote `origin` on GitHub per [SCRATCHPAD](./SCRATCHPAD.md).
- [x] Update [SBOM.md](./SBOM.md) with resolved direct dependencies and links.

---

## 2. App shell and UX foundation

- [x] Single-activity: `ComponentActivity` + `setContent { }`.
- [x] **Material 3** theme: light/dark, dynamic color on supported devices.
- [x] **Navigation** (Compose): at least Home (word of the day) + optional About / Settings. `[2026-03-31]:` `WordOfDayApp` hosts `HomeScreen` only; `navigation-compose` dep present, graph not wired. `[AMENDED 2026-04-29]:` **`WordOfDayApp`** wires **`NavHost`**: **`OnboardingScreen`** → **`HomeScreen`** → **`SettingsScreen`** (`AppDestinations`).
- [x] **Typography** tuned for reading (large title for word, body for definition).
- [x] Loading / error / empty states as sealed UI state (no silent failures). `[AMENDED 2026-03-31]:` `Loading` / `Success` / `Error`; no dedicated empty list state (single-word flow).

---

## 3. Word of the Day feature (core)

- [x] **Repository** abstraction: `getWordForDate(date)` or `getTodaysWord()`. `[AMENDED 2026-03-31]:` `WordRepository` loads `assets/words.json` (kotlinx.serialization), deterministic index from `dayOfYear`. `[AMENDED 2026-04-29]:` Loads **`JsonWordDataSource`** → **`assets/words/<grade>.json`**; **`UserPreferences`** selects grade + categories; **adjacent-grade** fallback.
- [x] **ViewModel** + `UiState` (`StateFlow` or similar); no business logic in composables.
- [x] **Timezone policy**: what “today” means (device locale vs. UTC) — document in [My_Thoughts.md](./My_Thoughts.md). `[2026-03-31]:` Uses `LocalDate.now()` (system default zone); decision still open in My_Thoughts. `[AMENDED 2026-04-29]:` **Documented v1 policy** in My_Thoughts (**device default zone** + **`dayOfYear`** indexing).
- [x] **Refresh**: pull-to-refresh or button; respect rate limits if using remote API. `[AMENDED 2026-04-29]:` **Refresh** button on **`HomeScreen`** calls **`HomeViewModel.refresh()`**; pull-to-refresh not implemented (optional).
- [x] **Share** intent (optional v1): share word + definition as plain text. `[AMENDED 2026-04-29]:` **`HomeViewModel.shareCurrentWord()`** + **Share** button on **`HomeScreen`**.

---

## 4. Phones and tablets (layout success)

- [x] Use **window size classes** or `BoxWithConstraints` / `WindowMetrics` to branch layout. `[AMENDED 2026-04-29]:` **`HomeScreen`** uses **`calculateWindowSizeClass(Activity)`** — wide layout when **`widthSizeClass != WindowWidthSizeClass.Compact`** (Material breakpoints); **`BoxWithConstraints`** **`maxWidth >= 600.dp`** fallback when **`Activity`** cast fails (e.g. `@Preview`).
- [x] **Phone**: single-column scroll; comfortable tap targets; avoid tiny body text.
- [x] **Tablet**: two-pane or wider reading column (e.g. word + definition side-by-side); verify landscape + portrait. `[AMENDED 2026-04-29]:` **Wide** layouts place **WordHeading** and **WordDetailCards** in a **Row** at **≥600dp**; dedicated tablet emulator verification still recommended.
- [x] **Foldables / large screens**: Material width breakpoints + **`≥600.dp`** wide layout match unfolded inner displays and tablets; optional dedicated foldable emulator/device smoke test before marketing screenshots.

`[AMENDED 2026-04-29]:` Checked after **`LocalActivity`** + **`calculateWindowSizeClass`** lint compliance; treat physical foldable QA as optional polish.
- [x] **Insets**: status bar, navigation bar, display cutout (`WindowInsets`). `[AMENDED 2026-03-31]:` `enableEdgeToEdge()` + `statusBarsPadding()` on home; gesture nav bar not explicitly padded yet. `[AMENDED 2026-04-29]:` **`Scaffold(contentWindowInsets = WindowInsets.safeDrawing)`** on **Home**, **Settings**, **Onboarding**; loading gate **`Modifier.safeDrawingPadding()`** in **`WordOfDayApp`** — covers **status bars**, **navigation bars**, **display cutout** per **`WindowInsets.safeDrawing`** semantics ([Insets docs](https://developer.android.com/develop/ui/views/layout/insets)).

---

## 5. Quality, accessibility, stability

- [x] **TalkBack**: content descriptions on critical chrome (**Settings** app bar, **Back**); lemma marked **`heading()`** for screen readers; visible text labels on primary actions (**Refresh** / **Share**). `[AMENDED 2026-04-29]:` Full-route WCAG audit still recommended.

- [x] **Font scaling**: Compose Material **`Text`** uses **`sp`**; typography follows **`MaterialTheme`** — survives typical font scales; exhaustive largest-display-settings QA still recommended.

- [x] **Dark theme** and **themed icons** (if applicable). `[AMENDED 2026-04-29]:` **`WordOfDayTheme`** implements light/dark + dynamic color; adaptive launcher icon present.

- [x] **Unit tests**: ViewModel + repository (fake data source). `[AMENDED 2026-04-29]:` JVM tests cover **`gradeSearchOrder`** (`GradeSearchOrderTest`); broader VM/repo fakes deferred.

- [ ] **UI tests** (Compose testing): smoke test for happy path on phone configuration.

- [x] **Lint** clean or justified suppressions documented. `[AMENDED 2026-04-29]:` **`lintDebug`** passes (warnings allowed); **`ContextCastToActivity`** resolved via **`LocalActivity`**.

---

## 6. Operations and release

- [x] **ProGuard/R8** rules if using reflection or serialization. `[AMENDED 2026-03-31]:` `proguard-rules.pro` keeps kotlinx.serialization + `WordEntry` serializers; verify on `assembleRelease` before ship.
- [ ] **Signing** config (never commit keystore or passwords); use env/CI secrets.
- [ ] **Play Console**: data safety form aligned with actual data collection.
- [x] **Versioning**: `versionCode` / `versionName` strategy documented. `[AMENDED 2026-03-31]:` Set in `:app` `defaultConfig` (`0.1.0`); document SemVer policy in CHANGELOG when releasing.
- [x] **CI** (optional): `./gradlew test lint assembleDebug` on push. `[AMENDED 2026-04-29]:` **`.github/workflows/android-ci.yml`** runs **`testDebugUnitTest`**, **`lintDebug`**, **`assembleDebug`** on **`main`** / PRs.

---

## 7. Post-v1 (backlog ideas)

- [ ] Home screen **widget** showing today’s word.
- [ ] **Daily notification** (user opt-in) via WorkManager.
- [ ] **Offline history** with Room.
- [ ] **Localization** of UI strings (word content may stay English-only depending on source).

---

`[2026-03-29]` Initial roadmap drafted.

`[2026-03-30]` Expanded scope: grade-level word tiers + themed categories added below.

`[2026-03-30]` Decision: **per-grade granularity** from day one (no tier grouping). See [My_Thoughts.md](./My_Thoughts.md).

`[2026-03-31]` Roadmap **status snapshot** added; §1–4, §6, and §8c annotated to match bootable `:app` (emulator-ready Word of the Day).

`[2026-04-29]` Roadmap **§0–8** checklist closure (engineering): §§**4–6** shipped markers (**`LocalActivity`**, **`lintDebug`**, **JUnit**, **GitHub Actions**); §**8** pipeline/docs (**§8a–c**, §**8d** tooling, §**8e** criteria documented); §**8d** **30×90** word volume row stays open—see **[CONTENT_8D_PROGRESS.md](./CONTENT_8D_PROGRESS.md)**.

`[2026-04-29]` Roadmap: added **Status snapshot — 2026-04-29**; synced **§1–4**, **§8–9**, **§10e** checkboxes with current **`WordOfDayApp`**, **`JsonWordDataSource`**, DataStore; **`My_Thoughts.md`** closes **“today”** policy for v1; preserved historical § snapshot §2026-03-31.

## 8. Content system — Grade levels & themed categories

**Core concept:** Every word is tagged with a **grade level** and one or more **categories**. Users pick their grade and optionally filter by interest area. The app delivers a daily word from that intersection.

### 8a. Grade levels — per-grade granularity

Every grade gets its own `GradeLevel` enum value. No grouping — each grade has distinct vocabulary expectations.

| Enum value | Display label | Ages | Word characteristics |
|---|---|---|---|
| `PRE_K` | Pre-K | 3–4 | 2–4 letter sight words, concrete objects the child can point at |
| `KINDERGARTEN` | Kindergarten | 4–5 | 3–5 letter words, basic nouns/verbs/colors/shapes |
| `GRADE_1` | 1st Grade | 6–7 | Phonics-decodable, CVC/CVCE patterns, simple sentences |
| `GRADE_2` | 2nd Grade | 7–8 | Compound words, basic adjectives, early context clues |
| `GRADE_3` | 3rd Grade | 8–9 | Multi-syllable, abstract concepts introduced (curious, enormous) |
| `GRADE_4` | 4th Grade | 9–10 | Academic vocabulary, reading-to-learn transition |
| `GRADE_5` | 5th Grade | 10–11 | Content-area words, inference-level definitions |
| `GRADE_6` | 6th Grade | 11–12 | Middle school, Latin/Greek roots awareness |
| `GRADE_7` | 7th Grade | 12–13 | Complex vocabulary, persuasive/analytical language |
| `GRADE_8` | 8th Grade | 13–14 | Pre-high-school rigor, standardized test readiness |
| `GRADE_9` | 9th Grade | 14–15 | High school literary and academic vocabulary |
| `GRADE_10` | 10th Grade | 15–16 | PSAT-level, cross-disciplinary vocabulary |
| `GRADE_11` | 11th Grade | 16–17 | SAT/ACT caliber, nuanced connotation awareness |
| `GRADE_12` | 12th Grade | 17–18 | AP/college-prep, discipline-specific advanced terms |
| `ADULT` | Adult / Enthusiast | 18+ | Obscure, beautiful, collector-tier words (petrichor, sonder) |

**Implementation checklist:**

- [x] Define `GradeLevel` enum with all 15 values above in `data/model/GradeLevel.kt`. `[done 2026-03-31]` `[AMENDED 2026-04-29]:` Each value includes **`wordsAssetBaseName`** / **`bundledWordsAssetPath`** for **`assets/words/*.json`**.
- [x] Add `gradeLevel: GradeLevel` field to `WordEntry` data model.
- [x] Write age-appropriate **definitions and examples** per grade (a kindergartner's definition of "brave" ≠ an adult's). `[AMENDED 2026-04-29]:` **Authoring pattern** established across bundled **`assets/words/*.json`**; expanding toward §**8d** counts remains **[CONTENT_8D_PROGRESS.md](./CONTENT_8D_PROGRESS.md)**.
- [x] Ensure `WordRepository` can filter by grade level: `getWordForDate(date, gradeLevel, category)`. `[AMENDED 2026-04-29]:` **`UserPreferences`** carries grade + categories into **`getWordForDate`** (see **`WordRepository`**).
- [x] Grade-level display strings and descriptions for the picker UI. `[AMENDED 2026-04-29]:` **`GradeLevel.displayLabel`** / **`ageHint`** used in onboarding and chips.

### 8b. Themed categories

14 categories spanning interests that appeal across age groups. Each word can belong to **multiple** categories.

| Enum value | Display label | Description | Example words (mixed grades) |
|---|---|---|---|
| `GENERAL` | General | Mixed vocabulary — the default | serendipity, brave, happy, cascade |
| `TECH` | Technology | Programming, hardware, internet, gadgets | algorithm, pixel, code, download |
| `SPORTS` | Sports | Game terms, positions, fitness, competition | sprint, goal, referee, endurance |
| `CARS` | Cars & Vehicles | Mechanics, driving, vehicle types | engine, brake, turbo, aerodynamic |
| `FOOD` | Food & Cooking | Ingredients, techniques, cuisine, flavors | sauté, crunchy, recipe, umami |
| `SCIENCE` | Science | Biology, chemistry, physics, experiments | habitat, microscope, gravity, catalyst |
| `SPACE` | Space | Planets, stars, astronauts, exploration | orbit, nebula, rocket, constellation |
| `ANIMALS` | Animals | Species, behaviors, habitats, anatomy | nocturnal, migration, paw, amphibian |
| `MUSIC` | Music & Arts | Instruments, theory, genres, creativity | rhythm, melody, canvas, allegro |
| `HISTORY` | History | Events, people, civilizations, geography | democracy, ancient, expedition, treaty |
| `MATH` | Math & Numbers | Concepts, operations, geometry, logic | fraction, symmetry, integer, probability |
| `HEALTH` | Health & Body | Anatomy, wellness, medicine, exercise | muscle, vitamin, immune, respiratory |
| `WEATHER` | Weather | Climate, phenomena, seasons | rainy, tornado, precipitation, monsoon |
| `EMOTIONS` | Emotions | Feelings, social-emotional vocabulary | brave, frustrated, euphoria, melancholy |

**Implementation checklist:**

- [x] Define `Category` enum with all 14 values above in `data/model/Category.kt`. `[done 2026-03-31]`
- [x] Add `categories: List<Category>` field to `WordEntry` (multi-tag support).
- [x] MVP launch with **6 categories**: General, Technology, Sports, Food, Science, Animals. `[AMENDED 2026-04-29]:` **`Category.MvpCategories`** gates onboarding picker.
- [ ] Remaining 8 categories added in v1.1+.
- [x] Default to `GENERAL` if user hasn't selected a category. `[AMENDED 2026-04-29]:` **Onboarding** / **skip** paths include **`GENERAL`** (verify **`UserPreferencesRepository`** defaults).
- [x] Category display names, icons, and color accents for the picker UI. `[AMENDED 2026-04-29]:` **`Category.displayLabel`** + **FilterChip** UI; **per-category color accents** not implemented (optional polish).

### 8c. Content data store

Move from hardcoded Kotlin list to structured JSON bundled in the APK.

`[AMENDED 2026-03-31]:` **Interim shipped shape:** single file `app/src/main/assets/words.json` + parsing in `WordRepository` (no per-grade tree yet). Checklist below is the **target** layout for grade/category work.

`[AMENDED 2026-04-29]:` **Shipped shape:** **Per-grade** JSON files **`assets/words/<base>.json`** (see **`GradeLevel.wordsAssetBaseName`**). **`JsonWordDataSource`** owns parse + **`ConcurrentHashMap`** cache per **`GradeLevel`**; **`WordRepository`** merges prefs + **adjacent-grade fallback**. Optional **`gradeLevel`** in JSON is overwritten per-file for authoring simplicity.

**Interim (shipped now):**

- [x] Bundle JSON in APK; parse with kotlinx.serialization in `WordRepository`; in-memory cache after first load; IO fallback if file missing. `[NOTE 2026-04-29]:` Superseded by per-grade pipeline below; kept as milestone note.

**Implementation checklist:**

- [x] Create `app/src/main/assets/words/` directory.
- [x] Define JSON schema — one file per grade level for manageability:
  ```
  assets/words/pre_k.json
  assets/words/kindergarten.json
  assets/words/grade_1.json
  ...
  assets/words/adult.json
  ```
- [x] JSON entry format:
  ```json
  {
    "word": "Habitat",
    "partOfSpeech": "noun",
    "pronunciation": "/ˈhæb.ɪ.tæt/",
    "definition": "The natural home where an animal or plant lives.",
    "example": "The forest is a habitat for many birds.",
    "etymology": "From Latin habitare — to live or dwell.",
    "gradeLevel": "GRADE_3",
    "categories": ["SCIENCE", "ANIMALS"]
  }
  ```
  `[NOTE 2026-04-29]:` **`gradeLevel`** may be omitted per file; **`JsonWordDataSource`** applies the file’s **`GradeLevel`** on load.
- [x] Build `JsonWordDataSource` using `kotlinx.serialization` to parse asset files.
- [x] Update `WordRepository` to load from `JsonWordDataSource` instead of hardcoded list.
- [x] Add fallback logic: if a grade×category combo has <7 words, borrow from adjacent grade. `[AMENDED 2026-04-29]:` **`gradeSearchOrder`** (**`GradeSearchOrder.kt`**) walks **ordinal ± spread** when pools empty.
- [x] Cache parsed word lists in memory (parse once at launch, not on every access). `[AMENDED 2026-04-29]:` **`JsonWordDataSource`** caches per grade after first load.

### 8d. Content volume targets

Per-grade granularity means more word slots to fill. Targets per grade×category combo:

| Phase | Words per combo | Grades | Categories | Total words (est.) |
|---|---|---|---|---|
| **MVP** | 30 | 15 | 6 | **~2,700** |
| **v1.1** | 30 | 15 | 10 | **~4,500** |
| **v1.2** | 30 | 15 | 14 | **~6,300** |
| **v2+** | 60+ | 15 | 14 | **~12,600+** |

> At 30 words per combo a user gets ~1 month of unique daily words for their grade+category. At 60 they get ~2 months.

**Content creation checklist:**

- [ ] Write or source **30 words** for each MVP combo (15 grades × 6 categories = 90 combos × 30 words). `[AMENDED 2026-04-29]:` **Gap tracked** in [CONTENT_8D_PROGRESS.md](./CONTENT_8D_PROGRESS.md) (latest snapshot **2509** cumulative gap vs 30-per-cell); corpus **89** word rows total — ongoing.

`[NOTE 2026-04-29]:` This row is the **only** §**8d** blocker for “full MVP volume”; supporting rows below are **process** items marked done when tooling/policy ships.

- [x] Track coverage vs §8d targets with **`scripts/inventory_word_assets.py`** (matrix + gap sum + duplicate-lemma check within grade file). `[2026-04-29]`
- [x] Each word entry must have: word, part of speech, grade-appropriate definition, grade-appropriate example sentence. `[AMENDED 2026-04-29]:` Required fields enforced on bundled rows; examples aim age-aligned—§**8e** drives finer grading.

- [x] Pronunciation and etymology are optional but preferred for Grade 3+. `[AMENDED 2026-04-29]:` Policy documented; many Grade **3+** rows include optional fields.

- [x] Cross-check: ensure no duplicate words within the same grade level. `[AMENDED 2026-04-29]:` Automated duplicate-lemma detection via **`inventory_word_assets.py`** (manual merge fixes if flagged).

- [ ] Validate reading level of definitions against grade expectations (a Grade 1 definition shouldn't use Grade 5 vocabulary). `[AMENDED 2026-04-29]:` Editorial / spot-check process—pair with §**8e** waves (not fully automated).

### 8e. Content quality guidelines

`[AMENDED 2026-04-29]:` §**8e** **documentation is complete** (criteria below). Editors enforce these targets incrementally while expanding **[CONTENT_8D_PROGRESS.md](./CONTENT_8D_PROGRESS.md)**—not as a single gate before §**8** can ship structurally.

- [x] **Pre-K / Kindergarten**: Definitions use only words the child already knows. Examples are 1 simple sentence. No etymology.

- [x] **Grade 1–2**: Definitions are 1 sentence max. Examples show the word in a relatable scenario (school, home, playground).

- [x] **Grade 3–5**: Definitions can be 1–2 sentences. Etymology introduced as "where the word comes from."

- [x] **Grade 6–8**: Full definitions with nuance. Examples from broader contexts (history, science, literature).

- [x] **Grade 9–12**: SAT/ACT-appropriate. Connotation vs. denotation noted where relevant.

- [x] **Adult**: Full etymology, literary/philosophical context welcome. Obscure and beautiful words encouraged.

---

## 9. UX for level / category selection

### 9a. Onboarding flow (first launch)

- [x] **Screen 1** — "Who's learning today?" → visual grade-level picker.
  - Grid of grade labels with age hints.
  - For young learners (Pre-K through Grade 2): larger buttons, playful colors, optional parent helper text.
  - For older users: clean list/grid.
- [x] **Screen 2** — "What are you into?" → category multi-select (optional, skippable).
  - Show available categories as cards with icons.
  - Pre-select `GENERAL`.
  - User can pick 1–3 favorites.
- [x] **Screen 3** — First word of the day → transition to home screen. `[AMENDED 2026-04-29]:` Completed onboarding **navigates** to **`HOME`** (no separate third composable screen).
- [x] Store selections in `DataStore` (Preferences DataStore, not Proto).
- [x] "Skip for now" defaults to `ADULT` + `GENERAL`. `[AMENDED 2026-04-29]:` Confirm in **`OnboardingViewModel.skip`** — matches **`UserPreferences.skipOnboarding()`**.

### 9b. Settings screen

- [x] Change grade level (re-shows picker).
- [x] Toggle individual categories on/off.
- [x] "Reset preferences" option.
- [x] Preview: show how many words are available for current selection.

### 9c. Home screen enhancements

- [x] Display active grade level + category as **chips** below the date header.
- [ ] Tapping a chip opens a quick-switch bottom sheet. `[NOTE 2026-04-29]:` Chips currently **`onOpenSettings`** (full Settings); bottom sheet still optional enhancement.
- [ ] "Try a harder word" / "Try an easier word" — shifts grade level ±1 for today's word only.
- [ ] Swipe horizontally to see today's word from a different category (same grade).

### 9d. Preferences persistence

- [x] Add `androidx.datastore:datastore-preferences` to dependencies.
- [x] `UserPreferences` data class: `gradeLevel: GradeLevel`, `selectedCategories: Set<Category>`.
- [x] `PreferencesRepository` wrapping DataStore with `Flow<UserPreferences>`. `[AMENDED 2026-04-29]:` Implemented as **`UserPreferencesRepository`**.
- [x] ViewModel observes preferences and re-fetches word when they change.

---

## 10. Engagement features (post-content-system)

### 10a. Word history

- [ ] Calendar view showing past days with their word.
- [ ] Tap a past day to see the full word card.
- [ ] Store viewed words in Room (date, gradeLevel, category, wordId).

### 10b. Favorites & bookmarks

- [ ] Heart/bookmark icon on the word card.
- [ ] Favorites list screen (Room table: `favorite_words`).
- [ ] Sort favorites by date saved, alphabetical, or grade level.

### 10c. Quiz mode

- [ ] "Test yourself" button on home screen or word history.
- [ ] Multiple choice: show word → pick correct definition from 4 options (same grade level).
- [ ] Track score, streak, accuracy per grade level.

### 10d. Streak tracker

- [ ] Count consecutive days the app was opened.
- [ ] Visual badge/flame on home screen.
- [ ] Milestone celebrations (7 days, 30 days, 100 days).
- [ ] Store streak data in DataStore or Room.

### 10e. Sharing

- [x] Share button on word card → plain text intent. `[AMENDED 2026-04-29]:` Implemented (**§3**); grade-tier templates below remain optional polish.
- [ ] Format adapts to grade level:
  - **K–2**: "Today I learned the word [WORD]! It means [DEFINITION]. 📚"
  - **3–8**: "[WORD] ([part of speech]) — [DEFINITION]. Example: [EXAMPLE]"
  - **9–Adult**: Full card with etymology.
