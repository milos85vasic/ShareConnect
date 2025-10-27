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


package com.shareconnect.seafileconnect

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.seafileconnect.data.api.SeafileApiClient
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith

/**
 * Integration tests for SeafileApiClient with Android environment
 */
@RunWith(AndroidJUnit4::class)
class SeafileApiClientIntegrationTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var client: SeafileApiClient

    @Before
    fun setup() {
        mockServer = MockWebServer()
        mockServer.start()
        
        val baseUrl = mockServer.url("/").toString()
        client = SeafileApiClient(baseUrl, "test@example.com", "password")
    }

    @After
    fun teardown() {
        mockServer.shutdown()
    }

    @Test
    fun testFullWorkflow_AuthBrowseDownload() = runTest {
        // Auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        val authResult = client.authenticate()
        assert(authResult.isSuccess)

        // List libraries
        mockServer.enqueue(MockResponse().setBody("""[{"id":"repo-1","name":"Test","desc":null,"owner":"test@example.com","encrypted":false,"size":1024,"mtime":0,"permission":"rw","type":"repo"}]"""))
        val librariesResult = client.listLibraries()
        assert(librariesResult.isSuccess)

        // List directory
        mockServer.enqueue(MockResponse().setBody("""[{"id":"file-1","name":"test.txt","type":"file","size":100,"mtime":0}]"""))
        val dirResult = client.listDirectory("repo-1", "/")
        assert(dirResult.isSuccess)

        // Get download link
        mockServer.enqueue(MockResponse().setBody(""""https://example.com/download/abc""""))
        val downloadLinkResult = client.getDownloadLink("repo-1", "/test.txt")
        assert(downloadLinkResult.isSuccess)
    }

    @Test
    fun testEncryptedLibraryWorkflow() = runTest {
        // Auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        client.authenticate()

        // List encrypted library
        mockServer.enqueue(MockResponse().setBody("""[{"id":"repo-2","name":"Encrypted","desc":null,"owner":"test@example.com","encrypted":true,"size":2048,"mtime":0,"permission":"rw","type":"repo"}]"""))
        val result = client.listLibraries()
        assert(result.isSuccess)
        assert(result.getOrNull()!![0].encrypted)

        // Decrypt library
        mockServer.enqueue(MockResponse().setBody("""{"success":true}"""))
        val decryptResult = client.decryptLibrary("repo-2", "library-password")
        assert(decryptResult.isSuccess)
    }

    @Test
    fun testFileOperations() = runTest {
        // Auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        client.authenticate()

        // Create directory
        mockServer.enqueue(MockResponse().setBody("""{"success":true}"""))
        val createResult = client.createDirectory("repo-1", "/newfolder")
        assert(createResult.isSuccess)

        // Delete item
        mockServer.enqueue(MockResponse().setBody("""{"success":true}"""))
        val deleteResult = client.deleteItem("repo-1", "/oldfile.txt")
        assert(deleteResult.isSuccess)

        // Move file
        mockServer.enqueue(MockResponse().setBody("""{"success":true}"""))
        val moveResult = client.moveFile("repo-1", "/file.txt", "repo-1", "/moved/file.txt")
        assert(moveResult.isSuccess)
    }

    @Test
    fun testSearchFunctionality() = runTest {
        // Auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        client.authenticate()

        // Search
        mockServer.enqueue(MockResponse().setBody("""{"total":1,"results":[{"name":"result.txt","oid":"obj-1","repo_id":"repo-1","repo_name":"Library","is_dir":false,"size":1024,"mtime":0,"fullpath":"/result.txt"}]}"""))
        val searchResult = client.search("query")
        assert(searchResult.isSuccess)
        assert(searchResult.getOrNull()!!.total == 1)
    }

    @Test
    fun testErrorHandling_InvalidCredentials() = runTest {
        mockServer.enqueue(MockResponse().setResponseCode(401).setBody("""{"error_msg":"Invalid credentials"}"""))
        val result = client.authenticate()
        assert(result.isFailure)
    }

    @Test
    fun testErrorHandling_NetworkError() = runTest {
        mockServer.enqueue(MockResponse().setResponseCode(500))
        val result = client.authenticate()
        assert(result.isFailure)
    }

    @Test
    fun testAccountInfo() = runTest {
        // Auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        client.authenticate()

        // Account info
        mockServer.enqueue(MockResponse().setBody("""{"email":"test@example.com","name":"Test User","usage":1073741824,"total":10737418240}"""))
        val result = client.getAccountInfo()
        assert(result.isSuccess)
        assert(result.getOrNull()!!.email == "test@example.com")
    }

    @Test
    fun testLibraryDetails() = runTest {
        // Auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        client.authenticate()

        // Get library
        mockServer.enqueue(MockResponse().setBody("""{"id":"repo-1","name":"Library","desc":"Test","owner":"test@example.com","encrypted":false,"size":2048,"mtime":0,"permission":"rw","type":"repo"}"""))
        val result = client.getLibrary("repo-1")
        assert(result.isSuccess)
        assert(result.getOrNull()!!.size == 2048L)
    }

    @Test
    fun testConnectionTest() = runTest {
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        val result = client.testConnection()
        assert(result.isSuccess)
        assert(result.getOrNull() == true)
    }

    @Test
    fun testAuthenticationState() = runTest {
        assert(!client.isAuthenticated())
        
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        client.authenticate()
        
        assert(client.isAuthenticated())
        
        client.clearAuth()
        assert(!client.isAuthenticated())
    }
}
