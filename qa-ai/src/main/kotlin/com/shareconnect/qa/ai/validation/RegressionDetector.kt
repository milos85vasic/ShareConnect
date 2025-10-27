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
