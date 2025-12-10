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

import com.shareconnect.motrixconnect.data.model.*
import kotlinx.coroutines.delay

/**
 * Stub implementation of MotrixApiService for testing and UI development.
 * Provides realistic JSON-RPC responses using MotrixTestData without requiring a live Motrix/Aria2 server.
 *
 * Features:
 * - Stateful download management with in-memory storage
 * - Complete coverage of all Aria2 JSON-RPC methods
 * - Realistic response delays (500ms)
 * - Error simulation for invalid GIDs
 * - State transitions (active → paused → complete, etc.)
 */
class MotrixApiStubService(
    private val requireAuth: Boolean = false
) : MotrixApiService {

    companion object {
        // Simulated network delay in milliseconds
        private const val NETWORK_DELAY_MS = 500L

        // In-memory download storage
        private val downloads = mutableMapOf<String, MotrixDownload>()
        private var requestCounter = 0

        init {
            resetState()
        }

        /**
         * Reset all stub state to initial test data
         */
        fun resetState() {
            downloads.clear()
            requestCounter = 0

            // Add test downloads
            MotrixTestData.testAllDownloads.forEach { download ->
                downloads[download.gid] = download
            }
        }

        /**
         * Get next request ID
         */
        private fun nextRequestId(): String {
            return (++requestCounter).toString()
        }
    }

    private var authenticated = !requireAuth

    /**
     * Simulate authentication (for testing auth flows)
     */
    fun authenticate() {
        authenticated = true
    }

    private fun checkAuth() {
        if (!authenticated) {
            throw SecurityException(MotrixTestData.ERROR_UNAUTHORIZED)
        }
    }

    // Server Information

    override suspend fun getVersion(): MotrixRpcResponse<MotrixVersion> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = MotrixTestData.testVersion
        )
    }

    override suspend fun getGlobalStat(): MotrixRpcResponse<MotrixGlobalStat> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        // Calculate real-time statistics
        val activeDownloads = downloads.values.filter { it.status == "active" }
        val waitingDownloads = downloads.values.filter { it.status == "waiting" }
        val stoppedDownloads = downloads.values.filter {
            it.status in listOf("paused", "complete", "error", "removed")
        }

        val totalDownloadSpeed = activeDownloads.sumOf { it.downloadSpeed.toLongOrNull() ?: 0L }
        val totalUploadSpeed = activeDownloads.sumOf { it.uploadSpeed.toLongOrNull() ?: 0L }

        val stat = MotrixGlobalStat(
            downloadSpeed = totalDownloadSpeed.toString(),
            uploadSpeed = totalUploadSpeed.toString(),
            numActive = activeDownloads.size.toString(),
            numWaiting = waitingDownloads.size.toString(),
            numStopped = stoppedDownloads.size.toString(),
            numStoppedTotal = stoppedDownloads.size.toString()
        )

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = stat
        )
    }

    // Download Management

    override suspend fun addUri(uris: List<String>, options: MotrixDownloadOptions?): MotrixRpcResponse<String> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        val gid = MotrixTestData.generateGid()

        // Create new download
        val download = MotrixDownload(
            gid = gid,
            status = "waiting",
            totalLength = "0",
            completedLength = "0",
            uploadLength = "0",
            downloadSpeed = "0",
            uploadSpeed = "0",
            files = listOf(
                MotrixDownload.DownloadFile(
                    index = "1",
                    path = options?.directory?.let { "$it/${options.outputFileName ?: "download"}" } ?: "/downloads/download",
                    length = "0",
                    completedLength = "0",
                    selected = "true",
                    uris = uris.map {
                        MotrixDownload.DownloadFile.Uri(uri = it, status = "waiting")
                    }
                )
            ),
            directory = options?.directory ?: "/downloads",
            errorCode = null,
            errorMessage = null,
            connections = options?.connections?.toString() ?: "0"
        )

        downloads[gid] = download

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = gid
        )
    }

    override suspend fun tellStatus(gid: String): MotrixRpcResponse<MotrixDownload> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        val download = downloads[gid]
            ?: return MotrixTestData.createErrorResponse(
                id = nextRequestId(),
                errorCode = MotrixTestData.RpcErrorCodes.GID_NOT_FOUND,
                errorMessage = MotrixTestData.ERROR_GID_NOT_FOUND
            )

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = download
        )
    }

    override suspend fun tellActive(): MotrixRpcResponse<List<MotrixDownload>> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        val activeDownloads = downloads.values.filter { it.status == "active" }

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = activeDownloads
        )
    }

    override suspend fun tellWaiting(offset: Int, limit: Int): MotrixRpcResponse<List<MotrixDownload>> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        val waitingDownloads = downloads.values
            .filter { it.status == "waiting" }
            .drop(offset)
            .take(limit)

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = waitingDownloads
        )
    }

    override suspend fun tellStopped(offset: Int, limit: Int): MotrixRpcResponse<List<MotrixDownload>> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        val stoppedDownloads = downloads.values
            .filter { it.status in listOf("paused", "complete", "error", "removed") }
            .drop(offset)
            .take(limit)

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = stoppedDownloads
        )
    }

    // Download Control

    override suspend fun pause(gid: String): MotrixRpcResponse<String> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        val download = downloads[gid]
            ?: return MotrixTestData.createErrorResponse(
                id = nextRequestId(),
                errorCode = MotrixTestData.RpcErrorCodes.GID_NOT_FOUND,
                errorMessage = MotrixTestData.ERROR_GID_NOT_FOUND
            )

        if (download.status in listOf("active", "waiting")) {
            downloads[gid] = download.copy(
                status = "paused",
                downloadSpeed = "0",
                uploadSpeed = "0"
            )
        }

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = gid
        )
    }

    override suspend fun pauseAll(): MotrixRpcResponse<String> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        downloads.entries
            .filter { it.value.status in listOf("active", "waiting") }
            .forEach { (gid, download) ->
                downloads[gid] = download.copy(
                    status = "paused",
                    downloadSpeed = "0",
                    uploadSpeed = "0"
                )
            }

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = "OK"
        )
    }

    override suspend fun unpause(gid: String): MotrixRpcResponse<String> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        val download = downloads[gid]
            ?: return MotrixTestData.createErrorResponse(
                id = nextRequestId(),
                errorCode = MotrixTestData.RpcErrorCodes.GID_NOT_FOUND,
                errorMessage = MotrixTestData.ERROR_GID_NOT_FOUND
            )

        if (download.status == "paused") {
            downloads[gid] = download.copy(
                status = "active",
                downloadSpeed = "524288",  // Resume with 512 KB/s
                uploadSpeed = if (download.files?.firstOrNull()?.uris?.isEmpty() == false) "0" else "131072"
            )
        }

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = gid
        )
    }

    override suspend fun unpauseAll(): MotrixRpcResponse<String> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        downloads.entries
            .filter { it.value.status == "paused" }
            .forEach { (gid, download) ->
                downloads[gid] = download.copy(
                    status = "active",
                    downloadSpeed = "524288",
                    uploadSpeed = "0"
                )
            }

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = "OK"
        )
    }

    override suspend fun remove(gid: String): MotrixRpcResponse<String> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        val download = downloads[gid]
            ?: return MotrixTestData.createErrorResponse(
                id = nextRequestId(),
                errorCode = MotrixTestData.RpcErrorCodes.GID_NOT_FOUND,
                errorMessage = MotrixTestData.ERROR_GID_NOT_FOUND
            )

        if (download.status in listOf("active", "waiting", "paused")) {
            downloads[gid] = download.copy(
                status = "removed",
                downloadSpeed = "0",
                uploadSpeed = "0"
            )
        }

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = gid
        )
    }

    override suspend fun forceRemove(gid: String): MotrixRpcResponse<String> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        val download = downloads[gid]
            ?: return MotrixTestData.createErrorResponse(
                id = nextRequestId(),
                errorCode = MotrixTestData.RpcErrorCodes.GID_NOT_FOUND,
                errorMessage = MotrixTestData.ERROR_GID_NOT_FOUND
            )

        downloads[gid] = download.copy(
            status = "removed",
            downloadSpeed = "0",
            uploadSpeed = "0"
        )

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = gid
        )
    }

    override suspend fun removeDownloadResult(gid: String): MotrixRpcResponse<String> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        if (!downloads.containsKey(gid)) {
            return MotrixTestData.createErrorResponse(
                id = nextRequestId(),
                errorCode = MotrixTestData.RpcErrorCodes.GID_NOT_FOUND,
                errorMessage = MotrixTestData.ERROR_GID_NOT_FOUND
            )
        }

        downloads.remove(gid)

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = "OK"
        )
    }

    // Options Management

    override suspend fun getGlobalOption(): MotrixRpcResponse<Map<String, String>> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = MotrixTestData.testGlobalOptions
        )
    }

    override suspend fun changeGlobalOption(options: Map<String, String>): MotrixRpcResponse<String> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        // In stub mode, we don't actually persist these changes
        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = "OK"
        )
    }

    override suspend fun getOption(gid: String): MotrixRpcResponse<Map<String, String>> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        if (!downloads.containsKey(gid)) {
            return MotrixTestData.createErrorResponse(
                id = nextRequestId(),
                errorCode = MotrixTestData.RpcErrorCodes.GID_NOT_FOUND,
                errorMessage = MotrixTestData.ERROR_GID_NOT_FOUND
            )
        }

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = MotrixTestData.testDownloadOptions2
        )
    }

    override suspend fun changeOption(gid: String, options: Map<String, String>): MotrixRpcResponse<String> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        if (!downloads.containsKey(gid)) {
            return MotrixTestData.createErrorResponse(
                id = nextRequestId(),
                errorCode = MotrixTestData.RpcErrorCodes.GID_NOT_FOUND,
                errorMessage = MotrixTestData.ERROR_GID_NOT_FOUND
            )
        }

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = "OK"
        )
    }

    // Session Management

    override suspend fun purgeDownloadResult(): MotrixRpcResponse<String> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        // Remove all stopped downloads
        downloads.entries.removeIf {
            it.value.status in listOf("complete", "error", "removed")
        }

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = "OK"
        )
    }

    override suspend fun saveSession(): MotrixRpcResponse<String> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = "OK"
        )
    }

    override suspend fun shutdown(): MotrixRpcResponse<String> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = "OK"
        )
    }

    override suspend fun forceShutdown(): MotrixRpcResponse<String> {
        delay(NETWORK_DELAY_MS)
        checkAuth()

        return MotrixTestData.createSuccessResponse(
            id = nextRequestId(),
            result = "OK"
        )
    }
}
