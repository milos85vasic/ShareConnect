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
    }

    fun selectTheme(theme: ThemeData) {
        _selectedTheme.value = theme
    }

    fun selectLanguage(language: LanguageData) {
        _selectedLanguage.value = language
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