package com.shareconnect.qa.ai.validation

import com.shareconnect.qa.ai.execution.TestExecutionOrchestrator

/**
 * Regression Detector for AI QA Testing
 *
 * Detects regressions by comparing current results with historical baselines
 */
class RegressionDetector {

    private val historicalResults = mutableMapOf<String, TestExecutionOrchestrator.TestResult>()

    /**
     * Check for regressions
     */
    fun checkForRegressions(testId: String, result: TestExecutionOrchestrator.TestResult): Map<String, Boolean> {
        val previousResult = historicalResults[testId]

        // Store current result for future comparisons
        historicalResults[testId] = result

        if (previousResult == null) {
            // First run, no regression possible
            return mapOf(
                "regression_no_performance_degradation" to true,
                "regression_no_new_failures" to result.success,
                "regression_baseline_established" to true
            )
        }

        return mapOf(
            "regression_no_performance_degradation" to (result.durationMs <= previousResult.durationMs * 1.2), // Allow 20% variance
            "regression_no_new_failures" to (result.success || !previousResult.success),
            "regression_no_new_errors" to (result.errorMessage.isNullOrEmpty() || !previousResult.errorMessage.isNullOrEmpty())
        )
    }
}
