package com.shareconnect.onlyofficeconnect.data.api

import android.util.Log
import com.shareconnect.onlyofficeconnect.data.models.*
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * ONLYOFFICE API client for interacting with ONLYOFFICE Document Server
 * Supports file management, folder operations, and document editing
 *
 * @param serverUrl Base URL of the ONLYOFFICE server (e.g., "https://office.example.com")
 * @param jwtSecret Optional JWT secret for token generation (used in document editing)
 * @param onlyOfficeApiService Optional service for dependency injection (testing)
 */
class OnlyOfficeApiClient(
    private val serverUrl: String,
    private val jwtSecret: String? = null,
    onlyOfficeApiService: OnlyOfficeApiService? = null
) {

    private val tag = "OnlyOfficeApiClient"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ensureTrailingSlash(serverUrl))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val service: OnlyOfficeApiService = onlyOfficeApiService ?: retrofit.create(OnlyOfficeApiService::class.java)

    private fun ensureTrailingSlash(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }

    /**
     * Generate JWT token for ONLYOFFICE Document Server
     */
    fun generateJwtToken(payload: Map<String, Any>): String? {
        if (jwtSecret.isNullOrEmpty()) return null

        return try {
            Jwts.builder()
                .setClaims(payload)
                .setIssuedAt(Date())
                .setExpiration(Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(SignatureAlgorithm.HS256, jwtSecret.toByteArray())
                .compact()
        } catch (e: Exception) {
            Log.e(tag, "Error generating JWT token", e)
            null
        }
    }

    // Authentication

    /**
     * Authenticate with username and password
     * Returns authentication token
     */
    suspend fun authenticate(username: String, password: String): Result<OnlyOfficeAuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = OnlyOfficeAuthRequest(userName = username, password = password)
            val response = service.authenticate(request)
            handleResponse(response)
        } catch (e: Exception) {
            Log.e(tag, "Error authenticating", e)
            Result.failure(e)
        }
    }

    /**
     * Get server capabilities
     */
    suspend fun getCapabilities(): Result<OnlyOfficeCapabilities> = withContext(Dispatchers.IO) {
        try {
            val response = service.getCapabilities()
            handleResponse(response) { it.response }
        } catch (e: Exception) {
            Log.e(tag, "Error getting capabilities", e)
            Result.failure(e)
        }
    }

    // File Operations

    /**
     * List files in a folder
     */
    suspend fun getFiles(
        token: String,
        folderId: String? = null,
        filterType: Int? = null,
        searchText: String? = null,
        startIndex: Int = 0,
        count: Int = 100
    ): Result<OnlyOfficeFilesData> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val response = service.getFiles(authHeader, folderId, filterType, searchText, startIndex, count)
            handleResponse(response) { it.response }
        } catch (e: Exception) {
            Log.e(tag, "Error getting files", e)
            Result.failure(e)
        }
    }

    /**
     * Get file information by ID
     */
    suspend fun getFileInfo(token: String, fileId: String): Result<OnlyOfficeFile> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val response = service.getFileInfo(authHeader, fileId)
            handleResponse(response)
        } catch (e: Exception) {
            Log.e(tag, "Error getting file info", e)
            Result.failure(e)
        }
    }

    /**
     * Upload a file to a folder
     */
    suspend fun uploadFile(
        token: String,
        folderId: String,
        file: File,
        createNewIfExist: Boolean = false
    ): Result<OnlyOfficeFile> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val createNewParam = createNewIfExist.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = service.uploadFile(authHeader, folderId, body, createNewParam)
            handleResponse(response) { it.response }
        } catch (e: Exception) {
            Log.e(tag, "Error uploading file", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a file
     */
    suspend fun deleteFile(
        token: String,
        fileId: String,
        deleteAfter: Boolean = true,
        immediately: Boolean = false
    ): Result<List<OnlyOfficeOperationProgress>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val response = service.deleteFile(authHeader, fileId, deleteAfter, immediately)
            handleResponse(response) { it.response }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting file", e)
            Result.failure(e)
        }
    }

    /**
     * Download a file
     */
    suspend fun downloadFile(token: String, fileId: String): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val response = service.downloadFile(authHeader, fileId)
            if (response.isSuccessful) {
                response.body()?.bytes()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Download failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error downloading file", e)
            Result.failure(e)
        }
    }

    // Folder Operations

    /**
     * Get folder contents
     */
    suspend fun getFolderContents(
        token: String,
        folderId: String,
        startIndex: Int = 0,
        count: Int = 100
    ): Result<OnlyOfficeFilesData> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val response = service.getFolderContents(authHeader, folderId, startIndex, count)
            handleResponse(response) { it.response }
        } catch (e: Exception) {
            Log.e(tag, "Error getting folder contents", e)
            Result.failure(e)
        }
    }

    /**
     * Create a new folder
     */
    suspend fun createFolder(
        token: String,
        title: String,
        parentId: String? = null
    ): Result<OnlyOfficeFolder> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val response = service.createFolder(authHeader, title, parentId)
            handleResponse(response) { it.response }
        } catch (e: Exception) {
            Log.e(tag, "Error creating folder", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a folder
     */
    suspend fun deleteFolder(
        token: String,
        folderId: String,
        deleteAfter: Boolean = true,
        immediately: Boolean = false
    ): Result<List<OnlyOfficeOperationProgress>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val response = service.deleteFolder(authHeader, folderId, deleteAfter, immediately)
            handleResponse(response) { it.response }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting folder", e)
            Result.failure(e)
        }
    }

    // Move and Copy Operations

    /**
     * Move a file to another folder
     */
    suspend fun moveFile(
        token: String,
        fileId: String,
        destFolderId: String,
        conflictResolveType: Int = 0,
        deleteAfter: Boolean = true
    ): Result<List<OnlyOfficeOperationProgress>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val request = OnlyOfficeMoveRequest(destFolderId, conflictResolveType, deleteAfter)
            val response = service.moveFile(authHeader, request, destFolderId, fileId)
            handleResponse(response) { it.response }
        } catch (e: Exception) {
            Log.e(tag, "Error moving file", e)
            Result.failure(e)
        }
    }

    /**
     * Move a folder to another folder
     */
    suspend fun moveFolder(
        token: String,
        folderId: String,
        destFolderId: String,
        conflictResolveType: Int = 0,
        deleteAfter: Boolean = true
    ): Result<List<OnlyOfficeOperationProgress>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val request = OnlyOfficeMoveRequest(destFolderId, conflictResolveType, deleteAfter)
            val response = service.moveFolder(authHeader, request, destFolderId, folderId)
            handleResponse(response) { it.response }
        } catch (e: Exception) {
            Log.e(tag, "Error moving folder", e)
            Result.failure(e)
        }
    }

    /**
     * Copy a file to another folder
     */
    suspend fun copyFile(
        token: String,
        fileId: String,
        destFolderId: String,
        conflictResolveType: Int = 0
    ): Result<List<OnlyOfficeOperationProgress>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val request = OnlyOfficeCopyRequest(destFolderId, conflictResolveType)
            val response = service.copyFile(authHeader, request, destFolderId, fileId)
            handleResponse(response) { it.response }
        } catch (e: Exception) {
            Log.e(tag, "Error copying file", e)
            Result.failure(e)
        }
    }

    /**
     * Copy a folder to another folder
     */
    suspend fun copyFolder(
        token: String,
        folderId: String,
        destFolderId: String,
        conflictResolveType: Int = 0
    ): Result<List<OnlyOfficeOperationProgress>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val request = OnlyOfficeCopyRequest(destFolderId, conflictResolveType)
            val response = service.copyFolder(authHeader, request, destFolderId, folderId)
            handleResponse(response) { it.response }
        } catch (e: Exception) {
            Log.e(tag, "Error copying folder", e)
            Result.failure(e)
        }
    }

    // Sharing Operations

    /**
     * Share a file with users
     */
    suspend fun shareFile(
        token: String,
        fileId: String,
        shareData: Map<String, Any>
    ): Result<OnlyOfficeFile> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val response = service.shareFile(authHeader, fileId, shareData)
            handleResponse(response)
        } catch (e: Exception) {
            Log.e(tag, "Error sharing file", e)
            Result.failure(e)
        }
    }

    // Document Editor Operations

    /**
     * Get editor configuration for a file
     */
    suspend fun getEditorConfig(token: String, fileId: String): Result<OnlyOfficeEditorConfiguration> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val response = service.getEditorConfig(authHeader, fileId)
            handleResponse(response)
        } catch (e: Exception) {
            Log.e(tag, "Error getting editor config", e)
            Result.failure(e)
        }
    }

    /**
     * Handle document save callback
     */
    suspend fun handleCallback(token: String, callback: OnlyOfficeCallback): Result<OnlyOfficeCallbackResponse> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val response = service.handleCallback(authHeader, callback)
            handleResponse(response)
        } catch (e: Exception) {
            Log.e(tag, "Error handling callback", e)
            Result.failure(e)
        }
    }

    // Helper methods

    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            response.body()?.let { Result.success(it) }
                ?: Result.failure(Exception("Empty response body"))
        } else {
            Result.failure(Exception("Request failed: ${response.code()} ${response.message()}"))
        }
    }

    private fun <T, R> handleResponse(response: Response<T>, transform: (T) -> R): Result<R> {
        return if (response.isSuccessful) {
            response.body()?.let { Result.success(transform(it)) }
                ?: Result.failure(Exception("Empty response body"))
        } else {
            Result.failure(Exception("Request failed: ${response.code()} ${response.message()}"))
        }
    }

    /**
     * Build file type for editor configuration
     */
    fun getDocumentType(fileExtension: String): String {
        return when (fileExtension.lowercase()) {
            "doc", "docx", "docm", "dot", "dotx", "dotm", "odt", "fodt", "ott", "rtf", "txt",
            "html", "htm", "mht", "pdf", "djvu", "fb2", "epub", "xps" -> "word"
            "xls", "xlsx", "xlsm", "xlt", "xltx", "xltm", "ods", "fods", "ots", "csv" -> "cell"
            "ppt", "pptx", "pptm", "pot", "potx", "potm", "odp", "fodp", "otp" -> "slide"
            else -> "word"
        }
    }

    /**
     * Check if file type is editable
     */
    fun isEditableFileType(fileExtension: String): Boolean {
        val editableExtensions = setOf(
            "docx", "xlsx", "pptx", "txt", "csv",
            "odt", "ods", "odp", "doc", "xls", "ppt",
            "rtf", "mht", "html", "htm"
        )
        return fileExtension.lowercase() in editableExtensions
    }
}
