package com.shareconnect.qa.ai.reporter

import com.shareconnect.qa.ai.analyzer.BatchAnalysis
import com.shareconnect.qa.ai.models.TestCase
import com.shareconnect.qa.ai.models.TestResult
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * JSON report generator
 */
class JSONReporter {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun generateReport(
        results: List<Pair<TestCase, TestResult>>,
        analysis: BatchAnalysis,
        outputPath: String
    ) {
        val report = JSONReport(
            summary = ReportSummary(
                totalTests = analysis.totalTests,
                passed = analysis.passed,
                failed = analysis.failed,
                errors = analysis.errors,
                skipped = analysis.skipped,
                passRate = analysis.passRate
            ),
            testResults = results.map { (testCase, result) ->
                TestCaseResult(
                    id = testCase.id,
                    name = testCase.name,
                    category = testCase.category.name,
                    priority = testCase.priority.name,
                    status = result.status.name,
                    durationMs = result.durationMs,
                    failureReason = result.failureReason,
                    screenshots = result.screenshots,
                    retryCount = result.retryCount
                )
            },
            analysis = analysis,
            timestamp = System.currentTimeMillis()
        )

        val jsonString = json.encodeToString(report)
        File(outputPath).writeText(jsonString)
    }
}

@Serializable
private data class JSONReport(
    val summary: ReportSummary,
    val testResults: List<TestCaseResult>,
    val analysis: BatchAnalysis,
    val timestamp: Long
)

@Serializable
private data class ReportSummary(
    val totalTests: Int,
    val passed: Int,
    val failed: Int,
    val errors: Int,
    val skipped: Int,
    val passRate: Double
)

@Serializable
private data class TestCaseResult(
    val id: String,
    val name: String,
    val category: String,
    val priority: String,
    val status: String,
    val durationMs: Long,
    val failureReason: String?,
    val screenshots: List<String>,
    val retryCount: Int
)
