package com.shareconnect.seafileconnect.data.api

import android.util.Log
import com.google.gson.Gson
import com.shareconnect.seafileconnect.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Retrofit service interface for Seafile API
 */
interface SeafileApiService {

    // Authentication
    @FormUrlEncoded
    @POST("api2/auth-token/")
    suspend fun authenticate(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<SeafileAuthResponse>

    // Account information
    @GET("api2/account/info/")
    suspend fun getAccountInfo(
        @Header("Authorization") token: String
    ): Response<SeafileAccount>

    // List libraries
    @GET("api2/repos/")
    suspend fun listLibraries(
        @Header("Authorization") token: String
    ): Response<List<SeafileLibrary>>

    // Get library details
    @GET("api/v2.1/repos/{repo_id}/")
    suspend fun getLibraryDetails(
        @Path("repo_id") repoId: String,
        @Header("Authorization") token: String
    ): Response<SeafileLibraryDetail>

    // List directory contents
    @GET("api2/repos/{repo_id}/dir/")
    suspend fun listDirectory(
        @Path("repo_id") repoId: String,
        @Query("p") path: String = "/",
        @Header("Authorization") token: String
    ): Response<List<SeafileDirectoryEntry>>

    // Get file detail
    @GET("api2/repos/{repo_id}/file/detail/")
    suspend fun getFileDetail(
        @Path("repo_id") repoId: String,
        @Query("p") path: String,
        @Header("Authorization") token: String
    ): Response<SeafileFile>

    // Get upload link
    @GET("api2/repos/{repo_id}/upload-link/")
    suspend fun getUploadLink(
        @Path("repo_id") repoId: String,
        @Query("p") path: String = "/",
        @Header("Authorization") token: String
    ): Response<String>

    // Upload file using multipart
    @Multipart
    @POST
    suspend fun uploadFile(
        @Url uploadUrl: String,
        @Part file: MultipartBody.Part,
        @Part("parent_dir") parentDir: RequestBody,
        @Part("replace") replace: RequestBody = "0".toRequestBody("text/plain".toMediaTypeOrNull()),
        @Header("Authorization") token: String
    ): Response<String>

    // Get download link
    @GET("api2/repos/{repo_id}/file/")
    suspend fun getDownloadLink(
        @Path("repo_id") repoId: String,
        @Query("p") path: String,
        @Header("Authorization") token: String,
        @Query("reuse") reuse: Int = 1
    ): Response<String>

    // Download file
    @GET
    @Streaming
    suspend fun downloadFile(
        @Url downloadUrl: String,
        @Header("Authorization") token: String
    ): Response<okhttp3.ResponseBody>

    // Create directory
    @POST("api2/repos/{repo_id}/dir/")
    suspend fun createDirectory(
        @Path("repo_id") repoId: String,
        @Query("p") path: String,
        @Header("Authorization") token: String,
        @Body body: RequestBody
    ): Response<String>

    // Delete file or directory
    @DELETE("api2/repos/{repo_id}/file/")
    suspend fun deleteFile(
        @Path("repo_id") repoId: String,
        @Query("p") path: String,
        @Header("Authorization") token: String
    ): Response<String>

    @DELETE("api2/repos/{repo_id}/dir/")
    suspend fun deleteDirectory(
        @Path("repo_id") repoId: String,
        @Query("p") path: String,
        @Header("Authorization") token: String
    ): Response<String>

    // Move file
    @POST("api/v2.1/repos/{repo_id}/file/move/")
    suspend fun moveFile(
        @Path("repo_id") repoId: String,
        @Query("p") path: String,
        @Header("Authorization") token: String,
        @Body body: RequestBody
    ): Response<String>

    // Copy file
    @POST("api/v2.1/repos/{repo_id}/file/copy/")
    suspend fun copyFile(
        @Path("repo_id") repoId: String,
        @Query("p") path: String,
        @Header("Authorization") token: String,
        @Body body: RequestBody
    ): Response<String>

    // Search
    @GET("api2/search/")
    suspend fun search(
        @Query("q") query: String,
        @Query("search_repo") repoId: String? = null,
        @Query("search_path") path: String? = null,
        @Query("per_page") perPage: Int = 25,
        @Header("Authorization") token: String
    ): Response<SeafileSearchResponse>

    // Set encrypted library password
    @POST("api2/repos/{repo_id}/")
    suspend fun setLibraryPassword(
        @Path("repo_id") repoId: String,
        @Header("Authorization") token: String,
        @Body password: RequestBody
    ): Response<SeafileLibraryPasswordResponse>

    // Create share link
    @FormUrlEncoded
    @POST("api/v2.1/share-links/")
    suspend fun createShareLink(
        @Field("repo_id") repoId: String,
        @Field("path") path: String,
        @Field("permissions") permissions: String = "view_download",
        @Header("Authorization") token: String
    ): Response<SeafileShareLink>

    // List share links
    @GET("api/v2.1/share-links/")
    suspend fun listShareLinks(
        @Query("repo_id") repoId: String? = null,
        @Header("Authorization") token: String
    ): Response<List<SeafileShareLink>>

    // Delete share link
    @DELETE("api/v2.1/share-links/{token}/")
    suspend fun deleteShareLink(
        @Path("token") token: String,
        @Header("Authorization") authToken: String
    ): Response<String>

    // Get file history
    @GET("api2/repos/{repo_id}/file/history/")
    suspend fun getFileHistory(
        @Path("repo_id") repoId: String,
        @Query("p") path: String,
        @Header("Authorization") token: String
    ): Response<List<SeafileFileHistory>>
}

/**
 * Seafile API client with full support for encrypted libraries
 */
class SeafileApiClient(
    private val serverUrl: String,
    private val username: String,
    private val password: String
) {
    private val tag = "SeafileApiClient"
    private var authToken: String? = null
    private val encryptedLibraryPasswords = mutableMapOf<String, String>()

    private val gson = Gson()

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(serverUrl.removeSuffix("/") + "/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val apiService: SeafileApiService by lazy {
        retrofit.create(SeafileApiService::class.java)
    }

    /**
     * Get authorization header
     */
    private fun getAuthHeader(): String {
        return "Token $authToken"
    }

    /**
     * Authenticate and obtain auth token
     */
    suspend fun authenticate(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.authenticate(username, password)
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    authToken = authResponse.token
                    Result.success(authResponse.token)
                } ?: Result.failure(Exception("Empty authentication response"))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Authentication failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error during authentication", e)
            Result.failure(e)
        }
    }

    /**
     * Ensure authenticated before making API calls
     */
    private suspend fun ensureAuthenticated(): Result<Unit> {
        if (authToken == null) {
            return authenticate().map { }
        }
        return Result.success(Unit)
    }

    /**
     * Get account information
     */
    suspend fun getAccountInfo(): Result<SeafileAccount> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val response = apiService.getAccountInfo(getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty account info response"))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Get account info failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting account info", e)
            Result.failure(e)
        }
    }

    /**
     * List all libraries
     */
    suspend fun listLibraries(): Result<List<SeafileLibrary>> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val response = apiService.listLibraries(getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.success(emptyList())
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("List libraries failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error listing libraries", e)
            Result.failure(e)
        }
    }

    /**
     * Get library details
     */
    suspend fun getLibraryDetails(repoId: String): Result<SeafileLibraryDetail> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val response = apiService.getLibraryDetails(repoId, getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty library details response"))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Get library details failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting library details", e)
            Result.failure(e)
        }
    }

    /**
     * Set password for encrypted library
     */
    suspend fun setLibraryPassword(repoId: String, libraryPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val passwordBody = gson.toJson(mapOf("password" to libraryPassword))
                .toRequestBody("application/json".toMediaTypeOrNull())

            val response = apiService.setLibraryPassword(repoId, getAuthHeader(), passwordBody)
            if (response.isSuccessful) {
                // Store password for future use
                encryptedLibraryPasswords[repoId] = libraryPassword
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Set library password failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error setting library password", e)
            Result.failure(e)
        }
    }

    /**
     * List directory contents
     */
    suspend fun listDirectory(repoId: String, path: String = "/"): Result<List<SeafileDirectoryEntry>> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val response = apiService.listDirectory(repoId, path, getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.success(emptyList())
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("List directory failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error listing directory", e)
            Result.failure(e)
        }
    }

    /**
     * Get file detail
     */
    suspend fun getFileDetail(repoId: String, path: String): Result<SeafileFile> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val response = apiService.getFileDetail(repoId, path, getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty file detail response"))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Get file detail failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting file detail", e)
            Result.failure(e)
        }
    }

    /**
     * Upload file to library
     */
    suspend fun uploadFile(
        repoId: String,
        file: File,
        destinationPath: String = "/",
        replace: Boolean = false
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            // Get upload link
            val uploadLinkResponse = apiService.getUploadLink(repoId, destinationPath, getAuthHeader())
            if (!uploadLinkResponse.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to get upload link: ${uploadLinkResponse.code()}"))
            }

            val uploadLink = uploadLinkResponse.body()?.trim('"')
                ?: return@withContext Result.failure(Exception("Empty upload link"))

            // Upload file
            val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val parentDirBody = destinationPath.toRequestBody("text/plain".toMediaTypeOrNull())
            val replaceBody = (if (replace) "1" else "0").toRequestBody("text/plain".toMediaTypeOrNull())

            val uploadResponse = apiService.uploadFile(
                uploadLink,
                filePart,
                parentDirBody,
                replaceBody,
                getAuthHeader()
            )

            if (uploadResponse.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = uploadResponse.errorBody()?.string()
                Result.failure(Exception("Upload failed: ${uploadResponse.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error uploading file", e)
            Result.failure(e)
        }
    }

    /**
     * Download file from library
     */
    suspend fun downloadFile(repoId: String, path: String): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            // Get download link
            val downloadLinkResponse = apiService.getDownloadLink(repoId, path, getAuthHeader())
            if (!downloadLinkResponse.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to get download link: ${downloadLinkResponse.code()}"))
            }

            val downloadLink = downloadLinkResponse.body()?.trim('"')
                ?: return@withContext Result.failure(Exception("Empty download link"))

            // Download file
            val downloadResponse = apiService.downloadFile(downloadLink, getAuthHeader())
            if (downloadResponse.isSuccessful) {
                downloadResponse.body()?.bytes()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty download response"))
            } else {
                val errorBody = downloadResponse.errorBody()?.string()
                Result.failure(Exception("Download failed: ${downloadResponse.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error downloading file", e)
            Result.failure(e)
        }
    }

    /**
     * Create directory in library
     */
    suspend fun createDirectory(repoId: String, path: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val body = "operation=mkdir".toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
            val response = apiService.createDirectory(repoId, path, getAuthHeader(), body)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Create directory failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error creating directory", e)
            Result.failure(e)
        }
    }

    /**
     * Delete file or directory
     */
    suspend fun delete(repoId: String, path: String, isDirectory: Boolean = false): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val response = if (isDirectory) {
                apiService.deleteDirectory(repoId, path, getAuthHeader())
            } else {
                apiService.deleteFile(repoId, path, getAuthHeader())
            }

            if (response.isSuccessful || response.code() == 200) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Delete failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting", e)
            Result.failure(e)
        }
    }

    /**
     * Move file or directory
     */
    suspend fun moveFile(
        repoId: String,
        sourcePath: String,
        destRepoId: String,
        destPath: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val moveRequest = MoveOperationRequest(
                operation = "move",
                dstRepo = destRepoId,
                dstDir = destPath
            )
            val body = gson.toJson(moveRequest).toRequestBody("application/json".toMediaTypeOrNull())

            val response = apiService.moveFile(repoId, sourcePath, getAuthHeader(), body)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Move failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error moving file", e)
            Result.failure(e)
        }
    }

    /**
     * Copy file or directory
     */
    suspend fun copyFile(
        repoId: String,
        sourcePath: String,
        destRepoId: String,
        destPath: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val copyRequest = MoveOperationRequest(
                operation = "copy",
                dstRepo = destRepoId,
                dstDir = destPath
            )
            val body = gson.toJson(copyRequest).toRequestBody("application/json".toMediaTypeOrNull())

            val response = apiService.copyFile(repoId, sourcePath, getAuthHeader(), body)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Copy failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error copying file", e)
            Result.failure(e)
        }
    }

    /**
     * Search for files
     */
    suspend fun search(
        query: String,
        repoId: String? = null,
        path: String? = null,
        perPage: Int = 25
    ): Result<SeafileSearchResponse> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val response = apiService.search(query, repoId, path, perPage, getAuthHeader())

            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.success(SeafileSearchResponse())
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Search failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error searching", e)
            Result.failure(e)
        }
    }

    /**
     * Create share link
     */
    suspend fun createShareLink(repoId: String, path: String, permissions: String = "view_download"): Result<SeafileShareLink> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val response = apiService.createShareLink(repoId, path, permissions, getAuthHeader())

            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty share link response"))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Create share link failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error creating share link", e)
            Result.failure(e)
        }
    }

    /**
     * List share links
     */
    suspend fun listShareLinks(repoId: String? = null): Result<List<SeafileShareLink>> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val response = apiService.listShareLinks(repoId, getAuthHeader())

            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.success(emptyList())
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("List share links failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error listing share links", e)
            Result.failure(e)
        }
    }

    /**
     * Delete share link
     */
    suspend fun deleteShareLink(token: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val response = apiService.deleteShareLink(token, getAuthHeader())

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Delete share link failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting share link", e)
            Result.failure(e)
        }
    }

    /**
     * Get file history
     */
    suspend fun getFileHistory(repoId: String, path: String): Result<List<SeafileFileHistory>> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated().getOrElse { return@withContext Result.failure(it) }

            val response = apiService.getFileHistory(repoId, path, getAuthHeader())

            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.success(emptyList())
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Get file history failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting file history", e)
            Result.failure(e)
        }
    }
}
