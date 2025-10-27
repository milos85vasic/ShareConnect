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


package com.shareconnect.qa.ai.execution

import com.shareconnect.qa.ai.mocks.MockServiceManager
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Test Execution Orchestrator
 *
 * Manages the complete test execution lifecycle including:
 * - Clean environment setup per test
 * - App scenario management
 * - Mock service lifecycle
 * - Result validation
 * - Environment cleanup
 */
class TestExecutionOrchestrator(
    private val emulatorManager: EmulatorManager,
    private val mockServiceManager: MockServiceManager,
    private val resultValidator: ResultValidator
) {

    data class TestExecutionConfig(
        val testId: String,
        val appScenario: String,
        val cleanEnvironment: Boolean = true,
        val mockServices: List<String> = MockServiceManager.SERVICE_CONFIGS.keys.toList(),
        val timeoutMinutes: Int = 30,
        val retryCount: Int = 2
    )

    data class TestResult(
        val testId: String,
        val success: Boolean,
        val durationMs: Long,
        val errorMessage: String? = null,
        val screenshots: List<File> = emptyList(),
        val logs: List<File> = emptyList(),
        val validationResults: Map<String, Boolean> = emptyMap()
    )

    private val executionHistory = mutableListOf<TestResult>()

    /**
     * Execute a single test with full environment management
     */
    fun executeTest(config: TestExecutionConfig): TestResult {
        val startTime = System.currentTimeMillis()
        var currentAttempt = 0
        var lastError: String? = null

        while (currentAttempt <= config.retryCount) {
            try {
                println("Executing test ${config.testId} (attempt ${currentAttempt + 1}/${config.retryCount + 1})")

                // Setup clean environment
                if (config.cleanEnvironment) {
                    setupCleanEnvironment(config)
                }

                // Execute test
                val result = executeTestInternal(config)

                // Validate results
                val validationResults = resultValidator.validateTestResults(config.testId, result)

                // Check if all validations passed
                val allValidationsPassed = validationResults.values.all { it }

                if (allValidationsPassed) {
                    val duration = System.currentTimeMillis() - startTime
                    val finalResult = result.copy(
                        success = true,
                        durationMs = duration,
                        validationResults = validationResults
                    )
                    executionHistory.add(finalResult)
                    return finalResult
                } else {
                    lastError = "Validation failed: ${validationResults.filter { !it.value }.keys.joinToString(", ")}"
                }

            } catch (e: Exception) {
                lastError = "Execution failed: ${e.message}"
                println("Test execution failed: ${e.message}")
            } finally {
                // Cleanup environment
                cleanupEnvironment(config)
                currentAttempt++
            }
        }

        // All attempts failed
        val duration = System.currentTimeMillis() - startTime
        val failedResult = TestResult(
            testId = config.testId,
            success = false,
            durationMs = duration,
            errorMessage = lastError ?: "All retry attempts failed"
        )
        executionHistory.add(failedResult)
        return failedResult
    }

    /**
     * Execute multiple tests in sequence with environment management
     */
    fun executeTestSuite(
        testConfigs: List<TestExecutionConfig>,
        parallelExecution: Boolean = false
    ): List<TestResult> {
        return if (parallelExecution) {
            executeTestsInParallel(testConfigs)
        } else {
            executeTestsSequentially(testConfigs)
        }
    }

    private fun executeTestsSequentially(testConfigs: List<TestExecutionConfig>): List<TestResult> {
        return testConfigs.map { config ->
            try {
                executeTest(config)
            } catch (e: Exception) {
                TestResult(
                    testId = config.testId,
                    success = false,
                    durationMs = 0,
                    errorMessage = "Suite execution failed: ${e.message}"
                )
            }
        }
    }

    private fun executeTestsInParallel(testConfigs: List<TestExecutionConfig>): List<TestResult> {
        // Group tests by app scenario to avoid conflicts
        val testsByScenario = testConfigs.groupBy { it.appScenario }

        val results = mutableListOf<TestResult>()

        for ((scenario, scenarioTests) in testsByScenario) {
            // Execute tests for same scenario in parallel
            data class ThreadWithResult(val thread: Thread, var result: TestResult? = null)

            val threads = scenarioTests.map { config ->
                var capturedResult: TestResult? = null
                val thread = Thread {
                    try {
                        capturedResult = executeTest(config)
                    } catch (e: Exception) {
                        capturedResult = TestResult(
                            testId = config.testId,
                            success = false,
                            durationMs = 0,
                            errorMessage = "Parallel execution failed: ${e.message}"
                        )
                    }
                }.apply { start() }
                ThreadWithResult(thread, null).apply {
                    thread.join()
                    result = capturedResult
                }
            }

            val scenarioResults = threads.mapNotNull { it.result }
            results.addAll(scenarioResults)
        }

        return results
    }

    private fun setupCleanEnvironment(config: TestExecutionConfig) {
        println("Setting up clean environment for test ${config.testId}")

        // Start mock services
        mockServiceManager.startAllServices()

        // Setup emulator with specific app scenario
        emulatorManager.setupEmulatorForScenario(config.appScenario)

        // Wait for everything to be ready
        Thread.sleep(5000) // Give services time to start
    }

    private fun executeTestInternal(config: TestExecutionConfig): TestResult {
        // This would integrate with the existing AI QA test execution
        // For now, return a mock successful result
        // In real implementation, this would call the AI test executor

        Thread.sleep(1000) // Simulate test execution time

        return TestResult(
            testId = config.testId,
            success = true,
            durationMs = 1000,
            screenshots = listOf(File("screenshot1.png")),
            logs = listOf(File("log1.txt"))
        )
    }

    private fun cleanupEnvironment(config: TestExecutionConfig) {
        println("Cleaning up environment for test ${config.testId}")

        try {
            // Stop mock services
            mockServiceManager.stopAllServices()

            // Clean emulator
            emulatorManager.cleanupEmulator()

        } catch (e: Exception) {
            println("Warning: Cleanup failed: ${e.message}")
        }
    }

    /**
     * Get execution statistics
     */
    fun getExecutionStats(): ExecutionStats {
        val totalTests = executionHistory.size
        val passedTests = executionHistory.count { it.success }
        val failedTests = totalTests - passedTests
        val averageDuration = if (totalTests > 0) {
            executionHistory.sumOf { it.durationMs } / totalTests
        } else 0

        return ExecutionStats(
            totalTests = totalTests,
            passedTests = passedTests,
            failedTests = failedTests,
            averageDurationMs = averageDuration,
            successRate = if (totalTests > 0) (passedTests.toDouble() / totalTests) * 100 else 0.0
        )
    }

    /**
     * Get failed tests for retry
     */
    fun getFailedTests(): List<TestResult> {
        return executionHistory.filter { !it.success }
    }

    data class ExecutionStats(
        val totalTests: Int,
        val passedTests: Int,
        val failedTests: Int,
        val averageDurationMs: Long,
        val successRate: Double
    )
}