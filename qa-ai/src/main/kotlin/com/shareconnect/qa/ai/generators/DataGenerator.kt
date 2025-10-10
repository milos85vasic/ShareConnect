package com.shareconnect.qa.ai.generators

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Main data generator orchestrator
 */
object DataGenerator {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val profileGenerator = ProfileDataGenerator()
    private val edgeCaseGenerator = EdgeCaseGenerator()
    private val syncDataGenerator = SyncDataGenerator()

    fun generateAllTestData(outputDir: String = "qa-ai/testdata") {
        println("Generating all test data...")

        val baseDir = File(outputDir)
        baseDir.mkdirs()

        // Generate profile test data
        generateProfileTestData(File(baseDir, "profiles"))

        // Generate edge case data
        generateEdgeCaseData(File(baseDir, "edge-cases"))

        // Generate sync test data
        generateSyncTestData(File(baseDir, "sync"))

        println("Test data generation complete!")
    }

    private fun generateProfileTestData(dir: File) {
        dir.mkdirs()

        println("Generating profile test data...")

        // Generate all profiles
        val profiles = profileGenerator.generateAllProfiles()
        File(dir, "all_profiles.json").writeText(json.encodeToString(profiles))
        println("  - Generated ${profiles.size} profile configurations")

        // Generate all scenarios
        val scenarios = profileGenerator.generateAllScenarios()
        File(dir, "profile_scenarios.json").writeText(json.encodeToString(scenarios))
        println("  - Generated ${scenarios.size} profile test scenarios")

        // Generate edge case profiles
        val edgeCaseProfiles = edgeCaseGenerator.generateEdgeCaseProfiles()
        File(dir, "edge_case_profiles.json").writeText(json.encodeToString(edgeCaseProfiles))
        println("  - Generated ${edgeCaseProfiles.size} edge case profiles")
    }

    private fun generateEdgeCaseData(dir: File) {
        dir.mkdirs()

        println("Generating edge case test data...")

        val edgeCases = edgeCaseGenerator.generateAllEdgeCases()
        File(dir, "all_edge_cases.json").writeText(json.encodeToString(edgeCases))
        println("  - Generated ${edgeCases.size} edge case scenarios")

        // Group by category
        edgeCases.groupBy { it.category }.forEach { (category, cases) ->
            File(dir, "${category.name.lowercase()}_cases.json").writeText(
                json.encodeToString(cases)
            )
        }
        println("  - Organized edge cases by category")
    }

    private fun generateSyncTestData(dir: File) {
        dir.mkdirs()

        println("Generating sync test data...")

        // Generate all sync scenarios
        val syncScenarios = syncDataGenerator.generateAllSyncScenarios()
        File(dir, "all_sync_scenarios.json").writeText(json.encodeToString(syncScenarios))
        println("  - Generated ${syncScenarios.size} sync test scenarios")

        // Generate rapid sync scenarios
        val rapidSyncScenarios = syncDataGenerator.generateRapidSyncScenarios()
        File(dir, "rapid_sync_scenarios.json").writeText(json.encodeToString(rapidSyncScenarios))
        println("  - Generated ${rapidSyncScenarios.size} rapid sync scenarios")

        // Group by sync type
        syncScenarios.groupBy { it.syncType }.forEach { (syncType, scenarios) ->
            File(dir, "${syncType.name.lowercase()}_sync.json").writeText(
                json.encodeToString(scenarios)
            )
        }
        println("  - Organized sync scenarios by type")
    }

    fun printStatistics() {
        println("\n=== Test Data Statistics ===")
        println("Profiles: ${profileGenerator.generateAllProfiles().size}")
        println("Profile Scenarios: ${profileGenerator.generateAllScenarios().size}")
        println("Edge Cases: ${edgeCaseGenerator.generateAllEdgeCases().size}")
        println("Sync Scenarios: ${syncDataGenerator.generateAllSyncScenarios().size}")
        println("Rapid Sync Scenarios: ${syncDataGenerator.generateRapidSyncScenarios().size}")
        println("============================\n")
    }
}

fun main() {
    println("ShareConnect AI QA - Test Data Generator")
    println("=========================================\n")

    // Generate all test data
    DataGenerator.generateAllTestData()

    // Print statistics
    DataGenerator.printStatistics()
}
