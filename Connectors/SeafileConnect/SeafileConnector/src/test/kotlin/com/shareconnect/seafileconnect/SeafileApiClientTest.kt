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

import com.shareconnect.seafileconnect.data.api.SeafileApiClient
import com.shareconnect.seafileconnect.data.models.*
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.Assert.*

/**
 * Unit tests for SeafileApiClient
 */
class SeafileApiClientTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var client: SeafileApiClient
    private val testUsername = "test@example.com"
    private val testPassword = "password123"

    @Before
    fun setup() {
        mockServer = MockWebServer()
        mockServer.start()
        
        val baseUrl = mockServer.url("/").toString()
        client = SeafileApiClient(baseUrl, testUsername, testPassword)
    }

    @After
    fun teardown() {
        mockServer.shutdown()
    }

    @Test
    fun `authenticate returns success with valid credentials`() = runTest {
        // Mock successful authentication response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token-123"}""")
        mockServer.enqueue(mockResponse)

        val result = client.authenticate()

        assertTrue(result.isSuccess)
        assertEquals("test-token-123", result.getOrNull()?.token)
    }

    @Test
    fun `authenticate returns failure with invalid credentials`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(401)
            .setBody("""{"error_msg":"Invalid credentials"}""")
        mockServer.enqueue(mockResponse)

        val result = client.authenticate()

        assertTrue(result.isFailure)
    }

    @Test
    fun `getAccountInfo returns account details`() = runTest {
        // Mock auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        
        // Mock account info
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "email":"test@example.com",
                    "name":"Test User",
                    "usage":1073741824,
                    "total":10737418240
                }
            """.trimIndent())
        mockServer.enqueue(mockResponse)

        val result = client.getAccountInfo()

        assertTrue(result.isSuccess)
        val accountInfo = result.getOrNull()!!
        assertEquals("test@example.com", accountInfo.email)
        assertEquals("Test User", accountInfo.name)
        assertEquals(1073741824L, accountInfo.usage)
    }

    @Test
    fun `listLibraries returns list of libraries`() = runTest {
        // Mock auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        
        // Mock libraries response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                [
                    {
                        "id":"repo-1",
                        "name":"My Library",
                        "desc":"Test library",
                        "owner":"test@example.com",
                        "encrypted":false,
                        "size":1024000,
                        "mtime":1700000000,
                        "permission":"rw",
                        "type":"repo"
                    }
                ]
            """.trimIndent())
        mockServer.enqueue(mockResponse)

        val result = client.listLibraries()

        assertTrue(result.isSuccess)
        val libraries = result.getOrNull()!!
        assertEquals(1, libraries.size)
        assertEquals("repo-1", libraries[0].id)
        assertEquals("My Library", libraries[0].name)
    }

    @Test
    fun `getLibrary returns specific library details`() = runTest {
        // Mock auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        
        // Mock library response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "id":"repo-1",
                    "name":"My Library",
                    "desc":"Test library",
                    "owner":"test@example.com",
                    "encrypted":false,
                    "size":2048000,
                    "mtime":1700000000,
                    "permission":"rw",
                    "type":"repo"
                }
            """.trimIndent())
        mockServer.enqueue(mockResponse)

        val result = client.getLibrary("repo-1")

        assertTrue(result.isSuccess)
        val library = result.getOrNull()!!
        assertEquals("repo-1", library.id)
        assertEquals(2048000L, library.size)
    }

    @Test
    fun `listDirectory returns directory contents`() = runTest {
        // Mock auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        
        // Mock directory listing
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                [
                    {
                        "id":"file-1",
                        "name":"document.pdf",
                        "type":"file",
                        "size":102400,
                        "mtime":1700000000
                    },
                    {
                        "id":"dir-1",
                        "name":"folder",
                        "type":"dir",
                        "size":0,
                        "mtime":1700000000
                    }
                ]
            """.trimIndent())
        mockServer.enqueue(mockResponse)

        val result = client.listDirectory("repo-1", "/")

        assertTrue(result.isSuccess)
        val entries = result.getOrNull()!!
        assertEquals(2, entries.size)
        assertTrue(entries[0].isFile)
        assertTrue(entries[1].isDirectory)
    }

    @Test
    fun `createDirectory succeeds`() = runTest {
        // Mock auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        
        // Mock create directory
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"success":true}""")
        mockServer.enqueue(mockResponse)

        val result = client.createDirectory("repo-1", "/newfolder")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.success)
    }

    @Test
    fun `deleteItem succeeds`() = runTest {
        // Mock auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        
        // Mock delete
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"success":true}""")
        mockServer.enqueue(mockResponse)

        val result = client.deleteItem("repo-1", "/oldfile.txt")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.success)
    }

    @Test
    fun `getDownloadLink returns valid URL`() = runTest {
        // Mock auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        
        // Mock download link
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(""""https://example.com/seafhttp/download/abc123"""")
        mockServer.enqueue(mockResponse)

        val result = client.getDownloadLink("repo-1", "/file.txt")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.startsWith("https://"))
    }

    @Test
    fun `search returns results`() = runTest {
        // Mock auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        
        // Mock search results
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "total":1,
                    "results":[
                        {
                            "name":"result.txt",
                            "oid":"obj-1",
                            "repo_id":"repo-1",
                            "repo_name":"My Library",
                            "is_dir":false,
                            "size":1024,
                            "mtime":1700000000,
                            "fullpath":"/path/to/result.txt"
                        }
                    ]
                }
            """.trimIndent())
        mockServer.enqueue(mockResponse)

        val result = client.search("test query")

        assertTrue(result.isSuccess)
        val searchResponse = result.getOrNull()!!
        assertEquals(1, searchResponse.total)
        assertEquals("result.txt", searchResponse.results[0].name)
    }

    @Test
    fun `decryptLibrary succeeds with correct password`() = runTest {
        // Mock auth
        mockServer.enqueue(MockResponse().setBody("""{"token":"test-token"}"""))
        
        // Mock decrypt
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"success":true}""")
        mockServer.enqueue(mockResponse)

        val result = client.decryptLibrary("repo-1", "library-password")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.success)
    }

    @Test
    fun `testConnection returns true on successful auth`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}""")
        mockServer.enqueue(mockResponse)

        val result = client.testConnection()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
    }

    @Test
    fun `isAuthenticated returns true after successful auth`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}""")
        mockServer.enqueue(mockResponse)

        assertFalse(client.isAuthenticated())
        client.authenticate()
        assertTrue(client.isAuthenticated())
    }

    @Test
    fun `clearAuth removes authentication`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}""")
        mockServer.enqueue(mockResponse)

        client.authenticate()
        assertTrue(client.isAuthenticated())
        
        client.clearAuth()
        assertFalse(client.isAuthenticated())
    }

    @Test
    fun `encrypted library is properly identified`() {
        val library = Library(
            id = "repo-1",
            name = "Encrypted Library",
            description = "Test",
            owner = "test@example.com",
            encrypted = true,
            size = 1024,
            modifiedTime = 1700000000,
            permission = "rw",
            type = "repo"
        )

        assertTrue(library.encrypted)
    }

    @Test
    fun `directory entry correctly identifies files and dirs`() {
        val fileEntry = DirectoryEntry(
            id = "file-1",
            name = "file.txt",
            type = "file",
            size = 1024,
            modifiedTime = 1700000000
        )
        
        val dirEntry = DirectoryEntry(
            id = "dir-1",
            name = "folder",
            type = "dir",
            size = 0,
            modifiedTime = 1700000000
        )

        assertTrue(fileEntry.isFile)
        assertFalse(fileEntry.isDirectory)
        assertTrue(dirEntry.isDirectory)
        assertFalse(dirEntry.isFile)
    }
}
