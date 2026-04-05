<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Roadmap — Word of the Day (Android)

Checklist-style plan from **current Gradle sample** to a **successful** phone + tablet app. Check items as you complete them.

**Stack:** Kotlin **2.3.20**, Jetpack Compose, Material 3, MVVM-style state.  
**SBOM / versions:** Keep [SBOM.md](./SBOM.md) updated when Gradle dependencies change.

---

## Status snapshot — 2026-03-31

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
- **Data Source**: Static bundled JSON with 10 curated words (assets/words.json)
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
- [ ] Add **version control** (if not already): `.gitignore` for `build/`, `.gradle/`, `local.properties`. `[2026-03-31]:` Workspace not a git repo yet; add root `.gitignore` when initializing.
- [x] Update [SBOM.md](./SBOM.md) with resolved direct dependencies and links.

---

## 2. App shell and UX foundation

- [x] Single-activity: `ComponentActivity` + `setContent { }`.
- [x] **Material 3** theme: light/dark, dynamic color on supported devices.
- [ ] **Navigation** (Compose): at least Home (word of the day) + optional About / Settings. `[2026-03-31]:` `WordOfDayApp` hosts `HomeScreen` only; `navigation-compose` dep present, graph not wired.
- [x] **Typography** tuned for reading (large title for word, body for definition).
- [x] Loading / error / empty states as sealed UI state (no silent failures). `[AMENDED 2026-03-31]:` `Loading` / `Success` / `Error`; no dedicated empty list state (single-word flow).

---

## 3. Word of the Day feature (core)

- [x] **Repository** abstraction: `getWordForDate(date)` or `getTodaysWord()`. `[AMENDED 2026-03-31]:` `WordRepository` loads `assets/words.json` (kotlinx.serialization), deterministic index from `dayOfYear`.
- [x] **ViewModel** + `UiState` (`StateFlow` or similar); no business logic in composables.
- [ ] **Timezone policy**: what “today” means (device locale vs. UTC) — document in [My_Thoughts.md](./My_Thoughts.md). `[2026-03-31]:` Uses `LocalDate.now()` (system default zone); decision still open in My_Thoughts.
- [ ] **Refresh**: pull-to-refresh or button; respect rate limits if using remote API.
- [ ] **Share** intent (optional v1): share word + definition as plain text.

---

## 4. Phones and tablets (layout success)

- [ ] Use **window size classes** or `BoxWithConstraints` / `WindowMetrics` to branch layout.
- [x] **Phone**: single-column scroll; comfortable tap targets; avoid tiny body text.
- [ ] **Tablet**: two-pane or wider reading column (e.g. word + definition side-by-side); verify landscape + portrait.
- [ ] **Foldables / large screens**: test at least one large resolution in emulator.
- [ ] **Insets**: status bar, navigation bar, display cutout (`WindowInsets`). `[AMENDED 2026-03-31]:` `enableEdgeToEdge()` + `statusBarsPadding()` on home; gesture nav bar not explicitly padded yet.

---

## 5. Quality, accessibility, stability

- [ ] **TalkBack**: content descriptions, focus order, headings where appropriate.
- [ ] **Font scaling**: layout survives largest font sizes without clipping critical text.
- [ ] **Dark theme** and **themed icons** (if applicable).
- [ ] **Unit tests**: ViewModel + repository (fake data source).
- [ ] **UI tests** (Compose testing): smoke test for happy path on phone configuration.
- [ ] **Lint** clean or justified suppressions documented.

---

## 6. Operations and release

- [x] **ProGuard/R8** rules if using reflection or serialization. `[AMENDED 2026-03-31]:` `proguard-rules.pro` keeps kotlinx.serialization + `WordEntry` serializers; verify on `assembleRelease` before ship.
- [ ] **Signing** config (never commit keystore or passwords); use env/CI secrets.
- [ ] **Play Console**: data safety form aligned with actual data collection.
- [x] **Versioning**: `versionCode` / `versionName` strategy documented. `[AMENDED 2026-03-31]:` Set in `:app` `defaultConfig` (`0.1.0`); document SemVer policy in CHANGELOG when releasing.
- [ ] **CI** (optional): `./gradlew test lint assembleRelease` on push.

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

---

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

- [ ] Define `GradeLevel` enum with all 15 values above in `data/model/GradeLevel.kt`.
- [ ] Add `gradeLevel: GradeLevel` field to `WordEntry` data model.
- [ ] Write age-appropriate **definitions and examples** per grade (a kindergartner's definition of "brave" ≠ an adult's).
- [ ] Ensure `WordRepository` can filter by grade level: `getWordForDate(date, gradeLevel, category)`.
- [ ] Grade-level display strings and descriptions for the picker UI.

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

- [ ] Define `Category` enum with all 14 values above in `data/model/Category.kt`.
- [ ] Add `categories: List<Category>` field to `WordEntry` (multi-tag support).
- [ ] MVP launch with **6 categories**: General, Technology, Sports, Food, Science, Animals.
- [ ] Remaining 8 categories added in v1.1+.
- [ ] Default to `GENERAL` if user hasn't selected a category.
- [ ] Category display names, icons, and color accents for the picker UI.

### 8c. Content data store

Move from hardcoded Kotlin list to structured JSON bundled in the APK.

`[AMENDED 2026-03-31]:` **Interim shipped shape:** single file `app/src/main/assets/words.json` + parsing in `WordRepository` (no per-grade tree yet). Checklist below is the **target** layout for grade/category work.

**Interim (shipped now):**

- [x] Bundle JSON in APK; parse with kotlinx.serialization in `WordRepository`; in-memory cache after first load; IO fallback if file missing.

**Implementation checklist:**

- [ ] Create `app/src/main/assets/words/` directory.
- [ ] Define JSON schema — one file per grade level for manageability:
  ```
  assets/words/pre_k.json
  assets/words/kindergarten.json
  assets/words/grade_1.json
  ...
  assets/words/adult.json
  ```
- [ ] JSON entry format:
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
- [ ] Build `JsonWordDataSource` using `kotlinx.serialization` to parse asset files.
- [ ] Update `WordRepository` to load from `JsonWordDataSource` instead of hardcoded list.
- [ ] Add fallback logic: if a grade×category combo has <7 words, borrow from adjacent grade.
- [ ] Cache parsed word lists in memory (parse once at launch, not on every access).

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

- [ ] Write or source **30 words** for each MVP combo (15 grades × 6 categories = 90 combos × 30 words).
- [ ] Each word entry must have: word, part of speech, grade-appropriate definition, grade-appropriate example sentence.
- [ ] Pronunciation and etymology are optional but preferred for Grade 3+.
- [ ] Cross-check: ensure no duplicate words within the same grade level.
- [ ] Validate reading level of definitions against grade expectations (a Grade 1 definition shouldn't use Grade 5 vocabulary).

### 8e. Content quality guidelines

- [ ] **Pre-K / Kindergarten**: Definitions use only words the child already knows. Examples are 1 simple sentence. No etymology.
- [ ] **Grade 1–2**: Definitions are 1 sentence max. Examples show the word in a relatable scenario (school, home, playground).
- [ ] **Grade 3–5**: Definitions can be 1–2 sentences. Etymology introduced as "where the word comes from."
- [ ] **Grade 6–8**: Full definitions with nuance. Examples from broader contexts (history, science, literature).
- [ ] **Grade 9–12**: SAT/ACT-appropriate. Connotation vs. denotation noted where relevant.
- [ ] **Adult**: Full etymology, literary/philosophical context welcome. Obscure and beautiful words encouraged.

---

## 9. UX for level / category selection

### 9a. Onboarding flow (first launch)

- [ ] **Screen 1** — "Who's learning today?" → visual grade-level picker.
  - Grid of grade labels with age hints.
  - For young learners (Pre-K through Grade 2): larger buttons, playful colors, optional parent helper text.
  - For older users: clean list/grid.
- [ ] **Screen 2** — "What are you into?" → category multi-select (optional, skippable).
  - Show available categories as cards with icons.
  - Pre-select `GENERAL`.
  - User can pick 1–3 favorites.
- [ ] **Screen 3** — First word of the day → transition to home screen.
- [ ] Store selections in `DataStore` (Preferences DataStore, not Proto).
- [ ] "Skip for now" defaults to `ADULT` + `GENERAL`.

### 9b. Settings screen

- [ ] Change grade level (re-shows picker).
- [ ] Toggle individual categories on/off.
- [ ] "Reset preferences" option.
- [ ] Preview: show how many words are available for current selection.

### 9c. Home screen enhancements

- [ ] Display active grade level + category as **chips** below the date header.
- [ ] Tapping a chip opens a quick-switch bottom sheet.
- [ ] "Try a harder word" / "Try an easier word" — shifts grade level ±1 for today's word only.
- [ ] Swipe horizontally to see today's word from a different category (same grade).

### 9d. Preferences persistence

- [ ] Add `androidx.datastore:datastore-preferences` to dependencies.
- [ ] `UserPreferences` data class: `gradeLevel: GradeLevel`, `selectedCategories: Set<Category>`.
- [ ] `PreferencesRepository` wrapping DataStore with `Flow<UserPreferences>`.
- [ ] ViewModel observes preferences and re-fetches word when they change.

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

- [ ] Share button on word card → plain text intent.
- [ ] Format adapts to grade level:
  - **K–2**: "Today I learned the word [WORD]! It means [DEFINITION]. 📚"
  - **3–8**: "[WORD] ([part of speech]) — [DEFINITION]. Example: [EXAMPLE]"
  - **9–Adult**: Full card with etymology.
