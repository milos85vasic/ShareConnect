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


package com.shareconnect.rsssync.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "synced_rss_feeds")
data class RSSFeedData(
    @PrimaryKey val id: String,
    val url: String,
    val name: String,
    val autoDownload: Boolean = false,
    val filters: String? = null, // JSON array of regex patterns
    val excludeFilters: String? = null, // JSON array of exclude patterns
    val updateInterval: Int = 30, // minutes
    val lastUpdate: Long = 0,
    val isEnabled: Boolean = true,
    val category: String? = null,
    val torrentClientType: String? = null, // "qbittorrent", "transmission"
    val downloadPath: String? = null,
    val sourceApp: String,
    val version: Int = 1,
    val lastModified: Long = System.currentTimeMillis()
) {
    companion object {
        const val OBJECT_TYPE = "rss_feed"
        const val TORRENT_CLIENT_QBITTORRENT = "qbittorrent"
        const val TORRENT_CLIENT_TRANSMISSION = "transmission"
        const val TORRENT_CLIENT_UTORRENT = "utorrent"
    }

    fun isForQBittorrent() = torrentClientType == TORRENT_CLIENT_QBITTORRENT
    fun isForTransmission() = torrentClientType == TORRENT_CLIENT_TRANSMISSION
    fun isForUTorrent() = torrentClientType == TORRENT_CLIENT_UTORRENT
}
