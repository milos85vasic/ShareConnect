package com.shareconnect.netdataconnect.data.api

import com.shareconnect.netdataconnect.data.models.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

/**
 * Unit tests for NetdataApiClient using MockK
 * Tests all major API client operations using mocked service
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = com.shareconnect.netdataconnect.TestApplication::class)
class NetdataApiClientMockKTest {

    private lateinit var mockService: NetdataApiService
    private lateinit var apiClient: NetdataApiClient

    @Before
    fun setUp() {
        mockService = mockk()
        apiClient = NetdataApiClient(
            serverUrl = "http://test.server:19999",
            netdataApiService = mockService
        )
    }

    // ===== Server Information Tests =====

    @Test
    fun `test getInfo success`() = runBlocking {
        val mockInfo = NetdataInfo(
            version = "v1.42.0",
            uid = "abc123",
            timezone = "UTC",
            osName = "Ubuntu",
            osId = "ubuntu",
            osIdLike = "debian",
            osVersion = "22.04",
            osVersionId = "22.04",
            osDetection = "systemd",
            coresTotal = 8,
            totalDiskSpace = 500000000000L,
            cpuFreq = "3.6GHz",
            ramTotal = 32000000000L,
            updateEvery = 1,
            history = 3600,
            memoryMode = "dbengine",
            hostLabels = mapOf("environment" to "production")
        )

        coEvery { mockService.getInfo() } returns Response.success(mockInfo)

        val result = apiClient.getInfo()

        assertTrue(result.isSuccess)
        val info = result.getOrNull()!!
        assertEquals("v1.42.0", info.version)
        assertEquals(8, info.coresTotal)
        assertEquals("Ubuntu", info.osName)

        coVerify { mockService.getInfo() }
    }

    @Test
    fun `test getInfo failure`() = runBlocking {
        coEvery { mockService.getInfo() } returns Response.error(500, mockk(relaxed = true))

        val result = apiClient.getInfo()

        assertTrue(result.isFailure)
    }

    // ===== Charts Tests =====

    @Test
    fun `test getCharts success`() = runBlocking {
        val mockCharts = NetdataCharts(
            hostname = "test-server",
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
                        "user" to NetdataDimension("user", "absolute", 1, 1, false)
                    ),
                    green = 75.0,
                    red = 90.0
                )
            ),
            chartsCount = 1,
            dimensionsCount = 1,
            alarmsCount = 5,
            rrdMemoryBytes = 104857600L
        )

        coEvery { mockService.getCharts() } returns Response.success(mockCharts)

        val result = apiClient.getCharts()

        assertTrue(result.isSuccess)
        val charts = result.getOrNull()!!
        assertEquals("test-server", charts.hostname)
        assertEquals(1, charts.chartsCount)
        assertEquals(1, charts.charts?.size)

        coVerify { mockService.getCharts() }
    }

    // ===== Chart Data Tests =====

    @Test
    fun `test getData success`() = runBlocking {
        val mockData = NetdataChartData(
            labels = listOf("user", "system", "idle"),
            data = listOf(
                listOf(25.5, 10.2, 64.3),
                listOf(30.1, 12.5, 57.4)
            ),
            viewLatestValues = listOf(30.1, 12.5, 57.4),
            dimensions = 3,
            points = 2,
            format = "json",
            result = "ok",
            min = 10.2,
            max = 64.3
        )

        coEvery { mockService.getData(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns Response.success(mockData)

        val result = apiClient.getData(chart = "system.cpu", points = 60)

        assertTrue(result.isSuccess)
        val data = result.getOrNull()!!
        assertEquals(3, data.dimensions)
        assertEquals(2, data.points)
        assertEquals("ok", data.result)

        coVerify { mockService.getData(any(), any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `test getData with parameters`() = runBlocking {
        val mockData = NetdataChartData(
            labels = listOf("dimension1"),
            data = listOf(listOf(42.0)),
            viewLatestValues = listOf(42.0),
            dimensions = 1,
            points = 1,
            format = "json",
            result = "ok",
            min = 42.0,
            max = 42.0
        )

        coEvery { mockService.getData(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns Response.success(mockData)

        val result = apiClient.getData(
            chart = "system.ram",
            after = -60L,
            before = 0L,
            points = 60,
            group = "average",
            dimensions = "used,cached"
        )

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.dimensions)

        coVerify { mockService.getData(any(), any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    // ===== Alarms Tests =====

    @Test
    fun `test getAlarms success`() = runBlocking {
        val mockAlarms = NetdataAlarms(
            hostname = "test-server",
            latestAlarmLogUniqueId = 12345L,
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
                )
            )
        )

        coEvery { mockService.getAlarms(any()) } returns Response.success(mockAlarms)

        val result = apiClient.getAlarms()

        assertTrue(result.isSuccess)
        val alarms = result.getOrNull()!!
        assertEquals("test-server", alarms.hostname)
        assertEquals(1, alarms.alarms?.size)
        assertEquals("WARNING", alarms.alarms?.get("cpu_usage")?.status)

        coVerify { mockService.getAlarms(any()) }
    }

    @Test
    fun `test getAlarms with all parameter`() = runBlocking {
        val mockAlarms = NetdataAlarms(
            hostname = "test-server",
            latestAlarmLogUniqueId = 12345L,
            status = true,
            now = 1698003600L,
            alarms = mapOf()
        )

        coEvery { mockService.getAlarms(any()) } returns Response.success(mockAlarms)

        val result = apiClient.getAlarms(all = true)

        assertTrue(result.isSuccess)

        coVerify { mockService.getAlarms(true) }
    }

    @Test
    fun `test getAlarmLog success`() = runBlocking {
        val mockAlarmLog = listOf(
            NetdataAlarm(
                id = 1L,
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
                info = "Disk space is critically low",
                value = 95.2,
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

        coEvery { mockService.getAlarmLog(any(), any()) } returns Response.success(mockAlarmLog)

        val result = apiClient.getAlarmLog()

        assertTrue(result.isSuccess)
        val alarmLog = result.getOrNull()!!
        assertEquals(1, alarmLog.size)
        assertEquals("CRITICAL", alarmLog[0].status)

        coVerify { mockService.getAlarmLog(any(), any()) }
    }

    // ===== All Metrics Tests =====

    @Test
    fun `test getAllMetrics success`() = runBlocking {
        val mockMetrics = NetdataAllMetrics(
            hostname = "test-server",
            version = "v1.42.0",
            os = "linux",
            releaseChannel = "stable",
            timezone = "UTC",
            updateEvery = 1,
            history = 3600,
            memoryMode = "dbengine",
            health = NetdataHealth(
                status = "WARNING",
                critical = 0,
                warning = 1,
                undefined = 0,
                uninitialized = 0,
                clear = 5
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
                        "system" to NetdataMetricDimension("system", 10.2)
                    )
                )
            )
        )

        coEvery { mockService.getAllMetrics(any(), any(), any(), any(), any(), any(), any(), any()) } returns Response.success(mockMetrics)

        val result = apiClient.getAllMetrics()

        assertTrue(result.isSuccess)
        val metrics = result.getOrNull()!!
        assertEquals("test-server", metrics.hostname)
        assertEquals("WARNING", metrics.health?.status)
        assertEquals(1, metrics.charts?.size)

        coVerify { mockService.getAllMetrics(any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `test getAllMetrics with parameters`() = runBlocking {
        val mockMetrics = NetdataAllMetrics(
            hostname = "test-server",
            version = "v1.42.0",
            os = "linux",
            releaseChannel = "stable",
            timezone = "UTC",
            updateEvery = 1,
            history = 3600,
            memoryMode = "dbengine",
            health = null,
            charts = mapOf()
        )

        coEvery { mockService.getAllMetrics(any(), any(), any(), any(), any(), any(), any(), any()) } returns Response.success(mockMetrics)

        val result = apiClient.getAllMetrics(
            format = "prometheus",
            help = "yes",
            types = "yes",
            timestamps = "yes"
        )

        assertTrue(result.isSuccess)

        coVerify { mockService.getAllMetrics("prometheus", "yes", "yes", "yes", any(), any(), any(), any()) }
    }

    // ===== Badge Tests =====

    @Test
    fun `test getBadge success`() = runBlocking {
        val svgContent = "<svg>...</svg>"

        coEvery { mockService.getBadge(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns Response.success(svgContent)

        val result = apiClient.getBadge(chart = "system.cpu")

        assertTrue(result.isSuccess)
        val badge = result.getOrNull()!!
        assertTrue(badge.contains("svg"))

        coVerify { mockService.getBadge(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `test getBadge with alarm`() = runBlocking {
        val svgContent = "<svg>...</svg>"

        coEvery { mockService.getBadge(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns Response.success(svgContent)

        val result = apiClient.getBadge(alarm = "cpu_usage", precision = 2)

        assertTrue(result.isSuccess)

        coVerify { mockService.getBadge(null, "cpu_usage", any(), any(), any(), any(), any(), any(), any(), 2) }
    }

    // ===== Contexts Tests =====

    @Test
    fun `test getContexts success`() = runBlocking {
        val mockContexts = NetdataContexts(
            contexts = mapOf(
                "system.cpu" to NetdataContext(
                    id = "system.cpu",
                    family = "cpu",
                    title = "Total CPU Utilization",
                    units = "%",
                    type = "stacked",
                    priority = 100,
                    charts = listOf("system.cpu", "system.core")
                )
            )
        )

        coEvery { mockService.getContexts() } returns Response.success(mockContexts)

        val result = apiClient.getContexts()

        assertTrue(result.isSuccess)
        val contexts = result.getOrNull()!!
        assertEquals(1, contexts.contexts?.size)
        assertEquals("Total CPU Utilization", contexts.contexts?.get("system.cpu")?.title)

        coVerify { mockService.getContexts() }
    }

    // ===== Functions Tests =====

    @Test
    fun `test getFunctions success`() = runBlocking {
        val mockFunctions = NetdataFunctions(
            functions = listOf(
                NetdataFunction(
                    name = "top",
                    help = "Show top processes",
                    tags = listOf("processes", "system"),
                    access = "members",
                    priority = 100
                )
            )
        )

        coEvery { mockService.getFunctions(any()) } returns Response.success(mockFunctions)

        val result = apiClient.getFunctions("system.cpu")

        assertTrue(result.isSuccess)
        val functions = result.getOrNull()!!
        assertEquals(1, functions.functions?.size)
        assertEquals("top", functions.functions?.get(0)?.name)

        coVerify { mockService.getFunctions("system.cpu") }
    }

    // ===== Node Tests =====

    @Test
    fun `test getNode success`() = runBlocking {
        val mockNode = NetdataNode(
            hostname = "test-server",
            machineGuid = "abc123-def456",
            version = "v1.42.0",
            os = "linux",
            timezone = "UTC",
            collectors = listOf("proc", "diskspace", "cgroups"),
            hostLabels = mapOf("environment" to "production", "region" to "us-east-1")
        )

        coEvery { mockService.getNode() } returns Response.success(mockNode)

        val result = apiClient.getNode()

        assertTrue(result.isSuccess)
        val node = result.getOrNull()!!
        assertEquals("test-server", node.hostname)
        assertEquals(3, node.collectors?.size)

        coVerify { mockService.getNode() }
    }

    // ===== Weights Tests =====

    @Test
    fun `test getWeights success`() = runBlocking {
        val mockWeights = NetdataWeights(
            charts = mapOf(
                "system.cpu" to 100.0,
                "system.ram" to 90.0,
                "disk.space" to 80.0
            )
        )

        coEvery { mockService.getWeights() } returns Response.success(mockWeights)

        val result = apiClient.getWeights()

        assertTrue(result.isSuccess)
        val weights = result.getOrNull()!!
        assertEquals(3, weights.charts?.size)
        assertEquals(100.0, weights.charts?.get("system.cpu") ?: 0.0, 0.01)

        coVerify { mockService.getWeights() }
    }

    // ===== Error Handling Tests =====

    @Test
    fun `test HTTP 404 error handling`() = runBlocking {
        coEvery { mockService.getCharts() } returns Response.error(404, mockk(relaxed = true))

        val result = apiClient.getCharts()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test HTTP 500 server error`() = runBlocking {
        coEvery { mockService.getInfo() } returns Response.error(500, mockk(relaxed = true))

        val result = apiClient.getInfo()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test exception handling`() = runBlocking {
        coEvery { mockService.getAlarms(any()) } throws Exception("Network error")

        val result = apiClient.getAlarms()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test getData exception handling`() = runBlocking {
        coEvery { mockService.getData(any(), any(), any(), any(), any(), any(), any(), any(), any()) } throws Exception("Connection timeout")

        val result = apiClient.getData("system.cpu")

        assertTrue(result.isFailure)
    }
}
