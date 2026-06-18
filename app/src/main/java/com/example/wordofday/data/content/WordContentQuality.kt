package com.example.wordofday.data.content

import com.example.wordofday.data.model.WordEntry
import kotlinx.serialization.Serializable

// [TRACE: DOCS/CONTENT_SOURCES.md] — curated patches for auto-filled corpus rows
@Serializable
data class WordEnrichmentPatch(
    val definition: String? = null,
    val example: String? = null,
    val pronunciation: String? = null,
    val etymology: String? = null,
    val synonyms: List<String>? = null,
    val usageNote: String? = null,
)

@Serializable
data class WordEnrichmentCatalog(
    val words: Map<String, WordEnrichmentPatch> = emptyMap(),
)

object WordContentQuality {

    private val placeholderDefinitionHints = listOf(
        "frequent in advanced coursework",
        "as an action or process",
        "in the tech domain",
        "in the sports domain",
        "Engineers debated how",
        "Analysts linked",
        "related term",
    )

    fun isThin(entry: WordEntry): Boolean =
        entry.synonyms.any { it.equals("related term", ignoreCase = true) } ||
            entry.synonyms.any { it.equals(entry.word, ignoreCase = true) } ||
            placeholderDefinitionHints.any { hint -> entry.definition.contains(hint, ignoreCase = true) } ||
            entry.example.contains("Engineers debated how", ignoreCase = true) ||
            entry.example.contains("Analysts linked", ignoreCase = true) ||
            (entry.usageNote.contains("Common in", ignoreCase = true) &&
                entry.usageNote.contains("topics at this reading level", ignoreCase = true))

    fun cleanedSynonyms(entry: WordEntry): List<String> =
        entry.synonyms
            .filterNot { it.equals("related term", ignoreCase = true) }
            .filterNot { it.equals(entry.word, ignoreCase = true) }
            .distinct()
}
