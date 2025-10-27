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


package com.shareconnect.paperlessngconnect.data.api

import android.util.Log
import com.shareconnect.paperlessngconnect.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Paperless-ngx API client for interacting with Paperless-ngx Document Management System
 * Based on Paperless-ngx REST API v3
 *
 * @param serverUrl Base URL of the Paperless-ngx server (e.g., "http://192.168.1.100:8000")
 * @param token Authentication token (optional, can be obtained via getToken())
 * @param paperlessApiService Optional service for dependency injection (testing)
 */
class PaperlessApiClient(
    private val serverUrl: String,
    private var token: String? = null,
    paperlessApiService: PaperlessApiService? = null
) {

    private val tag = "PaperlessApiClient"

    /**
     * Get the authorization header value
     */
    private fun getAuthHeader(): String {
        return "Token $token"
    }

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
            .baseUrl(ensureTrailingSlash(serverUrl))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val service: PaperlessApiService = paperlessApiService ?: retrofit.create(PaperlessApiService::class.java)

    private fun ensureTrailingSlash(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }

    // Authentication

    /**
     * Authenticate and get token
     * @param username Username for authentication
     * @param password Password for authentication
     * @return Result with token response
     */
    suspend fun getToken(username: String, password: String): Result<PaperlessTokenResponse> = withContext(Dispatchers.IO) {
        try {
            val request = PaperlessAuthRequest(username, password)
            val response = service.getToken(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    token = it.token // Store token for subsequent requests
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Authentication failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting token", e)
            Result.failure(e)
        }
    }

    /**
     * Set the authentication token manually
     */
    fun setToken(authToken: String) {
        token = authToken
    }

    // Document Operations

    /**
     * Get list of documents with optional filters
     * @param page Page number (1-based)
     * @param pageSize Number of documents per page
     * @param query Search query
     * @param ordering Ordering field (e.g., "created", "-created", "title")
     * @param tagsAll Comma-separated tag IDs (all must match)
     * @param tagsIn Comma-separated tag IDs (any can match)
     * @param documentType Document type ID filter
     * @param correspondent Correspondent ID filter
     * @param storagePath Storage path ID filter
     * @return Result with document list response
     */
    suspend fun getDocuments(
        page: Int? = null,
        pageSize: Int? = null,
        query: String? = null,
        ordering: String? = null,
        tagsAll: List<Int>? = null,
        tagsIn: List<Int>? = null,
        documentType: Int? = null,
        correspondent: Int? = null,
        storagePath: Int? = null
    ): Result<PaperlessDocumentListResponse> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getDocuments(
                token = getAuthHeader(),
                page = page,
                pageSize = pageSize,
                query = query,
                ordering = ordering,
                tagsAll = tagsAll?.joinToString(","),
                tagsIn = tagsIn?.joinToString(","),
                documentType = documentType,
                correspondent = correspondent,
                storagePath = storagePath
            )

            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get documents failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting documents", e)
            Result.failure(e)
        }
    }

    /**
     * Get a specific document by ID
     * @param documentId Document ID
     * @return Result with document details
     */
    suspend fun getDocument(documentId: Int): Result<PaperlessDocument> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getDocument(documentId, getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get document failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting document", e)
            Result.failure(e)
        }
    }

    /**
     * Create a new document (metadata only, use uploadDocument for file upload)
     * @param documentRequest Document creation request
     * @return Result with created document
     */
    suspend fun createDocument(documentRequest: PaperlessDocumentRequest): Result<PaperlessDocument> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.createDocument(getAuthHeader(), documentRequest)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Create document failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error creating document", e)
            Result.failure(e)
        }
    }

    /**
     * Update document metadata
     * @param documentId Document ID
     * @param updates Document update request
     * @return Result with updated document
     */
    suspend fun updateDocument(documentId: Int, updates: PaperlessDocumentUpdateRequest): Result<PaperlessDocument> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.updateDocument(documentId, getAuthHeader(), updates)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Update document failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating document", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a document
     * @param documentId Document ID
     * @return Result indicating success or failure
     */
    suspend fun deleteDocument(documentId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.deleteDocument(documentId, getAuthHeader())
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete document failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting document", e)
            Result.failure(e)
        }
    }

    /**
     * Download document file
     * @param documentId Document ID
     * @param original If true, download original file; if false, download archived version
     * @return Result with response body containing file data
     */
    suspend fun downloadDocument(documentId: Int, original: Boolean = false): Result<ResponseBody> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.downloadDocument(documentId, getAuthHeader(), original)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Download document failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error downloading document", e)
            Result.failure(e)
        }
    }

    /**
     * Get document preview image
     * @param documentId Document ID
     * @return Result with response body containing preview image
     */
    suspend fun getDocumentPreview(documentId: Int): Result<ResponseBody> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getDocumentPreview(documentId, getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get preview failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting document preview", e)
            Result.failure(e)
        }
    }

    /**
     * Get document thumbnail
     * @param documentId Document ID
     * @return Result with response body containing thumbnail image
     */
    suspend fun getDocumentThumbnail(documentId: Int): Result<ResponseBody> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getDocumentThumbnail(documentId, getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get thumbnail failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting document thumbnail", e)
            Result.failure(e)
        }
    }

    /**
     * Upload a document file with optional metadata
     * @param file File to upload
     * @param title Document title
     * @param correspondent Correspondent ID
     * @param documentType Document type ID
     * @param storagePath Storage path ID
     * @param tags List of tag IDs
     * @param created Creation date (ISO 8601 format)
     * @param archiveSerialNumber Archive serial number
     * @return Result with upload response
     */
    suspend fun uploadDocument(
        file: File,
        title: String? = null,
        correspondent: Int? = null,
        documentType: Int? = null,
        storagePath: Int? = null,
        tags: List<Int>? = null,
        created: String? = null,
        archiveSerialNumber: String? = null
    ): Result<PaperlessUploadResponse> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val requestBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val documentPart = MultipartBody.Part.createFormData("document", file.name, requestBody)

            val response = service.uploadDocument(
                token = getAuthHeader(),
                document = documentPart,
                title = title,
                correspondent = correspondent,
                documentType = documentType,
                storagePath = storagePath,
                tags = tags?.joinToString(","),
                created = created,
                archiveSerialNumber = archiveSerialNumber
            )

            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Upload document failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error uploading document", e)
            Result.failure(e)
        }
    }

    // Tag Operations

    /**
     * Get list of tags
     * @param page Page number (1-based)
     * @param pageSize Number of tags per page
     * @param ordering Ordering field
     * @return Result with tag list response
     */
    suspend fun getTags(
        page: Int? = null,
        pageSize: Int? = null,
        ordering: String? = null
    ): Result<PaperlessTagListResponse> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getTags(getAuthHeader(), page, pageSize, ordering)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get tags failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting tags", e)
            Result.failure(e)
        }
    }

    /**
     * Get a specific tag by ID
     * @param tagId Tag ID
     * @return Result with tag details
     */
    suspend fun getTag(tagId: Int): Result<PaperlessTag> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getTag(tagId, getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get tag failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting tag", e)
            Result.failure(e)
        }
    }

    /**
     * Create a new tag
     * @param tag Tag to create
     * @return Result with created tag
     */
    suspend fun createTag(tag: PaperlessTag): Result<PaperlessTag> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.createTag(getAuthHeader(), tag)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Create tag failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error creating tag", e)
            Result.failure(e)
        }
    }

    /**
     * Update a tag
     * @param tagId Tag ID
     * @param tag Updated tag data
     * @return Result with updated tag
     */
    suspend fun updateTag(tagId: Int, tag: PaperlessTag): Result<PaperlessTag> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.updateTag(tagId, getAuthHeader(), tag)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Update tag failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating tag", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a tag
     * @param tagId Tag ID
     * @return Result indicating success or failure
     */
    suspend fun deleteTag(tagId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.deleteTag(tagId, getAuthHeader())
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete tag failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting tag", e)
            Result.failure(e)
        }
    }

    // Correspondent Operations

    /**
     * Get list of correspondents
     * @param page Page number (1-based)
     * @param pageSize Number of correspondents per page
     * @param ordering Ordering field
     * @return Result with correspondent list response
     */
    suspend fun getCorrespondents(
        page: Int? = null,
        pageSize: Int? = null,
        ordering: String? = null
    ): Result<PaperlessCorrespondentListResponse> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getCorrespondents(getAuthHeader(), page, pageSize, ordering)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get correspondents failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting correspondents", e)
            Result.failure(e)
        }
    }

    /**
     * Get a specific correspondent by ID
     * @param correspondentId Correspondent ID
     * @return Result with correspondent details
     */
    suspend fun getCorrespondent(correspondentId: Int): Result<PaperlessCorrespondent> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getCorrespondent(correspondentId, getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get correspondent failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting correspondent", e)
            Result.failure(e)
        }
    }

    /**
     * Create a new correspondent
     * @param correspondent Correspondent to create
     * @return Result with created correspondent
     */
    suspend fun createCorrespondent(correspondent: PaperlessCorrespondent): Result<PaperlessCorrespondent> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.createCorrespondent(getAuthHeader(), correspondent)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Create correspondent failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error creating correspondent", e)
            Result.failure(e)
        }
    }

    /**
     * Update a correspondent
     * @param correspondentId Correspondent ID
     * @param correspondent Updated correspondent data
     * @return Result with updated correspondent
     */
    suspend fun updateCorrespondent(correspondentId: Int, correspondent: PaperlessCorrespondent): Result<PaperlessCorrespondent> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.updateCorrespondent(correspondentId, getAuthHeader(), correspondent)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Update correspondent failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating correspondent", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a correspondent
     * @param correspondentId Correspondent ID
     * @return Result indicating success or failure
     */
    suspend fun deleteCorrespondent(correspondentId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.deleteCorrespondent(correspondentId, getAuthHeader())
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete correspondent failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting correspondent", e)
            Result.failure(e)
        }
    }

    // Document Type Operations

    /**
     * Get list of document types
     * @param page Page number (1-based)
     * @param pageSize Number of document types per page
     * @param ordering Ordering field
     * @return Result with document type list response
     */
    suspend fun getDocumentTypes(
        page: Int? = null,
        pageSize: Int? = null,
        ordering: String? = null
    ): Result<PaperlessDocumentTypeListResponse> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getDocumentTypes(getAuthHeader(), page, pageSize, ordering)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get document types failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting document types", e)
            Result.failure(e)
        }
    }

    /**
     * Get a specific document type by ID
     * @param documentTypeId Document type ID
     * @return Result with document type details
     */
    suspend fun getDocumentType(documentTypeId: Int): Result<PaperlessDocumentType> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getDocumentType(documentTypeId, getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get document type failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting document type", e)
            Result.failure(e)
        }
    }

    /**
     * Create a new document type
     * @param documentType Document type to create
     * @return Result with created document type
     */
    suspend fun createDocumentType(documentType: PaperlessDocumentType): Result<PaperlessDocumentType> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.createDocumentType(getAuthHeader(), documentType)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Create document type failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error creating document type", e)
            Result.failure(e)
        }
    }

    /**
     * Update a document type
     * @param documentTypeId Document type ID
     * @param documentType Updated document type data
     * @return Result with updated document type
     */
    suspend fun updateDocumentType(documentTypeId: Int, documentType: PaperlessDocumentType): Result<PaperlessDocumentType> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.updateDocumentType(documentTypeId, getAuthHeader(), documentType)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Update document type failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating document type", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a document type
     * @param documentTypeId Document type ID
     * @return Result indicating success or failure
     */
    suspend fun deleteDocumentType(documentTypeId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.deleteDocumentType(documentTypeId, getAuthHeader())
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete document type failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting document type", e)
            Result.failure(e)
        }
    }

    // Storage Path Operations

    /**
     * Get list of storage paths
     * @param page Page number (1-based)
     * @param pageSize Number of storage paths per page
     * @param ordering Ordering field
     * @return Result with storage path list response
     */
    suspend fun getStoragePaths(
        page: Int? = null,
        pageSize: Int? = null,
        ordering: String? = null
    ): Result<PaperlessStoragePathListResponse> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getStoragePaths(getAuthHeader(), page, pageSize, ordering)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get storage paths failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting storage paths", e)
            Result.failure(e)
        }
    }

    /**
     * Get a specific storage path by ID
     * @param storagePathId Storage path ID
     * @return Result with storage path details
     */
    suspend fun getStoragePath(storagePathId: Int): Result<PaperlessStoragePath> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getStoragePath(storagePathId, getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get storage path failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting storage path", e)
            Result.failure(e)
        }
    }

    /**
     * Create a new storage path
     * @param storagePath Storage path to create
     * @return Result with created storage path
     */
    suspend fun createStoragePath(storagePath: PaperlessStoragePath): Result<PaperlessStoragePath> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.createStoragePath(getAuthHeader(), storagePath)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Create storage path failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error creating storage path", e)
            Result.failure(e)
        }
    }

    /**
     * Update a storage path
     * @param storagePathId Storage path ID
     * @param storagePath Updated storage path data
     * @return Result with updated storage path
     */
    suspend fun updateStoragePath(storagePathId: Int, storagePath: PaperlessStoragePath): Result<PaperlessStoragePath> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.updateStoragePath(storagePathId, getAuthHeader(), storagePath)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Update storage path failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating storage path", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a storage path
     * @param storagePathId Storage path ID
     * @return Result indicating success or failure
     */
    suspend fun deleteStoragePath(storagePathId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.deleteStoragePath(storagePathId, getAuthHeader())
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete storage path failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting storage path", e)
            Result.failure(e)
        }
    }

    // Search

    /**
     * Search for documents
     * @param query Search query string
     * @param page Page number (1-based)
     * @param pageSize Number of results per page
     * @return Result with search results
     */
    suspend fun search(
        query: String,
        page: Int? = null,
        pageSize: Int? = null
    ): Result<PaperlessSearchResult> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.search(getAuthHeader(), query, page, pageSize)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Search failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error searching", e)
            Result.failure(e)
        }
    }

    // Bulk Operations

    /**
     * Perform bulk edit on documents
     * @param request Bulk edit request
     * @return Result indicating success or failure
     */
    suspend fun bulkEdit(request: PaperlessBulkEditRequest): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.bulkEdit(getAuthHeader(), request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Bulk edit failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error performing bulk edit", e)
            Result.failure(e)
        }
    }

    // Task Management

    /**
     * Get task status
     * @param taskId Task ID
     * @return Result with task status
     */
    suspend fun getTaskStatus(taskId: String): Result<PaperlessTaskStatus> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getTaskStatus(taskId, getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get task status failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting task status", e)
            Result.failure(e)
        }
    }

    // Saved Views

    /**
     * Get list of saved views
     * @return Result with saved views list
     */
    suspend fun getSavedViews(): Result<List<PaperlessSavedView>> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getSavedViews(getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get saved views failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting saved views", e)
            Result.failure(e)
        }
    }

    /**
     * Get a specific saved view by ID
     * @param viewId Saved view ID
     * @return Result with saved view details
     */
    suspend fun getSavedView(viewId: Int): Result<PaperlessSavedView> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getSavedView(viewId, getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get saved view failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting saved view", e)
            Result.failure(e)
        }
    }

    // Workflows

    /**
     * Get list of workflows
     * @return Result with workflows list
     */
    suspend fun getWorkflows(): Result<List<PaperlessWorkflow>> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getWorkflows(getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get workflows failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting workflows", e)
            Result.failure(e)
        }
    }

    /**
     * Get a specific workflow by ID
     * @param workflowId Workflow ID
     * @return Result with workflow details
     */
    suspend fun getWorkflow(workflowId: Int): Result<PaperlessWorkflow> = withContext(Dispatchers.IO) {
        try {
            if (token == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = service.getWorkflow(workflowId, getAuthHeader())
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get workflow failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting workflow", e)
            Result.failure(e)
        }
    }
}
