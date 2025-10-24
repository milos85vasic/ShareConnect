package com.shareconnect.data.api

import android.util.Base64
import android.util.Log
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
     * Add download with comprehensive options
     */
    suspend fun addDownload(
        url: String,
        outputTemplate: String? = null,
        format: String? = null,
        audioOnly: Boolean = false,
        postProcessor: String? = null,
        postProcessorArgs: String? = null,
        writeSubtitles: Boolean = false,
        subtitleLanguages: String? = null,
        embedThumbnail: Boolean = false
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("url", url)
                outputTemplate?.let { put("output_template", it) }
                format?.let { put("format", it) }
                if (audioOnly) put("audio_only", true)
                postProcessor?.let { put("post_processor", it) }
                postProcessorArgs?.let { put("post_processor_args", it) }
                if (writeSubtitles) put("write_subtitles", true)
                subtitleLanguages?.let { put("subtitle_languages", it) }
                if (embedThumbnail) put("embed_thumbnail", true)
            }

            val requestBody = json.toString().toRequestBody("application/json".toMediaType())

            val requestBuilder = Request.Builder()
                .url("$baseUrl/download")
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
     * Add URL for download (simplified version)
     */
    suspend fun addUrl(url: String, format: String? = null): Result<Unit> =
        addDownload(url = url, format = format)

    companion object {
        private const val TAG = "YtdlApiClient"
    }
}
