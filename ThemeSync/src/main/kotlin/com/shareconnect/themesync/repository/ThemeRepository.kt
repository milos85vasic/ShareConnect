package com.shareconnect.themesync.repository

import android.content.Context
import com.shareconnect.themesync.database.ThemeDao
import com.shareconnect.themesync.database.ThemeDatabase
import com.shareconnect.themesync.models.ThemeData
import kotlinx.coroutines.flow.Flow

class ThemeRepository(
    context: Context,
    private val appIdentifier: String,
    testDao: ThemeDao? = null
) {
    private val themeDao: ThemeDao = testDao ?: ThemeDatabase.getInstance(context).themeDao()

    fun getAllThemes(): Flow<List<ThemeData>> = themeDao.getAllThemes()

    suspend fun getAllThemesSync(): List<ThemeData> = themeDao.getAllThemesSync()

    suspend fun getThemeById(themeId: String): ThemeData? = themeDao.getThemeById(themeId)

    fun observeThemeById(themeId: String): Flow<ThemeData?> = themeDao.observeThemeById(themeId)

    suspend fun getDefaultTheme(): ThemeData? = themeDao.getDefaultTheme()

    fun observeDefaultTheme(): Flow<ThemeData?> = themeDao.observeDefaultTheme()

    suspend fun getThemeByColorSchemeAndMode(
        colorScheme: String,
        isDarkMode: Boolean
    ): ThemeData? = themeDao.getThemeByColorSchemeAndMode(colorScheme, isDarkMode)

    suspend fun getThemesBySourceApp(sourceApp: String): List<ThemeData> =
        themeDao.getThemesBySourceApp(sourceApp)

    suspend fun insertTheme(theme: ThemeData): Long = themeDao.insert(theme)

    suspend fun insertAll(themes: List<ThemeData>) = themeDao.insertAll(themes)

    suspend fun updateTheme(theme: ThemeData) = themeDao.update(theme)

    suspend fun setDefaultTheme(themeId: String) {
        themeDao.clearDefaultThemes()
        themeDao.setDefaultTheme(themeId)
    }

    suspend fun deleteTheme(themeId: String) = themeDao.deleteTheme(themeId)

    suspend fun deleteAllThemes() = themeDao.deleteAllThemes()

    suspend fun getThemeCount(): Int = themeDao.getThemeCount()

    fun getCustomThemes(): Flow<List<ThemeData>> = themeDao.getCustomThemes()

    suspend fun getCustomThemesSync(): List<ThemeData> = themeDao.getCustomThemesSync()

    suspend fun getCustomThemesBySourceApp(sourceApp: String): List<ThemeData> =
        themeDao.getCustomThemesBySourceApp(sourceApp)

    fun getDefaultThemes(): Flow<List<ThemeData>> = themeDao.getDefaultThemes()

    suspend fun getDefaultThemesSync(): List<ThemeData> = themeDao.getDefaultThemesSync()

    suspend fun createCustomTheme(themeData: ThemeData): Long = themeDao.insert(themeData)

    suspend fun updateCustomTheme(themeData: ThemeData) = themeDao.update(themeData)

    suspend fun deleteCustomTheme(themeId: String) = themeDao.deleteTheme(themeId)

    suspend fun initializeDefaultThemes() {
        val count = getThemeCount()
        if (count == 0) {
            val defaultThemes = ThemeData.getDefaultThemes(appIdentifier)
            insertAll(defaultThemes)
        }
    }
}
