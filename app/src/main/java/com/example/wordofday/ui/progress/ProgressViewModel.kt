package com.example.wordofday.ui.progress

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordofday.data.model.AchievementProgress
import com.example.wordofday.data.model.LearningDashboard
import com.example.wordofday.data.repository.LearningRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProgressUiState {
    data object Loading : ProgressUiState
    data class Ready(
        val dashboard: LearningDashboard,
        val achievements: List<AchievementProgress>,
    ) : ProgressUiState
}

class ProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val learningRepository = LearningRepository(application)

    private val _uiState = MutableStateFlow<ProgressUiState>(ProgressUiState.Loading)
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = ProgressUiState.Loading
            val dashboard = learningRepository.loadDashboard()
            val achievements = learningRepository.loadAchievements()
            _uiState.value = ProgressUiState.Ready(dashboard, achievements)
        }
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
                        return ProgressViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
