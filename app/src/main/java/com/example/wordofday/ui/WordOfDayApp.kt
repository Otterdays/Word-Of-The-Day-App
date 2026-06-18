package com.example.wordofday.ui

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wordofday.BuildConfig
import com.example.wordofday.data.preferences.ReleaseNotesRepository
import com.example.wordofday.data.preferences.UserPreferencesRepository
import com.example.wordofday.data.release.AppRelease
import com.example.wordofday.ui.home.HomeScreen
import com.example.wordofday.ui.home.HomeViewModel
import com.example.wordofday.ui.library.LibraryScreen
import com.example.wordofday.ui.navigation.AppDestinations
import com.example.wordofday.ui.onboarding.OnboardingScreen
import com.example.wordofday.ui.quiz.QuizScreen
import com.example.wordofday.ui.quiz.QuizViewModel
import com.example.wordofday.ui.settings.SettingsScreen
import com.example.wordofday.ui.update.UpdateModal
import com.example.wordofday.ui.word.WordDetailScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Root navigation: onboarding (first launch) → home; settings as secondary destination.
 */
@Composable
fun WordOfDayApp() {
    val app = LocalContext.current.applicationContext as Application
    val prefsRepo = remember(app) { UserPreferencesRepository(app) }
    val releaseNotesRepo = remember(app) { ReleaseNotesRepository(app) }
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    var ready by remember { mutableStateOf(false) }
    var startAtHome by remember { mutableStateOf(false) }
    var showUpdateModal by remember { mutableStateOf(false) }
    var pendingReleases by remember { mutableStateOf<List<AppRelease>>(emptyList()) }
    var whatsNewRequest by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        val first = prefsRepo.preferences.first()
        startAtHome = first.onboardingComplete
        ready = true
    }

    LaunchedEffect(ready, startAtHome) {
        if (!ready || !startAtHome) return@LaunchedEffect
        val pending = releaseNotesRepo.pendingReleases(BuildConfig.VERSION_CODE)
        if (pending.isNotEmpty()) {
            pendingReleases = pending
            showUpdateModal = true
        }
    }

    LaunchedEffect(whatsNewRequest) {
        if (whatsNewRequest > 0) {
            pendingReleases = listOfNotNull(releaseNotesRepo.latestRelease())
            showUpdateModal = true
        }
    }

    if (!ready) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Box(Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = if (startAtHome) AppDestinations.HOME else AppDestinations.ONBOARDING,
        ) {
            composable(AppDestinations.ONBOARDING) {
                OnboardingScreen(
                    onFinished = {
                        navController.navigate(AppDestinations.HOME) {
                            popUpTo(AppDestinations.ONBOARDING) { inclusive = true }
                        }
                    },
                )
            }
            composable(AppDestinations.HOME) {
                HomeScreen(
                    viewModel = viewModel(factory = HomeViewModel.Factory(app)),
                    onOpenSettings = { navController.navigate(AppDestinations.SETTINGS) },
                    onOpenQuiz = { navController.navigate(AppDestinations.QUIZ) },
                    onOpenLibrary = { navController.navigate(AppDestinations.LIBRARY) },
                )
            }
            composable(AppDestinations.SETTINGS) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onShowWhatsNew = { whatsNewRequest++ },
                )
            }
            composable(AppDestinations.QUIZ) {
                QuizScreen(
                    viewModel = viewModel(factory = QuizViewModel.Factory(app)),
                    onBack = { navController.popBackStack() },
                )
            }
            composable(AppDestinations.LIBRARY) {
                LibraryScreen(
                    onBack = { navController.popBackStack() },
                    onOpenWord = { key ->
                        navController.navigate(AppDestinations.wordDetailRoute(key))
                    },
                )
            }
            composable(
                route = AppDestinations.WORD_DETAIL,
                arguments = listOf(
                    navArgument("wordKey") { type = NavType.StringType },
                ),
            ) { entry ->
                val raw = entry.arguments?.getString("wordKey").orEmpty()
                val key = URLDecoder.decode(raw, StandardCharsets.UTF_8.toString())
                WordDetailScreen(
                    wordKey = key,
                    onBack = { navController.popBackStack() },
                )
            }
        }

        if (showUpdateModal && pendingReleases.isNotEmpty()) {
            UpdateModal(
                releases = pendingReleases,
                onDismiss = {
                    scope.launch {
                        releaseNotesRepo.markSeen(BuildConfig.VERSION_CODE)
                        showUpdateModal = false
                    }
                },
            )
        }
    }
}
