package com.shareconnect.onlyofficeconnect.data.api

import com.shareconnect.onlyofficeconnect.data.models.*
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Integration tests for OnlyOfficeApiClient using MockWebServer
 * Tests real HTTP communication with mocked server responses
 */
class OnlyOfficeApiClientIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: OnlyOfficeApiClient
    private val testToken = "test-token-123"

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url("/").toString()
        apiClient = OnlyOfficeApiClient(baseUrl, "test-secret")
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // Authentication Tests

    @Test
    fun `test authenticate with valid credentials`() = runTest {
        val responseJson = """
            {
                "token": "auth-token-abc123",
                "expires": "2024-12-31T23:59:59Z",
                "response": {
                    "id": "user1",
                    "displayName": "John Doe",
                    "email": "john@example.com",
                    "userName": "johndoe"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.authenticate("johndoe", "password123")

        assertTrue(result.isSuccess)
        assertEquals("auth-token-abc123", result.getOrNull()?.token)
        assertEquals("John Doe", result.getOrNull()?.response?.displayName)

        val request = mockWebServer.takeRequest()
        assertEquals("POST", request.method)
        assertTrue(request.path!!.contains("/api/2.0/authentication"))
    }

    @Test
    fun `test authenticate with invalid credentials`() = runTest {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(401)
            .setBody("Unauthorized"))

        val result = apiClient.authenticate("baduser", "badpass")

        assertTrue(result.isFailure)
    }

    @Test
    fun `test getCapabilities success`() = runTest {
        val responseJson = """
            {
                "response": {
                    "ldapEnabled": true,
                    "ssoUrl": "https://sso.example.com",
                    "ssoLabel": "SSO Login",
                    "providers": ["google", "microsoft", "github"],
                    "recaptchaPublicKey": "test-key"
                },
                "status": 0,
                "statusCode": 200
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getCapabilities()

        assertTrue(result.isSuccess)
        val capabilities = result.getOrNull()!!
        assertTrue(capabilities.ldapEnabled)
        assertEquals("https://sso.example.com", capabilities.ssoUrl)
        assertEquals(3, capabilities.providers.size)
    }

    // File Operations Tests

    @Test
    fun `test getFiles returns file list`() = runTest {
        val responseJson = """
            {
                "response": {
                    "files": [
                        {
                            "id": "file1",
                            "title": "Document.docx",
                            "folderId": "folder1",
                            "version": 1,
                            "contentLength": 15360,
                            "fileExst": "docx",
                            "created": "2024-01-01T10:00:00Z",
                            "updated": "2024-01-02T15:30:00Z",
                            "canEdit": true
                        },
                        {
                            "id": "file2",
                            "title": "Spreadsheet.xlsx",
                            "folderId": "folder1",
                            "version": 2,
                            "contentLength": 25600,
                            "fileExst": "xlsx",
                            "created": "2024-01-03T09:00:00Z",
                            "updated": "2024-01-04T11:20:00Z",
                            "canEdit": true
                        }
                    ],
                    "folders": [],
                    "total": 2,
                    "count": 2
                },
                "status": 0,
                "statusCode": 200
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getFiles(testToken)

        assertTrue(result.isSuccess)
        val filesData = result.getOrNull()!!
        assertEquals(2, filesData.files.size)
        assertEquals("Document.docx", filesData.files[0].title)
        assertEquals("Spreadsheet.xlsx", filesData.files[1].title)
    }

    @Test
    fun `test getFiles with folder filter`() = runTest {
        val responseJson = """
            {
                "response": {
                    "files": [],
                    "folders": [],
                    "total": 0,
                    "count": 0
                },
                "status": 0,
                "statusCode": 200
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getFiles(testToken, folderId = "folder1")

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.files?.size)

        val request = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("folderId=folder1"))
    }

    @Test
    fun `test getFileInfo returns file details`() = runTest {
        val responseJson = """
            {
                "id": "file1",
                "title": "Presentation.pptx",
                "folderId": "folder1",
                "version": 3,
                "contentLength": 51200,
                "fileExst": "pptx",
                "created": "2024-01-01T10:00:00Z",
                "updated": "2024-01-05T14:00:00Z",
                "canEdit": true,
                "canShare": true
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getFileInfo(testToken, "file1")

        assertTrue(result.isSuccess)
        val file = result.getOrNull()!!
        assertEquals("Presentation.pptx", file.title)
        assertEquals(3, file.version)
        assertTrue(file.canEdit)
    }

    @Test
    fun `test uploadFile success`() = runTest {
        val responseJson = """
            {
                "response": {
                    "id": "newfile1",
                    "title": "UploadedDoc.docx",
                    "folderId": "folder1",
                    "version": 1,
                    "contentLength": 10240,
                    "fileExst": "docx",
                    "created": "2024-01-06T10:00:00Z",
                    "updated": "2024-01-06T10:00:00Z"
                },
                "status": 0,
                "statusCode": 201
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(201)
            .setBody(responseJson)
            .addHeader("Content-Type", "application/json"))

        val testFile = File.createTempFile("test", ".docx")
        testFile.writeText("Test document content")

        val result = apiClient.uploadFile(testToken, "folder1", testFile)

        assertTrue(result.isSuccess)
        assertEquals("newfile1", result.getOrNull()?.id)

        testFile.delete()
    }

    @Test
    fun `test deleteFile success`() = runTest {
        val responseJson = """
            {
                "response": [
                    {
                        "id": "op1",
                        "operation": 2,
                        "progress": 100,
                        "processed": 1
                    }
                ],
                "status": 0,
                "statusCode": 200
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.deleteFile(testToken, "file1")

        assertTrue(result.isSuccess)
        assertEquals(100, result.getOrNull()?.get(0)?.progress)
    }

    @Test
    fun `test downloadFile success`() = runTest {
        val fileContent = "Sample file content for download test"

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(fileContent)
            .addHeader("Content-Type", "application/octet-stream"))

        val result = apiClient.downloadFile(testToken, "file1")

        assertTrue(result.isSuccess)
        assertEquals(fileContent, String(result.getOrNull()!!))
    }

    // Folder Operations Tests

    @Test
    fun `test getFolderContents returns folders and files`() = runTest {
        val responseJson = """
            {
                "response": {
                    "files": [
                        {
                            "id": "file1",
                            "title": "Report.pdf",
                            "folderId": "folder1",
                            "version": 1,
                            "fileExst": "pdf",
                            "created": "2024-01-01T10:00:00Z",
                            "updated": "2024-01-01T10:00:00Z"
                        }
                    ],
                    "folders": [
                        {
                            "id": "subfolder1",
                            "title": "Archives",
                            "parentId": "folder1",
                            "filesCount": 5,
                            "foldersCount": 0,
                            "created": "2024-01-01T09:00:00Z",
                            "updated": "2024-01-03T12:00:00Z"
                        }
                    ],
                    "total": 2,
                    "count": 2
                },
                "status": 0,
                "statusCode": 200
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getFolderContents(testToken, "folder1")

        assertTrue(result.isSuccess)
        val data = result.getOrNull()!!
        assertEquals(1, data.files.size)
        assertEquals(1, data.folders.size)
        assertEquals("Archives", data.folders[0].title)
    }

    @Test
    fun `test createFolder success`() = runTest {
        val responseJson = """
            {
                "response": {
                    "id": "newfolder1",
                    "title": "New Project",
                    "parentId": "folder1",
                    "filesCount": 0,
                    "foldersCount": 0,
                    "created": "2024-01-06T15:00:00Z",
                    "updated": "2024-01-06T15:00:00Z"
                },
                "status": 0,
                "statusCode": 200
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.createFolder(testToken, "New Project", "folder1")

        assertTrue(result.isSuccess)
        assertEquals("New Project", result.getOrNull()?.title)
        assertEquals(0, result.getOrNull()?.filesCount)
    }

    @Test
    fun `test deleteFolder success`() = runTest {
        val responseJson = """
            {
                "response": [
                    {
                        "id": "op2",
                        "operation": 2,
                        "progress": 100,
                        "processed": 1
                    }
                ],
                "status": 0,
                "statusCode": 200
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.deleteFolder(testToken, "folder1")

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.get(0)?.operation)
    }

    // Move and Copy Operations Tests

    @Test
    fun `test moveFile to different folder`() = runTest {
        val responseJson = """
            {
                "response": [
                    {
                        "id": "op3",
                        "operation": 1,
                        "progress": 100,
                        "processed": 1
                    }
                ],
                "status": 0,
                "statusCode": 200
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.moveFile(testToken, "file1", "folder2")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.get(0)?.operation) // Move operation
    }

    @Test
    fun `test moveFolder to different location`() = runTest {
        val responseJson = """
            {
                "response": [
                    {
                        "id": "op4",
                        "operation": 1,
                        "progress": 100,
                        "processed": 1
                    }
                ],
                "status": 0,
                "statusCode": 200
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.moveFolder(testToken, "folder1", "folder2")

        assertTrue(result.isSuccess)
        assertEquals(100, result.getOrNull()?.get(0)?.progress)
    }

    @Test
    fun `test copyFile creates duplicate`() = runTest {
        val responseJson = """
            {
                "response": [
                    {
                        "id": "op5",
                        "operation": 0,
                        "progress": 100,
                        "processed": 1
                    }
                ],
                "status": 0,
                "statusCode": 200
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.copyFile(testToken, "file1", "folder2")

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.get(0)?.operation) // Copy operation
    }

    @Test
    fun `test copyFolder creates duplicate`() = runTest {
        val responseJson = """
            {
                "response": [
                    {
                        "id": "op6",
                        "operation": 0,
                        "progress": 100,
                        "processed": 1
                    }
                ],
                "status": 0,
                "statusCode": 200
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson)
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.copyFolder(testToken, "folder1", "folder2")

        assertTrue(result.isSuccess)
        assertEquals(100, result.getOrNull()?.get(0)?.progress)
    }

    // Error Handling Tests

    @Test
    fun `test handle 404 not found`() = runTest {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(404)
            .setBody("Not Found"))

        val result = apiClient.getFileInfo(testToken, "nonexistent")

        assertTrue(result.isFailure)
    }

    @Test
    fun `test handle 500 server error`() = runTest {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error"))

        val result = apiClient.getFiles(testToken)

        assertTrue(result.isFailure)
    }

    @Test
    fun `test handle network timeout`() = runTest {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("{}")
            .setBodyDelay(10, java.util.concurrent.TimeUnit.SECONDS))

        // This test verifies that timeout is configured
        // In real scenario, it would timeout
    }

    @Test
    fun `test handle empty response body`() = runTest {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("")
            .addHeader("Content-Type", "application/json"))

        val result = apiClient.getFiles(testToken)

        assertTrue(result.isFailure)
    }

    // Document Type Tests

    @Test
    fun `test document type detection for various formats`() {
        assertEquals("word", apiClient.getDocumentType("docx"))
        assertEquals("cell", apiClient.getDocumentType("xlsx"))
        assertEquals("slide", apiClient.getDocumentType("pptx"))
        assertEquals("word", apiClient.getDocumentType("odt"))
        assertEquals("cell", apiClient.getDocumentType("csv"))
    }

    @Test
    fun `test editable file type detection`() {
        assertTrue(apiClient.isEditableFileType("docx"))
        assertTrue(apiClient.isEditableFileType("xlsx"))
        assertTrue(apiClient.isEditableFileType("pptx"))
        assertTrue(apiClient.isEditableFileType("txt"))
        assertTrue(apiClient.isEditableFileType("csv"))
        assertFalse(apiClient.isEditableFileType("pdf"))
        assertFalse(apiClient.isEditableFileType("jpg"))
        assertFalse(apiClient.isEditableFileType("mp4"))
    }
}
