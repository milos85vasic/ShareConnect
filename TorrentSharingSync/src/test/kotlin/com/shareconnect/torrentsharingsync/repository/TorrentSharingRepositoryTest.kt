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


package com.shareconnect.torrentsharingsync.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.torrentsharingsync.database.TorrentSharingDatabase
import com.shareconnect.torrentsharingsync.models.TorrentSharingData
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TorrentSharingRepositoryTest {

    private lateinit var database: TorrentSharingDatabase
    private lateinit var repository: TorrentSharingRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TorrentSharingDatabase::class.java
        ).build()

        repository = TorrentSharingRepository(database.torrentSharingDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `test insertTorrentSharing saves torrent sharing data correctly`() = runTest {
        // Given
        val torrentSharingData = TorrentSharingData(
            id = "test-torrent-sharing-id",
            torrentName = "Test Torrent",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )

        // When
        repository.insertTorrentSharing(torrentSharingData)

        // Then
        val savedData = repository.getTorrentSharingById("test-torrent-sharing-id")
        assertNotNull(savedData)
        assertEquals("test-torrent-sharing-id", savedData?.id)
        assertEquals("Test Torrent", savedData?.torrentName)
        assertEquals("abc123", savedData?.torrentHash)
        assertEquals("magnet:?xt=urn:btih:abc123", savedData?.magnetUri)
    }

    @Test
    fun `test updateTorrentSharing modifies existing torrent sharing data`() = runTest {
        // Given
        val originalData = TorrentSharingData(
            id = "test-torrent-sharing-id",
            torrentName = "Original Torrent",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertTorrentSharing(originalData)

        val updatedData = TorrentSharingData(
            id = "test-torrent-sharing-id",
            torrentName = "Updated Torrent",
            torrentHash = "def456",
            magnetUri = "magnet:?xt=urn:btih:def456",
            timestamp = System.currentTimeMillis(),
            version = 2,
            lastModified = System.currentTimeMillis()
        )

        // When
        repository.updateTorrentSharing(updatedData)

        // Then
        val savedData = repository.getTorrentSharingById("test-torrent-sharing-id")
        assertNotNull(savedData)
        assertEquals("Updated Torrent", savedData?.torrentName)
        assertEquals("def456", savedData?.torrentHash)
        assertEquals("magnet:?xt=urn:btih:def456", savedData?.magnetUri)
        assertEquals(2, savedData?.version)
    }

    @Test
    fun `test deleteTorrentSharing removes torrent sharing data`() = runTest {
        // Given
        val torrentSharingData = TorrentSharingData(
            id = "test-torrent-sharing-id",
            torrentName = "Test Torrent",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertTorrentSharing(torrentSharingData)

        // Verify it exists
        assertNotNull(repository.getTorrentSharingById("test-torrent-sharing-id"))

        // When
        repository.deleteTorrentSharing("test-torrent-sharing-id")

        // Then
        val deletedData = repository.getTorrentSharingById("test-torrent-sharing-id")
        assertNull(deletedData)
    }

    @Test
    fun `test getAllTorrentSharing returns all saved torrent sharing data`() = runTest {
        // Given
        val data1 = TorrentSharingData(
            id = "test-torrent-sharing-id-1",
            torrentName = "Torrent 1",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        val data2 = TorrentSharingData(
            id = "test-torrent-sharing-id-2",
            torrentName = "Torrent 2",
            torrentHash = "def456",
            magnetUri = "magnet:?xt=urn:btih:def456",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )

        repository.insertTorrentSharing(data1)
        repository.insertTorrentSharing(data2)

        // When
        val allData = repository.getAllTorrentSharing()

        // Then
        assertEquals(2, allData.size)
        assertTrue(allData.any { it.id == "test-torrent-sharing-id-1" })
        assertTrue(allData.any { it.id == "test-torrent-sharing-id-2" })
    }

    @Test
    fun `test getTorrentSharingById returns null for non-existent id`() = runTest {
        // When
        val data = repository.getTorrentSharingById("non-existent-id")

        // Then
        assertNull(data)
    }

    @Test
    fun `test getTorrentSharingByHash returns correct torrent sharing data`() = runTest {
        // Given
        val torrentSharingData = TorrentSharingData(
            id = "test-torrent-sharing-id",
            torrentName = "Test Torrent",
            torrentHash = "abc123",
            magnetUri = "magnet:?xt=urn:btih:abc123",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertTorrentSharing(torrentSharingData)

        // When
        val data = repository.getTorrentSharingByHash("abc123")

        // Then
        assertNotNull(data)
        assertEquals("test-torrent-sharing-id", data?.id)
        assertEquals("Test Torrent", data?.torrentName)
        assertEquals("abc123", data?.torrentHash)
    }

    @Test
    fun `test getTorrentSharingByHash returns null for non-existent hash`() = runTest {
        // When
        val data = repository.getTorrentSharingByHash("non-existent-hash")

        // Then
        assertNull(data)
    }
}