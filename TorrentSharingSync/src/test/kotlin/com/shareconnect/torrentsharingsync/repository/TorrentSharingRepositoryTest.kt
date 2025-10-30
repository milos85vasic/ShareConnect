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
import kotlinx.coroutines.flow.first
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
    fun `test setTorrentSharingPrefs saves preferences correctly`() = runTest {
        // Given
        val prefs = TorrentSharingData.createDefault().copy(directSharingEnabled = false)

        // When
        repository.setTorrentSharingPrefs(prefs)

        // Then
        val savedPrefs = repository.getTorrentSharingPrefs()
        assertNotNull(savedPrefs)
        assertEquals(TorrentSharingData.PREFS_ID, savedPrefs?.id)
        assertFalse(savedPrefs?.directSharingEnabled ?: true)
        assertEquals(prefs.dontAskQBitConnect, savedPrefs?.dontAskQBitConnect ?: false)
        assertEquals(prefs.dontAskTransmissionConnect, savedPrefs?.dontAskTransmissionConnect ?: false)
    }

    @Test
    fun `test updateTorrentSharingPrefs modifies existing preferences`() = runTest {
        // Given
        val originalPrefs = TorrentSharingData.createDefault()
        repository.setTorrentSharingPrefs(originalPrefs)

        val updatedPrefs = originalPrefs.copy(
            directSharingEnabled = false,
            dontAskQBitConnect = true,
            version = 2
        )

        // When
        repository.updateTorrentSharingPrefs(updatedPrefs)

        // Then
        val savedPrefs = repository.getTorrentSharingPrefs()
        assertNotNull(savedPrefs)
        assertFalse(savedPrefs?.directSharingEnabled ?: true)
        assertTrue(savedPrefs?.dontAskQBitConnect ?: false)
        assertEquals(originalPrefs.dontAskTransmissionConnect, savedPrefs?.dontAskTransmissionConnect ?: false)
        assertEquals(2, savedPrefs?.version ?: 0)
    }

    @Test
    fun `test deleteTorrentSharingPrefs removes preferences`() = runTest {
        // Given
        val prefs = TorrentSharingData.createDefault()
        repository.setTorrentSharingPrefs(prefs)

        // Verify it exists
        assertNotNull(repository.getTorrentSharingPrefs())

        // When
        repository.deleteTorrentSharingPrefs()

        // Then
        val deletedPrefs = repository.getTorrentSharingPrefs()
        assertNull(deletedPrefs)
    }

    @Test
    fun `test getTorrentSharingPrefsFlow emits preferences changes`() = runTest {
        // Given
        val prefs1 = TorrentSharingData.createDefault().copy(directSharingEnabled = true)
        val prefs2 = prefs1.copy(directSharingEnabled = false)

        // When
        repository.setTorrentSharingPrefs(prefs1)
        val flowValue1 = repository.getTorrentSharingPrefsFlow().first()

        repository.setTorrentSharingPrefs(prefs2)
        val flowValue2 = repository.getTorrentSharingPrefsFlow().first()

        // Then
        assertNotNull(flowValue1)
        assertTrue(flowValue1?.directSharingEnabled ?: false)

        assertNotNull(flowValue2)
        assertFalse(flowValue2?.directSharingEnabled ?: true)
    }

    @Test
    fun `test getTorrentSharingPrefs returns null when no preferences exist`() = runTest {
        // When
        val prefs = repository.getTorrentSharingPrefs()

        // Then
        assertNull(prefs)
    }

    @Test
    fun `test getOrCreateDefault returns existing preferences when available`() = runTest {
        // Given
        val existingPrefs = TorrentSharingData.createDefault().copy(directSharingEnabled = false)
        repository.setTorrentSharingPrefs(existingPrefs)

        // When
        val result = repository.getOrCreateDefault()

        // Then
        assertEquals(existingPrefs, result)
    }

    @Test
    fun `test getOrCreateDefault creates default when none exists`() = runTest {
        // When
        val result = repository.getOrCreateDefault()

        // Then
        assertEquals(TorrentSharingData.PREFS_ID, result.id)
        assertTrue(result.directSharingEnabled)
        assertFalse(result.dontAskQBitConnect)
        assertFalse(result.dontAskTransmissionConnect)
        assertEquals(1, result.version)
    }
}