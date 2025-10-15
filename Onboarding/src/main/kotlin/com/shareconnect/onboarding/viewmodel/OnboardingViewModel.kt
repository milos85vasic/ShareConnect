package com.shareconnect.onboarding.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.languagesync.models.LanguageData
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.profilesync.models.ProfileData
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.themesync.models.ThemeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = application

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

    fun initializeSyncManagers(
        themeManager: ThemeSyncManager,
        profileManager: ProfileSyncManager,
        languageManager: LanguageSyncManager
    ) {
        themeSyncManager = themeManager
        profileSyncManager = profileManager
        languageSyncManager = languageManager

        // Load available themes and languages
        loadAvailableThemes()
        loadAvailableLanguages()
    }

    fun selectTheme(theme: ThemeData) {
        _selectedTheme.value = theme
    }

    fun selectLanguage(language: LanguageData) {
        _selectedLanguage.value = language
        // Immediately apply the language selection
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

    private fun applyLanguage(language: LanguageData) {
        // Apply language immediately by updating the app's locale
        // This would typically involve updating the app's configuration
        // For now, we'll just save the preference
        viewModelScope.launch {
            try {
                languageSyncManager.setLanguagePreference(language.languageCode, language.displayName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun markOnboardingComplete() {
        // Save that onboarding is complete
        val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("onboarding_completed", true).apply()

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