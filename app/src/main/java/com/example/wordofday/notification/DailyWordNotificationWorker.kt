package com.example.wordofday.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.wordofday.data.preferences.NotificationPreferencesRepository
import com.example.wordofday.data.preferences.UserPreferencesRepository
import com.example.wordofday.data.repository.WordRepository
import kotlinx.coroutines.flow.first

// [TRACE: DOCS/EDITIONS_ROADMAP.md] — posts today's word then schedules the next reminder
class DailyWordNotificationWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val notificationPrefsRepo = NotificationPreferencesRepository(applicationContext)
        val notificationPrefs = notificationPrefsRepo.preferences.first()
        if (!notificationPrefs.enabled) return Result.success()

        val userPrefs = UserPreferencesRepository(applicationContext).preferences.first()
        val word = WordRepository(applicationContext).getWordForDate(preferences = userPrefs)
        WordNotificationHelper.showDailyWord(applicationContext, word)
        DailyNotificationScheduler.scheduleNext(
            applicationContext,
            notificationPrefs.hour,
            notificationPrefs.minute,
        )
        return Result.success()
    }
}
