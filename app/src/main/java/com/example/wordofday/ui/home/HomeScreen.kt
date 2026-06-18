package com.example.wordofday.ui.home

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordofday.data.model.Category
import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.UserPreferences
import com.example.wordofday.data.model.WordEntry
import com.example.wordofday.ui.components.ThemeToggleIconButton
import com.example.wordofday.ui.components.WordDetailContent
import com.example.wordofday.ui.theme.AppSpacing
import com.example.wordofday.ui.theme.CategoryAccent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenQuiz: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenExplore: () -> Unit,
    onOpenReview: () -> Unit,
    onOpenProgress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val quickSwitchVisible by viewModel.quickSwitchVisible.collectAsState()
    val isRefreshing = when (val state = uiState) {
        HomeUiState.Loading -> true
        is HomeUiState.Success -> state.isRefreshing
        is HomeUiState.Error -> false
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            val streak = (uiState as? HomeUiState.Success)?.streakDays ?: 0
            CenterAlignedTopAppBar(
                title = { Text("Word of the Day") },
                navigationIcon = {
                    ThemeToggleIconButton(
                        isDarkTheme = isDarkTheme,
                        onToggle = onToggleTheme,
                    )
                },
                actions = {
                    if (streak > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocalFireDepartment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "$streak",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            val activity = LocalActivity.current
            val wideLayout = if (activity != null) {
                val wc = calculateWindowSizeClass(activity)
                wc.widthSizeClass != WindowWidthSizeClass.Compact
            } else {
                maxWidth >= 600.dp
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier.fillMaxSize(),
            ) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> LoadingState()
                    is HomeUiState.Success -> WordContent(
                        state = state,
                        wideLayout = wideLayout,
                        onOpenSettings = onOpenSettings,
                        onQuickSwitch = viewModel::showQuickSwitch,
                        onOpenQuiz = onOpenQuiz,
                        onOpenLibrary = onOpenLibrary,
                        onOpenExplore = onOpenExplore,
                        onOpenReview = onOpenReview,
                        onOpenProgress = onOpenProgress,
                        onRefresh = viewModel::refresh,
                        onShare = viewModel::shareCurrentWord,
                        onTryEasier = viewModel::tryEasierWord,
                        onTryHarder = viewModel::tryHarderWord,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onSpeak = viewModel::speakCurrentWord,
                        onCategoryPageChanged = viewModel::setActiveCategoryIndex,
                    )
                    is HomeUiState.Error -> ErrorState(
                        message = state.message,
                        onRetry = viewModel::refresh,
                    )
                }
            }
        }
    }

    val success = uiState as? HomeUiState.Success
    if (success != null) {
        QuickSwitchSheet(
            visible = quickSwitchVisible,
            currentGrade = success.preferences.gradeLevel,
            currentCategories = success.preferences.selectedCategories,
            onDismiss = viewModel::hideQuickSwitch,
            onApply = viewModel::applyQuickSwitch,
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp),
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onRetry) {
            Text("Try again")
        }
    }
}

@Composable
private fun WordContent(
    state: HomeUiState.Success,
    wideLayout: Boolean,
    onOpenSettings: () -> Unit,
    onQuickSwitch: () -> Unit,
    onOpenQuiz: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenExplore: () -> Unit,
    onOpenReview: () -> Unit,
    onOpenProgress: () -> Unit,
    onRefresh: () -> Unit,
    onShare: () -> Unit,
    onTryEasier: () -> Unit,
    onTryHarder: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSpeak: () -> Unit,
    onCategoryPageChanged: (Int) -> Unit,
) {
    var showDate by remember { mutableStateOf(false) }
    var showWord by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showDate = true
        delay(200)
        showWord = true
        delay(300)
        showDetails = true
    }

    val pagerState = rememberPagerState(
        initialPage = state.activeCategoryIndex,
        pageCount = { state.categoryWords.size.coerceAtLeast(1) },
    )

    LaunchedEffect(state.activeCategoryIndex) {
        if (pagerState.currentPage != state.activeCategoryIndex) {
            pagerState.scrollToPage(state.activeCategoryIndex)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                if (page != state.activeCategoryIndex) {
                    onCategoryPageChanged(page)
                }
            }
    }

    val scroll = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 920.dp)
                .padding(horizontal = AppSpacing.screenHorizontal, vertical = AppSpacing.screenVertical),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        AnimatedVisibility(
            visible = showDate,
            enter = fadeIn() + slideInVertically { -40 },
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "WORD OF THE DAY",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (state.sessionGradeOffset != 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = gradeShiftLabel(state.effectiveGrade, state.sessionGradeOffset),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
                streakMilestoneMessage(state.streakDays)?.let { milestone ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = milestone,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.chipGap),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.chipGap),
                ) {
                    PreferenceChip(
                        label = state.effectiveGrade.displayLabel,
                        category = null,
                        onClick = onQuickSwitch,
                    )
                    state.preferences.selectedCategories.forEach { cat ->
                        PreferenceChip(
                            label = cat.displayLabel,
                            category = cat,
                            onClick = onQuickSwitch,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Learning hub",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${state.masteredCount} mastered · ${state.dueReviewCount} due for review",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            FilledTonalButton(onClick = onOpenExplore, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) {
                Text("Explore", style = MaterialTheme.typography.labelLarge)
            }
            FilledTonalButton(onClick = onOpenReview, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) {
                Text(if (state.dueReviewCount > 0) "Review (${state.dueReviewCount})" else "Review", style = MaterialTheme.typography.labelLarge)
            }
            FilledTonalButton(onClick = onOpenProgress, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) {
                Text("Progress", style = MaterialTheme.typography.labelLarge)
            }
            FilledTonalButton(onClick = onOpenQuiz, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) {
                Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Quiz", style = MaterialTheme.typography.labelLarge)
            }
            FilledTonalButton(onClick = onOpenLibrary, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Library", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = onTryEasier,
                enabled = state.canTryEasier,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text("Easier")
            }
            OutlinedButton(
                onClick = onTryHarder,
                enabled = state.canTryHarder,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text("Harder")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilledTonalButton(
                onClick = onRefresh,
                enabled = !state.isRefreshing,
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (state.isRefreshing) "Finding..." else "More words")
            }
            FilledTonalButton(onClick = onShare) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Share")
            }
            FilledTonalIconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (state.isFavorite) "Remove favorite" else "Add favorite",
                )
            }
        }
        state.refreshErrorMessage?.let { message ->
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
        }

        if (state.categoryWords.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Swipe for other topics",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = showWord,
            enter = fadeIn() + slideInVertically { 60 },
        ) {
            if (state.categoryWords.size > 1) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth(),
                ) { page ->
                    val entry = state.categoryWords[page]
                    WordPage(
                        word = entry.word,
                        category = entry.category,
                        wideLayout = wideLayout,
                        showDetails = showDetails,
                        onSpeak = onSpeak,
                    )
                }
            } else {
                WordPage(
                    word = state.word,
                    category = state.preferences.selectedCategories.firstOrNull(),
                    wideLayout = wideLayout,
                    showDetails = showDetails,
                    onSpeak = onSpeak,
                )
            }
        }
    }
}

}

@Composable
private fun WordPage(
    word: WordEntry,
    category: Category?,
    wideLayout: Boolean,
    showDetails: Boolean,
    onSpeak: () -> Unit,
) {
    if (wideLayout) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            WordHeading(
                word = word,
                category = category,
                onSpeak = onSpeak,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp),
            )
            Column(Modifier.weight(1f)) {
                AnimatedVisibility(
                    visible = showDetails,
                    enter = fadeIn() + slideInVertically { 80 },
                ) {
                    WordDetailBlock(word = word)
                }
            }
        }
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            WordHeading(
                word = word,
                category = category,
                onSpeak = onSpeak,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = showDetails,
                enter = fadeIn() + slideInVertically { 80 },
            ) {
                WordDetailBlock(word = word)
            }
        }
    }
}

@Composable
private fun PreferenceChip(
    label: String,
    category: Category?,
    onClick: () -> Unit,
) {
    val colors = if (category != null) {
        AssistChipDefaults.assistChipColors(
            containerColor = CategoryAccent.container(category),
            labelColor = CategoryAccent.onContainer(category),
        )
    } else {
        AssistChipDefaults.assistChipColors()
    }
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        colors = colors,
    )
}

@Composable
private fun WordHeading(
    word: WordEntry,
    category: Category?,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (category != null) {
            Text(
                text = category.displayLabel.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = CategoryAccent.onContainer(category),
                letterSpacing = 2.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(
            text = word.word,
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = word.partOfSpeech,
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary,
            )
            if (word.pronunciation.isNotBlank()) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = word.pronunciation,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onSpeak) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Listen to pronunciation",
                )
            }
        }
    }
}

@Composable
private fun WordDetailBlock(word: WordEntry) {
    Column {
        WordDetailContent(word = word)
        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(2.dp)),
            thickness = 3.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
        )
        Spacer(modifier = Modifier.height(48.dp))
    }
}

private fun gradeShiftLabel(grade: GradeLevel, offset: Int): String {
    val direction = if (offset < 0) "easier" else "harder"
    return "Showing a $direction word (${grade.displayLabel})"
}

private fun streakMilestoneMessage(streakDays: Int): String? = when (streakDays) {
    7 -> "🔥 One-week streak! Keep learning daily."
    30 -> "🔥 30-day streak — vocabulary champion!"
    100 -> "🔥 100-day streak — legendary dedication!"
    else -> null
}
