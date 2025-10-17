package com.shareconnect.torrentsharingsync.models

import org.junit.Assert.*
import org.junit.Test

class TorrentSharingDataTest {

    @Test
    fun `test TorrentSharingData creation with all fields`() {
        // Given
        val id = "test-torrent-sharing-id"
        val torrentName = "Test Torrent"
        val torrentHash = "abc123"
        val magnetUri = "magnet:?xt=urn:btih:abc123"
        val torrentUrl = "https://example.com/torrent.torrent"
        val fileSize = 1024L
        val fileCount = 5
        val category = "Movies"
        val tags = "action,adventure"
        val description = "Test torrent description"
        val source = "ShareConnector"
        val isPrivate = false
        val isSeeded = true
        val seedCount = 10
        val peerCount = 5
        val downloadSpeed = 1024L
        val uploadSpeed = 512L
        val eta = 3600L
        val progress = 75.5f
        val ratio = 1.2f
        val downloaded = 768L
        val uploaded = 512L
        val availability = 2.5f
        val timestamp = System.currentTimeMillis()
        val version = 1
        val lastModified = System.currentTimeMillis()

        // When
        val torrentSharingData = TorrentSharingData(
            id = id,
            torrentName = torrentName,
            torrentHash = torrentHash,
            magnetUri = magnetUri,
            torrentUrl = torrentUrl,
            fileSize = fileSize,
            fileCount = fileCount,
            category = category,
            tags = tags,
            description = description,
            source = source,
            isPrivate = isPrivate,
            isSeeded = isSeeded,
            seedCount = seedCount,
            peerCount = peerCount,
            downloadSpeed = downloadSpeed,
            uploadSpeed = uploadSpeed,
            eta = eta,
            progress = progress,
            ratio = ratio,
            downloaded = downloaded,
            uploaded = uploaded,
            availability = availability,
            timestamp = timestamp,
            version = version,
            lastModified = lastModified
        )

        // Then
        assertEquals(id, torrentSharingData.id)
        assertEquals(torrentName, torrentSharingData.torrentName)
        assertEquals(torrentHash, torrentSharingData.torrentHash)
        assertEquals(magnetUri, torrentSharingData.magnetUri)
        assertEquals(torrentUrl, torrentSharingData.torrentUrl)
        assertEquals(fileSize, torrentSharingData.fileSize)
        assertEquals(fileCount, torrentSharingData.fileCount)
        assertEquals(category, torrentSharingData.category)
        assertEquals(tags, torrentSharingData.tags)
        assertEquals(description, torrentSharingData.description)
        assertEquals(source, torrentSharingData.source)
        assertEquals(isPrivate, torrentSharingData.isPrivate)
        assertEquals(isSeeded, torrentSharingData.isSeeded)
        assertEquals(seedCount, torrentSharingData.seedCount)
        assertEquals(peerCount, torrentSharingData.peerCount)
        assertEquals(downloadSpeed, torrentSharingData.downloadSpeed)
        assertEquals(uploadSpeed, torrentSharingData.uploadSpeed)
        assertEquals(eta, torrentSharingData.eta)
        assertEquals(progress, torrentSharingData.progress)
        assertEquals(ratio, torrentSharingData.ratio)
        assertEquals(downloaded, torrentSharingData.downloaded)
        assertEquals(uploaded, torrentSharingData.uploaded)
        assertEquals(availability, torrentSharingData.availability)
        assertEquals(timestamp, torrentSharingData.timestamp)
        assertEquals(version, torrentSharingData.version)
        assertEquals(lastModified, torrentSharingData.lastModified)
    }

    @Test
    fun `test TorrentSharingData creation with minimal fields`() {
        // Given
        val id = "test-torrent-sharing-id"
        val torrentName = "Test Torrent"
        val torrentHash = "abc123"
        val magnetUri = "magnet:?xt=urn:btih:abc123"
        val timestamp = System.currentTimeMillis()

        // When
        val torrentSharingData = TorrentSharingData(
            id = id,
            torrentName = torrentName,
            torrentHash = torrentHash,
            magnetUri = magnetUri,
            timestamp = timestamp
        )

        // Then
        assertEquals(id, torrentSharingData.id)
        assertEquals(torrentName, torrentSharingData.torrentName)
        assertEquals(torrentHash, torrentSharingData.torrentHash)
        assertEquals(magnetUri, torrentSharingData.magnetUri)
        assertEquals(timestamp, torrentSharingData.timestamp)
        assertNull(torrentSharingData.torrentUrl)
        assertEquals(0L, torrentSharingData.fileSize)
        assertEquals(0, torrentSharingData.fileCount)
        assertNull(torrentSharingData.category)
        assertNull(torrentSharingData.tags)
        assertNull(torrentSharingData.description)
        assertNull(torrentSharingData.source)
        assertFalse(torrentSharingData.isPrivate)
        assertFalse(torrentSharingData.isSeeded)
        assertEquals(0, torrentSharingData.seedCount)
        assertEquals(0, torrentSharingData.peerCount)
        assertEquals(0L, torrentSharingData.downloadSpeed)
        assertEquals(0L, torrentSharingData.uploadSpeed)
        assertEquals(0L, torrentSharingData.eta)
        assertEquals(0.0f, torrentSharingData.progress)
        assertEquals(0.0f, torrentSharingData.ratio)
        assertEquals(0L, torrentSharingData.downloaded)
        assertEquals(0L, torrentSharingData.uploaded)
        assertEquals(0.0f, torrentSharingData.availability)
        assertEquals(0, torrentSharingData.version)
        assertEquals(0L, torrentSharingData.lastModified)
    }

    @Test
    fun `test TorrentSharingData equals and hashCode work correctly`() {
        // Given
        val data1 = TorrentSharingData(
            id = "test-torrent-sharing-id",
            torrentName = "Test Torrent",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis()
        )
        val data2 = TorrentSharingData(
            id = "test-torrent-sharing-id",
            torrentName = "Test Torrent",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis()
        )
        val data3 = TorrentSharingData(
            id = "different-torrent-sharing-id",
            torrentName = "Test Torrent",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis()
        )

        // Then
        assertEquals(data1, data2)
        assertEquals(data1.hashCode(), data2.hashCode())
        assertNotEquals(data1, data3)
        assertNotEquals(data1.hashCode(), data3.hashCode())
    }

    @Test
    fun `test TorrentSharingData toString contains relevant information`() {
        // Given
        val torrentSharingData = TorrentSharingData(
            id = "test-torrent-sharing-id",
            torrentName = "Test Torrent",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis()
        )

        // When
        val toString = torrentSharingData.toString()

        // Then
        assertTrue(toString.contains("test-torrent-sharing-id"))
        assertTrue(toString.contains("Test Torrent"))
        assertTrue(toString.contains("abc123"))
        assertTrue(toString.contains("magnet:?xt=urn:btih:abc123"))
    }
}