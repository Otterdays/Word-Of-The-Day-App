<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# SBOM — Word of the Day (Android)

`[AMENDED 2026-03-30]:` `:app` module now exists. Versions below are **declared** in `gradle/libs.versions.toml`. Lock exact transitive versions after the first successful `gradlew :app:dependencies` resolve.

Software Bill of Materials for the planned **Kotlin + Jetpack Compose** Android app. Versions below are the **target baseline** for the first Android module; lock exact transitive versions in this file after the first successful Gradle resolve.

**Last reviewed:** 2026-03-30  
`[AMENDED 2026-04-29]:` Toolchain pin refreshed — **`gradle/libs.versions.toml`** declares **`agp = "9.2.0"`** (**Android Gradle Plugin** rows below).

---

## Build toolchain

| Component | Target version | Role | Version / release page |
| --- | --- | --- | --- |
| **Kotlin** | `2.3.20` | Language + standard library | [GitHub release v2.3.20](https://github.com/JetBrains/kotlin/releases/tag/v2.3.20) · [Kotlin repo (source)](https://github.com/JetBrains/kotlin) · [What’s new in 2.3.20](https://kotlinlang.org/docs/whatsnew2320.html) · [Release blog](https://blog.jetbrains.com/kotlin/2026/03/kotlin-2-3-20-released/) |
| **Kotlin Gradle Plugin** (`org.jetbrains.kotlin.*`) | `2.3.20` | Android + JVM compilation in Gradle | [Plugin Portal: org.jetbrains.kotlin.android](https://plugins.gradle.org/plugin/org.jetbrains.kotlin.android/2.3.20) |

`[AMENDED 2026-03-30]:` The `org.jetbrains.kotlin.android` plugin has been **removed** from both root and `:app` build files — AGP **9.2.0** handles Kotlin compilation internally. `[NOTE]:` Was **9.1.0** when plugin removal landed; catalog now **`agp = "9.2.0"`**. The Compose Compiler plugin (`org.jetbrains.kotlin.plugin.compose`) and Serialization plugin remain.
| **Android Gradle Plugin (AGP)** | `9.2.0` | Android packaging, R8, resources | [AGP 9.2.0 release notes](https://developer.android.com/build/releases/agp-9-2-0-release-notes) · [About AGP](https://developer.android.com/build/releases/about-agp) |
| **Gradle** | ≥ `9.4.1` for AGP **9.2.x** (wrapper uses newer snapshot below — **≥ minimum**) | Build orchestration | [Gradle releases](https://gradle.org/releases/) · [Compatibility (AGP ↔ Gradle)](https://developer.android.com/build/releases/gradle-plugin) |
| **JDK** | `17` (AGP default; `21` OK if team standardizes) | Toolchain for Kotlin/AGP | [AGP JDK guidance](https://developer.android.com/studio/intro/studio-config#jdk) · [Eclipse Temurin](https://adoptium.net/) |

`[AMENDED 2026-03-30]:` **Pin:** Wrapper uses **Gradle 9.6 nightly** (see section below), above every AGP-required minimum.

`[AMENDED 2026-04-29]:` **Compatibility:** **AGP 9.2.x** needs **Gradle ≥ 9.4.1** ([compatibility](https://developer.android.com/build/releases/gradle-plugin)); nightly **9.6.x** qualifies. Historical: **AGP 9.1.x** paired with Gradle **9.3.1+**.

---

## Gradle wrapper (pinned nightly)

This repo’s **wrapper** uses a **snapshot / nightly** distribution from `services.gradle.org`, not a GA release only.

| Field | Value |
| --- | --- |
| **Full version string** | `9.6.0-20260330000154+0000` |
| **VCS revision** (from `gradlew --version`) | `ce9f7ea4b782dd4f6260415b5aa839c4b5f7b554` |
| **Distribution** | Binary snapshot (`-bin.zip`) |
| **Resolved URL** | `https://services.gradle.org/distributions-snapshots/gradle-9.6.0-20260330000154+0000-bin.zip` |
| **Declared in** | `gradle/wrapper/gradle-wrapper.properties` → `distributionUrl` |
| **User Manual (versioned docs)** | [Gradle 9.6.0 docs](https://docs.gradle.org/9.6.0/userguide/userguide.html) (when published for this line; nightly may track `current` for bleeding-edge) |

**Version / index links:** [Gradle distribution snapshots (index)](https://services.gradle.org/distributions-snapshots/) · [Gradle releases (GA builds)](https://gradle.org/releases/) · [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)

**Refresh wrapper to this snapshot (or a newer nightly):**

```bash
./gradlew wrapper --gradle-version=9.6.0-20260330000154+0000
```

On Windows, same command with `gradlew.bat`.

`NOTE:` Nightlies change often; when you adopt a newer snapshot, update **both** `gradle-wrapper.properties` and the **Full version string** row in this section.

---

`NOTE:` Before first CI green build, confirm **Kotlin ↔ Compose Compiler** alignment using [Compose Compiler release notes](https://developer.android.com/jetpack/androidx/releases/compose-compiler) and Kotlin release documentation (2.3.x may ship a matching compiler plugin version).

---

## Jetpack Compose (UI)

| Component | Target | Role | Version / release page |
| --- | --- | --- | --- |
| **Compose BOM** | `2026.03.01` | Aligns all `androidx.compose.*` artifacts | [Use a BOM](https://developer.android.com/develop/ui/compose/bom) · [BOM ↔ library mapping](https://developer.android.com/develop/ui/compose/bom/bom-mapping) · [Compose overview / per-group versions](https://developer.android.com/jetpack/androidx/releases/compose) |
| **Compose Material 3** | Via BOM | Theming, components, Material You | [compose-material3 releases](https://developer.android.com/jetpack/androidx/releases/compose-material3) |
| **Compose Material 3 – Window Size Class** | Via BOM | Breakpoints for adaptive layout (`calculateWindowSizeClass`) | [compose-material3 releases](https://developer.android.com/jetpack/androidx/releases/compose-material3) · artifact `material3-window-size-class` |
| **Compose UI / Foundation / Runtime** | Via BOM | Layout, gestures, core runtime | [compose-ui](https://developer.android.com/jetpack/androidx/releases/compose-ui) · [compose-foundation](https://developer.android.com/jetpack/androidx/releases/compose-foundation) · [compose-runtime](https://developer.android.com/jetpack/androidx/releases/compose-runtime) |

`[AMENDED 2026-04-29]:` **`material3-window-size-class`** declared in `gradle/libs.versions.toml` as **`compose-material3-window-size`** (Gradle catalog aliases cannot end with reserved substring **`class`**).

`[AMENDED 2026-03-30]:` Compose BOM **2026.03.01** now declared in `gradle/libs.versions.toml`.

---

## AndroidX (declared)

`[AMENDED 2026-03-30]:` Versions now declared in `gradle/libs.versions.toml`. TBD rows preserved for reference.

Add rows here when dependencies are declared in Gradle. Each link points at the canonical **AndroidX release notes** page (version anchors appear on those pages).

| Component | Target version | Role | Version / release page |
| --- | --- | --- | --- |
| **androidx.core:core-ktx** | `1.17.0` | Kotlin extensions, compatibility | [Core releases](https://developer.android.com/jetpack/androidx/releases/core) |
| **androidx.activity:activity-compose** | `1.13.0` | Compose in `ComponentActivity` | [Activity releases](https://developer.android.com/jetpack/androidx/releases/activity) |
| **androidx.lifecycle** (`lifecycle-viewmodel-compose`, `lifecycle-runtime-compose`) | `2.10.0` | ViewModel, lifecycle-aware components | [Lifecycle releases](https://developer.android.com/jetpack/androidx/releases/lifecycle) |
| **androidx.navigation:navigation-compose** | `2.9.0` | Typed navigation for Compose | [Navigation releases](https://developer.android.com/jetpack/androidx/releases/navigation) |
| **androidx.datastore:datastore-preferences** | `1.1.7` | User grade/category + onboarding flags | [DataStore releases](https://developer.android.com/jetpack/androidx/releases/datastore) |
| **androidx.compose.material:material-icons-extended** | Via Compose BOM | Extra Material icons (share, refresh, …) | [Compose BOM mapping](https://developer.android.com/develop/ui/compose/bom/bom-mapping) |
| **androidx.window:window** (optional) | TBD | Foldable / large-screen helpers | [Window releases](https://developer.android.com/jetpack/androidx/releases/window) |

`[2026-03-31]:` Root **`gradle.properties`** added with `org.gradle.jvmargs=-Xmx2048m` to avoid D8 heap exhaustion on `assembleDebug` with extended icon set.

`[AMENDED 2026-04-29]:` **`junit:junit`** **`4.13.2`** — JVM unit tests (**`GradeSearchOrderTest`**); declared in **`gradle/libs.versions.toml`** as **`libs.junit`**.

---

## Networking & data (optional — fill when chosen)

| Component | Target version | Role | Version / release page |
| --- | --- | --- | --- |
| **Retrofit** | TBD | REST API for “word of the day” source | [Square Retrofit releases](https://github.com/square/retrofit/releases) |
| **OkHttp** | TBD | HTTP client | [Square OkHttp releases](https://github.com/square/okhttp/releases) |
| **Kotlin Serialization** | `1.8.1` | JSON parsing for word data model | [Kotlin serialization docs](https://github.com/Kotlin/kotlinx.serialization) · [releases](https://github.com/Kotlin/kotlinx.serialization/releases) |
| **Room** | TBD | Offline cache / history | [Room releases](https://developer.android.com/jetpack/androidx/releases/room) |

---

## Kotlin ecosystem reference (SBOM context)

The **Kotlin compiler and language** artifacts are versioned with the same line as the Gradle plugin. Primary authoritative links:

- [JetBrains/kotlin on GitHub](https://github.com/JetBrains/kotlin) — source, `ChangeLog.md`, tags.
- [Kotlin releases (GitHub)](https://github.com/JetBrains/kotlin/releases) — all tagged versions including `v2.3.20`.

Pre-release Kotlin builds (only if you intentionally opt in):

- [Using `-dev` versions](https://github.com/JetBrains/kotlin/blob/master/ReadMe.md) (bootstrap Maven repo documented in Kotlin repo README).

---

## Next action

`[AMENDED 2026-03-30]:` Step 1 done — module exists. Steps 2–3 remain.

1. ~~Create the Android application module and run `./gradlew :app:dependencies` (or Windows `gradlew`).~~ DONE
2. Run `gradlew.bat :app:dependencies` and paste **resolved** versions of direct + critical transitive dependencies into new tables above (append; do not remove baseline rows).
3. Optionally enable Gradle [dependency verification](https://docs.gradle.org/current/userguide/dependency_verification.html) (`verification-metadata.xml`) for supply-chain consistency.
