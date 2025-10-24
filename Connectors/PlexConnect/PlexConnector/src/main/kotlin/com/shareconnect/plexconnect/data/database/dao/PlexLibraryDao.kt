package com.shareconnect.plexconnect.data.database.dao

import androidx.room.*
import com.shareconnect.plexconnect.data.model.PlexLibrary
import kotlinx.coroutines.flow.Flow

@Dao
interface PlexLibraryDao {

    @Query("SELECT * FROM plex_libraries ORDER BY title ASC")
    fun getAllLibraries(): Flow<List<PlexLibrary>>

    @Query("SELECT * FROM plex_libraries WHERE serverId = :serverId ORDER BY title ASC")
    fun getLibrariesForServer(serverId: Long): Flow<List<PlexLibrary>>

    @Query("SELECT * FROM plex_libraries WHERE key = :libraryKey")
    suspend fun getLibraryByKey(libraryKey: String): PlexLibrary?

    @Query("SELECT * FROM plex_libraries WHERE serverId = :serverId AND type = :type ORDER BY title ASC")
    fun getLibrariesByType(serverId: Long, type: String): Flow<List<PlexLibrary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibrary(library: PlexLibrary): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibraries(libraries: List<PlexLibrary>): List<Long>

    @Update
    suspend fun updateLibrary(library: PlexLibrary)

    @Delete
    suspend fun deleteLibrary(library: PlexLibrary)

    @Query("DELETE FROM plex_libraries WHERE key = :libraryKey")
    suspend fun deleteLibraryByKey(libraryKey: String)

    @Query("DELETE FROM plex_libraries WHERE serverId = :serverId")
    suspend fun deleteLibrariesForServer(serverId: Long)

    @Query("DELETE FROM plex_libraries")
    suspend fun deleteAllLibraries()

    @Query("SELECT COUNT(*) FROM plex_libraries")
    suspend fun getLibraryCount(): Int

    @Query("SELECT COUNT(*) FROM plex_libraries WHERE serverId = :serverId")
    suspend fun getLibraryCountForServer(serverId: Long): Int
}