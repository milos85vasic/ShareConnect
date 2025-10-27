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


package com.shareconnect.qa.ai.mocks

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONObject
import java.net.URLDecoder
import kotlin.random.Random

/**
 * Mock dispatcher for qBittorrent WebUI API
 * Simulates qBittorrent's HTTP API for torrent management
 */
class QBittorrentMockDispatcher : QueueDispatcher() {

    private val torrents = mutableMapOf<String, TorrentInfo>()
    private val categories = mutableMapOf<String, CategoryInfo>()
    private var authenticated = false

    data class TorrentInfo(
        val hash: String,
        val name: String,
        val size: Long,
        val progress: Float,
        val state: String,
        val downloadSpeed: Long,
        val uploadSpeed: Long,
        val eta: Long,
        val category: String = "",
        val tags: List<String> = emptyList()
    )

    data class CategoryInfo(
        val name: String,
        val savePath: String
    )

    init {
        // Initialize with default category
        categories["default"] = CategoryInfo("default", "/downloads")
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path ?: return createErrorResponse("Invalid request path")

        // Check authentication for protected endpoints
        if (!isPublicEndpoint(path) && !authenticated) {
            return createAuthError()
        }

        return when {
            path == "/api/v2/auth/login" && request.method == "POST" -> handleLogin(request)
            path == "/api/v2/torrents/info" && request.method == "GET" -> handleGetTorrents()
            path == "/api/v2/torrents/add" && request.method == "POST" -> handleAddTorrent(request)
            path == "/api/v2/torrents/delete" && request.method == "POST" -> handleDeleteTorrent(request)
            path == "/api/v2/torrents/pause" && request.method == "POST" -> handlePauseTorrent(request)
            path == "/api/v2/torrents/resume" && request.method == "POST" -> handleResumeTorrent(request)
            path == "/api/v2/torrents/categories" && request.method == "GET" -> handleGetCategories()
            path == "/api/v2/torrents/createCategory" && request.method == "POST" -> handleCreateCategory(request)
            path == "/api/v2/transfer/info" && request.method == "GET" -> handleTransferInfo()
            path == "/api/v2/app/version" && request.method == "GET" -> handleVersion()
            else -> createErrorResponse("Endpoint not found: $path")
        }
    }

    private fun isPublicEndpoint(path: String): Boolean {
        return path == "/api/v2/auth/login"
    }

    private fun handleLogin(request: RecordedRequest): MockResponse {
        val body = request.body.readUtf8()
        val params = parseFormData(body)

        val username = params["username"]
        val password = params["password"]

        return if (username == "admin" && password == "adminadmin") {
            authenticated = true
            MockResponse()
                .setResponseCode(200)
                .setBody("Ok.")
                .addHeader("Set-Cookie", "SID=mock_session_id")
        } else {
            authenticated = false
            MockResponse()
                .setResponseCode(403)
                .setBody("Fails.")
        }
    }

    private fun handleGetTorrents(): MockResponse {
        val torrentsArray = torrents.values.map { torrent ->
            JSONObject().apply {
                put("hash", torrent.hash)
                put("name", torrent.name)
                put("size", torrent.size)
                put("progress", torrent.progress)
                put("state", torrent.state)
                put("dlspeed", torrent.downloadSpeed)
                put("upspeed", torrent.uploadSpeed)
                put("eta", torrent.eta)
                put("category", torrent.category)
                put("tags", torrent.tags.joinToString(","))
            }
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(JSONObject().put("torrents", torrentsArray).toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleAddTorrent(request: RecordedRequest): MockResponse {
        val body = request.body.readUtf8()
        val params = parseFormData(body)

        val urls = params["urls"]
        val torrentFile = params["torrents"]

        if (urls.isNullOrEmpty() && torrentFile.isNullOrEmpty()) {
            return createErrorResponse("No torrent URLs or files provided")
        }

        // Parse URLs (can be magnet links or HTTP URLs to .torrent files)
        val torrentUrls = urls?.split("\n")?.filter { it.isNotEmpty() } ?: emptyList()

        torrentUrls.forEach { url ->
            val hash = generateTorrentHash(url)
            val torrent = TorrentInfo(
                hash = hash,
                name = extractTorrentName(url),
                size = (100 * 1024 * 1024..10 * 1024 * 1024 * 1024).random().toLong(), // 100MB to 10GB
                progress = 0.0f,
                state = "downloading",
                downloadSpeed = (100 * 1024..5 * 1024 * 1024).random().toLong(), // 100KB/s to 5MB/s
                uploadSpeed = (10 * 1024..500 * 1024).random().toLong(), // 10KB/s to 500KB/s
                eta = (60..3600).random().toLong() // 1 min to 1 hour
            )
            torrents[hash] = torrent

            // Simulate download progress
            simulateTorrentProgress(hash)
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody("Ok.")
    }

    private fun handleDeleteTorrent(request: RecordedRequest): MockResponse {
        val body = request.body.readUtf8()
        val params = parseFormData(body)

        val hashes = params["hashes"]?.split("|") ?: emptyList()
        val deleteFiles = params["deleteFiles"] == "true"

        hashes.forEach { hash ->
            torrents.remove(hash)
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody("Ok.")
    }

    private fun handlePauseTorrent(request: RecordedRequest): MockResponse {
        val body = request.body.readUtf8()
        val params = parseFormData(body)

        val hashes = params["hashes"]?.split("|") ?: emptyList()

        hashes.forEach { hash ->
            torrents[hash]?.let { torrent ->
                torrents[hash] = torrent.copy(state = "pausedDL")
            }
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody("Ok.")
    }

    private fun handleResumeTorrent(request: RecordedRequest): MockResponse {
        val body = request.body.readUtf8()
        val params = parseFormData(body)

        val hashes = params["hashes"]?.split("|") ?: emptyList()

        hashes.forEach { hash ->
            torrents[hash]?.let { torrent ->
                torrents[hash] = torrent.copy(state = "downloading")
                simulateTorrentProgress(hash)
            }
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody("Ok.")
    }

    private fun handleGetCategories(): MockResponse {
        val categoriesObj = JSONObject()
        categories.forEach { (name, info) ->
            categoriesObj.put(name, JSONObject().put("name", info.name).put("savePath", info.savePath))
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(categoriesObj.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleCreateCategory(request: RecordedRequest): MockResponse {
        val body = request.body.readUtf8()
        val params = parseFormData(body)

        val category = params["category"] ?: return createErrorResponse("Category name required")
        val savePath = params["savePath"] ?: "/downloads/$category"

        categories[category] = CategoryInfo(category, savePath)

        return MockResponse()
            .setResponseCode(200)
            .setBody("Ok.")
    }

    private fun handleTransferInfo(): MockResponse {
        val info = JSONObject().apply {
            put("dl_info_speed", (500 * 1024..10 * 1024 * 1024).random()) // 500KB/s to 10MB/s
            put("up_info_speed", (50 * 1024..2 * 1024 * 1024).random()) // 50KB/s to 2MB/s
            put("dl_info_data", 1024 * 1024 * 1024) // 1GB downloaded
            put("up_info_data", 100 * 1024 * 1024) // 100MB uploaded
            put("dl_rate_limit", 0)
            put("up_rate_limit", 0)
            put("dht_nodes", 150)
            put("connection_status", "connected")
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(info.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleVersion(): MockResponse {
        return MockResponse()
            .setResponseCode(200)
            .setBody("v4.6.0")
    }

    private fun parseFormData(body: String): Map<String, String> {
        return body.split("&").associate { param ->
            val parts = param.split("=", limit = 2)
            val key = URLDecoder.decode(parts[0], "UTF-8")
            val value = if (parts.size > 1) URLDecoder.decode(parts[1], "UTF-8") else ""
            key to value
        }
    }

    private fun generateTorrentHash(url: String): String {
        // Generate a mock 40-character hash
        return url.hashCode().toString(16).padStart(40, '0').take(40)
    }

    private fun extractTorrentName(url: String): String {
        return when {
            url.startsWith("magnet:") -> {
                val dn = url.substringAfter("dn=").substringBefore("&")
                if (dn.isNotEmpty()) URLDecoder.decode(dn, "UTF-8") else "Magnet Torrent"
            }
            url.endsWith(".torrent") -> {
                url.substringAfterLast("/").substringBefore(".torrent").replace("+", " ")
            }
            else -> "Unknown Torrent"
        }
    }

    private fun simulateTorrentProgress(hash: String) {
        Thread {
            try {
                var progress = torrents[hash]?.progress ?: 0.0f
                while (progress < 1.0f) {
                    Thread.sleep(1000) // Update every second
                    progress += Random.nextFloat() * (0.01f - 0.001f) + 0.001f // 0.1% to 1% per second
                    if (progress > 1.0f) progress = 1.0f

                    torrents[hash]?.let { torrent ->
                        val newState = if (progress >= 1.0f) "uploading" else "downloading"
                        torrents[hash] = torrent.copy(
                            progress = progress,
                            state = newState,
                            downloadSpeed = if (progress >= 1.0f) 0 else torrent.downloadSpeed,
                            eta = if (progress >= 1.0f) 86400 else (torrent.eta - 1).coerceAtLeast(0) // 24 hours when seeding
                        )
                    }
                }
            } catch (e: Exception) {
                torrents[hash]?.let { torrent ->
                    torrents[hash] = torrent.copy(state = "error")
                }
            }
        }.start()
    }

    private fun createErrorResponse(message: String, code: Int = 400): MockResponse {
        return MockResponse()
            .setResponseCode(code)
            .setBody(message)
    }

    private fun createAuthError(): MockResponse {
        return MockResponse()
            .setResponseCode(403)
            .setBody("Forbidden")
    }
}