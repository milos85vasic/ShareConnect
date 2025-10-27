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