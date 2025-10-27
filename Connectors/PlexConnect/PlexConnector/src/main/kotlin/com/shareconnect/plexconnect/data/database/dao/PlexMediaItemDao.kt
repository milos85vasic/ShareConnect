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


package com.shareconnect.plexconnect.data.database.dao

import androidx.room.*
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PlexMediaItemDao {

    @Query("SELECT * FROM plex_media_items ORDER BY addedAt DESC")
    fun getAllMediaItems(): Flow<List<PlexMediaItem>>

    @Query("SELECT * FROM plex_media_items WHERE ratingKey = :ratingKey")
    suspend fun getMediaItemByKey(ratingKey: String): PlexMediaItem?

    @Query("SELECT * FROM plex_media_items WHERE serverId = :serverId ORDER BY addedAt DESC")
    fun getMediaItemsForServer(serverId: Long): Flow<List<PlexMediaItem>>

    @Query("SELECT * FROM plex_media_items WHERE librarySectionID = :libraryId ORDER BY addedAt DESC")
    fun getMediaItemsForLibrary(libraryId: Long): Flow<List<PlexMediaItem>>

    @Query("SELECT * FROM plex_media_items WHERE type = :type ORDER BY addedAt DESC")
    fun getMediaItemsByType(type: String): Flow<List<PlexMediaItem>>

    @Query("SELECT * FROM plex_media_items WHERE viewCount > 0 ORDER BY lastViewedAt DESC")
    fun getWatchedItems(): Flow<List<PlexMediaItem>>

    @Query("SELECT * FROM plex_media_items WHERE viewCount = 0 AND viewOffset > 0 ORDER BY updatedAt DESC")
    fun getInProgressItems(): Flow<List<PlexMediaItem>>

    @Query("SELECT * FROM plex_media_items WHERE title LIKE '%' || :query || '%' OR originalTitle LIKE '%' || :query || '%' ORDER BY addedAt DESC")
    fun searchMediaItems(query: String): Flow<List<PlexMediaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(item: PlexMediaItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItems(items: List<PlexMediaItem>): List<Long>

    @Update
    suspend fun updateMediaItem(item: PlexMediaItem)

    @Delete
    suspend fun deleteMediaItem(item: PlexMediaItem)

    @Query("DELETE FROM plex_media_items WHERE ratingKey = :ratingKey")
    suspend fun deleteMediaItemByKey(ratingKey: String)

    @Query("DELETE FROM plex_media_items WHERE serverId = :serverId")
    suspend fun deleteMediaItemsForServer(serverId: Long)

    @Query("DELETE FROM plex_media_items WHERE librarySectionID = :libraryId")
    suspend fun deleteMediaItemsForLibrary(libraryId: Long)

    @Query("DELETE FROM plex_media_items")
    suspend fun deleteAllMediaItems()

    @Query("SELECT COUNT(*) FROM plex_media_items")
    suspend fun getMediaItemCount(): Int

    @Query("SELECT COUNT(*) FROM plex_media_items WHERE serverId = :serverId")
    suspend fun getMediaItemCountForServer(serverId: Long): Int

    @Query("SELECT COUNT(*) FROM plex_media_items WHERE librarySectionID = :libraryId")
    suspend fun getMediaItemCountForLibrary(libraryId: Long): Int

    @Query("SELECT COUNT(*) FROM plex_media_items WHERE viewCount > 0")
    suspend fun getWatchedCount(): Int

    @Query("SELECT COUNT(*) FROM plex_media_items WHERE viewCount = 0 AND viewOffset > 0")
    suspend fun getInProgressCount(): Int
}