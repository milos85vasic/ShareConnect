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