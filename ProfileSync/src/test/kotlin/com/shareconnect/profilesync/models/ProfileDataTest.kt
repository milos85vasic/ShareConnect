package com.shareconnect.profilesync.models

import org.junit.Assert.*
import org.junit.Test

class ProfileDataTest {

    @Test
    fun `test profile creation with all fields`() {
        val profile = ProfileData(
            id = "test-id",
            name = "Test Profile",
            host = "192.168.1.100",
            port = 9091,
            isDefault = true,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_TRANSMISSION,
            username = "admin",
            password = "password123",
            sourceApp = "com.test.app",
            version = 1,
            lastModified = 1234567890L,
            rpcUrl = "transmission/rpc",
            useHttps = true,
            trustSelfSignedCert = false
        )

        assertEquals("test-id", profile.id)
        assertEquals("Test Profile", profile.name)
        assertEquals("192.168.1.100", profile.host)
        assertEquals(9091, profile.port)
        assertTrue(profile.isDefault)
        assertEquals(ProfileData.TYPE_TORRENT, profile.serviceType)
        assertEquals(ProfileData.TORRENT_CLIENT_TRANSMISSION, profile.torrentClientType)
        assertEquals("admin", profile.username)
        assertEquals("password123", profile.password)
        assertEquals("com.test.app", profile.sourceApp)
        assertEquals(1, profile.version)
        assertEquals(1234567890L, profile.lastModified)
        assertEquals("transmission/rpc", profile.rpcUrl)
        assertTrue(profile.useHttps)
        assertFalse(profile.trustSelfSignedCert)
    }

    @Test
    fun `test profile creation with minimal fields`() {
        val profile = ProfileData(
            id = "minimal-id",
            name = "Minimal Profile",
            host = "localhost",
            port = 8080,
            isDefault = false,
            serviceType = ProfileData.TYPE_METUBE,
            torrentClientType = null,
            username = null,
            password = null,
            sourceApp = "com.test.app"
        )

        assertEquals("minimal-id", profile.id)
        assertEquals("Minimal Profile", profile.name)
        assertNull(profile.torrentClientType)
        assertNull(profile.username)
        assertNull(profile.password)
        assertNull(profile.rpcUrl)
        assertFalse(profile.useHttps)
        assertFalse(profile.trustSelfSignedCert)
    }

    @Test
    fun `test isTransmissionProfile returns true for Transmission client`() {
        val profile = ProfileData(
            id = "trans-1",
            name = "Transmission",
            host = "localhost",
            port = 9091,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_TRANSMISSION,
            username = null,
            password = null,
            sourceApp = "com.test.app"
        )

        assertTrue(profile.isTransmissionProfile())
        assertFalse(profile.isQBitTorrentProfile())
    }

    @Test
    fun `test isQBitTorrentProfile returns true for qBittorrent client`() {
        val profile = ProfileData(
            id = "qbit-1",
            name = "qBittorrent",
            host = "localhost",
            port = 8080,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_QBITTORRENT,
            username = null,
            password = null,
            sourceApp = "com.test.app"
        )

        assertTrue(profile.isQBitTorrentProfile())
        assertFalse(profile.isTransmissionProfile())
    }

    @Test
    fun `test non-torrent profile returns false for both client checks`() {
        val profile = ProfileData(
            id = "metube-1",
            name = "MeTube",
            host = "localhost",
            port = 8081,
            isDefault = false,
            serviceType = ProfileData.TYPE_METUBE,
            torrentClientType = null,
            username = null,
            password = null,
            sourceApp = "com.test.app"
        )

        assertFalse(profile.isTransmissionProfile())
        assertFalse(profile.isQBitTorrentProfile())
    }

    @Test
    fun `test profile constants are correct`() {
        assertEquals("profile", ProfileData.OBJECT_TYPE)
        assertEquals("torrent", ProfileData.TYPE_TORRENT)
        assertEquals("metube", ProfileData.TYPE_METUBE)
        assertEquals("ytdl", ProfileData.TYPE_YTDL)
        assertEquals("jdownloader", ProfileData.TYPE_JDOWNLOADER)
        assertEquals("qbittorrent", ProfileData.TORRENT_CLIENT_QBITTORRENT)
        assertEquals("transmission", ProfileData.TORRENT_CLIENT_TRANSMISSION)
        assertEquals("utorrent", ProfileData.TORRENT_CLIENT_UTORRENT)
    }

    @Test
    fun `test profile with uTorrent client type`() {
        val profile = ProfileData(
            id = "utorrent-1",
            name = "uTorrent",
            host = "localhost",
            port = 8000,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_UTORRENT,
            username = null,
            password = null,
            sourceApp = "com.test.app"
        )

        assertFalse(profile.isTransmissionProfile())
        assertFalse(profile.isQBitTorrentProfile())
        assertEquals(ProfileData.TORRENT_CLIENT_UTORRENT, profile.torrentClientType)
    }

    @Test
    fun `test profile with YouTube downloader service type`() {
        val profile = ProfileData(
            id = "ytdl-1",
            name = "YouTube Downloader",
            host = "localhost",
            port = 8082,
            isDefault = false,
            serviceType = ProfileData.TYPE_YTDL,
            torrentClientType = null,
            username = null,
            password = null,
            sourceApp = "com.test.app"
        )

        assertEquals(ProfileData.TYPE_YTDL, profile.serviceType)
        assertFalse(profile.isTransmissionProfile())
        assertFalse(profile.isQBitTorrentProfile())
    }

    @Test
    fun `test default profile flag`() {
        val defaultProfile = ProfileData(
            id = "default-1",
            name = "Default",
            host = "localhost",
            port = 8080,
            isDefault = true,
            serviceType = ProfileData.TYPE_METUBE,
            torrentClientType = null,
            username = null,
            password = null,
            sourceApp = "com.test.app"
        )

        val nonDefaultProfile = ProfileData(
            id = "non-default-1",
            name = "Non-Default",
            host = "localhost",
            port = 8081,
            isDefault = false,
            serviceType = ProfileData.TYPE_METUBE,
            torrentClientType = null,
            username = null,
            password = null,
            sourceApp = "com.test.app"
        )

        assertTrue(defaultProfile.isDefault)
        assertFalse(nonDefaultProfile.isDefault)
    }
}
