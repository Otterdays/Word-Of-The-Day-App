package com.example.wordofday.ui.settings

import java.util.Locale

internal fun formatReminderTime(hour: Int, minute: Int): String {
    val normalizedHour = hour.coerceIn(0, 23)
    val normalizedMinute = minute.coerceIn(0, 59)
    val amPm = if (normalizedHour < 12) "AM" else "PM"
    val displayHour = when (val mod = normalizedHour % 12) {
        0 -> 12
        else -> mod
    }
    return String.format(
        Locale.getDefault(),
        "%d:%02d %s",
        displayHour,
        normalizedMinute,
        amPm,
    )
}
