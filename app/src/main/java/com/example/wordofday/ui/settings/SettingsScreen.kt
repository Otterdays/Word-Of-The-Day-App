package com.example.wordofday.ui.settings

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(LocalContext.current.applicationContext as Application),
    ),
) {
    val prefs by viewModel.preferences.collectAsState()
    val matchCount by viewModel.matchingWordCount.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Grade level",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                GradeLevel.entries.forEach { level ->
                    FilterChip(
                        selected = level == prefs.gradeLevel,
                        onClick = { viewModel.setGrade(level) },
                        label = { Text(level.displayLabel) },
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Interests (up to 3)",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Category.MvpCategories.forEach { cat ->
                    val maxReached = !prefs.selectedCategories.contains(cat) &&
                        prefs.selectedCategories.size >= 3
                    FilterChip(
                        selected = cat in prefs.selectedCategories,
                        onClick = { if (!maxReached || cat in prefs.selectedCategories) viewModel.toggleCategory(cat) },
                        label = { Text(cat.displayLabel) },
                        enabled = !maxReached || cat in prefs.selectedCategories,
                    )
                }
            }
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
                onClick = { viewModel.resetPreferences() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Reset preferences")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
