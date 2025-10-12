package com.shareconnect.themesync

import android.content.Context
import digital.vasic.asinka.AsinkaClient
import digital.vasic.asinka.AsinkaConfig
import digital.vasic.asinka.models.ObjectSchema
import digital.vasic.asinka.models.FieldSchema
import digital.vasic.asinka.models.FieldType
import digital.vasic.asinka.sync.SyncChange
import com.shareconnect.themesync.models.SyncableTheme
import com.shareconnect.themesync.models.ThemeData
import com.shareconnect.themesync.repository.ThemeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.net.ServerSocket

class ThemeSyncManager private constructor(
    private val context: Context,
    private val appIdentifier: String,
    private val asinkaClient: AsinkaClient,
    private val repository: ThemeRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _themeChangeFlow = MutableSharedFlow<ThemeData>()
    val themeChangeFlow: Flow<ThemeData> = _themeChangeFlow.asSharedFlow()

    private var isStarted = false

    suspend fun start() {
        if (isStarted) return
        isStarted = true

        // Initialize default themes if needed
        repository.initializeDefaultThemes()

        // Start Asinka client
        asinkaClient.start()

        // Register existing themes with Asinka
        val existingThemes = repository.getAllThemesSync()
        existingThemes.forEach { theme ->
            val syncableTheme = SyncableTheme.fromThemeData(theme)
            asinkaClient.syncManager.registerObject(syncableTheme)
        }

        // Observe Asinka sync changes
        scope.launch {
            asinkaClient.syncManager.observeAllChanges().collect { change ->
                when (change) {
                    is SyncChange.Updated -> {
                        val syncableTheme = change.obj as? SyncableTheme
                        syncableTheme?.let {
                            val themeData = it.getThemeData()
                            // Don't sync default flag changes from other apps
                            if (themeData.sourceApp != appIdentifier && themeData.isDefault) {
                                val updatedTheme = themeData.copy(isDefault = false)
                                repository.insertTheme(updatedTheme)
                            } else {
                                repository.insertTheme(themeData)
                            }
                            _themeChangeFlow.emit(themeData)
                        }
                    }
                    is SyncChange.Deleted -> {
                        repository.deleteTheme(change.objectId)
                    }
                }
            }
        }
    }

    suspend fun stop() {
        isStarted = false
        asinkaClient.stop()
    }

    suspend fun addTheme(theme: ThemeData) {
        repository.insertTheme(theme)
        val syncableTheme = SyncableTheme.fromThemeData(theme)
        asinkaClient.syncManager.registerObject(syncableTheme)
    }

    suspend fun updateTheme(theme: ThemeData) {
        val updatedTheme = theme.copy(
            version = theme.version + 1,
            lastModified = System.currentTimeMillis()
        )
        repository.updateTheme(updatedTheme)

        val syncableTheme = SyncableTheme.fromThemeData(updatedTheme)
        asinkaClient.syncManager.updateObject(
            updatedTheme.id,
            syncableTheme.toFieldMap()
        )
    }

    suspend fun setDefaultTheme(themeId: String) {
        val theme = repository.getThemeById(themeId) ?: return

        // Clear all defaults locally
        repository.setDefaultTheme(themeId)

        // Update in sync manager
        val updatedTheme = theme.copy(
            isDefault = true,
            version = theme.version + 1,
            lastModified = System.currentTimeMillis()
        )

        val syncableTheme = SyncableTheme.fromThemeData(updatedTheme)
        asinkaClient.syncManager.updateObject(
            updatedTheme.id,
            syncableTheme.toFieldMap()
        )

        _themeChangeFlow.emit(updatedTheme)
    }

    suspend fun deleteTheme(themeId: String) {
        repository.deleteTheme(themeId)
        asinkaClient.syncManager.deleteObject(themeId)
    }

    fun getAllThemes(): Flow<List<ThemeData>> = repository.getAllThemes()

    suspend fun getDefaultTheme(): ThemeData? = repository.getDefaultTheme()

    fun observeDefaultTheme(): Flow<ThemeData?> = repository.observeDefaultTheme()

    suspend fun connect(host: String, port: Int): Result<AsinkaClient.SessionInfo> {
        return asinkaClient.connect(host, port)
    }

    suspend fun disconnect(sessionId: String) {
        asinkaClient.disconnect(sessionId)
    }

    fun getSessions(): List<AsinkaClient.SessionInfo> = asinkaClient.getSessions()

    companion object {
        @Volatile
        private var INSTANCE: ThemeSyncManager? = null

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

        fun getInstance(
            context: Context,
            appId: String,
            appName: String,
            appVersion: String
        ): ThemeSyncManager {
            return INSTANCE ?: synchronized(this) {
                val themeSchema = ObjectSchema(
                    objectType = ThemeData.OBJECT_TYPE,
                    version = "1",
                    fields = listOf(
                        FieldSchema("id", FieldType.STRING),
                        FieldSchema("name", FieldType.STRING),
                        FieldSchema("colorScheme", FieldType.STRING),
                        FieldSchema("isDarkMode", FieldType.BOOLEAN),
                        FieldSchema("isDefault", FieldType.BOOLEAN),
                        FieldSchema("sourceApp", FieldType.STRING),
                        FieldSchema("version", FieldType.INT),
                        FieldSchema("lastModified", FieldType.LONG)
                    )
                )

                val basePort = 8890
                val preferredPort = basePort + Math.abs(appId.hashCode() % 100)
                val uniquePort = findAvailablePort(preferredPort)

                Log.d("ThemeSyncManager", "App $appId using port $uniquePort (preferred: $preferredPort)")

                val asinkaConfig = AsinkaConfig(
                    appId = appId,
                    appName = appName,
                    appVersion = appVersion,
                    serverPort = uniquePort,
                    serviceName = "theme-sync",
                    exposedSchemas = listOf(themeSchema),
                    capabilities = mapOf("theme_sync" to "1.0")
                )

                val asinkaClient = AsinkaClient.create(context, asinkaConfig)
                val repository = ThemeRepository(context, appId)

                ThemeSyncManager(context.applicationContext, appId, asinkaClient, repository).also { INSTANCE = it }
            }
        }

        fun resetInstance() {
            INSTANCE = null
        }
    }
}
