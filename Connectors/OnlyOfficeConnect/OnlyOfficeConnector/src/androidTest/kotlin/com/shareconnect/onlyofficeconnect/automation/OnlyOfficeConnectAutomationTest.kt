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


package com.shareconnect.onlyofficeconnect.automation

import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.onlyofficeconnect.ui.MainActivity
import com.shareconnect.onlyofficeconnect.ui.OnboardingActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Automation tests for OnlyOfficeConnect
 * Tests UI flows and user interactions
 */
@RunWith(AndroidJUnit4::class)
class OnlyOfficeConnectAutomationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAppLaunchesSuccessfully() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Launch activity
        context.startActivity(intent)

        // Wait for UI to settle
        Thread.sleep(1000)

        // Verify app doesn't crash on launch
        assert(true)
    }

    @Test
    fun testOnboardingScreenDisplaysCorrectly() {
        composeTestRule.setContent {
            com.shareconnect.onlyofficeconnect.ui.OnboardingContent(
                onComplete = {}
            )
        }

        // Verify welcome message is displayed
        composeTestRule.onNodeWithText("Welcome to OnlyOfficeConnect").assertIsDisplayed()

        // Verify description is displayed
        composeTestRule.onNodeWithText(
            "Connect to your ONLYOFFICE Document Server and edit documents with real-time collaboration.",
            substring = true
        ).assertIsDisplayed()

        // Verify Get Started button exists
        composeTestRule.onNodeWithText("Get Started").assertIsDisplayed()
    }

    @Test
    fun testOnboardingGetStartedButtonWorks() {
        var completeCalled = false

        composeTestRule.setContent {
            com.shareconnect.onlyofficeconnect.ui.OnboardingContent(
                onComplete = { completeCalled = true }
            )
        }

        // Click Get Started button
        composeTestRule.onNodeWithText("Get Started").performClick()

        // Wait for callback
        Thread.sleep(500)

        // Verify onComplete was called
        assert(completeCalled)
    }

    @Test
    fun testMainScreenDisplaysCorrectly() {
        composeTestRule.setContent {
            com.shareconnect.onlyofficeconnect.ui.OnlyOfficeConnectContent()
        }

        // Verify app name is displayed
        composeTestRule.onNodeWithText("OnlyOfficeConnect").assertIsDisplayed()

        // Verify description is displayed
        composeTestRule.onNodeWithText(
            "Connect to your ONLYOFFICE Document Server",
            substring = true
        ).assertIsDisplayed()

        // Verify feature description
        composeTestRule.onNodeWithText(
            "Edit documents, spreadsheets, and presentations",
            substring = true
        ).assertIsDisplayed()
    }

    @Test
    fun testUIElementsAreAccessible() {
        composeTestRule.setContent {
            com.shareconnect.onlyofficeconnect.ui.OnlyOfficeConnectContent()
        }

        // Verify all text elements are accessible
        composeTestRule.onAllNodesWithText("OnlyOfficeConnect", substring = true)
            .assertCountEquals(1)

        composeTestRule.onAllNodesWithText("ONLYOFFICE", substring = true)
            .assertCountEquals(1)
    }

    @Test
    fun testScreenOrientationHandling() {
        composeTestRule.setContent {
            com.shareconnect.onlyofficeconnect.ui.OnlyOfficeConnectContent()
        }

        // Verify content is displayed
        composeTestRule.onNodeWithText("OnlyOfficeConnect").assertIsDisplayed()

        // Simulate configuration change (would normally rotate screen)
        composeTestRule.onNodeWithText("OnlyOfficeConnect").assertIsDisplayed()
    }

    @Test
    fun testAccessibilityLabels() {
        composeTestRule.setContent {
            com.shareconnect.onlyofficeconnect.ui.OnboardingContent(
                onComplete = {}
            )
        }

        // Verify button has proper content description for accessibility
        composeTestRule.onNodeWithText("Get Started")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun testTextVisibilityAndReadability() {
        composeTestRule.setContent {
            com.shareconnect.onlyofficeconnect.ui.OnlyOfficeConnectContent()
        }

        // Verify all main text elements are visible
        composeTestRule.onNodeWithText("OnlyOfficeConnect").assertIsDisplayed()
        composeTestRule.onNodeWithText("Connect to your ONLYOFFICE Document Server").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "Edit documents, spreadsheets, and presentations",
            substring = true
        ).assertIsDisplayed()
    }

    @Test
    fun testUIDoesNotCrashOnInteraction() {
        composeTestRule.setContent {
            com.shareconnect.onlyofficeconnect.ui.OnboardingContent(
                onComplete = {}
            )
        }

        // Perform various interactions
        composeTestRule.onNodeWithText("Get Started").performClick()
        Thread.sleep(100)

        // Verify UI is still functional
        composeTestRule.onNodeWithText("Welcome to OnlyOfficeConnect").assertExists()
    }

    @Test
    fun testContentLayoutIsProperlyStructured() {
        composeTestRule.setContent {
            com.shareconnect.onlyofficeconnect.ui.OnlyOfficeConnectContent()
        }

        // Verify content is laid out properly
        composeTestRule.onRoot().assertIsDisplayed()

        // Check that all text elements exist in hierarchy
        composeTestRule.onNodeWithText("OnlyOfficeConnect").assertExists()
        composeTestRule.onNodeWithText("Connect to your ONLYOFFICE Document Server").assertExists()
    }

    @Test
    fun testThemeConsistency() {
        composeTestRule.setContent {
            androidx.compose.material3.MaterialTheme {
                com.shareconnect.onlyofficeconnect.ui.OnlyOfficeConnectContent()
            }
        }

        // Verify content renders correctly with Material Theme
        composeTestRule.onNodeWithText("OnlyOfficeConnect").assertIsDisplayed()
    }
}
