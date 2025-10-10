package com.shareconnect.qa.ai.models

import kotlinx.serialization.Serializable

/**
 * Test case definition from test bank
 */
@Serializable
data class TestCase(
    val id: String,
    val name: String,
    val description: String,
    val category: TestCategory,
    val priority: Priority,
    val tags: List<String> = emptyList(),
    val preconditions: List<String> = emptyList(),
    val steps: List<TestStep>,
    val expectedResults: List<String>,
    val postconditions: List<String> = emptyList(),
    val dataRequirements: List<String> = emptyList(),
    val estimatedDurationMs: Long = 30000,
    val retryOnFailure: Boolean = true,
    val maxRetries: Int = 2
)

@Serializable
enum class TestCategory {
    PROFILE_MANAGEMENT,
    SYNC_FUNCTIONALITY,
    UI_FLOW,
    EDGE_CASE,
    INTEGRATION,
    PERFORMANCE,
    SECURITY,
    ACCESSIBILITY,
    ONBOARDING,
    SETTINGS
}

@Serializable
enum class Priority {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW
}

@Serializable
data class TestStep(
    val stepNumber: Int,
    val action: String,
    val description: String,
    val target: String? = null,
    val input: Map<String, String> = emptyMap(),
    val expectedOutcome: String,
    val screenshot: Boolean = false,
    val waitAfterMs: Long = 0
)

/**
 * Test suite containing multiple test cases
 */
@Serializable
data class TestSuite(
    val id: String,
    val name: String,
    val description: String,
    val testCases: List<String>, // Test case IDs
    val setupSteps: List<String> = emptyList(),
    val teardownSteps: List<String> = emptyList()
)

/**
 * Test execution result
 */
@Serializable
data class TestResult(
    val testCaseId: String,
    val status: TestStatus,
    val startTime: Long,
    val endTime: Long,
    val durationMs: Long,
    val stepResults: List<StepResult>,
    val failureReason: String? = null,
    val screenshots: List<String> = emptyList(),
    val logs: List<String> = emptyList(),
    val retryCount: Int = 0
)

@Serializable
enum class TestStatus {
    PASSED,
    FAILED,
    SKIPPED,
    ERROR,
    IN_PROGRESS
}

@Serializable
data class StepResult(
    val stepNumber: Int,
    val status: TestStatus,
    val actualOutcome: String,
    val screenshot: String? = null,
    val errorMessage: String? = null
)
