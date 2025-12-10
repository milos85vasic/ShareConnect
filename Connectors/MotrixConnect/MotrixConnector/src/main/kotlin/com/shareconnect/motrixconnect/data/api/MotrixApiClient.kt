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


package com.shareconnect.motrixconnect.data.api

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shareconnect.motrixconnect.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Motrix API client using JSON-RPC protocol
 * Motrix uses Aria2 JSON-RPC interface
 */
class MotrixApiClient(
    private val serverUrl: String,
    private val secret: String? = null, // RPC secret token if configured
    motrixApiService: MotrixApiService? = null,
    private val isStubMode: Boolean = false
) {
    private val tag = "MotrixApiClient"
    private val gson = Gson()

    private val rpcUrl = "${serverUrl.removeSuffix("/")}/jsonrpc"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val service: MotrixApiService = when {
        motrixApiService != null -> motrixApiService
        isStubMode -> {
            Log.d(tag, "MotrixApiClient initialized in STUB MODE - using test data")
            MotrixApiStubService(requireAuth = secret != null)
        }
        else -> MotrixApiLiveService(okHttpClient, rpcUrl, secret, gson)
    }

    /**
     * Execute JSON-RPC method (used by live service)
     */
    private suspend fun <T> executeRpc(method: String, params: List<Any> = emptyList(), typeToken: TypeToken<MotrixRpcResponse<T>>): Result<T> = withContext(Dispatchers.IO) {
        try {
            // Add secret token as first parameter if configured
            val finalParams = if (secret != null) {
                listOf("token:$secret") + params
            } else {
                params
            }

            val request = MotrixRpcRequest(
                id = UUID.randomUUID().toString(),
                method = method,
                params = finalParams
            )

            val requestBody = gson.toJson(request).toRequestBody("application/json".toMediaType())

            val httpRequest = Request.Builder()
                .url(rpcUrl)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(httpRequest).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val rpcResponse: MotrixRpcResponse<T> = gson.fromJson(responseBody, typeToken.type)

                    if (rpcResponse.error != null) {
                        Result.failure(Exception("RPC Error ${rpcResponse.error.code}: ${rpcResponse.error.message}"))
                    } else {
                        rpcResponse.result?.let { Result.success(it) }
                            ?: Result.failure(Exception("Empty result"))
                    }
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error executing RPC method: $method", e)
            Result.failure(e)
        }
    }

    /**
     * Helper to convert RpcResponse to Result
     */
    private fun <T> handleRpcResponse(response: MotrixRpcResponse<T>): Result<T> {
        return if (response.error != null) {
            Result.failure(Exception("RPC Error ${response.error.code}: ${response.error.message}"))
        } else {
            response.result?.let { Result.success(it) }
                ?: Result.failure(Exception("Empty result"))
        }
    }

    /**
     * Get Motrix/Aria2 version
     */
    suspend fun getVersion(): Result<MotrixVersion> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.getVersion())
        } catch (e: Exception) {
            Log.e(tag, "Error getting version", e)
            Result.failure(e)
        }
    }

    /**
     * Get global statistics
     */
    suspend fun getGlobalStat(): Result<MotrixGlobalStat> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.getGlobalStat())
        } catch (e: Exception) {
            Log.e(tag, "Error getting global stat", e)
            Result.failure(e)
        }
    }

    /**
     * Add a new download by URI
     */
    suspend fun addUri(uri: String, options: MotrixDownloadOptions? = null): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.addUri(listOf(uri), options))
        } catch (e: Exception) {
            Log.e(tag, "Error adding URI", e)
            Result.failure(e)
        }
    }

    /**
     * Add multiple URIs for the same download
     */
    suspend fun addUris(uris: List<String>, options: MotrixDownloadOptions? = null): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.addUri(uris, options))
        } catch (e: Exception) {
            Log.e(tag, "Error adding URIs", e)
            Result.failure(e)
        }
    }

    /**
     * Get download status by GID
     */
    suspend fun tellStatus(gid: String): Result<MotrixDownload> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.tellStatus(gid))
        } catch (e: Exception) {
            Log.e(tag, "Error getting status", e)
            Result.failure(e)
        }
    }

    /**
     * Get list of active downloads
     */
    suspend fun tellActive(): Result<List<MotrixDownload>> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.tellActive())
        } catch (e: Exception) {
            Log.e(tag, "Error getting active downloads", e)
            Result.failure(e)
        }
    }

    /**
     * Get list of waiting downloads
     */
    suspend fun tellWaiting(offset: Int = 0, limit: Int = 100): Result<List<MotrixDownload>> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.tellWaiting(offset, limit))
        } catch (e: Exception) {
            Log.e(tag, "Error getting waiting downloads", e)
            Result.failure(e)
        }
    }

    /**
     * Get list of stopped downloads
     */
    suspend fun tellStopped(offset: Int = 0, limit: Int = 100): Result<List<MotrixDownload>> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.tellStopped(offset, limit))
        } catch (e: Exception) {
            Log.e(tag, "Error getting stopped downloads", e)
            Result.failure(e)
        }
    }

    /**
     * Pause a download
     */
    suspend fun pause(gid: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.pause(gid))
        } catch (e: Exception) {
            Log.e(tag, "Error pausing download", e)
            Result.failure(e)
        }
    }

    /**
     * Pause all active downloads
     */
    suspend fun pauseAll(): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.pauseAll())
        } catch (e: Exception) {
            Log.e(tag, "Error pausing all downloads", e)
            Result.failure(e)
        }
    }

    /**
     * Resume a paused download
     */
    suspend fun unpause(gid: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.unpause(gid))
        } catch (e: Exception) {
            Log.e(tag, "Error resuming download", e)
            Result.failure(e)
        }
    }

    /**
     * Resume all paused downloads
     */
    suspend fun unpauseAll(): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.unpauseAll())
        } catch (e: Exception) {
            Log.e(tag, "Error resuming all downloads", e)
            Result.failure(e)
        }
    }

    /**
     * Remove a download
     */
    suspend fun remove(gid: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.remove(gid))
        } catch (e: Exception) {
            Log.e(tag, "Error removing download", e)
            Result.failure(e)
        }
    }

    /**
     * Force remove a download
     */
    suspend fun forceRemove(gid: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.forceRemove(gid))
        } catch (e: Exception) {
            Log.e(tag, "Error force removing download", e)
            Result.failure(e)
        }
    }

    /**
     * Remove a completed/error/removed download from the list
     */
    suspend fun removeDownloadResult(gid: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.removeDownloadResult(gid))
        } catch (e: Exception) {
            Log.e(tag, "Error removing download result", e)
            Result.failure(e)
        }
    }

    /**
     * Get global options
     */
    suspend fun getGlobalOption(): Result<Map<String, String>> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.getGlobalOption())
        } catch (e: Exception) {
            Log.e(tag, "Error getting global options", e)
            Result.failure(e)
        }
    }

    /**
     * Change global options
     */
    suspend fun changeGlobalOption(options: Map<String, String>): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.changeGlobalOption(options))
        } catch (e: Exception) {
            Log.e(tag, "Error changing global options", e)
            Result.failure(e)
        }
    }

    /**
     * Get download options by GID
     */
    suspend fun getOption(gid: String): Result<Map<String, String>> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.getOption(gid))
        } catch (e: Exception) {
            Log.e(tag, "Error getting download options", e)
            Result.failure(e)
        }
    }

    /**
     * Change download options
     */
    suspend fun changeOption(gid: String, options: Map<String, String>): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.changeOption(gid, options))
        } catch (e: Exception) {
            Log.e(tag, "Error changing download options", e)
            Result.failure(e)
        }
    }

    /**
     * Purge completed/error/removed downloads
     */
    suspend fun purgeDownloadResult(): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.purgeDownloadResult())
        } catch (e: Exception) {
            Log.e(tag, "Error purging download results", e)
            Result.failure(e)
        }
    }

    /**
     * Shutdown Motrix/Aria2
     */
    suspend fun shutdown(): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.shutdown())
        } catch (e: Exception) {
            Log.e(tag, "Error shutting down", e)
            Result.failure(e)
        }
    }

    /**
     * Force shutdown Motrix/Aria2
     */
    suspend fun forceShutdown(): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.forceShutdown())
        } catch (e: Exception) {
            Log.e(tag, "Error force shutting down", e)
            Result.failure(e)
        }
    }

    /**
     * Save session (current downloads state)
     */
    suspend fun saveSession(): Result<String> = withContext(Dispatchers.IO) {
        try {
            handleRpcResponse(service.saveSession())
        } catch (e: Exception) {
            Log.e(tag, "Error saving session", e)
            Result.failure(e)
        }
    }
}
