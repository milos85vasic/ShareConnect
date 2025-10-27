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