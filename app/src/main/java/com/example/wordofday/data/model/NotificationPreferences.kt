package com.example.wordofday.data.model

// [TRACE: DOCS/EDITIONS_ROADMAP.md] — daily reminder prefs
data class NotificationPreferences(
    val enabled: Boolean,
    val hour: Int,
    val minute: Int,
)
