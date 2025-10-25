package com.shareconnect.matrixconnect.integration

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.matrixconnect.api.MatrixApiClient
import com.shareconnect.matrixconnect.api.MatrixEncryptionManager
import com.shareconnect.matrixconnect.models.MatrixResult
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Integration tests for E2EE functionality
 * Tests Olm/Megolm encryption with real crypto operations
 */
@RunWith(AndroidJUnit4::class)
class MatrixE2EEIntegrationTest {

    private lateinit var context: Context
    private lateinit var apiClient: MatrixApiClient
    private lateinit var encryptionManager: MatrixEncryptionManager

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        apiClient = mockk(relaxed = true)
        encryptionManager = MatrixEncryptionManager(
            context,
            apiClient,
            "@user:matrix.org",
            "DEVICE_TEST"
        )
    }

    @Test
    fun testCreateOutboundGroupSession() = runTest {
        val result = encryptionManager.createOutboundGroupSession("!room1:matrix.org")

        assertTrue(result is MatrixResult.Success)
        val sessionId = (result as MatrixResult.Success).data
        assertNotNull(sessionId)
        assertTrue(sessionId.isNotEmpty())
        assertTrue(sessionId.length > 10)
    }

    @Test
    fun testMultipleGroupSessions() = runTest {
        val result1 = encryptionManager.createOutboundGroupSession("!room1:matrix.org")
        val result2 = encryptionManager.createOutboundGroupSession("!room2:matrix.org")

        assertTrue(result1 is MatrixResult.Success)
        assertTrue(result2 is MatrixResult.Success)

        val sessionId1 = (result1 as MatrixResult.Success).data
        val sessionId2 = (result2 as MatrixResult.Success).data

        assertNotEquals(sessionId1, sessionId2)
    }

    @Test
    fun testCleanupAfterCreation() = runTest {
        encryptionManager.createOutboundGroupSession("!room1:matrix.org")

        // Should not throw
        encryptionManager.cleanup()
    }

    @Test
    fun testQueryDeviceKeysEmptyList() = runTest {
        val result = encryptionManager.queryDeviceKeys(emptyList())
        // Should handle gracefully
        assertNotNull(result)
    }
}
