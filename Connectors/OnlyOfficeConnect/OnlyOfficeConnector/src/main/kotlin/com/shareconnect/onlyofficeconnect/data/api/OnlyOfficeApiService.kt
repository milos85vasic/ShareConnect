package com.shareconnect.onlyofficeconnect.data.api

import com.shareconnect.onlyofficeconnect.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit service interface for ONLYOFFICE Document Server API
 */
interface OnlyOfficeApiService {

    /**
     * Authentication
     */
    @POST("api/2.0/authentication")
    suspend fun authenticate(
        @Body request: OnlyOfficeAuthRequest
    ): Response<OnlyOfficeAuthResponse>

    /**
     * Get capabilities
     */
    @GET("api/2.0/capabilities")
    suspend fun getCapabilities(): Response<OnlyOfficeCapabilitiesResponse>

    /**
     * File operations
     */
    @GET("products/files/api/v2/files")
    suspend fun getFiles(
        @Header("Authorization") authorization: String,
        @Query("folderId") folderId: String? = null,
        @Query("filterType") filterType: Int? = null,
        @Query("searchText") searchText: String? = null,
        @Query("startIndex") startIndex: Int = 0,
        @Query("count") count: Int = 100
    ): Response<OnlyOfficeFilesResponse>

    @GET("products/files/api/v2/files/{fileId}")
    suspend fun getFileInfo(
        @Header("Authorization") authorization: String,
        @Path("fileId") fileId: String
    ): Response<OnlyOfficeFile>

    @Multipart
    @POST("products/files/api/v2/files/{folderId}/upload")
    suspend fun uploadFile(
        @Header("Authorization") authorization: String,
        @Path("folderId") folderId: String,
        @Part file: MultipartBody.Part,
        @Part("createNewIfExist") createNewIfExist: RequestBody? = null
    ): Response<OnlyOfficeUploadResponse>

    @DELETE("products/files/api/v2/files/{fileId}")
    suspend fun deleteFile(
        @Header("Authorization") authorization: String,
        @Path("fileId") fileId: String,
        @Query("deleteAfter") deleteAfter: Boolean = true,
        @Query("immediately") immediately: Boolean = false
    ): Response<OnlyOfficeOperationResponse>

    @GET("products/files/api/v2/files/{fileId}/download")
    suspend fun downloadFile(
        @Header("Authorization") authorization: String,
        @Path("fileId") fileId: String
    ): Response<okhttp3.ResponseBody>

    /**
     * Folder operations
     */
    @GET("products/files/api/v2/folders/{folderId}")
    suspend fun getFolderContents(
        @Header("Authorization") authorization: String,
        @Path("folderId") folderId: String,
        @Query("startIndex") startIndex: Int = 0,
        @Query("count") count: Int = 100
    ): Response<OnlyOfficeFilesResponse>

    @POST("products/files/api/v2/folders")
    suspend fun createFolder(
        @Header("Authorization") authorization: String,
        @Query("title") title: String,
        @Query("parentId") parentId: String? = null
    ): Response<OnlyOfficeFolderResponse>

    @DELETE("products/files/api/v2/folders/{folderId}")
    suspend fun deleteFolder(
        @Header("Authorization") authorization: String,
        @Path("folderId") folderId: String,
        @Query("deleteAfter") deleteAfter: Boolean = true,
        @Query("immediately") immediately: Boolean = false
    ): Response<OnlyOfficeOperationResponse>

    /**
     * File/Folder move and copy operations
     */
    @PUT("products/files/api/v2/fileops/move")
    suspend fun moveFile(
        @Header("Authorization") authorization: String,
        @Body request: OnlyOfficeMoveRequest,
        @Query("destFolderId") destFolderId: String,
        @Query("fileIds") fileIds: String
    ): Response<OnlyOfficeOperationResponse>

    @PUT("products/files/api/v2/fileops/move")
    suspend fun moveFolder(
        @Header("Authorization") authorization: String,
        @Body request: OnlyOfficeMoveRequest,
        @Query("destFolderId") destFolderId: String,
        @Query("folderIds") folderIds: String
    ): Response<OnlyOfficeOperationResponse>

    @POST("products/files/api/v2/fileops/copy")
    suspend fun copyFile(
        @Header("Authorization") authorization: String,
        @Body request: OnlyOfficeCopyRequest,
        @Query("destFolderId") destFolderId: String,
        @Query("fileIds") fileIds: String
    ): Response<OnlyOfficeOperationResponse>

    @POST("products/files/api/v2/fileops/copy")
    suspend fun copyFolder(
        @Header("Authorization") authorization: String,
        @Body request: OnlyOfficeCopyRequest,
        @Query("destFolderId") destFolderId: String,
        @Query("folderIds") folderIds: String
    ): Response<OnlyOfficeOperationResponse>

    /**
     * Sharing operations
     */
    @PUT("products/files/api/v2/files/{fileId}/share")
    suspend fun shareFile(
        @Header("Authorization") authorization: String,
        @Path("fileId") fileId: String,
        @Body shareData: Map<String, Any>
    ): Response<OnlyOfficeFile>

    /**
     * Document operations
     */
    @GET("products/files/api/v2/files/{fileId}/openedit")
    suspend fun getEditorConfig(
        @Header("Authorization") authorization: String,
        @Path("fileId") fileId: String
    ): Response<OnlyOfficeEditorConfiguration>

    /**
     * Callback endpoint for document save events
     */
    @POST("products/files/api/v2/files/callback")
    suspend fun handleCallback(
        @Header("Authorization") authorization: String,
        @Body callback: OnlyOfficeCallback
    ): Response<OnlyOfficeCallbackResponse>
}
