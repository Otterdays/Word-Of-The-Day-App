package com.example.wordofday.ui.home

import android.app.Application
import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordofday.data.model.UserPreferences
import com.example.wordofday.data.model.WordEntry
import com.example.wordofday.data.preferences.EngagementRepository
import com.example.wordofday.data.preferences.UserPreferencesRepository
import com.example.wordofday.data.repository.LearningRepository
import com.example.wordofday.data.repository.WordRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// [TRACE: DOCS/ARCHITECTURE.md] — Presentation layer: ViewModel + UiState
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WordRepository(application)
    private val preferencesRepository = UserPreferencesRepository(application)
    private val engagementRepository = EngagementRepository(application)
    private val learningRepository = LearningRepository(application)

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _quickSwitchVisible = MutableStateFlow(false)
    val quickSwitchVisible: StateFlow<Boolean> = _quickSwitchVisible.asStateFlow()

    private var sessionGradeOffset = 0
    private var activeCategoryIndex = 0
    private var refreshOffset = 0
    private var loadJob: Job? = null
    private var tts: TextToSpeech? = null

    init {
        viewModelScope.launch {
            engagementRepository.recordDailyOpen()
        }
        viewModelScope.launch {
            preferencesRepository.preferences
                .distinctUntilChanged()
                .collect { prefs ->
                    sessionGradeOffset = 0
                    activeCategoryIndex = 0
                    refreshOffset = 0
                    loadWord(prefs, showFullLoading = true)
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val prefs = preferencesRepository.preferences.first()
            refreshOffset += 1
            loadWord(prefs, showFullLoading = false)
        }
    }

    fun showQuickSwitch() {
        _quickSwitchVisible.value = true
    }

    fun hideQuickSwitch() {
        _quickSwitchVisible.value = false
    }

    fun applyQuickSwitch(grade: com.example.wordofday.data.model.GradeLevel, categories: Set<com.example.wordofday.data.model.Category>) {
        viewModelScope.launch {
            preferencesRepository.setGradeLevel(grade)
            preferencesRepository.setSelectedCategories(categories)
            hideQuickSwitch()
        }
    }

    fun tryEasierWord() {
        shiftGrade(-1)
    }

    fun tryHarderWord() {
        shiftGrade(+1)
    }

    fun setActiveCategoryIndex(index: Int) {
        val state = _uiState.value as? HomeUiState.Success ?: return
        if (index !in state.categoryWords.indices) return
        activeCategoryIndex = index
        val selected = state.categoryWords[index]
        _uiState.value = state.copy(
            word = selected.word,
            activeCategoryIndex = index,
            isFavorite = false,
        )
        viewModelScope.launch {
            val fav = engagementRepository.isFavorite(repository.favoriteKey(selected.word))
            val current = _uiState.value as? HomeUiState.Success ?: return@launch
            _uiState.value = current.copy(isFavorite = fav)
        }
    }

    fun toggleFavorite() {
        val state = _uiState.value as? HomeUiState.Success ?: return
        viewModelScope.launch {
            val key = repository.favoriteKey(state.word)
            val nowFavorite = engagementRepository.toggleFavorite(key)
            _uiState.value = state.copy(isFavorite = nowFavorite)
        }
    }

    fun speakCurrentWord() {
        val state = _uiState.value as? HomeUiState.Success ?: return
        val app = getApplication<Application>()
        if (tts == null) {
            tts = TextToSpeech(app) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.getDefault()
                    tts?.speak(state.word.word, TextToSpeech.QUEUE_FLUSH, null, "word")
                }
            }
        } else {
            tts?.speak(state.word.word, TextToSpeech.QUEUE_FLUSH, null, "word")
        }
    }

    fun shareCurrentWord() {
        val state = _uiState.value as? HomeUiState.Success ?: return
        val text = ShareFormatter.format(state.word, state.effectiveGrade)
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

    private fun shiftGrade(delta: Int) {
        val state = _uiState.value as? HomeUiState.Success ?: return
        val nextOffset = sessionGradeOffset + delta
        val nextGrade = state.preferences.gradeLevel.withOffset(nextOffset)
        if (nextGrade == state.effectiveGrade && delta != 0) return
        sessionGradeOffset = nextOffset
        viewModelScope.launch {
            learningRepository.recordGradeShiftUsed()
            refreshOffset += 1
            loadWord(state.preferences, showFullLoading = false)
        }
    }

    private fun loadWord(prefs: UserPreferences, showFullLoading: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val previousSuccess = _uiState.value as? HomeUiState.Success
            if (showFullLoading || previousSuccess == null) {
                _uiState.value = HomeUiState.Loading
            } else {
                _uiState.value = previousSuccess.copy(
                    isRefreshing = true,
                    refreshErrorMessage = null,
                )
            }
            try {
                val today = LocalDate.now()
                val effectiveGrade = prefs.gradeLevel.withOffset(sessionGradeOffset)
                val categoryRows = repository.getWordsByCategory(
                    date = today,
                    preferences = prefs,
                    gradeLevelOverride = effectiveGrade,
                    rotationOffset = refreshOffset,
                )
                val safeIndex = activeCategoryIndex.coerceIn(0, (categoryRows.size - 1).coerceAtLeast(0))
                val activeWord = categoryRows.getOrNull(safeIndex)?.word
                    ?: repository.getWordForDate(
                        date = today,
                        preferences = prefs,
                        gradeLevelOverride = effectiveGrade,
                        rotationOffset = refreshOffset,
                    )
                val streak = engagementRepository.recordDailyOpen(today)
                engagementRepository.recordWordView(
                    date = today,
                    wordKey = repository.favoriteKey(activeWord),
                    word = activeWord.word,
                    gradeLevel = activeWord.gradeLevel,
                )
                val fav = engagementRepository.isFavorite(repository.favoriteKey(activeWord))
                val dashboard = learningRepository.loadDashboard()
                learningRepository.ensureMasterySeed(repository.favoriteKey(activeWord))
                learningRepository.checkAchievements()
                val formatted = today.format(
                    DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault()),
                )
                _uiState.value = HomeUiState.Success(
                    word = activeWord,
                    formattedDate = formatted,
                    preferences = prefs,
                    effectiveGrade = effectiveGrade,
                    sessionGradeOffset = sessionGradeOffset,
                    categoryWords = categoryRows.map { CategoryWordUi(it.category, it.word) },
                    activeCategoryIndex = safeIndex,
                    streakDays = streak,
                    isFavorite = fav,
                    dueReviewCount = dashboard.dueReviewCount,
                    masteredCount = dashboard.masteredCount,
                )
            } catch (e: Exception) {
                val message = e.message ?: "Something went wrong"
                if (previousSuccess == null) {
                    _uiState.value = HomeUiState.Error(message = message)
                } else {
                    _uiState.value = previousSuccess.copy(
                        isRefreshing = false,
                        refreshErrorMessage = message,
                    )
                }
            }
        }
    }

    override fun onCleared() {
        tts?.shutdown()
        tts = null
        super.onCleared()
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
