package com.example.wordofday.ui.library

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onBack: () -> Unit,
    onOpenWord: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = viewModel(
        factory = LibraryViewModel.Factory(
            LocalContext.current.applicationContext as Application,
        ),
    ),
) {
    val history by viewModel.history.collectAsState()
    val favorites by viewModel.resolvedFavorites.collectAsState()
    var tab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Library") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(padding),
        ) {
            PrimaryTabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Calendar") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("List") })
                Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text("Favorites") })
            }
            when (tab) {
                0 -> HistoryCalendar(
                    history = history,
                    onOpenWord = onOpenWord,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                )
                1 -> HistoryList(history, onOpenWord)
                2 -> FavoritesList(favorites, onOpenWord)
            }
        }
    }
}

@Composable
private fun HistoryList(
    history: List<com.example.wordofday.data.model.WordHistoryEntry>,
    onOpenWord: (String) -> Unit,
) {
    if (history.isEmpty()) {
        EmptyLibraryMessage("Words you view each day will appear here.")
        return
    }
    val formatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy") }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(history, key = { "${it.date}|${it.wordKey}" }) { item ->
            ListItem(
                headlineContent = { Text(item.word) },
                supportingContent = {
                    Text("${item.gradeLevel.displayLabel} · ${item.date.format(formatter)}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenWord(item.wordKey) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun FavoritesList(
    favorites: List<LibraryWordItem>,
    onOpenWord: (String) -> Unit,
) {
    if (favorites.isEmpty()) {
        EmptyLibraryMessage("Tap the heart on any word to save it here.")
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(favorites, key = { it.key }) { item ->
            ListItem(
                headlineContent = { Text(item.word.word) },
                supportingContent = {
                    Text("${item.word.gradeLevel.displayLabel} · ${item.word.partOfSpeech}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenWord(item.key) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun EmptyLibraryMessage(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
