package com.shareconnect.plexconnect.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.firebase.firestore.FirebaseFirestore
import com.shareconnect.plexconnect.data.local.PlexDatabase
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.ml.PlexModelTrainer
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * Cross-platform Recommendation Synchronization Manager
 */
class RecommendationSyncManager(
    private val context: Context,
    private val database: PlexDatabase,
    private val modelTrainer: PlexModelTrainer
) {
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Synchronize user recommendations across platforms
     */
    suspend fun synchronizeRecommendations(
        userId: String,
        syncConfig: RecommendationSyncConfig = RecommendationSyncConfig()
    ): Result<SyncReport> {
        return try {
            // Fetch local watch history
            val localWatchHistory = fetchLocalWatchHistory(userId)

            // Upload watch history to cloud
            uploadWatchHistory(userId, localWatchHistory)

            // Fetch cross-platform recommendations
            val crossPlatformRecommendations = fetchCrossPlatformRecommendations(userId)

            // Merge and update local recommendations
            val mergedRecommendations = mergeCrossPlatformRecommendations(
                localWatchHistory, 
                crossPlatformRecommendations
            )

            // Update local database
            updateLocalRecommendations(userId, mergedRecommendations)

            // Train personalized ML model
            val modelTrainingResult = modelTrainer.trainFederatedModel(userId)

            // Generate sync report
            val syncReport = generateSyncReport(
                localWatchHistory, 
                crossPlatformRecommendations, 
                mergedRecommendations,
                modelTrainingResult
            )

            Result.success(syncReport)
        } catch (e: Exception) {
            Log.e("RecommendationSyncManager", "Sync failed", e)
            Result.failure(e)
        }
    }

    /**
     * Fetch local watch history
     */
    private suspend fun fetchLocalWatchHistory(userId: String): List<PlexMediaItem> {
        return database.plexMediaItemDao()
            .getWatchedMediaItemsForUser(userId)
            .map { it.toPlexMediaItem() }
    }

    /**
     * Upload watch history to cloud
     */
    private suspend fun uploadWatchHistory(
        userId: String, 
        watchHistory: List<PlexMediaItem>
    ) {
        val watchHistoryData = watchHistory.map { item ->
            mapOf(
                "id" to item.ratingKey,
                "title" to item.title,
                "type" to item.type,
                "year" to item.year,
                "timestamp" to Instant.now().toEpochMilli()
            )
        }

        firestore.collection("user_watch_history")
            .document(userId)
            .set(mapOf("history" to watchHistoryData))
            .await()
    }

    /**
     * Fetch cross-platform recommendations
     */
    private suspend fun fetchCrossPlatformRecommendations(
        userId: String
    ): List<PlexMediaItem> {
        val snapshot = firestore.collection("recommendations")
            .document(userId)
            .get()
            .await()

        return snapshot.get("items") as? List<PlexMediaItem> ?: emptyList()
    }

    /**
     * Merge cross-platform recommendations
     */
    private fun mergeCrossPlatformRecommendations(
        localWatchHistory: List<PlexMediaItem>,
        crossPlatformRecommendations: List<PlexMediaItem>
    ): List<PlexMediaItem> {
        // Intelligent merging strategy
        val combinedItems = (localWatchHistory + crossPlatformRecommendations)
            .distinctBy { it.ratingKey }
            .sortedByDescending { calculateRelevanceScore(it) }
            .take(50) // Limit to 50 recommendations

        return combinedItems
    }

    /**
     * Update local recommendations database
     */
    private suspend fun updateLocalRecommendations(
        userId: String,
        recommendations: List<PlexMediaItem>
    ) {
        // Convert and insert recommendations
        val recommendationEntities = recommendations.map { item ->
            PlexMediaItemEntity(
                id = item.ratingKey ?: "unknown",
                libraryId = "recommendations",
                title = item.title ?: "Unknown Title",
                type = item.type ?: "unknown",
                year = item.year,
                summary = item.summary
            )
        }

        database.plexMediaItemDao().insertMediaItems(recommendationEntities)
    }

    /**
     * Calculate relevance score for recommendations
     */
    private fun calculateRelevanceScore(item: PlexMediaItem): Double {
        var score = 0.0

        // Year proximity bonus
        item.year?.let { year ->
            val currentYear = Instant.now().toString().substring(0, 4).toInt()
            score += when {
                year == currentYear -> 10.0
                year > currentYear - 5 -> 5.0
                year > currentYear - 10 -> 2.0
                else -> 0.0
            }
        }

        // Type bonus
        score += when(item.type?.uppercase()) {
            "MOVIE" -> 3.0
            "TV_SHOW" -> 2.0
            else -> 1.0
        }

        return score
    }

    /**
     * Generate comprehensive sync report
     */
    private fun generateSyncReport(
        localHistory: List<PlexMediaItem>,
        crossPlatformRecommendations: List<PlexMediaItem>,
        mergedRecommendations: List<PlexMediaItem>,
        modelTrainingResult: Result<PlexModelTrainer.ModelTrainingReport>
    ): SyncReport {
        return SyncReport(
            timestamp = Instant.now(),
            localHistoryCount = localHistory.size,
            crossPlatformRecommendationsCount = crossPlatformRecommendations.size,
            mergedRecommendationsCount = mergedRecommendations.size,
            modelTrainingSuccessful = modelTrainingResult.isSuccess,
            modelTrainingReport = modelTrainingResult.getOrNull()
        )
    }

    /**
     * Recommendation synchronization configuration
     */
    data class RecommendationSyncConfig(
        val syncInterval: Long = 24, // hours
        val maxRecommendations: Int = 50,
        val enableCrossPlatformSync: Boolean = true
    )

    /**
     * Comprehensive sync report
     */
    data class SyncReport(
        val timestamp: Instant,
        val localHistoryCount: Int,
        val crossPlatformRecommendationsCount: Int,
        val mergedRecommendationsCount: Int,
        val modelTrainingSuccessful: Boolean,
        val modelTrainingReport: PlexModelTrainer.ModelTrainingReport?
    )

    companion object {
        /**
         * Schedule periodic recommendation synchronization
         */
        fun scheduleRecommendationSync(context: Context) {
            val syncRequest = PeriodicWorkRequestBuilder<RecommendationSyncWorker>(
                repeatInterval = 24, 
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "recommendation_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }

        /**
         * Background worker for recommendation sync
         */
        class RecommendationSyncWorker(
            context: Context, 
            params: WorkerParameters
        ) : CoroutineWorker(context, params) {
            override suspend fun doWork(): Result {
                // Implement recommendation sync logic
                return Result.success()
            }
        }
    }
}