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

import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for MotrixApiStubService to ensure stub data is returned correctly.
 */
class MotrixApiStubServiceTest {

    private lateinit var stubService: MotrixApiStubService

    @Before
    fun setup() {
        stubService = MotrixApiStubService(requireAuth = false)
        MotrixApiStubService.resetState()
    }

    @After
    fun tearDown() {
        MotrixApiStubService.resetState()
    }

    // Server Information Tests

    @Test
    fun `test getVersion returns valid version`() = runTest {
        val response = stubService.getVersion()

        assertNotNull("Response should not be null", response)
        assertEquals("JSON-RPC version should be 2.0", "2.0", response.jsonrpc)
        assertNull("Error should be null", response.error)
        assertNotNull("Result should not be null", response.result)

        val version = response.result!!
        assertEquals("Version should match", MotrixTestData.testVersion.version, version.version)
        assertNotNull("Enabled features should not be null", version.enabledFeatures)
        assertTrue("Should have enabled features", version.enabledFeatures!!.isNotEmpty())
    }

    @Test
    fun `test getGlobalStat returns valid statistics`() = runTest {
        val response = stubService.getGlobalStat()

        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
        assertNotNull("Result should not be null", response.result)

        val stat = response.result!!
        assertTrue("Should have active downloads", stat.numActive.toInt() >= 0)
        assertTrue("Should have waiting downloads", stat.numWaiting.toInt() >= 0)
        assertTrue("Should have stopped downloads", stat.numStopped.toInt() >= 0)
    }

    // Download Management Tests

    @Test
    fun `test addUri creates new download`() = runTest {
        val testUri = "https://example.com/file.zip"
        val response = stubService.addUri(listOf(testUri), null)

        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
        assertNotNull("Result should not be null", response.result)

        val gid = response.result!!
        assertTrue("GID should not be empty", gid.isNotEmpty())

        // Verify download was created
        val statusResponse = stubService.tellStatus(gid)
        assertNotNull("Status should be returned", statusResponse.result)
        assertEquals("Status should be waiting", "waiting", statusResponse.result!!.status)
    }

    @Test
    fun `test addUri with options creates download with options`() = runTest {
        val testUri = "https://example.com/file.zip"
        val options = MotrixTestData.testDownloadOptions

        val response = stubService.addUri(listOf(testUri), options)

        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
        assertNotNull("GID should be returned", response.result)

        val gid = response.result!!
        val statusResponse = stubService.tellStatus(gid)
        val download = statusResponse.result!!

        assertEquals("Directory should match", options.directory, download.directory)
    }

    @Test
    fun `test tellStatus returns download info`() = runTest {
        val gid = MotrixTestData.testDownloadActiveHttp.gid
        val response = stubService.tellStatus(gid)

        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
        assertNotNull("Result should not be null", response.result)

        val download = response.result!!
        assertEquals("GID should match", gid, download.gid)
        assertEquals("Status should be active", "active", download.status)
    }

    @Test
    fun `test tellStatus returns error for invalid GID`() = runTest {
        val response = stubService.tellStatus("invalid-gid")

        assertNotNull("Response should not be null", response)
        assertNull("Result should be null", response.result)
        assertNotNull("Error should not be null", response.error)

        assertEquals("Error code should match", MotrixTestData.RpcErrorCodes.GID_NOT_FOUND, response.error!!.code)
    }

    @Test
    fun `test tellActive returns only active downloads`() = runTest {
        val response = stubService.tellActive()

        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
        assertNotNull("Result should not be null", response.result)

        val downloads = response.result!!
        assertTrue("Should have active downloads", downloads.isNotEmpty())
        downloads.forEach { download ->
            assertEquals("All downloads should be active", "active", download.status)
        }
    }

    @Test
    fun `test tellWaiting returns only waiting downloads`() = runTest {
        val response = stubService.tellWaiting(0, 100)

        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
        assertNotNull("Result should not be null", response.result)

        val downloads = response.result!!
        downloads.forEach { download ->
            assertEquals("All downloads should be waiting", "waiting", download.status)
        }
    }

    @Test
    fun `test tellStopped returns stopped downloads`() = runTest {
        val response = stubService.tellStopped(0, 100)

        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
        assertNotNull("Result should not be null", response.result)

        val downloads = response.result!!
        downloads.forEach { download ->
            assertTrue("Download should be stopped",
                download.status in listOf("paused", "complete", "error", "removed"))
        }
    }

    // Download Control Tests

    @Test
    fun `test pause changes download status`() = runTest {
        val gid = MotrixTestData.testDownloadActiveHttp.gid

        val pauseResponse = stubService.pause(gid)
        assertNotNull("Pause response should not be null", pauseResponse)
        assertNull("Error should be null", pauseResponse.error)

        val statusResponse = stubService.tellStatus(gid)
        assertEquals("Status should be paused", "paused", statusResponse.result!!.status)
        assertEquals("Download speed should be zero", "0", statusResponse.result!!.downloadSpeed)
    }

    @Test
    fun `test pauseAll pauses all active downloads`() = runTest {
        val pauseResponse = stubService.pauseAll()
        assertNotNull("Response should not be null", pauseResponse)
        assertNull("Error should be null", pauseResponse.error)

        val activeResponse = stubService.tellActive()
        assertTrue("Should have no active downloads", activeResponse.result!!.isEmpty())
    }

    @Test
    fun `test unpause resumes paused download`() = runTest {
        val gid = MotrixTestData.testDownloadPaused.gid

        val unpauseResponse = stubService.unpause(gid)
        assertNotNull("Unpause response should not be null", unpauseResponse)
        assertNull("Error should be null", unpauseResponse.error)

        val statusResponse = stubService.tellStatus(gid)
        assertEquals("Status should be active", "active", statusResponse.result!!.status)
        assertTrue("Download speed should be positive", statusResponse.result!!.downloadSpeed.toLong() > 0)
    }

    @Test
    fun `test unpauseAll resumes all paused downloads`() = runTest {
        // First pause all
        stubService.pauseAll()

        // Then unpause all
        val unpauseResponse = stubService.unpauseAll()
        assertNotNull("Response should not be null", unpauseResponse)
        assertNull("Error should be null", unpauseResponse.error)

        val activeResponse = stubService.tellActive()
        assertTrue("Should have active downloads", activeResponse.result!!.isNotEmpty())
    }

    @Test
    fun `test remove marks download as removed`() = runTest {
        val gid = MotrixTestData.testDownloadActiveHttp.gid

        val removeResponse = stubService.remove(gid)
        assertNotNull("Response should not be null", removeResponse)
        assertNull("Error should be null", removeResponse.error)

        val statusResponse = stubService.tellStatus(gid)
        assertEquals("Status should be removed", "removed", statusResponse.result!!.status)
    }

    @Test
    fun `test forceRemove marks download as removed`() = runTest {
        val gid = MotrixTestData.testDownloadActiveHttp.gid

        val removeResponse = stubService.forceRemove(gid)
        assertNotNull("Response should not be null", removeResponse)
        assertNull("Error should be null", removeResponse.error)

        val statusResponse = stubService.tellStatus(gid)
        assertEquals("Status should be removed", "removed", statusResponse.result!!.status)
    }

    @Test
    fun `test removeDownloadResult deletes download`() = runTest {
        val gid = MotrixTestData.testDownloadComplete.gid

        val removeResponse = stubService.removeDownloadResult(gid)
        assertNotNull("Response should not be null", removeResponse)
        assertNull("Error should be null", removeResponse.error)

        val statusResponse = stubService.tellStatus(gid)
        assertNotNull("Error should be returned", statusResponse.error)
        assertEquals("Error code should be GID_NOT_FOUND", MotrixTestData.RpcErrorCodes.GID_NOT_FOUND, statusResponse.error!!.code)
    }

    // Options Management Tests

    @Test
    fun `test getGlobalOption returns options`() = runTest {
        val response = stubService.getGlobalOption()

        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
        assertNotNull("Result should not be null", response.result)

        val options = response.result!!
        assertTrue("Should have options", options.isNotEmpty())
        assertTrue("Should have dir option", options.containsKey("dir"))
    }

    @Test
    fun `test changeGlobalOption succeeds`() = runTest {
        val options = mapOf("max-download-limit" to "5M")

        val response = stubService.changeGlobalOption(options)

        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
        assertEquals("Result should be OK", "OK", response.result)
    }

    @Test
    fun `test getOption returns download options`() = runTest {
        val gid = MotrixTestData.testDownloadActiveHttp.gid

        val response = stubService.getOption(gid)

        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
        assertNotNull("Result should not be null", response.result)

        val options = response.result!!
        assertTrue("Should have options", options.isNotEmpty())
    }

    @Test
    fun `test getOption returns error for invalid GID`() = runTest {
        val response = stubService.getOption("invalid-gid")

        assertNotNull("Response should not be null", response)
        assertNull("Result should be null", response.result)
        assertNotNull("Error should not be null", response.error)
        assertEquals("Error code should be GID_NOT_FOUND", MotrixTestData.RpcErrorCodes.GID_NOT_FOUND, response.error!!.code)
    }

    @Test
    fun `test changeOption succeeds for valid GID`() = runTest {
        val gid = MotrixTestData.testDownloadActiveHttp.gid
        val options = mapOf("max-download-limit" to "2M")

        val response = stubService.changeOption(gid, options)

        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
        assertEquals("Result should be OK", "OK", response.result)
    }

    // Session Management Tests

    @Test
    fun `test purgeDownloadResult removes stopped downloads`() = runTest {
        val initialStoppedResponse = stubService.tellStopped(0, 100)
        val initialCount = initialStoppedResponse.result!!.size

        val purgeResponse = stubService.purgeDownloadResult()
        assertNotNull("Response should not be null", purgeResponse)
        assertNull("Error should be null", purgeResponse.error)

        val finalStoppedResponse = stubService.tellStopped(0, 100)
        val finalCount = finalStoppedResponse.result!!.size

        assertTrue("Should have removed stopped downloads", finalCount < initialCount)
    }

    @Test
    fun `test saveSession succeeds`() = runTest {
        val response = stubService.saveSession()

        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
        assertEquals("Result should be OK", "OK", response.result)
    }

    @Test
    fun `test shutdown succeeds`() = runTest {
        val response = stubService.shutdown()

        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
        assertEquals("Result should be OK", "OK", response.result)
    }

    @Test
    fun `test forceShutdown succeeds`() = runTest {
        val response = stubService.forceShutdown()

        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
        assertEquals("Result should be OK", "OK", response.result)
    }

    // Authentication Tests

    @Test
    fun `test authentication required when enabled`() = runTest {
        val authService = MotrixApiStubService(requireAuth = true)

        try {
            authService.getVersion()
            fail("Should throw SecurityException")
        } catch (e: SecurityException) {
            assertTrue("Should be unauthorized", e.message!!.contains("Unauthorized"))
        }
    }

    @Test
    fun `test authentication succeeds after authenticate`() = runTest {
        val authService = MotrixApiStubService(requireAuth = true)
        authService.authenticate()

        val response = authService.getVersion()
        assertNotNull("Response should not be null", response)
        assertNull("Error should be null", response.error)
    }

    // State Management Tests

    @Test
    fun `test resetState restores initial state`() = runTest {
        // Modify state
        val addResponse = stubService.addUri(listOf("https://example.com/test.zip"), null)
        val newGid = addResponse.result!!

        // Reset state
        MotrixApiStubService.resetState()

        // Verify new download is gone
        val statusResponse = stubService.tellStatus(newGid)
        assertNotNull("Error should be returned", statusResponse.error)

        // Verify test data is back
        val testGidResponse = stubService.tellStatus(MotrixTestData.testDownloadActiveHttp.gid)
        assertNull("Error should be null", testGidResponse.error)
        assertNotNull("Test download should exist", testGidResponse.result)
    }

    // State Transition Tests

    @Test
    fun `test download state transition from active to paused to active`() = runTest {
        val gid = MotrixTestData.testDownloadActiveHttp.gid

        // Verify initial state
        var statusResponse = stubService.tellStatus(gid)
        assertEquals("Should start as active", "active", statusResponse.result!!.status)

        // Pause
        stubService.pause(gid)
        statusResponse = stubService.tellStatus(gid)
        assertEquals("Should be paused", "paused", statusResponse.result!!.status)
        assertEquals("Speed should be zero", "0", statusResponse.result!!.downloadSpeed)

        // Resume
        stubService.unpause(gid)
        statusResponse = stubService.tellStatus(gid)
        assertEquals("Should be active again", "active", statusResponse.result!!.status)
        assertTrue("Speed should be positive", statusResponse.result!!.downloadSpeed.toLong() > 0)
    }

    @Test
    fun `test waiting download pagination`() = runTest {
        // Add multiple waiting downloads
        repeat(5) {
            stubService.addUri(listOf("https://example.com/file$it.zip"), null)
        }

        // Test pagination
        val page1 = stubService.tellWaiting(0, 2)
        assertEquals("First page should have 2 items", 2, page1.result!!.size)

        val page2 = stubService.tellWaiting(2, 2)
        assertEquals("Second page should have 2 items", 2, page2.result!!.size)

        // Verify different downloads
        assertNotEquals("Pages should have different downloads",
            page1.result!!.first().gid, page2.result!!.first().gid)
    }
}
