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


package com.shareconnect.plexconnect.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.plexconnect.data.database.dao.PlexServerDao
import com.shareconnect.plexconnect.data.model.PlexServer
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlexDatabaseIntegrationTest {

    private lateinit var database: PlexDatabase
    private lateinit var serverDao: PlexServerDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PlexDatabase::class.java
        ).build()
        serverDao = database.plexServerDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveServer_worksCorrectly() = runTest {
        // Given
        val server = PlexServer(
            name = "Test Server",
            address = "192.168.1.100",
            port = 32400,
            token = "test-token",
            isLocal = true,
            isOwned = true,
            machineIdentifier = "test-machine-id",
            version = "1.32.0.0"
        )

        // When
        val insertedId = serverDao.insertServer(server)
        val retrievedServer = serverDao.getServerById(insertedId)

        // Then
        assertNotNull(retrievedServer)
        assertEquals(insertedId, retrievedServer?.id)
        assertEquals(server.name, retrievedServer?.name)
        assertEquals(server.address, retrievedServer?.address)
        assertEquals(server.port, retrievedServer?.port)
        assertEquals(server.token, retrievedServer?.token)
        assertEquals(server.isLocal, retrievedServer?.isLocal)
        assertEquals(server.isOwned, retrievedServer?.isOwned)
        assertEquals(server.machineIdentifier, retrievedServer?.machineIdentifier)
        assertEquals(server.version, retrievedServer?.version)
    }

    @Test
    fun getAllServers_returnsAllInsertedServers() = runTest {
        // Given
        val server1 = PlexServer(name = "Server 1", address = "192.168.1.100")
        val server2 = PlexServer(name = "Server 2", address = "192.168.1.101")
        serverDao.insertServer(server1)
        serverDao.insertServer(server2)

        // When
        val servers = serverDao.getAllServers()

        // Then
        servers.collect { serverList ->
            assertEquals(2, serverList.size)
            assertTrue(serverList.any { it.name == "Server 1" })
            assertTrue(serverList.any { it.name == "Server 2" })
        }
    }

    @Test
    fun getLocalServers_returnsOnlyLocalServers() = runTest {
        // Given
        val localServer = PlexServer(name = "Local Server", address = "192.168.1.100", isLocal = true)
        val remoteServer = PlexServer(name = "Remote Server", address = "plex.tv", isLocal = false)
        serverDao.insertServer(localServer)
        serverDao.insertServer(remoteServer)

        // When
        val localServers = serverDao.getLocalServers()

        // Then
        localServers.collect { serverList ->
            assertEquals(1, serverList.size)
            assertEquals("Local Server", serverList.first().name)
        }
    }

    @Test
    fun getOwnedServers_returnsOnlyOwnedServers() = runTest {
        // Given
        val ownedServer = PlexServer(name = "Owned Server", address = "192.168.1.100", isOwned = true)
        val sharedServer = PlexServer(name = "Shared Server", address = "192.168.1.101", isOwned = false)
        serverDao.insertServer(ownedServer)
        serverDao.insertServer(sharedServer)

        // When
        val ownedServers = serverDao.getOwnedServers()

        // Then
        ownedServers.collect { serverList ->
            assertEquals(1, serverList.size)
            assertEquals("Owned Server", serverList.first().name)
        }
    }

    @Test
    fun getServerByMachineIdentifier_returnsCorrectServer() = runTest {
        // Given
        val machineId = "unique-machine-id"
        val server = PlexServer(
            name = "Test Server",
            address = "192.168.1.100",
            machineIdentifier = machineId
        )
        serverDao.insertServer(server)

        // When
        val retrievedServer = serverDao.getServerByMachineIdentifier(machineId)

        // Then
        assertNotNull(retrievedServer)
        assertEquals(machineId, retrievedServer?.machineIdentifier)
    }

    @Test
    fun getServerCount_returnsCorrectCount() = runTest {
        // Given
        serverDao.insertServer(PlexServer(name = "Server 1", address = "192.168.1.100"))
        serverDao.insertServer(PlexServer(name = "Server 2", address = "192.168.1.101"))

        // When
        val count = serverDao.getServerCount()

        // Then
        assertEquals(2, count)
    }

    @Test
    fun getAuthenticatedServerCount_returnsOnlyServersWithTokens() = runTest {
        // Given
        serverDao.insertServer(PlexServer(name = "Authenticated", address = "192.168.1.100", token = "token123"))
        serverDao.insertServer(PlexServer(name = "Not Authenticated", address = "192.168.1.101", token = null))

        // When
        val count = serverDao.getAuthenticatedServerCount()

        // Then
        assertEquals(1, count)
    }

    @Test
    fun updateServer_modifiesExistingServer() = runTest {
        // Given
        val server = PlexServer(name = "Original Name", address = "192.168.1.100")
        val insertedId = serverDao.insertServer(server)
        val updatedServer = server.copy(id = insertedId, name = "Updated Name")

        // When
        serverDao.updateServer(updatedServer)
        val retrievedServer = serverDao.getServerById(insertedId)

        // Then
        assertNotNull(retrievedServer)
        assertEquals("Updated Name", retrievedServer?.name)
    }

    @Test
    fun deleteServerById_removesServer() = runTest {
        // Given
        val server = PlexServer(name = "Test Server", address = "192.168.1.100")
        val insertedId = serverDao.insertServer(server)

        // When
        serverDao.deleteServerById(insertedId)
        val retrievedServer = serverDao.getServerById(insertedId)

        // Then
        assertNull(retrievedServer)
    }
}