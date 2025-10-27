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
