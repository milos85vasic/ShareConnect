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


package com.shareconnect.plexconnect.service

import android.content.Context
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding
import com.shareconnect.plexconnect.nlp.MediaMetadataAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Service for AI-powered media analysis and recommendations
 * Integrates NLP capabilities with PlexConnect system
 */
class PlexAiRecommendationService(
    private val context: Context
) {
    // Lazy initialization of NLP components
    private val semanticEmbedding by lazy { AdvancedSemanticEmbedding(context) }
    private val metadataAnalyzer by lazy { MediaMetadataAnalyzer(context) }

    /**
     * Get enhanced media items with semantic analysis
     */
    suspend fun getEnhancedMediaItems(mediaItems: Flow<List<PlexMediaItem>>): Flow<List<EnhancedMediaItem>> = 
        mediaItems.map { items ->
            withContext(Dispatchers.Default) {
                items.map { item ->
                    enhanceMediaItem(item)
                }
            }
        }

    /**
     * Enhance a single media item with AI analysis
     */
    suspend fun enhanceMediaItem(mediaItem: PlexMediaItem): EnhancedMediaItem = withContext(Dispatchers.Default) {
        try {
            // Perform metadata analysis
            val metadataAnalysis = metadataAnalyzer.analyzeMetadata(mediaItem)
            
            // Generate semantic embedding
            val embeddingResult = semanticEmbedding.generateEmbedding(
                text = "${mediaItem.title} ${mediaItem.summary}",
                additionalContext = mapOf(
                    "media_type" to (mediaItem.type ?: "UNKNOWN"),
                    "genre" to metadataAnalysis.genres.firstOrNull()
                )
            )

            EnhancedMediaItem(
                originalItem = mediaItem,
                metadataAnalysis = metadataAnalysis,
                semanticEmbedding = embeddingResult.embedding,
                semanticLanguage = embeddingResult.language,
                embeddingSource = embeddingResult.source
            )
        } catch (e: Exception) {
            // Return enhanced item with error state
            EnhancedMediaItem(
                originalItem = mediaItem,
                metadataAnalysis = null,
                semanticEmbedding = FloatArray(768) { 0f },
                semanticLanguage = "en",
                embeddingSource = AdvancedSemanticEmbedding.EmbeddingSource.ERROR,
                analysisError = e.message
            )
        }
    }

    /**
     * Find similar media items using semantic similarity
     */
    suspend fun findSimilarMedia(
        targetItem: PlexMediaItem,
        candidateItems: List<PlexMediaItem>,
        threshold: Double = 0.7
    ): List<SimilarMediaResult> = withContext(Dispatchers.Default) {
        try {
            // Generate embedding for target item
            val targetEmbeddingResult = semanticEmbedding.generateEmbedding(
                "${targetItem.title} ${targetItem.summary}"
            )
            val targetEmbedding = targetEmbeddingResult.embedding

            // Compare with candidate items
            candidateItems.map { candidate ->
                val candidateEmbeddingResult = semanticEmbedding.generateEmbedding(
                    "${candidate.title} ${candidate.summary}"
                )
                val candidateEmbedding = candidateEmbeddingResult.embedding

                val similarity = semanticEmbedding.calculateSemanticSimilarity(
                    targetEmbedding, 
                    candidateEmbedding
                )

                SimilarMediaResult(
                    mediaItem = candidate,
                    similarity = similarity,
                    isAboveThreshold = similarity >= threshold
                )
            }
            .filter { it.isAboveThreshold }
            .sortedByDescending { it.similarity }

        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Get cross-lingual recommendations
     */
    suspend fun getCrossLingualRecommendations(
        mediaItems: List<PlexMediaItem>,
        targetLanguage: String = "en"
    ): List<EnhancedMediaItem> = withContext(Dispatchers.Default) {
        mediaItems.mapNotNull { item ->
            try {
                val title = item.title ?: return@mapNotNull null
                val summary = item.summary ?: return@mapNotNull null
                
                // Generate cross-lingual embedding
                val crossLingualResult = semanticEmbedding.generateCrossLingualEmbedding(
                    text = "$title $summary",
                    targetLanguage = targetLanguage
                )

                EnhancedMediaItem(
                    originalItem = item,
                    metadataAnalysis = null,
                    semanticEmbedding = crossLingualResult.embedding,
                    semanticLanguage = crossLingualResult.language,
                    embeddingSource = AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMED
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Enhanced media item with AI analysis
     */
    data class EnhancedMediaItem(
        val originalItem: PlexMediaItem,
        val metadataAnalysis: MediaMetadataAnalyzer.MetadataAnalysisResult?,
        val semanticEmbedding: FloatArray,
        val semanticLanguage: String,
        val embeddingSource: AdvancedSemanticEmbedding.EmbeddingSource,
        val analysisError: String? = null
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as EnhancedMediaItem

            if (originalItem != other.originalItem) return false
            if (metadataAnalysis != other.metadataAnalysis) return false
            if (!semanticEmbedding.contentEquals(other.semanticEmbedding)) return false
            if (semanticLanguage != other.semanticLanguage) return false
            if (embeddingSource != other.embeddingSource) return false
            if (analysisError != other.analysisError) return false

            return true
        }

        override fun hashCode(): Int {
            var result = originalItem.hashCode()
            result = 31 * result + (metadataAnalysis?.hashCode() ?: 0)
            result = 31 * result + semanticEmbedding.contentHashCode()
            result = 31 * result + semanticLanguage.hashCode()
            result = 31 * result + embeddingSource.hashCode()
            result = 31 * result + (analysisError?.hashCode() ?: 0)
            return result
        }
    }

    /**
     * Result of media similarity comparison
     */
    data class SimilarMediaResult(
        val mediaItem: PlexMediaItem,
        val similarity: Double,
        val isAboveThreshold: Boolean
    )

    companion object {
        // Default similarity threshold for recommendations
        const val DEFAULT_SIMILARITY_THRESHOLD = 0.7
        
        // Maximum number of recommendations to return
        const val MAX_RECOMMENDATIONS = 20
    }
}