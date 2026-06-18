package com.example.wordofday.ui.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordofday.data.model.WordEntry
import com.example.wordofday.data.preferences.EngagementRepository
import com.example.wordofday.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// [TRACE: DOCS/ROADMAP.md] — §10a history + §10b favorites list
class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val wordRepository = WordRepository(application)
    private val engagementRepository = EngagementRepository(application)

    private val _resolvedFavorites = MutableStateFlow<List<LibraryWordItem>>(emptyList())
    val resolvedFavorites: StateFlow<List<LibraryWordItem>> = _resolvedFavorites.asStateFlow()

    val history = engagementRepository.wordHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refreshFavorites()
    }

    fun refreshFavorites() {
        viewModelScope.launch {
            val keys = engagementRepository.favoriteKeys.first()
            _resolvedFavorites.value = keys.mapNotNull { key ->
                val word = wordRepository.resolveKey(key) ?: return@mapNotNull null
                LibraryWordItem(key = key, word = word)
            }.sortedBy { it.word.word.lowercase() }
        }
    }

    suspend fun loadWordForKey(key: String): WordEntry? = wordRepository.resolveKey(key)

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
                        return LibraryViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}

data class LibraryWordItem(
    val key: String,
    val word: WordEntry,
)
