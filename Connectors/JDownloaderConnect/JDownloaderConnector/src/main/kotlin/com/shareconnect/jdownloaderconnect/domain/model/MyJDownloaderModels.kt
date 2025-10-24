package com.shareconnect.jdownloaderconnect.domain.model

import kotlinx.serialization.Serializable

// My JDownloader Instance Management
@Serializable
data class JDownloaderInstance(
    val id: String,
    val name: String,
    val status: InstanceStatus,
    val version: String,
    val lastSeen: Long,
    val isOnline: Boolean,
    val deviceId: String,
    val accountId: String,
    val currentSpeed: Long = 0,
    val activeDownloads: Int = 0,
    val totalDownloads: Int = 0,
    val uptime: Long = 0,
    val errorMessage: String? = null
)

@Serializable
enum class InstanceStatus {
    RUNNING,
    PAUSED,
    STOPPED,
    ERROR,
    OFFLINE,
    CONNECTING;

    fun getDisplayName(): String = when (this) {
        RUNNING -> "Running"
        PAUSED -> "Paused"
        STOPPED -> "Stopped"
        ERROR -> "Error"
        OFFLINE -> "Offline"
        CONNECTING -> "Connecting"
    }

    fun getColorRes(): String = when (this) {
        RUNNING -> "status_running"
        PAUSED -> "status_paused"
        STOPPED -> "status_stopped"
        ERROR -> "status_error"
        OFFLINE -> "status_offline"
        CONNECTING -> "status_connecting"
    }

    fun getIconRes(): String = when (this) {
        RUNNING -> "ic_status_running"
        PAUSED -> "ic_status_paused"
        STOPPED -> "ic_status_stopped"
        ERROR -> "ic_status_error"
        OFFLINE -> "ic_status_offline"
        CONNECTING -> "ic_status_connecting"
    }
}

// Speed monitoring
@Serializable
data class SpeedPoint(
    val timestamp: Long,
    val speed: Long, // bytes per second
    val activeConnections: Int
)

@Serializable
data class SpeedHistory(
    val instanceId: String,
    val points: List<SpeedPoint>,
    val averageSpeed: Long,
    val maxSpeed: Long,
    val minSpeed: Long,
    val durationMinutes: Int
)

// Dashboard data
@Serializable
data class InstanceDashboard(
    val instance: JDownloaderInstance,
    val speedHistory: SpeedHistory,
    val recentActivity: List<DownloadActivity>
)

@Serializable
data class DownloadActivity(
    val id: String,
    val filename: String,
    val status: DownloadStatus,
    val progress: Float,
    val speed: Long,
    val eta: Long,
    val timestamp: Long
)

// Real-time updates
@Serializable
data class InstanceUpdate(
    val instanceId: String,
    val type: UpdateType,
    val data: UpdateData
)

@Serializable
enum class UpdateType {
    STATUS_CHANGED,
    SPEED_UPDATED,
    DOWNLOAD_STARTED,
    DOWNLOAD_FINISHED,
    DOWNLOAD_FAILED,
    INSTANCE_CONNECTED,
    INSTANCE_DISCONNECTED
}

@Serializable
data class UpdateData(
    val status: InstanceStatus? = null,
    val speed: Long? = null,
    val downloadId: String? = null,
    val filename: String? = null,
    val progress: Float? = null,
    val errorMessage: String? = null
)