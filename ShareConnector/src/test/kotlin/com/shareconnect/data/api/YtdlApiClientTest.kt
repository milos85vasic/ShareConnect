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
 * Unit tests for YtdlApiClient
 *
 * Tests cover:
 * - Adding downloads with yt-dlp REST API
 * - Output template configuration
 * - Post-processor options
 * - Authentication (Basic Auth)
 * - Error handling
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class YtdlApiClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: YtdlApiClient
    private val testUsername = "ytdl"
    private val testPassword = "password"

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url("/").toString().removeSuffix("/")
        apiClient = YtdlApiClient(baseUrl, testUsername, testPassword)
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
    fun `test add download with default options`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "download-123", "status": "queued"}"""))

        val result = apiClient.addDownload("https://www.youtube.com/watch?v=test")

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest()
        assertEquals("POST", request.method)
        assertTrue(request.path!!.contains("/download"))
    }

    @Test
    fun `test add download with output template`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "download-123", "status": "queued"}"""))

        val result = apiClient.addDownload(
            url = "https://www.youtube.com/watch?v=test",
            outputTemplate = "%(title)s.%(ext)s"
        )

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8()
        assertTrue(body.contains("%(title)s.%(ext)s"))
    }

    @Test
    fun `test add download with format specification`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "download-123", "status": "queued"}"""))

        val result = apiClient.addDownload(
            url = "https://www.youtube.com/watch?v=test",
            format = "bestvideo[height<=1080]+bestaudio/best"
        )

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8()
        assertTrue(body.contains("bestvideo"))
    }

    @Test
    fun `test add audio only download`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "download-123", "status": "queued"}"""))

        val result = apiClient.addDownload(
            url = "https://www.youtube.com/watch?v=test",
            audioOnly = true
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test add download with post-processor`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "download-123", "status": "queued"}"""))

        val result = apiClient.addDownload(
            url = "https://www.youtube.com/watch?v=test",
            postProcessor = "FFmpegExtractAudio",
            postProcessorArgs = "--audio-format mp3 --audio-quality 0"
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test basic authentication header`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "download-123"}"""))

        apiClient.addDownload("https://test.com/video")

        val request = mockWebServer.takeRequest()
        val authHeader = request.getHeader("Authorization")
        assertNotNull(authHeader)
        assertTrue(authHeader!!.startsWith("Basic "))
    }

    @Test
    fun `test add download without authentication`() = runBlocking {
        val noAuthClient = YtdlApiClient(
            mockWebServer.url("/").toString().removeSuffix("/")
        )

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "download-123"}"""))

        val result = noAuthClient.addDownload("https://test.com/video")

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest()
        assertNull(request.getHeader("Authorization"))
    }

    @Test
    fun `test HTTP error response`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(400)
            .setBody("""{"error": "Invalid URL"}"""))

        val result = apiClient.addDownload("invalid-url")

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
    fun `test JSON content type header`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "123"}"""))

        apiClient.addDownload("https://test.com/video")

        val request = mockWebServer.takeRequest()
        val contentType = request.getHeader("Content-Type")
        assertEquals("application/json; charset=utf-8", contentType)
    }

    @Test
    fun `test playlist download`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "playlist-123", "type": "playlist"}"""))

        val result = apiClient.addDownload(
            url = "https://www.youtube.com/playlist?list=PLtest",
            outputTemplate = "%(playlist_title)s/%(title)s.%(ext)s"
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test various output templates`() = runBlocking {
        val templates = listOf(
            "%(title)s.%(ext)s",
            "%(uploader)s/%(title)s.%(ext)s",
            "%(upload_date)s - %(title)s.%(ext)s",
            "%(playlist_title)s/%(playlist_index)02d - %(title)s.%(ext)s"
        )

        templates.forEach { template ->
            mockWebServer.enqueue(MockResponse()
                .setResponseCode(200)
                .setBody("""{"id": "download-123"}"""))

            val result = apiClient.addDownload(
                url = "https://test.com/video",
                outputTemplate = template
            )

            assertTrue("Template '$template' should succeed", result.isSuccess)
        }
    }

    @Test
    fun `test various format strings`() = runBlocking {
        val formats = listOf(
            "best",
            "worst",
            "bestvideo+bestaudio",
            "bestvideo[height<=1080]+bestaudio/best",
            "bestaudio/best"
        )

        formats.forEach { format ->
            mockWebServer.enqueue(MockResponse()
                .setResponseCode(200)
                .setBody("""{"id": "download-123"}"""))

            val result = apiClient.addDownload(
                url = "https://test.com/video",
                format = format
            )

            assertTrue("Format '$format' should succeed", result.isSuccess)
        }
    }

    @Test
    fun `test download with subtitle options`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "download-123"}"""))

        val result = apiClient.addDownload(
            url = "https://test.com/video",
            writeSubtitles = true,
            subtitleLanguages = "en,es"
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test download with thumbnail embedding`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "download-123"}"""))

        val result = apiClient.addDownload(
            url = "https://test.com/video",
            embedThumbnail = true
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test concurrent downloads`() = runBlocking {
        repeat(5) {
            mockWebServer.enqueue(MockResponse()
                .setResponseCode(200)
                .setBody("""{"id": "download-$it"}"""))
        }

        val urls = List(5) { "https://test.com/video$it" }
        val results = urls.map { url ->
            apiClient.addDownload(url)
        }

        assertTrue(results.all { it.isSuccess })
        assertEquals(5, mockWebServer.requestCount)
    }
}
