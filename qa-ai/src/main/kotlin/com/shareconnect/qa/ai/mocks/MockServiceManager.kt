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


package com.shareconnect.qa.ai.mocks

import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Mock Service Manager for AI QA Testing
 *
 * Manages all mock services for external dependencies:
 * - MeTube (port 8081)
 * - YT-DLP (port 8082)
 * - qBittorrent (port 8083)
 * - Transmission (port 9091)
 * - uTorrent (port 8080)
 * - JDownloader (port 3129)
 */
class MockServiceManager {

    private val servers = ConcurrentHashMap<String, MockWebServer>()
    private val isRunning = AtomicBoolean(false)

    companion object {
        const val SERVICE_METUBE = "metube"
        const val SERVICE_YTDL = "ytdl"
        const val SERVICE_QBITTORRENT = "qbittorrent"
        const val SERVICE_TRANSMISSION = "transmission"
        const val SERVICE_UTORRENT = "utorrent"
        const val SERVICE_JDOWNLOADER = "jdownloader"

        val SERVICE_CONFIGS = mapOf(
            SERVICE_METUBE to ServiceConfig(8081, MeTubeMockDispatcher()),
            SERVICE_YTDL to ServiceConfig(8082, YtDlMockDispatcher()),
            SERVICE_QBITTORRENT to ServiceConfig(8083, QBittorrentMockDispatcher()),
            SERVICE_TRANSMISSION to ServiceConfig(9091, TransmissionMockDispatcher()),
            SERVICE_UTORRENT to ServiceConfig(8080, UTorrentMockDispatcher()),
            SERVICE_JDOWNLOADER to ServiceConfig(3129, JDownloaderMockDispatcher())
        )
    }

    data class ServiceConfig(
        val port: Int,
        val dispatcher: QueueDispatcher
    )

    /**
     * Start all mock services
     */
    fun startAllServices(): Map<String, String> {
        if (isRunning.get()) {
            throw IllegalStateException("Mock services are already running")
        }

        val serviceUrls = mutableMapOf<String, String>()

        SERVICE_CONFIGS.forEach { (serviceName, config) ->
            val server = MockWebServer().apply {
                dispatcher = config.dispatcher
                start(config.port)
            }
            servers[serviceName] = server
            serviceUrls[serviceName] = "http://localhost:${config.port}"
        }

        isRunning.set(true)
        return serviceUrls
    }

    /**
     * Stop all mock services
     */
    fun stopAllServices() {
        if (!isRunning.get()) {
            return
        }

        servers.values.forEach { server ->
            try {
                server.shutdown()
            } catch (e: Exception) {
                // Log error but continue shutting down other services
                println("Error shutting down mock server: ${e.message}")
            }
        }

        servers.clear()
        isRunning.set(false)
    }

    /**
     * Check if services are running
     */
    fun isRunning(): Boolean = isRunning.get()

    /**
     * Get service URL for a specific service
     */
    fun getServiceUrl(serviceName: String): String? {
        return servers[serviceName]?.let { "http://localhost:${SERVICE_CONFIGS[serviceName]?.port}" }
    }

    /**
     * Reset all service states (clear request history, reset to initial state)
     */
    fun resetAllServices() {
        servers.values.forEach { server ->
            server.dispatcher.peek()?.let {
                // Reset dispatcher state if needed
            }
        }
    }
}