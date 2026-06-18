package com.example.wordofday.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordofday.data.preferences.EngagementRepository
import com.example.wordofday.data.preferences.UserPreferencesRepository
import com.example.wordofday.data.quiz.QuizEngine
import com.example.wordofday.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// [TRACE: DOCS/ROADMAP.md] — §10c quiz mode
class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val wordRepository = WordRepository(application)
    private val preferencesRepository = UserPreferencesRepository(application)
    private val engagementRepository = EngagementRepository(application)

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        startSession()
    }

    fun startSession() {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            try {
                val prefs = preferencesRepository.preferences.first()
                val stats = engagementRepository.quizStats.first()
                val pool = wordRepository.getQuizPool(prefs)
                val questions = QuizEngine.buildSession(pool)
                if (questions.isEmpty()) {
                    _uiState.value = QuizUiState.Error(
                        "Need at least 4 words in your grade/topics to quiz. Try adding categories in Settings.",
                    )
                    return@launch
                }
                _uiState.value = QuizUiState.InProgress(
                    questions = questions,
                    index = 0,
                    sessionCorrect = 0,
                    sessionStreak = 0,
                    selectedIndex = null,
                    answered = false,
                    stats = stats,
                )
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Error(e.message ?: "Could not start quiz")
            }
        }
    }

    fun selectOption(index: Int) {
        val state = _uiState.value as? QuizUiState.InProgress ?: return
        if (state.answered) return
        _uiState.value = state.copy(selectedIndex = index, answered = true)
    }

    fun advance() {
        val state = _uiState.value as? QuizUiState.InProgress ?: return
        if (!state.answered) return

        viewModelScope.launch {
            val correct = state.selectedIndex == state.current.correctIndex
            val nextStreak = if (correct) state.sessionStreak + 1 else 0
            val nextCorrect = state.sessionCorrect + if (correct) 1 else 0
            val stats = engagementRepository.recordQuizAnswer(correct, nextStreak)

            if (state.isLast) {
                _uiState.value = QuizUiState.Finished(
                    totalQuestions = state.questions.size,
                    sessionCorrect = nextCorrect,
                    stats = stats,
                )
            } else {
                _uiState.value = state.copy(
                    index = state.index + 1,
                    sessionCorrect = nextCorrect,
                    sessionStreak = nextStreak,
                    selectedIndex = null,
                    answered = false,
                    stats = stats,
                )
            }
        }
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
                        return QuizViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
