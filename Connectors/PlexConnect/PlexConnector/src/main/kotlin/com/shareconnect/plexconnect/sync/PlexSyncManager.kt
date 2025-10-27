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


package com.shareconnect.plexconnect.sync

import android.content.Context
import android.util.Log
import digital.vasic.asinka.AsinkaClient
import digital.vasic.asinka.AsinkaConfig
import digital.vasic.asinka.models.ObjectSchema
import digital.vasic.asinka.models.FieldSchema
import digital.vasic.asinka.models.FieldType
import digital.vasic.asinka.sync.SyncChange
import com.shareconnect.plexconnect.data.model.PlexServer
import com.shareconnect.plexconnect.data.repository.PlexServerRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.ServerSocket

class PlexSyncManager private constructor(
    private val context: Context,
    private val appIdentifier: String,
    private val appName: String,
    private val appVersion: String,
    private val asinkaClient: AsinkaClient,
    private val serverRepository: PlexServerRepository
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _serverChangeFlow = MutableSharedFlow<PlexServer>()
    val serverChangeFlow: Flow<PlexServer> = _serverChangeFlow.asSharedFlow()

    private var isStarted = false

    suspend fun start() {
        if (isStarted) return
        isStarted = true

        // Start Asinka client with retry logic for port conflicts
        try {
            asinkaClient.start()
        } catch (e: Exception) {
            if (e.message?.contains("bind failed: EADDRINUSE") == true) {
                Log.w("PlexSyncManager", "Port conflict detected, retrying with different port", e)
                // Force recreation of singleton with new port
                synchronized(PlexSyncManager::class.java) {
                    INSTANCE = null
                }
                // Recreate with new port
                val newInstance = getInstance(context, appIdentifier, appName, appVersion, serverRepository)
                newInstance.asinkaClient.start()
                // Update the instance reference
                synchronized(PlexSyncManager::class.java) {
                    INSTANCE = newInstance
                }
            } else {
                throw e
            }
        }

        // Register existing servers with Asinka
        val existingServers = serverRepository.getAllServers()
        existingServers.collect { servers ->
            servers.forEach { server ->
                val syncableServer = SyncablePlexServer.fromPlexServer(server)
                asinkaClient.syncManager.registerObject(syncableServer)
            }
        }

        // Observe Asinka sync changes
        scope.launch {
            asinkaClient.syncManager.observeAllChanges().collect { change ->
                when (change) {
                    is SyncChange.Updated -> {
                        val syncableServer = change.obj as? SyncablePlexServer
                        syncableServer?.let {
                            val serverData = it.getPlexServer()
                            // Don't sync tokens from other apps for security
                            if (serverData.token != null) {
                                val serverWithoutToken = serverData.copy(token = null)
                                serverRepository.updateServer(serverWithoutToken)
                            } else {
                                serverRepository.updateServer(serverData)
                            }
                            _serverChangeFlow.emit(serverData)
                        }
                    }
                    is SyncChange.Deleted -> {
                        try {
                            val serverId = change.objectId.toLong()
                            serverRepository.deleteServerById(serverId)
                        } catch (e: NumberFormatException) {
                            Log.w("PlexSyncManager", "Invalid server ID format: ${change.objectId}", e)
                        }
                    }
                }
            }
        }
    }

    suspend fun stop() {
        isStarted = false
        asinkaClient.stop()
    }

    suspend fun addServer(server: PlexServer) {
        serverRepository.addServer(server)
        val syncableServer = SyncablePlexServer.fromPlexServer(server)
        asinkaClient.syncManager.registerObject(syncableServer)
        _serverChangeFlow.emit(server)
    }

    suspend fun updateServer(server: PlexServer) {
        val updatedServer = server.copy(
            updatedAt = System.currentTimeMillis()
        )
        serverRepository.updateServer(updatedServer)

        val syncableServer = SyncablePlexServer.fromPlexServer(updatedServer)
        asinkaClient.syncManager.updateObject(
            updatedServer.id.toString(),
            syncableServer.toFieldMap()
        )
        _serverChangeFlow.emit(updatedServer)
    }

    suspend fun deleteServer(server: PlexServer) {
        serverRepository.deleteServer(server)
        asinkaClient.syncManager.deleteObject(server.id.toString())
    }

    fun getAllServers(): Flow<List<PlexServer>> = serverRepository.getAllServers()

    suspend fun getServerById(serverId: Long): PlexServer? = serverRepository.getServerById(serverId)

    suspend fun connect(host: String, port: Int): Result<AsinkaClient.SessionInfo> {
        return asinkaClient.connect(host, port)
    }

    suspend fun disconnect(sessionId: String) {
        asinkaClient.disconnect(sessionId)
    }

    fun getSessions(): List<AsinkaClient.SessionInfo> = asinkaClient.getSessions()

    companion object {
        @Volatile
        private var INSTANCE: PlexSyncManager? = null

        /**
         * Check if a port is available
         */
        private fun isPortAvailable(port: Int): Boolean {
            return try {
                ServerSocket(port).use { true }
            } catch (e: Exception) {
                false
            }
        }

        /**
         * Find an available port starting from the preferred port
         */
        private fun findAvailablePort(preferredPort: Int, maxAttempts: Int = 10): Int {
            var port = preferredPort
            for (i in 0 until maxAttempts) {
                if (isPortAvailable(port)) {
                    return port
                }
                port++
            }
            throw IllegalStateException("No available ports found in range $preferredPort-${preferredPort + maxAttempts - 1}")
        }

        fun getInstance(
            context: Context,
            appId: String,
            appName: String,
            appVersion: String,
            serverRepository: PlexServerRepository
        ): PlexSyncManager {
            return INSTANCE ?: synchronized(this) {
                val serverSchema = ObjectSchema(
                    objectType = "plex_server",
                    version = "1",
                    fields = listOf(
                        FieldSchema("id", FieldType.LONG),
                        FieldSchema("name", FieldType.STRING),
                        FieldSchema("address", FieldType.STRING),
                        FieldSchema("port", FieldType.INT),
                        FieldSchema("isLocal", FieldType.BOOLEAN),
                        FieldSchema("isOwned", FieldType.BOOLEAN),
                        FieldSchema("ownerId", FieldType.LONG),
                        FieldSchema("machineIdentifier", FieldType.STRING),
                        FieldSchema("version", FieldType.STRING),
                        FieldSchema("createdAt", FieldType.LONG),
                        FieldSchema("updatedAt", FieldType.LONG)
                    )
                )

                val basePort = 8960
                val preferredPort = basePort + Math.abs(appId.hashCode() % 100)
                val uniquePort = findAvailablePort(preferredPort)

                Log.d("PlexSyncManager", "App $appId using port $uniquePort (preferred: $preferredPort)")

                val asinkaConfig = AsinkaConfig(
                    appId = appId,
                    appName = appName,
                    appVersion = appVersion,
                    serverPort = uniquePort,
                    serviceName = "plex-sync",
                    exposedSchemas = listOf(serverSchema),
                    capabilities = mapOf("plex_sync" to "1.0")
                )

                val asinkaClient = AsinkaClient.create(context, asinkaConfig)

                PlexSyncManager(context.applicationContext, appId, appName, appVersion, asinkaClient, serverRepository).also { INSTANCE = it }
            }
        }

        fun resetInstance() {
            INSTANCE = null
        }
    }
}