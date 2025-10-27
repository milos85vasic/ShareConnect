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


package com.shareconnect

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.redelf.commons.logging.Console
import com.shareconnect.database.Theme
import com.shareconnect.database.ThemeRepository
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.themesync.models.ThemeData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ThemeManager private constructor(private val context: Context) {
    private val themeRepository: ThemeRepository
    private val sharedPreferences: SharedPreferences
    private var themeSyncManager: ThemeSyncManager? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        themeRepository = ThemeRepository(context)
        themeRepository.initializeDefaultThemes()
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Initialize theme sync manager lazily to avoid race condition
        initializeThemeSyncManager()
    }

    private fun initializeThemeSyncManager() {
        try {
            // Get ThemeSyncManager from Application - may not be initialized yet
            val app = context.applicationContext as SCApplication
            if (themeSyncManager != null) {
                // Already initialized
                return
            }

            themeSyncManager = app.themeSyncManager

            // Observe theme changes from sync
            scope.launch {
                themeSyncManager?.themeChangeFlow?.collect { syncedTheme ->
                    // If this is the default theme, notify change
                    if (syncedTheme.isDefault) {
                        notifyThemeChanged()
                    }
                }
            }
        } catch (e: UninitializedPropertyAccessException) {
            // themeSyncManager not initialized yet in application
            // Will be initialized later when needed
            Console.debug("ThemeManager: themeSyncManager not ready yet, will initialize later")
        }
    }

    fun applyTheme(activity: Activity) {
        val defaultTheme = themeRepository.defaultTheme
        Console.debug(context.getString(R.string.log_get_default_theme, if (defaultTheme != null) defaultTheme.name + " (ID: " + defaultTheme.id + ", isDefault: " + defaultTheme.isDefault + ")" else "null"))
        if (defaultTheme != null) {
            applyTheme(activity, defaultTheme)
        } else {
            // Apply default material theme
            Console.debug(context.getString(R.string.log_applying_default_material_theme))
            activity.setTheme(R.style.Theme_ShareConnect_Material_Light)
        }
    }

    fun applyTheme(activity: Activity, theme: Theme) {
        val colorScheme = theme.colorScheme
        val isDarkMode = theme.isDarkMode

        val currentTheme = (colorScheme ?: "").uppercase() + if (isDarkMode) "_DARK" else "_LIGHT"

        Console.debug(context.getString(R.string.log_applying_theme, theme.name, colorScheme, isDarkMode, currentTheme))

        // Check if activity uses Toolbar (requires NoActionBar theme)
        val usesToolbar = activity is MainActivity || activity is ThemeSelectionActivity || activity is SettingsActivity || activity is SplashActivity || activity is ProfilesActivity || activity is ShareActivity || activity is EditProfileActivity || activity is HistoryActivity || activity is WebUIActivity

        Console.debug(context.getString(R.string.log_activity_uses_toolbar, activity.javaClass.simpleName, usesToolbar))

        when (currentTheme) {
            "WARM_ORANGE_DARK" -> activity.setTheme(
                if (usesToolbar)
                    R.style.Theme_ShareConnect_WarmOrange_Dark_NoActionBar
                else
                    R.style.Theme_ShareConnect_WarmOrange_Dark
            )
            "WARM_ORANGE_LIGHT" -> activity.setTheme(
                if (usesToolbar)
                    R.style.Theme_ShareConnect_WarmOrange_Light_NoActionBar
                else
                    R.style.Theme_ShareConnect_WarmOrange_Light
            )
            "CRIMSON_DARK" -> activity.setTheme(
                if (usesToolbar)
                    R.style.Theme_ShareConnect_Crimson_Dark_NoActionBar
                else
                    R.style.Theme_ShareConnect_Crimson_Dark
            )
            "CRIMSON_LIGHT" -> activity.setTheme(
                if (usesToolbar)
                    R.style.Theme_ShareConnect_Crimson_Light_NoActionBar
                else
                    R.style.Theme_ShareConnect_Crimson_Light
            )
            "LIGHT_BLUE_DARK" -> activity.setTheme(
                if (usesToolbar)
                    R.style.Theme_ShareConnect_LightBlue_Dark_NoActionBar
                else
                    R.style.Theme_ShareConnect_LightBlue_Dark
            )
            "LIGHT_BLUE_LIGHT" -> activity.setTheme(
                if (usesToolbar)
                    R.style.Theme_ShareConnect_LightBlue_Light_NoActionBar
                else
                    R.style.Theme_ShareConnect_LightBlue_Light
            )
            "PURPLE_DARK" -> activity.setTheme(
                if (usesToolbar)
                    R.style.Theme_ShareConnect_Purple_Dark_NoActionBar
                else
                    R.style.Theme_ShareConnect_Purple_Dark
            )
            "PURPLE_LIGHT" -> activity.setTheme(
                if (usesToolbar)
                    R.style.Theme_ShareConnect_Purple_Light_NoActionBar
                else
                    R.style.Theme_ShareConnect_Purple_Light
            )
            "GREEN_DARK" -> activity.setTheme(
                if (usesToolbar)
                    R.style.Theme_ShareConnect_Green_Dark_NoActionBar
                else
                    R.style.Theme_ShareConnect_Green_Dark
            )
            "GREEN_LIGHT" -> activity.setTheme(
                if (usesToolbar)
                    R.style.Theme_ShareConnect_Green_Light_NoActionBar
                else
                    R.style.Theme_ShareConnect_Green_Light
            )
            "MATERIAL_DARK" -> activity.setTheme(
                if (usesToolbar)
                    R.style.Theme_ShareConnect_Material_Dark_NoActionBar
                else
                    R.style.Theme_ShareConnect_Material_Dark
            )
            "MATERIAL_LIGHT" -> activity.setTheme(
                if (usesToolbar)
                    R.style.Theme_ShareConnect_Material_Light_NoActionBar
                else
                    R.style.Theme_ShareConnect_Material_Light
            )
            else -> activity.setTheme(
                if (usesToolbar)
                    R.style.Theme_ShareConnect_Material_Light_NoActionBar
                else
                    R.style.Theme_ShareConnect_Material_Light
            )
        }

        // Apply day/night mode
        if (isDarkMode) {
            Console.debug(context.getString(R.string.log_setting_night_mode_yes))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            Console.debug(context.getString(R.string.log_setting_night_mode_no))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    val themeRepositoryVal: ThemeRepository
        get() = themeRepository

    // Method to notify that theme has changed
    fun notifyThemeChanged() {
        Console.debug(context.getString(R.string.log_notify_theme_changed_called))
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_THEME_CHANGED, true)
        editor.commit()
        Console.debug(context.getString(R.string.log_notify_theme_changed_completed))
    }

    // Method to reset theme changed flag
    fun resetThemeChangedFlag() {
        Console.debug(context.getString(R.string.log_reset_theme_changed_flag_called))
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_THEME_CHANGED, false)
        editor.commit()
        Console.debug(context.getString(R.string.log_reset_theme_changed_flag_completed))
    }

    // Method to check if theme has changed
    fun hasThemeChanged(): Boolean {
        val changed = sharedPreferences.getBoolean(KEY_THEME_CHANGED, false)
        Console.debug(context.getString(R.string.log_has_theme_changed, changed))
        return changed
    }

    companion object {
        private var instance: ThemeManager? = null
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME_CHANGED = "theme_changed"

        @Synchronized
        fun getInstance(context: Context): ThemeManager {
            if (instance == null) {
                instance = ThemeManager(context.applicationContext)
            }
            return instance!!
        }
    }
}