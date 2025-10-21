package com.shareconnect.jdownloaderconnect.repository

import com.shareconnect.jdownloaderconnect.data.dao.*
import com.shareconnect.jdownloaderconnect.data.model.*
import com.shareconnect.jdownloaderconnect.network.api.MyJDownloaderApi
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class JDownloaderRepositoryTest {

    private lateinit var repository: JDownloaderRepository
    private lateinit var mockDownloadDao: DownloadDao
    private lateinit var mockLinkGrabberDao: LinkGrabberDao
    private lateinit var mockAccountDao: AccountDao
    private lateinit var mockApi: MyJDownloaderApi

    @Before
    fun setUp() {
        mockDownloadDao = mockk()
        mockLinkGrabberDao = mockk()
        mockAccountDao = mockk()
        mockApi = mockk()
        
        repository = JDownloaderRepository(
            mockDownloadDao,
            mockLinkGrabberDao,
            mockAccountDao,
            mockApi
        )
    }

    @Test
    fun `saveAccount should deactivate all accounts and save new account`() = runTest {
        // Given
        val account = JDownloaderAccount(
            id = "test-id",
            email = "test@example.com",
            password = "password",
            deviceName = "Test Device",
            deviceId = "device-123"
        )

        coEvery { mockAccountDao.deactivateAllAccounts() } just Runs
        coEvery { mockAccountDao.insertAccount(any()) } just Runs

        // When
        repository.saveAccount(account)

        // Then
        coVerify {
            mockAccountDao.deactivateAllAccounts()
            mockAccountDao.insertAccount(account.copy(isActive = true))
        }
    }

    @Test
    fun `getActiveAccount should return flow from accountDao`() = runTest {
        // Given
        val account = JDownloaderAccount(
            id = "test-id",
            email = "test@example.com",
            password = "password",
            deviceName = "Test Device",
            deviceId = "device-123",
            isActive = true
        )
        val flow = flowOf(account)

        every { mockAccountDao.getActiveAccount() } returns flow

        // When
        val result = repository.getActiveAccount()

        // Then
        assert(result == flow)
    }

    @Test
    fun `addDownloadPackage should insert package with links`() = runTest {
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
            )
        )

        coEvery { mockDownloadDao.insertPackageWithLinks(downloadPackage, links) } just Runs

        // When
        repository.addDownloadPackage(downloadPackage, links)

        // Then
        coVerify { mockDownloadDao.insertPackageWithLinks(downloadPackage, links) }
    }

    @Test
    fun `connect should call API and save account on success`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password"
        val appKey = "ShareConnect_JDownloaderConnect"
        
        val connectResponse = ConnectResponse(
            sessionToken = "token-123",
            deviceId = "device-123",
            serverEncryptionToken = "server-token",
            deviceEncryptionToken = "device-token"
        )

        coEvery { mockApi.connect(any()) } returns Response.success(connectResponse)
        coEvery { mockAccountDao.deactivateAllAccounts() } just Runs
        coEvery { mockAccountDao.insertAccount(any()) } just Runs

        // When
        repository.connect(email, password, appKey)

        // Then
        coVerify {
            mockApi.connect(ConnectRequest(email, password, appKey))
            mockAccountDao.deactivateAllAccounts()
            mockAccountDao.insertAccount(any())
        }
    }

    @Test(expected = Exception::class)
    fun `connect should throw exception on API failure`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password"
        val appKey = "ShareConnect_JDownloaderConnect"

        coEvery { mockApi.connect(any()) } returns Response.error(400, mockk())

        // When
        repository.connect(email, password, appKey)

        // Then - Exception should be thrown
    }

    @Test
    fun `deleteDownloadPackage should delete package with links`() = runTest {
        // Given
        val packageUuid = "package-123"

        coEvery { mockDownloadDao.deletePackageWithLinks(packageUuid) } just Runs

        // When
        repository.deleteDownloadPackage(packageUuid)

        // Then
        coVerify { mockDownloadDao.deletePackageWithLinks(packageUuid) }
    }

    @Test
    fun `updateDownloadLink should update link in database`() = runTest {
        // Given
        val link = DownloadLink(
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
        )

        coEvery { mockDownloadDao.updateLink(link) } just Runs

        // When
        repository.updateDownloadLink(link)

        // Then
        coVerify { mockDownloadDao.updateLink(link) }
    }
}