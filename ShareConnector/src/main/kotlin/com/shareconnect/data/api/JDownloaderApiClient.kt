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


package com.shareconnect.data.api

import android.util.Log
import com.google.gson.Gson
import com.shareconnect.data.model.JDownloaderDevice
import com.shareconnect.data.model.JDownloaderPackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * JDownloader My.JDownloader API client
 * Remote download manager control
 */
class JDownloaderApiClient(
    private val email: String,
    private val password: String,
    private val deviceId: String? = null,
    baseUrl: String = "https://api.jdownloader.org"
) {
    private val tag = "JDownloaderApiClient"
    private val gson = Gson()

    private val apiUrl = baseUrl.removeSuffix("/")
    private var sessionToken: String? = null
    private var regainToken: String? = null

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    /**
     * Connect to My.JDownloader
     */
    suspend fun connect(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("email", email)
                put("password", password)
            }

            val requestBody = json.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$apiUrl/my/connect")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                response.close()

                if (responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)
                    sessionToken = jsonResponse.optString("sessiontoken")
                    regainToken = jsonResponse.optString("regaintoken")
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                response.close()
                Result.failure(Exception("Connect failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error connecting", e)
            Result.failure(e)
        }
    }

    /**
     * List devices
     */
    suspend fun listDevices(): Result<List<JDownloaderDevice>> = withContext(Dispatchers.IO) {
        try {
            ensureConnected()

            val request = Request.Builder()
                .url("$apiUrl/my/listdevices")
                .addHeader("Authorization", "Bearer $sessionToken")
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                response.close()

                if (responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)
                    val listArray = jsonResponse.optJSONArray("list")
                    if (listArray != null) {
                        val devices = mutableListOf<JDownloaderDevice>()
                        for (i in 0 until listArray.length()) {
                            val deviceJson = listArray.getJSONObject(i)
                            devices.add(JDownloaderDevice(
                                id = deviceJson.getString("id"),
                                name = deviceJson.getString("name"),
                                type = deviceJson.getString("type"),
                                status = deviceJson.optString("status", "")
                            ))
                        }
                        Result.success(devices)
                    } else {
                        Result.success(emptyList())
                    }
                } else {
                    Result.success(emptyList())
                }
            } else {
                response.close()
                Result.failure(Exception("List devices failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error listing devices", e)
            Result.failure(e)
        }
    }

    /**
     * Add links to link grabber
     */
    suspend fun addLinks(links: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureConnected()

            val json = JSONObject().apply {
                put("links", links.joinToString("\n"))
            }

            val requestBody = json.toString().toRequestBody("application/json".toMediaType())

            val devicePath = if (deviceId != null) "/device/$deviceId" else ""
            val request = Request.Builder()
                .url("$apiUrl/my$devicePath/linkgrabberv2/addLinks")
                .addHeader("Authorization", "Bearer $sessionToken")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                response.close()
                Result.success(Unit)
            } else {
                response.close()
                Result.failure(Exception("Add links failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error adding links", e)
            Result.failure(e)
        }
    }

    /**
     * Get download packages
     */
    suspend fun getPackages(): Result<List<JDownloaderPackage>> = withContext(Dispatchers.IO) {
        try {
            ensureConnected()

            val devicePath = if (deviceId != null) "/device/$deviceId" else ""
            val request = Request.Builder()
                .url("$apiUrl/my$devicePath/downloadsV2/queryPackages")
                .addHeader("Authorization", "Bearer $sessionToken")
                .post("{}".toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                response.close()

                if (responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)
                    val dataArray = jsonResponse.optJSONArray("data")
                    if (dataArray != null) {
                        val packages = mutableListOf<JDownloaderPackage>()
                        for (i in 0 until dataArray.length()) {
                            val packageJson = dataArray.getJSONObject(i)
                            packages.add(JDownloaderPackage(
                                uuid = packageJson.getString("uuid"),
                                name = packageJson.getString("name"),
                                saveTo = packageJson.optString("saveto", ""),
                                bytesTotal = packageJson.getLong("bytesTotal"),
                                bytesLoaded = packageJson.getLong("bytesLoaded"),
                                enabled = packageJson.getBoolean("enabled"),
                                finished = packageJson.getBoolean("finished")
                            ))
                        }
                        Result.success(packages)
                    } else {
                        Result.success(emptyList())
                    }
                } else {
                    Result.success(emptyList())
                }
            } else {
                response.close()
                Result.failure(Exception("Get packages failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting packages", e)
            Result.failure(e)
        }
    }

    /**
     * Start downloads
     */
    suspend fun startDownloads(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureConnected()

            val devicePath = if (deviceId != null) "/device/$deviceId" else ""
            val request = Request.Builder()
                .url("$apiUrl/my$devicePath/downloadcontroller/start")
                .addHeader("Authorization", "Bearer $sessionToken")
                .post("{}".toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                response.close()
                Result.success(Unit)
            } else {
                response.close()
                Result.failure(Exception("Start downloads failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error starting downloads", e)
            Result.failure(e)
        }
    }

    /**
     * Stop downloads
     */
    suspend fun stopDownloads(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureConnected()

            val devicePath = if (deviceId != null) "/device/$deviceId" else ""
            val request = Request.Builder()
                .url("$apiUrl/my$devicePath/downloadcontroller/stop")
                .addHeader("Authorization", "Bearer $sessionToken")
                .post("{}".toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                response.close()
                Result.success(Unit)
            } else {
                response.close()
                Result.failure(Exception("Stop downloads failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error stopping downloads", e)
            Result.failure(e)
        }
    }

    /**
     * Pause downloads
     */
    suspend fun pauseDownloads(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureConnected()

            val devicePath = if (deviceId != null) "/device/$deviceId" else ""
            val request = Request.Builder()
                .url("$apiUrl/my$devicePath/downloadcontroller/pause")
                .addHeader("Authorization", "Bearer $sessionToken")
                .post("{}".toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                response.close()
                Result.success(Unit)
            } else {
                response.close()
                Result.failure(Exception("Pause downloads failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error pausing downloads", e)
            Result.failure(e)
        }
    }

    /**
     * Ensure connected (auto-connect if needed)
     */
    private suspend fun ensureConnected() {
        if (sessionToken == null) {
            val result = connect()
            if (result.isFailure) {
                throw result.exceptionOrNull() ?: Exception("Connection failed")
            }
        }
    }

    companion object {
        private const val TAG = "JDownloaderApiClient"
    }
}
