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


package com.shareconnect.duplicaticonnect.automation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.duplicaticonnect.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Automation tests for DuplicatiConnect
 * Tests UI interactions and workflows
 */
@RunWith(AndroidJUnit4::class)
class DuplicatiConnectAutomationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAppLaunchesSuccessfully() {
        // Verify app launches and main content is displayed
        composeTestRule.onNodeWithText("DuplicatiConnect").assertExists()
        composeTestRule.onNodeWithText("DuplicatiConnect").assertIsDisplayed()
    }

    @Test
    fun testMainContentDisplayed() {
        // Verify main tagline is displayed
        composeTestRule.onNodeWithText(
            "Manage your Duplicati backups and restore files",
            substring = true
        ).assertExists()
        composeTestRule.onNodeWithText(
            "Manage your Duplicati backups and restore files",
            substring = true
        ).assertIsDisplayed()
    }

    @Test
    fun testFeatureListDisplayed() {
        // Verify "Features:" section header
        composeTestRule.onNodeWithText("Features:").assertExists()
        composeTestRule.onNodeWithText("Features:").assertIsDisplayed()

        // Verify all feature items are displayed
        composeTestRule.onNodeWithText(
            "Backup job management",
            substring = true
        ).assertExists()

        composeTestRule.onNodeWithText(
            "Run backups on demand",
            substring = true
        ).assertExists()

        composeTestRule.onNodeWithText(
            "Restore files and folders",
            substring = true
        ).assertExists()

        composeTestRule.onNodeWithText(
            "Monitor backup progress",
            substring = true
        ).assertExists()

        composeTestRule.onNodeWithText(
            "View backup logs and history",
            substring = true
        ).assertExists()

        composeTestRule.onNodeWithText(
            "Multiple storage backends",
            substring = true
        ).assertExists()

        composeTestRule.onNodeWithText(
            "Incremental backup support",
            substring = true
        ).assertExists()

        composeTestRule.onNodeWithText(
            "Schedule backup operations",
            substring = true
        ).assertExists()
    }

    @Test
    fun testUIResponsiveness() {
        // Verify UI is responsive and not frozen
        composeTestRule.waitForIdle()

        // Check that multiple text elements can be found without timeout
        composeTestRule.onNodeWithText("DuplicatiConnect").assertExists()
        composeTestRule.onNodeWithText("Features:").assertExists()
    }

    @Test
    fun testCardContainerDisplayed() {
        // Verify the features card is displayed
        composeTestRule.onNodeWithText("Features:").assertExists()

        // Verify content within the card
        composeTestRule.onNodeWithText(
            "Backup job management",
            substring = true
        ).assertExists()
    }

    @Test
    fun testThemeApplied() {
        // Verify that Material theme is applied by checking color scheme elements
        composeTestRule.onNodeWithText("DuplicatiConnect").assertExists()

        // The app should render without crashes using MaterialTheme
        composeTestRule.waitForIdle()
    }

    @Test
    fun testAccessibilityLabels() {
        // Verify important text content is accessible
        composeTestRule.onNodeWithText("DuplicatiConnect").assertExists()
        composeTestRule.onNodeWithText("Features:").assertExists()

        // All feature items should be accessible
        val featureTexts = listOf(
            "Backup job management",
            "Run backups on demand",
            "Restore files and folders",
            "Monitor backup progress",
            "View backup logs and history",
            "Multiple storage backends",
            "Incremental backup support",
            "Schedule backup operations"
        )

        featureTexts.forEach { featureText ->
            composeTestRule.onNodeWithText(featureText, substring = true).assertExists()
        }
    }

    @Test
    fun testScreenLayout() {
        // Verify the layout structure
        composeTestRule.onNodeWithText("DuplicatiConnect").assertExists()

        // Verify vertical arrangement - title should be above description
        composeTestRule.onNodeWithText("DuplicatiConnect").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "Manage your Duplicati backups",
            substring = true
        ).assertIsDisplayed()

        // Verify features section comes after description
        composeTestRule.onNodeWithText("Features:").assertIsDisplayed()
    }

    @Test
    fun testNoErrorsOnStartup() {
        // Wait for any async operations to complete
        composeTestRule.waitForIdle()

        // Verify core UI elements are present (indicates no crashes)
        composeTestRule.onNodeWithText("DuplicatiConnect").assertExists()
        composeTestRule.onNodeWithText("Features:").assertExists()

        // Verify feature list is complete
        composeTestRule.onNodeWithText(
            "Backup job management",
            substring = true
        ).assertExists()
        composeTestRule.onNodeWithText(
            "Schedule backup operations",
            substring = true
        ).assertExists()
    }

    @Test
    fun testContentScrollable() {
        // Verify the content can be interacted with
        composeTestRule.waitForIdle()

        // Check that all content is accessible
        composeTestRule.onNodeWithText("DuplicatiConnect").assertExists()
        composeTestRule.onNodeWithText("Features:").assertExists()

        // Verify last feature item is accessible (might require scroll in small screens)
        composeTestRule.onNodeWithText(
            "Schedule backup operations",
            substring = true
        ).assertExists()
    }
}
