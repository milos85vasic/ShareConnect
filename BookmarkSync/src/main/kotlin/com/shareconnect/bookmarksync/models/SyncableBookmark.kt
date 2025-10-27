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

import digital.vasic.asinka.models.SyncableObject

class SyncableBookmark(
    private var bookmarkData: BookmarkData
) : SyncableObject {

    override val objectId: String get() = bookmarkData.id
    override val objectType: String get() = BookmarkData.OBJECT_TYPE
    override val version: Int get() = bookmarkData.version

    fun getBookmarkData(): BookmarkData = bookmarkData

    override fun toFieldMap(): Map<String, Any?> = mapOf(
        "id" to bookmarkData.id,
        "url" to bookmarkData.url,
        "title" to bookmarkData.title,
        "description" to bookmarkData.description,
        "thumbnailUrl" to bookmarkData.thumbnailUrl,
        "type" to bookmarkData.type,
        "category" to bookmarkData.category,
        "tags" to bookmarkData.tags,
        "isFavorite" to bookmarkData.isFavorite,
        "notes" to bookmarkData.notes,
        "serviceProvider" to bookmarkData.serviceProvider,
        "torrentHash" to bookmarkData.torrentHash,
        "magnetUri" to bookmarkData.magnetUri,
        "createdAt" to bookmarkData.createdAt,
        "lastAccessedAt" to bookmarkData.lastAccessedAt,
        "accessCount" to bookmarkData.accessCount,
        "sourceApp" to bookmarkData.sourceApp,
        "version" to bookmarkData.version,
        "lastModified" to bookmarkData.lastModified
    )

    override fun fromFieldMap(fields: Map<String, Any?>) {
        val versionValue = when (val v = fields["version"]) {
            is Int -> v
            is Long -> v.toInt()
            else -> bookmarkData.version
        }

        val createdAtValue = when (val ca = fields["createdAt"]) {
            is Int -> ca.toLong()
            is Long -> ca
            else -> bookmarkData.createdAt
        }

        val lastAccessedAtValue = when (val laa = fields["lastAccessedAt"]) {
            is Int -> laa.toLong()
            is Long -> laa
            null -> null
            else -> bookmarkData.lastAccessedAt
        }

        val accessCountValue = when (val ac = fields["accessCount"]) {
            is Int -> ac
            is Long -> ac.toInt()
            else -> bookmarkData.accessCount
        }

        val lastModifiedValue = when (val lm = fields["lastModified"]) {
            is Int -> lm.toLong()
            is Long -> lm
            else -> bookmarkData.lastModified
        }

        bookmarkData = BookmarkData(
            id = fields["id"] as? String ?: bookmarkData.id,
            url = fields["url"] as? String ?: bookmarkData.url,
            title = fields["title"] as? String,
            description = fields["description"] as? String,
            thumbnailUrl = fields["thumbnailUrl"] as? String,
            type = fields["type"] as? String ?: bookmarkData.type,
            category = fields["category"] as? String,
            tags = fields["tags"] as? String,
            isFavorite = fields["isFavorite"] as? Boolean ?: bookmarkData.isFavorite,
            notes = fields["notes"] as? String,
            serviceProvider = fields["serviceProvider"] as? String,
            torrentHash = fields["torrentHash"] as? String,
            magnetUri = fields["magnetUri"] as? String,
            createdAt = createdAtValue,
            lastAccessedAt = lastAccessedAtValue,
            accessCount = accessCountValue,
            sourceApp = fields["sourceApp"] as? String ?: bookmarkData.sourceApp,
            version = versionValue,
            lastModified = lastModifiedValue
        )
    }

    companion object {
        fun fromBookmarkData(bookmarkData: BookmarkData) = SyncableBookmark(bookmarkData)
    }
}
