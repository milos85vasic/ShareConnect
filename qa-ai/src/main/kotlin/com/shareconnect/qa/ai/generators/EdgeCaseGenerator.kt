package com.shareconnect.qa.ai.generators

import com.shareconnect.qa.ai.models.*
import java.util.UUID

/**
 * Generates edge case test data
 */
class EdgeCaseGenerator {

    fun generateAllEdgeCases(): List<EdgeCaseTestData> {
        val edgeCases = mutableListOf<EdgeCaseTestData>()

        edgeCases.addAll(generateInvalidInputCases())
        edgeCases.addAll(generateBoundaryValueCases())
        edgeCases.addAll(generateSpecialCharacterCases())
        edgeCases.addAll(generateMalformedDataCases())
        edgeCases.addAll(generateExtremeValueCases())
        edgeCases.addAll(generateNullValueCases())
        edgeCases.addAll(generateRaceConditionCases())
        edgeCases.addAll(generateNetworkFailureCases())

        return edgeCases
    }

    private fun generateInvalidInputCases(): List<EdgeCaseTestData> {
        return listOf(
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.INVALID_INPUT,
                description = "Profile with invalid URL format",
                input = mapOf(
                    "name" to "Test Profile",
                    "url" to "not-a-valid-url",
                    "port" to "8081"
                ),
                expectedBehavior = EdgeCaseBehavior.REJECT_WITH_ERROR,
                severity = Severity.HIGH
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.INVALID_INPUT,
                description = "Profile with invalid port number",
                input = mapOf(
                    "name" to "Test Profile",
                    "url" to "http://localhost",
                    "port" to "99999"
                ),
                expectedBehavior = EdgeCaseBehavior.REJECT_WITH_ERROR,
                severity = Severity.MEDIUM
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.INVALID_INPUT,
                description = "Profile with negative port number",
                input = mapOf(
                    "name" to "Test Profile",
                    "url" to "http://localhost",
                    "port" to "-1"
                ),
                expectedBehavior = EdgeCaseBehavior.REJECT_WITH_ERROR,
                severity = Severity.MEDIUM
            )
        )
    }

    private fun generateBoundaryValueCases(): List<EdgeCaseTestData> {
        return listOf(
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.BOUNDARY_VALUE,
                description = "Profile with minimum valid port (1)",
                input = mapOf(
                    "name" to "Min Port Profile",
                    "url" to "http://localhost",
                    "port" to "1"
                ),
                expectedBehavior = EdgeCaseBehavior.HANDLE_GRACEFULLY,
                severity = Severity.LOW
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.BOUNDARY_VALUE,
                description = "Profile with maximum valid port (65535)",
                input = mapOf(
                    "name" to "Max Port Profile",
                    "url" to "http://localhost",
                    "port" to "65535"
                ),
                expectedBehavior = EdgeCaseBehavior.HANDLE_GRACEFULLY,
                severity = Severity.LOW
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.BOUNDARY_VALUE,
                description = "Profile name with maximum length (256 characters)",
                input = mapOf(
                    "name" to "A".repeat(256),
                    "url" to "http://localhost",
                    "port" to "8081"
                ),
                expectedBehavior = EdgeCaseBehavior.SANITIZE_INPUT,
                severity = Severity.MEDIUM
            )
        )
    }

    private fun generateSpecialCharacterCases(): List<EdgeCaseTestData> {
        return listOf(
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.SPECIAL_CHARACTERS,
                description = "Profile name with special characters",
                input = mapOf(
                    "name" to "Test@Profile#123!",
                    "url" to "http://localhost",
                    "port" to "8081"
                ),
                expectedBehavior = EdgeCaseBehavior.SANITIZE_INPUT,
                severity = Severity.LOW
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.SPECIAL_CHARACTERS,
                description = "Profile name with Unicode characters",
                input = mapOf(
                    "name" to "测试配置文件 🚀",
                    "url" to "http://localhost",
                    "port" to "8081"
                ),
                expectedBehavior = EdgeCaseBehavior.HANDLE_GRACEFULLY,
                severity = Severity.LOW
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.SPECIAL_CHARACTERS,
                description = "URL with special characters in path",
                input = mapOf(
                    "name" to "Special URL",
                    "url" to "http://localhost/path%20with%20spaces",
                    "port" to "8081"
                ),
                expectedBehavior = EdgeCaseBehavior.HANDLE_GRACEFULLY,
                severity = Severity.MEDIUM
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.SPECIAL_CHARACTERS,
                description = "SQL injection attempt in profile name",
                input = mapOf(
                    "name" to "'; DROP TABLE profiles; --",
                    "url" to "http://localhost",
                    "port" to "8081"
                ),
                expectedBehavior = EdgeCaseBehavior.SANITIZE_INPUT,
                severity = Severity.CRITICAL
            )
        )
    }

    private fun generateMalformedDataCases(): List<EdgeCaseTestData> {
        return listOf(
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.MALFORMED_DATA,
                description = "Profile with malformed JSON",
                input = mapOf(
                    "data" to "{name: 'Test', url: incomplete"
                ),
                expectedBehavior = EdgeCaseBehavior.REJECT_WITH_ERROR,
                severity = Severity.HIGH
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.MALFORMED_DATA,
                description = "URL with missing protocol",
                input = mapOf(
                    "name" to "No Protocol",
                    "url" to "localhost:8081",
                    "port" to "8081"
                ),
                expectedBehavior = EdgeCaseBehavior.SANITIZE_INPUT,
                severity = Severity.MEDIUM
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.MALFORMED_DATA,
                description = "Port as string instead of number",
                input = mapOf(
                    "name" to "String Port",
                    "url" to "http://localhost",
                    "port" to "eight-thousand"
                ),
                expectedBehavior = EdgeCaseBehavior.REJECT_WITH_ERROR,
                severity = Severity.MEDIUM
            )
        )
    }

    private fun generateExtremeValueCases(): List<EdgeCaseTestData> {
        return listOf(
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.EXTREME_VALUES,
                description = "Profile name with 10,000 characters",
                input = mapOf(
                    "name" to "A".repeat(10000),
                    "url" to "http://localhost",
                    "port" to "8081"
                ),
                expectedBehavior = EdgeCaseBehavior.REJECT_WITH_ERROR,
                severity = Severity.MEDIUM
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.EXTREME_VALUES,
                description = "Create 1000 profiles simultaneously",
                input = mapOf(
                    "action" to "create_bulk",
                    "count" to "1000"
                ),
                expectedBehavior = EdgeCaseBehavior.HANDLE_GRACEFULLY,
                severity = Severity.HIGH
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.EXTREME_VALUES,
                description = "URL with extremely long domain name",
                input = mapOf(
                    "name" to "Long Domain",
                    "url" to "http://" + "subdomain.".repeat(50) + "example.com",
                    "port" to "8081"
                ),
                expectedBehavior = EdgeCaseBehavior.SANITIZE_INPUT,
                severity = Severity.MEDIUM
            )
        )
    }

    private fun generateNullValueCases(): List<EdgeCaseTestData> {
        return listOf(
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.NULL_VALUES,
                description = "Profile with null name",
                input = mapOf(
                    "name" to "null",
                    "url" to "http://localhost",
                    "port" to "8081"
                ),
                expectedBehavior = EdgeCaseBehavior.REJECT_WITH_ERROR,
                severity = Severity.HIGH
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.NULL_VALUES,
                description = "Profile with null URL",
                input = mapOf(
                    "name" to "Test",
                    "url" to "null",
                    "port" to "8081"
                ),
                expectedBehavior = EdgeCaseBehavior.REJECT_WITH_ERROR,
                severity = Severity.HIGH
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.NULL_VALUES,
                description = "Profile with empty string values",
                input = mapOf(
                    "name" to "",
                    "url" to "",
                    "port" to ""
                ),
                expectedBehavior = EdgeCaseBehavior.REJECT_WITH_ERROR,
                severity = Severity.MEDIUM
            )
        )
    }

    private fun generateRaceConditionCases(): List<EdgeCaseTestData> {
        return listOf(
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.RACE_CONDITION,
                description = "Simultaneous profile creation with same name",
                input = mapOf(
                    "action" to "simultaneous_create",
                    "name" to "Duplicate Profile",
                    "threads" to "5"
                ),
                expectedBehavior = EdgeCaseBehavior.HANDLE_GRACEFULLY,
                severity = Severity.MEDIUM
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.RACE_CONDITION,
                description = "Simultaneous set default on different profiles",
                input = mapOf(
                    "action" to "simultaneous_set_default",
                    "profiles" to "3"
                ),
                expectedBehavior = EdgeCaseBehavior.HANDLE_GRACEFULLY,
                severity = Severity.HIGH
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.RACE_CONDITION,
                description = "Edit and delete same profile simultaneously",
                input = mapOf(
                    "action" to "simultaneous_edit_delete",
                    "profileId" to "test-profile-1"
                ),
                expectedBehavior = EdgeCaseBehavior.HANDLE_GRACEFULLY,
                severity = Severity.HIGH
            )
        )
    }

    private fun generateNetworkFailureCases(): List<EdgeCaseTestData> {
        return listOf(
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.NETWORK_FAILURE,
                description = "Profile sync during network disconnection",
                input = mapOf(
                    "action" to "sync_offline",
                    "syncType" to "profiles"
                ),
                expectedBehavior = EdgeCaseBehavior.RETRY_OPERATION,
                severity = Severity.HIGH
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.NETWORK_FAILURE,
                description = "Profile creation with intermittent network",
                input = mapOf(
                    "action" to "create_with_intermittent_network",
                    "dropRate" to "50"
                ),
                expectedBehavior = EdgeCaseBehavior.RETRY_OPERATION,
                severity = Severity.MEDIUM
            ),
            EdgeCaseTestData(
                id = UUID.randomUUID().toString(),
                category = EdgeCaseCategory.NETWORK_FAILURE,
                description = "Sync timeout scenario",
                input = mapOf(
                    "action" to "sync_with_timeout",
                    "timeoutMs" to "1000"
                ),
                expectedBehavior = EdgeCaseBehavior.SHOW_USER_MESSAGE,
                severity = Severity.MEDIUM
            )
        )
    }

    /**
     * Generate edge case profiles for testing
     */
    fun generateEdgeCaseProfiles(): List<TestProfile> {
        return listOf(
            // SQL injection attempt
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "'; DROP TABLE profiles; --",
                url = "http://localhost",
                port = 8081,
                serviceType = ServiceType.METUBE,
                isEdgeCase = true,
                edgeCaseType = "sql_injection",
                description = "SQL injection attempt in profile name"
            ),
            // XSS attempt
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "<script>alert('XSS')</script>",
                url = "http://localhost",
                port = 8081,
                serviceType = ServiceType.METUBE,
                isEdgeCase = true,
                edgeCaseType = "xss_attempt",
                description = "XSS attempt in profile name"
            ),
            // Extremely long name
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "A".repeat(10000),
                url = "http://localhost",
                port = 8081,
                serviceType = ServiceType.METUBE,
                isEdgeCase = true,
                edgeCaseType = "extreme_length",
                description = "Extremely long profile name"
            ),
            // Invalid port
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "Invalid Port",
                url = "http://localhost",
                port = 99999,
                serviceType = ServiceType.METUBE,
                isEdgeCase = true,
                edgeCaseType = "invalid_port",
                description = "Port number exceeds valid range"
            ),
            // Unicode name
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "测试配置 🚀 Тест",
                url = "http://localhost",
                port = 8081,
                serviceType = ServiceType.METUBE,
                isEdgeCase = true,
                edgeCaseType = "unicode",
                description = "Profile name with various Unicode characters"
            )
        )
    }
}
