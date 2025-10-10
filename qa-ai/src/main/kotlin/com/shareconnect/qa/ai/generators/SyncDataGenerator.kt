package com.shareconnect.qa.ai.generators

import com.shareconnect.qa.ai.models.*
import java.util.UUID

/**
 * Generates sync test scenarios
 */
class SyncDataGenerator {

    fun generateAllSyncScenarios(): List<SyncTestScenario> {
        val scenarios = mutableListOf<SyncTestScenario>()

        // Generate scenarios for each sync type
        SyncType.values().forEach { syncType ->
            scenarios.addAll(generateScenariosForType(syncType))
        }

        // Generate multi-sync scenarios
        scenarios.addAll(generateMultiSyncScenarios())

        // Generate conflict scenarios
        scenarios.addAll(generateConflictScenarios())

        return scenarios
    }

    private fun generateScenariosForType(syncType: SyncType): List<SyncTestScenario> {
        val scenarios = mutableListOf<SyncTestScenario>()

        // Basic sync scenario
        scenarios.add(
            SyncTestScenario(
                id = UUID.randomUUID().toString(),
                name = "Basic ${syncType.name} Sync",
                description = "Test basic ${syncType.name} synchronization between 2 devices",
                syncType = syncType,
                deviceCount = 2,
                networkCondition = NetworkCondition.NORMAL,
                expectedBehavior = "Changes from device 1 should sync to device 2",
                testSteps = listOf(
                    SyncTestStep(
                        deviceId = "device1",
                        action = "create_${syncType.name.lowercase()}",
                        data = mapOf("name" to "Test ${syncType.name}"),
                        expectedResult = "${syncType.name} created successfully"
                    ),
                    SyncTestStep(
                        deviceId = "device1",
                        action = "wait_for_sync",
                        delayMs = 2000
                    ),
                    SyncTestStep(
                        deviceId = "device2",
                        action = "verify_${syncType.name.lowercase()}_exists",
                        data = mapOf("name" to "Test ${syncType.name}"),
                        expectedResult = "${syncType.name} should exist on device 2"
                    )
                )
            )
        )

        // Offline sync scenario
        scenarios.add(
            SyncTestScenario(
                id = UUID.randomUUID().toString(),
                name = "${syncType.name} Offline Sync",
                description = "Test ${syncType.name} sync when device goes offline",
                syncType = syncType,
                deviceCount = 2,
                networkCondition = NetworkCondition.OFFLINE,
                expectedBehavior = "Changes should queue and sync when online",
                testSteps = listOf(
                    SyncTestStep(
                        deviceId = "device1",
                        action = "set_network_offline"
                    ),
                    SyncTestStep(
                        deviceId = "device1",
                        action = "create_${syncType.name.lowercase()}",
                        data = mapOf("name" to "Offline ${syncType.name}")
                    ),
                    SyncTestStep(
                        deviceId = "device1",
                        action = "set_network_online"
                    ),
                    SyncTestStep(
                        deviceId = "device1",
                        action = "wait_for_sync",
                        delayMs = 3000
                    ),
                    SyncTestStep(
                        deviceId = "device2",
                        action = "verify_${syncType.name.lowercase()}_exists",
                        data = mapOf("name" to "Offline ${syncType.name}"),
                        expectedResult = "${syncType.name} should sync after coming online"
                    )
                )
            )
        )

        // Slow network scenario
        scenarios.add(
            SyncTestScenario(
                id = UUID.randomUUID().toString(),
                name = "${syncType.name} Slow Network Sync",
                description = "Test ${syncType.name} sync on slow 3G network",
                syncType = syncType,
                deviceCount = 2,
                networkCondition = NetworkCondition.SLOW_3G,
                expectedBehavior = "Sync should complete despite slow network",
                testSteps = listOf(
                    SyncTestStep(
                        deviceId = "device1",
                        action = "set_network_slow_3g"
                    ),
                    SyncTestStep(
                        deviceId = "device1",
                        action = "create_${syncType.name.lowercase()}",
                        data = mapOf("name" to "Slow Network ${syncType.name}")
                    ),
                    SyncTestStep(
                        deviceId = "device1",
                        action = "wait_for_sync",
                        delayMs = 10000
                    ),
                    SyncTestStep(
                        deviceId = "device2",
                        action = "verify_${syncType.name.lowercase()}_exists",
                        data = mapOf("name" to "Slow Network ${syncType.name}"),
                        expectedResult = "${syncType.name} should sync on slow network"
                    )
                )
            )
        )

        // Multi-device sync
        scenarios.add(
            SyncTestScenario(
                id = UUID.randomUUID().toString(),
                name = "${syncType.name} Multi-Device Sync",
                description = "Test ${syncType.name} sync across 3 devices",
                syncType = syncType,
                deviceCount = 3,
                networkCondition = NetworkCondition.NORMAL,
                expectedBehavior = "Changes should sync to all devices",
                testSteps = listOf(
                    SyncTestStep(
                        deviceId = "device1",
                        action = "create_${syncType.name.lowercase()}",
                        data = mapOf("name" to "Multi-Device ${syncType.name}")
                    ),
                    SyncTestStep(
                        deviceId = "device1",
                        action = "wait_for_sync",
                        delayMs = 2000
                    ),
                    SyncTestStep(
                        deviceId = "device2",
                        action = "verify_${syncType.name.lowercase()}_exists",
                        data = mapOf("name" to "Multi-Device ${syncType.name}"),
                        expectedResult = "${syncType.name} should exist on device 2"
                    ),
                    SyncTestStep(
                        deviceId = "device3",
                        action = "verify_${syncType.name.lowercase()}_exists",
                        data = mapOf("name" to "Multi-Device ${syncType.name}"),
                        expectedResult = "${syncType.name} should exist on device 3"
                    )
                )
            )
        )

        return scenarios
    }

    private fun generateMultiSyncScenarios(): List<SyncTestScenario> {
        return listOf(
            SyncTestScenario(
                id = UUID.randomUUID().toString(),
                name = "All Sync Types Simultaneously",
                description = "Test all sync types working simultaneously",
                syncType = SyncType.PROFILE, // Primary type
                deviceCount = 2,
                networkCondition = NetworkCondition.NORMAL,
                expectedBehavior = "All sync types should work without interference",
                testSteps = listOf(
                    SyncTestStep("device1", "create_theme", mapOf("name" to "Test Theme")),
                    SyncTestStep("device1", "create_profile", mapOf("name" to "Test Profile")),
                    SyncTestStep("device1", "create_language", mapOf("code" to "es")),
                    SyncTestStep("device1", "create_bookmark", mapOf("url" to "http://test.com")),
                    SyncTestStep("device1", "wait_for_sync", delayMs = 3000),
                    SyncTestStep("device2", "verify_all_synced", expectedResult = "All items should be synced")
                )
            )
        )
    }

    private fun generateConflictScenarios(): List<SyncTestScenario> {
        val scenarios = mutableListOf<SyncTestScenario>()

        // Simultaneous update conflict
        scenarios.add(
            SyncTestScenario(
                id = UUID.randomUUID().toString(),
                name = "Simultaneous Update Conflict",
                description = "Test conflict resolution when same item is updated on 2 devices simultaneously",
                syncType = SyncType.PROFILE,
                deviceCount = 2,
                networkCondition = NetworkCondition.NORMAL,
                conflictType = ConflictType.SIMULTANEOUS_UPDATE,
                expectedBehavior = "Conflict should be resolved with last-write-wins or version merge",
                testSteps = listOf(
                    SyncTestStep("device1", "create_profile", mapOf("id" to "shared-profile", "name" to "Original")),
                    SyncTestStep("device1", "wait_for_sync", delayMs = 2000),
                    SyncTestStep("device2", "verify_profile_exists", mapOf("id" to "shared-profile")),
                    SyncTestStep("device1", "update_profile", mapOf("id" to "shared-profile", "name" to "Updated Device 1")),
                    SyncTestStep("device2", "update_profile", mapOf("id" to "shared-profile", "name" to "Updated Device 2"), delayMs = 100),
                    SyncTestStep("device1", "wait_for_sync", delayMs = 3000),
                    SyncTestStep("device2", "verify_conflict_resolved", expectedResult = "Conflict should be resolved")
                )
            )
        )

        // Offline update conflict
        scenarios.add(
            SyncTestScenario(
                id = UUID.randomUUID().toString(),
                name = "Offline Update Conflict",
                description = "Test conflict when device updates while offline",
                syncType = SyncType.PROFILE,
                deviceCount = 2,
                networkCondition = NetworkCondition.OFFLINE,
                conflictType = ConflictType.OFFLINE_UPDATE,
                expectedBehavior = "Changes should merge when device comes online",
                testSteps = listOf(
                    SyncTestStep("device1", "create_profile", mapOf("id" to "conflict-profile", "name" to "Original")),
                    SyncTestStep("device1", "wait_for_sync", delayMs = 2000),
                    SyncTestStep("device2", "set_network_offline"),
                    SyncTestStep("device2", "update_profile", mapOf("id" to "conflict-profile", "name" to "Offline Update")),
                    SyncTestStep("device1", "update_profile", mapOf("id" to "conflict-profile", "name" to "Online Update")),
                    SyncTestStep("device2", "set_network_online"),
                    SyncTestStep("device2", "wait_for_sync", delayMs = 3000),
                    SyncTestStep("device1", "verify_conflict_resolved", expectedResult = "Offline changes should merge")
                )
            )
        )

        // Delete-update conflict
        scenarios.add(
            SyncTestScenario(
                id = UUID.randomUUID().toString(),
                name = "Delete-Update Conflict",
                description = "Test conflict when one device deletes while another updates",
                syncType = SyncType.PROFILE,
                deviceCount = 2,
                networkCondition = NetworkCondition.NORMAL,
                conflictType = ConflictType.DELETE_UPDATE_CONFLICT,
                expectedBehavior = "Delete should take precedence or show conflict dialog",
                testSteps = listOf(
                    SyncTestStep("device1", "create_profile", mapOf("id" to "delete-conflict", "name" to "Original")),
                    SyncTestStep("device1", "wait_for_sync", delayMs = 2000),
                    SyncTestStep("device1", "delete_profile", mapOf("id" to "delete-conflict")),
                    SyncTestStep("device2", "update_profile", mapOf("id" to "delete-conflict", "name" to "Updated")),
                    SyncTestStep("device1", "wait_for_sync", delayMs = 3000),
                    SyncTestStep("device2", "verify_delete_conflict_resolved", expectedResult = "Delete-update conflict resolved")
                )
            )
        )

        // Version conflict
        scenarios.add(
            SyncTestScenario(
                id = UUID.randomUUID().toString(),
                name = "Version Conflict",
                description = "Test conflict when versions diverge",
                syncType = SyncType.THEME,
                deviceCount = 2,
                networkCondition = NetworkCondition.INTERMITTENT,
                conflictType = ConflictType.VERSION_CONFLICT,
                expectedBehavior = "Higher version or manual merge should resolve conflict",
                testSteps = listOf(
                    SyncTestStep("device1", "create_theme", mapOf("id" to "version-conflict", "version" to "1")),
                    SyncTestStep("device1", "wait_for_sync", delayMs = 2000),
                    SyncTestStep("device1", "set_network_intermittent"),
                    SyncTestStep("device1", "update_theme", mapOf("id" to "version-conflict", "version" to "2")),
                    SyncTestStep("device2", "update_theme", mapOf("id" to "version-conflict", "version" to "2")),
                    SyncTestStep("device1", "set_network_online"),
                    SyncTestStep("device1", "wait_for_sync", delayMs = 3000),
                    SyncTestStep("device1", "verify_version_resolved", expectedResult = "Version conflict resolved")
                )
            )
        )

        return scenarios
    }

    /**
     * Generate rapid sync scenarios for stress testing
     */
    fun generateRapidSyncScenarios(): List<SyncTestScenario> {
        return listOf(
            SyncTestScenario(
                id = UUID.randomUUID().toString(),
                name = "Rapid Profile Creation",
                description = "Create 100 profiles rapidly and verify sync",
                syncType = SyncType.PROFILE,
                deviceCount = 2,
                networkCondition = NetworkCondition.NORMAL,
                expectedBehavior = "All profiles should sync correctly",
                testSteps = listOf(
                    SyncTestStep("device1", "create_profiles_bulk", mapOf("count" to "100")),
                    SyncTestStep("device1", "wait_for_sync", delayMs = 10000),
                    SyncTestStep("device2", "verify_profiles_count", mapOf("expected" to "100"))
                )
            ),
            SyncTestScenario(
                id = UUID.randomUUID().toString(),
                name = "Rapid Updates",
                description = "Rapidly update same item multiple times",
                syncType = SyncType.THEME,
                deviceCount = 2,
                networkCondition = NetworkCondition.NORMAL,
                expectedBehavior = "Final state should be consistent",
                testSteps = listOf(
                    SyncTestStep("device1", "create_theme", mapOf("id" to "rapid-update")),
                    SyncTestStep("device1", "wait_for_sync", delayMs = 1000),
                    SyncTestStep("device1", "update_theme_rapidly", mapOf("id" to "rapid-update", "count" to "50")),
                    SyncTestStep("device1", "wait_for_sync", delayMs = 5000),
                    SyncTestStep("device2", "verify_theme_final_state", expectedResult = "Theme should have final update")
                )
            )
        )
    }
}
