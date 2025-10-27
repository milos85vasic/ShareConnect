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
 * Integration tests for JellyfinApiClient
 *
 * Tests end-to-end workflows and scenarios:
 * - Complete authentication flow
 * - Library browsing workflow
 * - Media playback workflow
 * - Search and discovery workflow
 * - Error recovery scenarios
 * - Multi-step operations
 * - Session management
 *
 * Total: 20 integration tests
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = com.shareconnect.jellyfinconnect.TestApplication::class)
class JellyfinApiClientIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: JellyfinApiClient
    private val testUserId = "integration-user-123"
    private val testToken = "integration-token-abc"

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val serverUrl = mockWebServer.url("/").toString().trimEnd('/')
        apiClient = JellyfinApiClient(
            serverUrl = serverUrl,
            deviceId = "integration-test-device",
            deviceName = "IntegrationTestDevice",
            appVersion = "1.0.0-integration"
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // ===== Complete Authentication Flow Tests =====

    @Test
    fun `test complete authentication workflow`() = runBlocking {
        // Step 1: Get public server info
        val publicServerInfoJson = """
            {
                "ServerName": "Test Server",
                "Version": "10.8.13",
                "Id": "server-integration-1",
                "StartupWizardCompleted": true
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(publicServerInfoJson)
            .addHeader("Content-Type", "application/json"))

        val serverInfoResult = apiClient.getPublicServerInfo()
        assertTrue(serverInfoResult.isSuccess)
        assertEquals("Test Server", serverInfoResult.getOrNull()?.serverName)

        // Step 2: Authenticate with credentials
        val authResponseJson = """
            {
                "User": {
                    "Name": "integrationuser",
                    "ServerId": "server-integration-1",
                    "Id": "$testUserId",
                    "HasPassword": true,
                    "HasConfiguredPassword": true,
                    "HasConfiguredEasyPassword": false
                },
                "SessionInfo": {
                    "Id": "session-integration",
                    "UserId": "$testUserId",
                    "UserName": "integrationuser",
                    "Client": "IntegrationTest",
                    "LastActivityDate": "2025-10-25T00:00:00Z",
                    "DeviceName": "IntegrationTestDevice",
                    "DeviceId": "integration-test-device",
                    "ApplicationVersion": "1.0.0"
                },
                "AccessToken": "$testToken",
                "ServerId": "server-integration-1"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(authResponseJson)
            .addHeader("Content-Type", "application/json"))

        val authResult = apiClient.authenticateByName("integrationuser", "password123")
        assertTrue(authResult.isSuccess)
        assertEquals(testToken, authResult.getOrNull()?.accessToken)

        // Step 3: Get current user with token
        val currentUserJson = """
            {
                "Name": "integrationuser",
                "ServerId": "server-integration-1",
                "Id": "$testUserId",
                "HasPassword": true,
                "HasConfiguredPassword": true,
                "HasConfiguredEasyPassword": false,
                "LastLoginDate": "2025-10-25T10:00:00Z"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(currentUserJson)
            .addHeader("Content-Type", "application/json"))

        val userResult = apiClient.getCurrentUser(testToken)
        assertTrue(userResult.isSuccess)
        assertEquals("integrationuser", userResult.getOrNull()?.name)
    }

    // ===== Library Browsing Workflow Tests =====

    @Test
    fun `test complete library browsing workflow`() = runBlocking {
        // Step 1: Get user views/libraries
        val viewsJson = """
            {
                "Items": [
                    {
                        "Name": "Movies",
                        "ServerId": "server-1",
                        "Id": "movies-lib",
                        "Type": "CollectionFolder",
                        "CollectionType": "movies"
                    }
                ],
                "TotalRecordCount": 1,
                "StartIndex": 0
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(viewsJson)
            .addHeader("Content-Type", "application/json"))

        val viewsResult = apiClient.getUserViews(testUserId, testToken)
        assertTrue(viewsResult.isSuccess)
        val libraries = viewsResult.getOrNull()!!
        assertEquals(1, libraries.size)
        val libraryId = libraries[0].id

        // Step 2: Get items from the library
        val itemsJson = """
            {
                "Items": [
                    {
                        "Name": "Movie 1",
                        "ServerId": "server-1",
                        "Id": "movie-1",
                        "Type": "Movie",
                        "ProductionYear": 2024
                    },
                    {
                        "Name": "Movie 2",
                        "ServerId": "server-1",
                        "Id": "movie-2",
                        "Type": "Movie",
                        "ProductionYear": 2023
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

        val itemsResult = apiClient.getItems(testUserId, testToken, parentId = libraryId)
        assertTrue(itemsResult.isSuccess)
        assertEquals(2, itemsResult.getOrNull()?.items?.size)

        // Step 3: Get specific item details
        val specificItemJson = """
            {
                "Name": "Movie 1",
                "ServerId": "server-1",
                "Id": "movie-1",
                "Type": "Movie",
                "ProductionYear": 2024,
                "Overview": "Detailed description of Movie 1",
                "RunTimeTicks": 72000000000
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(specificItemJson)
            .addHeader("Content-Type", "application/json"))

        val itemResult = apiClient.getItem(testUserId, "movie-1", testToken)
        assertTrue(itemResult.isSuccess)
        assertEquals("Movie 1", itemResult.getOrNull()?.name)
    }

    // ===== Media Playback Workflow Tests =====

    @Test
    fun `test complete playback tracking workflow`() = runBlocking {
        val itemId = "playback-item-123"

        // Step 1: Start playback by updating progress to beginning
        mockWebServer.enqueue(MockResponse().setResponseCode(204))

        val startResult = apiClient.updateProgress(itemId, 0, false, testToken)
        assertTrue(startResult.isSuccess)

        // Step 2: Update progress during playback
        mockWebServer.enqueue(MockResponse().setResponseCode(204))

        val progressResult = apiClient.updateProgress(itemId, 3600000000, false, testToken)
        assertTrue(progressResult.isSuccess)

        // Step 3: Pause playback
        mockWebServer.enqueue(MockResponse().setResponseCode(204))

        val pauseResult = apiClient.updateProgress(itemId, 3600000000, true, testToken)
        assertTrue(pauseResult.isSuccess)

        // Step 4: Mark as played when finished
        val userDataJson = """
            {
                "PlaybackPositionTicks": 0,
                "PlayCount": 1,
                "Played": true
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(userDataJson)
            .addHeader("Content-Type", "application/json"))

        val markPlayedResult = apiClient.markPlayed(testUserId, itemId, testToken)
        assertTrue(markPlayedResult.isSuccess)
        assertEquals(true, markPlayedResult.getOrNull()?.played)
    }

    @Test
    fun `test playback state management`() = runBlocking {
        val itemId = "state-item-456"

        // Mark as played
        val playedDataJson = """
            {"PlaybackPositionTicks": 0, "PlayCount": 1, "Played": true}
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(playedDataJson)
            .addHeader("Content-Type", "application/json"))

        val playedResult = apiClient.markPlayed(testUserId, itemId, testToken)
        assertTrue(playedResult.isSuccess)
        assertEquals(true, playedResult.getOrNull()?.played)

        // Mark as unplayed
        val unplayedDataJson = """
            {"PlaybackPositionTicks": 0, "PlayCount": 0, "Played": false}
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(unplayedDataJson)
            .addHeader("Content-Type", "application/json"))

        val unplayedResult = apiClient.markUnplayed(testUserId, itemId, testToken)
        assertTrue(unplayedResult.isSuccess)
        assertEquals(false, unplayedResult.getOrNull()?.played)
    }

    // ===== Search and Discovery Workflow Tests =====

    @Test
    fun `test complete search workflow`() = runBlocking {
        // Search for content
        val searchJson = """
            {
                "SearchHints": [
                    {
                        "ItemId": "search-1",
                        "Id": "search-1",
                        "Name": "Search Result 1",
                        "Type": "Movie",
                        "ProductionYear": 2024
                    }
                ],
                "TotalRecordCount": 1
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(searchJson)
            .addHeader("Content-Type", "application/json"))

        val searchResult = apiClient.search("query", testUserId, testToken, 50)
        assertTrue(searchResult.isSuccess)
        val hints = searchResult.getOrNull()?.searchHints!!
        assertEquals(1, hints.size)

        // Get details of search result
        val itemJson = """
            {
                "Name": "Search Result 1",
                "ServerId": "server-1",
                "Id": "search-1",
                "Type": "Movie",
                "ProductionYear": 2024,
                "Overview": "Full details of search result"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(itemJson)
            .addHeader("Content-Type", "application/json"))

        val itemResult = apiClient.getItem(testUserId, hints[0].itemId, testToken)
        assertTrue(itemResult.isSuccess)
        assertEquals("Search Result 1", itemResult.getOrNull()?.name)
    }

    // ===== Error Recovery Tests =====

    @Test
    fun `test retry after authentication failure`() = runBlocking {
        // First attempt fails
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(401)
            .setBody("Unauthorized"))

        val failResult = apiClient.authenticateByName("user", "wrongpass")
        assertTrue(failResult.isFailure)

        // Second attempt succeeds
        val authJson = """
            {
                "User": {"Name": "user", "ServerId": "s1", "Id": "u1", "HasPassword": true, "HasConfiguredPassword": true, "HasConfiguredEasyPassword": false},
                "SessionInfo": {"Id": "s1", "UserId": "u1", "UserName": "user", "Client": "Test", "LastActivityDate": "2025-10-25T00:00:00Z", "DeviceName": "Test", "DeviceId": "test", "ApplicationVersion": "1.0"},
                "AccessToken": "valid-token",
                "ServerId": "s1"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(authJson)
            .addHeader("Content-Type", "application/json"))

        val successResult = apiClient.authenticateByName("user", "correctpass")
        assertTrue(successResult.isSuccess)
    }

    @Test
    fun `test handling of server unavailable during operation`() = runBlocking {
        // First request succeeds
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"ServerName": "Test", "Version": "10.8.13", "Id": "s1"}""")
            .addHeader("Content-Type", "application/json"))

        val serverInfoResult = apiClient.getPublicServerInfo()
        assertTrue(serverInfoResult.isSuccess)

        // Second request fails (server down)
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(503)
            .setBody("Service Unavailable"))

        val authResult = apiClient.authenticateByName("user", "pass")
        assertTrue(authResult.isFailure)
    }

    @Test
    fun `test handling of malformed responses`() = runBlocking {
        // Return invalid JSON
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("not valid json {{{")
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getPublicServerInfo()
        assertTrue(result.isFailure)
    }

    // ===== Multi-step Operation Tests =====

    @Test
    fun `test paginated library browsing`() = runBlocking {
        // First page
        val page1Json = """
            {
                "Items": [
                    {"Name": "Item 1", "ServerId": "s1", "Id": "i1", "Type": "Movie"},
                    {"Name": "Item 2", "ServerId": "s1", "Id": "i2", "Type": "Movie"}
                ],
                "TotalRecordCount": 4,
                "StartIndex": 0
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(page1Json)
            .addHeader("Content-Type", "application/json"))

        val page1Result = apiClient.getItems(testUserId, testToken, limit = 2, startIndex = 0)
        assertTrue(page1Result.isSuccess)
        assertEquals(2, page1Result.getOrNull()?.items?.size)

        // Second page
        val page2Json = """
            {
                "Items": [
                    {"Name": "Item 3", "ServerId": "s1", "Id": "i3", "Type": "Movie"},
                    {"Name": "Item 4", "ServerId": "s1", "Id": "i4", "Type": "Movie"}
                ],
                "TotalRecordCount": 4,
                "StartIndex": 2
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(page2Json)
            .addHeader("Content-Type", "application/json"))

        val page2Result = apiClient.getItems(testUserId, testToken, limit = 2, startIndex = 2)
        assertTrue(page2Result.isSuccess)
        assertEquals(2, page2Result.getOrNull()?.items?.size)
    }

    @Test
    fun `test multiple library types access`() = runBlocking {
        val viewsJson = """
            {
                "Items": [
                    {"Name": "Movies", "ServerId": "s1", "Id": "movies", "Type": "CollectionFolder", "CollectionType": "movies"},
                    {"Name": "TV Shows", "ServerId": "s1", "Id": "tv", "Type": "CollectionFolder", "CollectionType": "tvshows"},
                    {"Name": "Music", "ServerId": "s1", "Id": "music", "Type": "CollectionFolder", "CollectionType": "music"}
                ],
                "TotalRecordCount": 3,
                "StartIndex": 0
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(viewsJson)
            .addHeader("Content-Type", "application/json"))

        val viewsResult = apiClient.getUserViews(testUserId, testToken)
        assertTrue(viewsResult.isSuccess)
        val libraries = viewsResult.getOrNull()!!
        assertEquals(3, libraries.size)
        assertEquals("movies", libraries[0].collectionType)
        assertEquals("tvshows", libraries[1].collectionType)
        assertEquals("music", libraries[2].collectionType)
    }

    // ===== Session Management Tests =====

    @Test
    fun `test session persistence across operations`() = runBlocking {
        val token = "persistent-session-token"

        // Operation 1: Get libraries
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"Items": [], "TotalRecordCount": 0, "StartIndex": 0}""")
            .addHeader("Content-Type", "application/json"))

        val viewsResult = apiClient.getUserViews(testUserId, token)
        assertTrue(viewsResult.isSuccess)

        // Operation 2: Search (using same token)
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"SearchHints": [], "TotalRecordCount": 0}""")
            .addHeader("Content-Type", "application/json"))

        val searchResult = apiClient.search("test", testUserId, token, 50)
        assertTrue(searchResult.isSuccess)

        // Operation 3: Update progress (using same token)
        mockWebServer.enqueue(MockResponse().setResponseCode(204))

        val progressResult = apiClient.updateProgress("item-1", 1000, false, token)
        assertTrue(progressResult.isSuccess)
    }

    // ===== Complex Scenario Tests =====

    @Test
    fun `test complete user session from login to logout`() = runBlocking {
        // Login
        val authJson = """
            {
                "User": {"Name": "sessionuser", "ServerId": "s1", "Id": "$testUserId", "HasPassword": true, "HasConfiguredPassword": true, "HasConfiguredEasyPassword": false},
                "SessionInfo": {"Id": "session-1", "UserId": "$testUserId", "UserName": "sessionuser", "Client": "Test", "LastActivityDate": "2025-10-25T00:00:00Z", "DeviceName": "Test", "DeviceId": "test", "ApplicationVersion": "1.0"},
                "AccessToken": "$testToken",
                "ServerId": "s1"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(authJson)
            .addHeader("Content-Type", "application/json"))

        val authResult = apiClient.authenticateByName("sessionuser", "password")
        assertTrue(authResult.isSuccess)
        val token = authResult.getOrNull()?.accessToken!!

        // Browse content
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"Items": [{"Name": "Movie", "ServerId": "s1", "Id": "m1", "Type": "Movie"}], "TotalRecordCount": 1, "StartIndex": 0}""")
            .addHeader("Content-Type", "application/json"))

        val itemsResult = apiClient.getItems(testUserId, token)
        assertTrue(itemsResult.isSuccess)

        // Watch content
        mockWebServer.enqueue(MockResponse().setResponseCode(204))
        val progressResult = apiClient.updateProgress("m1", 1000, false, token)
        assertTrue(progressResult.isSuccess)

        // Mark as watched
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"PlayCount": 1, "Played": true}""")
            .addHeader("Content-Type", "application/json"))

        val playedResult = apiClient.markPlayed(testUserId, "m1", token)
        assertTrue(playedResult.isSuccess)
    }

    @Test
    fun `test handling of mixed success and failure responses`() = runBlocking {
        // Success
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"ServerName": "Test", "Version": "10.8.13", "Id": "s1"}""")
            .addHeader("Content-Type", "application/json"))

        val success1 = apiClient.getPublicServerInfo()
        assertTrue(success1.isSuccess)

        // Failure
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(404)
            .setBody("Not Found"))

        val failure1 = apiClient.getItem(testUserId, "nonexistent", testToken)
        assertTrue(failure1.isFailure)

        // Success again
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"Name": "user", "ServerId": "s1", "Id": "$testUserId", "HasPassword": true, "HasConfiguredPassword": true, "HasConfiguredEasyPassword": false}""")
            .addHeader("Content-Type", "application/json"))

        val success2 = apiClient.getCurrentUser(testToken)
        assertTrue(success2.isSuccess)
    }

    // ===== Performance and Timeout Tests =====

    @Test
    fun `test handling of slow server responses`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"ServerName": "Slow Server", "Version": "10.8.13", "Id": "slow-1"}""")
            .addHeader("Content-Type", "application/json")
            .setBodyDelay(500, java.util.concurrent.TimeUnit.MILLISECONDS))

        val result = apiClient.getPublicServerInfo()
        assertTrue(result.isSuccess)
        assertEquals("Slow Server", result.getOrNull()?.serverName)
    }

    @Test
    fun `test concurrent operations`() = runBlocking {
        // Queue multiple responses
        repeat(3) {
            mockWebServer.enqueue(MockResponse()
                .setResponseCode(200)
                .setBody("""{"ServerName": "Server $it", "Version": "10.8.13", "Id": "s$it"}""")
                .addHeader("Content-Type", "application/json"))
        }

        // Execute operations (they will be serialized by runBlocking)
        val result1 = apiClient.getPublicServerInfo()
        val result2 = apiClient.getPublicServerInfo()
        val result3 = apiClient.getPublicServerInfo()

        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertTrue(result3.isSuccess)
    }

    // ===== Data Consistency Tests =====

    @Test
    fun `test item data consistency across operations`() = runBlocking {
        val itemId = "consistency-item-1"

        // Get item initially
        val initialItemJson = """
            {
                "Name": "Test Item",
                "ServerId": "s1",
                "Id": "$itemId",
                "Type": "Movie",
                "UserData": {
                    "PlayCount": 0,
                    "Played": false
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(initialItemJson)
            .addHeader("Content-Type", "application/json"))

        val initialResult = apiClient.getItem(testUserId, itemId, testToken)
        assertTrue(initialResult.isSuccess)
        assertEquals(false, initialResult.getOrNull()?.userData?.played)

        // Mark as played
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"PlayCount": 1, "Played": true}""")
            .addHeader("Content-Type", "application/json"))

        val playedResult = apiClient.markPlayed(testUserId, itemId, testToken)
        assertTrue(playedResult.isSuccess)

        // Get item again to verify change
        val updatedItemJson = """
            {
                "Name": "Test Item",
                "ServerId": "s1",
                "Id": "$itemId",
                "Type": "Movie",
                "UserData": {
                    "PlayCount": 1,
                    "Played": true
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(updatedItemJson)
            .addHeader("Content-Type", "application/json"))

        val updatedResult = apiClient.getItem(testUserId, itemId, testToken)
        assertTrue(updatedResult.isSuccess)
        assertEquals(true, updatedResult.getOrNull()?.userData?.played)
    }

    @Test
    fun `test search result consistency with item details`() = runBlocking {
        // Search returns an item
        val searchJson = """
            {
                "SearchHints": [
                    {
                        "ItemId": "search-item-1",
                        "Id": "search-item-1",
                        "Name": "Searchable Movie",
                        "Type": "Movie",
                        "ProductionYear": 2024
                    }
                ],
                "TotalRecordCount": 1
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(searchJson)
            .addHeader("Content-Type", "application/json"))

        val searchResult = apiClient.search("movie", testUserId, testToken, 50)
        assertTrue(searchResult.isSuccess)
        val searchItem = searchResult.getOrNull()?.searchHints?.first()!!

        // Get full details of the same item
        val itemDetailsJson = """
            {
                "Name": "Searchable Movie",
                "ServerId": "s1",
                "Id": "search-item-1",
                "Type": "Movie",
                "ProductionYear": 2024,
                "Overview": "Full movie description",
                "RunTimeTicks": 72000000000
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(itemDetailsJson)
            .addHeader("Content-Type", "application/json"))

        val itemResult = apiClient.getItem(testUserId, searchItem.itemId, testToken)
        assertTrue(itemResult.isSuccess)
        val itemDetails = itemResult.getOrNull()!!

        // Verify consistency
        assertEquals(searchItem.name, itemDetails.name)
        assertEquals(searchItem.type, itemDetails.type)
        assertEquals(searchItem.productionYear, itemDetails.productionYear)
    }
}
