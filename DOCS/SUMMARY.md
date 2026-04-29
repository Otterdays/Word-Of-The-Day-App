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
| [ARCHITECTURE.md](./ARCHITECTURE.md) | Planned layers and boundaries |
| [STYLE_GUIDE.md](./STYLE_GUIDE.md) | Naming, structure, trace tags for this project |
| [CHANGELOG.md](./CHANGELOG.md) | Human-readable history |
| [SCRATCHPAD.md](./SCRATCHPAD.md) | Active tasks and last actions |
| [My_Thoughts.md](./My_Thoughts.md) | Decisions log |

## Quick links (external)

- [Kotlin 2.3.20 release (GitHub)](https://github.com/JetBrains/kotlin/releases/tag/v2.3.20)
- [Kotlin programming language repository](https://github.com/JetBrains/kotlin)
- [Jetpack Compose releases](https://developer.android.com/jetpack/androidx/releases/compose)
- [Android Gradle plugin releases](https://developer.android.com/build/releases/gradle-plugin)

## Status

`[2026-03-30]` `:app` Android module created — AGP 9.1.0, Kotlin 2.3.20, Compose BOM 2026.03.01. MVVM shell with HomeScreen, ViewModel, Repository (14 bundled words), M3 theme. Bootable.  
`[2026-03-30]` SBOM documents Gradle 9.6 nightly pin; Android module still not added.  
`[2026-03-29]` Documentation and SBOM baseline created; Android app module and dependencies not yet implemented.

`[2026-04-29]` Initial repo hygiene + metadata update pushed; `:app` is buildable with SDKs `min/target/compile = 26/36/36`.
