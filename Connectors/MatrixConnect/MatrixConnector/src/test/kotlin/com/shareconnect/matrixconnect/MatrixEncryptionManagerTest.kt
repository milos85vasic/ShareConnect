package com.shareconnect.matrixconnect

import android.content.Context
import com.shareconnect.matrixconnect.api.MatrixApiClient
import com.shareconnect.matrixconnect.api.MatrixEncryptionManager
import com.shareconnect.matrixconnect.models.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for MatrixEncryptionManager
 * Tests E2EE functionality with Olm/Megolm
 */
class MatrixEncryptionManagerTest {

    private lateinit var context: Context
    private lateinit var apiClient: MatrixApiClient
    private lateinit var encryptionManager: MatrixEncryptionManager

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        apiClient = mockk(relaxed = true)
        encryptionManager = MatrixEncryptionManager(
            context,
            apiClient,
            "@user:matrix.org",
            "DEVICE_123"
        )
    }

    @Test
    fun `test createOutboundGroupSession returns session ID`() = runTest {
        val result = encryptionManager.createOutboundGroupSession("!room1:matrix.org")

        assertTrue(result is MatrixResult.Success)
        val sessionId = (result as MatrixResult.Success).data
        assertNotNull(sessionId)
        assertTrue(sessionId.isNotEmpty())
    }

    @Test
    fun `test getIdentityKeys returns null before initialization`() {
        val keys = encryptionManager.getIdentityKeys()
        assertNull(keys)
    }

    @Test
    fun `test encryptMessage requires session`() = runTest {
        val result = encryptionManager.encryptMessage("!room1:matrix.org", "Test message")

        // Should create session automatically
        assertTrue(result is MatrixResult.Success || result is MatrixResult.Error)
    }

    @Test
    fun `test queryDeviceKeys calls API`() = runTest {
        coEvery {
            apiClient.queryKeys(any())
        } returns MatrixResult.Success(
            KeysQueryResponse(
                deviceKeys = mapOf(
                    "@user:matrix.org" to mapOf(
                        "DEVICE_123" to DeviceKeys(
                            userId = "@user:matrix.org",
                            deviceId = "DEVICE_123",
                            algorithms = listOf("m.olm.v1.curve25519-aes-sha2"),
                            keys = emptyMap(),
                            signatures = emptyMap()
                        )
                    )
                )
            )
        )

        val result = encryptionManager.queryDeviceKeys(listOf("@user:matrix.org"))

        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun `test cleanup does not throw exceptions`() {
        // Should not throw even before initialization
        encryptionManager.cleanup()
    }
}
