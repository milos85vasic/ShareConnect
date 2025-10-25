package com.shareconnect.designsystem.performance

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

/**
 * Unit tests for performance utility classes:
 * - BatchProcessor
 * - ConnectionPool
 * - MemoryCache
 */
class PerformanceUtilitiesTest {

    // BatchProcessor Tests
    @Test
    fun `BatchProcessor processes items in batches`() = runBlocking {
        val processedBatches = mutableListOf<List<Int>>()
        val processor = BatchProcessor<Int>(
            batchSize = 3,
            batchTimeoutMs = 5000
        ) { batch ->
            processedBatches.add(batch)
        }

        // Add 5 items - should create 1 batch of 3, then need flush for remaining 2
        processor.add(1)
        processor.add(2)
        processor.add(3) // Should trigger batch processing
        delay(50) // Give time for processing

        assertEquals(1, processedBatches.size)
        assertEquals(listOf(1, 2, 3), processedBatches[0])

        processor.add(4)
        processor.add(5)
        processor.flush() // Process remaining items
        delay(50)

        assertEquals(2, processedBatches.size)
        assertEquals(listOf(4, 5), processedBatches[1])
    }

    @Test
    fun `BatchProcessor respects batch timeout`() = runBlocking {
        val processedBatches = mutableListOf<List<String>>()
        val processor = BatchProcessor<String>(
            batchSize = 10,
            batchTimeoutMs = 100
        ) { batch ->
            processedBatches.add(batch)
        }

        processor.add("item1")
        processor.add("item2")

        // Wait for timeout to trigger batch processing
        delay(150)

        // Add another item which should trigger processing due to timeout
        processor.add("item3")
        delay(50)

        assertTrue(processedBatches.isNotEmpty())
    }

    @Test
    fun `BatchProcessor flush processes remaining items`() = runBlocking {
        val processedBatches = mutableListOf<List<Int>>()
        val processor = BatchProcessor<Int>(
            batchSize = 5,
            batchTimeoutMs = 5000
        ) { batch ->
            processedBatches.add(batch)
        }

        processor.add(1)
        processor.add(2)
        processor.flush()
        delay(50)

        assertEquals(1, processedBatches.size)
        assertEquals(listOf(1, 2), processedBatches[0])
    }

    // ConnectionPool Tests
    @Test
    fun `ConnectionPool creates and reuses connections`() = runBlocking {
        val creationCount = AtomicInteger(0)
        val pool = ConnectionPool<String>(
            maxConnections = 3,
            connectionFactory = {
                "connection_${creationCount.incrementAndGet()}"
            }
        )

        // Acquire first connection
        val conn1 = pool.acquire()
        assertEquals("connection_1", conn1)
        assertEquals(1, creationCount.get())

        // Release and acquire again - should reuse
        pool.release(conn1)
        val conn2 = pool.acquire()
        assertEquals("connection_1", conn2) // Same connection
        assertEquals(1, creationCount.get()) // No new connection created

        pool.clear()
        Unit
    }

    @Test
    fun `ConnectionPool respects max connections limit`() = runBlocking {
        val creationCount = AtomicInteger(0)
        val pool = ConnectionPool<Int>(
            maxConnections = 2,
            connectionFactory = {
                creationCount.incrementAndGet()
            }
        )

        val conn1 = pool.acquire()
        val conn2 = pool.acquire()

        assertEquals(2, creationCount.get())
        assertEquals(1, conn1)
        assertEquals(2, conn2)

        pool.clear()
        Unit
    }

    @Test
    fun `ConnectionPool validates connections before reuse`() = runBlocking {
        val invalidConnections = mutableSetOf<String>()
        val creationCount = AtomicInteger(0)

        val pool = ConnectionPool<String>(
            maxConnections = 3,
            connectionFactory = {
                "connection_${creationCount.incrementAndGet()}"
            },
            connectionValidator = { connection ->
                !invalidConnections.contains(connection)
            }
        )

        val conn1 = pool.acquire()
        assertEquals("connection_1", conn1)

        // Mark as invalid
        invalidConnections.add(conn1)
        pool.release(conn1)

        // Should create new connection since old one is invalid
        val conn2 = pool.acquire()
        assertEquals("connection_2", conn2)
        assertEquals(2, creationCount.get())

        pool.clear()
        Unit
    }

    @Test
    fun `ConnectionPool clear removes all connections`() = runBlocking {
        val pool = ConnectionPool<String>(
            maxConnections = 3,
            connectionFactory = { "connection" }
        )

        val conn1 = pool.acquire()
        val conn2 = pool.acquire()
        pool.release(conn1)
        pool.release(conn2)

        pool.clear()

        // After clear, acquiring should create new connections
        val conn3 = pool.acquire()
        assertNotNull(conn3)

        pool.clear()
        Unit
    }

    // MemoryCache Tests
    @Test
    fun `MemoryCache stores and retrieves values`() {
        val cache = MemoryCache<String, Int>()

        cache.put("key1", 100)
        cache.put("key2", 200)

        assertEquals(100, cache.get("key1"))
        assertEquals(200, cache.get("key2"))
        assertNull(cache.get("key3"))
    }

    @Test
    fun `MemoryCache respects max size with LRU eviction`() {
        val cache = MemoryCache<Int, String>(maxSize = 3)

        cache.put(1, "one")
        cache.put(2, "two")
        cache.put(3, "three")
        cache.put(4, "four") // Should evict key 1

        assertNull(cache.get(1)) // Oldest, should be evicted
        assertEquals("two", cache.get(2))
        assertEquals("three", cache.get(3))
        assertEquals("four", cache.get(4))
        assertEquals(3, cache.size())
    }

    @Test
    fun `MemoryCache LRU updates on access`() {
        val cache = MemoryCache<Int, String>(maxSize = 3)

        cache.put(1, "one")
        cache.put(2, "two")
        cache.put(3, "three")

        // Access key 1 to make it recently used
        cache.get(1)

        // Add new item - should evict key 2 (oldest)
        cache.put(4, "four")

        assertEquals("one", cache.get(1)) // Should still exist
        assertNull(cache.get(2)) // Should be evicted
        assertEquals("three", cache.get(3))
        assertEquals("four", cache.get(4))
    }

    @Test
    fun `MemoryCache respects TTL expiration`() {
        val cache = MemoryCache<String, String>()

        cache.put("key1", "value1", ttlMs = 100) // 100ms TTL

        assertEquals("value1", cache.get("key1"))

        Thread.sleep(150) // Wait for expiration

        assertNull(cache.get("key1")) // Should be expired
    }

    @Test
    fun `MemoryCache remove deletes entry`() {
        val cache = MemoryCache<String, String>()

        cache.put("key1", "value1")
        assertEquals("value1", cache.get("key1"))

        cache.remove("key1")
        assertNull(cache.get("key1"))
    }

    @Test
    fun `MemoryCache clear removes all entries`() {
        val cache = MemoryCache<String, Int>()

        cache.put("key1", 1)
        cache.put("key2", 2)
        cache.put("key3", 3)

        assertEquals(3, cache.size())

        cache.clear()

        assertEquals(0, cache.size())
        assertNull(cache.get("key1"))
        assertNull(cache.get("key2"))
        assertNull(cache.get("key3"))
    }

    @Test
    fun `MemoryCache handles concurrent access safely`() {
        val cache = MemoryCache<Int, String>(maxSize = 100)
        val threads = (1..10).map { threadIndex ->
            Thread {
                repeat(10) { i ->
                    val key = threadIndex * 100 + i
                    cache.put(key, "value_$key")
                    cache.get(key)
                }
            }
        }

        threads.forEach { it.start() }
        threads.forEach { it.join() }

        // Cache should not crash and should maintain size limit
        assertTrue(cache.size() <= 100)
    }

    @Test
    fun `MemoryCache size returns correct count`() {
        val cache = MemoryCache<String, String>()

        assertEquals(0, cache.size())

        cache.put("key1", "value1")
        assertEquals(1, cache.size())

        cache.put("key2", "value2")
        assertEquals(2, cache.size())

        cache.remove("key1")
        assertEquals(1, cache.size())

        cache.clear()
        assertEquals(0, cache.size())
    }
}
