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
 * Sync test scenario data
 */
@Serializable
data class SyncTestScenario(
    val id: String,
    val name: String,
    val description: String,
    val syncType: SyncType,
    val deviceCount: Int = 2,
    val networkCondition: NetworkCondition = NetworkCondition.NORMAL,
    val conflictType: ConflictType? = null,
    val expectedBehavior: String,
    val testSteps: List<SyncTestStep>
)

@Serializable
enum class SyncType {
    THEME,
    PROFILE,
    LANGUAGE,
    HISTORY,
    RSS,
    BOOKMARK,
    PREFERENCES,
    TORRENT_SHARING
}

@Serializable
enum class NetworkCondition {
    NORMAL,
    SLOW_3G,
    FAST_3G,
    LTE_4G,
    WIFI,
    OFFLINE,
    INTERMITTENT
}

@Serializable
enum class ConflictType {
    SIMULTANEOUS_UPDATE,
    OFFLINE_UPDATE,
    VERSION_CONFLICT,
    DELETE_UPDATE_CONFLICT
}

@Serializable
data class SyncTestStep(
    val deviceId: String,
    val action: String,
    val data: Map<String, String> = emptyMap(),
    val delayMs: Long = 0,
    val expectedResult: String = ""
)
