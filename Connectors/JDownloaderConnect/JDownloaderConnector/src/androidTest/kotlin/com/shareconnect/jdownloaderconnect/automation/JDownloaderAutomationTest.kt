package com.shareconnect.jdownloaderconnect.automation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.jdownloaderconnect.ui.onboarding.OnboardingScreen
import com.shareconnect.jdownloaderconnect.ui.viewmodels.OnboardingViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JDownloaderAutomationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onboardingScreen_shouldDisplayCorrectElements() {
        // Given
        val mockViewModel = mockk<OnboardingViewModel>(relaxed = true)

        // When
        composeTestRule.setContent {
            OnboardingScreen(mockViewModel)
        }

        // Then
        composeTestRule.onNodeWithText("JDownloaderConnect").assertExists()
        composeTestRule.onNodeWithText("Connect to your MyJDownloader account to manage downloads remotely").assertExists()
        composeTestRule.onNodeWithText("Email").assertExists()
        composeTestRule.onNodeWithText("Password").assertExists()
        composeTestRule.onNodeWithText("Connect Account").assertExists()
        composeTestRule.onNodeWithText("Skip for now").assertExists()
    }

    @Test
    fun onboardingScreen_shouldAllowEmailInput() {
        // Given
        val mockViewModel = mockk<OnboardingViewModel>(relaxed = true)
        val testEmail = "test@example.com"

        // When
        composeTestRule.setContent {
            OnboardingScreen(mockViewModel)
        }

        // Then
        composeTestRule.onNodeWithText("Email").performTextInput(testEmail)
        // Note: We can't easily verify the internal state without exposing it
        // This test verifies the UI interaction works without crashing
    }

    @Test
    fun onboardingScreen_shouldAllowPasswordInput() {
        // Given
        val mockViewModel = mockk<OnboardingViewModel>(relaxed = true)
        val testPassword = "testpassword123"

        // When
        composeTestRule.setContent {
            OnboardingScreen(mockViewModel)
        }

        // Then
        composeTestRule.onNodeWithText("Password").performTextInput(testPassword)
        // Note: We can't easily verify the internal state without exposing it
        // This test verifies the UI interaction works without crashing
    }

    @Test
    fun onboardingScreen_shouldAllowConnectButtonClick() {
        // Given
        val mockViewModel = mockk<OnboardingViewModel>(relaxed = true)

        // When
        composeTestRule.setContent {
            OnboardingScreen(mockViewModel)
        }

        // Then
        composeTestRule.onNodeWithText("Connect Account").performClick()
        // Note: This verifies the button is clickable without crashing
    }

    @Test
    fun onboardingScreen_shouldAllowSkipButtonClick() {
        // Given
        val mockViewModel = mockk<OnboardingViewModel>(relaxed = true)

        // When
        composeTestRule.setContent {
            OnboardingScreen(mockViewModel)
        }

        // Then
        composeTestRule.onNodeWithText("Skip for now").performClick()
        // Note: This verifies the button is clickable without crashing
    }

    @Test
    fun onboardingScreen_shouldShowErrorWhenFieldsEmpty() {
        // Given
        val mockViewModel = mockk<OnboardingViewModel>(relaxed = true)

        // When
        composeTestRule.setContent {
            OnboardingScreen(mockViewModel)
        }

        // Then - Click connect without entering credentials
        composeTestRule.onNodeWithText("Connect Account").performClick()
        
        // The error message should appear
        composeTestRule.onNodeWithText("Please enter both email and password").assertExists()
    }
}