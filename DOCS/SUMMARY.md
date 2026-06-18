<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Project summary — Word of the Day (Android)

## Purpose

Single-purpose v1: a **Word of the Day** experience on **Android phones and tablets**, built with **Kotlin** and **Jetpack Compose**, using a **current Kotlin toolchain** (baseline **2.3.20**).

## Current repository state

`[AMENDED 2026-03-30]:` Android `:app` module **now exists** — multi-module project with MVVM Compose shell. See structure below.
- Gradle **Java** sample application (`com.example.App`); **Android module not yet added**.
- Gradle wrapper pins **Gradle 9.6 nightly** snapshot **`9.6.0-20260330000154+0000`** (`distributions-snapshots`); see [SBOM.md](./SBOM.md) and `gradle/wrapper/gradle-wrapper.properties`.

`[AMENDED 2026-03-30]:` Replaced vague “9.6 snapshot” wording with the exact version string above.

`[AMENDED 2026-04-29]:` Root Java sample is not tracked anymore; the repo is an Android multi-module project where the actual app lives in `:app`.

### Module layout

| Module | Purpose |
| --- | --- |
| `:app` | Android application — `com.example.wordofday` |
| (root) | Plugin declarations only; old Java `src/` is orphaned |

## Documentation map

| Doc | Purpose |
| --- | --- |
| [SBOM.md](./SBOM.md) | Bill of materials + **version page links** for Kotlin, AGP, Compose, AndroidX |
| [ROADMAP.md](./ROADMAP.md) | **Sectioned checklist** from empty repo → shippable app (phone + tablet) |
| [CONTENT_8D_PROGRESS.md](./CONTENT_8D_PROGRESS.md) | §**8d** MVP corpus coverage vs targets + inventory scripts |
| [CONTENT_SOURCES.md](./CONTENT_SOURCES.md) | **Open lexicon** licensing, opt-in packs (WordNet, myth, sacred, literary), age gates |
| [ARCHITECTURE.md](./ARCHITECTURE.md) | Planned layers and boundaries |
| [STYLE_GUIDE.md](./STYLE_GUIDE.md) | Naming, structure, trace tags for this project |
| [AGENT_WORKMAP.md](./AGENT_WORKMAP.md) | Fast "where to edit" map for routes, constants, data, config, and CI |
| [EDITIONS_ROADMAP.md](./EDITIONS_ROADMAP.md) | **Multi-edition** product plan (E1–E8) + release discipline |
| [AGENTS.md](../AGENTS.md) | Agent init + **urgent release sync** (CHANGELOG + update modal) |
| [CHANGELOG.md](./CHANGELOG.md) | Human-readable history |
| [SCRATCHPAD.md](./SCRATCHPAD.md) | Active tasks and last actions |
| [My_Thoughts.md](./My_Thoughts.md) | Decisions log |

## Quick links (external)

- [Kotlin 2.3.20 release (GitHub)](https://github.com/JetBrains/kotlin/releases/tag/v2.3.20)
- [Kotlin programming language repository](https://github.com/JetBrains/kotlin)
- [Jetpack Compose releases](https://developer.android.com/jetpack/androidx/releases/compose)
- [Android Gradle plugin releases](https://developer.android.com/build/releases/gradle-plugin)

## Status

`[2026-06-17]` **Zen UI (`0.2.4`)** shipped: centered max-width content on Home, Settings, Onboarding, Quiz, Library, Word Detail. App **0.2.4** / versionCode **6**.

`[2026-06-17]` **Open lexicon (`0.2.3`)** shipped: **10,351** WordNet rows + myth/sacred/literary packs under **`assets/lexicon/`**; Settings **Extended sources** opt-in; age-rated filters; **`CONTENT_SOURCES.md`**. App **0.2.3** / versionCode **5**.

`[2026-06-17]` **Mega corpus (`0.2.2`)**: **11,667** curated rows in **`assets/words/`**; **60** per (grade × category) across **13** topics.

`[2026-06-17]` **Edition 2 (`0.2.0`)** shipped: quiz, library (calendar/list/favorites), update modal, **`EDITIONS_ROADMAP.md`**, release-sync rules in **`AGENTS.md`**.

`[2026-04-29]` ROADMAP §**0–8** engineering items synced (§**8d** volume row + reading-level audit remain open); CI (**`.github/workflows/android-ci.yml`**), **`lintDebug`**, JVM **`GradeSearchOrder`** tests, **`LocalActivity`** + lemma **`heading()`**.

`[2026-04-29]` Edge-to-edge inset pass: **`WindowInsets.safeDrawing`** on **Material3 `Scaffold`** (home/settings/onboarding) + **`calculateWindowSizeClass`** for adaptive home layout; catalog alias **`compose-material3-window-size`** (`material3-window-size-class`).

`[2026-03-30]` `:app` Android module created — AGP 9.1.0, Kotlin 2.3.20, Compose BOM 2026.03.01. MVVM shell with HomeScreen, ViewModel, Repository (14 bundled words), M3 theme. Bootable.  
`[2026-03-30]` SBOM documents Gradle 9.6 nightly pin; Android module still not added.  
`[2026-03-29]` Documentation and SBOM baseline created; Android app module and dependencies not yet implemented.

`[2026-04-29]` §**8d** tracker: [CONTENT_8D_PROGRESS.md](./CONTENT_8D_PROGRESS.md) + `scripts/inventory_word_assets.py`; cumulative gap vs 30-per-cell snapshot logged there.

`[AMENDED 2026-04-29]:` Roadmap **[ROADMAP.md](./ROADMAP.md)** checklist synced with shipping **Nav**, **DataStore**, **per-grade JSON**, **timezone** decision in **[My_Thoughts.md](./My_Thoughts.md)**; §**8d** content volume and §**9c**/§**10** polish remain backlog.

`[2026-04-29]` Initial repo hygiene + metadata update pushed; `:app` is buildable with SDKs `min/target/compile = 26/36/36`.

`[AMENDED 2026-04-29]:` Word lists ship as **`assets/words/<grade>.json`** (see `GradeLevel.bundledWordsAssetPath`, `JsonWordDataSource`) instead of a single `words.json`.

`[2026-04-30]` Documentation audit pass: added **[AGENT_WORKMAP.md](./AGENT_WORKMAP.md)** to centralize change locations (navigation routes, model constants, DataStore keys/defaults, dependency pins, CI paths, and content tooling) for faster future-agent handoffs.
