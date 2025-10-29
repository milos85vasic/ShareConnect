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


package com.shareconnect.plexconnect.data.api

import android.util.Log
import com.shareconnect.plexconnect.data.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
class PlexApiClient(
    plexApiService: PlexApiService? = null
) {

    private val tag = "PlexApiClient"

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
            .baseUrl("https://plex.tv/") // Default base URL for auth, will be overridden for server calls
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val service: PlexApiService = plexApiService ?: retrofit.create(PlexApiService::class.java)

    // Authentication methods
    suspend fun requestPin(clientIdentifier: String): Result<PlexPinResponse> = withContext(Dispatchers.IO) {
        try {
            val request = PlexPinRequest(
                strong = true,
                product = "PlexConnect",
                clientIdentifier = clientIdentifier
            )
            val response = service.requestPin(request)
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
            val response = service.checkPin(pinId)
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
            val response = service.getServerInfo(serverUrl)
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
            val response = service.getLibraries(serverUrl, token)
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
            val response = service.getLibraryItems(serverUrl, sectionKey, token, limit, offset)
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
            val response = service.getMediaItem(serverUrl, ratingKey, token)
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
            val response = service.getMediaChildren(serverUrl, ratingKey, token)
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
            val response = service.markAsPlayed(serverUrl, key, token = token)
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
            val response = service.markAsUnplayed(serverUrl, key, token = token)
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
            val response = service.updateProgress(serverUrl, key, time = time, token = token)
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
            val response = service.search(serverUrl, query, limit, token)
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

    companion object {
        // Network efficiency: Request deduplication
        private val activeRequests = mutableMapOf<String, Deferred<Result<Any>>>()
        private val requestLock = Mutex()

        // Exponential backoff configuration
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val INITIAL_RETRY_DELAY = 1000L // 1 second

        // Exponential backoff retry logic
        suspend fun <T> executeWithRetry(
            requestId: String,
            block: suspend () -> Result<T>
        ): Result<T> {
            return requestLock.withLock {
                // Check for duplicate request
                activeRequests[requestId]?.let { deferred ->
                    return@withLock try {
                        @Suppress("UNCHECKED_CAST")
                        deferred.await() as Result<T>
                    } catch (e: Exception) {
                        activeRequests.remove(requestId)
                        Result.failure(e)
                    }
                }

                // Create new request with retry logic
                val deferred = CoroutineScope(Dispatchers.IO).async {
                    var lastException: Exception? = null
                    repeat(MAX_RETRY_ATTEMPTS) { attempt ->
                        try {
                            val result = block()
                            activeRequests.remove(requestId)
                            return@async result
                        } catch (e: Exception) {
                            lastException = e
                            if (attempt < MAX_RETRY_ATTEMPTS - 1) {
                                val delay = INITIAL_RETRY_DELAY * (1L shl attempt) // Exponential backoff
                                delay(delay)
                            }
                        }
                    }
                    activeRequests.remove(requestId)
                    Result.failure(lastException ?: Exception("Unknown error"))
                }

                activeRequests[requestId] = deferred
                try {
                    deferred.await()
                } catch (e: Exception) {
                    activeRequests.remove(requestId)
                    Result.failure(e)
                }
            }
        }
    }

}
