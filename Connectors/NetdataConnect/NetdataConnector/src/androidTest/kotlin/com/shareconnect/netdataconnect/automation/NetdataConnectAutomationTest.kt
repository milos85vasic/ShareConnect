package com.shareconnect.netdataconnect.automation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.netdataconnect.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Automation tests for NetdataConnect
 * UI testing using Jetpack Compose Testing
 */
@RunWith(AndroidJUnit4::class)
class NetdataConnectAutomationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAppLaunches() {
        // Verify app launches successfully
        composeTestRule.waitForIdle()
    }

    @Test
    fun testMainScreenDisplaysTitle() {
        // Wait for SecurityAccess to complete (if no PIN is set, it shows content immediately)
        composeTestRule.waitForIdle()

        // Verify the main title is displayed
        composeTestRule.onNodeWithText("NetdataConnect")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testMainScreenDisplaysSubtitle() {
        composeTestRule.waitForIdle()

        // Verify the subtitle text is displayed
        composeTestRule.onNodeWithText("Monitor your Netdata servers in real-time")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testFeaturesCardIsDisplayed() {
        composeTestRule.waitForIdle()

        // Verify the features card exists
        composeTestRule.onNodeWithText("Features:")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testAllFeaturesAreDisplayed() {
        composeTestRule.waitForIdle()

        // Verify all feature items are displayed
        val features = listOf(
            "• Real-time system metrics monitoring",
            "• Comprehensive chart visualization",
            "• Alarm and alert tracking",
            "• Server health monitoring",
            "• Historical data analysis",
            "• Multi-server support"
        )

        features.forEach { feature ->
            composeTestRule.onNodeWithText(feature)
                .assertExists()
                .assertIsDisplayed()
        }
    }

    @Test
    fun testUIElementsAreAccessible() {
        composeTestRule.waitForIdle()

        // Verify main UI elements are present and accessible
        composeTestRule.onNodeWithText("NetdataConnect")
            .assertHasClickAction()
            .assertIsDisplayed()
    }

    @Test
    fun testSecurityAccessIntegration() {
        // This test verifies SecurityAccess doesn't crash the app
        // If no PIN is set, content should be visible immediately
        composeTestRule.waitForIdle()

        // Verify we can see the main content (SecurityAccess passed through)
        composeTestRule.onNodeWithText("NetdataConnect")
            .assertExists()
    }

    @Test
    fun testAppRespondsToUserInteraction() {
        composeTestRule.waitForIdle()

        // Verify the app is responsive
        // Try scrolling (if scrollable content exists)
        composeTestRule.onRoot().performTouchInput {
            swipeUp()
        }

        composeTestRule.waitForIdle()

        // Content should still be visible after interaction
        composeTestRule.onNodeWithText("NetdataConnect")
            .assertExists()
    }
}
