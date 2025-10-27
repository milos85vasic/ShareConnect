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


package com.shareconnect.qa.ai.runner

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.shareconnect.qa.ai.analyzer.BatchAnalysis
import com.shareconnect.qa.ai.analyzer.TestResultAnalyzer
import com.shareconnect.qa.ai.emulator.EmulatorBridge
import com.shareconnect.qa.ai.executor.AIClient
import com.shareconnect.qa.ai.executor.AITestExecutor
import com.shareconnect.qa.ai.fixer.AutoFixer
import com.shareconnect.qa.ai.models.*
import com.shareconnect.qa.ai.reporter.HTMLReporter
import com.shareconnect.qa.ai.reporter.JSONReporter
import com.shareconnect.qa.ai.testbank.TestBank
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Main orchestrator for AI-powered QA testing
 */
class AIQARunner {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private var apiKey: String = ""
    private lateinit var aiClient: AIClient
    private lateinit var emulatorBridge: EmulatorBridge
    private lateinit var testExecutor: AITestExecutor
    private lateinit var resultAnalyzer: TestResultAnalyzer
    private lateinit var autoFixer: AutoFixer
    private lateinit var testBank: TestBank

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    /**
     * Initialize the AI QA system
     */
    fun initialize(anthropicApiKey: String) {
        println("Initializing AI QA System...")

        apiKey = anthropicApiKey
        aiClient = AIClient(apiKey)
        emulatorBridge = EmulatorBridge()
        testExecutor = AITestExecutor(aiClient, emulatorBridge)
        resultAnalyzer = TestResultAnalyzer(aiClient)
        autoFixer = AutoFixer(aiClient)
        testBank = TestBank("qa-ai/testbank")

        println("AI QA System initialized successfully")
        testBank.printStatistics()
    }

    /**
     * Run all tests
     */
    suspend fun runAllTests(): QARunResult = withContext(Dispatchers.Default) {
        println("\n=== Running All Tests ===\n")

        val testCases = testBank.getAllTestCases()
        runTests(testCases, "Full Test Run")
    }

    /**
     * Run smoke tests
     */
    suspend fun runSmokeTests(): QARunResult = withContext(Dispatchers.Default) {
        println("\n=== Running Smoke Tests ===\n")

        val smokeSuite = testBank.getTestSuite("TS_SMOKE_001")
        val testCases = smokeSuite?.let { testBank.getTestCasesInSuite(it.id) } ?: emptyList()

        runTests(testCases, "Smoke Test Run")
    }

    /**
     * Run tests by category
     */
    suspend fun runTestsByCategory(category: TestCategory): QARunResult = withContext(Dispatchers.Default) {
        println("\n=== Running ${category.name} Tests ===\n")

        val testCases = testBank.getTestCasesByCategory(category)
        runTests(testCases, "${category.name} Test Run")
    }

    /**
     * Run tests by priority
     */
    suspend fun runTestsByPriority(priority: Priority): QARunResult = withContext(Dispatchers.Default) {
        println("\n=== Running ${priority.name} Priority Tests ===\n")

        val testCases = testBank.getTestCasesByPriority(priority)
        runTests(testCases, "${priority.name} Priority Test Run")
    }

    /**
     * Run a specific test suite
     */
    suspend fun runTestSuite(suiteId: String): QARunResult = withContext(Dispatchers.Default) {
        println("\n=== Running Test Suite: $suiteId ===\n")

        val testCases = testBank.getTestCasesInSuite(suiteId)
        runTests(testCases, "Suite: $suiteId")
    }

    /**
     * Core test execution logic
     */
    private suspend fun runTests(testCases: List<TestCase>, runName: String): QARunResult {
        val startTime = System.currentTimeMillis()
        val results = mutableListOf<Pair<TestCase, TestResult>>()

        println("Executing ${testCases.size} test cases...\n")

        // Launch app
        emulatorBridge.launchApp()
        delay(2000)

        // Execute tests sequentially
        for ((index, testCase) in testCases.withIndex()) {
            println("[${index + 1}/${testCases.size}] Running: ${testCase.name}")

            try {
                val result = testExecutor.executeTestCase(testCase)
                results.add(testCase to result)

                val status = when (result.status) {
                    TestStatus.PASSED -> "✓ PASSED"
                    TestStatus.FAILED -> "✗ FAILED"
                    TestStatus.ERROR -> "⚠ ERROR"
                    TestStatus.SKIPPED -> "⊘ SKIPPED"
                    else -> "? UNKNOWN"
                }

                println("  $status (${result.durationMs}ms)")

                if (result.status == TestStatus.FAILED || result.status == TestStatus.ERROR) {
                    println("    Failure: ${result.failureReason}")
                }

            } catch (e: Exception) {
                println("  ⚠ ERROR: ${e.message}")
                results.add(
                    testCase to TestResult(
                        testCaseId = testCase.id,
                        status = TestStatus.ERROR,
                        startTime = System.currentTimeMillis(),
                        endTime = System.currentTimeMillis(),
                        durationMs = 0,
                        stepResults = emptyList(),
                        failureReason = e.message
                    )
                )
            }

            // Brief pause between tests
            delay(1000)
        }

        val endTime = System.currentTimeMillis()

        // Analyze results
        println("\n=== Analyzing Results ===\n")
        val batchAnalysis = resultAnalyzer.analyzeBatchResults(results)

        // Generate fixes for failures
        val fixes = generateFixesForFailures(results)

        // Generate reports
        val reportDir = generateReports(runName, results, batchAnalysis)

        // Print summary
        printSummary(batchAnalysis, endTime - startTime)

        return QARunResult(
            runName = runName,
            startTime = startTime,
            endTime = endTime,
            totalDurationMs = endTime - startTime,
            testResults = results,
            batchAnalysis = batchAnalysis,
            fixSuggestions = fixes,
            reportDirectory = reportDir
        )
    }

    /**
     * Generate fixes for failed tests
     */
    private suspend fun generateFixesForFailures(
        results: List<Pair<TestCase, TestResult>>
    ): List<com.shareconnect.qa.ai.fixer.FixSuggestion> = withContext(Dispatchers.Default) {
        val failedTests = results.filter {
            it.second.status == TestStatus.FAILED || it.second.status == TestStatus.ERROR
        }

        val fixes = mutableListOf<com.shareconnect.qa.ai.fixer.FixSuggestion>()

        println("Generating fixes for ${failedTests.size} failed tests...")

        for ((testCase, result) in failedTests) {
            val analysis = resultAnalyzer.analyzeTestResult(testCase, result)
            val fix = autoFixer.generateFix(testCase, result, analysis)
            fixes.add(fix)
            println("  Generated fix for: ${testCase.name}")
        }

        return@withContext fixes
    }

    /**
     * Generate reports
     */
    private fun generateReports(
        runName: String,
        results: List<Pair<TestCase, TestResult>>,
        batchAnalysis: BatchAnalysis
    ): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val reportDir = "qa-ai/reports/run_$timestamp"
        File(reportDir).mkdirs()

        println("\nGenerating reports in: $reportDir")

        // JSON Report
        val jsonReporter = JSONReporter()
        jsonReporter.generateReport(results, batchAnalysis, "$reportDir/results.json")
        println("  - JSON report generated")

        // HTML Report
        val htmlReporter = HTMLReporter()
        htmlReporter.generateReport(results, batchAnalysis, "$reportDir/report.html")
        println("  - HTML report generated")

        // Summary file
        val summaryFile = File(reportDir, "summary.txt")
        summaryFile.writeText(generateTextSummary(batchAnalysis))
        println("  - Summary text generated")

        return reportDir
    }

    /**
     * Print summary to console
     */
    private fun printSummary(batchAnalysis: BatchAnalysis, durationMs: Long) {
        println("\n" + "=".repeat(60))
        println("TEST RUN SUMMARY")
        println("=".repeat(60))
        println("Total Tests:    ${batchAnalysis.totalTests}")
        println("Passed:         ${batchAnalysis.passed} ✓")
        println("Failed:         ${batchAnalysis.failed} ✗")
        println("Errors:         ${batchAnalysis.errors} ⚠")
        println("Skipped:        ${batchAnalysis.skipped} ⊘")
        println("Pass Rate:      ${"%.2f".format(batchAnalysis.passRate)}%")
        println("Duration:       ${durationMs / 1000}s")
        println("=".repeat(60))

        if (batchAnalysis.commonPatterns.isNotEmpty()) {
            println("\nCommon Patterns:")
            batchAnalysis.commonPatterns.forEach { println("  - $it") }
        }

        if (batchAnalysis.recommendations.isNotEmpty()) {
            println("\nRecommendations:")
            batchAnalysis.recommendations.forEach { println("  - $it") }
        }

        println()
    }

    /**
     * Generate text summary
     */
    private fun generateTextSummary(batchAnalysis: BatchAnalysis): String {
        return buildString {
            appendLine("ShareConnect AI QA Test Summary")
            appendLine("================================")
            appendLine()
            appendLine("Total Tests: ${batchAnalysis.totalTests}")
            appendLine("Passed: ${batchAnalysis.passed}")
            appendLine("Failed: ${batchAnalysis.failed}")
            appendLine("Errors: ${batchAnalysis.errors}")
            appendLine("Skipped: ${batchAnalysis.skipped}")
            appendLine("Pass Rate: ${"%.2f".format(batchAnalysis.passRate)}%")
            appendLine()

            if (batchAnalysis.commonPatterns.isNotEmpty()) {
                appendLine("Common Patterns:")
                batchAnalysis.commonPatterns.forEach { appendLine("  - $it") }
                appendLine()
            }

            if (batchAnalysis.recommendations.isNotEmpty()) {
                appendLine("Recommendations:")
                batchAnalysis.recommendations.forEach { appendLine("  - $it") }
                appendLine()
            }

            if (batchAnalysis.failedTestAnalyses.isNotEmpty()) {
                appendLine("Failed Tests:")
                batchAnalysis.failedTestAnalyses.forEach { analysis ->
                    appendLine("  - ${analysis.testCaseId}: ${analysis.summary}")
                }
            }
        }
    }
}

/**
 * QA run result
 */
data class QARunResult(
    val runName: String,
    val startTime: Long,
    val endTime: Long,
    val totalDurationMs: Long,
    val testResults: List<Pair<TestCase, TestResult>>,
    val batchAnalysis: BatchAnalysis,
    val fixSuggestions: List<com.shareconnect.qa.ai.fixer.FixSuggestion>,
    val reportDirectory: String
)
