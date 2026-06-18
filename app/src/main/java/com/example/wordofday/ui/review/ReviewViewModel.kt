package com.example.wordofday.ui.review

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordofday.data.model.WordEntry
import com.example.wordofday.data.preferences.UserPreferencesRepository
import com.example.wordofday.data.repository.LearningRepository
import com.example.wordofday.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface ReviewUiState {
    data object Loading : ReviewUiState
    data class Empty(val message: String) : ReviewUiState
    data class Card(
        val word: WordEntry,
        val wordKey: String,
        val remaining: Int,
        val showDefinition: Boolean,
    ) : ReviewUiState
    data class SessionComplete(val reviewed: Int) : ReviewUiState
}

class ReviewViewModel(application: Application) : AndroidViewModel(application) {

    private val wordRepository = WordRepository(application)
    private val prefsRepository = UserPreferencesRepository(application)
    private val learningRepository = LearningRepository(application)

    private val _uiState = MutableStateFlow<ReviewUiState>(ReviewUiState.Loading)
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private var queue: List<Pair<String, WordEntry>> = emptyList()
    private var index = 0
    private var reviewed = 0
    private var showingDefinition = false

    init {
        loadQueue()
    }

    fun loadQueue() {
        viewModelScope.launch {
            _uiState.value = ReviewUiState.Loading
            val prefs = prefsRepository.preferences.first()
            val due = learningRepository.getDueMasteries(40)
            val favorites = com.example.wordofday.data.preferences.EngagementRepository(
                getApplication(),
            ).favoriteKeys.first()
            val keys = (due.map { it.wordKey } + favorites).distinct().take(30)
            val resolved = keys.mapNotNull { key ->
                wordRepository.resolveKey(key)?.let { key to it }
            }
            if (resolved.isEmpty()) {
                val seed = wordRepository.browseWords(prefs, 10)
                queue = seed.map { wordRepository.favoriteKey(it) to it }
            } else {
                queue = resolved
            }
            index = 0
            reviewed = 0
            showingDefinition = false
            showCurrent()
        }
    }

    fun flipCard() {
        val state = _uiState.value as? ReviewUiState.Card ?: return
        showingDefinition = !showingDefinition
        _uiState.value = state.copy(showDefinition = showingDefinition)
    }

    fun mark(correct: Boolean) {
        val state = _uiState.value as? ReviewUiState.Card ?: return
        viewModelScope.launch {
            learningRepository.recordReview(state.wordKey, correct)
            reviewed++
            index++
            showingDefinition = false
            showCurrent()
        }
    }

    private suspend fun showCurrent() {
        if (index >= queue.size) {
            _uiState.value = ReviewUiState.SessionComplete(reviewed)
            return
        }
        val (key, word) = queue[index]
        learningRepository.ensureMasterySeed(key)
        _uiState.value = ReviewUiState.Card(
            word = word,
            wordKey = key,
            remaining = queue.size - index,
            showDefinition = showingDefinition,
        )
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
                        return ReviewViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
