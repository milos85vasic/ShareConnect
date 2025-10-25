package com.shareconnect.jellyfinconnect.data.api

import com.shareconnect.jellyfinconnect.data.models.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

/**
 * Unit tests for JellyfinApiClient using MockK
 * Tests all major API client operations using mocked service
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = com.shareconnect.jellyfinconnect.TestApplication::class)
class JellyfinApiClientMockKTest {

    private lateinit var mockService: JellyfinApiService
    private lateinit var apiClient: JellyfinApiClient

    @Before
    fun setUp() {
        mockService = mockk()
        apiClient = JellyfinApiClient(
            serverUrl = "http://test.server:8096",
            deviceId = "test-device-123",
            deviceName = "TestDevice",
            appVersion = "1.0.0-test",
            jellyfinApiService = mockService
        )
    }

    // ===== Authentication Tests =====

    @Test
    fun `test authenticateByName success`() = runBlocking {
        val mockUser = JellyfinUser(
            name = "testuser",
            serverId = "server123",
            id = "user123",
            hasPassword = true,
            hasConfiguredPassword = true,
            hasConfiguredEasyPassword = false
        )
        val mockSession = JellyfinSession(
            id = "session123",
            userId = "user123",
            userName = "testuser",
            client = "TestClient",
            lastActivityDate = "2025-10-25T00:00:00Z",
            deviceName = "TestDevice",
            deviceId = "test-device-123",
            applicationVersion = "1.0.0"
        )
        val mockAuth = JellyfinAuthResponse(
            user = mockUser,
            sessionInfo = mockSession,
            accessToken = "test-token-123",
            serverId = "server123"
        )

        coEvery { mockService.authenticateByName(any(), any()) } returns Response.success(mockAuth)

        val result = apiClient.authenticateByName("testuser", "testpass")

        assertTrue(result.isSuccess)
        val auth = result.getOrNull()!!
        assertEquals("test-token-123", auth.accessToken)
        assertEquals("testuser", auth.user?.name)

        coVerify { mockService.authenticateByName(any(), any()) }
    }

    @Test
    fun `test authenticateByName failure`() = runBlocking {
        coEvery { mockService.authenticateByName(any(), any()) } returns Response.error(401, mockk(relaxed = true))

        val result = apiClient.authenticateByName("testuser", "wrongpass")

        assertTrue(result.isFailure)
    }

    // ===== Server Information Tests =====

    @Test
    fun `test getPublicServerInfo success`() = runBlocking {
        val mockServerInfo = JellyfinServerInfo(
            localAddress = "http://192.168.1.100:8096",
            serverName = "My Jellyfin Server",
            version = "10.8.13",
            productName = "Jellyfin Server",
            operatingSystem = "Linux",
            id = "server123",
            startupWizardCompleted = true
        )

        coEvery { mockService.getPublicServerInfo(any()) } returns Response.success(mockServerInfo)

        val result = apiClient.getPublicServerInfo()

        assertTrue(result.isSuccess)
        val serverInfo = result.getOrNull()!!
        assertEquals("My Jellyfin Server", serverInfo.serverName)
        assertEquals("10.8.13", serverInfo.version)

        coVerify { mockService.getPublicServerInfo(any()) }
    }

    @Test
    fun `test getServerInfo with token success`() = runBlocking {
        val mockServerInfo = JellyfinServerInfo(
            localAddress = "http://192.168.1.100:8096",
            serverName = "My Jellyfin Server",
            version = "10.8.13",
            productName = "Jellyfin Server",
            operatingSystem = "Linux",
            id = "server123",
            startupWizardCompleted = true
        )

        coEvery { mockService.getServerInfo(any(), any()) } returns Response.success(mockServerInfo)

        val result = apiClient.getServerInfo("test-token")

        assertTrue(result.isSuccess)
        val serverInfo = result.getOrNull()!!
        assertEquals("My Jellyfin Server", serverInfo.serverName)

        coVerify { mockService.getServerInfo(any(), any()) }
    }

    @Test
    fun `test getCurrentUser success`() = runBlocking {
        val mockUser = JellyfinUser(
            name = "testuser",
            serverId = "server123",
            id = "user123",
            hasPassword = true,
            hasConfiguredPassword = true,
            hasConfiguredEasyPassword = false,
            enableAutoLogin = false,
            lastLoginDate = "2025-10-25T00:00:00Z",
            lastActivityDate = "2025-10-25T01:00:00Z"
        )

        coEvery { mockService.getCurrentUser(any(), any()) } returns Response.success(mockUser)

        val result = apiClient.getCurrentUser("test-token")

        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("testuser", user.name)
        assertEquals("user123", user.id)

        coVerify { mockService.getCurrentUser(any(), any()) }
    }

    // ===== Library and Media Tests =====

    @Test
    fun `test getUserViews success`() = runBlocking {
        val mockItems = listOf(
            JellyfinItem(
                name = "Movies",
                serverId = "server123",
                id = "lib1",
                type = "CollectionFolder",
                collectionType = "movies"
            ),
            JellyfinItem(
                name = "TV Shows",
                serverId = "server123",
                id = "lib2",
                type = "CollectionFolder",
                collectionType = "tvshows"
            )
        )
        val mockResult = JellyfinItemsResult(
            items = mockItems,
            totalRecordCount = 2,
            startIndex = 0
        )

        coEvery { mockService.getUserViews(any(), any(), any()) } returns Response.success(mockResult)

        val result = apiClient.getUserViews("user123", "test-token")

        assertTrue(result.isSuccess)
        val libraries = result.getOrNull()!!
        assertEquals(2, libraries.size)
        assertEquals("Movies", libraries[0].name)
        assertEquals("TV Shows", libraries[1].name)

        coVerify { mockService.getUserViews(any(), any(), any()) }
    }

    @Test
    fun `test getItems success`() = runBlocking {
        val mockItems = listOf(
            JellyfinItem(
                name = "Test Movie",
                serverId = "server123",
                id = "item1",
                type = "Movie",
                productionYear = 2024,
                runTimeTicks = 72000000000, // 2 hours in ticks
                overview = "A test movie"
            )
        )
        val mockResult = JellyfinItemsResult(
            items = mockItems,
            totalRecordCount = 1,
            startIndex = 0
        )

        coEvery { mockService.getItems(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns Response.success(mockResult)

        val result = apiClient.getItems("user123", "test-token", parentId = "lib1", limit = 50)

        assertTrue(result.isSuccess)
        val itemsResult = result.getOrNull()!!
        assertEquals(1, itemsResult.items.size)
        assertEquals("Test Movie", itemsResult.items[0].name)

        coVerify { mockService.getItems(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `test getItem success`() = runBlocking {
        val mockItem = JellyfinItem(
            name = "Test Movie",
            serverId = "server123",
            id = "item1",
            type = "Movie",
            productionYear = 2024,
            runTimeTicks = 72000000000,
            overview = "A test movie",
            userData = JellyfinUserData(
                playbackPositionTicks = 0,
                playCount = 0,
                isFavorite = false,
                played = false
            )
        )

        coEvery { mockService.getItem(any(), any(), any(), any()) } returns Response.success(mockItem)

        val result = apiClient.getItem("user123", "item1", "test-token")

        assertTrue(result.isSuccess)
        val item = result.getOrNull()!!
        assertEquals("Test Movie", item.name)
        assertEquals("item1", item.id)

        coVerify { mockService.getItem(any(), any(), any(), any()) }
    }

    // ===== Playback Tracking Tests =====

    @Test
    fun `test markPlayed success`() = runBlocking {
        val mockUserData = JellyfinUserData(
            playbackPositionTicks = null,
            playCount = 1,
            isFavorite = false,
            played = true
        )

        coEvery { mockService.markPlayed(any(), any(), any(), any()) } returns Response.success(mockUserData)

        val result = apiClient.markPlayed("user123", "item1", "test-token")

        assertTrue(result.isSuccess)
        val userData = result.getOrNull()!!
        assertEquals(1, userData.playCount)
        assertEquals(true, userData.played)

        coVerify { mockService.markPlayed(any(), any(), any(), any()) }
    }

    @Test
    fun `test markUnplayed success`() = runBlocking {
        val mockUserData = JellyfinUserData(
            playbackPositionTicks = null,
            playCount = 0,
            isFavorite = false,
            played = false
        )

        coEvery { mockService.markUnplayed(any(), any(), any(), any()) } returns Response.success(mockUserData)

        val result = apiClient.markUnplayed("user123", "item1", "test-token")

        assertTrue(result.isSuccess)
        val userData = result.getOrNull()!!
        assertEquals(0, userData.playCount)
        assertEquals(false, userData.played)

        coVerify { mockService.markUnplayed(any(), any(), any(), any()) }
    }

    @Test
    fun `test updateProgress success`() = runBlocking {
        coEvery { mockService.updateProgress(any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.updateProgress("item1", 36000000000, isPaused = false, "test-token")

        assertTrue(result.isSuccess)

        coVerify { mockService.updateProgress(any(), any(), any()) }
    }

    // ===== Search Tests =====

    @Test
    fun `test search with results`() = runBlocking {
        val mockHints = listOf(
            JellyfinSearchHint(
                itemId = "item1",
                id = "item1",
                name = "Test Movie",
                type = "Movie",
                mediaType = "Video",
                productionYear = 2024,
                matchedTerm = "Test"
            ),
            JellyfinSearchHint(
                itemId = "item2",
                id = "item2",
                name = "Test Series",
                type = "Series",
                mediaType = "Video",
                productionYear = 2023,
                matchedTerm = "Test"
            )
        )
        val mockResult = JellyfinSearchResult(
            searchHints = mockHints,
            totalRecordCount = 2
        )

        coEvery { mockService.search(any(), any(), any(), any(), any()) } returns Response.success(mockResult)

        val result = apiClient.search("Test", "user123", "test-token")

        assertTrue(result.isSuccess)
        val searchResult = result.getOrNull()!!
        assertEquals(2, searchResult.searchHints.size)
        assertEquals("Test Movie", searchResult.searchHints[0].name)
        assertEquals("Test Series", searchResult.searchHints[1].name)

        coVerify { mockService.search(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `test search with empty results`() = runBlocking {
        val mockResult = JellyfinSearchResult(
            searchHints = emptyList(),
            totalRecordCount = 0
        )

        coEvery { mockService.search(any(), any(), any(), any(), any()) } returns Response.success(mockResult)

        val result = apiClient.search("NonexistentMovie", "user123", "test-token")

        assertTrue(result.isSuccess)
        val searchResult = result.getOrNull()!!
        assertEquals(0, searchResult.searchHints.size)

        coVerify { mockService.search(any(), any(), any(), any(), any()) }
    }

    // ===== Error Handling Tests =====

    @Test
    fun `test HTTP 404 error handling`() = runBlocking {
        coEvery { mockService.getItem(any(), any(), any(), any()) } returns Response.error(404, mockk(relaxed = true))

        val result = apiClient.getItem("user123", "nonexistent", "test-token")

        assertTrue(result.isFailure)
    }

    @Test
    fun `test HTTP 401 unauthorized error`() = runBlocking {
        coEvery { mockService.getUserViews(any(), any(), any()) } returns Response.error(401, mockk(relaxed = true))

        val result = apiClient.getUserViews("user123", "invalid-token")

        assertTrue(result.isFailure)
    }

    @Test
    fun `test exception handling`() = runBlocking {
        coEvery { mockService.getPublicServerInfo(any()) } throws Exception("Network error")

        val result = apiClient.getPublicServerInfo()

        assertTrue(result.isFailure)
    }

    // ===== Auth Header Tests =====

    @Test
    fun `test auth header generation without token`() {
        val header = apiClient.getAuthHeader()

        assertNotNull(header)
        assertTrue(header.contains("MediaBrowser"))
        assertTrue(header.contains("Client=\"TestDevice\""))
        assertTrue(header.contains("DeviceId=\"test-device-123\""))
        assertTrue(header.contains("Version=\"1.0.0-test\""))
        assertFalse(header.contains("Token="))
    }

    @Test
    fun `test auth header generation with token`() {
        val header = apiClient.getAuthHeader("my-access-token")

        assertNotNull(header)
        assertTrue(header.contains("Token=\"my-access-token\""))
    }
}
