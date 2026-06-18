package com.example.wordofday.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.wordofday.data.release.AppRelease
import com.example.wordofday.data.release.AppReleaseCatalog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// [TRACE: DOCS/EDITIONS_ROADMAP.md] — tracks last seen release for update modal
private val Context.releaseNotesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "release_notes",
)

private object ReleaseNotesKeys {
    val lastSeenVersionCode = intPreferencesKey("last_seen_version_code")
}

class ReleaseNotesRepository(private val context: Context) {

    suspend fun lastSeenVersionCode(): Int =
        context.releaseNotesDataStore.data.map { it[ReleaseNotesKeys.lastSeenVersionCode] ?: 0 }
            .first()

    suspend fun markSeen(currentVersionCode: Int) {
        context.releaseNotesDataStore.edit { prefs ->
            val existing = prefs[ReleaseNotesKeys.lastSeenVersionCode] ?: 0
            prefs[ReleaseNotesKeys.lastSeenVersionCode] = maxOf(existing, currentVersionCode)
        }
    }

    suspend fun pendingReleases(currentVersionCode: Int): List<AppRelease> {
        val lastSeen = lastSeenVersionCode()
        return AppReleaseCatalog.pendingReleases(lastSeen, currentVersionCode)
    }

    fun latestRelease(): AppRelease? = AppReleaseCatalog.releases.firstOrNull()
}
