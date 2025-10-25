package com.shareconnect.homeassistantconnect.data.api

import com.shareconnect.homeassistantconnect.data.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Home Assistant REST API service interface
 *
 * API Documentation: https://developers.home-assistant.io/docs/api/rest/
 *
 * Main endpoints:
 * - /api/ - API status
 * - /api/config - Configuration
 * - /api/states - Entity states
 * - /api/services - Available services
 * - /api/events - Events
 * - /api/history - Historical data
 */
interface HomeAssistantApiService {

    /**
     * Get API status
     * GET /api/
     */
    @GET("api/")
    suspend fun getApiStatus(
        @Header("Authorization") authorization: String
    ): Response<HomeAssistantApiStatus>

    /**
     * Get Home Assistant configuration
     * GET /api/config
     */
    @GET("api/config")
    suspend fun getConfig(
        @Header("Authorization") authorization: String
    ): Response<HomeAssistantConfig>

    /**
     * Get all events
     * GET /api/events
     */
    @GET("api/events")
    suspend fun getEvents(
        @Header("Authorization") authorization: String
    ): Response<List<HomeAssistantEvent>>

    /**
     * Get all services
     * GET /api/services
     */
    @GET("api/services")
    suspend fun getServices(
        @Header("Authorization") authorization: String
    ): Response<List<HomeAssistantServiceDomain>>

    /**
     * Get all entity states
     * GET /api/states
     */
    @GET("api/states")
    suspend fun getStates(
        @Header("Authorization") authorization: String
    ): Response<List<HomeAssistantState>>

    /**
     * Get specific entity state
     * GET /api/states/<entity_id>
     */
    @GET("api/states/{entity_id}")
    suspend fun getState(
        @Path("entity_id") entityId: String,
        @Header("Authorization") authorization: String
    ): Response<HomeAssistantState>

    /**
     * Set entity state
     * POST /api/states/<entity_id>
     */
    @POST("api/states/{entity_id}")
    suspend fun setState(
        @Path("entity_id") entityId: String,
        @Header("Authorization") authorization: String,
        @Body state: HomeAssistantState
    ): Response<HomeAssistantState>

    /**
     * Call a service
     * POST /api/services/<domain>/<service>
     */
    @POST("api/services/{domain}/{service}")
    suspend fun callService(
        @Path("domain") domain: String,
        @Path("service") service: String,
        @Header("Authorization") authorization: String,
        @Body request: ServiceCallRequest
    ): Response<List<ServiceCallResponse>>

    /**
     * Fire an event
     * POST /api/events/<event_type>
     */
    @POST("api/events/{event_type}")
    suspend fun fireEvent(
        @Path("event_type") eventType: String,
        @Header("Authorization") authorization: String,
        @Body data: Map<String, Any>
    ): Response<ServiceCallResponse>

    /**
     * Get error log
     * GET /api/error_log
     */
    @GET("api/error_log")
    suspend fun getErrorLog(
        @Header("Authorization") authorization: String
    ): Response<String>

    /**
     * Get camera proxy image
     * GET /api/camera_proxy/<camera_entity_id>
     */
    @GET("api/camera_proxy/{entity_id}")
    suspend fun getCameraImage(
        @Path("entity_id") entityId: String,
        @Header("Authorization") authorization: String
    ): Response<ByteArray>

    /**
     * Get history for period
     * GET /api/history/period/<timestamp>
     *
     * @param timestamp ISO 8601 timestamp
     * @param filterEntityId Optional entity ID filter
     * @param endTime Optional end time
     * @param minimalResponse Minimize response data
     * @param noAttributes Exclude attributes
     * @param significantChangesOnly Only significant changes
     */
    @GET("api/history/period/{timestamp}")
    suspend fun getHistory(
        @Path("timestamp") timestamp: String,
        @Header("Authorization") authorization: String,
        @Query("filter_entity_id") filterEntityId: String? = null,
        @Query("end_time") endTime: String? = null,
        @Query("minimal_response") minimalResponse: Boolean? = null,
        @Query("no_attributes") noAttributes: Boolean? = null,
        @Query("significant_changes_only") significantChangesOnly: Boolean? = null
    ): Response<List<List<HomeAssistantState>>>

    /**
     * Get logbook entries
     * GET /api/logbook/<timestamp>
     *
     * @param timestamp ISO 8601 timestamp
     * @param entity Optional entity ID filter
     * @param endTime Optional end time
     */
    @GET("api/logbook/{timestamp}")
    suspend fun getLogbook(
        @Path("timestamp") timestamp: String,
        @Header("Authorization") authorization: String,
        @Query("entity") entity: String? = null,
        @Query("end_time") endTime: String? = null
    ): Response<List<HomeAssistantLogbookEntry>>

    /**
     * Render a template
     * POST /api/template
     */
    @POST("api/template")
    suspend fun renderTemplate(
        @Header("Authorization") authorization: String,
        @Body request: TemplateRequest
    ): Response<String>

    /**
     * Process intent
     * POST /api/intent/handle
     */
    @POST("api/intent/handle")
    suspend fun handleIntent(
        @Header("Authorization") authorization: String,
        @Body request: IntentRequest
    ): Response<IntentResponse>

    /**
     * Get calendar events
     * GET /api/calendars/<calendar_entity_id>
     *
     * @param entityId Calendar entity ID
     * @param start Start date (YYYY-MM-DD)
     * @param end End date (YYYY-MM-DD)
     */
    @GET("api/calendars/{entity_id}")
    suspend fun getCalendarEvents(
        @Path("entity_id") entityId: String,
        @Header("Authorization") authorization: String,
        @Query("start") start: String? = null,
        @Query("end") end: String? = null
    ): Response<List<CalendarEvent>>
}
