package com.shareconnect.historysync.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "synced_history")
data class HistoryData(
    @PrimaryKey val id: String,
    val url: String,
    val title: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val serviceProvider: String?, // "YouTube", "Vimeo", etc.
    val type: String, // "video", "playlist", "channel", "torrent", "magnet"
    val timestamp: Long,
    val profileId: String?,
    val profileName: String?,
    val isSentSuccessfully: Boolean,
    val serviceType: String, // "metube", "ytdl", "torrent", "jdownloader"
    val torrentClientType: String?, // "qbittorrent", "transmission", "utorrent" (for torrent types)
    val sourceApp: String,
    val version: Int = 1,
    val lastModified: Long = System.currentTimeMillis(),

    // Additional metadata
    val fileSize: Long? = null, // in bytes
    val duration: Int? = null, // in seconds (for videos)
    val quality: String? = null, // "1080p", "720p", etc.
    val downloadPath: String? = null, // where it was/will be downloaded
    val torrentHash: String? = null, // for torrents
    val magnetUri: String? = null, // full magnet link
    val category: String? = null, // "movies", "tv shows", "music", etc.
    val tags: String? = null // comma-separated tags
) {
    companion object {
        const val OBJECT_TYPE = "history"

        // Type constants
        const val TYPE_VIDEO = "video"
        const val TYPE_PLAYLIST = "playlist"
        const val TYPE_CHANNEL = "channel"
        const val TYPE_TORRENT = "torrent"
        const val TYPE_MAGNET = "magnet"
        const val TYPE_FILE = "file"

        // Service type constants
        const val SERVICE_METUBE = "metube"
        const val SERVICE_YTDL = "ytdl"
        const val SERVICE_TORRENT = "torrent"
        const val SERVICE_JDOWNLOADER = "jdownloader"

        // Torrent client constants
        const val TORRENT_CLIENT_QBITTORRENT = "qbittorrent"
        const val TORRENT_CLIENT_TRANSMISSION = "transmission"
        const val TORRENT_CLIENT_UTORRENT = "utorrent"
    }

    /**
     * Check if this history item is a torrent-related download
     */
    fun isTorrentHistory(): Boolean {
        return serviceType == SERVICE_TORRENT ||
               type == TYPE_TORRENT ||
               type == TYPE_MAGNET
    }

    /**
     * Check if this history item is for qBittorrent
     */
    fun isQBitTorrentHistory(): Boolean {
        return isTorrentHistory() && torrentClientType == TORRENT_CLIENT_QBITTORRENT
    }

    /**
     * Check if this history item is for Transmission
     */
    fun isTransmissionHistory(): Boolean {
        return isTorrentHistory() && torrentClientType == TORRENT_CLIENT_TRANSMISSION
    }

    /**
     * Check if this history item is a media download (video/audio)
     */
    fun isMediaHistory(): Boolean {
        return serviceType == SERVICE_METUBE ||
               serviceType == SERVICE_YTDL ||
               type == TYPE_VIDEO ||
               type == TYPE_PLAYLIST
    }

    /**
     * Get display title with fallback
     */
    fun getDisplayTitle(): String {
        return title ?: url
    }

    /**
     * Get tags as list
     */
    fun getTagsList(): List<String> {
        return tags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    }

    /**
     * Format file size for display
     */
    fun getFormattedFileSize(): String? {
        if (fileSize == null) return null

        return when {
            fileSize < 1024 -> "$fileSize B"
            fileSize < 1024 * 1024 -> "${fileSize / 1024} KB"
            fileSize < 1024 * 1024 * 1024 -> "${fileSize / (1024 * 1024)} MB"
            else -> "${fileSize / (1024 * 1024 * 1024)} GB"
        }
    }

    /**
     * Format duration for display
     */
    fun getFormattedDuration(): String? {
        if (duration == null) return null

        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        val seconds = duration % 60

        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%d:%02d", minutes, seconds)
        }
    }
}
