package com.example.wordofday.ui.home

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordofday.data.model.UserPreferences
import com.example.wordofday.data.preferences.UserPreferencesRepository
import com.example.wordofday.data.repository.WordRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// [TRACE: DOCS/ARCHITECTURE.md] — Presentation layer: ViewModel + UiState
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WordRepository(application)
    private val preferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.preferences
                .distinctUntilChanged()
                .collect { prefs -> loadWord(prefs) }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val prefs = preferencesRepository.preferences.first()
            loadWord(prefs)
        }
    }

    fun shareCurrentWord() {
        val state = _uiState.value
        if (state !is HomeUiState.Success) return
        val w = state.word
        val text = buildString {
            append(w.word)
            append(" (${w.partOfSpeech})\n\n")
            append(w.definition)
            if (w.example.isNotBlank()) {
                append("\n\nExample: ")
                append(w.example)
            }
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        getApplication<Application>().startActivity(chooser)
    }

    private fun loadWord(prefs: UserPreferences) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val today = LocalDate.now()
                val word = repository.getWordForDate(today, prefs)
                val formatted = today.format(
                    DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault()),
                )
                _uiState.value = HomeUiState.Success(
                    word = word,
                    formattedDate = formatted,
                    preferences = prefs,
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    message = e.message ?: "Something went wrong",
                )
            }
        }
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                        return HomeViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
