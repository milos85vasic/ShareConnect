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


package com.shareconnect.jdownloaderconnect.presentation.viewmodel

import com.shareconnect.jdownloaderconnect.data.repository.MyJDownloaderRepository
import com.shareconnect.jdownloaderconnect.domain.model.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyJDownloaderViewModelTest {

    @MockK
    private lateinit var repository: MyJDownloaderRepository

    private lateinit var viewModel: MyJDownloaderViewModel
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        MockKAnnotations.init(this, relaxUnitFun = true)

        // Mock the flows
        val mockInstancesFlow = MutableStateFlow<List<JDownloaderInstance>>(emptyList())
        val mockInstanceUpdatesFlow = MutableSharedFlow<InstanceUpdate>()

        every { repository.instances } returns mockInstancesFlow
        every { repository.instanceUpdates } returns mockInstanceUpdatesFlow

        // Mock getInstances to prevent init block from failing
        coEvery { repository.getInstances() } returns Result.success(emptyList())

        viewModel = MyJDownloaderViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle() // Complete the init block
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is success with empty list after init`() = runTest {
        // Given - ViewModel is initialized and loadInstances is called in init

        // When - Collect state after initialization
        val initialState = viewModel.uiState.value

        // Then - Should be Success with empty list (from mocked getInstances)
        assertTrue(initialState is MyJDownloaderUiState.Success)
        val successState = initialState as MyJDownloaderUiState.Success
        assertEquals(0, successState.instances.size)
    }

    @Test
    fun `refreshInstances success updates state to success with instances`() = runTest {
        // Given - ViewModel already initialized with empty list
        val mockInstances = listOf(
            JDownloaderInstance(
                id = "instance1",
                name = "Instance 1",
                status = InstanceStatus.RUNNING,
                version = "2.0.0",
                lastSeen = System.currentTimeMillis(),
                isOnline = true,
                deviceId = "device1",
                accountId = "account1",
                currentSpeed = 2048000,
                activeDownloads = 3
            ),
            JDownloaderInstance(
                id = "instance2",
                name = "Instance 2",
                status = InstanceStatus.PAUSED,
                version = "2.0.0",
                lastSeen = System.currentTimeMillis(),
                isOnline = true,
                deviceId = "device2",
                accountId = "account1",
                currentSpeed = 0,
                activeDownloads = 0
            )
        )

        // Override the mock for this specific test
        coEvery { repository.getInstances() } returns Result.success(mockInstances)

        // When
        viewModel.refreshInstances()

        // Wait for all coroutines to complete
        advanceUntilIdle()
        val finalState = viewModel.uiState.value

        // Then
        assertTrue(finalState is MyJDownloaderUiState.Success)
        val successState = finalState as MyJDownloaderUiState.Success
        assertEquals(2, successState.instances.size)
        assertEquals("instance1", successState.instances[0].id)
        assertEquals("instance2", successState.instances[1].id)
    }

    @Test
    fun `refreshInstances failure updates state to error`() = runTest {
        // Given - ViewModel already initialized
        val errorMessage = "Network error"
        coEvery { repository.getInstances() } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.refreshInstances()

        // Wait for all coroutines to complete
        advanceUntilIdle()
        val finalState = viewModel.uiState.value

        // Then
        assertTrue(finalState is MyJDownloaderUiState.Error)
        val errorState = finalState as MyJDownloaderUiState.Error
        assertEquals(errorMessage, errorState.message)
    }

    @Test
    fun `selectInstance updates selected instance and loads dashboard`() = runTest {
        // Given
        val instanceId = "instance1"
        val mockInstance = JDownloaderInstance(
            id = instanceId,
            name = "Test Instance",
            status = InstanceStatus.RUNNING,
            version = "2.0.0",
            lastSeen = System.currentTimeMillis(),
            isOnline = true,
            deviceId = "device1",
            accountId = "account1"
        )

        val mockDashboard = InstanceDashboard(
            instance = mockInstance,
            speedHistory = SpeedHistory(
                instanceId = instanceId,
                points = emptyList(),
                averageSpeed = 0,
                maxSpeed = 0,
                minSpeed = 0,
                durationMinutes = 60
            ),
            recentActivity = emptyList()
        )

        // Need to mock the instances flow to contain our instance
        val mockInstancesFlow = MutableStateFlow(listOf(mockInstance))
        every { repository.instances } returns mockInstancesFlow

        coEvery { repository.getInstanceDashboard(instanceId) } returns Result.success(mockDashboard)

        // When
        viewModel.selectInstance(instanceId)

        // Wait for all coroutines to complete
        advanceUntilIdle()

        // Then
        assertEquals(mockInstance, viewModel.selectedInstance.value)
        assertEquals(mockDashboard, viewModel.dashboardData.value)
    }

    @Test
    fun `controlInstance calls repository method`() = runTest {
        // Given
        val instanceId = "instance1"
        val action = InstanceAction.START
        val repoAction = com.shareconnect.jdownloaderconnect.data.repository.InstanceAction.START

        coEvery { repository.controlInstance(instanceId, repoAction) } returns Result.success(Unit)

        // When
        viewModel.controlInstance(instanceId, action)

        // Wait for all coroutines to complete
        advanceUntilIdle()

        // Then
        coVerify { repository.controlInstance(instanceId, repoAction) }
    }

    @Test
    fun `refreshDashboard does nothing when no instance selected`() = runTest {
        // Given - no instance selected

        // When
        viewModel.refreshDashboard()

        // Then - no repository calls should be made
        coVerify(exactly = 0) { repository.getInstanceDashboard(any()) }
    }

    @Test
    fun `instance updates are observed from repository`() = runTest {
        // Given
        val instanceId = "instance1"
        val mockInstance = JDownloaderInstance(
            id = instanceId,
            name = "Test Instance",
            status = InstanceStatus.RUNNING,
            version = "2.0.0",
            lastSeen = System.currentTimeMillis(),
            isOnline = true,
            deviceId = "device1",
            accountId = "account1"
        )

        val mockDashboard = InstanceDashboard(
            instance = mockInstance,
            speedHistory = SpeedHistory(
                instanceId = instanceId,
                points = emptyList(),
                averageSpeed = 0,
                maxSpeed = 0,
                minSpeed = 0,
                durationMinutes = 60
            ),
            recentActivity = emptyList()
        )

        coEvery { repository.getInstanceDashboard(instanceId) } returns Result.success(mockDashboard)

        // When - Select an instance
        viewModel.selectInstance(instanceId)

        // Then - The ViewModel should expose the repository's instance updates flow
        assertNotNull(viewModel.instanceUpdates)
    }

    // @Test
    // fun `onCleared cleans up repository`() = runTest {
    //     // When
    //     viewModel.onCleared()
    //
    //     // Then
    //     verify { repository.cleanup() }
    // }

    @Test
    fun `auto-select first online instance on successful load`() = runTest {
        // Given
        val offlineInstance = JDownloaderInstance(
            id = "instance1",
            name = "Offline Instance",
            status = InstanceStatus.OFFLINE,
            version = "2.0.0",
            lastSeen = System.currentTimeMillis(),
            isOnline = false,
            deviceId = "device1",
            accountId = "account1"
        )

        val onlineInstance = JDownloaderInstance(
            id = "instance2",
            name = "Online Instance",
            status = InstanceStatus.RUNNING,
            version = "2.0.0",
            lastSeen = System.currentTimeMillis(),
            isOnline = true,
            deviceId = "device2",
            accountId = "account1"
        )

        val mockInstances = listOf(offlineInstance, onlineInstance)

        // Mock instances flow to return online instance
        val mockInstancesFlow = MutableStateFlow(mockInstances)
        every { repository.instances } returns mockInstancesFlow

        // Mock dashboard call for the auto-selected instance
        coEvery { repository.getInstanceDashboard("instance2") } returns Result.success(
            InstanceDashboard(
                instance = onlineInstance,
                speedHistory = SpeedHistory("instance2", emptyList(), 0, 0, 0, 60),
                recentActivity = emptyList()
            )
        )

        coEvery { repository.getInstances() } returns Result.success(mockInstances)

        // When
        viewModel.refreshInstances()

        // Wait for all coroutines to complete
        advanceUntilIdle()
        val selectedInstance = viewModel.selectedInstance.value

        // Then - Should auto-select the first online instance
        assertEquals(onlineInstance, selectedInstance)
    }
}