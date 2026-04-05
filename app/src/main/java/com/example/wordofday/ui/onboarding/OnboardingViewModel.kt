package com.example.wordofday.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordofday.data.model.Category
import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.UserPreferences
import com.example.wordofday.data.preferences.UserPreferencesRepository
import com.example.wordofday.ui.preferences.toggleCategorySelection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = UserPreferencesRepository(application)

    private val _step = MutableStateFlow(0)
    val step: StateFlow<Int> = _step.asStateFlow()

    private val _grade = MutableStateFlow(GradeLevel.GRADE_6)
    val grade: StateFlow<GradeLevel> = _grade.asStateFlow()

    private val _categories = MutableStateFlow<Set<Category>>(setOf(Category.GENERAL))
    val categories: StateFlow<Set<Category>> = _categories.asStateFlow()

    fun setGrade(level: GradeLevel) {
        _grade.value = level
    }

    fun toggleCategory(category: Category) {
        _categories.value = _categories.value.toggleCategorySelection(category)
    }

    fun goToCategories() {
        _step.value = 1
    }

    fun goBackToGrade() {
        _step.value = 0
    }

    fun complete(onDone: () -> Unit) {
        viewModelScope.launch {
            prefsRepo.completeOnboarding(
                UserPreferences(
                    onboardingComplete = true,
                    gradeLevel = _grade.value,
                    selectedCategories = _categories.value,
                ),
            )
            onDone()
        }
    }

    fun skip(onDone: () -> Unit) {
        viewModelScope.launch {
            prefsRepo.skipOnboarding()
            onDone()
        }
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
                        return OnboardingViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
