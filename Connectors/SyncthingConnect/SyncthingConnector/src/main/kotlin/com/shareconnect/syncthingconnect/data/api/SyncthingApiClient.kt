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


package com.shareconnect.syncthingconnect.data.api

import android.util.Log
import com.google.gson.Gson
import com.shareconnect.syncthingconnect.data.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class SyncthingApiClient(
    private val baseUrl: String,
    private val apiKey: String
) {
    companion object {
        private const val TAG = "SyncthingApiClient"
        private const val TIMEOUT_SECONDS = 30L
    }

    private val gson = Gson()
    
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("X-API-Key", apiKey)
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl.trimEnd('/') + "/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val apiService = retrofit.create(SyncthingApiService::class.java)

    suspend fun getConfig(): Result<SystemConfig> = executeRequest {
        apiService.getConfig(apiKey)
    }

    suspend fun setConfig(config: SystemConfig): Result<Unit> = executeRequest {
        apiService.updateConfig(apiKey, config)
    }

    suspend fun getStatus(): Result<SystemStatus> = executeRequest {
        apiService.getStatus(apiKey)
    }

    suspend fun getVersion(): Result<SystemVersion> = executeRequest {
        apiService.getVersion(apiKey)
    }

    suspend fun getConnections(): Result<SystemConnections> = executeRequest {
        apiService.getConnections(apiKey)
    }

    suspend fun getDatabaseStatus(folder: String): Result<DatabaseStatus> = executeRequest {
        apiService.getDatabaseStatus(apiKey, folder)
    }

    suspend fun browseDirectory(folder: String, prefix: String = ""): Result<List<DirectoryEntry>> = executeRequest {
        apiService.browseDirectory(apiKey, folder, prefix)
    }

    suspend fun getCompletion(device: String, folder: String): Result<FolderCompletion> = executeRequest {
        apiService.getCompletion(apiKey, device, folder)
    }

    suspend fun scan(folder: String, sub: String? = null): Result<Unit> = executeRequest {
        apiService.scan(apiKey, folder, sub)
    }

    suspend fun restart(): Result<Unit> = executeRequest {
        apiService.restart(apiKey)
    }

    suspend fun shutdown(): Result<Unit> = executeRequest {
        apiService.shutdown(apiKey)
    }

    suspend fun testConnection(): Result<Boolean> = try {
        getStatus().map { true }
    } catch (e: Exception) {
        Log.e(TAG, "Connection test failed", e)
        Result.failure(e)
    }

    private suspend fun <T> executeRequest(block: suspend () -> Response<T>): Result<T> = try {
        val response = block()
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(IOException("HTTP ${response.code()}: ${response.message()}"))
        }
    } catch (e: Exception) {
        Log.e(TAG, "Request error", e)
        Result.failure(e)
    }
}
