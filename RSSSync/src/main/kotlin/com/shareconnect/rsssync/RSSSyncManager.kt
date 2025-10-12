package com.shareconnect.rsssync

import android.content.Context
import android.util.Log
import com.shareconnect.rsssync.database.RSSDatabase
import com.shareconnect.rsssync.models.*
import com.shareconnect.rsssync.repository.RSSRepository
import digital.vasic.asinka.*
import digital.vasic.asinka.models.*
import digital.vasic.asinka.sync.SyncChange
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.net.ServerSocket

class RSSSyncManager private constructor(
    private val context: Context,
    private val appIdentifier: String,
    private val asinkaClient: AsinkaClient,
    private val repository: RSSRepository,
    private val clientTypeFilter: String? = null
) {
    private val TAG = "RSSSyncManager"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _feedChangeFlow = MutableSharedFlow<RSSFeedData>(replay = 0, extraBufferCapacity = 10)
    val feedChangeFlow: SharedFlow<RSSFeedData> = _feedChangeFlow.asSharedFlow()

    private var isStarted = false

    suspend fun start() {
        if (isStarted) return
        Log.d(TAG, "Starting RSSSyncManager for $appIdentifier with filter: $clientTypeFilter")

        asinkaClient.start()
        syncLocalFeedsToAsinka()

        scope.launch {
            asinkaClient.syncManager.observeAllChanges().collect { change ->
                when (change) {
                    is SyncChange.Updated -> {
                        (change.obj as? SyncableRSSFeed)?.let { handleReceivedFeed(it) }
                    }
                    is SyncChange.Deleted -> handleDeletedFeed(change.objectId)
                }
            }
        }

        isStarted = true
        Log.d(TAG, "RSSSyncManager started")
    }

    suspend fun stop() {
        if (!isStarted) return
        asinkaClient.stop()
        isStarted = false
    }

    fun getAllFeeds(): Flow<List<RSSFeedData>> = repository.getAllFeeds()
    fun getEnabledFeeds(): Flow<List<RSSFeedData>> = repository.getEnabledFeeds()
    suspend fun getFeedById(feedId: String) = repository.getFeedById(feedId)

    suspend fun addFeed(feed: RSSFeedData) {
        repository.insertFeed(feed)
        asinkaClient.syncManager.registerObject(SyncableRSSFeed.fromFeedData(feed))
        _feedChangeFlow.emit(feed)
        Log.d(TAG, "RSS feed added: ${feed.name}")
    }

    suspend fun updateFeed(feed: RSSFeedData) {
        val updated = feed.copy(version = feed.version + 1, lastModified = System.currentTimeMillis())
        repository.updateFeed(updated)
        asinkaClient.syncManager.updateObject(updated.id, SyncableRSSFeed.fromFeedData(updated).toFieldMap())
        _feedChangeFlow.emit(updated)
    }

    suspend fun deleteFeed(feedId: String) {
        repository.deleteFeed(feedId)
        asinkaClient.syncManager.deleteObject(feedId)
    }

    private suspend fun syncLocalFeedsToAsinka() {
        try {
            repository.getAllFeedsSync().forEach {
                asinkaClient.syncManager.registerObject(SyncableRSSFeed.fromFeedData(it))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing RSS feeds", e)
        }
    }

    private suspend fun handleReceivedFeed(syncableFeed: SyncableRSSFeed) {
        try {
            val feed = syncableFeed.getFeedData()
            val existing = repository.getFeedById(feed.id)

            if (existing == null) {
                repository.insertFeed(feed)
                _feedChangeFlow.emit(feed)
            } else if (feed.version > existing.version) {
                repository.updateFeed(feed)
                _feedChangeFlow.emit(feed)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling RSS feed", e)
        }
    }

    private suspend fun handleDeletedFeed(feedId: String) {
        try {
            repository.deleteFeed(feedId)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting RSS feed", e)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: RSSSyncManager? = null

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

        fun getInstance(context: Context, appId: String, appName: String, appVersion: String, clientTypeFilter: String? = null): RSSSyncManager {
            return INSTANCE ?: synchronized(this) {
                val schema = ObjectSchema(
                    objectType = RSSFeedData.OBJECT_TYPE,
                    version = "1",
                    fields = listOf(
                        FieldSchema("id", FieldType.STRING),
                        FieldSchema("url", FieldType.STRING),
                        FieldSchema("name", FieldType.STRING),
                        FieldSchema("autoDownload", FieldType.BOOLEAN),
                        FieldSchema("filters", FieldType.STRING),
                        FieldSchema("excludeFilters", FieldType.STRING),
                        FieldSchema("updateInterval", FieldType.INT),
                        FieldSchema("lastUpdate", FieldType.LONG),
                        FieldSchema("isEnabled", FieldType.BOOLEAN),
                        FieldSchema("category", FieldType.STRING),
                        FieldSchema("torrentClientType", FieldType.STRING),
                        FieldSchema("downloadPath", FieldType.STRING),
                        FieldSchema("sourceApp", FieldType.STRING),
                        FieldSchema("version", FieldType.INT),
                        FieldSchema("lastModified", FieldType.LONG)
                    )
                )

                val basePort = 8890
                val preferredPort = basePort + Math.abs(appId.hashCode() % 100)
                val uniquePort = findAvailablePort(preferredPort)

                Log.d("RSSSyncManager", "App $appId using port $uniquePort (preferred: $preferredPort)")

                val config = AsinkaConfig(
                    appId = appId,
                    appName = appName,
                    appVersion = appVersion,
                    serverPort = uniquePort,
                    serviceName = "rss-sync",
                    exposedSchemas = listOf(schema),
                    capabilities = mapOf("rss_sync" to "1.0")
                )
                val client = AsinkaClient.create(context, config)
                val db = RSSDatabase.getInstance(context)
                val repo = RSSRepository(db.rssDao())

                RSSSyncManager(context.applicationContext, appId, client, repo, clientTypeFilter).also { INSTANCE = it }
            }
        }

        fun resetInstance() { INSTANCE = null }
    }
}
