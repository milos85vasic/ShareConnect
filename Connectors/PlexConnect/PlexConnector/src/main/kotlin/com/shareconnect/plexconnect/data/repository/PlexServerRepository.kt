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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
class PlexServerRepository(
    private val serverDao: PlexServerDao,
    private val apiClient: PlexApiClient
) {

    fun getAllServers(): Flow<List<PlexServer>> = serverDao.getAllServers()

    fun getLocalServers(): Flow<List<PlexServer>> = serverDao.getLocalServers()

    fun getOwnedServers(): Flow<List<PlexServer>> = serverDao.getOwnedServers()

    suspend fun getServerById(serverId: Long): PlexServer? = serverDao.getServerById(serverId)

    suspend fun getServerByMachineIdentifier(machineIdentifier: String): PlexServer? =
        serverDao.getServerByMachineIdentifier(machineIdentifier)

    suspend fun addServer(server: PlexServer): Long {
        val serverWithTimestamp = server.copy(updatedAt = System.currentTimeMillis())
        return serverDao.insertServer(serverWithTimestamp)
    }

    suspend fun updateServer(server: PlexServer) {
        val serverWithTimestamp = server.copy(updatedAt = System.currentTimeMillis())
        serverDao.updateServer(serverWithTimestamp)
    }

    suspend fun deleteServer(server: PlexServer) = serverDao.deleteServer(server)

    suspend fun deleteServerById(serverId: Long) = serverDao.deleteServerById(serverId)

    suspend fun refreshServerInfo(server: PlexServer): Result<PlexServer> {
        return try {
            val serverInfoResult = apiClient.getServerInfo(server.baseUrl)
            serverInfoResult.fold(
                onSuccess = { serverInfo ->
                    val updatedServer = server.copy(
                        name = serverInfo.name ?: server.name,
                        machineIdentifier = serverInfo.machineIdentifier ?: server.machineIdentifier,
                        version = serverInfo.version ?: server.version,
                        isOwned = serverInfo.owned == 1,
                        updatedAt = System.currentTimeMillis()
                    )
                    updateServer(updatedServer)
                    Result.success(updatedServer)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun testServerConnection(server: PlexServer): Result<PlexServerInfo> {
        return try {
            apiClient.getServerInfo(server.baseUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun authenticateServer(server: PlexServer, token: String): Result<PlexServer> {
        return try {
            // Test authentication by trying to get server info
            val serverInfoResult = apiClient.getServerInfo("${server.baseUrl}?X-Plex-Token=$token")
            serverInfoResult.fold(
                onSuccess = { serverInfo ->
                    val authenticatedServer = server.copy(
                        token = token,
                        name = serverInfo.name ?: server.name,
                        machineIdentifier = serverInfo.machineIdentifier ?: server.machineIdentifier,
                        version = serverInfo.version ?: server.version,
                        isOwned = serverInfo.owned == 1,
                        updatedAt = System.currentTimeMillis()
                    )
                    updateServer(authenticatedServer)
                    Result.success(authenticatedServer)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getServerCount(): Int = serverDao.getServerCount()

    suspend fun getAuthenticatedServerCount(): Int = serverDao.getAuthenticatedServerCount()
}