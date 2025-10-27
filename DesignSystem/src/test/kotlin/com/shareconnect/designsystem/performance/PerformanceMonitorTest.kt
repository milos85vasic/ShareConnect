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

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for PerformanceMonitor
 */
class PerformanceMonitorTest {

    private lateinit var monitor: PerformanceMonitor

    @Before
    fun setup() {
        monitor = PerformanceMonitor()
    }

    @After
    fun tearDown() {
        monitor.clearStats()
    }

    @Test
    fun `measure operation records execution time`() {
        val operationName = "test_operation"

        val result = monitor.measure(operationName) {
            Thread.sleep(50)
            "result"
        }

        assertEquals("result", result)
        val stats = monitor.getOperationStats(operationName)
        assertNotNull(stats)
        assertTrue(stats!!.average >= 50)
    }

    @Test
    fun `measureOperation records execution time for suspend functions`() = runBlocking {
        val operationName = "test_suspend_operation"

        val result = monitor.measureOperation(operationName) {
            delay(50)
            "result"
        }

        assertEquals("result", result)
        val stats = monitor.getOperationStats(operationName)
        assertNotNull(stats)
        assertTrue(stats!!.average >= 50)
    }

    @Test
    fun `multiple operations tracked correctly`() {
        monitor.measure("operation1") { Thread.sleep(10) }
        monitor.measure("operation2") { Thread.sleep(20) }
        monitor.measure("operation1") { Thread.sleep(15) }

        val stats1 = monitor.getOperationStats("operation1")
        val stats2 = monitor.getOperationStats("operation2")

        assertNotNull(stats1)
        assertNotNull(stats2)
        assertEquals(2, stats1!!.count)
        assertEquals(1, stats2!!.count)
    }

    @Test
    fun `operation stats calculates correctly`() {
        // Execute operation multiple times with known delays
        monitor.measure("test_op") { Thread.sleep(10) }
        monitor.measure("test_op") { Thread.sleep(20) }
        monitor.measure("test_op") { Thread.sleep(30) }

        val stats = monitor.getOperationStats("test_op")
        assertNotNull(stats)
        assertEquals(3, stats!!.count)
        assertTrue(stats.min >= 10)
        assertTrue(stats.max >= 30)
        assertTrue(stats.average >= 20)
    }

    @Test
    fun `getAllStats returns all operations`() {
        monitor.measure("op1") { Thread.sleep(5) }
        monitor.measure("op2") { Thread.sleep(5) }
        monitor.measure("op3") { Thread.sleep(5) }

        val allStats = monitor.getAllStats()
        assertEquals(3, allStats.size)
        assertTrue(allStats.any { it.operationName == "op1" })
        assertTrue(allStats.any { it.operationName == "op2" })
        assertTrue(allStats.any { it.operationName == "op3" })
    }

    @Test
    fun `clearStats removes all statistics`() {
        monitor.measure("op1") { Thread.sleep(5) }
        monitor.measure("op2") { Thread.sleep(5) }

        assertEquals(2, monitor.getAllStats().size)

        monitor.clearStats()

        assertEquals(0, monitor.getAllStats().size)
        assertNull(monitor.getOperationStats("op1"))
        assertNull(monitor.getOperationStats("op2"))
    }

    @Test
    fun `getOperationStats returns null for unknown operation`() {
        val stats = monitor.getOperationStats("non_existent")
        assertNull(stats)
    }

    @Test
    fun `measure preserves exceptions`() {
        try {
            monitor.measure("failing_op") {
                throw RuntimeException("Test exception")
            }
            fail("Should have thrown exception")
        } catch (e: RuntimeException) {
            assertEquals("Test exception", e.message)
            // Stats should still be recorded even when exception is thrown
            val stats = monitor.getOperationStats("failing_op")
            assertNotNull(stats)
        }
    }

    @Test
    fun `measureOperation preserves exceptions`() = runBlocking {
        try {
            monitor.measureOperation("failing_suspend_op") {
                throw RuntimeException("Test exception")
            }
            fail("Should have thrown exception")
        } catch (e: RuntimeException) {
            assertEquals("Test exception", e.message)
            // Stats should still be recorded even when exception is thrown
            val stats = monitor.getOperationStats("failing_suspend_op")
            assertNotNull(stats)
        }
    }

    @Test
    fun `median calculation is correct`() {
        // Create known distribution
        repeat(5) { monitor.measure("median_test") { Thread.sleep(10) } }
        repeat(5) { monitor.measure("median_test") { Thread.sleep(20) } }
        repeat(5) { monitor.measure("median_test") { Thread.sleep(30) } }

        val stats = monitor.getOperationStats("median_test")
        assertNotNull(stats)
        // Median should be around 20ms (middle value)
        assertTrue(stats!!.median >= 15 && stats.median <= 25)
    }

    @Test
    fun `GlobalPerformanceMonitor singleton works correctly`() {
        val result = GlobalPerformanceMonitor.measure("global_test") {
            Thread.sleep(10)
            "success"
        }

        assertEquals("success", result)
        val stats = GlobalPerformanceMonitor.monitor.getOperationStats("global_test")
        assertNotNull(stats)
        assertTrue(stats!!.average >= 10)
    }

    @Test
    fun `GlobalPerformanceMonitor measureSuspend works correctly`() = runBlocking {
        val result = GlobalPerformanceMonitor.measureSuspend("global_suspend_test") {
            delay(10)
            "success"
        }

        assertEquals("success", result)
        val stats = GlobalPerformanceMonitor.monitor.getOperationStats("global_suspend_test")
        assertNotNull(stats)
    }
}
