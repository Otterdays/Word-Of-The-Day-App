package com.example.wordofday.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// [TRACE: DOCS/STYLE_GUIDE.md] — app theme preference (default: bright light)
private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "theme_preferences",
)

private object ThemeKeys {
    val darkTheme = booleanPreferencesKey("dark_theme")
}

class ThemePreferencesRepository(private val context: Context) {

    val isDarkTheme: Flow<Boolean> = context.themeDataStore.data.map { prefs ->
        prefs[ThemeKeys.darkTheme] ?: false
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[ThemeKeys.darkTheme] = enabled
        }
    }

    suspend fun toggleDarkTheme() {
        context.themeDataStore.edit { prefs ->
            val current = prefs[ThemeKeys.darkTheme] ?: false
            prefs[ThemeKeys.darkTheme] = !current
        }
    }
}
