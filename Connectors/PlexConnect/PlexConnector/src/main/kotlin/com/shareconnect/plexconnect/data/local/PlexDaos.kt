package com.shareconnect.plexconnect.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Plex server operations
 */
@Dao
interface PlexServerDao {
    @Query("SELECT * FROM plex_servers")
    fun getAllServers(): Flow<List<PlexServerEntity>>

    @Query("SELECT * FROM plex_servers WHERE id = :serverId")
    fun getServerById(serverId: String): Flow<PlexServerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: PlexServerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<PlexServerEntity>)

    @Update
    suspend fun updateServer(server: PlexServerEntity)

    @Query("DELETE FROM plex_servers WHERE id = :serverId")
    suspend fun deleteServer(serverId: String)

    @Query("DELETE FROM plex_servers")
    suspend fun clearAllServers()
}

/**
 * DAO for Plex library operations
 */
@Dao
interface PlexLibraryDao {
    @Query("SELECT * FROM plex_libraries WHERE serverId = :serverId")
    fun getLibrariesForServer(serverId: String): Flow<List<PlexLibraryEntity>>

    @Query("SELECT * FROM plex_libraries WHERE id = :libraryId")
    fun getLibraryById(libraryId: String): Flow<PlexLibraryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibrary(library: PlexLibraryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibraries(libraries: List<PlexLibraryEntity>)

    @Update
    suspend fun updateLibrary(library: PlexLibraryEntity)

    @Query("DELETE FROM plex_libraries WHERE serverId = :serverId")
    suspend fun deleteLibrariesForServer(serverId: String)

    @Query("DELETE FROM plex_libraries WHERE id = :libraryId")
    suspend fun deleteLibrary(libraryId: String)
}

/**
 * DAO for Plex media item operations
 */
@Dao
interface PlexMediaItemDao {
    @Query("SELECT * FROM plex_media_items WHERE libraryId = :libraryId")
    fun getMediaItemsForLibrary(libraryId: String): Flow<List<PlexMediaItemEntity>>

    @Query("SELECT * FROM plex_media_items WHERE id = :mediaItemId")
    fun getMediaItemById(mediaItemId: String): Flow<PlexMediaItemEntity?>

    @Query("SELECT * FROM plex_media_items WHERE isWatched = 0")
    fun getUnwatchedMediaItems(): Flow<List<PlexMediaItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(mediaItem: PlexMediaItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItems(mediaItems: List<PlexMediaItemEntity>)

    @Update
    suspend fun updateMediaItem(mediaItem: PlexMediaItemEntity)

    @Query("UPDATE plex_media_items SET isWatched = 1, lastPlayedTime = :playedTime WHERE id = :mediaItemId")
    suspend fun markAsWatched(mediaItemId: String, playedTime: Long)

    @Query("UPDATE plex_media_items SET isWatched = 0, lastPlayedTime = NULL WHERE id = :mediaItemId")
    suspend fun markAsUnwatched(mediaItemId: String)

    @Query("DELETE FROM plex_media_items WHERE libraryId = :libraryId")
    suspend fun deleteMediaItemsForLibrary(libraryId: String)

    @Query("DELETE FROM plex_media_items WHERE id = :mediaItemId")
    suspend fun deleteMediaItem(mediaItemId: String)
}