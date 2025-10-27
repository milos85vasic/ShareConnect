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


package com.shareconnect.motrixconnect.data.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.motrixconnect.data.model.*
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MotrixApiClientIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: MotrixApiClient
    private val testSecret = "test-secret-token"

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiClient = MotrixApiClient(mockWebServer.url("/").toString(), testSecret)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getVersion_returnsVersion_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "id": "test-id",
                    "jsonrpc": "2.0",
                    "result": {
                        "version": "1.43.0",
                        "enabledFeatures": ["Async DNS", "BitTorrent", "HTTPS"]
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getVersion()

        // Then
        assertTrue(result.isSuccess)
        val version = result.getOrNull()!!
        assertEquals("1.43.0", version.version)
        assertNotNull(version.enabledFeatures)
        assertEquals(3, version.enabledFeatures?.size)
    }

    @Test
    fun getVersion_returnsFailure_onNetworkError() = runTest {
        // Given
        mockWebServer.shutdown()

        // When
        val result = apiClient.getVersion()

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun getGlobalStat_returnsStat_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "id": "test-id",
                    "jsonrpc": "2.0",
                    "result": {
                        "downloadSpeed": "1048576",
                        "uploadSpeed": "524288",
                        "numActive": "3",
                        "numWaiting": "2",
                        "numStopped": "10",
                        "numStoppedTotal": "100"
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getGlobalStat()

        // Then
        assertTrue(result.isSuccess)
        val stat = result.getOrNull()!!
        assertEquals("1048576", stat.downloadSpeed)
        assertEquals("3", stat.numActive)
    }

    @Test
    fun addUri_returnsGid_onSuccessfulResponse() = runTest {
        // Given
        val gid = "2089b05ecca3d829"
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "id": "test-id",
                    "jsonrpc": "2.0",
                    "result": "$gid"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.addUri("https://example.com/file.zip")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(gid, result.getOrNull())
    }

    @Test
    fun addUri_withOptions_sendsCorrectRequest() = runTest {
        // Given
        val options = MotrixDownloadOptions(
            directory = "/downloads",
            outputFileName = "custom-name.zip",
            connections = 16
        )
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "test-id", "jsonrpc": "2.0", "result": "test-gid"}""")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.addUri("https://example.com/file.zip", options)

        // Then
        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertTrue(requestBody.contains("/downloads"))
        assertTrue(requestBody.contains("custom-name.zip"))
    }

    @Test
    fun tellStatus_returnsDownload_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "id": "test-id",
                    "jsonrpc": "2.0",
                    "result": {
                        "gid": "2089b05ecca3d829",
                        "status": "active",
                        "totalLength": "1073741824",
                        "completedLength": "536870912",
                        "uploadLength": "0",
                        "downloadSpeed": "1048576",
                        "uploadSpeed": "0",
                        "dir": "/downloads",
                        "connections": "16",
                        "files": []
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.tellStatus("2089b05ecca3d829")

        // Then
        assertTrue(result.isSuccess)
        val download = result.getOrNull()!!
        assertEquals("2089b05ecca3d829", download.gid)
        assertEquals("active", download.status)
        assertEquals("1073741824", download.totalLength)
    }

    @Test
    fun tellActive_returnsDownloadsList_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "id": "test-id",
                    "jsonrpc": "2.0",
                    "result": [
                        {
                            "gid": "gid1",
                            "status": "active",
                            "totalLength": "1000000",
                            "completedLength": "500000",
                            "uploadLength": "0",
                            "downloadSpeed": "100000",
                            "uploadSpeed": "0",
                            "dir": "/downloads",
                            "connections": "8",
                            "files": []
                        }
                    ]
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.tellActive()

        // Then
        assertTrue(result.isSuccess)
        val downloads = result.getOrNull()!!
        assertEquals(1, downloads.size)
        assertEquals("gid1", downloads[0].gid)
    }

    @Test
    fun pause_succeeds_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "test-id", "jsonrpc": "2.0", "result": "2089b05ecca3d829"}""")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.pause("2089b05ecca3d829")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("2089b05ecca3d829", result.getOrNull())
    }

    @Test
    fun unpause_succeeds_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "test-id", "jsonrpc": "2.0", "result": "2089b05ecca3d829"}""")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.unpause("2089b05ecca3d829")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun remove_succeeds_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "test-id", "jsonrpc": "2.0", "result": "2089b05ecca3d829"}""")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.remove("2089b05ecca3d829")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun getGlobalOption_returnsOptions_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "id": "test-id",
                    "jsonrpc": "2.0",
                    "result": {
                        "max-download-limit": "0",
                        "split": "5"
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getGlobalOption()

        // Then
        assertTrue(result.isSuccess)
        val options = result.getOrNull()!!
        assertTrue(options.containsKey("max-download-limit"))
        assertEquals("5", options["split"])
    }

    @Test
    fun rpcError_returnsFailure() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "id": "test-id",
                    "jsonrpc": "2.0",
                    "error": {
                        "code": 1,
                        "message": "Unauthorized"
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getVersion()

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Unauthorized") == true)
    }

    @Test
    fun httpError_returnsFailure() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getVersion()

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun secretToken_includedInRequest() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": "test-id", "jsonrpc": "2.0", "result": {"version": "1.43.0"}}""")
        mockWebServer.enqueue(mockResponse)

        // When
        apiClient.getVersion()

        // Then
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertTrue(requestBody.contains("token:$testSecret"))
    }
}
