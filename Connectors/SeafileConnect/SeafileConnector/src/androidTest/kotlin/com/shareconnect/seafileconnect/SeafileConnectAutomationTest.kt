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


package com.shareconnect.seafileconnect

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.designsystem.theme.ShareConnectTheme
import com.shareconnect.seafileconnect.ui.FilesTab
import com.shareconnect.seafileconnect.ui.SeafileConnectScreen
import com.shareconnect.seafileconnect.ui.SettingsTab
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Automation/UI tests for SeafileConnect
 */
@RunWith(AndroidJUnit4::class)
class SeafileConnectAutomationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAppLaunches() {
        composeTestRule.setContent {
            ShareConnectTheme {
                SeafileConnectScreen()
            }
        }

        composeTestRule.onNodeWithText("SeafileConnect").assertExists()
    }

    @Test
    fun testNavigationTabs() {
        composeTestRule.setContent {
            ShareConnectTheme {
                SeafileConnectScreen()
            }
        }

        composeTestRule.onNodeWithText("Libraries").assertExists()
        composeTestRule.onNodeWithText("Files").assertExists()
        composeTestRule.onNodeWithText("Search").assertExists()
        composeTestRule.onNodeWithText("Settings").assertExists()
    }

    @Test
    fun testLibrariesTabContent() {
        composeTestRule.setContent {
            ShareConnectTheme {
                SeafileConnectScreen()
            }
        }

        composeTestRule.onNodeWithText("Libraries").performClick()
        composeTestRule.onNodeWithText("Libraries - Browse and manage Seafile libraries").assertExists()
    }

    @Test
    fun testFilesTabContent() {
        composeTestRule.setContent {
            ShareConnectTheme {
                SeafileConnectScreen()
            }
        }

        composeTestRule.onNodeWithText("Files").performClick()
        composeTestRule.onNodeWithText("Files - Browse files and directories").assertExists()
    }

    @Test
    fun testSearchTabContent() {
        composeTestRule.setContent {
            ShareConnectTheme {
                SeafileConnectScreen()
            }
        }

        composeTestRule.onNodeWithText("Search").performClick()
        composeTestRule.onNodeWithText("Search - Search across all libraries").assertExists()
    }

    @Test
    fun testSettingsTabContent() {
        composeTestRule.setContent {
            ShareConnectTheme {
                SeafileConnectScreen()
            }
        }

        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Settings - Configure SeafileConnect").assertExists()
    }

    @Test
    fun testTabSwitching() {
        composeTestRule.setContent {
            ShareConnectTheme {
                SeafileConnectScreen()
            }
        }

        // Switch to Files
        composeTestRule.onNodeWithText("Files").performClick()
        composeTestRule.waitForIdle()

        // Switch to Search
        composeTestRule.onNodeWithText("Search").performClick()
        composeTestRule.waitForIdle()

        // Switch back to Libraries
        composeTestRule.onNodeWithText("Libraries").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun testUIElements() {
        composeTestRule.setContent {
            ShareConnectTheme {
                SeafileConnectScreen()
            }
        }

        // Check top bar
        composeTestRule.onNodeWithText("SeafileConnect").assertIsDisplayed()

        // Check all navigation items exist
        composeTestRule.onAllNodesWithText("Libraries")[0].assertExists()
        composeTestRule.onAllNodesWithText("Files")[0].assertExists()
        composeTestRule.onAllNodesWithText("Search")[0].assertExists()
        composeTestRule.onAllNodesWithText("Settings")[0].assertExists()
    }

    @Test
    fun testFilesTabQRScanningDialog() {
        composeTestRule.setContent {
            ShareConnectTheme {
                FilesTab()
            }
        }

        // Click the floating action button to show add options
        composeTestRule.onNodeWithText("+").performClick()

        // Verify dialog appears
        composeTestRule.onNodeWithText("Add Files").assertIsDisplayed()
        composeTestRule.onNodeWithText("Scan QR Code").assertIsDisplayed()
    }

    @Test
    fun testSettingsTabQRScanning() {
        composeTestRule.setContent {
            ShareConnectTheme {
                SettingsTab()
            }
        }

        // Check QR scan button exists
        composeTestRule.onNodeWithText("Scan QR Code for Configuration").assertIsDisplayed()

        // Click the button
        composeTestRule.onNodeWithText("Scan QR Code for Configuration").performClick()

        // Verify QR dialog appears
        composeTestRule.onNodeWithText("Scan Configuration QR").assertIsDisplayed()
        composeTestRule.onNodeWithText("Scan a QR code containing Seafile server configuration or settings.").assertIsDisplayed()
    }
}
