package com.shareconnect.seafileconnect

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.seafileconnect.ui.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertNotNull

/**
 * Automation tests for SeafileConnect UI and workflows
 */
@RunWith(AndroidJUnit4::class)
class SeafileConnectAutomationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testApplicationLaunchesSuccessfully() {
        // Verify app launches and main UI is displayed
        composeTestRule.onNodeWithText("SeafileConnect").assertExists()
    }

    @Test
    fun testMainScreenDisplaysTitle() {
        // Verify main screen shows the correct title
        composeTestRule.onNodeWithText("SeafileConnect")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testMainScreenDisplaysSubtitle() {
        // Verify subtitle is displayed
        composeTestRule.onNodeWithText("Encrypted Cloud Storage")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testMainScreenDisplaysDescription() {
        // Verify description text is present
        composeTestRule.onNodeWithText(
            "Connect to your Seafile server to securely sync and share files across all ShareConnect apps.",
            substring = true
        ).assertExists()
    }

    @Test
    fun testApplicationContextIsValid() {
        // Verify application context
        assertNotNull(context)
        assertEquals("com.shareconnect.seafileconnect.debug", context.packageName)
    }

    @Test
    fun testActivityRecreation() {
        // Verify activity handles recreation properly
        composeTestRule.activityRule.scenario.recreate()

        // UI should still be present after recreation
        composeTestRule.onNodeWithText("SeafileConnect")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testUIElementsAreAccessible() {
        // Verify all main UI elements are accessible
        composeTestRule.onNodeWithText("SeafileConnect").assertExists()
        composeTestRule.onNodeWithText("Encrypted Cloud Storage").assertExists()
        composeTestRule.onNodeWithText("Supports encrypted libraries", substring = true).assertExists()
    }

    @Test
    fun testApplicationSurvivesBackgroundTransition() {
        // Send app to background
        composeTestRule.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.CREATED)

        // Bring app back to foreground
        composeTestRule.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED)

        // Verify UI is still intact
        composeTestRule.onNodeWithText("SeafileConnect").assertExists()
    }

    @Test
    fun testIntentHandling() {
        // Create an intent
        val intent = Intent(context, MainActivity::class.java)
        intent.action = Intent.ACTION_MAIN

        // Launch activity with intent
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assertNotNull(activity)
            assertEquals(Intent.ACTION_MAIN, activity.intent.action)
        }
    }

    @Test
    fun testTextContentIsCorrect() {
        // Verify all expected text content
        val expectedTexts = listOf(
            "SeafileConnect",
            "Encrypted Cloud Storage",
            "Supports encrypted libraries"
        )

        expectedTexts.forEach { text ->
            composeTestRule.onNodeWithText(text, substring = true)
                .assertExists()
        }
    }

    @Test
    fun testApplicationStateAfterRotation() {
        // Get current activity
        var currentActivity: MainActivity? = null
        composeTestRule.activityRule.scenario.onActivity { activity ->
            currentActivity = activity
        }

        // Rotate device (simulate configuration change)
        composeTestRule.activityRule.scenario.recreate()

        // Verify UI is restored
        composeTestRule.onNodeWithText("SeafileConnect")
            .assertExists()
            .assertIsDisplayed()
    }

    private fun assertEquals(expected: String, actual: String) {
        kotlin.test.assertEquals(expected, actual)
    }
}
