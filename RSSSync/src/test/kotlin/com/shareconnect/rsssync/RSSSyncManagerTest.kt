package com.shareconnect.rsssync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.shareconnect.rsssync.models.RSSFeedData
import com.shareconnect.rsssync.repository.RSSRepository
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

class RSSSyncManagerTest {

    @Mock
    private lateinit var mockAsinkaClient: AsinkaClient

    @Mock
    private lateinit var mockRepository: RSSRepository

    private lateinit var context: Context
    private lateinit var rssSyncManager: RSSSyncManager

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = ApplicationProvider.getApplicationContext()

        // Create a test instance
        rssSyncManager = RSSSyncManager(
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
        RSSSyncManager.resetInstance()
    }

    @Test
    fun `test getInstance creates singleton instance`() = runTest {
        val instance1 = RSSSyncManager.getInstance(
            context, "test-app-1", "TestApp1", "1.0.0"
        )
        val instance2 = RSSSyncManager.getInstance(
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
        rssSyncManager.start()

        // Then
        verify(mockAsinkaClient).start()
    }

    @Test
    fun `test handleReceivedFeed saves new RSS feed`() = runTest {
        // Given
        val feedData = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            title = "Test Feed",
            description = "Test RSS Feed",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        whenever(mockRepository.getFeedById("test-feed-id")).thenReturn(null)
        whenever(mockRepository.insertFeed(any())).then { }

        // When
        rssSyncManager.handleReceivedFeed(feedData)

        // Then
        verify(mockRepository).insertFeed(feedData)
    }

    @Test
    fun `test handleReceivedFeed updates existing feed when newer version`() = runTest {
        // Given
        val existingFeed = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            title = "Old Title",
            timestamp = System.currentTimeMillis() - 1000,
            version = 1,
            lastModified = System.currentTimeMillis() - 1000
        )
        val newFeed = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            title = "New Title",
            timestamp = System.currentTimeMillis(),
            version = 2,
            lastModified = System.currentTimeMillis()
        )

        whenever(mockRepository.getFeedById("test-feed-id")).thenReturn(existingFeed)
        whenever(mockRepository.updateFeed(any())).then { }

        // When
        rssSyncManager.handleReceivedFeed(newFeed)

        // Then
        verify(mockRepository).updateFeed(newFeed)
    }

    @Test
    fun `test handleReceivedFeed ignores older version of feed`() = runTest {
        // Given
        val existingFeed = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            title = "New Title",
            timestamp = System.currentTimeMillis(),
            version = 2,
            lastModified = System.currentTimeMillis()
        )
        val oldFeed = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            title = "Old Title",
            timestamp = System.currentTimeMillis() - 1000,
            version = 1,
            lastModified = System.currentTimeMillis() - 1000
        )

        whenever(mockRepository.getFeedById("test-feed-id")).thenReturn(existingFeed)

        // When
        rssSyncManager.handleReceivedFeed(oldFeed)

        // Then
        verify(mockRepository, never()).updateFeed(any())
        verify(mockRepository, never()).insertFeed(any())
    }

    @Test
    fun `test handleDeletedFeed removes RSS feed`() = runTest {
        // Given
        whenever(mockRepository.deleteFeed("test-feed-id")).then { }

        // When
        rssSyncManager.handleDeletedFeed("test-feed-id")

        // Then
        verify(mockRepository).deleteFeed("test-feed-id")
    }

    @Test
    fun `test feedChangeFlow emits when feed is saved`() = runTest {
        // Given
        val feedData = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            title = "Test Feed",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )

        whenever(mockRepository.getFeedById("test-feed-id")).thenReturn(null)
        whenever(mockRepository.insertFeed(any())).then { }

        var emittedFeed: RSSFeedData? = null
        val job = kotlinx.coroutines.launch {
            rssSyncManager.feedChangeFlow.collect { emittedFeed = it }
        }

        // When
        rssSyncManager.handleReceivedFeed(feedData)

        // Small delay to allow flow emission
        kotlinx.coroutines.delay(100)

        // Then
        assertNotNull(emittedFeed)
        assertEquals("test-feed-id", emittedFeed?.id)
        assertEquals("Test Feed", emittedFeed?.title)

        job.cancel()
    }
}