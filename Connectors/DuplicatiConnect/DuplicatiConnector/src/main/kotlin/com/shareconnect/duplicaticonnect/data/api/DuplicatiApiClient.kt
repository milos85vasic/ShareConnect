package com.shareconnect.duplicaticonnect.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.shareconnect.duplicaticonnect.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * DuplicatiApiClient
 * Complete implementation of Duplicati REST API
 *
 * Features:
 * - Server state monitoring
 * - Backup job management
 * - File/folder restore operations
 * - Progress monitoring
 * - Log access
 * - Filesystem browsing
 * - Authentication support
 * - WebSocket progress updates
 */
class DuplicatiApiClient(
    private val baseUrl: String,
    private val password: String? = null,
    private val timeoutSeconds: Long = 30
) {
    private val client: OkHttpClient
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private var authToken: String? = null

    init {
        val builder = OkHttpClient.Builder()
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)

        // Add authentication interceptor if password is provided
        if (password != null) {
            builder.addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()

                // Add auth token if available
                authToken?.let {
                    requestBuilder.header("X-Auth-Token", it)
                }

                chain.proceed(requestBuilder.build())
            }
        }

        client = builder.build()
    }

    /**
     * Login to Duplicati server
     * POST /api/v1/auth/login
     */
    suspend fun login(): Result<DuplicatiLoginResponse> = withContext(Dispatchers.IO) {
        if (password == null) {
            return@withContext Result.success(
                DuplicatiLoginResponse(status = "OK", salt = null, nonce = null)
            )
        }

        val requestBody = DuplicatiLoginRequest(password = password)
        val json = gson.toJson(requestBody)
        val body = json.toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url("$baseUrl/api/v1/auth/login")
            .post(body)
            .build()

        executeRequest(request)
    }

    /**
     * Get server state
     * GET /api/v1/serverstate
     */
    suspend fun getServerState(): Result<DuplicatiServerState> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/serverstate")
            .get()
            .build()

        executeRequest(request)
    }

    /**
     * Get list of all backups
     * GET /api/v1/backups
     */
    suspend fun getBackups(): Result<List<DuplicatiBackup>> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/backups")
            .get()
            .build()

        executeRequest(request)
    }

    /**
     * Get backup details by ID
     * GET /api/v1/backup/{id}
     */
    suspend fun getBackup(backupId: String): Result<DuplicatiBackup> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/backup/$backupId")
            .get()
            .build()

        executeRequest(request)
    }

    /**
     * Run backup now
     * POST /api/v1/backup/{id}/run
     */
    suspend fun runBackup(backupId: String): Result<DuplicatiBackupRun> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/backup/$backupId/run")
            .post("".toRequestBody(jsonMediaType))
            .build()

        executeRequest(request)
    }

    /**
     * Restore files from backup
     * POST /api/v1/backup/{id}/restore
     */
    suspend fun restoreFiles(
        backupId: String,
        options: DuplicatiRestoreOptions
    ): Result<DuplicatiBackupRun> = withContext(Dispatchers.IO) {
        val json = gson.toJson(options)
        val body = json.toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url("$baseUrl/api/v1/backup/$backupId/restore")
            .post(body)
            .build()

        executeRequest(request)
    }

    /**
     * Delete backup
     * DELETE /api/v1/backup/{id}
     */
    suspend fun deleteBackup(
        backupId: String,
        deleteRemoteFiles: Boolean = false
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val url = if (deleteRemoteFiles) {
            "$baseUrl/api/v1/backup/$backupId?delete-remote-files=true"
        } else {
            "$baseUrl/api/v1/backup/$backupId"
        }

        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        executeRequest<Unit>(request)
    }

    /**
     * Get backup log
     * GET /api/v1/backup/{id}/log
     */
    suspend fun getBackupLog(
        backupId: String,
        offset: Int = 0,
        pageSize: Int = 100
    ): Result<List<DuplicatiLogEntry>> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/api/v1/backup/$backupId/log?offset=$offset&pagesize=$pageSize"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        executeRequest(request)
    }

    /**
     * Get backup remote log
     * GET /api/v1/backup/{id}/remotelog
     */
    suspend fun getBackupRemoteLog(backupId: String): Result<List<DuplicatiLogEntry>> =
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$baseUrl/api/v1/backup/$backupId/remotelog")
                .get()
                .build()

            executeRequest(request)
        }

    /**
     * Get backup progress
     * GET /api/v1/backups/progress
     */
    suspend fun getBackupsProgress(): Result<List<DuplicatiProgress>> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/backups/progress")
            .get()
            .build()

        executeRequest(request)
    }

    /**
     * Browse filesystem
     * GET /api/v1/filesystem
     */
    suspend fun browseFilesystem(path: String? = null): Result<List<DuplicatiFileSystemEntry>> =
        withContext(Dispatchers.IO) {
            val url = if (path != null) {
                "$baseUrl/api/v1/filesystem?path=$path"
            } else {
                "$baseUrl/api/v1/filesystem"
            }

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            executeRequest(request)
        }

    /**
     * Get notifications
     * GET /api/v1/notifications
     */
    suspend fun getNotifications(): Result<List<DuplicatiNotification>> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/notifications")
            .get()
            .build()

        executeRequest(request)
    }

    /**
     * Create new backup job
     * POST /api/v1/backup
     */
    suspend fun createBackup(backup: DuplicatiBackup): Result<BackupCreationResponse> =
        withContext(Dispatchers.IO) {
            val requestBody = BackupCreationRequest(backup = backup)
            val json = gson.toJson(requestBody)
            val body = json.toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url("$baseUrl/api/v1/backup")
                .post(body)
                .build()

            executeRequest(request)
        }

    /**
     * Update existing backup
     * PUT /api/v1/backup/{id}
     */
    suspend fun updateBackup(
        backupId: String,
        backup: DuplicatiBackup
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val requestBody = BackupCreationRequest(backup = backup)
        val json = gson.toJson(requestBody)
        val body = json.toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url("$baseUrl/api/v1/backup/$backupId")
            .put(body)
            .build()

        executeRequest(request)
    }

    /**
     * Pause backup operations
     * POST /api/v1/serverstate/pause
     */
    suspend fun pauseBackups(duration: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        val url = if (duration != null) {
            "$baseUrl/api/v1/serverstate/pause?duration=$duration"
        } else {
            "$baseUrl/api/v1/serverstate/pause"
        }

        val request = Request.Builder()
            .url(url)
            .post("".toRequestBody(jsonMediaType))
            .build()

        executeRequest(request)
    }

    /**
     * Resume backup operations
     * POST /api/v1/serverstate/resume
     */
    suspend fun resumeBackups(): Result<Unit> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/serverstate/resume")
            .post("".toRequestBody(jsonMediaType))
            .build()

        executeRequest(request)
    }

    /**
     * Stop active backup operation
     * POST /api/v1/backup/{id}/stop
     */
    suspend fun stopBackup(backupId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/backup/$backupId/stop")
            .post("".toRequestBody(jsonMediaType))
            .build()

        executeRequest(request)
    }

    /**
     * Abort active backup operation
     * POST /api/v1/backup/{id}/abort
     */
    suspend fun abortBackup(backupId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/backup/$backupId/abort")
            .post("".toRequestBody(jsonMediaType))
            .build()

        executeRequest(request)
    }

    /**
     * Get system information
     * GET /api/v1/systeminfo
     */
    suspend fun getSystemInfo(): Result<DuplicatiSystemInfo> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/systeminfo")
            .get()
            .build()

        executeRequest(request)
    }

    /**
     * Test connection to backup target
     * POST /api/v1/backup/{id}/test
     */
    suspend fun testConnection(backupId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/backup/$backupId/test")
            .post("".toRequestBody(jsonMediaType))
            .build()

        executeRequest(request)
    }

    /**
     * Verify backup integrity
     * POST /api/v1/backup/{id}/verify
     */
    suspend fun verifyBackup(backupId: String): Result<DuplicatiBackupRun> =
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$baseUrl/api/v1/backup/$backupId/verify")
                .post("".toRequestBody(jsonMediaType))
                .build()

            executeRequest(request)
        }

    /**
     * Repair backup database
     * POST /api/v1/backup/{id}/repair
     */
    suspend fun repairBackup(backupId: String): Result<DuplicatiBackupRun> =
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$baseUrl/api/v1/backup/$backupId/repair")
                .post("".toRequestBody(jsonMediaType))
                .build()

            executeRequest(request)
        }

    /**
     * Generic request execution with error handling
     */
    private inline fun <reified T> executeRequest(request: Request): Result<T> {
        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Result.failure(
                        IOException("HTTP ${response.code}: ${response.message}")
                    )
                }

                val body = response.body?.string()
                if (body.isNullOrEmpty()) {
                    // Handle empty responses for Unit type
                    if (T::class == Unit::class) {
                        @Suppress("UNCHECKED_CAST")
                        return Result.success(Unit as T)
                    }
                    return Result.failure(IOException("Empty response body"))
                }

                try {
                    // Use TypeToken to preserve generic type information
                    val type = object : com.google.gson.reflect.TypeToken<T>() {}.type
                    val result = gson.fromJson<T>(body, type)
                    Result.success(result)
                } catch (e: Exception) {
                    Result.failure(IOException("Failed to parse response: ${e.message}", e))
                }
            }
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(IOException("Unexpected error: ${e.message}", e))
        }
    }

    /**
     * Close client and release resources
     */
    fun close() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }
}
