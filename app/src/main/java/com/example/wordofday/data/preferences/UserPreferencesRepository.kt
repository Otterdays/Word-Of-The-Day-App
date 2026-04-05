package com.example.wordofday.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.wordofday.data.model.Category
import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// [TRACE: DOCS/ROADMAP.md] — §9d DataStore preferences
private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences",
)

private object Keys {
    val onboardingComplete = booleanPreferencesKey("onboarding_complete")
    val gradeLevel = stringPreferencesKey("grade_level")
    val categoriesCsv = stringPreferencesKey("categories_csv")
}

class UserPreferencesRepository(private val context: Context) {

    val preferences: Flow<UserPreferences> = context.userPreferencesDataStore.data.map { p ->
        p.toUserPreferences()
    }

    suspend fun completeOnboarding(prefs: UserPreferences) {
        context.userPreferencesDataStore.edit { m ->
            m[Keys.onboardingComplete] = true
            m[Keys.gradeLevel] = prefs.gradeLevel.name
            val normalized = prefs.selectedCategories.normalizeCategorySelection()
            m[Keys.categoriesCsv] = normalized.encodeCsv()
        }
    }

    suspend fun skipOnboarding() {
        completeOnboarding(UserPreferences.skipOnboarding())
    }

    suspend fun setGradeLevel(grade: GradeLevel) {
        context.userPreferencesDataStore.edit { it[Keys.gradeLevel] = grade.name }
    }

    suspend fun setSelectedCategories(categories: Set<Category>) {
        val normalized = categories.normalizeCategorySelection()
        context.userPreferencesDataStore.edit { it[Keys.categoriesCsv] = normalized.encodeCsv() }
    }

    suspend fun resetPreferences() {
        val defaults = UserPreferences.settingsDefault()
        context.userPreferencesDataStore.edit { m ->
            m[Keys.gradeLevel] = defaults.gradeLevel.name
            m[Keys.categoriesCsv] = defaults.selectedCategories.encodeCsv()
        }
    }

    private fun Preferences.toUserPreferences(): UserPreferences {
        val onboarding = this[Keys.onboardingComplete] ?: false
        val gradeStr = this[Keys.gradeLevel]
        val grade = gradeStr?.let { runCatching { GradeLevel.valueOf(it) }.getOrNull() }
            ?: GradeLevel.GRADE_6
        val csv = this[Keys.categoriesCsv]
        val categories = csv.decodeCategoriesCsv()
        return UserPreferences(
            onboardingComplete = onboarding,
            gradeLevel = grade,
            selectedCategories = categories,
        )
    }

    private fun Set<Category>.encodeCsv(): String =
        sortedBy { it.ordinal }.joinToString(",") { it.name }

    private fun String?.decodeCategoriesCsv(): Set<Category> {
        if (isNullOrBlank()) return setOf(Category.GENERAL)
        val parsed = split(',').mapNotNull { token ->
            runCatching { Category.valueOf(token.trim()) }.getOrNull()
        }.toSet()
        return parsed.normalizeCategorySelection()
    }

    private fun Set<Category>.normalizeCategorySelection(): Set<Category> {
        val base = if (isEmpty()) setOf(Category.GENERAL) else this
        return base.toList().sortedBy { it.ordinal }.take(MAX_CATEGORY_SELECTION).toSet()
    }

    companion object {
        private const val MAX_CATEGORY_SELECTION = 3
    }
}
