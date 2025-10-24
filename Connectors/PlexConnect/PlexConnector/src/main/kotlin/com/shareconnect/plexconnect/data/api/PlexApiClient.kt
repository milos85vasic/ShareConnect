package com.shareconnect.plexconnect.data.api

import android.util.Log
import com.shareconnect.plexconnect.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
class PlexApiClient {

    private val tag = "PlexApiClient"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://plex.tv/") // Default base URL for auth, will be overridden for server calls
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val plexApiService = retrofit.create(PlexApiService::class.java)

    // Authentication methods
    suspend fun requestPin(clientIdentifier: String): Result<PlexPinResponse> = withContext(Dispatchers.IO) {
        try {
            val request = PlexPinRequest(
                strong = true,
                product = "PlexConnect",
                clientIdentifier = clientIdentifier
            )
            val response = plexApiService.requestPin(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Request failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error requesting PIN", e)
            Result.failure(e)
        }
    }

    suspend fun checkPin(pinId: Long): Result<PlexPinResponse> = withContext(Dispatchers.IO) {
        try {
            val response = plexApiService.checkPin(pinId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Check failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error checking PIN", e)
            Result.failure(e)
        }
    }

    // Server interaction methods
    suspend fun getServerInfo(serverUrl: String): Result<PlexServerInfo> = withContext(Dispatchers.IO) {
        try {
            val response = plexApiService.getServerInfo(serverUrl)
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

    suspend fun getLibraries(serverUrl: String, token: String): Result<List<PlexLibrary>> = withContext(Dispatchers.IO) {
        try {
            val response = plexApiService.getLibraries(serverUrl, token)
            if (response.isSuccessful) {
                val libraries = response.body()?.mediaContainer?.Directory ?: emptyList()
                Result.success(libraries)
            } else {
                Result.failure(Exception("Libraries failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting libraries", e)
            Result.failure(e)
        }
    }

    suspend fun getLibraryItems(
        serverUrl: String,
        sectionKey: String,
        token: String,
        limit: Int = 50,
        offset: Int = 0
    ): Result<List<PlexMediaItem>> = withContext(Dispatchers.IO) {
        try {
            val response = plexApiService.getLibraryItems(serverUrl, sectionKey, token, limit, offset)
            if (response.isSuccessful) {
                val items = response.body()?.mediaContainer?.Metadata ?: emptyList()
                Result.success(items)
            } else {
                Result.failure(Exception("Library items failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting library items", e)
            Result.failure(e)
        }
    }

    suspend fun getMediaItem(serverUrl: String, ratingKey: String, token: String): Result<PlexMediaItem?> = withContext(Dispatchers.IO) {
        try {
            val response = plexApiService.getMediaItem(serverUrl, ratingKey, token)
            if (response.isSuccessful) {
                val item = response.body()?.mediaContainer?.Metadata?.firstOrNull()
                Result.success(item)
            } else {
                Result.failure(Exception("Media item failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting media item", e)
            Result.failure(e)
        }
    }

    suspend fun getMediaChildren(serverUrl: String, ratingKey: String, token: String): Result<List<PlexMediaItem>> = withContext(Dispatchers.IO) {
        try {
            val response = plexApiService.getMediaChildren(serverUrl, ratingKey, token)
            if (response.isSuccessful) {
                val children = response.body()?.mediaContainer?.Metadata ?: emptyList()
                Result.success(children)
            } else {
                Result.failure(Exception("Media children failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting media children", e)
            Result.failure(e)
        }
    }

    suspend fun markAsPlayed(serverUrl: String, key: String, token: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = plexApiService.markAsPlayed(serverUrl, key, token = token)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Mark played failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error marking as played", e)
            Result.failure(e)
        }
    }

    suspend fun markAsUnplayed(serverUrl: String, key: String, token: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = plexApiService.markAsUnplayed(serverUrl, key, token = token)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Mark unplayed failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error marking as unplayed", e)
            Result.failure(e)
        }
    }

    suspend fun updateProgress(serverUrl: String, key: String, time: Long, token: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = plexApiService.updateProgress(serverUrl, key, time = time, token = token)
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

    suspend fun search(serverUrl: String, query: String, token: String, limit: Int = 50): Result<List<PlexMediaItem>> = withContext(Dispatchers.IO) {
        try {
            val response = plexApiService.search(serverUrl, query, limit, token)
            if (response.isSuccessful) {
                val results = response.body()?.mediaContainer?.Metadata ?: emptyList()
                Result.success(results)
            } else {
                Result.failure(Exception("Search failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error searching", e)
            Result.failure(e)
        }
    }
}