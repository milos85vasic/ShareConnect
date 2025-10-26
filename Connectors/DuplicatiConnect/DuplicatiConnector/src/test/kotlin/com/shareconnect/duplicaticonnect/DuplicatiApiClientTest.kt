package com.shareconnect.duplicaticonnect

import com.shareconnect.duplicaticonnect.data.api.DuplicatiApiClient
import com.shareconnect.duplicaticonnect.data.models.*
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for DuplicatiApiClient
 * Tests all API endpoints with mocked responses
 */
class DuplicatiApiClientTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var apiClient: DuplicatiApiClient
    private lateinit var baseUrl: String

    @Before
    fun setup() {
        mockServer = MockWebServer()
        mockServer.start()
        baseUrl = mockServer.url("/").toString().removeSuffix("/")
        apiClient = DuplicatiApiClient(baseUrl = baseUrl)
    }

    @After
    fun teardown() {
        apiClient.close()
        mockServer.shutdown()
    }

    @Test
    fun `test login success`() = runBlocking {
        val response = """
            {
                "Status": "OK",
                "Salt": "test-salt",
                "Nonce": "test-nonce"
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(response).setResponseCode(200))

        val clientWithPassword = DuplicatiApiClient(baseUrl = baseUrl, password = "test123")
        val result = clientWithPassword.login()

        assertTrue(result.isSuccess)
        val loginResponse = result.getOrNull()
        assertNotNull(loginResponse)
        assertEquals("OK", loginResponse?.status)
        assertEquals("test-salt", loginResponse?.salt)
        assertEquals("test-nonce", loginResponse?.nonce)
        clientWithPassword.close()
    }

    @Test
    fun `test get server state success`() = runBlocking {
        val response = """
            {
                "ProgramState": "Running",
                "LastEventID": 12345,
                "LastDataUpdateID": 67890,
                "ActiveTask": null,
                "PausedUntil": null,
                "SuggestedStatusIcon": "normal",
                "EstimatedPauseEnd": null,
                "UpdatedVersion": null,
                "UpdateDownloadProgress": null,
                "HasWarning": false,
                "HasError": false
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(response).setResponseCode(200))

        val result = apiClient.getServerState()

        assertTrue(result.isSuccess)
        val serverState = result.getOrNull()
        assertNotNull(serverState)
        assertEquals("Running", serverState?.programState)
        assertEquals(12345L, serverState?.lastEventId)
        assertEquals(67890L, serverState?.lastDataUpdateId)
        assertFalse(serverState?.hasWarning ?: true)
        assertFalse(serverState?.hasError ?: true)
    }

    @Test
    fun `test get backups list success`() = runBlocking {
        val response = """
            [
                {
                    "ID": "1",
                    "Name": "Test Backup",
                    "Description": "Test backup description",
                    "Tags": ["important", "daily"],
                    "TargetURL": "file:///backup/location",
                    "Sources": ["/home/user/documents", "/home/user/photos"],
                    "IsTemporary": false
                }
            ]
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(response).setResponseCode(200))

        val result = apiClient.getBackups()

        assertTrue(result.isSuccess)
        val backups = result.getOrNull()
        assertNotNull(backups)
        assertEquals(1, backups?.size)
        assertEquals("Test Backup", backups?.get(0)?.name)
        assertEquals("1", backups?.get(0)?.id)
        assertEquals(2, backups?.get(0)?.sources?.size)
    }

    @Test
    fun `test get backup by id success`() = runBlocking {
        val response = """
            {
                "ID": "1",
                "Name": "Test Backup",
                "Description": "Detailed backup",
                "Tags": ["test"],
                "TargetURL": "file:///backup/test",
                "Sources": ["/test/path"],
                "Settings": [
                    {
                        "Name": "encryption-module",
                        "Value": "aes"
                    }
                ],
                "IsTemporary": false
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(response).setResponseCode(200))

        val result = apiClient.getBackup("1")

        assertTrue(result.isSuccess)
        val backup = result.getOrNull()
        assertNotNull(backup)
        assertEquals("1", backup?.id)
        assertEquals("Test Backup", backup?.name)
        assertEquals(1, backup?.settings?.size)
        assertEquals("encryption-module", backup?.settings?.get(0)?.name)
    }

    @Test
    fun `test run backup success`() = runBlocking {
        val response = """
            {
                "ID": 123,
                "Status": "Started",
                "Message": "Backup started successfully"
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(response).setResponseCode(200))

        val result = apiClient.runBackup("1")

        assertTrue(result.isSuccess)
        val backupRun = result.getOrNull()
        assertNotNull(backupRun)
        assertEquals(123L, backupRun?.id)
        assertEquals("Started", backupRun?.status)
        assertEquals("Backup started successfully", backupRun?.message)
    }

    @Test
    fun `test restore files success`() = runBlocking {
        val response = """
            {
                "ID": 456,
                "Status": "Started",
                "Message": "Restore operation started"
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(response).setResponseCode(200))

        val restoreOptions = DuplicatiRestoreOptions(
            restorePath = "/restore/location",
            overwrite = false,
            paths = listOf("/backup/file1.txt", "/backup/file2.txt")
        )

        val result = apiClient.restoreFiles("1", restoreOptions)

        assertTrue(result.isSuccess)
        val restoreRun = result.getOrNull()
        assertNotNull(restoreRun)
        assertEquals(456L, restoreRun?.id)
        assertEquals("Started", restoreRun?.status)
    }

    @Test
    fun `test delete backup success`() = runBlocking {
        mockServer.enqueue(MockResponse().setResponseCode(200))

        val result = apiClient.deleteBackup("1", deleteRemoteFiles = false)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test get backup log success`() = runBlocking {
        val response = """
            [
                {
                    "ID": 1,
                    "BackupID": "1",
                    "Type": "Information",
                    "Timestamp": "2025-10-25T10:00:00Z",
                    "Message": "Backup started",
                    "Exception": null
                },
                {
                    "ID": 2,
                    "BackupID": "1",
                    "Type": "Information",
                    "Timestamp": "2025-10-25T10:05:00Z",
                    "Message": "Backup completed",
                    "Exception": null
                }
            ]
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(response).setResponseCode(200))

        val result = apiClient.getBackupLog("1", offset = 0, pageSize = 100)

        assertTrue(result.isSuccess)
        val logs = result.getOrNull()
        assertNotNull(logs)
        assertEquals(2, logs?.size)
        assertEquals("Backup started", logs?.get(0)?.message)
        assertEquals("Information", logs?.get(0)?.type)
    }

    @Test
    fun `test get backup progress success`() = runBlocking {
        val response = """
            [
                {
                    "Phase": "Backup_ProcessingFiles",
                    "Overall": {
                        "Speed": 1048576.0,
                        "Count": 100,
                        "Size": 104857600
                    },
                    "Current": {
                        "Speed": 524288.0,
                        "Count": 50,
                        "Size": 52428800
                    },
                    "ProcessedFileCount": 50,
                    "ProcessedFileSize": 52428800,
                    "TotalFileCount": 100,
                    "TotalFileSize": 104857600,
                    "StillCounting": false
                }
            ]
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(response).setResponseCode(200))

        val result = apiClient.getBackupsProgress()

        assertTrue(result.isSuccess)
        val progressList = result.getOrNull()
        assertNotNull(progressList)
        assertEquals(1, progressList?.size)
        assertEquals("Backup_ProcessingFiles", progressList?.get(0)?.phase)
        assertEquals(50L, progressList?.get(0)?.processedFileCount)
        assertEquals(100L, progressList?.get(0)?.totalFileCount)
    }

    @Test
    fun `test browse filesystem success`() = runBlocking {
        val response = """
            [
                {
                    "Path": "/home/user",
                    "IconCls": "folder",
                    "IsFolder": true,
                    "Resolves": true,
                    "Hidden": false
                },
                {
                    "Path": "/home/user/document.txt",
                    "IconCls": "file",
                    "IsFolder": false,
                    "Resolves": true,
                    "Hidden": false
                }
            ]
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(response).setResponseCode(200))

        val result = apiClient.browseFilesystem("/home/user")

        assertTrue(result.isSuccess)
        val entries = result.getOrNull()
        assertNotNull(entries)
        assertEquals(2, entries?.size)
        assertTrue(entries?.get(0)?.isFolder ?: false)
        assertFalse(entries?.get(1)?.isFolder ?: true)
    }

    @Test
    fun `test get notifications success`() = runBlocking {
        val response = """
            [
                {
                    "ID": 1,
                    "Type": "Information",
                    "Title": "Backup Completed",
                    "Message": "Backup job completed successfully",
                    "Exception": null,
                    "BackupID": "1",
                    "Action": null,
                    "Timestamp": "2025-10-25T10:00:00Z"
                }
            ]
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(response).setResponseCode(200))

        val result = apiClient.getNotifications()

        assertTrue(result.isSuccess)
        val notifications = result.getOrNull()
        assertNotNull(notifications)
        assertEquals(1, notifications?.size)
        assertEquals("Backup Completed", notifications?.get(0)?.title)
        assertEquals("Information", notifications?.get(0)?.type)
    }

    @Test
    fun `test create backup success`() = runBlocking {
        val jsonResponse = """
            {
                "ID": "2",
                "Status": "Created"
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        val newBackup = DuplicatiBackup(
            id = "",
            name = "New Backup",
            description = "New backup job",
            tags = listOf("new"),
            targetUrl = "file:///backup/new",
            sources = listOf("/new/path"),
            dbPath = null,
            settings = null,
            filters = null,
            schedule = null,
            metadata = null,
            isTemporary = false
        )

        val result = apiClient.createBackup(newBackup)

        assertTrue(result.isSuccess)
        val response = result.getOrNull()
        assertNotNull(response)
        assertEquals("2", response?.id)
        assertEquals("Created", response?.status)
    }

    @Test
    fun `test pause backups success`() = runBlocking {
        mockServer.enqueue(MockResponse().setResponseCode(200))

        val result = apiClient.pauseBackups(duration = "1h")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test resume backups success`() = runBlocking {
        mockServer.enqueue(MockResponse().setResponseCode(200))

        val result = apiClient.resumeBackups()

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test stop backup success`() = runBlocking {
        mockServer.enqueue(MockResponse().setResponseCode(200))

        val result = apiClient.stopBackup("1")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test abort backup success`() = runBlocking {
        mockServer.enqueue(MockResponse().setResponseCode(200))

        val result = apiClient.abortBackup("1")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test get system info success`() = runBlocking {
        val response = """
            {
                "APIVersion": 1,
                "ServerVersion": "2.0.6.3",
                "ServerVersionName": "Duplicati - 2.0.6.3",
                "OSType": "Linux",
                "OSVersion": "5.15.0",
                "MachineName": "backup-server",
                "UserName": "duplicati",
                "NewLine": "\n",
                "DirectorySeparator": "/"
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(response).setResponseCode(200))

        val result = apiClient.getSystemInfo()

        assertTrue(result.isSuccess)
        val systemInfo = result.getOrNull()
        assertNotNull(systemInfo)
        assertEquals(1, systemInfo?.apiVersion)
        assertEquals("2.0.6.3", systemInfo?.serverVersion)
        assertEquals("Linux", systemInfo?.osType)
    }

    @Test
    fun `test verify backup success`() = runBlocking {
        val response = """
            {
                "ID": 789,
                "Status": "Started",
                "Message": "Verification started"
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(response).setResponseCode(200))

        val result = apiClient.verifyBackup("1")

        assertTrue(result.isSuccess)
        val verifyRun = result.getOrNull()
        assertNotNull(verifyRun)
        assertEquals(789L, verifyRun?.id)
        assertEquals("Started", verifyRun?.status)
    }

    @Test
    fun `test repair backup success`() = runBlocking {
        val response = """
            {
                "ID": 999,
                "Status": "Started",
                "Message": "Repair started"
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(response).setResponseCode(200))

        val result = apiClient.repairBackup("1")

        assertTrue(result.isSuccess)
        val repairRun = result.getOrNull()
        assertNotNull(repairRun)
        assertEquals(999L, repairRun?.id)
        assertEquals("Started", repairRun?.status)
    }

    @Test
    fun `test http error handling`() = runBlocking {
        mockServer.enqueue(MockResponse().setResponseCode(404).setBody("Not Found"))

        val result = apiClient.getBackup("nonexistent")

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception?.message?.contains("404") ?: false)
    }

    @Test
    fun `test network error handling`() = runBlocking {
        // Shutdown server to simulate network error
        mockServer.shutdown()

        val result = apiClient.getServerState()

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }
}
