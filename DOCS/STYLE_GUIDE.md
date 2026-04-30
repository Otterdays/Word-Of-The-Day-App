<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Style guide — Word of the Day (Android)

## Language and naming

- **Kotlin** naming per [official style](https://kotlinlang.org/docs/coding-conventions.html): `PascalCase` types, `camelCase` members, `SCREAMING_SNAKE_CASE` only for `const val` if used.
- **Packages**: lowercase, no underscores (e.g. `com.example.wordofday.feature.home`).
- **Resources**: `snake_case` for XML/Compose resource names where applicable.

## UI code

- Prefer **stateless** composables: pass data and lambdas in; keep screen-level state in ViewModel.
- **Material 3** components; centralize color/typography in `Theme`.
- Prefer **`remember` + stable params** for expensive composable inputs; avoid unnecessary recompositions (measure if needed).

## Architecture comments

- Trace tag format: `// [TRACE: DOCS/filename.md]` only when linking non-obvious behavior to documentation.
- Comments explain **why**, not what.

## Constants and ownership

- Keep route string constants only in `ui/navigation/AppDestinations.kt`.
- Keep taxonomy constants in enums (`GradeLevel`, `Category`) instead of scattered strings.
- Keep dependency version constants only in `gradle/libs.versions.toml` (not duplicated in docs/code).
- Keep DataStore key constants private to `UserPreferencesRepository`.
- If a constant/source-of-truth location moves, update `DOCS/AGENT_WORKMAP.md` in the same change.

## Limits (project targets)

- Aim for **≤ 100** characters per line where practical.
- Prefer **small files**: split by screen or feature when a file grows past ~400 lines.

## Testing

- ViewModel tests use **coroutine test** dispatchers; fake repositories over mocks when possible.

`[2026-03-29]` Initial style guide for Kotlin/Compose Android work.

`[2026-04-30]` Added constants ownership rules and pointer to `DOCS/AGENT_WORKMAP.md` for future-agent handoffs.
