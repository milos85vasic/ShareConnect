package com.shareconnect.jdownloaderconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "download_packages")
@Serializable
data class DownloadPackage(
    @PrimaryKey
    val uuid: String,
    val name: String,
    val bytesTotal: Long,
    val bytesLoaded: Long,
    val enabled: Boolean,
    val finished: Boolean,
    val priority: DownloadPriority,
    val host: String,
    val status: DownloadStatus,
    val addedDate: Long,
    val finishedDate: Long? = null,
    val downloadDirectory: String,
    val archivePassword: String? = null,
    val comment: String? = null,
    val downloadUrls: List<String> = emptyList()
)

@Serializable
enum class DownloadPriority {
    HIGHEST, HIGH, DEFAULT, LOW, LOWEST
}

@Serializable
enum class DownloadStatus {
    DOWNLOADING, FINISHED, FAILED, SKIPPED, ABORTED, QUEUED, CONNECTING, CAPTCHA_REQUIRED
}