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


package com.shareconnect.portainerconnect.data.events

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = com.shareconnect.portainerconnect.TestApplication::class)
class DockerEventsMessagesTest {

    private val parser = DockerEventParser()

    @Test
    fun `test ContainerEventMessage creation`() {
        val message = ContainerEventMessage(
            eventType = "start",
            containerId = "abc123",
            containerName = "my-container",
            image = "nginx:latest",
            attributes = mapOf("name" to "my-container", "image" to "nginx:latest"),
            time = 1234567890L
        )

        assertEquals("container_event", message.type)
        assertEquals("start", message.eventType)
        assertEquals("abc123", message.containerId)
        assertEquals("my-container", message.containerName)
        assertEquals("nginx:latest", message.image)
        assertEquals(2, message.attributes.size)
    }

    @Test
    fun `test ImageEventMessage creation`() {
        val message = ImageEventMessage(
            eventType = "pull",
            imageId = "sha256:abc123",
            imageName = "nginx:latest",
            attributes = mapOf("name" to "nginx:latest"),
            time = 1234567890L
        )

        assertEquals("image_event", message.type)
        assertEquals("pull", message.eventType)
        assertEquals("sha256:abc123", message.imageId)
        assertEquals("nginx:latest", message.imageName)
    }

    @Test
    fun `test NetworkEventMessage creation`() {
        val message = NetworkEventMessage(
            eventType = "connect",
            networkId = "net123",
            networkName = "bridge",
            container = "container123",
            attributes = mapOf("name" to "bridge", "container" to "container123"),
            time = 1234567890L
        )

        assertEquals("network_event", message.type)
        assertEquals("connect", message.eventType)
        assertEquals("net123", message.networkId)
        assertEquals("bridge", message.networkName)
        assertEquals("container123", message.container)
    }

    @Test
    fun `test VolumeEventMessage creation`() {
        val message = VolumeEventMessage(
            eventType = "create",
            volumeName = "my-volume",
            driver = "local",
            attributes = mapOf("driver" to "local"),
            time = 1234567890L
        )

        assertEquals("volume_event", message.type)
        assertEquals("create", message.eventType)
        assertEquals("my-volume", message.volumeName)
        assertEquals("local", message.driver)
    }

    @Test
    fun `test ServiceEventMessage creation`() {
        val message = ServiceEventMessage(
            eventType = "update",
            serviceId = "service123",
            serviceName = "web",
            attributes = mapOf("name" to "web"),
            time = 1234567890L
        )

        assertEquals("service_event", message.type)
        assertEquals("update", message.eventType)
        assertEquals("service123", message.serviceId)
        assertEquals("web", message.serviceName)
    }

    @Test
    fun `test NodeEventMessage creation`() {
        val message = NodeEventMessage(
            eventType = "update",
            nodeId = "node123",
            nodeName = "node-01",
            attributes = mapOf("name" to "node-01"),
            time = 1234567890L
        )

        assertEquals("node_event", message.type)
        assertEquals("update", message.eventType)
        assertEquals("node123", message.nodeId)
        assertEquals("node-01", message.nodeName)
    }

    @Test
    fun `test DaemonEventMessage creation`() {
        val message = DaemonEventMessage(
            eventType = "reload",
            attributes = emptyMap(),
            time = 1234567890L
        )

        assertEquals("daemon_event", message.type)
        assertEquals("reload", message.eventType)
    }

    @Test
    fun `test StreamConnectionMessage serialization`() {
        val message = StreamConnectionMessage(
            connected = true,
            endpointUrl = "http://localhost:9000",
            errorMessage = null,
            timestamp = 1234567890L
        )

        assertEquals("stream_connection", message.type)
        assertTrue(message.connected)
        assertEquals("http://localhost:9000", message.endpointUrl)

        val json = message.toJson()
        assertTrue(json.contains("\"type\":\"stream_connection\""))
        assertTrue(json.contains("\"connected\":true"))
        assertTrue(json.contains("\"endpoint_url\":\"http://localhost:9000\""))
    }

    @Test
    fun `test StreamErrorMessage serialization`() {
        val message = StreamErrorMessage(
            error = "Connection failed",
            errorCode = 500,
            timestamp = 1234567890L
        )

        assertEquals("stream_error", message.type)
        assertEquals("Connection failed", message.error)
        assertEquals(500, message.errorCode)

        val json = message.toJson()
        assertTrue(json.contains("\"type\":\"stream_error\""))
        assertTrue(json.contains("\"error\":\"Connection failed\""))
        assertTrue(json.contains("\"error_code\":500"))
    }

    @Test
    fun `test parse Docker container start event`() {
        val json = """
            {
                "Type":"container",
                "Action":"start",
                "Actor":{
                    "ID":"abc123def456",
                    "Attributes":{
                        "image":"nginx:latest",
                        "name":"my-nginx"
                    }
                },
                "time":1234567890,
                "timeNano":1234567890000000000
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is ContainerEventMessage)
        val containerEvent = message as ContainerEventMessage

        assertEquals("start", containerEvent.eventType)
        assertEquals("abc123def456", containerEvent.containerId)
        assertEquals("my-nginx", containerEvent.containerName)
        assertEquals("nginx:latest", containerEvent.image)
        assertEquals(1234567890L, containerEvent.time)
    }

    @Test
    fun `test parse Docker container stop event`() {
        val json = """
            {
                "Type":"container",
                "Action":"stop",
                "Actor":{
                    "ID":"container456",
                    "Attributes":{
                        "image":"redis:alpine",
                        "name":"my-redis",
                        "exitCode":"0"
                    }
                },
                "time":1234567900
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is ContainerEventMessage)
        val containerEvent = message as ContainerEventMessage

        assertEquals("stop", containerEvent.eventType)
        assertEquals("container456", containerEvent.containerId)
        assertEquals("my-redis", containerEvent.containerName)
        assertEquals("redis:alpine", containerEvent.image)
        assertEquals("0", containerEvent.attributes["exitCode"])
    }

    @Test
    fun `test parse Docker image pull event`() {
        val json = """
            {
                "Type":"image",
                "Action":"pull",
                "Actor":{
                    "ID":"sha256:abc123",
                    "Attributes":{
                        "name":"nginx:latest"
                    }
                },
                "time":1234567890
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is ImageEventMessage)
        val imageEvent = message as ImageEventMessage

        assertEquals("pull", imageEvent.eventType)
        assertEquals("sha256:abc123", imageEvent.imageId)
        assertEquals("nginx:latest", imageEvent.imageName)
    }

    @Test
    fun `test parse Docker network connect event`() {
        val json = """
            {
                "Type":"network",
                "Action":"connect",
                "Actor":{
                    "ID":"network123",
                    "Attributes":{
                        "name":"bridge",
                        "container":"abc123def456",
                        "type":"bridge"
                    }
                },
                "time":1234567890
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is NetworkEventMessage)
        val networkEvent = message as NetworkEventMessage

        assertEquals("connect", networkEvent.eventType)
        assertEquals("network123", networkEvent.networkId)
        assertEquals("bridge", networkEvent.networkName)
        assertEquals("abc123def456", networkEvent.container)
    }

    @Test
    fun `test parse Docker volume create event`() {
        val json = """
            {
                "Type":"volume",
                "Action":"create",
                "Actor":{
                    "ID":"my-volume",
                    "Attributes":{
                        "driver":"local"
                    }
                },
                "time":1234567890
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is VolumeEventMessage)
        val volumeEvent = message as VolumeEventMessage

        assertEquals("create", volumeEvent.eventType)
        assertEquals("my-volume", volumeEvent.volumeName)
        assertEquals("local", volumeEvent.driver)
    }

    @Test
    fun `test parse Docker service update event`() {
        val json = """
            {
                "Type":"service",
                "Action":"update",
                "Actor":{
                    "ID":"service123",
                    "Attributes":{
                        "name":"web"
                    }
                },
                "time":1234567890
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is ServiceEventMessage)
        val serviceEvent = message as ServiceEventMessage

        assertEquals("update", serviceEvent.eventType)
        assertEquals("service123", serviceEvent.serviceId)
        assertEquals("web", serviceEvent.serviceName)
    }

    @Test
    fun `test parse Docker daemon reload event`() {
        val json = """
            {
                "Type":"daemon",
                "Action":"reload",
                "Actor":{
                    "ID":"",
                    "Attributes":{}
                },
                "time":1234567890
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is DaemonEventMessage)
        val daemonEvent = message as DaemonEventMessage

        assertEquals("reload", daemonEvent.eventType)
    }

    @Test
    fun `test parse StreamConnectionMessage`() {
        val json = """
            {
                "type":"stream_connection",
                "connected":true,
                "endpoint_url":"http://localhost:9000",
                "timestamp":1234567890
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is StreamConnectionMessage)
        val connectionMessage = message as StreamConnectionMessage

        assertTrue(connectionMessage.connected)
        assertEquals("http://localhost:9000", connectionMessage.endpointUrl)
        assertNull(connectionMessage.errorMessage)
    }

    @Test
    fun `test parse StreamErrorMessage`() {
        val json = """
            {
                "type":"stream_error",
                "error":"Connection timeout",
                "error_code":408,
                "timestamp":1234567890
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is StreamErrorMessage)
        val errorMessage = message as StreamErrorMessage

        assertEquals("Connection timeout", errorMessage.error)
        assertEquals(408, errorMessage.errorCode)
    }

    @Test
    fun `test parse invalid JSON`() {
        val json = """{"invalid json"""

        val message = parser.parse(json)

        assertNull(message)
    }

    @Test
    fun `test parse unknown event type`() {
        val json = """{"Type":"unknown","Action":"test","time":1234567890}"""

        val message = parser.parse(json)

        assertNull(message)
    }

    @Test
    fun `test parse event without Type field`() {
        val json = """{"Action":"test","time":1234567890}"""

        val message = parser.parse(json)

        assertNull(message)
    }

    @Test
    fun `test container event with health status`() {
        val json = """
            {
                "Type":"container",
                "Action":"health_status: healthy",
                "Actor":{
                    "ID":"container789",
                    "Attributes":{
                        "image":"postgres:14",
                        "name":"my-postgres",
                        "health":"healthy"
                    }
                },
                "time":1234567890
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is ContainerEventMessage)
        val containerEvent = message as ContainerEventMessage

        assertEquals("health_status: healthy", containerEvent.eventType)
        assertEquals("healthy", containerEvent.attributes["health"])
    }

    @Test
    fun `test timestamp defaults to current time`() {
        val message = ContainerEventMessage(
            eventType = "create",
            containerId = "test123",
            containerName = "test",
            image = "test:latest",
            time = 1234567890L
        )

        assertTrue(message.timestamp > 0)
        assertTrue(message.timestamp <= System.currentTimeMillis())
    }
}
