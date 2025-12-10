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
    private suspend fun calculateMetadataSimilarity(
        item: PlexMediaItem, 
        watchHistory: List<PlexMediaItem>
    ): Double {
        // Use NLP metadata analyzer for advanced similarity calculation
        val metadataAnalyzer = PlexMetadataAnalyzer(context)
        
        // Analyze current item
        val currentItemAnalysis = metadataAnalyzer.analyzeMetadata(item)
        
        // Compare with watch history
        val similarityScores = watchHistory
            .map { historyItem ->
                val historyAnalysis = metadataAnalyzer.analyzeMetadata(historyItem)
                
                // Multi-dimensional similarity calculation
                calculateMultiDimensionalSimilarity(
                    currentItemAnalysis, 
                    historyAnalysis
                )
            }
        
        return similarityScores.maxOrNull() ?: 0.0
    }

    /**
     * Advanced multi-dimensional similarity calculation
     */
    private fun calculateMultiDimensionalSimilarity(
        item1Analysis: MediaMetadataAnalyzer.MetadataAnalysisResult,
        item2Analysis: MediaMetadataAnalyzer.MetadataAnalysisResult
    ): Double {
        var similarityScore = 0.0

        // Genre similarity
        val genreSimilarity = calculateGenreSimilarity(
            item1Analysis.genres, 
            item2Analysis.genres
        )
        similarityScore += genreSimilarity * 0.3

        // Semantic embedding similarity
        val embeddingSimilarity = calculateEmbeddingSimilarity(
            item1Analysis.semanticEmbedding, 
            item2Analysis.semanticEmbedding
        )
        similarityScore += embeddingSimilarity * 0.4

        // Sentiment similarity
        val sentimentSimilarity = 1.0 - kotlin.math.abs(
            item1Analysis.sentimentScore - item2Analysis.sentimentScore
        )
        similarityScore += sentimentSimilarity * 0.2

        // Title and summary structural similarity
        val titleSimilarity = calculateTitleSimilarity(
            item1Analysis.title, 
            item2Analysis.title
        )
        similarityScore += titleSimilarity * 0.1

        return similarityScore
    }

    /**
     * Calculate genre similarity
     */
    private fun calculateGenreSimilarity(
        genres1: List<String>, 
        genres2: List<String>
    ): Double {
        val intersection = genres1.intersect(genres2.toSet())
        return intersection.size.toDouble() / maxOf(genres1.size, genres2.size)
    }

    /**
     * Calculate semantic embedding similarity using cosine similarity
     */
    private fun calculateEmbeddingSimilarity(
        embedding1: FloatArray, 
        embedding2: FloatArray
    ): Double {
        // Ensure embeddings are of equal length
        val minLength = minOf(embedding1.size, embedding2.size)
        val truncatedEmbedding1 = embedding1.take(minLength)
        val truncatedEmbedding2 = embedding2.take(minLength)

        // Calculate dot product
        val dotProduct = truncatedEmbedding1
            .zip(truncatedEmbedding2)
            .map { it.first * it.second }
            .sum()

        // Calculate magnitudes
        val magnitude1 = kotlin.math.sqrt(
            truncatedEmbedding1.map { it * it }.sum().toDouble()
        )
        val magnitude2 = kotlin.math.sqrt(
            truncatedEmbedding2.map { it * it }.sum().toDouble()
        )

        // Avoid division by zero
        return if (magnitude1 > 0 && magnitude2 > 0) {
            dotProduct / (magnitude1 * magnitude2)
        } else {
            0.0
        }
    }

    /**
     * Calculate title similarity based on structural analysis
     */
    private fun calculateTitleSimilarity(
        title1: MediaMetadataAnalyzer.TitleAnalysis, 
        title2: MediaMetadataAnalyzer.TitleAnalysis
    ): Double {
        var similarityScore = 0.0

        // Word count similarity
        similarityScore += 1.0 - kotlin.math.abs(
            title1.wordCount - title2.wordCount
        ).toDouble() / maxOf(title1.wordCount, title2.wordCount)

        // Special character presence
        similarityScore += if (
            title1.containsSpecialChars == title2.containsSpecialChars
        ) 1.0 else 0.0

        return similarityScore / 2.0
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