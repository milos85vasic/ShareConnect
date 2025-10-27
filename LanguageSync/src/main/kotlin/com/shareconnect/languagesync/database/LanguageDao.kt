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


package com.shareconnect.languagesync.database

import androidx.room.*
import com.shareconnect.languagesync.models.LanguageData
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for language preferences
 */
@Dao
interface LanguageDao {

    @Query("SELECT * FROM synced_language_preference WHERE id = 'language_preference' LIMIT 1")
    suspend fun getLanguagePreference(): LanguageData?

    @Query("SELECT * FROM synced_language_preference WHERE id = 'language_preference' LIMIT 1")
    fun getLanguagePreferenceFlow(): Flow<LanguageData?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguagePreference(language: LanguageData)

    @Update
    suspend fun updateLanguagePreference(language: LanguageData)

    @Query("DELETE FROM synced_language_preference WHERE id = 'language_preference'")
    suspend fun deleteLanguagePreference()

    @Query("DELETE FROM synced_language_preference")
    suspend fun deleteAll()
}
