package com.shareconnect.jdownloaderconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "linkgrabber_links")
@Serializable
data class LinkGrabberLink(
    @PrimaryKey
    val uuid: String,
    val packageUuid: String,
    val name: String,
    val url: String,
    val host: String,
    val bytesTotal: Long,
    val enabled: Boolean,
    val status: LinkGrabberStatus,
    val availability: LinkAvailability,
    val addedDate: Long,
    val comment: String? = null,
    val extractorStatus: ExtractorStatus? = null
)