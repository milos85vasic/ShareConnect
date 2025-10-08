package com.shareconnect.preferencessync.repository

import com.shareconnect.preferencessync.database.PreferencesDao
import com.shareconnect.preferencessync.models.PreferencesData
import kotlinx.coroutines.flow.Flow

class PreferencesRepository(private val preferencesDao: PreferencesDao) {

    fun getAllPreferences(): Flow<List<PreferencesData>> = preferencesDao.getAllPreferences()
    suspend fun getAllPreferencesSync(): List<PreferencesData> = preferencesDao.getAllPreferencesSync()
    suspend fun getPreferenceById(prefId: String): PreferencesData? = preferencesDao.getPreferenceById(prefId)
    fun getPreferencesByCategory(category: String): Flow<List<PreferencesData>> = preferencesDao.getPreferencesByCategory(category)
    suspend fun getPreferenceByKey(category: String, key: String): PreferencesData? = preferencesDao.getPreferenceByKey(category, key)
    suspend fun getPreferencesByCategorySync(category: String): List<PreferencesData> = preferencesDao.getPreferencesByCategorySync(category)

    suspend fun insertPreference(preference: PreferencesData): Long = preferencesDao.insert(preference)
    suspend fun insertAllPreferences(preferences: List<PreferencesData>) = preferencesDao.insertAll(preferences)
    suspend fun updatePreference(preference: PreferencesData) = preferencesDao.update(preference)
    suspend fun deletePreference(prefId: String) = preferencesDao.deletePreference(prefId)
    suspend fun deletePreferencesByCategory(category: String) = preferencesDao.deletePreferencesByCategory(category)
    suspend fun deleteAllPreferences() = preferencesDao.deleteAllPreferences()
    suspend fun getPreferenceCount(): Int = preferencesDao.getPreferenceCount()
    suspend fun getPreferenceCountByCategory(category: String): Int = preferencesDao.getPreferenceCountByCategory(category)

    // Helper methods for common preference operations
    suspend fun setStringPreference(category: String, key: String, value: String, sourceApp: String, description: String? = null) {
        val pref = createPreference(category, key, value, PreferencesData.TYPE_STRING, sourceApp, description)
        insertPreference(pref)
    }

    suspend fun setIntPreference(category: String, key: String, value: Int, sourceApp: String, description: String? = null) {
        val pref = createPreference(category, key, value.toString(), PreferencesData.TYPE_INT, sourceApp, description)
        insertPreference(pref)
    }

    suspend fun setLongPreference(category: String, key: String, value: Long, sourceApp: String, description: String? = null) {
        val pref = createPreference(category, key, value.toString(), PreferencesData.TYPE_LONG, sourceApp, description)
        insertPreference(pref)
    }

    suspend fun setBooleanPreference(category: String, key: String, value: Boolean, sourceApp: String, description: String? = null) {
        val pref = createPreference(category, key, value.toString(), PreferencesData.TYPE_BOOLEAN, sourceApp, description)
        insertPreference(pref)
    }

    suspend fun setJsonPreference(category: String, key: String, value: String, sourceApp: String, description: String? = null) {
        val pref = createPreference(category, key, value, PreferencesData.TYPE_JSON, sourceApp, description)
        insertPreference(pref)
    }

    private fun createPreference(
        category: String,
        key: String,
        value: String,
        type: String,
        sourceApp: String,
        description: String?
    ): PreferencesData {
        val id = "${category}_${key}"
        val existing = null // We'll check in the manager
        return PreferencesData(
            id = id,
            category = category,
            key = key,
            value = value,
            type = type,
            description = description,
            sourceApp = sourceApp,
            version = 1,
            lastModified = System.currentTimeMillis()
        )
    }

    suspend fun getStringPreference(category: String, key: String, default: String? = null): String? {
        return getPreferenceByKey(category, key)?.getStringValue() ?: default
    }

    suspend fun getIntPreference(category: String, key: String, default: Int? = null): Int? {
        return getPreferenceByKey(category, key)?.getIntValue() ?: default
    }

    suspend fun getLongPreference(category: String, key: String, default: Long? = null): Long? {
        return getPreferenceByKey(category, key)?.getLongValue() ?: default
    }

    suspend fun getBooleanPreference(category: String, key: String, default: Boolean? = null): Boolean? {
        return getPreferenceByKey(category, key)?.getBooleanValue() ?: default
    }

    suspend fun getJsonPreference(category: String, key: String, default: String? = null): String? {
        return getPreferenceByKey(category, key)?.getJsonValue() ?: default
    }
}
