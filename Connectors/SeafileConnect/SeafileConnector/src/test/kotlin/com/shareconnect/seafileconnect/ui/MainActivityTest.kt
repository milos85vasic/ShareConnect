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

package com.shareconnect.seafileconnect.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.seafileconnect.TestApplication
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun seafileConnectScreen_displaysCorrectTabs() {
        composeTestRule.setContent {
            SeafileConnectScreen()
        }

        composeTestRule.onNodeWithText("Libraries").assertIsDisplayed()
        composeTestRule.onNodeWithText("Files").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search").assertIsDisplayed()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun filesTab_displaysAddOptionsDialog() {
        composeTestRule.setContent {
            FilesTab()
        }

        // Click the floating action button
        composeTestRule.onNodeWithText("+").performClick()

        // Verify dialog appears
        composeTestRule.onNodeWithText("Add Files").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Files Manually").assertIsDisplayed()
        composeTestRule.onNodeWithText("Scan QR Code").assertIsDisplayed()
    }

    @Test
    fun settingsTab_displaysQRScanButton() {
        composeTestRule.setContent {
            SettingsTab()
        }

        composeTestRule.onNodeWithText("Scan QR Code for Configuration").assertIsDisplayed()
    }

    @Test
    fun settingsTab_showsQRDialog_whenButtonClicked() {
        composeTestRule.setContent {
            SettingsTab()
        }

        composeTestRule.onNodeWithText("Scan QR Code for Configuration").performClick()

        composeTestRule.onNodeWithText("Scan Configuration QR").assertIsDisplayed()
        composeTestRule.onNodeWithText("Scan a QR code containing Seafile server configuration or settings.").assertIsDisplayed()
    }
}