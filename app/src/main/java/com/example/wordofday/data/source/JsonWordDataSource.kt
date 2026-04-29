package com.example.wordofday.data.source

import android.content.Context
import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.WordEntry
import java.io.FileNotFoundException
import java.util.concurrent.ConcurrentHashMap
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

// [TRACE: DOCS/ARCHITECTURE.md] — Data layer: parse bundled grade files once, cache by grade
/**
 * Loads `[WordEntry]` lists from `assets/words/<grade>.json`. Entries omit redundant `gradeLevel`;
 * it is filled from the file’s grade for authoring simplicity.
 */
class JsonWordDataSource(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }
    private val cache = ConcurrentHashMap<GradeLevel, List<WordEntry>>()

    fun loadWordsForGrade(grade: GradeLevel): List<WordEntry> =
        cache.getOrPut(grade) { loadFromAsset(grade) }

    private fun loadFromAsset(grade: GradeLevel): List<WordEntry> {
        return try {
            val path = grade.bundledWordsAssetPath
            val text = context.assets.open(path).bufferedReader().use { it.readText() }
            val entries = json.decodeFromString<List<WordEntry>>(text)
            entries.map { it.copy(gradeLevel = grade) }
        } catch (_: FileNotFoundException) {
            emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
