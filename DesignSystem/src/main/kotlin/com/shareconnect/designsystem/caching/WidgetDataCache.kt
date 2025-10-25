package com.shareconnect.designsystem.caching

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

/**
 * Widget Data Cache
 * Persistent cache for widget data using DataStore
 */
class WidgetDataCache(private val context: Context) {

    companion object {
        private val Context.widgetDataStore: DataStore<Preferences> by preferencesDataStore(
            name = "widget_data_cache"
        )

        private const val MAX_CACHE_AGE_MS = 30 * 60 * 1000L // 30 minutes
    }

    private val dataStore = context.widgetDataStore
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Save widget data to cache
     */
    suspend fun <T> saveWidgetData(
        widgetKey: String,
        data: T,
        serializer: (T) -> String
    ) {
        val cacheEntry = CacheEntry(
            data = serializer(data),
            timestamp = System.currentTimeMillis()
        )

        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(widgetKey)] = json.encodeToString(cacheEntry)
        }
    }

    /**
     * Load widget data from cache
     */
    suspend fun <T> loadWidgetData(
        widgetKey: String,
        deserializer: (String) -> T
    ): CachedData<T>? {
        val preferences = dataStore.data.first()
        val cacheJson = preferences[stringPreferencesKey(widgetKey)] ?: return null

        return try {
            val cacheEntry = json.decodeFromString<CacheEntry>(cacheJson)
            val age = System.currentTimeMillis() - cacheEntry.timestamp
            val isStale = age > MAX_CACHE_AGE_MS

            CachedData(
                data = deserializer(cacheEntry.data),
                timestamp = cacheEntry.timestamp,
                isStale = isStale
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Observe widget data changes
     */
    fun <T> observeWidgetData(
        widgetKey: String,
        deserializer: (String) -> T
    ): Flow<CachedData<T>?> {
        return dataStore.data.map { preferences ->
            val cacheJson = preferences[stringPreferencesKey(widgetKey)] ?: return@map null

            try {
                val cacheEntry = json.decodeFromString<CacheEntry>(cacheJson)
                val age = System.currentTimeMillis() - cacheEntry.timestamp
                val isStale = age > MAX_CACHE_AGE_MS

                CachedData(
                    data = deserializer(cacheEntry.data),
                    timestamp = cacheEntry.timestamp,
                    isStale = isStale
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Clear widget cache
     */
    suspend fun clearWidgetCache(widgetKey: String) {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(widgetKey))
        }
    }

    /**
     * Clear all widget caches
     */
    suspend fun clearAllCaches() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Check if cache exists and is fresh
     */
    suspend fun isCacheFresh(widgetKey: String): Boolean {
        val preferences = dataStore.data.first()
        val cacheJson = preferences[stringPreferencesKey(widgetKey)] ?: return false

        return try {
            val cacheEntry = json.decodeFromString<CacheEntry>(cacheJson)
            val age = System.currentTimeMillis() - cacheEntry.timestamp
            age <= MAX_CACHE_AGE_MS
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Cache entry with timestamp
 */
@Serializable
private data class CacheEntry(
    val data: String,
    val timestamp: Long
)

/**
 * Cached data with metadata
 */
data class CachedData<T>(
    val data: T,
    val timestamp: Long,
    val isStale: Boolean
) {
    val ageMinutes: Long
        get() = (System.currentTimeMillis() - timestamp) / (60 * 1000)
}

/**
 * Widget-specific cache helpers
 */

// Shared JSON instance for widget cache extension functions
private val widgetCacheJson = Json { ignoreUnknownKeys = true }

// HomeAssistant
suspend fun WidgetDataCache.saveHomeAssistantData(
    lightsOn: Int,
    totalLights: Int,
    switchesOn: Int,
    totalSwitches: Int,
    totalSensors: Int,
    isConnected: Boolean
) {
    saveWidgetData(
        "home_assistant",
        HomeAssistantData(lightsOn, totalLights, switchesOn, totalSwitches, totalSensors, isConnected)
    ) { widgetCacheJson.encodeToString(it) }
}

suspend fun WidgetDataCache.loadHomeAssistantData(): CachedData<HomeAssistantData>? {
    return loadWidgetData("home_assistant") {
        widgetCacheJson.decodeFromString<HomeAssistantData>(it)
    }
}

@Serializable
data class HomeAssistantData(
    val lightsOn: Int,
    val totalLights: Int,
    val switchesOn: Int,
    val totalSwitches: Int,
    val totalSensors: Int,
    val isConnected: Boolean
)

// Jellyfin
suspend fun WidgetDataCache.saveJellyfinData(
    nowPlaying: String?,
    activeSessions: Int,
    playing: Int,
    isConnected: Boolean
) {
    saveWidgetData(
        "jellyfin",
        JellyfinData(nowPlaying, activeSessions, playing, isConnected)
    ) { widgetCacheJson.encodeToString(it) }
}

suspend fun WidgetDataCache.loadJellyfinData(): CachedData<JellyfinData>? {
    return loadWidgetData("jellyfin") {
        widgetCacheJson.decodeFromString<JellyfinData>(it)
    }
}

@Serializable
data class JellyfinData(
    val nowPlaying: String?,
    val activeSessions: Int,
    val playing: Int,
    val isConnected: Boolean
)

// Netdata
suspend fun WidgetDataCache.saveNetdataData(
    healthStatus: String,
    cpuUsage: Int,
    ramUsage: Int,
    diskUsage: Int,
    criticalAlarms: Int,
    warningAlarms: Int
) {
    saveWidgetData(
        "netdata",
        NetdataData(healthStatus, cpuUsage, ramUsage, diskUsage, criticalAlarms, warningAlarms)
    ) { widgetCacheJson.encodeToString(it) }
}

suspend fun WidgetDataCache.loadNetdataData(): CachedData<NetdataData>? {
    return loadWidgetData("netdata") {
        widgetCacheJson.decodeFromString<NetdataData>(it)
    }
}

@Serializable
data class NetdataData(
    val healthStatus: String,
    val cpuUsage: Int,
    val ramUsage: Int,
    val diskUsage: Int,
    val criticalAlarms: Int,
    val warningAlarms: Int
)

// Portainer
suspend fun WidgetDataCache.savePortainerData(
    runningContainers: Int,
    stoppedContainers: Int,
    imagesCount: Int,
    totalContainers: Int,
    isConnected: Boolean
) {
    saveWidgetData(
        "portainer",
        PortainerData(runningContainers, stoppedContainers, imagesCount, totalContainers, isConnected)
    ) { widgetCacheJson.encodeToString(it) }
}

suspend fun WidgetDataCache.loadPortainerData(): CachedData<PortainerData>? {
    return loadWidgetData("portainer") {
        widgetCacheJson.decodeFromString<PortainerData>(it)
    }
}

@Serializable
data class PortainerData(
    val runningContainers: Int,
    val stoppedContainers: Int,
    val imagesCount: Int,
    val totalContainers: Int,
    val isConnected: Boolean
)
