package com.shareconnect.seafileconnect.data.api

import com.shareconnect.seafileconnect.data.model.*
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for SeafileApiClient using MockWebServer
 */
class SeafileApiClientTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var apiClient: SeafileApiClient

    @Before
    fun setup() {
        mockServer = MockWebServer()
        mockServer.start()

        val serverUrl = mockServer.url("/").toString()
        apiClient = SeafileApiClient(
            serverUrl = serverUrl,
            username = "test@example.com",
            password = "testpass"
        )
    }

    @After
    fun teardown() {
        mockServer.shutdown()
    }

    @Test
    fun `test authenticate success`() = runBlocking {
        // Given
        val authResponse = """{"token":"test-auth-token-12345"}"""
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(authResponse)
            .addHeader("Content-Type", "application/json"))

        // When
        val result = apiClient.authenticate()

        // Then
        assertTrue(result.isSuccess)
        assertEquals("test-auth-token-12345", result.getOrNull())

        // Verify request
        val request = mockServer.takeRequest()
        assertEquals("POST", request.method)
        assertTrue(request.path?.contains("api2/auth-token/") == true)
    }

    @Test
    fun `test authenticate failure with invalid credentials`() = runBlocking {
        // Given
        mockServer.enqueue(MockResponse()
            .setResponseCode(400)
            .setBody("""{"error_msg":"Invalid credentials"}""")
            .addHeader("Content-Type", "application/json"))

        // When
        val result = apiClient.authenticate()

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Authentication failed") == true)
    }

    @Test
    fun `test getAccountInfo success`() = runBlocking {
        // Given - mock authentication
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}""")
            .addHeader("Content-Type", "application/json"))

        // Mock account info
        val accountJson = """
            {
                "email":"user@example.com",
                "name":"Test User",
                "usage":1073741824,
                "total":10737418240,
                "create_time":1609459200
            }
        """
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(accountJson)
            .addHeader("Content-Type", "application/json"))

        // When
        val result = apiClient.getAccountInfo()

        // Then
        assertTrue(result.isSuccess)
        val account = result.getOrNull()
        assertNotNull(account)
        assertEquals("user@example.com", account.email)
        assertEquals("Test User", account.name)
        assertEquals(1073741824L, account.usage)
        assertEquals(10737418240L, account.total)
    }

    @Test
    fun `test listLibraries success`() = runBlocking {
        // Given - mock authentication
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}"""))

        // Mock libraries list
        val librariesJson = """
            [
                {
                    "id":"lib-123",
                    "name":"My Library",
                    "desc":"Test library",
                    "owner":"user@example.com",
                    "mtime":1609459200,
                    "size":1073741824,
                    "encrypted":false,
                    "permission":"rw",
                    "type":"repo",
                    "version":1
                },
                {
                    "id":"lib-456",
                    "name":"Encrypted Library",
                    "desc":"Secure storage",
                    "owner":"user@example.com",
                    "mtime":1609459300,
                    "size":2147483648,
                    "encrypted":true,
                    "permission":"rw",
                    "type":"repo",
                    "version":1
                }
            ]
        """
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(librariesJson)
            .addHeader("Content-Type", "application/json"))

        // When
        val result = apiClient.listLibraries()

        // Then
        assertTrue(result.isSuccess)
        val libraries = result.getOrNull()
        assertNotNull(libraries)
        assertEquals(2, libraries.size)
        assertEquals("My Library", libraries[0].name)
        assertEquals(false, libraries[0].encrypted)
        assertEquals("Encrypted Library", libraries[1].name)
        assertEquals(true, libraries[1].encrypted)
    }

    @Test
    fun `test getLibraryDetails success`() = runBlocking {
        // Given - mock authentication
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}"""))

        // Mock library details
        val detailsJson = """
            {
                "id":"lib-123",
                "name":"My Library",
                "desc":"Test library",
                "owner":"user@example.com",
                "owner_name":"Test User",
                "mtime":1609459200,
                "size":1073741824,
                "encrypted":false,
                "permission":"rw",
                "version":1,
                "file_count":42,
                "root":"/"
            }
        """
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(detailsJson)
            .addHeader("Content-Type", "application/json"))

        // When
        val result = apiClient.getLibraryDetails("lib-123")

        // Then
        assertTrue(result.isSuccess)
        val details = result.getOrNull()
        assertNotNull(details)
        assertEquals("lib-123", details.id)
        assertEquals("My Library", details.name)
        assertEquals(42, details.fileCount)
        assertEquals("/", details.root)
    }

    @Test
    fun `test listDirectory success`() = runBlocking {
        // Given - mock authentication
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}"""))

        // Mock directory listing
        val dirJson = """
            [
                {
                    "id":"file-123",
                    "type":"file",
                    "name":"document.pdf",
                    "size":1048576,
                    "mtime":1609459200,
                    "permission":"rw"
                },
                {
                    "id":"dir-456",
                    "type":"dir",
                    "name":"subfolder",
                    "size":0,
                    "mtime":1609459300,
                    "permission":"rw"
                }
            ]
        """
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(dirJson)
            .addHeader("Content-Type", "application/json"))

        // When
        val result = apiClient.listDirectory("lib-123", "/")

        // Then
        assertTrue(result.isSuccess)
        val entries = result.getOrNull()
        assertNotNull(entries)
        assertEquals(2, entries.size)
        assertEquals("document.pdf", entries[0].name)
        assertTrue(entries[0].isFile)
        assertEquals("subfolder", entries[1].name)
        assertTrue(entries[1].isDirectory)
    }

    @Test
    fun `test setLibraryPassword success`() = runBlocking {
        // Given - mock authentication
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}"""))

        // Mock password set response
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"success":true}""")
            .addHeader("Content-Type", "application/json"))

        // When
        val result = apiClient.setLibraryPassword("lib-456", "password123")

        // Then
        assertTrue(result.isSuccess)

        // Verify request
        val request = mockServer.takeRequest() // auth request
        mockServer.takeRequest() // password request
    }

    @Test
    fun `test createDirectory success`() = runBlocking {
        // Given - mock authentication
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}"""))

        // Mock directory creation
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"success":true}""")
            .addHeader("Content-Type", "application/json"))

        // When
        val result = apiClient.createDirectory("lib-123", "/new-folder/")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test deleteFile success`() = runBlocking {
        // Given - mock authentication
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}"""))

        // Mock delete response
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"success":true}"""))

        // When
        val result = apiClient.delete("lib-123", "/document.pdf", isDirectory = false)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test deleteDirectory success`() = runBlocking {
        // Given - mock authentication
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}"""))

        // Mock delete response
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"success":true}"""))

        // When
        val result = apiClient.delete("lib-123", "/folder/", isDirectory = true)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test search success`() = runBlocking {
        // Given - mock authentication
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}"""))

        // Mock search response
        val searchJson = """
            {
                "total":2,
                "results":[
                    {
                        "name":"report.pdf",
                        "repo_id":"lib-123",
                        "repo_name":"My Library",
                        "is_dir":false,
                        "size":2048576,
                        "mtime":1609459200,
                        "fullpath":"/documents/report.pdf",
                        "permission":"rw"
                    },
                    {
                        "name":"report2.pdf",
                        "repo_id":"lib-123",
                        "repo_name":"My Library",
                        "is_dir":false,
                        "size":1048576,
                        "mtime":1609459300,
                        "fullpath":"/archive/report2.pdf",
                        "permission":"r"
                    }
                ],
                "has_more":false
            }
        """
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(searchJson)
            .addHeader("Content-Type", "application/json"))

        // When
        val result = apiClient.search("report", repoId = "lib-123")

        // Then
        assertTrue(result.isSuccess)
        val searchResponse = result.getOrNull()
        assertNotNull(searchResponse)
        assertEquals(2, searchResponse.total)
        assertEquals(2, searchResponse.results.size)
        assertEquals("report.pdf", searchResponse.results[0].name)
        assertEquals("/documents/report.pdf", searchResponse.results[0].fullPath)
    }

    @Test
    fun `test createShareLink success`() = runBlocking {
        // Given - mock authentication
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}"""))

        // Mock share link creation
        val shareLinkJson = """
            {
                "token":"share-token-123",
                "link":"https://seafile.example.com/f/share-token-123/",
                "repo_id":"lib-123",
                "path":"/document.pdf",
                "ctime":1609459200,
                "is_dir":false
            }
        """
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(shareLinkJson)
            .addHeader("Content-Type", "application/json"))

        // When
        val result = apiClient.createShareLink("lib-123", "/document.pdf")

        // Then
        assertTrue(result.isSuccess)
        val shareLink = result.getOrNull()
        assertNotNull(shareLink)
        assertEquals("share-token-123", shareLink.token)
        assertTrue(shareLink.link.contains("share-token-123"))
        assertEquals("/document.pdf", shareLink.path)
    }

    @Test
    fun `test listShareLinks success`() = runBlocking {
        // Given - mock authentication
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}"""))

        // Mock share links list
        val shareLinksJson = """
            [
                {
                    "token":"share-1",
                    "link":"https://seafile.example.com/f/share-1/",
                    "repo_id":"lib-123",
                    "path":"/doc1.pdf",
                    "ctime":1609459200,
                    "is_dir":false
                },
                {
                    "token":"share-2",
                    "link":"https://seafile.example.com/f/share-2/",
                    "repo_id":"lib-123",
                    "path":"/folder/",
                    "ctime":1609459300,
                    "is_dir":true
                }
            ]
        """
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(shareLinksJson)
            .addHeader("Content-Type", "application/json"))

        // When
        val result = apiClient.listShareLinks("lib-123")

        // Then
        assertTrue(result.isSuccess)
        val shareLinks = result.getOrNull()
        assertNotNull(shareLinks)
        assertEquals(2, shareLinks.size)
        assertEquals("share-1", shareLinks[0].token)
        assertEquals(false, shareLinks[0].isDirectory)
        assertEquals("share-2", shareLinks[1].token)
        assertEquals(true, shareLinks[1].isDirectory)
    }

    @Test
    fun `test deleteShareLink success`() = runBlocking {
        // Given - mock authentication
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}"""))

        // Mock delete share link
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"success":true}"""))

        // When
        val result = apiClient.deleteShareLink("share-token-123")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test getFileHistory success`() = runBlocking {
        // Given - mock authentication
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}"""))

        // Mock file history
        val historyJson = """
            [
                {
                    "commit_id":"commit-123",
                    "rev_file_id":"rev-123",
                    "ctime":1609459200,
                    "creator_name":"Test User",
                    "creator":"user@example.com",
                    "size":1048576
                },
                {
                    "commit_id":"commit-456",
                    "rev_file_id":"rev-456",
                    "ctime":1609459100,
                    "creator_name":"Test User",
                    "creator":"user@example.com",
                    "size":1048500
                }
            ]
        """
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(historyJson)
            .addHeader("Content-Type", "application/json"))

        // When
        val result = apiClient.getFileHistory("lib-123", "/document.pdf")

        // Then
        assertTrue(result.isSuccess)
        val history = result.getOrNull()
        assertNotNull(history)
        assertEquals(2, history.size)
        assertEquals("commit-123", history[0].commitId)
        assertEquals("Test User", history[0].creatorName)
        assertEquals(1048576L, history[0].size)
    }

    @Test
    fun `test error handling for network failure`() = runBlocking {
        // Given - mock authentication
        mockServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"token":"test-token"}"""))

        // Mock network error
        mockServer.enqueue(MockResponse()
            .setResponseCode(500)
            .setBody("""{"error_msg":"Internal server error"}"""))

        // When
        val result = apiClient.listLibraries()

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("List libraries failed") == true)
    }
}
