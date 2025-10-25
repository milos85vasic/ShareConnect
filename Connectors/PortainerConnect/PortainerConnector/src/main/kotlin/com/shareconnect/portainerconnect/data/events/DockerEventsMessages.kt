package com.shareconnect.portainerconnect.data.events

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.shareconnect.websocket.WebSocketMessage

/**
 * Docker Events API message types
 * Docker uses HTTP streaming for /events endpoint, not WebSocket
 * These messages represent event stream updates wrapped in WebSocket message format
 *
 * Reference: https://docs.docker.com/engine/api/v1.43/#tag/System/operation/SystemEvents
 */

/**
 * Base class for Docker event messages
 */
sealed class DockerEventMessage : WebSocketMessage() {
    abstract val timestamp: Long
}

/**
 * Container event message
 */
data class ContainerEventMessage(
    val eventType: String, // create, start, stop, die, destroy, pause, unpause, restart, kill, health_status, etc.
    val containerId: String,
    val containerName: String,
    val image: String,
    val attributes: Map<String, String> = emptyMap(),
    val time: Long, // Event timestamp
    val timeNano: Long? = null,
    override val timestamp: Long = System.currentTimeMillis()
) : DockerEventMessage() {
    override val type = "container_event"

    override fun toJson(): String = ""
}

/**
 * Image event message
 */
data class ImageEventMessage(
    val eventType: String, // pull, push, delete, tag, untag, save, load
    val imageId: String,
    val imageName: String?,
    val attributes: Map<String, String> = emptyMap(),
    val time: Long,
    val timeNano: Long? = null,
    override val timestamp: Long = System.currentTimeMillis()
) : DockerEventMessage() {
    override val type = "image_event"

    override fun toJson(): String = ""
}

/**
 * Network event message
 */
data class NetworkEventMessage(
    val eventType: String, // create, connect, disconnect, destroy, remove
    val networkId: String,
    val networkName: String,
    val container: String? = null,
    val attributes: Map<String, String> = emptyMap(),
    val time: Long,
    val timeNano: Long? = null,
    override val timestamp: Long = System.currentTimeMillis()
) : DockerEventMessage() {
    override val type = "network_event"

    override fun toJson(): String = ""
}

/**
 * Volume event message
 */
data class VolumeEventMessage(
    val eventType: String, // create, mount, unmount, destroy
    val volumeName: String,
    val driver: String?,
    val attributes: Map<String, String> = emptyMap(),
    val time: Long,
    val timeNano: Long? = null,
    override val timestamp: Long = System.currentTimeMillis()
) : DockerEventMessage() {
    override val type = "volume_event"

    override fun toJson(): String = ""
}

/**
 * Service event message (Docker Swarm)
 */
data class ServiceEventMessage(
    val eventType: String, // create, update, remove
    val serviceId: String,
    val serviceName: String,
    val attributes: Map<String, String> = emptyMap(),
    val time: Long,
    val timeNano: Long? = null,
    override val timestamp: Long = System.currentTimeMillis()
) : DockerEventMessage() {
    override val type = "service_event"

    override fun toJson(): String = ""
}

/**
 * Node event message (Docker Swarm)
 */
data class NodeEventMessage(
    val eventType: String, // create, update, remove
    val nodeId: String,
    val nodeName: String?,
    val attributes: Map<String, String> = emptyMap(),
    val time: Long,
    val timeNano: Long? = null,
    override val timestamp: Long = System.currentTimeMillis()
) : DockerEventMessage() {
    override val type = "node_event"

    override fun toJson(): String = ""
}

/**
 * Daemon event message
 */
data class DaemonEventMessage(
    val eventType: String, // reload
    val attributes: Map<String, String> = emptyMap(),
    val time: Long,
    val timeNano: Long? = null,
    override val timestamp: Long = System.currentTimeMillis()
) : DockerEventMessage() {
    override val type = "daemon_event"

    override fun toJson(): String = ""
}

/**
 * Stream connection status message
 */
data class StreamConnectionMessage(
    val connected: Boolean,
    val endpointUrl: String,
    val errorMessage: String? = null,
    override val timestamp: Long = System.currentTimeMillis()
) : DockerEventMessage() {
    override val type = "stream_connection"

    override fun toJson(): String {
        return """{"type":"stream_connection","connected":$connected,"endpoint_url":"$endpointUrl","timestamp":$timestamp}"""
    }
}

/**
 * Stream error message
 */
data class StreamErrorMessage(
    val error: String,
    val errorCode: Int? = null,
    override val timestamp: Long = System.currentTimeMillis()
) : DockerEventMessage() {
    override val type = "stream_error"

    override fun toJson(): String {
        return if (errorCode != null) {
            """{"type":"stream_error","error":"$error","error_code":$errorCode,"timestamp":$timestamp}"""
        } else {
            """{"type":"stream_error","error":"$error","timestamp":$timestamp}"""
        }
    }
}

/**
 * Parse Docker event messages from JSON
 * Docker events API returns newline-delimited JSON stream
 */
class DockerEventParser {
    private val gson = Gson()

    fun parse(json: String): DockerEventMessage? {
        return try {
            val jsonObject = gson.fromJson(json, JsonObject::class.java)

            // Check for our custom message types first
            val messageType = jsonObject.get("type")?.asString
            if (messageType != null) {
                return when (messageType) {
                    "stream_connection" -> parseStreamConnection(jsonObject)
                    "stream_error" -> parseStreamError(jsonObject)
                    else -> null
                }
            }

            // Parse Docker event format
            val dockerType = jsonObject.get("Type")?.asString ?: return null
            val action = jsonObject.get("Action")?.asString ?: ""
            val time = jsonObject.get("time")?.asLong ?: 0
            val timeNano = jsonObject.get("timeNano")?.asLong

            val actor = jsonObject.getAsJsonObject("Actor")
            val attributes = parseAttributes(actor?.getAsJsonObject("Attributes"))

            when (dockerType) {
                "container" -> {
                    val id = actor?.get("ID")?.asString ?: ""
                    val containerName = attributes["name"] ?: ""
                    val image = attributes["image"] ?: ""

                    ContainerEventMessage(
                        eventType = action,
                        containerId = id,
                        containerName = containerName,
                        image = image,
                        attributes = attributes,
                        time = time,
                        timeNano = timeNano
                    )
                }

                "image" -> {
                    val id = actor?.get("ID")?.asString ?: ""
                    val imageName = attributes["name"]

                    ImageEventMessage(
                        eventType = action,
                        imageId = id,
                        imageName = imageName,
                        attributes = attributes,
                        time = time,
                        timeNano = timeNano
                    )
                }

                "network" -> {
                    val id = actor?.get("ID")?.asString ?: ""
                    val networkName = attributes["name"] ?: ""
                    val container = attributes["container"]

                    NetworkEventMessage(
                        eventType = action,
                        networkId = id,
                        networkName = networkName,
                        container = container,
                        attributes = attributes,
                        time = time,
                        timeNano = timeNano
                    )
                }

                "volume" -> {
                    val volumeName = actor?.get("ID")?.asString ?: ""
                    val driver = attributes["driver"]

                    VolumeEventMessage(
                        eventType = action,
                        volumeName = volumeName,
                        driver = driver,
                        attributes = attributes,
                        time = time,
                        timeNano = timeNano
                    )
                }

                "service" -> {
                    val id = actor?.get("ID")?.asString ?: ""
                    val serviceName = attributes["name"] ?: ""

                    ServiceEventMessage(
                        eventType = action,
                        serviceId = id,
                        serviceName = serviceName,
                        attributes = attributes,
                        time = time,
                        timeNano = timeNano
                    )
                }

                "node" -> {
                    val id = actor?.get("ID")?.asString ?: ""
                    val nodeName = attributes["name"]

                    NodeEventMessage(
                        eventType = action,
                        nodeId = id,
                        nodeName = nodeName,
                        attributes = attributes,
                        time = time,
                        timeNano = timeNano
                    )
                }

                "daemon" -> {
                    DaemonEventMessage(
                        eventType = action,
                        attributes = attributes,
                        time = time,
                        timeNano = timeNano
                    )
                }

                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseAttributes(attributesObj: JsonObject?): Map<String, String> {
        if (attributesObj == null) return emptyMap()

        return try {
            attributesObj.entrySet().associate { (key, value) ->
                key to value.asString
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun parseStreamConnection(json: JsonObject): StreamConnectionMessage {
        return StreamConnectionMessage(
            connected = json.get("connected")?.asBoolean ?: false,
            endpointUrl = json.get("endpoint_url")?.asString ?: "",
            errorMessage = json.get("error_message")?.asString,
            timestamp = json.get("timestamp")?.asLong ?: System.currentTimeMillis()
        )
    }

    private fun parseStreamError(json: JsonObject): StreamErrorMessage {
        return StreamErrorMessage(
            error = json.get("error")?.asString ?: "Unknown error",
            errorCode = json.get("error_code")?.asInt,
            timestamp = json.get("timestamp")?.asLong ?: System.currentTimeMillis()
        )
    }
}
