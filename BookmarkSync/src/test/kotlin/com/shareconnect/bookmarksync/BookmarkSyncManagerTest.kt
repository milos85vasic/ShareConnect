package com.shareconnect.bookmarksync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.shareconnect.bookmarksync.models.BookmarkData
import com.shareconnect.bookmarksync.repository.BookmarkRepository
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

class BookmarkSyncManagerTest {

    @Mock
    private lateinit var mockAsinkaClient: AsinkaClient

    @Mock
    private lateinit var mockRepository: BookmarkRepository

    private lateinit var context: Context
    private lateinit var bookmarkSyncManager: BookmarkSyncManager

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = ApplicationProvider.getApplicationContext()

        // Create a test instance
        bookmarkSyncManager = BookmarkSyncManager(
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
        BookmarkSyncManager.resetInstance()
    }

    @Test
    fun `test getInstance creates singleton instance`() = runTest {
        val instance1 = BookmarkSyncManager.getInstance(
            context, "test-app-1", "TestApp1", "1.0.0"
        )
        val instance2 = BookmarkSyncManager.getInstance(
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
        bookmarkSyncManager.start()

        // Then
        verify(mockAsinkaClient).start()
    }

    @Test
    fun `test handleReceivedBookmark saves new bookmark`() = runTest {
        // Given
        val bookmarkData = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "Test Bookmark",
            description = "Test Description",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        whenever(mockRepository.getBookmarkById("test-bookmark-id")).thenReturn(null)
        whenever(mockRepository.insertBookmark(any())).then { }

        // When
        bookmarkSyncManager.handleReceivedBookmark(bookmarkData)

        // Then
        verify(mockRepository).insertBookmark(bookmarkData)
    }

    @Test
    fun `test handleReceivedBookmark updates existing bookmark when newer version`() = runTest {
        // Given
        val existingBookmark = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "Old Title",
            timestamp = System.currentTimeMillis() - 1000,
            version = 1,
            lastModified = System.currentTimeMillis() - 1000
        )
        val newBookmark = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "New Title",
            timestamp = System.currentTimeMillis(),
            version = 2,
            lastModified = System.currentTimeMillis()
        )

        whenever(mockRepository.getBookmarkById("test-bookmark-id")).thenReturn(existingBookmark)
        whenever(mockRepository.updateBookmark(any())).then { }

        // When
        bookmarkSyncManager.handleReceivedBookmark(newBookmark)

        // Then
        verify(mockRepository).updateBookmark(newBookmark)
    }

    @Test
    fun `test handleReceivedBookmark ignores older version of bookmark`() = runTest {
        // Given
        val existingBookmark = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "New Title",
            timestamp = System.currentTimeMillis(),
            version = 2,
            lastModified = System.currentTimeMillis()
        )
        val oldBookmark = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "Old Title",
            timestamp = System.currentTimeMillis() - 1000,
            version = 1,
            lastModified = System.currentTimeMillis() - 1000
        )

        whenever(mockRepository.getBookmarkById("test-bookmark-id")).thenReturn(existingBookmark)

        // When
        bookmarkSyncManager.handleReceivedBookmark(oldBookmark)

        // Then
        verify(mockRepository, never()).updateBookmark(any())
        verify(mockRepository, never()).insertBookmark(any())
    }

    @Test
    fun `test handleDeletedBookmark removes bookmark`() = runTest {
        // Given
        whenever(mockRepository.deleteBookmark("test-bookmark-id")).then { }

        // When
        bookmarkSyncManager.handleDeletedBookmark("test-bookmark-id")

        // Then
        verify(mockRepository).deleteBookmark("test-bookmark-id")
    }

    @Test
    fun `test bookmarkChangeFlow emits when bookmark is saved`() = runTest {
        // Given
        val bookmarkData = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "Test Bookmark",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )

        whenever(mockRepository.getBookmarkById("test-bookmark-id")).thenReturn(null)
        whenever(mockRepository.insertBookmark(any())).then { }

        var emittedBookmark: BookmarkData? = null
        val job = kotlinx.coroutines.launch {
            bookmarkSyncManager.bookmarkChangeFlow.collect { emittedBookmark = it }
        }

        // When
        bookmarkSyncManager.handleReceivedBookmark(bookmarkData)

        // Small delay to allow flow emission
        kotlinx.coroutines.delay(100)

        // Then
        assertNotNull(emittedBookmark)
        assertEquals("test-bookmark-id", emittedBookmark?.id)
        assertEquals("Test Bookmark", emittedBookmark?.title)

        job.cancel()
    }
}