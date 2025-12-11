package com.shareconnect.plexconnect.ml

import android.util.Log
import com.shareconnect.plexconnect.data.database.PlexDatabase
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.Instant
import kotlin.math.ln

/**
 * Advanced Recommendation Algorithm with Multi-Dimensional Scoring (Original Implementation)
 */
class AdvancedRecommendationAlgorithmOriginal(
    private val database: PlexDatabase
) {
    /**
     * Generate sophisticated recommendations using multiple scoring strategies
     */
    fun generateRecommendations(
        userId: String,
        config: RecommendationConfig = RecommendationConfig()
    ): Flow<List<RecommendedItem>> = flow {
        try {
            // Fetch user's watch history - using watched items from DAO
            val watchHistory = database.plexMediaItemDao()
                .getWatchedItems()
                .first()

            // Fetch all potential recommendation items
            val allItems = database.plexMediaItemDao()
                .getAllMediaItems()
                .first()
                .filter { item -> 
                    // Filter out already watched items
                    item.ratingKey !in watchHistory.mapNotNull { it.ratingKey }
                }

            // Calculate recommendations using multiple strategies
            val recommendations = allItems
                .map { item -> 
                    val score = calculateMultiDimensionalScore(item, watchHistory, config)
                    RecommendedItem(item, score)
                }
                .sortedByDescending { it.score }
                .take(config.maxRecommendations)

            emit(recommendations)
        } catch (e: Exception) {
            Log.e("AdvancedRecommendationAlgorithm", "Failed to generate recommendations", e)
            emit(emptyList())
        }
    }

    /**
     * Multi-dimensional scoring algorithm
     */
    private suspend fun calculateMultiDimensionalScore(
        item: PlexMediaItem,
        watchHistory: List<PlexMediaItem>,
        config: RecommendationConfig
    ): Double {
        var totalScore = 0.0

        // 1. Genre/Type Similarity
        totalScore += calculateTypeSimilarity(item, watchHistory) * config.weights.typeSimilarity

        // 2. Year Proximity
        totalScore += calculateYearProximityScore(item, watchHistory) * config.weights.yearProximity

        // 3. Metadata Similarity
        totalScore += calculateMetadataSimilarity(item, watchHistory) * config.weights.metadataSimilarity

        // 4. Novelty Score (penalize overly similar recommendations)
        totalScore *= (1.0 - calculateNoveltyPenalty(item, watchHistory))

        // 5. Popularity Boost
        totalScore += calculatePopularityBoost(item) * config.weights.popularityBoost

        // 6. Temporal Decay (recent items get a boost)
        totalScore *= calculateTemporalDecay(item)

        return totalScore
    }

    /**
     * Calculate type similarity between item and watch history
     */
    private fun calculateTypeSimilarity(
        item: PlexMediaItem, 
        watchHistory: List<PlexMediaItem>
    ): Double {
        val typeFrequency = watchHistory
            .groupingBy { it.type }
            .eachCount()
            .maxByOrNull { it.value }

        return if (typeFrequency?.key == item.type) 1.0 else 0.5
    }

    /**
     * Calculate year proximity score
     */
    private fun calculateYearProximityScore(
        item: PlexMediaItem, 
        watchHistory: List<PlexMediaItem>
    ): Double {
        val avgWatchHistoryYear = watchHistory
            .mapNotNull { it.year }
            .average()
            .takeIf { it.isFinite() } ?: return 0.0

        return 1.0 / (1.0 + kotlin.math.abs(item.year?.toDouble()?.minus(avgWatchHistoryYear) ?: 0.0))
    }

    /**
     * Calculate metadata similarity - stubbed implementation
     */
    private suspend fun calculateMetadataSimilarity(
        item: PlexMediaItem, 
        watchHistory: List<PlexMediaItem>
    ): Double {
        // Stub implementation - return simple similarity based on type matching
        return if (watchHistory.any { it.type == item.type }) 0.8 else 0.3
    }









    /**
     * Calculate novelty penalty to avoid overly similar recommendations
     */
    private fun calculateNoveltyPenalty(
        item: PlexMediaItem, 
        watchHistory: List<PlexMediaItem>
    ): Double {
        // Simple title similarity check for stub
        val titleSimilarity = watchHistory
            .mapNotNull { history -> 
                if (item.title != null && history.title != null) {
                    calculateSimpleTextSimilarity(item.title, history.title)
                } else 0.0
            }
        
        return if (titleSimilarity.isNotEmpty()) {
            titleSimilarity.average()
        } else 0.0
    }
    
    /**
     * Simple text similarity calculation
     */
    private fun calculateSimpleTextSimilarity(text1: String, text2: String): Double {
        // Simple word overlap calculation
        val words1 = text1.lowercase().split("\\s+")
        val words2 = text2.lowercase().split("\\s+")
        val intersection = words1.intersect(words2.toSet())
        return intersection.size.toDouble() / maxOf(words1.size, words2.size)
    }

    /**
     * Calculate popularity boost
     */
    private fun calculatePopularityBoost(item: PlexMediaItem): Double {
        // Could be enhanced with actual view count from database
        return ln(1 + (item.year?.toDouble() ?: 0.0))
    }

    /**
     * Temporal decay to favor recent items
     */
    private fun calculateTemporalDecay(item: PlexMediaItem): Double {
        val currentYear = java.time.Year.now().value
        val itemYear = item.year ?: currentYear
        
        // Exponential decay, favoring more recent items
        return kotlin.math.exp(-0.1 * (currentYear - itemYear))
    }

    /**
     * Recommendation configuration with customizable weights
     */
    data class RecommendationConfig(
        val maxRecommendations: Int = 50,
        val weights: ScoringWeights = ScoringWeights()
    )

    /**
     * Configurable scoring weights
     */
    data class ScoringWeights(
        val typeSimilarity: Double = 0.3,
        val yearProximity: Double = 0.2,
        val metadataSimilarity: Double = 0.2,
        val popularityBoost: Double = 0.1
    )

    /**
     * Recommended item with its calculated score
     */
    data class RecommendedItem(
        val mediaItem: PlexMediaItem,
        val score: Double
    )

    companion object {
        /**
         * Periodic recommendation algorithm refinement
         */
        fun refineRecommendationAlgorithm() {
            // Placeholder for potential periodic algorithm updates
            Log.d("RecommendationAlgorithm", "Algorithm refinement initiated")
        }
    }
}