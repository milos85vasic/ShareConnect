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