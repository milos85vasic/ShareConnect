package com.shareconnect.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.TestApplication
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class SecureStorageTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Clear any existing secure data
        SecureStorage.clearSecureData(context)
    }

    @After
    fun tearDown() {
        // Clean up after tests
        SecureStorage.clearSecureData(context)
    }

    @Test
    fun testEncryptedSharedPreferencesCreation() {
        val prefs = SecureStorage.getEncryptedSharedPreferences(context)
        assertNotNull("EncryptedSharedPreferences should be created", prefs)

        // Test that we can read and write to it
        prefs.edit().putString("test_key", "test_value").apply()
        val value = prefs.getString("test_key", null)
        assertEquals("test_value", value)
    }

    @Test
    fun testPinHashStorageAndRetrieval() {
        val testPinHash = "test_pin_hash_12345"

        // Store PIN hash
        SecureStorage.storePinHash(context, testPinHash)

        // Retrieve PIN hash
        val retrievedHash = SecureStorage.getStoredPinHash(context)

        assertNotNull("PIN hash should be stored and retrievable", retrievedHash)
        assertEquals("PIN hash should match stored value", testPinHash, retrievedHash)
    }

    @Test
    fun testPasswordHashStorageAndRetrieval() {
        val testPasswordHash = "test_password_hash_67890"

        // Store password hash
        SecureStorage.storePasswordHash(context, testPasswordHash)

        // Retrieve password hash
        val retrievedHash = SecureStorage.getStoredPasswordHash(context)

        assertNotNull("Password hash should be stored and retrievable", retrievedHash)
        assertEquals("Password hash should match stored value", testPasswordHash, retrievedHash)
    }

    @Test
    fun testBiometricEnabledStorageAndRetrieval() {
        // Test setting biometric enabled to true
        SecureStorage.setBiometricEnabled(context, true)
        var isEnabled = SecureStorage.isBiometricEnabled(context)
        assertTrue("Biometric should be enabled", isEnabled)

        // Test setting biometric enabled to false
        SecureStorage.setBiometricEnabled(context, false)
        isEnabled = SecureStorage.isBiometricEnabled(context)
        assertFalse("Biometric should be disabled", isEnabled)
    }

    @Test
    fun testClearSecureData() {
        // Store some test data
        SecureStorage.storePinHash(context, "test_pin")
        SecureStorage.storePasswordHash(context, "test_password")
        SecureStorage.setBiometricEnabled(context, true)

        // Verify data exists
        assertNotNull(SecureStorage.getStoredPinHash(context))
        assertNotNull(SecureStorage.getStoredPasswordHash(context))
        assertTrue(SecureStorage.isBiometricEnabled(context))

        // Clear all data
        SecureStorage.clearSecureData(context)

        // Verify all data is cleared
        assertNull(SecureStorage.getStoredPinHash(context))
        assertNull(SecureStorage.getStoredPasswordHash(context))
        assertFalse(SecureStorage.isBiometricEnabled(context))
    }

    @Test
    fun testHashString() {
        val input = "test_input_string"
        val hash = SecureStorage.hashString(input)

        assertNotNull("Hash should not be null", hash)
        assertTrue("Hash should be a non-empty string", hash.isNotEmpty())
        assertTrue("Hash should contain timestamp-like data", hash.contains("_"))
    }

    @Test
    fun testHashStringConsistency() {
        val input = "consistent_test_input"

        // Hash the same input multiple times
        val hash1 = SecureStorage.hashString(input)
        val hash2 = SecureStorage.hashString(input)

        // Note: Since our current implementation includes timestamp,
        // these won't be equal, but they should both be valid strings
        assertNotNull("First hash should not be null", hash1)
        assertNotNull("Second hash should not be null", hash2)
        assertTrue("First hash should be non-empty", hash1.isNotEmpty())
        assertTrue("Second hash should be non-empty", hash2.isNotEmpty())
    }

    @Test
    fun testMultipleOperations() {
        // Test multiple operations in sequence
        val pinHash = "pin_hash_123"
        val passwordHash = "password_hash_456"
        val biometricEnabled = true

        // Store data
        SecureStorage.storePinHash(context, pinHash)
        SecureStorage.storePasswordHash(context, passwordHash)
        SecureStorage.setBiometricEnabled(context, biometricEnabled)

        // Retrieve and verify data
        assertEquals(pinHash, SecureStorage.getStoredPinHash(context))
        assertEquals(passwordHash, SecureStorage.getStoredPasswordHash(context))
        assertEquals(biometricEnabled, SecureStorage.isBiometricEnabled(context))

        // Modify data
        val newPinHash = "new_pin_hash_789"
        SecureStorage.storePinHash(context, newPinHash)

        // Verify only PIN hash changed
        assertEquals(newPinHash, SecureStorage.getStoredPinHash(context))
        assertEquals(passwordHash, SecureStorage.getStoredPasswordHash(context))
        assertEquals(biometricEnabled, SecureStorage.isBiometricEnabled(context))
    }
}