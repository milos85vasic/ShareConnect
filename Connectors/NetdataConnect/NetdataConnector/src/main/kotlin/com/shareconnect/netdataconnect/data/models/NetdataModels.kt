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


package com.shareconnect.netdataconnect.data.models

import com.google.gson.annotations.SerializedName

/**
 * Netdata API v1/v2 data models for system monitoring
 * API Documentation: https://learn.netdata.cloud/docs/netdata-agent/netdata-api
 */

// ===== Server Information =====

/**
 * Server information and version details
 */
data class NetdataInfo(
    @SerializedName("version") val version: String?,
    @SerializedName("uid") val uid: String?,
    @SerializedName("timezone") val timezone: String?,
    @SerializedName("os_name") val osName: String?,
    @SerializedName("os_id") val osId: String?,
    @SerializedName("os_id_like") val osIdLike: String?,
    @SerializedName("os_version") val osVersion: String?,
    @SerializedName("os_version_id") val osVersionId: String?,
    @SerializedName("os_detection") val osDetection: String?,
    @SerializedName("cores_total") val coresTotal: Int?,
    @SerializedName("total_disk_space") val totalDiskSpace: Long?,
    @SerializedName("cpu_freq") val cpuFreq: String?,
    @SerializedName("ram_total") val ramTotal: Long?,
    @SerializedName("update_every") val updateEvery: Int?,
    @SerializedName("history") val history: Int?,
    @SerializedName("memory_mode") val memoryMode: String?,
    @SerializedName("host_labels") val hostLabels: Map<String, String>?
)

// ===== Charts and Metrics =====

/**
 * List of available charts
 */
data class NetdataCharts(
    @SerializedName("hostname") val hostname: String?,
    @SerializedName("version") val version: String?,
    @SerializedName("os") val os: String?,
    @SerializedName("history") val history: Int?,
    @SerializedName("update_every") val updateEvery: Int?,
    @SerializedName("charts") val charts: Map<String, NetdataChart>?,
    @SerializedName("charts_count") val chartsCount: Int?,
    @SerializedName("dimensions_count") val dimensionsCount: Int?,
    @SerializedName("alarms_count") val alarmsCount: Int?,
    @SerializedName("rrd_memory_bytes") val rrdMemoryBytes: Long?
)

/**
 * Individual chart definition
 */
data class NetdataChart(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("family") val family: String?,
    @SerializedName("context") val context: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("priority") val priority: Int?,
    @SerializedName("plugin") val plugin: String?,
    @SerializedName("module") val module: String?,
    @SerializedName("enabled") val enabled: Boolean?,
    @SerializedName("units") val units: String?,
    @SerializedName("data_url") val dataUrl: String?,
    @SerializedName("chart_type") val chartType: String?,
    @SerializedName("duration") val duration: Int?,
    @SerializedName("first_entry") val firstEntry: Long?,
    @SerializedName("last_entry") val lastEntry: Long?,
    @SerializedName("update_every") val updateEvery: Int?,
    @SerializedName("dimensions") val dimensions: Map<String, NetdataDimension>?,
    @SerializedName("green") val green: Double?,
    @SerializedName("red") val red: Double?
)

/**
 * Chart dimension (metric)
 */
data class NetdataDimension(
    @SerializedName("name") val name: String?,
    @SerializedName("algorithm") val algorithm: String?,
    @SerializedName("multiplier") val multiplier: Int?,
    @SerializedName("divisor") val divisor: Int?,
    @SerializedName("hidden") val hidden: Boolean?
)

/**
 * Chart data response
 */
data class NetdataChartData(
    @SerializedName("labels") val labels: List<String>?,
    @SerializedName("data") val data: List<List<Double?>>?,
    @SerializedName("view_latest_values") val viewLatestValues: List<Double?>?,
    @SerializedName("dimensions") val dimensions: Int?,
    @SerializedName("points") val points: Int?,
    @SerializedName("format") val format: String?,
    @SerializedName("result") val result: String?,
    @SerializedName("min") val min: Double?,
    @SerializedName("max") val max: Double?
)

// ===== Alarms =====

/**
 * Alarms/alerts status
 */
data class NetdataAlarms(
    @SerializedName("hostname") val hostname: String?,
    @SerializedName("latest_alarm_log_unique_id") val latestAlarmLogUniqueId: Long?,
    @SerializedName("status") val status: Boolean?,
    @SerializedName("now") val now: Long?,
    @SerializedName("alarms") val alarms: Map<String, NetdataAlarm>?
)

/**
 * Individual alarm
 */
data class NetdataAlarm(
    @SerializedName("id") val id: Long?,
    @SerializedName("status") val status: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("chart") val chart: String?,
    @SerializedName("family") val family: String?,
    @SerializedName("active") val active: Boolean?,
    @SerializedName("disabled") val disabled: Boolean?,
    @SerializedName("silenced") val silenced: Boolean?,
    @SerializedName("exec") val exec: String?,
    @SerializedName("recipient") val recipient: String?,
    @SerializedName("source") val source: String?,
    @SerializedName("units") val units: String?,
    @SerializedName("info") val info: String?,
    @SerializedName("value") val value: Double?,
    @SerializedName("last_status_change") val lastStatusChange: Long?,
    @SerializedName("last_updated") val lastUpdated: Long?,
    @SerializedName("next_update") val nextUpdate: Long?,
    @SerializedName("update_every") val updateEvery: Int?,
    @SerializedName("delay_up_duration") val delayUpDuration: Int?,
    @SerializedName("delay_down_duration") val delayDownDuration: Int?,
    @SerializedName("delay_max_duration") val delayMaxDuration: Int?,
    @SerializedName("delay_multiplier") val delayMultiplier: Double?,
    @SerializedName("delay") val delay: Int?,
    @SerializedName("delay_up_to_timestamp") val delayUpToTimestamp: Long?,
    @SerializedName("warn_repeat_every") val warnRepeatEvery: Int?,
    @SerializedName("crit_repeat_every") val critRepeatEvery: Int?
)

// ===== All Metrics =====

/**
 * All metrics in JSON format
 */
data class NetdataAllMetrics(
    @SerializedName("hostname") val hostname: String?,
    @SerializedName("version") val version: String?,
    @SerializedName("os") val os: String?,
    @SerializedName("release_channel") val releaseChannel: String?,
    @SerializedName("timezone") val timezone: String?,
    @SerializedName("update_every") val updateEvery: Int?,
    @SerializedName("history") val history: Int?,
    @SerializedName("memory_mode") val memoryMode: String?,
    @SerializedName("health") val health: NetdataHealth?,
    @SerializedName("charts") val charts: Map<String, NetdataMetricChart>?
)

/**
 * Health status summary
 */
data class NetdataHealth(
    @SerializedName("status") val status: String?,
    @SerializedName("critical") val critical: Int?,
    @SerializedName("warning") val warning: Int?,
    @SerializedName("undefined") val undefined: Int?,
    @SerializedName("uninitialized") val uninitialized: Int?,
    @SerializedName("clear") val clear: Int?
)

/**
 * Metric chart with current values
 */
data class NetdataMetricChart(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("family") val family: String?,
    @SerializedName("context") val context: String?,
    @SerializedName("units") val units: String?,
    @SerializedName("last_updated") val lastUpdated: Long?,
    @SerializedName("dimensions") val dimensions: Map<String, NetdataMetricDimension>?
)

/**
 * Metric dimension with value
 */
data class NetdataMetricDimension(
    @SerializedName("name") val name: String?,
    @SerializedName("value") val value: Double?
)

// ===== Badges (Simple Metrics) =====

/**
 * Badge value (simple metric value)
 */
data class NetdataBadge(
    @SerializedName("value") val value: String?,
    @SerializedName("units") val units: String?,
    @SerializedName("label") val label: String?,
    @SerializedName("chart") val chart: String?,
    @SerializedName("dimension") val dimension: String?
)

// ===== Functions (v2 API) =====

/**
 * Available functions on a chart
 */
data class NetdataFunctions(
    @SerializedName("functions") val functions: List<NetdataFunction>?
)

/**
 * Individual function
 */
data class NetdataFunction(
    @SerializedName("name") val name: String?,
    @SerializedName("help") val help: String?,
    @SerializedName("tags") val tags: List<String>?,
    @SerializedName("access") val access: String?,
    @SerializedName("priority") val priority: Int?
)

// ===== Contexts =====

/**
 * Available contexts (chart groupings)
 */
data class NetdataContexts(
    @SerializedName("contexts") val contexts: Map<String, NetdataContext>?
)

/**
 * Individual context
 */
data class NetdataContext(
    @SerializedName("id") val id: String?,
    @SerializedName("family") val family: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("units") val units: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("priority") val priority: Int?,
    @SerializedName("charts") val charts: List<String>?
)

// ===== Node Information =====

/**
 * Node/host information
 */
data class NetdataNode(
    @SerializedName("hostname") val hostname: String?,
    @SerializedName("machine_guid") val machineGuid: String?,
    @SerializedName("version") val version: String?,
    @SerializedName("os") val os: String?,
    @SerializedName("timezone") val timezone: String?,
    @SerializedName("collectors") val collectors: List<String>?,
    @SerializedName("host_labels") val hostLabels: Map<String, String>?
)

// ===== Weights (Chart importance) =====

/**
 * Chart weights/priorities
 */
data class NetdataWeights(
    @SerializedName("charts") val charts: Map<String, Double>?
)
