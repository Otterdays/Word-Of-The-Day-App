package com.example.wordofday.data.model

// [TRACE: DOCS/CONTENT_SOURCES.md] — opt-in imported lexicon packs
data class LexiconPreferences(
    val includeWordNet: Boolean = false,
    val includeMythology: Boolean = false,
    val includeSacredReference: Boolean = false,
    val includeLiteraryHistorical: Boolean = false,
) {
    fun anyEnabled(): Boolean =
        includeWordNet || includeMythology || includeSacredReference || includeLiteraryHistorical
}
