package com.example.wordofday.ui.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordofday.data.model.WordEntry
import com.example.wordofday.data.preferences.UserPreferencesRepository
import com.example.wordofday.data.repository.LearningRepository
import com.example.wordofday.data.repository.WordRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class ExploreViewModel(application: Application) : AndroidViewModel(application) {

    private val wordRepository = WordRepository(application)
    private val prefsRepository = UserPreferencesRepository(application)
    private val learningRepository = LearningRepository(application)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<WordEntry>>(emptyList())
    val results: StateFlow<List<WordEntry>> = _results.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        viewModelScope.launch {
            _query.debounce(250).distinctUntilChanged().collect { q ->
                search(q)
            }
        }
    }

    fun setQuery(value: String) {
        _query.value = value
    }

    fun recordOpened() {
        viewModelScope.launch { learningRepository.recordExploreOpen() }
    }

    private suspend fun search(q: String) {
        _loading.value = true
        try {
            val prefs = prefsRepository.preferences.first()
            _results.value = wordRepository.searchWords(q, prefs)
        } finally {
            _loading.value = false
        }
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
                        return ExploreViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
