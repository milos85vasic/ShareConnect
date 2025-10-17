package com.shareconnect.rsssync.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.rsssync.database.RSSDatabase
import com.shareconnect.rsssync.models.RSSFeedData
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RSSRepositoryTest {

    private lateinit var database: RSSDatabase
    private lateinit var repository: RSSRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RSSDatabase::class.java
        ).build()

        repository = RSSRepository(database.rssDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `test insertFeed saves RSS feed correctly`() = runTest {
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

        // When
        repository.insertFeed(feedData)

        // Then
        val savedFeed = repository.getFeedById("test-feed-id")
        assertNotNull(savedFeed)
        assertEquals("test-feed-id", savedFeed?.id)
        assertEquals("Test Feed", savedFeed?.title)
        assertEquals("https://example.com/feed.xml", savedFeed?.url)
    }

    @Test
    fun `test updateFeed modifies existing RSS feed`() = runTest {
        // Given
        val originalFeed = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            title = "Original Title",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertFeed(originalFeed)

        val updatedFeed = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            title = "Updated Title",
            timestamp = System.currentTimeMillis(),
            version = 2,
            lastModified = System.currentTimeMillis()
        )

        // When
        repository.updateFeed(updatedFeed)

        // Then
        val savedFeed = repository.getFeedById("test-feed-id")
        assertNotNull(savedFeed)
        assertEquals("Updated Title", savedFeed?.title)
        assertEquals(2, savedFeed?.version)
    }

    @Test
    fun `test deleteFeed removes RSS feed`() = runTest {
        // Given
        val feedData = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            title = "Test Feed",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertFeed(feedData)

        // Verify it exists
        assertNotNull(repository.getFeedById("test-feed-id"))

        // When
        repository.deleteFeed("test-feed-id")

        // Then
        val deletedFeed = repository.getFeedById("test-feed-id")
        assertNull(deletedFeed)
    }

    @Test
    fun `test getAllFeeds returns all saved RSS feeds`() = runTest {
        // Given
        val feed1 = RSSFeedData(
            id = "test-feed-id-1",
            url = "https://example1.com/feed.xml",
            title = "Feed 1",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        val feed2 = RSSFeedData(
            id = "test-feed-id-2",
            url = "https://example2.com/feed.xml",
            title = "Feed 2",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )

        repository.insertFeed(feed1)
        repository.insertFeed(feed2)

        // When
        val allFeeds = repository.getAllFeeds()

        // Then
        assertEquals(2, allFeeds.size)
        assertTrue(allFeeds.any { it.id == "test-feed-id-1" })
        assertTrue(allFeeds.any { it.id == "test-feed-id-2" })
    }

    @Test
    fun `test getFeedById returns null for non-existent id`() = runTest {
        // When
        val feed = repository.getFeedById("non-existent-id")

        // Then
        assertNull(feed)
    }

    @Test
    fun `test getFeedByUrl returns correct RSS feed`() = runTest {
        // Given
        val feedData = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            title = "Test Feed",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertFeed(feedData)

        // When
        val feed = repository.getFeedByUrl("https://example.com/feed.xml")

        // Then
        assertNotNull(feed)
        assertEquals("test-feed-id", feed?.id)
        assertEquals("Test Feed", feed?.title)
    }

    @Test
    fun `test getFeedByUrl returns null for non-existent url`() = runTest {
        // When
        val feed = repository.getFeedByUrl("https://non-existent.com/feed.xml")

        // Then
        assertNull(feed)
    }
}