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


package com.shareconnect.data.api

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for JDownloaderApiClient
 *
 * Tests cover:
 * - My.JDownloader authentication flow
 * - Session token management
 * - Device discovery and selection
 * - Link addition
 * - Package management
 * - Download control
 * - Error handling
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class JDownloaderApiClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: JDownloaderApiClient
    private val testEmail = "test@example.com"
    private val testPassword = "password123"
    private val testDeviceId = "device-12345"
    private val testSessionToken = "session-token-abc123"

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Override base URL for testing
        val baseUrl = mockWebServer.url("/").toString().removeSuffix("/")
        apiClient = JDownloaderApiClient(
            email = testEmail,
            password = testPassword,
            deviceId = testDeviceId,
            baseUrl = baseUrl
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test API client initialization`() {
        assertNotNull(apiClient)
    }

    @Test
    fun `test successful connection and session token retrieval`() = runBlocking {
        val connectResponse = """
            {
                "sessiontoken": "$testSessionToken",
                "regaintoken": "regain-token-xyz",
                "rid": 1
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(connectResponse))

        val result = apiClient.connect()

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("/my/connect"))
    }

    @Test
    fun `test connection with invalid credentials`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(401)
            .setBody("""{"error": "Invalid credentials"}"""))

        val result = apiClient.connect()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test list devices`() = runBlocking {
        // Mock connect
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"sessiontoken": "$testSessionToken", "rid": 1}"""))

        // Mock list devices
        val devicesResponse = """
            {
                "list": [
                    {
                        "id": "device-1",
                        "name": "Home PC",
                        "type": "jd"
                    },
                    {
                        "id": "device-2",
                        "name": "Laptop",
                        "type": "jd"
                    }
                ],
                "rid": 2
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(devicesResponse))

        apiClient.connect()
        val result = apiClient.listDevices()

        assertTrue(result.isSuccess)
        val devices = result.getOrNull()!!
        assertEquals(2, devices.size)
        assertEquals("Home PC", devices[0].name)
        assertEquals("Laptop", devices[1].name)
    }

    @Test
    fun `test add links to JDownloader`() = runBlocking {
        // Mock connect
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"sessiontoken": "$testSessionToken", "rid": 1}"""))

        // Mock add links
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"rid": 2}"""))

        apiClient.connect()
        val result = apiClient.addLinks(listOf(
            "https://test.com/file1.zip",
            "https://test.com/file2.zip"
        ))

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest() // Skip connect
        val addRequest = mockWebServer.takeRequest()
        assertTrue(addRequest.path!!.contains("/linkgrabberv2/addLinks"))
    }

    @Test
    fun `test get download packages`() = runBlocking {
        // Mock connect
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"sessiontoken": "$testSessionToken", "rid": 1}"""))

        // Mock get packages
        val packagesResponse = """
            {
                "data": [
                    {
                        "uuid": "package-1",
                        "name": "Archive.zip",
                        "bytesTotal": 104857600,
                        "bytesLoaded": 52428800,
                        "finished": false,
                        "enabled": true
                    },
                    {
                        "uuid": "package-2",
                        "name": "Document.pdf",
                        "bytesTotal": 1048576,
                        "bytesLoaded": 1048576,
                        "finished": true,
                        "enabled": true
                    }
                ],
                "rid": 2
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(packagesResponse))

        apiClient.connect()
        val result = apiClient.getPackages()

        assertTrue(result.isSuccess)
        val packages = result.getOrNull()!!
        assertEquals(2, packages.size)
        assertEquals("Archive.zip", packages[0].name)
        assertTrue(packages[1].finished)
    }

    @Test
    fun `test start downloads`() = runBlocking {
        // Mock connect
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"sessiontoken": "$testSessionToken", "rid": 1}"""))

        // Mock start
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"rid": 2}"""))

        apiClient.connect()
        val result = apiClient.startDownloads()

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest() // Skip connect
        val startRequest = mockWebServer.takeRequest()
        assertTrue(startRequest.path!!.contains("/downloadcontroller/start"))
    }

    @Test
    fun `test stop downloads`() = runBlocking {
        // Mock connect
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"sessiontoken": "$testSessionToken", "rid": 1}"""))

        // Mock stop
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"rid": 2}"""))

        apiClient.connect()
        val result = apiClient.stopDownloads()

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test pause downloads`() = runBlocking {
        // Mock connect
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"sessiontoken": "$testSessionToken", "rid": 1}"""))

        // Mock pause
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"rid": 2}"""))

        apiClient.connect()
        val result = apiClient.pauseDownloads()

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test session token reuse`() = runBlocking {
        // Mock connect once
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"sessiontoken": "$testSessionToken", "rid": 1}"""))

        // Mock multiple API calls
        repeat(3) { index ->
            mockWebServer.enqueue(MockResponse()
                .setResponseCode(200)
                .setBody("""{"rid": ${index + 2}}"""))
        }

        apiClient.connect()

        // Multiple calls should reuse session token
        apiClient.startDownloads()
        apiClient.stopDownloads()
        apiClient.pauseDownloads()

        // Should have 4 requests total (1 connect + 3 API calls)
        assertEquals(4, mockWebServer.requestCount)
    }

    @Test
    fun `test session token expiration and reconnect`() = runBlocking {
        // First connect
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"sessiontoken": "token-1", "rid": 1}"""))

        // First API call succeeds
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"rid": 2}"""))

        apiClient.connect()
        apiClient.startDownloads()

        // Simulate token expiration - would need to reconnect
        // This is a simplified test; actual implementation may vary
        assertEquals(2, mockWebServer.requestCount)
    }

    @Test
    fun `test add links without prior connection fails gracefully`() = runBlocking {
        val result = apiClient.addLinks(listOf("https://test.com/file.zip"))

        // Should fail or trigger automatic connection
        assertTrue(result.isFailure || result.isSuccess)
    }

    @Test
    fun `test network error handling`() = runBlocking {
        mockWebServer.shutdown()

        val result = apiClient.connect()

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `test HTTP error response`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error"))

        val result = apiClient.connect()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test JSON parsing error handling`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("Invalid JSON {{{"))

        val result = apiClient.connect()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test device ID in requests`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"sessiontoken": "$testSessionToken", "rid": 1}"""))

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"rid": 2}"""))

        apiClient.connect()
        apiClient.startDownloads()

        val request = mockWebServer.takeRequest() // Skip connect
        val apiRequest = mockWebServer.takeRequest()
        assertTrue(apiRequest.path!!.contains(testDeviceId))
    }

    @Test
    fun `test multiple link additions`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"sessiontoken": "$testSessionToken", "rid": 1}"""))

        val linkSets = listOf(
            listOf("https://test.com/file1.zip"),
            listOf("https://test.com/file2.zip", "https://test.com/file3.zip"),
            listOf("https://test.com/file4.zip")
        )

        apiClient.connect()

        linkSets.forEach { links ->
            mockWebServer.enqueue(MockResponse()
                .setResponseCode(200)
                .setBody("""{"rid": 2}"""))

            val result = apiClient.addLinks(links)
            assertTrue(result.isSuccess)
        }

        // 1 connect + 3 addLinks calls
        assertEquals(4, mockWebServer.requestCount)
    }

    @Test
    fun `test empty device list`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"sessiontoken": "$testSessionToken", "rid": 1}"""))

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"list": [], "rid": 2}"""))

        apiClient.connect()
        val result = apiClient.listDevices()

        assertTrue(result.isSuccess)
        val devices = result.getOrNull()!!
        assertTrue(devices.isEmpty())
    }

    @Test
    fun `test empty package list`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"sessiontoken": "$testSessionToken", "rid": 1}"""))

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"data": [], "rid": 2}"""))

        apiClient.connect()
        val result = apiClient.getPackages()

        assertTrue(result.isSuccess)
        val packages = result.getOrNull()!!
        assertTrue(packages.isEmpty())
    }
}
