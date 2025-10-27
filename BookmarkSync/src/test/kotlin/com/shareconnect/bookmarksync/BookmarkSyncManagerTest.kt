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


package com.shareconnect.bookmarksync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.shareconnect.bookmarksync.models.BookmarkData
import com.shareconnect.bookmarksync.repository.BookmarkRepository
import digital.vasic.asinka.AsinkaClient
import digital.vasic.asinka.models.SyncableBookmark
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
import java.lang.reflect.Constructor

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

        // Use reflection to access private constructor for testing
        val constructor = BookmarkSyncManager::class.java.getDeclaredConstructor(
            Context::class.java,
            String::class.java,
            String::class.java,
            String::class.java,
            AsinkaClient::class.java,
            BookmarkRepository::class.java
        )
        constructor.isAccessible = true
        bookmarkSyncManager = constructor.newInstance(
            context,
            "test-app",
            "TestApp",
            "1.0.0",
            mockAsinkaClient,
            mockRepository
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
            thumbnailUrl = null,
            type = "website",
            category = null,
            tags = null,
            isFavorite = false,
            notes = null,
            serviceProvider = null,
            torrentHash = null,
            magnetUri = null,
            createdAt = System.currentTimeMillis(),
            lastAccessedAt = null,
            accessCount = 0,
            sourceApp = "TestApp",
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        val syncableBookmark = SyncableBookmark.fromBookmarkData(bookmarkData)

        whenever(mockRepository.getBookmarkById("test-bookmark-id")).thenReturn(null)
        whenever(mockRepository.insertBookmark(any())).then { }

        // Use reflection to access private method
        val method = BookmarkSyncManager::class.java.getDeclaredMethod("handleReceivedBookmark", SyncableBookmark::class.java)
        method.isAccessible = true

        // When
        method.invoke(bookmarkSyncManager, syncableBookmark)

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
            description = null,
            thumbnailUrl = null,
            type = "website",
            category = null,
            tags = null,
            isFavorite = false,
            notes = null,
            serviceProvider = null,
            torrentHash = null,
            magnetUri = null,
            createdAt = System.currentTimeMillis() - 1000,
            lastAccessedAt = null,
            accessCount = 0,
            sourceApp = "TestApp",
            version = 1,
            lastModified = System.currentTimeMillis() - 1000
        )
        val newBookmark = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "New Title",
            description = null,
            thumbnailUrl = null,
            type = "website",
            category = null,
            tags = null,
            isFavorite = false,
            notes = null,
            serviceProvider = null,
            torrentHash = null,
            magnetUri = null,
            createdAt = System.currentTimeMillis(),
            lastAccessedAt = null,
            accessCount = 0,
            sourceApp = "TestApp",
            version = 2,
            lastModified = System.currentTimeMillis()
        )
        val syncableBookmark = SyncableBookmark.fromBookmarkData(newBookmark)

        whenever(mockRepository.getBookmarkById("test-bookmark-id")).thenReturn(existingBookmark)
        whenever(mockRepository.updateBookmark(any())).then { }

        // Use reflection to access private method
        val method = BookmarkSyncManager::class.java.getDeclaredMethod("handleReceivedBookmark", SyncableBookmark::class.java)
        method.isAccessible = true

        // When
        method.invoke(bookmarkSyncManager, syncableBookmark)

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
            description = null,
            thumbnailUrl = null,
            type = "website",
            category = null,
            tags = null,
            isFavorite = false,
            notes = null,
            serviceProvider = null,
            torrentHash = null,
            magnetUri = null,
            createdAt = System.currentTimeMillis(),
            lastAccessedAt = null,
            accessCount = 0,
            sourceApp = "TestApp",
            version = 2,
            lastModified = System.currentTimeMillis()
        )
        val oldBookmark = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "Old Title",
            description = null,
            thumbnailUrl = null,
            type = "website",
            category = null,
            tags = null,
            isFavorite = false,
            notes = null,
            serviceProvider = null,
            torrentHash = null,
            magnetUri = null,
            createdAt = System.currentTimeMillis() - 1000,
            lastAccessedAt = null,
            accessCount = 0,
            sourceApp = "TestApp",
            version = 1,
            lastModified = System.currentTimeMillis() - 1000
        )
        val syncableBookmark = SyncableBookmark.fromBookmarkData(oldBookmark)

        whenever(mockRepository.getBookmarkById("test-bookmark-id")).thenReturn(existingBookmark)

        // Use reflection to access private method
        val method = BookmarkSyncManager::class.java.getDeclaredMethod("handleReceivedBookmark", SyncableBookmark::class.java)
        method.isAccessible = true

        // When
        method.invoke(bookmarkSyncManager, syncableBookmark)

        // Then
        verify(mockRepository, never()).updateBookmark(any())
        verify(mockRepository, never()).insertBookmark(any())
    }

    @Test
    fun `test handleDeletedBookmark removes bookmark`() = runTest {
        // Given
        whenever(mockRepository.deleteBookmark("test-bookmark-id")).then { }

        // Use reflection to access private method
        val method = BookmarkSyncManager::class.java.getDeclaredMethod("handleDeletedBookmark", String::class.java)
        method.isAccessible = true

        // When
        method.invoke(bookmarkSyncManager, "test-bookmark-id")

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
            description = null,
            thumbnailUrl = null,
            type = "website",
            category = null,
            tags = null,
            isFavorite = false,
            notes = null,
            serviceProvider = null,
            torrentHash = null,
            magnetUri = null,
            createdAt = System.currentTimeMillis(),
            lastAccessedAt = null,
            accessCount = 0,
            sourceApp = "TestApp",
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        val syncableBookmark = SyncableBookmark.fromBookmarkData(bookmarkData)

        whenever(mockRepository.getBookmarkById("test-bookmark-id")).thenReturn(null)
        whenever(mockRepository.insertBookmark(any())).then { }

        var emittedBookmark: BookmarkData? = null
        val job = kotlinx.coroutines.launch {
            bookmarkSyncManager.bookmarkChangeFlow.collect { emittedBookmark = it }
        }

        // Use reflection to access private method
        val method = BookmarkSyncManager::class.java.getDeclaredMethod("handleReceivedBookmark", SyncableBookmark::class.java)
        method.isAccessible = true

        // When
        method.invoke(bookmarkSyncManager, syncableBookmark)

        // Then
        assertEquals(bookmarkData, emittedBookmark)
        job.cancel()
    }
}