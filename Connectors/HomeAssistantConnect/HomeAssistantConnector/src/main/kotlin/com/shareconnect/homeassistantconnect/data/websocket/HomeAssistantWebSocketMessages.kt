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


package com.shareconnect.homeassistantconnect.data.websocket

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.shareconnect.websocket.WebSocketMessage

/**
 * Home Assistant WebSocket API message types
 * Reference: https://developers.home-assistant.io/docs/api/websocket
 */

/**
 * Base class for Home Assistant WebSocket messages
 */
sealed class HomeAssistantWebSocketMessage : WebSocketMessage() {
    abstract val id: Int?
}

/**
 * Authentication message (first message sent after connection)
 */
data class AuthMessage(
    val accessToken: String
) : HomeAssistantWebSocketMessage() {
    override val type = "auth"
    override val id: Int? = null

    override fun toJson(): String {
        return """{"type":"auth","access_token":"$accessToken"}"""
    }
}

/**
 * Authentication OK response
 */
data class AuthOkMessage(
    val haVersion: String
) : HomeAssistantWebSocketMessage() {
    override val type = "auth_ok"
    override val id: Int? = null

    override fun toJson(): String = ""
}

/**
 * Authentication invalid response
 */
data class AuthInvalidMessage(
    val message: String
) : HomeAssistantWebSocketMessage() {
    override val type = "auth_invalid"
    override val id: Int? = null

    override fun toJson(): String = ""
}

/**
 * Subscribe to state changes
 */
data class SubscribeEventsMessage(
    override val id: Int,
    val eventType: String = "state_changed"
) : HomeAssistantWebSocketMessage() {
    override val type = "subscribe_events"

    override fun toJson(): String {
        return """{"id":$id,"type":"subscribe_events","event_type":"$eventType"}"""
    }
}

/**
 * Unsubscribe from events
 */
data class UnsubscribeEventsMessage(
    override val id: Int,
    val subscription: Int
) : HomeAssistantWebSocketMessage() {
    override val type = "unsubscribe_events"

    override fun toJson(): String {
        return """{"id":$id,"type":"unsubscribe_events","subscription":$subscription}"""
    }
}

/**
 * Get states command
 */
data class GetStatesMessage(
    override val id: Int
) : HomeAssistantWebSocketMessage() {
    override val type = "get_states"

    override fun toJson(): String {
        return """{"id":$id,"type":"get_states"}"""
    }
}

/**
 * Call service command
 */
data class CallServiceMessage(
    override val id: Int,
    val domain: String,
    val service: String,
    val serviceData: Map<String, Any>? = null,
    val target: Map<String, Any>? = null
) : HomeAssistantWebSocketMessage() {
    override val type = "call_service"

    override fun toJson(): String {
        val gson = Gson()
        val json = JsonObject().apply {
            addProperty("id", id)
            addProperty("type", type)
            addProperty("domain", domain)
            addProperty("service", service)
            if (serviceData != null) {
                add("service_data", gson.toJsonTree(serviceData))
            }
            if (target != null) {
                add("target", gson.toJsonTree(target))
            }
        }
        return gson.toJson(json)
    }
}

/**
 * Result message (response to commands)
 */
data class ResultMessage(
    override val id: Int,
    val success: Boolean,
    val result: Any? = null,
    val error: ErrorInfo? = null
) : HomeAssistantWebSocketMessage() {
    override val type = "result"

    override fun toJson(): String = ""

    data class ErrorInfo(
        val code: String,
        val message: String
    )
}

/**
 * Event message (state changes, etc.)
 */
data class EventMessage(
    override val id: Int,
    val eventData: EventData
) : HomeAssistantWebSocketMessage() {
    override val type = "event"

    override fun toJson(): String = ""

    data class EventData(
        val eventType: String,
        val data: StateChangeData?
    )

    data class StateChangeData(
        val entityId: String,
        val newState: EntityState?,
        val oldState: EntityState?
    )

    data class EntityState(
        val entityId: String,
        val state: String,
        val attributes: Map<String, Any>?,
        val lastChanged: String?,
        val lastUpdated: String?
    )
}

/**
 * Pong response to ping
 */
data class PongMessage(
    override val id: Int
) : HomeAssistantWebSocketMessage() {
    override val type = "pong"

    override fun toJson(): String = ""
}

/**
 * Ping message
 */
data class PingMessage(
    override val id: Int
) : HomeAssistantWebSocketMessage() {
    override val type = "ping"

    override fun toJson(): String {
        return """{"id":$id,"type":"ping"}"""
    }
}

/**
 * Parse Home Assistant WebSocket messages from JSON
 */
class HomeAssistantMessageParser {
    private val gson = Gson()

    fun parse(json: String): HomeAssistantWebSocketMessage? {
        return try {
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            val type = jsonObject.get("type")?.asString ?: return null

            when (type) {
                "auth_ok" -> {
                    val haVersion = jsonObject.get("ha_version")?.asString ?: "unknown"
                    AuthOkMessage(haVersion)
                }

                "auth_invalid" -> {
                    val message = jsonObject.get("message")?.asString ?: "Authentication failed"
                    AuthInvalidMessage(message)
                }

                "result" -> {
                    val id = jsonObject.get("id")?.asInt ?: return null
                    val success = jsonObject.get("success")?.asBoolean ?: false
                    val result = jsonObject.get("result")
                    val errorObj = jsonObject.getAsJsonObject("error")
                    val error = if (errorObj != null) {
                        ResultMessage.ErrorInfo(
                            code = errorObj.get("code")?.asString ?: "unknown",
                            message = errorObj.get("message")?.asString ?: "Unknown error"
                        )
                    } else null

                    ResultMessage(id, success, result, error)
                }

                "event" -> {
                    val id = jsonObject.get("id")?.asInt ?: return null
                    val eventObj = jsonObject.getAsJsonObject("event") ?: return null
                    val eventType = eventObj.get("event_type")?.asString ?: return null

                    if (eventType == "state_changed") {
                        val dataObj = eventObj.getAsJsonObject("data")
                        val entityId = dataObj?.get("entity_id")?.asString
                        val newStateObj = dataObj?.getAsJsonObject("new_state")
                        val oldStateObj = dataObj?.getAsJsonObject("old_state")

                        val newState = parseEntityState(newStateObj)
                        val oldState = parseEntityState(oldStateObj)

                        EventMessage(
                            id = id,
                            eventData = EventMessage.EventData(
                                eventType = eventType,
                                data = EventMessage.StateChangeData(
                                    entityId = entityId ?: "",
                                    newState = newState,
                                    oldState = oldState
                                )
                            )
                        )
                    } else {
                        // Other event types can be added here
                        null
                    }
                }

                "pong" -> {
                    val id = jsonObject.get("id")?.asInt ?: return null
                    PongMessage(id)
                }

                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseEntityState(stateObj: JsonObject?): EventMessage.EntityState? {
        if (stateObj == null) return null

        return try {
            EventMessage.EntityState(
                entityId = stateObj.get("entity_id")?.asString ?: "",
                state = stateObj.get("state")?.asString ?: "",
                attributes = parseAttributes(stateObj.getAsJsonObject("attributes")),
                lastChanged = stateObj.get("last_changed")?.asString,
                lastUpdated = stateObj.get("last_updated")?.asString
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseAttributes(attributesObj: JsonObject?): Map<String, Any>? {
        if (attributesObj == null) return null

        return try {
            attributesObj.entrySet().associate { (key, value) ->
                key to when {
                    value.isJsonPrimitive -> {
                        val primitive = value.asJsonPrimitive
                        when {
                            primitive.isBoolean -> primitive.asBoolean
                            primitive.isNumber -> primitive.asNumber
                            primitive.isString -> primitive.asString
                            else -> value.toString()
                        }
                    }
                    else -> value.toString()
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
