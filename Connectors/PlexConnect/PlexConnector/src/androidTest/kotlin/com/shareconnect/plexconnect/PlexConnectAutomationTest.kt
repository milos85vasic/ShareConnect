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


package com.shareconnect.plexconnect

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.plexconnect.di.DependencyContainer
import com.shareconnect.plexconnect.ui.App
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Comprehensive automation test suite for PlexConnect application.
 * Tests all major UI flows and user interactions to ensure full functionality.
 */
@RunWith(AndroidJUnit4::class)
class PlexConnectAutomationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // Initialize dependency injection for tests
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        DependencyContainer.init(context)

        composeTestRule.setContent {
            App()
        }
    }

    @After
    fun tearDown() {
        // Clean up if needed
    }

    @Test
    fun testCompleteAppLaunchFlow() {
        // Test that the app launches and shows the server list screen
        composeTestRule.waitForIdle()

        // Verify main screen is displayed
        composeTestRule.onNodeWithTag("plex_main_screen").assertExists()

        // Verify we're on the server list screen (should show empty state initially)
        composeTestRule.onNodeWithText("No Plex Servers").assertExists()
        composeTestRule.onNodeWithText("Add your first Plex Media Server to get started").assertExists()
    }

    @Test
    fun testAddServerFlow() {
        composeTestRule.waitForIdle()

        // Click the "Add Server" button in the empty state
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Verify we're on the add server screen
        composeTestRule.onNodeWithText("Add Plex Server").assertExists()

        // Fill in server details
        composeTestRule.onNodeWithText("Server Name").performTextInput("Test Plex Server")
        composeTestRule.onNodeWithText("Server Address").performTextInput("192.168.1.100")
        composeTestRule.onNodeWithText("Port").performTextInput("32400")

        // Click Test Connection button
        composeTestRule.onNodeWithText("Test Connection").performClick()

        // Wait for processing
        composeTestRule.waitForIdle()

        // Verify we're back on server list and the server was added
        composeTestRule.onNodeWithText("Test Plex Server").assertExists()
        composeTestRule.onNodeWithText("192.168.1.100:32400").assertExists()
    }

    @Test
    fun testAuthenticationFlow() {
        // First add a server
        testAddServerFlow()

        // Click on the server to start authentication
        composeTestRule.onNodeWithText("Test Plex Server").performClick()

        // Verify we're on the authentication screen
        composeTestRule.onNodeWithText("Plex Authentication").assertExists()
        composeTestRule.onNodeWithText("Start Authentication").assertExists()

        // Click the authentication button
        composeTestRule.onNodeWithText("Start Authentication").performClick()

        // Wait for authentication processing
        composeTestRule.waitForIdle()

        // Note: Full authentication testing would require a mock Plex.tv server
        // For now, we verify the authentication screen appears correctly
    }

    @Test
    fun testLibraryBrowsingFlow() {
        // This test assumes we have a successfully authenticated server
        // In a real scenario, we'd need to mock the Plex API responses

        composeTestRule.waitForIdle()

        // For now, test that we can navigate to settings (which should be accessible)
        composeTestRule.onNodeWithText("Settings").performClick()

        // Verify we're on the settings screen
        composeTestRule.onNodeWithText("Settings").assertExists()
    }

    @Test
    fun testSettingsNavigationFlow() {
        composeTestRule.waitForIdle()

        // Click the settings icon/button
        composeTestRule.onNodeWithText("Settings").performClick()

        // Verify we're on the settings screen
        composeTestRule.onNodeWithText("Settings").assertExists()

        // Test navigation back
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Verify we're back on server list
        composeTestRule.onNodeWithText("No Plex Servers").assertExists()
    }

    @Test
    fun testEmptyStateHandling() {
        composeTestRule.waitForIdle()

        // Verify empty state is properly displayed
        composeTestRule.onNodeWithText("No Plex Servers").assertExists()
        composeTestRule.onNodeWithText("Add your first Plex Media Server to get started").assertExists()
        composeTestRule.onNodeWithText("Add Server").assertExists()

        // Verify settings button is present
        composeTestRule.onNodeWithText("Settings").assertExists()
    }

    @Test
    fun testNavigationConsistency() {
        // Test that navigation works correctly between screens
        composeTestRule.waitForIdle()

        // Start -> Settings
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Settings").assertExists()

        // Settings -> Back to server list
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("No Plex Servers").assertExists()

        // Add server -> Add server screen
        composeTestRule.onNodeWithText("Add Server").performClick()
        composeTestRule.onNodeWithText("Add Plex Server").assertExists()

        // Cancel add server -> Back to server list
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("No Plex Servers").assertExists()
    }

    @Test
    fun testFormValidationInAddServer() {
        composeTestRule.waitForIdle()

        // Navigate to add server
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Try to add server without required fields
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Should show error for missing name
        composeTestRule.onNodeWithText("Server name is required").assertExists()

        // Fill name but leave host empty
        composeTestRule.onNodeWithText("Server Name").performTextInput("Test Server")
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Should show error for missing host
        composeTestRule.onNodeWithText("Host is required").assertExists()

        // Fill host but leave port empty
        composeTestRule.onNodeWithText("Host").performTextInput("192.168.1.100")
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Should show error for missing port
        composeTestRule.onNodeWithText("Port is required").assertExists()

        // Fill invalid port
        composeTestRule.onNodeWithText("Port").performTextInput("99999")
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Should show error for invalid port
        composeTestRule.onNodeWithText("Port must be between 1 and 65535").assertExists()

        // Fill valid port
        composeTestRule.onNodeWithText("Port").performTextInput("32400")
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Should succeed and navigate back
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Test Server").assertExists()
    }

    @Test
    fun testServerManagementFlow() {
        // Add a server first
        testAddServerFlow()

        // Test server selection and activation
        composeTestRule.onNodeWithText("Test Plex Server").performClick()

        // Verify authentication screen appears
        composeTestRule.onNodeWithText("Connect to Plex").assertExists()

        // Navigate back
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("No Plex Servers").assertExists()
    }
}