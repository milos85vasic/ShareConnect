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
 * Unit tests for MeTubeApiClient
 *
 * Tests cover:
 * - Adding downloads by URL
 * - Quality and format selection
 * - Authentication (Basic Auth)
 * - Error handling
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MeTubeApiClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: MeTubeApiClient
    private val testUsername = "user"
    private val testPassword = "pass"

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url("/").toString().removeSuffix("/")
        apiClient = MeTubeApiClient(baseUrl, testUsername, testPassword)
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
    fun `test add download with default quality`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"status": "ok"}"""))

        val result = apiClient.addDownload("https://www.youtube.com/watch?v=test")

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest()
        assertEquals("/add", request.path)
        assertEquals("POST", request.method)
    }

    @Test
    fun `test add download with specific quality`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"status": "ok"}"""))

        val result = apiClient.addDownload(
            url = "https://www.youtube.com/watch?v=test",
            quality = "1080p"
        )

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8()
        assertTrue(body.contains("\"quality\":\"1080p\""))
    }

    @Test
    fun `test add download with format specification`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"status": "ok"}"""))

        val result = apiClient.addDownload(
            url = "https://www.youtube.com/watch?v=test",
            quality = "best",
            format = "mp4"
        )

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8()
        assertTrue(body.contains("\"format\":\"mp4\""))
    }

    @Test
    fun `test add download with audio only format`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"status": "ok"}"""))

        val result = apiClient.addDownload(
            url = "https://www.youtube.com/watch?v=test",
            quality = "audio",
            format = "mp3"
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test basic authentication header`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"status": "ok"}"""))

        apiClient.addDownload("https://test.com/video")

        val request = mockWebServer.takeRequest()
        val authHeader = request.getHeader("Authorization")
        assertNotNull(authHeader)
        assertTrue(authHeader!!.startsWith("Basic "))
    }

    @Test
    fun `test add download without authentication`() = runBlocking {
        val noAuthClient = MeTubeApiClient(
            mockWebServer.url("/").toString().removeSuffix("/")
        )

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"status": "ok"}"""))

        val result = noAuthClient.addDownload("https://test.com/video")

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest()
        assertNull(request.getHeader("Authorization"))
    }

    @Test
    fun `test HTTP error response`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(400)
            .setBody("Bad Request"))

        val result = apiClient.addDownload("https://invalid-url")

        assertTrue(result.isFailure)
    }

    @Test
    fun `test server error response`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error"))

        val result = apiClient.addDownload("https://test.com/video")

        assertTrue(result.isFailure)
    }

    @Test
    fun `test authentication failure`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(401)
            .setBody("Unauthorized"))

        val result = apiClient.addDownload("https://test.com/video")

        assertTrue(result.isFailure)
    }

    @Test
    fun `test network error handling`() = runBlocking {
        mockWebServer.shutdown()

        val result = apiClient.addDownload("https://test.com/video")

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `test URL encoding in request`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"status": "ok"}"""))

        val urlWithSpecialChars = "https://test.com/video?id=123&title=test video"
        val result = apiClient.addDownload(urlWithSpecialChars)

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8()
        assertTrue(body.contains("https://test.com/video"))
    }

    @Test
    fun `test JSON content type header`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"status": "ok"}"""))

        apiClient.addDownload("https://test.com/video")

        val request = mockWebServer.takeRequest()
        val contentType = request.getHeader("Content-Type")
        assertEquals("application/json; charset=utf-8", contentType)
    }

    @Test
    fun `test empty username password handling`() {
        val emptyAuthClient = MeTubeApiClient(
            mockWebServer.url("/").toString().removeSuffix("/"),
            username = "",
            password = ""
        )

        assertNotNull(emptyAuthClient)
    }

    @Test
    fun `test various quality options`() = runBlocking {
        val qualities = listOf("best", "worst", "1080p", "720p", "480p", "360p", "audio")

        qualities.forEach { quality ->
            mockWebServer.enqueue(MockResponse()
                .setResponseCode(200)
                .setBody("""{"status": "ok"}"""))

            val result = apiClient.addDownload(
                url = "https://test.com/video",
                quality = quality
            )

            assertTrue("Quality $quality should succeed", result.isSuccess)
        }
    }

    @Test
    fun `test various format options`() = runBlocking {
        val formats = listOf("mp4", "webm", "mkv", "mp3", "m4a", "opus")

        formats.forEach { format ->
            mockWebServer.enqueue(MockResponse()
                .setResponseCode(200)
                .setBody("""{"status": "ok"}"""))

            val result = apiClient.addDownload(
                url = "https://test.com/video",
                format = format
            )

            assertTrue("Format $format should succeed", result.isSuccess)
        }
    }

    @Test
    fun `test multiple sequential downloads`() = runBlocking {
        val urls = listOf(
            "https://test.com/video1",
            "https://test.com/video2",
            "https://test.com/video3"
        )

        urls.forEach { _ ->
            mockWebServer.enqueue(MockResponse()
                .setResponseCode(200)
                .setBody("""{"status": "ok"}"""))
        }

        urls.forEach { url ->
            val result = apiClient.addDownload(url)
            assertTrue(result.isSuccess)
        }

        assertEquals(3, mockWebServer.requestCount)
    }
}
