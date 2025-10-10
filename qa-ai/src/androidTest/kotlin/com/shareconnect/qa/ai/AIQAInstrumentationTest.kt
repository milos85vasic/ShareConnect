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
