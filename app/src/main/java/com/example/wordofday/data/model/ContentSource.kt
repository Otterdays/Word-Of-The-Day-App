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
        displayLabel = "WordNet",
        attribution = "Princeton WordNet © Princeton University. WordNet License.",
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
}

@Serializable
enum class ContentRating {
    ALL_AGES,
    TEEN,
    ADULT,
}
