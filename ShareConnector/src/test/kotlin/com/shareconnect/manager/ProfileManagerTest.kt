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


package com.shareconnect.manager

import com.shareconnect.ServerProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Simple unit tests for ServerProfile data class functionality.
 * ProfileManager integration tests are in ProfileManagerInstrumentationTest.
 */
class ProfileManagerTest {

    @Test
    fun testServerProfileCreation() {
        val profile = ServerProfile()
        assertNotNull(profile)
    }

    @Test
    fun testServerProfileWithValues() {
        val profile = ServerProfile()
        profile.id = "test-id"
        profile.name = "Test Profile"
        profile.url = "http://example.com"
        profile.port = 8080
        profile.serviceType = ServerProfile.TYPE_METUBE
        profile.username = "testuser"
        profile.password = "testpass"

        assertEquals("test-id", profile.id)
        assertEquals("Test Profile", profile.name)
        assertEquals("http://example.com", profile.url)
        assertEquals(8080, profile.port)
        assertEquals(ServerProfile.TYPE_METUBE, profile.serviceType)
        assertEquals("testuser", profile.username)
        assertEquals("testpass", profile.password)
    }

    @Test
    fun testServiceTypeChecks() {
        val metubeProfile = ServerProfile()
        metubeProfile.serviceType = ServerProfile.TYPE_METUBE
        assertTrue(metubeProfile.isMeTube())
        assertFalse(metubeProfile.isYtDl())
        assertFalse(metubeProfile.isTorrent())
        assertFalse(metubeProfile.isJDownloader())

        val torrentProfile = ServerProfile()
        torrentProfile.serviceType = ServerProfile.TYPE_TORRENT
        assertFalse(torrentProfile.isMeTube())
        assertFalse(torrentProfile.isYtDl())
        assertTrue(torrentProfile.isTorrent())
        assertFalse(torrentProfile.isJDownloader())

        val ytdlProfile = ServerProfile()
        ytdlProfile.serviceType = ServerProfile.TYPE_YTDL
        assertFalse(ytdlProfile.isMeTube())
        assertTrue(ytdlProfile.isYtDl())
        assertFalse(ytdlProfile.isTorrent())
        assertFalse(ytdlProfile.isJDownloader())

        val jdownloaderProfile = ServerProfile()
        jdownloaderProfile.serviceType = ServerProfile.TYPE_JDOWNLOADER
        assertFalse(jdownloaderProfile.isMeTube())
        assertFalse(jdownloaderProfile.isYtDl())
        assertFalse(jdownloaderProfile.isTorrent())
        assertTrue(jdownloaderProfile.isJDownloader())
    }

    @Test
    fun testTorrentClientTypes() {
        val profile = ServerProfile()
        profile.serviceType = ServerProfile.TYPE_TORRENT
        profile.torrentClientType = ServerProfile.TORRENT_CLIENT_QBITTORRENT

        assertEquals(ServerProfile.TORRENT_CLIENT_QBITTORRENT, profile.torrentClientType)
        assertEquals("qBittorrent", profile.getTorrentClientName())
    }

    @Test
    fun testEqualsAndHashCode() {
        val profile1 = ServerProfile()
        profile1.id = "test-id"
        profile1.name = "Test Profile"
        profile1.url = "http://example.com"
        profile1.port = 8080

        val profile2 = ServerProfile()
        profile2.id = "test-id"
        profile2.name = "Test Profile"
        profile2.url = "http://example.com"
        profile2.port = 8080

        assertEquals(profile1, profile2)
        assertEquals(profile1.hashCode(), profile2.hashCode())
    }
}