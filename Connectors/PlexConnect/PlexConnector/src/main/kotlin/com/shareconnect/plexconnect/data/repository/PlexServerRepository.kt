package com.shareconnect.plexconnect.data.repository

import com.shareconnect.plexconnect.data.api.PlexApiClient
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