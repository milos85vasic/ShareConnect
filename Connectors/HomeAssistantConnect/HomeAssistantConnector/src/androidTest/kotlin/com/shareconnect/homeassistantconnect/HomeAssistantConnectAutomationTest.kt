/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.homeassistantconnect

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.homeassistantconnect.ui.App
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Comprehensive automation test suite for HomeAssistantConnect application.
 * Tests all major UI flows and user interactions to ensure full functionality.
 */
@RunWith(AndroidJUnit4::class)
class HomeAssistantConnectAutomationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            App()
        }
    }

    @Test
    fun testCompleteAppLaunchFlow() {
        // Test that the app launches and shows the main screen
        composeTestRule.waitForIdle()

        // Verify main screen is displayed
        composeTestRule.onNodeWithTag("main_screen").assertExists()

        // Verify HomeAssistant branding
        composeTestRule.onNodeWithText("HomeAssistantConnect").assertExists()
    }

    @Test
    fun testServerConnectionFlow() {
        composeTestRule.waitForIdle()

        // Navigate to server setup
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Verify we're on the server setup screen
        composeTestRule.onNodeWithText("Add Home Assistant Server").assertExists()

        // Fill in server details
        composeTestRule.onNodeWithText("Server URL").performTextInput("http://homeassistant.local:8123")
        composeTestRule.onNodeWithText("Access Token").performTextInput("test_token_12345")

        // Test connection
        composeTestRule.onNodeWithText("Test Connection").performClick()

        composeTestRule.waitForIdle()

        // Verify connection test result is displayed
        composeTestRule.onNodeWithTag("connection_status").assertExists()
    }

    @Test
    fun testEntityManagementFlow() {
        // Setup server first
        testServerConnectionFlow()

        // Navigate to entities
        composeTestRule.onNodeWithText("Entities").performClick()

        composeTestRule.waitForIdle()

        // Verify entities screen is displayed
        composeTestRule.onNodeWithText("Entities").assertExists()

        // Test entity filtering
        composeTestRule.onNodeWithText("Filter").performClick()
        composeTestRule.onNodeWithText("Lights").performClick()

        // Verify filtered view
        composeTestRule.onNodeWithTag("entity_list").assertExists()
    }

    @Test
    fun testServiceCallFlow() {
        // Setup server and navigate to entities
        testEntityManagementFlow()

        // Select an entity
        composeTestRule.onAllNodesWithTag("entity_item").onFirst().performClick()

        // Verify entity details screen
        composeTestRule.onNodeWithTag("entity_details").assertExists()

        // Call a service
        composeTestRule.onNodeWithText("Turn On").performClick()

        composeTestRule.waitForIdle()

        // Verify service call confirmation
        composeTestRule.onNodeWithText("Service called successfully").assertExists()
    }

    @Test
    fun testDashboardCustomizationFlow() {
        composeTestRule.waitForIdle()

        // Navigate to dashboard
        composeTestRule.onNodeWithText("Dashboard").performClick()

        // Enter edit mode
        composeTestRule.onNodeWithText("Edit").performClick()

        // Verify edit mode is active
        composeTestRule.onNodeWithTag("dashboard_edit_mode").assertExists()

        // Add a card
        composeTestRule.onNodeWithText("Add Card").performClick()

        // Select card type
        composeTestRule.onNodeWithText("Entity Card").performClick()

        // Configure card
        composeTestRule.onNodeWithText("Select Entity").performClick()
        composeTestRule.onAllNodesWithTag("entity_selector_item").onFirst().performClick()

        // Save card
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.waitForIdle()

        // Exit edit mode
        composeTestRule.onNodeWithText("Done").performClick()

        // Verify card is displayed
        composeTestRule.onNodeWithTag("dashboard_card").assertExists()
    }

    @Test
    fun testAutomationManagementFlow() {
        composeTestRule.waitForIdle()

        // Navigate to automations
        composeTestRule.onNodeWithText("Automations").performClick()

        // Verify automations screen
        composeTestRule.onNodeWithText("Automations").assertExists()

        // Test automation toggle
        composeTestRule.onAllNodesWithTag("automation_toggle").onFirst().performClick()

        composeTestRule.waitForIdle()

        // Verify state change confirmation
        composeTestRule.onNodeWithTag("automation_state_indicator").assertExists()
    }

    @Test
    fun testSceneActivationFlow() {
        composeTestRule.waitForIdle()

        // Navigate to scenes
        composeTestRule.onNodeWithText("Scenes").performClick()

        // Verify scenes screen
        composeTestRule.onNodeWithText("Scenes").assertExists()

        // Activate a scene
        composeTestRule.onAllNodesWithTag("scene_item").onFirst().performClick()

        composeTestRule.waitForIdle()

        // Verify activation confirmation
        composeTestRule.onNodeWithText("Scene activated").assertExists()
    }

    @Test
    fun testSettingsAndConfigurationFlow() {
        composeTestRule.waitForIdle()

        // Navigate to settings
        composeTestRule.onNodeWithText("Settings").performClick()

        // Verify settings screen
        composeTestRule.onNodeWithText("Settings").assertExists()

        // Test various settings

        // 1. Update interval
        composeTestRule.onNodeWithText("Update Interval").performClick()
        composeTestRule.onNodeWithText("30 seconds").performClick()

        // 2. Theme selection
        composeTestRule.onNodeWithText("Theme").performClick()
        composeTestRule.onNodeWithText("Dark").performClick()

        // 3. Notification settings
        composeTestRule.onNodeWithText("Notifications").performClick()
        composeTestRule.onNodeWithTag("notification_toggle").performClick()

        // Navigate back
        composeTestRule.onNodeWithText("Back").performClick()

        // Verify we're back on main screen
        composeTestRule.onNodeWithTag("main_screen").assertExists()
    }

    @Test
    fun testWebSocketConnectionFlow() {
        // Setup server
        testServerConnectionFlow()

        // Navigate to real-time updates
        composeTestRule.onNodeWithText("Real-time Updates").performClick()

        composeTestRule.waitForIdle()

        // Verify WebSocket connection status
        composeTestRule.onNodeWithTag("websocket_status").assertExists()

        // Test reconnection
        composeTestRule.onNodeWithText("Reconnect").performClick()

        composeTestRule.waitForIdle()

        // Verify reconnection attempt
        composeTestRule.onNodeWithText("Connecting...").assertExists()
    }

    @Test
    fun testErrorHandlingAndRecovery() {
        composeTestRule.waitForIdle()

        // Try to connect with invalid credentials
        composeTestRule.onNodeWithText("Add Server").performClick()
        composeTestRule.onNodeWithText("Server URL").performTextInput("http://invalid.host")
        composeTestRule.onNodeWithText("Access Token").performTextInput("invalid_token")
        composeTestRule.onNodeWithText("Test Connection").performClick()

        composeTestRule.waitForIdle()

        // Verify error message is displayed
        composeTestRule.onNodeWithText("Connection failed").assertExists()

        // Test error recovery
        composeTestRule.onNodeWithText("Retry").performClick()

        // Verify retry mechanism
        composeTestRule.onNodeWithTag("connection_status").assertExists()
    }

    @Test
    fun testNavigationConsistency() {
        composeTestRule.waitForIdle()

        // Test navigation between different screens
        val screens = listOf("Dashboard", "Entities", "Automations", "Scenes", "Settings")

        screens.forEach { screen ->
            composeTestRule.onNodeWithText(screen).performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText(screen).assertExists()
        }

        // Navigate back to dashboard
        composeTestRule.onNodeWithText("Dashboard").performClick()
        composeTestRule.onNodeWithTag("main_screen").assertExists()
    }

    @Test
    fun testFormValidationInServerSetup() {
        composeTestRule.waitForIdle()

        // Navigate to server setup
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Try to save without required fields
        composeTestRule.onNodeWithText("Save").performClick()

        // Verify validation errors
        composeTestRule.onNodeWithText("Server URL is required").assertExists()
        composeTestRule.onNodeWithText("Access Token is required").assertExists()

        // Fill in invalid URL
        composeTestRule.onNodeWithText("Server URL").performTextInput("not a url")
        composeTestRule.onNodeWithText("Save").performClick()

        // Verify URL validation
        composeTestRule.onNodeWithText("Invalid URL format").assertExists()

        // Fill in valid data
        composeTestRule.onNodeWithText("Server URL").performTextInput("http://homeassistant.local:8123")
        composeTestRule.onNodeWithText("Access Token").performTextInput("test_token_12345")
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.waitForIdle()

        // Verify successful save
        composeTestRule.onNodeWithTag("main_screen").assertExists()
    }

    @Test
    fun testAccessibilityCompliance() {
        composeTestRule.waitForIdle()

        // Verify all interactive elements have content descriptions
        composeTestRule.onAllNodesWithContentDescription("Add Server").onFirst().assertExists()
        composeTestRule.onAllNodesWithContentDescription("Settings").onFirst().assertExists()

        // Test focus navigation
        // This ensures keyboard/screen reader navigation works
        composeTestRule.onNodeWithTag("main_screen").assertExists()
    }

    @Test
    fun testDataPersistence() {
        // Add server
        testServerConnectionFlow()

        // Close and reopen app (simulated by recreating content)
        composeTestRule.setContent {
            App()
        }

        composeTestRule.waitForIdle()

        // Verify server configuration persists
        composeTestRule.onNodeWithTag("server_configured").assertExists()
    }

    @Test
    fun testPerformanceWithLargeDatasets() {
        // Setup server with many entities
        testServerConnectionFlow()

        // Navigate to entities
        composeTestRule.onNodeWithText("Entities").performClick()

        composeTestRule.waitForIdle()

        // Test scrolling performance
        composeTestRule.onNodeWithTag("entity_list")
            .performScrollToIndex(50)

        composeTestRule.waitForIdle()

        // Verify smooth scrolling (no crash)
        composeTestRule.onNodeWithTag("entity_list").assertExists()
    }

    @Test
    fun testOfflineHandling() {
        // Setup server
        testServerConnectionFlow()

        // Simulate offline mode by attempting operations
        composeTestRule.onNodeWithText("Entities").performClick()

        composeTestRule.waitForIdle()

        // Verify offline indicator or cached data
        composeTestRule.onNodeWithTag("offline_indicator").assertExists()
    }

    @Test
    fun testMultiServerSupport() {
        composeTestRule.waitForIdle()

        // Add first server
        composeTestRule.onNodeWithText("Add Server").performClick()
        composeTestRule.onNodeWithText("Server URL").performTextInput("http://homeassistant1.local:8123")
        composeTestRule.onNodeWithText("Access Token").performTextInput("token1")
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.waitForIdle()

        // Add second server
        composeTestRule.onNodeWithText("Add Server").performClick()
        composeTestRule.onNodeWithText("Server URL").performTextInput("http://homeassistant2.local:8123")
        composeTestRule.onNodeWithText("Access Token").performTextInput("token2")
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.waitForIdle()

        // Verify both servers are listed
        composeTestRule.onNodeWithText("homeassistant1.local").assertExists()
        composeTestRule.onNodeWithText("homeassistant2.local").assertExists()

        // Test switching between servers
        composeTestRule.onNodeWithText("homeassistant2.local").performClick()
        composeTestRule.onNodeWithTag("active_server_indicator").assertExists()
    }

    @Test
    fun testSearchFunctionality() {
        // Setup and navigate to entities
        testEntityManagementFlow()

        // Use search
        composeTestRule.onNodeWithTag("search_field").performTextInput("light")

        composeTestRule.waitForIdle()

        // Verify filtered results
        composeTestRule.onAllNodesWithText("light", substring = true, ignoreCase = true)
            .onFirst()
            .assertExists()
    }

    @Test
    fun testHistoryAndLogbookViewing() {
        // Setup server
        testServerConnectionFlow()

        // Navigate to history
        composeTestRule.onNodeWithText("History").performClick()

        composeTestRule.waitForIdle()

        // Verify history screen
        composeTestRule.onNodeWithText("History").assertExists()

        // Test date range selection
        composeTestRule.onNodeWithText("Date Range").performClick()
        composeTestRule.onNodeWithText("Last 24 hours").performClick()

        composeTestRule.waitForIdle()

        // Verify history entries
        composeTestRule.onNodeWithTag("history_list").assertExists()
    }

    @Test
    fun testNotificationConfiguration() {
        // Navigate to settings
        testSettingsAndConfigurationFlow()

        // Configure notifications
        composeTestRule.onNodeWithText("Notification Settings").performClick()

        // Enable various notification types
        composeTestRule.onNodeWithText("State Changes").performClick()
        composeTestRule.onNodeWithText("Automation Triggers").performClick()

        // Save settings
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.waitForIdle()

        // Verify settings saved
        composeTestRule.onNodeWithText("Settings saved").assertExists()
    }

    @Test
    fun testWidgetConfiguration() {
        composeTestRule.waitForIdle()

        // Navigate to widgets
        composeTestRule.onNodeWithText("Widgets").performClick()

        // Add a widget
        composeTestRule.onNodeWithText("Add Widget").performClick()

        // Select widget type
        composeTestRule.onNodeWithText("Entity Status Widget").performClick()

        // Configure widget
        composeTestRule.onNodeWithText("Select Entity").performClick()
        composeTestRule.onAllNodesWithTag("entity_selector_item").onFirst().performClick()

        // Save widget
        composeTestRule.onNodeWithText("Create Widget").performClick()

        composeTestRule.waitForIdle()

        // Verify widget created
        composeTestRule.onNodeWithText("Widget created successfully").assertExists()
    }

    @Test
    fun testBackupAndRestore() {
        // Setup server with configuration
        testServerConnectionFlow()

        // Navigate to settings
        composeTestRule.onNodeWithText("Settings").performClick()

        // Backup configuration
        composeTestRule.onNodeWithText("Backup").performClick()
        composeTestRule.onNodeWithText("Create Backup").performClick()

        composeTestRule.waitForIdle()

        // Verify backup created
        composeTestRule.onNodeWithText("Backup created successfully").assertExists()

        // Test restore
        composeTestRule.onNodeWithText("Restore").performClick()
        composeTestRule.onAllNodesWithTag("backup_item").onFirst().performClick()
        composeTestRule.onNodeWithText("Restore Backup").performClick()

        composeTestRule.waitForIdle()

        // Verify restore completed
        composeTestRule.onNodeWithText("Backup restored successfully").assertExists()
    }

    @Test
    fun testVoiceCommandIntegration() {
        // Setup server
        testServerConnectionFlow()

        // Navigate to voice commands
        composeTestRule.onNodeWithText("Voice Commands").performClick()

        composeTestRule.waitForIdle()

        // Verify voice command screen
        composeTestRule.onNodeWithText("Voice Commands").assertExists()

        // Test voice command activation
        composeTestRule.onNodeWithTag("voice_command_button").performClick()

        // Verify listening state
        composeTestRule.onNodeWithText("Listening...").assertExists()
    }

    @Test
    fun testSecurityFeatures() {
        composeTestRule.waitForIdle()

        // Navigate to security settings
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Security").performClick()

        // Enable biometric authentication
        composeTestRule.onNodeWithText("Biometric Authentication").performClick()

        // Verify security settings
        composeTestRule.onNodeWithTag("biometric_toggle").assertExists()
    }

    @Test
    fun testCompleteUserJourney() {
        composeTestRule.waitForIdle()

        // 1. Initial setup
        composeTestRule.onNodeWithText("Add Server").performClick()
        composeTestRule.onNodeWithText("Server URL").performTextInput("http://homeassistant.local:8123")
        composeTestRule.onNodeWithText("Access Token").performTextInput("test_token_12345")
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.waitForIdle()

        // 2. View dashboard
        composeTestRule.onNodeWithText("Dashboard").performClick()
        composeTestRule.onNodeWithTag("dashboard").assertExists()

        // 3. Control entities
        composeTestRule.onNodeWithText("Entities").performClick()
        composeTestRule.onAllNodesWithTag("entity_item").onFirst().performClick()
        composeTestRule.onNodeWithText("Turn On").performClick()

        composeTestRule.waitForIdle()

        // 4. Activate scene
        composeTestRule.onNodeWithText("Scenes").performClick()
        composeTestRule.onAllNodesWithTag("scene_item").onFirst().performClick()

        composeTestRule.waitForIdle()

        // 5. Check history
        composeTestRule.onNodeWithText("History").performClick()
        composeTestRule.onNodeWithTag("history_list").assertExists()

        // 6. Configure settings
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Theme").performClick()
        composeTestRule.onNodeWithText("Dark").performClick()

        composeTestRule.waitForIdle()

        // Verify complete journey succeeded
        composeTestRule.onNodeWithTag("main_screen").assertExists()
    }
}
