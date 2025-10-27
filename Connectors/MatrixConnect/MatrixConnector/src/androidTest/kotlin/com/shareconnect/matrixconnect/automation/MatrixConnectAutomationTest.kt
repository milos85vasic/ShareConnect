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


package com.shareconnect.matrixconnect.automation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.matrixconnect.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Automation tests for MatrixConnect UI
 * Tests complete user workflows and UI interactions
 */
@RunWith(AndroidJUnit4::class)
class MatrixConnectAutomationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testMainScreenLoads() {
        // Wait for main screen to load
        composeTestRule.waitForIdle()

        // Should show navigation bar
        composeTestRule.onNodeWithText("Rooms").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contacts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Discover").assertIsDisplayed()
    }

    @Test
    fun testNavigationBetweenTabs() {
        composeTestRule.waitForIdle()

        // Navigate to Contacts
        composeTestRule.onNodeWithText("Contacts").performClick()
        composeTestRule.onNodeWithText("No contacts yet").assertIsDisplayed()

        // Navigate to Discover
        composeTestRule.onNodeWithText("Discover").performClick()
        composeTestRule.onNodeWithText("Discover rooms").assertIsDisplayed()

        // Navigate back to Rooms
        composeTestRule.onNodeWithText("Rooms").performClick()
        composeTestRule.onNodeWithText("No rooms yet").assertIsDisplayed()
    }

    @Test
    fun testSettingsNavigation() {
        composeTestRule.waitForIdle()

        // Open settings
        composeTestRule.onNodeWithContentDescription("Settings").performClick()

        // Should show settings screen
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()

        // Navigate back
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Should return to main screen
        composeTestRule.onNodeWithText("Rooms").assertIsDisplayed()
    }

    @Test
    fun testRoomsScreenContent() {
        composeTestRule.waitForIdle()

        // Should show empty state
        composeTestRule.onNodeWithText("No rooms yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start a conversation or join a room").assertIsDisplayed()
    }

    @Test
    fun testContactsScreenContent() {
        composeTestRule.waitForIdle()

        // Navigate to Contacts
        composeTestRule.onNodeWithText("Contacts").performClick()

        // Should show empty state
        composeTestRule.onNodeWithText("No contacts yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add friends to start messaging").assertIsDisplayed()
    }

    @Test
    fun testDiscoverScreenContent() {
        composeTestRule.waitForIdle()

        // Navigate to Discover
        composeTestRule.onNodeWithText("Discover").performClick()

        // Should show discover content
        composeTestRule.onNodeWithText("Discover rooms").assertIsDisplayed()
        composeTestRule.onNodeWithText("Find public rooms to join").assertIsDisplayed()
    }

    @Test
    fun testNavigationBarSelection() {
        composeTestRule.waitForIdle()

        // Rooms tab should be selected by default
        composeTestRule.onNodeWithText("Rooms").assertIsSelected()

        // Select Contacts
        composeTestRule.onNodeWithText("Contacts").performClick()
        composeTestRule.onNodeWithText("Contacts").assertIsSelected()

        // Select Discover
        composeTestRule.onNodeWithText("Discover").performClick()
        composeTestRule.onNodeWithText("Discover").assertIsSelected()
    }

    @Test
    fun testTopBarPresence() {
        composeTestRule.waitForIdle()

        // Top bar should always be visible
        composeTestRule.onNodeWithText("MatrixConnect").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
    }

    @Test
    fun testBottomBarPresence() {
        composeTestRule.waitForIdle()

        // Bottom navigation should always be visible
        composeTestRule.onNodeWithText("Rooms").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contacts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Discover").assertIsDisplayed()
    }

    @Test
    fun testMultipleNavigationCycles() {
        composeTestRule.waitForIdle()

        // Cycle through all tabs multiple times
        repeat(3) {
            composeTestRule.onNodeWithText("Contacts").performClick()
            composeTestRule.waitForIdle()

            composeTestRule.onNodeWithText("Discover").performClick()
            composeTestRule.waitForIdle()

            composeTestRule.onNodeWithText("Rooms").performClick()
            composeTestRule.waitForIdle()
        }

        // Should still be functional
        composeTestRule.onNodeWithText("No rooms yet").assertIsDisplayed()
    }

    @Test
    fun testSettingsAndBackNavigation() {
        composeTestRule.waitForIdle()

        // Open and close settings multiple times
        repeat(3) {
            composeTestRule.onNodeWithContentDescription("Settings").performClick()
            composeTestRule.waitForIdle()

            composeTestRule.onNodeWithContentDescription("Back").performClick()
            composeTestRule.waitForIdle()
        }

        // Should still be on main screen
        composeTestRule.onNodeWithText("Rooms").assertIsDisplayed()
    }
}
