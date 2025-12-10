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

package com.shareconnect.plexconnect.data.api

import com.shareconnect.plexconnect.data.model.LibraryType
import com.shareconnect.plexconnect.data.model.MediaType
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for PlexApiStubService to ensure stub data is returned correctly.
 */
class PlexApiStubServiceTest {

    private lateinit var stubService: PlexApiStubService

    @Before
    fun setup() {
        stubService = PlexApiStubService()
        stubService.resetState()
    }

    @After
    fun tearDown() {
        stubService.resetState()
    }

    // Authentication Tests

    @Test
    fun `test requestPin returns valid PIN response`() = runTest {
        val request = PlexTestData.testPinRequest
        val response = stubService.requestPin(request)

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val pin = response.body()!!
        assertEquals("Client identifier should match", request.clientIdentifier, pin.clientIdentifier)
        assertEquals("Product should match", request.product, pin.product)
        assertNotNull("PIN code should be set", pin.code)
        assertNull("Auth token should be null initially", pin.authToken)
        assertFalse("PIN should not be trusted initially", pin.trusted)
    }

    @Test
    fun `test checkPin returns unauthorized PIN initially`() = runTest {
        // Request a PIN first
        val request = PlexTestData.testPinRequest
        val requestResponse = stubService.requestPin(request)
        val pinId = requestResponse.body()!!.id

        // Check PIN (should be unauthorized initially)
        val checkResponse = stubService.checkPin(pinId)

        assertTrue("Response should be successful", checkResponse.isSuccessful)
        assertNotNull("Response body should not be null", checkResponse.body())

        val pin = checkResponse.body()!!
        assertNull("Auth token should be null", pin.authToken)
        assertFalse("PIN should not be trusted", pin.trusted)
    }

    @Test
    fun `test checkPin returns authorized PIN after enablePinAuthorization`() = runTest {
        // Request a PIN first
        val request = PlexTestData.testPinRequest
        val requestResponse = stubService.requestPin(request)
        val pinId = requestResponse.body()!!.id

        // Enable authorization
        stubService.enablePinAuthorization()

        // Check PIN (should be authorized now)
        val checkResponse = stubService.checkPin(pinId)

        assertTrue("Response should be successful", checkResponse.isSuccessful)
        assertNotNull("Response body should not be null", checkResponse.body())

        val pin = checkResponse.body()!!
        assertNotNull("Auth token should be set", pin.authToken)
        assertEquals("Auth token should match test token", PlexTestData.TEST_AUTH_TOKEN, pin.authToken)
        assertTrue("PIN should be trusted", pin.trusted)
    }

    @Test
    fun `test checkPin returns 404 for non-existent PIN`() = runTest {
        val response = stubService.checkPin(999999L)

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 404", 404, response.code())
    }

    // Server Discovery Tests

    @Test
    fun `test getServerInfo returns valid server information`() = runTest {
        val response = stubService.getServerInfo(PlexTestData.TEST_SERVER_URL)

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val serverInfo = response.body()!!
        assertEquals("Server name should match", PlexTestData.TEST_SERVER_NAME, serverInfo.name)
        assertEquals("Machine identifier should match", PlexTestData.TEST_MACHINE_IDENTIFIER, serverInfo.machineIdentifier)
        assertNotNull("Version should be set", serverInfo.version)
        assertEquals("Port should be 32400", 32400, serverInfo.port)
    }

    // Library Tests

    @Test
    fun `test getLibraries returns all test libraries`() = runTest {
        val response = stubService.getLibraries(PlexTestData.TEST_SERVER_URL, PlexTestData.TEST_AUTH_TOKEN)

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val libraryResponse = response.body()!!
        val libraries = libraryResponse.mediaContainer?.Directory ?: emptyList()

        assertEquals("Should return 3 libraries", 3, libraries.size)
        assertTrue("Should contain Movies library", libraries.any { it.title == "Movies" && it.type == LibraryType.MOVIE })
        assertTrue("Should contain TV Shows library", libraries.any { it.title == "TV Shows" && it.type == LibraryType.SHOW })
        assertTrue("Should contain Music library", libraries.any { it.title == "Music" && it.type == LibraryType.MUSIC })
    }

    @Test
    fun `test getLibraries returns 401 for invalid token`() = runTest {
        val response = stubService.getLibraries(PlexTestData.TEST_SERVER_URL, "")

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 401", 401, response.code())
    }

    // Library Items Tests

    @Test
    fun `test getLibraryItems returns movies for section 1`() = runTest {
        val response = stubService.getLibraryItems(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            sectionKey = "1",
            token = PlexTestData.TEST_AUTH_TOKEN,
            limit = 50,
            offset = 0
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val mediaResponse = response.body()!!
        val items = mediaResponse.mediaContainer?.Metadata ?: emptyList()

        assertTrue("Should return at least 2 movies", items.size >= 2)
        assertTrue("All items should be movies", items.all { it.type == MediaType.MOVIE })
        assertTrue("Should contain The Matrix", items.any { it.title == "The Matrix" })
        assertTrue("Should contain Inception", items.any { it.title == "Inception" })
    }

    @Test
    fun `test getLibraryItems returns TV shows for section 2`() = runTest {
        val response = stubService.getLibraryItems(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            sectionKey = "2",
            token = PlexTestData.TEST_AUTH_TOKEN,
            limit = 50,
            offset = 0
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val mediaResponse = response.body()!!
        val items = mediaResponse.mediaContainer?.Metadata ?: emptyList()

        assertTrue("Should return at least 1 TV show", items.size >= 1)
        assertTrue("All items should be shows", items.all { it.type == MediaType.SHOW })
        assertTrue("Should contain Breaking Bad", items.any { it.title == "Breaking Bad" })
    }

    @Test
    fun `test getLibraryItems respects pagination`() = runTest {
        val response = stubService.getLibraryItems(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            sectionKey = "1",
            token = PlexTestData.TEST_AUTH_TOKEN,
            limit = 1,
            offset = 0
        )

        assertTrue("Response should be successful", response.isSuccessful)
        val items = response.body()?.mediaContainer?.Metadata ?: emptyList()

        assertEquals("Should return exactly 1 item", 1, items.size)
    }

    @Test
    fun `test getLibraryItems returns 401 for invalid token`() = runTest {
        val response = stubService.getLibraryItems(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            sectionKey = "1",
            token = "",
            limit = 50,
            offset = 0
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 401", 401, response.code())
    }

    // Media Item Tests

    @Test
    fun `test getMediaItem returns specific movie`() = runTest {
        val response = stubService.getMediaItem(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            ratingKey = "1001",
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val mediaResponse = response.body()!!
        val items = mediaResponse.mediaContainer?.Metadata ?: emptyList()

        assertEquals("Should return exactly 1 item", 1, items.size)

        val movie = items.first()
        assertEquals("Should be The Matrix", "The Matrix", movie.title)
        assertEquals("Type should be movie", MediaType.MOVIE, movie.type)
        assertEquals("Year should be 1999", 1999, movie.year)
    }

    @Test
    fun `test getMediaItem returns 404 for non-existent item`() = runTest {
        val response = stubService.getMediaItem(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            ratingKey = "999999",
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 404", 404, response.code())
    }

    // Media Children Tests

    @Test
    fun `test getMediaChildren returns episodes for TV show`() = runTest {
        val response = stubService.getMediaChildren(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            ratingKey = "2001",
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val mediaResponse = response.body()!!
        val children = mediaResponse.mediaContainer?.Metadata ?: emptyList()

        assertTrue("Should return at least 1 episode", children.size >= 1)
        assertTrue("All children should be episodes", children.all { it.type == MediaType.EPISODE })
    }

    // Playback Status Tests

    @Test
    fun `test markAsPlayed succeeds with valid token`() = runTest {
        val response = stubService.markAsPlayed(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            key = "/library/metadata/1001",
            identifier = "com.plexapp.plugins.library",
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Response should be successful", response.isSuccessful)
    }

    @Test
    fun `test markAsPlayed returns 401 for invalid token`() = runTest {
        val response = stubService.markAsPlayed(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            key = "/library/metadata/1001",
            identifier = "com.plexapp.plugins.library",
            token = ""
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 401", 401, response.code())
    }

    @Test
    fun `test markAsUnplayed succeeds with valid token`() = runTest {
        val response = stubService.markAsUnplayed(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            key = "/library/metadata/1001",
            identifier = "com.plexapp.plugins.library",
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Response should be successful", response.isSuccessful)
    }

    @Test
    fun `test updateProgress succeeds with valid token`() = runTest {
        val response = stubService.updateProgress(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            key = "/library/metadata/1001",
            identifier = "com.plexapp.plugins.library",
            time = 3600000L,
            state = "playing",
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Response should be successful", response.isSuccessful)
    }

    // Search Tests

    @Test
    fun `test search returns matching results for title query`() = runTest {
        val response = stubService.search(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            query = "Matrix",
            limit = 50,
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val searchResponse = response.body()!!
        val results = searchResponse.mediaContainer?.Metadata ?: emptyList()

        assertTrue("Should return at least 1 result", results.size >= 1)
        assertTrue("Should contain The Matrix", results.any { it.title.contains("Matrix") })
    }

    @Test
    fun `test search returns matching results for summary query`() = runTest {
        val response = stubService.search(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            query = "chemistry teacher",
            limit = 50,
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val searchResponse = response.body()!!
        val results = searchResponse.mediaContainer?.Metadata ?: emptyList()

        assertTrue("Should return at least 1 result", results.size >= 1)
        assertTrue("Should contain Breaking Bad", results.any { it.title == "Breaking Bad" })
    }

    @Test
    fun `test search respects limit parameter`() = runTest {
        val response = stubService.search(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            query = "the",
            limit = 1,
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Response should be successful", response.isSuccessful)
        val results = response.body()?.mediaContainer?.Metadata ?: emptyList()

        assertTrue("Should return at most 1 result", results.size <= 1)
    }

    @Test
    fun `test search returns empty results for non-matching query`() = runTest {
        val response = stubService.search(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            query = "xyz-nonexistent-query-xyz",
            limit = 50,
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Response should be successful", response.isSuccessful)
        val results = response.body()?.mediaContainer?.Metadata ?: emptyList()

        assertEquals("Should return no results", 0, results.size)
    }

    @Test
    fun `test search returns 401 for invalid token`() = runTest {
        val response = stubService.search(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            query = "Matrix",
            limit = 50,
            token = ""
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 401", 401, response.code())
    }
}
