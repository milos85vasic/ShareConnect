package com.shareconnect.profilesync.adapters

import com.shareconnect.profilesync.models.ProfileData
import org.junit.Assert.*
import org.junit.Test

class UTorrentConnectProfileAdapterTest {

    @Test
    fun `toProfileData converts Server to ProfileData correctly`() {
        val server = UTorrentConnectProfileAdapter.Server(
            id = "utorrent-server-1",
            name = "My uTorrent Server",
            host = "192.168.1.100",
            port = 8080,
            useAuthentication = true,
            username = "admin",
            password = "password123",
            rpcUrl = "gui",
            useHttps = true,
            trustSelfSignedSslCert = true
        )

        val profileData = UTorrentConnectProfileAdapter.toProfileData(
            server = server,
            sourceApp = ProfileData.APP_UTORRENT_CONNECT
        )

        assertEquals("utorrent-server-1", profileData.id)
        assertEquals("My uTorrent Server", profileData.name)
        assertEquals("192.168.1.100", profileData.host)
        assertEquals(8080, profileData.port)
        assertFalse(profileData.isDefault)
        assertEquals(ProfileData.TYPE_TORRENT, profileData.serviceType)
        assertEquals(ProfileData.TORRENT_CLIENT_UTORRENT, profileData.torrentClientType)
        assertEquals("admin", profileData.username)
        assertEquals("password123", profileData.password)
        assertEquals(ProfileData.APP_UTORRENT_CONNECT, profileData.sourceApp)
        assertEquals("gui", profileData.rpcUrl)
        assertTrue(profileData.useHttps)
        assertTrue(profileData.trustSelfSignedCert)
    }

    @Test
    fun `toProfileData handles server without authentication`() {
        val server = UTorrentConnectProfileAdapter.Server(
            id = "utorrent-server-2",
            name = "Public Server",
            host = "example.com",
            port = 8080,
            useAuthentication = false,
            username = null,
            password = null,
            rpcUrl = "gui",
            useHttps = false,
            trustSelfSignedSslCert = false
        )

        val profileData = UTorrentConnectProfileAdapter.toProfileData(
            server = server,
            sourceApp = ProfileData.APP_UTORRENT_CONNECT
        )

        assertNull(profileData.username)
        assertNull(profileData.password)
    }

    @Test
    fun `fromProfileData converts ProfileData to Server correctly`() {
        val profileData = ProfileData(
            id = "utorrent-profile-1",
            name = "Test uTorrent Profile",
            host = "10.0.0.1",
            port = 8080,
            isDefault = true,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_UTORRENT,
            username = "user",
            password = "pass",
            sourceApp = ProfileData.APP_UTORRENT_CONNECT,
            rpcUrl = "gui",
            useHttps = true,
            trustSelfSignedCert = true
        )

        val server = UTorrentConnectProfileAdapter.fromProfileData(profileData)

        assertEquals("utorrent-profile-1", server.id)
        assertEquals("Test uTorrent Profile", server.name)
        assertEquals("10.0.0.1", server.host)
        assertEquals(8080, server.port)
        assertTrue(server.useAuthentication)
        assertEquals("user", server.username)
        assertEquals("pass", server.password)
        assertEquals("gui", server.rpcUrl)
        assertTrue(server.useHttps)
        assertTrue(server.trustSelfSignedSslCert)
    }

    @Test
    fun `fromProfileData uses default rpcUrl when null`() {
        val profileData = ProfileData(
            id = "utorrent-profile-2",
            name = "Default RPC",
            host = "localhost",
            port = 8080,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_UTORRENT,
            username = null,
            password = null,
            sourceApp = ProfileData.APP_UTORRENT_CONNECT,
            rpcUrl = null,
            useHttps = false,
            trustSelfSignedCert = false
        )

        val server = UTorrentConnectProfileAdapter.fromProfileData(profileData)

        assertEquals("gui", server.rpcUrl)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `fromProfileData throws exception for non-uTorrent profile`() {
        val qbittorrentProfile = ProfileData(
            id = "qbit-profile-1",
            name = "qBittorrent Profile",
            host = "localhost",
            port = 8080,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_QBITTORRENT,
            username = null,
            password = null,
            sourceApp = ProfileData.APP_QBIT_CONNECT
        )

        // Should throw IllegalArgumentException
        UTorrentConnectProfileAdapter.fromProfileData(qbittorrentProfile)
    }

    @Test
    fun `isEligibleForUTorrentConnect returns true for uTorrent profiles`() {
        val utorrentProfile = ProfileData(
            id = "utorrent-1",
            name = "uTorrent Server",
            host = "localhost",
            port = 8080,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_UTORRENT,
            username = null,
            password = null,
            sourceApp = ProfileData.APP_UTORRENT_CONNECT
        )

        assertTrue(UTorrentConnectProfileAdapter.isEligibleForUTorrentConnect(utorrentProfile))
    }

    @Test
    fun `isEligibleForUTorrentConnect returns false for other profiles`() {
        val transmissionProfile = ProfileData(
            id = "transmission-1",
            name = "Transmission Server",
            host = "localhost",
            port = 9091,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_TRANSMISSION,
            username = null,
            password = null,
            sourceApp = ProfileData.APP_TRANSMISSION_CONNECT
        )

        assertFalse(UTorrentConnectProfileAdapter.isEligibleForUTorrentConnect(transmissionProfile))
    }

    @Test
    fun `fromProfileData handles profile without authentication`() {
        val profileData = ProfileData(
            id = "utorrent-no-auth",
            name = "No Auth Server",
            host = "localhost",
            port = 8080,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_UTORRENT,
            username = null,
            password = null,
            sourceApp = ProfileData.APP_UTORRENT_CONNECT
        )

        val server = UTorrentConnectProfileAdapter.fromProfileData(profileData)

        assertFalse(server.useAuthentication)
        assertNull(server.username)
        assertNull(server.password)
    }
}
