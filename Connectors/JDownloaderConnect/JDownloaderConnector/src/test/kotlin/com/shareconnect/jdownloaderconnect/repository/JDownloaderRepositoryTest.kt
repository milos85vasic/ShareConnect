package com.shareconnect.jdownloaderconnect.data.repository

import com.shareconnect.jdownloaderconnect.data.dao.*
import com.shareconnect.jdownloaderconnect.data.model.*
import com.shareconnect.jdownloaderconnect.domain.model.*
import com.shareconnect.jdownloaderconnect.network.api.MyJDownloaderApi
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JDownloaderRepositoryTest {

    private lateinit var repository: JDownloaderRepository
    private lateinit var mockDownloadDao: DownloadDao
    private lateinit var mockLinkGrabberDao: LinkGrabberDao
    private lateinit var mockAccountDao: AccountDao
    private lateinit var mockApi: MyJDownloaderApi

    @Before
    fun setUp() {
        mockAccountDao = mockk()
        mockDownloadDao = mockk()
        mockLinkGrabberDao = mockk()
        mockApi = mockk()
        
        repository = JDownloaderRepository(
            mockAccountDao,
            mockDownloadDao,
            mockLinkGrabberDao,
            mockApi
        )
    }

    @Test
    fun `saveAccount should insert account`() = runTest {
        // Given
        val account = JDownloaderAccount(
            id = "test-id",
            email = "test@example.com",
            password = "password",
            deviceName = "Test Device",
            deviceId = "device-123"
        )

        coEvery { mockAccountDao.insertAccount(account) } just Runs

        // When
        repository.saveAccount(account)

        // Then
        coVerify { mockAccountDao.insertAccount(account) }
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
    fun `setActiveAccount should deactivate all accounts and save new account`() = runTest {
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
        repository.setActiveAccount(account)

        // Then
        coVerify {
            mockAccountDao.deactivateAllAccounts()
            mockAccountDao.insertAccount(account.copy(isActive = true))
        }
    }

    @Test
    fun `connectAccount should return device connection`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password"
        val deviceName = "Test Device"

        // When
        val result = repository.connectAccount(email, password, deviceName)

        // Then
        assert(result.deviceId == "mock_device_id")
        assert(result.sessionToken == "mock_session_token")
    }

    @Test
    fun `getAllAccounts should return flow from accountDao`() = runTest {
        // Given
        val accounts = listOf(
            JDownloaderAccount(
                id = "test-id-1",
                email = "test1@example.com",
                password = "password",
                deviceName = "Test Device 1",
                deviceId = "device-123"
            ),
            JDownloaderAccount(
                id = "test-id-2",
                email = "test2@example.com",
                password = "password",
                deviceName = "Test Device 2",
                deviceId = "device-456"
            )
        )
        val flow = flowOf(accounts)

        every { mockAccountDao.getAllAccounts() } returns flow

        // When
        val result = repository.getAllAccounts()

        // Then
        assert(result == flow)
    }

    @Test
    fun `deleteAccount should delete account from dao`() = runTest {
        // Given
        val account = JDownloaderAccount(
            id = "test-id",
            email = "test@example.com",
            password = "password",
            deviceName = "Test Device",
            deviceId = "device-123"
        )

        coEvery { mockAccountDao.deleteAccount(account) } just Runs

        // When
        repository.deleteAccount(account)

        // Then
        coVerify { mockAccountDao.deleteAccount(account) }
    }
}