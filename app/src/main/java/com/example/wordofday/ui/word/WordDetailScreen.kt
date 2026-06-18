package com.example.wordofday.ui.word

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.wordofday.data.model.WordEntry
import com.example.wordofday.data.repository.LearningRepository
import com.example.wordofday.data.repository.WordRepository
import com.example.wordofday.ui.components.WordDetailContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailScreen(
    wordKey: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LocalContext.current.applicationContext as Application
    val repository = remember(app) { WordRepository(app) }
    val learningRepository = remember(app) { LearningRepository(app) }
    val scope = rememberCoroutineScope()
    var word by remember(wordKey) { mutableStateOf<WordEntry?>(null) }
    var error by remember(wordKey) { mutableStateOf<String?>(null) }
    var userSentence by remember(wordKey) { mutableStateOf("") }
    var markedHard by remember(wordKey) { mutableStateOf(false) }
    val noteFlow = remember(wordKey) { learningRepository.observeNote(wordKey) }
    val note by noteFlow.collectAsState(initial = null)

    LaunchedEffect(note) {
        userSentence = note?.userSentence.orEmpty()
        markedHard = note?.markedHard == true
    }

    LaunchedEffect(wordKey) {
        try {
            word = withContext(Dispatchers.IO) { repository.resolveKey(wordKey) }
            learningRepository.ensureMasterySeed(wordKey)
            if (word == null) error = "Word not found"
        } catch (e: Exception) {
            error = e.message ?: "Could not load word"
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(word?.word ?: "Word") },
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
        when {
            error != null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(error!!, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            }
            word == null -> Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(padding).padding(top = 48.dp))
            }
            else -> {
                val entry = word!!
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 760.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = entry.word,
                            modifier = Modifier.semantics { heading() },
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "${entry.partOfSpeech} · ${entry.gradeLevel.displayLabel}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                        )
                        if (entry.pronunciation.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = entry.pronunciation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        WordDetailContent(word = entry, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(28.dp))
                        Text(
                            text = "Your sentence",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = userSentence,
                            onValueChange = { userSentence = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Use ${entry.word} in a sentence…") },
                            minLines = 2,
                        )
                        Spacer(Modifier.height(8.dp))
                        FilterChip(
                            selected = markedHard,
                            onClick = {
                                scope.launch {
                                    markedHard = learningRepository.toggleHard(wordKey)
                                }
                            },
                            label = { Text(if (markedHard) "Marked hard" else "Mark as hard") },
                        )
                        if (userSentence != note?.userSentence.orEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        learningRepository.saveUserSentence(wordKey, userSentence)
                                    }
                                },
                            ) {
                                Text("Save sentence")
                            }
                        }
                    }
                }
            }
        }
    }
}
