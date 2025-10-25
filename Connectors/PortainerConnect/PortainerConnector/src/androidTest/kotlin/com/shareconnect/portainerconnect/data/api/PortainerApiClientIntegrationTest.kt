package com.shareconnect.portainerconnect.data.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.portainerconnect.data.models.*
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Integration tests for PortainerApiClient using MockWebServer
 * Tests real HTTP requests and responses with mock server
 * Ensures end-to-end functionality of the API client
 */
@RunWith(AndroidJUnit4::class)
class PortainerApiClientIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: PortainerApiClient
    private lateinit var apiService: PortainerApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(PortainerApiService::class.java)
        apiClient = PortainerApiClient(
            serverUrl = mockWebServer.url("/").toString(),
            portainerApiService = apiService
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // ===== Authentication Integration Tests =====

    @Test
    fun authenticate_withValidCredentials_returnsJwtToken() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"jwt": "valid-jwt-token-abc123"}""")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.authenticate("admin", "password123")

        // Then
        assertTrue("Authentication should succeed", result.isSuccess)
        val authResponse = result.getOrNull()!!
        assertEquals("valid-jwt-token-abc123", authResponse.jwt)

        // Verify request
        val request = mockWebServer.takeRequest()
        assertEquals("POST", request.method)
        assertTrue(request.path!!.contains("auth"))
    }

    @Test
    fun authenticate_withInvalidCredentials_returnsFailure() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(401)
            .setBody("""{"message": "Invalid credentials"}""")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.authenticate("admin", "wrongpassword")

        // Then
        assertTrue("Authentication should fail", result.isFailure)
    }

    @Test
    fun authenticate_withNetworkError_returnsFailure() = runTest {
        // Given - shutdown server to simulate network error
        mockWebServer.shutdown()

        // When
        val result = apiClient.authenticate("admin", "password")

        // Then
        assertTrue("Should handle network error", result.isFailure)
    }

    // ===== Status Integration Tests =====

    @Test
    fun getStatus_returnsPortainerVersion() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "Version": "2.19.4",
                    "Edition": "CE",
                    "InstanceID": "instance-uuid-123"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getStatus()

        // Then
        assertTrue(result.isSuccess)
        val status = result.getOrNull()!!
        assertEquals("2.19.4", status.version)
        assertEquals("CE", status.edition)
        assertEquals("instance-uuid-123", status.instanceId)
    }

    @Test
    fun getStatus_withServerError_returnsFailure() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(500)
            .setBody("""{"message": "Internal server error"}""")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getStatus()

        // Then
        assertTrue(result.isFailure)
    }

    // ===== Endpoint Integration Tests =====

    @Test
    fun getEndpoints_withValidToken_returnsEndpointList() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                [
                    {
                        "Id": 1,
                        "Name": "local",
                        "Type": 1,
                        "URL": "unix:///var/run/docker.sock",
                        "Status": 1
                    },
                    {
                        "Id": 2,
                        "Name": "production",
                        "Type": 2,
                        "URL": "tcp://192.168.1.100:2375",
                        "Status": 1
                    }
                ]
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getEndpoints("test-jwt-token")

        // Then
        assertTrue(result.isSuccess)
        val endpoints = result.getOrNull()!!
        assertEquals(2, endpoints.size)
        assertEquals("local", endpoints[0].name)
        assertEquals("production", endpoints[1].name)
        assertEquals(1, endpoints[0].type) // Docker
        assertEquals(2, endpoints[1].type) // Agent

        // Verify authorization header was sent
        val request = mockWebServer.takeRequest()
        assertTrue(request.headers["Authorization"]!!.contains("Bearer"))
    }

    @Test
    fun getEndpoints_withInvalidToken_returnsUnauthorized() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(401)
            .setBody("""{"message": "Unauthorized"}""")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getEndpoints("invalid-token")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun getEndpoints_withEmptyResponse_returnsEmptyList() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("[]")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getEndpoints("test-token")

        // Then
        assertTrue(result.isSuccess)
        val endpoints = result.getOrNull()!!
        assertEquals(0, endpoints.size)
    }

    // ===== Container Management Integration Tests =====

    @Test
    fun getContainers_withRunningContainers_returnsContainerList() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                [
                    {
                        "Id": "container123abc",
                        "Names": ["/nginx"],
                        "Image": "nginx:latest",
                        "ImageID": "sha256:img123",
                        "Created": 1698345600,
                        "State": "running",
                        "Status": "Up 2 hours",
                        "Ports": [
                            {
                                "PrivatePort": 80,
                                "PublicPort": 8080,
                                "Type": "tcp"
                            }
                        ]
                    },
                    {
                        "Id": "container456def",
                        "Names": ["/postgres"],
                        "Image": "postgres:15",
                        "ImageID": "sha256:img456",
                        "Created": 1698259200,
                        "State": "exited",
                        "Status": "Exited (0) 1 hour ago"
                    }
                ]
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getContainers(1, "test-token", all = true)

        // Then
        assertTrue(result.isSuccess)
        val containers = result.getOrNull()!!
        assertEquals(2, containers.size)
        assertEquals("/nginx", containers[0].names[0])
        assertEquals("running", containers[0].state)
        assertEquals("/postgres", containers[1].names[0])
        assertEquals("exited", containers[1].state)
    }

    @Test
    fun startContainer_withValidContainerId_startsSuccessfully() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.startContainer(1, "container123", "test-token")

        // Then
        assertTrue(result.isSuccess)

        // Verify correct endpoint was called
        val request = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("containers/container123/start"))
    }

    @Test
    fun stopContainer_withValidContainerId_stopsSuccessfully() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.stopContainer(1, "container123", "test-token")

        // Then
        assertTrue(result.isSuccess)

        // Verify correct endpoint was called
        val request = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("containers/container123/stop"))
    }

    @Test
    fun restartContainer_withValidContainerId_restartsSuccessfully() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.restartContainer(1, "container123", "test-token")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun pauseContainer_withValidContainerId_pausesSuccessfully() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.pauseContainer(1, "container123", "test-token")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun unpauseContainer_withValidContainerId_unpausesSuccessfully() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.unpauseContainer(1, "container123", "test-token")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun removeContainer_withForceFlag_removesSuccessfully() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.removeContainer(1, "container123", "test-token", force = true)

        // Then
        assertTrue(result.isSuccess)

        // Verify force parameter was sent
        val request = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("force=true"))
    }

    @Test
    fun removeContainer_withoutForceFlag_sendsCorrectParameter() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.removeContainer(1, "container123", "test-token", force = false)

        // Then
        assertTrue(result.isSuccess)

        // Verify force parameter was sent as false
        val request = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("force=false"))
    }

    // ===== Resource Management Integration Tests =====

    @Test
    fun getImages_returnsImageList() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                [
                    {
                        "Id": "sha256:img123",
                        "RepoTags": ["nginx:latest", "nginx:1.25"],
                        "Created": 1698259200,
                        "Size": 141231872,
                        "VirtualSize": 141231872
                    },
                    {
                        "Id": "sha256:img456",
                        "RepoTags": ["postgres:15"],
                        "Created": 1698172800,
                        "Size": 347485184,
                        "VirtualSize": 347485184
                    }
                ]
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getImages(1, "test-token")

        // Then
        assertTrue(result.isSuccess)
        val images = result.getOrNull()!!
        assertEquals(2, images.size)
        assertEquals("nginx:latest", images[0].repoTags!![0])
        assertEquals(141231872L, images[0].size)
    }

    @Test
    fun getVolumes_returnsVolumeList() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "Volumes": [
                        {
                            "Name": "postgres_data",
                            "Driver": "local",
                            "Mountpoint": "/var/lib/docker/volumes/postgres_data/_data",
                            "Scope": "local"
                        },
                        {
                            "Name": "nginx_config",
                            "Driver": "local",
                            "Mountpoint": "/var/lib/docker/volumes/nginx_config/_data",
                            "Scope": "local"
                        }
                    ],
                    "Warnings": null
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getVolumes(1, "test-token")

        // Then
        assertTrue(result.isSuccess)
        val volumes = result.getOrNull()!!
        assertEquals(2, volumes.size)
        assertEquals("postgres_data", volumes[0].name)
        assertEquals("local", volumes[0].driver)
    }

    @Test
    fun getNetworks_returnsNetworkList() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                [
                    {
                        "Name": "bridge",
                        "Id": "net123abc",
                        "Scope": "local",
                        "Driver": "bridge",
                        "EnableIPv6": false,
                        "Internal": false
                    },
                    {
                        "Name": "custom_network",
                        "Id": "net456def",
                        "Scope": "local",
                        "Driver": "bridge",
                        "EnableIPv6": false,
                        "Internal": false
                    }
                ]
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getNetworks(1, "test-token")

        // Then
        assertTrue(result.isSuccess)
        val networks = result.getOrNull()!!
        assertEquals(2, networks.size)
        assertEquals("bridge", networks[0].name)
        assertEquals("bridge", networks[0].driver)
    }

    @Test
    fun getContainerStats_returnsStatistics() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "read": "2025-10-25T10:30:00Z",
                    "preread": "2025-10-25T10:29:55Z",
                    "cpu_stats": {
                        "cpu_usage": {
                            "total_usage": 1234567890
                        },
                        "system_cpu_usage": 9876543210,
                        "online_cpus": 4
                    },
                    "memory_stats": {
                        "usage": 104857600,
                        "limit": 2147483648
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getContainerStats(1, "container123", "test-token")

        // Then
        assertTrue(result.isSuccess)
        val stats = result.getOrNull()!!
        assertEquals(4, stats.cpuStats?.onlineCpus)
        assertEquals(104857600L, stats.memoryStats?.usage)
        assertEquals(2147483648L, stats.memoryStats?.limit)
    }

    // ===== Error Handling Integration Tests =====

    @Test
    fun apiCall_withTimeout_handlesGracefully() = runTest {
        // Given - server with delayed response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"jwt": "token"}""")
            .setBodyDelay(35, java.util.concurrent.TimeUnit.SECONDS)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.authenticate("admin", "password")

        // Then - should timeout and return failure
        assertTrue(result.isFailure)
    }

    @Test
    fun apiCall_withMalformedResponse_handlesGracefully() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("invalid json {{{")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getStatus()

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun apiCall_with404Error_returnsFailure() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(404)
            .setBody("""{"message": "Not found"}""")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getContainers(999, "test-token")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun apiCall_with500Error_returnsFailure() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(500)
            .setBody("""{"message": "Internal server error"}""")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getEndpoints("test-token")

        // Then
        assertTrue(result.isFailure)
    }
}
