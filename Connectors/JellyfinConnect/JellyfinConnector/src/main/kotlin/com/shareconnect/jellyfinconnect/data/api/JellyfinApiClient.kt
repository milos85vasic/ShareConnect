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


package com.shareconnect.jellyfinconnect.data.api

import android.util.Log
import com.shareconnect.jellyfinconnect.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Jellyfin API client for interacting with Jellyfin Media Server
 * Based on Jellyfin REST API v1
 *
 * @param serverUrl Base URL of the Jellyfin server (e.g., "http://192.168.1.100:8096")
 * @param deviceId Unique device identifier for authentication
 * @param deviceName Name of the device (e.g., "JellyfinConnect")
 * @param appVersion Application version
 * @param jellyfinApiService Optional service for dependency injection (testing)
 */
class JellyfinApiClient(
    private val serverUrl: String,
    private val deviceId: String = "jellyfin-connect-${System.currentTimeMillis()}",
    private val deviceName: String = "JellyfinConnect",
    private val appVersion: String = "1.0.0",
    jellyfinApiService: JellyfinApiService? = null
) {

    private val tag = "JellyfinApiClient"

    /**
     * Generate the X-Emby-Authorization header value
     * Format: MediaBrowser Client="AppName", Device="DeviceName", DeviceId="DeviceId", Version="Version", Token="AccessToken"
     */
    fun getAuthHeader(token: String? = null): String {
        val parts = mutableListOf(
            "MediaBrowser Client=\"$deviceName\"",
            "Device=\"Android\"",
            "DeviceId=\"$deviceId\"",
            "Version=\"$appVersion\""
        )
        if (!token.isNullOrEmpty()) {
            parts.add("Token=\"$token\"")
        }
        return parts.joinToString(", ")
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ensureTrailingSlash(serverUrl))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val service: JellyfinApiService = jellyfinApiService ?: retrofit.create(JellyfinApiService::class.java)

    private fun ensureTrailingSlash(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }

    private fun buildUrl(path: String): String {
        return "${ensureTrailingSlash(serverUrl)}$path"
    }

    // Authentication methods

    /**
     * Authenticate with username and password
     */
    suspend fun authenticateByName(username: String, password: String): Result<JellyfinAuthResponse> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader()
            val request = JellyfinAuthRequest(username = username, pw = password)
            val response = service.authenticateByName(authHeader, request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Authentication failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error authenticating", e)
            Result.failure(e)
        }
    }

    // Server information methods

    /**
     * Get public server information (no authentication required)
     */
    suspend fun getPublicServerInfo(): Result<JellyfinServerInfo> = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl("System/Info/Public")
            val response = service.getPublicServerInfo(url)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Server info failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting server info", e)
            Result.failure(e)
        }
    }

    /**
     * Get server information (requires authentication)
     */
    suspend fun getServerInfo(token: String): Result<JellyfinServerInfo> = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl("System/Info")
            val authHeader = getAuthHeader(token)
            val response = service.getServerInfo(url, authHeader)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Server info failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting server info", e)
            Result.failure(e)
        }
    }

    /**
     * Get current user information
     */
    suspend fun getCurrentUser(token: String): Result<JellyfinUser> = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl("Users/Me")
            val authHeader = getAuthHeader(token)
            val response = service.getCurrentUser(url, authHeader)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get user failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting current user", e)
            Result.failure(e)
        }
    }

    // Library and media methods

    /**
     * Get user's views (libraries)
     */
    suspend fun getUserViews(userId: String, token: String): Result<List<JellyfinLibrary>> = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl("Users/$userId/Views")
            val authHeader = getAuthHeader(token)
            val response = service.getUserViews(url, userId, authHeader)
            if (response.isSuccessful) {
                val items = response.body()?.items?.map { item ->
                    JellyfinLibrary(
                        name = item.name,
                        serverId = item.serverId,
                        id = item.id,
                        collectionType = item.collectionType,
                        imageTags = item.imageTags,
                        primaryImageAspectRatio = null
                    )
                } ?: emptyList()
                Result.success(items)
            } else {
                Result.failure(Exception("Get views failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting user views", e)
            Result.failure(e)
        }
    }

    /**
     * Get items from a library
     */
    suspend fun getItems(
        userId: String,
        token: String,
        parentId: String? = null,
        includeItemTypes: String? = null,
        recursive: Boolean = false,
        limit: Int = 50,
        startIndex: Int = 0
    ): Result<JellyfinItemsResult> = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl("Users/$userId/Items")
            val authHeader = getAuthHeader(token)
            val response = service.getItems(
                url = url,
                userId = userId,
                parentId = parentId,
                includeItemTypes = includeItemTypes,
                recursive = recursive,
                limit = limit,
                startIndex = startIndex,
                authHeader = authHeader
            )
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get items failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting items", e)
            Result.failure(e)
        }
    }

    /**
     * Get a specific item by ID
     */
    suspend fun getItem(userId: String, itemId: String, token: String): Result<JellyfinItem> = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl("Users/$userId/Items/$itemId")
            val authHeader = getAuthHeader(token)
            val response = service.getItem(url, userId, itemId, authHeader)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get item failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting item", e)
            Result.failure(e)
        }
    }

    // Playback tracking methods

    /**
     * Mark item as played
     */
    suspend fun markPlayed(userId: String, itemId: String, token: String): Result<JellyfinUserData> = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl("Users/$userId/PlayedItems/$itemId")
            val authHeader = getAuthHeader(token)
            val response = service.markPlayed(url, userId, itemId, authHeader)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Mark played failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error marking as played", e)
            Result.failure(e)
        }
    }

    /**
     * Mark item as unplayed
     */
    suspend fun markUnplayed(userId: String, itemId: String, token: String): Result<JellyfinUserData> = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl("Users/$userId/PlayedItems/$itemId")
            val authHeader = getAuthHeader(token)
            val response = service.markUnplayed(url, userId, itemId, authHeader)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Mark unplayed failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error marking as unplayed", e)
            Result.failure(e)
        }
    }

    /**
     * Update playback progress
     */
    suspend fun updateProgress(
        itemId: String,
        positionTicks: Long,
        isPaused: Boolean,
        token: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl("Sessions/Playing/Progress")
            val authHeader = getAuthHeader(token)
            val progress = JellyfinPlaybackProgress(
                itemId = itemId,
                positionTicks = positionTicks,
                isPaused = isPaused
            )
            val response = service.updateProgress(url, authHeader, progress)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Update progress failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating progress", e)
            Result.failure(e)
        }
    }

    // Search methods

    /**
     * Search for items
     */
    suspend fun search(
        searchTerm: String,
        userId: String,
        token: String,
        limit: Int = 50
    ): Result<JellyfinSearchResult> = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl("Search/Hints")
            val authHeader = getAuthHeader(token)
            val response = service.search(url, searchTerm, userId, limit, authHeader)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Search failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error searching", e)
            Result.failure(e)
        }
    }
}
