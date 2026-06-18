package com.example.wordofday.notification

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.wordofday.MainActivity
import com.example.wordofday.R
import com.example.wordofday.data.model.WordEntry

// [TRACE: DOCS/EDITIONS_ROADMAP.md] — daily word notification surface
object WordNotificationHelper {

    const val CHANNEL_ID = "daily_word"
    private const val DAILY_NOTIFICATION_ID = 1001
    private const val TEST_NOTIFICATION_ID = 1002

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val channel = android.app.NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.notification_channel_description)
        }
        manager.createNotificationChannel(channel)
    }

    fun canPostNotifications(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun showDailyWord(context: Context, word: WordEntry, isTest: Boolean = false) {
        val appContext = context.applicationContext
        ensureChannel(appContext)
        if (!canPostNotifications(appContext)) return

        val title = if (isTest) {
            appContext.getString(R.string.notification_test_title)
        } else {
            appContext.getString(R.string.notification_daily_title)
        }
        val body = appContext.getString(
            R.string.notification_daily_body,
            word.word,
            word.definition.take(140).let { text ->
                if (word.definition.length > 140) "$text…" else text
            },
        )
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(openAppPendingIntent(appContext))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(appContext).notify(
            if (isTest) TEST_NOTIFICATION_ID else DAILY_NOTIFICATION_ID,
            notification,
        )
    }

    private fun openAppPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
