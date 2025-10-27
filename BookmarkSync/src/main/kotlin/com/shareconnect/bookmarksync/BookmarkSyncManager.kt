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


package com.shareconnect.bookmarksync

import android.content.Context
import android.util.Log
import com.shareconnect.bookmarksync.database.BookmarkDatabase
import com.shareconnect.bookmarksync.models.*
import com.shareconnect.bookmarksync.repository.BookmarkRepository
import digital.vasic.asinka.*
import digital.vasic.asinka.models.*
import digital.vasic.asinka.sync.SyncChange
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.net.ServerSocket
import androidx.startup.Initializer

class BookmarkSyncManager private constructor(
    private val context: Context,
    private val appIdentifier: String,
    private val appName: String,
    private val appVersion: String,
    private val asinkaClient: AsinkaClient,
    private val repository: BookmarkRepository
) : Initializer<BookmarkSyncManager> {
    private val TAG = "BookmarkSyncManager"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _bookmarkChangeFlow = MutableSharedFlow<BookmarkData>(replay = 0, extraBufferCapacity = 10)
    val bookmarkChangeFlow: SharedFlow<BookmarkData> = _bookmarkChangeFlow.asSharedFlow()

    private var isStarted = false

    suspend fun start() {
        if (isStarted) return
        Log.d(TAG, "Starting BookmarkSyncManager for $appIdentifier")

        // Start Asinka client with retry logic for port conflicts
        try {
            asinkaClient.start()
        } catch (e: Exception) {
            if (e.message?.contains("bind failed: EADDRINUSE") == true) {
                Log.w(TAG, "Port conflict detected, retrying with different port", e)
                // Force recreation of singleton with new port
                synchronized(BookmarkSyncManager::class.java) {
                    INSTANCE = null
                }
                // Recreate with new port
                val newInstance = getInstance(context, appIdentifier, appName, appVersion)
                newInstance.asinkaClient.start()
                // Update the instance reference
                synchronized(BookmarkSyncManager::class.java) {
                    INSTANCE = newInstance
                }
            } else {
                throw e
            }
        }

        syncLocalBookmarksToAsinka()

        scope.launch {
            asinkaClient.syncManager.observeAllChanges().collect { change ->
                when (change) {
                    is SyncChange.Updated -> {
                        (change.obj as? SyncableBookmark)?.let { handleReceivedBookmark(it) }
                    }
                    is SyncChange.Deleted -> handleDeletedBookmark(change.objectId)
                }
            }
        }

        isStarted = true
        Log.d(TAG, "BookmarkSyncManager started")
    }

    suspend fun stop() {
        if (!isStarted) return
        asinkaClient.stop()
        isStarted = false
    }

    fun getAllBookmarks(): Flow<List<BookmarkData>> = repository.getAllBookmarks()
    fun getFavoriteBookmarks(): Flow<List<BookmarkData>> = repository.getFavoriteBookmarks()
    fun getBookmarksByType(type: String): Flow<List<BookmarkData>> = repository.getBookmarksByType(type)
    suspend fun getBookmarkById(bookmarkId: String) = repository.getBookmarkById(bookmarkId)
    suspend fun searchBookmarks(query: String) = repository.searchBookmarks(query)

    suspend fun addBookmark(bookmark: BookmarkData) {
        repository.insertBookmark(bookmark)
        asinkaClient.syncManager.registerObject(SyncableBookmark.fromBookmarkData(bookmark))
        _bookmarkChangeFlow.emit(bookmark)
        Log.d(TAG, "Bookmark added: ${bookmark.title ?: bookmark.url}")
    }

    suspend fun updateBookmark(bookmark: BookmarkData) {
        val updated = bookmark.copy(version = bookmark.version + 1, lastModified = System.currentTimeMillis())
        repository.updateBookmark(updated)
        asinkaClient.syncManager.updateObject(updated.id, SyncableBookmark.fromBookmarkData(updated).toFieldMap())
        _bookmarkChangeFlow.emit(updated)
    }

    suspend fun deleteBookmark(bookmarkId: String) {
        repository.deleteBookmark(bookmarkId)
        asinkaClient.syncManager.deleteObject(bookmarkId)
    }

    suspend fun toggleFavorite(bookmarkId: String) {
        repository.getBookmarkById(bookmarkId)?.let { bookmark ->
            val updated = bookmark.copy(
                isFavorite = !bookmark.isFavorite,
                version = bookmark.version + 1,
                lastModified = System.currentTimeMillis()
            )
            updateBookmark(updated)
        }
    }

    suspend fun recordAccess(bookmarkId: String) {
        repository.getBookmarkById(bookmarkId)?.let { bookmark ->
            updateBookmark(bookmark.incrementAccessCount())
        }
    }

    private suspend fun syncLocalBookmarksToAsinka() {
        try {
            repository.getAllBookmarksSync().forEach {
                asinkaClient.syncManager.registerObject(SyncableBookmark.fromBookmarkData(it))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing bookmarks", e)
        }
    }

    private suspend fun handleReceivedBookmark(syncableBookmark: SyncableBookmark) {
        try {
            val bookmark = syncableBookmark.getBookmarkData()
            val existing = repository.getBookmarkById(bookmark.id)

            if (existing == null) {
                repository.insertBookmark(bookmark)
                _bookmarkChangeFlow.emit(bookmark)
            } else if (bookmark.version > existing.version) {
                repository.updateBookmark(bookmark)
                _bookmarkChangeFlow.emit(bookmark)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling bookmark", e)
        }
    }

    private suspend fun handleDeletedBookmark(bookmarkId: String) {
        try {
            repository.deleteBookmark(bookmarkId)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting bookmark", e)
        }
    }

    override fun create(context: Context): BookmarkSyncManager {
        return getInstance(context, "bookmark-sync", "BookmarkSync", "1.0")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

    companion object {
        @Volatile
        private var INSTANCE: BookmarkSyncManager? = null

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

        fun getInstance(context: Context, appId: String, appName: String, appVersion: String): BookmarkSyncManager {
            return INSTANCE ?: synchronized(this) {
                val schema = ObjectSchema(
                    objectType = BookmarkData.OBJECT_TYPE,
                    version = "1",
                    fields = listOf(
                        FieldSchema("id", FieldType.STRING),
                        FieldSchema("url", FieldType.STRING),
                        FieldSchema("title", FieldType.STRING),
                        FieldSchema("description", FieldType.STRING),
                        FieldSchema("thumbnailUrl", FieldType.STRING),
                        FieldSchema("type", FieldType.STRING),
                        FieldSchema("category", FieldType.STRING),
                        FieldSchema("tags", FieldType.STRING),
                        FieldSchema("isFavorite", FieldType.BOOLEAN),
                        FieldSchema("notes", FieldType.STRING),
                        FieldSchema("serviceProvider", FieldType.STRING),
                        FieldSchema("torrentHash", FieldType.STRING),
                        FieldSchema("magnetUri", FieldType.STRING),
                        FieldSchema("createdAt", FieldType.LONG),
                        FieldSchema("lastAccessedAt", FieldType.LONG),
                        FieldSchema("accessCount", FieldType.INT),
                        FieldSchema("sourceApp", FieldType.STRING),
                        FieldSchema("version", FieldType.INT),
                        FieldSchema("lastModified", FieldType.LONG)
                    )
                )

                 val basePort = 8930
                val preferredPort = basePort + Math.abs(appId.hashCode() % 100)
                val uniquePort = findAvailablePort(preferredPort)

                Log.d("BookmarkSyncManager", "App $appId using port $uniquePort (preferred: $preferredPort)")

                val config = AsinkaConfig(
                    appId = appId,
                    appName = appName,
                    appVersion = appVersion,
                    serverPort = uniquePort,
                    serviceName = "bookmark-sync",
                    exposedSchemas = listOf(schema),
                    capabilities = mapOf("bookmark_sync" to "1.0")
                )
                val client = AsinkaClient.create(context, config)
                val db = BookmarkDatabase.getInstance(context)
                val repo = BookmarkRepository(db.bookmarkDao())

                BookmarkSyncManager(context.applicationContext, appId, appName, appVersion, client, repo).also { INSTANCE = it }
            }
        }

        fun resetInstance() { INSTANCE = null }
    }
}
