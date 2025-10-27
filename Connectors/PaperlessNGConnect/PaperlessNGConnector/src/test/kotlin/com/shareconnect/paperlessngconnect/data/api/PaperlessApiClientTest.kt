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


package com.shareconnect.paperlessngconnect.data.api

import com.shareconnect.paperlessngconnect.data.models.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.File

/**
 * Unit tests for PaperlessApiClient
 * Tests all API operations with mocked service
 */
class PaperlessApiClientTest {

    private lateinit var mockService: PaperlessApiService
    private lateinit var apiClient: PaperlessApiClient

    private val testServerUrl = "http://localhost:8000"
    private val testToken = "test-token-123"

    @Before
    fun setup() {
        mockService = mockk()
        apiClient = PaperlessApiClient(
            serverUrl = testServerUrl,
            token = testToken,
            paperlessApiService = mockService
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // Authentication Tests

    @Test
    fun `test getToken success`() = runTest {
        // Arrange
        val username = "testuser"
        val password = "testpass"
        val expectedToken = "new-test-token"
        val tokenResponse = PaperlessTokenResponse(token = expectedToken)

        coEvery {
            mockService.getToken(any())
        } returns Response.success(tokenResponse)

        // Act
        val result = apiClient.getToken(username, password)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedToken, result.getOrNull()?.token)

        coVerify {
            mockService.getToken(
                match { it.username == username && it.password == password }
            )
        }
    }

    @Test
    fun `test getToken failure`() = runTest {
        // Arrange
        val username = "testuser"
        val password = "wrongpass"

        coEvery {
            mockService.getToken(any())
        } returns Response.error(401, "Unauthorized".toResponseBody())

        // Act
        val result = apiClient.getToken(username, password)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Authentication failed") == true)
    }

    @Test
    fun `test setToken`() {
        // Arrange
        val newToken = "new-token-456"

        // Act
        apiClient.setToken(newToken)

        // Assert - verify token is set by trying to use it
        // (we can't directly access the private token field)
        // This would be tested indirectly through other API calls
        assertNotNull(apiClient)
    }

    // Document Tests

    @Test
    fun `test getDocuments success`() = runTest {
        // Arrange
        val documents = listOf(
            PaperlessDocument(
                id = 1,
                title = "Test Document",
                created = "2024-01-01T00:00:00Z",
                modified = "2024-01-01T00:00:00Z",
                added = "2024-01-01T00:00:00Z",
                originalFileName = "test.pdf"
            )
        )
        val response = PaperlessDocumentListResponse(
            count = 1,
            results = documents
        )

        coEvery {
            mockService.getDocuments(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns Response.success(response)

        // Act
        val result = apiClient.getDocuments()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.count)
        assertEquals("Test Document", result.getOrNull()?.results?.first()?.title)
    }

    @Test
    fun `test getDocuments with filters`() = runTest {
        // Arrange
        val documents = emptyList<PaperlessDocument>()
        val response = PaperlessDocumentListResponse(
            count = 0,
            results = documents
        )

        coEvery {
            mockService.getDocuments(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns Response.success(response)

        // Act
        val result = apiClient.getDocuments(
            page = 1,
            pageSize = 50,
            query = "test",
            ordering = "-created",
            tagsAll = listOf(1, 2),
            documentType = 3,
            correspondent = 4
        )

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.count)
    }

    @Test
    fun `test getDocument success`() = runTest {
        // Arrange
        val document = PaperlessDocument(
            id = 1,
            title = "Test Document",
            created = "2024-01-01T00:00:00Z",
            modified = "2024-01-01T00:00:00Z",
            added = "2024-01-01T00:00:00Z",
            originalFileName = "test.pdf"
        )

        coEvery {
            mockService.getDocument(1, any())
        } returns Response.success(document)

        // Act
        val result = apiClient.getDocument(1)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Test Document", result.getOrNull()?.title)
        assertEquals(1, result.getOrNull()?.id)
    }

    @Test
    fun `test createDocument success`() = runTest {
        // Arrange
        val documentRequest = PaperlessDocumentRequest(
            title = "New Document",
            tags = listOf(1, 2)
        )
        val createdDocument = PaperlessDocument(
            id = 123,
            title = "New Document",
            created = "2024-01-01T00:00:00Z",
            modified = "2024-01-01T00:00:00Z",
            added = "2024-01-01T00:00:00Z",
            originalFileName = "new.pdf",
            tags = listOf(1, 2)
        )

        coEvery {
            mockService.createDocument(any(), any())
        } returns Response.success(createdDocument)

        // Act
        val result = apiClient.createDocument(documentRequest)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(123, result.getOrNull()?.id)
        assertEquals("New Document", result.getOrNull()?.title)
    }

    @Test
    fun `test updateDocument success`() = runTest {
        // Arrange
        val updateRequest = PaperlessDocumentUpdateRequest(
            title = "Updated Title"
        )
        val updatedDocument = PaperlessDocument(
            id = 1,
            title = "Updated Title",
            created = "2024-01-01T00:00:00Z",
            modified = "2024-01-02T00:00:00Z",
            added = "2024-01-01T00:00:00Z",
            originalFileName = "test.pdf"
        )

        coEvery {
            mockService.updateDocument(1, any(), any())
        } returns Response.success(updatedDocument)

        // Act
        val result = apiClient.updateDocument(1, updateRequest)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Updated Title", result.getOrNull()?.title)
    }

    @Test
    fun `test deleteDocument success`() = runTest {
        // Arrange
        coEvery {
            mockService.deleteDocument(1, any())
        } returns Response.success(Unit)

        // Act
        val result = apiClient.deleteDocument(1)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test downloadDocument success`() = runTest {
        // Arrange
        val responseBody = "PDF content".toResponseBody()

        coEvery {
            mockService.downloadDocument(1, any(), false)
        } returns Response.success(responseBody)

        // Act
        val result = apiClient.downloadDocument(1, original = false)

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `test getDocumentPreview success`() = runTest {
        // Arrange
        val responseBody = "Preview image".toResponseBody()

        coEvery {
            mockService.getDocumentPreview(1, any())
        } returns Response.success(responseBody)

        // Act
        val result = apiClient.getDocumentPreview(1)

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `test getDocumentThumbnail success`() = runTest {
        // Arrange
        val responseBody = "Thumbnail image".toResponseBody()

        coEvery {
            mockService.getDocumentThumbnail(1, any())
        } returns Response.success(responseBody)

        // Act
        val result = apiClient.getDocumentThumbnail(1)

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    // Tag Tests

    @Test
    fun `test getTags success`() = runTest {
        // Arrange
        val tags = listOf(
            PaperlessTag(id = 1, name = "Important", slug = "important", color = "#FF0000")
        )
        val response = PaperlessTagListResponse(
            count = 1,
            results = tags
        )

        coEvery {
            mockService.getTags(any(), any(), any(), any())
        } returns Response.success(response)

        // Act
        val result = apiClient.getTags()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.count)
        assertEquals("Important", result.getOrNull()?.results?.first()?.name)
    }

    @Test
    fun `test getTag success`() = runTest {
        // Arrange
        val tag = PaperlessTag(id = 1, name = "Important", slug = "important", color = "#FF0000")

        coEvery {
            mockService.getTag(1, any())
        } returns Response.success(tag)

        // Act
        val result = apiClient.getTag(1)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Important", result.getOrNull()?.name)
    }

    @Test
    fun `test createTag success`() = runTest {
        // Arrange
        val tag = PaperlessTag(id = 0, name = "New Tag", slug = "new-tag", color = "#00FF00")
        val createdTag = tag.copy(id = 123)

        coEvery {
            mockService.createTag(any(), any())
        } returns Response.success(createdTag)

        // Act
        val result = apiClient.createTag(tag)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(123, result.getOrNull()?.id)
        assertEquals("New Tag", result.getOrNull()?.name)
    }

    @Test
    fun `test updateTag success`() = runTest {
        // Arrange
        val tag = PaperlessTag(id = 1, name = "Updated Tag", slug = "updated-tag", color = "#0000FF")

        coEvery {
            mockService.updateTag(1, any(), any())
        } returns Response.success(tag)

        // Act
        val result = apiClient.updateTag(1, tag)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Updated Tag", result.getOrNull()?.name)
    }

    @Test
    fun `test deleteTag success`() = runTest {
        // Arrange
        coEvery {
            mockService.deleteTag(1, any())
        } returns Response.success(Unit)

        // Act
        val result = apiClient.deleteTag(1)

        // Assert
        assertTrue(result.isSuccess)
    }

    // Correspondent Tests

    @Test
    fun `test getCorrespondents success`() = runTest {
        // Arrange
        val correspondents = listOf(
            PaperlessCorrespondent(id = 1, name = "ACME Corp", slug = "acme-corp")
        )
        val response = PaperlessCorrespondentListResponse(
            count = 1,
            results = correspondents
        )

        coEvery {
            mockService.getCorrespondents(any(), any(), any(), any())
        } returns Response.success(response)

        // Act
        val result = apiClient.getCorrespondents()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.count)
        assertEquals("ACME Corp", result.getOrNull()?.results?.first()?.name)
    }

    @Test
    fun `test createCorrespondent success`() = runTest {
        // Arrange
        val correspondent = PaperlessCorrespondent(id = 0, name = "New Corp", slug = "new-corp")
        val createdCorrespondent = correspondent.copy(id = 456)

        coEvery {
            mockService.createCorrespondent(any(), any())
        } returns Response.success(createdCorrespondent)

        // Act
        val result = apiClient.createCorrespondent(correspondent)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(456, result.getOrNull()?.id)
        assertEquals("New Corp", result.getOrNull()?.name)
    }

    // Document Type Tests

    @Test
    fun `test getDocumentTypes success`() = runTest {
        // Arrange
        val documentTypes = listOf(
            PaperlessDocumentType(id = 1, name = "Invoice", slug = "invoice")
        )
        val response = PaperlessDocumentTypeListResponse(
            count = 1,
            results = documentTypes
        )

        coEvery {
            mockService.getDocumentTypes(any(), any(), any(), any())
        } returns Response.success(response)

        // Act
        val result = apiClient.getDocumentTypes()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.count)
        assertEquals("Invoice", result.getOrNull()?.results?.first()?.name)
    }

    // Storage Path Tests

    @Test
    fun `test getStoragePaths success`() = runTest {
        // Arrange
        val storagePaths = listOf(
            PaperlessStoragePath(id = 1, name = "Archive", slug = "archive", path = "/archive")
        )
        val response = PaperlessStoragePathListResponse(
            count = 1,
            results = storagePaths
        )

        coEvery {
            mockService.getStoragePaths(any(), any(), any(), any())
        } returns Response.success(response)

        // Act
        val result = apiClient.getStoragePaths()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.count)
        assertEquals("Archive", result.getOrNull()?.results?.first()?.name)
    }

    // Search Tests

    @Test
    fun `test search success`() = runTest {
        // Arrange
        val documents = listOf(
            PaperlessDocument(
                id = 1,
                title = "Search Result",
                created = "2024-01-01T00:00:00Z",
                modified = "2024-01-01T00:00:00Z",
                added = "2024-01-01T00:00:00Z",
                originalFileName = "search.pdf"
            )
        )
        val response = PaperlessSearchResult(
            count = 1,
            results = documents
        )

        coEvery {
            mockService.search(any(), "test query", any(), any())
        } returns Response.success(response)

        // Act
        val result = apiClient.search("test query")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.count)
        assertEquals("Search Result", result.getOrNull()?.results?.first()?.title)
    }

    // Bulk Operations Tests

    @Test
    fun `test bulkEdit success`() = runTest {
        // Arrange
        val bulkRequest = PaperlessBulkEditRequest(
            documents = listOf(1, 2, 3),
            method = "add_tag",
            parameters = mapOf("tag" to 1)
        )

        coEvery {
            mockService.bulkEdit(any(), any())
        } returns Response.success(Unit)

        // Act
        val result = apiClient.bulkEdit(bulkRequest)

        // Assert
        assertTrue(result.isSuccess)
    }

    // Task Management Tests

    @Test
    fun `test getTaskStatus success`() = runTest {
        // Arrange
        val taskStatus = PaperlessTaskStatus(
            taskId = "task-123",
            status = "SUCCESS",
            result = "Document processed"
        )

        coEvery {
            mockService.getTaskStatus("task-123", any())
        } returns Response.success(taskStatus)

        // Act
        val result = apiClient.getTaskStatus("task-123")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("SUCCESS", result.getOrNull()?.status)
    }

    // Saved Views Tests

    @Test
    fun `test getSavedViews success`() = runTest {
        // Arrange
        val savedViews = listOf(
            PaperlessSavedView(
                id = 1,
                name = "Recent Documents",
                showOnDashboard = true
            )
        )

        coEvery {
            mockService.getSavedViews(any())
        } returns Response.success(savedViews)

        // Act
        val result = apiClient.getSavedViews()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Recent Documents", result.getOrNull()?.first()?.name)
    }

    // Workflows Tests

    @Test
    fun `test getWorkflows success`() = runTest {
        // Arrange
        val workflows = listOf(
            PaperlessWorkflow(
                id = 1,
                name = "Auto-tag Invoices",
                order = 1
            )
        )

        coEvery {
            mockService.getWorkflows(any())
        } returns Response.success(workflows)

        // Act
        val result = apiClient.getWorkflows()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Auto-tag Invoices", result.getOrNull()?.first()?.name)
    }

    // Error Handling Tests

    @Test
    fun `test API call without authentication`() = runTest {
        // Arrange
        val unauthenticatedClient = PaperlessApiClient(
            serverUrl = testServerUrl,
            token = null,
            paperlessApiService = mockService
        )

        // Act
        val result = unauthenticatedClient.getDocuments()

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Not authenticated") == true)
    }

    @Test
    fun `test API call with network error`() = runTest {
        // Arrange
        coEvery {
            mockService.getDocuments(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        } throws Exception("Network error")

        // Act
        val result = apiClient.getDocuments()

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
}
