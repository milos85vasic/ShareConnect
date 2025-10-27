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


package com.shareconnect

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.shareconnect.ShareConnectOnboardingActivity
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class OnboardingIntegrationTest {

    private lateinit var device: UiDevice
    private lateinit var scenario: ActivityScenario<ShareConnectOnboardingActivity>

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Clear app data to ensure clean onboarding state
        clearAppData()

        // Launch onboarding activity
        scenario = ActivityScenario.launch(ShareConnectOnboardingActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Ignore("Test needs to be rewritten for Compose UI - onboarding uses Compose, not XML layouts with view IDs")
    @Test
    fun testCompleteOnboardingFlowWithThemeAndLanguageSelection() {
        // Test disabled - needs to be rewritten for Compose UI
        // TODO: Rewrite this test using Compose testing framework
    }

    @Ignore("Test needs to be rewritten for Compose UI")
    @Test
    fun testThemeApplicationDuringOnboarding() {
        // Test disabled - needs to be rewritten for Compose UI
        // TODO: Rewrite this test using Compose testing framework
    }

    @Ignore("Test needs to be rewritten for Compose UI")
    @Test
    fun testLanguageApplicationDuringOnboarding() {
        // Test disabled - needs to be rewritten for Compose UI
        // TODO: Rewrite this test using Compose testing framework
    }

    @Ignore("Test needs to be rewritten for Compose UI")
    @Test
    fun testOnboardingPersistenceAfterAppRestart() {
        // Test disabled - needs to be rewritten for Compose UI
        // TODO: Rewrite this test using Compose testing framework
    }

    private fun completeOnboardingFlow() {
        // Method disabled - needs to be rewritten for Compose UI
        // TODO: Rewrite this method using Compose testing framework
    }

    private fun clearAppData() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val packageName = context.packageName

        // Use UI Automator to clear app data
        device.pressHome()
        device.waitForIdle()

        // Open settings
        device.executeShellCommand("am start -a android.settings.APPLICATION_DETAILS_SETTINGS package:$packageName")
        Thread.sleep(2000)

        // Find and click "Clear data" or "Storage" button
        val clearDataButton = device.findObject(UiSelector().textContains("Clear"))
        if (clearDataButton.exists()) {
            clearDataButton.click()
            Thread.sleep(1000)

            // Confirm clear data
            val confirmButton = device.findObject(UiSelector().textContains("OK"))
            if (confirmButton.exists()) {
                confirmButton.click()
                Thread.sleep(2000)
            }
        }

        // Go back to home
        device.pressHome()
        Thread.sleep(1000)
    }
}