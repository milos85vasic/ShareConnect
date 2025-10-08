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
