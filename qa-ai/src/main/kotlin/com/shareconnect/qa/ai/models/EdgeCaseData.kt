package com.shareconnect.qa.ai.models

import kotlinx.serialization.Serializable

/**
 * Edge case test data
 */
@Serializable
data class EdgeCaseTestData(
    val id: String,
    val category: EdgeCaseCategory,
    val description: String,
    val input: Map<String, String>,
    val expectedBehavior: EdgeCaseBehavior,
    val severity: Severity = Severity.MEDIUM
)

@Serializable
enum class EdgeCaseCategory {
    INVALID_INPUT,
    BOUNDARY_VALUE,
    SPECIAL_CHARACTERS,
    MALFORMED_DATA,
    EXTREME_VALUES,
    NULL_VALUES,
    RACE_CONDITION,
    MEMORY_PRESSURE,
    NETWORK_FAILURE,
    DATABASE_ERROR
}

@Serializable
enum class EdgeCaseBehavior {
    REJECT_WITH_ERROR,
    HANDLE_GRACEFULLY,
    SANITIZE_INPUT,
    FALLBACK_TO_DEFAULT,
    RETRY_OPERATION,
    SHOW_USER_MESSAGE
}

@Serializable
enum class Severity {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW
}
