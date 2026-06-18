package com.example.wordofday.data.model

import kotlinx.serialization.Serializable

// [TRACE: DOCS/ROADMAP.md] — §8b categories + expanded interest tags
@Serializable
enum class Category(
    val displayLabel: String,
    val group: InterestGroup = InterestGroup.POPULAR,
) {
    GENERAL("General", InterestGroup.POPULAR),
    EMOTIONS("Emotions", InterestGroup.POPULAR),
    TECH("Technology", InterestGroup.STEM),
    SCIENCE("Science", InterestGroup.STEM),
    MATH("Math & Numbers", InterestGroup.STEM),
    SPACE("Space", InterestGroup.STEM),
    CODING("Coding & Software", InterestGroup.STEM),
    AI_DATA("AI & Data", InterestGroup.STEM),
    CYBER("Cybersecurity", InterestGroup.STEM),
    ENGINEERING("Engineering", InterestGroup.STEM),
    ROBOTICS("Robotics", InterestGroup.STEM),
    AVIATION("Aviation & Flight", InterestGroup.STEM),
    GAMING("Gaming & Esports", InterestGroup.STEM),
    SPORTS("Sports", InterestGroup.LIFE),
    FITNESS("Fitness & Training", InterestGroup.LIFE),
    HEALTH("Health & Body", InterestGroup.LIFE),
    NUTRITION("Nutrition", InterestGroup.LIFE),
    FOOD("Food & Cooking", InterestGroup.LIFE),
    WEATHER("Weather", InterestGroup.LIFE),
    NATURE("Nature & Wildlife", InterestGroup.LIFE),
    GARDENING("Gardening", InterestGroup.LIFE),
    OCEAN("Ocean & Marine", InterestGroup.LIFE),
    OUTDOORS("Outdoors & Camping", InterestGroup.LIFE),
    PETS("Pets & Animals", InterestGroup.LIFE),
    ANIMALS("Animals", InterestGroup.LIFE),
    CARS("Cars & Vehicles", InterestGroup.LIFE),
    MUSIC("Music & Arts", InterestGroup.CREATIVE),
    ART("Art & Museums", InterestGroup.CREATIVE),
    DESIGN("Design & UX", InterestGroup.CREATIVE),
    FASHION("Fashion & Style", InterestGroup.CREATIVE),
    PHOTOGRAPHY("Photography", InterestGroup.CREATIVE),
    MOVIES("Movies & TV", InterestGroup.CREATIVE),
    BOOKS("Books & Writing", InterestGroup.CREATIVE),
    PODCASTS("Podcasts & Audio", InterestGroup.CREATIVE),
    COMEDY("Comedy & Humor", InterestGroup.CREATIVE),
    DIY("DIY & Crafts", InterestGroup.CREATIVE),
    HISTORY("History", InterestGroup.WORLD),
    TRAVEL("Travel & Places", InterestGroup.WORLD),
    CULTURE("Culture & Society", InterestGroup.WORLD),
    LANGUAGES("Languages", InterestGroup.WORLD),
    WORLD("World & Geography", InterestGroup.WORLD),
    MYTHOLOGY("Myth & Lore", InterestGroup.WORLD),
    LITERATURE("Literature", InterestGroup.ACADEMIC),
    PHILOSOPHY("Philosophy", InterestGroup.ACADEMIC),
    SACRED("Sacred Reference", InterestGroup.ACADEMIC),
    BUSINESS("Business", InterestGroup.ACADEMIC),
    FINANCE("Money & Finance", InterestGroup.ACADEMIC),
    CAREERS("Careers & Jobs", InterestGroup.ACADEMIC),
    PSYCHOLOGY("Psychology", InterestGroup.ACADEMIC),
    RELATIONSHIPS("Relationships", InterestGroup.ACADEMIC),
    MINDFULNESS("Mindfulness", InterestGroup.ACADEMIC),
    LAW("Law & Justice", InterestGroup.ACADEMIC),
    POLITICS("Politics & Civics", InterestGroup.ACADEMIC);

    companion object {
        /** Roadmap MVP: six categories for early content targeting. */
        val MvpCategories: List<Category> = listOf(
            GENERAL,
            TECH,
            SPORTS,
            FOOD,
            SCIENCE,
            ANIMALS,
        )

        /** All interests in onboarding/settings, grouped then A–Z within group. */
        val AllPickerCategories: List<Category> =
            InterestGroup.entries.flatMap { group ->
                entries.filter { it.group == group }.sortedBy { it.displayLabel }
            }

        val InterestSections: List<Pair<InterestGroup, List<Category>>> =
            InterestGroup.entries.mapNotNull { group ->
                val items = entries.filter { it.group == group }
                if (items.isEmpty()) null else group to items.sortedBy { it.displayLabel }
            }

        /** Opt-in imported packs (Settings → Extended sources). */
        val ExtendedCategories: List<Category> = listOf(
            MYTHOLOGY,
            LITERATURE,
            PHILOSOPHY,
            SACRED,
            HISTORY,
        )

        /** Categories whose corpus tags should satisfy a selected interest. */
        fun expandedMatchSelection(selected: Set<Category>): Set<Category> {
            if (selected.isEmpty()) return setOf(GENERAL)
            return selected.flatMap { interest -> ContentAffinity[interest] ?: setOf(interest) }.toSet()
        }
    }
}

/** Maps newer interest tags to corpus categories until JSON is fully tagged. */
private val ContentAffinity: Map<Category, Set<Category>> = mapOf(
    Category.GAMING to setOf(Category.GAMING, Category.TECH, Category.GENERAL),
    Category.CODING to setOf(Category.CODING, Category.TECH, Category.GENERAL),
    Category.AI_DATA to setOf(Category.AI_DATA, Category.TECH, Category.SCIENCE, Category.GENERAL),
    Category.CYBER to setOf(Category.CYBER, Category.TECH, Category.GENERAL),
    Category.ENGINEERING to setOf(Category.ENGINEERING, Category.TECH, Category.SCIENCE, Category.GENERAL),
    Category.ROBOTICS to setOf(Category.ROBOTICS, Category.TECH, Category.ENGINEERING, Category.GENERAL),
    Category.AVIATION to setOf(Category.AVIATION, Category.SPACE, Category.ENGINEERING, Category.GENERAL),
    Category.MOVIES to setOf(Category.MOVIES, Category.LITERATURE, Category.MUSIC, Category.GENERAL),
    Category.BOOKS to setOf(Category.BOOKS, Category.LITERATURE, Category.GENERAL),
    Category.PODCASTS to setOf(Category.PODCASTS, Category.TECH, Category.MUSIC, Category.GENERAL),
    Category.NATURE to setOf(Category.NATURE, Category.SCIENCE, Category.ANIMALS, Category.WEATHER, Category.GENERAL),
    Category.GARDENING to setOf(Category.GARDENING, Category.NATURE, Category.FOOD, Category.GENERAL),
    Category.OCEAN to setOf(Category.OCEAN, Category.NATURE, Category.SCIENCE, Category.ANIMALS, Category.GENERAL),
    Category.OUTDOORS to setOf(Category.OUTDOORS, Category.NATURE, Category.SPORTS, Category.GENERAL),
    Category.PETS to setOf(Category.PETS, Category.ANIMALS, Category.GENERAL),
    Category.FITNESS to setOf(Category.FITNESS, Category.SPORTS, Category.HEALTH, Category.GENERAL),
    Category.NUTRITION to setOf(Category.NUTRITION, Category.FOOD, Category.HEALTH, Category.GENERAL),
    Category.ART to setOf(Category.ART, Category.MUSIC, Category.GENERAL),
    Category.DESIGN to setOf(Category.DESIGN, Category.ART, Category.TECH, Category.GENERAL),
    Category.FASHION to setOf(Category.FASHION, Category.ART, Category.GENERAL),
    Category.PHOTOGRAPHY to setOf(Category.PHOTOGRAPHY, Category.ART, Category.TECH, Category.GENERAL),
    Category.COMEDY to setOf(Category.COMEDY, Category.EMOTIONS, Category.GENERAL),
    Category.DIY to setOf(Category.DIY, Category.ART, Category.TECH, Category.GENERAL),
    Category.TRAVEL to setOf(Category.TRAVEL, Category.WORLD, Category.CULTURE, Category.HISTORY, Category.GENERAL),
    Category.CULTURE to setOf(Category.CULTURE, Category.HISTORY, Category.WORLD, Category.GENERAL),
    Category.LANGUAGES to setOf(Category.LANGUAGES, Category.LITERATURE, Category.GENERAL),
    Category.WORLD to setOf(Category.WORLD, Category.HISTORY, Category.CULTURE, Category.GENERAL),
    Category.BUSINESS to setOf(Category.BUSINESS, Category.FINANCE, Category.CAREERS, Category.GENERAL),
    Category.FINANCE to setOf(Category.FINANCE, Category.BUSINESS, Category.MATH, Category.GENERAL),
    Category.CAREERS to setOf(Category.CAREERS, Category.BUSINESS, Category.GENERAL),
    Category.PSYCHOLOGY to setOf(Category.PSYCHOLOGY, Category.EMOTIONS, Category.HEALTH, Category.GENERAL),
    Category.RELATIONSHIPS to setOf(Category.RELATIONSHIPS, Category.EMOTIONS, Category.PSYCHOLOGY, Category.GENERAL),
    Category.MINDFULNESS to setOf(Category.MINDFULNESS, Category.HEALTH, Category.EMOTIONS, Category.GENERAL),
    Category.LAW to setOf(Category.LAW, Category.POLITICS, Category.HISTORY, Category.GENERAL),
    Category.POLITICS to setOf(Category.POLITICS, Category.HISTORY, Category.LAW, Category.GENERAL),
)
