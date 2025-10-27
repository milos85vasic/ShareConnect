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


package com.shareconnect.qa.ai.testbank

import com.shareconnect.qa.ai.models.*
import kotlinx.serialization.json.Json
import org.yaml.snakeyaml.Yaml
import java.io.File

/**
 * Test bank for loading and managing test cases
 */
class TestBank(private val basePath: String = "qa-ai/testbank") {

    private val testCases = mutableMapOf<String, TestCase>()
    private val testSuites = mutableMapOf<String, TestSuite>()

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val yaml = Yaml()

    init {
        loadAllTestCases()
        loadAllTestSuites()
    }

    /**
     * Load all test cases from the test bank directory
     */
    private fun loadAllTestCases() {
        val testBankDir = File(basePath)
        if (!testBankDir.exists()) {
            println("Test bank directory not found: $basePath")
            return
        }

        // Recursively load all test case files
        testBankDir.walkTopDown()
            .filter { it.isFile && (it.extension == "yaml" || it.extension == "yml" || it.extension == "json") }
            .forEach { file ->
                try {
                    loadTestCaseFile(file)
                } catch (e: Exception) {
                    println("Error loading test case file ${file.name}: ${e.message}")
                }
            }

        println("Loaded ${testCases.size} test cases from test bank")
    }

    /**
     * Load test suites
     */
    private fun loadAllTestSuites() {
        val suitesDir = File(basePath, "suites")
        if (!suitesDir.exists()) {
            return
        }

        suitesDir.walkTopDown()
            .filter { it.isFile && (it.extension == "yaml" || it.extension == "yml" || it.extension == "json") }
            .forEach { file ->
                try {
                    loadTestSuiteFile(file)
                } catch (e: Exception) {
                    println("Error loading test suite file ${file.name}: ${e.message}")
                }
            }

        println("Loaded ${testSuites.size} test suites")
    }

    /**
     * Load a test case file (YAML or JSON)
     */
    private fun loadTestCaseFile(file: File) {
        val testCase = when (file.extension) {
            "json" -> json.decodeFromString<TestCase>(file.readText())
            "yaml", "yml" -> {
                val yamlData = yaml.load<Map<String, Any>>(file.readText())
                parseYamlTestCase(yamlData)
            }
            else -> return
        }

        testCases[testCase.id] = testCase
    }

    /**
     * Load a test suite file
     */
    private fun loadTestSuiteFile(file: File) {
        val testSuite = when (file.extension) {
            "json" -> json.decodeFromString<TestSuite>(file.readText())
            "yaml", "yml" -> {
                val yamlData = yaml.load<Map<String, Any>>(file.readText())
                parseYamlTestSuite(yamlData)
            }
            else -> return
        }

        testSuites[testSuite.id] = testSuite
    }

    /**
     * Parse YAML test case to TestCase object
     */
    @Suppress("UNCHECKED_CAST")
    private fun parseYamlTestCase(yamlData: Map<String, Any>): TestCase {
        val steps = (yamlData["steps"] as? List<Map<String, Any>> ?: emptyList()).mapIndexed { index, stepData ->
            TestStep(
                stepNumber = stepData["step_number"] as? Int ?: (index + 1),
                action = stepData["action"] as? String ?: "",
                description = stepData["description"] as? String ?: "",
                target = stepData["target"] as? String,
                input = stepData["input"] as? Map<String, String> ?: emptyMap(),
                expectedOutcome = stepData["expected_outcome"] as? String ?: "",
                screenshot = stepData["screenshot"] as? Boolean ?: false,
                waitAfterMs = (stepData["wait_after_ms"] as? Int)?.toLong() ?: 0L
            )
        }

        return TestCase(
            id = yamlData["id"] as? String ?: "",
            name = yamlData["name"] as? String ?: "",
            description = yamlData["description"] as? String ?: "",
            category = TestCategory.valueOf((yamlData["category"] as? String ?: "INTEGRATION").uppercase()),
            priority = Priority.valueOf((yamlData["priority"] as? String ?: "MEDIUM").uppercase()),
            tags = yamlData["tags"] as? List<String> ?: emptyList(),
            preconditions = yamlData["preconditions"] as? List<String> ?: emptyList(),
            steps = steps,
            expectedResults = yamlData["expected_results"] as? List<String> ?: emptyList(),
            postconditions = yamlData["postconditions"] as? List<String> ?: emptyList(),
            dataRequirements = yamlData["data_requirements"] as? List<String> ?: emptyList(),
            estimatedDurationMs = (yamlData["estimated_duration_ms"] as? Int)?.toLong() ?: 30000L,
            retryOnFailure = yamlData["retry_on_failure"] as? Boolean ?: true,
            maxRetries = yamlData["max_retries"] as? Int ?: 2
        )
    }

    /**
     * Parse YAML test suite to TestSuite object
     */
    @Suppress("UNCHECKED_CAST")
    private fun parseYamlTestSuite(yamlData: Map<String, Any>): TestSuite {
        return TestSuite(
            id = yamlData["id"] as? String ?: "",
            name = yamlData["name"] as? String ?: "",
            description = yamlData["description"] as? String ?: "",
            testCases = yamlData["test_cases"] as? List<String> ?: emptyList(),
            setupSteps = yamlData["setup_steps"] as? List<String> ?: emptyList(),
            teardownSteps = yamlData["teardown_steps"] as? List<String> ?: emptyList()
        )
    }

    /**
     * Get a test case by ID
     */
    fun getTestCase(id: String): TestCase? = testCases[id]

    /**
     * Get all test cases
     */
    fun getAllTestCases(): List<TestCase> = testCases.values.toList()

    /**
     * Get test cases by category
     */
    fun getTestCasesByCategory(category: TestCategory): List<TestCase> =
        testCases.values.filter { it.category == category }

    /**
     * Get test cases by priority
     */
    fun getTestCasesByPriority(priority: Priority): List<TestCase> =
        testCases.values.filter { it.priority == priority }

    /**
     * Get test cases by tag
     */
    fun getTestCasesByTag(tag: String): List<TestCase> =
        testCases.values.filter { tag in it.tags }

    /**
     * Get a test suite by ID
     */
    fun getTestSuite(id: String): TestSuite? = testSuites[id]

    /**
     * Get all test suites
     */
    fun getAllTestSuites(): List<TestSuite> = testSuites.values.toList()

    /**
     * Get test cases in a suite
     */
    fun getTestCasesInSuite(suiteId: String): List<TestCase> {
        val suite = testSuites[suiteId] ?: return emptyList()
        return suite.testCases.mapNotNull { testCases[it] }
    }

    /**
     * Print test bank statistics
     */
    fun printStatistics() {
        println("\n=== Test Bank Statistics ===")
        println("Total Test Cases: ${testCases.size}")
        println("Total Test Suites: ${testSuites.size}")
        println("\nBy Category:")
        TestCategory.values().forEach { category ->
            val count = getTestCasesByCategory(category).size
            if (count > 0) {
                println("  $category: $count")
            }
        }
        println("\nBy Priority:")
        Priority.values().forEach { priority ->
            val count = getTestCasesByPriority(priority).size
            if (count > 0) {
                println("  $priority: $count")
            }
        }
        println("============================\n")
    }
}
