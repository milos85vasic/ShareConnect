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
