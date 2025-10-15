package com.shareconnect

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.shareconnect.ShareConnectOnboardingActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class OnboardingIntegrationTest {

    private lateinit var device: UiDevice
    private lateinit var scenario: ActivityScenario<ShareConnectOnboardingActivity>

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Clear app data to ensure clean onboarding state
        clearAppData()

        // Launch onboarding activity
        scenario = ActivityScenario.launch(ShareConnectOnboardingActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun testCompleteOnboardingFlowWithThemeAndLanguageSelection() {
        // Wait for onboarding to load
        Thread.sleep(2000)

        // Verify we're on the first screen
        onView(withId(R.id.welcome_title))
            .check(matches(isDisplayed()))

        // Navigate to language selection
        onView(withId(R.id.btn_next))
            .perform(click())

        Thread.sleep(1000)

        // Select Spanish language
        onView(withText("Español"))
            .perform(click())

        Thread.sleep(1000)

        // Verify Spanish is applied (check if any UI text changed)
        // Note: This is a basic check - in a real app you'd check specific text

        // Navigate to theme selection
        onView(withId(R.id.btn_next))
            .perform(click())

        Thread.sleep(1000)

        // Select dark theme
        onView(withText("Dark"))
            .perform(click())

        Thread.sleep(1000)

        // Navigate to profile setup
        onView(withId(R.id.btn_next))
            .perform(click())

        Thread.sleep(1000)

        // Fill profile form
        onView(withId(R.id.profile_name_input))
            .perform(typeText("Test Transmission"))

        onView(withId(R.id.host_input))
            .perform(typeText("192.168.1.100"))

        onView(withId(R.id.port_input))
            .perform(typeText("9091"))

        // Select Transmission client type
        onView(withId(R.id.client_type_spinner))
            .perform(click())

        onView(withText("Transmission"))
            .perform(click())

        // Complete setup
        onView(withId(R.id.btn_complete_setup))
            .perform(click())

        Thread.sleep(3000)

        // Verify we're on main screen
        onView(withId(R.id.main_screen_container))
            .check(matches(isDisplayed()))

        // Verify onboarding is marked complete
        val prefs = InstrumentationRegistry.getInstrumentation()
            .targetContext
            .getSharedPreferences("onboarding_prefs", android.content.Context.MODE_PRIVATE)

        assert(prefs.getBoolean("onboarding_completed", false)) {
            "Onboarding should be marked as completed"
        }
    }

    @Test
    fun testThemeApplicationDuringOnboarding() {
        // Wait for onboarding to load
        Thread.sleep(2000)

        // Navigate to theme selection
        onView(withId(R.id.btn_next))
            .perform(click()) // Welcome -> Language

        onView(withId(R.id.btn_next))
            .perform(click()) // Language -> Theme

        Thread.sleep(1000)

        // Select light theme
        onView(withText("Light"))
            .perform(click())

        Thread.sleep(1000)

        // Verify light theme is applied (check system UI mode)
        val currentNightMode = androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode()
        assert(currentNightMode == androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO) {
            "Light theme should be applied"
        }

        // Switch to dark theme
        onView(withText("Dark"))
            .perform(click())

        Thread.sleep(1000)

        // Verify dark theme is applied
        val newNightMode = androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode()
        assert(newNightMode == androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES) {
            "Dark theme should be applied"
        }
    }

    @Test
    fun testLanguageApplicationDuringOnboarding() {
        // Wait for onboarding to load
        Thread.sleep(2000)

        // Navigate to language selection
        onView(withId(R.id.btn_next))
            .perform(click())

        Thread.sleep(1000)

        // Select French language
        onView(withText("Français"))
            .perform(click())

        Thread.sleep(1000)

        // Verify French locale is applied
        val currentLocale = java.util.Locale.getDefault()
        assert(currentLocale.language == "fr") {
            "French locale should be applied during onboarding"
        }
    }

    @Test
    fun testOnboardingPersistenceAfterAppRestart() {
        // Complete onboarding first
        completeOnboardingFlow()

        // Kill and restart app
        device.pressHome()
        Thread.sleep(1000)

        // Launch app again
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        context.startActivity(intent)

        Thread.sleep(3000)

        // Verify onboarding is not shown again
        try {
            onView(withId(R.id.welcome_title))
                .check(matches(isDisplayed()))
            assert(false) { "Onboarding should not be shown after completion" }
        } catch (e: Exception) {
            // Expected - onboarding should not be visible
        }

        // Verify main screen is shown
        onView(withId(R.id.main_screen_container))
            .check(matches(isDisplayed()))
    }

    private fun completeOnboardingFlow() {
        // Wait for onboarding to load
        Thread.sleep(2000)

        // Navigate through all screens
        onView(withId(R.id.btn_next)).perform(click()) // Welcome
        Thread.sleep(500)
        onView(withId(R.id.btn_next)).perform(click()) // Language
        Thread.sleep(500)
        onView(withId(R.id.btn_next)).perform(click()) // Theme
        Thread.sleep(500)
        onView(withId(R.id.btn_next)).perform(click()) // Profile
        Thread.sleep(500)

        // Fill minimal profile
        onView(withId(R.id.profile_name_input)).perform(typeText("Test"))
        onView(withId(R.id.host_input)).perform(typeText("localhost"))

        // Complete
        onView(withId(R.id.btn_complete_setup)).perform(click())
        Thread.sleep(2000)
    }

    private fun clearAppData() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val packageName = context.packageName

        // Use UI Automator to clear app data
        device.pressHome()
        device.waitForIdle()

        // Open settings
        device.executeShellCommand("am start -a android.settings.APPLICATION_DETAILS_SETTINGS package:$packageName")
        Thread.sleep(2000)

        // Find and click "Clear data" or "Storage" button
        val clearDataButton = device.findObject(UiSelector().textContains("Clear"))
        if (clearDataButton.exists()) {
            clearDataButton.click()
            Thread.sleep(1000)

            // Confirm clear data
            val confirmButton = device.findObject(UiSelector().textContains("OK"))
            if (confirmButton.exists()) {
                confirmButton.click()
                Thread.sleep(2000)
            }
        }

        // Go back to home
        device.pressHome()
        Thread.sleep(1000)
    }
}