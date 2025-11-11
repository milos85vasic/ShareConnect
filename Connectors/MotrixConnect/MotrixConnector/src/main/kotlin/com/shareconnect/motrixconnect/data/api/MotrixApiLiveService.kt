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
import java.util.UUID

/**
 * Live implementation of MotrixApiService that makes actual HTTP calls to Motrix/Aria2 server
 */
class MotrixApiLiveService(
    private val okHttpClient: OkHttpClient,
    private val rpcUrl: String,
    private val secret: String?,
    private val gson: Gson
) : MotrixApiService {

    private val tag = "MotrixApiLiveService"

    /**
     * Execute JSON-RPC method
     */
    private suspend fun <T> executeRpc(method: String, params: List<Any> = emptyList(), typeToken: TypeToken<MotrixRpcResponse<T>>): MotrixRpcResponse<T> = withContext(Dispatchers.IO) {
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
                gson.fromJson(responseBody, typeToken.type)
            } else {
                throw Exception("Empty response body")
            }
        } else {
            throw Exception("HTTP ${response.code}: ${response.message}")
        }
    }

    override suspend fun getVersion(): MotrixRpcResponse<MotrixVersion> {
        return executeRpc("aria2.getVersion", emptyList(), object : TypeToken<MotrixRpcResponse<MotrixVersion>>() {})
    }

    override suspend fun getGlobalStat(): MotrixRpcResponse<MotrixGlobalStat> {
        return executeRpc("aria2.getGlobalStat", emptyList(), object : TypeToken<MotrixRpcResponse<MotrixGlobalStat>>() {})
    }

    override suspend fun addUri(uris: List<String>, options: MotrixDownloadOptions?): MotrixRpcResponse<String> {
        val params = if (options != null) {
            listOf(uris, options)
        } else {
            listOf(uris)
        }
        return executeRpc("aria2.addUri", params, object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    override suspend fun tellStatus(gid: String): MotrixRpcResponse<MotrixDownload> {
        return executeRpc("aria2.tellStatus", listOf(gid), object : TypeToken<MotrixRpcResponse<MotrixDownload>>() {})
    }

    override suspend fun tellActive(): MotrixRpcResponse<List<MotrixDownload>> {
        return executeRpc("aria2.tellActive", emptyList(), object : TypeToken<MotrixRpcResponse<List<MotrixDownload>>>() {})
    }

    override suspend fun tellWaiting(offset: Int, limit: Int): MotrixRpcResponse<List<MotrixDownload>> {
        return executeRpc("aria2.tellWaiting", listOf(offset, limit), object : TypeToken<MotrixRpcResponse<List<MotrixDownload>>>() {})
    }

    override suspend fun tellStopped(offset: Int, limit: Int): MotrixRpcResponse<List<MotrixDownload>> {
        return executeRpc("aria2.tellStopped", listOf(offset, limit), object : TypeToken<MotrixRpcResponse<List<MotrixDownload>>>() {})
    }

    override suspend fun pause(gid: String): MotrixRpcResponse<String> {
        return executeRpc("aria2.pause", listOf(gid), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    override suspend fun pauseAll(): MotrixRpcResponse<String> {
        return executeRpc("aria2.pauseAll", emptyList(), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    override suspend fun unpause(gid: String): MotrixRpcResponse<String> {
        return executeRpc("aria2.unpause", listOf(gid), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    override suspend fun unpauseAll(): MotrixRpcResponse<String> {
        return executeRpc("aria2.unpauseAll", emptyList(), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    override suspend fun remove(gid: String): MotrixRpcResponse<String> {
        return executeRpc("aria2.remove", listOf(gid), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    override suspend fun forceRemove(gid: String): MotrixRpcResponse<String> {
        return executeRpc("aria2.forceRemove", listOf(gid), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    override suspend fun removeDownloadResult(gid: String): MotrixRpcResponse<String> {
        return executeRpc("aria2.removeDownloadResult", listOf(gid), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    override suspend fun getGlobalOption(): MotrixRpcResponse<Map<String, String>> {
        return executeRpc("aria2.getGlobalOption", emptyList(), object : TypeToken<MotrixRpcResponse<Map<String, String>>>() {})
    }

    override suspend fun changeGlobalOption(options: Map<String, String>): MotrixRpcResponse<String> {
        return executeRpc("aria2.changeGlobalOption", listOf(options), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    override suspend fun getOption(gid: String): MotrixRpcResponse<Map<String, String>> {
        return executeRpc("aria2.getOption", listOf(gid), object : TypeToken<MotrixRpcResponse<Map<String, String>>>() {})
    }

    override suspend fun changeOption(gid: String, options: Map<String, String>): MotrixRpcResponse<String> {
        return executeRpc("aria2.changeOption", listOf(gid, options), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    override suspend fun purgeDownloadResult(): MotrixRpcResponse<String> {
        return executeRpc("aria2.purgeDownloadResult", emptyList(), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    override suspend fun saveSession(): MotrixRpcResponse<String> {
        return executeRpc("aria2.saveSession", emptyList(), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    override suspend fun shutdown(): MotrixRpcResponse<String> {
        return executeRpc("aria2.shutdown", emptyList(), object : TypeToken<MotrixRpcResponse<String>>() {})
    }

    override suspend fun forceShutdown(): MotrixRpcResponse<String> {
        return executeRpc("aria2.forceShutdown", emptyList(), object : TypeToken<MotrixRpcResponse<String>>() {})
    }
}
