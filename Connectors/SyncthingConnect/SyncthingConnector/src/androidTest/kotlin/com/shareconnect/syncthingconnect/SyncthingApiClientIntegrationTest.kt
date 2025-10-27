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
