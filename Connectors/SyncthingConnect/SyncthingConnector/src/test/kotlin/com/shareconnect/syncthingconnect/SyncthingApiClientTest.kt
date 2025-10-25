package com.shareconnect.syncthingconnect

import com.shareconnect.syncthingconnect.data.api.SyncthingApiClient
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.Assert.*

class SyncthingApiClientTest {
    private lateinit var mockServer: MockWebServer
    private lateinit var client: SyncthingApiClient

    @Before
    fun setup() {
        mockServer = MockWebServer()
        mockServer.start()
        client = SyncthingApiClient(mockServer.url("/").toString(), "test-api-key")
    }

    @After
    fun teardown() {
        mockServer.shutdown()
    }

    @Test
    fun `getStatus returns system status`() = runTest {
        mockServer.enqueue(MockResponse().setBody("""{"myID":"ABC123","uptime":3600,"cpuPercent":5.2,"goroutines":50,"discoveryEnabled":true,"connectionServiceStatus":{},"alloc":100000,"sys":200000,"pathSeparator":"/","startTime":"2024-01-01T00:00:00Z"}"""))
        val result = client.getStatus()
        assertTrue(result.isSuccess)
        assertEquals("ABC123", result.getOrNull()?.myID)
    }

    @Test
    fun `getVersion returns version info`() = runTest {
        mockServer.enqueue(MockResponse().setBody("""{"arch":"amd64","longVersion":"syncthing v1.23.0","os":"linux","version":"v1.23.0"}"""))
        val result = client.getVersion()
        assertTrue(result.isSuccess)
        assertEquals("v1.23.0", result.getOrNull()?.version)
    }

    @Test
    fun `getConnections returns connection info`() = runTest {
        mockServer.enqueue(MockResponse().setBody("""{"connections":{},"total":{"at":"2024-01-01T00:00:00Z","inBytesTotal":1000,"outBytesTotal":2000}}"""))
        val result = client.getConnections()
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull()?.total)
    }

    @Test
    fun `getDatabaseStatus returns folder status`() = runTest {
        mockServer.enqueue(MockResponse().setBody("""{"errors":0,"globalBytes":1000000,"globalFiles":10,"localBytes":900000,"localFiles":9,"needBytes":100000,"needFiles":1,"state":"syncing","inSyncBytes":900000,"inSyncFiles":9,"globalDeleted":0,"localDeleted":0,"globalDirectories":5,"localDirectories":5,"needDeletes":0,"needDirectories":0}"""))
        val result = client.getDatabaseStatus("default")
        assertTrue(result.isSuccess)
        assertEquals("syncing", result.getOrNull()?.state)
    }

    @Test
    fun `browseDirectory returns directory listing`() = runTest {
        mockServer.enqueue(MockResponse().setBody("""[{"name":"file.txt","type":"file","size":1024,"modified":"2024-01-01T00:00:00Z"}]"""))
        val result = client.browseDirectory("default", "/")
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `getCompletion returns sync completion`() = runTest {
        mockServer.enqueue(MockResponse().setBody("""{"completion":95.5,"globalBytes":1000000,"needBytes":45000,"needDeletes":0,"needItems":2}"""))
        val result = client.getCompletion("device-id", "default")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.completion > 95.0)
    }

    @Test
    fun `scan triggers folder scan`() = runTest {
        mockServer.enqueue(MockResponse().setResponseCode(200))
        val result = client.scan("default")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `testConnection succeeds`() = runTest {
        mockServer.enqueue(MockResponse().setBody("""{"myID":"ABC","uptime":0,"cpuPercent":0,"goroutines":0,"discoveryEnabled":true,"connectionServiceStatus":{},"alloc":0,"sys":0,"pathSeparator":"/","startTime":"2024-01-01T00:00:00Z"}"""))
        val result = client.testConnection()
        assertTrue(result.isSuccess)
    }
}
