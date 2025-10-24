package com.shareconnect.plexconnect.data.database.dao

import androidx.room.*
import com.shareconnect.plexconnect.data.model.PlexServer
import kotlinx.coroutines.flow.Flow

@Dao
interface PlexServerDao {

    @Query("SELECT * FROM plex_servers ORDER BY updatedAt DESC")
    fun getAllServers(): Flow<List<PlexServer>>

    @Query("SELECT * FROM plex_servers WHERE id = :serverId")
    suspend fun getServerById(serverId: Long): PlexServer?

    @Query("SELECT * FROM plex_servers WHERE machineIdentifier = :machineIdentifier")
    suspend fun getServerByMachineIdentifier(machineIdentifier: String): PlexServer?

    @Query("SELECT * FROM plex_servers WHERE isLocal = 1 ORDER BY updatedAt DESC")
    fun getLocalServers(): Flow<List<PlexServer>>

    @Query("SELECT * FROM plex_servers WHERE isOwned = 1 ORDER BY updatedAt DESC")
    fun getOwnedServers(): Flow<List<PlexServer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: PlexServer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<PlexServer>): List<Long>

    @Update
    suspend fun updateServer(server: PlexServer)

    @Delete
    suspend fun deleteServer(server: PlexServer)

    @Query("DELETE FROM plex_servers WHERE id = :serverId")
    suspend fun deleteServerById(serverId: Long)

    @Query("DELETE FROM plex_servers")
    suspend fun deleteAllServers()

    @Query("SELECT COUNT(*) FROM plex_servers")
    suspend fun getServerCount(): Int

    @Query("SELECT COUNT(*) FROM plex_servers WHERE token IS NOT NULL AND token != ''")
    suspend fun getAuthenticatedServerCount(): Int
}