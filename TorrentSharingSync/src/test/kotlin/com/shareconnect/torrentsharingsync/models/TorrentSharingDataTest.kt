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


package com.shareconnect.torrentsharingsync.models

import org.junit.Assert.*
import org.junit.Test

class TorrentSharingDataTest {

    @Test
    fun `test TorrentSharingData creation with default values`() {
        // When
        val data = TorrentSharingData.createDefault()

        // Then
        assertEquals(TorrentSharingData.PREFS_ID, data.id)
        assertTrue(data.directSharingEnabled)
        assertFalse(data.dontAskQBitConnect)
        assertFalse(data.dontAskTransmissionConnect)
        assertEquals(1, data.version)
        assertTrue(data.lastModified > 0)
    }

    @Test
    fun `test TorrentSharingData creation with custom values`() {
        // Given
        val timestamp = System.currentTimeMillis()

        // When
        val data = TorrentSharingData(
            id = "custom-id",
            directSharingEnabled = false,
            dontAskQBitConnect = true,
            dontAskTransmissionConnect = true,
            version = 5,
            lastModified = timestamp
        )

        // Then
        assertEquals("custom-id", data.id)
        assertFalse(data.directSharingEnabled)
        assertTrue(data.dontAskQBitConnect)
        assertTrue(data.dontAskTransmissionConnect)
        assertEquals(5, data.version)
        assertEquals(timestamp, data.lastModified)
    }

    @Test
    fun `test createUpdated updates specific fields and increments version`() {
        // Given
        val original = TorrentSharingData.createDefault()

        // When
        val updated = TorrentSharingData.createUpdated(
            current = original,
            directSharingEnabled = false,
            dontAskQBitConnect = true
        )

        // Then
        assertEquals(original.id, updated.id)
        assertFalse(updated.directSharingEnabled)
        assertTrue(updated.dontAskQBitConnect)
        assertEquals(original.dontAskTransmissionConnect, updated.dontAskTransmissionConnect)
        assertEquals(original.version + 1, updated.version)
        assertTrue(updated.lastModified >= original.lastModified)
    }

    @Test
    fun `test shouldAskForApp returns correct values based on dont ask flags`() {
        // Given
        val data = TorrentSharingData.createDefault().copy(
            dontAskQBitConnect = true,
            dontAskTransmissionConnect = false
        )

        // Then
        assertFalse(data.shouldAskForApp("com.shareconnect.qbitconnect"))
        assertTrue(data.shouldAskForApp("com.shareconnect.transmissionconnect"))
        assertTrue(data.shouldAskForApp("com.shareconnect.unknown"))
    }

    @Test
    fun `test withIncrementedVersion increments version and updates timestamp`() {
        // Given
        val original = TorrentSharingData.createDefault()

        // When
        val incremented = original.withIncrementedVersion()

        // Then
        assertEquals(original.id, incremented.id)
        assertEquals(original.directSharingEnabled, incremented.directSharingEnabled)
        assertEquals(original.version + 1, incremented.version)
        assertTrue(incremented.lastModified >= original.lastModified)
    }

    @Test
    fun `test TorrentSharingData equals and hashCode work correctly`() {
        // Given
        val data1 = TorrentSharingData.createDefault()
        val data2 = TorrentSharingData.createDefault()
        val data3 = TorrentSharingData.createDefault().copy(directSharingEnabled = false)

        // Then
        assertEquals(data1, data2)
        assertEquals(data1.hashCode(), data2.hashCode())
        assertNotEquals(data1, data3)
        assertNotEquals(data1.hashCode(), data3.hashCode())
    }

    @Test
    fun `test TorrentSharingData toString contains relevant information`() {
        // Given
        val data = TorrentSharingData.createDefault()

        // When
        val toString = data.toString()

        // Then
        assertTrue(toString.contains(TorrentSharingData.PREFS_ID))
        assertTrue(toString.contains("directSharingEnabled=true"))
        assertTrue(toString.contains("dontAskQBitConnect=false"))
        assertTrue(toString.contains("dontAskTransmissionConnect=false"))
    }
}