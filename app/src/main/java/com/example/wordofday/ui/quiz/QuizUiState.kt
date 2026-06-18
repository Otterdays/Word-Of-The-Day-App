package com.example.wordofday.ui.quiz

import com.example.wordofday.data.model.QuizQuestion
import com.example.wordofday.data.model.QuizStats

sealed interface QuizUiState {
    data object Loading : QuizUiState

    data class Ready(
        val questions: List<QuizQuestion>,
        val stats: QuizStats,
    ) : QuizUiState

    data class InProgress(
        val questions: List<QuizQuestion>,
        val index: Int,
        val sessionCorrect: Int,
        val sessionStreak: Int,
        val selectedIndex: Int?,
        val answered: Boolean,
        val stats: QuizStats,
    ) : QuizUiState {
        val current: QuizQuestion get() = questions[index]
        val isLast: Boolean get() = index == questions.lastIndex
    }

    data class Finished(
        val totalQuestions: Int,
        val sessionCorrect: Int,
        val stats: QuizStats,
    ) : QuizUiState

    data class Error(val message: String) : QuizUiState
}
