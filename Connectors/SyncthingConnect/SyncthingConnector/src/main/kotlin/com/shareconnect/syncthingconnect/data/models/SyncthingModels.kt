package com.shareconnect.syncthingconnect.data.models

import com.google.gson.annotations.SerializedName

data class SystemConfig(
    @SerializedName("version") val version: Int,
    @SerializedName("folders") val folders: List<FolderConfig>,
    @SerializedName("devices") val devices: List<DeviceConfig>,
    @SerializedName("options") val options: Options
)

data class FolderConfig(
    @SerializedName("id") val id: String,
    @SerializedName("label") val label: String,
    @SerializedName("path") val path: String,
    @SerializedName("type") val type: String,
    @SerializedName("devices") val devices: List<Device>,
    @SerializedName("paused") val paused: Boolean = false
)

data class DeviceConfig(
    @SerializedName("deviceID") val deviceID: String,
    @SerializedName("name") val name: String,
    @SerializedName("addresses") val addresses: List<String>,
    @SerializedName("paused") val paused: Boolean = false
)

data class Device(
    @SerializedName("deviceID") val deviceID: String
)

data class Options(
    @SerializedName("listenAddresses") val listenAddresses: List<String>,
    @SerializedName("globalAnnounceEnabled") val globalAnnounceEnabled: Boolean,
    @SerializedName("localAnnounceEnabled") val localAnnounceEnabled: Boolean
)

data class SystemStatus(
    @SerializedName("alloc") val alloc: Long,
    @SerializedName("connectionServiceStatus") val connectionServiceStatus: Map<String, String>,
    @SerializedName("cpuPercent") val cpuPercent: Double,
    @SerializedName("discoveryEnabled") val discoveryEnabled: Boolean,
    @SerializedName("goroutines") val goroutines: Int,
    @SerializedName("myID") val myID: String,
    @SerializedName("pathSeparator") val pathSeparator: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("sys") val sys: Long,
    @SerializedName("uptime") val uptime: Long
)

data class SystemVersion(
    @SerializedName("arch") val arch: String,
    @SerializedName("longVersion") val longVersion: String,
    @SerializedName("os") val os: String,
    @SerializedName("version") val version: String
)

data class SystemConnections(
    @SerializedName("connections") val connections: Map<String, Connection>,
    @SerializedName("total") val total: TotalConnection
)

data class Connection(
    @SerializedName("at") val at: String,
    @SerializedName("connected") val connected: Boolean,
    @SerializedName("address") val address: String,
    @SerializedName("type") val type: String,
    @SerializedName("inBytesTotal") val inBytesTotal: Long,
    @SerializedName("outBytesTotal") val outBytesTotal: Long
)

data class TotalConnection(
    @SerializedName("at") val at: String,
    @SerializedName("inBytesTotal") val inBytesTotal: Long,
    @SerializedName("outBytesTotal") val outBytesTotal: Long
)

data class DatabaseStatus(
    @SerializedName("errors") val errors: Int,
    @SerializedName("globalBytes") val globalBytes: Long,
    @SerializedName("globalDeleted") val globalDeleted: Int,
    @SerializedName("globalDirectories") val globalDirectories: Int,
    @SerializedName("globalFiles") val globalFiles: Int,
    @SerializedName("inSyncBytes") val inSyncBytes: Long,
    @SerializedName("inSyncFiles") val inSyncFiles: Int,
    @SerializedName("localBytes") val localBytes: Long,
    @SerializedName("localDeleted") val localDeleted: Int,
    @SerializedName("localDirectories") val localDirectories: Int,
    @SerializedName("localFiles") val localFiles: Int,
    @SerializedName("needBytes") val needBytes: Long,
    @SerializedName("needDeletes") val needDeletes: Int,
    @SerializedName("needDirectories") val needDirectories: Int,
    @SerializedName("needFiles") val needFiles: Int,
    @SerializedName("state") val state: String
)

data class FolderCompletion(
    @SerializedName("completion") val completion: Double,
    @SerializedName("globalBytes") val globalBytes: Long,
    @SerializedName("needBytes") val needBytes: Long,
    @SerializedName("needDeletes") val needDeletes: Int,
    @SerializedName("needItems") val needItems: Int
)

data class DirectoryEntry(
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("size") val size: Long,
    @SerializedName("modified") val modified: String
)

data class SyncthingEvent(
    @SerializedName("id") val id: Long,
    @SerializedName("type") val type: String,
    @SerializedName("time") val time: String,
    @SerializedName("data") val data: Map<String, Any>?
)
