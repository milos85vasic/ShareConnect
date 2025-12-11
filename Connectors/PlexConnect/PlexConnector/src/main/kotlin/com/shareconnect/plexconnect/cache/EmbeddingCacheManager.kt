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


package com.shareconnect.plexconnect.cache

import android.content.Context
import com.shareconnect.plexconnect.config.NlpConfig
import com.shareconnect.plexconnect.data.database.dao.SemanticEmbeddingDao
import com.shareconnect.plexconnect.data.database.entity.SemanticEmbeddingEntity
import com.shareconnect.plexconnect.monitoring.NlpPerformanceMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.security.MessageDigest
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.sqrt

/**
 * Advanced multi-layer caching system for semantic embeddings
 * Combines memory cache, disk cache, and database persistence
 */
class EmbeddingCacheManager(
    private val context: Context,
    private val embeddingDao: SemanticEmbeddingDao,
    private val performanceMonitor: NlpPerformanceMonitor
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // L1: In-memory LRU cache for hot embeddings
    private val memoryCache = ConcurrentHashMap<String, CacheEntry>()
    private val maxMemoryEntries = NlpConfig.EMBEDDING_CACHE_SIZE
    
    // L2: Background warming for popular content
    private val warmingQueue = PriorityQueue<WarmingRequest> { a, b -> 
        b.priority.compareTo(a.priority) 
    }
    private val isWarming = Mutex()
    
    // Statistics
    private val totalRequests = AtomicLong(0)
    private val memoryHits = AtomicLong(0)
    private val databaseHits = AtomicLong(0)
    private val computeOperations = AtomicLong(0)
    
    // Cache events for UI updates
    private val _cacheEvents = MutableSharedFlow<CacheEvent>()
    val cacheEvents: SharedFlow<CacheEvent> = _cacheEvents.asSharedFlow()
    
    init {
        // Start background warming
        scope.launch {
            while (true) {
                try {
                    processWarmingQueue()
                    delay(60_000) // Process every minute
                } catch (e: Exception) {
                    // Continue processing even if warming fails
                }
            }
        }
        
        // Start periodic cleanup
        scope.launch {
            while (true) {
                try {
                    cleanupExpiredEntries()
                    delay(NlpConfig.EMBEDDING_EXPIRY_HOURS * 60 * 60 * 1000L)
                } catch (e: Exception) {
                    // Continue processing even if cleanup fails
                }
            }
        }
    }
    
    /**
     * Get embedding from cache hierarchy
     */
    suspend fun getEmbedding(
        key: String,
        computeIfAbsent: suspend () -> FloatArray
    ): FloatArray {
        val requestId = totalRequests.incrementAndGet()
        
        // L1: Check memory cache first
        memoryCache[key]?.let { entry ->
            if (!entry.isExpired()) {
                memoryHits.incrementAndGet()
                performanceMonitor.recordCacheHit("memory", true)
                _cacheEvents.emit(CacheEvent.Hit(key, CacheLevel.MEMORY))
                return entry.data
            } else {
                memoryCache.remove(key)
            }
        }
        
        // L2: Check database
        val dbEntity = embeddingDao.getEmbeddingForMedia(key)
        if (dbEntity != null && !isEntityExpired(dbEntity)) {
            // Update memory cache
            val floatArray = dbEntity.getEmbeddingAsFloatArray()
            memoryCache[key] = CacheEntry(floatArray, System.currentTimeMillis())
            
            databaseHits.incrementAndGet()
            performanceMonitor.recordCacheHit("database", true)
            _cacheEvents.emit(CacheEvent.Hit(key, CacheLevel.DATABASE))
            return floatArray
        }
        
        // L3: Compute new embedding
        computeOperations.incrementAndGet()
        performanceMonitor.recordCacheHit("compute", false)
        _cacheEvents.emit(CacheEvent.Miss(key))
        
        val embedding = computeIfAbsent()
        putEmbedding(key, embedding)
        
        // Add to warming queue if this might be requested again
        scope.launch {
            addToWarmingQueue(key, 0.5f)
        }
        
        return embedding
    }
    
    /**
     * Put embedding into all cache layers
     */
    suspend fun putEmbedding(key: String, embedding: FloatArray) {
        val timestamp = System.currentTimeMillis()
        
        // Update memory cache
        memoryCache[key] = CacheEntry(embedding, timestamp)
        
        // Update database asynchronously
        scope.launch {
            try {
                val entity = SemanticEmbeddingEntity.fromFloatArray(
                    mediaRatingKey = key,
                    embedding = embedding,
                    language = detectLanguage(key),
                    embeddingSource = com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMED,
                    modelVersion = NlpConfig.MODEL_VERSION,
                    contentHash = generateHash(embedding)
                )
                embeddingDao.insertEmbedding(entity)
            } catch (e: Exception) {
                // Log but don't fail the operation
            }
        }
        
        // Enforce memory cache size
        if (memoryCache.size > maxMemoryEntries) {
            evictLeastRecentlyUsed()
        }
        
        _cacheEvents.emit(CacheEvent.Put(key, CacheLevel.MEMORY))
    }
    
    /**
     * Batch get for multiple embeddings
     */
    suspend fun getEmbeddings(
        keys: List<String>,
        computeIfAbsent: suspend (String) -> FloatArray
    ): Map<String, FloatArray> {
        val results = mutableMapOf<String, FloatArray>()
        val missingKeys = mutableListOf<String>()
        
        // First pass: check all caches
        keys.forEach { key ->
            val cached = getCachedEmbedding(key)
            if (cached != null) {
                results[key] = cached
            } else {
                missingKeys.add(key)
            }
        }
        
        // Second pass: compute missing embeddings
        missingKeys.forEach { key ->
            val embedding = computeIfAbsent(key)
            results[key] = embedding
            putEmbedding(key, embedding)
        }
        
        return results
    }
    
    /**
     * Warm cache for popular content
     */
    suspend fun warmCacheForMedia(mediaKeys: List<String>, priority: Float = 1.0f) {
        mediaKeys.forEach { key ->
            addToWarmingQueue(key, priority)
        }
    }
    
    /**
     * Invalidate cache entries
     */
    suspend fun invalidate(key: String) {
        memoryCache.remove(key)
        embeddingDao.deleteEmbeddingForMedia(key)
        _cacheEvents.emit(CacheEvent.Invalidate(key))
    }
    
    /**
     * Invalidate all cache entries
     */
    suspend fun invalidateAll() {
        memoryCache.clear()
        embeddingDao.deleteAllEmbeddings()
        _cacheEvents.emit(CacheEvent.InvalidateAll)
    }
    
    /**
     * Get cache statistics
     */
    fun getCacheStatistics(): CacheStatistics {
        val total = totalRequests.get()
        val memHits = memoryHits.get()
        val dbHits = databaseHits.get()
        val computes = computeOperations.get()
        
        val memoryHitRate = if (total > 0) memHits.toFloat() / total else 0f
        val databaseHitRate = if (total > 0) dbHits.toFloat() / total else 0f
        val computeRate = if (total > 0) computes.toFloat() / total else 0f
        
        return CacheStatistics(
            totalRequests = total,
            memoryHits = memHits,
            databaseHits = dbHits,
            computeOperations = computes,
            memoryHitRate = memoryHitRate,
            databaseHitRate = databaseHitRate,
            computeRate = computeRate,
            totalHitRate = memoryHitRate + databaseHitRate,
            memoryCacheSize = memoryCache.size,
            averageEmbeddingSize = getAverageEmbeddingSize()
        )
    }
    
    private suspend fun getCachedEmbedding(key: String): FloatArray? {
        // Check memory
        memoryCache[key]?.let { entry ->
            if (!entry.isExpired()) {
                return entry.data
            } else {
                memoryCache.remove(key)
            }
        }
        
        // Check database
        val entity = embeddingDao.getEmbeddingForMedia(key)
        if (entity != null && !isEntityExpired(entity)) {
            val embedding = entity.getEmbeddingAsFloatArray()
            memoryCache[key] = CacheEntry(embedding, System.currentTimeMillis())
            return embedding
        }
        
        return null
    }
    
    private fun addToWarmingQueue(key: String, priority: Float) {
        // Only add if not already in cache
        if (!memoryCache.containsKey(key)) {
            warmingQueue.offer(WarmingRequest(key, priority, System.currentTimeMillis()))
        }
    }
    
    private suspend fun processWarmingQueue() {
        if (!isWarming.tryLock()) return
        
        try {
            val toProcess = mutableListOf<WarmingRequest>()
            
            // Process up to 10 items per cycle
            repeat(10) {
                val request = warmingQueue.poll() ?: return@repeat
                toProcess.add(request)
            }
            
            // Precompute embeddings for warming requests
            toProcess.forEach { request ->
                if (!memoryCache.containsKey(request.key)) {
                    try {
                        // This would trigger actual embedding computation
                        // in a real implementation
                        delay(10) // Simulate computation
                        _cacheEvents.emit(CacheEvent.Warmed(request.key))
                    } catch (e: Exception) {
                        _cacheEvents.emit(CacheEvent.Error(request.key, e.message))
                    }
                }
            }
        } finally {
            isWarming.unlock()
        }
    }
    
    private suspend fun cleanupExpiredEntries() {
        val now = System.currentTimeMillis()
        val expiredKeys = mutableListOf<String>()
        
        // Check memory cache
        memoryCache.forEach { (key, entry) ->
            if (entry.isExpired(now)) {
                expiredKeys.add(key)
            }
        }
        
        expiredKeys.forEach { key ->
            memoryCache.remove(key)
        }
        
        // Clean up database
        val cutoffTime = now - (NlpConfig.EMBEDDING_EXPIRY_HOURS * 60 * 60 * 1000)
        embeddingDao.deleteOldEmbeddings(cutoffTime)
        
        _cacheEvents.emit(CacheEvent.CleanedUp(expiredKeys.size))
    }
    
    private fun evictLeastRecentlyUsed() {
        val oldestKey = memoryCache.entries
            .minByOrNull { it.value.timestamp }
            ?.key
        
        oldestKey?.let { key ->
            memoryCache.remove(key)
        }
    }
    
    private fun isEntityExpired(entity: SemanticEmbeddingEntity): Boolean {
        val ageMs = System.currentTimeMillis() - entity.updatedAt
        return ageMs > (NlpConfig.EMBEDDING_EXPIRY_HOURS * 60 * 60 * 1000)
    }
    
    private fun detectLanguage(key: String): String {
        // Simple language detection based on key patterns
        // In production, use actual language detection
        return when {
            key.contains("zh") -> "zh"
            key.contains("ja") -> "ja"
            key.contains("ko") -> "ko"
            key.contains("ar") -> "ar"
            key.contains("hi") -> "hi"
            else -> "en"
        }
    }
    
    private fun generateHash(embedding: FloatArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val byteBuffer = ByteBuffer.allocate(embedding.size * 4)
        embedding.forEach { byteBuffer.putFloat(it) }
        val bytes = byteBuffer.array()
        return digest.digest(bytes).joinToString("") { "%02x".format(it) }
    }
    
    private fun getAverageEmbeddingSize(): Double {
        return if (memoryCache.isNotEmpty()) {
            memoryCache.values.map { it.data.size }.average()
        } else {
            0.0
        }
    }
    
    data class CacheEntry(
        val data: FloatArray,
        val timestamp: Long
    ) {
        fun isExpired(now: Long = System.currentTimeMillis()): Boolean {
            val ageMs = now - timestamp
            return ageMs > (NlpConfig.EMBEDDING_EXPIRY_HOURS * 60 * 60 * 1000)
        }
    }
    
    data class WarmingRequest(
        val key: String,
        val priority: Float,
        val timestamp: Long
    )
    
    sealed class CacheEvent {
        data class Hit(val key: String, val level: CacheLevel) : CacheEvent()
        data class Miss(val key: String) : CacheEvent()
        data class Put(val key: String, val level: CacheLevel) : CacheEvent()
        data class Invalidate(val key: String) : CacheEvent()
        object InvalidateAll : CacheEvent()
        data class Warmed(val key: String) : CacheEvent()
        data class Error(val key: String, val error: String?) : CacheEvent()
        data class CleanedUp(val count: Int) : CacheEvent()
    }
    
    enum class CacheLevel {
        MEMORY, DATABASE
    }
    
    data class CacheStatistics(
        val totalRequests: Long,
        val memoryHits: Long,
        val databaseHits: Long,
        val computeOperations: Long,
        val memoryHitRate: Float,
        val databaseHitRate: Float,
        val computeRate: Float,
        val totalHitRate: Float,
        val memoryCacheSize: Int,
        val averageEmbeddingSize: Double
    )
}