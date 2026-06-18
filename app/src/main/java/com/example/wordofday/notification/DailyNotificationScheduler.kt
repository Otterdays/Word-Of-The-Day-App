package com.example.wordofday.notification

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

// [TRACE: DOCS/EDITIONS_ROADMAP.md] — chains one-time work to the user's reminder time
object DailyNotificationScheduler {

    const val UNIQUE_WORK_NAME = "daily_word_notification"

    fun scheduleNext(context: Context, hour: Int, minute: Int) {
        val appContext = context.applicationContext
        val delayMs = millisUntilNext(hour, minute)
        val request = OneTimeWorkRequestBuilder<DailyWordNotificationWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .addTag(UNIQUE_WORK_NAME)
            .build()
        WorkManager.getInstance(appContext).enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context.applicationContext).cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    fun millisUntilNext(hour: Int, minute: Int): Long {
        val now = LocalDateTime.now()
        var target = now.with(LocalTime.of(hour.coerceIn(0, 23), minute.coerceIn(0, 59)))
        if (!target.isAfter(now)) {
            target = target.plusDays(1)
        }
        return Duration.between(now, target).toMillis()
    }
}
