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


package com.shareconnect.jdownloaderconnect.integration

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.jdownloaderconnect.data.database.JDownloaderDatabase
import com.shareconnect.jdownloaderconnect.data.model.*
import com.shareconnect.jdownloaderconnect.data.repository.JDownloaderRepository
import com.shareconnect.jdownloaderconnect.data.repository.ServerRepository
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class JDownloaderIntegrationTest {

    private lateinit var database: JDownloaderDatabase
    private lateinit var repository: JDownloaderRepository
    private lateinit var serverRepository: ServerRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, JDownloaderDatabase::class.java
        ).build()
        
        repository = JDownloaderRepository(
            database.downloadDao(),
            database.linkGrabberDao(),
            database.accountDao(),
            mockk() // Mock API for integration tests
        )
        
        serverRepository = ServerRepository(database.serverDao())
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun `save and retrieve server profile`() = runTest {
        // Given
        val serverProfile = ServerProfile(
            id = "test-server",
            name = "Test Server",
            serverUrl = "https://api.jdownloader.org",
            username = "testuser",
            isDefault = true
        )

        // When
        serverRepository.saveServer(serverProfile)
        val savedServer = serverRepository.getServerById("test-server")

        // Then
        assert(savedServer != null)
        assert(savedServer?.name == "Test Server")
        assert(savedServer?.isDefault == true)
    }

    @Test
    fun `save and retrieve download package with links`() = runTest {
        // Given
        val downloadPackage = DownloadPackage(
            uuid = "package-123",
            name = "Test Package",
            bytesTotal = 1000L,
            bytesLoaded = 500L,
            enabled = true,
            finished = false,
            priority = DownloadPriority.DEFAULT,
            host = "example.com",
            status = DownloadStatus.DOWNLOADING,
            addedDate = System.currentTimeMillis(),
            downloadDirectory = "/downloads"
        )
        
        val links = listOf(
            DownloadLink(
                uuid = "link-1",
                packageUuid = "package-123",
                name = "File 1",
                url = "http://example.com/file1",
                host = "example.com",
                bytesTotal = 500L,
                bytesLoaded = 250L,
                enabled = true,
                finished = false,
                status = DownloadStatus.DOWNLOADING,
                priority = DownloadPriority.DEFAULT,
                addedDate = System.currentTimeMillis(),
                availability = LinkAvailability.ONLINE
            ),
            DownloadLink(
                uuid = "link-2",
                packageUuid = "package-123",
                name = "File 2",
                url = "http://example.com/file2",
                host = "example.com",
                bytesTotal = 500L,
                bytesLoaded = 250L,
                enabled = true,
                finished = false,
                status = DownloadStatus.DOWNLOADING,
                priority = DownloadPriority.DEFAULT,
                addedDate = System.currentTimeMillis(),
                availability = LinkAvailability.ONLINE
            )
        )

        // When
        repository.addDownloadPackage(downloadPackage, links)
        val retrievedLinks = repository.getLinksByPackage("package-123")

        // Then
        var linkCount = 0
        retrievedLinks.collect { linkList ->
            linkCount = linkList.size
            assert(linkList.size == 2)
            assert(linkList.all { it.packageUuid == "package-123" })
        }
        assert(linkCount == 2)
    }

    @Test
    fun `save and retrieve account`() = runTest {
        // Given
        val account = JDownloaderAccount(
            id = "account-123",
            email = "test@example.com",
            password = "password",
            deviceName = "Test Device",
            deviceId = "device-123",
            isActive = true
        )

        // When
        repository.saveAccount(account)
        val activeAccount = repository.getActiveAccount()

        // Then
        var retrievedAccount: JDownloaderAccount? = null
        activeAccount.collect { acc ->
            retrievedAccount = acc
        }
        
        assert(retrievedAccount != null)
        assert(retrievedAccount?.email == "test@example.com")
        assert(retrievedAccount?.isActive == true)
    }

    @Test
    fun `delete download package should remove package and links`() = runTest {
        // Given
        val downloadPackage = DownloadPackage(
            uuid = "package-to-delete",
            name = "Package to Delete",
            bytesTotal = 1000L,
            bytesLoaded = 500L,
            enabled = true,
            finished = false,
            priority = DownloadPriority.DEFAULT,
            host = "example.com",
            status = DownloadStatus.DOWNLOADING,
            addedDate = System.currentTimeMillis(),
            downloadDirectory = "/downloads"
        )
        
        val links = listOf(
            DownloadLink(
                uuid = "link-to-delete",
                packageUuid = "package-to-delete",
                name = "File to Delete",
                url = "http://example.com/file",
                host = "example.com",
                bytesTotal = 500L,
                bytesLoaded = 250L,
                enabled = true,
                finished = false,
                status = DownloadStatus.DOWNLOADING,
                priority = DownloadPriority.DEFAULT,
                addedDate = System.currentTimeMillis(),
                availability = LinkAvailability.ONLINE
            )
        )

        repository.addDownloadPackage(downloadPackage, links)

        // When
        repository.deleteDownloadPackage("package-to-delete")
        val retrievedLinks = repository.getLinksByPackage("package-to-delete")

        // Then
        var linkCount = 0
        retrievedLinks.collect { linkList ->
            linkCount = linkList.size
        }
        assert(linkCount == 0)
    }
}