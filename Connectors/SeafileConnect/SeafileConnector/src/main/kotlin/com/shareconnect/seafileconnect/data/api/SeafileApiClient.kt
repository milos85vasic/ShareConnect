package com.shareconnect.seafileconnect.data.api

import android.util.Log
import com.google.gson.Gson
import com.shareconnect.seafileconnect.data.models.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Seafile API Client
 * 
 * Provides high-level access to Seafile server functionality including:
 * - Authentication with username/password
 * - Library (repository) management
 * - File and directory operations
 * - Upload and download
 * - Search functionality
 * - Encrypted library support
 */
class SeafileApiClient(
    private val baseUrl: String,
    private val username: String,
    private val password: String
) {
    companion object {
        private const val TAG = "SeafileApiClient"
        private const val TIMEOUT_SECONDS = 30L
    }

    private var authToken: String? = null
    private val gson = Gson()
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()

            // Add auth token if available
            authToken?.let {
                requestBuilder.header("Authorization", "Token $it")
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl.trimEnd('/') + "/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val apiService = retrofit.create(SeafileApiService::class.java)

    /**
     * Authenticate with the Seafile server
     */
    suspend fun authenticate(): Result<AuthToken> = executeRequest {
        val response = apiService.authenticate(username, password)
        if (response.isSuccessful && response.body() != null) {
            val token = response.body()!!
            authToken = token.token
            Result.success(token)
        } else {
            Result.failure(IOException("Authentication failed: ${response.code()}"))
        }
    }

    /**
     * Get account information for the authenticated user
     */
    suspend fun getAccountInfo(): Result<AccountInfo> = executeAuthenticatedRequest {
        apiService.getAccountInfo()
    }

    /**
     * List all accessible libraries
     */
    suspend fun listLibraries(): Result<List<Library>> = executeAuthenticatedRequest {
        apiService.listLibraries()
    }

    /**
     * Get details for a specific library
     */
    suspend fun getLibrary(repoId: String): Result<Library> = executeAuthenticatedRequest {
        apiService.getLibrary(repoId)
    }

    /**
     * List contents of a directory in a library
     */
    suspend fun listDirectory(repoId: String, path: String = "/"): Result<List<DirectoryEntry>> =
        executeAuthenticatedRequest {
            apiService.listDirectory(repoId, path)
        }

    /**
     * Create a new directory
     */
    suspend fun createDirectory(repoId: String, path: String): Result<FileOperationResult> =
        executeAuthenticatedRequest {
            apiService.createDirectory(repoId, path)
        }

    /**
     * Delete a file or directory
     */
    suspend fun deleteItem(repoId: String, path: String): Result<FileOperationResult> =
        executeAuthenticatedRequest {
            apiService.deleteItem(repoId, path)
        }

    /**
     * Move or copy a file/directory
     */
    suspend fun moveFile(
        srcRepoId: String,
        srcPath: String,
        dstRepoId: String,
        dstPath: String
    ): Result<FileOperationResult> = executeAuthenticatedRequest {
        apiService.moveFile(srcRepoId, srcRepoId, srcPath, dstRepoId, dstPath)
    }

    /**
     * Upload a file to a library
     */
    suspend fun uploadFile(
        repoId: String,
        parentDir: String,
        file: File,
        replace: Boolean = true
    ): Result<String> = try {
        ensureAuthenticated()

        // Get upload link
        val linkResponse = apiService.getUploadLink(repoId, parentDir)
        if (!linkResponse.isSuccessful || linkResponse.body() == null) {
            return Result.failure(IOException("Failed to get upload link: ${linkResponse.code()}"))
        }

        val uploadUrl = linkResponse.body()!!.trim('"')

        // Prepare file part
        val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

        // Upload file
        val uploadResponse = apiService.uploadFile(
            uploadUrl,
            parentDir,
            filePart,
            if (replace) 1 else 0
        )

        if (uploadResponse.isSuccessful) {
            Result.success(uploadResponse.body() ?: "Upload successful")
        } else {
            Result.failure(IOException("Upload failed: ${uploadResponse.code()}"))
        }
    } catch (e: Exception) {
        Log.e(TAG, "Upload error", e)
        Result.failure(e)
    }

    /**
     * Get download link for a file
     */
    suspend fun getDownloadLink(repoId: String, path: String): Result<String> =
        executeAuthenticatedRequest {
            val response = apiService.getDownloadLink(repoId, path)
            if (response.isSuccessful && response.body() != null) {
                val downloadUrl = response.body()!!.trim('"')
                Response.success(downloadUrl)
            } else {
                response
            }
        }

    /**
     * Download a file
     */
    suspend fun downloadFile(downloadUrl: String): Result<ByteArray> = try {
        ensureAuthenticated()

        val response = apiService.downloadFile(downloadUrl)
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!.bytes())
        } else {
            Result.failure(IOException("Download failed: ${response.code()}"))
        }
    } catch (e: Exception) {
        Log.e(TAG, "Download error", e)
        Result.failure(e)
    }

    /**
     * Search for files across all libraries
     */
    suspend fun search(query: String, perPage: Int = 25): Result<SearchResponse> =
        executeAuthenticatedRequest {
            apiService.search(query, perPage)
        }

    /**
     * Decrypt an encrypted library
     */
    suspend fun decryptLibrary(repoId: String, password: String): Result<DecryptLibraryResult> =
        executeAuthenticatedRequest {
            apiService.decryptLibrary(repoId, DecryptLibraryRequest(password))
        }

    /**
     * Test connection to the Seafile server
     */
    suspend fun testConnection(): Result<Boolean> = try {
        authenticate().map { true }
    } catch (e: Exception) {
        Log.e(TAG, "Connection test failed", e)
        Result.failure(e)
    }

    /**
     * Ensure authentication is complete before making API calls
     */
    private suspend fun ensureAuthenticated() {
        if (authToken == null) {
            authenticate().getOrThrow()
        }
    }

    /**
     * Execute a request that requires authentication
     */
    private suspend fun <T> executeAuthenticatedRequest(
        block: suspend () -> Response<T>
    ): Result<T> = try {
        ensureAuthenticated()
        val response = block()
        handleResponse(response)
    } catch (e: Exception) {
        Log.e(TAG, "Request error", e)
        Result.failure(e)
    }

    /**
     * Execute a general request
     */
    private suspend fun <T> executeRequest(
        block: suspend () -> Result<T>
    ): Result<T> = try {
        block()
    } catch (e: Exception) {
        Log.e(TAG, "Request error", e)
        Result.failure(e)
    }

    /**
     * Handle API response and convert to Result
     */
    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                gson.fromJson(errorBody, SeafileErrorResponse::class.java)?.getMessage()
                    ?: "HTTP ${response.code()}"
            } catch (e: Exception) {
                "HTTP ${response.code()}: ${errorBody ?: response.message()}"
            }
            Result.failure(IOException(errorMessage))
        }
    }

    /**
     * Clear authentication token (logout)
     */
    fun clearAuth() {
        authToken = null
    }

    /**
     * Check if client is authenticated
     */
    fun isAuthenticated(): Boolean = authToken != null
}
