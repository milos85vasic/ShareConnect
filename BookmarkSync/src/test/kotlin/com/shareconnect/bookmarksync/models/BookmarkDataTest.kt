package com.shareconnect.bookmarksync.models

import org.junit.Assert.*
import org.junit.Test

class BookmarkDataTest {

    @Test
    fun `test BookmarkData creation with all fields`() {
        // Given
        val id = "test-bookmark-id"
        val url = "https://example.com"
        val title = "Test Bookmark"
        val description = "Test Description"
        val thumbnailUrl = "https://example.com/thumb.jpg"
        val faviconUrl = "https://example.com/favicon.ico"
        val tags = "test,bookmark"
        val folder = "Test Folder"
        val isRead = true
        val isFavorite = false
        val timestamp = System.currentTimeMillis()
        val version = 1
        val lastModified = System.currentTimeMillis()

        // When
        val bookmarkData = BookmarkData(
            id = id,
            url = url,
            title = title,
            description = description,
            thumbnailUrl = thumbnailUrl,
            faviconUrl = faviconUrl,
            tags = tags,
            folder = folder,
            isRead = isRead,
            isFavorite = isFavorite,
            timestamp = timestamp,
            version = version,
            lastModified = lastModified
        )

        // Then
        assertEquals(id, bookmarkData.id)
        assertEquals(url, bookmarkData.url)
        assertEquals(title, bookmarkData.title)
        assertEquals(description, bookmarkData.description)
        assertEquals(thumbnailUrl, bookmarkData.thumbnailUrl)
        assertEquals(faviconUrl, bookmarkData.faviconUrl)
        assertEquals(tags, bookmarkData.tags)
        assertEquals(folder, bookmarkData.folder)
        assertEquals(isRead, bookmarkData.isRead)
        assertEquals(isFavorite, bookmarkData.isFavorite)
        assertEquals(timestamp, bookmarkData.timestamp)
        assertEquals(version, bookmarkData.version)
        assertEquals(lastModified, bookmarkData.lastModified)
    }

    @Test
    fun `test BookmarkData creation with minimal fields`() {
        // Given
        val id = "test-bookmark-id"
        val url = "https://example.com"
        val title = "Test Bookmark"
        val timestamp = System.currentTimeMillis()

        // When
        val bookmarkData = BookmarkData(
            id = id,
            url = url,
            title = title,
            timestamp = timestamp
        )

        // Then
        assertEquals(id, bookmarkData.id)
        assertEquals(url, bookmarkData.url)
        assertEquals(title, bookmarkData.title)
        assertEquals(timestamp, bookmarkData.timestamp)
        assertNull(bookmarkData.description)
        assertNull(bookmarkData.thumbnailUrl)
        assertNull(bookmarkData.faviconUrl)
        assertNull(bookmarkData.tags)
        assertNull(bookmarkData.folder)
        assertFalse(bookmarkData.isRead)
        assertFalse(bookmarkData.isFavorite)
        assertEquals(0, bookmarkData.version)
        assertEquals(0L, bookmarkData.lastModified)
    }

    @Test
    fun `test BookmarkData equals and hashCode work correctly`() {
        // Given
        val bookmark1 = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "Test Bookmark",
            timestamp = System.currentTimeMillis()
        )
        val bookmark2 = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "Test Bookmark",
            timestamp = System.currentTimeMillis()
        )
        val bookmark3 = BookmarkData(
            id = "different-bookmark-id",
            url = "https://example.com",
            title = "Test Bookmark",
            timestamp = System.currentTimeMillis()
        )

        // Then
        assertEquals(bookmark1, bookmark2)
        assertEquals(bookmark1.hashCode(), bookmark2.hashCode())
        assertNotEquals(bookmark1, bookmark3)
        assertNotEquals(bookmark1.hashCode(), bookmark3.hashCode())
    }

    @Test
    fun `test BookmarkData toString contains relevant information`() {
        // Given
        val bookmarkData = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "Test Bookmark",
            timestamp = System.currentTimeMillis()
        )

        // When
        val toString = bookmarkData.toString()

        // Then
        assertTrue(toString.contains("test-bookmark-id"))
        assertTrue(toString.contains("https://example.com"))
        assertTrue(toString.contains("Test Bookmark"))
    }
}