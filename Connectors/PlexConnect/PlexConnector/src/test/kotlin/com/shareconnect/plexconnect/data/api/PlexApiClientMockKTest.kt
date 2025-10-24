package com.shareconnect.plexconnect.data.api

import com.shareconnect.plexconnect.data.model.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

/**
 * Unit tests for PlexApiClient using MockK
 *
 * This approach mocks the PlexApiService interface directly,
 * avoiding SSL/TLS issues with MockWebServer + Robolectric.
 *
 * Tests cover:
 * - PIN-based authentication flow
 * - Server discovery and information retrieval
 * - Library browsing and management
 * - Media item queries and searches
 * - Playback status updates (played, unplayed, progress)
 * - Error handling
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PlexApiClientMockKTest {

    private lateinit var mockService: PlexApiService
    private lateinit var apiClient: PlexApiClient
    private val testToken = "test-plex-token-123"
    private val testServerUrl = "http://192.168.1.100:32400"

    @Before
    fun setUp() {
        mockService = mockk()
        apiClient = PlexApiClient(mockService)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test API client initialization`() {
        assertNotNull(apiClient)
    }

    @Test
    fun `test request PIN success`() = runBlocking {
        val mockPin = PlexPinResponse(
            id = 12345,
            code = "ABCD",
            product = "PlexConnect",
            trusted = false,
            clientIdentifier = "test-client-123",
            expiresIn = 900,
            createdAt = "2025-10-25T00:00:00Z",
            expiresAt = "2025-10-25T00:15:00Z"
        )

        coEvery { mockService.requestPin(any()) } returns Response.success(mockPin)

        val result = apiClient.requestPin("test-client-123")

        assertTrue(result.isSuccess)
        val pin = result.getOrNull()!!
        assertEquals(12345L, pin.id)
        assertEquals("ABCD", pin.code)

        coVerify { mockService.requestPin(any()) }
    }

    @Test
    fun `test request PIN with HTTP error`() = runBlocking {
        coEvery { mockService.requestPin(any()) } returns Response.error(500, mockk(relaxed = true))

        val result = apiClient.requestPin("test-client-123")

        assertTrue(result.isFailure)
        coVerify { mockService.requestPin(any()) }
    }

    @Test
    fun `test check PIN before auth`() = runBlocking {
        val mockPin = PlexPinResponse(
            id = 12345,
            code = "ABCD",
            product = "PlexConnect",
            trusted = false,
            clientIdentifier = "test-client-123",
            expiresIn = 900,
            createdAt = "2025-10-25T00:00:00Z",
            expiresAt = "2025-10-25T00:15:00Z",
            authToken = null
        )

        coEvery { mockService.checkPin(12345) } returns Response.success(mockPin)

        val result = apiClient.checkPin(12345)

        assertTrue(result.isSuccess)
        val pin = result.getOrNull()!!
        assertNull(pin.authToken)
        assertFalse(pin.trusted)
    }

    @Test
    fun `test check PIN after auth`() = runBlocking {
        val mockPin = PlexPinResponse(
            id = 12345,
            code = "ABCD",
            product = "PlexConnect",
            trusted = true,
            clientIdentifier = "test-client-123",
            expiresIn = 900,
            createdAt = "2025-10-25T00:00:00Z",
            expiresAt = "2025-10-25T00:15:00Z",
            authToken = "authenticated-token-xyz"
        )

        coEvery { mockService.checkPin(12345) } returns Response.success(mockPin)

        val result = apiClient.checkPin(12345)

        assertTrue(result.isSuccess)
        val pin = result.getOrNull()!!
        assertTrue(pin.trusted)
        assertEquals("authenticated-token-xyz", pin.authToken)
    }

    @Test
    fun `test get server info`() = runBlocking {
        val mockServerInfo = PlexServerInfo(
            machineIdentifier = "test-machine-id",
            version = "1.40.0.7998",
            name = "Test Plex Server",
            host = "192.168.1.100",
            address = "192.168.1.100",
            port = 32400,
            scheme = "http"
        )

        coEvery { mockService.getServerInfo(testServerUrl) } returns Response.success(mockServerInfo)

        val result = apiClient.getServerInfo(testServerUrl)

        assertTrue(result.isSuccess)
        val serverInfo = result.getOrNull()!!
        assertEquals("Test Plex Server", serverInfo.name)
        assertEquals("test-machine-id", serverInfo.machineIdentifier)
        assertEquals(32400, serverInfo.port)
    }

    @Test
    fun `test get libraries`() = runBlocking {
        val mockLibraries = listOf(
            PlexLibrary(key = "1", type = "movie", title = "Movies"),
            PlexLibrary(key = "2", type = "show", title = "TV Shows")
        )
        val mockResponse = PlexLibraryResponse(
            mediaContainer = PlexMediaContainer(
                size = 2,
                Directory = mockLibraries
            )
        )

        coEvery { mockService.getLibraries(testServerUrl, testToken) } returns Response.success(mockResponse)

        val result = apiClient.getLibraries(testServerUrl, testToken)

        assertTrue(result.isSuccess)
        val libraries = result.getOrNull()!!
        assertEquals(2, libraries.size)
        assertEquals("Movies", libraries[0].title)
        assertEquals("movie", libraries[0].type)
    }

    @Test
    fun `test get library items`() = runBlocking {
        val mockItems = listOf(
            PlexMediaItem(ratingKey = "100", type = "movie", title = "Test Movie", year = 2024),
            PlexMediaItem(ratingKey = "101", type = "movie", title = "Another Movie", year = 2023)
        )
        val mockResponse = PlexMediaResponse(
            mediaContainer = PlexMediaContainer(
                size = 2,
                Metadata = mockItems
            )
        )

        coEvery { mockService.getLibraryItems(testServerUrl, "1", testToken, 50, 0) } returns Response.success(mockResponse)

        val result = apiClient.getLibraryItems(testServerUrl, "1", testToken, 50, 0)

        assertTrue(result.isSuccess)
        val items = result.getOrNull()!!
        assertEquals(2, items.size)
        assertEquals("Test Movie", items[0].title)
        assertEquals(2024, items[0].year)
    }

    @Test
    fun `test get media item`() = runBlocking {
        val mockItem = PlexMediaItem(
            ratingKey = "100",
            type = "movie",
            title = "Test Movie",
            year = 2024
        )
        val mockResponse = PlexMediaResponse(
            mediaContainer = PlexMediaContainer(
                size = 1,
                Metadata = listOf(mockItem)
            )
        )

        coEvery { mockService.getMediaItem(testServerUrl, "100", testToken) } returns Response.success(mockResponse)

        val result = apiClient.getMediaItem(testServerUrl, "100", testToken)

        assertTrue(result.isSuccess)
        val item = result.getOrNull()!!
        assertNotNull(item)
        assertEquals("Test Movie", item!!.title)
        assertEquals(2024, item.year)
    }

    @Test
    fun `test get media children for TV show`() = runBlocking {
        val mockChildren = listOf(
            PlexMediaItem(ratingKey = "201", type = "season", title = "Season 1", index = 1),
            PlexMediaItem(ratingKey = "202", type = "season", title = "Season 2", index = 2),
            PlexMediaItem(ratingKey = "203", type = "season", title = "Season 3", index = 3)
        )
        val mockResponse = PlexMediaResponse(
            mediaContainer = PlexMediaContainer(
                size = 3,
                Metadata = mockChildren
            )
        )

        coEvery { mockService.getMediaChildren(testServerUrl, "200", testToken) } returns Response.success(mockResponse)

        val result = apiClient.getMediaChildren(testServerUrl, "200", testToken)

        assertTrue(result.isSuccess)
        val children = result.getOrNull()!!
        assertEquals(3, children.size)
        assertEquals("Season 1", children[0].title)
        assertEquals("season", children[0].type)
    }

    @Test
    fun `test mark as played`() = runBlocking {
        coEvery { mockService.markAsPlayed(testServerUrl, "/library/metadata/100", any(), testToken) } returns Response.success(Unit)

        val result = apiClient.markAsPlayed(testServerUrl, "/library/metadata/100", testToken)

        assertTrue(result.isSuccess)
        coVerify { mockService.markAsPlayed(testServerUrl, "/library/metadata/100", any(), testToken) }
    }

    @Test
    fun `test mark as unplayed`() = runBlocking {
        coEvery { mockService.markAsUnplayed(testServerUrl, "/library/metadata/100", any(), testToken) } returns Response.success(Unit)

        val result = apiClient.markAsUnplayed(testServerUrl, "/library/metadata/100", testToken)

        assertTrue(result.isSuccess)
        coVerify { mockService.markAsUnplayed(testServerUrl, "/library/metadata/100", any(), testToken) }
    }

    @Test
    fun `test update progress`() = runBlocking {
        coEvery { mockService.updateProgress(testServerUrl, "/library/metadata/100", any(), 3600000, any(), testToken) } returns Response.success(Unit)

        val result = apiClient.updateProgress(testServerUrl, "/library/metadata/100", 3600000, testToken)

        assertTrue(result.isSuccess)
        coVerify { mockService.updateProgress(testServerUrl, "/library/metadata/100", any(), 3600000, any(), testToken) }
    }

    @Test
    fun `test search`() = runBlocking {
        val mockResults = listOf(
            PlexMediaItem(ratingKey = "100", type = "movie", title = "Test Movie Result", year = 2024),
            PlexMediaItem(ratingKey = "200", type = "show", title = "Test Show Result", year = 2023)
        )
        val mockResponse = PlexSearchResponse(
            mediaContainer = PlexMediaContainer(
                size = 2,
                Metadata = mockResults
            )
        )

        coEvery { mockService.search(testServerUrl, "test", 50, testToken) } returns Response.success(mockResponse)

        val result = apiClient.search(testServerUrl, "test", testToken, 50)

        assertTrue(result.isSuccess)
        val results = result.getOrNull()!!
        assertEquals(2, results.size)
        assertEquals("Test Movie Result", results[0].title)
        assertEquals("movie", results[0].type)
    }

    @Test
    fun `test search with empty results`() = runBlocking {
        val mockResponse = PlexSearchResponse(
            mediaContainer = PlexMediaContainer(
                size = 0,
                Metadata = emptyList()
            )
        )

        coEvery { mockService.search(testServerUrl, "nonexistent", 50, testToken) } returns Response.success(mockResponse)

        val result = apiClient.search(testServerUrl, "nonexistent", testToken, 50)

        assertTrue(result.isSuccess)
        val results = result.getOrNull()!!
        assertTrue(results.isEmpty())
    }

    @Test
    fun `test HTTP 404 error handling`() = runBlocking {
        coEvery { mockService.getLibraries(testServerUrl, testToken) } returns Response.error(404, mockk(relaxed = true))

        val result = apiClient.getLibraries(testServerUrl, testToken)

        assertTrue(result.isFailure)
    }

    @Test
    fun `test HTTP 401 unauthorized`() = runBlocking {
        coEvery { mockService.getLibraries(testServerUrl, "invalid-token") } returns Response.error(401, mockk(relaxed = true))

        val result = apiClient.getLibraries(testServerUrl, "invalid-token")

        assertTrue(result.isFailure)
    }

    @Test
    fun `test exception handling`() = runBlocking {
        coEvery { mockService.getLibraries(any(), any()) } throws RuntimeException("Network error")

        val result = apiClient.getLibraries(testServerUrl, testToken)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}
