package com.example.wordofday.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.QuizStats
import com.example.wordofday.data.model.WordHistoryEntry
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// [TRACE: DOCS/ROADMAP.md] — §10a history, §10b favorites, §10c quiz, §10d streak
private val Context.engagementDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "engagement",
)

private object EngagementKeys {
    val streakCount = intPreferencesKey("streak_count")
    val lastOpenDate = stringPreferencesKey("last_open_date")
    val favoriteKeys = stringSetPreferencesKey("favorite_keys")
    val historyRecords = stringSetPreferencesKey("history_records")
    val quizAnswered = intPreferencesKey("quiz_total_answered")
    val quizCorrect = intPreferencesKey("quiz_total_correct")
    val quizBestStreak = intPreferencesKey("quiz_best_streak")
}

class EngagementRepository(private val context: Context) {

    val streakCount: Flow<Int> = context.engagementDataStore.data.map { prefs ->
        prefs[EngagementKeys.streakCount] ?: 0
    }

    val favoriteKeys: Flow<Set<String>> = context.engagementDataStore.data.map { prefs ->
        prefs[EngagementKeys.favoriteKeys] ?: emptySet()
    }

    val wordHistory: Flow<List<WordHistoryEntry>> = context.engagementDataStore.data.map { prefs ->
        (prefs[EngagementKeys.historyRecords] ?: emptySet())
            .mapNotNull { it.decodeHistoryRecord() }
            .sortedByDescending { it.date }
    }

    val quizStats: Flow<QuizStats> = context.engagementDataStore.data.map { prefs ->
        QuizStats(
            totalAnswered = prefs[EngagementKeys.quizAnswered] ?: 0,
            totalCorrect = prefs[EngagementKeys.quizCorrect] ?: 0,
            bestStreak = prefs[EngagementKeys.quizBestStreak] ?: 0,
        )
    }

    suspend fun recordDailyOpen(today: LocalDate = LocalDate.now()): Int {
        val iso = today.toString()
        var updated = 0
        context.engagementDataStore.edit { prefs ->
            val last = prefs[EngagementKeys.lastOpenDate]
            val current = prefs[EngagementKeys.streakCount] ?: 0
            updated = when {
                last == iso -> current
                last == today.minusDays(1).toString() -> current + 1
                else -> 1
            }
            prefs[EngagementKeys.streakCount] = updated
            prefs[EngagementKeys.lastOpenDate] = iso
        }
        return updated
    }

    suspend fun recordWordView(
        date: LocalDate,
        wordKey: String,
        word: String,
        gradeLevel: GradeLevel,
    ) {
        val record = encodeHistoryRecord(date, wordKey, word, gradeLevel)
        context.engagementDataStore.edit { prefs ->
            val current = prefs[EngagementKeys.historyRecords]?.toMutableSet() ?: mutableSetOf()
            current.removeAll { it.startsWith("${date}|") }
            current.add(record)
            val trimmed = current
                .mapNotNull { it.decodeHistoryRecord() }
                .sortedByDescending { it.date }
                .take(MAX_HISTORY_DAYS)
                .map { encodeHistoryRecord(it.date, it.wordKey, it.word, it.gradeLevel) }
                .toSet()
            prefs[EngagementKeys.historyRecords] = trimmed
        }
    }

    suspend fun isFavorite(key: String): Boolean =
        favoriteKeys.first().contains(key)

    suspend fun toggleFavorite(key: String): Boolean {
        var nowFavorite = false
        context.engagementDataStore.edit { prefs ->
            val current = prefs[EngagementKeys.favoriteKeys]?.toMutableSet() ?: mutableSetOf()
            nowFavorite = if (key in current) {
                current.remove(key)
                false
            } else {
                current.add(key)
                true
            }
            prefs[EngagementKeys.favoriteKeys] = current
        }
        return nowFavorite
    }

    suspend fun recordQuizAnswer(correct: Boolean, sessionStreak: Int): QuizStats {
        var stats = QuizStats(0, 0, 0)
        context.engagementDataStore.edit { prefs ->
            val answered = (prefs[EngagementKeys.quizAnswered] ?: 0) + 1
            val correctTotal = (prefs[EngagementKeys.quizCorrect] ?: 0) + if (correct) 1 else 0
            val best = maxOf(prefs[EngagementKeys.quizBestStreak] ?: 0, if (correct) sessionStreak else 0)
            prefs[EngagementKeys.quizAnswered] = answered
            prefs[EngagementKeys.quizCorrect] = correctTotal
            prefs[EngagementKeys.quizBestStreak] = best
            stats = QuizStats(answered, correctTotal, best)
        }
        return stats
    }

    companion object {
        private const val MAX_HISTORY_DAYS = 90

        fun encodeHistoryRecord(
            date: LocalDate,
            wordKey: String,
            word: String,
            gradeLevel: GradeLevel,
        ): String = "${date}|$wordKey|$word|${gradeLevel.name}"

        fun decodeHistoryRecord(raw: String): WordHistoryEntry? {
            val parts = raw.split("|", limit = 4)
            if (parts.size < 4) return null
            val date = runCatching { LocalDate.parse(parts[0]) }.getOrNull() ?: return null
            val grade = runCatching { GradeLevel.valueOf(parts[3]) }.getOrNull() ?: return null
            return WordHistoryEntry(
                date = date,
                wordKey = parts[1],
                word = parts[2],
                gradeLevel = grade,
            )
        }
    }
}

private fun String.decodeHistoryRecord(): WordHistoryEntry? =
    EngagementRepository.decodeHistoryRecord(this)
