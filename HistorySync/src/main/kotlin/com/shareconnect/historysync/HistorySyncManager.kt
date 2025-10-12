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
import kotlinx.coroutines.launch

class HistorySyncManager private constructor(
    private val context: Context,
    private val appIdentifier: String,
    private val asinkaClient: AsinkaClient,
    private val repository: HistoryRepository
) {
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

        asinkaClient.start()

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

    companion object {
        @Volatile
        private var INSTANCE: HistorySyncManager? = null

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

                val basePort = 8890
                val uniquePort = basePort + Math.abs(appId.hashCode() % 100)

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

                val instance = HistorySyncManager(
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
