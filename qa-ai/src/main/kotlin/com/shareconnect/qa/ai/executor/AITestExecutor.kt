package com.shareconnect.qa.ai.executor

import android.graphics.Bitmap
import android.util.Base64
import com.shareconnect.qa.ai.emulator.EmulatorBridge
import com.shareconnect.qa.ai.models.*
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * AI-powered test executor that interprets and executes test cases
 */
class AITestExecutor(
    private val aiClient: AIClient,
    private val emulatorBridge: EmulatorBridge
) {

    private val systemPrompt = """
        You are an expert QA engineer specialized in Android app testing.
        Your task is to analyze test steps and device screenshots to determine if the app behaves correctly.

        When analyzing a test step:
        1. Understand the expected outcome
        2. Examine the screenshot (if provided)
        3. Determine if the actual outcome matches the expected outcome
        4. Provide clear reasoning for your decision
        5. Suggest fixes if the test fails

        Always respond in a structured format:
        - STATUS: PASS/FAIL/ERROR
        - REASON: Brief explanation
        - DETAILS: Detailed analysis
        - SUGGESTION: (if failed) How to fix the issue
    """.trimIndent()

    /**
     * Execute a single test case
     */
    suspend fun executeTestCase(testCase: TestCase): TestResult {
        val startTime = System.currentTimeMillis()
        val stepResults = mutableListOf<StepResult>()
        val screenshots = mutableListOf<String>()
        var failureReason: String? = null
        var retryCount = 0

        println("Executing test case: ${testCase.name}")

        try {
            // Check preconditions
            if (!checkPreconditions(testCase.preconditions)) {
                return TestResult(
                    testCaseId = testCase.id,
                    status = TestStatus.SKIPPED,
                    startTime = startTime,
                    endTime = System.currentTimeMillis(),
                    durationMs = System.currentTimeMillis() - startTime,
                    stepResults = emptyList(),
                    failureReason = "Preconditions not met"
                )
            }

            // Execute each step
            for (step in testCase.steps) {
                println("  Step ${step.stepNumber}: ${step.description}")

                val stepResult = executeTestStep(step, testCase)
                stepResults.add(stepResult)

                if (stepResult.screenshot != null) {
                    screenshots.add(stepResult.screenshot)
                }

                if (stepResult.status == TestStatus.FAILED) {
                    failureReason = stepResult.errorMessage ?: "Step ${step.stepNumber} failed"

                    // Retry logic
                    if (testCase.retryOnFailure && retryCount < testCase.maxRetries) {
                        retryCount++
                        println("  Retrying test case (attempt $retryCount/${testCase.maxRetries})...")
                        delay(1000)
                        continue
                    }

                    break
                }

                // Wait after step if specified
                if (step.waitAfterMs > 0) {
                    delay(step.waitAfterMs)
                }
            }

            // Verify expected results
            val allStepsPassed = stepResults.all { it.status == TestStatus.PASSED }
            val finalStatus = if (allStepsPassed) TestStatus.PASSED else TestStatus.FAILED

            return TestResult(
                testCaseId = testCase.id,
                status = finalStatus,
                startTime = startTime,
                endTime = System.currentTimeMillis(),
                durationMs = System.currentTimeMillis() - startTime,
                stepResults = stepResults,
                failureReason = failureReason,
                screenshots = screenshots,
                retryCount = retryCount
            )

        } catch (e: Exception) {
            return TestResult(
                testCaseId = testCase.id,
                status = TestStatus.ERROR,
                startTime = startTime,
                endTime = System.currentTimeMillis(),
                durationMs = System.currentTimeMillis() - startTime,
                stepResults = stepResults,
                failureReason = "Exception: ${e.message}",
                screenshots = screenshots,
                retryCount = retryCount
            )
        }
    }

    /**
     * Execute a single test step
     */
    private suspend fun executeTestStep(step: TestStep, testCase: TestCase): StepResult {
        try {
            // Perform the action
            val actionResult = when (step.action.lowercase()) {
                "tap" -> emulatorBridge.tap(step.target ?: "")
                "long_press" -> emulatorBridge.longPress(step.target ?: "")
                "input" -> emulatorBridge.input(step.target ?: "", step.input["text"] ?: "")
                "select" -> emulatorBridge.select(step.target ?: "", step.input["value"] ?: "")
                "swipe" -> emulatorBridge.swipe(step.input["direction"] ?: "up")
                "navigate" -> emulatorBridge.navigate(step.target ?: "")
                "verify" -> true // Verification is done via AI
                "wait" -> {
                    delay(step.waitAfterMs)
                    true
                }
                else -> {
                    // Use AI to interpret unknown actions
                    interpretActionWithAI(step)
                }
            }

            // Capture screenshot if needed
            var screenshotPath: String? = null
            if (step.screenshot) {
                screenshotPath = captureAndSaveScreenshot(testCase.id, step.stepNumber)
            }

            // Verify outcome with AI
            val verificationResult = verifyStepOutcome(step, screenshotPath)

            return StepResult(
                stepNumber = step.stepNumber,
                status = if (verificationResult.success) TestStatus.PASSED else TestStatus.FAILED,
                actualOutcome = verificationResult.actualOutcome,
                screenshot = screenshotPath,
                errorMessage = verificationResult.errorMessage
            )

        } catch (e: Exception) {
            return StepResult(
                stepNumber = step.stepNumber,
                status = TestStatus.ERROR,
                actualOutcome = "Error executing step",
                errorMessage = e.message
            )
        }
    }

    /**
     * Interpret and execute an action using AI
     */
    private suspend fun interpretActionWithAI(step: TestStep): Boolean {
        val prompt = """
            I need to perform the following test step on an Android app:

            Action: ${step.action}
            Description: ${step.description}
            Target: ${step.target ?: "N/A"}
            Input: ${step.input}
            Expected Outcome: ${step.expectedOutcome}

            Please provide the exact UI Automator or Espresso commands needed to perform this action.
        """.trimIndent()

        val response = aiClient.sendPrompt(prompt, systemPrompt)

        if (response.success) {
            println("    AI interpretation: ${response.text}")
            // Parse AI response and execute the suggested commands
            // This is a simplified version - in production, you'd parse and execute the commands
            return true
        }

        return false
    }

    /**
     * Verify step outcome using AI
     */
    private suspend fun verifyStepOutcome(step: TestStep, screenshotPath: String?): VerificationResult {
        val prompt = buildString {
            appendLine("Test Step Verification:")
            appendLine()
            appendLine("Step ${step.stepNumber}: ${step.description}")
            appendLine("Action: ${step.action}")
            appendLine("Expected Outcome: ${step.expectedOutcome}")
            appendLine()
            appendLine("Please analyze the screenshot (if provided) and determine if the expected outcome was achieved.")
            appendLine("Respond in the following format:")
            appendLine("STATUS: PASS/FAIL")
            appendLine("ACTUAL_OUTCOME: <what you observe>")
            appendLine("REASON: <brief explanation>")
        }

        val response = if (screenshotPath != null) {
            val screenshotBase64 = convertScreenshotToBase64(screenshotPath)
            aiClient.analyzeScreenshot(screenshotBase64, prompt, "Android app UI test")
        } else {
            aiClient.sendPrompt(prompt, systemPrompt)
        }

        if (response.success) {
            val text = response.text
            val status = when {
                text.contains("STATUS: PASS", ignoreCase = true) -> true
                text.contains("STATUS: FAIL", ignoreCase = true) -> false
                else -> {
                    // If AI doesn't give clear status, try to infer from content
                    text.contains("expected outcome", ignoreCase = true) &&
                    !text.contains("not", ignoreCase = true)
                }
            }

            val actualOutcome = text.lines()
                .find { it.startsWith("ACTUAL_OUTCOME:", ignoreCase = true) }
                ?.substringAfter(":")?.trim()
                ?: "AI analysis completed"

            val reason = text.lines()
                .find { it.startsWith("REASON:", ignoreCase = true) }
                ?.substringAfter(":")?.trim()

            return VerificationResult(
                success = status,
                actualOutcome = actualOutcome,
                errorMessage = if (!status) reason else null
            )
        }

        return VerificationResult(
            success = false,
            actualOutcome = "AI verification failed",
            errorMessage = response.error
        )
    }

    /**
     * Check preconditions
     */
    private fun checkPreconditions(preconditions: List<String>): Boolean {
        // Simplified - in production, you'd actually verify each precondition
        println("  Checking preconditions: $preconditions")
        return true
    }

    /**
     * Capture and save screenshot
     */
    private fun captureAndSaveScreenshot(testCaseId: String, stepNumber: Int): String {
        val screenshot = emulatorBridge.captureScreenshot()
        val screenshotDir = File("qa-ai/screenshots/$testCaseId")
        screenshotDir.mkdirs()

        val screenshotFile = File(screenshotDir, "step_$stepNumber.png")
        screenshot.compress(Bitmap.CompressFormat.PNG, 100, screenshotFile.outputStream())

        return screenshotFile.absolutePath
    }

    /**
     * Convert screenshot to base64 for AI analysis
     */
    private fun convertScreenshotToBase64(screenshotPath: String): String {
        val file = File(screenshotPath)
        val bitmap = android.graphics.BitmapFactory.decodeFile(screenshotPath)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}

/**
 * Verification result
 */
private data class VerificationResult(
    val success: Boolean,
    val actualOutcome: String,
    val errorMessage: String? = null
)
