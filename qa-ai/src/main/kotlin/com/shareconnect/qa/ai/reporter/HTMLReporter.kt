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
import com.shareconnect.qa.ai.models.TestStatus
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * HTML report generator
 */
class HTMLReporter {

    fun generateReport(
        results: List<Pair<TestCase, TestResult>>,
        analysis: BatchAnalysis,
        outputPath: String
    ) {
        val html = buildString {
            appendLine("<!DOCTYPE html>")
            appendLine("<html lang=\"en\">")
            appendLine("<head>")
            appendLine("    <meta charset=\"UTF-8\">")
            appendLine("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
            appendLine("    <title>ShareConnect AI QA Report</title>")
            appendLine("    <style>")
            appendLine(getCSS())
            appendLine("    </style>")
            appendLine("</head>")
            appendLine("<body>")
            appendLine("    <div class=\"container\">")
            appendLine("        <header>")
            appendLine("            <h1>ShareConnect AI QA Test Report</h1>")
            appendLine("            <p class=\"timestamp\">${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())}</p>")
            appendLine("        </header>")

            // Summary
            appendLine("        <section class=\"summary\">")
            appendLine("            <h2>Test Summary</h2>")
            appendLine("            <div class=\"stats\">")
            appendLine("                <div class=\"stat-card\">")
            appendLine("                    <div class=\"stat-value\">${analysis.totalTests}</div>")
            appendLine("                    <div class=\"stat-label\">Total Tests</div>")
            appendLine("                </div>")
            appendLine("                <div class=\"stat-card passed\">")
            appendLine("                    <div class=\"stat-value\">${analysis.passed}</div>")
            appendLine("                    <div class=\"stat-label\">Passed</div>")
            appendLine("                </div>")
            appendLine("                <div class=\"stat-card failed\">")
            appendLine("                    <div class=\"stat-value\">${analysis.failed}</div>")
            appendLine("                    <div class=\"stat-label\">Failed</div>")
            appendLine("                </div>")
            appendLine("                <div class=\"stat-card error\">")
            appendLine("                    <div class=\"stat-value\">${analysis.errors}</div>")
            appendLine("                    <div class=\"stat-label\">Errors</div>")
            appendLine("                </div>")
            appendLine("                <div class=\"stat-card skipped\">")
            appendLine("                    <div class=\"stat-value\">${analysis.skipped}</div>")
            appendLine("                    <div class=\"stat-label\">Skipped</div>")
            appendLine("                </div>")
            appendLine("                <div class=\"stat-card\">")
            appendLine("                    <div class=\"stat-value\">${"%.1f".format(analysis.passRate)}%</div>")
            appendLine("                    <div class=\"stat-label\">Pass Rate</div>")
            appendLine("                </div>")
            appendLine("            </div>")
            appendLine("        </section>")

            // Common Patterns
            if (analysis.commonPatterns.isNotEmpty()) {
                appendLine("        <section class=\"patterns\">")
                appendLine("            <h2>Common Patterns</h2>")
                appendLine("            <ul>")
                analysis.commonPatterns.forEach { pattern ->
                    appendLine("                <li>$pattern</li>")
                }
                appendLine("            </ul>")
                appendLine("        </section>")
            }

            // Recommendations
            if (analysis.recommendations.isNotEmpty()) {
                appendLine("        <section class=\"recommendations\">")
                appendLine("            <h2>Recommendations</h2>")
                appendLine("            <ul>")
                analysis.recommendations.forEach { rec ->
                    appendLine("                <li>$rec</li>")
                }
                appendLine("            </ul>")
                appendLine("        </section>")
            }

            // Test Results
            appendLine("        <section class=\"results\">")
            appendLine("            <h2>Test Results</h2>")
            results.forEach { (testCase, result) ->
                appendLine("            <div class=\"test-result ${result.status.name.lowercase()}\">")
                appendLine("                <div class=\"test-header\">")
                appendLine("                    <span class=\"test-status ${result.status.name.lowercase()}\">${getStatusIcon(result.status)} ${result.status.name}</span>")
                appendLine("                    <span class=\"test-name\">${testCase.name}</span>")
                appendLine("                    <span class=\"test-duration\">${result.durationMs}ms</span>")
                appendLine("                </div>")
                appendLine("                <div class=\"test-details\">")
                appendLine("                    <p><strong>ID:</strong> ${testCase.id}</p>")
                appendLine("                    <p><strong>Category:</strong> ${testCase.category}</p>")
                appendLine("                    <p><strong>Priority:</strong> ${testCase.priority}</p>")
                appendLine("                    <p><strong>Description:</strong> ${testCase.description}</p>")
                if (result.failureReason != null) {
                    appendLine("                    <p class=\"failure-reason\"><strong>Failure:</strong> ${result.failureReason}</p>")
                }
                if (result.screenshots.isNotEmpty()) {
                    appendLine("                    <p><strong>Screenshots:</strong> ${result.screenshots.size} captured</p>")
                }
                if (result.retryCount > 0) {
                    appendLine("                    <p><strong>Retries:</strong> ${result.retryCount}</p>")
                }
                appendLine("                </div>")
                appendLine("            </div>")
            }
            appendLine("        </section>")

            appendLine("    </div>")
            appendLine("</body>")
            appendLine("</html>")
        }

        File(outputPath).writeText(html)
    }

    private fun getStatusIcon(status: TestStatus): String {
        return when (status) {
            TestStatus.PASSED -> "✓"
            TestStatus.FAILED -> "✗"
            TestStatus.ERROR -> "⚠"
            TestStatus.SKIPPED -> "⊘"
            else -> "?"
        }
    }

    private fun getCSS(): String {
        return """
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                background: #f5f5f5;
                color: #333;
                line-height: 1.6;
            }

            .container {
                max-width: 1200px;
                margin: 0 auto;
                padding: 20px;
            }

            header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 40px;
                border-radius: 10px;
                margin-bottom: 30px;
                box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            }

            header h1 {
                font-size: 2.5em;
                margin-bottom: 10px;
            }

            .timestamp {
                opacity: 0.9;
                font-size: 0.9em;
            }

            section {
                background: white;
                padding: 30px;
                margin-bottom: 20px;
                border-radius: 10px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.05);
            }

            h2 {
                color: #667eea;
                margin-bottom: 20px;
                font-size: 1.8em;
            }

            .stats {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
                gap: 15px;
            }

            .stat-card {
                background: #f8f9fa;
                padding: 20px;
                border-radius: 8px;
                text-align: center;
                border-left: 4px solid #667eea;
            }

            .stat-card.passed {
                border-left-color: #10b981;
                background: #ecfdf5;
            }

            .stat-card.failed {
                border-left-color: #ef4444;
                background: #fef2f2;
            }

            .stat-card.error {
                border-left-color: #f59e0b;
                background: #fffbeb;
            }

            .stat-card.skipped {
                border-left-color: #6b7280;
                background: #f9fafb;
            }

            .stat-value {
                font-size: 2.5em;
                font-weight: bold;
                color: #333;
            }

            .stat-label {
                font-size: 0.9em;
                color: #666;
                margin-top: 5px;
            }

            .patterns ul, .recommendations ul {
                list-style: none;
            }

            .patterns li, .recommendations li {
                padding: 10px;
                margin-bottom: 10px;
                background: #f8f9fa;
                border-left: 3px solid #667eea;
                border-radius: 4px;
            }

            .test-result {
                border: 1px solid #e5e7eb;
                border-radius: 8px;
                margin-bottom: 15px;
                overflow: hidden;
            }

            .test-header {
                display: flex;
                align-items: center;
                padding: 15px;
                background: #f8f9fa;
                border-bottom: 1px solid #e5e7eb;
            }

            .test-status {
                font-weight: bold;
                padding: 5px 12px;
                border-radius: 20px;
                font-size: 0.85em;
                margin-right: 15px;
            }

            .test-status.passed {
                background: #10b981;
                color: white;
            }

            .test-status.failed {
                background: #ef4444;
                color: white;
            }

            .test-status.error {
                background: #f59e0b;
                color: white;
            }

            .test-status.skipped {
                background: #6b7280;
                color: white;
            }

            .test-name {
                flex: 1;
                font-weight: 500;
            }

            .test-duration {
                color: #666;
                font-size: 0.9em;
            }

            .test-details {
                padding: 15px;
            }

            .test-details p {
                margin-bottom: 8px;
            }

            .failure-reason {
                color: #ef4444;
                background: #fef2f2;
                padding: 10px;
                border-radius: 4px;
                margin-top: 10px;
            }
        """.trimIndent()
    }
}
