package com.shareconnect.qa.ai.analyzer

import com.shareconnect.qa.ai.executor.AIClient
import com.shareconnect.qa.ai.models.*
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class TestResultAnalyzerTest {

    @Mock
    private lateinit var mockAIClient: AIClient

    private lateinit var analyzer: TestResultAnalyzer

    init {
        MockitoAnnotations.openMocks(this)
        analyzer = TestResultAnalyzer(mockAIClient)
    }

    @Test
    fun testAnalyzePassedTest() {
        val testCase = createTestCase()
        val result = createPassedResult()

        val analysis = analyzer.analyzeTestResult(testCase, result)

        assertEquals(testCase.id, analysis.testCaseId)
        assertEquals(TestStatus.PASSED, analysis.status)
        assertTrue(analysis.summary.contains("passed successfully"))
        assertTrue(analysis.insights.isNotEmpty())
        assertTrue(analysis.recommendations.isEmpty())
        assertNull(analysis.rootCause)
        assertTrue(analysis.suggestedFixes.isEmpty())
    }

    @Test
    fun testAnalyzeFailedTest() {
        val testCase = createTestCase()
        val result = createFailedResult()

        `when`(mockAIClient.sendPrompt(anyString(), anyString(), anyInt())).thenReturn(AIClient.Response(
            success = true,
            text = """
                SUMMARY: Network connection failed
                INSIGHTS: - Connection timeout occurred
                ROOT_CAUSE: Server not responding
                RECOMMENDATIONS: - Check server status
                SUGGESTED_FIXES: - Increase timeout values
            """.trimIndent()
        ))

        val analysis = analyzer.analyzeTestResult(testCase, result)

        assertEquals(testCase.id, analysis.testCaseId)
        assertEquals(TestStatus.FAILED, analysis.status)
        assertTrue(analysis.summary.contains("Network connection failed"))
        assertTrue(analysis.insights.contains("Connection timeout occurred"))
        assertTrue(analysis.recommendations.contains("Check server status"))
        assertEquals("Server not responding", analysis.rootCause)
        assertTrue(analysis.suggestedFixes.contains("Increase timeout values"))
    }

    @Test
    fun testAnalyzeBatchResults() {
        val results = listOf(
            createTestCase() to createPassedResult(),
            createTestCase("test2") to createFailedResult()
        )

        `when`(mockAIClient.sendPrompt(anyString(), anyString(), anyInt())).thenReturn(AIClient.Response(
            success = true,
            text = "SUMMARY: Test failed\nINSIGHTS: - Failed step\nRECOMMENDATIONS: - Fix issue"
        ))

        val batchAnalysis = analyzer.analyzeBatchResults(results)

        assertEquals(2, batchAnalysis.totalTests)
        assertEquals(1, batchAnalysis.passed)
        assertEquals(1, batchAnalysis.failed)
        assertEquals(0, batchAnalysis.errors)
        assertEquals(0, batchAnalysis.skipped)
        assertEquals(50.0, batchAnalysis.passRate, 0.1)
        assertEquals(1, batchAnalysis.failedTestAnalyses.size)
        assertTrue(batchAnalysis.commonPatterns.isNotEmpty())
        assertTrue(batchAnalysis.recommendations.isNotEmpty())
    }

    private fun createTestCase(id: String = "test1"): TestCase {
        return TestCase(
            id = id,
            name = "Test Case",
            description = "Test description",
            category = TestCategory.UI_FLOW,
            priority = Priority.HIGH,
            steps = listOf(
                TestStep(
                    stepNumber = 1,
                    action = "click",
                    description = "Click button",
                    expectedOutcome = "Button clicked"
                )
            ),
            expectedResults = listOf("Success")
        )
    }

    private fun createPassedResult(): TestResult {
        return TestResult(
            testCaseId = "test1",
            status = TestStatus.PASSED,
            startTime = System.currentTimeMillis(),
            endTime = System.currentTimeMillis() + 1000,
            durationMs = 1000,
            stepResults = listOf(
                StepResult(
                    stepNumber = 1,
                    status = TestStatus.PASSED,
                    actualOutcome = "Button clicked"
                )
            )
        )
    }

    private fun createFailedResult(): TestResult {
        return TestResult(
            testCaseId = "test1",
            status = TestStatus.FAILED,
            startTime = System.currentTimeMillis(),
            endTime = System.currentTimeMillis() + 1000,
            durationMs = 1000,
            stepResults = listOf(
                StepResult(
                    stepNumber = 1,
                    status = TestStatus.FAILED,
                    actualOutcome = "Button not found",
                    errorMessage = "Element not found"
                )
            ),
            failureReason = "Network timeout"
        )
    }
}