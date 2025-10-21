package com.shareconnect.jdownloaderconnect.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shareconnect.jdownloaderconnect.AppContainer
import com.shareconnect.jdownloaderconnect.ui.onboarding.JDownloaderConnectOnboardingActivity
import com.shareconnect.jdownloaderconnect.ui.screens.*
import com.shareconnect.jdownloaderconnect.ui.theme.JDownloaderConnectTheme

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if onboarding is completed
        val sharedPrefs = getSharedPreferences("jdownloader_prefs", MODE_PRIVATE)
        val onboardingCompleted = sharedPrefs.getBoolean("onboarding_completed", false)
        
        if (!onboardingCompleted) {
            // Launch onboarding activity
            val intent = Intent(this, JDownloaderConnectOnboardingActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        
        appContainer = AppContainer(this)
        
        setContent {
            JDownloaderConnectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    JDownloaderConnectApp(appContainer)
                }
            }
        }
    }
}

@Composable
fun JDownloaderConnectApp(appContainer: AppContainer) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Accounts.route
    ) {
        composable(Screen.Accounts.route) {
            AccountsScreen(
                onNavigateToDownloads = { navController.navigate(Screen.Downloads.route) },
                onNavigateToLinkGrabber = { navController.navigate(Screen.LinkGrabber.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                viewModel = appContainer.accountViewModel
            )
        }
        composable(Screen.Downloads.route) {
            DownloadsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAccounts = { navController.navigate(Screen.Accounts.route) },
                downloadsViewModel = appContainer.downloadsViewModel,
                accountViewModel = appContainer.accountViewModel
            )
        }
        composable(Screen.LinkGrabber.route) {
            LinkGrabberScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAccounts = { navController.navigate(Screen.Accounts.route) },
                linkGrabberViewModel = appContainer.linkGrabberViewModel,
                accountViewModel = appContainer.accountViewModel
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                accountViewModel = appContainer.accountViewModel
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Accounts : Screen("accounts")
    object Downloads : Screen("downloads")
    object LinkGrabber : Screen("linkgrabber")
    object Settings : Screen("settings")
}