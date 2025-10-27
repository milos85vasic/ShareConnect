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


package com.shareconnect.jdownloaderconnect.network.api

import com.shareconnect.jdownloaderconnect.data.model.*
import com.shareconnect.jdownloaderconnect.domain.model.JDownloaderInstance
import com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.*

interface MyJDownloaderApi {

    // Authentication
    @POST("/my/connect")
    suspend fun connect(
        @Body request: ConnectRequest
    ): Response<ConnectResponse>

    @POST("/my/disconnect")
    suspend fun disconnect(
        @Header("Authorization") token: String
    ): Response<Unit>

    // Device management
    @GET("/my/listdevices")
    suspend fun listDevices(
        @Header("Authorization") token: String
    ): Response<ListDevicesResponse>

    // Instance monitoring (My JDownloader specific)
    @GET("/my/listinstances")
    suspend fun listInstances(
        @Header("Authorization") token: String
    ): Response<ListInstancesResponse>

    @GET("/instance/status")
    suspend fun getInstanceStatus(
        @Header("Authorization") token: String,
        @Query("instanceId") instanceId: String
    ): Response<InstanceStatusResponse>

    @GET("/instance/speed")
    suspend fun getDownloadSpeed(
        @Header("Authorization") token: String,
        @Query("instanceId") instanceId: String
    ): Response<DownloadSpeedResponse>

    @GET("/instance/speedhistory")
    suspend fun getSpeedHistory(
        @Header("Authorization") token: String,
        @Query("instanceId") instanceId: String,
        @Query("duration") durationMinutes: Int = 60
    ): Response<SpeedHistoryResponse>

    // Downloads
    @GET("/downloads/queryLinks")
    suspend fun queryLinks(
        @Header("Authorization") token: String,
        @Query("deviceId") deviceId: String,
        @Query("packageUUIDs") packageUUIDs: List<String>? = null
    ): Response<QueryLinksResponse>

    @GET("/downloads/queryPackages")
    suspend fun queryPackages(
        @Header("Authorization") token: String,
        @Query("deviceId") deviceId: String
    ): Response<QueryPackagesResponse>

    @POST("/downloads/addLinks")
    suspend fun addLinks(
        @Header("Authorization") token: String,
        @Body request: AddLinksRequest
    ): Response<Unit>

    @POST("/downloads/startDownloads")
    suspend fun startDownloads(
        @Header("Authorization") token: String,
        @Body request: DownloadActionRequest
    ): Response<Unit>

    @POST("/downloads/stopDownloads")
    suspend fun stopDownloads(
        @Header("Authorization") token: String,
        @Body request: DownloadActionRequest
    ): Response<Unit>

    @POST("/downloads/removeLinks")
    suspend fun removeLinks(
        @Header("Authorization") token: String,
        @Body request: DownloadActionRequest
    ): Response<Unit>

    // Link Grabber
    @POST("/linkgrabberv2/addLinks")
    suspend fun addLinksToLinkGrabber(
        @Header("Authorization") token: String,
        @Body request: AddLinksRequest
    ): Response<AddLinksResponse>

    @GET("/linkgrabberv2/queryLinks")
    suspend fun queryLinkGrabberLinks(
        @Header("Authorization") token: String,
        @Query("deviceId") deviceId: String
    ): Response<QueryLinksResponse>

    @POST("/linkgrabberv2/moveToDownloadlist")
    suspend fun moveToDownloadList(
        @Header("Authorization") token: String,
        @Body request: DownloadActionRequest
    ): Response<Unit>

    // Account management
    @GET("/accounts/listAccounts")
    suspend fun listAccounts(
        @Header("Authorization") token: String,
        @Query("deviceId") deviceId: String
    ): Response<ListAccountsResponse>

    // System information
    @GET("/system/getInfo")
    suspend fun getSystemInfo(
        @Header("Authorization") token: String,
        @Query("deviceId") deviceId: String
    ): Response<SystemInfoResponse>
}

// Request/Response models
@Serializable
data class ConnectRequest(
    val email: String,
    val password: String,
    val appKey: String
)

@Serializable
data class ConnectResponse(
    val sessionToken: String,
    val deviceId: String,
    val serverEncryptionToken: String,
    val deviceEncryptionToken: String
)

@Serializable
data class ListDevicesResponse(
    val devices: List<JDownloaderDevice>
)

@Serializable
data class JDownloaderDevice(
    val id: String,
    val name: String,
    val type: String,
    val status: String,
    val version: String
)

@Serializable
data class QueryLinksResponse(
    val data: List<DownloadLink>
)

@Serializable
data class QueryPackagesResponse(
    val data: List<DownloadPackage>
)

@Serializable
data class AddLinksRequest(
    val deviceId: String,
    val links: List<String>,
    val packageName: String? = null,
    val destinationFolder: String? = null,
    val extractAfterDownload: Boolean = false,
    val autoStart: Boolean = false
)

@Serializable
data class AddLinksResponse(
    val packageUUID: String,
    val linkUUIDs: List<String>
)

@Serializable
data class DownloadActionRequest(
    val deviceId: String,
    val linkIds: List<String>? = null,
    val packageIds: List<String>? = null
)

@Serializable
data class ListAccountsResponse(
    val accounts: List<HostAccount>
)

@Serializable
data class HostAccount(
    val hostname: String,
    val username: String,
    val enabled: Boolean,
    val validUntil: Long? = null,
    val trafficLeft: Long? = null
)

@Serializable
data class SystemInfoResponse(
    val memory: MemoryInfo,
    val os: OSInfo,
    val java: JavaInfo,
    val jd: JDInfo
)

@Serializable
data class MemoryInfo(
    val total: Long,
    val used: Long,
    val free: Long
)

@Serializable
data class OSInfo(
    val name: String,
    val version: String,
    val arch: String
)

@Serializable
data class JavaInfo(
    val version: String,
    val vendor: String
)

@Serializable
data class JDInfo(
    val version: String,
    val revision: String,
    val build: String
)

// My JDownloader Instance Management
// Using domain models from com.shareconnect.jdownloaderconnect.domain.model

@Serializable
data class ListInstancesResponse(
    val instances: List<JDownloaderInstance>
)

@Serializable
data class InstanceStatusResponse(
    val instanceId: String,
    val status: InstanceStatus,
    val uptime: Long,
    val activeDownloads: Int,
    val totalDownloads: Int,
    val errorMessage: String? = null,
    val lastUpdated: Long
)

@Serializable
data class DownloadSpeedResponse(
    val instanceId: String,
    val currentSpeed: Long, // bytes per second
    val maxSpeed: Long, // bytes per second
    val averageSpeed: Long, // bytes per second
    val timestamp: Long
)

@Serializable
data class SpeedHistoryResponse(
    val instanceId: String,
    val speedPoints: List<SpeedPoint>
)

@Serializable
data class SpeedPoint(
    val timestamp: Long,
    val speed: Long, // bytes per second
    val activeConnections: Int
)