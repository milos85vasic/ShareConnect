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


package com.shareconnect.plexconnect.data.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.plexconnect.data.api.PlexApiClient
import com.shareconnect.plexconnect.data.api.PlexApiService
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(AndroidJUnit4::class)
class PlexApiClientIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: PlexApiClient
    private lateinit var apiService: PlexApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(PlexApiService::class.java)
        apiClient = PlexApiClient()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getServerInfo_returnsServerInfo_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "MediaContainer": {
                        "friendlyName": "Test Server",
                        "machineIdentifier": "test-machine-123",
                        "version": "1.32.0.0"
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getServerInfo("http://test.com")

        // Then
        assertTrue(result.isSuccess)
        val serverInfo = result.getOrNull()!!
        assertEquals("Test Server", serverInfo.name)
        assertEquals("test-machine-123", serverInfo.machineIdentifier)
        assertEquals("1.32.0.0", serverInfo.version)
    }

    @Test
    fun getServerInfo_returnsFailure_onNetworkError() = runTest {
        // Given
        mockWebServer.shutdown() // Simulate network error

        // When
        val result = apiClient.getServerInfo("http://invalid-url")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun requestPin_returnsPinResponse_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "id": 12345,
                    "code": "ABCD1234"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.requestPin("test-client-id")

        // Then
        assertTrue(result.isSuccess)
        val pinResponse = result.getOrNull()!!
        assertEquals(12345, pinResponse.id)
        assertEquals("ABCD1234", pinResponse.code)
    }

    @Test
    fun checkPin_returnsAuthenticatedToken_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "authToken": "test-auth-token-123"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.checkPin(12345)

        // Then
        assertTrue(result.isSuccess)
        val authResponse = result.getOrNull()!!
        assertEquals("test-auth-token-123", authResponse.authToken)
    }
}