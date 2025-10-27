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
