package com.shareconnect.homeassistantconnect.data.models

import com.google.gson.annotations.SerializedName

/**
 * Home Assistant REST API data models
 * API Documentation: https://developers.home-assistant.io/docs/api/rest/
 */

// ===== API Status =====

/**
 * API status and server information
 */
data class HomeAssistantApiStatus(
    @SerializedName("message") val message: String?
)

// ===== Configuration =====

/**
 * Home Assistant configuration
 */
data class HomeAssistantConfig(
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("elevation") val elevation: Int?,
    @SerializedName("unit_system") val unitSystem: UnitSystem?,
    @SerializedName("location_name") val locationName: String?,
    @SerializedName("time_zone") val timeZone: String?,
    @SerializedName("components") val components: List<String>?,
    @SerializedName("config_dir") val configDir: String?,
    @SerializedName("whitelist_external_dirs") val whitelistExternalDirs: List<String>?,
    @SerializedName("allowlist_external_dirs") val allowlistExternalDirs: List<String>?,
    @SerializedName("version") val version: String?,
    @SerializedName("config_source") val configSource: String?,
    @SerializedName("safe_mode") val safeMode: Boolean?,
    @SerializedName("state") val state: String?,
    @SerializedName("external_url") val externalUrl: String?,
    @SerializedName("internal_url") val internalUrl: String?
)

data class UnitSystem(
    @SerializedName("length") val length: String?,
    @SerializedName("mass") val mass: String?,
    @SerializedName("temperature") val temperature: String?,
    @SerializedName("volume") val volume: String?
)

// ===== Events =====

/**
 * Event definition
 */
data class HomeAssistantEvent(
    @SerializedName("event") val event: String?,
    @SerializedName("listener_count") val listenerCount: Int?
)

// ===== Services =====

/**
 * Service domain with available services
 */
data class HomeAssistantServiceDomain(
    @SerializedName("domain") val domain: String?,
    @SerializedName("services") val services: Map<String, HomeAssistantService>?
)

/**
 * Service definition
 */
data class HomeAssistantService(
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("fields") val fields: Map<String, ServiceField>?,
    @SerializedName("target") val target: ServiceTarget?
)

data class ServiceField(
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("required") val required: Boolean?,
    @SerializedName("example") val example: Any?,
    @SerializedName("selector") val selector: Map<String, Any>?
)

data class ServiceTarget(
    @SerializedName("entity") val entity: List<Map<String, String>>?,
    @SerializedName("device") val device: List<Map<String, String>>?,
    @SerializedName("area") val area: List<Map<String, String>>?
)

// ===== States =====

/**
 * Entity state
 */
data class HomeAssistantState(
    @SerializedName("entity_id") val entityId: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("attributes") val attributes: Map<String, Any>?,
    @SerializedName("last_changed") val lastChanged: String?,
    @SerializedName("last_updated") val lastUpdated: String?,
    @SerializedName("context") val context: StateContext?
)

data class StateContext(
    @SerializedName("id") val id: String?,
    @SerializedName("parent_id") val parentId: String?,
    @SerializedName("user_id") val userId: String?
)

// ===== Error Log =====

/**
 * Error log entry
 */
data class HomeAssistantErrorLog(
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("level") val level: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("source") val source: String?,
    @SerializedName("exception") val exception: String?,
    @SerializedName("count") val count: Int?,
    @SerializedName("first_occurred") val firstOccurred: String?
)

// ===== Camera =====

/**
 * Camera proxy image (byte array)
 */
data class CameraImage(
    @SerializedName("content_type") val contentType: String?,
    @SerializedName("data") val data: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CameraImage

        if (contentType != other.contentType) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = contentType?.hashCode() ?: 0
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }
}

// ===== History =====

/**
 * Historical state changes
 */
data class HomeAssistantHistory(
    @SerializedName("entity_id") val entityId: String?,
    @SerializedName("states") val states: List<HomeAssistantState>?
)

// ===== Logbook =====

/**
 * Logbook entry
 */
data class HomeAssistantLogbookEntry(
    @SerializedName("when") val `when`: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("domain") val domain: String?,
    @SerializedName("entity_id") val entityId: String?,
    @SerializedName("context_id") val contextId: String?,
    @SerializedName("context_user_id") val contextUserId: String?,
    @SerializedName("context_parent_id") val contextParentId: String?
)

// ===== Service Call Requests =====

/**
 * Service call request body
 */
data class ServiceCallRequest(
    @SerializedName("entity_id") val entityId: String? = null,
    @SerializedName("area_id") val areaId: String? = null,
    @SerializedName("device_id") val deviceId: String? = null,
    @SerializedName("data") val data: Map<String, Any>? = null
)

/**
 * Service call response
 */
data class ServiceCallResponse(
    @SerializedName("context") val context: StateContext?
)

// ===== Template Rendering =====

/**
 * Template render request
 */
data class TemplateRequest(
    @SerializedName("template") val template: String?
)

/**
 * Template render response
 */
data class TemplateResponse(
    @SerializedName("result") val result: String?
)

// ===== Intent Handling =====

/**
 * Intent request
 */
data class IntentRequest(
    @SerializedName("name") val name: String?,
    @SerializedName("data") val data: Map<String, Any>?
)

/**
 * Intent response
 */
data class IntentResponse(
    @SerializedName("speech") val speech: Map<String, Any>?,
    @SerializedName("card") val card: Map<String, Any>?,
    @SerializedName("data") val data: Map<String, Any>?
)

// ===== Calendar =====

/**
 * Calendar event
 */
data class CalendarEvent(
    @SerializedName("start") val start: EventTime?,
    @SerializedName("end") val end: EventTime?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("uid") val uid: String?,
    @SerializedName("recurrence_id") val recurrenceId: String?,
    @SerializedName("rrule") val rrule: String?
)

data class EventTime(
    @SerializedName("dateTime") val dateTime: String?,
    @SerializedName("date") val date: String?
)

// ===== Websocket Messages (for completeness) =====

/**
 * WebSocket authentication message
 */
data class WebSocketAuthMessage(
    @SerializedName("type") val type: String = "auth",
    @SerializedName("access_token") val accessToken: String?
)

/**
 * WebSocket command message
 */
data class WebSocketCommandMessage(
    @SerializedName("id") val id: Int?,
    @SerializedName("type") val type: String?,
    @SerializedName("event_type") val eventType: String? = null,
    @SerializedName("entity_id") val entityId: String? = null
)

/**
 * WebSocket result message
 */
data class WebSocketResultMessage(
    @SerializedName("id") val id: Int?,
    @SerializedName("type") val type: String?,
    @SerializedName("success") val success: Boolean?,
    @SerializedName("result") val result: Any?,
    @SerializedName("error") val error: WebSocketError?
)

data class WebSocketError(
    @SerializedName("code") val code: String?,
    @SerializedName("message") val message: String?
)
