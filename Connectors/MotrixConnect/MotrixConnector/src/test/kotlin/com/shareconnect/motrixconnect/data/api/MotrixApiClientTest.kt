package com.shareconnect.motrixconnect.data.api

import com.shareconnect.motrixconnect.data.model.*
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
 * Unit tests for MotrixApiClient using MockWebServer
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = com.shareconnect.motrixconnect.TestApplication::class)
class MotrixApiClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: MotrixApiClient
    private val testSecret = "test-secret-token"

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url("/").toString().removeSuffix("/")
        apiClient = MotrixApiClient(baseUrl, testSecret)
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
    fun `test get version success`() = runBlocking {
        val versionJson = """
            {
                "id": "test-id",
                "jsonrpc": "2.0",
                "result": {
                    "version": "1.43.0",
                    "enabledFeatures": ["Async DNS", "BitTorrent", "Firefox3 Cookie", "GZip", "HTTPS"]
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(versionJson))

        val result = apiClient.getVersion()

        assertTrue(result.isSuccess)
        val version = result.getOrNull()!!
        assertEquals("1.43.0", version.version)
        assertNotNull(version.enabledFeatures)
        assertEquals(5, version.enabledFeatures?.size)
    }

    @Test
    fun `test get global stat success`() = runBlocking {
        val statJson = """
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
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(statJson))

        val result = apiClient.getGlobalStat()

        assertTrue(result.isSuccess)
        val stat = result.getOrNull()!!
        assertEquals("1048576", stat.downloadSpeed)
        assertEquals("524288", stat.uploadSpeed)
        assertEquals("3", stat.numActive)
        assertEquals("2", stat.numWaiting)
        assertEquals("10", stat.numStopped)
    }

    @Test
    fun `test add URI success`() = runBlocking {
        val gid = "2089b05ecca3d829"

        val responseJson = """
            {
                "id": "test-id",
                "jsonrpc": "2.0",
                "result": "$gid"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.addUri("https://example.com/file.zip")

        assertTrue(result.isSuccess)
        assertEquals(gid, result.getOrNull())

        // Verify request contains the URI
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertTrue(requestBody.contains("https://example.com/file.zip"))
        assertTrue(requestBody.contains("aria2.addUri"))
    }

    @Test
    fun `test add URI with options`() = runBlocking {
        val options = MotrixDownloadOptions(
            directory = "/downloads",
            outputFileName = "custom-name.zip",
            connections = 16,
            maxDownloadSpeed = "1M"
        )

        val responseJson = """
            {
                "id": "test-id",
                "jsonrpc": "2.0",
                "result": "test-gid"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.addUri("https://example.com/file.zip", options)

        assertTrue(result.isSuccess)

        // Verify options are included in request
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertTrue(requestBody.contains("/downloads"))
        assertTrue(requestBody.contains("custom-name.zip"))
    }

    @Test
    fun `test tell status success`() = runBlocking {
        val statusJson = """
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
                    "files": [
                        {
                            "index": "1",
                            "path": "/downloads/file.zip",
                            "length": "1073741824",
                            "completedLength": "536870912",
                            "selected": "true",
                            "uris": [
                                {
                                    "uri": "https://example.com/file.zip",
                                    "status": "used"
                                }
                            ]
                        }
                    ]
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(statusJson))

        val result = apiClient.tellStatus("2089b05ecca3d829")

        assertTrue(result.isSuccess)
        val download = result.getOrNull()!!
        assertEquals("2089b05ecca3d829", download.gid)
        assertEquals("active", download.status)
        assertEquals("1073741824", download.totalLength)
        assertEquals("536870912", download.completedLength)
        assertNotNull(download.files)
        assertEquals(1, download.files?.size)
    }

    @Test
    fun `test tell active success`() = runBlocking {
        val activeJson = """
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
                    },
                    {
                        "gid": "gid2",
                        "status": "active",
                        "totalLength": "2000000",
                        "completedLength": "1000000",
                        "uploadLength": "0",
                        "downloadSpeed": "200000",
                        "uploadSpeed": "0",
                        "dir": "/downloads",
                        "connections": "16",
                        "files": []
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(activeJson))

        val result = apiClient.tellActive()

        assertTrue(result.isSuccess)
        val downloads = result.getOrNull()!!
        assertEquals(2, downloads.size)
        assertEquals("gid1", downloads[0].gid)
        assertEquals("gid2", downloads[1].gid)
    }

    @Test
    fun `test pause download success`() = runBlocking {
        val responseJson = """
            {
                "id": "test-id",
                "jsonrpc": "2.0",
                "result": "2089b05ecca3d829"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.pause("2089b05ecca3d829")

        assertTrue(result.isSuccess)
        assertEquals("2089b05ecca3d829", result.getOrNull())

        val request = mockWebServer.takeRequest()
        assertTrue(request.body.readUtf8().contains("aria2.pause"))
    }

    @Test
    fun `test unpause download success`() = runBlocking {
        val responseJson = """
            {
                "id": "test-id",
                "jsonrpc": "2.0",
                "result": "2089b05ecca3d829"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.unpause("2089b05ecca3d829")

        assertTrue(result.isSuccess)

        val request = mockWebServer.takeRequest()
        assertTrue(request.body.readUtf8().contains("aria2.unpause"))
    }

    @Test
    fun `test remove download success`() = runBlocking {
        val responseJson = """
            {
                "id": "test-id",
                "jsonrpc": "2.0",
                "result": "2089b05ecca3d829"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.remove("2089b05ecca3d829")

        assertTrue(result.isSuccess)

        val request = mockWebServer.takeRequest()
        assertTrue(request.body.readUtf8().contains("aria2.remove"))
    }

    @Test
    fun `test get global option success`() = runBlocking {
        val optionsJson = """
            {
                "id": "test-id",
                "jsonrpc": "2.0",
                "result": {
                    "max-download-limit": "0",
                    "max-overall-download-limit": "0",
                    "max-upload-limit": "0",
                    "split": "5"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(optionsJson))

        val result = apiClient.getGlobalOption()

        assertTrue(result.isSuccess)
        val options = result.getOrNull()!!
        assertTrue(options.containsKey("max-download-limit"))
        assertEquals("5", options["split"])
    }

    @Test
    fun `test change global option success`() = runBlocking {
        val responseJson = """
            {
                "id": "test-id",
                "jsonrpc": "2.0",
                "result": "OK"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.changeGlobalOption(mapOf("max-download-limit" to "1M"))

        assertTrue(result.isSuccess)

        val request = mockWebServer.takeRequest()
        assertTrue(request.body.readUtf8().contains("changeGlobalOption"))
    }

    @Test
    fun `test purge download result success`() = runBlocking {
        val responseJson = """
            {
                "id": "test-id",
                "jsonrpc": "2.0",
                "result": "OK"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.purgeDownloadResult()

        assertTrue(result.isSuccess)

        val request = mockWebServer.takeRequest()
        assertTrue(request.body.readUtf8().contains("purgeDownloadResult"))
    }

    @Test
    fun `test RPC error handling`() = runBlocking {
        val errorJson = """
            {
                "id": "test-id",
                "jsonrpc": "2.0",
                "error": {
                    "code": 1,
                    "message": "Unauthorized"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(errorJson))

        val result = apiClient.getVersion()

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception!!.message!!.contains("Unauthorized"))
    }

    @Test
    fun `test HTTP error handling`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error"))

        val result = apiClient.getVersion()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test network error handling`() = runBlocking {
        mockWebServer.shutdown()

        val result = apiClient.getVersion()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test secret token included in request`() = runBlocking {
        val responseJson = """
            {
                "id": "test-id",
                "jsonrpc": "2.0",
                "result": {
                    "version": "1.43.0"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        apiClient.getVersion()

        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        // Secret should be included as "token:secret"
        assertTrue(requestBody.contains("token:$testSecret"))
    }

    @Test
    fun `test multiple URIs add`() = runBlocking {
        val responseJson = """
            {
                "id": "test-id",
                "jsonrpc": "2.0",
                "result": "test-gid"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val uris = listOf(
            "https://mirror1.example.com/file.zip",
            "https://mirror2.example.com/file.zip"
        )

        val result = apiClient.addUris(uris)

        assertTrue(result.isSuccess)

        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertTrue(requestBody.contains("mirror1.example.com"))
        assertTrue(requestBody.contains("mirror2.example.com"))
    }
}
