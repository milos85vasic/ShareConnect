package com.shareconnect.plexconnect.data.offline

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.*
import com.shareconnect.plexconnect.data.api.PlexApiClient
import com.shareconnect.plexconnect.data.local.PlexDatabase
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.Instant

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
        forceRefresh: Boolean = false,
        maxCacheAge: Duration = Duration.ofHours(24)
    ): Flow<List<PlexMediaItem>> = flow {
        val cachedItems = plexDatabase.plexMediaItemDao().getMediaItemsForLibrary(sectionKey)
        val lastSyncTime = cachedItems.firstOrNull()?.lastSyncTime ?: Instant.MIN

        // Determine if cache is valid
        val isCacheValid = !forceRefresh && 
            Instant.now().isBefore(lastSyncTime.plus(maxCacheAge))

        // If cache is valid, emit cached items
        if (isCacheValid) {
            emit(cachedItems.map { it.toPlexMediaItem() })
            return@flow
        }

        // If network is available, fetch from API
        if (isNetworkAvailable()) {
            try {
                val apiResult = plexApiClient.getLibraryItems(serverUrl, sectionKey, token)
                apiResult.onSuccess { items ->
                    // Update local database
                    val mediaItemEntities = items.map { it.toPlexMediaItemEntity(sectionKey) }
                    plexDatabase.plexMediaItemDao().insertMediaItems(mediaItemEntities)
                    
                    // Emit items
                    emit(items)
                }.onFailure { 
                    // Fallback to cached items if network request fails
                    emit(cachedItems.map { it.toPlexMediaItem() })
                }
            } catch (e: Exception) {
                // Network error, emit cached items
                emit(cachedItems.map { it.toPlexMediaItem() })
            }
        } else {
            // No network, emit cached items
            emit(cachedItems.map { it.toPlexMediaItem() })
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

    /**
     * Convert PlexMediaItem to PlexMediaItemEntity for local storage
     */
    private fun PlexMediaItem.toPlexMediaItemEntity(libraryId: String) = 
        PlexMediaItemEntity(
            id = this.ratingKey ?: this.guid ?: "unknown",
            libraryId = libraryId,
            title = this.title ?: "Unknown Title",
            type = this.type ?: "unknown",
            year = this.year,
            summary = this.summary,
            lastSyncTime = Instant.now()
        )

    /**
     * Convert PlexMediaItemEntity to PlexMediaItem for API compatibility
     */
    private fun PlexMediaItemEntity.toPlexMediaItem() = 
        PlexMediaItem(
            ratingKey = this.id,
            key = this.id,
            guid = this.id,
            title = this.title,
            type = this.type,
            year = this.year,
            summary = this.summary
        )
}