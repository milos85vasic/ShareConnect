package com.shareconnect.profilesync.adapters

import com.shareconnect.profilesync.models.ProfileData
import org.junit.Assert.*
import org.junit.Test

class QBitConnectProfileAdapterTest {

    @Test
    fun `test toProfileData converts ServerConfig correctly with HTTP`() {
        val serverConfig = QBitConnectProfileAdapter.ServerConfig(
            id = 1,
            name = "My qBittorrent Server",
            url = "http://192.168.1.100:8080",
            username = "admin",
            password = "secret"
        )

        val profileData = QBitConnectProfileAdapter.toProfileData(serverConfig, "com.qbitconnect")

        assertEquals("com.qbitconnect_1", profileData.id)
        assertEquals("My qBittorrent Server", profileData.name)
        assertEquals("192.168.1.100", profileData.host)
        assertEquals(8080, profileData.port)
        assertEquals(ProfileData.TYPE_TORRENT, profileData.serviceType)
        assertEquals(ProfileData.TORRENT_CLIENT_QBITTORRENT, profileData.torrentClientType)
        assertEquals("admin", profileData.username)
        assertEquals("secret", profileData.password)
        assertEquals("com.qbitconnect", profileData.sourceApp)
        assertFalse(profileData.isDefault)
        assertFalse(profileData.useHttps)
    }

    @Test
    fun `test toProfileData converts ServerConfig correctly with HTTPS`() {
        val serverConfig = QBitConnectProfileAdapter.ServerConfig(
            id = 2,
            name = "Secure qBit Server",
            url = "https://qbit.example.com:8443",
            username = "user",
            password = "pass"
        )

        val profileData = QBitConnectProfileAdapter.toProfileData(serverConfig, "com.qbitconnect")

        assertEquals("com.qbitconnect_2", profileData.id)
        assertEquals("Secure qBit Server", profileData.name)
        assertEquals("qbit.example.com", profileData.host)
        assertEquals(8443, profileData.port)
        assertTrue(profileData.useHttps)
    }

    @Test
    fun `test toProfileData handles URL without port`() {
        val serverConfig = QBitConnectProfileAdapter.ServerConfig(
            id = 3,
            name = "Default Port Server",
            url = "http://localhost",
            username = null,
            password = null
        )

        val profileData = QBitConnectProfileAdapter.toProfileData(serverConfig, "com.qbitconnect")

        assertEquals("localhost", profileData.host)
        assertEquals(8080, profileData.port)  // Default port
    }

    @Test
    fun `test toProfileData handles null server name`() {
        val serverConfig = QBitConnectProfileAdapter.ServerConfig(
            id = 4,
            name = null,
            url = "http://192.168.1.50:9000",
            username = null,
            password = null
        )

        val profileData = QBitConnectProfileAdapter.toProfileData(serverConfig, "com.qbitconnect")

        assertEquals("qBittorrent Server", profileData.name)  // Default name
    }

    @Test
    fun `test fromProfileData converts ProfileData correctly`() {
        val profileData = ProfileData(
            id = "com.qbitconnect_5",
            name = "Test Profile",
            host = "192.168.1.200",
            port = 8080,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_QBITTORRENT,
            username = "testuser",
            password = "testpass",
            sourceApp = "com.qbitconnect",
            useHttps = false
        )

        val serverConfig = QBitConnectProfileAdapter.fromProfileData(profileData)

        assertEquals(5, serverConfig.id)
        assertEquals("Test Profile", serverConfig.name)
        assertEquals("http://192.168.1.200:8080", serverConfig.url)
        assertEquals("testuser", serverConfig.username)
        assertEquals("testpass", serverConfig.password)
    }

    @Test
    fun `test fromProfileData converts ProfileData with HTTPS correctly`() {
        val profileData = ProfileData(
            id = "com.qbitconnect_10",
            name = "Secure Profile",
            host = "secure.example.com",
            port = 443,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_QBITTORRENT,
            username = "admin",
            password = "admin123",
            sourceApp = "com.qbitconnect",
            useHttps = true
        )

        val serverConfig = QBitConnectProfileAdapter.fromProfileData(profileData)

        assertEquals("https://secure.example.com:443", serverConfig.url)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test fromProfileData throws exception for non-qBittorrent profile`() {
        val profileData = ProfileData(
            id = "transmission_1",
            name = "Transmission Profile",
            host = "localhost",
            port = 9091,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_TRANSMISSION,
            username = null,
            password = null,
            sourceApp = "com.transmissionconnect"
        )

        QBitConnectProfileAdapter.fromProfileData(profileData)
    }

    @Test
    fun `test isEligibleForQBitConnect returns true for qBittorrent profiles`() {
        val qBitProfile = ProfileData(
            id = "qbit_1",
            name = "qBit",
            host = "localhost",
            port = 8080,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_QBITTORRENT,
            username = null,
            password = null,
            sourceApp = "com.test"
        )

        assertTrue(QBitConnectProfileAdapter.isEligibleForQBitConnect(qBitProfile))
    }

    @Test
    fun `test isEligibleForQBitConnect returns false for Transmission profiles`() {
        val transmissionProfile = ProfileData(
            id = "trans_1",
            name = "Transmission",
            host = "localhost",
            port = 9091,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_TRANSMISSION,
            username = null,
            password = null,
            sourceApp = "com.test"
        )

        assertFalse(QBitConnectProfileAdapter.isEligibleForQBitConnect(transmissionProfile))
    }

    @Test
    fun `test isEligibleForQBitConnect returns false for non-torrent profiles`() {
        val metubeProfile = ProfileData(
            id = "metube_1",
            name = "MeTube",
            host = "localhost",
            port = 8081,
            isDefault = false,
            serviceType = ProfileData.TYPE_METUBE,
            torrentClientType = null,
            username = null,
            password = null,
            sourceApp = "com.test"
        )

        assertFalse(QBitConnectProfileAdapter.isEligibleForQBitConnect(metubeProfile))
    }

    @Test
    fun `test round trip conversion preserves data`() {
        val originalConfig = QBitConnectProfileAdapter.ServerConfig(
            id = 99,
            name = "Round Trip Test",
            url = "https://example.com:9999",
            username = "roundtrip",
            password = "password"
        )

        val profileData = QBitConnectProfileAdapter.toProfileData(originalConfig, "com.qbitconnect")
        val reconstructedConfig = QBitConnectProfileAdapter.fromProfileData(profileData)

        assertEquals(originalConfig.id, reconstructedConfig.id)
        assertEquals(originalConfig.name, reconstructedConfig.name)
        assertEquals(originalConfig.url, reconstructedConfig.url)
        assertEquals(originalConfig.username, reconstructedConfig.username)
        assertEquals(originalConfig.password, reconstructedConfig.password)
    }
}
