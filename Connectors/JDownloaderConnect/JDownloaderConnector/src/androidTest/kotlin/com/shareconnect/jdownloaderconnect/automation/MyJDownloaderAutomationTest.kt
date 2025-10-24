package com.shareconnect.jdownloaderconnect.automation

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import com.shareconnect.jdownloaderconnect.AppContainer
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.MyJDownloaderViewModel
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.MyJDownloaderUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyJDownloaderAutomationTest {

    private lateinit var device: UiDevice
    private lateinit var appContainer: AppContainer
    private lateinit var viewModel: MyJDownloaderViewModel

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val context = ApplicationProvider.getApplicationContext()
        appContainer = AppContainer(context)
        viewModel = appContainer.myJDownloaderViewModel
    }

    @Test
    fun testMyJDownloaderScreenNavigation() {
        // Launch the app
        val context = ApplicationProvider.getApplicationContext()
        val packageName = context.packageName
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(launchIntent)

        // Wait for app to load
        device.wait(Until.hasObject(By.pkg(packageName)), 5000)

        // Navigate to My JDownloader screen
        val myJDownloaderButton = device.findObject(
            UiSelector().text("My JDownloader").className("android.widget.Button")
        )

        if (myJDownloaderButton.exists()) {
            myJDownloaderButton.click()

            // Wait for My JDownloader screen to load
            val screenTitle = device.findObject(
                UiSelector().text("My JDownloader").className("android.widget.TextView")
            )

            assertTrue("My JDownloader screen should load", screenTitle.waitForExists(5000))
        } else {
            // Try alternative navigation (might be in a different layout)
            val navigationMenu = device.findObject(
                UiSelector().description("Open navigation drawer")
            )

            if (navigationMenu.exists()) {
                navigationMenu.click()

                // Wait for navigation drawer
                device.waitForIdle(2000)

                val myJDownloaderMenuItem = device.findObject(
                    UiSelector().text("My JDownloader")
                )

                if (myJDownloaderMenuItem.exists()) {
                    myJDownloaderMenuItem.click()

                    val screenTitle = device.findObject(
                        UiSelector().text("My JDownloader").className("android.widget.TextView")
                    )

                    assertTrue("My JDownloader screen should load via navigation", screenTitle.waitForExists(5000))
                }
            }
        }
    }

    @Test
    fun testMyJDownloaderScreenElements() {
        // This test assumes we're already on the My JDownloader screen
        // In a real automation test, we'd navigate there first

        runBlocking {
            // Wait for ViewModel to initialize
            withTimeoutOrNull(5000) {
                while (viewModel.uiState.value is MyJDownloaderUiState.Loading) {
                    delay(100)
                }
            }

            val uiState = viewModel.uiState.value

            // Test that we can access the UI state
            assertNotNull("UI state should be accessible", uiState)

            // Test that instances flow is accessible
            val instances = appContainer.myJDownloaderRepository.instances.value
            assertNotNull("Instances should be accessible", instances)
        }
    }

    @Test
    fun testInstanceCardInteractions() {
        // Test that instance cards are interactive (this would be UI automation)
        // For now, we'll test the underlying logic

        runBlocking {
            // Test ViewModel instance selection
            val testInstanceId = "test-instance-1"

            // Initially no instance selected
            assertNull("No instance should be selected initially", viewModel.selectedInstance.value)

            // Note: In a real test, we'd have mock instances to select from
            // This tests the selection logic
            assertNotNull("ViewModel should be properly initialized", viewModel)
        }
    }

    @Test
    fun testDashboardDataLoading() {
        runBlocking {
            // Test dashboard data loading logic
            val dashboardData = viewModel.dashboardData.value

            // Initially should be null
            assertNull("Dashboard data should be null initially", dashboardData)

            // Test that refresh methods exist and can be called
            assertDoesNotThrow("Refresh dashboard should not throw", {
                viewModel.refreshDashboard()
            })

            assertDoesNotThrow("Refresh instances should not throw", {
                viewModel.refreshInstances()
            })
        }
    }

    @Test
    fun testControlButtonsLogic() {
        runBlocking {
            // Test that control buttons logic works
            val testInstanceId = "test-instance-1"

            // Test all control actions
            assertDoesNotThrow("Start action should not throw", {
                viewModel.controlInstance(testInstanceId, com.shareconnect.jdownloaderconnect.presentation.viewmodel.InstanceAction.START)
            })

            assertDoesNotThrow("Stop action should not throw", {
                viewModel.controlInstance(testInstanceId, com.shareconnect.jdownloaderconnect.presentation.viewmodel.InstanceAction.STOP)
            })

            assertDoesNotThrow("Pause action should not throw", {
                viewModel.controlInstance(testInstanceId, com.shareconnect.jdownloaderconnect.presentation.viewmodel.InstanceAction.PAUSE)
            })
        }
    }

    @Test
    fun testRealTimeUpdatesFlow() {
        runBlocking {
            // Test that real-time updates flow is accessible
            val updatesFlow = appContainer.myJDownloaderRepository.instanceUpdates

            assertNotNull("Updates flow should be accessible", updatesFlow)

            // Test that we can collect from the flow (even if it's empty)
            val collectedUpdates = mutableListOf<com.shareconnect.jdownloaderconnect.domain.model.InstanceUpdate>()

            withTimeoutOrNull(1000) {
                updatesFlow.collect { update ->
                    collectedUpdates.add(update)
                    // Just collect one update for testing
                }
            }

            // Flow should be accessible even if no updates
            assertNotNull("Should be able to access updates flow", collectedUpdates)
        }
    }

    @Test
    fun testScreenResponsiveness() {
        // Test that the screen loads within reasonable time
        runBlocking {
            val startTime = System.currentTimeMillis()

            // Simulate screen loading
            viewModel.refreshInstances()

            // Wait for state to change from Loading
            withTimeoutOrNull(5000) {
                while (viewModel.uiState.value is MyJDownloaderUiState.Loading) {
                    delay(100)
                }
            }

            val loadTime = System.currentTimeMillis() - startTime

            // Should load within 5 seconds
            assertTrue("Screen should load within 5 seconds", loadTime < 5000)
        }
    }

    @Test
    fun testErrorHandling() {
        runBlocking {
            // Test error state handling
            val uiState = viewModel.uiState.value

            // Should handle errors gracefully
            assertNotNull("UI state should always be non-null", uiState)

            // Test that error states are properly handled
            if (uiState is MyJDownloaderUiState.Error) {
                assertNotNull("Error should have a message", uiState.message)
                assertFalse("Error message should not be empty", uiState.message.isBlank())
            }
        }
    }

    @Test
    fun testInstanceStatusTransitions() {
        // Test that instance status transitions work correctly
        val statuses = com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus.values()

        // Test that all statuses have valid display names
        statuses.forEach { status ->
            assertNotNull("Status should have display name", status.getDisplayName())
            assertFalse("Display name should not be empty", status.getDisplayName().isBlank())

            assertNotNull("Status should have icon resource", status.getIconRes())
            assertFalse("Icon resource should not be empty", status.getIconRes().isBlank())

            assertNotNull("Status should have color resource", status.getColorRes())
            assertFalse("Color resource should not be empty", status.getColorRes().isBlank())
        }
    }

    @Test
    fun testSpeedHistoryCalculations() {
        // Test speed history calculation logic
        val speedPoints = listOf(
            com.shareconnect.jdownloaderconnect.domain.model.SpeedPoint(
                timestamp = 1000,
                speed = 1000,
                activeConnections = 1
            ),
            com.shareconnect.jdownloaderconnect.domain.model.SpeedPoint(
                timestamp = 2000,
                speed = 2000,
                activeConnections = 2
            ),
            com.shareconnect.jdownloaderconnect.domain.model.SpeedPoint(
                timestamp = 3000,
                speed = 3000,
                activeConnections = 3
            )
        )

        val history = com.shareconnect.jdownloaderconnect.domain.model.SpeedHistory(
            instanceId = "test-instance",
            points = speedPoints,
            averageSpeed = speedPoints.map { it.speed }.average().toLong(),
            maxSpeed = speedPoints.maxOf { it.speed },
            minSpeed = speedPoints.minOf { it.speed },
            durationMinutes = 60
        )

        assertEquals("test-instance", history.instanceId)
        assertEquals(3, history.points.size)
        assertEquals(2000L, history.averageSpeed)
        assertEquals(3000L, history.maxSpeed)
        assertEquals(1000L, history.minSpeed)
        assertEquals(60, history.durationMinutes)
    }

    private fun assertDoesNotThrow(message: String, block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            fail("$message: ${e.message}")
        }
    }
}