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
