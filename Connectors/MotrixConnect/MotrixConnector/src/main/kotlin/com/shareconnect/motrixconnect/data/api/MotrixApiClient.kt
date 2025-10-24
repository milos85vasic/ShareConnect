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
    private val secret: String? = null // RPC secret token if configured
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

    /**
     * Execute JSON-RPC method
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
     * Get Motrix/Aria2 version
     */
    suspend fun getVersion(): Result<MotrixVersion> {
        return executeRpc("aria2.getVersion", emptyList(), object : TypeToken<MotrixRpcResponse<MotrixVersion>>() {})
    }

    /**
     * Get global statistics
     */
    suspend fun getGlobalStat(): Result<MotrixGlobalStat> {
        return executeRpc("aria2.getGlobalStat", emptyList(), object : TypeToken<MotrixRpcResponse<MotrixGlobalStat>>() {})
    }

    /**
     * Add a new download by URI
     */
    suspend fun addUri(uri: String, options: MotrixDownloadOptions? = null): Result<String> {
        val params = if (options != null) {
            listOf(listOf(uri), options)
        } else {
            listOf(listOf(uri))
        }

        return executeRpc("aria2.addUri", params, object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    /**
     * Add multiple URIs for the same download
     */
    suspend fun addUris(uris: List<String>, options: MotrixDownloadOptions? = null): Result<String> {
        val params = if (options != null) {
            listOf(uris, options)
        } else {
            listOf(uris)
        }

        return executeRpc("aria2.addUri", params, object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    /**
     * Get download status by GID
     */
    suspend fun tellStatus(gid: String): Result<MotrixDownload> {
        return executeRpc("aria2.tellStatus", listOf(gid), object : TypeToken<MotrixRpcResponse<MotrixDownload>>() {})
    }

    /**
     * Get list of active downloads
     */
    suspend fun tellActive(): Result<List<MotrixDownload>> {
        return executeRpc("aria2.tellActive", emptyList(), object : TypeToken<MotrixRpcResponse<List<MotrixDownload>>>() {})
    }

    /**
     * Get list of waiting downloads
     */
    suspend fun tellWaiting(offset: Int = 0, limit: Int = 100): Result<List<MotrixDownload>> {
        return executeRpc("aria2.tellWaiting", listOf(offset, limit), object : TypeToken<MotrixRpcResponse<List<MotrixDownload>>>() {})
    }

    /**
     * Get list of stopped downloads
     */
    suspend fun tellStopped(offset: Int = 0, limit: Int = 100): Result<List<MotrixDownload>> {
        return executeRpc("aria2.tellStopped", listOf(offset, limit), object : TypeToken<MotrixRpcResponse<List<MotrixDownload>>>() {})
    }

    /**
     * Pause a download
     */
    suspend fun pause(gid: String): Result<String> {
        return executeRpc("aria2.pause", listOf(gid), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    /**
     * Pause all active downloads
     */
    suspend fun pauseAll(): Result<String> {
        return executeRpc("aria2.pauseAll", emptyList(), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    /**
     * Resume a paused download
     */
    suspend fun unpause(gid: String): Result<String> {
        return executeRpc("aria2.unpause", listOf(gid), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    /**
     * Resume all paused downloads
     */
    suspend fun unpauseAll(): Result<String> {
        return executeRpc("aria2.unpauseAll", emptyList(), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    /**
     * Remove a download
     */
    suspend fun remove(gid: String): Result<String> {
        return executeRpc("aria2.remove", listOf(gid), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    /**
     * Force remove a download
     */
    suspend fun forceRemove(gid: String): Result<String> {
        return executeRpc("aria2.forceRemove", listOf(gid), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    /**
     * Remove a completed/error/removed download from the list
     */
    suspend fun removeDownloadResult(gid: String): Result<String> {
        return executeRpc("aria2.removeDownloadResult", listOf(gid), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    /**
     * Get global options
     */
    suspend fun getGlobalOption(): Result<Map<String, String>> {
        return executeRpc("aria2.getGlobalOption", emptyList(), object : TypeToken<MotrixRpcResponse<Map<String, String>>>() {})
    }

    /**
     * Change global options
     */
    suspend fun changeGlobalOption(options: Map<String, String>): Result<String> {
        return executeRpc("aria2.changeGlobalOption", listOf(options), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    /**
     * Get download options by GID
     */
    suspend fun getOption(gid: String): Result<Map<String, String>> {
        return executeRpc("aria2.getOption", listOf(gid), object : TypeToken<MotrixRpcResponse<Map<String, String>>>() {})
    }

    /**
     * Change download options
     */
    suspend fun changeOption(gid: String, options: Map<String, String>): Result<String> {
        return executeRpc("aria2.changeOption", listOf(gid, options), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    /**
     * Purge completed/error/removed downloads
     */
    suspend fun purgeDownloadResult(): Result<String> {
        return executeRpc("aria2.purgeDownloadResult", emptyList(), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    /**
     * Shutdown Motrix/Aria2
     */
    suspend fun shutdown(): Result<String> {
        return executeRpc("aria2.shutdown", emptyList(), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    /**
     * Force shutdown Motrix/Aria2
     */
    suspend fun forceShutdown(): Result<String> {
        return executeRpc("aria2.forceShutdown", emptyList(), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    /**
     * Save session (current downloads state)
     */
    suspend fun saveSession(): Result<String> {
        return executeRpc("aria2.saveSession", emptyList(), object : TypeToken<MotrixRpcResponse<String>>() {})
    }
}
