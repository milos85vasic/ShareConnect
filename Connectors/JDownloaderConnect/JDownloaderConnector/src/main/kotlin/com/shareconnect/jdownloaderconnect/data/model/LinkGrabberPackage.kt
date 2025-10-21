package com.shareconnect.jdownloaderconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "linkgrabber_packages")
@Serializable
data class LinkGrabberPackage(
    @PrimaryKey
    val uuid: String,
    val name: String,
    val bytesTotal: Long,
    val enabled: Boolean,
    val host: String,
    val status: LinkGrabberStatus,
    val addedDate: Long,
    val downloadDirectory: String,
    val archivePassword: String? = null,
    val comment: String? = null
)

@Serializable
enum class LinkGrabberStatus {
    PENDING, ANALYZING, READY, FAILED, DUPLICATE
}