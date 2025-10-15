package com.shareconnect.onboarding.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.languagesync.models.LanguageData
import com.shareconnect.languagesync.utils.LocaleHelper
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.profilesync.models.ProfileData
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.themesync.models.ThemeData
import com.shareconnect.themesync.utils.ThemeApplier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = application
    private var onboardingActivity: Activity? = null

    // Sync managers - will be injected or retrieved from application
    private lateinit var themeSyncManager: ThemeSyncManager
    private lateinit var profileSyncManager: ProfileSyncManager
    private lateinit var languageSyncManager: LanguageSyncManager

    // Selected preferences
    private val _selectedTheme = MutableStateFlow<ThemeData?>(null)
    val selectedTheme: StateFlow<ThemeData?> = _selectedTheme.asStateFlow()

    private val _selectedLanguage = MutableStateFlow<LanguageData?>(null)
    val selectedLanguage: StateFlow<LanguageData?> = _selectedLanguage.asStateFlow()

    private val _selectedProfile = MutableStateFlow<ProfileData?>(null)
    val selectedProfile: StateFlow<ProfileData?> = _selectedProfile.asStateFlow()

    // Available options
    private val _availableThemes = MutableStateFlow<List<ThemeData>>(emptyList())
    val availableThemes: StateFlow<List<ThemeData>> = _availableThemes.asStateFlow()

    private val _availableLanguages = MutableStateFlow<List<LanguageData>>(emptyList())
    val availableLanguages: StateFlow<List<LanguageData>> = _availableLanguages.asStateFlow()

    // Loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Load available languages immediately (hardcoded list)
        loadAvailableLanguages()
        // Restore previously selected preferences if any
        restoreSelectedLanguage()
        restoreSelectedTheme()
    }

    fun initializeSyncManagers(
        themeManager: ThemeSyncManager,
        profileManager: ProfileSyncManager,
        languageManager: LanguageSyncManager
    ) {
        themeSyncManager = themeManager
        profileSyncManager = profileManager
        languageSyncManager = languageManager

        loadAvailableThemes()
    }

    fun setOnboardingActivity(activity: Activity) {
        onboardingActivity = activity
    }

    fun selectTheme(theme: ThemeData) {
        _selectedTheme.value = theme
        // Persist the selection for onboarding
        persistSelectedTheme(theme)
        // Apply theme immediately during onboarding
        applyTheme(theme)
    }

    fun selectLanguage(language: LanguageData) {
        _selectedLanguage.value = language
        // Persist the selection for onboarding
        persistSelectedLanguage(language)
        // Apply language immediately during onboarding
        applyLanguage(language)
    }

    fun selectProfile(profile: ProfileData) {
        _selectedProfile.value = profile
    }

    fun saveSelectedPreferences() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Save theme preference
                _selectedTheme.value?.let { theme ->
                    themeSyncManager.setDefaultTheme(theme.id)
                }

                // Save language preference
                _selectedLanguage.value?.let { language ->
                    languageSyncManager.setLanguagePreference(language.languageCode, language.displayName)
                }

                // Save profile (this would typically create a new profile)
                _selectedProfile.value?.let { profile ->
                    profileSyncManager.addOrUpdateProfile(profile)
                }

            } catch (e: Exception) {
                // Handle errors - could emit error state
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadAvailableThemes() {
        viewModelScope.launch {
            try {
                val themes = themeSyncManager.getAllThemes().first()
                _availableThemes.value = themes
            } catch (e: Exception) {
                // Fallback to default themes if sync manager fails
                _availableThemes.value = ThemeData.getDefaultThemes("com.shareconnect")
            }
        }
    }

    private fun loadAvailableLanguages() {
        // Load supported languages - these are the languages the app supports
        _availableLanguages.value = listOf(
            LanguageData(languageCode = "en", displayName = "English"),
            LanguageData(languageCode = "es", displayName = "Español"),
            LanguageData(languageCode = "fr", displayName = "Français"),
            LanguageData(languageCode = "de", displayName = "Deutsch"),
            LanguageData(languageCode = "it", displayName = "Italiano"),
            LanguageData(languageCode = "pt", displayName = "Português"),
            LanguageData(languageCode = "ru", displayName = "Русский"),
            LanguageData(languageCode = "ja", displayName = "日本語"),
            LanguageData(languageCode = "ko", displayName = "한국어"),
            LanguageData(languageCode = "zh", displayName = "中文"),
            LanguageData(languageCode = "ar", displayName = "العربية"),
            LanguageData(languageCode = "hi", displayName = "हिन्दी"),
            LanguageData(languageCode = "bn", displayName = "বাংলা"),
            LanguageData(languageCode = "kn", displayName = "ಕನ್ನಡ")
        )
    }

    private fun persistSelectedLanguage(language: LanguageData) {
        val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("selected_language_code", language.languageCode)
            .putString("selected_language_name", language.displayName)
            .apply()
    }

    private fun persistSelectedTheme(theme: ThemeData) {
        val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("selected_theme_id", theme.id)
            .putString("selected_theme_name", theme.name)
            .apply()
    }

    private fun restoreSelectedLanguage() {
        val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("selected_language_code", null)
        val languageName = prefs.getString("selected_language_name", null)

        if (languageCode != null && languageName != null) {
            val language = LanguageData(
                id = "language_preference",
                languageCode = languageCode,
                displayName = languageName
            )
            _selectedLanguage.value = language
        }
    }

    private fun restoreSelectedTheme() {
        val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        val themeId = prefs.getString("selected_theme_id", null)
        val themeName = prefs.getString("selected_theme_name", null)

        if (themeId != null && themeName != null) {
            // Try to find the theme from available themes first
            viewModelScope.launch {
                try {
                    val availableThemes = themeSyncManager.getAllThemes().first()
                    val existingTheme = availableThemes.find { it.id == themeId }
                    if (existingTheme != null) {
                        _selectedTheme.value = existingTheme
                        // Apply the theme immediately
                        applyTheme(existingTheme)
                    } else {
                        // Create a basic theme if not found, but preserve dark mode from name
                        val isDarkMode = themeName.contains("dark", ignoreCase = true)
                        val theme = ThemeData(
                            id = themeId,
                            name = themeName,
                            colorScheme = "default",
                            isDarkMode = isDarkMode,
                            isDefault = false,
                            sourceApp = "onboarding"
                        )
                        _selectedTheme.value = theme
                        // Apply the theme immediately
                        applyTheme(theme)
                    }
                } catch (e: Exception) {
                    // Fallback to basic theme
                    val isDarkMode = themeName.contains("dark", ignoreCase = true)
                    val theme = ThemeData(
                        id = themeId,
                        name = themeName,
                        colorScheme = "default",
                        isDarkMode = isDarkMode,
                        isDefault = false,
                        sourceApp = "onboarding"
                    )
                    _selectedTheme.value = theme
                    applyTheme(theme)
                }
            }
        }
    }

    private fun clearPersistedSelections() {
        val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .remove("selected_language_code")
            .remove("selected_language_name")
            .remove("selected_theme_id")
            .remove("selected_theme_name")
            .apply()
    }

    private fun applyTheme(theme: ThemeData) {
        // Apply theme by updating the app's theme mode
        try {
            ThemeApplier.applyDarkMode(theme)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyLanguage(language: LanguageData) {
        // Apply language by updating the app's locale
        viewModelScope.launch {
            try {
                // Save the preference to sync manager
                languageSyncManager.setLanguagePreference(language.languageCode, language.displayName)

                // Apply the language change to the current activity without recreating it
                onboardingActivity?.let { activity ->
                    // Update the activity's resources configuration for immediate effect
                    val newContext = LocaleHelper.setLocale(activity, language.languageCode)
                    val config = newContext.resources.configuration
                    activity.resources.updateConfiguration(config, activity.resources.displayMetrics)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun markOnboardingComplete() {
        // Save that onboarding is complete
        val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("onboarding_completed", true).apply()

        // Apply the selected language now that onboarding is complete
        _selectedLanguage.value?.let { language ->
            applyLanguage(language)
        }

        // Clear temporary persisted selections
        clearPersistedSelections()

        // Save the selected preferences
        saveSelectedPreferences()
    }

    companion object {
        fun isOnboardingNeeded(context: Context): Boolean {
            val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
            return !prefs.getBoolean("onboarding_completed", false)
        }
    }
}