package com.example.wordofday.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.wordofday.data.model.NotificationPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// [TRACE: DOCS/EDITIONS_ROADMAP.md] — E5 daily habit notifications
private val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "notification_preferences",
)

private object NotificationKeys {
    val enabled = booleanPreferencesKey("daily_enabled")
    val hour = intPreferencesKey("reminder_hour")
    val minute = intPreferencesKey("reminder_minute")
}

class NotificationPreferencesRepository(private val context: Context) {

    val preferences: Flow<NotificationPreferences> = context.notificationDataStore.data.map { prefs ->
        NotificationPreferences(
            enabled = prefs[NotificationKeys.enabled] ?: false,
            hour = prefs[NotificationKeys.hour] ?: DEFAULT_HOUR,
            minute = prefs[NotificationKeys.minute] ?: DEFAULT_MINUTE,
        )
    }

    suspend fun setEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { prefs ->
            prefs[NotificationKeys.enabled] = enabled
        }
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        context.notificationDataStore.edit { prefs ->
            prefs[NotificationKeys.hour] = hour.coerceIn(0, 23)
            prefs[NotificationKeys.minute] = minute.coerceIn(0, 59)
        }
    }

    companion object {
        const val DEFAULT_HOUR = 8
        const val DEFAULT_MINUTE = 0
    }
}
