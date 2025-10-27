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


package com.shareconnect.themesync.utils

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import com.shareconnect.themesync.models.ThemeData

/**
 * Utility class to help apps apply themes consistently.
 * Apps should extend this or implement their own theme application logic.
 */
object ThemeApplier {

    /**
     * Apply dark/light mode based on theme data
     */
    fun applyDarkMode(theme: ThemeData) {
        if (theme.isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    /**
     * Get theme identifier for styling
     */
    fun getThemeIdentifier(theme: ThemeData): String {
        return "${theme.colorScheme.uppercase()}_${if (theme.isDarkMode) "DARK" else "LIGHT"}"
    }

    /**
     * Check if a theme is light mode
     */
    fun isLightMode(theme: ThemeData): Boolean = !theme.isDarkMode

    /**
     * Check if a theme is dark mode
     */
    fun isDarkMode(theme: ThemeData): Boolean = theme.isDarkMode

    /**
     * Get display name for theme
     */
    fun getDisplayName(theme: ThemeData): String = theme.name

    /**
     * Get color scheme name
     */
    fun getColorSchemeName(theme: ThemeData): String {
        return theme.colorScheme.split("_").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}
