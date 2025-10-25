package com.shareconnect.syncthingconnect

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.syncthingconnect.data.api.SyncthingApiClient
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SyncthingApiClientIntegrationTest {
    private lateinit var mockServer: MockWebServer
    private lateinit var client: SyncthingApiClient

    @Before
    fun setup() {
        mockServer = MockWebServer()
        mockServer.start()
        client = SyncthingApiClient(mockServer.url("/").toString(), "test-key")
    }

    @After
    fun teardown() {
        mockServer.shutdown()
    }

    @Test
    fun testFullWorkflow() = runTest {
        mockServer.enqueue(MockResponse().setBody("""{"myID":"ABC","uptime":0,"cpuPercent":0,"goroutines":0,"discoveryEnabled":true,"connectionServiceStatus":{},"alloc":0,"sys":0,"pathSeparator":"/","startTime":"2024-01-01T00:00:00Z"}"""))
        val statusResult = client.getStatus()
        assert(statusResult.isSuccess)

        mockServer.enqueue(MockResponse().setBody("""{"errors":0,"globalBytes":1000,"globalFiles":1,"localBytes":1000,"localFiles":1,"needBytes":0,"needFiles":0,"state":"idle","inSyncBytes":1000,"inSyncFiles":1,"globalDeleted":0,"localDeleted":0,"globalDirectories":0,"localDirectories":0,"needDeletes":0,"needDirectories":0}"""))
        val dbResult = client.getDatabaseStatus("default")
        assert(dbResult.isSuccess)
    }
}
