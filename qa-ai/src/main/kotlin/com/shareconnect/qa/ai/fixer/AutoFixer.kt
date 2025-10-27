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


package com.shareconnect.qa.ai.fixer

import com.shareconnect.qa.ai.analyzer.TestAnalysis
import com.shareconnect.qa.ai.executor.AIClient
import com.shareconnect.qa.ai.models.TestCase
import com.shareconnect.qa.ai.models.TestResult
import kotlinx.serialization.Serializable
import java.io.File

/**
 * Automatic fix generator for test failures
 */
class AutoFixer(private val aiClient: AIClient) {

    /**
     * Generate fix suggestions for a failed test
     */
    fun generateFix(
        testCase: TestCase,
        result: TestResult,
        analysis: TestAnalysis,
        sourceCodeContext: String = ""
    ): FixSuggestion {
        val prompt = buildString {
            appendLine("=== Automated Fix Generation ===")
            appendLine()
            appendLine("Test Case: ${testCase.name}")
            appendLine("Description: ${testCase.description}")
            appendLine()
            appendLine("Failure Analysis:")
            appendLine("Root Cause: ${analysis.rootCause}")
            appendLine("Summary: ${analysis.summary}")
            appendLine()
            appendLine("Insights:")
            analysis.insights.forEach { appendLine("- $it") }
            appendLine()

            if (sourceCodeContext.isNotEmpty()) {
                appendLine("Source Code Context:")
                appendLine(sourceCodeContext)
                appendLine()
            }

            appendLine("Please provide:")
            appendLine("1. FIX_TYPE: The type of fix needed (code, config, data, infrastructure)")
            appendLine("2. AFFECTED_FILES: Which files need to be modified")
            appendLine("3. CODE_CHANGES: Specific code changes needed")
            appendLine("4. EXPLANATION: Why this fix should work")
            appendLine("5. RISK_LEVEL: LOW, MEDIUM, or HIGH")
            appendLine("6. TEST_IMPACT: What other tests might be affected")
        }

        val systemPrompt = """
            You are an expert Android developer and QA engineer.
            Generate specific, actionable fixes for test failures.
            Provide actual code snippets, not just descriptions.
            Consider edge cases and potential side effects.
        """.trimIndent()

        val response = aiClient.sendPrompt(prompt, systemPrompt, maxTokens = 3072)

        if (response.success) {
            return parsFixSuggestion(response.text, testCase.id)
        }

        return FixSuggestion(
            testCaseId = testCase.id,
            fixType = FixType.MANUAL,
            affectedFiles = emptyList(),
            codeChanges = emptyList(),
            explanation = "Unable to generate automatic fix. Manual intervention required.",
            riskLevel = RiskLevel.UNKNOWN,
            testImpact = "Unknown",
            approved = false
        )
    }

    /**
     * Parse fix suggestion from AI response
     */
    private fun parsFixSuggestion(text: String, testCaseId: String): FixSuggestion {
        val lines = text.lines()

        val fixTypeStr = lines.find { it.startsWith("FIX_TYPE:", ignoreCase = true) }
            ?.substringAfter(":")?.trim()?.uppercase()
        val fixType = when {
            fixTypeStr?.contains("CODE") == true -> FixType.CODE
            fixTypeStr?.contains("CONFIG") == true -> FixType.CONFIGURATION
            fixTypeStr?.contains("DATA") == true -> FixType.DATA
            fixTypeStr?.contains("INFRASTRUCTURE") == true -> FixType.INFRASTRUCTURE
            else -> FixType.MANUAL
        }

        val affectedFiles = lines
            .filter { it.startsWith("AFFECTED_FILES:", ignoreCase = true) || it.contains(".kt") || it.contains(".xml") }
            .map { it.removePrefix("AFFECTED_FILES:").trim() }
            .filter { it.isNotEmpty() && (it.endsWith(".kt") || it.endsWith(".xml") || it.endsWith(".gradle")) }

        val codeChanges = extractCodeChanges(text)

        val explanation = lines
            .find { it.startsWith("EXPLANATION:", ignoreCase = true) }
            ?.substringAfter(":")?.trim()
            ?: "Fix generated by AI analysis"

        val riskLevelStr = lines.find { it.startsWith("RISK_LEVEL:", ignoreCase = true) }
            ?.substringAfter(":")?.trim()?.uppercase()
        val riskLevel = when (riskLevelStr) {
            "LOW" -> RiskLevel.LOW
            "MEDIUM" -> RiskLevel.MEDIUM
            "HIGH" -> RiskLevel.HIGH
            else -> RiskLevel.MEDIUM
        }

        val testImpact = lines.find { it.startsWith("TEST_IMPACT:", ignoreCase = true) }
            ?.substringAfter(":")?.trim()
            ?: "Impact unknown"

        return FixSuggestion(
            testCaseId = testCaseId,
            fixType = fixType,
            affectedFiles = affectedFiles,
            codeChanges = codeChanges,
            explanation = explanation,
            riskLevel = riskLevel,
            testImpact = testImpact,
            approved = false
        )
    }

    /**
     * Extract code changes from text
     */
    private fun extractCodeChanges(text: String): List<CodeChange> {
        val changes = mutableListOf<CodeChange>()
        val lines = text.lines()

        var inCodeBlock = false
        var codeBlock = StringBuilder()
        var fileName: String? = null

        for (line in lines) {
            when {
                line.trim().startsWith("```") -> {
                    if (inCodeBlock) {
                        // End of code block
                        if (fileName != null && codeBlock.isNotEmpty()) {
                            changes.add(
                                CodeChange(
                                    file = fileName,
                                    oldCode = "",
                                    newCode = codeBlock.toString(),
                                    lineNumber = null
                                )
                            )
                        }
                        codeBlock = StringBuilder()
                        fileName = null
                    }
                    inCodeBlock = !inCodeBlock
                }
                inCodeBlock -> {
                    codeBlock.appendLine(line)
                }
                line.contains(".kt") || line.contains(".xml") -> {
                    fileName = line.trim()
                }
            }
        }

        return changes
    }

    /**
     * Apply fix (with approval)
     */
    fun applyFix(fix: FixSuggestion, dryRun: Boolean = true): FixApplicationResult {
        if (!fix.approved) {
            return FixApplicationResult(
                success = false,
                message = "Fix not approved. Cannot apply unapproved fixes.",
                filesModified = emptyList()
            )
        }

        if (dryRun) {
            return FixApplicationResult(
                success = true,
                message = "Dry run successful. Fix can be applied.",
                filesModified = fix.affectedFiles
            )
        }

        val modifiedFiles = mutableListOf<String>()

        try {
            fix.codeChanges.forEach { change ->
                val file = File(change.file)
                if (file.exists()) {
                    // Backup original file
                    val backupFile = File("${file.absolutePath}.backup")
                    file.copyTo(backupFile, overwrite = true)

                    // Apply change (simplified - in production, use proper diff/patch)
                    if (change.oldCode.isNotEmpty()) {
                        val content = file.readText()
                        val newContent = content.replace(change.oldCode, change.newCode)
                        file.writeText(newContent)
                    } else {
                        // Append new code
                        file.appendText("\n${change.newCode}\n")
                    }

                    modifiedFiles.add(file.absolutePath)
                }
            }

            return FixApplicationResult(
                success = true,
                message = "Fix applied successfully to ${modifiedFiles.size} file(s)",
                filesModified = modifiedFiles
            )

        } catch (e: Exception) {
            return FixApplicationResult(
                success = false,
                message = "Error applying fix: ${e.message}",
                filesModified = modifiedFiles
            )
        }
    }

    /**
     * Validate fix before applying
     */
    fun validateFix(fix: FixSuggestion): ValidationResult {
        val issues = mutableListOf<String>()

        // Check if files exist
        fix.affectedFiles.forEach { filePath ->
            if (!File(filePath).exists()) {
                issues.add("File not found: $filePath")
            }
        }

        // Check risk level
        if (fix.riskLevel == RiskLevel.HIGH) {
            issues.add("High risk fix - requires manual review")
        }

        // Check for code quality
        fix.codeChanges.forEach { change ->
            if (change.newCode.contains("TODO") || change.newCode.contains("FIXME")) {
                issues.add("Code contains unresolved TODOs or FIXMEs")
            }
        }

        return ValidationResult(
            valid = issues.isEmpty(),
            issues = issues
        )
    }
}

@Serializable
enum class FixType {
    CODE,
    CONFIGURATION,
    DATA,
    INFRASTRUCTURE,
    MANUAL
}

@Serializable
enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    UNKNOWN
}

@Serializable
data class FixSuggestion(
    val testCaseId: String,
    val fixType: FixType,
    val affectedFiles: List<String>,
    val codeChanges: List<CodeChange>,
    val explanation: String,
    val riskLevel: RiskLevel,
    val testImpact: String,
    var approved: Boolean = false
)

@Serializable
data class CodeChange(
    val file: String,
    val oldCode: String,
    val newCode: String,
    val lineNumber: Int? = null
)

data class FixApplicationResult(
    val success: Boolean,
    val message: String,
    val filesModified: List<String>
)

data class ValidationResult(
    val valid: Boolean,
    val issues: List<String>
)
