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


package com.shareconnect.bookmarksync.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.bookmarksync.database.BookmarkDatabase
import com.shareconnect.bookmarksync.models.BookmarkData
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookmarkRepositoryTest {

    private lateinit var database: BookmarkDatabase
    private lateinit var repository: BookmarkRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BookmarkDatabase::class.java
        ).build()

        repository = BookmarkRepository(database.bookmarkDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `test insertBookmark saves bookmark correctly`() = runTest {
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

        // When
        repository.insertBookmark(bookmarkData)

        // Then
        val retrievedBookmark = repository.getBookmarkById("test-bookmark-id")
        assertNotNull(retrievedBookmark)
        assertEquals(bookmarkData, retrievedBookmark)
    }

    @Test
    fun `test updateBookmark modifies existing bookmark`() = runTest {
        // Given
        val originalBookmark = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "Original Title",
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
        repository.insertBookmark(originalBookmark)

        val updatedBookmark = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "Updated Title",
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

        // When
        repository.updateBookmark(updatedBookmark)

        // Then
        val savedBookmark = repository.getBookmarkById("test-bookmark-id")
        assertNotNull(savedBookmark)
        assertEquals("Updated Title", savedBookmark?.title)
        assertEquals(2, savedBookmark?.version)
    }

    @Test
    fun `test deleteBookmark removes bookmark`() = runTest {
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
        repository.insertBookmark(bookmarkData)

        // Verify it exists
        assertNotNull(repository.getBookmarkById("test-bookmark-id"))

        // When
        repository.deleteBookmark("test-bookmark-id")

        // Then
        val deletedBookmark = repository.getBookmarkById("test-bookmark-id")
        assertNull(deletedBookmark)
    }

    @Test
    fun `test getAllBookmarks returns all saved bookmarks`() = runTest {
        // Given
        val bookmark1 = BookmarkData(
            id = "test-bookmark-id-1",
            url = "https://example1.com",
            title = "Bookmark 1",
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
        val bookmark2 = BookmarkData(
            id = "test-bookmark-id-2",
            url = "https://example2.com",
            title = "Bookmark 2",
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
        repository.insertBookmark(bookmark1)
        repository.insertBookmark(bookmark2)

        // When
        val allBookmarks = repository.getAllBookmarks()

        // Then
        assertEquals(2, allBookmarks.size)
        assertTrue(allBookmarks.contains(bookmark1))
        assertTrue(allBookmarks.contains(bookmark2))
    }

    @Test
    fun `test getBookmarkById returns null for non-existent id`() = runTest {
        // When
        val bookmark = repository.getBookmarkById("non-existent-id")

        // Then
        assertNull(bookmark)
    }

    @Test
    fun `test getBookmarkByUrl returns correct bookmark`() = runTest {
        // Given
        val bookmarkData = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "Test Bookmark",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertBookmark(bookmarkData)

        // When
        val bookmark = repository.getBookmarkByUrl("https://example.com")

        // Then
        assertNotNull(bookmark)
        assertEquals("test-bookmark-id", bookmark?.id)
        assertEquals("Test Bookmark", bookmark?.title)
    }

    @Test
    fun `test getBookmarkByUrl returns null for non-existent url`() = runTest {
        // When
        val bookmark = repository.getBookmarkByUrl("https://non-existent.com")

        // Then
        assertNull(bookmark)
    }
}