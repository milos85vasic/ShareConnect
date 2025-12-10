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

/**
 * Test data provider for Motrix stub mode.
 *
 * Provides realistic sample data for:
 * - Active, waiting, paused, and completed downloads
 * - Global statistics
 * - Version information
 * - Download options
 * - JSON-RPC responses
 */
object MotrixTestData {

    // Server Configuration
    const val TEST_SERVER_URL = "http://localhost:16800"
    const val TEST_RPC_SECRET = "test-secret-token"

    // Version Information
    val testVersion = MotrixVersion(
        version = "1.8.19",
        enabledFeatures = listOf(
            "Async DNS",
            "BitTorrent",
            "Firefox3 Cookie",
            "GZip",
            "HTTPS",
            "Message Digest",
            "Metalink",
            "XML-RPC",
            "SFTP"
        )
    )

    // Global Statistics
    val testGlobalStatActive = MotrixGlobalStat(
        downloadSpeed = "524288",    // 512 KB/s
        uploadSpeed = "131072",       // 128 KB/s
        numActive = "2",
        numWaiting = "1",
        numStopped = "5",
        numStoppedTotal = "15"
    )

    val testGlobalStatIdle = MotrixGlobalStat(
        downloadSpeed = "0",
        uploadSpeed = "0",
        numActive = "0",
        numWaiting = "0",
        numStopped = "8",
        numStoppedTotal = "20"
    )

    // Sample Download Files
    private val testFileUbuntuIso = MotrixDownload.DownloadFile(
        index = "1",
        path = "/downloads/ubuntu-22.04.3-desktop-amd64.iso",
        length = "4700372992",  // ~4.4 GB
        completedLength = "2350186496",  // 50% complete
        selected = "true",
        uris = listOf(
            MotrixDownload.DownloadFile.Uri(
                uri = "https://releases.ubuntu.com/22.04/ubuntu-22.04.3-desktop-amd64.iso",
                status = "used"
            )
        )
    )

    private val testFileMovieMp4 = MotrixDownload.DownloadFile(
        index = "1",
        path = "/downloads/BigBuckBunny.mp4",
        length = "158008374",  // ~150 MB
        completedLength = "158008374",
        selected = "true",
        uris = listOf(
            MotrixDownload.DownloadFile.Uri(
                uri = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                status = "used"
            )
        )
    )

    private val testFileAlbumZip = MotrixDownload.DownloadFile(
        index = "1",
        path = "/downloads/music-album.zip",
        length = "104857600",  // 100 MB
        completedLength = "0",
        selected = "true",
        uris = listOf(
            MotrixDownload.DownloadFile.Uri(
                uri = "https://example.com/downloads/music-album.zip",
                status = "waiting"
            )
        )
    )

    private val testFileTorrent1 = MotrixDownload.DownloadFile(
        index = "1",
        path = "/downloads/debian-12.0.0-amd64-DVD-1.iso",
        length = "4009754624",  // ~3.7 GB
        completedLength = "401097472",  // 10% complete
        selected = "true",
        uris = emptyList()
    )

    private val testFileDocument = MotrixDownload.DownloadFile(
        index = "1",
        path = "/downloads/technical-manual.pdf",
        length = "52428800",  // 50 MB
        completedLength = "52428800",
        selected = "true",
        uris = listOf(
            MotrixDownload.DownloadFile.Uri(
                uri = "https://downloads.example.com/manual.pdf",
                status = "used"
            )
        )
    )

    // Sample Downloads
    val testDownloadActiveHttp = MotrixDownload(
        gid = "2089b05ecca3d829",
        status = "active",
        totalLength = "4700372992",
        completedLength = "2350186496",
        uploadLength = "0",
        downloadSpeed = "524288",    // 512 KB/s
        uploadSpeed = "0",
        files = listOf(testFileUbuntuIso),
        directory = "/downloads",
        errorCode = null,
        errorMessage = null,
        connections = "16"
    )

    val testDownloadActiveTorrent = MotrixDownload(
        gid = "3fa2b15fce4a9876",
        status = "active",
        totalLength = "4009754624",
        completedLength = "401097472",
        uploadLength = "50331648",   // 48 MB uploaded
        downloadSpeed = "262144",    // 256 KB/s
        uploadSpeed = "131072",      // 128 KB/s
        files = listOf(testFileTorrent1),
        directory = "/downloads",
        errorCode = null,
        errorMessage = null,
        connections = "8"
    )

    val testDownloadWaiting = MotrixDownload(
        gid = "4ba3c26gdf5ba987",
        status = "waiting",
        totalLength = "104857600",
        completedLength = "0",
        uploadLength = "0",
        downloadSpeed = "0",
        uploadSpeed = "0",
        files = listOf(testFileAlbumZip),
        directory = "/downloads",
        errorCode = null,
        errorMessage = null,
        connections = "0"
    )

    val testDownloadPaused = MotrixDownload(
        gid = "5cb4d37he6gcb098",
        status = "paused",
        totalLength = "4700372992",
        completedLength = "1175093248",  // 25% complete
        uploadLength = "0",
        downloadSpeed = "0",
        uploadSpeed = "0",
        files = listOf(testFileUbuntuIso.copy(completedLength = "1175093248")),
        directory = "/downloads",
        errorCode = null,
        errorMessage = null,
        connections = "0"
    )

    val testDownloadComplete = MotrixDownload(
        gid = "6dc5e48if7hdc109",
        status = "complete",
        totalLength = "158008374",
        completedLength = "158008374",
        uploadLength = "0",
        downloadSpeed = "0",
        uploadSpeed = "0",
        files = listOf(testFileMovieMp4),
        directory = "/downloads",
        errorCode = null,
        errorMessage = null,
        connections = "0"
    )

    val testDownloadError = MotrixDownload(
        gid = "7ed6f59jg8ied21a",
        status = "error",
        totalLength = "0",
        completedLength = "0",
        uploadLength = "0",
        downloadSpeed = "0",
        uploadSpeed = "0",
        files = emptyList(),
        directory = "/downloads",
        errorCode = "1",
        errorMessage = "Unknown error",
        connections = "0"
    )

    val testDownloadRemoved = MotrixDownload(
        gid = "8fe7g60kh9jfe32b",
        status = "removed",
        totalLength = "52428800",
        completedLength = "26214400",  // 50% when removed
        uploadLength = "0",
        downloadSpeed = "0",
        uploadSpeed = "0",
        files = listOf(testFileDocument.copy(completedLength = "26214400")),
        directory = "/downloads",
        errorCode = null,
        errorMessage = null,
        connections = "0"
    )

    // Collections
    val testAllDownloads = listOf(
        testDownloadActiveHttp,
        testDownloadActiveTorrent,
        testDownloadWaiting,
        testDownloadPaused,
        testDownloadComplete,
        testDownloadError,
        testDownloadRemoved
    )

    val testActiveDownloads = listOf(
        testDownloadActiveHttp,
        testDownloadActiveTorrent
    )

    val testWaitingDownloads = listOf(
        testDownloadWaiting
    )

    val testStoppedDownloads = listOf(
        testDownloadPaused,
        testDownloadComplete,
        testDownloadError,
        testDownloadRemoved
    )

    // Download Options
    val testDownloadOptions = MotrixDownloadOptions(
        directory = "/downloads",
        outputFileName = "custom-filename.iso",
        connections = 16,
        maxDownloadSpeed = "5M",
        maxUploadSpeed = "1M",
        headers = listOf("User-Agent: Mozilla/5.0"),
        referer = "https://example.com",
        userAgent = "Mozilla/5.0"
    )

    // Global Options
    val testGlobalOptions = mapOf(
        "dir" to "/downloads",
        "max-download-limit" to "0",
        "max-upload-limit" to "0",
        "max-concurrent-downloads" to "5",
        "split" to "16",
        "min-split-size" to "1M",
        "max-connection-per-server" to "16",
        "continue" to "true",
        "file-allocation" to "prealloc"
    )

    val testDownloadOptions2 = mapOf(
        "dir" to "/downloads",
        "split" to "8",
        "max-download-limit" to "1M",
        "out" to "myfile.zip"
    )

    // RPC Response Factory Methods
    fun <T> createSuccessResponse(id: String, result: T): MotrixRpcResponse<T> {
        return MotrixRpcResponse(
            id = id,
            jsonrpc = "2.0",
            result = result,
            error = null
        )
    }

    fun <T> createErrorResponse(id: String, errorCode: Int, errorMessage: String): MotrixRpcResponse<T> {
        return MotrixRpcResponse(
            id = id,
            jsonrpc = "2.0",
            result = null,
            error = MotrixRpcResponse.RpcError(
                code = errorCode,
                message = errorMessage
            )
        )
    }

    // Common RPC Error Codes
    object RpcErrorCodes {
        const val PARSE_ERROR = -32700
        const val INVALID_REQUEST = -32600
        const val METHOD_NOT_FOUND = -32601
        const val INVALID_PARAMS = -32602
        const val INTERNAL_ERROR = -32603
        const val UNAUTHORIZED = 1  // Custom: Authentication failed
        const val GID_NOT_FOUND = 2  // Custom: GID not found
    }

    // Sample RPC Error Messages
    const val ERROR_UNAUTHORIZED = "Unauthorized"
    const val ERROR_GID_NOT_FOUND = "Active Download not found"
    const val ERROR_METHOD_NOT_FOUND = "Method not found"
    const val ERROR_INVALID_PARAMS = "Invalid params"

    /**
     * Get download by GID
     */
    fun getDownloadByGid(gid: String): MotrixDownload? {
        return testAllDownloads.find { it.gid == gid }
    }

    /**
     * Generate new GID for test downloads
     */
    fun generateGid(): String {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16)
    }
}
