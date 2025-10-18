package com.shareconnect.qa.ai.mocks

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONObject

/**
 * Mock dispatcher for MeTube service
 * Simulates MeTube's REST API for downloading videos from streaming platforms
 */
class MeTubeMockDispatcher : QueueDispatcher() {

    private val downloads = mutableMapOf<String, DownloadInfo>()
    private var downloadCounter = 0

    data class DownloadInfo(
        val id: String,
        val url: String,
        val status: String,
        val progress: Int = 0,
        val filename: String? = null
    )

    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path ?: return createErrorResponse("Invalid request path")

        return when {
            path == "/api/downloads" && request.method == "GET" -> handleGetDownloads()
            path == "/api/downloads" && request.method == "POST" -> handleCreateDownload(request)
            path.startsWith("/api/downloads/") && request.method == "GET" -> handleGetDownload(path)
            path.startsWith("/api/downloads/") && request.method == "DELETE" -> handleDeleteDownload(path)
            path == "/api/health" -> handleHealthCheck()
            else -> createErrorResponse("Endpoint not found: $path")
        }
    }

    private fun handleGetDownloads(): MockResponse {
        val downloadsArray = downloads.values.map { download ->
            JSONObject().apply {
                put("id", download.id)
                put("url", download.url)
                put("status", download.status)
                put("progress", download.progress)
                put("filename", download.filename)
            }
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(JSONObject().put("downloads", downloadsArray).toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleCreateDownload(request: RecordedRequest): MockResponse {
        val requestBody = request.body.readUtf8()
        val json = JSONObject(requestBody)

        val url = json.optString("url", "")
        if (url.isEmpty()) {
            return createErrorResponse("URL is required")
        }

        // Validate URL is from supported streaming platform
        if (!isSupportedStreamingUrl(url)) {
            return createErrorResponse("Unsupported URL format")
        }

        val downloadId = "download_${++downloadCounter}"
        val download = DownloadInfo(
            id = downloadId,
            url = url,
            status = "pending",
            progress = 0,
            filename = generateFilename(url)
        )

        downloads[downloadId] = download

        // Simulate download progress in background
        simulateDownloadProgress(downloadId)

        val response = JSONObject().apply {
            put("id", downloadId)
            put("status", "pending")
            put("message", "Download queued successfully")
        }

        return MockResponse()
            .setResponseCode(201)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleGetDownload(path: String): MockResponse {
        val downloadId = path.substringAfter("/api/downloads/")
        val download = downloads[downloadId]

        return if (download != null) {
            val response = JSONObject().apply {
                put("id", download.id)
                put("url", download.url)
                put("status", download.status)
                put("progress", download.progress)
                put("filename", download.filename)
            }

            MockResponse()
                .setResponseCode(200)
                .setBody(response.toString())
                .addHeader("Content-Type", "application/json")
        } else {
            createErrorResponse("Download not found", 404)
        }
    }

    private fun handleDeleteDownload(path: String): MockResponse {
        val downloadId = path.substringAfter("/api/downloads/")
        val removed = downloads.remove(downloadId)

        return if (removed != null) {
            MockResponse()
                .setResponseCode(204)
        } else {
            createErrorResponse("Download not found", 404)
        }
    }

    private fun handleHealthCheck(): MockResponse {
        return MockResponse()
            .setResponseCode(200)
            .setBody(JSONObject().put("status", "healthy").toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun isSupportedStreamingUrl(url: String): Boolean {
        val supportedDomains = listOf(
            "youtube.com", "youtu.be", "vimeo.com", "twitch.tv", "tiktok.com",
            "instagram.com", "twitter.com", "x.com", "facebook.com", "fb.com",
            "soundcloud.com", "spotify.com", "dailymotion.com", "bilibili.com",
            "nicovideo.jp", "vevo.com", "bandcamp.com", "mixcloud.com"
        )

        return supportedDomains.any { domain ->
            url.contains(domain, ignoreCase = true)
        }
    }

    private fun generateFilename(url: String): String {
        // Extract video ID or generate meaningful filename
        val videoId = when {
            url.contains("youtube.com") || url.contains("youtu.be") -> "youtube_video"
            url.contains("vimeo.com") -> "vimeo_video"
            url.contains("twitch.tv") -> "twitch_stream"
            url.contains("tiktok.com") -> "tiktok_video"
            else -> "video"
        }

        return "${videoId}_${System.currentTimeMillis()}.mp4"
    }

    private fun simulateDownloadProgress(downloadId: String) {
        Thread {
            try {
                var progress = 0
                while (progress < 100) {
                    Thread.sleep(500) // Update every 500ms
                    progress += (5..15).random() // Random progress increment
                    if (progress > 100) progress = 100

                    downloads[downloadId]?.let { download ->
                        downloads[downloadId] = download.copy(
                            status = if (progress >= 100) "completed" else "downloading",
                            progress = progress
                        )
                    }
                }
            } catch (e: Exception) {
                downloads[downloadId]?.let { download ->
                    downloads[downloadId] = download.copy(status = "failed")
                }
            }
        }.start()
    }

    private fun createErrorResponse(message: String, code: Int = 400): MockResponse {
        return MockResponse()
            .setResponseCode(code)
            .setBody(JSONObject().put("error", message).toString())
            .addHeader("Content-Type", "application/json")
    }
}