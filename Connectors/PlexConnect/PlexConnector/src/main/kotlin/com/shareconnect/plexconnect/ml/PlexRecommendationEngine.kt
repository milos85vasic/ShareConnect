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

package com.shareconnect.plexconnect.ml

import android.content.Context
import com.shareconnect.plexconnect.data.local.PlexDatabase
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Stub implementation of PlexRecommendationEngine for compilation
 * This provides minimal functionality while full ML implementation is disabled
 */
class PlexRecommendationEngine(
    private val context: Context,
    private val database: PlexDatabase
) {
    
    /**
     * Generate personalized media recommendations
     */
    fun generateRecommendations(
        userId: String,
        recommendationOptions: RecommendationOptions = RecommendationOptions()
    ): Flow<List<PlexMediaItem>> = flow {
        // Stub implementation - return empty list
        emit(emptyList())
    }
    
    /**
     * Get recommendation statistics
     */
    fun getRecommendationStats(): RecommendationStats {
        return RecommendationStats(
            totalRecommendations = 0,
            accuracyScore = 0.0f,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Clear recommendation cache
     */
    suspend fun clearCache() {
        // Stub implementation - no-op
    }
}

/**
 * Options for recommendation generation
 */
data class RecommendationOptions(
    val maxResults: Int = 10,
    val includeWatched: Boolean = false,
    val genres: List<String> = emptyList(),
    val minRating: Float = 0.0f
)

/**
 * Recommendation statistics
 */
data class RecommendationStats(
    val totalRecommendations: Int,
    val accuracyScore: Float,
    val lastUpdated: Long
)