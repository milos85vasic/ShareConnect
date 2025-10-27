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
