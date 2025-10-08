package com.shareconnect.bookmarksync.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "synced_bookmarks")
data class BookmarkData(
    @PrimaryKey val id: String,
    val url: String,
    val title: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val type: String, // "video", "torrent", "magnet", "website", "playlist", "channel"
    val category: String? = null, // "movies", "tv", "music", "software", etc.
    val tags: String? = null, // JSON array of tags
    val isFavorite: Boolean = false,
    val notes: String? = null,
    val serviceProvider: String? = null, // "YouTube", "Vimeo", "ThePirateBay", etc.
    val torrentHash: String? = null,
    val magnetUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastAccessedAt: Long? = null,
    val accessCount: Int = 0,
    val sourceApp: String,
    val version: Int = 1,
    val lastModified: Long = System.currentTimeMillis()
) {
    companion object {
        const val OBJECT_TYPE = "bookmark"

        const val TYPE_VIDEO = "video"
        const val TYPE_TORRENT = "torrent"
        const val TYPE_MAGNET = "magnet"
        const val TYPE_WEBSITE = "website"
        const val TYPE_PLAYLIST = "playlist"
        const val TYPE_CHANNEL = "channel"
    }

    fun isTorrentBookmark(): Boolean = type == TYPE_TORRENT || type == TYPE_MAGNET

    fun isMediaBookmark(): Boolean = type == TYPE_VIDEO || type == TYPE_PLAYLIST || type == TYPE_CHANNEL

    fun getDisplayTitle(): String = title ?: url

    fun incrementAccessCount(): BookmarkData = copy(
        accessCount = accessCount + 1,
        lastAccessedAt = System.currentTimeMillis(),
        lastModified = System.currentTimeMillis(),
        version = version + 1
    )
}
