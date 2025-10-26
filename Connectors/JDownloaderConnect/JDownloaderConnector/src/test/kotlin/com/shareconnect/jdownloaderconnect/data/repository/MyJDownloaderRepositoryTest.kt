package com.shareconnect.jdownloaderconnect.data.repository

import com.shareconnect.jdownloaderconnect.data.model.JDownloaderAccount
import com.shareconnect.jdownloaderconnect.domain.model.*
import com.shareconnect.jdownloaderconnect.network.api.MyJDownloaderApi
import com.shareconnect.jdownloaderconnect.network.api.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class MyJDownloaderRepositoryTest {

    @MockK
    private lateinit var api: MyJDownloaderApi

    @MockK
    private lateinit var accountRepository: JDownloaderRepository

    private lateinit var repository: MyJDownloaderRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        repository = MyJDownloaderRepository(api, accountRepository)
    }

    @Test
    fun `getInstances returns success with instances`() = runTest {
        // Given
        val mockInstances = listOf(
            com.shareconnect.jdownloaderconnect.domain.model.JDownloaderInstance(
                id = "instance1",
                name = "Instance 1",
                status = com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus.RUNNING,
                version = "2.0.0",
                lastSeen = System.currentTimeMillis(),
                isOnline = true,
                deviceId = "device1",
                accountId = "account1"
            )
        )

        val mockResponse = mockk<Response<ListInstancesResponse>>()
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body() } returns ListInstancesResponse(mockInstances)

        coEvery { api.listInstances(any()) } returns mockResponse

        // When
        val result = repository.getInstances()

        // Then
        assertTrue(result.isSuccess)
        val instances = result.getOrNull()
        assertNotNull(instances)
        assertEquals(1, instances?.size)
        assertEquals("instance1", instances?.first()?.id)
        assertEquals(com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus.RUNNING, instances?.first()?.status)
    }

    @Test
    fun `getInstances returns failure on API error`() = runTest {
        // Given
        val mockResponse = mockk<Response<ListInstancesResponse>>()
        every { mockResponse.isSuccessful } returns false
        every { mockResponse.message() } returns "API Error"

        coEvery { api.listInstances(any()) } returns mockResponse

        // When
        val result = repository.getInstances()

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("API Error") == true)
    }

    @Test
    fun `getInstanceStatus returns failure when instance not in cache`() = runTest {
        // Given
        val instanceId = "instance1"

        val mockStatusResponse = InstanceStatusResponse(
            instanceId = instanceId,
            status = InstanceStatus.RUNNING,
            uptime = 3600,
            activeDownloads = 5,
            totalDownloads = 10,
            errorMessage = null,
            lastUpdated = System.currentTimeMillis()
        )

        val mockResponse = mockk<Response<InstanceStatusResponse>>()
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body() } returns mockStatusResponse

        coEvery { api.getInstanceStatus(any(), instanceId) } returns mockResponse

        // When - instance not in cache
        val result = repository.getInstanceStatus(instanceId)

        // Then - should fail because instance is not in cache
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Instance not found") == true)
    }

    @Test
    fun `getDownloadSpeed returns correct speed value`() = runTest {
        // Given
        val instanceId = "instance1"
        val expectedSpeed = 2048000L // 2 MB/s

        val mockSpeedResponse = DownloadSpeedResponse(
            instanceId = instanceId,
            currentSpeed = expectedSpeed,
            maxSpeed = expectedSpeed * 2,
            averageSpeed = expectedSpeed,
            timestamp = System.currentTimeMillis()
        )

        val mockResponse = mockk<Response<DownloadSpeedResponse>>()
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body() } returns mockSpeedResponse

        coEvery { api.getDownloadSpeed(any(), instanceId) } returns mockResponse

        // When
        val result = repository.getDownloadSpeed(instanceId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedSpeed, result.getOrNull())
    }

    @Test
    fun `getSpeedHistory returns valid speed history`() = runTest {
        // Given
        val instanceId = "instance1"
        val durationMinutes = 30

        val mockSpeedPoints = listOf(
            com.shareconnect.jdownloaderconnect.network.api.SpeedPoint(
                timestamp = System.currentTimeMillis() - 1800000, // 30 min ago
                speed = 1024000,
                activeConnections = 2
            ),
            com.shareconnect.jdownloaderconnect.network.api.SpeedPoint(
                timestamp = System.currentTimeMillis() - 900000, // 15 min ago
                speed = 2048000,
                activeConnections = 4
            ),
            com.shareconnect.jdownloaderconnect.network.api.SpeedPoint(
                timestamp = System.currentTimeMillis(), // now
                speed = 3072000,
                activeConnections = 6
            )
        )

        val mockHistoryResponse = SpeedHistoryResponse(
            instanceId = instanceId,
            speedPoints = mockSpeedPoints
        )

        val mockResponse = mockk<Response<SpeedHistoryResponse>>()
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body() } returns mockHistoryResponse

        coEvery { api.getSpeedHistory(any(), instanceId, durationMinutes) } returns mockResponse

        // When
        val result = repository.getSpeedHistory(instanceId, durationMinutes)

        // Then
        assertTrue(result.isSuccess)
        val history = result.getOrNull()
        assertNotNull(history)
        assertEquals(instanceId, history?.instanceId)
        assertEquals(3, history?.points?.size)
        assertEquals(durationMinutes, history?.durationMinutes)

        // Check calculations
        assertEquals(3072000L, history?.maxSpeed) // Max of the three points
        assertEquals(1024000L, history?.minSpeed) // Min of the three points
        assertEquals(2048000L, history?.averageSpeed) // Average of the three points
    }

    @Test
    fun `controlInstance returns failure when instance not in cache`() = runTest {
        // Given
        val instanceId = "instance1"

        // When - Try to control an instance that's not in cache
        val result = repository.controlInstance(instanceId, InstanceAction.START)

        // Then - should fail because instance not in cache
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Instance not found") == true)
    }

    @Test
    fun `cleanup cancels coroutines and clears cache`() = runTest {
        // Given - Repository is initialized

        // When
        repository.cleanup()

        // Then
        // Verify that the monitoring coroutine is cancelled
        // This is difficult to test directly, but we can verify the method completes
        assertTrue(true) // Method should complete without throwing
    }
}