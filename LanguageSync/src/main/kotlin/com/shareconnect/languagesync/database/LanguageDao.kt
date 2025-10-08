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
