package com.shareconnect.qa.ai.mocks

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONObject
import java.net.URLDecoder
import java.util.Base64
import kotlin.random.Random

/**
 * Mock dispatcher for Transmission RPC API
 * Simulates Transmission's JSON-RPC API for torrent management
 */
class TransmissionMockDispatcher : QueueDispatcher() {

    private val torrents = mutableMapOf<Int, TorrentInfo>()
    private var authenticated = false
    private var sessionId = "mock_session_${System.currentTimeMillis()}"
    private var torrentIdCounter = 1

    data class TorrentInfo(
        val id: Int,
        val name: String,
        val totalSize: Long,
        val downloadedEver: Long,
        val uploadedEver: Long,
        val downloadSpeed: Long,
        val uploadSpeed: Long,
        val status: Int, // 0=stopped, 1=check pending, 2=checking, 3=download pending, 4=downloading, 5=seed pending, 6=seeding
        val percentDone: Float,
        val eta: Long,
        val magnetLink: String? = null,
        val files: List<FileInfo> = emptyList()
    )

    data class FileInfo(
        val name: String,
        val length: Long,
        val bytesCompleted: Long
    )

    override fun dispatch(request: RecordedRequest): MockResponse {
        // Check for session header
        val sessionHeader = request.getHeader("X-Transmission-Session-Id")
        if (sessionHeader != sessionId) {
            return MockResponse()
                .setResponseCode(409)
                .addHeader("X-Transmission-Session-Id", sessionId)
                .setBody("Session ID mismatch")
        }

        // Check basic auth
        if (!checkBasicAuth(request)) {
            return createAuthError()
        }

        val requestBody = request.body.readUtf8()
        if (requestBody.isEmpty()) {
            return createErrorResponse("Empty request body")
        }

        return try {
            val jsonRequest = JSONObject(requestBody)
            val method = jsonRequest.getString("method")

            when (method) {
                "torrent-get" -> handleTorrentGet(jsonRequest)
                "torrent-add" -> handleTorrentAdd(jsonRequest)
                "torrent-remove" -> handleTorrentRemove(jsonRequest)
                "torrent-stop" -> handleTorrentStop(jsonRequest)
                "torrent-start" -> handleTorrentStart(jsonRequest)
                "session-get" -> handleSessionGet()
                "session-stats" -> handleSessionStats()
                else -> createErrorResponse("Unknown method: $method")
            }
        } catch (e: Exception) {
            createErrorResponse("Invalid JSON: ${e.message}")
        }
    }

    private fun checkBasicAuth(request: RecordedRequest): Boolean {
        val authHeader = request.getHeader("Authorization") ?: return false

        if (!authHeader.startsWith("Basic ")) {
            return false
        }

        return try {
            val credentials = String(Base64.getDecoder().decode(authHeader.substring(6)))
            val (username, password) = credentials.split(":", limit = 2)
            username == "transmission" && password == "transmission"
        } catch (e: Exception) {
            false
        }
    }

    private fun handleTorrentGet(request: JSONObject): MockResponse {
        val fields = request.optJSONArray("fields") ?: return createErrorResponse("Missing fields array")

        val torrentsArray = torrents.values.map { torrent ->
            val torrentObj = JSONObject()
            for (i in 0 until fields.length()) {
                val field = fields.getString(i)
                when (field) {
                    "id" -> torrentObj.put("id", torrent.id)
                    "name" -> torrentObj.put("name", torrent.name)
                    "totalSize" -> torrentObj.put("totalSize", torrent.totalSize)
                    "downloadedEver" -> torrentObj.put("downloadedEver", torrent.downloadedEver)
                    "uploadedEver" -> torrentObj.put("uploadedEver", torrent.uploadSpeed)
                    "rateDownload" -> torrentObj.put("rateDownload", torrent.downloadSpeed)
                    "rateUpload" -> torrentObj.put("rateUpload", torrent.uploadSpeed)
                    "status" -> torrentObj.put("status", torrent.status)
                    "percentDone" -> torrentObj.put("percentDone", torrent.percentDone)
                    "eta" -> torrentObj.put("eta", torrent.eta)
                    "magnetLink" -> torrent.magnetLink?.let { torrentObj.put("magnetLink", it) }
                    "files" -> {
                        val filesArray = torrent.files.map { file ->
                            JSONObject().apply {
                                put("name", file.name)
                                put("length", file.length)
                                put("bytesCompleted", file.bytesCompleted)
                            }
                        }
                        torrentObj.put("files", filesArray)
                    }
                }
            }
            torrentObj
        }

        val response = JSONObject().apply {
            put("result", "success")
            put("arguments", JSONObject().put("torrents", torrentsArray))
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleTorrentAdd(request: JSONObject): MockResponse {
        val arguments = request.getJSONObject("arguments")
        val filename = arguments.optString("filename", "")
        val metainfo = arguments.optString("metainfo", "")

        if (filename.isEmpty() && metainfo.isEmpty()) {
            return createErrorResponse("No torrent file or magnet link provided")
        }

        val torrentId = torrentIdCounter++
        val torrentName = extractTorrentName(filename, metainfo)
        val totalSize = (100 * 1024 * 1024..5 * 1024 * 1024 * 1024).random().toLong() // 100MB to 5GB

        val torrent = TorrentInfo(
            id = torrentId,
            name = torrentName,
            totalSize = totalSize,
            downloadedEver = 0,
            uploadedEver = 0,
            downloadSpeed = (100 * 1024..5 * 1024 * 1024).random().toLong(),
            uploadSpeed = (10 * 1024..500 * 1024).random().toLong(),
            status = 4, // downloading
            percentDone = 0.0f,
            eta = (300..3600).random().toLong(), // 5 min to 1 hour
            magnetLink = if (filename.startsWith("magnet:")) filename else null,
            files = generateMockFiles(torrentName, totalSize)
        )

        torrents[torrentId] = torrent

        // Simulate download progress
        simulateTorrentProgress(torrentId)

        val response = JSONObject().apply {
            put("result", "success")
            put("arguments", JSONObject().apply {
                put("torrent-added", JSONObject().put("id", torrentId).put("name", torrentName))
            })
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleTorrentRemove(request: JSONObject): MockResponse {
        val arguments = request.getJSONObject("arguments")
        val ids = arguments.optJSONArray("ids") ?: return createErrorResponse("Missing torrent IDs")

        for (i in 0 until ids.length()) {
            val id = ids.getInt(i)
            torrents.remove(id)
        }

        val response = JSONObject().apply {
            put("result", "success")
            put("arguments", JSONObject())
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleTorrentStop(request: JSONObject): MockResponse {
        val arguments = request.getJSONObject("arguments")
        val ids = arguments.optJSONArray("ids") ?: return createErrorResponse("Missing torrent IDs")

        for (i in 0 until ids.length()) {
            val id = ids.getInt(i)
            torrents[id]?.let { torrent ->
                torrents[id] = torrent.copy(status = 0) // stopped
            }
        }

        val response = JSONObject().apply {
            put("result", "success")
            put("arguments", JSONObject())
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleTorrentStart(request: JSONObject): MockResponse {
        val arguments = request.getJSONObject("arguments")
        val ids = arguments.optJSONArray("ids") ?: return createErrorResponse("Missing torrent IDs")

        for (i in 0 until ids.length()) {
            val id = ids.getInt(i)
            torrents[id]?.let { torrent ->
                torrents[id] = torrent.copy(status = 4) // downloading
                simulateTorrentProgress(id)
            }
        }

        val response = JSONObject().apply {
            put("result", "success")
            put("arguments", JSONObject())
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleSessionGet(): MockResponse {
        val session = JSONObject().apply {
            put("download-dir", "/downloads")
            put("seedRatioLimit", 2.0)
            put("seedRatioLimited", true)
            put("speed-limit-down", 1000) // KB/s
            put("speed-limit-down-enabled", false)
            put("speed-limit-up", 100) // KB/s
            put("speed-limit-up-enabled", false)
            put("version", "4.0.0")
        }

        val response = JSONObject().apply {
            put("result", "success")
            put("arguments", session)
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleSessionStats(): MockResponse {
        val stats = JSONObject().apply {
            put("activeTorrentCount", torrents.count { it.value.status == 4 })
            put("downloadSpeed", torrents.values.sumOf { if (it.status == 4) it.downloadSpeed else 0 })
            put("uploadSpeed", torrents.values.sumOf { if (it.status in listOf(4, 6)) it.uploadSpeed else 0 })
            put("pausedTorrentCount", torrents.count { it.value.status == 0 })
            put("torrentCount", torrents.size)
        }

        val response = JSONObject().apply {
            put("result", "success")
            put("arguments", stats)
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun extractTorrentName(filename: String, metainfo: String): String {
        return when {
            filename.startsWith("magnet:") -> {
                val dn = filename.substringAfter("dn=").substringBefore("&")
                if (dn.isNotEmpty()) URLDecoder.decode(dn, "UTF-8") else "Magnet Torrent"
            }
            filename.isNotEmpty() -> filename.substringAfterLast("/").substringBefore(".torrent")
            else -> "Torrent File ${torrentIdCounter}"
        }
    }

    private fun generateMockFiles(torrentName: String, totalSize: Long): List<FileInfo> {
        val fileCount = (1..10).random()
        val files = mutableListOf<FileInfo>()

        for (i in 1..fileCount) {
            val fileSize = totalSize / fileCount + (0..totalSize / fileCount / 2).random()
            files.add(FileInfo(
                name = "$torrentName/file_$i.${if (i % 3 == 0) "mp4" else if (i % 3 == 1) "avi" else "mkv"}",
                length = fileSize,
                bytesCompleted = 0
            ))
        }

        return files
    }

    private fun simulateTorrentProgress(torrentId: Int) {
        Thread {
            try {
                var percentDone = torrents[torrentId]?.percentDone ?: 0.0f
                while (percentDone < 1.0f) {
                    Thread.sleep(2000) // Update every 2 seconds
                    percentDone += Random.nextFloat() * (0.02f - 0.005f) + 0.005f // 0.5% to 2% per update
                    if (percentDone > 1.0f) percentDone = 1.0f

                    torrents[torrentId]?.let { torrent ->
                        val downloadedEver = (torrent.totalSize * percentDone).toLong()
                        val newStatus = if (percentDone >= 1.0f) 6 else 4 // seeding or downloading

                        torrents[torrentId] = torrent.copy(
                            percentDone = percentDone,
                            downloadedEver = downloadedEver,
                            status = newStatus,
                            eta = if (percentDone >= 1.0f) -1 else (torrent.eta - 2).coerceAtLeast(0)
                        )

                        // Update file progress
                        val updatedFiles = torrent.files.map { file ->
                            file.copy(bytesCompleted = (file.length * percentDone).toLong())
                        }
                        torrents[torrentId] = torrents[torrentId]!!.copy(files = updatedFiles)
                    }
                }
            } catch (e: Exception) {
                torrents[torrentId]?.let { torrent ->
                    torrents[torrentId] = torrent.copy(status = 0) // stopped on error
                }
            }
        }.start()
    }

    private fun createErrorResponse(message: String): MockResponse {
        val response = JSONObject().apply {
            put("result", "error")
            put("error-message", message)
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun createAuthError(): MockResponse {
        return MockResponse()
            .setResponseCode(401)
            .setBody("Unauthorized")
    }
}