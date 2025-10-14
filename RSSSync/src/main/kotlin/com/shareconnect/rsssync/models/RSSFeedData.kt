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
