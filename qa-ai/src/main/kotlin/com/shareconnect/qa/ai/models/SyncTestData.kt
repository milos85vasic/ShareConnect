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
