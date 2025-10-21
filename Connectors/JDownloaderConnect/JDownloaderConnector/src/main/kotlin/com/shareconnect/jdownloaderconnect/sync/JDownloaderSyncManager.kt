package com.shareconnect.jdownloaderconnect.sync

import android.content.Context
import android.util.Log
import digital.vasic.asinka.AsinkaClient
import digital.vasic.asinka.AsinkaConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ServerSocket

/**
 * JDownloaderSyncManager handles synchronization of JDownloader data across ShareConnect ecosystem
 * Uses Asinka for real-time IPC communication between apps
 */
class JDownloaderSyncManager private constructor(
    private val context: Context,
    private val appIdentifier: String,
    private val appName: String,
    private val appVersion: String,
    private val asinkaClient: AsinkaClient
) {
    
    companion object {
        private const val BASE_PORT = 8970
        
        @Volatile
        private var INSTANCE: JDownloaderSyncManager? = null
        
        fun getInstance(
            context: Context,
            appId: String,
            appName: String,
            appVersion: String
        ): JDownloaderSyncManager {
            return INSTANCE ?: synchronized(this) {
                val preferredPort = calculatePreferredPort(appId)
                
                val asinkaConfig = AsinkaConfig(
                    appId = appId,
                    appName = appName,
                    appVersion = appVersion,
                    serverPort = preferredPort,
                    serviceName = "jdownloader-sync",
                    exposedSchemas = emptyList(), // TODO: Add JDownloader sync schemas
                    capabilities = mapOf("jdownloader_sync" to "1.0")
                )
                
                val asinkaClient = AsinkaClient.create(context, asinkaConfig)
                
                INSTANCE ?: JDownloaderSyncManager(context, appId, appName, appVersion, asinkaClient).also {
                    INSTANCE = it
                }
            }
        }
        
        private fun calculatePreferredPort(appId: String): Int {
            val preferredPort = BASE_PORT + Math.abs(appId.hashCode() % 100)
            return findAvailablePort(preferredPort)
        }
        
        private fun findAvailablePort(preferredPort: Int): Int {
            return try {
                ServerSocket(preferredPort).use { socket ->
                    socket.localPort
                }
            } catch (e: Exception) {
                // Port is in use, find next available
                var port = preferredPort + 1
                while (port < preferredPort + 100) {
                    try {
                        ServerSocket(port).use { socket ->
                            return socket.localPort
                        }
                    } catch (e: Exception) {
                        port++
                    }
                }
                // Fallback to system-assigned port
                0
            }
        }
    }
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isStarted = false
    
    /**
     * Start synchronization with other ShareConnect apps
     */
    suspend fun startSync() {
        if (isStarted) return
        isStarted = true
        
        try {
            asinkaClient.start()
            Log.d("JDownloaderSyncManager", "Sync started successfully")
        } catch (e: Exception) {
            if (e.message?.contains("bind failed: EADDRINUSE") == true) {
                Log.w("JDownloaderSyncManager", "Port conflict detected, retrying with different port", e)
                // Force recreation of singleton with new port
                INSTANCE = null
                val newInstance = getInstance(context, appIdentifier, appName, appVersion)
                newInstance.startSync()
            } else {
                Log.e("JDownloaderSyncManager", "Failed to start sync", e)
                throw e
            }
        }
    }
    
    /**
     * Stop synchronization
     */
    suspend fun stopSync() {
        if (!isStarted) return
        isStarted = false
        
        try {
            asinkaClient.stop()
            Log.d("JDownloaderSyncManager", "Sync stopped successfully")
        } catch (e: Exception) {
            Log.e("JDownloaderSyncManager", "Failed to stop sync", e)
        }
    }
    
    /**
     * Broadcast download status update to other apps
     */
    fun broadcastDownloadStatus(downloadId: String, status: String, progress: Int) {
        scope.launch {
            try {
                // TODO: Implement broadcast logic using Asinka sync objects
                Log.d("JDownloaderSyncManager", "Broadcasting download status: $downloadId, $status, $progress")
            } catch (e: Exception) {
                Log.e("JDownloaderSyncManager", "Failed to broadcast download status", e)
            }
        }
    }
    
    /**
     * Broadcast account connection status
     */
    fun broadcastAccountStatus(accountId: String, isConnected: Boolean) {
        scope.launch {
            try {
                // TODO: Implement broadcast logic using Asinka sync objects
                Log.d("JDownloaderSyncManager", "Broadcasting account status: $accountId, $isConnected")
            } catch (e: Exception) {
                Log.e("JDownloaderSyncManager", "Failed to broadcast account status", e)
            }
        }
    }
    
    /**
     * Check if sync is active
     */
    fun isSyncActive(): Boolean = isStarted
}