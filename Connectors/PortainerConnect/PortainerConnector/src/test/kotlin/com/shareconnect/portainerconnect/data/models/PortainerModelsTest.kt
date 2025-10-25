package com.shareconnect.portainerconnect.data.models

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for Portainer data models
 * Ensures proper data class structure and serialization
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = com.shareconnect.portainerconnect.TestApplication::class)
class PortainerModelsTest {

    @Test
    fun `test PortainerAuthRequest creation`() {
        val authRequest = PortainerAuthRequest(
            username = "admin",
            password = "secretpassword"
        )

        assertEquals("admin", authRequest.username)
        assertEquals("secretpassword", authRequest.password)
    }

    @Test
    fun `test PortainerAuthResponse creation`() {
        val authResponse = PortainerAuthResponse(jwt = "token123")

        assertEquals("token123", authResponse.jwt)
    }

    @Test
    fun `test PortainerStatus creation with all fields`() {
        val status = PortainerStatus(
            version = "2.19.4",
            edition = "CE",
            instanceId = "instance-uuid"
        )

        assertEquals("2.19.4", status.version)
        assertEquals("CE", status.edition)
        assertEquals("instance-uuid", status.instanceId)
    }

    @Test
    fun `test PortainerStatus creation with optional fields null`() {
        val status = PortainerStatus(
            version = "2.19.4",
            edition = null,
            instanceId = null
        )

        assertEquals("2.19.4", status.version)
        assertNull(status.edition)
        assertNull(status.instanceId)
    }

    @Test
    fun `test PortainerEndpoint creation`() {
        val endpoint = PortainerEndpoint(
            id = 1,
            name = "local",
            type = 1,
            url = "unix:///var/run/docker.sock",
            status = 1
        )

        assertEquals(1, endpoint.id)
        assertEquals("local", endpoint.name)
        assertEquals(1, endpoint.type)
        assertEquals("unix:///var/run/docker.sock", endpoint.url)
        assertEquals(1, endpoint.status)
    }

    @Test
    fun `test PortainerContainer creation with minimal fields`() {
        val container = PortainerContainer(
            id = "abc123",
            names = listOf("/nginx"),
            image = "nginx:latest",
            imageId = "img123",
            created = 1698345600,
            state = "running",
            status = "Up 2 hours"
        )

        assertEquals("abc123", container.id)
        assertEquals(listOf("/nginx"), container.names)
        assertEquals("nginx:latest", container.image)
        assertEquals("running", container.state)
    }

    @Test
    fun `test PortainerContainer with ports`() {
        val ports = listOf(
            PortainerPort(
                privatePort = 80,
                publicPort = 8080,
                type = "tcp",
                ip = "0.0.0.0"
            )
        )

        val container = PortainerContainer(
            id = "abc123",
            names = listOf("/nginx"),
            image = "nginx:latest",
            imageId = "img123",
            created = 1698345600,
            state = "running",
            status = "Up 2 hours",
            ports = ports
        )

        assertNotNull(container.ports)
        assertEquals(1, container.ports!!.size)
        assertEquals(80, container.ports!![0].privatePort)
        assertEquals(8080, container.ports!![0].publicPort)
    }

    @Test
    fun `test PortainerImage creation`() {
        val image = PortainerImage(
            id = "sha256:img123",
            repoTags = listOf("nginx:latest", "nginx:1.25"),
            created = 1698259200,
            size = 141231872
        )

        assertEquals("sha256:img123", image.id)
        assertEquals(listOf("nginx:latest", "nginx:1.25"), image.repoTags)
        assertEquals(141231872L, image.size)
    }

    @Test
    fun `test PortainerVolume creation`() {
        val volume = PortainerVolume(
            name = "postgres_data",
            driver = "local",
            mountpoint = "/var/lib/docker/volumes/postgres_data/_data",
            scope = "local"
        )

        assertEquals("postgres_data", volume.name)
        assertEquals("local", volume.driver)
        assertEquals("/var/lib/docker/volumes/postgres_data/_data", volume.mountpoint)
        assertEquals("local", volume.scope)
    }

    @Test
    fun `test PortainerNetwork creation`() {
        val network = PortainerNetwork(
            name = "bridge",
            id = "net123",
            scope = "local",
            driver = "bridge"
        )

        assertEquals("bridge", network.name)
        assertEquals("net123", network.id)
        assertEquals("local", network.scope)
        assertEquals("bridge", network.driver)
    }

    @Test
    fun `test PortainerContainerStats creation`() {
        val cpuUsage = PortainerCPUUsage(totalUsage = 1234567890)
        val cpuStats = PortainerCPUStats(
            cpuUsage = cpuUsage,
            systemCpuUsage = 9876543210,
            onlineCpus = 4
        )
        val memoryStats = PortainerMemoryStats(
            usage = 104857600,
            limit = 2147483648
        )

        val stats = PortainerContainerStats(
            read = "2025-10-25T10:30:00Z",
            preread = "2025-10-25T10:29:55Z",
            cpuStats = cpuStats,
            memoryStats = memoryStats
        )

        assertEquals("2025-10-25T10:30:00Z", stats.read)
        assertEquals(4, stats.cpuStats?.onlineCpus)
        assertEquals(104857600L, stats.memoryStats?.usage)
    }

    @Test
    fun `test PortainerVolumesResponse with volumes`() {
        val volumes = listOf(
            PortainerVolume(
                name = "vol1",
                driver = "local",
                mountpoint = "/path1",
                scope = "local"
            ),
            PortainerVolume(
                name = "vol2",
                driver = "local",
                mountpoint = "/path2",
                scope = "local"
            )
        )

        val response = PortainerVolumesResponse(
            volumes = volumes,
            warnings = null
        )

        assertNotNull(response.volumes)
        assertEquals(2, response.volumes!!.size)
        assertNull(response.warnings)
    }

    @Test
    fun `test PortainerVolumesResponse with warnings`() {
        val response = PortainerVolumesResponse(
            volumes = emptyList(),
            warnings = listOf("Warning 1", "Warning 2")
        )

        assertEquals(0, response.volumes!!.size)
        assertNotNull(response.warnings)
        assertEquals(2, response.warnings!!.size)
    }

    @Test
    fun `test PortainerMount creation`() {
        val mount = PortainerMount(
            type = "volume",
            source = "postgres_data",
            destination = "/var/lib/postgresql/data",
            mode = "rw",
            rw = true,
            propagation = "rprivate"
        )

        assertEquals("volume", mount.type)
        assertEquals("postgres_data", mount.source)
        assertEquals("/var/lib/postgresql/data", mount.destination)
        assertTrue(mount.rw!!)
    }

    @Test
    fun `test PortainerNetworkConfig creation`() {
        val networkConfig = PortainerNetworkConfig(
            networkId = "net123",
            endpointId = "endpoint123",
            gateway = "172.17.0.1",
            ipAddress = "172.17.0.2",
            ipPrefixLen = 16
        )

        assertEquals("net123", networkConfig.networkId)
        assertEquals("172.17.0.2", networkConfig.ipAddress)
        assertEquals(16, networkConfig.ipPrefixLen)
    }
}
