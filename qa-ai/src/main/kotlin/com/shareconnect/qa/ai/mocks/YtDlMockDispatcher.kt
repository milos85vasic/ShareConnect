package com.shareconnect.qa.ai.mocks

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONObject

/**
 * Mock dispatcher for YT-DLP service
 * Simulates YT-DLP's REST API for downloading videos and extracting metadata
 */
class YtDlMockDispatcher : QueueDispatcher() {

    private val downloads = mutableMapOf<String, DownloadInfo>()
    private var downloadCounter = 0

    data class DownloadInfo(
        val id: String,
        val url: String,
        val status: String,
        val progress: Int = 0,
        val filename: String? = null,
        val format: String = "mp4",
        val quality: String = "best"
    )

    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path ?: return createErrorResponse("Invalid request path")

        return when {
            path == "/api/downloads" && request.method == "GET" -> handleGetDownloads()
            path == "/api/downloads" && request.method == "POST" -> handleCreateDownload(request)
            path.startsWith("/api/downloads/") && request.method == "GET" -> handleGetDownload(path)
            path.startsWith("/api/downloads/") && request.method == "DELETE" -> handleDeleteDownload(path)
            path.startsWith("/api/info") && request.method == "GET" -> handleGetInfo(request)
            path == "/api/formats" && request.method == "GET" -> handleGetFormats(request)
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
                put("format", download.format)
                put("quality", download.quality)
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

        // YT-DLP supports almost any URL, but we'll validate basic format
        if (!isValidUrl(url)) {
            return createErrorResponse("Invalid URL format")
        }

        val format = json.optString("format", "mp4")
        val quality = json.optString("quality", "best")

        val downloadId = "ytdl_${++downloadCounter}"
        val download = DownloadInfo(
            id = downloadId,
            url = url,
            status = "pending",
            progress = 0,
            filename = generateFilename(url, format),
            format = format,
            quality = quality
        )

        downloads[downloadId] = download

        // Simulate download progress
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
                put("format", download.format)
                put("quality", download.quality)
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

    private fun handleGetInfo(request: RecordedRequest): MockResponse {
        val url = request.requestUrl?.queryParameter("url")
            ?: return createErrorResponse("URL parameter required")

        if (!isValidUrl(url)) {
            return createErrorResponse("Invalid URL format")
        }

        // Generate mock video info
        val info = JSONObject().apply {
            put("title", generateTitle(url))
            put("duration", (60..3600).random()) // 1 min to 1 hour
            put("uploader", "Mock Uploader")
            put("view_count", (1000..1000000).random())
            put("upload_date", "20240101")
            put("formats", generateFormats())
            put("thumbnail", "https://example.com/thumbnail.jpg")
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(info.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleGetFormats(request: RecordedRequest): MockResponse {
        val url = request.requestUrl?.queryParameter("url")
            ?: return createErrorResponse("URL parameter required")

        return MockResponse()
            .setResponseCode(200)
            .setBody(JSONObject().put("formats", generateFormats()).toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleHealthCheck(): MockResponse {
        return MockResponse()
            .setResponseCode(200)
            .setBody(JSONObject().put("status", "healthy").put("version", "2024.01.01").toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun isValidUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }

    private fun generateTitle(url: String): String {
        return when {
            url.contains("youtube.com") || url.contains("youtu.be") -> "Amazing YouTube Video"
            url.contains("vimeo.com") -> "Creative Vimeo Video"
            url.contains("twitch.tv") -> "Live Twitch Stream"
            url.contains("tiktok.com") -> "Viral TikTok Video"
            url.contains("instagram.com") -> "Instagram Reel"
            else -> "Downloaded Content"
        }
    }

    private fun generateFormats(): List<JSONObject> {
        return listOf(
            JSONObject().apply {
                put("format_id", "best")
                put("ext", "mp4")
                put("resolution", "1920x1080")
                put("fps", 30)
                put("filesize", 104857600) // 100MB
            },
            JSONObject().apply {
                put("format_id", "worst")
                put("ext", "mp4")
                put("resolution", "640x360")
                put("fps", 24)
                put("filesize", 10485760) // 10MB
            },
            JSONObject().apply {
                put("format_id", "audio_only")
                put("ext", "m4a")
                put("resolution", null as String?)
                put("fps", null as Int?)
                put("filesize", 5242880) // 5MB
            }
        )
    }

    private fun generateFilename(url: String, format: String): String {
        val baseName = when {
            url.contains("youtube.com") || url.contains("youtu.be") -> "youtube_video"
            url.contains("vimeo.com") -> "vimeo_video"
            url.contains("twitch.tv") -> "twitch_stream"
            url.contains("tiktok.com") -> "tiktok_video"
            else -> "download"
        }

        val extension = when (format.lowercase()) {
            "mp4" -> "mp4"
            "webm" -> "webm"
            "m4a" -> "m4a"
            "mp3" -> "mp3"
            else -> "mp4"
        }

        return "${baseName}_${System.currentTimeMillis()}.$extension"
    }

    private fun simulateDownloadProgress(downloadId: String) {
        Thread {
            try {
                var progress = 0
                while (progress < 100) {
                    Thread.sleep(300) // Update every 300ms
                    progress += (3..10).random()
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