package com.example.wordofday.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordofday.data.model.Category
import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.LexiconPreferences
import com.example.wordofday.data.model.NotificationPreferences
import com.example.wordofday.data.model.UserPreferences
import com.example.wordofday.data.preferences.NotificationPreferencesRepository
import com.example.wordofday.data.preferences.UserPreferencesRepository
import com.example.wordofday.data.repository.LearningRepository
import com.example.wordofday.data.repository.WordRepository
import com.example.wordofday.notification.DailyNotificationScheduler
import com.example.wordofday.notification.WordNotificationHelper
import com.example.wordofday.ui.preferences.toggleCategorySelection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = UserPreferencesRepository(application)
    private val notificationRepo = NotificationPreferencesRepository(application)
    private val wordRepository = WordRepository(application)
    private val learningRepository = LearningRepository(application)

    val preferences: StateFlow<UserPreferences> = prefsRepo.preferences.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UserPreferences.settingsDefault(),
    )

    val notificationPreferences: StateFlow<NotificationPreferences> =
        notificationRepo.preferences.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            NotificationPreferences(
                enabled = false,
                hour = NotificationPreferencesRepository.DEFAULT_HOUR,
                minute = NotificationPreferencesRepository.DEFAULT_MINUTE,
            ),
        )

    private val _matchingWordCount = MutableStateFlow<Int?>(null)
    val matchingWordCount: StateFlow<Int?> = _matchingWordCount.asStateFlow()

    init {
        viewModelScope.launch {
            prefsRepo.preferences.collect { prefs ->
                _matchingWordCount.value = wordRepository.countWordsMatching(prefs)
            }
        }
    }

    fun setGrade(grade: GradeLevel) {
        viewModelScope.launch { prefsRepo.setGradeLevel(grade) }
    }

    fun toggleCategory(category: Category) {
        viewModelScope.launch {
            val current = preferences.value.selectedCategories
            val next = current.toggleCategorySelection(category)
            prefsRepo.setSelectedCategories(next)
            if (next.size >= 8) {
                learningRepository.recordFiveCategoriesSelected()
            }
            learningRepository.checkAchievements()
        }
    }

    fun resetPreferences() {
        viewModelScope.launch { prefsRepo.resetPreferences() }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            notificationRepo.setEnabled(enabled)
            val reminder = notificationRepo.preferences.first()
            if (enabled) {
                DailyNotificationScheduler.scheduleNext(
                    getApplication(),
                    reminder.hour,
                    reminder.minute,
                )
            } else {
                DailyNotificationScheduler.cancel(getApplication())
            }
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            notificationRepo.setReminderTime(hour, minute)
            val reminder = notificationRepo.preferences.first()
            if (reminder.enabled) {
                DailyNotificationScheduler.scheduleNext(
                    getApplication(),
                    reminder.hour,
                    reminder.minute,
                )
            }
        }
    }

    fun sendTestNotification() {
        viewModelScope.launch {
            val userPrefs = prefsRepo.preferences.first()
            val word = wordRepository.getWordForDate(preferences = userPrefs)
            WordNotificationHelper.showDailyWord(getApplication(), word, isTest = true)
        }
    }

    fun setLexiconWordNet(enabled: Boolean) = updateLexicon { it.copy(includeWordNet = enabled) }

    fun setLexiconMythology(enabled: Boolean) = updateLexicon { it.copy(includeMythology = enabled) }

    fun setLexiconSacred(enabled: Boolean) = updateLexicon { it.copy(includeSacredReference = enabled) }

    fun setLexiconLiterary(enabled: Boolean) = updateLexicon { it.copy(includeLiteraryHistorical = enabled) }

    private fun updateLexicon(transform: (LexiconPreferences) -> LexiconPreferences) {
        viewModelScope.launch {
            val next = transform(preferences.value.lexicon)
            prefsRepo.setLexiconPreferences(next)
        }
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                        return SettingsViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
