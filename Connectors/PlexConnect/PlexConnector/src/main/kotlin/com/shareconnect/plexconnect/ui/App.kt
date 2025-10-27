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


package com.shareconnect.plexconnect.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shareconnect.plexconnect.ui.viewmodels.AppViewModel
import com.shareconnect.plexconnect.ui.viewmodels.AppViewModelFactory

// Plex-specific color scheme
private val PlexLightColorScheme = lightColorScheme(
    primary = Color(0xFFE5A00D), // Plex orange
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE082),
    onPrimaryContainer = Color(0xFF2E1A00),
    secondary = Color(0xFF1E88E5), // Plex blue
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1E4FF),
    onSecondaryContainer = Color(0xFF001D36),
    tertiary = Color(0xFF43A047), // Success green
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB8E6BA),
    onTertiaryContainer = Color(0xFF002107),
    background = Color(0xFFFDFCF8),
    onBackground = Color(0xFF1B1B1B),
    surface = Color(0xFFFDFCF8),
    onSurface = Color(0xFF1B1B1B),
    surfaceVariant = Color(0xFFE7E2E1),
    onSurfaceVariant = Color(0xFF494645),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

private val PlexDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB74D), // Lighter Plex orange for dark theme
    onPrimary = Color(0xFF4A2C00),
    primaryContainer = Color(0xFF6B4E00),
    onPrimaryContainer = Color(0xFFFFE082),
    secondary = Color(0xFF64B5F6), // Lighter Plex blue for dark theme
    onSecondary = Color(0xFF003258),
    secondaryContainer = Color(0xFF00497D),
    onSecondaryContainer = Color(0xFFD1E4FF),
    tertiary = Color(0xFF66BB6A), // Lighter success green for dark theme
    onTertiary = Color(0xFF00390F),
    tertiaryContainer = Color(0xFF005221),
    onTertiaryContainer = Color(0xFFB8E6BA),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF494645),
    onSurfaceVariant = Color(0xFFCAC6C5),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun App() {
    val isDarkTheme = isSystemInDarkTheme()

    MaterialTheme(
        colorScheme = if (isDarkTheme) PlexDarkColorScheme else PlexLightColorScheme
    ) {
        Box(modifier = Modifier.testTag("plex_main_screen")) {
            AppNavigation()
        }
    }
}