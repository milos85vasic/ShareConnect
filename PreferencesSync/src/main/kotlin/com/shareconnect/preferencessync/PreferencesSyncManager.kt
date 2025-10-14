package com.shareconnect.preferencessync

import android.content.Context
import android.util.Log
import com.shareconnect.preferencessync.database.PreferencesDatabase
import com.shareconnect.preferencessync.models.*
import com.shareconnect.preferencessync.repository.PreferencesRepository
import digital.vasic.asinka.*
import digital.vasic.asinka.models.*
import digital.vasic.asinka.sync.SyncChange
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.net.ServerSocket

class PreferencesSyncManager private constructor(
    private val context: Context,
    private val appIdentifier: String,
    private val appName: String,
    private val appVersion: String,
    private val asinkaClient: AsinkaClient,
    private val repository: PreferencesRepository
) {
    private val TAG = "PreferencesSyncManager"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _preferenceChangeFlow = MutableSharedFlow<PreferencesData>(replay = 0, extraBufferCapacity = 10)
    val preferenceChangeFlow: SharedFlow<PreferencesData> = _preferenceChangeFlow.asSharedFlow()

    private var isStarted = false

    suspend fun start() {
        if (isStarted) return
        Log.d(TAG, "Starting PreferencesSyncManager for $appIdentifier")

        // Start Asinka client with retry logic for port conflicts
        try {
            asinkaClient.start()
        } catch (e: Exception) {
            if (e.message?.contains("bind failed: EADDRINUSE") == true) {
                Log.w(TAG, "Port conflict detected, retrying with different port", e)
                // Force recreation of singleton with new port
                synchronized(PreferencesSyncManager::class.java) {
                    INSTANCE = null
                }
                // Recreate with new port
                val newInstance = getInstance(context, appIdentifier, appName, appVersion)
                newInstance.asinkaClient.start()
                // Update the instance reference
                synchronized(PreferencesSyncManager::class.java) {
                    INSTANCE = newInstance
                }
            } else {
                throw e
            }
        }

        syncLocalPreferencesToAsinka()

        scope.launch {
            asinkaClient.syncManager.observeAllChanges().collect { change ->
                when (change) {
                    is SyncChange.Updated -> {
                        (change.obj as? SyncablePreferences)?.let { handleReceivedPreference(it) }
                    }
                    is SyncChange.Deleted -> handleDeletedPreference(change.objectId)
                }
            }
        }

        isStarted = true
        Log.d(TAG, "PreferencesSyncManager started")
    }

    suspend fun stop() {
        if (!isStarted) return
        asinkaClient.stop()
        isStarted = false
    }

    // Flow-based getters
    fun getAllPreferences(): Flow<List<PreferencesData>> = repository.getAllPreferences()
    fun getPreferencesByCategory(category: String): Flow<List<PreferencesData>> = repository.getPreferencesByCategory(category)

    // Download preferences
    fun getDownloadPreferences(): Flow<List<PreferencesData>> =
        repository.getPreferencesByCategory(PreferencesData.CATEGORY_DOWNLOAD)

    // Bandwidth preferences
    fun getBandwidthPreferences(): Flow<List<PreferencesData>> =
        repository.getPreferencesByCategory(PreferencesData.CATEGORY_BANDWIDTH)

    // Notification preferences
    fun getNotificationPreferences(): Flow<List<PreferencesData>> =
        repository.getPreferencesByCategory(PreferencesData.CATEGORY_NOTIFICATION)

    // UI preferences
    fun getUIPreferences(): Flow<List<PreferencesData>> =
        repository.getPreferencesByCategory(PreferencesData.CATEGORY_UI)

    // Connection preferences
    fun getConnectionPreferences(): Flow<List<PreferencesData>> =
        repository.getPreferencesByCategory(PreferencesData.CATEGORY_CONNECTION)

    // Update preferences
    fun getUpdatePreferences(): Flow<List<PreferencesData>> =
        repository.getPreferencesByCategory(PreferencesData.CATEGORY_UPDATE)

    // Advanced preferences
    fun getAdvancedPreferences(): Flow<List<PreferencesData>> =
        repository.getPreferencesByCategory(PreferencesData.CATEGORY_ADVANCED)

    // Suspend getters
    suspend fun getPreferenceById(prefId: String) = repository.getPreferenceById(prefId)
    suspend fun getPreferenceByKey(category: String, key: String) = repository.getPreferenceByKey(category, key)

    // Set preference methods
    suspend fun setPreference(preference: PreferencesData) {
        val existing = repository.getPreferenceById(preference.id)
        val updated = if (existing != null) {
            preference.copy(version = existing.version + 1, lastModified = System.currentTimeMillis())
        } else {
            preference
        }
        repository.insertPreference(updated)
        asinkaClient.syncManager.registerObject(SyncablePreferences.fromPreferencesData(updated))
        _preferenceChangeFlow.emit(updated)
        Log.d(TAG, "Preference set: ${updated.category}/${updated.key}")
    }

    suspend fun setStringPreference(category: String, key: String, value: String, description: String? = null) {
        val id = "${category}_${key}"
        val existing = repository.getPreferenceById(id)
        val pref = PreferencesData(
            id = id,
            category = category,
            key = key,
            value = value,
            type = PreferencesData.TYPE_STRING,
            description = description,
            sourceApp = appIdentifier,
            version = (existing?.version ?: 0) + 1,
            lastModified = System.currentTimeMillis()
        )
        setPreference(pref)
    }

    suspend fun setIntPreference(category: String, key: String, value: Int, description: String? = null) {
        val id = "${category}_${key}"
        val existing = repository.getPreferenceById(id)
        val pref = PreferencesData(
            id = id,
            category = category,
            key = key,
            value = value.toString(),
            type = PreferencesData.TYPE_INT,
            description = description,
            sourceApp = appIdentifier,
            version = (existing?.version ?: 0) + 1,
            lastModified = System.currentTimeMillis()
        )
        setPreference(pref)
    }

    suspend fun setBooleanPreference(category: String, key: String, value: Boolean, description: String? = null) {
        val id = "${category}_${key}"
        val existing = repository.getPreferenceById(id)
        val pref = PreferencesData(
            id = id,
            category = category,
            key = key,
            value = value.toString(),
            type = PreferencesData.TYPE_BOOLEAN,
            description = description,
            sourceApp = appIdentifier,
            version = (existing?.version ?: 0) + 1,
            lastModified = System.currentTimeMillis()
        )
        setPreference(pref)
    }

    suspend fun updatePreference(preference: PreferencesData) {
        val updated = preference.copy(version = preference.version + 1, lastModified = System.currentTimeMillis())
        repository.updatePreference(updated)
        asinkaClient.syncManager.updateObject(updated.id, SyncablePreferences.fromPreferencesData(updated).toFieldMap())
        _preferenceChangeFlow.emit(updated)
    }

    suspend fun deletePreference(prefId: String) {
        repository.deletePreference(prefId)
        asinkaClient.syncManager.deleteObject(prefId)
    }

    suspend fun deletePreferencesByCategory(category: String) {
        val prefs = repository.getPreferencesByCategorySync(category)
        prefs.forEach { deletePreference(it.id) }
    }

    private suspend fun syncLocalPreferencesToAsinka() {
        try {
            repository.getAllPreferencesSync().forEach {
                asinkaClient.syncManager.registerObject(SyncablePreferences.fromPreferencesData(it))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing preferences", e)
        }
    }

    private suspend fun handleReceivedPreference(syncablePreference: SyncablePreferences) {
        try {
            val preference = syncablePreference.getPreferencesData()
            val existing = repository.getPreferenceById(preference.id)

            if (existing == null) {
                repository.insertPreference(preference)
                _preferenceChangeFlow.emit(preference)
            } else if (preference.version > existing.version) {
                repository.updatePreference(preference)
                _preferenceChangeFlow.emit(preference)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling preference", e)
        }
    }

    private suspend fun handleDeletedPreference(prefId: String) {
        try {
            repository.deletePreference(prefId)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting preference", e)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: PreferencesSyncManager? = null

        /**
         * Check if a port is available
         */
        private fun isPortAvailable(port: Int): Boolean {
            return try {
                ServerSocket(port).use { true }
            } catch (e: Exception) {
                false
            }
        }

        /**
         * Find an available port starting from the preferred port
         */
        private fun findAvailablePort(preferredPort: Int, maxAttempts: Int = 10): Int {
            var port = preferredPort
            for (i in 0 until maxAttempts) {
                if (isPortAvailable(port)) {
                    return port
                }
                port++
            }
            throw IllegalStateException("No available ports found in range $preferredPort-${preferredPort + maxAttempts - 1}")
        }

        fun getInstance(context: Context, appId: String, appName: String, appVersion: String): PreferencesSyncManager {
            return INSTANCE ?: synchronized(this) {
                val schema = ObjectSchema(
                    objectType = PreferencesData.OBJECT_TYPE,
                    version = "1",
                    fields = listOf(
                        FieldSchema("id", FieldType.STRING),
                        FieldSchema("category", FieldType.STRING),
                        FieldSchema("key", FieldType.STRING),
                        FieldSchema("value", FieldType.STRING),
                        FieldSchema("type", FieldType.STRING),
                        FieldSchema("description", FieldType.STRING),
                        FieldSchema("sourceApp", FieldType.STRING),
                        FieldSchema("version", FieldType.INT),
                        FieldSchema("lastModified", FieldType.LONG)
                    )
                )

                val basePort = 8940
                val preferredPort = basePort + Math.abs(appId.hashCode() % 100)
                val uniquePort = findAvailablePort(preferredPort)

                Log.d("PreferencesSyncManager", "App $appId using port $uniquePort (preferred: $preferredPort)")

                val config = AsinkaConfig(
                    appId = appId,
                    appName = appName,
                    appVersion = appVersion,
                    serverPort = uniquePort,
                    serviceName = "preferences-sync",
                    exposedSchemas = listOf(schema),
                    capabilities = mapOf("preferences_sync" to "1.0")
                )
                val client = AsinkaClient.create(context, config)
                val db = PreferencesDatabase.getInstance(context)
                val repo = PreferencesRepository(db.preferencesDao())

                PreferencesSyncManager(context.applicationContext, appId, appName, appVersion, client, repo).also { INSTANCE = it }
            }
        }

        fun resetInstance() { INSTANCE = null }
    }
}
