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


package com.shareconnect.historysync.models

import org.junit.Assert.*
import org.junit.Test

class HistoryDataTest {

    @Test
    fun `test HistoryData creation with all fields`() {
        // Given
        val id = "test-id"
        val url = "https://example.com"
        val title = "Test Title"
        val description = "Test Description"
        val thumbnailUrl = "https://example.com/thumb.jpg"
        val serviceProvider = "YouTube"
        val type = "video"
        val timestamp = System.currentTimeMillis()
        val profileId = "profile-123"
        val profileName = "Test Profile"
        val isSentSuccessfully = true
        val serviceType = "streaming"
        val torrentClientType = "qbittorrent"
        val sourceApp = "ShareConnector"
        val version = 1
        val lastModified = System.currentTimeMillis()
        val fileSize = 1024L
        val duration = 300
        val quality = "1080p"
        val downloadPath = "/downloads/test.mp4"
        val torrentHash = "abc123"
        val magnetUri = "magnet:?xt=urn:btih:abc123"
        val category = "Entertainment"
        val tags = "test,video"

        // When
        val historyData = HistoryData(
            id = id,
            url = url,
            title = title,
            description = description,
            thumbnailUrl = thumbnailUrl,
            serviceProvider = serviceProvider,
            type = type,
            timestamp = timestamp,
            profileId = profileId,
            profileName = profileName,
            isSentSuccessfully = isSentSuccessfully,
            serviceType = serviceType,
            torrentClientType = torrentClientType,
            sourceApp = sourceApp,
            version = version,
            lastModified = lastModified,
            fileSize = fileSize,
            duration = duration,
            quality = quality,
            downloadPath = downloadPath,
            torrentHash = torrentHash,
            magnetUri = magnetUri,
            category = category,
            tags = tags
        )

        // Then
        assertEquals(id, historyData.id)
        assertEquals(url, historyData.url)
        assertEquals(title, historyData.title)
        assertEquals(description, historyData.description)
        assertEquals(thumbnailUrl, historyData.thumbnailUrl)
        assertEquals(serviceProvider, historyData.serviceProvider)
        assertEquals(type, historyData.type)
        assertEquals(timestamp, historyData.timestamp)
        assertEquals(profileId, historyData.profileId)
        assertEquals(profileName, historyData.profileName)
        assertEquals(isSentSuccessfully, historyData.isSentSuccessfully)
        assertEquals(serviceType, historyData.serviceType)
        assertEquals(torrentClientType, historyData.torrentClientType)
        assertEquals(sourceApp, historyData.sourceApp)
        assertEquals(version, historyData.version)
        assertEquals(lastModified, historyData.lastModified)
        assertEquals(fileSize, historyData.fileSize)
        assertEquals(duration, historyData.duration)
        assertEquals(quality, historyData.quality)
        assertEquals(downloadPath, historyData.downloadPath)
        assertEquals(torrentHash, historyData.torrentHash)
        assertEquals(magnetUri, historyData.magnetUri)
        assertEquals(category, historyData.category)
        assertEquals(tags, historyData.tags)
    }

    @Test
    fun `test HistoryData creation with minimal fields`() {
        // Given
        val id = "test-id"
        val url = "https://example.com"
        val title = "Test Title"
        val timestamp = System.currentTimeMillis()

        // When
        val historyData = HistoryData(
            id = id,
            url = url,
            title = title,
            description = null,
            thumbnailUrl = null,
            serviceProvider = null,
            type = "video",
            timestamp = timestamp,
            profileId = null,
            profileName = null,
            isSentSuccessfully = false,
            serviceType = "test",
            torrentClientType = null,
            sourceApp = "TestApp",
            version = 0,
            lastModified = 0L
        )

        // Then
        assertEquals(id, historyData.id)
        assertEquals(url, historyData.url)
        assertEquals(title, historyData.title)
        assertEquals(timestamp, historyData.timestamp)
        assertNull(historyData.description)
        assertNull(historyData.thumbnailUrl)
        assertNull(historyData.serviceProvider)
        assertEquals("video", historyData.type)
        assertNull(historyData.profileId)
        assertNull(historyData.profileName)
        assertFalse(historyData.isSentSuccessfully)
        assertEquals("test", historyData.serviceType)
        assertNull(historyData.torrentClientType)
        assertEquals("TestApp", historyData.sourceApp)
        assertEquals(0, historyData.version)
        assertEquals(0L, historyData.lastModified)
        assertNull(historyData.fileSize)
        assertNull(historyData.duration)
        assertNull(historyData.quality)
        assertNull(historyData.downloadPath)
        assertNull(historyData.torrentHash)
        assertNull(historyData.magnetUri)
        assertNull(historyData.category)
        assertNull(historyData.tags)
    }

    @Test
    fun `test HistoryData equals and hashCode work correctly`() {
        // Given
        val history1 = HistoryData(
            id = "test-id",
            url = "https://example.com",
            title = "Test Title",
            description = null,
            thumbnailUrl = null,
            serviceProvider = null,
            type = "video",
            timestamp = System.currentTimeMillis(),
            profileId = null,
            profileName = null,
            isSentSuccessfully = false,
            serviceType = "test",
            torrentClientType = null,
            sourceApp = "TestApp"
        )
        val history2 = HistoryData(
            id = "test-id",
            url = "https://example.com",
            title = "Test Title",
            description = null,
            thumbnailUrl = null,
            serviceProvider = null,
            type = "video",
            timestamp = System.currentTimeMillis(),
            profileId = null,
            profileName = null,
            isSentSuccessfully = false,
            serviceType = "test",
            torrentClientType = null,
            sourceApp = "TestApp"
        )
        val history3 = HistoryData(
            id = "different-id",
            url = "https://example.com",
            title = "Test Title",
            description = null,
            thumbnailUrl = null,
            serviceProvider = null,
            type = "video",
            timestamp = System.currentTimeMillis(),
            profileId = null,
            profileName = null,
            isSentSuccessfully = false,
            serviceType = "test",
            torrentClientType = null,
            sourceApp = "TestApp"
        )

        // Then
        assertEquals(history1, history2)
        assertEquals(history1.hashCode(), history2.hashCode())
        assertNotEquals(history1, history3)
        assertNotEquals(history1.hashCode(), history3.hashCode())
    }

    @Test
    fun `test HistoryData toString contains relevant information`() {
        // Given
        val historyData = HistoryData(
            id = "test-id",
            url = "https://example.com",
            title = "Test Title",
            description = null,
            thumbnailUrl = null,
            serviceProvider = null,
            type = "video",
            timestamp = System.currentTimeMillis(),
            profileId = null,
            profileName = null,
            isSentSuccessfully = false,
            serviceType = "test",
            torrentClientType = null,
            sourceApp = "TestApp"
        )

        // When
        val toString = historyData.toString()

        // Then
        assertTrue(toString.contains("test-id"))
        assertTrue(toString.contains("https://example.com"))
        assertTrue(toString.contains("Test Title"))
    }
}