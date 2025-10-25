package com.shareconnect.giteaconnect

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.giteaconnect.ui.MainActivity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Automation test suite for GiteaConnect application.
 * Tests basic app launch and functionality.
 */
@RunWith(AndroidJUnit4::class)
class GiteaConnectAutomationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testAppLaunches() {
        // Verify app context
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.shareconnect.giteaconnect.debug", appContext.packageName)

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
    fun testApiServiceInitialization() {
        activityRule.scenario.onActivity { activity ->
            // Verify that the activity context can be used for API service initialization
            val context = activity.applicationContext
            assertNotNull(context)

            // Verify Retrofit can be created (basic API setup test)
            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl("https://test.com/")
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(com.shareconnect.giteaconnect.data.api.GiteaApiService::class.java)
            assertNotNull(apiService)
        }
    }
}
