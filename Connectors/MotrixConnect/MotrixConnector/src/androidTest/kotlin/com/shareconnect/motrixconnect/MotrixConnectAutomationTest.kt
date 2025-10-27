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


package com.shareconnect.motrixconnect

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.motrixconnect.ui.MainActivity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Automation test suite for MotrixConnect application.
 * Tests basic app launch and functionality.
 */
@RunWith(AndroidJUnit4::class)
class MotrixConnectAutomationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testAppLaunches() {
        // Verify app context
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.shareconnect.motrixconnect.debug", appContext.packageName)

        // Verify activity launches
        activityRule.scenario.onActivity { activity ->
            assertNotNull(activity)
            assertFalse(activity.isDestroyed)
        }
    }

    @Test
    fun testAppConfiguration() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Verify app is configured correctly
        assertNotNull(appContext.applicationInfo)
        assertNotNull(appContext.packageManager)
    }

    @Test
    fun testActivityLifecycle() {
        activityRule.scenario.onActivity { activity ->
            // Verify activity is in resumed state
            assertNotNull(activity)
            assertFalse(activity.isFinishing)
        }

        // Recreate activity (simulating configuration change)
        activityRule.scenario.recreate()

        activityRule.scenario.onActivity { activity ->
            // Verify activity is still functional after recreation
            assertNotNull(activity)
            assertFalse(activity.isDestroyed)
        }
    }

    @Test
    fun testNetworkPermissions() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val packageManager = appContext.packageManager
        val packageInfo = packageManager.getPackageInfo(
            appContext.packageName,
            android.content.pm.PackageManager.GET_PERMISSIONS
        )

        // Verify required permissions are declared
        val permissions = packageInfo.requestedPermissions?.toList() ?: emptyList()
        assertTrue(
            "INTERNET permission required",
            permissions.contains(android.Manifest.permission.INTERNET)
        )
    }

    @Test
    fun testAppMemoryManagement() {
        activityRule.scenario.onActivity { activity ->
            // Test that activity handles low memory conditions
            activity.onLowMemory()

            // Verify activity is still functional
            assertNotNull(activity)
            assertFalse(activity.isDestroyed)
        }
    }

    @Test
    fun testApiClientInitialization() {
        activityRule.scenario.onActivity { activity ->
            // Verify that the activity context can be used for API client initialization
            val context = activity.applicationContext
            assertNotNull(context)

            // Create an API client to verify it can be instantiated
            val apiClient = com.shareconnect.motrixconnect.data.api.MotrixApiClient(
                "http://test.com",
                "test-secret"
            )
            assertNotNull(apiClient)
        }
    }
}
