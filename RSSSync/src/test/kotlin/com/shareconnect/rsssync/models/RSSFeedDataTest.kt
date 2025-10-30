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


package com.shareconnect.rsssync.models

import org.junit.Assert.*
import org.junit.Test

class RSSFeedDataTest {

    @Test
    fun `test RSSFeedData creation with all fields`() {
        // Given
        val id = "test-feed-id"
        val url = "https://example.com/feed.xml"
        val name = "Test Feed"
        val sourceApp = "TestApp"
        val autoDownload = true
        val filters = ".*torrent.*"
        val excludeFilters = ".*cam.*"
        val updateInterval = 60
        val lastUpdate = System.currentTimeMillis()
        val isEnabled = false
        val category = "Technology"
        val torrentClientType = "qbittorrent"
        val downloadPath = "/downloads"
        val version = 2

        // When
        val feedData = RSSFeedData(
            id = id,
            url = url,
            name = name,
            autoDownload = autoDownload,
            filters = filters,
            excludeFilters = excludeFilters,
            updateInterval = updateInterval,
            lastUpdate = lastUpdate,
            isEnabled = isEnabled,
            category = category,
            torrentClientType = torrentClientType,
            downloadPath = downloadPath,
            sourceApp = sourceApp,
            version = version
        )

        // Then
        assertEquals(id, feedData.id)
        assertEquals(url, feedData.url)
        assertEquals(name, feedData.name)
        assertEquals(sourceApp, feedData.sourceApp)
        assertEquals(autoDownload, feedData.autoDownload)
        assertEquals(filters, feedData.filters)
        assertEquals(excludeFilters, feedData.excludeFilters)
        assertEquals(updateInterval, feedData.updateInterval)
        assertEquals(lastUpdate, feedData.lastUpdate)
        assertEquals(isEnabled, feedData.isEnabled)
        assertEquals(category, feedData.category)
        assertEquals(torrentClientType, feedData.torrentClientType)
        assertEquals(downloadPath, feedData.downloadPath)
        assertEquals(version, feedData.version)
        assertTrue(feedData.lastModified > 0)
    }

    @Test
    fun `test RSSFeedData creation with minimal fields`() {
        // Given
        val id = "test-feed-id"
        val url = "https://example.com/feed.xml"
        val name = "Test Feed"
        val sourceApp = "TestApp"

        // When
        val feedData = RSSFeedData(
            id = id,
            url = url,
            name = name,
            sourceApp = sourceApp
        )

        // Then
        assertEquals(id, feedData.id)
        assertEquals(url, feedData.url)
        assertEquals(name, feedData.name)
        assertEquals(sourceApp, feedData.sourceApp)
        assertEquals(false, feedData.autoDownload)
        assertNull(feedData.filters)
        assertNull(feedData.excludeFilters)
        assertEquals(30, feedData.updateInterval)
        assertEquals(0L, feedData.lastUpdate)
        assertTrue(feedData.isEnabled)
        assertNull(feedData.category)
        assertNull(feedData.torrentClientType)
        assertNull(feedData.downloadPath)
        assertEquals(1, feedData.version)
        assertTrue(feedData.lastModified > 0)
    }

    @Test
    fun `test RSSFeedData equals and hashCode work correctly`() {
        // Given
        val feed1 = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            name = "Test Feed",
            sourceApp = "TestApp"
        )
        val feed2 = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            name = "Test Feed",
            sourceApp = "TestApp"
        )
        val feed3 = RSSFeedData(
            id = "different-feed-id",
            url = "https://example.com/feed.xml",
            name = "Test Feed",
            sourceApp = "TestApp"
        )

        // Then
        assertEquals(feed1, feed2)
        assertEquals(feed1.hashCode(), feed2.hashCode())
        assertNotEquals(feed1, feed3)
        assertNotEquals(feed1.hashCode(), feed3.hashCode())
    }

    @Test
    fun `test RSSFeedData toString contains relevant information`() {
        // Given
        val feedData = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            name = "Test Feed",
            sourceApp = "TestApp"
        )

        // When
        val toString = feedData.toString()

        // Then
        assertTrue(toString.contains("test-feed-id"))
        assertTrue(toString.contains("https://example.com/feed.xml"))
        assertTrue(toString.contains("Test Feed"))
    }
}