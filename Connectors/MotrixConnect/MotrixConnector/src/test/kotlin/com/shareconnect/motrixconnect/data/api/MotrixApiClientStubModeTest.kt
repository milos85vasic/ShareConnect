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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for MotrixApiClient in stub mode to ensure stub data integration works correctly.
 */
@RunWith(RobolectricTestRunner::class)
class MotrixApiClientStubModeTest {

    private lateinit var apiClient: MotrixApiClient

    @Before
    fun setup() {
        apiClient = MotrixApiClient(
            serverUrl = MotrixTestData.TEST_SERVER_URL,
            secret = null,
            isStubMode = true
        )
        MotrixApiStubService.resetState()
    }

    @Test
    fun `test getVersion succeeds in stub mode`() = runTest {
        val result = apiClient.getVersion()

        assertTrue("Request should succeed", result.isSuccess)

        val version = result.getOrThrow()
        assertNotNull("Version should not be null", version)
        assertEquals("Version should match", MotrixTestData.testVersion.version, version.version)
        assertTrue("Should have enabled features", version.enabledFeatures!!.isNotEmpty())
    }

    @Test
    fun `test getGlobalStat succeeds in stub mode`() = runTest {
        val result = apiClient.getGlobalStat()

        assertTrue("Request should succeed", result.isSuccess)

        val stat = result.getOrThrow()
        assertNotNull("Stat should not be null", stat)
        assertTrue("Should have download speed", stat.downloadSpeed.toLong() >= 0)
        assertTrue("Should have upload speed", stat.uploadSpeed.toLong() >= 0)
        assertTrue("Should have active count", stat.numActive.toInt() >= 0)
    }

    @Test
    fun `test addUri creates new download in stub mode`() = runTest {
        val testUri = "https://example.com/test-file.zip"

        val result = apiClient.addUri(testUri, null)

        assertTrue("Request should succeed", result.isSuccess)

        val gid = result.getOrThrow()
        assertNotNull("GID should not be null", gid)
        assertTrue("GID should not be empty", gid.isNotEmpty())

        // Verify download was created
        val statusResult = apiClient.tellStatus(gid)
        assertTrue("Status request should succeed", statusResult.isSuccess)
        assertEquals("Status should be waiting", "waiting", statusResult.getOrThrow().status)
    }

    @Test
    fun `test addUri with options in stub mode`() = runTest {
        val testUri = "https://example.com/video.mp4"
        val options = MotrixTestData.testDownloadOptions

        val result = apiClient.addUri(testUri, options)

        assertTrue("Request should succeed", result.isSuccess)

        val gid = result.getOrThrow()
        val statusResult = apiClient.tellStatus(gid)
        val download = statusResult.getOrThrow()

        assertEquals("Directory should match", options.directory, download.directory)
    }

    @Test
    fun `test addUris with multiple URIs in stub mode`() = runTest {
        val uris = listOf(
            "https://example.com/mirror1/file.zip",
            "https://example.com/mirror2/file.zip"
        )

        val result = apiClient.addUris(uris, null)

        assertTrue("Request should succeed", result.isSuccess)

        val gid = result.getOrThrow()
        assertNotNull("GID should not be null", gid)

        val statusResult = apiClient.tellStatus(gid)
        assertTrue("Status request should succeed", statusResult.isSuccess)
    }

    @Test
    fun `test tellStatus returns download info in stub mode`() = runTest {
        val gid = MotrixTestData.testDownloadActiveHttp.gid

        val result = apiClient.tellStatus(gid)

        assertTrue("Request should succeed", result.isSuccess)

        val download = result.getOrThrow()
        assertEquals("GID should match", gid, download.gid)
        assertEquals("Status should be active", "active", download.status)
        assertNotNull("Files should not be null", download.files)
    }

    @Test
    fun `test tellStatus fails for invalid GID in stub mode`() = runTest {
        val result = apiClient.tellStatus("invalid-gid-12345")

        assertTrue("Request should fail", result.isFailure)
        assertTrue("Error should mention not found", result.exceptionOrNull()!!.message!!.contains("not found"))
    }

    @Test
    fun `test tellActive returns active downloads in stub mode`() = runTest {
        val result = apiClient.tellActive()

        assertTrue("Request should succeed", result.isSuccess)

        val downloads = result.getOrThrow()
        assertTrue("Should have active downloads", downloads.isNotEmpty())
        downloads.forEach { download ->
            assertEquals("All downloads should be active", "active", download.status)
        }
    }

    @Test
    fun `test tellWaiting returns waiting downloads in stub mode`() = runTest {
        val result = apiClient.tellWaiting()

        assertTrue("Request should succeed", result.isSuccess)

        val downloads = result.getOrThrow()
        downloads.forEach { download ->
            assertEquals("All downloads should be waiting", "waiting", download.status)
        }
    }

    @Test
    fun `test tellStopped returns stopped downloads in stub mode`() = runTest {
        val result = apiClient.tellStopped()

        assertTrue("Request should succeed", result.isSuccess)

        val downloads = result.getOrThrow()
        downloads.forEach { download ->
            assertTrue("Download should be stopped",
                download.status in listOf("paused", "complete", "error", "removed"))
        }
    }

    @Test
    fun `test pause download in stub mode`() = runTest {
        val gid = MotrixTestData.testDownloadActiveHttp.gid

        val pauseResult = apiClient.pause(gid)
        assertTrue("Pause should succeed", pauseResult.isSuccess)

        val statusResult = apiClient.tellStatus(gid)
        val download = statusResult.getOrThrow()

        assertEquals("Status should be paused", "paused", download.status)
        assertEquals("Download speed should be zero", "0", download.downloadSpeed)
    }

    @Test
    fun `test pauseAll in stub mode`() = runTest {
        val result = apiClient.pauseAll()

        assertTrue("Request should succeed", result.isSuccess)

        val activeResult = apiClient.tellActive()
        assertTrue("Should have no active downloads", activeResult.getOrThrow().isEmpty())
    }

    @Test
    fun `test unpause download in stub mode`() = runTest {
        val gid = MotrixTestData.testDownloadPaused.gid

        val unpauseResult = apiClient.unpause(gid)
        assertTrue("Unpause should succeed", unpauseResult.isSuccess)

        val statusResult = apiClient.tellStatus(gid)
        val download = statusResult.getOrThrow()

        assertEquals("Status should be active", "active", download.status)
        assertTrue("Download speed should be positive", download.downloadSpeed.toLong() > 0)
    }

    @Test
    fun `test unpauseAll in stub mode`() = runTest {
        // First pause all
        apiClient.pauseAll()

        // Then unpause all
        val result = apiClient.unpauseAll()
        assertTrue("Request should succeed", result.isSuccess)

        val activeResult = apiClient.tellActive()
        assertTrue("Should have active downloads", activeResult.getOrThrow().isNotEmpty())
    }

    @Test
    fun `test remove download in stub mode`() = runTest {
        val gid = MotrixTestData.testDownloadActiveHttp.gid

        val removeResult = apiClient.remove(gid)
        assertTrue("Remove should succeed", removeResult.isSuccess)

        val statusResult = apiClient.tellStatus(gid)
        assertEquals("Status should be removed", "removed", statusResult.getOrThrow().status)
    }

    @Test
    fun `test forceRemove download in stub mode`() = runTest {
        val gid = MotrixTestData.testDownloadActiveTorrent.gid

        val removeResult = apiClient.forceRemove(gid)
        assertTrue("Force remove should succeed", removeResult.isSuccess)

        val statusResult = apiClient.tellStatus(gid)
        assertEquals("Status should be removed", "removed", statusResult.getOrThrow().status)
    }

    @Test
    fun `test removeDownloadResult deletes download in stub mode`() = runTest {
        val gid = MotrixTestData.testDownloadComplete.gid

        val removeResult = apiClient.removeDownloadResult(gid)
        assertTrue("Remove result should succeed", removeResult.isSuccess)

        val statusResult = apiClient.tellStatus(gid)
        assertTrue("Status request should fail", statusResult.isFailure)
    }

    @Test
    fun `test getGlobalOption in stub mode`() = runTest {
        val result = apiClient.getGlobalOption()

        assertTrue("Request should succeed", result.isSuccess)

        val options = result.getOrThrow()
        assertTrue("Should have options", options.isNotEmpty())
        assertTrue("Should have dir option", options.containsKey("dir"))
    }

    @Test
    fun `test changeGlobalOption in stub mode`() = runTest {
        val options = mapOf(
            "max-download-limit" to "10M",
            "max-upload-limit" to "5M"
        )

        val result = apiClient.changeGlobalOption(options)

        assertTrue("Request should succeed", result.isSuccess)
    }

    @Test
    fun `test getOption in stub mode`() = runTest {
        val gid = MotrixTestData.testDownloadActiveHttp.gid

        val result = apiClient.getOption(gid)

        assertTrue("Request should succeed", result.isSuccess)

        val options = result.getOrThrow()
        assertTrue("Should have options", options.isNotEmpty())
    }

    @Test
    fun `test changeOption in stub mode`() = runTest {
        val gid = MotrixTestData.testDownloadActiveHttp.gid
        val options = mapOf("max-download-limit" to "3M")

        val result = apiClient.changeOption(gid, options)

        assertTrue("Request should succeed", result.isSuccess)
    }

    @Test
    fun `test purgeDownloadResult in stub mode`() = runTest {
        val result = apiClient.purgeDownloadResult()

        assertTrue("Request should succeed", result.isSuccess)

        val stoppedResult = apiClient.tellStopped()
        // After purge, there should be fewer stopped downloads
        assertTrue("Stopped list should be accessible", stoppedResult.isSuccess)
    }

    @Test
    fun `test saveSession in stub mode`() = runTest {
        val result = apiClient.saveSession()

        assertTrue("Request should succeed", result.isSuccess)
    }

    @Test
    fun `test shutdown in stub mode`() = runTest {
        val result = apiClient.shutdown()

        assertTrue("Request should succeed", result.isSuccess)
    }

    @Test
    fun `test forceShutdown in stub mode`() = runTest {
        val result = apiClient.forceShutdown()

        assertTrue("Request should succeed", result.isSuccess)
    }

    @Test
    fun `test complete download workflow in stub mode`() = runTest {
        // 1. Add a new download
        val addResult = apiClient.addUri("https://example.com/movie.mp4", MotrixTestData.testDownloadOptions)
        assertTrue("Add should succeed", addResult.isSuccess)

        val gid = addResult.getOrThrow()

        // 2. Check status (should be waiting)
        var statusResult = apiClient.tellStatus(gid)
        assertEquals("Should be waiting", "waiting", statusResult.getOrThrow().status)

        // 3. Start download (unpause to make it active)
        apiClient.unpause(gid)
        statusResult = apiClient.tellStatus(gid)
        assertEquals("Should be active", "active", statusResult.getOrThrow().status)

        // 4. Pause download
        apiClient.pause(gid)
        statusResult = apiClient.tellStatus(gid)
        assertEquals("Should be paused", "paused", statusResult.getOrThrow().status)

        // 5. Resume download
        apiClient.unpause(gid)
        statusResult = apiClient.tellStatus(gid)
        assertEquals("Should be active again", "active", statusResult.getOrThrow().status)

        // 6. Remove download
        apiClient.remove(gid)
        statusResult = apiClient.tellStatus(gid)
        assertEquals("Should be removed", "removed", statusResult.getOrThrow().status)

        // 7. Delete from list
        apiClient.removeDownloadResult(gid)
        statusResult = apiClient.tellStatus(gid)
        assertTrue("Should not exist", statusResult.isFailure)
    }

    @Test
    fun `test batch operations in stub mode`() = runTest {
        // Get initial active count
        val initialActive = apiClient.tellActive().getOrThrow().size

        // Pause all
        apiClient.pauseAll()
        var activeResult = apiClient.tellActive()
        assertEquals("Should have no active downloads", 0, activeResult.getOrThrow().size)

        // Unpause all
        apiClient.unpauseAll()
        activeResult = apiClient.tellActive()
        assertEquals("Should restore active downloads", initialActive, activeResult.getOrThrow().size)
    }

    @Test
    fun `test error handling for invalid operations in stub mode`() = runTest {
        val invalidGid = "ffffffffffffffff"

        // Test various operations with invalid GID
        assertTrue("tellStatus should fail", apiClient.tellStatus(invalidGid).isFailure)
        assertTrue("pause should fail", apiClient.pause(invalidGid).isFailure)
        assertTrue("unpause should fail", apiClient.unpause(invalidGid).isFailure)
        assertTrue("remove should fail", apiClient.remove(invalidGid).isFailure)
        assertTrue("getOption should fail", apiClient.getOption(invalidGid).isFailure)
        assertTrue("changeOption should fail", apiClient.changeOption(invalidGid, emptyMap()).isFailure)
    }
}
