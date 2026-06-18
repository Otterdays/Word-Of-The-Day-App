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
            versionCode = 12,
            versionName = "0.3.5",
            editionName = "Daily Habit",
            highlights = listOf(
                "Opt-in daily word notifications at your chosen reminder time",
                "WorkManager schedules the next reminder after each delivery and after reboot",
                "Settings debug build button sends an instant test notification",
            ),
        ),
        AppRelease(
            versionCode = 11,
            versionName = "0.3.4",
            editionName = "Bright UI",
            highlights = listOf(
                "More words now advances through your full topic pool instead of bouncing between two picks",
                "Refresh keeps your current interest selected — swipe the pager to change topics",
            ),
        ),
        AppRelease(
            versionCode = 10,
            versionName = "0.3.3",
            editionName = "Bright UI",
            highlights = listOf(
                "Default theme is now crisp bright white with sharp contrast for easier reading",
                "Tap the sun or moon icon on the top left of Home to switch light and dark mode",
                "Your theme choice is remembered across app launches",
            ),
        ),
        AppRelease(
            versionCode = 9,
            versionName = "0.3.2",
            editionName = "Flow Polish",
            highlights = listOf(
                "Keep tapping More words to cycle through smarter randomized picks from your grade and interests",
                "Selecting more interests now broadens the visible refresh rotation across topics",
                "Home refresh now keeps the current card on screen while the next word loads",
                "Crowded home action buttons wrap cleanly on small phones and large-text settings",
            ),
        ),
        AppRelease(
            versionCode = 8,
            versionName = "0.3.1",
            editionName = "Depth & Interests",
            highlights = listOf(
                "53 interest topics in six browsable sections — search Gaming, Ocean, AI, Fashion, and more",
                "Pick up to eight interests; new tags map to the full corpus intelligently",
                "Rich word enrichment fixes thin auto-generated entries like stream, podcast, and emoji",
                "Tighter, more compact home and word cards — less scrolling, same depth",
            ),
        ),
        AppRelease(
            versionCode = 7,
            versionName = "0.3.0",
            editionName = "Maturity Edition",
            highlights = listOf(
                "Explore — search 22,000+ words across curated corpus and opt-in lexicon",
                "Spaced review deck with SM-2 scheduling and a Progress dashboard",
                "Quiz modes: Classic, Reverse (definition → word), and 15-second Blitz",
                "Achievements, weekly stats, user sentences, and mark-as-hard on word cards",
                "All 14 interest categories in settings; pick up to five at once",
                "Library search on history list and favorites (A–Z sorted)",
            ),
        ),
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
