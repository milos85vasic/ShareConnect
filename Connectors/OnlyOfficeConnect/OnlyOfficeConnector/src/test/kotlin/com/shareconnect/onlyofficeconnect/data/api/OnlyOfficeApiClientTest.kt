package com.shareconnect.onlyofficeconnect.data.api

import com.shareconnect.onlyofficeconnect.data.models.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.File

/**
 * Unit tests for OnlyOfficeApiClient
 * Tests all API operations with mocked responses
 */
class OnlyOfficeApiClientTest {

    private lateinit var apiService: OnlyOfficeApiService
    private lateinit var apiClient: OnlyOfficeApiClient

    private val testServerUrl = "https://office.example.com"
    private val testToken = "test-token-123"
    private val testJwtSecret = "test-secret"

    @Before
    fun setup() {
        apiService = mockk()
        apiClient = OnlyOfficeApiClient(testServerUrl, testJwtSecret, apiService)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    // Authentication Tests

    @Test
    fun `test authenticate success`() = runTest {
        val authRequest = OnlyOfficeAuthRequest("testuser", "testpass")
        val authUser = OnlyOfficeAuthUser("user1", "Test User", "test@example.com", "testuser")
        val authResponse = OnlyOfficeAuthResponse(testToken, "2024-12-31", authUser)

        coEvery { apiService.authenticate(authRequest) } returns Response.success(authResponse)

        val result = apiClient.authenticate("testuser", "testpass")

        assertTrue(result.isSuccess)
        assertEquals(testToken, result.getOrNull()?.token)
        assertEquals("Test User", result.getOrNull()?.response?.displayName)
    }

    @Test
    fun `test authenticate failure`() = runTest {
        val authRequest = OnlyOfficeAuthRequest("baduser", "badpass")

        coEvery { apiService.authenticate(authRequest) } returns Response.error(
            401,
            ResponseBody.create(null, "Unauthorized")
        )

        val result = apiClient.authenticate("baduser", "badpass")

        assertTrue(result.isFailure)
    }

    @Test
    fun `test getCapabilities success`() = runTest {
        val capabilities = OnlyOfficeCapabilities(
            ldapEnabled = true,
            ssoUrl = "https://sso.example.com",
            providers = listOf("google", "microsoft")
        )
        val response = OnlyOfficeCapabilitiesResponse(capabilities)

        coEvery { apiService.getCapabilities() } returns Response.success(response)

        val result = apiClient.getCapabilities()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.ldapEnabled == true)
        assertEquals(2, result.getOrNull()?.providers?.size)
    }

    // File Operations Tests

    @Test
    fun `test getFiles success`() = runTest {
        val file1 = OnlyOfficeFile(
            id = "file1",
            title = "Document.docx",
            folderId = "folder1",
            version = 1,
            fileExst = "docx",
            created = "2024-01-01",
            updated = "2024-01-02"
        )
        val filesData = OnlyOfficeFilesData(
            files = listOf(file1),
            folders = emptyList(),
            total = 1,
            count = 1
        )
        val filesResponse = OnlyOfficeFilesResponse(filesData)

        coEvery {
            apiService.getFiles(any(), any(), any(), any(), any(), any())
        } returns Response.success(filesResponse)

        val result = apiClient.getFiles(testToken)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.files?.size)
        assertEquals("Document.docx", result.getOrNull()?.files?.get(0)?.title)
    }

    @Test
    fun `test getFiles with search`() = runTest {
        val filesData = OnlyOfficeFilesData(files = emptyList(), total = 0, count = 0)
        val filesResponse = OnlyOfficeFilesResponse(filesData)

        coEvery {
            apiService.getFiles(any(), any(), any(), "test", any(), any())
        } returns Response.success(filesResponse)

        val result = apiClient.getFiles(testToken, searchText = "test")

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.files?.size)
    }

    @Test
    fun `test getFileInfo success`() = runTest {
        val file = OnlyOfficeFile(
            id = "file1",
            title = "Test.xlsx",
            folderId = "folder1",
            version = 2,
            fileExst = "xlsx",
            created = "2024-01-01",
            updated = "2024-01-02"
        )

        coEvery { apiService.getFileInfo(any(), "file1") } returns Response.success(file)

        val result = apiClient.getFileInfo(testToken, "file1")

        assertTrue(result.isSuccess)
        assertEquals("Test.xlsx", result.getOrNull()?.title)
        assertEquals(2, result.getOrNull()?.version)
    }

    @Test
    fun `test uploadFile success`() = runTest {
        val file = File.createTempFile("test", ".docx")
        file.writeText("Test content")

        val uploadedFile = OnlyOfficeFile(
            id = "newfile1",
            title = file.name,
            folderId = "folder1",
            version = 1,
            fileExst = "docx",
            created = "2024-01-01",
            updated = "2024-01-01"
        )
        val uploadResponse = OnlyOfficeUploadResponse(uploadedFile)

        coEvery {
            apiService.uploadFile(any(), any(), any(), any())
        } returns Response.success(uploadResponse)

        val result = apiClient.uploadFile(testToken, "folder1", file)

        assertTrue(result.isSuccess)
        assertEquals("newfile1", result.getOrNull()?.id)

        file.delete()
    }

    @Test
    fun `test deleteFile success`() = runTest {
        val progress = OnlyOfficeOperationProgress(
            id = "op1",
            operation = 2, // Delete
            progress = 100,
            processed = 1
        )
        val operationResponse = OnlyOfficeOperationResponse(listOf(progress))

        coEvery {
            apiService.deleteFile(any(), "file1", any(), any())
        } returns Response.success(operationResponse)

        val result = apiClient.deleteFile(testToken, "file1")

        assertTrue(result.isSuccess)
        assertEquals(100, result.getOrNull()?.get(0)?.progress)
    }

    @Test
    fun `test downloadFile success`() = runTest {
        val fileContent = "Test file content".toByteArray()
        val responseBody = ResponseBody.create(null, fileContent)

        coEvery {
            apiService.downloadFile(any(), "file1")
        } returns Response.success(responseBody)

        val result = apiClient.downloadFile(testToken, "file1")

        assertTrue(result.isSuccess)
        assertArrayEquals(fileContent, result.getOrNull())
    }

    // Folder Operations Tests

    @Test
    fun `test getFolderContents success`() = runTest {
        val folder = OnlyOfficeFolder(
            id = "folder1",
            title = "Documents",
            filesCount = 5,
            foldersCount = 2,
            created = "2024-01-01",
            updated = "2024-01-02"
        )
        val filesData = OnlyOfficeFilesData(
            files = emptyList(),
            folders = listOf(folder),
            total = 1,
            count = 1
        )
        val filesResponse = OnlyOfficeFilesResponse(filesData)

        coEvery {
            apiService.getFolderContents(any(), "folder1", any(), any())
        } returns Response.success(filesResponse)

        val result = apiClient.getFolderContents(testToken, "folder1")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.folders?.size)
        assertEquals("Documents", result.getOrNull()?.folders?.get(0)?.title)
    }

    @Test
    fun `test createFolder success`() = runTest {
        val newFolder = OnlyOfficeFolder(
            id = "folder2",
            title = "New Folder",
            filesCount = 0,
            foldersCount = 0,
            created = "2024-01-01",
            updated = "2024-01-01"
        )
        val folderResponse = OnlyOfficeFolderResponse(newFolder)

        coEvery {
            apiService.createFolder(any(), "New Folder", any())
        } returns Response.success(folderResponse)

        val result = apiClient.createFolder(testToken, "New Folder")

        assertTrue(result.isSuccess)
        assertEquals("New Folder", result.getOrNull()?.title)
        assertEquals(0, result.getOrNull()?.filesCount)
    }

    @Test
    fun `test deleteFolder success`() = runTest {
        val progress = OnlyOfficeOperationProgress(
            id = "op2",
            operation = 2,
            progress = 100,
            processed = 1
        )
        val operationResponse = OnlyOfficeOperationResponse(listOf(progress))

        coEvery {
            apiService.deleteFolder(any(), "folder1", any(), any())
        } returns Response.success(operationResponse)

        val result = apiClient.deleteFolder(testToken, "folder1")

        assertTrue(result.isSuccess)
        assertEquals(100, result.getOrNull()?.get(0)?.progress)
    }

    // Move and Copy Operations Tests

    @Test
    fun `test moveFile success`() = runTest {
        val progress = OnlyOfficeOperationProgress(
            id = "op3",
            operation = 1, // Move
            progress = 100,
            processed = 1
        )
        val operationResponse = OnlyOfficeOperationResponse(listOf(progress))

        coEvery {
            apiService.moveFile(any(), any(), any(), any())
        } returns Response.success(operationResponse)

        val result = apiClient.moveFile(testToken, "file1", "folder2")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.get(0)?.operation) // Move operation
    }

    @Test
    fun `test copyFile success`() = runTest {
        val progress = OnlyOfficeOperationProgress(
            id = "op4",
            operation = 0, // Copy
            progress = 100,
            processed = 1
        )
        val operationResponse = OnlyOfficeOperationResponse(listOf(progress))

        coEvery {
            apiService.copyFile(any(), any(), any(), any())
        } returns Response.success(operationResponse)

        val result = apiClient.copyFile(testToken, "file1", "folder2")

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.get(0)?.operation) // Copy operation
    }

    // Document Type Detection Tests

    @Test
    fun `test getDocumentType for word documents`() {
        assertEquals("word", apiClient.getDocumentType("docx"))
        assertEquals("word", apiClient.getDocumentType("doc"))
        assertEquals("word", apiClient.getDocumentType("odt"))
        assertEquals("word", apiClient.getDocumentType("txt"))
        assertEquals("word", apiClient.getDocumentType("rtf"))
    }

    @Test
    fun `test getDocumentType for spreadsheets`() {
        assertEquals("cell", apiClient.getDocumentType("xlsx"))
        assertEquals("cell", apiClient.getDocumentType("xls"))
        assertEquals("cell", apiClient.getDocumentType("ods"))
        assertEquals("cell", apiClient.getDocumentType("csv"))
    }

    @Test
    fun `test getDocumentType for presentations`() {
        assertEquals("slide", apiClient.getDocumentType("pptx"))
        assertEquals("slide", apiClient.getDocumentType("ppt"))
        assertEquals("slide", apiClient.getDocumentType("odp"))
    }

    @Test
    fun `test isEditableFileType`() {
        assertTrue(apiClient.isEditableFileType("docx"))
        assertTrue(apiClient.isEditableFileType("xlsx"))
        assertTrue(apiClient.isEditableFileType("pptx"))
        assertTrue(apiClient.isEditableFileType("txt"))
        assertFalse(apiClient.isEditableFileType("pdf"))
        assertFalse(apiClient.isEditableFileType("jpg"))
    }

    // JWT Token Generation Tests

    @Test
    fun `test generateJwtToken creates valid token`() {
        val payload = mapOf(
            "fileId" to "file1",
            "userId" to "user1"
        )

        val token = apiClient.generateJwtToken(payload)

        assertNotNull(token)
        assertTrue(token!!.split(".").size == 3) // JWT has 3 parts
    }

    @Test
    fun `test generateJwtToken without secret returns null`() {
        val clientWithoutSecret = OnlyOfficeApiClient(testServerUrl, null, apiService)
        val payload = mapOf("test" to "data")

        val token = clientWithoutSecret.generateJwtToken(payload)

        assertNull(token)
    }
}
