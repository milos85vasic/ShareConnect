package com.shareconnect.onboarding.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shareconnect.languagesync.models.LanguageData
import com.shareconnect.onboarding.ui.screens.WelcomeScreen
import com.shareconnect.onboarding.ui.screens.ThemeSelectionScreen
import com.shareconnect.onboarding.ui.screens.LanguageSelectionScreen
import com.shareconnect.onboarding.ui.screens.ProfileCreationScreen
import com.shareconnect.onboarding.ui.screens.CompletionScreen
import com.shareconnect.onboarding.ui.theme.OnboardingTheme
import com.shareconnect.onboarding.viewmodel.OnboardingViewModel
import java.util.Locale

open class OnboardingActivity : ComponentActivity() {

    protected lateinit var viewModel: OnboardingViewModel
    protected open var appName: String = "ShareConnect"
    protected open var appDescription: String = "Connect and share across your favorite applications"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        viewModel = OnboardingViewModel(application)

        setContent {
            val selectedTheme by viewModel.selectedTheme.collectAsState()

            OnboardingTheme(selectedTheme = selectedTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OnboardingNavHost(viewModel)
                }
            }
        }
    }

    @Composable
    private fun OnboardingNavHost(viewModel: OnboardingViewModel) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "welcome") {
            composable("welcome") {
                WelcomeScreen(
                    appName = appName,
                    appDescription = appDescription,
                    onNext = { navController.navigate("theme") }
                )
            }
            composable("theme") {
                ThemeSelectionScreen(
                    viewModel = viewModel,
                    onNext = { navController.navigate("language") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("language") {
                LanguageSelectionScreen(
                    viewModel = viewModel,
                    onNext = { navController.navigate("profile") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("profile") {
                ProfileCreationScreen(
                    viewModel = viewModel,
                    onNext = { navController.navigate("completion") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("completion") {
                CompletionScreen(
                    onComplete = {
                        viewModel.markOnboardingComplete()
                        launchMainApp()
                    }
                )
            }
        }
    }

    protected open fun launchMainApp() {
        // This will be overridden by each app to launch their specific main activity
        finish()
    }
}