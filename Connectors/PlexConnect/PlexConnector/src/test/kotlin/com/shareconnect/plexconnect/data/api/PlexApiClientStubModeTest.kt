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

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for PlexApiClient in stub mode to ensure stub data integration works correctly.
 */
@RunWith(RobolectricTestRunner::class)
class PlexApiClientStubModeTest {

    private lateinit var apiClient: PlexApiClient

    @Before
    fun setup() {
        apiClient = PlexApiClient(isStubMode = true)
    }

    @Test
    fun `test requestPin succeeds in stub mode`() = runTest {
        val result = apiClient.requestPin(PlexTestData.TEST_CLIENT_IDENTIFIER)

        assertTrue("Request should succeed", result.isSuccess)

        val pin = result.getOrThrow()
        assertNotNull("PIN should not be null", pin)
        assertEquals("Client identifier should match", PlexTestData.TEST_CLIENT_IDENTIFIER, pin.clientIdentifier)
        assertNotNull("PIN code should be set", pin.code)
    }

    @Test
    fun `test checkPin succeeds in stub mode`() = runTest {
        // First request a PIN
        val pinResult = apiClient.requestPin(PlexTestData.TEST_CLIENT_IDENTIFIER)
        assertTrue("PIN request should succeed", pinResult.isSuccess)

        val pinId = pinResult.getOrThrow().id

        // Check the PIN
        val checkResult = apiClient.checkPin(pinId)
        assertTrue("Check PIN should succeed", checkResult.isSuccess)

        val pin = checkResult.getOrThrow()
        assertNotNull("PIN should not be null", pin)
        assertEquals("PIN ID should match", pinId, pin.id)
    }

    @Test
    fun `test getServerInfo succeeds in stub mode`() = runTest {
        val result = apiClient.getServerInfo(PlexTestData.TEST_SERVER_URL)

        assertTrue("Request should succeed", result.isSuccess)

        val serverInfo = result.getOrThrow()
        assertNotNull("Server info should not be null", serverInfo)
        assertEquals("Server name should match", PlexTestData.TEST_SERVER_NAME, serverInfo.name)
        assertEquals("Machine identifier should match", PlexTestData.TEST_MACHINE_IDENTIFIER, serverInfo.machineIdentifier)
    }

    @Test
    fun `test getLibraries succeeds in stub mode`() = runTest {
        val result = apiClient.getLibraries(PlexTestData.TEST_SERVER_URL, PlexTestData.TEST_AUTH_TOKEN)

        assertTrue("Request should succeed", result.isSuccess)

        val libraries = result.getOrThrow()
        assertNotNull("Libraries should not be null", libraries)
        assertEquals("Should return 3 libraries", 3, libraries.size)
        assertTrue("Should contain Movies library", libraries.any { it.title == "Movies" })
        assertTrue("Should contain TV Shows library", libraries.any { it.title == "TV Shows" })
        assertTrue("Should contain Music library", libraries.any { it.title == "Music" })
    }

    @Test
    fun `test getLibraryItems succeeds in stub mode`() = runTest {
        val result = apiClient.getLibraryItems(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            sectionKey = "1",
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Request should succeed", result.isSuccess)

        val items = result.getOrThrow()
        assertNotNull("Items should not be null", items)
        assertTrue("Should return at least 2 movies", items.size >= 2)
        assertTrue("Should contain The Matrix", items.any { it.title == "The Matrix" })
        assertTrue("Should contain Inception", items.any { it.title == "Inception" })
    }

    @Test
    fun `test getMediaItem succeeds in stub mode`() = runTest {
        val result = apiClient.getMediaItem(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            ratingKey = "1001",
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Request should succeed", result.isSuccess)

        val item = result.getOrThrow()
        assertNotNull("Item should not be null", item)
        assertEquals("Should be The Matrix", "The Matrix", item?.title)
        assertEquals("Year should be 1999", 1999, item?.year)
    }

    @Test
    fun `test getMediaChildren succeeds in stub mode`() = runTest {
        val result = apiClient.getMediaChildren(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            ratingKey = "2001",
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Request should succeed", result.isSuccess)

        val children = result.getOrThrow()
        assertNotNull("Children should not be null", children)
        assertTrue("Should return at least 1 episode", children.size >= 1)
    }

    @Test
    fun `test markAsPlayed succeeds in stub mode`() = runTest {
        val result = apiClient.markAsPlayed(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            key = "/library/metadata/1001",
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Request should succeed", result.isSuccess)
    }

    @Test
    fun `test markAsUnplayed succeeds in stub mode`() = runTest {
        val result = apiClient.markAsUnplayed(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            key = "/library/metadata/1001",
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Request should succeed", result.isSuccess)
    }

    @Test
    fun `test updateProgress succeeds in stub mode`() = runTest {
        val result = apiClient.updateProgress(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            key = "/library/metadata/1001",
            time = 3600000L,
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Request should succeed", result.isSuccess)
    }

    @Test
    fun `test search succeeds in stub mode`() = runTest {
        val result = apiClient.search(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            query = "Matrix",
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Request should succeed", result.isSuccess)

        val results = result.getOrThrow()
        assertNotNull("Results should not be null", results)
        assertTrue("Should return at least 1 result", results.size >= 1)
        assertTrue("Should contain The Matrix", results.any { it.title?.contains("Matrix") == true })
    }

    @Test
    fun `test search with non-matching query returns empty results`() = runTest {
        val result = apiClient.search(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            query = "xyz-nonexistent-query-xyz",
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        assertTrue("Request should succeed", result.isSuccess)

        val results = result.getOrThrow()
        assertNotNull("Results should not be null", results)
        assertEquals("Should return no results", 0, results.size)
    }
}
