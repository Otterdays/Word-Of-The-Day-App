package com.example.wordofday.data.model

// [TRACE: DOCS/ROADMAP.md] — §9d preferences
data class UserPreferences(
    val onboardingComplete: Boolean,
    val gradeLevel: GradeLevel,
    val selectedCategories: Set<Category>,
    val lexicon: LexiconPreferences = LexiconPreferences(),
) {
    companion object {
        fun firstLaunchPlaceholder(): UserPreferences = UserPreferences(
            onboardingComplete = false,
            gradeLevel = GradeLevel.GRADE_6,
            selectedCategories = setOf(Category.GENERAL),
        )

        fun skipOnboarding(): UserPreferences = UserPreferences(
            onboardingComplete = true,
            gradeLevel = GradeLevel.ADULT,
            selectedCategories = setOf(Category.GENERAL),
        )

        fun settingsDefault(): UserPreferences = UserPreferences(
            onboardingComplete = true,
            gradeLevel = GradeLevel.ADULT,
            selectedCategories = setOf(Category.GENERAL),
        )
    }
}
