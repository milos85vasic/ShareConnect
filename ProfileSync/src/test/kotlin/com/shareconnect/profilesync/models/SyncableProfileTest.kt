package com.shareconnect.profilesync.models

import org.junit.Assert.*
import org.junit.Test

class SyncableProfileTest {

    @Test
    fun `test syncable profile creation from profile data`() {
        val profileData = ProfileData(
            id = "test-1",
            name = "Test Profile",
            host = "192.168.1.100",
            port = 9091,
            isDefault = true,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_TRANSMISSION,
            username = "admin",
            password = "secret",
            sourceApp = "com.test.app",
            version = 2,
            lastModified = 1234567890L,
            rpcUrl = "transmission/rpc",
            useHttps = true,
            trustSelfSignedCert = false
        )

        val syncableProfile = SyncableProfile.fromProfileData(profileData)

        assertEquals("test-1", syncableProfile.objectId)
        assertEquals(ProfileData.OBJECT_TYPE, syncableProfile.objectType)
        assertEquals(2, syncableProfile.version)
        assertEquals(profileData, syncableProfile.getProfileData())
    }

    @Test
    fun `test toFieldMap converts all fields correctly`() {
        val profileData = ProfileData(
            id = "field-test",
            name = "Field Test",
            host = "example.com",
            port = 8080,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_QBITTORRENT,
            username = "user",
            password = "pass",
            sourceApp = "com.test.app",
            version = 3,
            lastModified = 9876543210L,
            rpcUrl = null,
            useHttps = false,
            trustSelfSignedCert = true
        )

        val syncableProfile = SyncableProfile.fromProfileData(profileData)
        val fieldMap = syncableProfile.toFieldMap()

        assertEquals("field-test", fieldMap["id"])
        assertEquals("Field Test", fieldMap["name"])
        assertEquals("example.com", fieldMap["host"])
        assertEquals(8080, fieldMap["port"])
        assertEquals(false, fieldMap["isDefault"])
        assertEquals(ProfileData.TYPE_TORRENT, fieldMap["serviceType"])
        assertEquals(ProfileData.TORRENT_CLIENT_QBITTORRENT, fieldMap["torrentClientType"])
        assertEquals("user", fieldMap["username"])
        assertEquals("pass", fieldMap["password"])
        assertEquals("com.test.app", fieldMap["sourceApp"])
        assertEquals(3, fieldMap["version"])
        assertEquals(9876543210L, fieldMap["lastModified"])
        assertEquals(null, fieldMap["rpcUrl"])
        assertEquals(false, fieldMap["useHttps"])
        assertEquals(true, fieldMap["trustSelfSignedCert"])
    }

    @Test
    fun `test fromFieldMap reconstructs profile data correctly`() {
        val originalData = ProfileData(
            id = "reconstruct-test",
            name = "Reconstruct Test",
            host = "test.local",
            port = 7070,
            isDefault = true,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_TRANSMISSION,
            username = "testuser",
            password = "testpass",
            sourceApp = "com.test.app",
            version = 5,
            lastModified = 1111111111L,
            rpcUrl = "rpc",
            useHttps = true,
            trustSelfSignedCert = false
        )

        val syncableProfile = SyncableProfile.fromProfileData(originalData)
        val fieldMap = syncableProfile.toFieldMap()

        val newSyncableProfile = SyncableProfile.fromProfileData(
            ProfileData(
                id = "dummy",
                name = "dummy",
                host = "dummy",
                port = 0,
                isDefault = false,
                serviceType = "dummy",
                torrentClientType = null,
                username = null,
                password = null,
                sourceApp = "dummy"
            )
        )

        newSyncableProfile.fromFieldMap(fieldMap)
        val reconstructedData = newSyncableProfile.getProfileData()

        assertEquals(originalData.id, reconstructedData.id)
        assertEquals(originalData.name, reconstructedData.name)
        assertEquals(originalData.host, reconstructedData.host)
        assertEquals(originalData.port, reconstructedData.port)
        assertEquals(originalData.isDefault, reconstructedData.isDefault)
        assertEquals(originalData.serviceType, reconstructedData.serviceType)
        assertEquals(originalData.torrentClientType, reconstructedData.torrentClientType)
        assertEquals(originalData.username, reconstructedData.username)
        assertEquals(originalData.password, reconstructedData.password)
        assertEquals(originalData.sourceApp, reconstructedData.sourceApp)
        assertEquals(originalData.version, reconstructedData.version)
        assertEquals(originalData.lastModified, reconstructedData.lastModified)
        assertEquals(originalData.rpcUrl, reconstructedData.rpcUrl)
        assertEquals(originalData.useHttps, reconstructedData.useHttps)
        assertEquals(originalData.trustSelfSignedCert, reconstructedData.trustSelfSignedCert)
    }

    @Test
    fun `test field map round trip with Long port value`() {
        val originalData = ProfileData(
            id = "long-port-test",
            name = "Long Port Test",
            host = "localhost",
            port = 65535,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_QBITTORRENT,
            username = null,
            password = null,
            sourceApp = "com.test.app"
        )

        val syncableProfile = SyncableProfile.fromProfileData(originalData)
        val fieldMap = syncableProfile.toFieldMap().toMutableMap()

        // Simulate Asinka converting Int to Long (which can happen during transmission)
        fieldMap["port"] = 65535L
        fieldMap["version"] = 1L

        val newSyncableProfile = SyncableProfile.fromProfileData(
            ProfileData(
                id = "dummy",
                name = "dummy",
                host = "dummy",
                port = 0,
                isDefault = false,
                serviceType = "dummy",
                torrentClientType = null,
                username = null,
                password = null,
                sourceApp = "dummy"
            )
        )

        newSyncableProfile.fromFieldMap(fieldMap)
        val reconstructedData = newSyncableProfile.getProfileData()

        assertEquals(65535, reconstructedData.port)
        assertEquals(1, reconstructedData.version)
    }

    @Test
    fun `test field map with null optional fields`() {
        val profileData = ProfileData(
            id = "null-test",
            name = "Null Fields Test",
            host = "localhost",
            port = 8080,
            isDefault = false,
            serviceType = ProfileData.TYPE_METUBE,
            torrentClientType = null,
            username = null,
            password = null,
            sourceApp = "com.test.app",
            rpcUrl = null,
            useHttps = false,
            trustSelfSignedCert = false
        )

        val syncableProfile = SyncableProfile.fromProfileData(profileData)
        val fieldMap = syncableProfile.toFieldMap()

        assertNull(fieldMap["torrentClientType"])
        assertNull(fieldMap["username"])
        assertNull(fieldMap["password"])
        assertNull(fieldMap["rpcUrl"])
    }

    @Test
    fun `test version handling with Int type`() {
        val profileData = ProfileData(
            id = "version-int-test",
            name = "Version Int Test",
            host = "localhost",
            port = 8080,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_TRANSMISSION,
            username = null,
            password = null,
            sourceApp = "com.test.app",
            version = 10
        )

        val syncableProfile = SyncableProfile.fromProfileData(profileData)
        val fieldMap = syncableProfile.toFieldMap().toMutableMap()
        fieldMap["version"] = 10  // Int type

        val newSyncableProfile = SyncableProfile.fromProfileData(
            ProfileData(
                id = "dummy",
                name = "dummy",
                host = "dummy",
                port = 0,
                isDefault = false,
                serviceType = "dummy",
                torrentClientType = null,
                username = null,
                password = null,
                sourceApp = "dummy",
                version = 1
            )
        )

        newSyncableProfile.fromFieldMap(fieldMap)
        assertEquals(10, newSyncableProfile.version)
    }

    @Test
    fun `test Transmission-specific fields are preserved`() {
        val profileData = ProfileData(
            id = "transmission-specific",
            name = "Transmission Specific",
            host = "transmission.local",
            port = 9091,
            isDefault = false,
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_TRANSMISSION,
            username = "trans_user",
            password = "trans_pass",
            sourceApp = "com.transmissionconnect",
            rpcUrl = "custom/rpc/path",
            useHttps = true,
            trustSelfSignedCert = true
        )

        val syncableProfile = SyncableProfile.fromProfileData(profileData)
        val fieldMap = syncableProfile.toFieldMap()

        assertEquals("custom/rpc/path", fieldMap["rpcUrl"])
        assertEquals(true, fieldMap["useHttps"])
        assertEquals(true, fieldMap["trustSelfSignedCert"])
    }
}
