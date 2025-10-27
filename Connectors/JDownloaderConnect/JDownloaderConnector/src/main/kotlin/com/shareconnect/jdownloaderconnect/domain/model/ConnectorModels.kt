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


package com.shareconnect.jdownloaderconnect.domain.model

import com.shareconnect.jdownloaderconnect.data.model.ServerProfile
import com.shareconnect.jdownloaderconnect.data.model.ServiceType
import kotlinx.serialization.Serializable

/**
 * Generic connector interface that all service connectors must implement
 */
interface Connector {
    val serviceType: ServiceType
    val profile: ServerProfile

    /**
     * Test connection to the service
     */
    suspend fun testConnection(): Result<Unit>

    /**
     * Get service health status
     */
    suspend fun getHealthStatus(): Result<HealthStatus>

    /**
     * Get basic service information
     */
    suspend fun getServiceInfo(): Result<ServiceInfo>

    /**
     * Cleanup resources when connector is no longer needed
     */
    fun cleanup()
}

/**
 * Authentication methods supported by connectors
 */
@Serializable
sealed class AuthenticationMethod {
    @Serializable
    data class ApiKey(val key: String, val headerName: String = "Authorization") : AuthenticationMethod()

    @Serializable
    data class BearerToken(val token: String) : AuthenticationMethod()

    @Serializable
    data class BasicAuth(val username: String, val password: String) : AuthenticationMethod()

    @Serializable
    data class OAuth2(
        val clientId: String,
        val clientSecret: String,
        val tokenUrl: String,
        val scope: String? = null
    ) : AuthenticationMethod()

    @Serializable
    data class CustomHeader(val headerName: String, val headerValue: String) : AuthenticationMethod()

    @Serializable
    object NoAuth : AuthenticationMethod()
}

/**
 * Health status of a service
 */
@Serializable
data class HealthStatus(
    val isHealthy: Boolean,
    val status: String,
    val responseTime: Long? = null,
    val lastChecked: Long = System.currentTimeMillis(),
    val errorMessage: String? = null,
    val version: String? = null,
    val uptime: Long? = null
)

/**
 * Basic service information
 */
@Serializable
data class ServiceInfo(
    val name: String,
    val version: String? = null,
    val description: String? = null,
    val capabilities: List<String> = emptyList(),
    val endpoints: Map<String, String> = emptyMap()
)

/**
 * Generic operation result
 */
@Serializable
data class OperationResult<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Connector configuration
 */
@Serializable
data class ConnectorConfig(
    val timeout: Int = 30,
    val retryCount: Int = 3,
    val retryDelay: Long = 1000,
    val enableCaching: Boolean = true,
    val cacheTimeout: Long = 300000, // 5 minutes
    val enableLogging: Boolean = true
)

/**
 * Generic file/directory item
 */
@Serializable
data class FileItem(
    val name: String,
    val path: String,
    val size: Long? = null,
    val modified: Long? = null,
    val isDirectory: Boolean = false,
    val permissions: String? = null,
    val mimeType: String? = null,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Download/upload progress information
 */
@Serializable
data class TransferProgress(
    val id: String,
    val filename: String,
    val totalSize: Long,
    val downloadedSize: Long,
    val speed: Long, // bytes per second
    val eta: Long, // estimated time in seconds
    val status: TransferStatus,
    val error: String? = null
)

@Serializable
enum class TransferStatus {
    QUEUED, DOWNLOADING, PAUSED, COMPLETED, FAILED, CANCELLED
}

/**
 * Generic service operation types
 */
enum class Operation {
    READ, WRITE, DELETE, LIST, UPLOAD, DOWNLOAD, STREAM, EXECUTE
}

/**
 * Connector capabilities
 */
@Serializable
data class ConnectorCapabilities(
    val supportedOperations: Set<Operation>,
    val supportedAuthMethods: Set<String>,
    val maxConcurrentConnections: Int = 10,
    val supportsStreaming: Boolean = false,
    val supportsPartialTransfers: Boolean = false,
    val supportsDirectoryOperations: Boolean = true
)