package com.example.wordofday.data.repository

import com.example.wordofday.data.model.Category
import com.example.wordofday.data.model.ContentRating
import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.UserPreferences
import com.example.wordofday.data.model.WordEntry
import com.example.wordofday.data.source.JsonWordDataSource

internal fun List<WordEntry>.filterForPreferencesInGrade(prefs: UserPreferences): List<WordEntry> {
    val gradeCap = prefs.gradeLevel
    val ageSafe = filter { JsonWordDataSource.isRatingAllowed(it, gradeCap) }
    val expanded = Category.expandedMatchSelection(prefs.selectedCategories)
    val byCategory = ageSafe.filter { entry ->
        entry.categories.any { cat -> cat in expanded }
    }
    return when {
        byCategory.isNotEmpty() -> byCategory
        else -> ageSafe
    }
}

internal fun ContentRating.allows(other: ContentRating): Boolean =
    other.ordinal <= this.ordinal

internal fun GradeLevel.maxContentRating(): ContentRating =
    JsonWordDataSource.maxContentRatingForGrade(this)
