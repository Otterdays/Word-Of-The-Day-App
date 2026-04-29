package com.example.wordofday.data.repository

import android.content.Context
import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.UserPreferences
import com.example.wordofday.data.model.WordEntry
import com.example.wordofday.data.source.JsonWordDataSource
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// [TRACE: DOCS/ARCHITECTURE.md] — Data layer: repository merges sources
/**
 * Word of the day from bundled per-grade JSON under `assets/words/`.
 * If a grade file is empty or missing, search outward by grade (Roadmap §8c adjacent fallback).
 */
class WordRepository(context: Context) {

    private val dataSource = JsonWordDataSource(context.applicationContext)

    suspend fun getWordForDate(
        date: LocalDate = LocalDate.now(),
        preferences: UserPreferences,
    ): WordEntry = withContext(Dispatchers.IO) {
        for (grade in gradeSearchOrder(preferences.gradeLevel)) {
            val words = dataSource.loadWordsForGrade(grade)
            val pool = words.filterForPreferencesInGrade(preferences)
            if (pool.isNotEmpty()) {
                return@withContext pool[date.dayOfYear % pool.size]
            }
        }
        FALLBACK_WORD
    }

    suspend fun countWordsMatching(preferences: UserPreferences): Int =
        withContext(Dispatchers.IO) {
            val words = dataSource.loadWordsForGrade(preferences.gradeLevel)
            words.filterForPreferencesInGrade(preferences).size
        }

    private fun List<WordEntry>.filterForPreferencesInGrade(prefs: UserPreferences): List<WordEntry> {
        val byCategory = filter { entry ->
            entry.categories.any { cat -> cat in prefs.selectedCategories }
        }
        return when {
            byCategory.isNotEmpty() -> byCategory
            else -> this
        }
    }

    companion object {
        private val FALLBACK_WORD = WordEntry(
            word = "Serendipity",
            partOfSpeech = "noun",
            pronunciation = "/ˌserənˈdipədē/",
            definition = "The occurrence and development of events by chance in a happy or beneficial way",
            example = "A fortunate stroke of serendipity brought the two old friends together.",
            etymology = "Coined by Horace Walpole in 1754",
            gradeLevel = GradeLevel.ADULT,
        )
    }
}
