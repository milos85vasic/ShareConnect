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
