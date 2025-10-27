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


package com.shareconnect.portainerconnect.data.api

import com.shareconnect.portainerconnect.data.models.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

/**
 * Unit tests for PortainerApiClient using MockK
 * Tests all major API client operations using mocked service
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = com.shareconnect.portainerconnect.TestApplication::class)
class PortainerApiClientMockKTest {

    private lateinit var mockService: PortainerApiService
    private lateinit var apiClient: PortainerApiClient

    @Before
    fun setUp() {
        mockService = mockk()
        apiClient = PortainerApiClient(
            serverUrl = "http://test.server:9000",
            portainerApiService = mockService
        )
    }

    // ===== Authentication Tests =====

    @Test
    fun `test authenticate success`() = runBlocking {
        val mockAuth = PortainerAuthResponse(jwt = "test-jwt-token-123")

        coEvery { mockService.authenticate(any()) } returns Response.success(mockAuth)

        val result = apiClient.authenticate("admin", "password")

        assertTrue(result.isSuccess)
        val auth = result.getOrNull()!!
        assertEquals("test-jwt-token-123", auth.jwt)

        coVerify { mockService.authenticate(any()) }
    }

    @Test
    fun `test authenticate failure`() = runBlocking {
        coEvery { mockService.authenticate(any()) } returns Response.error(401, mockk(relaxed = true))

        val result = apiClient.authenticate("admin", "wrongpass")

        assertTrue(result.isFailure)
    }

    // ===== Status Tests =====

    @Test
    fun `test getStatus success`() = runBlocking {
        val mockStatus = PortainerStatus(
            version = "2.19.0",
            edition = "CE",
            instanceId = "instance-123"
        )

        coEvery { mockService.getStatus() } returns Response.success(mockStatus)

        val result = apiClient.getStatus()

        assertTrue(result.isSuccess)
        val status = result.getOrNull()!!
        assertEquals("2.19.0", status.version)
        assertEquals("CE", status.edition)

        coVerify { mockService.getStatus() }
    }

    // ===== Endpoint Tests =====

    @Test
    fun `test getEndpoints success`() = runBlocking {
        val mockEndpoints = listOf(
            PortainerEndpoint(
                id = 1,
                name = "local",
                type = 1,
                url = "unix:///var/run/docker.sock",
                status = 1
            ),
            PortainerEndpoint(
                id = 2,
                name = "remote-docker",
                type = 2,
                url = "tcp://192.168.1.100:2375",
                status = 1
            )
        )

        coEvery { mockService.getEndpoints(any()) } returns Response.success(mockEndpoints)

        val result = apiClient.getEndpoints("test-token")

        assertTrue(result.isSuccess)
        val endpoints = result.getOrNull()!!
        assertEquals(2, endpoints.size)
        assertEquals("local", endpoints[0].name)
        assertEquals("remote-docker", endpoints[1].name)

        coVerify { mockService.getEndpoints(any()) }
    }

    // ===== Container Tests =====

    @Test
    fun `test getContainers success`() = runBlocking {
        val mockContainers = listOf(
            PortainerContainer(
                id = "abc123",
                names = listOf("/nginx"),
                image = "nginx:latest",
                imageId = "img123",
                created = 1698345600,
                state = "running",
                status = "Up 2 hours"
            ),
            PortainerContainer(
                id = "def456",
                names = listOf("/postgres"),
                image = "postgres:15",
                imageId = "img456",
                created = 1698259200,
                state = "running",
                status = "Up 1 day"
            )
        )

        coEvery { mockService.getContainers(any(), any(), any()) } returns Response.success(mockContainers)

        val result = apiClient.getContainers(1, "test-token")

        assertTrue(result.isSuccess)
        val containers = result.getOrNull()!!
        assertEquals(2, containers.size)
        assertEquals("/nginx", containers[0].names[0])
        assertEquals("running", containers[0].state)

        coVerify { mockService.getContainers(any(), any(), any()) }
    }

    @Test
    fun `test getContainers with empty result`() = runBlocking {
        coEvery { mockService.getContainers(any(), any(), any()) } returns Response.success(emptyList())

        val result = apiClient.getContainers(1, "test-token")

        assertTrue(result.isSuccess)
        val containers = result.getOrNull()!!
        assertEquals(0, containers.size)
    }

    @Test
    fun `test startContainer success`() = runBlocking {
        coEvery { mockService.startContainer(any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.startContainer(1, "abc123", "test-token")

        assertTrue(result.isSuccess)

        coVerify { mockService.startContainer(any(), any(), any()) }
    }

    @Test
    fun `test stopContainer success`() = runBlocking {
        coEvery { mockService.stopContainer(any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.stopContainer(1, "abc123", "test-token")

        assertTrue(result.isSuccess)

        coVerify { mockService.stopContainer(any(), any(), any()) }
    }

    @Test
    fun `test restartContainer success`() = runBlocking {
        coEvery { mockService.restartContainer(any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.restartContainer(1, "abc123", "test-token")

        assertTrue(result.isSuccess)

        coVerify { mockService.restartContainer(any(), any(), any()) }
    }

    @Test
    fun `test pauseContainer success`() = runBlocking {
        coEvery { mockService.pauseContainer(any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.pauseContainer(1, "abc123", "test-token")

        assertTrue(result.isSuccess)

        coVerify { mockService.pauseContainer(any(), any(), any()) }
    }

    @Test
    fun `test unpauseContainer success`() = runBlocking {
        coEvery { mockService.unpauseContainer(any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.unpauseContainer(1, "abc123", "test-token")

        assertTrue(result.isSuccess)

        coVerify { mockService.unpauseContainer(any(), any(), any()) }
    }

    @Test
    fun `test removeContainer success`() = runBlocking {
        coEvery { mockService.removeContainer(any(), any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.removeContainer(1, "abc123", "test-token", force = true)

        assertTrue(result.isSuccess)

        coVerify { mockService.removeContainer(any(), any(), any(), any()) }
    }

    // ===== Resource Tests =====

    @Test
    fun `test getImages success`() = runBlocking {
        val mockImages = listOf(
            PortainerImage(
                id = "img123",
                repoTags = listOf("nginx:latest", "nginx:1.25"),
                created = 1698259200,
                size = 141231872
            ),
            PortainerImage(
                id = "img456",
                repoTags = listOf("postgres:15"),
                created = 1698172800,
                size = 347485184
            )
        )

        coEvery { mockService.getImages(any(), any()) } returns Response.success(mockImages)

        val result = apiClient.getImages(1, "test-token")

        assertTrue(result.isSuccess)
        val images = result.getOrNull()!!
        assertEquals(2, images.size)
        assertEquals("nginx:latest", images[0].repoTags!![0])

        coVerify { mockService.getImages(any(), any()) }
    }

    @Test
    fun `test getVolumes success`() = runBlocking {
        val mockVolumes = listOf(
            PortainerVolume(
                name = "postgres_data",
                driver = "local",
                mountpoint = "/var/lib/docker/volumes/postgres_data/_data",
                scope = "local"
            ),
            PortainerVolume(
                name = "nginx_config",
                driver = "local",
                mountpoint = "/var/lib/docker/volumes/nginx_config/_data",
                scope = "local"
            )
        )
        val mockResponse = PortainerVolumesResponse(volumes = mockVolumes)

        coEvery { mockService.getVolumes(any(), any()) } returns Response.success(mockResponse)

        val result = apiClient.getVolumes(1, "test-token")

        assertTrue(result.isSuccess)
        val volumes = result.getOrNull()!!
        assertEquals(2, volumes.size)
        assertEquals("postgres_data", volumes[0].name)

        coVerify { mockService.getVolumes(any(), any()) }
    }

    @Test
    fun `test getVolumes with empty result`() = runBlocking {
        val mockResponse = PortainerVolumesResponse(volumes = emptyList())

        coEvery { mockService.getVolumes(any(), any()) } returns Response.success(mockResponse)

        val result = apiClient.getVolumes(1, "test-token")

        assertTrue(result.isSuccess)
        val volumes = result.getOrNull()!!
        assertEquals(0, volumes.size)
    }

    @Test
    fun `test getNetworks success`() = runBlocking {
        val mockNetworks = listOf(
            PortainerNetwork(
                name = "bridge",
                id = "net123",
                scope = "local",
                driver = "bridge"
            ),
            PortainerNetwork(
                name = "custom_network",
                id = "net456",
                scope = "local",
                driver = "bridge"
            )
        )

        coEvery { mockService.getNetworks(any(), any()) } returns Response.success(mockNetworks)

        val result = apiClient.getNetworks(1, "test-token")

        assertTrue(result.isSuccess)
        val networks = result.getOrNull()!!
        assertEquals(2, networks.size)
        assertEquals("bridge", networks[0].name)

        coVerify { mockService.getNetworks(any(), any()) }
    }

    @Test
    fun `test getContainerStats success`() = runBlocking {
        val mockStats = PortainerContainerStats(
            read = "2025-10-25T10:00:00Z",
            preread = "2025-10-25T09:59:55Z",
            cpuStats = PortainerCPUStats(
                cpuUsage = PortainerCPUUsage(totalUsage = 1234567890),
                systemCpuUsage = 9876543210,
                onlineCpus = 4
            ),
            memoryStats = PortainerMemoryStats(
                usage = 104857600,
                limit = 2147483648
            )
        )

        coEvery { mockService.getContainerStats(any(), any(), any(), any()) } returns Response.success(mockStats)

        val result = apiClient.getContainerStats(1, "abc123", "test-token")

        assertTrue(result.isSuccess)
        val stats = result.getOrNull()!!
        assertEquals(4, stats.cpuStats?.onlineCpus)
        assertEquals(104857600L, stats.memoryStats?.usage)

        coVerify { mockService.getContainerStats(any(), any(), any(), any()) }
    }

    // ===== Error Handling Tests =====

    @Test
    fun `test HTTP 404 error handling`() = runBlocking {
        coEvery { mockService.getContainers(any(), any(), any()) } returns Response.error(404, mockk(relaxed = true))

        val result = apiClient.getContainers(1, "test-token")

        assertTrue(result.isFailure)
    }

    @Test
    fun `test HTTP 401 unauthorized error`() = runBlocking {
        coEvery { mockService.getEndpoints(any()) } returns Response.error(401, mockk(relaxed = true))

        val result = apiClient.getEndpoints("invalid-token")

        assertTrue(result.isFailure)
    }

    @Test
    fun `test exception handling`() = runBlocking {
        coEvery { mockService.getStatus() } throws Exception("Network error")

        val result = apiClient.getStatus()

        assertTrue(result.isFailure)
    }

    // ===== Auth Header Tests =====

    @Test
    fun `test auth header generation`() {
        val header = apiClient.getAuthHeader("my-jwt-token")

        assertNotNull(header)
        assertEquals("Bearer my-jwt-token", header)
    }
}
