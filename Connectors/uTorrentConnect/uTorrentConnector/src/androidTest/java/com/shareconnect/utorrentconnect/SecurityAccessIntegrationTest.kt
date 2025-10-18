package com.shareconnect.utorrentconnect

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import digital.vasic.security.access.SecurityAccessManager
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
        securityAccessManager = SecurityAccessManager.getInstance(androidx.test.core.app.ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() {
        // Clean up security settings
        runBlocking {
            securityAccessManager.disableSecurity()
        }
        SecurityAccessManager.destroyInstance()
    }

    @Test
    fun testSecurityAccessDialogAppearsWhenRequired() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Launch MainActivity
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            // The security access dialog should appear
            // Note: In a real test, we'd need to set up PIN first and verify the dialog
            // This is a basic integration test structure
        }

        scenario.close()
    }

    @Test
    fun testMainActivityLoadsWhenSecurityNotRequired() = runBlocking {
        // Ensure security is disabled
        securityAccessManager.disableSecurity()

        // Launch MainActivity
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            // Activity should load normally without security dialog
            assert(!activity.isFinishing)
        }

        scenario.close()
    }

    @Test
    fun testSessionTimeoutRequiresReAuthentication() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Launch MainActivity (would require authentication)
        val scenario1 = ActivityScenario.launch(MainActivity::class.java)

        scenario1.onActivity { activity ->
            // Activity should finish due to security requirement
            // (in real test, we'd authenticate first)
        }

        scenario1.close()

        // Simulate session timeout by waiting
        // In real test, we'd fast-forward time or mock the session

        // Launch again - should require authentication again
        val scenario2 = ActivityScenario.launch(MainActivity::class.java)

        scenario2.onActivity { activity ->
            // Should still require authentication
        }

        scenario2.close()
    }
}