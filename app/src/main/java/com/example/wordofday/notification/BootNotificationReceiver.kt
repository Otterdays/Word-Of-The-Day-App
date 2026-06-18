package com.example.wordofday.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.wordofday.data.preferences.NotificationPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// [TRACE: DOCS/EDITIONS_ROADMAP.md] — restore daily reminder after device reboot
class BootNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = NotificationPreferencesRepository(context).preferences.first()
                if (prefs.enabled) {
                    DailyNotificationScheduler.scheduleNext(context, prefs.hour, prefs.minute)
                }
            } finally {
                pending.finish()
            }
        }
    }
}
