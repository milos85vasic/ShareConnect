package com.shareconnect.qa.ai.mocks

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder

/**
 * Mock dispatcher for uTorrent WebUI API
 * Simulates uTorrent's HTTP API for torrent management
 */
class UTorrentMockDispatcher : QueueDispatcher() {

    private val torrents = mutableMapOf<String, TorrentInfo>()
    private var authenticated = false
    private val token = "mock_token_${System.currentTimeMillis()}"

    data class TorrentInfo(
        val hash: String,
        val name: String,
        val size: Long,
        val downloaded: Long,
        val uploaded: Long,
        val downloadSpeed: Long,
        val uploadSpeed: Long,
        val status: Int, // uTorrent status codes
        val progress: Int, // 0-1000 (per mille)
        val eta: Long,
        val label: String = "",
        val peers: Int = 0,
        val seeds: Int = 0
    )

    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path ?: return createErrorResponse("Invalid request path")

        return when {
            path == "/gui/token.html" && request.method == "GET" -> handleGetToken(request)
            path == "/gui/" && request.method == "GET" -> handleGuiRequest(request)
            path == "/gui/" && request.method == "POST" -> handleGuiRequest(request)
            else -> createErrorResponse("Endpoint not found: $path")
        }
    }

    private fun handleGetToken(request: RecordedRequest): MockResponse {
        // Check basic auth
        if (!checkBasicAuth(request)) {
            return createAuthError()
        }

        authenticated = true

        return MockResponse()
            .setResponseCode(200)
            .setBody("<html><div id='token' style='display:none;'>$token</div></html>")
            .addHeader("Content-Type", "text/html")
    }

    private fun handleGuiRequest(request: RecordedRequest): MockResponse {
        // Check authentication
        if (!authenticated) {
            return createAuthError()
        }

        // Check token parameter
        val tokenParam = request.requestUrl?.queryParameter("token")
        if (tokenParam != token) {
            return createErrorResponse("Invalid token")
        }

        val action = request.requestUrl?.queryParameter("action") ?: "list"
        val hash = request.requestUrl?.queryParameter("hash")

        return when (action) {
            "list" -> handleListTorrents()
            "add-url" -> handleAddUrl(request)
            "remove" -> handleRemoveTorrent(hash)
            "start" -> handleStartTorrent(hash)
            "stop" -> handleStopTorrent(hash)
            "pause" -> handlePauseTorrent(hash)
            "getsettings" -> handleGetSettings()
            "setsetting" -> handleSetSetting(request)
            else -> createErrorResponse("Unknown action: $action")
        }
    }

    private fun checkBasicAuth(request: RecordedRequest): Boolean {
        val authHeader = request.getHeader("Authorization") ?: return false

        if (!authHeader.startsWith("Basic ")) {
            return false
        }

        return try {
            val credentials = String(java.util.Base64.getDecoder().decode(authHeader.substring(6)))
            val (username, password) = credentials.split(":", limit = 2)
            username == "admin" && password == "admin"
        } catch (e: Exception) {
            false
        }
    }

    private fun handleListTorrents(): MockResponse {
        val torrentsArray = JSONArray()

        torrents.values.forEach { torrent ->
            val torrentArray = JSONArray().apply {
                put(torrent.hash)           // [0] HASH
                put(torrent.status)         // [1] STATUS
                put(torrent.name)           // [2] NAME
                put(torrent.size)           // [3] SIZE
                put(torrent.progress)       // [4] PERCENT PROGRESS (per mille)
                put(torrent.downloaded)     // [5] DOWNLOADED
                put(torrent.uploaded)       // [6] UPLOADED
                put(torrent.downloadSpeed)  // [7] RATIO (actually download speed)
                put(torrent.uploadSpeed)    // [8] UPLOAD SPEED
                put(torrent.eta)            // [9] ETA
                put(torrent.label)          // [10] LABEL
                put(torrent.peers)          // [11] PEERS CONNECTED
                put(torrent.seeds)          // [12] PEERS IN SWARM
                put(0)                      // [13] SEEDS CONNECTED
                put(0)                      // [14] SEEDS IN SWARM
                put(0)                      // [15] AVAILABILITY
                put(0)                      // [16] TORRENT QUEUE ORDER
                put(0)                      // [17] REMAINING
            }
            torrentsArray.put(torrentArray)
        }

        val response = JSONObject().apply {
            put("build", 46050)
            put("torrents", torrentsArray)
            put("label", JSONArray()) // Empty labels array
            put("torrentc", "608886496") // Cache ID
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleAddUrl(request: RecordedRequest): MockResponse {
        val url = request.requestUrl?.queryParameter("s")
            ?: return createErrorResponse("Missing URL parameter 's'")

        if (!isValidTorrentUrl(url)) {
            return createErrorResponse("Invalid torrent URL")
        }

        val hash = generateTorrentHash(url)
        val torrent = TorrentInfo(
            hash = hash,
            name = extractTorrentName(url),
            size = (50 * 1024 * 1024..3 * 1024 * 1024 * 1024).random().toLong(), // 50MB to 3GB
            downloaded = 0,
            uploaded = 0,
            downloadSpeed = (50 * 1024..3 * 1024 * 1024).random().toLong(),
            uploadSpeed = (5 * 1024..300 * 1024).random().toLong(),
            status = 1, // Started
            progress = 0,
            eta = (180..1800).random().toLong(), // 3 min to 30 min
            peers = (5..50).random(),
            seeds = (10..200).random()
        )

        torrents[hash] = torrent

        // Simulate download progress
        simulateTorrentProgress(hash)

        val response = JSONObject().apply {
            put("build", 46050)
            put("torrentp", JSONObject().put("hash", hash))
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleRemoveTorrent(hash: String?): MockResponse {
        if (hash.isNullOrEmpty()) {
            return createErrorResponse("Missing hash parameter")
        }

        torrents.remove(hash)

        val response = JSONObject().apply {
            put("build", 46050)
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleStartTorrent(hash: String?): MockResponse {
        if (hash.isNullOrEmpty()) {
            return createErrorResponse("Missing hash parameter")
        }

        torrents[hash]?.let { torrent ->
            torrents[hash] = torrent.copy(status = 1) // Started
            simulateTorrentProgress(hash)
        }

        val response = JSONObject().apply {
            put("build", 46050)
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleStopTorrent(hash: String?): MockResponse {
        if (hash.isNullOrEmpty()) {
            return createErrorResponse("Missing hash parameter")
        }

        torrents[hash]?.let { torrent ->
            torrents[hash] = torrent.copy(status = 0) // Stopped
        }

        val response = JSONObject().apply {
            put("build", 46050)
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handlePauseTorrent(hash: String?): MockResponse {
        if (hash.isNullOrEmpty()) {
            return createErrorResponse("Missing hash parameter")
        }

        torrents[hash]?.let { torrent ->
            torrents[hash] = torrent.copy(status = 2) // Paused
        }

        val response = JSONObject().apply {
            put("build", 46050)
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleGetSettings(): MockResponse {
        val settings = JSONArray().apply {
            // Common uTorrent settings
            put(JSONArray().apply {
                put("max_dl_rate")
                put(0)
                put("number")
            })
            put(JSONArray().apply {
                put("max_ul_rate")
                put(0)
                put("number")
            })
            put(JSONArray().apply {
                put("max_num_connections")
                put(200)
                put("number")
            })
            put(JSONArray().apply {
                put("max_num_slots")
                put(8)
                put("number")
            })
        }

        val response = JSONObject().apply {
            put("build", 46050)
            put("settings", settings)
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleSetSetting(request: RecordedRequest): MockResponse {
        val setting = request.requestUrl?.queryParameter("s")
        val value = request.requestUrl?.queryParameter("v")

        if (setting.isNullOrEmpty()) {
            return createErrorResponse("Missing setting parameter 's'")
        }

        // Accept any setting change
        val response = JSONObject().apply {
            put("build", 46050)
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun isValidTorrentUrl(url: String): Boolean {
        return url.startsWith("magnet:") ||
               url.startsWith("http://") && url.endsWith(".torrent") ||
               url.startsWith("https://") && url.endsWith(".torrent")
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
                var progress = torrents[hash]?.progress ?: 0
                while (progress < 1000) {
                    Thread.sleep(1500) // Update every 1.5 seconds
                    progress += (2..15).random() // Progress in per mille (0-1000)
                    if (progress > 1000) progress = 1000

                    torrents[hash]?.let { torrent ->
                        val downloaded = (torrent.size * progress / 1000)
                        val newStatus = when {
                            progress >= 1000 -> 201 // Finished
                            torrent.status == 1 -> 1 // Downloading
                            else -> torrent.status
                        }

                        torrents[hash] = torrent.copy(
                            progress = progress,
                            downloaded = downloaded,
                            status = newStatus,
                            eta = if (progress >= 1000) -1 else (torrent.eta - 1).coerceAtLeast(0)
                        )
                    }
                }
            } catch (e: Exception) {
                torrents[hash]?.let { torrent ->
                    torrents[hash] = torrent.copy(status = 0) // Stopped on error
                }
            }
        }.start()
    }

    private fun createErrorResponse(message: String): MockResponse {
        return MockResponse()
            .setResponseCode(400)
            .setBody(message)
    }

    private fun createAuthError(): MockResponse {
        return MockResponse()
            .setResponseCode(401)
            .setBody("Unauthorized")
    }
}