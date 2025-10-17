package com.shareconnect.torrentsharingsync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.shareconnect.torrentsharingsync.models.TorrentSharingData
import com.shareconnect.torrentsharingsync.repository.TorrentSharingRepository
import digital.vasic.asinka.AsinkaClient
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TorrentSharingSyncManagerTest {

    @Mock
    private lateinit var mockAsinkaClient: AsinkaClient

    @Mock
    private lateinit var mockRepository: TorrentSharingRepository

    private lateinit var context: Context
    private lateinit var torrentSharingSyncManager: TorrentSharingSyncManager

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = ApplicationProvider.getApplicationContext()

        // Create a test instance
        torrentSharingSyncManager = TorrentSharingSyncManager(
            context = context,
            appIdentifier = "test-app",
            appName = "TestApp",
            appVersion = "1.0.0",
            asinkaClient = mockAsinkaClient,
            repository = mockRepository
        )
    }

    @After
    fun tearDown() {
        TorrentSharingSyncManager.resetInstance()
    }

    @Test
    fun `test getInstance creates singleton instance`() = runTest {
        val instance1 = TorrentSharingSyncManager.getInstance(
            context, "test-app-1", "TestApp1", "1.0.0"
        )
        val instance2 = TorrentSharingSyncManager.getInstance(
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
        torrentSharingSyncManager.start()

        // Then
        verify(mockAsinkaClient).start()
    }

    @Test
    fun `test handleReceivedTorrentSharing saves new torrent sharing data`() = runTest {
        // Given
        val torrentSharingData = TorrentSharingData(
            id = "test-torrent-sharing-id",
            torrentName = "Test Torrent",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        whenever(mockRepository.getTorrentSharingById("test-torrent-sharing-id")).thenReturn(null)
        whenever(mockRepository.insertTorrentSharing(any())).then { }

        // When
        torrentSharingSyncManager.handleReceivedTorrentSharing(torrentSharingData)

        // Then
        verify(mockRepository).insertTorrentSharing(torrentSharingData)
    }

    @Test
    fun `test handleReceivedTorrentSharing updates existing data when newer version`() = runTest {
        // Given
        val existingData = TorrentSharingData(
            id = "test-torrent-sharing-id",
            torrentName = "Old Torrent",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis() - 1000,
            version = 1,
            lastModified = System.currentTimeMillis() - 1000
        )
        val newData = TorrentSharingData(
            id = "test-torrent-sharing-id",
            torrentName = "New Torrent",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis(),
            version = 2,
            lastModified = System.currentTimeMillis()
        )

        whenever(mockRepository.getTorrentSharingById("test-torrent-sharing-id")).thenReturn(existingData)
        whenever(mockRepository.updateTorrentSharing(any())).then { }

        // When
        torrentSharingSyncManager.handleReceivedTorrentSharing(newData)

        // Then
        verify(mockRepository).updateTorrentSharing(newData)
    }

    @Test
    fun `test handleReceivedTorrentSharing ignores older version of data`() = runTest {
        // Given
        val existingData = TorrentSharingData(
            id = "test-torrent-sharing-id",
            torrentName = "New Torrent",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis(),
            version = 2,
            lastModified = System.currentTimeMillis()
        )
        val oldData = TorrentSharingData(
            id = "test-torrent-sharing-id",
            torrentName = "Old Torrent",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis() - 1000,
            version = 1,
            lastModified = System.currentTimeMillis() - 1000
        )

        whenever(mockRepository.getTorrentSharingById("test-torrent-sharing-id")).thenReturn(existingData)

        // When
        torrentSharingSyncManager.handleReceivedTorrentSharing(oldData)

        // Then
        verify(mockRepository, never()).updateTorrentSharing(any())
        verify(mockRepository, never()).insertTorrentSharing(any())
    }

    @Test
    fun `test handleDeletedTorrentSharing removes torrent sharing data`() = runTest {
        // Given
        whenever(mockRepository.deleteTorrentSharing("test-torrent-sharing-id")).then { }

        // When
        torrentSharingSyncManager.handleDeletedTorrentSharing("test-torrent-sharing-id")

        // Then
        verify(mockRepository).deleteTorrentSharing("test-torrent-sharing-id")
    }

    @Test
    fun `test prefsChangeFlow emits when torrent sharing data is saved`() = runTest {
        // Given
        val torrentSharingData = TorrentSharingData(
            id = "test-torrent-sharing-id",
            torrentName = "Test Torrent",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )

        whenever(mockRepository.getTorrentSharingById("test-torrent-sharing-id")).thenReturn(null)
        whenever(mockRepository.insertTorrentSharing(any())).then { }

        var emittedData: TorrentSharingData? = null
        val job = kotlinx.coroutines.launch {
            torrentSharingSyncManager.prefsChangeFlow.collect { emittedData = it }
        }

        // When
        torrentSharingSyncManager.handleReceivedTorrentSharing(torrentSharingData)

        // Small delay to allow flow emission
        kotlinx.coroutines.delay(100)

        // Then
        assertNotNull(emittedData)
        assertEquals("test-torrent-sharing-id", emittedData?.id)
        assertEquals("Test Torrent", emittedData?.torrentName)
        assertEquals("abc123", emittedData?.torrentHash)

        job.cancel()
    }
}