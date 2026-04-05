package com.example.wordofday.data.repository

import android.content.Context
import com.example.wordofday.data.model.UserPreferences
import com.example.wordofday.data.model.WordEntry
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

// [TRACE: DOCS/ARCHITECTURE.md] — Data layer: repository merges sources
/**
 * Provides the word of the day. Loads from bundled JSON assets;
 * swap with API/Room implementation later without touching the ViewModel.
 */
class WordRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }
    private var words: List<WordEntry>? = null

    private suspend fun loadWords(): List<WordEntry> = withContext(Dispatchers.IO) {
        words?.let { return@withContext it }

        try {
            val jsonString = context.assets.open("words.json").bufferedReader().use { it.readText() }
            val loadedWords = json.decodeFromString<List<WordEntry>>(jsonString)
            words = loadedWords
            loadedWords
        } catch (e: Exception) {
            listOf(
                WordEntry(
                    word = "Serendipity",
                    partOfSpeech = "noun",
                    pronunciation = "/ˌserənˈdipədē/",
                    definition = "The occurrence and development of events by chance in a happy or beneficial way",
                    example = "A fortunate stroke of serendipity brought the two old friends together.",
                    etymology = "Coined by Horace Walpole in 1754",
                ),
            )
        }
    }

    /**
     * Words matching grade and at least one selected category; falls back to grade-only, then full list.
     */
    suspend fun getWordForDate(
        date: LocalDate = LocalDate.now(),
        preferences: UserPreferences,
    ): WordEntry {
        val wordList = loadWords()
        val pool = wordList.filterForPreferences(preferences)
        if (pool.isEmpty()) return wordList[date.dayOfYear % wordList.size]
        val index = date.dayOfYear % pool.size
        return pool[index]
    }

    suspend fun countWordsMatching(preferences: UserPreferences): Int =
        loadWords().filterForPreferences(preferences).size

    private fun List<WordEntry>.filterForPreferences(prefs: UserPreferences): List<WordEntry> {
        val byGrade = filter { it.gradeLevel == prefs.gradeLevel }
        val byCategory = byGrade.filter { entry ->
            entry.categories.any { cat -> cat in prefs.selectedCategories }
        }
        return when {
            byCategory.isNotEmpty() -> byCategory
            byGrade.isNotEmpty() -> byGrade
            else -> this
        }
    }

}
