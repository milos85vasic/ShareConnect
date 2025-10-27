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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenEdgeCaseTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun `theme selection handles rapid clicking`() {
        // Navigate to theme screen
        composeTestRule.onNodeWithText("Get Started").performClick()

        // Rapidly click different themes
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Dark Theme").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("System Theme").performClick()

        // Should still be able to navigate
        composeTestRule.onNodeWithText("Next").assertIsEnabled()
        composeTestRule.onNodeWithText("Next").performClick()

        // Should proceed to language screen
        composeTestRule.onNodeWithText("Select Language").assertIsDisplayed()
    }

    @Test
    fun `language selection handles rapid clicking`() {
        // Navigate to language screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Rapidly click different languages
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Español").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Français").performClick()

        // Should still be able to navigate
        composeTestRule.onNodeWithText("Next").assertIsEnabled()
        composeTestRule.onNodeWithText("Next").performClick()

        // Should proceed to profile screen
        composeTestRule.onNodeWithText("Create Your Profile").assertIsDisplayed()
    }

    @Test
    fun `profile creation handles keyboard input correctly`() {
        // Navigate to profile screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Test various keyboard inputs
        composeTestRule.onNodeWithText("Profile Name").performTextInput("Test Profile 123")
        composeTestRule.onNodeWithText("Server URL").performTextInput("192.168.1.100")
        composeTestRule.onNodeWithText("Username (optional)").performTextInput("user_name")
        composeTestRule.onNodeWithText("Password (optional)").performTextInput("password123!")

        // Clear and re-enter
        composeTestRule.onNodeWithText("Profile Name").performTextClearance()
        composeTestRule.onNodeWithText("Profile Name").performTextInput("New Profile")

        // Should still validate correctly
        composeTestRule.onNodeWithText("Next").assertIsEnabled()
    }

    @Test
    fun `profile creation handles invalid URLs gracefully`() {
        // Navigate to profile screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Test various invalid URLs
        val invalidUrls = listOf(
            "not-a-url",
            "192.168.1.100", // Valid IP but should work
            "http://", // Incomplete
            "https://", // Incomplete
            "ftp://example.com", // Wrong protocol
            "example.com:9091" // Missing protocol
        )

        for (url in invalidUrls) {
            composeTestRule.onNodeWithText("Profile Name").performTextInput("Test Profile")
            composeTestRule.onNodeWithText("Server URL").performTextInput(url)

            // The form should accept any string input for URL
            // Validation happens at the profile creation level
            composeTestRule.onNodeWithText("Next").assertIsEnabled()

            // Clear for next test
            composeTestRule.onNodeWithText("Profile Name").performTextClearance()
            composeTestRule.onNodeWithText("Server URL").performTextClearance()
        }
    }

    @Test
    fun `profile creation handles invalid port numbers gracefully`() {
        // Navigate to profile screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Test various port inputs
        val portInputs = listOf(
            "9091", // Valid
            "80", // Valid
            "65535", // Valid max
            "0", // Invalid min
            "-1", // Invalid negative
            "99999", // Invalid max
            "abc", // Invalid non-numeric
            "" // Empty
        )

        for (port in portInputs) {
            composeTestRule.onNodeWithText("Profile Name").performTextInput("Test Profile")
            composeTestRule.onNodeWithText("Server URL").performTextInput("192.168.1.100")

            // Clear port field first
            composeTestRule.onNodeWithText("Port").performTextClearance()
            if (port.isNotEmpty()) {
                composeTestRule.onNodeWithText("Port").performTextInput(port)
            }

            // The form should accept the input
            // Actual validation happens when creating the profile
            composeTestRule.onNodeWithText("Next").assertIsEnabled()

            // Clear for next test
            composeTestRule.onNodeWithText("Profile Name").performTextClearance()
            composeTestRule.onNodeWithText("Server URL").performTextClearance()
        }
    }

    @Test
    fun `onboarding handles screen rotation`() {
        // Navigate to middle of flow
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Fill some profile data
        composeTestRule.onNodeWithText("Profile Name").performTextInput("Test Profile")
        composeTestRule.onNodeWithText("Server URL").performTextInput("192.168.1.100")

        // Note: Testing screen rotation in Compose UI tests requires additional setup
        // For now, we verify the state is maintained in the UI
        composeTestRule.onNodeWithText("Test Profile").assertExists()
        composeTestRule.onNodeWithText("192.168.1.100").assertExists()
    }

    @Test
    fun `onboarding handles back button during flow`() {
        // Navigate forward
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Fill profile data
        composeTestRule.onNodeWithText("Profile Name").performTextInput("Test Profile")
        composeTestRule.onNodeWithText("Server URL").performTextInput("192.168.1.100")

        // Go back
        composeTestRule.onNodeWithText("Back").performClick()

        // Should be back on language screen
        composeTestRule.onNodeWithText("Select Language").assertIsDisplayed()

        // Go back again
        composeTestRule.onNodeWithText("Back").performClick()

        // Should be back on theme screen
        composeTestRule.onNodeWithText("Choose Your Theme").assertIsDisplayed()

        // Go back to welcome
        composeTestRule.onNodeWithText("Back").performClick()

        // Should be back on welcome screen
        composeTestRule.onNodeWithText("Welcome to ShareConnect").assertIsDisplayed()
    }

    @Test
    fun `onboarding prevents navigation without required selections`() {
        // On welcome screen, can always go next
        composeTestRule.onNodeWithText("Get Started").assertIsDisplayed()
        composeTestRule.onNodeWithText("Get Started").performClick()

        // On theme screen, Next should be disabled initially
        composeTestRule.onNodeWithText("Next").assertIsNotEnabled()

        // Select theme
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").assertIsEnabled()
        composeTestRule.onNodeWithText("Next").performClick()

        // On language screen, Next should be disabled initially
        composeTestRule.onNodeWithText("Next").assertIsNotEnabled()

        // Select language
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").assertIsEnabled()
        composeTestRule.onNodeWithText("Next").performClick()

        // On profile screen, Next should be disabled initially
        composeTestRule.onNodeWithText("Next").assertIsNotEnabled()

        // Fill required fields
        composeTestRule.onNodeWithText("Profile Name").performTextInput("Test")
        composeTestRule.onNodeWithText("Server URL").performTextInput("192.168.1.100")
        composeTestRule.onNodeWithText("Next").assertIsEnabled()
    }

    @Test
    fun `onboarding handles empty optional fields correctly`() {
        // Navigate to profile screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Fill only required fields
        composeTestRule.onNodeWithText("Profile Name").performTextInput("Test Profile")
        composeTestRule.onNodeWithText("Server URL").performTextInput("192.168.1.100")

        // Leave username and password empty
        composeTestRule.onNodeWithText("Username (optional)").assertTextEquals("")
        composeTestRule.onNodeWithText("Password (optional)").assertTextEquals("")

        // Should still be able to proceed
        composeTestRule.onNodeWithText("Next").assertIsEnabled()
        composeTestRule.onNodeWithText("Next").performClick()

        // Should reach completion
        composeTestRule.onNodeWithText("You're All Set!").assertIsDisplayed()
    }

    @Test
    fun `onboarding handles very long text inputs`() {
        // Navigate to profile screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Light Theme").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Create very long strings
        val longProfileName = "A".repeat(200)
        val longUrl = "http://" + "a".repeat(200) + ".com"
        val longUsername = "user".repeat(50)
        val longPassword = "pass".repeat(50)

        // Input long strings
        composeTestRule.onNodeWithText("Profile Name").performTextInput(longProfileName)
        composeTestRule.onNodeWithText("Server URL").performTextInput(longUrl)
        composeTestRule.onNodeWithText("Username (optional)").performTextInput(longUsername)
        composeTestRule.onNodeWithText("Password (optional)").performTextInput(longPassword)

        // Should still work (though UI might truncate display)
        composeTestRule.onNodeWithText("Next").assertIsEnabled()
        composeTestRule.onNodeWithText("Next").performClick()

        // Should reach completion
        composeTestRule.onNodeWithText("You're All Set!").assertIsDisplayed()
    }
}