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
