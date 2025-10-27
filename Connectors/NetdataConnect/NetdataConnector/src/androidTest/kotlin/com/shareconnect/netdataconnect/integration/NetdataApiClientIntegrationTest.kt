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


package com.shareconnect.netdataconnect.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.netdataconnect.data.api.NetdataApiClient
import com.shareconnect.netdataconnect.data.api.NetdataApiService
import com.shareconnect.netdataconnect.data.models.*
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson

/**
 * Integration tests for NetdataApiClient
 * Tests actual API client behavior with MockWebServer
 */
@RunWith(AndroidJUnit4::class)
class NetdataApiClientIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: NetdataApiClient
    private val gson = Gson()

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NetdataApiService::class.java)

        apiClient = NetdataApiClient(
            serverUrl = mockWebServer.url("/").toString(),
            netdataApiService = service
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // ===== Server Info Integration Tests =====

    @Test
    fun testGetInfoIntegration() = runBlocking {
        val mockInfo = NetdataInfo(
            version = "v1.42.0",
            uid = "abc123",
            timezone = "America/New_York",
            osName = "Ubuntu",
            osId = "ubuntu",
            osIdLike = "debian",
            osVersion = "22.04 LTS",
            osVersionId = "22.04",
            osDetection = "systemd",
            coresTotal = 8,
            totalDiskSpace = 500000000000L,
            cpuFreq = "3.6GHz",
            ramTotal = 32000000000L,
            updateEvery = 1,
            history = 3600,
            memoryMode = "dbengine",
            hostLabels = mapOf("environment" to "production", "datacenter" to "us-east")
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(mockInfo))
        )

        val result = apiClient.getInfo()

        Assert.assertTrue(result.isSuccess)
        val info = result.getOrNull()!!
        Assert.assertEquals("v1.42.0", info.version)
        Assert.assertEquals(8, info.coresTotal)
        Assert.assertEquals("Ubuntu", info.osName)
        Assert.assertEquals("dbengine", info.memoryMode)
    }

    @Test
    fun testGetInfo404Error() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
        )

        val result = apiClient.getInfo()

        Assert.assertTrue(result.isFailure)
    }

    // ===== Charts Integration Tests =====

    @Test
    fun testGetChartsIntegration() = runBlocking {
        val mockCharts = NetdataCharts(
            hostname = "production-server-01",
            version = "v1.42.0",
            os = "linux",
            history = 3600,
            updateEvery = 1,
            charts = mapOf(
                "system.cpu" to NetdataChart(
                    id = "system.cpu",
                    name = "cpu",
                    type = "system",
                    family = "cpu",
                    context = "system.cpu",
                    title = "Total CPU utilization",
                    priority = 100,
                    plugin = "proc",
                    module = "stat",
                    enabled = true,
                    units = "percentage",
                    dataUrl = "/api/v1/data?chart=system.cpu",
                    chartType = "stacked",
                    duration = 3600,
                    firstEntry = 1698000000,
                    lastEntry = 1698003600,
                    updateEvery = 1,
                    dimensions = mapOf(
                        "user" to NetdataDimension("user", "absolute", 1, 1, false),
                        "system" to NetdataDimension("system", "absolute", 1, 1, false)
                    ),
                    green = 75.0,
                    red = 90.0
                ),
                "system.ram" to NetdataChart(
                    id = "system.ram",
                    name = "ram",
                    type = "system",
                    family = "memory",
                    context = "system.ram",
                    title = "System RAM",
                    priority = 200,
                    plugin = "proc",
                    module = "meminfo",
                    enabled = true,
                    units = "MiB",
                    dataUrl = "/api/v1/data?chart=system.ram",
                    chartType = "stacked",
                    duration = 3600,
                    firstEntry = 1698000000,
                    lastEntry = 1698003600,
                    updateEvery = 1,
                    dimensions = mapOf(
                        "used" to NetdataDimension("used", "absolute", 1, 1024, false),
                        "cached" to NetdataDimension("cached", "absolute", 1, 1024, false)
                    ),
                    green = null,
                    red = null
                )
            ),
            chartsCount = 2,
            dimensionsCount = 4,
            alarmsCount = 10,
            rrdMemoryBytes = 104857600L
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(mockCharts))
        )

        val result = apiClient.getCharts()

        Assert.assertTrue(result.isSuccess)
        val charts = result.getOrNull()!!
        Assert.assertEquals("production-server-01", charts.hostname)
        Assert.assertEquals(2, charts.chartsCount)
        Assert.assertEquals(2, charts.charts?.size)
        Assert.assertTrue(charts.charts!!.containsKey("system.cpu"))
        Assert.assertTrue(charts.charts.containsKey("system.ram"))
    }

    @Test
    fun testGetChartsEmptyResponse() = runBlocking {
        val emptyCharts = NetdataCharts(
            hostname = "empty-server",
            version = "v1.42.0",
            os = "linux",
            history = 3600,
            updateEvery = 1,
            charts = emptyMap(),
            chartsCount = 0,
            dimensionsCount = 0,
            alarmsCount = 0,
            rrdMemoryBytes = 0L
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(emptyCharts))
        )

        val result = apiClient.getCharts()

        Assert.assertTrue(result.isSuccess)
        val charts = result.getOrNull()!!
        Assert.assertEquals(0, charts.chartsCount)
        Assert.assertTrue(charts.charts?.isEmpty() ?: true)
    }

    // ===== Chart Data Integration Tests =====

    @Test
    fun testGetDataIntegration() = runBlocking {
        val mockData = NetdataChartData(
            labels = listOf("user", "system", "idle"),
            data = listOf(
                listOf(25.5, 10.2, 64.3),
                listOf(30.1, 12.5, 57.4),
                listOf(28.3, 11.1, 60.6)
            ),
            viewLatestValues = listOf(28.3, 11.1, 60.6),
            dimensions = 3,
            points = 3,
            format = "json",
            result = "ok",
            min = 10.2,
            max = 64.3
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(mockData))
        )

        val result = apiClient.getData(
            chart = "system.cpu",
            after = -60,
            points = 60
        )

        Assert.assertTrue(result.isSuccess)
        val data = result.getOrNull()!!
        Assert.assertEquals(3, data.dimensions)
        Assert.assertEquals(3, data.points)
        Assert.assertEquals("ok", data.result)
        Assert.assertEquals(3, data.data?.size)
    }

    @Test
    fun testGetDataWithDimensionFilter() = runBlocking {
        val mockData = NetdataChartData(
            labels = listOf("used"),
            data = listOf(listOf(15360.0), listOf(15872.0)),
            viewLatestValues = listOf(15872.0),
            dimensions = 1,
            points = 2,
            format = "json",
            result = "ok",
            min = 15360.0,
            max = 15872.0
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(mockData))
        )

        val result = apiClient.getData(
            chart = "system.ram",
            dimensions = "used",
            points = 60
        )

        Assert.assertTrue(result.isSuccess)
        val data = result.getOrNull()!!
        Assert.assertEquals(1, data.dimensions)
        Assert.assertEquals(listOf("used"), data.labels)
    }

    // ===== Alarms Integration Tests =====

    @Test
    fun testGetAlarmsIntegration() = runBlocking {
        val mockAlarms = NetdataAlarms(
            hostname = "production-server-01",
            latestAlarmLogUniqueId = 98765L,
            status = true,
            now = 1698003600L,
            alarms = mapOf(
                "cpu_usage" to NetdataAlarm(
                    id = 1L,
                    status = "WARNING",
                    name = "cpu_usage",
                    chart = "system.cpu",
                    family = "cpu",
                    active = true,
                    disabled = false,
                    silenced = false,
                    exec = "alarm-notify.sh",
                    recipient = "sysadmin",
                    source = "health.d/cpu.conf",
                    units = "%",
                    info = "CPU usage is high",
                    value = 85.5,
                    lastStatusChange = 1698003000L,
                    lastUpdated = 1698003600L,
                    nextUpdate = 1698003601L,
                    updateEvery = 1,
                    delayUpDuration = 0,
                    delayDownDuration = 0,
                    delayMaxDuration = 0,
                    delayMultiplier = 1.0,
                    delay = 0,
                    delayUpToTimestamp = 0L,
                    warnRepeatEvery = 60,
                    critRepeatEvery = 300
                ),
                "disk_space" to NetdataAlarm(
                    id = 2L,
                    status = "CRITICAL",
                    name = "disk_space",
                    chart = "disk.space",
                    family = "disk",
                    active = true,
                    disabled = false,
                    silenced = false,
                    exec = "alarm-notify.sh",
                    recipient = "sysadmin",
                    source = "health.d/disk.conf",
                    units = "%",
                    info = "Disk space critically low",
                    value = 95.2,
                    lastStatusChange = 1698002000L,
                    lastUpdated = 1698003600L,
                    nextUpdate = 1698003601L,
                    updateEvery = 1,
                    delayUpDuration = 0,
                    delayDownDuration = 0,
                    delayMaxDuration = 0,
                    delayMultiplier = 1.0,
                    delay = 0,
                    delayUpToTimestamp = 0L,
                    warnRepeatEvery = 60,
                    critRepeatEvery = 300
                )
            )
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(mockAlarms))
        )

        val result = apiClient.getAlarms()

        Assert.assertTrue(result.isSuccess)
        val alarms = result.getOrNull()!!
        Assert.assertEquals("production-server-01", alarms.hostname)
        Assert.assertEquals(2, alarms.alarms?.size)
        Assert.assertEquals("WARNING", alarms.alarms?.get("cpu_usage")?.status)
        Assert.assertEquals("CRITICAL", alarms.alarms?.get("disk_space")?.status)
    }

    @Test
    fun testGetAlarmsNoActiveAlarms() = runBlocking {
        val mockAlarms = NetdataAlarms(
            hostname = "healthy-server",
            latestAlarmLogUniqueId = 123L,
            status = true,
            now = 1698003600L,
            alarms = emptyMap()
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(mockAlarms))
        )

        val result = apiClient.getAlarms()

        Assert.assertTrue(result.isSuccess)
        val alarms = result.getOrNull()!!
        Assert.assertEquals(0, alarms.alarms?.size)
    }

    @Test
    fun testGetAlarmLogIntegration() = runBlocking {
        val mockAlarmLog = listOf(
            NetdataAlarm(
                id = 1L,
                status = "WARNING",
                name = "ram_usage",
                chart = "system.ram",
                family = "memory",
                active = false,
                disabled = false,
                silenced = false,
                exec = "alarm-notify.sh",
                recipient = "sysadmin",
                source = "health.d/ram.conf",
                units = "%",
                info = "RAM usage was high",
                value = 82.0,
                lastStatusChange = 1698001000L,
                lastUpdated = 1698002000L,
                nextUpdate = 1698002001L,
                updateEvery = 1,
                delayUpDuration = 0,
                delayDownDuration = 0,
                delayMaxDuration = 0,
                delayMultiplier = 1.0,
                delay = 0,
                delayUpToTimestamp = 0L,
                warnRepeatEvery = 60,
                critRepeatEvery = 300
            ),
            NetdataAlarm(
                id = 2L,
                status = "CLEAR",
                name = "cpu_usage",
                chart = "system.cpu",
                family = "cpu",
                active = false,
                disabled = false,
                silenced = false,
                exec = "alarm-notify.sh",
                recipient = "sysadmin",
                source = "health.d/cpu.conf",
                units = "%",
                info = "CPU usage returned to normal",
                value = 45.0,
                lastStatusChange = 1698003000L,
                lastUpdated = 1698003600L,
                nextUpdate = 1698003601L,
                updateEvery = 1,
                delayUpDuration = 0,
                delayDownDuration = 0,
                delayMaxDuration = 0,
                delayMultiplier = 1.0,
                delay = 0,
                delayUpToTimestamp = 0L,
                warnRepeatEvery = 60,
                critRepeatEvery = 300
            )
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(mockAlarmLog))
        )

        val result = apiClient.getAlarmLog()

        Assert.assertTrue(result.isSuccess)
        val alarmLog = result.getOrNull()!!
        Assert.assertEquals(2, alarmLog.size)
        Assert.assertEquals("WARNING", alarmLog[0].status)
        Assert.assertEquals("CLEAR", alarmLog[1].status)
    }

    // ===== All Metrics Integration Tests =====

    @Test
    fun testGetAllMetricsIntegration() = runBlocking {
        val mockMetrics = NetdataAllMetrics(
            hostname = "production-server-01",
            version = "v1.42.0",
            os = "linux",
            releaseChannel = "stable",
            timezone = "UTC",
            updateEvery = 1,
            history = 3600,
            memoryMode = "dbengine",
            health = NetdataHealth(
                status = "WARNING",
                critical = 1,
                warning = 2,
                undefined = 0,
                uninitialized = 0,
                clear = 15
            ),
            charts = mapOf(
                "system.cpu" to NetdataMetricChart(
                    id = "system.cpu",
                    name = "cpu",
                    type = "system",
                    family = "cpu",
                    context = "system.cpu",
                    units = "%",
                    lastUpdated = 1698003600L,
                    dimensions = mapOf(
                        "user" to NetdataMetricDimension("user", 25.5),
                        "system" to NetdataMetricDimension("system", 10.2),
                        "idle" to NetdataMetricDimension("idle", 64.3)
                    )
                )
            )
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(mockMetrics))
        )

        val result = apiClient.getAllMetrics()

        Assert.assertTrue(result.isSuccess)
        val metrics = result.getOrNull()!!
        Assert.assertEquals("production-server-01", metrics.hostname)
        Assert.assertEquals("WARNING", metrics.health?.status)
        Assert.assertEquals(1, metrics.health?.critical)
        Assert.assertEquals(2, metrics.health?.warning)
        Assert.assertEquals(1, metrics.charts?.size)
    }

    // ===== Additional Integration Tests =====

    @Test
    fun testGetContextsIntegration() = runBlocking {
        val mockContexts = NetdataContexts(
            contexts = mapOf(
                "system.cpu" to NetdataContext(
                    id = "system.cpu",
                    family = "cpu",
                    title = "Total CPU Utilization",
                    units = "%",
                    type = "stacked",
                    priority = 100,
                    charts = listOf("system.cpu", "cpu.cpu0", "cpu.cpu1")
                )
            )
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(mockContexts))
        )

        val result = apiClient.getContexts()

        Assert.assertTrue(result.isSuccess)
        val contexts = result.getOrNull()!!
        Assert.assertEquals(1, contexts.contexts?.size)
        Assert.assertEquals(3, contexts.contexts?.get("system.cpu")?.charts?.size)
    }

    @Test
    fun testGetNodeIntegration() = runBlocking {
        val mockNode = NetdataNode(
            hostname = "production-server-01",
            machineGuid = "abc123-def456-ghi789",
            version = "v1.42.0",
            os = "linux",
            timezone = "UTC",
            collectors = listOf("proc", "diskspace", "cgroups", "tc"),
            hostLabels = mapOf(
                "environment" to "production",
                "datacenter" to "us-east-1",
                "team" to "platform"
            )
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(mockNode))
        )

        val result = apiClient.getNode()

        Assert.assertTrue(result.isSuccess)
        val node = result.getOrNull()!!
        Assert.assertEquals("production-server-01", node.hostname)
        Assert.assertEquals(4, node.collectors?.size)
        Assert.assertEquals(3, node.hostLabels?.size)
    }

    @Test
    fun testGetWeightsIntegration() = runBlocking {
        val mockWeights = NetdataWeights(
            charts = mapOf(
                "system.cpu" to 100.0,
                "system.ram" to 95.0,
                "disk.space" to 90.0,
                "net.eth0" to 85.0
            )
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(mockWeights))
        )

        val result = apiClient.getWeights()

        Assert.assertTrue(result.isSuccess)
        val weights = result.getOrNull()!!
        Assert.assertEquals(4, weights.charts?.size)
        Assert.assertEquals(100.0, weights.charts?.get("system.cpu") ?: 0.0, 0.01)
    }

    // ===== Error Scenario Integration Tests =====

    @Test
    fun testServerError500() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        val result = apiClient.getInfo()

        Assert.assertTrue(result.isFailure)
    }

    @Test
    fun testUnauthorizedError401() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("Unauthorized")
        )

        val result = apiClient.getCharts()

        Assert.assertTrue(result.isFailure)
    }

    @Test
    fun testMalformedJsonResponse() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{invalid json}")
        )

        val result = apiClient.getInfo()

        Assert.assertTrue(result.isFailure)
    }
}
