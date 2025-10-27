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


package com.shareconnect.profilesync.adapters

import com.shareconnect.profilesync.models.ProfileData
import org.junit.Assert.*
import org.junit.Test

class TransmissionConnectProfileAdapterTest {

    @Test
    fun `test toProfileData converts Server correctly with authentication`() {
        val server = TransmissionConnectProfileAdapter.Server(
            id = "server-uuid-1",
            name = "My Transmission Server",
            host = "192.168.1.100",
            port = 9091,
            useAuthentication = true,
            username = "admin",
            password = "secret",
            rpcUrl = "transmission/rpc",
            useHttps = false,
            trustSelfSignedSslCert = false
        )

        val profileData = TransmissionConnectProfileAdapter.toProfileData(server, "com.transmissionconnect")

        assertEquals("server-uuid-1", profileData.id)
        assertEquals("My Transmission Server", profileData.name)
        assertEquals("192.168.1.100", profileData.host)
        assertEquals(9091, profileData.port)
        assertEquals(ProfileData.TYPE_TORRENT, profileData.serviceType)
        assertEquals(ProfileData.TORRENT_CLIENT_TRANSMISSION, profileData.torrentClientType)
        assertEquals("admin", profileData.username)
        assertEquals("secret", profileData.password)
        assertEquals("com.transmissionconnect", profileData.sourceApp)
        assertEquals("transmission/rpc", profileData.rpcUrl)
        assertFalse(profileData.useHttps)
        assertFalse(profileData.trustSelfSignedCert)
        assertFalse(profileData.isDefault)
    }

    @Test
    fun `test toProfileData converts Server with HTTPS and SSL cert trust`() {
        val server = TransmissionConnectProfileAdapter.Server(
            id = "server-uuid-2",
            name = "Secure Transmission",
            host = "transmission.example.com",
            port = 443,
            useAuthentication = true,
            username = "user",
            password = "pass",
            rpcUrl = "rpc",
            useHttps = true,
            trustSelfSignedSslCert = true
        )

        val profileData = TransmissionConnectProfileAdapter.toProfileData(server, "com.transmissionconnect")

        assertTrue(profileData.useHttps)
        assertTrue(profileData.trustSelfSignedCert)
        assertEquals("rpc", profileData.rpcUrl)
    }

    @Test
    fun `test toProfileData handles no authentication`() {
        val server = TransmissionConnectProfileAdapter.Server(
            id = "server-uuid-3",
            name = "No Auth Server",
            host = "localhost",
            port = 9091,
            useAuthentication = false,
            username = "ignored",
            password = "ignored",
            rpcUrl = "transmission/rpc",
            useHttps = false,
            trustSelfSignedSslCert = false
        )

        val profileData = TransmissionConnectProfileAdapter.toProfileData(server, "com.transmissionconnect")

        assertNull(profileData.username)
        assertNull(profileData.password)
    }

    @Test
    fun `test fromProfileData converts ProfileData correctly`() {
        val profileData = ProfileData(
            id = "profile-id-1",
            name = "Test Profile",
            host = "192.168.1.200",
            port = 9091,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_TRANSMISSION,
            username = "testuser",
            password = "testpass",
            sourceApp = "com.transmissionconnect",
            rpcUrl = "custom/rpc",
            useHttps = true,
            trustSelfSignedCert = false
        )

        val server = TransmissionConnectProfileAdapter.fromProfileData(profileData)

        assertEquals("profile-id-1", server.id)
        assertEquals("Test Profile", server.name)
        assertEquals("192.168.1.200", server.host)
        assertEquals(9091, server.port)
        assertTrue(server.useAuthentication)
        assertEquals("testuser", server.username)
        assertEquals("testpass", server.password)
        assertEquals("custom/rpc", server.rpcUrl)
        assertTrue(server.useHttps)
        assertFalse(server.trustSelfSignedSslCert)
    }

    @Test
    fun `test fromProfileData handles null username and password`() {
        val profileData = ProfileData(
            id = "profile-id-2",
            name = "No Auth Profile",
            host = "localhost",
            port = 9091,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_TRANSMISSION,
            username = null,
            password = null,
            sourceApp = "com.transmissionconnect",
            rpcUrl = "transmission/rpc",
            useHttps = false,
            trustSelfSignedCert = false
        )

        val server = TransmissionConnectProfileAdapter.fromProfileData(profileData)

        assertFalse(server.useAuthentication)
        assertNull(server.username)
        assertNull(server.password)
    }

    @Test
    fun `test fromProfileData handles null rpcUrl with default`() {
        val profileData = ProfileData(
            id = "profile-id-3",
            name = "Default RPC",
            host = "localhost",
            port = 9091,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_TRANSMISSION,
            username = null,
            password = null,
            sourceApp = "com.transmissionconnect",
            rpcUrl = null,
            useHttps = false,
            trustSelfSignedCert = false
        )

        val server = TransmissionConnectProfileAdapter.fromProfileData(profileData)

        assertEquals("transmission/rpc", server.rpcUrl)  // Default value
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test fromProfileData throws exception for non-Transmission profile`() {
        val profileData = ProfileData(
            id = "qbit_1",
            name = "qBittorrent Profile",
            host = "localhost",
            port = 8080,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_QBITTORRENT,
            username = null,
            password = null,
            sourceApp = "com.qbitconnect"
        )

        TransmissionConnectProfileAdapter.fromProfileData(profileData)
    }

    @Test
    fun `test isEligibleForTransmissionConnect returns true for Transmission profiles`() {
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

        assertTrue(TransmissionConnectProfileAdapter.isEligibleForTransmissionConnect(transmissionProfile))
    }

    @Test
    fun `test isEligibleForTransmissionConnect returns false for qBittorrent profiles`() {
        val qBitProfile = ProfileData(
            id = "qbit_1",
            name = "qBittorrent",
            host = "localhost",
            port = 8080,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_QBITTORRENT,
            username = null,
            password = null,
            sourceApp = "com.test"
        )

        assertFalse(TransmissionConnectProfileAdapter.isEligibleForTransmissionConnect(qBitProfile))
    }

    @Test
    fun `test isEligibleForTransmissionConnect returns false for non-torrent profiles`() {
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

        assertFalse(TransmissionConnectProfileAdapter.isEligibleForTransmissionConnect(metubeProfile))
    }

    @Test
    fun `test round trip conversion preserves all data`() {
        val originalServer = TransmissionConnectProfileAdapter.Server(
            id = "round-trip-uuid",
            name = "Round Trip Test",
            host = "example.com",
            port = 9999,
            useAuthentication = true,
            username = "rounduser",
            password = "roundpass",
            rpcUrl = "custom/path",
            useHttps = true,
            trustSelfSignedSslCert = true
        )

        val profileData = TransmissionConnectProfileAdapter.toProfileData(originalServer, "com.transmissionconnect")
        val reconstructedServer = TransmissionConnectProfileAdapter.fromProfileData(profileData)

        assertEquals(originalServer.id, reconstructedServer.id)
        assertEquals(originalServer.name, reconstructedServer.name)
        assertEquals(originalServer.host, reconstructedServer.host)
        assertEquals(originalServer.port, reconstructedServer.port)
        assertEquals(originalServer.useAuthentication, reconstructedServer.useAuthentication)
        assertEquals(originalServer.username, reconstructedServer.username)
        assertEquals(originalServer.password, reconstructedServer.password)
        assertEquals(originalServer.rpcUrl, reconstructedServer.rpcUrl)
        assertEquals(originalServer.useHttps, reconstructedServer.useHttps)
        assertEquals(originalServer.trustSelfSignedSslCert, reconstructedServer.trustSelfSignedSslCert)
    }

    @Test
    fun `test authentication detection with only username`() {
        val profileData = ProfileData(
            id = "partial-auth",
            name = "Partial Auth",
            host = "localhost",
            port = 9091,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_TRANSMISSION,
            username = "user",
            password = null,
            sourceApp = "com.transmissionconnect"
        )

        val server = TransmissionConnectProfileAdapter.fromProfileData(profileData)

        // Should be false because password is null
        assertFalse(server.useAuthentication)
    }

    @Test
    fun `test authentication detection with only password`() {
        val profileData = ProfileData(
            id = "partial-auth-2",
            name = "Partial Auth 2",
            host = "localhost",
            port = 9091,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_TRANSMISSION,
            username = null,
            password = "pass",
            sourceApp = "com.transmissionconnect"
        )

        val server = TransmissionConnectProfileAdapter.fromProfileData(profileData)

        // Should be false because username is null
        assertFalse(server.useAuthentication)
    }
}
