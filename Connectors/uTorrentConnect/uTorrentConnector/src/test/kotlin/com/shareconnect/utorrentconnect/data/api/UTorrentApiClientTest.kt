package com.shareconnect.utorrentconnect.data.api

import com.shareconnect.utorrentconnect.data.model.UTorrentTorrent
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
 * Unit tests for UTorrentApiClient
 *
 * Tests cover:
 * - Token-based authentication with HTML parsing
 * - Automatic token refresh on 400 errors
 * - Torrent operations (add, start, stop, pause, remove)
 * - Label management
 * - RSS feed operations
 * - Settings management
 * - Error handling
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class UTorrentApiClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: UTorrentApiClient
    private val testUsername = "admin"
    private val testPassword = "password"
    private val testToken = "test-token-abc123"

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url("/gui").toString().removeSuffix("/")
        apiClient = UTorrentApiClient(baseUrl, testUsername, testPassword)
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
    fun `test get token from HTML`() = runBlocking {
        val tokenHtml = """
            <html>
            <head><title>WebUI</title></head>
            <body>
            <div id="token">$testToken</div>
            </body>
            </html>
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(tokenHtml))

        val result = apiClient.getToken()

        assertTrue(result.isSuccess)
        assertEquals(testToken, result.getOrNull())
    }

    @Test
    fun `test token caching`() = runBlocking {
        val tokenHtml = """
            <html><body><div id='token'>$testToken</div></body></html>
        """.trimIndent()

        // First call - fetches token
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(tokenHtml))

        val result1 = apiClient.getToken()
        assertTrue(result1.isSuccess)

        // Second call - uses cached token (no HTTP request)
        val result2 = apiClient.getToken()
        assertTrue(result2.isSuccess)
        assertEquals(result1.getOrNull(), result2.getOrNull())

        // Only one request should have been made
        assertEquals(1, mockWebServer.requestCount)
    }

    @Test
    fun `test force token refresh`() = runBlocking {
        val tokenHtml1 = "<html><body><div id='token'>token1</div></body></html>"
        val tokenHtml2 = "<html><body><div id='token'>token2</div></body></html>"

        // First token fetch
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(tokenHtml1))

        val result1 = apiClient.getToken()
        assertEquals("token1", result1.getOrNull())

        // Force refresh
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(tokenHtml2))

        val result2 = apiClient.getToken(forceRefresh = true)
        assertEquals("token2", result2.getOrNull())

        assertEquals(2, mockWebServer.requestCount)
    }

    @Test
    fun `test get torrents list`() = runBlocking {
        // Mock token request
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>$testToken</div></body></html>"))

        // Mock torrents list response (uTorrent array format)
        // Array format: [hash, status, name, size, progress, downloaded, uploaded, ratio, upload_speed, download_speed, eta, label, peers_connected, peers_in_swarm, seeds_connected, seeds_in_swarm, availability, torrent_queue_order, remaining]
        val torrentsJson = """
            {
                "torrents": [
                    ["hash123", 201, "Test Torrent", 1073741824, 750, 102400, 51200, 1500, 100000, 200000, 300, "Movie", 5, 10, 2, 5, 65536, 1, 500000]
                ],
                "torrentc": "12345"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(torrentsJson))

        val result = apiClient.getTorrents()

        assertTrue(result.isSuccess)
        val torrents = result.getOrNull()!!
        assertEquals(1, torrents.size)
        assertEquals("hash123", torrents[0].hash)
        assertEquals("Test Torrent", torrents[0].name)
    }

    @Test
    fun `test automatic token refresh on 400 error`() = runBlocking {
        // Initial token
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>old-token</div></body></html>"))

        // First request with old token returns 400
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(400)
            .setBody("Invalid token"))

        // New token request
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>new-token</div></body></html>"))

        // Retry with new token succeeds
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"torrents": [], "torrentc": "0"}"""))

        val result = apiClient.getTorrents()

        assertTrue(result.isSuccess)
        // Should have made 4 requests (token, failed request, new token, successful retry)
        assertEquals(4, mockWebServer.requestCount)
    }

    @Test
    fun `test add torrent by URL`() = runBlocking {
        // Mock token
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>$testToken</div></body></html>"))

        // Mock add response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"build": "25302"}"""))

        val result = apiClient.addUrl("magnet:?xt=urn:btih:test")

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest() // Skip token
        val addRequest = mockWebServer.takeRequest()
        assertTrue(addRequest.path!!.contains("action=add-url"))
        assertTrue(addRequest.path!!.contains("s=magnet"))
    }

    @Test
    fun `test start torrent`() = runBlocking {
        // Mock token
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>$testToken</div></body></html>"))

        // Mock start response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"build": "25302"}"""))

        val result = apiClient.start("hash123")

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest() // Skip token
        val startRequest = mockWebServer.takeRequest()
        assertTrue(startRequest.path!!.contains("action=start"))
        assertTrue(startRequest.path!!.contains("hash=hash123"))
    }

    @Test
    fun `test stop torrent`() = runBlocking {
        // Mock token
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>$testToken</div></body></html>"))

        // Mock stop response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"build": "25302"}"""))

        val result = apiClient.stop("hash123")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test pause torrent`() = runBlocking {
        // Mock token
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>$testToken</div></body></html>"))

        // Mock pause response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"build": "25302"}"""))

        val result = apiClient.pause("hash123")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test resume torrent`() = runBlocking {
        // Mock token
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>$testToken</div></body></html>"))

        // Mock unpause response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"build": "25302"}"""))

        val result = apiClient.resume("hash123")

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest() // Skip token
        val resumeRequest = mockWebServer.takeRequest()
        assertTrue(resumeRequest.path!!.contains("action=unpause"))
    }

    @Test
    fun `test remove torrent`() = runBlocking {
        // Mock token
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>$testToken</div></body></html>"))

        // Mock remove response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"build": "25302"}"""))

        val result = apiClient.remove("hash123")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test remove torrent with data`() = runBlocking {
        // Mock token
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>$testToken</div></body></html>"))

        // Mock removedata response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"build": "25302"}"""))

        val result = apiClient.removeData("hash123")

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest() // Skip token
        val removeRequest = mockWebServer.takeRequest()
        assertTrue(removeRequest.path!!.contains("action=removedata"))
    }

    @Test
    fun `test set label`() = runBlocking {
        // Mock token
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>$testToken</div></body></html>"))

        // Mock setprops response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"build": "25302"}"""))

        val result = apiClient.setLabel("hash123", "Movies")

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest() // Skip token
        val labelRequest = mockWebServer.takeRequest()
        assertTrue(labelRequest.path!!.contains("action=setprops"))
        assertTrue(labelRequest.path!!.contains("s=label"))
        assertTrue(labelRequest.path!!.contains("v=Movies"))
    }

    @Test
    fun `test get labels`() = runBlocking {
        // Mock token
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>$testToken</div></body></html>"))

        // Mock labels response
        val labelsJson = """
            {
                "label": [
                    ["Movies", 5, 2],
                    ["TV Shows", 10, 0]
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(labelsJson))

        val result = apiClient.getLabels()

        assertTrue(result.isSuccess)
        val labels = result.getOrNull()!!
        assertEquals(2, labels.size)
        assertEquals("Movies", labels[0].name)
        assertEquals("TV Shows", labels[1].name)
    }

    @Test
    fun `test create label`() = runBlocking {
        // Mock token
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>$testToken</div></body></html>"))

        // Mock create-label response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"build": "25302"}"""))

        val result = apiClient.createLabel("NewLabel")

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest() // Skip token
        val createRequest = mockWebServer.takeRequest()
        assertTrue(createRequest.path!!.contains("action=create-label"))
    }

    @Test
    fun `test remove label`() = runBlocking {
        // Mock token
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>$testToken</div></body></html>"))

        // Mock remove-label response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"build": "25302"}"""))

        val result = apiClient.removeLabel("OldLabel")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test get RSS feeds`() = runBlocking {
        // Mock token
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>$testToken</div></body></html>"))

        // Mock RSS feeds response
        // Array format: [id, enabled, use_url, custom_alias, subscribe_to_downloads, smart_filter_enabled, url, alias]
        val rssJson = """
            {
                "rssfeeds": [
                    [1, 1, 1, "Feed Name", 0, 0, "http://example.com/feed.rss", "Feed Name"]
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(rssJson))

        val result = apiClient.getRssFeeds()

        assertTrue(result.isSuccess)
        val feeds = result.getOrNull()!!
        assertEquals(1, feeds.size)
        assertEquals("Feed Name", feeds[0].alias)
    }

    @Test
    fun `test network error handling`() = runBlocking {
        // Shutdown server to simulate network error
        mockWebServer.shutdown()

        val result = apiClient.getTorrents()

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `test HTTP error code handling`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error"))

        val result = apiClient.getToken()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test retry limit for 400 errors`() = runBlocking {
        // Return 400 three times (exceeds retry limit of 2)
        for (i in 1..4) {
            mockWebServer.enqueue(MockResponse()
                .setResponseCode(200)
                .setBody("<html><body><div id='token'>token-$i</div></body></html>"))

            mockWebServer.enqueue(MockResponse()
                .setResponseCode(400)
                .setBody("Invalid token"))
        }

        val result = apiClient.getTorrents()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test basic authentication header`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("<html><body><div id='token'>$testToken</div></body></html>"))

        apiClient.getToken()

        val request = mockWebServer.takeRequest()
        val authHeader = request.getHeader("Authorization")
        assertNotNull(authHeader)
        assertTrue(authHeader!!.startsWith("Basic "))
    }

    @Test
    fun `test torrent status parsing`() {
        // Test status bitfield parsing
        val torrentStarted = UTorrentTorrent(
            hash = "test",
            status = 1, // Started bit
            name = "Test",
            size = 1000L,
            progress = 500L,
            downloaded = 500L,
            uploaded = 250L,
            ratio = 500L, // Ratio in per mille (0.5 = 500)
            uploadSpeed = 100L,
            downloadSpeed = 200L,
            eta = 300L,
            label = "",
            peersConnected = 5,
            peersInSwarm = 10,
            seedsConnected = 2,
            seedsInSwarm = 5,
            availability = 65536L, // 1.0 in 1/65536ths
            queueOrder = 1,
            remaining = 500L
        )

        assertTrue(torrentStarted.isStarted())
        assertFalse(torrentStarted.isPaused())

        val torrentPaused = torrentStarted.copy(status = 32) // Paused bit
        assertFalse(torrentPaused.isStarted())
        assertTrue(torrentPaused.isPaused())
    }
}
