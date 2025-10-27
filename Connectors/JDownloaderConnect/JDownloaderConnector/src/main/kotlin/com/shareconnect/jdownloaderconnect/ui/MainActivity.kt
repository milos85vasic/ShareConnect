/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


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
                onNavigateToMyJDownloader = { navController.navigate(Screen.MyJDownloader.route) },
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
        composable(Screen.MyJDownloader.route) {
            MyJDownloaderScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = appContainer.myJDownloaderViewModel
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
    object MyJDownloader : Screen("myjdownloader")
    object Settings : Screen("settings")
}