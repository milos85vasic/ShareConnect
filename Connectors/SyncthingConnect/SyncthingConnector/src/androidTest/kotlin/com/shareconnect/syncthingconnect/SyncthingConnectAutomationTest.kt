package com.shareconnect.syncthingconnect

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.designsystem.theme.ShareConnectTheme
import com.shareconnect.syncthingconnect.ui.SyncthingConnectScreen
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SyncthingConnectAutomationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAppLaunches() {
        composeTestRule.setContent {
            ShareConnectTheme { SyncthingConnectScreen() }
        }
        composeTestRule.onNodeWithText("SyncthingConnect").assertExists()
    }

    @Test
    fun testNavigationTabs() {
        composeTestRule.setContent {
            ShareConnectTheme { SyncthingConnectScreen() }
        }
        composeTestRule.onNodeWithText("Folders").assertExists()
        composeTestRule.onNodeWithText("Devices").assertExists()
        composeTestRule.onNodeWithText("Status").assertExists()
        composeTestRule.onNodeWithText("Settings").assertExists()
    }

    @Test
    fun testTabSwitching() {
        composeTestRule.setContent {
            ShareConnectTheme { SyncthingConnectScreen() }
        }
        composeTestRule.onNodeWithText("Devices").performClick()
        composeTestRule.onNodeWithText("Status").performClick()
        composeTestRule.onNodeWithText("Folders").performClick()
    }
}
