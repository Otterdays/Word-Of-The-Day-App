package com.example.wordofday.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordofday.data.model.QuizMode
import com.example.wordofday.data.preferences.EngagementRepository
import com.example.wordofday.data.preferences.UserPreferencesRepository
import com.example.wordofday.data.quiz.QuizEngine
import com.example.wordofday.data.repository.LearningRepository
import com.example.wordofday.data.repository.WordRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val wordRepository = WordRepository(application)
    private val preferencesRepository = UserPreferencesRepository(application)
    private val engagementRepository = EngagementRepository(application)
    private val learningRepository = LearningRepository(application)

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        prepareModeSelect()
    }

    fun prepareModeSelect() {
        timerJob?.cancel()
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            try {
                val stats = engagementRepository.quizStats.first()
                _uiState.value = QuizUiState.ModeSelect(stats)
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Error(e.message ?: "Could not load quiz")
            }
        }
    }

    fun startMode(mode: QuizMode) {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            try {
                val prefs = preferencesRepository.preferences.first()
                val stats = engagementRepository.quizStats.first()
                val pool = wordRepository.getQuizPool(prefs)
                val questions = QuizEngine.buildSession(pool, mode)
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
                    mode = mode,
                    secondsLeft = if (mode == QuizMode.BLITZ) QuizEngine.BLITZ_SECONDS else null,
                )
                if (mode == QuizMode.BLITZ) startBlitzTimer()
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Error(e.message ?: "Could not start quiz")
            }
        }
    }

    fun selectOption(index: Int) {
        val state = _uiState.value as? QuizUiState.InProgress ?: return
        if (state.answered) return
        timerJob?.cancel()
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
            learningRepository.recordQuizAttempt(
                wordRepository.favoriteKey(state.current.promptWord),
                correct,
                state.mode,
            )

            if (state.isLast) {
                if (nextCorrect == state.questions.size) {
                    learningRepository.recordPerfectQuiz()
                }
                learningRepository.checkAchievements()
                _uiState.value = QuizUiState.Finished(
                    totalQuestions = state.questions.size,
                    sessionCorrect = nextCorrect,
                    stats = stats,
                    mode = state.mode,
                )
            } else {
                _uiState.value = state.copy(
                    index = state.index + 1,
                    sessionCorrect = nextCorrect,
                    sessionStreak = nextStreak,
                    selectedIndex = null,
                    answered = false,
                    stats = stats,
                    secondsLeft = if (state.mode == QuizMode.BLITZ) QuizEngine.BLITZ_SECONDS else null,
                )
                if (state.mode == QuizMode.BLITZ) startBlitzTimer()
            }
        }
    }

    private fun startBlitzTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000)
                val state = _uiState.value as? QuizUiState.InProgress ?: return@launch
                if (state.answered) return@launch
                val left = (state.secondsLeft ?: 0) - 1
                if (left <= 0) {
                    _uiState.value = state.copy(answered = true, selectedIndex = -1)
                    return@launch
                }
                _uiState.value = state.copy(secondsLeft = left)
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
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
