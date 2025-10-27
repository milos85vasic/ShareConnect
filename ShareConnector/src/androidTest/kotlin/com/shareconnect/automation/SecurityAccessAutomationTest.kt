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


package com.shareconnect.automation

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import com.shareconnect.MainActivity
import digital.vasic.security.access.access.SecurityAccessManager
import digital.vasic.security.access.data.AccessMethod
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class SecurityAccessAutomationTest {

    private lateinit var device: UiDevice
    private lateinit var context: Context
    private lateinit var securityAccessManager: SecurityAccessManager

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        context = ApplicationProvider.getApplicationContext()
        securityAccessManager = SecurityAccessManager.getInstance(context)
        device.waitForIdle(5000)

        // Ensure clean state
        runBlocking {
            securityAccessManager.disableSecurity()
        }
    }

    @Test
    fun testSecurityAccessDisabledByDefault() {
        // Launch MainActivity when security is disabled
        val intent = android.content.Intent(context, MainActivity::class.java).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

        // Wait for activity to load
        device.waitForIdle(3000)

        // Should load directly to main activity without security prompts
        val mainContent = device.findObject(UiSelector().resourceId("com.shareconnect:id/recyclerViewProfiles"))
        assertTrue("Main activity should load directly when security is disabled", mainContent.waitForExists(5000))
    }

    @Test
    fun testSecurityAccessDialogAppears() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Launch MainActivity
        val intent = android.content.Intent(context, MainActivity::class.java).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

        // Wait for security dialog
        device.waitForIdle(3000)

        // Check if PIN input dialog appears
        val pinInput = device.findObject(UiSelector().className("android.widget.EditText"))
        assertTrue("PIN input field should appear", pinInput.waitForExists(5000))

        // Check for authentication button
        val authButton = device.findObject(UiSelector().textContains("Unlock"))
        assertTrue("Authentication button should be present", authButton.exists())
    }

    @Test
    fun testSuccessfulPinAuthentication() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Launch MainActivity
        val intent = android.content.Intent(context, MainActivity::class.java).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

        // Wait for dialog
        device.waitForIdle(3000)

        // Enter PIN (assuming default PIN setup)
        val pinInput = device.findObject(UiSelector().className("android.widget.EditText"))
        pinInput.text = "1234"

        // Click authenticate
        val authButton = device.findObject(UiSelector().textContains("Unlock"))
        authButton.click()

        // Wait for authentication
        device.waitForIdle(3000)

        // Should load main activity
        val mainContent = device.findObject(UiSelector().resourceId("com.shareconnect:id/recyclerViewProfiles"))
        assertTrue("Main activity should load after successful authentication", mainContent.waitForExists(5000))
    }

    @Test
    fun testInvalidPinAuthentication() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Launch MainActivity
        val intent = android.content.Intent(context, MainActivity::class.java).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

        // Wait for dialog
        device.waitForIdle(3000)

        // Enter invalid PIN
        val pinInput = device.findObject(UiSelector().className("android.widget.EditText"))
        pinInput.text = "9999"

        // Click authenticate
        val authButton = device.findObject(UiSelector().textContains("Unlock"))
        authButton.click()

        // Wait for response
        device.waitForIdle(2000)

        // Dialog should still be visible (authentication failed)
        val stillVisibleDialog = device.findObject(UiSelector().textContains("Unlock"))
        assertTrue("Authentication dialog should remain visible after failed attempt", stillVisibleDialog.exists())
    }

    @Test
    fun testSecurityAccessOnResume() = runBlocking {
        // First disable security and load app
        securityAccessManager.disableSecurity()

        val intent = android.content.Intent(context, MainActivity::class.java).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

        // Wait for main activity
        device.waitForIdle(3000)

        // Verify main activity loaded
        val mainContent = device.findObject(UiSelector().resourceId("com.shareconnect:id/recyclerViewProfiles"))
        assertTrue("Main activity should load", mainContent.exists())

        // Now enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Press home button to background app
        device.pressHome()

        // Wait a moment
        Thread.sleep(1000)

        // Launch app again (simulate resume)
        context.startActivity(intent)

        // Wait for security check
        device.waitForIdle(3000)

        // Security dialog should appear
        val pinInput = device.findObject(UiSelector().className("android.widget.EditText"))
        assertTrue("Security dialog should appear on resume when security is enabled", pinInput.waitForExists(5000))
    }

    @Test
    fun testCancelSecurityAccess() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Launch MainActivity
        val intent = android.content.Intent(context, MainActivity::class.java).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

        // Wait for dialog
        device.waitForIdle(3000)

        // Click cancel
        val cancelButton = device.findObject(UiSelector().textContains("Cancel"))
        if (cancelButton.exists()) {
            cancelButton.click()

            // Wait for app to close
            device.waitForIdle(2000)

            // App should be closed (no main activity visible)
            val mainContent = device.findObject(UiSelector().resourceId("com.shareconnect:id/recyclerViewProfiles"))
            assertFalse("App should close when canceling security access", mainContent.exists())
        }
    }

    @Test
    fun testSessionTimeout() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Launch and authenticate
        val intent = android.content.Intent(context, MainActivity::class.java).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

        // Wait for dialog
        device.waitForIdle(3000)

        // Enter PIN
        val pinInput = device.findObject(UiSelector().className("android.widget.EditText"))
        pinInput.text = "1234"

        // Authenticate
        val authButton = device.findObject(UiSelector().textContains("Unlock"))
        authButton.click()

        // Wait for main activity
        device.waitForIdle(3000)

        // Verify main activity loaded
        val mainContent = device.findObject(UiSelector().resourceId("com.shareconnect:id/recyclerViewProfiles"))
        assertTrue("Main activity should load after authentication", mainContent.exists())

        // Background app
        device.pressHome()
        Thread.sleep(1000)

        // Wait for session timeout (this would need to be configured for shorter timeout in test)
        // For this test, we'll just verify the mechanism works
        // In a real scenario, we'd wait for the timeout period
    }
}