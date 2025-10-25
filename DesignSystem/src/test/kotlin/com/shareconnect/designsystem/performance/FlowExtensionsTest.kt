package com.shareconnect.designsystem.performance

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Flow extension functions:
 * - debounce
 * - throttle
 */
class FlowExtensionsTest {

    @Test
    fun `debounce filters rapid emissions`() = runBlocking {
        val source = flow {
            emit(1)
            delay(50)
            emit(2)
            delay(50)
            emit(3)
            delay(150) // Wait longer than debounce time
            emit(4)
        }

        val debounced = source.debounce(100).toList()

        // Should only emit values after 100ms of silence
        // Values 1, 2, 3 come quickly, only last one (3) should emit
        // Then 4 after longer delay
        assertTrue(debounced.contains(3))
        assertTrue(debounced.contains(4))
        assertTrue(debounced.size <= 3) // Should filter out rapid emissions
    }

    @Test
    fun `debounce emits immediately when first value arrives`() = runBlocking {
        val source = flow {
            emit(1)
            delay(200) // Wait longer than debounce
            emit(2)
        }

        val debounced = source.debounce(100).toList()

        // Both should be emitted since they're separated by > 100ms
        assertEquals(listOf(1, 2), debounced)
    }

    @Test
    fun `debounce with zero timeout emits all values`() = runBlocking {
        val source = flow {
            emit(1)
            emit(2)
            emit(3)
        }

        val debounced = source.debounce(0).toList()

        // With 0ms debounce, all values should pass through
        assertEquals(listOf(1, 2, 3), debounced)
    }

    @Test
    fun `throttle limits emission rate`() = runBlocking {
        val source = flow {
            repeat(10) { i ->
                emit(i)
                delay(20) // Emit every 20ms
            }
        }

        val throttled = source.throttle(100).toList()

        // With 100ms throttle and emissions every 20ms,
        // should emit roughly every 100ms
        assertTrue(throttled.size < 10) // Should filter some values
        assertTrue(throttled.size >= 2) // Should emit at least a few
    }

    @Test
    fun `throttle emits first value immediately`() = runBlocking {
        val source = flow {
            emit(1)
            delay(10)
            emit(2)
            delay(10)
            emit(3)
        }

        val throttled = source.throttle(50).toList()

        // First emission should always go through
        assertEquals(1, throttled.first())
    }

    @Test
    fun `throttle with slow emissions passes all values`() = runBlocking {
        val source = flow {
            emit(1)
            delay(150)
            emit(2)
            delay(150)
            emit(3)
        }

        val throttled = source.throttle(100).toList()

        // All values should pass since they're slower than throttle period
        assertEquals(listOf(1, 2, 3), throttled)
    }

    @Test
    fun `throttle respects period between emissions`() = runBlocking {
        val emissions = mutableListOf<Long>()
        val startTime = System.currentTimeMillis()

        val source = flow {
            repeat(100) { i ->
                emit(i)
                delay(10)
            }
        }

        source.throttle(100).collect { value ->
            val elapsed = System.currentTimeMillis() - startTime
            emissions.add(elapsed)
        }

        // Check that emissions are at least 100ms apart
        for (i in 1 until emissions.size) {
            val timeBetween = emissions[i] - emissions[i - 1]
            assertTrue("Emissions should be >= 100ms apart, but were ${timeBetween}ms",
                       timeBetween >= 90) // Allow small tolerance
        }
    }

    @Test
    fun `debounce handles single emission correctly`() = runBlocking {
        val source = flow {
            emit(42)
        }

        val debounced = source.debounce(100).toList()

        assertEquals(listOf(42), debounced)
    }

    @Test
    fun `throttle handles single emission correctly`() = runBlocking {
        val source = flow {
            emit(42)
        }

        val throttled = source.throttle(100).toList()

        assertEquals(listOf(42), throttled)
    }

    @Test
    fun `debounce with rapid burst emits only last value`() = runBlocking {
        val source = flow {
            // Rapid burst
            emit(1)
            emit(2)
            emit(3)
            emit(4)
            emit(5)
            delay(150) // Wait for debounce
        }

        val debounced = source.debounce(100).toList()

        // Should only get the last value from the burst
        assertTrue(debounced.size == 1)
        assertEquals(5, debounced.last())
    }

    @Test
    fun `throttle drops intermediate rapid values`() = runBlocking {
        val source = flow {
            emit(1) // Should emit
            delay(10)
            emit(2) // Should drop
            delay(10)
            emit(3) // Should drop
            delay(10)
            emit(4) // Should drop
            delay(100)
            emit(5) // Should emit (after throttle period)
        }

        val throttled = source.throttle(50).toList()

        assertEquals(1, throttled.first())
        assertTrue(throttled.contains(5))
        assertTrue(throttled.size <= 3) // Should drop some intermediate values
    }
}
