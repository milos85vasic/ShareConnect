package com.shareconnect.syncthingconnect.data.models

import com.google.gson.annotations.SerializedName

/**
 * Syncthing system configuration
 */
data class SyncthingConfig(
    @SerializedName("version")
    val version: Int,

    @SerializedName("folders")
    val folders: List<SyncthingFolder>,

    @SerializedName("devices")
    val devices: List<SyncthingDevice>,

    @SerializedName("gui")
    val gui: SyncthingGUI,

    @SerializedName("options")
    val options: SyncthingOptions,

    @SerializedName("ignoredDevices")
    val ignoredDevices: List<Map<String, Any>>? = emptyList(),

    @SerializedName("ignoredFolders")
    val ignoredFolders: List<Map<String, Any>>? = emptyList()
)

/**
 * Syncthing folder configuration
 */
data class SyncthingFolder(
    @SerializedName("id")
    val id: String,

    @SerializedName("label")
    val label: String,

    @SerializedName("path")
    val path: String,

    @SerializedName("type")
    val type: String, // sendreceive, sendonly, receiveonly

    @SerializedName("devices")
    val devices: List<SyncthingFolderDevice>,

    @SerializedName("rescanIntervalS")
    val rescanIntervalS: Int,

    @SerializedName("fsWatcherEnabled")
    val fsWatcherEnabled: Boolean,

    @SerializedName("fsWatcherDelayS")
    val fsWatcherDelayS: Double,

    @SerializedName("ignorePerms")
    val ignorePerms: Boolean,

    @SerializedName("autoNormalize")
    val autoNormalize: Boolean,

    @SerializedName("minDiskFree")
    val minDiskFree: SyncthingSize,

    @SerializedName("versioning")
    val versioning: SyncthingVersioning? = null,

    @SerializedName("copiers")
    val copiers: Int,

    @SerializedName("pullerMaxPendingKiB")
    val pullerMaxPendingKiB: Int,

    @SerializedName("hashers")
    val hashers: Int,

    @SerializedName("order")
    val order: String,

    @SerializedName("ignoreDelete")
    val ignoreDelete: Boolean,

    @SerializedName("scanProgressIntervalS")
    val scanProgressIntervalS: Int,

    @SerializedName("pullerPauseS")
    val pullerPauseS: Int,

    @SerializedName("maxConflicts")
    val maxConflicts: Int,

    @SerializedName("disableSparseFiles")
    val disableSparseFiles: Boolean,

    @SerializedName("disableTempIndexes")
    val disableTempIndexes: Boolean,

    @SerializedName("paused")
    val paused: Boolean,

    @SerializedName("weakHashThresholdPct")
    val weakHashThresholdPct: Int,

    @SerializedName("markerName")
    val markerName: String
)

/**
 * Device reference in folder configuration
 */
data class SyncthingFolderDevice(
    @SerializedName("deviceID")
    val deviceID: String,

    @SerializedName("introducedBy")
    val introducedBy: String? = null,

    @SerializedName("encryptionPassword")
    val encryptionPassword: String? = null
)

/**
 * Size specification
 */
data class SyncthingSize(
    @SerializedName("value")
    val value: Double,

    @SerializedName("unit")
    val unit: String
)

/**
 * File versioning configuration
 */
data class SyncthingVersioning(
    @SerializedName("type")
    val type: String, // simple, trashcan, staggered, external

    @SerializedName("params")
    val params: Map<String, String>,

    @SerializedName("cleanupIntervalS")
    val cleanupIntervalS: Int,

    @SerializedName("fsPath")
    val fsPath: String,

    @SerializedName("fsType")
    val fsType: String
)

/**
 * Syncthing device configuration
 */
data class SyncthingDevice(
    @SerializedName("deviceID")
    val deviceID: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("addresses")
    val addresses: List<String>,

    @SerializedName("compression")
    val compression: String,

    @SerializedName("certName")
    val certName: String,

    @SerializedName("introducer")
    val introducer: Boolean,

    @SerializedName("skipIntroductionRemovals")
    val skipIntroductionRemovals: Boolean,

    @SerializedName("introducedBy")
    val introducedBy: String,

    @SerializedName("paused")
    val paused: Boolean,

    @SerializedName("allowedNetworks")
    val allowedNetworks: List<String>? = emptyList(),

    @SerializedName("autoAcceptFolders")
    val autoAcceptFolders: Boolean,

    @SerializedName("maxSendKbps")
    val maxSendKbps: Int,

    @SerializedName("maxRecvKbps")
    val maxRecvKbps: Int,

    @SerializedName("ignoredFolders")
    val ignoredFolders: List<Map<String, Any>>? = emptyList(),

    @SerializedName("maxRequestKiB")
    val maxRequestKiB: Int,

    @SerializedName("untrusted")
    val untrusted: Boolean,

    @SerializedName("remoteGUIPort")
    val remoteGUIPort: Int
)

/**
 * GUI configuration
 */
data class SyncthingGUI(
    @SerializedName("enabled")
    val enabled: Boolean,

    @SerializedName("address")
    val address: String,

    @SerializedName("unixSocketPermissions")
    val unixSocketPermissions: String,

    @SerializedName("user")
    val user: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("authMode")
    val authMode: String,

    @SerializedName("useTLS")
    val useTLS: Boolean,

    @SerializedName("apiKey")
    val apiKey: String,

    @SerializedName("insecureAdminAccess")
    val insecureAdminAccess: Boolean,

    @SerializedName("theme")
    val theme: String,

    @SerializedName("debugging")
    val debugging: Boolean,

    @SerializedName("insecureSkipHostcheck")
    val insecureSkipHostcheck: Boolean,

    @SerializedName("insecureAllowFrameLoading")
    val insecureAllowFrameLoading: Boolean
)

/**
 * Global options
 */
data class SyncthingOptions(
    @SerializedName("listenAddresses")
    val listenAddresses: List<String>,

    @SerializedName("globalAnnounceServers")
    val globalAnnounceServers: List<String>,

    @SerializedName("globalAnnounceEnabled")
    val globalAnnounceEnabled: Boolean,

    @SerializedName("localAnnounceEnabled")
    val localAnnounceEnabled: Boolean,

    @SerializedName("localAnnouncePort")
    val localAnnouncePort: Int,

    @SerializedName("localAnnounceMCAddr")
    val localAnnounceMCAddr: String,

    @SerializedName("maxSendKbps")
    val maxSendKbps: Int,

    @SerializedName("maxRecvKbps")
    val maxRecvKbps: Int,

    @SerializedName("reconnectionIntervalS")
    val reconnectionIntervalS: Int,

    @SerializedName("relaysEnabled")
    val relaysEnabled: Boolean,

    @SerializedName("relayReconnectIntervalM")
    val relayReconnectIntervalM: Int,

    @SerializedName("startBrowser")
    val startBrowser: Boolean,

    @SerializedName("natEnabled")
    val natEnabled: Boolean,

    @SerializedName("natLeaseMinutes")
    val natLeaseMinutes: Int,

    @SerializedName("natRenewalMinutes")
    val natRenewalMinutes: Int,

    @SerializedName("natTimeoutSeconds")
    val natTimeoutSeconds: Int,

    @SerializedName("urAccepted")
    val urAccepted: Int,

    @SerializedName("urSeen")
    val urSeen: Int,

    @SerializedName("urUniqueID")
    val urUniqueID: String,

    @SerializedName("urURL")
    val urURL: String,

    @SerializedName("urPostInsecurely")
    val urPostInsecurely: Boolean,

    @SerializedName("urInitialDelayS")
    val urInitialDelayS: Int,

    @SerializedName("autoUpgradeIntervalH")
    val autoUpgradeIntervalH: Int,

    @SerializedName("upgradeToPreReleases")
    val upgradeToPreReleases: Boolean,

    @SerializedName("keepTemporariesH")
    val keepTemporariesH: Int,

    @SerializedName("cacheIgnoredFiles")
    val cacheIgnoredFiles: Boolean,

    @SerializedName("progressUpdateIntervalS")
    val progressUpdateIntervalS: Int,

    @SerializedName("limitBandwidthInLan")
    val limitBandwidthInLan: Boolean,

    @SerializedName("minHomeDiskFree")
    val minHomeDiskFree: SyncthingSize,

    @SerializedName("releasesURL")
    val releasesURL: String,

    @SerializedName("alwaysLocalNets")
    val alwaysLocalNets: List<String>,

    @SerializedName("overwriteRemoteDeviceNamesOnConnect")
    val overwriteRemoteDeviceNamesOnConnect: Boolean,

    @SerializedName("tempIndexMinBlocks")
    val tempIndexMinBlocks: Int,

    @SerializedName("unackedNotificationIDs")
    val unackedNotificationIDs: List<String>? = emptyList(),

    @SerializedName("trafficClass")
    val trafficClass: Int,

    @SerializedName("defaultFolderPath")
    val defaultFolderPath: String,

    @SerializedName("setLowPriority")
    val setLowPriority: Boolean,

    @SerializedName("maxFolderConcurrency")
    val maxFolderConcurrency: Int,

    @SerializedName("crashReportingEnabled")
    val crashReportingEnabled: Boolean,

    @SerializedName("crashReportingURL")
    val crashReportingURL: String,

    @SerializedName("stunKeepaliveStartS")
    val stunKeepaliveStartS: Int,

    @SerializedName("stunKeepaliveMinS")
    val stunKeepaliveMinS: Int,

    @SerializedName("stunServers")
    val stunServers: List<String>,

    @SerializedName("databaseTuning")
    val databaseTuning: String,

    @SerializedName("maxConcurrentIncomingRequestKiB")
    val maxConcurrentIncomingRequestKiB: Int,

    @SerializedName("announceLANAddresses")
    val announceLANAddresses: Boolean,

    @SerializedName("sendFullIndexOnUpgrade")
    val sendFullIndexOnUpgrade: Boolean
)

/**
 * System status
 */
data class SyncthingStatus(
    @SerializedName("alloc")
    val alloc: Long,

    @SerializedName("connectionServiceStatus")
    val connectionServiceStatus: Map<String, Any>,

    @SerializedName("cpuPercent")
    val cpuPercent: Double,

    @SerializedName("discoveryEnabled")
    val discoveryEnabled: Boolean,

    @SerializedName("discoveryErrors")
    val discoveryErrors: Map<String, String>,

    @SerializedName("discoveryMethods")
    val discoveryMethods: Int,

    @SerializedName("goroutines")
    val goroutines: Int,

    @SerializedName("guiAddressOverridden")
    val guiAddressOverridden: Boolean,

    @SerializedName("guiAddressUsed")
    val guiAddressUsed: String,

    @SerializedName("lastDialStatus")
    val lastDialStatus: Map<String, Any>,

    @SerializedName("myID")
    val myID: String,

    @SerializedName("pathSeparator")
    val pathSeparator: String,

    @SerializedName("startTime")
    val startTime: String,

    @SerializedName("sys")
    val sys: Long,

    @SerializedName("themes")
    val themes: List<String>,

    @SerializedName("tilde")
    val tilde: String,

    @SerializedName("uptime")
    val uptime: Long,

    @SerializedName("urVersionMax")
    val urVersionMax: Int
)

/**
 * System version information
 */
data class SyncthingVersion(
    @SerializedName("arch")
    val arch: String,

    @SerializedName("codename")
    val codename: String,

    @SerializedName("container")
    val container: Boolean,

    @SerializedName("isBeta")
    val isBeta: Boolean,

    @SerializedName("isCandidate")
    val isCandidate: Boolean,

    @SerializedName("isRelease")
    val isRelease: Boolean,

    @SerializedName("longVersion")
    val longVersion: String,

    @SerializedName("os")
    val os: String,

    @SerializedName("stamp")
    val stamp: String,

    @SerializedName("tags")
    val tags: List<String>,

    @SerializedName("user")
    val user: String,

    @SerializedName("version")
    val version: String
)

/**
 * Connection status for devices
 */
data class SyncthingConnections(
    @SerializedName("connections")
    val connections: Map<String, SyncthingConnection>,

    @SerializedName("total")
    val total: SyncthingConnectionStats
)

/**
 * Connection details for a single device
 */
data class SyncthingConnection(
    @SerializedName("address")
    val address: String,

    @SerializedName("at")
    val at: String,

    @SerializedName("clientVersion")
    val clientVersion: String,

    @SerializedName("connected")
    val connected: Boolean,

    @SerializedName("crypto")
    val crypto: String,

    @SerializedName("inBytesTotal")
    val inBytesTotal: Long,

    @SerializedName("outBytesTotal")
    val outBytesTotal: Long,

    @SerializedName("paused")
    val paused: Boolean,

    @SerializedName("type")
    val type: String,

    @SerializedName("isLocal")
    val isLocal: Boolean? = null,

    @SerializedName("priority")
    val priority: Int? = null
)

/**
 * Total connection statistics
 */
data class SyncthingConnectionStats(
    @SerializedName("at")
    val at: String,

    @SerializedName("inBytesTotal")
    val inBytesTotal: Long,

    @SerializedName("outBytesTotal")
    val outBytesTotal: Long
)

/**
 * Database status for a folder
 */
data class SyncthingDBStatus(
    @SerializedName("errors")
    val errors: Long,

    @SerializedName("globalBytes")
    val globalBytes: Long,

    @SerializedName("globalDeleted")
    val globalDeleted: Long,

    @SerializedName("globalDirectories")
    val globalDirectories: Long,

    @SerializedName("globalFiles")
    val globalFiles: Long,

    @SerializedName("globalSymlinks")
    val globalSymlinks: Long,

    @SerializedName("globalTotalItems")
    val globalTotalItems: Long,

    @SerializedName("ignorePatterns")
    val ignorePatterns: Boolean,

    @SerializedName("inSyncBytes")
    val inSyncBytes: Long,

    @SerializedName("inSyncFiles")
    val inSyncFiles: Long,

    @SerializedName("invalid")
    val invalid: String,

    @SerializedName("localBytes")
    val localBytes: Long,

    @SerializedName("localDeleted")
    val localDeleted: Long,

    @SerializedName("localDirectories")
    val localDirectories: Long,

    @SerializedName("localFiles")
    val localFiles: Long,

    @SerializedName("localSymlinks")
    val localSymlinks: Long,

    @SerializedName("localTotalItems")
    val localTotalItems: Long,

    @SerializedName("needBytes")
    val needBytes: Long,

    @SerializedName("needDeletes")
    val needDeletes: Long,

    @SerializedName("needDirectories")
    val needDirectories: Long,

    @SerializedName("needFiles")
    val needFiles: Long,

    @SerializedName("needSymlinks")
    val needSymlinks: Long,

    @SerializedName("needTotalItems")
    val needTotalItems: Long,

    @SerializedName("pullErrors")
    val pullErrors: Long,

    @SerializedName("receiveOnlyChangedBytes")
    val receiveOnlyChangedBytes: Long,

    @SerializedName("receiveOnlyChangedDeletes")
    val receiveOnlyChangedDeletes: Long,

    @SerializedName("receiveOnlyChangedDirectories")
    val receiveOnlyChangedDirectories: Long,

    @SerializedName("receiveOnlyChangedFiles")
    val receiveOnlyChangedFiles: Long,

    @SerializedName("receiveOnlyChangedSymlinks")
    val receiveOnlyChangedSymlinks: Long,

    @SerializedName("receiveOnlyTotalItems")
    val receiveOnlyTotalItems: Long,

    @SerializedName("sequence")
    val sequence: Long,

    @SerializedName("state")
    val state: String,

    @SerializedName("stateChanged")
    val stateChanged: String,

    @SerializedName("version")
    val version: Long
)

/**
 * Folder completion status
 */
data class SyncthingCompletion(
    @SerializedName("completion")
    val completion: Double,

    @SerializedName("globalBytes")
    val globalBytes: Long,

    @SerializedName("needBytes")
    val needBytes: Long,

    @SerializedName("globalItems")
    val globalItems: Long,

    @SerializedName("needItems")
    val needItems: Long,

    @SerializedName("needDeletes")
    val needDeletes: Long,

    @SerializedName("sequence")
    val sequence: Long
)

/**
 * Event from event stream
 */
data class SyncthingEvent(
    @SerializedName("id")
    val id: Long,

    @SerializedName("globalID")
    val globalID: Long,

    @SerializedName("time")
    val time: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("data")
    val data: Map<String, Any>
)

/**
 * File/directory entry from browse
 */
data class SyncthingBrowseEntry(
    @SerializedName("name")
    val name: String,

    @SerializedName("size")
    val size: Long,

    @SerializedName("modified")
    val modified: String,

    @SerializedName("type")
    val type: String // file or directory
)

/**
 * Folder statistics
 */
data class SyncthingFolderStats(
    @SerializedName("lastFile")
    val lastFile: SyncthingLastFile,

    @SerializedName("lastScan")
    val lastScan: String
)

/**
 * Last file information
 */
data class SyncthingLastFile(
    @SerializedName("at")
    val at: String,

    @SerializedName("deleted")
    val deleted: Boolean,

    @SerializedName("filename")
    val filename: String
)

/**
 * Device statistics
 */
data class SyncthingDeviceStats(
    @SerializedName("lastSeen")
    val lastSeen: String,

    @SerializedName("lastConnectionDurationS")
    val lastConnectionDurationS: Double? = null
)

/**
 * Error entry
 */
data class SyncthingError(
    @SerializedName("when")
    val `when`: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("path")
    val path: String? = null
)

/**
 * Generic API response
 */
data class SyncthingApiResponse(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("error")
    val error: String? = null
)
