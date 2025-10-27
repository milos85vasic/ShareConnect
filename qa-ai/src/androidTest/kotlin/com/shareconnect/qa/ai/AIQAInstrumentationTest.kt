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


package com.shareconnect.qa.ai

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.qa.ai.models.Priority
import com.shareconnect.qa.ai.models.TestCategory
import com.shareconnect.qa.ai.runner.AIQARunner
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Main instrumentation test class for AI QA system
 * Run this test on an emulator to execute AI-powered tests
 */
@RunWith(AndroidJUnit4::class)
class AIQAInstrumentationTest {

    private lateinit var qaRunner: AIQARunner

    @Before
    fun setup() {
        qaRunner = AIQARunner()

        // Get API key from environment variable
        val apiKey = System.getenv("ANTHROPIC_API_KEY")
            ?: throw IllegalStateException("ANTHROPIC_API_KEY environment variable not set")

        qaRunner.initialize(apiKey)
    }

    /**
     * Run smoke tests - quick validation of core functionality
     */
    @Test
    fun runSmokeTests() = runBlocking {
        val result = qaRunner.runSmokeTests()
        println("\nSmoke test completed: ${result.reportDirectory}")

        // Assert pass rate is acceptable
        assert(result.batchAnalysis.passRate >= 80.0) {
            "Smoke tests pass rate (${result.batchAnalysis.passRate}%) is below 80%"
        }
    }

    /**
     * Run all tests - comprehensive test suite
     */
    @Test
    fun runAllTests() = runBlocking {
        val result = qaRunner.runAllTests()
        println("\nFull test run completed: ${result.reportDirectory}")

        // Log results
        println("Pass rate: ${result.batchAnalysis.passRate}%")
        if (result.batchAnalysis.failed > 0) {
            println("Failed tests: ${result.batchAnalysis.failed}")
        }
    }

    /**
     * Run profile management tests
     */
    @Test
    fun runProfileManagementTests() = runBlocking {
        val result = qaRunner.runTestsByCategory(TestCategory.PROFILE_MANAGEMENT)
        println("\nProfile management tests completed: ${result.reportDirectory}")
    }

    /**
     * Run sync functionality tests
     */
    @Test
    fun runSyncTests() = runBlocking {
        val result = qaRunner.runTestsByCategory(TestCategory.SYNC_FUNCTIONALITY)
        println("\nSync tests completed: ${result.reportDirectory}")
    }

    /**
     * Run UI flow tests
     */
    @Test
    fun runUIFlowTests() = runBlocking {
        val result = qaRunner.runTestsByCategory(TestCategory.UI_FLOW)
        println("\nUI flow tests completed: ${result.reportDirectory}")
    }

    /**
     * Run edge case tests
     */
    @Test
    fun runEdgeCaseTests() = runBlocking {
        val result = qaRunner.runTestsByCategory(TestCategory.EDGE_CASE)
        println("\nEdge case tests completed: ${result.reportDirectory}")
    }

    /**
     * Run critical priority tests only
     */
    @Test
    fun runCriticalTests() = runBlocking {
        val result = qaRunner.runTestsByPriority(Priority.CRITICAL)
        println("\nCritical tests completed: ${result.reportDirectory}")

        // Critical tests must have 100% pass rate
        assert(result.batchAnalysis.passRate == 100.0) {
            "Critical tests must have 100% pass rate, got ${result.batchAnalysis.passRate}%"
        }
    }

    /**
     * Run specific test suite
     */
    @Test
    fun runRegressionSuite() = runBlocking {
        val result = qaRunner.runTestSuite("TS_REG_001")
        println("\nRegression suite completed: ${result.reportDirectory}")
    }
}
