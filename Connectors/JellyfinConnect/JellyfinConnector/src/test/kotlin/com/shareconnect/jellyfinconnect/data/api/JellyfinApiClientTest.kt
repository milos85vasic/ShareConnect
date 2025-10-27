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


package com.shareconnect.jellyfinconnect.data.api

import com.shareconnect.jellyfinconnect.data.models.*
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
 * Unit tests for JellyfinApiClient using MockWebServer
 *
 * Tests cover:
 * - Authentication flows (username/password, API key)
 * - Server information retrieval (public and authenticated)
 * - User information queries
 * - Library and view management
 * - Media item queries and searches
 * - Playback tracking (played, unplayed, progress)
 * - Error handling and network failures
 * - Edge cases and malformed responses
 *
 * Total: 25+ comprehensive tests
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = com.shareconnect.jellyfinconnect.TestApplication::class)
class JellyfinApiClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: JellyfinApiClient
    private val testToken = "test-jellyfin-token-abc123"
    private val testUserId = "user-id-123"

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Create API client with mock server URL
        val serverUrl = mockWebServer.url("/").toString().trimEnd('/')
        apiClient = JellyfinApiClient(
            serverUrl = serverUrl,
            deviceId = "test-device-123",
            deviceName = "TestDevice",
            appVersion = "1.0.0-test"
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // ===== API Client Initialization Tests =====

    @Test
    fun `test API client initialization`() {
        assertNotNull(apiClient)
    }

    @Test
    fun `test auth header generation without token`() {
        val header = apiClient.getAuthHeader()

        assertTrue(header.contains("MediaBrowser Client=\"TestDevice\""))
        assertTrue(header.contains("Device=\"Android\""))
        assertTrue(header.contains("DeviceId=\"test-device-123\""))
        assertTrue(header.contains("Version=\"1.0.0-test\""))
        assertFalse(header.contains("Token="))
    }

    @Test
    fun `test auth header generation with token`() {
        val header = apiClient.getAuthHeader("my-token-123")

        assertTrue(header.contains("MediaBrowser Client=\"TestDevice\""))
        assertTrue(header.contains("Token=\"my-token-123\""))
    }

    // ===== Authentication Tests =====

    @Test
    fun `test authenticateByName success`() = runBlocking {
        val authResponseJson = """
            {
                "User": {
                    "Name": "testuser",
                    "ServerId": "server123",
                    "Id": "user123",
                    "HasPassword": true,
                    "HasConfiguredPassword": true,
                    "HasConfiguredEasyPassword": false
                },
                "SessionInfo": {
                    "Id": "session123",
                    "UserId": "user123",
                    "UserName": "testuser",
                    "Client": "TestClient",
                    "LastActivityDate": "2025-10-25T00:00:00Z",
                    "DeviceName": "TestDevice",
                    "DeviceId": "test-device-123",
                    "ApplicationVersion": "1.0.0"
                },
                "AccessToken": "test-access-token-abc",
                "ServerId": "server123"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(authResponseJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.authenticateByName("testuser", "testpass")

        assertTrue(result.isSuccess)
        val auth = result.getOrNull()!!
        assertEquals("test-access-token-abc", auth.accessToken)
        assertEquals("server123", auth.serverId)
        assertEquals("testuser", auth.user?.name)
        assertEquals("user123", auth.user?.id)
    }

    @Test
    fun `test authenticateByName with invalid credentials`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(401)
            .setBody("Unauthorized"))

        val result = apiClient.authenticateByName("testuser", "wrongpassword")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("401") == true)
    }

    @Test
    fun `test authenticateByName with network error`() = runBlocking {
        mockWebServer.shutdown()

        val result = apiClient.authenticateByName("testuser", "testpass")

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `test authenticateByName with malformed JSON`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("{ invalid json }")
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.authenticateByName("testuser", "testpass")

        assertTrue(result.isFailure)
    }

    // ===== Server Information Tests =====

    @Test
    fun `test getPublicServerInfo success`() = runBlocking {
        val serverInfoJson = """
            {
                "LocalAddress": "http://192.168.1.100:8096",
                "ServerName": "My Jellyfin Server",
                "Version": "10.8.13",
                "ProductName": "Jellyfin Server",
                "OperatingSystem": "Linux",
                "Id": "server-abc-123",
                "StartupWizardCompleted": true
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(serverInfoJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getPublicServerInfo()

        assertTrue(result.isSuccess)
        val serverInfo = result.getOrNull()!!
        assertEquals("My Jellyfin Server", serverInfo.serverName)
        assertEquals("10.8.13", serverInfo.version)
        assertEquals("server-abc-123", serverInfo.id)
        assertEquals("Linux", serverInfo.operatingSystem)
    }

    @Test
    fun `test getPublicServerInfo with 500 error`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error"))

        val result = apiClient.getPublicServerInfo()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test getServerInfo with authentication`() = runBlocking {
        val serverInfoJson = """
            {
                "LocalAddress": "http://192.168.1.100:8096",
                "ServerName": "Authenticated Server",
                "Version": "10.9.0",
                "ProductName": "Jellyfin Server",
                "OperatingSystem": "Windows",
                "Id": "auth-server-456",
                "StartupWizardCompleted": true
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(serverInfoJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getServerInfo(testToken)

        assertTrue(result.isSuccess)
        val serverInfo = result.getOrNull()!!
        assertEquals("Authenticated Server", serverInfo.serverName)
        assertEquals("10.9.0", serverInfo.version)
    }

    // ===== User Information Tests =====

    @Test
    fun `test getCurrentUser success`() = runBlocking {
        val userJson = """
            {
                "Name": "currentuser",
                "ServerId": "server789",
                "Id": "current-user-id",
                "HasPassword": true,
                "HasConfiguredPassword": true,
                "HasConfiguredEasyPassword": false,
                "EnableAutoLogin": false,
                "LastLoginDate": "2025-10-25T10:00:00Z",
                "LastActivityDate": "2025-10-25T12:00:00Z"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(userJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getCurrentUser(testToken)

        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("currentuser", user.name)
        assertEquals("current-user-id", user.id)
        assertEquals("server789", user.serverId)
        assertTrue(user.hasPassword)
    }

    @Test
    fun `test getCurrentUser with invalid token`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(401)
            .setBody("Invalid token"))

        val result = apiClient.getCurrentUser("invalid-token")

        assertTrue(result.isFailure)
    }

    // ===== Library and Views Tests =====

    @Test
    fun `test getUserViews success`() = runBlocking {
        val viewsJson = """
            {
                "Items": [
                    {
                        "Name": "Movies",
                        "ServerId": "server123",
                        "Id": "movies-lib-1",
                        "Type": "CollectionFolder",
                        "CollectionType": "movies",
                        "ImageTags": {
                            "Primary": "tag123"
                        }
                    },
                    {
                        "Name": "TV Shows",
                        "ServerId": "server123",
                        "Id": "tv-lib-2",
                        "Type": "CollectionFolder",
                        "CollectionType": "tvshows",
                        "ImageTags": {
                            "Primary": "tag456"
                        }
                    }
                ],
                "TotalRecordCount": 2,
                "StartIndex": 0
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(viewsJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getUserViews(testUserId, testToken)

        assertTrue(result.isSuccess)
        val views = result.getOrNull()!!
        assertEquals(2, views.size)
        assertEquals("Movies", views[0].name)
        assertEquals("movies", views[0].collectionType)
        assertEquals("TV Shows", views[1].name)
        assertEquals("tvshows", views[1].collectionType)
    }

    @Test
    fun `test getUserViews with empty libraries`() = runBlocking {
        val emptyViewsJson = """
            {
                "Items": [],
                "TotalRecordCount": 0,
                "StartIndex": 0
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(emptyViewsJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getUserViews(testUserId, testToken)

        assertTrue(result.isSuccess)
        val views = result.getOrNull()!!
        assertTrue(views.isEmpty())
    }

    // ===== Items Query Tests =====

    @Test
    fun `test getItems success`() = runBlocking {
        val itemsJson = """
            {
                "Items": [
                    {
                        "Name": "Test Movie",
                        "ServerId": "server123",
                        "Id": "movie-1",
                        "Type": "Movie",
                        "ProductionYear": 2024,
                        "RunTimeTicks": 72000000000,
                        "OfficialRating": "PG-13",
                        "Overview": "A test movie description",
                        "ImageTags": {
                            "Primary": "img123"
                        }
                    },
                    {
                        "Name": "Another Movie",
                        "ServerId": "server123",
                        "Id": "movie-2",
                        "Type": "Movie",
                        "ProductionYear": 2023,
                        "RunTimeTicks": 54000000000
                    }
                ],
                "TotalRecordCount": 2,
                "StartIndex": 0
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(itemsJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getItems(
            userId = testUserId,
            token = testToken,
            parentId = "movies-lib-1",
            includeItemTypes = "Movie",
            recursive = true,
            limit = 50,
            startIndex = 0
        )

        assertTrue(result.isSuccess)
        val itemsResult = result.getOrNull()!!
        assertEquals(2, itemsResult.totalRecordCount)
        assertEquals(2, itemsResult.items.size)
        assertEquals("Test Movie", itemsResult.items[0].name)
        assertEquals(2024, itemsResult.items[0].productionYear)
    }

    @Test
    fun `test getItem by ID success`() = runBlocking {
        val itemJson = """
            {
                "Name": "Specific Movie",
                "ServerId": "server123",
                "Id": "specific-item-id",
                "Type": "Movie",
                "ProductionYear": 2025,
                "RunTimeTicks": 90000000000,
                "OfficialRating": "R",
                "Overview": "Detailed movie information",
                "MediaType": "Video",
                "UserData": {
                    "PlaybackPositionTicks": 3600000000,
                    "PlayCount": 2,
                    "IsFavorite": true,
                    "Played": true
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(itemJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getItem(testUserId, "specific-item-id", testToken)

        assertTrue(result.isSuccess)
        val item = result.getOrNull()!!
        assertEquals("Specific Movie", item.name)
        assertEquals(2025, item.productionYear)
        assertEquals(true, item.userData?.isFavorite)
        assertEquals(2, item.userData?.playCount)
    }

    @Test
    fun `test getItem with not found error`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(404)
            .setBody("Item not found"))

        val result = apiClient.getItem(testUserId, "nonexistent-id", testToken)

        assertTrue(result.isFailure)
    }

    // ===== Playback Tracking Tests =====

    @Test
    fun `test markPlayed success`() = runBlocking {
        val userDataJson = """
            {
                "PlaybackPositionTicks": 0,
                "PlayCount": 1,
                "IsFavorite": false,
                "Played": true,
                "Key": "item-key-123"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(userDataJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.markPlayed(testUserId, "item-123", testToken)

        assertTrue(result.isSuccess)
        val userData = result.getOrNull()!!
        assertEquals(true, userData.played)
        assertEquals(1, userData.playCount)
    }

    @Test
    fun `test markUnplayed success`() = runBlocking {
        val userDataJson = """
            {
                "PlaybackPositionTicks": 0,
                "PlayCount": 0,
                "IsFavorite": false,
                "Played": false,
                "Key": "item-key-456"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(userDataJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.markUnplayed(testUserId, "item-456", testToken)

        assertTrue(result.isSuccess)
        val userData = result.getOrNull()!!
        assertEquals(false, userData.played)
        assertEquals(0, userData.playCount)
    }

    @Test
    fun `test updateProgress success`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(204)) // No content response is common for progress updates

        val result = apiClient.updateProgress(
            itemId = "movie-789",
            positionTicks = 3600000000, // 1 hour
            isPaused = false,
            token = testToken
        )

        assertTrue(result.isSuccess)

        // Verify the request was made
        val request = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("/Sessions/Playing/Progress"))
    }

    @Test
    fun `test updateProgress with paused state`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(204))

        val result = apiClient.updateProgress(
            itemId = "movie-paused",
            positionTicks = 1800000000, // 30 minutes
            isPaused = true,
            token = testToken
        )

        assertTrue(result.isSuccess)
    }

    // ===== Search Tests =====

    @Test
    fun `test search success`() = runBlocking {
        val searchJson = """
            {
                "SearchHints": [
                    {
                        "ItemId": "search-result-1",
                        "Id": "search-result-1",
                        "Name": "Search Result Movie",
                        "Type": "Movie",
                        "MediaType": "Video",
                        "ProductionYear": 2024,
                        "MatchedTerm": "search"
                    },
                    {
                        "ItemId": "search-result-2",
                        "Id": "search-result-2",
                        "Name": "Search Result Show",
                        "Type": "Series",
                        "MediaType": "Video",
                        "ProductionYear": 2023
                    }
                ],
                "TotalRecordCount": 2
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(searchJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.search("search", testUserId, testToken, 50)

        assertTrue(result.isSuccess)
        val searchResult = result.getOrNull()!!
        assertEquals(2, searchResult.totalRecordCount)
        assertEquals(2, searchResult.searchHints.size)
        assertEquals("Search Result Movie", searchResult.searchHints[0].name)
        assertEquals("Movie", searchResult.searchHints[0].type)
    }

    @Test
    fun `test search with no results`() = runBlocking {
        val emptySearchJson = """
            {
                "SearchHints": [],
                "TotalRecordCount": 0
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(emptySearchJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.search("nonexistent", testUserId, testToken, 50)

        assertTrue(result.isSuccess)
        val searchResult = result.getOrNull()!!
        assertEquals(0, searchResult.totalRecordCount)
        assertTrue(searchResult.searchHints.isEmpty())
    }

    @Test
    fun `test search with network timeout`() = runBlocking {
        mockWebServer.shutdown()

        val result = apiClient.search("test", testUserId, testToken, 50)

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    // ===== Additional Edge Case Tests =====

    @Test
    fun `test API with empty server URL`() {
        val emptyClient = JellyfinApiClient(
            serverUrl = "",
            deviceId = "test-123",
            deviceName = "Test",
            appVersion = "1.0.0"
        )
        assertNotNull(emptyClient)
    }

    @Test
    fun `test API response with missing optional fields`() = runBlocking {
        val minimalItemJson = """
            {
                "Name": "Minimal Item",
                "ServerId": "server123",
                "Id": "minimal-1",
                "Type": "Movie"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(minimalItemJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getItem(testUserId, "minimal-1", testToken)

        assertTrue(result.isSuccess)
        val item = result.getOrNull()!!
        assertEquals("Minimal Item", item.name)
        assertNull(item.productionYear)
        assertNull(item.overview)
    }
}
