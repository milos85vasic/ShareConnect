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


package com.shareconnect.torrentsharingsync

import android.content.Context
import android.util.Log
import digital.vasic.asinka.AsinkaClient
import digital.vasic.asinka.AsinkaConfig
import digital.vasic.asinka.models.ObjectSchema
import digital.vasic.asinka.models.FieldSchema
import digital.vasic.asinka.models.FieldType
import digital.vasic.asinka.sync.SyncChange
import com.shareconnect.torrentsharingsync.database.TorrentSharingDatabase
import com.shareconnect.torrentsharingsync.models.TorrentSharingData
import com.shareconnect.torrentsharingsync.models.SyncableTorrentSharing
import com.shareconnect.torrentsharingsync.repository.TorrentSharingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.net.ServerSocket
import androidx.startup.Initializer

/**
 * Singleton manager for torrent sharing preferences synchronization across apps
 */
class TorrentSharingSyncManager internal constructor(
    private val context: Context,
    private val appIdentifier: String,
    private val appName: String,
    private val appVersion: String,
    private val asinkaClient: AsinkaClient,
    private val repository: TorrentSharingRepository
) : Initializer<TorrentSharingSyncManager> {

    private val tag = "TorrentSharingSyncManager"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _prefsChangeFlow = MutableSharedFlow<TorrentSharingData>()
    val prefsChangeFlow: Flow<TorrentSharingData> = _prefsChangeFlow.asSharedFlow()

    private var isStarted = false

    /**
     * Start the torrent sharing sync manager
     */
    suspend fun start() {
        if (isStarted) {
            Log.w(tag, "TorrentSharingSyncManager already started")
            return
        }
        isStarted = true

        Log.d(tag, "Starting TorrentSharingSyncManager")

        // Start Asinka client with retry logic for port conflicts
        try {
            asinkaClient.start()
        } catch (e: Exception) {
            if (e.message?.contains("bind failed: EADDRINUSE") == true) {
                Log.w(tag, "Port conflict detected, retrying with different port", e)
                // Force recreation of singleton with new port
                synchronized(TorrentSharingSyncManager::class.java) {
                    INSTANCE = null
                }
                // Recreate with new port
                val newInstance = getInstance(context, appIdentifier, appName, appVersion)
                newInstance.asinkaClient.start()
                // Update the instance reference
                synchronized(TorrentSharingSyncManager::class.java) {
                    INSTANCE = newInstance
                }
            } else {
                throw e
            }
        }

        // Register existing preferences with Asinka
        val existingPrefs = repository.getTorrentSharingPrefs()
        if (existingPrefs != null) {
            val syncablePrefs = SyncableTorrentSharing.fromTorrentSharingData(existingPrefs)
            asinkaClient.syncManager.registerObject(syncablePrefs)
        } else {
            // Create and register default
            val default = TorrentSharingData.createDefault()
            repository.setTorrentSharingPrefs(default)
            val syncablePrefs = SyncableTorrentSharing.fromTorrentSharingData(default)
            asinkaClient.syncManager.registerObject(syncablePrefs)
        }

        // Observe Asinka sync changes
        scope.launch {
            asinkaClient.syncManager.observeAllChanges().collect { change ->
                when (change) {
                    is SyncChange.Updated -> {
                        val syncablePrefs = change.obj as? SyncableTorrentSharing
                        syncablePrefs?.let {
                            val prefs = it.getTorrentSharingData()
                            repository.setTorrentSharingPrefs(prefs)
                            _prefsChangeFlow.emit(prefs)
                            Log.d(tag, "Torrent sharing prefs updated from sync: directSharing=${prefs.directSharingEnabled}")
                        }
                    }
                    is SyncChange.Deleted -> {
                        // Preferences should not be deleted, but recreate default if it happens
                        val default = TorrentSharingData.createDefault()
                        repository.setTorrentSharingPrefs(default)
                        val syncablePrefs = SyncableTorrentSharing.fromTorrentSharingData(default)
                        asinkaClient.syncManager.registerObject(syncablePrefs)
                        _prefsChangeFlow.emit(default)
                        Log.d(tag, "Torrent sharing prefs deleted, recreated default")
                    }
                }
            }
        }

        // Observe local preference changes and emit
        scope.launch {
            repository.getTorrentSharingPrefsFlow().collect { prefs ->
                prefs?.let {
                    _prefsChangeFlow.emit(it)
                }
            }
        }

        Log.d(tag, "TorrentSharingSyncManager started successfully")
    }

    /**
     * Stop the torrent sharing sync manager
     */
    suspend fun stop() {
        isStarted = false
        asinkaClient.stop()
        Log.d(tag, "TorrentSharingSyncManager stopped")
    }

    /**
     * Get the current torrent sharing preferences
     */
    suspend fun getTorrentSharingPrefs(): TorrentSharingData? {
        return repository.getTorrentSharingPrefs()
    }

    /**
     * Get or create default torrent sharing preferences
     */
    suspend fun getOrCreateDefault(): TorrentSharingData {
        return repository.getOrCreateDefault()
    }

    /**
     * Set direct sharing enabled/disabled
     */
    suspend fun setDirectSharingEnabled(enabled: Boolean) {
        val current = getOrCreateDefault()
        val updated = TorrentSharingData.createUpdated(
            current = current,
            directSharingEnabled = enabled
        )

        repository.setTorrentSharingPrefs(updated)

        // Update in Asinka sync
        val syncablePrefs = SyncableTorrentSharing.fromTorrentSharingData(updated)
        asinkaClient.syncManager.updateObject(
            updated.id,
            syncablePrefs.toFieldMap()
        )

        _prefsChangeFlow.emit(updated)
        Log.d(tag, "Direct sharing enabled set: $enabled")
    }

    /**
     * Set "don't ask again" for qBitConnect
     */
    suspend fun setDontAskQBitConnect(dontAsk: Boolean) {
        val current = getOrCreateDefault()
        val updated = TorrentSharingData.createUpdated(
            current = current,
            dontAskQBitConnect = dontAsk
        )

        repository.setTorrentSharingPrefs(updated)

        // Update in Asinka sync
        val syncablePrefs = SyncableTorrentSharing.fromTorrentSharingData(updated)
        asinkaClient.syncManager.updateObject(
            updated.id,
            syncablePrefs.toFieldMap()
        )

        _prefsChangeFlow.emit(updated)
        Log.d(tag, "Don't ask again for qBitConnect set: $dontAsk")
    }

    /**
     * Set "don't ask again" for TransmissionConnect
     */
    suspend fun setDontAskTransmissionConnect(dontAsk: Boolean) {
        val current = getOrCreateDefault()
        val updated = TorrentSharingData.createUpdated(
            current = current,
            dontAskTransmissionConnect = dontAsk
        )

        repository.setTorrentSharingPrefs(updated)

        // Update in Asinka sync
        val syncablePrefs = SyncableTorrentSharing.fromTorrentSharingData(updated)
        asinkaClient.syncManager.updateObject(
            updated.id,
            syncablePrefs.toFieldMap()
        )

        _prefsChangeFlow.emit(updated)
        Log.d(tag, "Don't ask again for TransmissionConnect set: $dontAsk")
    }

    /**
     * Reset all "don't ask again" preferences
     */
    suspend fun resetDontAskPreferences() {
        val current = getOrCreateDefault()
        val updated = TorrentSharingData.createUpdated(
            current = current,
            dontAskQBitConnect = false,
            dontAskTransmissionConnect = false
        )

        repository.setTorrentSharingPrefs(updated)

        // Update in Asinka sync
        val syncablePrefs = SyncableTorrentSharing.fromTorrentSharingData(updated)
        asinkaClient.syncManager.updateObject(
            updated.id,
            syncablePrefs.toFieldMap()
        )

        _prefsChangeFlow.emit(updated)
        Log.d(tag, "All don't ask again preferences reset")
    }

    override fun create(context: Context): TorrentSharingSyncManager {
        return getInstance(context, "torrent-sharing-sync", "TorrentSharingSync", "1.0")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

    companion object {
        @Volatile
        private var INSTANCE: TorrentSharingSyncManager? = null

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
        ): TorrentSharingSyncManager {
            return INSTANCE ?: synchronized(this) {
                val torrentSharingSchema = ObjectSchema(
                    objectType = TorrentSharingData.OBJECT_TYPE,
                    version = "1",
                    fields = listOf(
                        FieldSchema("id", FieldType.STRING),
                        FieldSchema("directSharingEnabled", FieldType.BOOLEAN),
                        FieldSchema("autoAcceptFromKnownDevices", FieldType.BOOLEAN),
                        FieldSchema("maxConcurrentShares", FieldType.INT),
                        FieldSchema("shareHistoryRetentionDays", FieldType.INT),
                        FieldSchema("requireConfirmationForLargeFiles", FieldType.BOOLEAN),
                        FieldSchema("largeFileThresholdMB", FieldType.INT),
                        FieldSchema("allowedFileTypes", FieldType.STRING),
                        FieldSchema("blockedFileTypes", FieldType.STRING),
                        FieldSchema("maxFileSizeMB", FieldType.INT),
                        FieldSchema("bandwidthLimitKbps", FieldType.INT),
                        FieldSchema("requirePasswordForShares", FieldType.BOOLEAN),
                        FieldSchema("defaultSharePassword", FieldType.STRING),
                        FieldSchema("autoDeleteAfterHours", FieldType.INT),
                        FieldSchema("notifyOnShareComplete", FieldType.BOOLEAN),
                        FieldSchema("notifyOnShareError", FieldType.BOOLEAN),
                        FieldSchema("dontAskQBitConnect", FieldType.BOOLEAN),
                        FieldSchema("dontAskTransmissionConnect", FieldType.BOOLEAN),
                        FieldSchema("version", FieldType.INT),
                        FieldSchema("lastModified", FieldType.LONG)
                    )
                )

                val basePort = 8960
                val preferredPort = basePort + Math.abs(appId.hashCode() % 100)
                val uniquePort = findAvailablePort(preferredPort)

                Log.d("TorrentSharingSyncManager", "App $appId using port $uniquePort (preferred: $preferredPort)")

                val asinkaConfig = AsinkaConfig(
                    appId = appId,
                    appName = appName,
                    appVersion = appVersion,
                    serverPort = uniquePort,
                    exposedSchemas = listOf(torrentSharingSchema),
                    capabilities = mapOf("torrent_sharing_sync" to "1.0")
                )

                val asinkaClient = AsinkaClient.create(context, asinkaConfig)
                val database = TorrentSharingDatabase.getInstance(context)
                val repository = TorrentSharingRepository(database.torrentSharingDao())

                TorrentSharingSyncManager(context.applicationContext, appId, appName, appVersion, asinkaClient, repository).also { INSTANCE = it }
            }
        }

        fun resetInstance() {
            INSTANCE = null
        }
    }
}
