<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# My thoughts (decisions)

## Kotlin version

- **Decision:** Target **Kotlin 2.3.20** for greenfield Android work (matches current Kotlin release train and official docs).
- **Rationale:** Strong tooling support, Compose ecosystem moves with Kotlin; release notes and GitHub tag give a stable SBOM anchor.
- **Links:** [v2.3.20 tag](https://github.com/JetBrains/kotlin/releases/tag/v2.3.20), [What’s new 2.3.20](https://kotlinlang.org/docs/whatsnew2320.html).

## Build pairing

- **AGP 9.1.0** documented with **Gradle ≥ 9.3.1** and **JDK 17** minimum per [AGP 9.1.0 release notes](https://developer.android.com/build/releases/agp-9-1-0-release-notes). Wrapper may use newer Gradle (e.g. 9.6.x) once verified.

## Word-of-the-day semantics

- **Open decision:** Define “today” in local timezone vs. UTC before implementing repository caching.

## Content strategy — grade levels + themed categories

- **Decision:** Tag every word with a `GradeLevel` (Pre-K through Adult) and one or more `Category` enums (Tech, Sports, Food, etc.). Users select their level and optionally filter by category. Daily word is drawn from that intersection.
- **Rationale:** A single "word of the day" feed limits the audience. A kindergartner and a college student have completely different vocabulary needs. Grade levels make it educational; categories make it fun and relevant to individual interests.
- ~~**MVP simplification:** Collapse 10 grade levels into **4 tier groups** for launch (Elementary, Upper Elementary, Middle School, High School+). Expand to per-grade granularity based on user demand. Launch with **6 of 14 categories** (General, Tech, Sports, Food, Science, Animals).~~
- `[AMENDED 2026-03-30]:` **Per-grade granularity from day one.** 15 distinct grade levels (Pre-K, K, 1st–12th, Adult). No grouping. Each grade has its own vocabulary expectations, definitions, and example sentences. MVP launches with 6 of 14 categories.
- ~~**Content volume:** Target **30 words per tier×category combo** minimum — gives roughly a month of non-repeating daily words. At 4 tiers × 6 categories × 30 words = ~720 words for MVP.~~
- `[AMENDED 2026-03-30]:` **Content volume:** 30 words × 15 grades × 6 categories = **~2,700 words for MVP**.

## Data storage evolution

- **Decision:** Move from hardcoded Kotlin list → bundled `assets/words.json` → remote API (later).
- **Rationale:** JSON in assets is version-controlled, easily editable by non-developers, and requires no backend infrastructure. Kotlin serialization (already a dependency) handles parsing. Remote API only needed when content volume exceeds what's reasonable to bundle in the APK (~8K+ words, or when dynamic updates are needed without app releases).
- **Why not Room immediately:** Room is overkill for read-only bundled content. Add Room later for favorites, history, and offline caching of remote API responses.

`[2026-03-30]` Content strategy decision added.

`[2026-03-29]` Initialized.
