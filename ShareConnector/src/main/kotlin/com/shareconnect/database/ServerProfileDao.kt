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


package com.shareconnect.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ServerProfileDao {
    @Query("SELECT * FROM server_profiles")
    fun getAllProfiles(): List<ServerProfileEntity>

    @Query("SELECT * FROM server_profiles WHERE id = :id")
    fun getProfileById(id: String): ServerProfileEntity?

    @Query("SELECT * FROM server_profiles WHERE isDefault = 1 LIMIT 1")
    fun getDefaultProfile(): ServerProfileEntity?

    @Query("SELECT * FROM server_profiles WHERE serviceType = :serviceType")
    fun getProfilesByServiceType(serviceType: String): List<ServerProfileEntity>

    @Insert
    fun insert(profile: ServerProfileEntity)

    @Update
    fun update(profile: ServerProfileEntity)

    @Delete
    fun delete(profile: ServerProfileEntity)

    @Query("DELETE FROM server_profiles")
    fun deleteAll()

    @Query("UPDATE server_profiles SET isDefault = 0")
    fun clearAllDefaults()

    @Query("UPDATE server_profiles SET isDefault = 1 WHERE id = :profileId")
    fun setDefaultProfile(profileId: String)
}