package com.shareconnect.plexconnect.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shareconnect.plexconnect.ui.viewmodels.AppViewModel
import com.shareconnect.plexconnect.ui.viewmodels.AppViewModelFactory

@Composable
fun App() {
    val viewModel = viewModel<AppViewModel>(
        factory = AppViewModelFactory()
    )

    // For now, use system theme - can be enhanced later with custom theme support
    val isDarkTheme = isSystemInDarkTheme()

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        Box(modifier = Modifier.testTag("plex_main_screen")) {
            AppNavigation()
        }
    }
}