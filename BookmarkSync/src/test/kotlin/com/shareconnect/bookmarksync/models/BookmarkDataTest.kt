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
        val type = "website"
        val category = "test"
        val tags = "test,bookmark"
        val isFavorite = false
        val notes = "Test notes"
        val serviceProvider = "TestProvider"
        val torrentHash = null
        val magnetUri = null
        val createdAt = System.currentTimeMillis()
        val lastAccessedAt = null
        val accessCount = 0
        val sourceApp = "TestApp"
        val version = 1
        val lastModified = System.currentTimeMillis()

        // When
        val bookmarkData = BookmarkData(
            id = id,
            url = url,
            title = title,
            description = description,
            thumbnailUrl = thumbnailUrl,
            type = type,
            category = category,
            tags = tags,
            isFavorite = isFavorite,
            notes = notes,
            serviceProvider = serviceProvider,
            torrentHash = torrentHash,
            magnetUri = magnetUri,
            createdAt = createdAt,
            lastAccessedAt = lastAccessedAt,
            accessCount = accessCount,
            sourceApp = sourceApp,
            version = version,
            lastModified = lastModified
        )

        // Then
        assertEquals(id, bookmarkData.id)
        assertEquals(url, bookmarkData.url)
        assertEquals(title, bookmarkData.title)
        assertEquals(description, bookmarkData.description)
        assertEquals(thumbnailUrl, bookmarkData.thumbnailUrl)
        assertEquals(type, bookmarkData.type)
        assertEquals(category, bookmarkData.category)
        assertEquals(tags, bookmarkData.tags)
        assertEquals(isFavorite, bookmarkData.isFavorite)
        assertEquals(notes, bookmarkData.notes)
        assertEquals(serviceProvider, bookmarkData.serviceProvider)
        assertEquals(torrentHash, bookmarkData.torrentHash)
        assertEquals(magnetUri, bookmarkData.magnetUri)
        assertEquals(createdAt, bookmarkData.createdAt)
        assertEquals(lastAccessedAt, bookmarkData.lastAccessedAt)
        assertEquals(accessCount, bookmarkData.accessCount)
        assertEquals(sourceApp, bookmarkData.sourceApp)
        assertEquals(version, bookmarkData.version)
        assertEquals(lastModified, bookmarkData.lastModified)
    }

    @Test
    fun `test BookmarkData creation with minimal fields`() {
        // Given
        val id = "test-bookmark-id"
        val url = "https://example.com"
        val title = "Test Bookmark"
        val type = "website"
        val sourceApp = "TestApp"
        val createdAt = System.currentTimeMillis()

        // When
        val bookmarkData = BookmarkData(
            id = id,
            url = url,
            title = title,
            type = type,
            sourceApp = sourceApp,
            createdAt = createdAt
        )

        // Then
        assertEquals(id, bookmarkData.id)
        assertEquals(url, bookmarkData.url)
        assertEquals(title, bookmarkData.title)
        assertEquals(type, bookmarkData.type)
        assertEquals(sourceApp, bookmarkData.sourceApp)
        assertEquals(createdAt, bookmarkData.createdAt)
        assertNull(bookmarkData.description)
        assertNull(bookmarkData.thumbnailUrl)
        assertNull(bookmarkData.category)
        assertNull(bookmarkData.tags)
        assertFalse(bookmarkData.isFavorite)
        assertNull(bookmarkData.notes)
        assertNull(bookmarkData.serviceProvider)
        assertNull(bookmarkData.torrentHash)
        assertNull(bookmarkData.magnetUri)
        assertNull(bookmarkData.lastAccessedAt)
        assertEquals(0, bookmarkData.accessCount)
        assertEquals(1, bookmarkData.version)
        assertEquals(createdAt, bookmarkData.lastModified)
    }

    @Test
    fun `test BookmarkData equals and hashCode work correctly`() {
        // Given
        val timestamp = System.currentTimeMillis()
        val bookmark1 = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "Test Bookmark",
            type = "website",
            sourceApp = "TestApp",
            createdAt = timestamp
        )
        val bookmark2 = BookmarkData(
            id = "test-bookmark-id",
            url = "https://example.com",
            title = "Test Bookmark",
            type = "website",
            sourceApp = "TestApp",
            createdAt = timestamp
        )
        val bookmark3 = BookmarkData(
            id = "different-bookmark-id",
            url = "https://example.com",
            title = "Test Bookmark",
            type = "website",
            sourceApp = "TestApp",
            createdAt = timestamp
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
            type = "website",
            sourceApp = "TestApp",
            createdAt = System.currentTimeMillis()
        )

        // When
        val toString = bookmarkData.toString()

        // Then
        assertTrue(toString.contains("test-bookmark-id"))
        assertTrue(toString.contains("https://example.com"))
        assertTrue(toString.contains("Test Bookmark"))
    }
}