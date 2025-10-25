package com.shareconnect.designsystem.performance

import android.os.SystemClock
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
        val startTime = SystemClock.elapsedRealtime()

        return try {
            operation()
        } finally {
            val duration = SystemClock.elapsedRealtime() - startTime

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
        val startTime = SystemClock.elapsedRealtime()

        return try {
            block()
        } finally {
            val duration = SystemClock.elapsedRealtime() - startTime
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
 */
fun <T> Flow<T>.debounce(timeoutMillis: Long): Flow<T> = flow {
    var lastEmission: T? = null
    var lastEmissionTime = 0L

    collect { value ->
        val currentTime = SystemClock.elapsedRealtime()
        val timeSinceLastEmission = currentTime - lastEmissionTime

        if (timeSinceLastEmission >= timeoutMillis) {
            emit(value)
            lastEmission = value
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
        val currentTime = SystemClock.elapsedRealtime()
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
    private var lastProcessTime = 0L
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

        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastProcessTime >= batchTimeoutMs) return true

        return false
    }

    private suspend fun processBatch() {
        val items = mutex.withLock {
            batch.toList().also { batch.clear() }
        }

        if (items.isNotEmpty()) {
            processor(items)
            lastProcessTime = SystemClock.elapsedRealtime()
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
 */
class MemoryCache<K, V>(
    private val maxSize: Int = 100
) {
    private val cache = LinkedHashMap<K, CacheEntry<V>>(maxSize, 0.75f, true)

    fun put(key: K, value: V, ttlMs: Long = Long.MAX_VALUE) {
        synchronized(cache) {
            cache[key] = CacheEntry(value, System.currentTimeMillis() + ttlMs)

            // Evict oldest if over size
            if (cache.size > maxSize) {
                val oldest = cache.entries.first()
                cache.remove(oldest.key)
            }
        }
    }

    fun get(key: K): V? {
        synchronized(cache) {
            val entry = cache[key] ?: return null

            // Check if expired
            if (System.currentTimeMillis() > entry.expiryTime) {
                cache.remove(key)
                return null
            }

            return entry.value
        }
    }

    fun remove(key: K) {
        synchronized(cache) {
            cache.remove(key)
        }
    }

    fun clear() {
        synchronized(cache) {
            cache.clear()
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
