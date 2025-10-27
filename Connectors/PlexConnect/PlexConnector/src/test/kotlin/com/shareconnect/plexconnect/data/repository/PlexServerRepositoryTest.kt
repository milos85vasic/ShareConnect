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


package com.shareconnect.plexconnect.data.repository

import com.shareconnect.plexconnect.data.api.PlexApiClient
import com.shareconnect.plexconnect.data.api.PlexServerInfo
import com.shareconnect.plexconnect.data.database.dao.PlexServerDao
import com.shareconnect.plexconnect.data.model.PlexServer
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class PlexServerRepositoryTest {

    private lateinit var repository: PlexServerRepository
    private lateinit var mockDao: PlexServerDao
    private lateinit var mockApiClient: PlexApiClient

    @Before
    fun setup() {
        mockDao = mockk()
        mockApiClient = mockk()
        repository = PlexServerRepository(mockDao, mockApiClient)
    }

    @Test
    fun `getAllServers returns flow from dao`() = runTest {
        // Given
        val expectedServers = listOf(
            PlexServer(id = 1, name = "Server 1", address = "server1.com"),
            PlexServer(id = 2, name = "Server 2", address = "server2.com")
        )
        every { mockDao.getAllServers() } returns flowOf(expectedServers)

        // When
        val result = repository.getAllServers()

        // Then
        result.collect { servers ->
            assertEquals(expectedServers, servers)
        }
    }

    @Test
    fun `getServerCount returns count from dao`() = runTest {
        // Given
        coEvery { mockDao.getServerCount() } returns 5

        // When
        val result = repository.getServerCount()

        // Then
        assertEquals(5, result)
    }

    @Test
    fun `addServer adds server with timestamp and returns id`() = runTest {
        // Given
        val server = PlexServer(name = "Test Server", address = "test.com")
        val expectedId = 123L
        coEvery { mockDao.insertServer(any()) } returns expectedId

        // When
        val result = repository.addServer(server)

        // Then
        assertEquals(expectedId, result)
        coVerify {
            mockDao.insertServer(withArg { insertedServer ->
                assertEquals(server.name, insertedServer.name)
                assertEquals(server.address, insertedServer.address)
                assertTrue(insertedServer.updatedAt > 0)
            })
        }
    }

    @Test
    fun `deleteServerById calls dao delete`() = runTest {
        // Given
        val serverId = 456L
        coEvery { mockDao.deleteServerById(serverId) } returns Unit

        // When
        repository.deleteServerById(serverId)

        // Then
        coVerify { mockDao.deleteServerById(serverId) }
    }

    @Test
    fun `testServerConnection returns server info on success`() = runTest {
        // Given
        val server = PlexServer(name = "Test Server", address = "test.com")
        val expectedInfo = PlexServerInfo(
            name = "Test Server",
            machineIdentifier = "machine123",
            version = "1.0.0",
            owned = 1
        )
        coEvery { mockApiClient.getServerInfo(server.baseUrl) } returns Result.success(expectedInfo)

        // When
        val result = repository.testServerConnection(server)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedInfo, result.getOrNull())
    }

    @Test
    fun `testServerConnection returns failure on api error`() = runTest {
        // Given
        val server = PlexServer(name = "Test Server", address = "test.com")
        val exception = IOException("Connection failed")
        coEvery { mockApiClient.getServerInfo(server.baseUrl) } returns Result.failure(exception)

        // When
        val result = repository.testServerConnection(server)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `authenticateServer updates server with token on success`() = runTest {
        // Given
        val server = PlexServer(id = 1, name = "Test Server", address = "test.com")
        val token = "auth_token_123"
        val serverInfo = PlexServerInfo(
            name = "Updated Server",
            machineIdentifier = "machine456",
            version = "2.0.0",
            owned = 1
        )
        coEvery { mockApiClient.getServerInfo("${server.baseUrl}?X-Plex-Token=$token") } returns Result.success(serverInfo)
        coEvery { mockDao.updateServer(any()) } returns Unit

        // When
        val result = repository.authenticateServer(server, token)

        // Then
        assertTrue(result.isSuccess)
        val authenticatedServer = result.getOrNull()!!
        assertEquals(token, authenticatedServer.token)
        assertEquals("Updated Server", authenticatedServer.name)
        assertEquals("machine456", authenticatedServer.machineIdentifier)
        assertEquals("2.0.0", authenticatedServer.version)
        assertTrue(authenticatedServer.isOwned)
        assertTrue(authenticatedServer.updatedAt > 0)

        coVerify { mockDao.updateServer(authenticatedServer) }
    }

    @Test
    fun `authenticateServer returns failure on api error`() = runTest {
        // Given
        val server = PlexServer(name = "Test Server", address = "test.com")
        val token = "auth_token_123"
        val exception = IOException("Authentication failed")
        coEvery { mockApiClient.getServerInfo("${server.baseUrl}?X-Plex-Token=$token") } returns Result.failure(exception)

        // When
        val result = repository.authenticateServer(server, token)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}