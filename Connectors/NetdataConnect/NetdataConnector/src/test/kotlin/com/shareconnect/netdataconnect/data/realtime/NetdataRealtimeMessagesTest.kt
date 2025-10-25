package com.shareconnect.netdataconnect.data.realtime

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = com.shareconnect.netdataconnect.TestApplication::class)
class NetdataRealtimeMessagesTest {

    private val parser = NetdataMessageParser()

    @Test
    fun `test MetricsUpdateMessage creation`() {
        val chart1 = MetricsUpdateMessage.ChartMetrics(
            chartId = "system.cpu",
            dimensions = mapOf("user" to 45.5, "system" to 15.2, "idle" to 39.3),
            lastUpdated = 1234567890L
        )

        val message = MetricsUpdateMessage(
            charts = mapOf("system.cpu" to chart1),
            timestamp = 1234567890L
        )

        assertEquals("metrics_update", message.type)
        assertEquals(1, message.charts.size)
        assertEquals("system.cpu", message.charts["system.cpu"]?.chartId)
        assertEquals(3, message.charts["system.cpu"]?.dimensions?.size)
        assertEquals(45.5, message.charts["system.cpu"]?.dimensions?.get("user")!!, 0.01)
    }

    @Test
    fun `test AlarmUpdateMessage creation`() {
        val alarm1 = AlarmUpdateMessage.AlarmStatus(
            id = 1,
            name = "cpu_usage",
            chart = "system.cpu",
            status = "WARNING",
            value = 85.5,
            units = "%",
            info = "CPU usage is high",
            active = true,
            silenced = false,
            lastStatusChange = 1234567890L
        )

        val message = AlarmUpdateMessage(
            alarms = listOf(alarm1),
            criticalCount = 0,
            warningCount = 1,
            clearCount = 0,
            timestamp = 1234567890L
        )

        assertEquals("alarm_update", message.type)
        assertEquals(1, message.alarms.size)
        assertEquals("WARNING", message.alarms[0].status)
        assertEquals(1, message.warningCount)
        assertEquals(0, message.criticalCount)
    }

    @Test
    fun `test ChartDataUpdateMessage creation`() {
        val message = ChartDataUpdateMessage(
            chartId = "system.cpu",
            labels = listOf("user", "system", "idle"),
            data = listOf(
                listOf(45.5, 15.2, 39.3),
                listOf(42.1, 18.3, 39.6)
            ),
            dimensions = 3,
            points = 2,
            min = 0.0,
            max = 100.0,
            timestamp = 1234567890L
        )

        assertEquals("chart_data_update", message.type)
        assertEquals("system.cpu", message.chartId)
        assertEquals(3, message.labels.size)
        assertEquals(2, message.data.size)
        assertEquals(3, message.dimensions)
        assertEquals(2, message.points)
    }

    @Test
    fun `test ServerInfoUpdateMessage creation`() {
        val message = ServerInfoUpdateMessage(
            hostname = "myserver",
            version = "v1.40.0",
            os = "linux",
            updateEvery = 1,
            chartsCount = 250,
            alarmsCount = 15,
            timestamp = 1234567890L
        )

        assertEquals("server_info_update", message.type)
        assertEquals("myserver", message.hostname)
        assertEquals("v1.40.0", message.version)
        assertEquals(250, message.chartsCount)
        assertEquals(15, message.alarmsCount)
    }

    @Test
    fun `test HealthStatusUpdateMessage creation`() {
        val message = HealthStatusUpdateMessage(
            status = "warning",
            critical = 0,
            warning = 2,
            undefined = 0,
            uninitialized = 0,
            clear = 10,
            timestamp = 1234567890L
        )

        assertEquals("health_status_update", message.type)
        assertEquals("warning", message.status)
        assertEquals(2, message.warning)
        assertEquals(10, message.clear)
    }

    @Test
    fun `test BadgeUpdateMessage creation and serialization`() {
        val message = BadgeUpdateMessage(
            chart = "system.cpu",
            dimension = "user",
            value = "45.5",
            units = "%",
            label = "CPU User",
            timestamp = 1234567890L
        )

        assertEquals("badge_update", message.type)
        assertEquals("system.cpu", message.chart)
        assertEquals("user", message.dimension)
        assertEquals("45.5", message.value)
    }

    @Test
    fun `test ConnectionStatusMessage serialization`() {
        val message = ConnectionStatusMessage(
            connected = true,
            serverUrl = "http://localhost:19999",
            errorMessage = null,
            timestamp = 1234567890L
        )

        assertEquals("connection_status", message.type)
        assertTrue(message.connected)

        val json = message.toJson()
        assertTrue(json.contains("\"type\":\"connection_status\""))
        assertTrue(json.contains("\"connected\":true"))
        assertTrue(json.contains("\"server_url\":\"http://localhost:19999\""))
    }

    @Test
    fun `test ConnectionStatusMessage with error`() {
        val message = ConnectionStatusMessage(
            connected = false,
            serverUrl = "http://localhost:19999",
            errorMessage = "Connection timeout",
            timestamp = 1234567890L
        )

        assertFalse(message.connected)
        assertEquals("Connection timeout", message.errorMessage)
    }

    @Test
    fun `test PollingIntervalMessage serialization`() {
        val message = PollingIntervalMessage(
            intervalMs = 2000L,
            timestamp = 1234567890L
        )

        assertEquals("polling_interval", message.type)
        assertEquals(2000L, message.intervalMs)

        val json = message.toJson()
        assertTrue(json.contains("\"type\":\"polling_interval\""))
        assertTrue(json.contains("\"interval_ms\":2000"))
    }

    @Test
    fun `test ErrorMessage serialization without code`() {
        val message = ErrorMessage(
            error = "Failed to connect",
            errorCode = null,
            timestamp = 1234567890L
        )

        assertEquals("error", message.type)
        assertEquals("Failed to connect", message.error)
        assertNull(message.errorCode)

        val json = message.toJson()
        assertTrue(json.contains("\"type\":\"error\""))
        assertTrue(json.contains("\"error\":\"Failed to connect\""))
        assertFalse(json.contains("error_code"))
    }

    @Test
    fun `test ErrorMessage serialization with code`() {
        val message = ErrorMessage(
            error = "HTTP Error",
            errorCode = 404,
            timestamp = 1234567890L
        )

        assertEquals(404, message.errorCode)

        val json = message.toJson()
        assertTrue(json.contains("\"error_code\":404"))
    }

    @Test
    fun `test parse MetricsUpdateMessage`() {
        val json = """
            {
                "type":"metrics_update",
                "timestamp":1234567890,
                "charts":{
                    "system.cpu":{
                        "dimensions":{
                            "user":45.5,
                            "system":15.2,
                            "idle":39.3
                        },
                        "last_updated":1234567890
                    }
                }
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is MetricsUpdateMessage)
        val metricsMessage = message as MetricsUpdateMessage

        assertEquals(1, metricsMessage.charts.size)
        val chart = metricsMessage.charts["system.cpu"]
        assertNotNull(chart)
        assertEquals("system.cpu", chart?.chartId)
        assertEquals(3, chart?.dimensions?.size)
        assertEquals(45.5, chart?.dimensions?.get("user")!!, 0.01)
        assertEquals(15.2, chart?.dimensions?.get("system")!!, 0.01)
    }

    @Test
    fun `test parse AlarmUpdateMessage`() {
        val json = """
            {
                "type":"alarm_update",
                "timestamp":1234567890,
                "critical_count":1,
                "warning_count":2,
                "clear_count":5,
                "alarms":[
                    {
                        "id":1,
                        "name":"cpu_usage",
                        "chart":"system.cpu",
                        "status":"CRITICAL",
                        "value":95.5,
                        "units":"%",
                        "info":"CPU usage critical",
                        "active":true,
                        "silenced":false,
                        "last_status_change":1234567890
                    },
                    {
                        "id":2,
                        "name":"ram_usage",
                        "chart":"system.ram",
                        "status":"WARNING",
                        "value":85.2,
                        "units":"%",
                        "active":true,
                        "silenced":false,
                        "last_status_change":1234567800
                    }
                ]
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is AlarmUpdateMessage)
        val alarmMessage = message as AlarmUpdateMessage

        assertEquals(2, alarmMessage.alarms.size)
        assertEquals(1, alarmMessage.criticalCount)
        assertEquals(2, alarmMessage.warningCount)
        assertEquals(5, alarmMessage.clearCount)

        val alarm1 = alarmMessage.alarms[0]
        assertEquals(1L, alarm1.id)
        assertEquals("cpu_usage", alarm1.name)
        assertEquals("CRITICAL", alarm1.status)
        assertEquals(95.5, alarm1.value!!, 0.01)
        assertTrue(alarm1.active)
        assertFalse(alarm1.silenced)
    }

    @Test
    fun `test parse ChartDataUpdateMessage`() {
        val json = """
            {
                "type":"chart_data_update",
                "timestamp":1234567890,
                "chart_id":"system.cpu",
                "labels":["user","system","idle"],
                "data":[[45.5,15.2,39.3],[42.1,18.3,39.6]],
                "dimensions":3,
                "points":2,
                "min":0.0,
                "max":100.0
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is ChartDataUpdateMessage)
        val chartDataMessage = message as ChartDataUpdateMessage

        assertEquals("system.cpu", chartDataMessage.chartId)
        assertEquals(3, chartDataMessage.labels.size)
        assertEquals("user", chartDataMessage.labels[0])
        assertEquals(2, chartDataMessage.data.size)
        assertEquals(3, chartDataMessage.data[0].size)
        assertEquals(45.5, chartDataMessage.data[0][0]!!, 0.01)
        assertEquals(0.0, chartDataMessage.min!!, 0.01)
        assertEquals(100.0, chartDataMessage.max!!, 0.01)
    }

    @Test
    fun `test parse ServerInfoUpdateMessage`() {
        val json = """
            {
                "type":"server_info_update",
                "timestamp":1234567890,
                "hostname":"myserver",
                "version":"v1.40.0",
                "os":"linux",
                "update_every":1,
                "charts_count":250,
                "alarms_count":15
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is ServerInfoUpdateMessage)
        val serverInfo = message as ServerInfoUpdateMessage

        assertEquals("myserver", serverInfo.hostname)
        assertEquals("v1.40.0", serverInfo.version)
        assertEquals("linux", serverInfo.os)
        assertEquals(1, serverInfo.updateEvery)
        assertEquals(250, serverInfo.chartsCount)
        assertEquals(15, serverInfo.alarmsCount)
    }

    @Test
    fun `test parse HealthStatusUpdateMessage`() {
        val json = """
            {
                "type":"health_status_update",
                "timestamp":1234567890,
                "status":"critical",
                "critical":2,
                "warning":5,
                "undefined":0,
                "uninitialized":0,
                "clear":20
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is HealthStatusUpdateMessage)
        val healthStatus = message as HealthStatusUpdateMessage

        assertEquals("critical", healthStatus.status)
        assertEquals(2, healthStatus.critical)
        assertEquals(5, healthStatus.warning)
        assertEquals(20, healthStatus.clear)
    }

    @Test
    fun `test parse BadgeUpdateMessage`() {
        val json = """
            {
                "type":"badge_update",
                "timestamp":1234567890,
                "chart":"system.cpu",
                "dimension":"user",
                "value":"45.5",
                "units":"%",
                "label":"CPU User"
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is BadgeUpdateMessage)
        val badge = message as BadgeUpdateMessage

        assertEquals("system.cpu", badge.chart)
        assertEquals("user", badge.dimension)
        assertEquals("45.5", badge.value)
        assertEquals("%", badge.units)
        assertEquals("CPU User", badge.label)
    }

    @Test
    fun `test parse ConnectionStatusMessage`() {
        val json = """
            {
                "type":"connection_status",
                "timestamp":1234567890,
                "connected":true,
                "server_url":"http://localhost:19999"
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is ConnectionStatusMessage)
        val status = message as ConnectionStatusMessage

        assertTrue(status.connected)
        assertEquals("http://localhost:19999", status.serverUrl)
        assertNull(status.errorMessage)
    }

    @Test
    fun `test parse PollingIntervalMessage`() {
        val json = """
            {
                "type":"polling_interval",
                "timestamp":1234567890,
                "interval_ms":5000
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is PollingIntervalMessage)
        val interval = message as PollingIntervalMessage

        assertEquals(5000L, interval.intervalMs)
    }

    @Test
    fun `test parse ErrorMessage`() {
        val json = """
            {
                "type":"error",
                "timestamp":1234567890,
                "error":"Connection failed",
                "error_code":500
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is ErrorMessage)
        val error = message as ErrorMessage

        assertEquals("Connection failed", error.error)
        assertEquals(500, error.errorCode)
    }

    @Test
    fun `test parse invalid JSON`() {
        val json = """{"invalid json"""

        val message = parser.parse(json)

        assertNull(message)
    }

    @Test
    fun `test parse unknown message type`() {
        val json = """{"type":"unknown_type","timestamp":1234567890}"""

        val message = parser.parse(json)

        assertNull(message)
    }

    @Test
    fun `test parse message without type`() {
        val json = """{"timestamp":1234567890,"data":"test"}"""

        val message = parser.parse(json)

        assertNull(message)
    }

    @Test
    fun `test timestamp defaults to current time`() {
        val message = MetricsUpdateMessage(charts = emptyMap())

        assertTrue(message.timestamp > 0)
        assertTrue(message.timestamp <= System.currentTimeMillis())
    }
}
