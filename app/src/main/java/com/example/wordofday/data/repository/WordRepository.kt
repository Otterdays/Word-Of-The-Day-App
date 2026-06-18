package com.example.wordofday.data.repository

import android.content.Context
import com.example.wordofday.data.content.WordContentEnhancer
import com.example.wordofday.data.model.Category
import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.LexiconPreferences
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

    private val appContext = context.applicationContext
    private val dataSource = JsonWordDataSource(appContext)
    private val enhancer = WordContentEnhancer(appContext)

    suspend fun getWordForDate(
        date: LocalDate = LocalDate.now(),
        preferences: UserPreferences,
        gradeLevelOverride: GradeLevel? = null,
        rotationOffset: Int = 0,
        categorySalt: Int = 0,
    ): WordEntry = withContext(Dispatchers.IO) {
        val primaryGrade = gradeLevelOverride ?: preferences.gradeLevel
        for (grade in gradeSearchOrder(primaryGrade)) {
            val words = dataSource.loadWordsForGrade(grade, preferences.lexicon)
            val pool = words.filterForPreferencesInGrade(preferences)
            if (pool.isNotEmpty()) {
                val index = selectionIndex(
                    dayOfYear = date.dayOfYear,
                    rotationOffset = rotationOffset,
                    poolSize = pool.size,
                    salt = categorySalt,
                )
                return@withContext pool[index].present()
            }
        }
        FALLBACK_WORD
    }

    /** One word per selected category for horizontal swipe (Roadmap §9c). */
    suspend fun getWordsByCategory(
        date: LocalDate,
        preferences: UserPreferences,
        gradeLevelOverride: GradeLevel? = null,
        rotationOffset: Int = 0,
    ): List<CategoryWord> = withContext(Dispatchers.IO) {
        val grade = gradeLevelOverride ?: preferences.gradeLevel
        preferences.selectedCategories
            .sortedBy { it.ordinal }
            .map { category ->
                val scoped = preferences.copy(
                    gradeLevel = grade,
                    selectedCategories = setOf(category),
                )
                CategoryWord(
                    category = category,
                    word = getWordForDate(
                        date = date,
                        preferences = scoped,
                        gradeLevelOverride = grade,
                        rotationOffset = rotationOffset,
                        categorySalt = category.ordinal,
                    ),
                )
            }
    }

    fun favoriteKey(entry: WordEntry): String =
        "${entry.gradeLevel.name}|${entry.word.lowercase()}"

    fun parseFavoriteKey(key: String): Pair<GradeLevel, String>? {
        val idx = key.indexOf('|')
        if (idx <= 0) return null
        val grade = runCatching { GradeLevel.valueOf(key.substring(0, idx)) }.getOrNull() ?: return null
        val lemma = key.substring(idx + 1)
        if (lemma.isBlank()) return null
        return grade to lemma
    }

    suspend fun resolveKey(key: String): WordEntry? = withContext(Dispatchers.IO) {
        val (grade, lemma) = parseFavoriteKey(key) ?: return@withContext null
        findWord(grade, lemma)
    }

    suspend fun findWord(grade: GradeLevel, lemma: String): WordEntry? =
        withContext(Dispatchers.IO) {
            val normalized = lemma.lowercase()
            val fullLexicon = preferencesLexiconAll()
            dataSource.loadWordsForGrade(grade, fullLexicon)
                .find { it.word.lowercase() == normalized }
                ?.present()
        }

    /** All words at user's grade (± adjacent fallback) matching category prefs — for quiz pool. */
    suspend fun getQuizPool(preferences: UserPreferences, minSize: Int = 4): List<WordEntry> =
        withContext(Dispatchers.IO) {
            for (grade in gradeSearchOrder(preferences.gradeLevel)) {
                val words = dataSource.loadWordsForGrade(grade, preferences.lexicon)
                val pool = words.filterForPreferencesInGrade(preferences).distinctBy { it.word.lowercase() }
                if (pool.size >= minSize) return@withContext pool.presentAll()
            }
            val all = gradeSearchOrder(preferences.gradeLevel)
                .flatMap { dataSource.loadWordsForGrade(it, preferences.lexicon) }
                .distinctBy { it.word.lowercase() }
            all.filterForPreferencesInGrade(preferences).ifEmpty { all }.presentAll()
        }

    data class CategoryWord(
        val category: Category,
        val word: WordEntry,
    )

    suspend fun countWordsMatching(preferences: UserPreferences): Int =
        withContext(Dispatchers.IO) {
            val words = dataSource.loadWordsForGrade(preferences.gradeLevel, preferences.lexicon)
            words.filterForPreferencesInGrade(preferences).size
        }

    suspend fun browseWords(
        preferences: UserPreferences,
        limit: Int = 150,
    ): List<WordEntry> = withContext(Dispatchers.IO) {
        expandedPool(preferences).take(limit).presentAll()
    }

    suspend fun searchWords(
        query: String,
        preferences: UserPreferences,
        limit: Int = 80,
    ): List<WordEntry> = withContext(Dispatchers.IO) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return@withContext browseWords(preferences, limit)
        expandedPool(preferences).filter { entry ->
            entry.word.lowercase().contains(q) ||
                entry.definition.lowercase().contains(q) ||
                entry.synonyms.any { it.lowercase().contains(q) }
        }.take(limit).presentAll()
    }

    private suspend fun expandedPool(preferences: UserPreferences): List<WordEntry> {
        val merged = gradeSearchOrder(preferences.gradeLevel)
            .flatMap { dataSource.loadWordsForGrade(it, preferences.lexicon) }
            .distinctBy { it.word.lowercase() }
        val filtered = merged.filterForPreferencesInGrade(preferences)
        return filtered.ifEmpty { merged }
    }

    private suspend fun WordEntry.present(): WordEntry = enhancer.enrich(this)

    private suspend fun List<WordEntry>.presentAll(): List<WordEntry> = map { it.present() }

    companion object {
        private fun preferencesLexiconAll() = LexiconPreferences(
            includeWordNet = true,
            includeMythology = true,
            includeSacredReference = true,
            includeLiteraryHistorical = true,
        )
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

internal fun selectionIndex(
    dayOfYear: Int,
    rotationOffset: Int,
    poolSize: Int,
    salt: Int = 0,
): Int {
    if (poolSize <= 0) return 0
    val seed = dayOfYear.toLong() * 1_103_515_245L +
        rotationOffset.toLong() * 2_654_435_761L +
        salt.toLong() * 97_531L
    return Math.floorMod(seed, poolSize.toLong()).toInt()
}
