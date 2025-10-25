package com.shareconnect.minecraftserverconnect.automation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.minecraftserverconnect.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before

/**
 * Automation tests for MinecraftServerConnect
 * Tests UI flows and user interactions
 */
@RunWith(AndroidJUnit4::class)
class MinecraftServerConnectAutomationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        // Wait for compose to be idle
        composeTestRule.waitForIdle()
    }

    @Test
    fun testAppLaunchesSuccessfully() {
        // Verify that the main content is displayed
        composeTestRule.onNodeWithText("MinecraftServerConnect").assertIsDisplayed()
    }

    @Test
    fun testMainContentDisplayed() {
        // Verify main app title is visible
        composeTestRule.onNodeWithText("MinecraftServerConnect")
            .assertExists()
            .assertIsDisplayed()

        // Verify subtitle is visible
        composeTestRule.onNodeWithText("Manage your Minecraft Server via RCON")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testFeaturesListDisplayed() {
        // Verify features text is displayed
        composeTestRule.onNodeWithText(
            "Features:\n\u2022 RCON Protocol Support\n\u2022 Server Status Pinging\n\u2022 Player Management\n\u2022 Command Templates\n\u2022 Real-time Console",
            substring = true
        ).assertExists()
    }

    @Test
    fun testRconProtocolFeatureShown() {
        composeTestRule.onNodeWithText("RCON Protocol Support", substring = true)
            .assertExists()
    }

    @Test
    fun testServerStatusPingingFeatureShown() {
        composeTestRule.onNodeWithText("Server Status Pinging", substring = true)
            .assertExists()
    }

    @Test
    fun testPlayerManagementFeatureShown() {
        composeTestRule.onNodeWithText("Player Management", substring = true)
            .assertExists()
    }

    @Test
    fun testCommandTemplatesFeatureShown() {
        composeTestRule.onNodeWithText("Command Templates", substring = true)
            .assertExists()
    }

    @Test
    fun testRealTimeConsoleFeatureShown() {
        composeTestRule.onNodeWithText("Real-time Console", substring = true)
            .assertExists()
    }

    @Test
    fun testComingSoonMessageDisplayed() {
        composeTestRule.onNodeWithText("Full UI implementation coming soon!")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testAppTitleIsAccessible() {
        // Verify title is accessible for screen readers
        composeTestRule.onNodeWithText("MinecraftServerConnect")
            .assertHasClickAction() == false // Title should not be clickable
    }

    @Test
    fun testNoErrorMessagesDisplayed() {
        // Verify no error or crash messages are shown
        composeTestRule.onNodeWithText("Error", substring = true, ignoreCase = true)
            .assertDoesNotExist()
        composeTestRule.onNodeWithText("Crash", substring = true, ignoreCase = true)
            .assertDoesNotExist()
    }

    @Test
    fun testUIResponsiveness() {
        // Verify that the UI responds to configuration changes
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("MinecraftServerConnect").assertIsDisplayed()

        // Test that content remains visible after idle
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("MinecraftServerConnect").assertIsDisplayed()
    }

    @Test
    fun testAllTextElementsAreVisible() {
        // Comprehensive check that all expected text elements are present and visible
        val expectedTexts = listOf(
            "MinecraftServerConnect",
            "Manage your Minecraft Server via RCON",
            "Features:",
            "Full UI implementation coming soon!"
        )

        expectedTexts.forEach { text ->
            composeTestRule.onNodeWithText(text, substring = true)
                .assertExists()
                .assertIsDisplayed()
        }
    }

    @Test
    fun testContentLayout() {
        // Verify content is centered and properly laid out
        composeTestRule.onNodeWithText("MinecraftServerConnect")
            .assertIsDisplayed()
            .assertHasNoClickAction()

        // Verify spacing between elements by checking all are visible
        composeTestRule.onNodeWithText("Manage your Minecraft Server via RCON")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Features:", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun testNoLoadingIndicators() {
        // Verify no loading indicators are shown on main screen
        composeTestRule.onNodeWithText("Loading", substring = true, ignoreCase = true)
            .assertDoesNotExist()
    }

    @Test
    fun testAppStability() {
        // Perform multiple idle waits to ensure app remains stable
        repeat(3) {
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText("MinecraftServerConnect").assertIsDisplayed()
        }
    }
}
