package com.shareconnect.nextcloudconnect.data.api

import com.shareconnect.nextcloudconnect.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Nextcloud API service interface
 * Uses OCS API v2 and WebDAV for file operations
 */
interface NextcloudApiService {

    /**
     * Get server status
     */
    @GET("status.php")
    suspend fun getServerStatus(): Response<NextcloudStatus>

    /**
     * Get current user information
     */
    @GET("ocs/v2.php/cloud/user")
    @Headers("OCS-APIRequest: true")
    suspend fun getUserInfo(
        @Header("Authorization") authorization: String
    ): Response<NextcloudResponse<NextcloudUser>>

    /**
     * List files in a directory (WebDAV)
     */
    @Headers(
        "Depth: 1",
        "Content-Type: application/xml"
    )
    @HTTP(method = "PROPFIND", path = "remote.php/dav/files/{userId}/{path}", hasBody = true)
    suspend fun listFiles(
        @Path("userId") userId: String,
        @Path("path") path: String,
        @Header("Authorization") authorization: String,
        @Body requestBody: String = """<?xml version="1.0"?>
            <d:propfind xmlns:d="DAV:" xmlns:oc="http://owncloud.org/ns" xmlns:nc="http://nextcloud.org/ns">
                <d:prop>
                    <d:resourcetype/>
                    <d:getcontentlength/>
                    <d:getlastmodified/>
                    <d:getetag/>
                    <d:getcontenttype/>
                    <oc:permissions/>
                </d:prop>
            </d:propfind>"""
    ): Response<String>

    /**
     * Download a file (WebDAV)
     */
    @GET("remote.php/dav/files/{userId}/{path}")
    suspend fun downloadFile(
        @Path("userId") userId: String,
        @Path("path") path: String,
        @Header("Authorization") authorization: String
    ): Response<okhttp3.ResponseBody>

    /**
     * Upload a file (WebDAV)
     */
    @PUT("remote.php/dav/files/{userId}/{path}")
    suspend fun uploadFile(
        @Path("userId") userId: String,
        @Path("path") path: String,
        @Header("Authorization") authorization: String,
        @Body file: okhttp3.RequestBody
    ): Response<Unit>

    /**
     * Create a folder (WebDAV)
     */
    @HTTP(method = "MKCOL", path = "remote.php/dav/files/{userId}/{path}")
    suspend fun createFolder(
        @Path("userId") userId: String,
        @Path("path") path: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>

    /**
     * Delete a file or folder (WebDAV)
     */
    @DELETE("remote.php/dav/files/{userId}/{path}")
    suspend fun delete(
        @Path("userId") userId: String,
        @Path("path") path: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>

    /**
     * Move/rename a file or folder (WebDAV)
     */
    @HTTP(method = "MOVE", path = "remote.php/dav/files/{userId}/{sourcePath}")
    suspend fun move(
        @Path("userId") userId: String,
        @Path("sourcePath") sourcePath: String,
        @Header("Authorization") authorization: String,
        @Header("Destination") destination: String
    ): Response<Unit>

    /**
     * Copy a file or folder (WebDAV)
     */
    @HTTP(method = "COPY", path = "remote.php/dav/files/{userId}/{sourcePath}")
    suspend fun copy(
        @Path("userId") userId: String,
        @Path("sourcePath") sourcePath: String,
        @Header("Authorization") authorization: String,
        @Header("Destination") destination: String
    ): Response<Unit>

    /**
     * Create a share link
     */
    @POST("ocs/v2.php/apps/files_sharing/api/v1/shares")
    @Headers("OCS-APIRequest: true")
    @FormUrlEncoded
    suspend fun createShare(
        @Header("Authorization") authorization: String,
        @Field("path") path: String,
        @Field("shareType") shareType: Int = 3, // 3 = public link
        @Field("permissions") permissions: Int = 1 // 1 = read
    ): Response<NextcloudResponse<NextcloudShare>>

    /**
     * Get shares for a file/folder
     */
    @GET("ocs/v2.php/apps/files_sharing/api/v1/shares")
    @Headers("OCS-APIRequest: true")
    suspend fun getShares(
        @Header("Authorization") authorization: String,
        @Query("path") path: String
    ): Response<NextcloudResponse<List<NextcloudShare>>>

    /**
     * Delete a share
     */
    @DELETE("ocs/v2.php/apps/files_sharing/api/v1/shares/{shareId}")
    @Headers("OCS-APIRequest: true")
    suspend fun deleteShare(
        @Header("Authorization") authorization: String,
        @Path("shareId") shareId: String
    ): Response<NextcloudResponse<Unit>>
}
