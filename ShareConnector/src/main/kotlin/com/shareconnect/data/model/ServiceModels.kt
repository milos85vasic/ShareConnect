package com.shareconnect.data.model

import com.google.gson.annotations.SerializedName

/**
 * MeTube download model
 */
data class MeTubeDownload(
    @SerializedName("url")
    val url: String,

    @SerializedName("quality")
    val quality: String = "best",

    @SerializedName("format")
    val format: String? = null,

    @SerializedName("folder")
    val folder: String? = null
)

/**
 * MeTube download status
 */
data class MeTubeStatus(
    @SerializedName("status")
    val status: String,

    @SerializedName("msg")
    val message: String? = null
)

/**
 * YT-DLP download options
 */
data class YtdlOptions(
    val url: String,
    val format: String? = null,
    val outputTemplate: String? = null,
    val audioOnly: Boolean = false,
    val videoOnly: Boolean = false
)

/**
 * JDownloader link grabber result
 */
data class JDownloaderLinkGrabberResult(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("links")
    val links: List<JDownloaderLink>
)

/**
 * JDownloader link
 */
data class JDownloaderLink(
    @SerializedName("url")
    val url: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("size")
    val size: Long
)

/**
 * JDownloader package
 */
data class JDownloaderPackage(
    @SerializedName("uuid")
    val uuid: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("saveto")
    val saveTo: String,

    @SerializedName("bytesTotal")
    val bytesTotal: Long,

    @SerializedName("bytesLoaded")
    val bytesLoaded: Long,

    @SerializedName("enabled")
    val enabled: Boolean,

    @SerializedName("finished")
    val finished: Boolean
)

/**
 * JDownloader device
 */
data class JDownloaderDevice(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("status")
    val status: String
)
