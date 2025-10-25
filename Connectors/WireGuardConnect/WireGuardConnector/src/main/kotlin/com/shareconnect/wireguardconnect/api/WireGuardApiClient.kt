package com.shareconnect.wireguardconnect.api

import android.util.Log
import com.shareconnect.wireguardconnect.data.models.WireGuardResult
import com.shareconnect.wireguardconnect.data.models.WireGuardStatistics
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * API client for WireGuard server management.
 * This is used when the WireGuard server exposes a management API.
 */
class WireGuardApiClient(
    private val baseUrl: String,
    private val username: String? = null,
    private val password: String? = null
) {

    companion object {
        private const val TAG = "WireGuardApiClient"
        private const val DEFAULT_TIMEOUT = 30_000L
    }

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = DEFAULT_TIMEOUT
            connectTimeoutMillis = DEFAULT_TIMEOUT
        }

        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)

            if (!username.isNullOrBlank() && !password.isNullOrBlank()) {
                basicAuth(username, password)
            }
        }
    }

    /**
     * Gets the server status.
     */
    suspend fun getServerStatus(): WireGuardResult<ServerStatus> = withContext(Dispatchers.IO) {
        try {
            val response: ServerStatus = client.get("/api/status").body()
            Log.d(TAG, "Server status retrieved: $response")
            WireGuardResult.Success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting server status", e)
            WireGuardResult.Error("Failed to get server status: ${e.message}", e)
        }
    }

    /**
     * Gets the list of peers.
     */
    suspend fun getPeers(): WireGuardResult<List<PeerInfo>> = withContext(Dispatchers.IO) {
        try {
            val response: PeerListResponse = client.get("/api/peers").body()
            Log.d(TAG, "Retrieved ${response.peers.size} peers")
            WireGuardResult.Success(response.peers)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting peers", e)
            WireGuardResult.Error("Failed to get peers: ${e.message}", e)
        }
    }

    /**
     * Gets statistics for a specific peer.
     */
    suspend fun getPeerStatistics(publicKey: String): WireGuardResult<WireGuardStatistics> =
        withContext(Dispatchers.IO) {
            try {
                val response: PeerStatisticsResponse = client.get("/api/peers/$publicKey/stats").body()
                val stats = WireGuardStatistics(
                    bytesReceived = response.rx,
                    bytesSent = response.tx,
                    lastHandshakeTime = response.lastHandshake,
                    peersConnected = if (response.connected) 1 else 0
                )
                Log.d(TAG, "Peer statistics retrieved for $publicKey")
                WireGuardResult.Success(stats)
            } catch (e: Exception) {
                Log.e(TAG, "Error getting peer statistics", e)
                WireGuardResult.Error("Failed to get peer statistics: ${e.message}", e)
            }
        }

    /**
     * Adds a new peer to the server.
     */
    suspend fun addPeer(
        publicKey: String,
        allowedIps: List<String>,
        presharedKey: String? = null
    ): WireGuardResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = AddPeerRequest(
                publicKey = publicKey,
                allowedIps = allowedIps,
                presharedKey = presharedKey
            )
            client.post("/api/peers") {
                setBody(request)
            }
            Log.d(TAG, "Peer added successfully: $publicKey")
            WireGuardResult.Success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding peer", e)
            WireGuardResult.Error("Failed to add peer: ${e.message}", e)
        }
    }

    /**
     * Removes a peer from the server.
     */
    suspend fun removePeer(publicKey: String): WireGuardResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            client.delete("/api/peers/$publicKey")
            Log.d(TAG, "Peer removed successfully: $publicKey")
            WireGuardResult.Success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing peer", e)
            WireGuardResult.Error("Failed to remove peer: ${e.message}", e)
        }
    }

    /**
     * Gets the server configuration.
     */
    suspend fun getServerConfig(): WireGuardResult<String> = withContext(Dispatchers.IO) {
        try {
            val response: ConfigResponse = client.get("/api/config").body()
            Log.d(TAG, "Server configuration retrieved")
            WireGuardResult.Success(response.config)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting server config", e)
            WireGuardResult.Error("Failed to get server configuration: ${e.message}", e)
        }
    }

    /**
     * Updates the server configuration.
     */
    suspend fun updateServerConfig(config: String): WireGuardResult<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val request = ConfigRequest(config)
                client.put("/api/config") {
                    setBody(request)
                }
                Log.d(TAG, "Server configuration updated")
                WireGuardResult.Success(true)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating server config", e)
                WireGuardResult.Error("Failed to update server configuration: ${e.message}", e)
            }
        }

    /**
     * Tests the connection to the server.
     */
    suspend fun testConnection(): WireGuardResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            client.get("/api/ping")
            Log.d(TAG, "Connection test successful")
            WireGuardResult.Success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Connection test failed", e)
            WireGuardResult.Error("Connection test failed: ${e.message}", e)
        }
    }

    /**
     * Closes the HTTP client.
     */
    fun close() {
        client.close()
    }

    // Data transfer objects

    @Serializable
    data class ServerStatus(
        val running: Boolean,
        val interface: String,
        val listenPort: Int,
        val publicKey: String,
        val peerCount: Int
    )

    @Serializable
    data class PeerInfo(
        val publicKey: String,
        val allowedIps: List<String>,
        val endpoint: String? = null,
        val lastHandshake: Long? = null,
        val rx: Long = 0,
        val tx: Long = 0,
        val connected: Boolean = false
    )

    @Serializable
    data class PeerListResponse(
        val peers: List<PeerInfo>
    )

    @Serializable
    data class PeerStatisticsResponse(
        val rx: Long,
        val tx: Long,
        val lastHandshake: Long?,
        val connected: Boolean
    )

    @Serializable
    data class AddPeerRequest(
        val publicKey: String,
        val allowedIps: List<String>,
        val presharedKey: String? = null
    )

    @Serializable
    data class ConfigResponse(
        val config: String
    )

    @Serializable
    data class ConfigRequest(
        val config: String
    )
}
