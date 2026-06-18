<div align="center">

# Word of the Day

**Expand your vocabulary every day — from Pre-K to Adult, on phone or tablet.**

[![Android CI](https://github.com/Otterdays/Word-Of-The-Day-App/actions/workflows/android-ci.yml/badge.svg)](https://github.com/Otterdays/Word-Of-The-Day-App/actions/workflows/android-ci.yml)
[![Version](https://img.shields.io/badge/version-0.3.1-7C4DFF?style=flat-square)](https://github.com/Otterdays/Word-Of-The-Day-App/releases)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.20-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![API](https://img.shields.io/badge/API-26%2B-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com/about/versions/nougat)
[![License](https://img.shields.io/badge/License-MIT-22C55E?style=flat-square)](LICENSE)
[![Words](https://img.shields.io/badge/Vocabulary-22k%2B-F59E0B?style=flat-square)](#-vocabulary-at-a-glance)
[![Offline](https://img.shields.io/badge/Offline-first-6366F1?style=flat-square)](#-features)

[Features](#-features) · [Screenshots](#-screenshots) · [Quick start](#-quick-start) · [Docs](#-documentation) · [Contributing](#-contributing)

</div>

---

## ✨ Features

| | |
| --- | --- |
| **Daily word** | Curated lemma each day with definition, pronunciation, example, etymology, synonyms, and usage tips |
| **15 grade levels** | Pre-K through Adult — age-appropriate pools with easier/harder session shifts |
| **Themed interests** | Technology, sports, food, science, animals, and more — swipe categories on the home screen |
| **Quiz mode** | Five multiple-choice questions drawn from your grade + topic pool |
| **Library** | History calendar, list view, favorites, and rich word detail screens |
| **Streaks & share** | Daily streak milestones, TTS listen, and grade-aware share cards |
| **Extended sources** | Opt-in WordNet dictionary/thesaurus + myth, sacred reference, and literary/historical packs (age-gated) |
| **Offline-first** | Bundled JSON corpus — no account, no network required for daily use |

Built with **MVVM**, **Jetpack Compose**, **Material 3**, **DataStore**, and **Kotlinx Serialization**.

---

## 📊 Vocabulary at a glance

| Corpus | Rows | Notes |
| --- | ---: | --- |
| **Curated** (`assets/words/`) | **11,667** | 60 words × 13 topics × 15 grades |
| **WordNet** (opt-in) | **10,351** | Princeton definitions + synonyms |
| **Myth & lore** (opt-in) | 62 | Public-domain mythology references |
| **Sacred reference** (opt-in) | 28 | Scripture vocabulary (PD examples) |
| **Literary & historical** (opt-in) | 28 | Philosophy, literature, mature history |

Enable supplemental packs in **Settings → Extended sources**. See [CONTENT_SOURCES.md](DOCS/CONTENT_SOURCES.md) for licensing.

---

## 📱 Screenshots

> Placeholder — add phone + tablet captures here.

| Home | Quiz | Library |
| :---: | :---: | :---: |
| *coming soon* | *coming soon* | *coming soon* |

---

## 🚀 Quick start

### Prerequisites

- **Android Studio** Ladybug or newer  
- **JDK 21+**  
- **Android SDK** — API 26 minimum, API 36 target  

### Clone & run

```bash
git clone https://github.com/Otterdays/Word-Of-The-Day-App.git
cd Word-Of-The-Day-App
./gradlew :app:installDebug
```

On Windows:

```bat
gradlew.bat :app:installDebug
```

### Verify

```bash
./gradlew testDebugUnitTest assembleDebug
```

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────┐
│  Compose UI  —  Home · Quiz · Library · …    │
├──────────────────────────────────────────────┤
│  ViewModels  —  UiState · user intents       │
├──────────────────────────────────────────────┤
│  Repository  —  WordRepository · prefs       │
├──────────────────────────────────────────────┤
│  Data        —  JsonWordDataSource · DataStore│
│              —  assets/words + assets/lexicon │
└──────────────────────────────────────────────┘
```

**Entry points:** `MainActivity` → `WordOfDayApp` → `NavHost` (onboarding → home → settings / quiz / library).

---

## 📂 Project structure

```
Word-Of-The-Day-App/
├── app/src/main/
│   ├── java/com/example/wordofday/
│   │   ├── data/          # models, repository, JsonWordDataSource
│   │   └── ui/            # Compose screens + theme
│   └── assets/
│       ├── words/         # Curated per-grade vocabulary (always on)
│       └── lexicon/       # Opt-in WordNet + specialty packs
├── scripts/               # Corpus fill + open lexicon import
├── DOCS/                  # Architecture, roadmap, changelog
└── gradle/libs.versions.toml
```

---

## 🛠️ Tech stack

| Layer | Choices |
| --- | --- |
| Language | Kotlin **2.3.20** |
| UI | Jetpack Compose · Material 3 · Window size classes |
| Architecture | MVVM · single-activity · Navigation Compose |
| Persistence | DataStore Preferences |
| Serialization | Kotlinx Serialization (bundled JSON) |
| Build | Gradle **9.6** · AGP **9.2.0** · version catalog |
| CI | GitHub Actions — unit tests, lint, `assembleDebug` |

---

## 🧪 Testing

```bash
./gradlew testDebugUnitTest    # JVM unit tests (quiz engine, grade order, …)
./gradlew lintDebug            # Android lint
./gradlew connectedAndroidTest # Instrumented (device/emulator required)
```

---

## 📖 Documentation

| Doc | Purpose |
| --- | --- |
| [ARCHITECTURE.md](DOCS/ARCHITECTURE.md) | Layers, data flow, bundled assets |
| [ROADMAP.md](DOCS/ROADMAP.md) | Engineering checklist |
| [EDITIONS_ROADMAP.md](DOCS/EDITIONS_ROADMAP.md) | Product editions E1–E8 |
| [CONTENT_SOURCES.md](DOCS/CONTENT_SOURCES.md) | Open lexicon licensing & regeneration |
| [CONTENT_8D_PROGRESS.md](DOCS/CONTENT_8D_PROGRESS.md) | Corpus coverage snapshots |
| [CHANGELOG.md](DOCS/CHANGELOG.md) | Version history |
| [AGENT_WORKMAP.md](DOCS/AGENT_WORKMAP.md) | Where to edit routes, models, scripts |

Regenerate supplemental lexicon:

```bash
pip install nltk
python -X utf8 scripts/import_open_lexicon.py
```

---

## 🤝 Contributing

PRs welcome. For larger changes, open an issue first.

1. Fork the repo and create a feature branch (`feature/…` or `fix/…`)
2. Match existing MVVM + Compose patterns
3. Run `./gradlew testDebugUnitTest assembleDebug`
4. Update `DOCS/SCRATCHPAD.md` (and `CHANGELOG.md` for user-facing releases)

---

## 📄 License

[MIT](LICENSE) — see [LICENSE](LICENSE) for details.

---

<div align="center">

**Word of the Day** · Kotlin · Jetpack Compose · Material 3

[Report a bug](https://github.com/Otterdays/Word-Of-The-Day-App/issues) · [Request a feature](https://github.com/Otterdays/Word-Of-The-Day-App/issues)

</div>
