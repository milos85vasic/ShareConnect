package com.shareconnect.jdownloaderconnect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DownloadPackage(
    val uuid: String,
    val name: String,
    val bytesTotal: Long,
    val bytesLoaded: Long,
    val status: DownloadStatus,
    val links: List<DownloadLink> = emptyList(),
    val speed: Long = 0,
    val eta: Long = 0,
    val addedDate: Long = System.currentTimeMillis(),
    val finishedDate: Long? = null
)

@Serializable
data class DownloadLink(
    val uuid: String,
    val name: String,
    val url: String,
    val host: String,
    val bytesTotal: Long,
    val bytesLoaded: Long,
    val status: DownloadStatus,
    val speed: Long = 0,
    val eta: Long = 0,
    val packageUUID: String,
    val availability: LinkAvailability = LinkAvailability.UNKNOWN,
    val enabled: Boolean = true
)

@Serializable
data class LinkGrabberPackage(
    val uuid: String,
    val name: String,
    val links: List<LinkGrabberLink> = emptyList(),
    val bytesTotal: Long = 0,
    val hosterCount: Int = 0,
    val enabled: Boolean = true
)

@Serializable
data class LinkGrabberLink(
    val uuid: String,
    val name: String,
    val url: String,
    val host: String,
    val bytesTotal: Long,
    val availability: LinkAvailability,
    val packageUUID: String,
    val enabled: Boolean = true
)

@Serializable
enum class DownloadStatus {
    QUEUED,
    DOWNLOADING,
    PAUSED,
    FINISHED,
    FAILED,
    ABORTED,
    SKIPPED,
    UNKNOWN
}

@Serializable
enum class LinkAvailability {
    ONLINE,
    OFFLINE,
    UNKNOWN,
    TEMPORARILY_UNAVAILABLE
}