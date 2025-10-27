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


package com.shareconnect.syncthingconnect.data.api

import com.shareconnect.syncthingconnect.data.models.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Syncthing REST API service interface
 */
interface SyncthingApiService {

    /**
     * Get system configuration
     */
    @GET("rest/system/config")
    suspend fun getConfig(
        @Header("X-API-Key") apiKey: String
    ): Response<SyncthingConfig>

    /**
     * Update system configuration
     */
    @POST("rest/system/config")
    suspend fun updateConfig(
        @Header("X-API-Key") apiKey: String,
        @Body config: SyncthingConfig
    ): Response<SyncthingApiResponse>

    /**
     * Get system status
     */
    @GET("rest/system/status")
    suspend fun getStatus(
        @Header("X-API-Key") apiKey: String
    ): Response<SyncthingStatus>

    /**
     * Get system version
     */
    @GET("rest/system/version")
    suspend fun getVersion(
        @Header("X-API-Key") apiKey: String
    ): Response<SyncthingVersion>

    /**
     * Get connection status
     */
    @GET("rest/system/connections")
    suspend fun getConnections(
        @Header("X-API-Key") apiKey: String
    ): Response<SyncthingConnections>

    /**
     * Restart Syncthing
     */
    @POST("rest/system/restart")
    suspend fun restart(
        @Header("X-API-Key") apiKey: String
    ): Response<SyncthingApiResponse>

    /**
     * Shutdown Syncthing
     */
    @POST("rest/system/shutdown")
    suspend fun shutdown(
        @Header("X-API-Key") apiKey: String
    ): Response<SyncthingApiResponse>

    /**
     * Get database status for a folder
     */
    @GET("rest/db/status")
    suspend fun getDBStatus(
        @Header("X-API-Key") apiKey: String,
        @Query("folder") folder: String
    ): Response<SyncthingDBStatus>

    /**
     * Browse files in a folder
     */
    @GET("rest/db/browse")
    suspend fun browse(
        @Header("X-API-Key") apiKey: String,
        @Query("folder") folder: String,
        @Query("prefix") prefix: String? = null,
        @Query("dirsonly") dirsonly: Boolean? = null,
        @Query("levels") levels: Int? = null
    ): Response<List<SyncthingBrowseEntry>>

    /**
     * Get folder completion for a device
     */
    @GET("rest/db/completion")
    suspend fun getCompletion(
        @Header("X-API-Key") apiKey: String,
        @Query("folder") folder: String,
        @Query("device") device: String
    ): Response<SyncthingCompletion>

    /**
     * Trigger folder scan
     */
    @POST("rest/db/scan")
    suspend fun scan(
        @Header("X-API-Key") apiKey: String,
        @Query("folder") folder: String,
        @Query("sub") sub: String? = null,
        @Query("next") next: Int? = null
    ): Response<SyncthingApiResponse>

    /**
     * Get folder statistics
     */
    @GET("rest/stats/folder")
    suspend fun getFolderStats(
        @Header("X-API-Key") apiKey: String
    ): Response<Map<String, SyncthingFolderStats>>

    /**
     * Get device statistics
     */
    @GET("rest/stats/device")
    suspend fun getDeviceStats(
        @Header("X-API-Key") apiKey: String
    ): Response<Map<String, SyncthingDeviceStats>>

    /**
     * Get system errors
     */
    @GET("rest/system/error")
    suspend fun getErrors(
        @Header("X-API-Key") apiKey: String
    ): Response<SyncthingApiResponse>

    /**
     * Clear system errors
     */
    @POST("rest/system/error/clear")
    suspend fun clearErrors(
        @Header("X-API-Key") apiKey: String
    ): Response<SyncthingApiResponse>

    /**
     * Get system log
     */
    @GET("rest/system/log")
    suspend fun getLog(
        @Header("X-API-Key") apiKey: String,
        @Query("since") since: Long? = null
    ): Response<Map<String, Any>>

    /**
     * Get system ping (health check)
     */
    @GET("rest/system/ping")
    suspend fun ping(
        @Header("X-API-Key") apiKey: String
    ): Response<Map<String, String>>

    /**
     * Pause/Resume a device
     */
    @POST("rest/system/pause")
    suspend fun pauseDevice(
        @Header("X-API-Key") apiKey: String,
        @Query("device") device: String
    ): Response<SyncthingApiResponse>

    @POST("rest/system/resume")
    suspend fun resumeDevice(
        @Header("X-API-Key") apiKey: String,
        @Query("device") device: String
    ): Response<SyncthingApiResponse>

    /**
     * Pause/Resume a folder
     */
    @POST("rest/config/folders/{folder}/pause")
    suspend fun pauseFolder(
        @Header("X-API-Key") apiKey: String,
        @Path("folder") folder: String
    ): Response<SyncthingApiResponse>

    @POST("rest/config/folders/{folder}/resume")
    suspend fun resumeFolder(
        @Header("X-API-Key") apiKey: String,
        @Path("folder") folder: String
    ): Response<SyncthingApiResponse>

    /**
     * Get discovery status
     */
    @GET("rest/system/discovery")
    suspend fun getDiscovery(
        @Header("X-API-Key") apiKey: String
    ): Response<Map<String, Any>>

    /**
     * Get system upgrade info
     */
    @GET("rest/system/upgrade")
    suspend fun getUpgrade(
        @Header("X-API-Key") apiKey: String
    ): Response<Map<String, Any>>

    /**
     * Perform system upgrade
     */
    @POST("rest/system/upgrade")
    suspend fun performUpgrade(
        @Header("X-API-Key") apiKey: String
    ): Response<SyncthingApiResponse>

    /**
     * Get ignore patterns for a folder
     */
    @GET("rest/db/ignores")
    suspend fun getIgnores(
        @Header("X-API-Key") apiKey: String,
        @Query("folder") folder: String
    ): Response<Map<String, Any>>

    /**
     * Update ignore patterns for a folder
     */
    @POST("rest/db/ignores")
    suspend fun updateIgnores(
        @Header("X-API-Key") apiKey: String,
        @Query("folder") folder: String,
        @Body ignores: Map<String, Any>
    ): Response<SyncthingApiResponse>

    /**
     * Override folder changes (receive-only folders)
     */
    @POST("rest/db/override")
    suspend fun override(
        @Header("X-API-Key") apiKey: String,
        @Query("folder") folder: String
    ): Response<SyncthingApiResponse>

    /**
     * Revert folder changes (receive-only folders)
     */
    @POST("rest/db/revert")
    suspend fun revert(
        @Header("X-API-Key") apiKey: String,
        @Query("folder") folder: String
    ): Response<SyncthingApiResponse>

    /**
     * Get need items for a folder
     */
    @GET("rest/db/need")
    suspend fun getNeed(
        @Header("X-API-Key") apiKey: String,
        @Query("folder") folder: String,
        @Query("page") page: Int? = null,
        @Query("perpage") perpage: Int? = null
    ): Response<Map<String, Any>>

    /**
     * Get remote need items for a folder
     */
    @GET("rest/db/remoteneed")
    suspend fun getRemoteNeed(
        @Header("X-API-Key") apiKey: String,
        @Query("folder") folder: String,
        @Query("device") device: String,
        @Query("page") page: Int? = null,
        @Query("perpage") perpage: Int? = null
    ): Response<Map<String, Any>>

    /**
     * Get file information
     */
    @GET("rest/db/file")
    suspend fun getFile(
        @Header("X-API-Key") apiKey: String,
        @Query("folder") folder: String,
        @Query("file") file: String
    ): Response<Map<String, Any>>

    /**
     * Get global file information
     */
    @GET("rest/db/globalfile")
    suspend fun getGlobalFile(
        @Header("X-API-Key") apiKey: String,
        @Query("folder") folder: String,
        @Query("file") file: String
    ): Response<Map<String, Any>>

    /**
     * Bump priority for a file
     */
    @POST("rest/db/prio")
    suspend fun setPriority(
        @Header("X-API-Key") apiKey: String,
        @Query("folder") folder: String,
        @Query("file") file: String
    ): Response<SyncthingApiResponse>

    /**
     * Get debug information
     */
    @GET("rest/system/debug")
    suspend fun getDebug(
        @Header("X-API-Key") apiKey: String
    ): Response<Map<String, Any>>

    /**
     * Set debug facilities
     */
    @POST("rest/system/debug")
    suspend fun setDebug(
        @Header("X-API-Key") apiKey: String,
        @Body facilities: Map<String, Any>
    ): Response<SyncthingApiResponse>

    /**
     * Get cluster config
     */
    @GET("rest/cluster/pending/devices")
    suspend fun getPendingDevices(
        @Header("X-API-Key") apiKey: String
    ): Response<Map<String, Any>>

    /**
     * Get pending folders
     */
    @GET("rest/cluster/pending/folders")
    suspend fun getPendingFolders(
        @Header("X-API-Key") apiKey: String
    ): Response<Map<String, Any>>

    /**
     * Get device ID from certificate
     */
    @GET("rest/svc/deviceid")
    suspend fun getDeviceID(
        @Header("X-API-Key") apiKey: String,
        @Query("id") id: String
    ): Response<Map<String, String>>

    /**
     * Get language list
     */
    @GET("rest/svc/lang")
    suspend fun getLanguages(
        @Header("X-API-Key") apiKey: String
    ): Response<List<Map<String, String>>>

    /**
     * Get random string (for API key generation)
     */
    @GET("rest/svc/random/string")
    suspend fun getRandomString(
        @Header("X-API-Key") apiKey: String,
        @Query("length") length: Int? = 32
    ): Response<Map<String, String>>

    /**
     * Get report data
     */
    @GET("rest/svc/report")
    suspend fun getReport(
        @Header("X-API-Key") apiKey: String
    ): Response<Map<String, Any>>
}
