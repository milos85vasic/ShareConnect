package com.shareconnect.qa.ai.analyzer

import com.shareconnect.qa.ai.executor.AIClient
import com.shareconnect.qa.ai.models.*
import kotlinx.serialization.Serializable

/**
 * Analyzes test results and generates insights using AI
 */
class TestResultAnalyzer(private val aiClient: AIClient) {

    /**
     * Analyze a test result and provide insights
     */
    fun analyzeTestResult(testCase: TestCase, result: TestResult): TestAnalysis {
        val failedSteps = result.stepResults.filter { it.status == TestStatus.FAILED }

        if (result.status == TestStatus.PASSED) {
            return TestAnalysis(
                testCaseId = testCase.id,
                status = result.status,
                summary = "Test passed successfully",
                insights = listOf("All ${result.stepResults.size} steps completed successfully"),
                recommendations = emptyList(),
                rootCause = null,
                suggestedFixes = emptyList()
            )
        }

        // Use AI to analyze failure
        val aiAnalysis = analyzeFailureWithAI(testCase, result, failedSteps)

        return TestAnalysis(
            testCaseId = testCase.id,
            status = result.status,
            summary = aiAnalysis.summary,
            insights = aiAnalysis.insights,
            recommendations = aiAnalysis.recommendations,
            rootCause = aiAnalysis.rootCause,
            suggestedFixes = aiAnalysis.suggestedFixes
        )
    }

    /**
     * Analyze failure using AI
     */
    private fun analyzeFailureWithAI(
        testCase: TestCase,
        result: TestResult,
        failedSteps: List<StepResult>
    ): AIAnalysisResult {
        val prompt = buildString {
            appendLine("=== Test Failure Analysis ===")
            appendLine()
            appendLine("Test Case: ${testCase.name}")
            appendLine("Description: ${testCase.description}")
            appendLine("Category: ${testCase.category}")
            appendLine()
            appendLine("Failed Steps:")
            failedSteps.forEach { step ->
                appendLine("  Step ${step.stepNumber}:")
                appendLine("    Expected: ${testCase.steps.find { it.stepNumber == step.stepNumber }?.expectedOutcome}")
                appendLine("    Actual: ${step.actualOutcome}")
                appendLine("    Error: ${step.errorMessage}")
            }
            appendLine()
            appendLine("Duration: ${result.durationMs}ms")
            appendLine("Retry Count: ${result.retryCount}")
            appendLine()
            appendLine("Please analyze this test failure and provide:")
            appendLine("1. SUMMARY: A brief summary of what went wrong")
            appendLine("2. INSIGHTS: Key observations about the failure")
            appendLine("3. ROOT_CAUSE: The likely root cause of the failure")
            appendLine("4. RECOMMENDATIONS: What should be checked or fixed")
            appendLine("5. SUGGESTED_FIXES: Specific code fixes or configuration changes")
        }

        val systemPrompt = """
            You are an expert QA engineer analyzing Android app test failures.
            Provide detailed, actionable insights about test failures.
            Focus on identifying root causes and suggesting specific fixes.
        """.trimIndent()

        val response = aiClient.sendPrompt(prompt, systemPrompt, maxTokens = 2048)

        if (response.success) {
            return parseAIAnalysis(response.text)
        }

        return AIAnalysisResult(
            summary = "Test failed: ${result.failureReason}",
            insights = listOf("Failed at step(s): ${failedSteps.joinToString { it.stepNumber.toString() }}"),
            recommendations = listOf("Review test logs and screenshots"),
            rootCause = result.failureReason,
            suggestedFixes = emptyList()
        )
    }

    /**
     * Parse AI analysis response
     */
    private fun parseAIAnalysis(text: String): AIAnalysisResult {
        val lines = text.lines()

        val summary = lines.find { it.startsWith("SUMMARY:", ignoreCase = true) }
            ?.substringAfter(":")?.trim()
            ?: "Test failure detected"

        val insights = lines
            .filter { it.startsWith("INSIGHTS:", ignoreCase = true) || it.startsWith("-", ignoreCase = true) }
            .map { it.removePrefix("INSIGHTS:").removePrefix("-").trim() }
            .filter { it.isNotEmpty() }

        val rootCause = lines.find { it.startsWith("ROOT_CAUSE:", ignoreCase = true) }
            ?.substringAfter(":")?.trim()

        val recommendations = lines
            .filter { it.startsWith("RECOMMENDATIONS:", ignoreCase = true) || it.contains("recommend", ignoreCase = true) }
            .map { it.removePrefix("RECOMMENDATIONS:").removePrefix("-").trim() }
            .filter { it.isNotEmpty() }

        val suggestedFixes = lines
            .filter { it.startsWith("SUGGESTED_FIXES:", ignoreCase = true) || it.startsWith("FIX:", ignoreCase = true) }
            .map { it.removePrefix("SUGGESTED_FIXES:").removePrefix("FIX:").removePrefix("-").trim() }
            .filter { it.isNotEmpty() }

        return AIAnalysisResult(
            summary = summary,
            insights = insights.ifEmpty { listOf("Analysis in progress") },
            recommendations = recommendations.ifEmpty { listOf("Review logs and screenshots") },
            rootCause = rootCause,
            suggestedFixes = suggestedFixes
        )
    }

    /**
     * Analyze batch of test results
     */
    fun analyzeBatchResults(results: List<Pair<TestCase, TestResult>>): BatchAnalysis {
        val total = results.size
        val passed = results.count { it.second.status == TestStatus.PASSED }
        val failed = results.count { it.second.status == TestStatus.FAILED }
        val errors = results.count { it.second.status == TestStatus.ERROR }
        val skipped = results.count { it.second.status == TestStatus.SKIPPED }

        val failedTests = results.filter { it.second.status == TestStatus.FAILED || it.second.status == TestStatus.ERROR }

        val analyses = failedTests.map { (testCase, result) ->
            analyzeTestResult(testCase, result)
        }

        val commonPatterns = identifyCommonPatterns(analyses)

        return BatchAnalysis(
            totalTests = total,
            passed = passed,
            failed = failed,
            errors = errors,
            skipped = skipped,
            passRate = if (total > 0) (passed.toDouble() / total * 100) else 0.0,
            failedTestAnalyses = analyses,
            commonPatterns = commonPatterns,
            recommendations = generateBatchRecommendations(analyses, commonPatterns)
        )
    }

    /**
     * Identify common failure patterns
     */
    private fun identifyCommonPatterns(analyses: List<TestAnalysis>): List<String> {
        val patterns = mutableListOf<String>()

        // Group by root cause
        val rootCauses = analyses.mapNotNull { it.rootCause }.groupingBy { it }.eachCount()
        rootCauses.filter { it.value > 1 }.forEach { (cause, count) ->
            patterns.add("Multiple tests ($count) failed with: $cause")
        }

        // Group by category
        val categories = analyses.groupBy { it.testCaseId.substringBefore("_") }
        categories.filter { it.value.size > 1 }.forEach { (category, tests) ->
            patterns.add("$category tests have ${tests.size} failures")
        }

        return patterns
    }

    /**
     * Generate batch recommendations
     */
    private fun generateBatchRecommendations(
        analyses: List<TestAnalysis>,
        patterns: List<String>
    ): List<String> {
        val recommendations = mutableListOf<String>()

        if (patterns.isNotEmpty()) {
            recommendations.add("Address common patterns identified across multiple tests")
        }

        val allRecommendations = analyses.flatMap { it.recommendations }
        val commonRecommendations = allRecommendations
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
            .keys

        recommendations.addAll(commonRecommendations)

        return recommendations.distinct()
    }
}

/**
 * AI analysis result
 */
private data class AIAnalysisResult(
    val summary: String,
    val insights: List<String>,
    val recommendations: List<String>,
    val rootCause: String?,
    val suggestedFixes: List<String>
)

/**
 * Test analysis result
 */
@Serializable
data class TestAnalysis(
    val testCaseId: String,
    val status: TestStatus,
    val summary: String,
    val insights: List<String>,
    val recommendations: List<String>,
    val rootCause: String?,
    val suggestedFixes: List<String>
)

/**
 * Batch analysis result
 */
@Serializable
data class BatchAnalysis(
    val totalTests: Int,
    val passed: Int,
    val failed: Int,
    val errors: Int,
    val skipped: Int,
    val passRate: Double,
    val failedTestAnalyses: List<TestAnalysis>,
    val commonPatterns: List<String>,
    val recommendations: List<String>
)
