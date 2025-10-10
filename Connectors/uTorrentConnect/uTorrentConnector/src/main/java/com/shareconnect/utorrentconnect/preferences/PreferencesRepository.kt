package com.shareconnect.utorrentconnect.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.shareconnect.utorrentconnect.theme.NightMode

class PreferencesRepository constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun setNightMode(mode: NightMode) {
        dataStore.edit { preferences ->
            preferences[KEY_NIGHT_MODE] = mode.name
        }
    }

    fun getNightMode(): Flow<NightMode> {
        return dataStore.data.map { preferences ->
            NightMode.valueOf(preferences[KEY_NIGHT_MODE] ?: NightMode.AUTO.name)
        }
    }

    companion object {
        private val KEY_NIGHT_MODE = stringPreferencesKey("key_night_mode")
    }
}
