package com.shareconnect.data.api

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.shareconnect.data.model.MeTubeDownload
import com.shareconnect.data.model.MeTubeStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * MeTube API client
 * Self-hosted YouTube downloader
 */
class MeTubeApiClient(
    private val serverUrl: String,
    private val username: String? = null,
    private val password: String? = null
) {
    private val tag = "MeTubeApiClient"
    private val gson = Gson()

    private val baseUrl = "${serverUrl.removeSuffix("/")}"

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
     * Add video download
     */
    suspend fun addDownload(
        url: String,
        quality: String = "best",
        format: String? = null,
        folder: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val download = MeTubeDownload(
                url = url,
                quality = quality,
                format = format,
                folder = folder
            )

            val json = gson.toJson(download)
            val requestBody = json.toRequestBody("application/json".toMediaType())

            val requestBuilder = Request.Builder()
                .url("$baseUrl/add")
                .post(requestBody)

            authHeader?.let { requestBuilder.addHeader("Authorization", it) }

            val request = requestBuilder.build()
            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                response.close()
                Result.success(Unit)
            } else {
                val errorBody = response.body?.string() ?: "Unknown error"
                response.close()
                Result.failure(Exception("Add download failed: ${response.code} $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error adding download", e)
            Result.failure(e)
        }
    }

    /**
     * Get download status
     */
    suspend fun getStatus(): Result<MeTubeStatus> = withContext(Dispatchers.IO) {
        try {
            val requestBuilder = Request.Builder()
                .url("$baseUrl/status")
                .get()

            authHeader?.let { requestBuilder.addHeader("Authorization", it) }

            val request = requestBuilder.build()
            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                response.close()

                if (responseBody != null) {
                    val status = gson.fromJson(responseBody, MeTubeStatus::class.java)
                    Result.success(status)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                response.close()
                Result.failure(Exception("Get status failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting status", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "MeTubeApiClient"
    }
}
