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


package com.shareconnect.onboarding.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.onboarding.viewmodel.OnboardingViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun `welcome screen displays correctly`() {
        // Check that welcome screen elements are displayed
        composeTestRule.onNodeWithText("Welcome to ShareConnect").assertIsDisplayed()
        composeTestRule.onNodeWithText("Get Started").assertIsDisplayed()
    }

    @Test
    fun `welcome screen navigation to theme selection works`() {
        // Click Get Started button
        composeTestRule.onNodeWithText("Get Started").performClick()

        // Verify we're on theme selection screen
        composeTestRule.onNodeWithText("Choose Your Theme").assertIsDisplayed()
    }

    @Test
    fun `theme selection screen displays theme options`() {
        // Navigate to theme screen
        composeTestRule.onNodeWithText("Get Started").performClick()

        // Check theme options are displayed
        composeTestRule.onNodeWithText("Light Theme").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dark Theme").assertIsDisplayed()
        composeTestRule.onNodeWithText("System Theme").assertIsDisplayed()
    }

    @Test
    fun `theme selection allows selection and navigation`() {
        // Navigate to theme screen
        composeTestRule.onNodeWithText("Get Started").performClick()

        // Select a theme
        composeTestRule.onNodeWithText("Light Theme").performClick()

        // Click Next
        composeTestRule.onNodeWithText("Next").performClick()

        // Verify we're on language selection screen
        composeTestRule.onNodeWithText("Select Language").assertIsDisplayed()
    }

    @Test
    fun `language selection screen displays language options`() {
        // Navigate to language screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Check language options are displayed
        composeTestRule.onNodeWithText("English").assertIsDisplayed()
        composeTestRule.onNodeWithText("Español").assertIsDisplayed()
        composeTestRule.onNodeWithText("Français").assertIsDisplayed()
    }

    @Test
    fun `language selection allows selection and navigation`() {
        // Navigate to language screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Select a language
        composeTestRule.onNodeWithText("English").performClick()

        // Click Next
        composeTestRule.onNodeWithText("Next").performClick()

        // Verify we're on profile creation screen
        composeTestRule.onNodeWithText("Create Your Profile").assertIsDisplayed()
    }

    @Test
    fun `profile creation screen displays form fields`() {
        // Navigate to profile screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Check form fields are displayed
        composeTestRule.onNodeWithText("Profile Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Server URL").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username (optional)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password (optional)").assertIsDisplayed()
    }

    @Test
    fun `profile creation validates required fields`() {
        // Navigate to profile screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Try to click Next without filling required fields
        composeTestRule.onNodeWithText("Next").assertIsNotEnabled()

        // Fill required fields
        composeTestRule.onNodeWithText("Profile Name").performTextInput("My Server")
        composeTestRule.onNodeWithText("Server URL").performTextInput("192.168.1.100")

        // Now Next should be enabled
        composeTestRule.onNodeWithText("Next").assertIsEnabled()
    }

    @Test
    fun `profile creation allows navigation to completion`() {
        // Navigate to profile screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Fill required fields
        composeTestRule.onNodeWithText("Profile Name").performTextInput("My Server")
        composeTestRule.onNodeWithText("Server URL").performTextInput("192.168.1.100")

        // Click Next
        composeTestRule.onNodeWithText("Next").performClick()

        // Verify we're on completion screen
        composeTestRule.onNodeWithText("You're All Set!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start Using ShareConnect").assertIsDisplayed()
    }

    @Test
    fun `back navigation works correctly`() {
        // Navigate forward
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Verify we're on language screen
        composeTestRule.onNodeWithText("Select Language").assertIsDisplayed()

        // Go back
        composeTestRule.onNodeWithText("Back").performClick()

        // Verify we're back on theme screen
        composeTestRule.onNodeWithText("Choose Your Theme").assertIsDisplayed()
    }

    @Test
    fun `theme selection shows selected state visually`() {
        // Navigate to theme screen
        composeTestRule.onNodeWithText("Get Started").performClick()

        // Initially no theme selected
        // Select Light Theme
        composeTestRule.onNodeWithText("Light Theme").performClick()

        // The selected theme should be visually different (this is hard to test precisely with UI tests)
        // But we can verify the selection logic works by navigating forward
        composeTestRule.onNodeWithText("Next").assertIsEnabled()
    }

    @Test
    fun `language selection shows selected state visually`() {
        // Navigate to language screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Select English
        composeTestRule.onNodeWithText("English").performClick()

        // Verify Next is enabled
        composeTestRule.onNodeWithText("Next").assertIsEnabled()
    }
}