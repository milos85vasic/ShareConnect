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


package com.shareconnect.nextcloudconnect.data.api

import android.util.Base64
import android.util.Log
import com.shareconnect.nextcloudconnect.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Nextcloud API client for WebDAV and OCS API operations
 */
class NextcloudApiClient(
    private val serverUrl: String,
    private val username: String,
    private val password: String,
    nextcloudApiService: NextcloudApiService? = null,
    private val isStubMode: Boolean = false
) {
    private val tag = "NextcloudApiClient"

    val authHeader: String
        get() {
            val credentials = "$username:$password"
            val encodedCredentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            return "Basic $encodedCredentials"
        }

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
            .baseUrl(serverUrl.removeSuffix("/") + "/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService: NextcloudApiService = when {
        nextcloudApiService != null -> nextcloudApiService
        isStubMode -> {
            Log.d(tag, "NextcloudApiClient initialized in STUB MODE - using test data")
            NextcloudApiStubService()
        }
        else -> retrofit.create(NextcloudApiService::class.java)
    }

    /**
     * Test connection and get server status
     */
    suspend fun getServerStatus(): Result<NextcloudStatus> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getServerStatus()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Status failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting server status", e)
            Result.failure(e)
        }
    }

    /**
     * Get current user information
     */
    suspend fun getUserInfo(): Result<NextcloudUser> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserInfo(authHeader)
            if (response.isSuccessful) {
                response.body()?.ocs?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty user data"))
            } else {
                Result.failure(Exception("User info failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting user info", e)
            Result.failure(e)
        }
    }

    /**
     * List files in a directory
     * Note: Actual WebDAV XML parsing would be needed in production
     */
    suspend fun listFiles(path: String = ""): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.listFiles(username, path, authHeader)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("List files failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error listing files", e)
            Result.failure(e)
        }
    }

    /**
     * Download a file
     */
    suspend fun downloadFile(path: String): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.downloadFile(username, path, authHeader)
            if (response.isSuccessful) {
                response.body()?.bytes()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty file data"))
            } else {
                Result.failure(Exception("Download failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error downloading file", e)
            Result.failure(e)
        }
    }

    /**
     * Upload a file
     */
    suspend fun uploadFile(path: String, data: ByteArray, mimeType: String = "application/octet-stream"): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val requestBody = data.toRequestBody(mimeType.toMediaTypeOrNull())
            val response = apiService.uploadFile(username, path, authHeader, requestBody)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Upload failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error uploading file", e)
            Result.failure(e)
        }
    }

    /**
     * Create a new folder
     */
    suspend fun createFolder(path: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createFolder(username, path, authHeader)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Create folder failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error creating folder", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a file or folder
     */
    suspend fun delete(path: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.delete(username, path, authHeader)
            if (response.isSuccessful || response.code() == 204) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting", e)
            Result.failure(e)
        }
    }

    /**
     * Move or rename a file/folder
     */
    suspend fun move(sourcePath: String, destinationPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val destination = "${serverUrl.removeSuffix("/")}/remote.php/dav/files/$username/$destinationPath"
            val response = apiService.move(username, sourcePath, authHeader, destination)
            if (response.isSuccessful || response.code() == 201) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Move failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error moving", e)
            Result.failure(e)
        }
    }

    /**
     * Copy a file/folder
     */
    suspend fun copy(sourcePath: String, destinationPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val destination = "${serverUrl.removeSuffix("/")}/remote.php/dav/files/$username/$destinationPath"
            val response = apiService.copy(username, sourcePath, authHeader, destination)
            if (response.isSuccessful || response.code() == 201) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Copy failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error copying", e)
            Result.failure(e)
        }
    }

    /**
     * Create a public share link
     */
    suspend fun createShareLink(path: String): Result<NextcloudShare> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createShare(authHeader, path)
            if (response.isSuccessful) {
                response.body()?.ocs?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty share data"))
            } else {
                Result.failure(Exception("Create share failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error creating share", e)
            Result.failure(e)
        }
    }

    /**
     * Get shares for a path
     */
    suspend fun getShares(path: String): Result<List<NextcloudShare>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getShares(authHeader, path)
            if (response.isSuccessful) {
                response.body()?.ocs?.data?.let { Result.success(it) }
                    ?: Result.success(emptyList())
            } else {
                Result.failure(Exception("Get shares failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting shares", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a share
     */
    suspend fun deleteShare(shareId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteShare(authHeader, shareId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete share failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting share", e)
            Result.failure(e)
        }
    }
}
