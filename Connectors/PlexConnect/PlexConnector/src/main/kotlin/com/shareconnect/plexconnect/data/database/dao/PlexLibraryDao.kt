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