package com.example.wordofday.ui.home

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
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
