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


package com.shareconnect.historysync

import android.content.Context
import android.util.Log
import com.shareconnect.historysync.database.HistoryDatabase
import com.shareconnect.historysync.models.HistoryData
import com.shareconnect.historysync.models.SyncableHistory
import com.shareconnect.historysync.repository.HistoryRepository
import digital.vasic.asinka.AsinkaClient
import digital.vasic.asinka.AsinkaConfig
import digital.vasic.asinka.models.FieldSchema
import digital.vasic.asinka.models.FieldType
import digital.vasic.asinka.models.ObjectSchema
import digital.vasic.asinka.sync.SyncChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.net.ServerSocket
import kotlinx.coroutines.launch
import androidx.startup.Initializer

class HistorySyncManager private constructor(
    private val context: Context,
    private val appIdentifier: String,
    private val appName: String,
    private val appVersion: String,
    private val asinkaClient: AsinkaClient,
    private val repository: HistoryRepository
) : Initializer<HistorySyncManager> {
    private val TAG = "HistorySyncManager"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _historyChangeFlow = MutableSharedFlow<HistoryData>(replay = 0, extraBufferCapacity = 10)
    val historyChangeFlow: SharedFlow<HistoryData> = _historyChangeFlow.asSharedFlow()

    private var isStarted = false

    suspend fun start() {
        if (isStarted) {
            Log.w(TAG, "HistorySyncManager already started")
            return
        }

        Log.d(TAG, "Starting HistorySyncManager for $appIdentifier")

        // Start Asinka client with retry logic for port conflicts
        try {
            asinkaClient.start()
        } catch (e: Exception) {
            if (e.message?.contains("bind failed: EADDRINUSE") == true) {
                Log.w(TAG, "Port conflict detected, retrying with different port", e)
                // Force recreation of singleton with new port
                synchronized(HistorySyncManager::class.java) {
                    INSTANCE = null
                }
                // Recreate with new port
                val newInstance = getInstance(context, appIdentifier, appName, appVersion)
                newInstance.asinkaClient.start()
                // Update the instance reference
                synchronized(HistorySyncManager::class.java) {
                    INSTANCE = newInstance
                }
            } else {
                throw e
            }
        }

        // Register existing history
        syncLocalHistoryToAsinka()

        // Observe Asinka sync changes
        scope.launch {
            asinkaClient.syncManager.observeAllChanges().collect { change ->
                when (change) {
                    is SyncChange.Updated -> {
                        val syncableHistory = change.obj as? SyncableHistory
                        syncableHistory?.let {
                            handleReceivedHistory(it)
                        }
                    }
                    is SyncChange.Deleted -> {
                        handleDeletedHistory(change.objectId)
                    }
                }
            }
        }

        isStarted = true
        Log.d(TAG, "HistorySyncManager started successfully")
    }

    suspend fun stop() {
        if (!isStarted) return
        asinkaClient.stop()
        isStarted = false
        Log.d(TAG, "HistorySyncManager stopped")
    }

    fun getAllHistory(): Flow<List<HistoryData>> {
        return repository.getAllHistory()
    }

    suspend fun getHistoryByServiceType(serviceType: String): List<HistoryData> {
        return repository.getHistoryByServiceType(serviceType)
    }

    suspend fun searchHistory(query: String): List<HistoryData> {
        return repository.searchHistory(query)
    }

    suspend fun getHistoryByCategory(category: String): List<HistoryData> {
        return repository.getHistoryByCategory(category)
    }

    suspend fun getSuccessfulHistory(): List<HistoryData> {
        return repository.getSuccessfulHistory()
    }

    suspend fun getFailedHistory(): List<HistoryData> {
        return repository.getFailedHistory()
    }

    suspend fun addHistory(history: HistoryData) {
        repository.insertHistory(history)

        val syncableHistory = SyncableHistory.fromHistoryData(history)
        asinkaClient.syncManager.registerObject(syncableHistory)

        _historyChangeFlow.emit(history)

        Log.d(TAG, "History added: ${history.title ?: history.url}")
    }

    suspend fun updateHistory(history: HistoryData) {
        val updatedHistory = history.copy(
            version = history.version + 1,
            lastModified = System.currentTimeMillis()
        )
        repository.updateHistory(updatedHistory)

        val syncableHistory = SyncableHistory.fromHistoryData(updatedHistory)
        asinkaClient.syncManager.updateObject(
            updatedHistory.id,
            syncableHistory.toFieldMap()
        )

        _historyChangeFlow.emit(updatedHistory)

        Log.d(TAG, "History updated: ${updatedHistory.title ?: updatedHistory.url}")
    }

    suspend fun deleteHistory(historyId: String) {
        repository.deleteHistory(historyId)
        asinkaClient.syncManager.deleteObject(historyId)
        Log.d(TAG, "History deleted: $historyId")
    }

    suspend fun deleteAllHistory() {
        repository.deleteAllHistory()
        Log.d(TAG, "All history deleted")
    }

    suspend fun deleteHistoryOlderThan(days: Int) {
        val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        repository.deleteHistoryOlderThan(cutoffTime)
        Log.d(TAG, "Deleted history older than $days days")
    }

    suspend fun getHistoryCount(): Int {
        return repository.getHistoryCount()
    }

    suspend fun getAllHistoryItems(): List<HistoryData> {
        return repository.getAllHistorySync()
    }

    suspend fun deleteHistoryItem(item: HistoryData) {
        deleteHistory(item.id)
    }

    suspend fun deleteHistoryItemsByServiceProvider(serviceProvider: String) {
        repository.deleteHistoryByServiceProvider(serviceProvider)
    }

    suspend fun deleteHistoryItemsByType(type: String) {
        repository.deleteHistoryByType(type)
    }

    suspend fun deleteHistoryItemsByServiceType(serviceType: String) {
        repository.deleteHistoryByServiceType(serviceType)
    }

    suspend fun filterForTransmissionConnect(items: List<HistoryData>): List<HistoryData> {
        return repository.filterForTransmissionConnect(items)
    }

    private suspend fun syncLocalHistoryToAsinka() {
        try {
            val historyList = repository.getAllHistorySync()
            Log.d(TAG, "Syncing ${historyList.size} local history items to Asinka")

            for (history in historyList) {
                val syncableHistory = SyncableHistory.fromHistoryData(history)
                asinkaClient.syncManager.registerObject(syncableHistory)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing local history to Asinka", e)
        }
    }

    private suspend fun handleReceivedHistory(syncableHistory: SyncableHistory) {
        try {
            val history = syncableHistory.getHistoryData()
            Log.d(TAG, "Received history from ${history.sourceApp}: ${history.title ?: history.url}")

            val existingHistory = repository.getHistoryById(history.id)

            if (existingHistory == null) {
                repository.insertHistory(history)
                _historyChangeFlow.emit(history)
                Log.d(TAG, "Saved new history: ${history.title ?: history.url}")
            } else {
                if (history.version > existingHistory.version || history.lastModified > existingHistory.lastModified) {
                    repository.updateHistory(history)
                    _historyChangeFlow.emit(history)
                    Log.d(TAG, "Updated history: ${history.title ?: history.url}")
                } else {
                    Log.d(TAG, "Ignoring older version of history")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling received history", e)
        }
    }

    private suspend fun handleDeletedHistory(historyId: String) {
        try {
            Log.d(TAG, "Received delete notification for history: $historyId")
            repository.deleteHistory(historyId)
        } catch (e: Exception) {
            Log.e(TAG, "Error handling deleted history", e)
        }
    }

    override fun create(context: Context): HistorySyncManager {
        return getInstance(context, "history-sync", "HistorySync", "1.0")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

    companion object {
        @Volatile
        private var INSTANCE: HistorySyncManager? = null

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
        ): HistorySyncManager {
            return INSTANCE ?: synchronized(this) {
                val historySchema = ObjectSchema(
                    objectType = HistoryData.OBJECT_TYPE,
                    version = "1",
                    fields = listOf(
                        FieldSchema("id", FieldType.STRING),
                        FieldSchema("url", FieldType.STRING),
                        FieldSchema("title", FieldType.STRING),
                        FieldSchema("description", FieldType.STRING),
                        FieldSchema("thumbnailUrl", FieldType.STRING),
                        FieldSchema("serviceProvider", FieldType.STRING),
                        FieldSchema("type", FieldType.STRING),
                        FieldSchema("timestamp", FieldType.LONG),
                        FieldSchema("profileId", FieldType.STRING),
                        FieldSchema("profileName", FieldType.STRING),
                        FieldSchema("isSentSuccessfully", FieldType.BOOLEAN),
                        FieldSchema("serviceType", FieldType.STRING),
                        FieldSchema("torrentClientType", FieldType.STRING),
                        FieldSchema("sourceApp", FieldType.STRING),
                        FieldSchema("version", FieldType.INT),
                        FieldSchema("lastModified", FieldType.LONG),
                        FieldSchema("fileSize", FieldType.LONG),
                        FieldSchema("duration", FieldType.INT),
                        FieldSchema("quality", FieldType.STRING),
                        FieldSchema("downloadPath", FieldType.STRING),
                        FieldSchema("torrentHash", FieldType.STRING),
                        FieldSchema("magnetUri", FieldType.STRING),
                        FieldSchema("category", FieldType.STRING),
                        FieldSchema("tags", FieldType.STRING)
                    )
                )

                 val basePort = 8910
                val preferredPort = basePort + Math.abs(appId.hashCode() % 100)
                val uniquePort = findAvailablePort(preferredPort)

                Log.d("HistorySyncManager", "App $appId using port $uniquePort (preferred: $preferredPort)")

                val asinkaConfig = AsinkaConfig(
                    appId = appId,
                    appName = appName,
                    appVersion = appVersion,
                    serverPort = uniquePort,
                    serviceName = "history-sync",
                    exposedSchemas = listOf(historySchema),
                    capabilities = mapOf("history_sync" to "1.0")
                )

                val asinkaClient = AsinkaClient.create(context, asinkaConfig)
                val database = HistoryDatabase.getInstance(context)
                val repository = HistoryRepository(database.historyDao())

                HistorySyncManager(context.applicationContext, appId, appName, appVersion, asinkaClient, repository).also { INSTANCE = it }
            }
        }

        fun resetInstance() {
            INSTANCE = null
        }
    }
}
