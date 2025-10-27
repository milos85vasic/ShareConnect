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


package com.shareconnect.languagesync.repository

import com.shareconnect.languagesync.database.LanguageDao
import com.shareconnect.languagesync.models.LanguageData
import kotlinx.coroutines.flow.Flow

/**
 * Repository for language preference data access
 */
class LanguageRepository(private val languageDao: LanguageDao) {

    /**
     * Get the current language preference
     */
    suspend fun getLanguagePreference(): LanguageData? {
        return languageDao.getLanguagePreference()
    }

    /**
     * Get language preference as Flow for reactive updates
     */
    fun getLanguagePreferenceFlow(): Flow<LanguageData?> {
        return languageDao.getLanguagePreferenceFlow()
    }

    /**
     * Set the language preference
     */
    suspend fun setLanguagePreference(language: LanguageData) {
        languageDao.insertLanguagePreference(language)
    }

    /**
     * Update the language preference
     */
    suspend fun updateLanguagePreference(language: LanguageData) {
        languageDao.updateLanguagePreference(language)
    }

    /**
     * Delete the language preference
     */
    suspend fun deleteLanguagePreference() {
        languageDao.deleteLanguagePreference()
    }

    /**
     * Get or create default language preference
     */
    suspend fun getOrCreateDefault(): LanguageData {
        return getLanguagePreference() ?: LanguageData.createDefault().also {
            setLanguagePreference(it)
        }
    }
}
