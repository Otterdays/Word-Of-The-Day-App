package com.example.wordofday.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.wordofday.data.learning.AchievementCatalog
import com.example.wordofday.data.learning.Sm2Scheduler
import com.example.wordofday.data.local.WordOfDayDatabase
import com.example.wordofday.data.local.entity.QuizAttemptEntity
import com.example.wordofday.data.local.entity.WordMasteryEntity
import com.example.wordofday.data.local.entity.WordNoteEntity
import com.example.wordofday.data.model.AchievementProgress
import com.example.wordofday.data.model.LearningDashboard
import com.example.wordofday.data.model.QuizMode
import com.example.wordofday.data.model.WeeklyProgress
import com.example.wordofday.data.preferences.EngagementRepository
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

// [TRACE: DOCS/EDITIONS_ROADMAP.md] — E6 adaptive learning repository
private val Context.learningDataStore by preferencesDataStore(name = "learning_meta")

private object LearningKeys {
    val unlockedAchievements = stringSetPreferencesKey("unlocked_achievements")
    val achievementTimestamps = stringSetPreferencesKey("achievement_timestamps")
    val exploreOpenCount = longPreferencesKey("explore_open_count")
    val reviewCompleteCount = longPreferencesKey("review_complete_count")
    val gradeShiftUsed = longPreferencesKey("grade_shift_used")
}

class LearningRepository(context: Context) {

    private val appContext = context.applicationContext
    private val db = WordOfDayDatabase.get(appContext)
    private val engagement = EngagementRepository(appContext)

    val dueReviewCount: Flow<Int> =
        db.wordMasteryDao().observeDueCount(Sm2Scheduler.todayEpochDay())

    val masteredCount: Flow<Int> = db.wordMasteryDao().observeMasteredCount()

    suspend fun getDueMasteries(limit: Int = 30): List<WordMasteryEntity> =
        withContext(Dispatchers.IO) {
            db.wordMasteryDao().getDue(Sm2Scheduler.todayEpochDay(), limit)
        }

    suspend fun recordReview(wordKey: String, correct: Boolean) =
        withContext(Dispatchers.IO) {
            val today = Sm2Scheduler.todayEpochDay()
            val current = db.wordMasteryDao().getByKey(wordKey)
            val quality = Sm2Scheduler.qualityFromCorrect(correct)
            val next = Sm2Scheduler.scheduleReview(current, wordKey, quality, today)
            db.wordMasteryDao().upsert(next)
            incrementReviewCount()
            checkAchievements()
        }

    suspend fun ensureMasterySeed(wordKey: String) = withContext(Dispatchers.IO) {
        if (db.wordMasteryDao().getByKey(wordKey) == null) {
            db.wordMasteryDao().upsert(Sm2Scheduler.seedForNewWord(wordKey))
        }
    }

    suspend fun getNote(wordKey: String): WordNoteEntity? =
        withContext(Dispatchers.IO) { db.wordNoteDao().getByKey(wordKey) }

    fun observeNote(wordKey: String): Flow<WordNoteEntity?> =
        db.wordNoteDao().observeByKey(wordKey)

    suspend fun saveUserSentence(wordKey: String, sentence: String) =
        withContext(Dispatchers.IO) {
            val existing = db.wordNoteDao().getByKey(wordKey)
            db.wordNoteDao().upsert(
                WordNoteEntity(
                    wordKey = wordKey,
                    userSentence = sentence.trim(),
                    markedHard = existing?.markedHard ?: false,
                ),
            )
            if (sentence.isNotBlank()) unlockAchievement("note_3")
            checkAchievements()
        }

    suspend fun toggleHard(wordKey: String): Boolean = withContext(Dispatchers.IO) {
        val existing = db.wordNoteDao().getByKey(wordKey)
        val nextHard = !(existing?.markedHard ?: false)
        db.wordNoteDao().upsert(
            WordNoteEntity(
                wordKey = wordKey,
                userSentence = existing?.userSentence.orEmpty(),
                markedHard = nextHard,
            ),
        )
        if (nextHard) checkAchievements()
        nextHard
    }

    suspend fun recordQuizAttempt(wordKey: String, correct: Boolean, mode: QuizMode) =
        withContext(Dispatchers.IO) {
            db.quizAttemptDao().insert(
                QuizAttemptEntity(
                    timestamp = System.currentTimeMillis(),
                    wordKey = wordKey,
                    correct = correct,
                    mode = mode.name,
                ),
            )
            ensureMasterySeed(wordKey)
            recordReview(wordKey, correct)
            if (mode == QuizMode.BLITZ) unlockAchievement("blitz_win")
            if (mode == QuizMode.REVERSE) unlockAchievement("reverse_win")
            checkAchievements()
        }

    suspend fun recordExploreOpen() = withContext(Dispatchers.IO) {
        appContext.learningDataStore.edit { prefs ->
            val c = (prefs[LearningKeys.exploreOpenCount] ?: 0L) + 1L
            prefs[LearningKeys.exploreOpenCount] = c
        }
        checkAchievements()
    }

    suspend fun recordGradeShiftUsed() = withContext(Dispatchers.IO) {
        appContext.learningDataStore.edit { it[LearningKeys.gradeShiftUsed] = 1L }
        unlockAchievement("grade_shift")
    }

    suspend fun loadDashboard(): LearningDashboard = withContext(Dispatchers.IO) {
        val today = Sm2Scheduler.todayEpochDay()
        val weekAgoMs = Instant.now().minus(7, ChronoUnit.DAYS).toEpochMilli()
        val streak = engagement.streakCount.first()
        val history = engagement.wordHistory.first()
        val favorites = engagement.favoriteKeys.first()
        val stats = engagement.quizStats.first()
        val due = db.wordMasteryDao().getDue(today).size
        val mastered = db.wordMasteryDao().getTopMastered(1000).count { it.masteryLevel >= 3 }
        val attempts = db.quizAttemptDao().countSince(weekAgoMs)
        val correct = db.quizAttemptDao().countCorrectSince(weekAgoMs)
        val achievements = loadAchievements()
        LearningDashboard(
            dueReviewCount = due,
            masteredCount = mastered,
            weekly = WeeklyProgress(
                wordsViewed = history.count { it.date.toEpochDay() >= today - 7 },
                quizAttempts = attempts,
                quizCorrect = correct,
                accuracyPercent = if (attempts == 0) 0 else (correct * 100) / attempts,
                streakDays = streak,
                masteredWords = mastered,
                dueReviews = due,
            ),
            recentAchievements = achievements.filter { it.unlocked }.takeLast(5),
        )
    }

    suspend fun loadAchievements(): List<AchievementProgress> = withContext(Dispatchers.IO) {
        val unlocked = appContext.learningDataStore.data.first()[LearningKeys.unlockedAchievements]
            ?: emptySet()
        val timestamps = decodeTimestamps(
            appContext.learningDataStore.data.first()[LearningKeys.achievementTimestamps],
        )
        AchievementCatalog.all.map { achievement ->
            AchievementProgress(
                achievement = achievement,
                unlocked = achievement.id in unlocked,
                unlockedAt = timestamps[achievement.id],
            )
        }
    }

    suspend fun unlockAchievement(id: String) = withContext(Dispatchers.IO) {
        appContext.learningDataStore.edit { prefs ->
            val set = prefs[LearningKeys.unlockedAchievements]?.toMutableSet() ?: mutableSetOf()
            if (set.add(id)) {
                prefs[LearningKeys.unlockedAchievements] = set
                val ts = decodeTimestamps(prefs[LearningKeys.achievementTimestamps]).toMutableMap()
                ts[id] = System.currentTimeMillis()
                prefs[LearningKeys.achievementTimestamps] = encodeTimestamps(ts)
            }
        }
    }

    suspend fun checkAchievements() = withContext(Dispatchers.IO) {
        unlockAchievement("first_open")
        val streak = engagement.streakCount.first()
        if (streak >= 7) unlockAchievement("streak_7")
        if (streak >= 30) unlockAchievement("streak_30")
        if (streak >= 100) unlockAchievement("streak_100")
        val stats = engagement.quizStats.first()
        if (stats.totalAnswered >= 10) unlockAchievement("quiz_10")
        if (stats.totalAnswered >= 100) unlockAchievement("quiz_100")
        val favs = engagement.favoriteKeys.first().size
        if (favs >= 5) unlockAchievement("favorite_5")
        if (favs >= 25) unlockAchievement("favorite_25")
        val mastered = db.wordMasteryDao().getTopMastered(1000).count { it.masteryLevel >= 3 }
        if (mastered >= 5) unlockAchievement("master_5")
        if (mastered >= 25) unlockAchievement("master_25")
        val reviews = appContext.learningDataStore.data.first()[LearningKeys.reviewCompleteCount] ?: 0L
        if (reviews >= 10) unlockAchievement("review_10")
        val explore = appContext.learningDataStore.data.first()[LearningKeys.exploreOpenCount] ?: 0L
        if (explore >= 50) unlockAchievement("explore_50")
        val hard = db.wordNoteDao().getHardWords().size
        if (hard >= 5) unlockAchievement("hard_5")
        val historyDays = engagement.wordHistory.first().map { it.date }.distinct().size
        if (historyDays >= 30) unlockAchievement("history_30")
    }

    suspend fun recordPerfectQuiz() = unlockAchievement("quiz_perfect")

    suspend fun recordFiveCategoriesSelected() = unlockAchievement("all_categories")

    private suspend fun incrementReviewCount() {
        appContext.learningDataStore.edit { prefs ->
            val c = (prefs[LearningKeys.reviewCompleteCount] ?: 0L) + 1L
            prefs[LearningKeys.reviewCompleteCount] = c
        }
    }

    private fun encodeTimestamps(map: Map<String, Long>): Set<String> =
        map.entries.map { "${it.key}|${it.value}" }.toSet()

    private fun decodeTimestamps(raw: Set<String>?): Map<String, Long> =
        raw.orEmpty().mapNotNull { line ->
            val idx = line.indexOf('|')
            if (idx <= 0) return@mapNotNull null
            val id = line.substring(0, idx)
            val ts = line.substring(idx + 1).toLongOrNull() ?: return@mapNotNull null
            id to ts
        }.toMap()
}
