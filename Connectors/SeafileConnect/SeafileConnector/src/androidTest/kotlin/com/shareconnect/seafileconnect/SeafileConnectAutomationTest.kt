package com.shareconnect.seafileconnect

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.designsystem.theme.ShareConnectTheme
import com.shareconnect.seafileconnect.ui.SeafileConnectScreen
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
}
