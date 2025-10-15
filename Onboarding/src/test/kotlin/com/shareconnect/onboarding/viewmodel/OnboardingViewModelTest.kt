package com.shareconnect.onboarding.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.languagesync.models.LanguageData
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.profilesync.models.ProfileData
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.themesync.models.ThemeData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class SimpleOnboardingViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockApplication: Application

    @Mock
    private lateinit var mockSharedPreferences: android.content.SharedPreferences

    @Mock
    private lateinit var mockEditor: android.content.SharedPreferences.Editor

    @Mock
    private lateinit var mockThemeSyncManager: ThemeSyncManager

    @Mock
    private lateinit var mockProfileSyncManager: ProfileSyncManager

    @Mock
    private lateinit var mockLanguageSyncManager: LanguageSyncManager

    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Mock SharedPreferences
        `when`(mockApplication.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences)
        `when`(mockSharedPreferences.getString(anyString(), anyString())).thenReturn(null)
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        `when`(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor)
        `when`(mockEditor.remove(anyString())).thenReturn(mockEditor)
        `when`(mockEditor.apply()).then { }

        // Mock sync manager methods - simplified to avoid suspend function issues
        `when`(mockThemeSyncManager.getAllThemes()).thenReturn(kotlinx.coroutines.flow.flowOf(emptyList()))

        viewModel = OnboardingViewModel(mockApplication)
        viewModel.initializeSyncManagers(mockThemeSyncManager, mockProfileSyncManager, mockLanguageSyncManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has no selected preferences`() = runTest {
        assertNull(viewModel.selectedTheme.value)
        assertNull(viewModel.selectedLanguage.value)
        assertNull(viewModel.selectedProfile.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `selectTheme updates selected theme`() = runTest {
        val theme = ThemeData(
            id = "test_theme",
            name = "Test Theme",
            colorScheme = "test",
            isDarkMode = false,
            isDefault = true,
            sourceApp = "test"
        )

        viewModel.selectTheme(theme)

        assertEquals(theme, viewModel.selectedTheme.value)
    }

    @Test
    fun `selectLanguage updates selected language`() = runTest {
        val language = LanguageData(
            languageCode = "en",
            displayName = "English"
        )

        viewModel.selectLanguage(language)

        assertEquals(language, viewModel.selectedLanguage.value)
    }

    @Test
    fun `selectProfile updates selected profile`() = runTest {
        val profile = ProfileData(
            id = "test_profile",
            name = "Test Profile",
            host = "192.168.1.100",
            port = 9091,
            isDefault = true,
            serviceType = "torrent",
            torrentClientType = "transmission",
            username = null,
            password = null,
            sourceApp = "test"
        )

        viewModel.selectProfile(profile)

        assertEquals(profile, viewModel.selectedProfile.value)
    }

    @Test
    fun `markOnboardingComplete sets onboarding completed flag in preferences`() = runTest {
        // Given - no selections needed for this basic test

        // When
        viewModel.markOnboardingComplete()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(mockEditor).putBoolean("onboarding_completed", true)
        verify(mockEditor, Mockito.times(2)).apply()  // Called once in markOnboardingComplete and once in clearPersistedSelections
    }

    @Test
    fun `isOnboardingNeeded returns true when not completed`() {
        val mockPrefs = mock(android.content.SharedPreferences::class.java)
        `when`(mockApplication.getSharedPreferences("onboarding_prefs", android.content.Context.MODE_PRIVATE)).thenReturn(mockPrefs)
        `when`(mockPrefs.getBoolean("onboarding_completed", false)).thenReturn(false)

        assertTrue(OnboardingViewModel.isOnboardingNeeded(mockApplication))
    }

    @Test
    fun `isOnboardingNeeded returns false when completed`() {
        val mockPrefs = mock(android.content.SharedPreferences::class.java)
        `when`(mockApplication.getSharedPreferences("onboarding_prefs", android.content.Context.MODE_PRIVATE)).thenReturn(mockPrefs)
        `when`(mockPrefs.getBoolean("onboarding_completed", false)).thenReturn(true)

        assertFalse(OnboardingViewModel.isOnboardingNeeded(mockApplication))
    }
}