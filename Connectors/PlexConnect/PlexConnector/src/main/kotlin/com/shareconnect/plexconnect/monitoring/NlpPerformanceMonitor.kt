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


package com.shareconnect.plexconnect.monitoring

import android.content.Context
import android.util.Log
import com.shareconnect.plexconnect.config.NlpConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Performance monitoring for NLP operations
 * Tracks embedding generation, similarity computation, and recommendation performance
 */
class NlpPerformanceMonitor(
    private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Performance metrics
    private val _metrics = MutableStateFlow(NlpPerformanceMetrics())
    val metrics: StateFlow<NlpPerformanceMetrics> = _metrics.asStateFlow()
    
    // Timing statistics
    private val embeddingTimings = ConcurrentHashMap<String, Long>()
    private val similarityTimings = ConcurrentHashMap<String, Long>()
    private val recommendationTimings = ConcurrentHashMap<String, Long>()
    
    // Counters
    private val embeddingCount = AtomicLong(0)
    private val similarityCount = AtomicLong(0)
    private val recommendationCount = AtomicLong(0)
    private val errorCount = AtomicLong(0)
    
    // Last operation times
    private val lastEmbeddingTime = AtomicReference<Long>(0L)
    private val lastSimilarityTime = AtomicReference<Long>(0L)
    private val lastRecommendationTime = AtomicReference<Long>(0L)
    
    // Average calculations
    private val totalEmbeddingTime = AtomicLong(0)
    private val totalSimilarityTime = AtomicLong(0)
    private val totalRecommendationTime = AtomicLong(0)
    
    init {
        // Start periodic metrics reporting
        if (NlpConfig.ENABLE_PERFORMANCE_LOGGING) {
            startPeriodicReporting()
        }
    }
    
    /**
     * Record embedding generation performance
     */
    fun recordEmbeddingPerformance(
        mediaKey: String,
        durationMs: Long,
        language: String,
        embeddingSource: String,
        success: Boolean
    ) {
        val count = embeddingCount.incrementAndGet()
        totalEmbeddingTime.addAndGet(durationMs)
        lastEmbeddingTime.set(System.currentTimeMillis())
        
        if (!success) {
            errorCount.incrementAndGet()
        }
        
        embeddingTimings[mediaKey] = durationMs
        
        // Log at intervals
        if (count % NlpConfig.EMBEDDING_GENERATION_LOG_INTERVAL == 0L) {
            Log.i(TAG, "Embedding stats: $count embeddings, avg: ${totalEmbeddingTime.get() / count}ms")
        }
        
        updateMetrics()
    }
    
    /**
     * Record similarity computation performance
     */
    fun recordSimilarityPerformance(
        key: String,
        durationMs: Long,
        candidateCount: Int,
        similarityScore: Double
    ) {
        val count = similarityCount.incrementAndGet()
        totalSimilarityTime.addAndGet(durationMs)
        lastSimilarityTime.set(System.currentTimeMillis())
        
        similarityTimings[key] = durationMs
        
        // Log at intervals
        if (count % NlpConfig.SIMILARITY_COMPUTE_LOG_INTERVAL == 0L) {
            Log.i(TAG, "Similarity stats: $count comparisons, avg: ${totalSimilarityTime.get() / count}ms")
        }
        
        updateMetrics()
    }
    
    /**
     * Record recommendation generation performance
     */
    fun recordRecommendationPerformance(
        type: String,
        durationMs: Long,
        resultCount: Int,
        success: Boolean
    ) {
        val count = recommendationCount.incrementAndGet()
        totalRecommendationTime.addAndGet(durationMs)
        lastRecommendationTime.set(System.currentTimeMillis())
        
        if (!success) {
            errorCount.incrementAndGet()
        }
        
        recommendationTimings[type] = durationMs
        
        Log.d(TAG, "Recommendation ($type): ${resultCount} results in ${durationMs}ms")
        
        updateMetrics()
    }
    
    /**
     * Record a cache hit/miss
     */
    fun recordCacheHit(cacheType: String, hit: Boolean) {
        val currentMetrics = _metrics.value
        val newCacheStats = currentMetrics.cacheStats.toMutableMap()
        val stats = newCacheStats.getOrPut(cacheType) { CacheStats() }
        
        if (hit) {
            stats.hits++
        } else {
            stats.misses++
        }
        
        newCacheStats[cacheType] = stats
        
        _metrics.value = currentMetrics.copy(
            cacheStats = newCacheStats
        )
    }
    
    /**
     * Record memory usage
     */
    fun recordMemoryUsage(operation: String, usedMemoryMB: Long) {
        val currentMetrics = _metrics.value
        val newMemoryStats = currentMetrics.memoryStats.toMutableMap()
        
        newMemoryStats[operation] = MemoryUsage(
            operation = operation,
            usedMemoryMB = usedMemoryMB,
            timestamp = System.currentTimeMillis()
        )
        
        // Keep only last 10 memory measurements per operation
        if (newMemoryStats.size > 10) {
            val oldestKey = newMemoryStats.keys.first()
            newMemoryStats.remove(oldestKey)
        }
        
        _metrics.value = currentMetrics.copy(
            memoryStats = newMemoryStats
        )
    }
    
    /**
     * Get performance summary
     */
    fun getPerformanceSummary(): String {
        val metrics = _metrics.value
        val avgEmbeddingTime = if (embeddingCount.get() > 0) totalEmbeddingTime.get() / embeddingCount.get() else 0
        val avgSimilarityTime = if (similarityCount.get() > 0) totalSimilarityTime.get() / similarityCount.get() else 0
        val avgRecommendationTime = if (recommendationCount.get() > 0) totalRecommendationTime.get() / recommendationCount.get() else 0
        
        return buildString {
            appendLine("=== NLP Performance Summary ===")
            appendLine("Embeddings: ${embeddingCount.get()} total, ${avgEmbeddingTime}ms avg")
            appendLine("Similarities: ${similarityCount.get()} total, ${avgSimilarityTime}ms avg")
            appendLine("Recommendations: ${recommendationCount.get()} total, ${avgRecommendationTime}ms avg")
            appendLine("Errors: ${errorCount.get()}")
            appendLine("Cache hit rates:")
            metrics.cacheStats.forEach { (type, stats) ->
                val hitRate = if (stats.hits + stats.misses > 0) {
                    (stats.hits.toFloat() / (stats.hits + stats.misses) * 100)
                } else 0f
                appendLine("  $type: ${String.format("%.1f", hitRate)}%")
            }
        }
    }
    
    /**
     * Reset all metrics
     */
    fun resetMetrics() {
        embeddingCount.set(0)
        similarityCount.set(0)
        recommendationCount.set(0)
        errorCount.set(0)
        
        totalEmbeddingTime.set(0)
        totalSimilarityTime.set(0)
        totalRecommendationTime.set(0)
        
        embeddingTimings.clear()
        similarityTimings.clear()
        recommendationTimings.clear()
        
        _metrics.value = NlpPerformanceMetrics()
        
        Log.i(TAG, "Performance metrics reset")
    }
    
    /**
     * Get metrics for dashboard display
     */
    fun getDashboardMetrics(): DashboardMetrics {
        return DashboardMetrics(
            totalEmbeddings = embeddingCount.get(),
            totalSimilarities = similarityCount.get(),
            totalRecommendations = recommendationCount.get(),
            totalErrors = errorCount.get(),
            avgEmbeddingTime = if (embeddingCount.get() > 0) totalEmbeddingTime.get() / embeddingCount.get() else 0,
            avgSimilarityTime = if (similarityCount.get() > 0) totalSimilarityTime.get() / similarityCount.get() else 0,
            avgRecommendationTime = if (recommendationCount.get() > 0) totalRecommendationTime.get() / recommendationCount.get() else 0,
            lastEmbeddingTime = lastEmbeddingTime.get(),
            lastSimilarityTime = lastSimilarityTime.get(),
            lastRecommendationTime = lastRecommendationTime.get()
        )
    }
    
    private fun updateMetrics() {
        val current = _metrics.value
        _metrics.value = current.copy(
            totalEmbeddings = embeddingCount.get(),
            totalSimilarities = similarityCount.get(),
            totalRecommendations = recommendationCount.get(),
            totalErrors = errorCount.get(),
            averageEmbeddingTimeMs = if (embeddingCount.get() > 0) totalEmbeddingTime.get() / embeddingCount.get() else 0,
            averageSimilarityTimeMs = if (similarityCount.get() > 0) totalSimilarityTime.get() / similarityCount.get() else 0,
            averageRecommendationTimeMs = if (recommendationCount.get() > 0) totalRecommendationTime.get() / recommendationCount.get() else 0,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    private fun startPeriodicReporting() {
        scope.launch {
            // Log performance summary every 5 minutes
            kotlinx.coroutines.delay(5 * 60 * 1000L)
            Log.i(TAG, getPerformanceSummary())
        }
    }
    
    companion object {
        private const val TAG = "NlpPerformanceMonitor"
    }
    
    data class NlpPerformanceMetrics(
        val totalEmbeddings: Long = 0,
        val totalSimilarities: Long = 0,
        val totalRecommendations: Long = 0,
        val totalErrors: Long = 0,
        val averageEmbeddingTimeMs: Long = 0,
        val averageSimilarityTimeMs: Long = 0,
        val averageRecommendationTimeMs: Long = 0,
        val cacheStats: Map<String, CacheStats> = emptyMap(),
        val memoryStats: Map<String, MemoryUsage> = emptyMap(),
        val lastUpdated: Long = System.currentTimeMillis()
    )
    
    data class CacheStats(
        var hits: Long = 0,
        var misses: Long = 0
    ) {
        val hitRate: Float
            get() = if (hits + misses > 0) hits.toFloat() / (hits + misses) else 0f
    }
    
    data class MemoryUsage(
        val operation: String,
        val usedMemoryMB: Long,
        val timestamp: Long
    )
    
    data class DashboardMetrics(
        val totalEmbeddings: Long,
        val totalSimilarities: Long,
        val totalRecommendations: Long,
        val totalErrors: Long,
        val avgEmbeddingTime: Long,
        val avgSimilarityTime: Long,
        val avgRecommendationTime: Long,
        val lastEmbeddingTime: Long,
        val lastSimilarityTime: Long,
        val lastRecommendationTime: Long
    )
}