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
                    dayKey = date.toEpochDay().toInt(),
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
        // Walk grades nearest-first and stop once we have enough hits, so a 100k+ lexicon
        // never has to be loaded into memory all at once.
        val seen = HashSet<String>()
        val wordHits = mutableListOf<WordEntry>()
        val otherHits = mutableListOf<WordEntry>()
        for (grade in gradeSearchOrder(preferences.gradeLevel)) {
            for (entry in dataSource.loadWordsForGrade(grade, preferences.lexicon)) {
                val key = entry.word.lowercase()
                if (key in seen) continue
                when {
                    key.contains(q) -> { seen += key; wordHits += entry }
                    otherHits.size < limit && (
                        entry.definition.lowercase().contains(q) ||
                            entry.synonyms.any { it.lowercase().contains(q) }
                        ) -> { seen += key; otherHits += entry }
                }
            }
            if (wordHits.size >= limit) break
        }
        // Exact match first, then prefix matches, then other lemma hits, then definition hits.
        wordHits.sortedWith(
            compareBy<WordEntry>(
                { it.word.lowercase() != q },
                { !it.word.lowercase().startsWith(q) },
                { it.word.length },
            ),
        ).plus(otherHits).take(limit).presentAll()
    }

    private suspend fun expandedPool(preferences: UserPreferences): List<WordEntry> {
        // Nearest grades only — enough for browsing without loading the whole lexicon.
        val merged = gradeSearchOrder(preferences.gradeLevel)
            .take(BROWSE_GRADE_SPAN)
            .flatMap { dataSource.loadWordsForGrade(it, preferences.lexicon) }
            .distinctBy { it.word.lowercase() }
        val filtered = merged.filterForPreferencesInGrade(preferences)
        return filtered.ifEmpty { merged }
    }

    private suspend fun WordEntry.present(): WordEntry = enhancer.enrich(this)

    private suspend fun List<WordEntry>.presentAll(): List<WordEntry> = map { it.present() }

    companion object {
        private const val BROWSE_GRADE_SPAN = 3
        private fun preferencesLexiconAll() = LexiconPreferences(
            includeWordNet = true,
            includeMythology = true,
            includeSacredReference = true,
            includeLiteraryHistorical = true,
            includeVintage = true,
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

/**
 * Deterministic pick. Keyed on epoch day (not day-of-year) so the same calendar
 * date gets a different word each year instead of repeating annually.
 */
internal fun selectionIndex(
    dayKey: Int,
    rotationOffset: Int,
    poolSize: Int,
    salt: Int = 0,
): Int {
    if (poolSize <= 0) return 0
    var hash = dayKey xor
        (rotationOffset * 0x9E3779B9.toInt()) xor
        (salt * 0x85EBCA6B.toInt())
    hash = hash xor (hash shl 16)
    hash = hash xor (hash ushr 16)
    hash *= 0x7FEB352D
    hash = hash xor (hash ushr 15)
    return Math.floorMod(hash, poolSize)
}
