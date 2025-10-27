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


package com.shareconnect.designsystem.performance

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

/**
 * Performance Monitor
 * Tracks and optimizes performance across ShareConnect
 */
class PerformanceMonitor {

    companion object {
        @PublishedApi
        internal const val TAG = "PerformanceMonitor"
        @PublishedApi
        internal const val SLOW_OPERATION_THRESHOLD_MS = 1000L
    }

    private val operationTimes = ConcurrentHashMap<String, MutableList<Long>>()

    /**
     * Measure operation execution time
     */
    suspend fun <T> measureOperation(
        operationName: String,
        operation: suspend () -> T
    ): T {
        val startTime = System.currentTimeMillis()

        return try {
            operation()
        } finally {
            val duration = System.currentTimeMillis() - startTime

            recordOperationTime(operationName, duration)

            if (duration > SLOW_OPERATION_THRESHOLD_MS) {
                Log.w(TAG, "Slow operation detected: $operationName took ${duration}ms")
            }
        }
    }

    /**
     * Measure block execution time
     */
    inline fun <T> measure(operationName: String, block: () -> T): T {
        val startTime = System.currentTimeMillis()

        return try {
            block()
        } finally {
            val duration = System.currentTimeMillis() - startTime
            recordOperationTime(operationName, duration)

            if (duration > SLOW_OPERATION_THRESHOLD_MS) {
                Log.w(TAG, "Slow operation detected: $operationName took ${duration}ms")
            }
        }
    }

    /**
     * Record operation time
     */
    @PublishedApi
    internal fun recordOperationTime(operationName: String, duration: Long) {
        operationTimes.getOrPut(operationName) { mutableListOf() }.add(duration)
    }

    /**
     * Get operation statistics
     */
    fun getOperationStats(operationName: String): OperationStats? {
        val times = operationTimes[operationName] ?: return null

        if (times.isEmpty()) return null

        return OperationStats(
            operationName = operationName,
            count = times.size,
            min = times.minOrNull() ?: 0,
            max = times.maxOrNull() ?: 0,
            average = times.average().toLong(),
            median = times.sorted()[times.size / 2]
        )
    }

    /**
     * Get all operation statistics
     */
    fun getAllStats(): List<OperationStats> {
        return operationTimes.keys.mapNotNull { getOperationStats(it) }
    }

    /**
     * Clear statistics
     */
    fun clearStats() {
        operationTimes.clear()
    }

    /**
     * Log performance report
     */
    fun logPerformanceReport() {
        val stats = getAllStats().sortedByDescending { it.average }

        Log.d(TAG, "=== Performance Report ===")
        stats.forEach { stat ->
            Log.d(
                TAG,
                "${stat.operationName}: " +
                        "avg=${stat.average}ms, " +
                        "min=${stat.min}ms, " +
                        "max=${stat.max}ms, " +
                        "count=${stat.count}"
            )
        }
        Log.d(TAG, "=========================")
    }
}

/**
 * Operation statistics
 */
data class OperationStats(
    val operationName: String,
    val count: Int,
    val min: Long,
    val max: Long,
    val average: Long,
    val median: Long
)

/**
 * Debounce flow emissions
 * Note: This is a simplified implementation. For production use, consider kotlinx.coroutines.flow.debounce
 */
fun <T> Flow<T>.debounce(timeoutMillis: Long): Flow<T> = flow {
    var lastEmissionTime = 0L

    collect { value ->
        val currentTime = System.currentTimeMillis()
        val timeSinceLastEmission = currentTime - lastEmissionTime

        if (lastEmissionTime == 0L || timeSinceLastEmission >= timeoutMillis) {
            emit(value)
            lastEmissionTime = currentTime
        }
    }
}

/**
 * Throttle flow emissions
 */
fun <T> Flow<T>.throttle(periodMillis: Long): Flow<T> = flow {
    var lastEmissionTime = 0L

    collect { value ->
        val currentTime = System.currentTimeMillis()
        val timeSinceLastEmission = currentTime - lastEmissionTime

        if (timeSinceLastEmission >= periodMillis) {
            emit(value)
            lastEmissionTime = currentTime
        }
    }
}

/**
 * Batch operations
 */
class BatchProcessor<T>(
    private val batchSize: Int = 10,
    private val batchTimeoutMs: Long = 5000,
    private val processor: suspend (List<T>) -> Unit
) {
    private val batch = mutableListOf<T>()
    private var lastProcessTime = System.currentTimeMillis()
    private val mutex = Mutex()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    suspend fun add(item: T) {
        val shouldProcess = mutex.withLock {
            batch.add(item)
            shouldProcessBatch()
        }

        if (shouldProcess) {
            processBatch()
        }
    }

    private fun shouldProcessBatch(): Boolean {
        if (batch.size >= batchSize) return true

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastProcessTime >= batchTimeoutMs) return true

        return false
    }

    private suspend fun processBatch() {
        val items = mutex.withLock {
            batch.toList().also { batch.clear() }
        }

        if (items.isNotEmpty()) {
            processor(items)
            lastProcessTime = System.currentTimeMillis()
        }
    }

    suspend fun flush() {
        processBatch()
    }
}

/**
 * Connection pool for reusing connections
 */
class ConnectionPool<T>(
    private val maxConnections: Int = 5,
    private val connectionFactory: suspend () -> T,
    private val connectionValidator: suspend (T) -> Boolean = { true }
) {
    private val availableConnections = mutableListOf<T>()
    private val activeConnections = mutableSetOf<T>()
    private val mutex = Mutex()

    suspend fun acquire(): T {
        val connection = mutex.withLock {
            // Try to reuse existing connection
            while (availableConnections.isNotEmpty()) {
                val conn = availableConnections.removeAt(0)
                if (connectionValidator(conn)) {
                    activeConnections.add(conn)
                    return@withLock conn
                }
            }

            // Create new connection if under limit
            if (activeConnections.size < maxConnections) {
                val conn = connectionFactory()
                activeConnections.add(conn)
                return@withLock conn
            }

            null
        }

        return connection ?: run {
            // Wait for available connection
            delay(100)
            acquire()
        }
    }

    suspend fun release(connection: T) {
        mutex.withLock {
            activeConnections.remove(connection)
            availableConnections.add(connection)
        }
    }

    suspend fun clear() {
        mutex.withLock {
            availableConnections.clear()
            activeConnections.clear()
        }
    }
}

/**
 * Memory cache with LRU eviction
 * Simple implementation using MutableMap with manual LRU tracking
 */
class MemoryCache<K, V>(
    private val maxSize: Int = 100
) {
    private val cache = mutableMapOf<K, CacheEntry<V>>()
    private val accessOrder = mutableListOf<K>()

    fun put(key: K, value: V, ttlMs: Long = -1) {
        synchronized(cache) {
            // Remove from access order if already exists
            accessOrder.remove(key)

            // Add to cache (ttlMs = -1 means no expiration)
            val expiryTime = if (ttlMs < 0) {
                Long.MAX_VALUE
            } else {
                System.currentTimeMillis() + ttlMs
            }
            cache[key] = CacheEntry(value, expiryTime)

            // Add to end of access order (most recently used)
            accessOrder.add(key)

            // Evict oldest if over size
            while (accessOrder.size > maxSize) {
                val oldest = accessOrder.removeAt(0)
                cache.remove(oldest)
            }
        }
    }

    fun get(key: K): V? {
        synchronized(cache) {
            val entry = cache[key] ?: return null

            // Check if expired (Long.MAX_VALUE means never expires)
            if (entry.expiryTime != Long.MAX_VALUE && System.currentTimeMillis() > entry.expiryTime) {
                cache.remove(key)
                accessOrder.remove(key)
                return null
            }

            // Update access order (move to end = most recently used)
            accessOrder.remove(key)
            accessOrder.add(key)

            return entry.value
        }
    }

    fun remove(key: K) {
        synchronized(cache) {
            cache.remove(key)
            accessOrder.remove(key)
        }
    }

    fun clear() {
        synchronized(cache) {
            cache.clear()
            accessOrder.clear()
        }
    }

    fun size(): Int = synchronized(cache) { cache.size }

    private data class CacheEntry<V>(
        val value: V,
        val expiryTime: Long
    )
}

/**
 * Global performance monitor instance
 */
object GlobalPerformanceMonitor {
    val monitor = PerformanceMonitor()

    inline fun <T> measure(operationName: String, block: () -> T): T {
        return monitor.measure(operationName, block)
    }

    suspend fun <T> measureSuspend(operationName: String, operation: suspend () -> T): T {
        return monitor.measureOperation(operationName, operation)
    }

    fun logReport() {
        monitor.logPerformanceReport()
    }
}
