package com.example.wordofday.data.release

// [TRACE: DOCS/EDITIONS_ROADMAP.md] — in-app "What's New" source of truth (keep synced with CHANGELOG)
data class AppRelease(
    val versionCode: Int,
    val versionName: String,
    val editionName: String,
    val highlights: List<String>,
)

object AppReleaseCatalog {

    /** Newest first. Add a row for every shipped user-facing release. */
    val releases: List<AppRelease> = listOf(
        AppRelease(
            versionCode = 6,
            versionName = "0.2.4",
            editionName = "Zen UI",
            highlights = listOf(
                "Centered, calmer home layout with a readable max-width lane",
                "Settings and onboarding now stay centered on tablets and large screens",
                "Quiz, Library, and Word Detail screens use balanced content widths",
                "Cleaner alignment for empty states, question text, and preference chips",
            ),
        ),
        AppRelease(
            versionCode = 5,
            versionName = "0.2.3",
            editionName = "Open Lexicon",
            highlights = listOf(
                "Opt-in WordNet dictionary + thesaurus (10,000+ supplemental definitions)",
                "Myth & lore, sacred reference, and literary/historical vocabulary packs",
                "Settings toggles — extended sources off by default",
                "Age-rated content gates teen and adult historical topics",
                "Source attribution on every imported word card",
            ),
        ),
        AppRelease(
            versionCode = 4,
            versionName = "0.2.2",
            editionName = "Mega Corpus",
            highlights = listOf(
                "11,600+ vocabulary words — up from 2,200",
                "60 words per topic in all 14 categories (Cars, Space, Music, History, and more)",
                "Every grade from Pre-K through Adult fully stocked",
                "Two months of unique daily words per grade and interest area",
            ),
        ),
        AppRelease(
            versionCode = 3,
            versionName = "0.2.1",
            editionName = "Content Scale Wave 1",
            highlights = listOf(
                "2,200+ vocabulary words across all 15 grade levels",
                "30 words per topic in Technology, Sports, Food, Science, and Animals",
                "Age-appropriate definitions from Pre-K through Adult",
                "Richer middle and high school entries with synonyms and usage tips",
            ),
        ),
        AppRelease(
            versionCode = 2,
            versionName = "0.2.0",
            editionName = "Substance & Discovery",
            highlights = listOf(
                "Quiz mode — test yourself with 5 multiple-choice questions",
                "Library — history calendar, list view, and favorites",
                "Richer word cards with synonyms and usage tips",
                "What's New dialog when you update the app",
                "Home upgrades: quick-switch, easier/harder, category swipe, streak milestones",
            ),
        ),
        AppRelease(
            versionCode = 1,
            versionName = "0.1.0",
            editionName = "Foundation",
            highlights = listOf(
                "Word of the Day with grade levels and themed categories",
                "Onboarding, settings, and per-grade vocabulary",
                "Share, refresh, and adaptive phone/tablet layouts",
            ),
        ),
    )

    fun releaseForVersionCode(code: Int): AppRelease? =
        releases.firstOrNull { it.versionCode == code }

    fun pendingReleases(lastSeenVersionCode: Int, currentVersionCode: Int): List<AppRelease> =
        releases
            .filter { it.versionCode in (lastSeenVersionCode + 1)..currentVersionCode }
            .sortedByDescending { it.versionCode }
}
