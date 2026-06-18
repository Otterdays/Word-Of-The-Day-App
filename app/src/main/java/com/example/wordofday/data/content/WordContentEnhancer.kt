package com.example.wordofday.data.content

import android.content.Context
import com.example.wordofday.data.model.WordEntry
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

// [TRACE: DOCS/CONTENT_SOURCES.md] — runtime enrichment for thin auto-generated rows
class WordContentEnhancer(context: Context) {

    private val appContext = context.applicationContext
    private val catalogRef = AtomicReference<WordEnrichmentCatalog?>(null)
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun enrich(entry: WordEntry): WordEntry = withContext(Dispatchers.IO) {
        val catalog = catalogRef.get() ?: loadCatalog().also { catalogRef.set(it) }
        enrichInternal(entry, catalog)
    }

    fun enrichBlocking(entry: WordEntry): WordEntry {
        val catalog = catalogRef.get() ?: loadCatalogSync().also { catalogRef.set(it) }
        return enrichInternal(entry, catalog)
    }

    private fun enrichInternal(entry: WordEntry, catalog: WordEnrichmentCatalog): WordEntry {
        val key = entry.word.lowercase()
        val patch = catalog.words[key]
        val merged = entry.copy(
            definition = patch?.definition?.takeIf { it.isNotBlank() } ?: entry.definition,
            example = patch?.example?.takeIf { it.isNotBlank() } ?: entry.example,
            pronunciation = patch?.pronunciation?.takeIf { it.isNotBlank() } ?: entry.pronunciation,
            etymology = patch?.etymology?.takeIf { it.isNotBlank() } ?: entry.etymology,
            usageNote = patch?.usageNote?.takeIf { it.isNotBlank() } ?: entry.usageNote,
            synonyms = patch?.synonyms?.filter { it.isNotBlank() }?.takeIf { it.isNotEmpty() }
                ?: WordContentQuality.cleanedSynonyms(entry),
        )
        return if (!WordContentQuality.isThin(merged)) merged else softenThinEntry(merged, patch != null)
    }

    private fun softenThinEntry(entry: WordEntry, hadPatch: Boolean): WordEntry {
        if (hadPatch) return entry
        val cleanedSyns = WordContentQuality.cleanedSynonyms(entry)
        val note = when {
            entry.usageNote.isNotBlank() &&
                !entry.usageNote.contains("topics at this reading level", ignoreCase = true) -> entry.usageNote
            else -> "Tip: enable WordNet under Settings → Extended sources, or search Explore for deeper definitions."
        }
        return entry.copy(
            synonyms = cleanedSyns,
            usageNote = note,
        )
    }

    private suspend fun loadCatalog(): WordEnrichmentCatalog = withContext(Dispatchers.IO) {
        loadCatalogSync()
    }

    private fun loadCatalogSync(): WordEnrichmentCatalog = try {
        appContext.assets.open("word_enrichment/overrides.json").use { stream ->
            json.decodeFromString<WordEnrichmentCatalog>(stream.bufferedReader().readText())
        }
    } catch (_: Exception) {
        WordEnrichmentCatalog()
    }
}
