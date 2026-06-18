package com.example.wordofday.data.model

import kotlinx.serialization.Serializable

// [TRACE: DOCS/ROADMAP.md] — §8b categories; §8b MVP uses first six
@Serializable
enum class Category(val displayLabel: String) {
    MYTHOLOGY("Myth & Lore"),
    LITERATURE("Literature"),
    PHILOSOPHY("Philosophy"),
    SACRED("Sacred Reference"),
    GENERAL("General"),
    TECH("Technology"),
    SPORTS("Sports"),
    CARS("Cars & Vehicles"),
    FOOD("Food & Cooking"),
    SCIENCE("Science"),
    SPACE("Space"),
    ANIMALS("Animals"),
    MUSIC("Music & Arts"),
    HISTORY("History"),
    MATH("Math & Numbers"),
    HEALTH("Health & Body"),
    WEATHER("Weather"),
    EMOTIONS("Emotions");

    companion object {
        /** Roadmap MVP: six categories for picker UI and early content. */
        val MvpCategories: List<Category> = listOf(
            GENERAL,
            TECH,
            SPORTS,
            FOOD,
            SCIENCE,
            ANIMALS,
        )

        /** Opt-in imported packs (Settings → Extended sources). */
        val ExtendedCategories: List<Category> = listOf(
            MYTHOLOGY,
            LITERATURE,
            PHILOSOPHY,
            SACRED,
            HISTORY,
        )
    }
}
