package com.shareconnect.historysync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.historysync.database.HistoryDatabase
import com.shareconnect.historysync.models.HistoryData
import com.shareconnect.historysync.repository.HistoryRepository
import digital.vasic.asinka.AsinkaClient
import digital.vasic.asinka.AsinkaConfig
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class HistorySyncManagerTest {

    @Mock
    private lateinit var mockAsinkaClient: AsinkaClient

    @Mock
    private lateinit var mockRepository: HistoryRepository

    private lateinit var context: Context
    private lateinit var historySyncManager: HistorySyncManager

@Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = ApplicationProvider.getApplicationContext()
        historySyncManager = HistorySyncManager.getInstance(
            context = context,
            appId = "test-app",
            appName = "Test App",
            appVersion = "1.0.0"
        )
    }

    @After
    fun tearDown() {
        HistorySyncManager.resetInstance()
    }

    @Test
    fun `test getInstance creates singleton instance`() = runTest {
        val instance1 = HistorySyncManager.getInstance(
            context, "test-app-1", "TestApp1", "1.0.0"
        )
        val instance2 = HistorySyncManager.getInstance(
            context, "test-app-2", "TestApp2", "1.0.0"
        )

        assertNotNull(instance1)
        assertNotNull(instance2)
        assertNotEquals(instance1, instance2)
    }

    @Test
    fun `test start initializes sync manager correctly`() = runTest {
        // Given
        whenever(mockAsinkaClient.start()).then { }

        // When
        historySyncManager.start()

        // Then
        verify(mockAsinkaClient).start()
    }

    @Test
    fun `test handleReceivedHistory saves new history item`() = runTest {
        // Given
        val historyData = HistoryData(
            id = "test-id",
            url = "https://example.com",
            title = "Test Title",
            description = "Test Description",
            thumbnailUrl = null,
            serviceProvider = "TestProvider",
            type = "video",
            timestamp = System.currentTimeMillis(),
            profileId = "test-profile",
            profileName = "Test Profile",
            isSentSuccessfully = false,
            serviceType = "test-service",
            torrentClientType = null,
            sourceApp = "TestApp",
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        whenever(mockRepository.getHistoryById("test-id")).thenReturn(null)
        whenever(mockRepository.insertHistory(any())).then { }

        // When
        historySyncManager.handleReceivedHistory(historyData)

        // Then
        verify(mockRepository).insertHistory(historyData)
    }

    @Test
    fun `test handleReceivedHistory updates existing history item when newer version`() = runTest {
        // Given
        val existingHistory = HistoryData(
            id = "test-id",
            url = "https://example.com",
            title = "Old Title",
            description = null,
            thumbnailUrl = null,
            serviceProvider = "TestProvider",
            type = "video",
            timestamp = System.currentTimeMillis() - 1000,
            profileId = "test-profile",
            profileName = "Test Profile",
            isSentSuccessfully = false,
            serviceType = "test-service",
            torrentClientType = null,
            sourceApp = "TestApp",
            version = 1,
            lastModified = System.currentTimeMillis() - 1000
        )
        val newHistory = HistoryData(
            id = "test-id",
            url = "https://example.com",
            title = "New Title",
            description = null,
            thumbnailUrl = null,
            serviceProvider = "TestProvider",
            type = "video",
            timestamp = System.currentTimeMillis(),
            profileId = "test-profile",
            profileName = "Test Profile",
            isSentSuccessfully = false,
            serviceType = "test-service",
            torrentClientType = null,
            sourceApp = "TestApp",
            version = 2,
            lastModified = System.currentTimeMillis()
        )

        whenever(mockRepository.getHistoryById("test-id")).thenReturn(existingHistory)
        whenever(mockRepository.updateHistory(any())).then { }

        // When
        historySyncManager.handleReceivedHistory(newHistory)

        // Then
        verify(mockRepository).updateHistory(newHistory)
    }

    @Test
    fun `test handleReceivedHistory ignores older version of history item`() = runTest {
        // Given
        val existingHistory = HistoryData(
            id = "test-id",
            url = "https://example.com",
            title = "New Title",
            description = null,
            thumbnailUrl = null,
            serviceProvider = "TestProvider",
            type = "video",
            timestamp = System.currentTimeMillis(),
            profileId = "test-profile",
            profileName = "Test Profile",
            isSentSuccessfully = false,
            serviceType = "test-service",
            torrentClientType = null,
            sourceApp = "TestApp",
            version = 2,
            lastModified = System.currentTimeMillis()
        )
        val oldHistory = HistoryData(
            id = "test-id",
            url = "https://example.com",
            title = "Old Title",
            description = null,
            thumbnailUrl = null,
            serviceProvider = "TestProvider",
            type = "video",
            timestamp = System.currentTimeMillis() - 1000,
            profileId = "test-profile",
            profileName = "Test Profile",
            isSentSuccessfully = false,
            serviceType = "test-service",
            torrentClientType = null,
            sourceApp = "TestApp",
            version = 1,
            lastModified = System.currentTimeMillis() - 1000
        )

        whenever(mockRepository.getHistoryById("test-id")).thenReturn(existingHistory)

        // When
        historySyncManager.handleReceivedHistory(oldHistory)

        // Then
        verify(mockRepository, never()).updateHistory(any())
        verify(mockRepository, never()).insertHistory(any())
    }

    @Test
    fun `test handleDeletedHistory removes history item`() = runTest {
        // Given
        whenever(mockRepository.deleteHistory("test-id")).then { }

        // When
        historySyncManager.handleDeletedHistory("test-id")

        // Then
        verify(mockRepository).deleteHistory("test-id")
    }

    @Test
    fun `test historyChangeFlow emits when history is saved`() = runTest {
        // Given
        val historyData = HistoryData(
            id = "test-id",
            url = "https://example.com",
            title = "Test Title",
            description = null,
            thumbnailUrl = null,
            serviceProvider = "TestProvider",
            type = "video",
            timestamp = System.currentTimeMillis(),
            profileId = "test-profile",
            profileName = "Test Profile",
            isSentSuccessfully = false,
            serviceType = "test-service",
            torrentClientType = null,
            sourceApp = "TestApp",
            version = 1,
            lastModified = System.currentTimeMillis()
        )

        whenever(mockRepository.getHistoryById("test-id")).thenReturn(null)
        whenever(mockRepository.insertHistory(any())).then { }

        var emittedHistory: HistoryData? = null
        val job = kotlinx.coroutines.launch {
            historySyncManager.historyChangeFlow.collect { emittedHistory = it }
        }

        // When
        historySyncManager.handleReceivedHistory(historyData)

        // Small delay to allow flow emission
        kotlinx.coroutines.delay(100)

        // Then
        assertNotNull(emittedHistory)
        assertEquals("test-id", emittedHistory?.id)
        assertEquals("Test Title", emittedHistory?.title)

        job.cancel()
    }
}