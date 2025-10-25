package com.shareconnect.netdataconnect.data.api

import com.shareconnect.netdataconnect.data.models.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Netdata API v1/v2 service interface
 *
 * API Documentation: https://learn.netdata.cloud/docs/netdata-agent/netdata-api
 *
 * Main endpoints:
 * - /api/v1/info - Server information
 * - /api/v1/charts - Available charts
 * - /api/v1/data - Chart data
 * - /api/v1/alarms - Active alarms
 * - /api/v1/allmetrics - All metrics snapshot
 */
interface NetdataApiService {

    /**
     * Get server information and version
     * GET /api/v1/info
     */
    @GET("api/v1/info")
    suspend fun getInfo(): Response<NetdataInfo>

    /**
     * Get all available charts
     * GET /api/v1/charts
     */
    @GET("api/v1/charts")
    suspend fun getCharts(): Response<NetdataCharts>

    /**
     * Get data for a specific chart
     * GET /api/v1/data
     *
     * @param chart Chart ID (e.g., "system.cpu", "system.ram")
     * @param after Start time (unix timestamp or relative seconds with negative value)
     * @param before End time (unix timestamp or relative seconds)
     * @param points Number of points to return
     * @param group Grouping method (average, max, min, sum, etc.)
     * @param gtime Group time in seconds
     * @param options Options (null2zero, percentage, jsonwrap, etc.)
     * @param dimensions Comma-separated list of dimensions to include
     * @param format Response format (json, csv, datasource, etc.)
     */
    @GET("api/v1/data")
    suspend fun getData(
        @Query("chart") chart: String,
        @Query("after") after: Long? = null,
        @Query("before") before: Long? = null,
        @Query("points") points: Int? = null,
        @Query("group") group: String? = null,
        @Query("gtime") gtime: Int? = null,
        @Query("options") options: String? = null,
        @Query("dimensions") dimensions: String? = null,
        @Query("format") format: String? = "json"
    ): Response<NetdataChartData>

    /**
     * Get all active alarms
     * GET /api/v1/alarms
     *
     * @param all Include all alarms (not just active)
     */
    @GET("api/v1/alarms")
    suspend fun getAlarms(
        @Query("all") all: Boolean? = null
    ): Response<NetdataAlarms>

    /**
     * Get specific alarm status
     * GET /api/v1/alarm_log
     *
     * @param after Start time filter
     * @param alarm Specific alarm name filter
     */
    @GET("api/v1/alarm_log")
    suspend fun getAlarmLog(
        @Query("after") after: Long? = null,
        @Query("alarm") alarm: String? = null
    ): Response<List<NetdataAlarm>>

    /**
     * Get all metrics in a single call
     * GET /api/v1/allmetrics
     *
     * @param format Response format (shell, prometheus, json, etc.)
     * @param help Include help text
     * @param types Include metric types
     * @param timestamps Include timestamps
     * @param names Include metric names
     * @param oldunits Use old units format
     * @param hideunits Hide units
     * @param server Include server info
     */
    @GET("api/v1/allmetrics")
    suspend fun getAllMetrics(
        @Query("format") format: String = "json",
        @Query("help") help: String? = null,
        @Query("types") types: String? = null,
        @Query("timestamps") timestamps: String? = null,
        @Query("names") names: String? = null,
        @Query("oldunits") oldunits: String? = null,
        @Query("hideunits") hideunits: String? = null,
        @Query("server") server: String? = null
    ): Response<NetdataAllMetrics>

    /**
     * Get badge value (simple metric value)
     * GET /api/v1/badge.svg
     *
     * @param chart Chart ID
     * @param alarm Alarm name (alternative to chart)
     * @param dimension Specific dimension
     * @param after Time range start
     * @param before Time range end
     * @param group Grouping method
     * @param options Options
     * @param label Custom label
     * @param units Custom units
     * @param precision Decimal precision
     */
    @GET("api/v1/badge.svg")
    suspend fun getBadge(
        @Query("chart") chart: String? = null,
        @Query("alarm") alarm: String? = null,
        @Query("dimension") dimension: String? = null,
        @Query("after") after: Long? = null,
        @Query("before") before: Long? = null,
        @Query("group") group: String? = null,
        @Query("options") options: String? = null,
        @Query("label") label: String? = null,
        @Query("units") units: String? = null,
        @Query("precision") precision: Int? = null
    ): Response<String>

    /**
     * Get available contexts (chart groupings)
     * GET /api/v1/contexts
     */
    @GET("api/v1/contexts")
    suspend fun getContexts(): Response<NetdataContexts>

    /**
     * Get chart functions (v2 API)
     * GET /api/v2/functions
     *
     * @param chart Chart ID
     */
    @GET("api/v2/functions")
    suspend fun getFunctions(
        @Query("chart") chart: String
    ): Response<NetdataFunctions>

    /**
     * Get node information
     * GET /api/v1/info with extended info
     */
    @GET("api/v1/info")
    suspend fun getNode(): Response<NetdataNode>

    /**
     * Get chart weights/priorities
     * GET /api/v1/weights
     */
    @GET("api/v1/weights")
    suspend fun getWeights(): Response<NetdataWeights>
}
