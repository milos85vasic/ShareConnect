package com.shareconnect.themesync.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shareconnect.themesync.models.ThemeData
import kotlinx.coroutines.flow.Flow

@Dao
interface ThemeDao {
    @Query("SELECT * FROM synced_themes ORDER BY sourceApp, colorScheme, isDarkMode")
    fun getAllThemes(): Flow<List<ThemeData>>

    @Query("SELECT * FROM synced_themes ORDER BY sourceApp, colorScheme, isDarkMode")
    suspend fun getAllThemesSync(): List<ThemeData>

    @Query("SELECT * FROM synced_themes WHERE id = :themeId")
    suspend fun getThemeById(themeId: String): ThemeData?

    @Query("SELECT * FROM synced_themes WHERE id = :themeId")
    fun observeThemeById(themeId: String): Flow<ThemeData?>

    @Query("SELECT * FROM synced_themes WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultTheme(): ThemeData?

    @Query("SELECT * FROM synced_themes WHERE isDefault = 1 LIMIT 1")
    fun observeDefaultTheme(): Flow<ThemeData?>

    @Query("SELECT * FROM synced_themes WHERE colorScheme = :colorScheme AND isDarkMode = :isDarkMode LIMIT 1")
    suspend fun getThemeByColorSchemeAndMode(colorScheme: String, isDarkMode: Boolean): ThemeData?

    @Query("SELECT * FROM synced_themes WHERE sourceApp = :sourceApp")
    suspend fun getThemesBySourceApp(sourceApp: String): List<ThemeData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(theme: ThemeData): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(themes: List<ThemeData>)

    @Update
    suspend fun update(theme: ThemeData)

    @Query("UPDATE synced_themes SET isDefault = 0")
    suspend fun clearDefaultThemes()

    @Query("UPDATE synced_themes SET isDefault = 1 WHERE id = :themeId")
    suspend fun setDefaultTheme(themeId: String)

    @Query("DELETE FROM synced_themes WHERE id = :themeId")
    suspend fun deleteTheme(themeId: String)

    @Query("DELETE FROM synced_themes")
    suspend fun deleteAllThemes()

    @Query("SELECT COUNT(*) FROM synced_themes")
    suspend fun getThemeCount(): Int

    @Query("SELECT * FROM synced_themes WHERE isCustom = 1")
    fun getCustomThemes(): Flow<List<ThemeData>>

    @Query("SELECT * FROM synced_themes WHERE isCustom = 1")
    suspend fun getCustomThemesSync(): List<ThemeData>

    @Query("SELECT * FROM synced_themes WHERE isCustom = 1 AND sourceApp = :sourceApp")
    suspend fun getCustomThemesBySourceApp(sourceApp: String): List<ThemeData>

    @Query("SELECT * FROM synced_themes WHERE isCustom = 0")
    fun getDefaultThemes(): Flow<List<ThemeData>>

    @Query("SELECT * FROM synced_themes WHERE isCustom = 0")
    suspend fun getDefaultThemesSync(): List<ThemeData>
}
