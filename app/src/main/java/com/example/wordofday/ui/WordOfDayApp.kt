package com.example.wordofday.ui

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wordofday.data.preferences.UserPreferencesRepository
import com.example.wordofday.ui.home.HomeScreen
import com.example.wordofday.ui.home.HomeViewModel
import com.example.wordofday.ui.navigation.AppDestinations
import com.example.wordofday.ui.onboarding.OnboardingScreen
import com.example.wordofday.ui.settings.SettingsScreen
import kotlinx.coroutines.flow.first

/**
 * Root navigation: onboarding (first launch) → home; settings as secondary destination.
 */
@Composable
fun WordOfDayApp() {
    val app = LocalContext.current.applicationContext as Application
    val prefsRepo = remember(app) { UserPreferencesRepository(app) }
    val navController = rememberNavController()
    var ready by remember { mutableStateOf(false) }
    var startAtHome by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val first = prefsRepo.preferences.first()
        startAtHome = first.onboardingComplete
        ready = true
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
            )
        }
        composable(AppDestinations.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
