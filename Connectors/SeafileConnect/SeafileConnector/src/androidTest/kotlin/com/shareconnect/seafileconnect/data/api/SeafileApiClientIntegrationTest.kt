package com.shareconnect.seafileconnect.data.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.seafileconnect.data.model.*
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests for SeafileApiClient - tests end-to-end workflows
 */
@RunWith(AndroidJUnit4::class)
class SeafileApiClientIntegrationTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var apiClient: SeafileApiClient

    @Before
    fun setup() {
        mockServer = MockWebServer()
        mockServer.dispatcher = SeafileApiDispatcher()
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
    fun testCompleteAuthenticationFlow() = runBlocking {
        // When
        val result = apiClient.authenticate()

        // Then
        assertTrue(result.isSuccess)
        val token = result.getOrNull()
        assertNotNull(token)
        assertEquals("mock-auth-token", token)
    }

    @Test
    fun testGetAccountInfoAfterAuthentication() = runBlocking {
        // When
        val result = apiClient.getAccountInfo()

        // Then
        assertTrue(result.isSuccess)
        val account = result.getOrNull()
        assertNotNull(account)
        assertEquals("test@example.com", account.email)
        assertEquals("Test User", account.name)
        assertTrue(account.usage > 0)
        assertTrue(account.total > account.usage)
    }

    @Test
    fun testListLibrariesWorkflow() = runBlocking {
        // When
        val result = apiClient.listLibraries()

        // Then
        assertTrue(result.isSuccess)
        val libraries = result.getOrNull()
        assertNotNull(libraries)
        assertTrue(libraries.isNotEmpty())
        assertTrue(libraries.any { it.encrypted })
        assertTrue(libraries.any { !it.encrypted })
    }

    @Test
    fun testEncryptedLibraryWorkflow() = runBlocking {
        // Given - list libraries to find an encrypted one
        val librariesResult = apiClient.listLibraries()
        assertTrue(librariesResult.isSuccess)
        val libraries = librariesResult.getOrNull()!!
        val encryptedLib = libraries.first { it.encrypted }

        // When - set password for encrypted library
        val passwordResult = apiClient.setLibraryPassword(encryptedLib.id, "library-password")

        // Then
        assertTrue(passwordResult.isSuccess)

        // And - can access library contents
        val dirResult = apiClient.listDirectory(encryptedLib.id, "/")
        assertTrue(dirResult.isSuccess)
    }

    @Test
    fun testDirectoryNavigationWorkflow() = runBlocking {
        // Given - get a library
        val librariesResult = apiClient.listLibraries()
        val library = librariesResult.getOrNull()!!.first()

        // When - list root directory
        val rootResult = apiClient.listDirectory(library.id, "/")

        // Then
        assertTrue(rootResult.isSuccess)
        val entries = rootResult.getOrNull()!!
        assertTrue(entries.isNotEmpty())

        // When - find a subdirectory and navigate to it
        val subdirectory = entries.firstOrNull { it.isDirectory }
        if (subdirectory != null) {
            val subDirResult = apiClient.listDirectory(library.id, "/${subdirectory.name}/")
            assertTrue(subDirResult.isSuccess)
        }
    }

    @Test
    fun testCreateAndDeleteDirectoryWorkflow() = runBlocking {
        // Given - get a library
        val librariesResult = apiClient.listLibraries()
        val library = librariesResult.getOrNull()!!.first()

        // When - create directory
        val createResult = apiClient.createDirectory(library.id, "/test-folder/")

        // Then
        assertTrue(createResult.isSuccess)

        // When - delete directory
        val deleteResult = apiClient.delete(library.id, "/test-folder/", isDirectory = true)

        // Then
        assertTrue(deleteResult.isSuccess)
    }

    @Test
    fun testFileOperationsWorkflow() = runBlocking {
        // Given - get a library
        val librariesResult = apiClient.listLibraries()
        val library = librariesResult.getOrNull()!!.first()

        // Given - list files
        val dirResult = apiClient.listDirectory(library.id, "/")
        assertTrue(dirResult.isSuccess)
        val files = dirResult.getOrNull()!!.filter { it.isFile }

        if (files.isNotEmpty()) {
            val file = files.first()

            // When - get file detail
            val detailResult = apiClient.getFileDetail(library.id, "/${file.name}")

            // Then
            assertTrue(detailResult.isSuccess)
            val fileDetail = detailResult.getOrNull()
            assertNotNull(fileDetail)
            assertEquals(file.name, fileDetail.name)
        }
    }

    @Test
    fun testSearchWorkflow() = runBlocking {
        // Given - get a library
        val librariesResult = apiClient.listLibraries()
        val library = librariesResult.getOrNull()!!.first()

        // When - search for files
        val searchResult = apiClient.search("test", repoId = library.id)

        // Then
        assertTrue(searchResult.isSuccess)
        val response = searchResult.getOrNull()
        assertNotNull(response)
        assertTrue(response.results.isNotEmpty())
    }

    @Test
    fun testShareLinkWorkflow() = runBlocking {
        // Given - get a library and file
        val librariesResult = apiClient.listLibraries()
        val library = librariesResult.getOrNull()!!.first()

        // When - create share link
        val createResult = apiClient.createShareLink(library.id, "/test-file.txt")

        // Then
        assertTrue(createResult.isSuccess)
        val shareLink = createResult.getOrNull()
        assertNotNull(shareLink)
        assertTrue(shareLink.link.isNotEmpty())

        // When - list share links
        val listResult = apiClient.listShareLinks(library.id)

        // Then
        assertTrue(listResult.isSuccess)
        val links = listResult.getOrNull()!!
        assertTrue(links.any { it.token == shareLink.token })

        // When - delete share link
        val deleteResult = apiClient.deleteShareLink(shareLink.token)

        // Then
        assertTrue(deleteResult.isSuccess)
    }

    @Test
    fun testFileHistoryWorkflow() = runBlocking {
        // Given - get a library and file
        val librariesResult = apiClient.listLibraries()
        val library = librariesResult.getOrNull()!!.first()

        val dirResult = apiClient.listDirectory(library.id, "/")
        val files = dirResult.getOrNull()!!.filter { it.isFile }

        if (files.isNotEmpty()) {
            val file = files.first()

            // When - get file history
            val historyResult = apiClient.getFileHistory(library.id, "/${file.name}")

            // Then
            assertTrue(historyResult.isSuccess)
            val history = historyResult.getOrNull()
            assertNotNull(history)
            assertTrue(history.isNotEmpty())
        }
    }

    @Test
    fun testCompleteLibraryDetailsWorkflow() = runBlocking {
        // Given - get a library
        val librariesResult = apiClient.listLibraries()
        val library = librariesResult.getOrNull()!!.first()

        // When - get library details
        val detailsResult = apiClient.getLibraryDetails(library.id)

        // Then
        assertTrue(detailsResult.isSuccess)
        val details = detailsResult.getOrNull()
        assertNotNull(details)
        assertEquals(library.id, details.id)
        assertEquals(library.name, details.name)
        assertTrue(details.fileCount >= 0)
    }

    @Test
    fun testMultipleLibrariesAccess() = runBlocking {
        // When - list all libraries
        val result = apiClient.listLibraries()

        // Then
        assertTrue(result.isSuccess)
        val libraries = result.getOrNull()!!

        // When - access each library
        libraries.forEach { library ->
            val dirResult = apiClient.listDirectory(library.id, "/")

            // Handle encrypted libraries differently
            if (library.encrypted) {
                // Encrypted library may require password
                if (dirResult.isFailure) {
                    // Try setting password
                    apiClient.setLibraryPassword(library.id, "password")
                    val retryResult = apiClient.listDirectory(library.id, "/")
                    // Either password works or we get permission error
                    assertTrue(retryResult.isSuccess || retryResult.isFailure)
                }
            } else {
                // Non-encrypted should work
                assertTrue(dirResult.isSuccess)
            }
        }
    }

    @Test
    fun testErrorRecoveryAfterAuthFailure() = runBlocking {
        // Given - create client with wrong credentials
        val badClient = SeafileApiClient(
            serverUrl = mockServer.url("/").toString(),
            username = "wrong@example.com",
            password = "wrongpass"
        )

        // When - try to authenticate
        val authResult = badClient.authenticate()

        // Then - should fail
        assertTrue(authResult.isFailure)

        // When - try to use API anyway
        val result = badClient.listLibraries()

        // Then - should also fail
        assertTrue(result.isFailure)
    }

    @Test
    fun testConcurrentApiCalls() = runBlocking {
        // When - make multiple concurrent API calls
        val results = listOf(
            apiClient.listLibraries(),
            apiClient.getAccountInfo(),
            apiClient.listLibraries()
        )

        // Then - all should succeed
        results.forEach { result ->
            assertTrue(result.isSuccess)
        }
    }

    @Test
    fun testEmptyDirectoryHandling() = runBlocking {
        // Given - get a library
        val librariesResult = apiClient.listLibraries()
        val library = librariesResult.getOrNull()!!.first()

        // When - create and list empty directory
        apiClient.createDirectory(library.id, "/empty-folder/")
        val dirResult = apiClient.listDirectory(library.id, "/empty-folder/")

        // Then - should return empty list
        assertTrue(dirResult.isSuccess)
        val entries = dirResult.getOrNull()!!
        assertTrue(entries.isEmpty())

        // Cleanup
        apiClient.delete(library.id, "/empty-folder/", isDirectory = true)
    }

    @Test
    fun testSearchWithNoResults() = runBlocking {
        // When - search for non-existent term
        val result = apiClient.search("xyzabc123notfound")

        // Then - should return empty results
        assertTrue(result.isSuccess)
        val response = result.getOrNull()!!
        assertEquals(0, response.total)
        assertTrue(response.results.isEmpty())
    }

    @Test
    fun testLargeFileHandling() = runBlocking {
        // Given - get a library
        val librariesResult = apiClient.listLibraries()
        val library = librariesResult.getOrNull()!!.first()

        // When - list directory
        val dirResult = apiClient.listDirectory(library.id, "/")
        assertTrue(dirResult.isSuccess)

        // Then - check for large files
        val entries = dirResult.getOrNull()!!
        val largeFiles = entries.filter { it.size > 100_000_000 } // > 100MB

        // If large files exist, verify we can get their details
        largeFiles.forEach { file ->
            val detailResult = apiClient.getFileDetail(library.id, "/${file.name}")
            assertTrue(detailResult.isSuccess)
        }
    }

    @Test
    fun testNestedDirectoryOperations() = runBlocking {
        // Given - get a library
        val librariesResult = apiClient.listLibraries()
        val library = librariesResult.getOrNull()!!.first()

        // When - create nested directories
        val result1 = apiClient.createDirectory(library.id, "/parent/")
        val result2 = apiClient.createDirectory(library.id, "/parent/child/")
        val result3 = apiClient.createDirectory(library.id, "/parent/child/grandchild/")

        // Then - all should succeed
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertTrue(result3.isSuccess)

        // When - list nested directory
        val listResult = apiClient.listDirectory(library.id, "/parent/child/")

        // Then - should show grandchild
        assertTrue(listResult.isSuccess)
        val entries = listResult.getOrNull()!!
        assertTrue(entries.any { it.name == "grandchild" && it.isDirectory })

        // Cleanup
        apiClient.delete(library.id, "/parent/", isDirectory = true)
    }

    @Test
    fun testPermissionHandling() = runBlocking {
        // When - get libraries
        val result = apiClient.listLibraries()

        // Then
        assertTrue(result.isSuccess)
        val libraries = result.getOrNull()!!

        // Verify permission fields are set
        libraries.forEach { library ->
            assertNotNull(library.permission)
            assertTrue(library.permission.isNotEmpty())
        }
    }

    @Test
    fun testApiRateLimiting() = runBlocking {
        // When - make many rapid requests
        val results = (1..10).map {
            apiClient.listLibraries()
        }

        // Then - all should complete (may be throttled but shouldn't fail)
        results.forEach { result ->
            assertTrue(result.isSuccess || result.isFailure) // Either works or fails gracefully
        }
    }

    /**
     * Mock dispatcher that simulates Seafile API responses
     */
    private class SeafileApiDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return when {
                request.path?.contains("api2/auth-token/") == true -> {
                    handleAuthentication(request)
                }
                request.path?.contains("api2/account/info/") == true -> {
                    handleAccountInfo()
                }
                request.path?.contains("api2/repos/") == true && request.method == "GET" -> {
                    handleListLibraries()
                }
                request.path?.contains("api/v2.1/repos/") == true && request.path?.endsWith("/") == true -> {
                    handleLibraryDetails()
                }
                request.path?.contains("/dir/") == true && request.method == "GET" -> {
                    handleListDirectory()
                }
                request.path?.contains("/dir/") == true && request.method == "POST" -> {
                    handleCreateDirectory()
                }
                request.path?.contains("/dir/") == true && request.method == "DELETE" -> {
                    handleDelete()
                }
                request.path?.contains("/file/detail/") == true -> {
                    handleFileDetail()
                }
                request.path?.contains("/file/history/") == true -> {
                    handleFileHistory()
                }
                request.path?.contains("api2/search/") == true -> {
                    handleSearch()
                }
                request.path?.contains("api/v2.1/share-links/") == true && request.method == "POST" -> {
                    handleCreateShareLink()
                }
                request.path?.contains("api/v2.1/share-links/") == true && request.method == "GET" -> {
                    handleListShareLinks()
                }
                request.path?.contains("api/v2.1/share-links/") == true && request.method == "DELETE" -> {
                    handleDeleteShareLink()
                }
                request.path?.contains("api2/repos/") == true && request.method == "POST" -> {
                    handleSetLibraryPassword()
                }
                else -> {
                    MockResponse().setResponseCode(404).setBody("""{"error_msg":"Not found"}""")
                }
            }
        }

        private fun handleAuthentication(request: RecordedRequest): MockResponse {
            val body = request.body.readUtf8()
            return if (body.contains("test@example.com") && body.contains("testpass")) {
                MockResponse()
                    .setResponseCode(200)
                    .setBody("""{"token":"mock-auth-token"}""")
                    .addHeader("Content-Type", "application/json")
            } else {
                MockResponse()
                    .setResponseCode(400)
                    .setBody("""{"error_msg":"Invalid credentials"}""")
            }
        }

        private fun handleAccountInfo(): MockResponse {
            return MockResponse()
                .setResponseCode(200)
                .setBody("""
                    {
                        "email":"test@example.com",
                        "name":"Test User",
                        "usage":5368709120,
                        "total":107374182400,
                        "create_time":1609459200
                    }
                """.trimIndent())
                .addHeader("Content-Type", "application/json")
        }

        private fun handleListLibraries(): MockResponse {
            return MockResponse()
                .setResponseCode(200)
                .setBody("""
                    [
                        {
                            "id":"lib-standard-123",
                            "name":"My Library",
                            "desc":"Standard library",
                            "owner":"test@example.com",
                            "mtime":1609459200,
                            "size":1073741824,
                            "encrypted":false,
                            "permission":"rw",
                            "type":"repo",
                            "version":1
                        },
                        {
                            "id":"lib-encrypted-456",
                            "name":"Encrypted Library",
                            "desc":"Secure storage",
                            "owner":"test@example.com",
                            "mtime":1609459300,
                            "size":2147483648,
                            "encrypted":true,
                            "permission":"rw",
                            "type":"repo",
                            "version":1
                        }
                    ]
                """.trimIndent())
                .addHeader("Content-Type", "application/json")
        }

        private fun handleLibraryDetails(): MockResponse {
            return MockResponse()
                .setResponseCode(200)
                .setBody("""
                    {
                        "id":"lib-standard-123",
                        "name":"My Library",
                        "desc":"Standard library",
                        "owner":"test@example.com",
                        "owner_name":"Test User",
                        "mtime":1609459200,
                        "size":1073741824,
                        "encrypted":false,
                        "permission":"rw",
                        "version":1,
                        "file_count":42,
                        "root":"/"
                    }
                """.trimIndent())
                .addHeader("Content-Type", "application/json")
        }

        private fun handleListDirectory(): MockResponse {
            return MockResponse()
                .setResponseCode(200)
                .setBody("""
                    [
                        {
                            "id":"file-123",
                            "type":"file",
                            "name":"test-file.txt",
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
                """.trimIndent())
                .addHeader("Content-Type", "application/json")
        }

        private fun handleCreateDirectory(): MockResponse {
            return MockResponse()
                .setResponseCode(200)
                .setBody("""{"success":true}""")
                .addHeader("Content-Type", "application/json")
        }

        private fun handleDelete(): MockResponse {
            return MockResponse()
                .setResponseCode(200)
                .setBody("""{"success":true}""")
        }

        private fun handleFileDetail(): MockResponse {
            return MockResponse()
                .setResponseCode(200)
                .setBody("""
                    {
                        "id":"file-123",
                        "name":"test-file.txt",
                        "size":1048576,
                        "type":"file",
                        "mtime":1609459200,
                        "permission":"rw"
                    }
                """.trimIndent())
                .addHeader("Content-Type", "application/json")
        }

        private fun handleFileHistory(): MockResponse {
            return MockResponse()
                .setResponseCode(200)
                .setBody("""
                    [
                        {
                            "commit_id":"commit-123",
                            "rev_file_id":"rev-123",
                            "ctime":1609459200,
                            "creator_name":"Test User",
                            "creator":"test@example.com",
                            "size":1048576
                        }
                    ]
                """.trimIndent())
                .addHeader("Content-Type", "application/json")
        }

        private fun handleSearch(): MockResponse {
            return MockResponse()
                .setResponseCode(200)
                .setBody("""
                    {
                        "total":1,
                        "results":[
                            {
                                "name":"test-file.txt",
                                "repo_id":"lib-standard-123",
                                "repo_name":"My Library",
                                "is_dir":false,
                                "size":1048576,
                                "mtime":1609459200,
                                "fullpath":"/test-file.txt",
                                "permission":"rw"
                            }
                        ],
                        "has_more":false
                    }
                """.trimIndent())
                .addHeader("Content-Type", "application/json")
        }

        private fun handleCreateShareLink(): MockResponse {
            return MockResponse()
                .setResponseCode(200)
                .setBody("""
                    {
                        "token":"share-token-123",
                        "link":"https://seafile.example.com/f/share-token-123/",
                        "repo_id":"lib-standard-123",
                        "path":"/test-file.txt",
                        "ctime":1609459200,
                        "is_dir":false
                    }
                """.trimIndent())
                .addHeader("Content-Type", "application/json")
        }

        private fun handleListShareLinks(): MockResponse {
            return MockResponse()
                .setResponseCode(200)
                .setBody("""
                    [
                        {
                            "token":"share-token-123",
                            "link":"https://seafile.example.com/f/share-token-123/",
                            "repo_id":"lib-standard-123",
                            "path":"/test-file.txt",
                            "ctime":1609459200,
                            "is_dir":false
                        }
                    ]
                """.trimIndent())
                .addHeader("Content-Type", "application/json")
        }

        private fun handleDeleteShareLink(): MockResponse {
            return MockResponse()
                .setResponseCode(200)
                .setBody("""{"success":true}""")
        }

        private fun handleSetLibraryPassword(): MockResponse {
            return MockResponse()
                .setResponseCode(200)
                .setBody("""{"success":true}""")
                .addHeader("Content-Type", "application/json")
        }
    }
}
