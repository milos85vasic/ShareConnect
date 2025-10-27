/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


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
