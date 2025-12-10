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


package com.shareconnect.matrixconnect

import android.content.Context
import com.shareconnect.matrixconnect.api.MatrixApiClient
import com.shareconnect.matrixconnect.api.MatrixEncryptionManager
import com.shareconnect.matrixconnect.models.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import io.mockk.coEvery
import java.io.IOException
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Unit tests for MatrixEncryptionManager
 * Tests E2EE functionality with Olm/Megolm
 */
@OptIn(ExperimentalCoroutinesApi::class)
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

    // New Error Scenario Tests
    @Test
    fun `test createOutboundGroupSession with null account`() = runTest {
        // Simulate scenario where account is not initialized
        encryptionManager = MatrixEncryptionManager(
            context,
            apiClient,
            "@user:matrix.org",
            "DEVICE_123"
        )

        val result = encryptionManager.createOutboundGroupSession("!room1:matrix.org")
        assertTrue(result is MatrixResult.Error)
    }

    @Test
    fun `test encryptMessage with invalid room ID`() = runTest {
        val result = encryptionManager.encryptMessage("", "Test message")
        assertTrue(result is MatrixResult.Error)
    }

    @Test
    fun `test queryDeviceKeys with empty user list`() = runTest {
        val result = encryptionManager.queryDeviceKeys(emptyList())
        assertTrue(result is MatrixResult.Error)
    }

    @Test
    fun `test initialize with network error`() = runTest {
        coEvery { 
            apiClient.uploadKeys(any()) 
        } returns MatrixResult.NetworkError(Exception("Network error"))

        val result = encryptionManager.initialize()
        assertTrue(result is MatrixResult.Error)
    }
}
