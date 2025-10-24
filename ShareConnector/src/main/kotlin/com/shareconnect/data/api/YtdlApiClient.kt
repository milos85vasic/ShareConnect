package com.shareconnect.data.api

import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * YT-DLP API client
 * Command-line downloader wrapper
 */
class YtdlApiClient(
    private val serverUrl: String,
    private val username: String? = null,
    private val password: String? = null
) {
    private val tag = "YtdlApiClient"

    private val baseUrl = serverUrl.removeSuffix("/")

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
     * Add URL for download
     */
    suspend fun addUrl(url: String, format: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val formBodyBuilder = FormBody.Builder()
                .add("url", url)

            format?.let { formBodyBuilder.add("format", it) }

            val requestBuilder = Request.Builder()
                .url("$baseUrl/download")
                .post(formBodyBuilder.build())

            authHeader?.let { requestBuilder.addHeader("Authorization", it) }

            val request = requestBuilder.build()
            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                response.close()
                Result.success(Unit)
            } else {
                val errorBody = response.body?.string() ?: "Unknown error"
                response.close()
                Result.failure(Exception("Add URL failed: ${response.code} $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error adding URL", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "YtdlApiClient"
    }
}
