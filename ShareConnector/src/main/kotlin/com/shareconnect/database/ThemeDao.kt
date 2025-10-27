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
interface ThemeDao {
    @Query("SELECT * FROM themes")
    fun getAllThemes(): List<Theme>

    @Query("SELECT * FROM themes WHERE id = :id")
    fun getThemeById(id: Int): Theme?

    @Query("SELECT * FROM themes WHERE isDefault = 1 LIMIT 1")
    fun getDefaultTheme(): Theme?

    @Query("SELECT * FROM themes WHERE colorScheme = :colorScheme AND isDarkMode = :isDarkMode LIMIT 1")
    fun getThemeByColorSchemeAndMode(colorScheme: String, isDarkMode: Boolean): Theme?

    @Insert
    fun insert(theme: Theme): Long

    @Insert
    fun insertTheme(theme: Theme): Long

    @Update
    fun update(theme: Theme)

    @Update
    fun updateTheme(theme: Theme)

    @Delete
    fun delete(theme: Theme)

    @Delete
    fun deleteTheme(theme: Theme)

    @Query("UPDATE themes SET isDefault = 0")
    fun clearDefaultThemes()

    @Query("UPDATE themes SET isDefault = 0")
    fun clearAllDefaults()

    @Query("UPDATE themes SET isDefault = 1 WHERE id = :id")
    fun setDefaultTheme(id: Int)

    @Query("UPDATE themes SET isDefault = 1 WHERE id = :id")
    fun setAsDefault(id: Int)
}