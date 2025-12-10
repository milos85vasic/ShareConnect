package com.shareconnect.plexconnect.ml

import android.util.Log
import com.shareconnect.plexconnect.data.local.PlexDatabase
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import kotlin.math.ln

/**
 * Advanced Recommendation Algorithm with Multi-Dimensional Scoring
 */
class AdvancedRecommendationAlgorithm(
    private val database: PlexDatabase
) {
    /**
     * Generate sophisticated recommendations using multiple scoring strategies
     */
    fun generateRecommendations(
        userId: String,
        config: RecommendationConfig = RecommendationConfig()
    ): Flow<List<RecommendedItem>> = flow {
        // Fetch user's watch history
        val watchHistory = database.plexMediaItemDao()
            .getWatchedMediaItemsForUser(userId)
            .map { it.toPlexMediaItem() }

        // Fetch all potential recommendation items
        val allItems = database.plexMediaItemDao().getAllMediaItems()
            .map { it.toPlexMediaItem() }
            .filter { item -> 
                // Filter out already watched items
                item.ratingKey !in watchHistory.map { it.ratingKey }
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
    }

    /**
     * Multi-dimensional scoring algorithm
     */
    private fun calculateMultiDimensionalScore(
        item: PlexMediaItem,
        watchHistory: List<PlexMediaItem>,
        config: RecommendationConfig
    ): Double {
        var totalScore = 0.0

        // 1. Genre/Type Similarity
        totalScore += calculateTypeSimiliarity(item, watchHistory) * config.weights.typeSimilarity

        // 2. Year Proximity
        totalScore += calculateYearProximityScore(item, watchHistory) * config.weights.yearProximity

        // 3. Metadata Similarity
        totalScore += calculateMetadataSimilarity(item, watchHistory) * config.weights.metadataSimilarity

        // 4. Novelty Score (penalize overly similar recommendations)
        totalScore *= (1 - calculateNoveltyPenalty(item, watchHistory))

        // 5. Popularity Boost
        totalScore += calculatePopularityBoost(item) * config.weights.popularityBoost

        // 6. Temporal Decay (recent items get a boost)
        totalScore *= calculateTemporalDecay(item)

        return totalScore
    }

    /**
     * Calculate type similarity between item and watch history
     */
    private fun calculateTypeSimiliarity(
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
     * Calculate metadata similarity
     */
    private fun calculateMetadataSimilarity(
        item: PlexMediaItem, 
        watchHistory: List<PlexMediaItem>
    ): Double {
        // Placeholder for more advanced metadata comparison
        // Could involve NLP techniques, genre matching, etc.
        return watchHistory
            .filter { it.summary != null && item.summary != null }
            .map { calculateTextSimilarity(it.summary!!, item.summary!!) }
            .maxOrNull() ?: 0.0
    }

    /**
     * Simple text similarity calculation
     */
    private fun calculateTextSimilarity(text1: String, text2: String): Double {
        val words1 = text1.lowercase().split("\\s+".toRegex()).toSet()
        val words2 = text2.lowercase().split("\\s+".toRegex()).toSet()
        
        val intersection = words1.intersect(words2)
        val union = words1.union(words2)
        
        return intersection.size.toDouble() / union.size
    }

    /**
     * Calculate novelty penalty to avoid overly similar recommendations
     */
    private fun calculateNoveltyPenalty(
        item: PlexMediaItem, 
        watchHistory: List<PlexMediaItem>
    ): Double {
        val similarityScores = watchHistory
            .map { calculateTextSimilarity(it.title ?: "", item.title ?: "") }
        
        return similarityScores.average().takeIf { it.isFinite() } ?: 0.0
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
        val currentYear = Instant.now().toString().substring(0, 4).toInt()
        val itemYear = item.year ?: currentYear
        
        // Exponential decay, favoring more recent items
        return kotlin.math.pow(0.9, (currentYear - itemYear).toDouble())
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