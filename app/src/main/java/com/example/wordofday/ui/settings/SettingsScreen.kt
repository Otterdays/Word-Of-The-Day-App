package com.example.wordofday.ui.settings

import android.app.Application
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.wordofday.BuildConfig
import com.example.wordofday.notification.WordNotificationHelper
import com.example.wordofday.ui.components.InterestPicker
import com.example.wordofday.ui.theme.AppSpacing
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordofday.data.model.Category
import com.example.wordofday.data.model.GradeLevel

// [TRACE: DOCS/ROADMAP.md] — §9b settings
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onShowWhatsNew: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(LocalContext.current.applicationContext as Application),
    ),
) {
    val prefs by viewModel.preferences.collectAsState()
    val notificationPrefs by viewModel.notificationPreferences.collectAsState()
    val matchCount by viewModel.matchingWordCount.collectAsState()
    var interestSearch by remember { mutableStateOf("") }
    var showTimePicker by remember { mutableStateOf(false) }
    var pendingNotificationAction by remember { mutableStateOf<NotificationPermissionAction?>(null) }
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (!granted) {
            pendingNotificationAction = null
            return@rememberLauncherForActivityResult
        }
        when (pendingNotificationAction) {
            NotificationPermissionAction.EnableDaily -> viewModel.setNotificationsEnabled(true)
            NotificationPermissionAction.SendTest -> viewModel.sendTestNotification()
            null -> Unit
        }
        pendingNotificationAction = null
    }

    fun requestNotificationsEnabled(enabled: Boolean) {
        if (!enabled) {
            viewModel.setNotificationsEnabled(false)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !WordNotificationHelper.canPostNotifications(context)
        ) {
            pendingNotificationAction = NotificationPermissionAction.EnableDaily
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        viewModel.setNotificationsEnabled(true)
    }

    fun sendTestNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !WordNotificationHelper.canPostNotifications(context)
        ) {
            pendingNotificationAction = NotificationPermissionAction.SendTest
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        viewModel.sendTestNotification()
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = notificationPrefs.hour,
            initialMinute = notificationPrefs.minute,
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setReminderTime(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Reminder time") },
            text = { TimePicker(state = timePickerState) },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 720.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Text(
                    text = "Grade level",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    GradeLevel.entries.forEach { level ->
                        FilterChip(
                            selected = level == prefs.gradeLevel,
                            onClick = { viewModel.setGrade(level) },
                            label = { Text(level.displayLabel) },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Interests (${Category.entries.size} topics, up to 8)",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                InterestPicker(
                    selected = prefs.selectedCategories,
                    onToggle = viewModel::toggleCategory,
                    maxSelection = 8,
                    searchQuery = interestSearch,
                    onSearchChange = { interestSearch = it },
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Daily reminder",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Get a morning notification with today's word based on your grade and interests.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(12.dp))
                LexiconToggle(
                    title = "Daily word notification",
                    subtitle = "Scheduled with WorkManager while the app is installed",
                    checked = notificationPrefs.enabled,
                    onCheckedChange = ::requestNotificationsEnabled,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = notificationPrefs.enabled) {
                            showTimePicker = true
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Reminder time", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = formatReminderTime(notificationPrefs.hour, notificationPrefs.minute),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = if (notificationPrefs.enabled) "Change" else "Enable first",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (notificationPrefs.enabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Extended sources (opt-in)",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Adds dictionary, thesaurus, myth, sacred reference, and mature historical vocabulary. " +
                        "Teen/adult entries respect your grade level.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(12.dp))
                LexiconToggle(
                    title = "WordNet dictionary",
                    subtitle = "Princeton WordNet definitions + synonyms",
                    checked = prefs.lexicon.includeWordNet,
                    onCheckedChange = viewModel::setLexiconWordNet,
                )
                LexiconToggle(
                    title = "Myth & lore",
                    subtitle = "Greek, Norse, and world mythology",
                    checked = prefs.lexicon.includeMythology,
                    onCheckedChange = viewModel::setLexiconMythology,
                )
                LexiconToggle(
                    title = "Sacred reference",
                    subtitle = "Public-domain scripture vocabulary",
                    checked = prefs.lexicon.includeSacredReference,
                    onCheckedChange = viewModel::setLexiconSacred,
                )
                LexiconToggle(
                    title = "Literary & historical",
                    subtitle = "Philosophy, grey history, mature themes",
                    checked = prefs.lexicon.includeLiteraryHistorical,
                    onCheckedChange = viewModel::setLexiconLiterary,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = when (val c = matchCount) {
                        null -> "Counting words…"
                        0 -> "No exact matches for this combo — home uses a relaxed pool."
                        1 -> "1 word matches your grade and categories today."
                        else -> "$c words match your grade and categories."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(32.dp))
                OutlinedButton(
                    onClick = onShowWhatsNew,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("What's new")
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { viewModel.resetPreferences() },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Reset preferences")
                }
                if (BuildConfig.DEBUG) {
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Debug",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fire an immediate test notification using today's word.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = ::sendTestNotification,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Send test notification")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun LexiconToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

private enum class NotificationPermissionAction {
    EnableDaily,
    SendTest,
}
