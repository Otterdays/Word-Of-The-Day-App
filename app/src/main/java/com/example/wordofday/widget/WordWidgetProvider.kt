package com.example.wordofday.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.wordofday.MainActivity
import com.example.wordofday.R
import com.example.wordofday.data.preferences.UserPreferencesRepository
import com.example.wordofday.data.repository.WordRepository
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

// [TRACE: DOCS/ROADMAP.md] — §7 home screen widget showing today's word
class WordWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        requestUpdate(context)
    }

    override fun onEnabled(context: Context) {
        requestUpdate(context)
    }

    override fun onDisabled(context: Context) {
        WorkManager.getInstance(context.applicationContext).cancelUniqueWork(MIDNIGHT_WORK)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_LOCALE_CHANGED,
            -> requestUpdate(context)
        }
    }

    companion object {
        private const val REFRESH_WORK = "word_widget_refresh"
        private const val MIDNIGHT_WORK = "word_widget_midnight"

        fun hasWidgets(context: Context): Boolean {
            val manager = AppWidgetManager.getInstance(context) ?: return false
            return manager.getAppWidgetIds(ComponentName(context, WordWidgetProvider::class.java)).isNotEmpty()
        }

        /** Safe to call anytime; no-op when no widget is placed. */
        fun requestUpdate(context: Context) {
            val app = context.applicationContext
            if (!hasWidgets(app)) return
            val wm = WorkManager.getInstance(app)
            wm.enqueueUniqueWork(
                REFRESH_WORK,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<WordWidgetWorker>().build(),
            )
            scheduleMidnight(app)
        }

        /** Refresh just after the next local midnight so the widget rolls over. */
        fun scheduleMidnight(context: Context) {
            val app = context.applicationContext
            if (!hasWidgets(app)) return
            val wm = WorkManager.getInstance(app)
            val now = LocalDateTime.now()
            val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay().plusMinutes(1)
            wm.enqueueUniqueWork(
                MIDNIGHT_WORK,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<WordWidgetWorker>()
                    .setInitialDelay(Duration.between(now, nextMidnight).toMillis(), TimeUnit.MILLISECONDS)
                    .build(),
            )
        }

        internal suspend fun render(context: Context) {
            val app = context.applicationContext
            val manager = AppWidgetManager.getInstance(app) ?: return
            val ids = manager.getAppWidgetIds(ComponentName(app, WordWidgetProvider::class.java))
            if (ids.isEmpty()) return

            val views = RemoteViews(app.packageName, R.layout.widget_word)
            val today = LocalDate.now()
            views.setTextViewText(
                R.id.widget_date,
                today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
            )
            val word = runCatching {
                val prefs = UserPreferencesRepository(app).preferences.first()
                WordRepository(app).getWordForDate(date = today, preferences = prefs)
            }.getOrNull()

            if (word == null) {
                views.setTextViewText(R.id.widget_word, app.getString(R.string.app_name))
                views.setTextViewText(R.id.widget_definition, app.getString(R.string.widget_open_app))
                views.setViewVisibility(R.id.widget_meta, View.GONE)
            } else {
                views.setTextViewText(R.id.widget_word, word.word)
                val meta = listOf(word.partOfSpeech, word.pronunciation.orEmpty())
                    .filter { it.isNotBlank() }
                    .joinToString("  ·  ")
                views.setTextViewText(R.id.widget_meta, meta)
                views.setViewVisibility(R.id.widget_meta, if (meta.isBlank()) View.GONE else View.VISIBLE)
                views.setTextViewText(R.id.widget_definition, word.definition)
                views.setContentDescription(
                    R.id.widget_root,
                    app.getString(R.string.widget_content_description, word.word, word.definition),
                )
            }

            val open = Intent(app, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            views.setOnClickPendingIntent(
                R.id.widget_root,
                PendingIntent.getActivity(
                    app,
                    2001,
                    open,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                ),
            )
            manager.updateAppWidget(ids, views)
        }
    }
}

class WordWidgetWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        WordWidgetProvider.render(applicationContext)
        // Keep the midnight chain alive after a midnight run.
        // The midnight run re-arms itself for the following night.
        if (LocalDateTime.now().hour == 0) WordWidgetProvider.scheduleMidnight(applicationContext)
        return Result.success()
    }
}
