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
import java.util.UUID

/**
 * Mock dispatcher for JDownloader API
 * Simulates JDownloader's REST API for download management
 */
class JDownloaderMockDispatcher : QueueDispatcher() {

    private val downloads = mutableMapOf<String, DownloadInfo>()
    private val devices = mutableMapOf<String, DeviceInfo>()
    private var downloadCounter = 0

    data class DownloadInfo(
        val uuid: String,
        val name: String,
        val url: String,
        val status: String,
        val progress: Int,
        val speed: Long,
        val size: Long,
        val downloaded: Long,
        val packageName: String,
        val host: String
    )

    data class DeviceInfo(
        val id: String,
        val name: String,
        val status: String
    )

    init {
        // Initialize with a default device
        devices["default_device"] = DeviceInfo("default_device", "MyJDownloader Device", "ONLINE")
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path ?: return createErrorResponse("Invalid request path")

        return when {
            path == "/jd/api/mobile" && request.method == "POST" -> handleMobileApi(request)
            path.startsWith("/jd/api/") && request.method == "POST" -> handleApiRequest(request)
            path == "/jd/version" && request.method == "GET" -> handleVersion()
            else -> createErrorResponse("Endpoint not found: $path")
        }
    }

    private fun handleMobileApi(request: RecordedRequest): MockResponse {
        val requestBody = request.body.readUtf8()
        val json = JSONObject(requestBody)

        val action = json.optString("action", "")
        val params = json.optJSONArray("params") ?: org.json.JSONArray()

        return when (action) {
            "addLinks" -> handleAddLinks(params)
            "getDownloadLinks" -> handleGetDownloadLinks()
            "startDownloads" -> handleStartDownloads(params)
            "stopDownloads" -> handleStopDownloads(params)
            "removeLinks" -> handleRemoveLinks(params)
            "getDevices" -> handleGetDevices()
            else -> createErrorResponse("Unknown action: $action")
        }
    }

    private fun handleApiRequest(request: RecordedRequest): MockResponse {
        val requestBody = request.body.readUtf8()
        val json = JSONObject(requestBody)

        // JDownloader API uses different format sometimes
        return handleMobileApi(request)
    }

    private fun handleAddLinks(params: org.json.JSONArray): MockResponse {
        if (params.length() == 0) {
            return createErrorResponse("No links provided")
        }

        val linksParam = params.optJSONObject(0) ?: return createErrorResponse("Invalid links format")
        val links = linksParam.optJSONArray("links") ?: return createErrorResponse("No links array")

        val addedLinks = mutableListOf<String>()

        for (i in 0 until links.length()) {
            val link = links.getString(i)
            if (isSupportedUrl(link)) {
                val uuid = UUID.randomUUID().toString()
                val download = DownloadInfo(
                    uuid = uuid,
                    name = extractFileName(link),
                    url = link,
                    status = "OFFLINE",
                    progress = 0,
                    speed = 0,
                    size = estimateFileSize(link),
                    downloaded = 0,
                    packageName = "Mock Package",
                    host = extractHost(link)
                )

                downloads[uuid] = download
                addedLinks.add(uuid)

                downloadCounter++
            }
        }

        val response = JSONObject().apply {
            put("addedLinks", addedLinks.size)
            put("linkIds", org.json.JSONArray(addedLinks))
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleGetDownloadLinks(): MockResponse {
        val linksArray = downloads.values.map { download ->
            JSONObject().apply {
                put("uuid", download.uuid)
                put("name", download.name)
                put("url", download.url)
                put("status", download.status)
                put("progress", download.progress)
                put("speed", download.speed)
                put("size", download.size)
                put("downloaded", download.downloaded)
                put("packageName", download.packageName)
                put("host", download.host)
            }
        }

        val response = JSONObject().apply {
            put("links", linksArray)
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleStartDownloads(params: org.json.JSONArray): MockResponse {
        val linkIds = if (params.length() > 0) {
            params.getJSONArray(0)
        } else {
            // Start all downloads
            org.json.JSONArray(downloads.keys.toList())
        }

        val started = mutableListOf<String>()

        for (i in 0 until linkIds.length()) {
            val linkId = linkIds.getString(i)
            downloads[linkId]?.let { download ->
                downloads[linkId] = download.copy(status = "DOWNLOADING")
                started.add(linkId)
                simulateDownloadProgress(linkId)
            }
        }

        val response = JSONObject().apply {
            put("started", started.size)
            put("linkIds", org.json.JSONArray(started))
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleStopDownloads(params: org.json.JSONArray): MockResponse {
        if (params.length() == 0) {
            return createErrorResponse("No link IDs provided")
        }

        val linkIds = params.getJSONArray(0)
        val stopped = mutableListOf<String>()

        for (i in 0 until linkIds.length()) {
            val linkId = linkIds.getString(i)
            downloads[linkId]?.let { download ->
                downloads[linkId] = download.copy(status = "STOPPED")
                stopped.add(linkId)
            }
        }

        val response = JSONObject().apply {
            put("stopped", stopped.size)
            put("linkIds", org.json.JSONArray(stopped))
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleRemoveLinks(params: org.json.JSONArray): MockResponse {
        if (params.length() == 0) {
            return createErrorResponse("No link IDs provided")
        }

        val linkIds = params.getJSONArray(0)
        var removed = 0

        for (i in 0 until linkIds.length()) {
            val linkId = linkIds.getString(i)
            if (downloads.remove(linkId) != null) {
                removed++
            }
        }

        val response = JSONObject().apply {
            put("removed", removed)
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleGetDevices(): MockResponse {
        val devicesArray = devices.values.map { device ->
            JSONObject().apply {
                put("id", device.id)
                put("name", device.name)
                put("status", device.status)
            }
        }

        val response = JSONObject().apply {
            put("devices", devicesArray)
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun handleVersion(): MockResponse {
        val response = JSONObject().apply {
            put("version", "2.0")
            put("revision", "12345")
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(response.toString())
            .addHeader("Content-Type", "application/json")
    }

    private fun isSupportedUrl(url: String): Boolean {
        val supportedHosts = listOf(
            "mediafire.com", "mega.nz", "google.com", "dropbox.com", "onedrive.com",
            "box.com", "pcloud.com", "rapidgator.net", "uploaded.net", "nitroflare.com",
            "filefactory.com", "fileboom.me", "keep2share.cc", "youtube.com", "youtu.be",
            "vimeo.com", "twitch.tv", "tiktok.com", "instagram.com", "twitter.com",
            "facebook.com", "soundcloud.com", "spotify.com", "dailymotion.com",
            "bilibili.com", "nicovideo.jp", "vevo.com", "bandcamp.com", "mixcloud.com"
        )

        return supportedHosts.any { host ->
            url.contains(host, ignoreCase = true)
        }
    }

    private fun extractFileName(url: String): String {
        return when {
            url.contains("mediafire.com") -> "mediafire_file.zip"
            url.contains("mega.nz") -> "mega_file.rar"
            url.contains("google.com") || url.contains("drive.google.com") -> "drive_file.pdf"
            url.contains("dropbox.com") -> "dropbox_file.docx"
            url.contains("youtube.com") || url.contains("youtu.be") -> "youtube_video.mp4"
            url.contains("vimeo.com") -> "vimeo_video.mp4"
            url.contains("twitch.tv") -> "twitch_stream.mp4"
            else -> "downloaded_file"
        }
    }

    private fun extractHost(url: String): String {
        return try {
            java.net.URL(url).host
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun estimateFileSize(url: String): Long {
        return when {
            url.contains("youtube.com") || url.contains("youtu.be") -> (50 * 1024 * 1024..500 * 1024 * 1024).random().toLong() // 50MB-500MB
            url.contains("vimeo.com") -> (100 * 1024 * 1024..2 * 1024 * 1024 * 1024).random().toLong() // 100MB-2GB
            url.contains("mediafire.com") -> (10 * 1024 * 1024..10 * 1024 * 1024 * 1024).random().toLong() // 10MB-10GB
            url.contains("mega.nz") -> (1 * 1024 * 1024..50 * 1024 * 1024 * 1024).random().toLong() // 1MB-50GB
            else -> (1 * 1024 * 1024..1 * 1024 * 1024 * 1024).random().toLong() // 1MB-1GB
        }
    }

    private fun simulateDownloadProgress(uuid: String) {
        Thread {
            try {
                var progress = downloads[uuid]?.progress ?: 0
                while (progress < 100) {
                    Thread.sleep(1000) // Update every second
                    progress += (1..5).random() // 1-5% per second
                    if (progress > 100) progress = 100

                    downloads[uuid]?.let { download ->
                        val downloaded = (download.size * progress / 100)
                        val speed = if (progress < 100) (100 * 1024..10 * 1024 * 1024).random().toLong() else 0 // 100KB/s to 10MB/s

                        downloads[uuid] = download.copy(
                            status = if (progress >= 100) "FINISHED" else "DOWNLOADING",
                            progress = progress,
                            downloaded = downloaded,
                            speed = speed
                        )
                    }
                }
            } catch (e: Exception) {
                downloads[uuid]?.let { download ->
                    downloads[uuid] = download.copy(status = "FAILED")
                }
            }
        }.start()
    }

    private fun createErrorResponse(message: String): MockResponse {
        return MockResponse()
            .setResponseCode(400)
            .setBody(JSONObject().put("error", message).toString())
            .addHeader("Content-Type", "application/json")
    }
}