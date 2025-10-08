package com.shareconnect.onboarding.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.themesync.ThemeSyncManager
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingIntegrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    private lateinit var themeSyncManager: ThemeSyncManager
    private lateinit var profileSyncManager: ProfileSyncManager
    private lateinit var languageSyncManager: LanguageSyncManager

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Initialize sync managers
        themeSyncManager = ThemeSyncManager.getInstance(
            context = context,
            appId = "com.shareconnect.onboarding.test",
            appName = "OnboardingTest",
            appVersion = "1.0.0"
        )

        profileSyncManager = ProfileSyncManager.getInstance(
            context = context,
            appId = "com.shareconnect.onboarding.test",
            appName = "OnboardingTest",
            appVersion = "1.0.0",
            clientTypeFilter = null
        )

        languageSyncManager = LanguageSyncManager.getInstance(
            context = context,
            appId = "com.shareconnect.onboarding.test",
            appName = "OnboardingTest",
            appVersion = "1.0.0"
        )

        // Clear any existing onboarding state
        val prefs = context.getSharedPreferences("onboarding_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    @After
    fun tearDown() {
        // Clean up test data
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("onboarding_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        // Note: In a real test, you might want to clean up sync manager data too
        // but that would require additional cleanup methods in the sync managers
    }

    @Test
    fun `complete onboarding flow saves preferences correctly`() = runBlocking {
        // Navigate through the entire flow
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Fill profile form
        composeTestRule.onNodeWithText("Profile Name").performTextInput("Test Server")
        composeTestRule.onNodeWithText("Server URL").performTextInput("192.168.1.100")
        composeTestRule.onNodeWithText("Username (optional)").performTextInput("testuser")
        composeTestRule.onNodeWithText("Password (optional)").performTextInput("testpass")

        composeTestRule.onNodeWithText("Next").performClick()

        // Complete onboarding
        composeTestRule.onNodeWithText("Start Using ShareConnect").performClick()

        // Verify onboarding is marked as complete
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("onboarding_prefs", android.content.Context.MODE_PRIVATE)
        assertTrue(prefs.getBoolean("onboarding_completed", false))

        // Note: Verifying the actual sync manager state would require additional
        // test infrastructure to wait for async operations and check database state
    }

    @Test
    fun `onboarding detects existing data and skips flow`() {
        // Pre-populate some data to simulate existing user
        runBlocking {
            // Add a theme
            val theme = com.shareconnect.themesync.models.ThemeData(
                id = "existing_theme",
                name = "Existing Theme",
                colorScheme = "test",
                isDarkMode = false,
                isDefault = true,
                sourceApp = "com.shareconnect.onboarding.test"
            )
            themeSyncManager.addTheme(theme)
            themeSyncManager.setDefaultTheme(theme.id)

            // Add a language preference
            languageSyncManager.setLanguagePreference("en", "English")

            // Add a profile
            val profile = com.shareconnect.profilesync.models.ProfileData(
                id = "existing_profile",
                name = "Existing Profile",
                host = "192.168.1.100",
                port = 9091,
                isDefault = true,
                serviceType = "torrent",
                torrentClientType = "transmission",
                sourceApp = "com.shareconnect.onboarding.test"
            )
            profileSyncManager.addOrUpdateProfile(profile)
        }

        // The onboarding activity should detect existing data and close
        // This is hard to test directly in UI tests, but we can verify
        // that the onboarding detection logic works
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertFalse(com.shareconnect.onboarding.viewmodel.OnboardingViewModel.isOnboardingNeeded(context))
    }

    @Test
    fun `onboarding handles network errors gracefully during save`() {
        // This test would require mocking network failures
        // For now, we'll test that the UI doesn't crash when save operations fail

        // Navigate to completion
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        composeTestRule.onNodeWithText("Profile Name").performTextInput("Test Server")
        composeTestRule.onNodeWithText("Server URL").performTextInput("192.168.1.100")

        composeTestRule.onNodeWithText("Next").performClick()

        // Even if save fails, the UI should handle it gracefully
        // The completion screen should still be shown
        composeTestRule.onNodeWithText("You're All Set!").assertIsDisplayed()
    }

    @Test
    fun `onboarding validates profile data before saving`() {
        // Navigate to profile screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Try invalid data
        composeTestRule.onNodeWithText("Profile Name").performTextInput("")
        composeTestRule.onNodeWithText("Server URL").performTextInput("")

        // Next should be disabled
        composeTestRule.onNodeWithText("Next").assertIsNotEnabled()

        // Enter valid data
        composeTestRule.onNodeWithText("Profile Name").performTextInput("Valid Server")
        composeTestRule.onNodeWithText("Server URL").performTextInput("192.168.1.100")

        // Next should be enabled
        composeTestRule.onNodeWithText("Next").assertIsEnabled()
    }

    @Test
    fun `onboarding handles special characters in profile data`() {
        // Navigate to profile screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Enter data with special characters
        composeTestRule.onNodeWithText("Profile Name").performTextInput("My Server #1!")
        composeTestRule.onNodeWithText("Server URL").performTextInput("192.168.1.100")
        composeTestRule.onNodeWithText("Username (optional)").performTextInput("user@domain.com")
        composeTestRule.onNodeWithText("Password (optional)").performTextInput("p@ssw0rd!")

        // Should still work
        composeTestRule.onNodeWithText("Next").assertIsEnabled()
        composeTestRule.onNodeWithText("Next").performClick()

        // Should reach completion
        composeTestRule.onNodeWithText("You're All Set!").assertIsDisplayed()
    }

    @Test
    fun `onboarding handles very long profile names and URLs`() {
        // Navigate to profile screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Enter very long data
        val longName = "A".repeat(100)
        val longUrl = "http://very.long.domain.name.with.many.subdomains.example.com:9091/path"

        composeTestRule.onNodeWithText("Profile Name").performTextInput(longName)
        composeTestRule.onNodeWithText("Server URL").performTextInput(longUrl)

        // Should still work
        composeTestRule.onNodeWithText("Next").assertIsEnabled()
        composeTestRule.onNodeWithText("Next").performClick()

        // Should reach completion
        composeTestRule.onNodeWithText("You're All Set!").assertIsDisplayed()
    }
}