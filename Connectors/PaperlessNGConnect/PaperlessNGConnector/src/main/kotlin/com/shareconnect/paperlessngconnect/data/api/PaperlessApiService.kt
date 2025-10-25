package com.shareconnect.paperlessngconnect.data.api

import com.shareconnect.paperlessngconnect.data.models.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Paperless-ngx REST API service interface
 * Based on Paperless-ngx API v3
 */
interface PaperlessApiService {

    // Authentication

    @POST("api/token/")
    suspend fun getToken(
        @Body request: PaperlessAuthRequest
    ): Response<PaperlessTokenResponse>

    // Documents

    @GET("api/documents/")
    suspend fun getDocuments(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("query") query: String? = null,
        @Query("ordering") ordering: String? = null,
        @Query("tags__id__all") tagsAll: String? = null,
        @Query("tags__id__in") tagsIn: String? = null,
        @Query("document_type__id") documentType: Int? = null,
        @Query("correspondent__id") correspondent: Int? = null,
        @Query("storage_path__id") storagePath: Int? = null
    ): Response<PaperlessDocumentListResponse>

    @GET("api/documents/{id}/")
    suspend fun getDocument(
        @Path("id") documentId: Int,
        @Header("Authorization") token: String
    ): Response<PaperlessDocument>

    @POST("api/documents/")
    suspend fun createDocument(
        @Header("Authorization") token: String,
        @Body document: PaperlessDocumentRequest
    ): Response<PaperlessDocument>

    @PATCH("api/documents/{id}/")
    suspend fun updateDocument(
        @Path("id") documentId: Int,
        @Header("Authorization") token: String,
        @Body updates: PaperlessDocumentUpdateRequest
    ): Response<PaperlessDocument>

    @DELETE("api/documents/{id}/")
    suspend fun deleteDocument(
        @Path("id") documentId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @GET("api/documents/{id}/download/")
    @Streaming
    suspend fun downloadDocument(
        @Path("id") documentId: Int,
        @Header("Authorization") token: String,
        @Query("original") original: Boolean = false
    ): Response<ResponseBody>

    @GET("api/documents/{id}/preview/")
    @Streaming
    suspend fun getDocumentPreview(
        @Path("id") documentId: Int,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("api/documents/{id}/thumb/")
    @Streaming
    suspend fun getDocumentThumbnail(
        @Path("id") documentId: Int,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Multipart
    @POST("api/documents/post_document/")
    suspend fun uploadDocument(
        @Header("Authorization") token: String,
        @Part document: MultipartBody.Part,
        @Part("title") title: String? = null,
        @Part("correspondent") correspondent: Int? = null,
        @Part("document_type") documentType: Int? = null,
        @Part("storage_path") storagePath: Int? = null,
        @Part("tags") tags: String? = null,
        @Part("created") created: String? = null,
        @Part("archive_serial_number") archiveSerialNumber: String? = null
    ): Response<PaperlessUploadResponse>

    // Tags

    @GET("api/tags/")
    suspend fun getTags(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("ordering") ordering: String? = null
    ): Response<PaperlessTagListResponse>

    @GET("api/tags/{id}/")
    suspend fun getTag(
        @Path("id") tagId: Int,
        @Header("Authorization") token: String
    ): Response<PaperlessTag>

    @POST("api/tags/")
    suspend fun createTag(
        @Header("Authorization") token: String,
        @Body tag: PaperlessTag
    ): Response<PaperlessTag>

    @PATCH("api/tags/{id}/")
    suspend fun updateTag(
        @Path("id") tagId: Int,
        @Header("Authorization") token: String,
        @Body tag: PaperlessTag
    ): Response<PaperlessTag>

    @DELETE("api/tags/{id}/")
    suspend fun deleteTag(
        @Path("id") tagId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    // Correspondents

    @GET("api/correspondents/")
    suspend fun getCorrespondents(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("ordering") ordering: String? = null
    ): Response<PaperlessCorrespondentListResponse>

    @GET("api/correspondents/{id}/")
    suspend fun getCorrespondent(
        @Path("id") correspondentId: Int,
        @Header("Authorization") token: String
    ): Response<PaperlessCorrespondent>

    @POST("api/correspondents/")
    suspend fun createCorrespondent(
        @Header("Authorization") token: String,
        @Body correspondent: PaperlessCorrespondent
    ): Response<PaperlessCorrespondent>

    @PATCH("api/correspondents/{id}/")
    suspend fun updateCorrespondent(
        @Path("id") correspondentId: Int,
        @Header("Authorization") token: String,
        @Body correspondent: PaperlessCorrespondent
    ): Response<PaperlessCorrespondent>

    @DELETE("api/correspondents/{id}/")
    suspend fun deleteCorrespondent(
        @Path("id") correspondentId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    // Document Types

    @GET("api/document_types/")
    suspend fun getDocumentTypes(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("ordering") ordering: String? = null
    ): Response<PaperlessDocumentTypeListResponse>

    @GET("api/document_types/{id}/")
    suspend fun getDocumentType(
        @Path("id") documentTypeId: Int,
        @Header("Authorization") token: String
    ): Response<PaperlessDocumentType>

    @POST("api/document_types/")
    suspend fun createDocumentType(
        @Header("Authorization") token: String,
        @Body documentType: PaperlessDocumentType
    ): Response<PaperlessDocumentType>

    @PATCH("api/document_types/{id}/")
    suspend fun updateDocumentType(
        @Path("id") documentTypeId: Int,
        @Header("Authorization") token: String,
        @Body documentType: PaperlessDocumentType
    ): Response<PaperlessDocumentType>

    @DELETE("api/document_types/{id}/")
    suspend fun deleteDocumentType(
        @Path("id") documentTypeId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    // Storage Paths

    @GET("api/storage_paths/")
    suspend fun getStoragePaths(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("ordering") ordering: String? = null
    ): Response<PaperlessStoragePathListResponse>

    @GET("api/storage_paths/{id}/")
    suspend fun getStoragePath(
        @Path("id") storagePathId: Int,
        @Header("Authorization") token: String
    ): Response<PaperlessStoragePath>

    @POST("api/storage_paths/")
    suspend fun createStoragePath(
        @Header("Authorization") token: String,
        @Body storagePath: PaperlessStoragePath
    ): Response<PaperlessStoragePath>

    @PATCH("api/storage_paths/{id}/")
    suspend fun updateStoragePath(
        @Path("id") storagePathId: Int,
        @Header("Authorization") token: String,
        @Body storagePath: PaperlessStoragePath
    ): Response<PaperlessStoragePath>

    @DELETE("api/storage_paths/{id}/")
    suspend fun deleteStoragePath(
        @Path("id") storagePathId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    // Search

    @GET("api/search/")
    suspend fun search(
        @Header("Authorization") token: String,
        @Query("query") query: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): Response<PaperlessSearchResult>

    // Bulk Operations

    @POST("api/documents/bulk_edit/")
    suspend fun bulkEdit(
        @Header("Authorization") token: String,
        @Body request: PaperlessBulkEditRequest
    ): Response<Unit>

    // Tasks

    @GET("api/tasks/{id}/")
    suspend fun getTaskStatus(
        @Path("id") taskId: String,
        @Header("Authorization") token: String
    ): Response<PaperlessTaskStatus>

    // Saved Views

    @GET("api/saved_views/")
    suspend fun getSavedViews(
        @Header("Authorization") token: String
    ): Response<List<PaperlessSavedView>>

    @GET("api/saved_views/{id}/")
    suspend fun getSavedView(
        @Path("id") viewId: Int,
        @Header("Authorization") token: String
    ): Response<PaperlessSavedView>

    // Workflows

    @GET("api/workflows/")
    suspend fun getWorkflows(
        @Header("Authorization") token: String
    ): Response<List<PaperlessWorkflow>>

    @GET("api/workflows/{id}/")
    suspend fun getWorkflow(
        @Path("id") workflowId: Int,
        @Header("Authorization") token: String
    ): Response<PaperlessWorkflow>
}
