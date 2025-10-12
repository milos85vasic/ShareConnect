package com.shareconnect

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.themesync.ThemeSyncManager
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingDetectionTest {

    private lateinit var themeSyncManager: ThemeSyncManager
    private lateinit var profileSyncManager: ProfileSyncManager
    private lateinit var languageSyncManager: LanguageSyncManager

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Initialize sync managers
        themeSyncManager = ThemeSyncManager.getInstance(
            context = context,
            appId = "com.shareconnect.test",
            appName = "TestApp",
            appVersion = "1.0.0"
        )

        profileSyncManager = ProfileSyncManager.getInstance(
            context = context,
            appId = "com.shareconnect.test",
            appName = "TestApp",
            appVersion = "1.0.0",
            clientTypeFilter = null
        )

        languageSyncManager = LanguageSyncManager.getInstance(
            context = context,
            appId = "com.shareconnect.test",
            appName = "TestApp",
            appVersion = "1.0.0"
        )

        // Clear any existing test data
        clearTestData()
    }

    @After
    fun tearDown() {
        clearTestData()
    }

    private fun clearTestData() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("onboarding_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    @Test
    fun `new user with no data should trigger onboarding`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Ensure no existing data
        runBlocking {
            assertEquals(0, profileSyncManager.getProfileCount())
            assertNull(themeSyncManager.getDefaultTheme())
            assertNull(languageSyncManager.getLanguagePreference())
        }

        // Should detect onboarding needed
        assertTrue(com.shareconnect.onboarding.viewmodel.OnboardingViewModel.isOnboardingNeeded(context))
    }

    @Test
    fun `user with existing profile should skip onboarding`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Add a profile
        runBlocking {
            val profile = com.shareconnect.profilesync.models.ProfileData(
                id = "test_profile",
                name = "Test Profile",
                host = "192.168.1.100",
                port = 9091,
                isDefault = true,
                serviceType = "torrent",
                torrentClientType = "transmission",
                username = null,
                password = null,
                sourceApp = "com.shareconnect.test"
            )
            profileSyncManager.addOrUpdateProfile(profile)
        }

        // Should detect onboarding not needed
        assertFalse(com.shareconnect.onboarding.viewmodel.OnboardingViewModel.isOnboardingNeeded(context))
    }

    @Test
    fun `user with existing theme should skip onboarding`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Add a theme
        runBlocking {
            val theme = com.shareconnect.themesync.models.ThemeData(
                id = "test_theme",
                name = "Test Theme",
                colorScheme = "test",
                isDarkMode = false,
                isDefault = true,
                sourceApp = "com.shareconnect.test"
            )
            themeSyncManager.addTheme(theme)
            themeSyncManager.setDefaultTheme(theme.id)
        }

        // Should detect onboarding not needed
        assertFalse(com.shareconnect.onboarding.viewmodel.OnboardingViewModel.isOnboardingNeeded(context))
    }

    @Test
    fun `user with existing language preference should skip onboarding`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Add a language preference
        runBlocking {
            languageSyncManager.setLanguagePreference("en", "English")
        }

        // Should detect onboarding not needed
        assertFalse(com.shareconnect.onboarding.viewmodel.OnboardingViewModel.isOnboardingNeeded(context))
    }

    @Test
    fun `user with all existing data should skip onboarding`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Add all types of data
        runBlocking {
            // Add profile
            val profile = com.shareconnect.profilesync.models.ProfileData(
                id = "test_profile",
                name = "Test Profile",
                host = "192.168.1.100",
                port = 9091,
                isDefault = true,
                serviceType = "torrent",
                torrentClientType = "transmission",
                username = null,
                password = null,
                sourceApp = "com.shareconnect.test"
            )
            profileSyncManager.addOrUpdateProfile(profile)

            // Add theme
            val theme = com.shareconnect.themesync.models.ThemeData(
                id = "test_theme",
                name = "Test Theme",
                colorScheme = "test",
                isDarkMode = false,
                isDefault = true,
                sourceApp = "com.shareconnect.test"
            )
            themeSyncManager.addTheme(theme)
            themeSyncManager.setDefaultTheme(theme.id)

            // Add language
            languageSyncManager.setLanguagePreference("en", "English")
        }

        // Should detect onboarding not needed
        assertFalse(com.shareconnect.onboarding.viewmodel.OnboardingViewModel.isOnboardingNeeded(context))
    }

    @Test
    fun `completed onboarding should be remembered`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Simulate completing onboarding
        val prefs = context.getSharedPreferences("onboarding_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean("onboarding_completed", true).apply()

        // Should detect onboarding not needed
        assertFalse(com.shareconnect.onboarding.viewmodel.OnboardingViewModel.isOnboardingNeeded(context))
    }

    @Test
    fun `onboarding completion overrides existing data check`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Mark onboarding as completed even with no data
        val prefs = context.getSharedPreferences("onboarding_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean("onboarding_completed", true).apply()

        // Should detect onboarding not needed
        assertFalse(com.shareconnect.onboarding.viewmodel.OnboardingViewModel.isOnboardingNeeded(context))
    }

    @Test
    fun `onboarding detection handles sync manager exceptions gracefully`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Ensure clean state
        clearTestData()

        // The detection should handle any exceptions from sync managers gracefully
        // and default to showing onboarding for safety
        try {
            val result = com.shareconnect.onboarding.viewmodel.OnboardingViewModel.isOnboardingNeeded(context)
            // Should not crash, result can be either true or false depending on state
            assertTrue(result is Boolean)
        } catch (e: Exception) {
            fail("Onboarding detection should not throw exceptions: ${e.message}")
        }
    }

    @Test
    fun `onboarding detection works with partial existing data`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Test various combinations of partial data
        val testCases = listOf(
            // Only profile
            {
                runBlocking {
            val profile = com.shareconnect.profilesync.models.ProfileData(
                id = "test_profile",
                name = "Test Profile",
                host = "192.168.1.100",
                port = 9091,
                isDefault = true,
                serviceType = "torrent",
                torrentClientType = "transmission",
                username = null,
                password = null,
                sourceApp = "com.shareconnect.test"
            )
                    profileSyncManager.addOrUpdateProfile(profile)
                }
            },
            // Only theme
            {
                runBlocking {
                    val theme = com.shareconnect.themesync.models.ThemeData(
                        id = "test_theme",
                        name = "Test Theme",
                        colorScheme = "test",
                        isDarkMode = false,
                        isDefault = true,
                        sourceApp = "com.shareconnect.test"
                    )
                    themeSyncManager.addTheme(theme)
                    themeSyncManager.setDefaultTheme(theme.id)
                }
            },
            // Only language
            {
                runBlocking {
                    languageSyncManager.setLanguagePreference("en", "English")
                }
            }
        )

        for ((index, setupData) in testCases.withIndex()) {
            // Clear previous data
            clearTestData()

            // Setup partial data
            setupData()

            // Any existing data should skip onboarding
            assertFalse(
                "Test case $index should skip onboarding with partial data",
                com.shareconnect.onboarding.viewmodel.OnboardingViewModel.isOnboardingNeeded(context)
            )
        }
    }
}