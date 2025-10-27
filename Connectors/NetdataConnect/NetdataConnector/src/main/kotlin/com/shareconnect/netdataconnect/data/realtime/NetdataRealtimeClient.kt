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


package com.shareconnect.netdataconnect.data.realtime

import android.util.Log
import com.shareconnect.netdataconnect.data.api.NetdataApiClient
import com.shareconnect.netdataconnect.data.models.NetdataAllMetrics
import com.shareconnect.netdataconnect.data.models.NetdataAlarms
import com.shareconnect.netdataconnect.data.models.NetdataChartData
import com.shareconnect.netdataconnect.data.models.NetdataCharts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Netdata Real-time Client
 * Provides real-time updates from Netdata server using HTTP polling
 *
 * Note: Netdata does not have native WebSocket support, so this implementation
 * uses periodic HTTP polling to simulate real-time updates for consistency with
 * other connectors in Phase 3.
 */
class NetdataRealtimeClient(
    private val serverUrl: String,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    companion object {
        private const val TAG = "NetdataRealtime"
        private const val DEFAULT_POLL_INTERVAL_MS = 2000L // 2 seconds
        private const val MIN_POLL_INTERVAL_MS = 500L
        private const val MAX_POLL_INTERVAL_MS = 60000L
    }

    private val apiClient = NetdataApiClient(serverUrl)

    private var pollJob: Job? = null
    private var alarmPollJob: Job? = null

    private var pollIntervalMs = DEFAULT_POLL_INTERVAL_MS
    private var isPolling = false

    // State flows
    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected.asStateFlow()

    private val _healthStatus = MutableStateFlow<HealthStatusUpdateMessage?>(null)
    val healthStatus: StateFlow<HealthStatusUpdateMessage?> = _healthStatus.asStateFlow()

    private val _alarms = MutableStateFlow<List<AlarmUpdateMessage.AlarmStatus>>(emptyList())
    val alarms: StateFlow<List<AlarmUpdateMessage.AlarmStatus>> = _alarms.asStateFlow()

    private val _serverInfo = MutableStateFlow<ServerInfoUpdateMessage?>(null)
    val serverInfo: StateFlow<ServerInfoUpdateMessage?> = _serverInfo.asStateFlow()

    // Callbacks
    private val metricsUpdateCallbacks = mutableListOf<(MetricsUpdateMessage) -> Unit>()
    private val alarmUpdateCallbacks = mutableListOf<(AlarmUpdateMessage) -> Unit>()
    private val chartDataUpdateCallbacks = mutableListOf<(ChartDataUpdateMessage) -> Unit>()
    private val healthStatusCallbacks = mutableListOf<(HealthStatusUpdateMessage) -> Unit>()
    private val connectionStatusCallbacks = mutableListOf<(ConnectionStatusMessage) -> Unit>()

    /**
     * Start real-time polling
     */
    suspend fun connect() {
        if (isPolling) {
            Log.d(TAG, "Already polling")
            return
        }

        Log.d(TAG, "Starting real-time polling for $serverUrl")
        isPolling = true
        _connected.value = true

        // Notify connection status
        notifyConnectionStatus(true, null)

        // Fetch initial server info
        fetchServerInfo()

        // Start metrics polling
        startMetricsPolling()

        // Start alarm polling
        startAlarmPolling()
    }

    /**
     * Stop real-time polling
     */
    suspend fun disconnect() {
        if (!isPolling) {
            return
        }

        Log.d(TAG, "Stopping real-time polling")
        isPolling = false
        _connected.value = false

        pollJob?.cancel()
        alarmPollJob?.cancel()
        pollJob = null
        alarmPollJob = null

        notifyConnectionStatus(false, null)
    }

    /**
     * Set polling interval
     */
    fun setPollingInterval(intervalMs: Long) {
        val clampedInterval = intervalMs.coerceIn(MIN_POLL_INTERVAL_MS, MAX_POLL_INTERVAL_MS)
        if (clampedInterval != pollIntervalMs) {
            pollIntervalMs = clampedInterval
            Log.d(TAG, "Polling interval set to ${pollIntervalMs}ms")

            // Restart polling with new interval
            if (isPolling) {
                scope.launch {
                    disconnect()
                    delay(100)
                    connect()
                }
            }
        }
    }

    /**
     * Subscribe to metrics updates
     */
    fun subscribeToMetrics(callback: (MetricsUpdateMessage) -> Unit) {
        metricsUpdateCallbacks.add(callback)
    }

    /**
     * Subscribe to alarm updates
     */
    fun subscribeToAlarms(callback: (AlarmUpdateMessage) -> Unit) {
        alarmUpdateCallbacks.add(callback)
    }

    /**
     * Subscribe to chart data updates
     */
    fun subscribeToChartData(callback: (ChartDataUpdateMessage) -> Unit) {
        chartDataUpdateCallbacks.add(callback)
    }

    /**
     * Subscribe to health status updates
     */
    fun subscribeToHealthStatus(callback: (HealthStatusUpdateMessage) -> Unit) {
        healthStatusCallbacks.add(callback)
    }

    /**
     * Subscribe to connection status
     */
    fun subscribeToConnectionStatus(callback: (ConnectionStatusMessage) -> Unit) {
        connectionStatusCallbacks.add(callback)
    }

    /**
     * Fetch chart data for specific chart
     */
    suspend fun fetchChartData(chartId: String, points: Int = 60) {
        try {
            val result = apiClient.getData(chart = chartId, points = points)
            result.onSuccess { chartData ->
                val message = convertToChartDataUpdate(chartId, chartData)
                notifyChartDataUpdate(message)
            }.onFailure { error ->
                Log.e(TAG, "Failed to fetch chart data: ${error.message}", error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching chart data: ${e.message}", e)
        }
    }

    /**
     * Check if connected
     */
    fun isConnected(): Boolean = _connected.value

    // Private methods

    private fun startMetricsPolling() {
        pollJob = scope.launch {
            while (isActive && isPolling) {
                try {
                    fetchMetrics()
                } catch (e: Exception) {
                    Log.e(TAG, "Metrics polling error: ${e.message}", e)
                    notifyConnectionStatus(false, e.message)
                }
                delay(pollIntervalMs)
            }
        }
    }

    private fun startAlarmPolling() {
        alarmPollJob = scope.launch {
            while (isActive && isPolling) {
                try {
                    fetchAlarms()
                } catch (e: Exception) {
                    Log.e(TAG, "Alarm polling error: ${e.message}", e)
                }
                delay(pollIntervalMs * 2) // Poll alarms less frequently
            }
        }
    }

    private suspend fun fetchServerInfo() {
        try {
            val result = apiClient.getCharts()
            result.onSuccess { charts ->
                val message = convertToServerInfoUpdate(charts)
                _serverInfo.value = message
            }.onFailure { error ->
                Log.e(TAG, "Failed to fetch server info: ${error.message}", error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching server info: ${e.message}", e)
        }
    }

    private suspend fun fetchMetrics() {
        try {
            val result = apiClient.getAllMetrics()
            result.onSuccess { allMetrics ->
                val metricsMessage = convertToMetricsUpdate(allMetrics)
                notifyMetricsUpdate(metricsMessage)

                // Update health status
                val healthMessage = convertToHealthStatusUpdate(allMetrics)
                _healthStatus.value = healthMessage
                notifyHealthStatusUpdate(healthMessage)
            }.onFailure { error ->
                Log.e(TAG, "Failed to fetch metrics: ${error.message}", error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching metrics: ${e.message}", e)
        }
    }

    private suspend fun fetchAlarms() {
        try {
            val result = apiClient.getAlarms()
            result.onSuccess { alarmsData ->
                val message = convertToAlarmUpdate(alarmsData)
                _alarms.value = message.alarms
                notifyAlarmUpdate(message)
            }.onFailure { error ->
                Log.e(TAG, "Failed to fetch alarms: ${error.message}", error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching alarms: ${e.message}", e)
        }
    }

    private fun convertToMetricsUpdate(allMetrics: NetdataAllMetrics): MetricsUpdateMessage {
        val charts = allMetrics.charts?.mapValues { (chartId, chart) ->
            val dimensions = chart.dimensions?.mapValues { (_, dimension) ->
                dimension.value ?: 0.0
            } ?: emptyMap()

            MetricsUpdateMessage.ChartMetrics(
                chartId = chartId,
                dimensions = dimensions,
                lastUpdated = chart.lastUpdated ?: System.currentTimeMillis()
            )
        } ?: emptyMap()

        return MetricsUpdateMessage(charts)
    }

    private fun convertToAlarmUpdate(alarmsData: NetdataAlarms): AlarmUpdateMessage {
        val alarms = alarmsData.alarms?.values?.map { alarm ->
            AlarmUpdateMessage.AlarmStatus(
                id = alarm.id ?: 0,
                name = alarm.name ?: "",
                chart = alarm.chart ?: "",
                status = alarm.status ?: "CLEAR",
                value = alarm.value,
                units = alarm.units,
                info = alarm.info,
                active = alarm.active ?: false,
                silenced = alarm.silenced ?: false,
                lastStatusChange = alarm.lastStatusChange ?: 0
            )
        } ?: emptyList()

        // Count alarms by status
        val criticalCount = alarms.count { it.status == "CRITICAL" }
        val warningCount = alarms.count { it.status == "WARNING" }
        val clearCount = alarms.count { it.status == "CLEAR" }

        return AlarmUpdateMessage(
            alarms = alarms,
            criticalCount = criticalCount,
            warningCount = warningCount,
            clearCount = clearCount
        )
    }

    private fun convertToChartDataUpdate(chartId: String, chartData: NetdataChartData): ChartDataUpdateMessage {
        return ChartDataUpdateMessage(
            chartId = chartId,
            labels = chartData.labels ?: emptyList(),
            data = chartData.data ?: emptyList(),
            dimensions = chartData.dimensions ?: 0,
            points = chartData.points ?: 0,
            min = chartData.min,
            max = chartData.max
        )
    }

    private fun convertToServerInfoUpdate(charts: NetdataCharts): ServerInfoUpdateMessage {
        return ServerInfoUpdateMessage(
            hostname = charts.hostname,
            version = charts.version,
            os = charts.os,
            updateEvery = charts.updateEvery,
            chartsCount = charts.chartsCount,
            alarmsCount = charts.alarmsCount
        )
    }

    private fun convertToHealthStatusUpdate(allMetrics: NetdataAllMetrics): HealthStatusUpdateMessage {
        val health = allMetrics.health
        return HealthStatusUpdateMessage(
            status = health?.status ?: "unknown",
            critical = health?.critical ?: 0,
            warning = health?.warning ?: 0,
            undefined = health?.undefined ?: 0,
            uninitialized = health?.uninitialized ?: 0,
            clear = health?.clear ?: 0
        )
    }

    private fun notifyMetricsUpdate(message: MetricsUpdateMessage) {
        metricsUpdateCallbacks.forEach { callback ->
            scope.launch {
                callback(message)
            }
        }
    }

    private fun notifyAlarmUpdate(message: AlarmUpdateMessage) {
        alarmUpdateCallbacks.forEach { callback ->
            scope.launch {
                callback(message)
            }
        }
    }

    private fun notifyChartDataUpdate(message: ChartDataUpdateMessage) {
        chartDataUpdateCallbacks.forEach { callback ->
            scope.launch {
                callback(message)
            }
        }
    }

    private fun notifyHealthStatusUpdate(message: HealthStatusUpdateMessage) {
        healthStatusCallbacks.forEach { callback ->
            scope.launch {
                callback(message)
            }
        }
    }

    private fun notifyConnectionStatus(connected: Boolean, error: String?) {
        val message = ConnectionStatusMessage(
            connected = connected,
            serverUrl = serverUrl,
            errorMessage = error
        )
        connectionStatusCallbacks.forEach { callback ->
            scope.launch {
                callback(message)
            }
        }
    }
}
