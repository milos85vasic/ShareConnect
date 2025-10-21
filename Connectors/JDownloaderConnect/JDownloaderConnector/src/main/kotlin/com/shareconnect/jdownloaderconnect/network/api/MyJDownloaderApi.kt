package com.shareconnect.jdownloaderconnect.network.api

import com.shareconnect.jdownloaderconnect.data.model.*
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