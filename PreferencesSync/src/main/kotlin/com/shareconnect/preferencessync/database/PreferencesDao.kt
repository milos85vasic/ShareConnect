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


package com.shareconnect.preferencessync.database

import androidx.room.*
import com.shareconnect.preferencessync.models.PreferencesData
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferencesDao {
    @Query("SELECT * FROM synced_preferences ORDER BY category ASC, key ASC")
    fun getAllPreferences(): Flow<List<PreferencesData>>

    @Query("SELECT * FROM synced_preferences ORDER BY category ASC, key ASC")
    suspend fun getAllPreferencesSync(): List<PreferencesData>

    @Query("SELECT * FROM synced_preferences WHERE id = :prefId")
    suspend fun getPreferenceById(prefId: String): PreferencesData?

    @Query("SELECT * FROM synced_preferences WHERE category = :category ORDER BY key ASC")
    fun getPreferencesByCategory(category: String): Flow<List<PreferencesData>>

    @Query("SELECT * FROM synced_preferences WHERE category = :category AND key = :key")
    suspend fun getPreferenceByKey(category: String, key: String): PreferencesData?

    @Query("SELECT * FROM synced_preferences WHERE category = :category")
    suspend fun getPreferencesByCategorySync(category: String): List<PreferencesData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preference: PreferencesData): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(preferences: List<PreferencesData>)

    @Update
    suspend fun update(preference: PreferencesData)

    @Query("DELETE FROM synced_preferences WHERE id = :prefId")
    suspend fun deletePreference(prefId: String)

    @Query("DELETE FROM synced_preferences WHERE category = :category")
    suspend fun deletePreferencesByCategory(category: String)

    @Query("DELETE FROM synced_preferences")
    suspend fun deleteAllPreferences()

    @Query("SELECT COUNT(*) FROM synced_preferences")
    suspend fun getPreferenceCount(): Int

    @Query("SELECT COUNT(*) FROM synced_preferences WHERE category = :category")
    suspend fun getPreferenceCountByCategory(category: String): Int
}
