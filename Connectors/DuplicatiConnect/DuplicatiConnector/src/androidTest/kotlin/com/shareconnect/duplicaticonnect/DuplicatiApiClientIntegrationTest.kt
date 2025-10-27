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


package com.shareconnect.duplicaticonnect

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.duplicaticonnect.data.api.DuplicatiApiClient
import com.shareconnect.duplicaticonnect.data.models.*
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Integration tests for DuplicatiApiClient
 * Tests API client behavior in Android environment with full network stack
 */
@RunWith(AndroidJUnit4::class)
class DuplicatiApiClientIntegrationTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var apiClient: DuplicatiApiClient
    private lateinit var baseUrl: String

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull("Context should not be null", context)

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
    fun testAuthenticationFlow() = runBlocking {
        val loginResponse = """
            {
                "Status": "OK",
                "Salt": "test-salt-123",
                "Nonce": "test-nonce-456"
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(loginResponse).setResponseCode(200))

        val clientWithAuth = DuplicatiApiClient(
            baseUrl = baseUrl,
            password = "securePassword123"
        )

        val result = clientWithAuth.login()

        assertTrue("Login should succeed", result.isSuccess)
        val response = result.getOrNull()
        assertNotNull("Response should not be null", response)
        assertEquals("Status should be OK", "OK", response?.status)
        assertEquals("Salt should match", "test-salt-123", response?.salt)

        clientWithAuth.close()
    }

    @Test
    fun testServerStateRetrieval() = runBlocking {
        val serverStateResponse = """
            {
                "ProgramState": "Running",
                "LastEventID": 98765,
                "LastDataUpdateID": 43210,
                "ActiveTask": {
                    "Item1": 1,
                    "Item2": "Backup"
                },
                "PausedUntil": null,
                "SuggestedStatusIcon": "active",
                "EstimatedPauseEnd": null,
                "UpdatedVersion": null,
                "UpdateDownloadProgress": null,
                "HasWarning": false,
                "HasError": false
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(serverStateResponse).setResponseCode(200))

        val result = apiClient.getServerState()

        assertTrue("Should retrieve server state successfully", result.isSuccess)
        val state = result.getOrNull()
        assertNotNull("State should not be null", state)
        assertEquals("Program state should be Running", "Running", state?.programState)
        assertEquals("Last event ID should match", 98765L, state?.lastEventId)
        assertNotNull("Active task should exist", state?.activeTask)
        assertEquals("Task type should be Backup", "Backup", state?.activeTask?.taskType)
    }

    @Test
    fun testBackupListRetrievalWithMetadata() = runBlocking {
        val backupsResponse = """
            [
                {
                    "ID": "backup-1",
                    "Name": "Documents Backup",
                    "Description": "Daily backup of documents folder",
                    "Tags": ["documents", "daily", "important"],
                    "TargetURL": "file:///mnt/backups/documents",
                    "DBPath": "/var/lib/duplicati/backup-1.sqlite",
                    "Sources": ["/home/user/documents", "/home/user/work"],
                    "Settings": [
                        {
                            "Name": "encryption-module",
                            "Value": "aes",
                            "Filter": null
                        },
                        {
                            "Name": "compression-module",
                            "Value": "zip",
                            "Filter": null
                        }
                    ],
                    "Metadata": {
                        "LastBackupDate": "2025-10-25T09:00:00Z",
                        "LastBackupFileCount": 1250,
                        "LastBackupFileSize": 524288000,
                        "SourceFilesCount": 1300,
                        "SourceFilesSize": 536870912
                    },
                    "IsTemporary": false
                },
                {
                    "ID": "backup-2",
                    "Name": "Photos Backup",
                    "Description": "Weekly photo archive",
                    "Tags": ["photos", "weekly"],
                    "TargetURL": "s3://my-bucket/photos",
                    "DBPath": "/var/lib/duplicati/backup-2.sqlite",
                    "Sources": ["/home/user/photos"],
                    "Settings": null,
                    "Metadata": null,
                    "IsTemporary": false
                }
            ]
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(backupsResponse).setResponseCode(200))

        val result = apiClient.getBackups()

        assertTrue("Should retrieve backups successfully", result.isSuccess)
        val backups = result.getOrNull()
        assertNotNull("Backups list should not be null", backups)
        assertEquals("Should have 2 backups", 2, backups?.size)

        val firstBackup = backups?.get(0)
        assertEquals("First backup name should match", "Documents Backup", firstBackup?.name)
        assertEquals("Should have 2 sources", 2, firstBackup?.sources?.size)
        assertEquals("Should have 2 settings", 2, firstBackup?.settings?.size)
        assertNotNull("Should have metadata", firstBackup?.metadata)
        assertEquals("File count should match", 1250L, firstBackup?.metadata?.lastBackupFileCount)
    }

    @Test
    fun testBackupExecutionAndMonitoring() = runBlocking {
        // Start backup
        val runResponse = """
            {
                "ID": 555,
                "Status": "Started",
                "Message": "Backup operation initiated"
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(runResponse).setResponseCode(200))

        val runResult = apiClient.runBackup("backup-1")

        assertTrue("Backup should start successfully", runResult.isSuccess)
        val runInfo = runResult.getOrNull()
        assertEquals("Status should be Started", "Started", runInfo?.status)
        assertEquals("Task ID should match", 555L, runInfo?.id)

        // Check progress
        val progressResponse = """
            [
                {
                    "Phase": "Backup_ProcessingFiles",
                    "Overall": {
                        "Speed": 2097152.0,
                        "Count": 500,
                        "Size": 209715200
                    },
                    "Current": {
                        "Speed": 1048576.0,
                        "Count": 250,
                        "Size": 104857600
                    },
                    "ProcessedFileCount": 250,
                    "ProcessedFileSize": 104857600,
                    "TotalFileCount": 500,
                    "TotalFileSize": 209715200,
                    "StillCounting": false
                }
            ]
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(progressResponse).setResponseCode(200))

        val progressResult = apiClient.getBackupsProgress()

        assertTrue("Should get progress successfully", progressResult.isSuccess)
        val progress = progressResult.getOrNull()
        assertNotNull("Progress should not be null", progress)
        assertEquals("Should have 1 progress entry", 1, progress?.size)
        assertEquals("Phase should match", "Backup_ProcessingFiles", progress?.get(0)?.phase)
        assertEquals("Processed count should be 250", 250L, progress?.get(0)?.processedFileCount)
        assertEquals("Total count should be 500", 500L, progress?.get(0)?.totalFileCount)
    }

    @Test
    fun testFileRestoreOperation() = runBlocking {
        val restoreResponse = """
            {
                "ID": 777,
                "Status": "Started",
                "Message": "Restore operation initiated"
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(restoreResponse).setResponseCode(200))

        val options = DuplicatiRestoreOptions(
            time = "2025-10-25T00:00:00Z",
            restorePath = "/tmp/restore",
            overwrite = true,
            permissions = true,
            skipMetadata = false,
            paths = listOf("/home/user/documents/important.pdf", "/home/user/documents/report.docx")
        )

        val result = apiClient.restoreFiles("backup-1", options)

        assertTrue("Restore should start successfully", result.isSuccess)
        val restoreInfo = result.getOrNull()
        assertNotNull("Restore info should not be null", restoreInfo)
        assertEquals("Status should be Started", "Started", restoreInfo?.status)
        assertEquals("Task ID should be 777", 777L, restoreInfo?.id)
    }

    @Test
    fun testBackupLogRetrieval() = runBlocking {
        val logResponse = """
            [
                {
                    "ID": 1,
                    "BackupID": "backup-1",
                    "Type": "Information",
                    "Timestamp": "2025-10-25T10:00:00Z",
                    "Message": "Starting backup operation",
                    "Exception": null
                },
                {
                    "ID": 2,
                    "BackupID": "backup-1",
                    "Type": "Information",
                    "Timestamp": "2025-10-25T10:01:00Z",
                    "Message": "Scanning source folders",
                    "Exception": null
                },
                {
                    "ID": 3,
                    "BackupID": "backup-1",
                    "Type": "Warning",
                    "Timestamp": "2025-10-25T10:02:00Z",
                    "Message": "File access denied: /home/user/protected.txt",
                    "Exception": "System.UnauthorizedAccessException"
                },
                {
                    "ID": 4,
                    "BackupID": "backup-1",
                    "Type": "Information",
                    "Timestamp": "2025-10-25T10:10:00Z",
                    "Message": "Backup completed successfully",
                    "Exception": null
                }
            ]
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(logResponse).setResponseCode(200))

        val result = apiClient.getBackupLog("backup-1", offset = 0, pageSize = 100)

        assertTrue("Should retrieve log successfully", result.isSuccess)
        val logs = result.getOrNull()
        assertNotNull("Logs should not be null", logs)
        assertEquals("Should have 4 log entries", 4, logs?.size)

        val warningLog = logs?.get(2)
        assertEquals("Should be Warning type", "Warning", warningLog?.type)
        assertNotNull("Should have exception", warningLog?.exception)
        assertTrue("Message should mention access denied",
            warningLog?.message?.contains("access denied") ?: false)
    }

    @Test
    fun testFilesystemBrowsing() = runBlocking {
        val filesystemResponse = """
            [
                {
                    "Path": "/home",
                    "IconCls": "folder",
                    "IsFolder": true,
                    "Resolves": true,
                    "Hidden": false
                },
                {
                    "Path": "/var",
                    "IconCls": "folder",
                    "IsFolder": true,
                    "Resolves": true,
                    "Hidden": false
                },
                {
                    "Path": "/etc",
                    "IconCls": "folder",
                    "IsFolder": true,
                    "Resolves": true,
                    "Hidden": false
                }
            ]
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(filesystemResponse).setResponseCode(200))

        val result = apiClient.browseFilesystem("/")

        assertTrue("Should browse filesystem successfully", result.isSuccess)
        val entries = result.getOrNull()
        assertNotNull("Entries should not be null", entries)
        assertEquals("Should have 3 entries", 3, entries?.size)
        assertTrue("All entries should be folders", entries?.all { it.isFolder } ?: false)
    }

    @Test
    fun testNotificationsRetrieval() = runBlocking {
        val notificationsResponse = """
            [
                {
                    "ID": 1,
                    "Type": "Information",
                    "Title": "Backup Completed",
                    "Message": "Documents Backup completed successfully in 10 minutes",
                    "Exception": null,
                    "BackupID": "backup-1",
                    "Action": null,
                    "Timestamp": "2025-10-25T10:10:00Z"
                },
                {
                    "ID": 2,
                    "Type": "Error",
                    "Title": "Backup Failed",
                    "Message": "Photos Backup failed due to network error",
                    "Exception": "System.Net.WebException: Connection timeout",
                    "BackupID": "backup-2",
                    "Action": "retry",
                    "Timestamp": "2025-10-25T11:00:00Z"
                }
            ]
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(notificationsResponse).setResponseCode(200))

        val result = apiClient.getNotifications()

        assertTrue("Should retrieve notifications successfully", result.isSuccess)
        val notifications = result.getOrNull()
        assertNotNull("Notifications should not be null", notifications)
        assertEquals("Should have 2 notifications", 2, notifications?.size)

        val errorNotification = notifications?.get(1)
        assertEquals("Should be Error type", "Error", errorNotification?.type)
        assertNotNull("Should have exception", errorNotification?.exception)
        assertEquals("Should have retry action", "retry", errorNotification?.action)
    }

    @Test
    fun testBackupCreationAndUpdate() = runBlocking {
        // Create backup
        val createResponse = """
            {
                "ID": "backup-3",
                "Status": "Created"
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(createResponse).setResponseCode(200))

        val newBackup = DuplicatiBackup(
            id = "",
            name = "New Test Backup",
            description = "Automated test backup",
            tags = listOf("test", "automated"),
            targetUrl = "file:///backup/test",
            dbPath = null,
            sources = listOf("/test/source"),
            settings = listOf(
                BackupSetting(name = "encryption-module", value = "aes", filter = null),
                BackupSetting(name = "passphrase", value = "test123", filter = null)
            ),
            filters = listOf(
                BackupFilter(order = 0, include = false, expression = "*.tmp")
            ),
            schedule = null,
            metadata = null,
            isTemporary = false
        )

        val createResult = apiClient.createBackup(newBackup)

        assertTrue("Should create backup successfully", createResult.isSuccess)
        val createInfo = createResult.getOrNull()
        assertEquals("Should return new backup ID", "backup-3", createInfo?.id)
        assertEquals("Status should be Created", "Created", createInfo?.status)

        // Update backup
        mockServer.enqueue(MockResponse().setResponseCode(200))

        val updatedBackup = newBackup.copy(
            id = "backup-3",
            description = "Updated description"
        )

        val updateResult = apiClient.updateBackup("backup-3", updatedBackup)

        assertTrue("Should update backup successfully", updateResult.isSuccess)
    }

    @Test
    fun testBackupDeletion() = runBlocking {
        mockServer.enqueue(MockResponse().setResponseCode(200))

        val result = apiClient.deleteBackup("backup-old", deleteRemoteFiles = true)

        assertTrue("Should delete backup successfully", result.isSuccess)

        val request = mockServer.takeRequest()
        assertTrue("Should include delete-remote-files parameter",
            request.path?.contains("delete-remote-files=true") ?: false)
    }

    @Test
    fun testBackupPauseAndResume() = runBlocking {
        // Pause backups
        mockServer.enqueue(MockResponse().setResponseCode(200))

        val pauseResult = apiClient.pauseBackups(duration = "2h")

        assertTrue("Should pause successfully", pauseResult.isSuccess)

        val pauseRequest = mockServer.takeRequest()
        assertTrue("Should include duration parameter",
            pauseRequest.path?.contains("duration=2h") ?: false)

        // Resume backups
        mockServer.enqueue(MockResponse().setResponseCode(200))

        val resumeResult = apiClient.resumeBackups()

        assertTrue("Should resume successfully", resumeResult.isSuccess)
    }

    @Test
    fun testBackupStopAndAbort() = runBlocking {
        // Stop backup gracefully
        mockServer.enqueue(MockResponse().setResponseCode(200))

        val stopResult = apiClient.stopBackup("backup-1")

        assertTrue("Should stop backup successfully", stopResult.isSuccess)

        // Abort backup forcefully
        mockServer.enqueue(MockResponse().setResponseCode(200))

        val abortResult = apiClient.abortBackup("backup-1")

        assertTrue("Should abort backup successfully", abortResult.isSuccess)
    }

    @Test
    fun testSystemInfoRetrieval() = runBlocking {
        val systemInfoResponse = """
            {
                "APIVersion": 1,
                "ServerVersion": "2.0.6.3",
                "ServerVersionName": "Duplicati - 2.0.6.3_beta_2021-06-07",
                "OSType": "Linux",
                "OSVersion": "5.15.0-76-generic",
                "MachineName": "backup-server-01",
                "UserName": "duplicati",
                "NewLine": "\n",
                "DirectorySeparator": "/"
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(systemInfoResponse).setResponseCode(200))

        val result = apiClient.getSystemInfo()

        assertTrue("Should retrieve system info successfully", result.isSuccess)
        val info = result.getOrNull()
        assertNotNull("System info should not be null", info)
        assertEquals("API version should be 1", 1, info?.apiVersion)
        assertEquals("Should be Linux", "Linux", info?.osType)
        assertEquals("Machine name should match", "backup-server-01", info?.machineName)
    }

    @Test
    fun testConnectionTest() = runBlocking {
        mockServer.enqueue(MockResponse().setResponseCode(200))

        val result = apiClient.testConnection("backup-1")

        assertTrue("Connection test should succeed", result.isSuccess)
    }

    @Test
    fun testBackupVerification() = runBlocking {
        val verifyResponse = """
            {
                "ID": 888,
                "Status": "Started",
                "Message": "Verification in progress"
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(verifyResponse).setResponseCode(200))

        val result = apiClient.verifyBackup("backup-1")

        assertTrue("Verification should start successfully", result.isSuccess)
        val verifyInfo = result.getOrNull()
        assertEquals("Task ID should be 888", 888L, verifyInfo?.id)
        assertEquals("Status should be Started", "Started", verifyInfo?.status)
    }

    @Test
    fun testBackupRepair() = runBlocking {
        val repairResponse = """
            {
                "ID": 999,
                "Status": "Started",
                "Message": "Repair operation started"
            }
        """.trimIndent()

        mockServer.enqueue(MockResponse().setBody(repairResponse).setResponseCode(200))

        val result = apiClient.repairBackup("backup-1")

        assertTrue("Repair should start successfully", result.isSuccess)
        val repairInfo = result.getOrNull()
        assertEquals("Task ID should be 999", 999L, repairInfo?.id)
        assertEquals("Status should be Started", "Started", repairInfo?.status)
    }

    @Test
    fun testErrorHandlingWithInvalidJson() = runBlocking {
        mockServer.enqueue(MockResponse()
            .setBody("Invalid JSON{{{")
            .setResponseCode(200))

        val result = apiClient.getServerState()

        assertTrue("Should fail with parse error", result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull("Exception should not be null", exception)
        assertTrue("Should contain parse error message",
            exception?.message?.contains("parse") ?: false)
    }

    @Test
    fun testErrorHandlingWithServerError() = runBlocking {
        mockServer.enqueue(MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error"))

        val result = apiClient.getBackups()

        assertTrue("Should fail with HTTP error", result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull("Exception should not be null", exception)
        assertTrue("Should contain 500 status code",
            exception?.message?.contains("500") ?: false)
    }

    @Test
    fun testTimeoutConfiguration() {
        val shortTimeoutClient = DuplicatiApiClient(
            baseUrl = baseUrl,
            timeoutSeconds = 1
        )

        // Client should be configured with short timeout
        assertNotNull("Client should be created", shortTimeoutClient)

        shortTimeoutClient.close()
    }
}
