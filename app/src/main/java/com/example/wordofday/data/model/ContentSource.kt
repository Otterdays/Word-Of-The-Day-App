package com.example.wordofday.data.model

import kotlinx.serialization.Serializable

// [TRACE: DOCS/CONTENT_SOURCES.md] — licensed import provenance
@Serializable
enum class ContentSource(val displayLabel: String, val attribution: String) {
    CURATED(
        displayLabel = "Curated",
        attribution = "Original Word of the Day editorial corpus.",
    ),
    WORDNET(
        displayLabel = "Open Dictionary",
        attribution = "Open English WordNet (CC BY 4.0), derived from Princeton WordNet © Princeton University. " +
            "Pronunciations, etymologies and examples from Wiktionary (CC BY-SA 4.0) via kaikki.org; " +
            "pronunciation fallback from the CMU Pronouncing Dictionary (BSD).",
    ),
    MYTHOLOGY(
        displayLabel = "Myth & Lore",
        attribution = "Public-domain classical mythology references (Bulfinch, Homer summaries).",
    ),
    SACRED_TEXT(
        displayLabel = "Sacred Reference",
        attribution = "Example phrases from public-domain King James Bible (1611).",
    ),
    LITERARY_HISTORICAL(
        displayLabel = "Literary & Historical",
        attribution = "Public-domain literature and historical vocabulary references.",
    ),
    VINTAGE(
        displayLabel = "Webster 1913",
        attribution = "Webster's Revised Unabridged Dictionary (1913), public domain via Project Gutenberg.",
    ),
}

@Serializable
enum class ContentRating {
    ALL_AGES,
    TEEN,
    ADULT,
}
