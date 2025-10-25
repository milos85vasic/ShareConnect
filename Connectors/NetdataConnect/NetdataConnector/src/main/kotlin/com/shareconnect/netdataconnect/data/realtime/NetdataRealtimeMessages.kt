package com.shareconnect.netdataconnect.data.realtime

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.shareconnect.websocket.WebSocketMessage

/**
 * Netdata Real-time Update Messages
 * Since Netdata uses HTTP polling rather than WebSocket, these messages represent
 * polling-based updates wrapped in WebSocket message format for consistency
 */

/**
 * Base class for Netdata real-time messages
 */
sealed class NetdataRealtimeMessage : WebSocketMessage() {
    abstract val timestamp: Long
}

/**
 * Metrics update message - contains current metric values
 */
data class MetricsUpdateMessage(
    val charts: Map<String, ChartMetrics>,
    override val timestamp: Long = System.currentTimeMillis()
) : NetdataRealtimeMessage() {
    override val type = "metrics_update"

    override fun toJson(): String = ""

    data class ChartMetrics(
        val chartId: String,
        val dimensions: Map<String, Double>,
        val lastUpdated: Long
    )
}

/**
 * Alarm status update message
 */
data class AlarmUpdateMessage(
    val alarms: List<AlarmStatus>,
    val criticalCount: Int,
    val warningCount: Int,
    val clearCount: Int,
    override val timestamp: Long = System.currentTimeMillis()
) : NetdataRealtimeMessage() {
    override val type = "alarm_update"

    override fun toJson(): String = ""

    data class AlarmStatus(
        val id: Long,
        val name: String,
        val chart: String,
        val status: String, // CLEAR, WARNING, CRITICAL
        val value: Double?,
        val units: String?,
        val info: String?,
        val active: Boolean,
        val silenced: Boolean,
        val lastStatusChange: Long
    )
}

/**
 * Chart data update message - time series data points
 */
data class ChartDataUpdateMessage(
    val chartId: String,
    val labels: List<String>,
    val data: List<List<Double?>>,
    val dimensions: Int,
    val points: Int,
    val min: Double?,
    val max: Double?,
    override val timestamp: Long = System.currentTimeMillis()
) : NetdataRealtimeMessage() {
    override val type = "chart_data_update"

    override fun toJson(): String = ""
}

/**
 * Server info update message
 */
data class ServerInfoUpdateMessage(
    val hostname: String?,
    val version: String?,
    val os: String?,
    val updateEvery: Int?,
    val chartsCount: Int?,
    val alarmsCount: Int?,
    override val timestamp: Long = System.currentTimeMillis()
) : NetdataRealtimeMessage() {
    override val type = "server_info_update"

    override fun toJson(): String = ""
}

/**
 * Health status update message
 */
data class HealthStatusUpdateMessage(
    val status: String, // healthy, warning, critical
    val critical: Int,
    val warning: Int,
    val undefined: Int,
    val uninitialized: Int,
    val clear: Int,
    override val timestamp: Long = System.currentTimeMillis()
) : NetdataRealtimeMessage() {
    override val type = "health_status_update"

    override fun toJson(): String = ""
}

/**
 * Badge value update message - simple metric snapshot
 */
data class BadgeUpdateMessage(
    val chart: String,
    val dimension: String,
    val value: String,
    val units: String?,
    val label: String?,
    override val timestamp: Long = System.currentTimeMillis()
) : NetdataRealtimeMessage() {
    override val type = "badge_update"

    override fun toJson(): String = ""
}

/**
 * Connection status message
 */
data class ConnectionStatusMessage(
    val connected: Boolean,
    val serverUrl: String,
    val errorMessage: String? = null,
    override val timestamp: Long = System.currentTimeMillis()
) : NetdataRealtimeMessage() {
    override val type = "connection_status"

    override fun toJson(): String {
        return """{"type":"connection_status","connected":$connected,"server_url":"$serverUrl","timestamp":$timestamp}"""
    }
}

/**
 * Polling interval update message
 */
data class PollingIntervalMessage(
    val intervalMs: Long,
    override val timestamp: Long = System.currentTimeMillis()
) : NetdataRealtimeMessage() {
    override val type = "polling_interval"

    override fun toJson(): String {
        return """{"type":"polling_interval","interval_ms":$intervalMs,"timestamp":$timestamp}"""
    }
}

/**
 * Error message
 */
data class ErrorMessage(
    val error: String,
    val errorCode: Int? = null,
    override val timestamp: Long = System.currentTimeMillis()
) : NetdataRealtimeMessage() {
    override val type = "error"

    override fun toJson(): String {
        return if (errorCode != null) {
            """{"type":"error","error":"$error","error_code":$errorCode,"timestamp":$timestamp}"""
        } else {
            """{"type":"error","error":"$error","timestamp":$timestamp}"""
        }
    }
}

/**
 * Parse Netdata real-time messages from JSON
 * Note: These messages are typically constructed programmatically from API responses
 * rather than parsed from WebSocket messages
 */
class NetdataMessageParser {
    private val gson = Gson()

    fun parse(json: String): NetdataRealtimeMessage? {
        return try {
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            val type = jsonObject.get("type")?.asString ?: return null
            val timestamp = jsonObject.get("timestamp")?.asLong ?: System.currentTimeMillis()

            when (type) {
                "metrics_update" -> parseMetricsUpdate(jsonObject, timestamp)
                "alarm_update" -> parseAlarmUpdate(jsonObject, timestamp)
                "chart_data_update" -> parseChartDataUpdate(jsonObject, timestamp)
                "server_info_update" -> parseServerInfoUpdate(jsonObject, timestamp)
                "health_status_update" -> parseHealthStatusUpdate(jsonObject, timestamp)
                "badge_update" -> parseBadgeUpdate(jsonObject, timestamp)
                "connection_status" -> parseConnectionStatus(jsonObject, timestamp)
                "polling_interval" -> parsePollingInterval(jsonObject, timestamp)
                "error" -> parseError(jsonObject, timestamp)
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseMetricsUpdate(json: JsonObject, timestamp: Long): MetricsUpdateMessage? {
        val chartsObj = json.getAsJsonObject("charts") ?: return null
        val charts = mutableMapOf<String, MetricsUpdateMessage.ChartMetrics>()

        chartsObj.entrySet().forEach { (chartId, chartData) ->
            val chartObj = chartData.asJsonObject
            val dimensionsObj = chartObj.getAsJsonObject("dimensions")
            val dimensions = dimensionsObj?.entrySet()?.associate { (dim, value) ->
                dim to value.asDouble
            } ?: emptyMap()

            charts[chartId] = MetricsUpdateMessage.ChartMetrics(
                chartId = chartId,
                dimensions = dimensions,
                lastUpdated = chartObj.get("last_updated")?.asLong ?: timestamp
            )
        }

        return MetricsUpdateMessage(charts, timestamp)
    }

    private fun parseAlarmUpdate(json: JsonObject, timestamp: Long): AlarmUpdateMessage? {
        val alarmsArray = json.getAsJsonArray("alarms") ?: return null
        val alarms = alarmsArray.mapNotNull { alarmElement ->
            val alarmObj = alarmElement.asJsonObject
            AlarmUpdateMessage.AlarmStatus(
                id = alarmObj.get("id")?.asLong ?: return@mapNotNull null,
                name = alarmObj.get("name")?.asString ?: "",
                chart = alarmObj.get("chart")?.asString ?: "",
                status = alarmObj.get("status")?.asString ?: "CLEAR",
                value = alarmObj.get("value")?.asDouble,
                units = alarmObj.get("units")?.asString,
                info = alarmObj.get("info")?.asString,
                active = alarmObj.get("active")?.asBoolean ?: false,
                silenced = alarmObj.get("silenced")?.asBoolean ?: false,
                lastStatusChange = alarmObj.get("last_status_change")?.asLong ?: 0
            )
        }

        return AlarmUpdateMessage(
            alarms = alarms,
            criticalCount = json.get("critical_count")?.asInt ?: 0,
            warningCount = json.get("warning_count")?.asInt ?: 0,
            clearCount = json.get("clear_count")?.asInt ?: 0,
            timestamp = timestamp
        )
    }

    private fun parseChartDataUpdate(json: JsonObject, timestamp: Long): ChartDataUpdateMessage? {
        val chartId = json.get("chart_id")?.asString ?: return null
        val labelsArray = json.getAsJsonArray("labels")
        val labels = labelsArray?.map { it.asString } ?: emptyList()

        val dataArray = json.getAsJsonArray("data")
        val data = dataArray?.map { row ->
            row.asJsonArray.map { it.asDouble }
        } ?: emptyList()

        return ChartDataUpdateMessage(
            chartId = chartId,
            labels = labels,
            data = data,
            dimensions = json.get("dimensions")?.asInt ?: 0,
            points = json.get("points")?.asInt ?: 0,
            min = json.get("min")?.asDouble,
            max = json.get("max")?.asDouble,
            timestamp = timestamp
        )
    }

    private fun parseServerInfoUpdate(json: JsonObject, timestamp: Long): ServerInfoUpdateMessage {
        return ServerInfoUpdateMessage(
            hostname = json.get("hostname")?.asString,
            version = json.get("version")?.asString,
            os = json.get("os")?.asString,
            updateEvery = json.get("update_every")?.asInt,
            chartsCount = json.get("charts_count")?.asInt,
            alarmsCount = json.get("alarms_count")?.asInt,
            timestamp = timestamp
        )
    }

    private fun parseHealthStatusUpdate(json: JsonObject, timestamp: Long): HealthStatusUpdateMessage {
        return HealthStatusUpdateMessage(
            status = json.get("status")?.asString ?: "unknown",
            critical = json.get("critical")?.asInt ?: 0,
            warning = json.get("warning")?.asInt ?: 0,
            undefined = json.get("undefined")?.asInt ?: 0,
            uninitialized = json.get("uninitialized")?.asInt ?: 0,
            clear = json.get("clear")?.asInt ?: 0,
            timestamp = timestamp
        )
    }

    private fun parseBadgeUpdate(json: JsonObject, timestamp: Long): BadgeUpdateMessage? {
        return BadgeUpdateMessage(
            chart = json.get("chart")?.asString ?: return null,
            dimension = json.get("dimension")?.asString ?: return null,
            value = json.get("value")?.asString ?: return null,
            units = json.get("units")?.asString,
            label = json.get("label")?.asString,
            timestamp = timestamp
        )
    }

    private fun parseConnectionStatus(json: JsonObject, timestamp: Long): ConnectionStatusMessage {
        return ConnectionStatusMessage(
            connected = json.get("connected")?.asBoolean ?: false,
            serverUrl = json.get("server_url")?.asString ?: "",
            errorMessage = json.get("error_message")?.asString,
            timestamp = timestamp
        )
    }

    private fun parsePollingInterval(json: JsonObject, timestamp: Long): PollingIntervalMessage {
        return PollingIntervalMessage(
            intervalMs = json.get("interval_ms")?.asLong ?: 1000,
            timestamp = timestamp
        )
    }

    private fun parseError(json: JsonObject, timestamp: Long): ErrorMessage {
        return ErrorMessage(
            error = json.get("error")?.asString ?: "Unknown error",
            errorCode = json.get("error_code")?.asInt,
            timestamp = timestamp
        )
    }
}
