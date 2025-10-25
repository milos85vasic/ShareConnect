package com.shareconnect.syncthingconnect.api

import com.google.gson.Gson
import com.shareconnect.syncthingconnect.data.api.SyncthingApiClient
import com.shareconnect.syncthingconnect.data.api.SyncthingApiService
import com.shareconnect.syncthingconnect.data.models.*
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncthingApiClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: SyncthingApiClient
    private lateinit var mockService: SyncthingApiService
    private val gson = Gson()

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockService = mock()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test getConfig returns success`() = runTest {
        val config = createMockConfig()
        whenever(mockService.getConfig("test-api-key"))
            .thenReturn(Response.success(config))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.getConfig()
        assertTrue(result.isSuccess)
        assertEquals(config.version, result.getOrNull()?.version)
    }

    @Test
    fun `test getConfig returns failure on error`() = runTest {
        whenever(mockService.getConfig("test-api-key"))
            .thenReturn(Response.error(404, okhttp3.ResponseBody.create(null, "")))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.getConfig()
        assertTrue(result.isFailure)
    }

    @Test
    fun `test getStatus returns success`() = runTest {
        val status = createMockStatus()
        whenever(mockService.getStatus("test-api-key"))
            .thenReturn(Response.success(status))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.getStatus()
        assertTrue(result.isSuccess)
        assertEquals(status.myID, result.getOrNull()?.myID)
    }

    @Test
    fun `test getVersion returns success`() = runTest {
        val version = createMockVersion()
        whenever(mockService.getVersion("test-api-key"))
            .thenReturn(Response.success(version))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.getVersion()
        assertTrue(result.isSuccess)
        assertEquals(version.version, result.getOrNull()?.version)
    }

    @Test
    fun `test getConnections returns success`() = runTest {
        val connections = createMockConnections()
        whenever(mockService.getConnections("test-api-key"))
            .thenReturn(Response.success(connections))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.getConnections()
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test restart returns success`() = runTest {
        val response = SyncthingApiResponse(message = "Restarting")
        whenever(mockService.restart("test-api-key"))
            .thenReturn(Response.success(response))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.restart()
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test shutdown returns success`() = runTest {
        val response = SyncthingApiResponse(message = "Shutting down")
        whenever(mockService.shutdown("test-api-key"))
            .thenReturn(Response.success(response))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.shutdown()
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test getDBStatus returns success`() = runTest {
        val dbStatus = createMockDBStatus()
        whenever(mockService.getDBStatus("test-api-key", "test-folder"))
            .thenReturn(Response.success(dbStatus))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.getDBStatus("test-folder")
        assertTrue(result.isSuccess)
        assertEquals(dbStatus.state, result.getOrNull()?.state)
    }

    @Test
    fun `test browse returns success`() = runTest {
        val entries = listOf(createMockBrowseEntry())
        whenever(mockService.browse("test-api-key", "test-folder", null, null, null))
            .thenReturn(Response.success(entries))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.browse("test-folder")
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `test getCompletion returns success`() = runTest {
        val completion = createMockCompletion()
        whenever(mockService.getCompletion("test-api-key", "test-folder", "test-device"))
            .thenReturn(Response.success(completion))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.getCompletion("test-folder", "test-device")
        assertTrue(result.isSuccess)
        assertEquals(completion.completion, result.getOrNull()?.completion)
    }

    @Test
    fun `test scan returns success`() = runTest {
        val response = SyncthingApiResponse(message = "Scanning")
        whenever(mockService.scan("test-api-key", "test-folder", null, null))
            .thenReturn(Response.success(response))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.scan("test-folder")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test getFolderStats returns success`() = runTest {
        val stats = mapOf("folder1" to createMockFolderStats())
        whenever(mockService.getFolderStats("test-api-key"))
            .thenReturn(Response.success(stats))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.getFolderStats()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `test getDeviceStats returns success`() = runTest {
        val stats = mapOf("device1" to createMockDeviceStats())
        whenever(mockService.getDeviceStats("test-api-key"))
            .thenReturn(Response.success(stats))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.getDeviceStats()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `test ping returns success`() = runTest {
        val pingResponse = mapOf("ping" to "pong")
        whenever(mockService.ping("test-api-key"))
            .thenReturn(Response.success(pingResponse))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.ping()
        assertTrue(result.isSuccess)
        assertEquals("pong", result.getOrNull()?.get("ping"))
    }

    @Test
    fun `test pauseDevice returns success`() = runTest {
        val response = SyncthingApiResponse(message = "Device paused")
        whenever(mockService.pauseDevice("test-api-key", "device-id"))
            .thenReturn(Response.success(response))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.pauseDevice("device-id")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test resumeDevice returns success`() = runTest {
        val response = SyncthingApiResponse(message = "Device resumed")
        whenever(mockService.resumeDevice("test-api-key", "device-id"))
            .thenReturn(Response.success(response))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.resumeDevice("device-id")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test pauseFolder returns success`() = runTest {
        val response = SyncthingApiResponse(message = "Folder paused")
        whenever(mockService.pauseFolder("test-api-key", "folder-id"))
            .thenReturn(Response.success(response))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.pauseFolder("folder-id")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test resumeFolder returns success`() = runTest {
        val response = SyncthingApiResponse(message = "Folder resumed")
        whenever(mockService.resumeFolder("test-api-key", "folder-id"))
            .thenReturn(Response.success(response))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.resumeFolder("folder-id")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test getRandomString returns success`() = runTest {
        val randomStr = mapOf("random" to "abcd1234")
        whenever(mockService.getRandomString("test-api-key", 32))
            .thenReturn(Response.success(randomStr))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.getRandomString(32)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test updateConfig returns success`() = runTest {
        val config = createMockConfig()
        val response = SyncthingApiResponse(message = "Config updated")
        whenever(mockService.updateConfig("test-api-key", config))
            .thenReturn(Response.success(response))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.updateConfig(config)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test override returns success`() = runTest {
        val response = SyncthingApiResponse(message = "Override applied")
        whenever(mockService.override("test-api-key", "folder-id"))
            .thenReturn(Response.success(response))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.override("folder-id")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test revert returns success`() = runTest {
        val response = SyncthingApiResponse(message = "Revert applied")
        whenever(mockService.revert("test-api-key", "folder-id"))
            .thenReturn(Response.success(response))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.revert("folder-id")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test setPriority returns success`() = runTest {
        val response = SyncthingApiResponse(message = "Priority set")
        whenever(mockService.setPriority("test-api-key", "folder-id", "file.txt"))
            .thenReturn(Response.success(response))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.setPriority("folder-id", "file.txt")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test performUpgrade returns success`() = runTest {
        val response = SyncthingApiResponse(message = "Upgrading")
        whenever(mockService.performUpgrade("test-api-key"))
            .thenReturn(Response.success(response))

        val client = SyncthingApiClient(
            "http://localhost:8384",
            "test-api-key",
            mockService
        )

        val result = client.performUpgrade()
        assertTrue(result.isSuccess)
    }

    // Helper functions to create mock objects
    private fun createMockConfig() = SyncthingConfig(
        version = 37,
        folders = emptyList(),
        devices = emptyList(),
        gui = createMockGUI(),
        options = createMockOptions()
    )

    private fun createMockGUI() = SyncthingGUI(
        enabled = true,
        address = "127.0.0.1:8384",
        unixSocketPermissions = "",
        user = "admin",
        password = "password",
        authMode = "static",
        useTLS = false,
        apiKey = "test-key",
        insecureAdminAccess = false,
        theme = "default",
        debugging = false,
        insecureSkipHostcheck = false,
        insecureAllowFrameLoading = false
    )

    private fun createMockOptions() = SyncthingOptions(
        listenAddresses = listOf("default"),
        globalAnnounceServers = emptyList(),
        globalAnnounceEnabled = true,
        localAnnounceEnabled = true,
        localAnnouncePort = 21027,
        localAnnounceMCAddr = "[ff12::8384]:21027",
        maxSendKbps = 0,
        maxRecvKbps = 0,
        reconnectionIntervalS = 60,
        relaysEnabled = true,
        relayReconnectIntervalM = 10,
        startBrowser = true,
        natEnabled = true,
        natLeaseMinutes = 60,
        natRenewalMinutes = 30,
        natTimeoutSeconds = 10,
        urAccepted = 0,
        urSeen = 0,
        urUniqueID = "",
        urURL = "https://data.syncthing.net/newdata",
        urPostInsecurely = false,
        urInitialDelayS = 1800,
        autoUpgradeIntervalH = 12,
        upgradeToPreReleases = false,
        keepTemporariesH = 24,
        cacheIgnoredFiles = false,
        progressUpdateIntervalS = 5,
        limitBandwidthInLan = false,
        minHomeDiskFree = SyncthingSize(1.0, "%"),
        releasesURL = "https://upgrades.syncthing.net/meta.json",
        alwaysLocalNets = emptyList(),
        overwriteRemoteDeviceNamesOnConnect = false,
        tempIndexMinBlocks = 10,
        unackedNotificationIDs = emptyList(),
        trafficClass = 0,
        defaultFolderPath = "~",
        setLowPriority = true,
        maxFolderConcurrency = 0,
        crashReportingEnabled = true,
        crashReportingURL = "https://crash.syncthing.net/newcrash",
        stunKeepaliveStartS = 180,
        stunKeepaliveMinS = 20,
        stunServers = listOf("default"),
        databaseTuning = "auto",
        maxConcurrentIncomingRequestKiB = 0,
        announceLANAddresses = true,
        sendFullIndexOnUpgrade = false
    )

    private fun createMockStatus() = SyncthingStatus(
        alloc = 0,
        connectionServiceStatus = emptyMap(),
        cpuPercent = 0.0,
        discoveryEnabled = true,
        discoveryErrors = emptyMap(),
        discoveryMethods = 0,
        goroutines = 0,
        guiAddressOverridden = false,
        guiAddressUsed = "127.0.0.1:8384",
        lastDialStatus = emptyMap(),
        myID = "TEST-ID-123",
        pathSeparator = "/",
        startTime = "2024-01-01T00:00:00Z",
        sys = 0,
        themes = emptyList(),
        tilde = "~",
        uptime = 0,
        urVersionMax = 3
    )

    private fun createMockVersion() = SyncthingVersion(
        arch = "amd64",
        codename = "Fermium Flea",
        container = false,
        isBeta = false,
        isCandidate = false,
        isRelease = true,
        longVersion = "syncthing v1.23.0 \"Fermium Flea\" (go1.20 linux-amd64) teamcity@build.syncthing.net 2023-06-27 06:16:01 UTC",
        os = "linux",
        stamp = "2023-06-27T06:16:01Z",
        tags = emptyList(),
        user = "teamcity",
        version = "v1.23.0"
    )

    private fun createMockConnections() = SyncthingConnections(
        connections = emptyMap(),
        total = SyncthingConnectionStats(
            at = "2024-01-01T00:00:00Z",
            inBytesTotal = 0,
            outBytesTotal = 0
        )
    )

    private fun createMockDBStatus() = SyncthingDBStatus(
        errors = 0,
        globalBytes = 0,
        globalDeleted = 0,
        globalDirectories = 0,
        globalFiles = 0,
        globalSymlinks = 0,
        globalTotalItems = 0,
        ignorePatterns = false,
        inSyncBytes = 0,
        inSyncFiles = 0,
        invalid = "",
        localBytes = 0,
        localDeleted = 0,
        localDirectories = 0,
        localFiles = 0,
        localSymlinks = 0,
        localTotalItems = 0,
        needBytes = 0,
        needDeletes = 0,
        needDirectories = 0,
        needFiles = 0,
        needSymlinks = 0,
        needTotalItems = 0,
        pullErrors = 0,
        receiveOnlyChangedBytes = 0,
        receiveOnlyChangedDeletes = 0,
        receiveOnlyChangedDirectories = 0,
        receiveOnlyChangedFiles = 0,
        receiveOnlyChangedSymlinks = 0,
        receiveOnlyTotalItems = 0,
        sequence = 0,
        state = "idle",
        stateChanged = "2024-01-01T00:00:00Z",
        version = 0
    )

    private fun createMockBrowseEntry() = SyncthingBrowseEntry(
        name = "test.txt",
        size = 1024,
        modified = "2024-01-01T00:00:00Z",
        type = "file"
    )

    private fun createMockCompletion() = SyncthingCompletion(
        completion = 100.0,
        globalBytes = 1024,
        needBytes = 0,
        globalItems = 10,
        needItems = 0,
        needDeletes = 0,
        sequence = 100
    )

    private fun createMockFolderStats() = SyncthingFolderStats(
        lastFile = SyncthingLastFile(
            at = "2024-01-01T00:00:00Z",
            deleted = false,
            filename = "test.txt"
        ),
        lastScan = "2024-01-01T00:00:00Z"
    )

    private fun createMockDeviceStats() = SyncthingDeviceStats(
        lastSeen = "2024-01-01T00:00:00Z",
        lastConnectionDurationS = 100.0
    )
}
