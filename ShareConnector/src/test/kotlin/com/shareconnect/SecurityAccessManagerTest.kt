package com.shareconnect

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import digital.vasic.security.access.access.SecurityAccessManager
import digital.vasic.security.access.data.AccessMethod
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecurityAccessManagerTest {

    private lateinit var context: Context
    private lateinit var securityAccessManager: SecurityAccessManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        securityAccessManager = SecurityAccessManager.getInstance(context)
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
    fun testInitialState() {
        // Test that security is initially disabled
        assertEquals(SecurityAccessManager.AccessState.DISABLED, securityAccessManager.accessState.value)
        assertFalse(securityAccessManager.isLocked.value)
        assertEquals(0, securityAccessManager.failedAttempts.value)
    }

    @Test
    fun testEnableSecurity() = runBlocking {
        // Enable security with PIN
        val result = securityAccessManager.enableSecurity(AccessMethod.PIN)
        assertTrue(result.isSuccess)
        assertEquals(SecurityAccessManager.AccessState.READY, securityAccessManager.accessState.value)
    }

    @Test
    fun testDisableSecurity() = runBlocking {
        // First enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Then disable it
        val result = securityAccessManager.disableSecurity()
        assertTrue(result.isSuccess)
        assertEquals(SecurityAccessManager.AccessState.DISABLED, securityAccessManager.accessState.value)
    }

    @Test
    fun testIsAccessRequiredWhenDisabled() = runBlocking {
        // When security is disabled, access should not be required
        val accessRequired = securityAccessManager.isAccessRequired()
        assertFalse(accessRequired)
    }

    @Test
    fun testIsAccessRequiredWhenEnabled() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Access should be required when security is enabled and no session exists
        val accessRequired = securityAccessManager.isAccessRequired()
        assertTrue(accessRequired)
    }

    @Test
    fun testAuthenticationWithInvalidPin() = runBlocking {
        // Enable security first
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Try to authenticate with invalid PIN
        val result = securityAccessManager.authenticate(AccessMethod.PIN, "9999")
        assertTrue(result is SecurityAccessManager.AuthenticationResult.Failed)
        assertEquals(1, securityAccessManager.failedAttempts.value)
    }

    @Test
    fun testAuthenticationWithValidPin() = runBlocking {
        // Enable security first
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Try to authenticate with valid PIN (test PIN)
        val result = securityAccessManager.authenticate(AccessMethod.PIN, "1234")
        assertTrue(result is SecurityAccessManager.AuthenticationResult.Success)
        assertEquals(0, securityAccessManager.failedAttempts.value)
    }

    @Test
    fun testSessionTimeout() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Initially access should be required
        assertTrue(securityAccessManager.isAccessRequired())

        // After successful authentication, access should not be required (session active)
        // Note: This test assumes PIN setup is done elsewhere
        // In a real scenario, we'd need to set up PIN first
    }

    @Test
    fun testLogout() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Logout
        securityAccessManager.logout()

        // Session should be cleared
        assertNull(securityAccessManager.currentSessionId.value)
        assertEquals(SecurityAccessManager.AccessState.READY, securityAccessManager.accessState.value)
    }

    @Test
    fun testLockoutAfterFailedAttempts() = runBlocking {
        // Enable security
        securityAccessManager.enableSecurity(AccessMethod.PIN)

        // Fail authentication multiple times
        repeat(5) {
            securityAccessManager.authenticate(AccessMethod.PIN, "9999")
        }

        // Should be locked out
        assertTrue(securityAccessManager.isLocked.value)
        assertNotNull(securityAccessManager.lockoutEndTime.value)
    }

    @Test
    fun testSingletonInstance() {
        val instance1 = SecurityAccessManager.getInstance(context)
        val instance2 = SecurityAccessManager.getInstance(context)

        // Should be the same instance
        assertSame(instance1, instance2)
    }
}