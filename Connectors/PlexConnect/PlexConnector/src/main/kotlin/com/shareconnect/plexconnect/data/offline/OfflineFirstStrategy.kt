package com.shareconnect.plexconnect.data.offline

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.*
import com.shareconnect.plexconnect.data.api.PlexApiClient
import com.shareconnect.plexconnect.data.api.PlexMediaItem as ApiPlexMediaItem
import com.shareconnect.plexconnect.data.database.PlexDatabase
import com.shareconnect.plexconnect.data.model.MediaType
import com.shareconnect.plexconnect.data.model.PlexMediaItem as ModelPlexMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * Comprehensive offline-first strategy for Plex media synchronization
 */
class OfflineFirstStrategy(
    private val context: Context,
    private val plexApiClient: PlexApiClient,
    private val plexDatabase: PlexDatabase
) {
    /**
     * Check current network connectivity
     */
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Fetch media items with offline-first approach
     * @param serverUrl Plex server URL
     * @param sectionKey Library section key
     * @param token Authentication token
     * @param forceRefresh Force network refresh
     * @param maxCacheAge Maximum allowed cache age
     */
    fun getMediaItems(
        serverUrl: String,
        sectionKey: String,
        token: String,
        serverId: Long,
        forceRefresh: Boolean = false,
        maxCacheAge: Duration = Duration.ofHours(24)
    ): Flow<List<ModelPlexMediaItem>> = flow {
        val cachedItems = try {
            runBlocking {
                plexDatabase.plexMediaItemDao().getMediaItemsForLibrary(sectionKey.toLongOrNull() ?: 0L).firstOrNull() ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList<ModelPlexMediaItem>()
        }
        
        val lastSyncTime = cachedItems.firstOrNull()?.let { item ->
            // Get the updated time from the model item
            if (item is ModelPlexMediaItem) {
                item.updatedAt
            } else {
                0L
            }
        } ?: 0L

        // Determine if cache is valid
        val isCacheValid = !forceRefresh && 
            lastSyncTime > 0 && (System.currentTimeMillis() - lastSyncTime) < maxCacheAge.toMillis()

        // If cache is valid, emit cached items
        if (isCacheValid && cachedItems.isNotEmpty()) {
            emit(cachedItems)
            return@flow
        }

        // If network is available, fetch from API
        if (isNetworkAvailable()) {
            try {
                val apiResult = plexApiClient.getLibraryItems(serverUrl, sectionKey, token)
                apiResult.onSuccess { apiItems ->
                    val modelItems = apiItems.map { apiItem ->
                        ModelPlexMediaItem(
                            ratingKey = apiItem.ratingKey ?: "",
                            key = apiItem.key ?: "",
                            title = apiItem.title ?: "",
                            type = MediaType.fromString(apiItem.type?.value ?: "movie"),
                            summary = apiItem.summary,
                            year = apiItem.year,
                            duration = apiItem.duration,
                            serverId = serverId
                        )
                    }
                    
                    // Cache new items
                    runBlocking {
                        plexDatabase.plexMediaItemDao().insertMediaItems(modelItems)
                    }
                    
                    emit(modelItems)
                }.onFailure { error ->
                    if (cachedItems.isNotEmpty()) {
                        emit(cachedItems)
                    } else {
                        throw error
                    }
                }
            } catch (e: Exception) {
                // Network error, emit cached items if available
                if (cachedItems.isNotEmpty()) {
                    emit(cachedItems)
                } else {
                    emit(emptyList<ModelPlexMediaItem>())
                }
            }
        } else {
            // No network, emit empty list
            emit(emptyList<ModelPlexMediaItem>())
        }
    }

    /**
     * Background sync worker for periodic media synchronization
     */
    class MediaSyncWorker(
        context: Context, 
        params: WorkerParameters
    ) : CoroutineWorker(context, params) {
        
        override suspend fun doWork(): Result {
            // Implement background sync logic
            // This would typically involve:
            // 1. Checking network availability
            // 2. Syncing media items for each library
            // 3. Updating local database
            // 4. Handling potential conflicts
            
            return Result.success()
        }

        companion object {
            fun schedulePeriodicSync() {
                val syncRequest = PeriodicWorkRequestBuilder<MediaSyncWorker>(
                    repeatInterval = 4, 
                    repeatIntervalTimeUnit = TimeUnit.HOURS
                )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

                WorkManager.getInstance().enqueueUniquePeriodicWork(
                    "plex_media_sync",
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncRequest
                )
            }
        }
    }
}