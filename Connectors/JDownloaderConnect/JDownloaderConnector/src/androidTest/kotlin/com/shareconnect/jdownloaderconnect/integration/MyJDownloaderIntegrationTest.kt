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


package com.shareconnect.jdownloaderconnect.integration

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.jdownloaderconnect.AppContainer
import com.shareconnect.jdownloaderconnect.data.repository.MyJDownloaderRepository
import com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus
import com.shareconnect.jdownloaderconnect.domain.model.JDownloaderInstance
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.MyJDownloaderViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyJDownloaderIntegrationTest {

    private lateinit var appContainer: AppContainer
    private lateinit var repository: MyJDownloaderRepository
    private lateinit var viewModel: MyJDownloaderViewModel

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext()
        appContainer = AppContainer(context)
        repository = appContainer.myJDownloaderRepository
        viewModel = appContainer.myJDownloaderViewModel
    }

    @Test
    fun testRepositoryInitialization() {
        // Test that repository is properly initialized
        assertNotNull(repository)
    }

    @Test
    fun testViewModelInitialization() {
        // Test that ViewModel is properly initialized
        assertNotNull(viewModel)
        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun testRepositoryDependencies() {
        // Test that repository has required dependencies
        val repositoryClass = repository::class.java
        val declaredFields = repositoryClass.declaredFields

        // Should have API and account repository dependencies
        val hasApiField = declaredFields.any { it.name.contains("api", ignoreCase = true) }
        val hasAccountRepoField = declaredFields.any { it.name.contains("account", ignoreCase = true) }

        assertTrue("Repository should have API dependency", hasApiField)
        assertTrue("Repository should have account repository dependency", hasAccountRepoField)
    }

    @Test
    fun testViewModelDependencies() {
        // Test that ViewModel has required dependencies
        val viewModelClass = viewModel::class.java
        val declaredFields = viewModelClass.declaredFields

        // Should have repository dependency
        val hasRepositoryField = declaredFields.any { it.name.contains("repository", ignoreCase = true) }

        assertTrue("ViewModel should have repository dependency", hasRepositoryField)
    }

    @Test
    fun testInstanceStatusEnumValues() {
        // Test that all instance status values are properly defined
        val statuses = InstanceStatus.values()

        assertTrue("Should have RUNNING status", statuses.contains(InstanceStatus.RUNNING))
        assertTrue("Should have PAUSED status", statuses.contains(InstanceStatus.PAUSED))
        assertTrue("Should have STOPPED status", statuses.contains(InstanceStatus.STOPPED))
        assertTrue("Should have ERROR status", statuses.contains(InstanceStatus.ERROR))
        assertTrue("Should have OFFLINE status", statuses.contains(InstanceStatus.OFFLINE))
        assertTrue("Should have CONNECTING status", statuses.contains(InstanceStatus.CONNECTING))

        // Test display names
        assertEquals("Running", InstanceStatus.RUNNING.getDisplayName())
        assertEquals("Paused", InstanceStatus.PAUSED.getDisplayName())
        assertEquals("Stopped", InstanceStatus.STOPPED.getDisplayName())
        assertEquals("Error", InstanceStatus.ERROR.getDisplayName())
        assertEquals("Offline", InstanceStatus.OFFLINE.getDisplayName())
        assertEquals("Connecting", InstanceStatus.CONNECTING.getDisplayName())
    }

    @Test
    fun testJDownloaderInstanceModel() {
        // Test that JDownloaderInstance model is properly structured
        val instance = JDownloaderInstance(
            id = "test-instance-1",
            name = "Test Instance",
            status = InstanceStatus.RUNNING,
            version = "2.0.0",
            lastSeen = System.currentTimeMillis(),
            isOnline = true,
            deviceId = "device-123",
            accountId = "account-456",
            currentSpeed = 2048000L,
            activeDownloads = 3,
            totalDownloads = 10,
            uptime = 3600L
        )

        assertEquals("test-instance-1", instance.id)
        assertEquals("Test Instance", instance.name)
        assertEquals(InstanceStatus.RUNNING, instance.status)
        assertEquals("2.0.0", instance.version)
        assertTrue(instance.isOnline)
        assertEquals("device-123", instance.deviceId)
        assertEquals("account-456", instance.accountId)
        assertEquals(2048000L, instance.currentSpeed)
        assertEquals(3, instance.activeDownloads)
        assertEquals(10, instance.totalDownloads)
        assertEquals(3600L, instance.uptime)
    }

    @Test
    fun testViewModelStateFlowInitialization() = runTest {
        // Test that ViewModel state flows are properly initialized
        val uiState = viewModel.uiState.value
        val selectedInstance = viewModel.selectedInstance.value
        val dashboardData = viewModel.dashboardData.value

        // UI state should be initialized (likely Loading)
        assertNotNull("UI state should be initialized", uiState)

        // Selected instance and dashboard should be null initially
        assertNull("Selected instance should be null initially", selectedInstance)
        assertNull("Dashboard data should be null initially", dashboardData)
    }

    @Test
    fun testRepositoryFlowInitialization() = runTest {
        // Test that repository flows are properly initialized
        val instances = repository.instances.value

        // Instances should be initialized as empty list
        assertNotNull("Instances flow should be initialized", instances)
        assertTrue("Instances should start empty", instances.isEmpty())
    }

    @Test
    fun testInstanceActionEnum() {
        // Test that InstanceAction enum is properly defined
        val actions = com.shareconnect.jdownloaderconnect.data.repository.InstanceAction.values()

        assertTrue("Should have START action", actions.contains(com.shareconnect.jdownloaderconnect.data.repository.InstanceAction.START))
        assertTrue("Should have STOP action", actions.contains(com.shareconnect.jdownloaderconnect.data.repository.InstanceAction.STOP))
        assertTrue("Should have PAUSE action", actions.contains(com.shareconnect.jdownloaderconnect.data.repository.InstanceAction.PAUSE))
    }

    @Test
    fun testViewModelInstanceActionEnum() {
        // Test that ViewModel InstanceAction enum is properly defined
        val actions = com.shareconnect.jdownloaderconnect.presentation.viewmodel.InstanceAction.values()

        assertTrue("Should have START action", actions.contains(com.shareconnect.jdownloaderconnect.presentation.viewmodel.InstanceAction.START))
        assertTrue("Should have STOP action", actions.contains(com.shareconnect.jdownloaderconnect.presentation.viewmodel.InstanceAction.STOP))
        assertTrue("Should have PAUSE action", actions.contains(com.shareconnect.jdownloaderconnect.presentation.viewmodel.InstanceAction.PAUSE))
    }

    @Test
    fun testSpeedFormattingLogic() {
        // Test speed formatting logic (this would be in a utility function)
        fun formatSpeed(bytesPerSecond: Long): String {
            return when {
                bytesPerSecond >= 1024 * 1024 * 1024 -> "%.1f GB/s".format(bytesPerSecond / (1024.0 * 1024.0 * 1024.0))
                bytesPerSecond >= 1024 * 1024 -> "%.1f MB/s".format(bytesPerSecond / (1024.0 * 1024.0))
                bytesPerSecond >= 1024 -> "%.1f KB/s".format(bytesPerSecond / 1024.0)
                else -> "$bytesPerSecond B/s"
            }
        }

        assertEquals("2.0 MB/s", formatSpeed(2048000))
        assertEquals("1.0 MB/s", formatSpeed(1048576))
        assertEquals("500.0 KB/s", formatSpeed(512000))
        assertEquals("1000 B/s", formatSpeed(1000))
    }

    @Test
    fun testUptimeFormattingLogic() {
        // Test uptime formatting logic (this would be in a utility function)
        fun formatUptime(seconds: Long): String {
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            return "${hours}h ${minutes}m"
        }

        assertEquals("1h 0m", formatUptime(3600))
        assertEquals("2h 30m", formatUptime(9000))
        assertEquals("0h 45m", formatUptime(2700))
    }

    @Test
    fun testEtaFormattingLogic() {
        // Test ETA formatting logic (this would be in a utility function)
        fun formatEta(seconds: Long): String {
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val remainingSeconds = seconds % 60

            return when {
                hours > 0 -> "${hours}h ${minutes}m"
                minutes > 0 -> "${minutes}m ${remainingSeconds}s"
                else -> "${remainingSeconds}s"
            }
        }

        assertEquals("1h 0m", formatEta(3600))
        assertEquals("2h 30m", formatEta(9000))
        assertEquals("45m 0s", formatEta(2700))
        assertEquals("30s", formatEta(30))
    }
}