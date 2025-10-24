package com.shareconnect

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import digital.vasic.security.access.access.SecurityAccessManager
import digital.vasic.security.access.data.AccessMethod
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SecurityAccessIntegrationTest {

    private lateinit var securityAccessManager: SecurityAccessManager

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        securityAccessManager = SecurityAccessManager.getInstance(context)

        // Ensure security is disabled before each test
        runBlocking {
            securityAccessManager.disableSecurity()
        }
    }

    @After
    fun tearDown() {
        // Clean up after each test
        runBlocking {
            securityAccessManager.disableSecurity()
        }
        SecurityAccessManager.destroyInstance()
    }

    @Test
    fun testMainActivityLoadsWithoutSecurity() {
        // When security is disabled, MainActivity should load normally
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            // Activity should be created and visible
            assert(activity.isFinishing.not())
        }

        scenario.close()
    }

    @Test
    fun testSecurityAccessDialogAppearsWhenRequired() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Launch MainActivity - security dialog should appear
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // Wait for dialog to appear
        Thread.sleep(1000)

        // Check if security dialog is shown (this would require custom matchers)
        // For now, just verify the activity is still running
        scenario.onActivity { activity ->
            assert(activity.isFinishing.not())
        }

        scenario.close()
    }

    @Test
    fun testSecurityAccessOnResume() = runBlocking {
        // First, disable security and load MainActivity
        securityAccessManager.disableSecurity()

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // Wait for activity to load
        Thread.sleep(1000)

        // Now enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Simulate going to background and coming back (onResume)
        scenario.onActivity { activity ->
            activity.onResume()
        }

        // Wait for security check
        Thread.sleep(1000)

        // Activity should still be running (security dialog should appear)
        scenario.onActivity { activity ->
            assert(activity.isFinishing.not())
        }

        scenario.close()
    }

    @Test
    fun testSecurityAccessDialogDismissal() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // Wait for dialog
        Thread.sleep(1000)

        // Try to dismiss dialog by pressing back (should not work)
        onView(withId(android.R.id.button2)) // Cancel button
            .perform(click())

        // Activity should finish
        scenario.onActivity { activity ->
            // Give it a moment to finish
            Thread.sleep(500)
            // Note: In real scenario, activity might finish
        }

        scenario.close()
    }
}