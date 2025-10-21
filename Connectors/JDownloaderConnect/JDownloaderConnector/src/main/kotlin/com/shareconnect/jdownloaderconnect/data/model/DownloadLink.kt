package com.shareconnect.jdownloaderconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "download_links")
@Serializable
data class DownloadLink(
    @PrimaryKey
    val uuid: String,
    val packageUuid: String,
    val name: String,
    val url: String,
    val host: String,
    val bytesTotal: Long,
    val bytesLoaded: Long,
    val enabled: Boolean,
    val finished: Boolean,
    val status: DownloadStatus,
    val priority: DownloadPriority,
    var addedDate: Long,
    var finishedDate: Long? = null,
    val downloadSpeed: Long = 0,
    val eta: Long = 0,
    val availability: LinkAvailability,
    val comment: String? = null,
    val extractorStatus: ExtractorStatus? = null
)

@Serializable
enum class LinkAvailability {
    ONLINE, OFFLINE, UNKNOWN, TEMP_UNAVAILABLE
}

@Serializable
enum class ExtractorStatus {
    IDLE, EXTRACTING, FINISHED, FAILED, PASSWORD_REQUIRED
}