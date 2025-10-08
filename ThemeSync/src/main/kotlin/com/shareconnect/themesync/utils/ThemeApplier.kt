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
