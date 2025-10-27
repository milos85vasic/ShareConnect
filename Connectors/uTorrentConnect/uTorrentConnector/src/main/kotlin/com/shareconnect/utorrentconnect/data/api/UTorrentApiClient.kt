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


package com.shareconnect.utorrentconnect.data.api

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.shareconnect.utorrentconnect.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * uTorrent Web UI API client
 * Implements uTorrent Web API with token-based authentication
 */
class UTorrentApiClient(
    private val serverUrl: String,
    private val username: String? = null,
    private val password: String? = null
) {
    private val tag = "UTorrentApiClient"
    private val gson = Gson()

    private val baseUrl = serverUrl.removeSuffix("/")
    private var cachedToken: String? = null

    private val authHeader: String?
        get() = if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            val credentials = "$username:$password"
            val encodedCredentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            "Basic $encodedCredentials"
        } else null

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    /**
     * Get authentication token from uTorrent
     */
    suspend fun getToken(forceRefresh: Boolean = false): Result<String> = withContext(Dispatchers.IO) {
        if (!forceRefresh && cachedToken != null) {
            return@withContext Result.success(cachedToken!!)
        }

        try {
            val tokenUrl = "$baseUrl/gui/token.html"
            val requestBuilder = Request.Builder()
                .url(tokenUrl)
                .get()

            authHeader?.let { requestBuilder.addHeader("Authorization", it) }

            val request = requestBuilder.build()
            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                response.close()

                if (responseBody != null) {
                    val token = extractTokenFromHtml(responseBody)
                    if (token != null) {
                        cachedToken = token
                        Result.success(token)
                    } else {
                        Result.failure(Exception("Failed to extract token from response"))
                    }
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                response.close()
                Result.failure(Exception("Get token failed: ${response.code} ${response.message}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting token", e)
            Result.failure(e)
        }
    }

    /**
     * Extract token from HTML response
     */
    private fun extractTokenFromHtml(html: String): String? {
        val tokenRegex = "<div[^>]*id=['\"]token['\"][^>]*>([^<]+)</div>".toRegex()
        val matchResult = tokenRegex.find(html)
        return matchResult?.groupValues?.get(1)?.trim()
    }

    /**
     * Execute Web UI action
     */
    private suspend fun executeAction(
        action: String,
        params: Map<String, String> = emptyMap(),
        retryCount: Int = 0
    ): Result<UTorrentResponse> = withContext(Dispatchers.IO) {
        try {
            val tokenResult = getToken()
            if (tokenResult.isFailure) {
                return@withContext Result.failure(tokenResult.exceptionOrNull() ?: Exception("Failed to get token"))
            }

            val token = tokenResult.getOrNull()!!

            val urlBuilder = StringBuilder("$baseUrl/gui/?action=$action&token=$token")
            params.forEach { (key, value) ->
                urlBuilder.append("&$key=$value")
            }

            val requestBuilder = Request.Builder()
                .url(urlBuilder.toString())
                .get()

            authHeader?.let { requestBuilder.addHeader("Authorization", it) }

            val request = requestBuilder.build()
            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                response.close()

                if (responseBody != null) {
                    try {
                        val utorrentResponse = gson.fromJson(responseBody, UTorrentResponse::class.java)
                        Result.success(utorrentResponse)
                    } catch (e: Exception) {
                        Log.e(tag, "Error parsing response", e)
                        Result.failure(e)
                    }
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else if (response.code == 400 && retryCount < 2) {
                // Invalid token, refresh and retry
                response.close()
                cachedToken = null
                return@withContext executeAction(action, params, retryCount + 1)
            } else {
                response.close()
                Result.failure(Exception("Action failed: ${response.code} ${response.message}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error executing action: $action", e)
            Result.failure(e)
        }
    }

    /**
     * Get list of all torrents
     */
    suspend fun getTorrents(): Result<List<UTorrentTorrent>> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("list")
            result.map { response ->
                response.parseTorrents()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting torrents", e)
            Result.failure(e)
        }
    }

    /**
     * Get torrent files
     */
    suspend fun getTorrentFiles(hash: String): Result<List<UTorrentFile>> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("getfiles", mapOf("hash" to hash))
            result.map { response ->
                response.parseFiles()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting torrent files", e)
            Result.failure(e)
        }
    }

    /**
     * Get labels
     */
    suspend fun getLabels(): Result<List<UTorrentLabel>> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("list")
            result.map { response ->
                response.parseLabels()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting labels", e)
            Result.failure(e)
        }
    }

    /**
     * Get RSS feeds
     */
    suspend fun getRssFeeds(): Result<List<UTorrentRssFeed>> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("list-rss")
            result.map { response ->
                response.parseRssFeeds()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting RSS feeds", e)
            Result.failure(e)
        }
    }

    /**
     * Get RSS filters
     */
    suspend fun getRssFilters(): Result<List<UTorrentRssFilter>> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("list-rss")
            result.map { response ->
                response.parseRssFilters()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting RSS filters", e)
            Result.failure(e)
        }
    }

    /**
     * Add torrent by URL
     */
    suspend fun addUrl(url: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("add-url", mapOf("s" to url))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error adding URL", e)
            Result.failure(e)
        }
    }

    /**
     * Start torrent
     */
    suspend fun start(hash: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("start", mapOf("hash" to hash))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error starting torrent", e)
            Result.failure(e)
        }
    }

    /**
     * Stop torrent
     */
    suspend fun stop(hash: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("stop", mapOf("hash" to hash))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error stopping torrent", e)
            Result.failure(e)
        }
    }

    /**
     * Pause torrent
     */
    suspend fun pause(hash: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("pause", mapOf("hash" to hash))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error pausing torrent", e)
            Result.failure(e)
        }
    }

    /**
     * Resume torrent
     */
    suspend fun resume(hash: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("unpause", mapOf("hash" to hash))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error resuming torrent", e)
            Result.failure(e)
        }
    }

    /**
     * Force start torrent
     */
    suspend fun forceStart(hash: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("forcestart", mapOf("hash" to hash))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error force starting torrent", e)
            Result.failure(e)
        }
    }

    /**
     * Recheck torrent
     */
    suspend fun recheck(hash: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("recheck", mapOf("hash" to hash))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error rechecking torrent", e)
            Result.failure(e)
        }
    }

    /**
     * Remove torrent
     */
    suspend fun remove(hash: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("remove", mapOf("hash" to hash))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error removing torrent", e)
            Result.failure(e)
        }
    }

    /**
     * Remove torrent and data
     */
    suspend fun removeData(hash: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("removedata", mapOf("hash" to hash))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error removing torrent and data", e)
            Result.failure(e)
        }
    }

    /**
     * Set torrent label
     */
    suspend fun setLabel(hash: String, label: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("setprops", mapOf("hash" to hash, "s" to "label", "v" to label))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error setting label", e)
            Result.failure(e)
        }
    }

    /**
     * Set file priority
     */
    suspend fun setFilePriority(hash: String, fileIndex: Int, priority: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("setprio", mapOf(
                "hash" to hash,
                "p" to priority.toString(),
                "f" to fileIndex.toString()
            ))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error setting file priority", e)
            Result.failure(e)
        }
    }

    /**
     * Get settings
     */
    suspend fun getSettings(): Result<UTorrentSettings> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("getsettings")
            when {
                result.isSuccess -> {
                    val response = result.getOrNull()!!
                    if (response.settings != null) {
                        try {
                            val settingsMap = mutableMapOf<String, UTorrentSettings.Setting>()
                            response.settings.forEach { setting ->
                                if (setting.size >= 3) {
                                    val key = setting[0] as String
                                    val type = (setting[1] as Number).toInt()
                                    val value = setting[2]
                                    val access = if (setting.size > 3) setting[3] as String else ""
                                    settingsMap[key] = UTorrentSettings.Setting(type, value, access)
                                }
                            }
                            Result.success(UTorrentSettings(settingsMap))
                        } catch (e: Exception) {
                            Log.e(tag, "Error parsing settings", e)
                            Result.failure(e)
                        }
                    } else {
                        Result.failure(Exception("No settings in response"))
                    }
                }
                else -> Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting settings", e)
            Result.failure(e)
        }
    }

    /**
     * Set setting value
     */
    suspend fun setSetting(key: String, value: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("setsetting", mapOf("s" to key, "v" to value))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error setting value", e)
            Result.failure(e)
        }
    }

    /**
     * Create label
     */
    suspend fun createLabel(label: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("create-label", mapOf("s" to label))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error creating label", e)
            Result.failure(e)
        }
    }

    /**
     * Remove label
     */
    suspend fun removeLabel(label: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("remove-label", mapOf("s" to label))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error removing label", e)
            Result.failure(e)
        }
    }

    /**
     * Add RSS feed
     */
    suspend fun addRssFeed(url: String, alias: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val params = mutableMapOf("url" to url)
            alias?.let { params["alias"] = it }
            val result = executeAction("rss-add", params)
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error adding RSS feed", e)
            Result.failure(e)
        }
    }

    /**
     * Remove RSS feed
     */
    suspend fun removeRssFeed(feedId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("rss-remove", mapOf("id" to feedId.toString()))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error removing RSS feed", e)
            Result.failure(e)
        }
    }

    /**
     * Update RSS feed
     */
    suspend fun updateRssFeed(feedId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = executeAction("rss-update", mapOf("id" to feedId.toString()))
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error updating RSS feed", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "UTorrentApiClient"
    }
}
