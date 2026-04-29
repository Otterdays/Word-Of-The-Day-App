<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# SCRATCHPAD

## Active focus

- `[AMENDED 2026-03-30]:` Android `:app` module created — MVVM shell with Compose UI is bootable. Kotlin **2.3.20** + AGP **9.1.0** + Compose BOM **2026.03.01**.
- Android **Word of the Day** app planning; Kotlin **2.3.20** + Jetpack Compose per [SBOM.md](./SBOM.md).

## Blockers

- None documented.

## Last actions (most recent first)

1. `[2026-04-05]` **Git:** `git init`, root `.gitignore` (Gradle `build/`, `local.properties`, `.hprof`, `/src/` stray sample, `bin/`), commit `chore: initial commit`, remote `origin` → [Word-Of-The-Day-App](https://github.com/Otterdays/Word-Of-The-Day-App), pushed `main`.
2. `[2026-03-31]` **Nav + layout + §8–10 foundation:** Compose `NavHost` (onboarding → home, settings); DataStore `UserPreferencesRepository`; `GradeLevel` / `Category` / tagged `words.json`; `WordRepository` filters by prefs; onboarding (grade + MVP categories) + settings (reset, match count); home top app bar, `safeDrawing`-style padding via scaffold, ≥600dp two-column word/detail, refresh/share buttons, preference chips. Added `gradle.properties` (2G heap), `material-icons-extended`, SBOM rows.
3. `[2026-03-31]` ROADMAP: **Status snapshot** + checkbox sync with bootable `:app` (words.json, MVVM, M3); noted open: git + `.gitignore`, Navigation, timezone doc, tablet/a11y/tests. `[AMENDED 2026-04-05]:` git + `.gitignore` done.
4. `[2026-03-30]` Finalized per-grade granularity: ROADMAP §8–10 fully rewritten with 15 grade levels (Pre-K through Adult), 14 categories, detailed checklists for data model, JSON schema, content quality guidelines, onboarding UX, and engagement features. Updated My_Thoughts.
5. `[2026-03-30]` Brainstormed content strategy: grade levels (K–Adult, 10 tiers), themed categories (14 total), data model changes, phased content volume targets. Updated ROADMAP §8–10, My_Thoughts, created `content_strategy.md` artifact.
6. `[2026-03-30]` Removed `org.jetbrains.kotlin.android` plugin from root and `:app` build files — AGP **9.1.0** handles Kotlin compilation internally; redundant plugin caused sync issues.
7. `[2026-03-30]` Created `:app` Android module: `build.gradle.kts` with AGP 9.1.0, Kotlin 2.3.20, Compose BOM 2026.03.01. Version catalog at `gradle/libs.versions.toml`. Converted root from Java app to multi-module Android project.
8. `[2026-03-30]` Added full MVVM app shell: `MainActivity`, `HomeViewModel`, `HomeScreen` (staggered animations), `WordRepository` (14 bundled words), Material 3 theme (ink/gold palette + dynamic color), sealed `HomeUiState`, adaptive launcher icon.
9. `[2026-03-30]` SBOM: pinned **Gradle 9.6 nightly** `9.6.0-20260330000154+0000` documented; verified `gradle-wrapper.properties` uses `distributions-snapshots` URL.
10. `[2026-03-29]` Added DOCS: SBOM, SUMMARY, ROADMAP, ARCHITECTURE, STYLE_GUIDE, CHANGELOG, My_Thoughts; verified Kotlin 2.3.20 and AGP 9.1.0 references via official pages.
11. `[2026-04-29]` **Hygiene/docs/metadata:** tightened `.gitignore` (ignore `.idea/` + `.vscode/`), untracked committed IDE noise, fixed README clone/run commands, amended `DOCS/SUMMARY.md`, added `LICENSE` + GitHub issue/PR templates.

## Next steps

1. ~~Verify build compiles (`gradlew :app:assembleDebug`) and test on emulator.~~ `[2026-03-31]:` `assembleDebug` green after heap bump; re-run after major Gradle/dep changes.
2. ~~Add `GradeLevel` enum (15 values) + `Category` enum (14 values) to data model (Roadmap §8a, §8b).~~ DONE `[2026-03-31]`
3. ~~Build onboarding flow: grade picker → category multi-select (Roadmap §9a).~~ DONE `[2026-03-31]` (2-step + skip)
4. Split `assets/words.json` into `assets/words/*.json` per grade + schema from Roadmap §8c; add `JsonWordDataSource` if file IO grows.
5. Populate MVP word content: ~30 words × 15 grades × 6 categories = **~2,700 words**.
6. Wire §9c home enhancements (quick-switch sheet, easier/harder) and §10 engagement as needed.
7. ~~Add `com.android.application` module and Gradle config aligned with SBOM.~~ DONE
8. ~~Implement Compose shell + first Word of the Day screen.~~ DONE

## Out-of-scope observations

- `[AMENDED 2026-03-30]:` Root project has been converted from Java app to Android multi-module parent. Old `src/main/java/com/example/App.java` is now orphaned (not in any module) — can be deleted.
- `[AMENDED 2026-03-31]:` ~~Root project is still a **Java application** module; Android migration is a discrete future task.~~ Superseded — `:app` is the Android module; root is Gradle parent only.
