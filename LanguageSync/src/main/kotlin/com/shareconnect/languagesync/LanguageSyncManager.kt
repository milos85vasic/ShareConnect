package com.shareconnect.languagesync

import android.content.Context
import android.util.Log
import digital.vasic.asinka.AsinkaClient
import digital.vasic.asinka.AsinkaConfig
import digital.vasic.asinka.models.ObjectSchema
import digital.vasic.asinka.models.FieldSchema
import digital.vasic.asinka.models.FieldType
import digital.vasic.asinka.sync.SyncChange
import com.shareconnect.languagesync.database.LanguageDatabase
import com.shareconnect.languagesync.models.LanguageData
import com.shareconnect.languagesync.models.SyncableLanguage
import com.shareconnect.languagesync.repository.LanguageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Singleton manager for language preference synchronization across apps
 */
class LanguageSyncManager private constructor(
    private val context: Context,
    private val appIdentifier: String,
    private val asinkaClient: AsinkaClient,
    private val repository: LanguageRepository
) {

    private val tag = "LanguageSyncManager"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _languageChangeFlow = MutableSharedFlow<LanguageData>()
    val languageChangeFlow: Flow<LanguageData> = _languageChangeFlow.asSharedFlow()

    private var isStarted = false

    /**
     * Start the language sync manager
     */
    suspend fun start() {
        if (isStarted) {
            Log.w(tag, "LanguageSyncManager already started")
            return
        }
        isStarted = true

        Log.d(tag, "Starting LanguageSyncManager")

        // Start Asinka client
        asinkaClient.start()

        // Register existing language preference with Asinka
        val existingLanguage = repository.getLanguagePreference()
        if (existingLanguage != null) {
            val syncableLanguage = SyncableLanguage.fromLanguageData(existingLanguage)
            asinkaClient.syncManager.registerObject(syncableLanguage)
        } else {
            // Create and register default
            val default = LanguageData.createDefault()
            repository.setLanguagePreference(default)
            val syncableLanguage = SyncableLanguage.fromLanguageData(default)
            asinkaClient.syncManager.registerObject(syncableLanguage)
        }

        // Observe Asinka sync changes
        scope.launch {
            asinkaClient.syncManager.observeAllChanges().collect { change ->
                when (change) {
                    is SyncChange.Updated -> {
                        val syncableLanguage = change.obj as? SyncableLanguage
                        syncableLanguage?.let {
                            val languageData = it.getLanguageData()
                            repository.setLanguagePreference(languageData)
                            _languageChangeFlow.emit(languageData)
                            Log.d(tag, "Language updated from sync: ${languageData.languageCode}")
                        }
                    }
                    is SyncChange.Deleted -> {
                        // Language preference should not be deleted, but recreate default if it happens
                        val default = LanguageData.createDefault()
                        repository.setLanguagePreference(default)
                        val syncableLanguage = SyncableLanguage.fromLanguageData(default)
                        asinkaClient.syncManager.registerObject(syncableLanguage)
                        _languageChangeFlow.emit(default)
                        Log.d(tag, "Language preference deleted, recreated default")
                    }
                }
            }
        }

        // Observe local language changes and emit
        scope.launch {
            repository.getLanguagePreferenceFlow().collect { language ->
                language?.let {
                    _languageChangeFlow.emit(it)
                }
            }
        }

        Log.d(tag, "LanguageSyncManager started successfully")
    }

    /**
     * Stop the language sync manager
     */
    suspend fun stop() {
        isStarted = false
        asinkaClient.stop()
        Log.d(tag, "LanguageSyncManager stopped")
    }

    /**
     * Get the current language preference
     */
    suspend fun getLanguagePreference(): LanguageData? {
        return repository.getLanguagePreference()
    }

    /**
     * Get or create default language preference
     */
    suspend fun getOrCreateDefault(): LanguageData {
        return repository.getOrCreateDefault()
    }

    /**
     * Set the language preference and sync to all apps
     */
    suspend fun setLanguagePreference(languageCode: String, displayName: String) {
        val current = getOrCreateDefault()
        val updated = current.copy(
            languageCode = languageCode,
            displayName = displayName,
            isSystemDefault = languageCode == LanguageData.CODE_SYSTEM_DEFAULT,
            version = current.version + 1,
            lastModified = System.currentTimeMillis()
        )

        repository.setLanguagePreference(updated)

        // Update in Asinka sync
        val syncableLanguage = SyncableLanguage.fromLanguageData(updated)
        asinkaClient.syncManager.updateObject(
            updated.id,
            syncableLanguage.toFieldMap()
        )

        _languageChangeFlow.emit(updated)
        Log.d(tag, "Language preference set: $languageCode ($displayName)")
    }

    companion object {
        @Volatile
        private var INSTANCE: LanguageSyncManager? = null

        fun getInstance(
            context: Context,
            appId: String,
            appName: String,
            appVersion: String
        ): LanguageSyncManager {
            return INSTANCE ?: synchronized(this) {
                val languageSchema = ObjectSchema(
                    objectType = LanguageData.OBJECT_TYPE,
                    version = "1",
                    fields = listOf(
                        FieldSchema("id", FieldType.STRING),
                        FieldSchema("languageCode", FieldType.STRING),
                        FieldSchema("displayName", FieldType.STRING),
                        FieldSchema("isSystemDefault", FieldType.BOOLEAN),
                        FieldSchema("version", FieldType.INT),
                        FieldSchema("lastModified", FieldType.LONG)
                    )
                )

                // Calculate unique port per app to avoid EADDRINUSE conflicts
                val basePort = 8890
                val appSpecificOffset = appId.hashCode() % 100 // Keep offset reasonable
                val uniquePort = basePort + Math.abs(appSpecificOffset)

                val asinkaConfig = AsinkaConfig(
                    appId = appId,
                    appName = appName,
                    appVersion = appVersion,
                    serverPort = uniquePort,
                    serviceName = "language-sync",
                    exposedSchemas = listOf(languageSchema),
                    capabilities = mapOf("language_sync" to "1.0")
                )

                val asinkaClient = AsinkaClient.create(context, asinkaConfig)
                val database = LanguageDatabase.getInstance(context)
                val repository = LanguageRepository(database.languageDao())

                val instance = LanguageSyncManager(
                    context = context.applicationContext,
                    appIdentifier = appId,
                    asinkaClient = asinkaClient,
                    repository = repository
                )
                INSTANCE = instance
                instance
            }
        }

        fun resetInstance() {
            INSTANCE = null
        }
    }
}
