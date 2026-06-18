package com.example.wordofday.ui.home

import com.example.wordofday.data.model.Category
import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.UserPreferences
import com.example.wordofday.data.model.WordEntry

/**
 * Sealed hierarchy for the home screen's UI state.
 * ViewModel exposes this via StateFlow; composables observe it.
 */
sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val word: WordEntry,
        val formattedDate: String,
        val preferences: UserPreferences,
        val effectiveGrade: GradeLevel,
        val sessionGradeOffset: Int,
        val categoryWords: List<CategoryWordUi>,
        val activeCategoryIndex: Int,
        val streakDays: Int,
        val isFavorite: Boolean,
        val dueReviewCount: Int = 0,
        val masteredCount: Int = 0,
        val isRefreshing: Boolean = false,
        val refreshErrorMessage: String? = null,
    ) : HomeUiState {
        val canTryEasier: Boolean
            get() = effectiveGrade.canShiftEasier()

        val canTryHarder: Boolean
            get() = effectiveGrade.canShiftHarder()
    }

    data class Error(val message: String) : HomeUiState
}

data class CategoryWordUi(
    val category: Category,
    val word: WordEntry,
)
