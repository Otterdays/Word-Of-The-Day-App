package com.example.wordofday.data.source

import android.content.Context
import com.example.wordofday.data.model.ContentRating
import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.LexiconPreferences
import com.example.wordofday.data.model.WordEntry
import java.io.FileNotFoundException
import java.util.concurrent.ConcurrentHashMap
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

// [TRACE: DOCS/CONTENT_SOURCES.md] — core grade JSON + optional lexicon packs
/**
 * Loads `[WordEntry]` from `assets/words/<grade>.json` plus optional imports under
 * `assets/lexicon/` when [LexiconPreferences] opt-in flags are set.
 */
class JsonWordDataSource(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }
    private val coreCache = ConcurrentHashMap<GradeLevel, List<WordEntry>>()
    private val mergedCache = ConcurrentHashMap<String, List<WordEntry>>()

    fun loadWordsForGrade(
        grade: GradeLevel,
        lexicon: LexiconPreferences = LexiconPreferences(),
    ): List<WordEntry> {
        val cacheKey = "${grade.name}|${lexiconCacheKey(lexicon)}"
        return mergedCache.getOrPut(cacheKey) {
            mergeDistinct(loadCore(grade), loadSupplemental(grade, lexicon))
        }
    }

    private fun loadCore(grade: GradeLevel): List<WordEntry> =
        coreCache.getOrPut(grade) { loadFromAsset(grade.bundledWordsAssetPath, grade) }

    private fun loadSupplemental(grade: GradeLevel, lexicon: LexiconPreferences): List<WordEntry> {
        if (!lexicon.anyEnabled()) return emptyList()
        val out = mutableListOf<WordEntry>()
        if (lexicon.includeWordNet) {
            out += loadFromAsset("lexicon/wordnet/${grade.wordsAssetBaseName}.json", grade)
        }
        if (lexicon.includeMythology) {
            out += loadPackFiltered("lexicon/packs/mythology.json", grade)
        }
        if (lexicon.includeSacredReference) {
            out += loadPackFiltered("lexicon/packs/sacred_reference.json", grade)
        }
        if (lexicon.includeLiteraryHistorical) {
            out += loadPackFiltered("lexicon/packs/literary_historical.json", grade)
        }
        return out
    }

    private fun loadPackFiltered(assetPath: String, grade: GradeLevel): List<WordEntry> {
        val minOrdinal = gradeMinOrdinal(grade)
        return loadFromAsset(assetPath, grade).filter { entry ->
            gradeMinOrdinal(entry.gradeLevel) <= minOrdinal + 2 ||
                entry.gradeLevel.ordinal <= grade.ordinal
        }
    }

    private fun gradeMinOrdinal(grade: GradeLevel): Int = grade.ordinal

    private fun loadFromAsset(relativePath: String, grade: GradeLevel): List<WordEntry> {
        return try {
            val text = context.assets.open(relativePath).bufferedReader().use { it.readText() }
            val entries = json.decodeFromString<List<WordEntry>>(text)
            if (relativePath.startsWith("words/")) {
                entries.map { it.copy(gradeLevel = grade) }
            } else {
                entries
            }
        } catch (_: FileNotFoundException) {
            emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun mergeDistinct(core: List<WordEntry>, extra: List<WordEntry>): List<WordEntry> {
        val seen = mutableSetOf<String>()
        val out = mutableListOf<WordEntry>()
        for (entry in core + extra) {
            val key = entry.word.trim().lowercase()
            if (key.isEmpty() || key in seen) continue
            seen.add(key)
            out.add(entry)
        }
        return out
    }

    private fun lexiconCacheKey(lexicon: LexiconPreferences): String =
        buildString {
            append(if (lexicon.includeWordNet) 'w' else '-')
            append(if (lexicon.includeMythology) 'm' else '-')
            append(if (lexicon.includeSacredReference) 's' else '-')
            append(if (lexicon.includeLiteraryHistorical) 'l' else '-')
        }

    companion object {
        fun maxContentRatingForGrade(grade: GradeLevel): ContentRating = when (grade) {
            GradeLevel.PRE_K,
            GradeLevel.KINDERGARTEN,
            GradeLevel.GRADE_1,
            GradeLevel.GRADE_2,
            -> ContentRating.ALL_AGES

            GradeLevel.GRADE_3,
            GradeLevel.GRADE_4,
            GradeLevel.GRADE_5,
            GradeLevel.GRADE_6,
            -> ContentRating.TEEN

            else -> ContentRating.ADULT
        }

        fun isRatingAllowed(entry: WordEntry, maxGrade: GradeLevel): Boolean {
            val cap = maxContentRatingForGrade(maxGrade)
            return entry.contentRating.ordinal <= cap.ordinal
        }
    }
}
