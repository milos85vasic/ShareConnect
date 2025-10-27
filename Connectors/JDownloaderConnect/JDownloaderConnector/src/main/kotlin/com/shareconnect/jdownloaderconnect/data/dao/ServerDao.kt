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


package com.shareconnect.jdownloaderconnect.data.dao

import androidx.room.*
import com.shareconnect.jdownloaderconnect.data.model.ServerProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {

    @Query("SELECT * FROM server_profiles")
    fun getAllServers(): Flow<List<ServerProfile>>

    @Query("SELECT * FROM server_profiles WHERE id = :id")
    suspend fun getServerById(id: String): ServerProfile?

    @Query("SELECT * FROM server_profiles WHERE isDefault = 1")
    fun getDefaultServer(): Flow<ServerProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: ServerProfile)

    @Update
    suspend fun updateServer(server: ServerProfile)

    @Delete
    suspend fun deleteServer(server: ServerProfile)

    @Query("UPDATE server_profiles SET isDefault = 0")
    suspend fun clearDefaultServers()

    @Query("UPDATE server_profiles SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultServer(id: String)
}