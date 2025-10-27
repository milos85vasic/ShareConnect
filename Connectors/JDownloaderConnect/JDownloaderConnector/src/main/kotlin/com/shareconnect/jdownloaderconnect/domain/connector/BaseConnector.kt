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


package com.shareconnect.jdownloaderconnect.domain.connector

import com.shareconnect.jdownloaderconnect.data.model.ServerProfile
import com.shareconnect.jdownloaderconnect.data.model.ServiceType
import com.shareconnect.jdownloaderconnect.domain.model.*
import com.shareconnect.jdownloaderconnect.network.api.MyJDownloaderApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

/**
 * Abstract base class for all service connectors providing common functionality
 */
abstract class BaseConnector(
    override val profile: ServerProfile,
    protected val config: ConnectorConfig = ConnectorConfig()
) : Connector {

    protected val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // HTTP client with timeout configuration
    protected val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(config.timeout.toLong(), TimeUnit.SECONDS)
            .readTimeout(config.timeout.toLong(), TimeUnit.SECONDS)
            .writeTimeout(config.timeout.toLong(), TimeUnit.SECONDS)
            .build()
    }

    // Cache for responses
    private val responseCache = ConcurrentHashMap<String, CachedResponse<Any>>()

    // Health status updates
    private val _healthUpdates = MutableSharedFlow<HealthStatus>()
    val healthUpdates: SharedFlow<HealthStatus> = _healthUpdates.asSharedFlow()

    override suspend fun testConnection(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val healthResult = getHealthStatus()
            if (healthResult.isSuccess && healthResult.getOrNull()?.isHealthy == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(healthResult.exceptionOrNull()?.message ?: "Connection test failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHealthStatus(): Result<HealthStatus> = withContext(Dispatchers.IO) {
        try {
            val startTime = System.currentTimeMillis()
            val healthResult = performHealthCheck()
            val responseTime = System.currentTimeMillis() - startTime

            val healthStatus = HealthStatus(
                isHealthy = healthResult.isSuccess,
                status = if (healthResult.isSuccess) "HEALTHY" else "UNHEALTHY",
                responseTime = responseTime,
                errorMessage = healthResult.exceptionOrNull()?.message
            )

            // Emit health update
            _healthUpdates.emit(healthStatus)

            Result.success(healthStatus)
        } catch (e: Exception) {
            val errorStatus = HealthStatus(
                isHealthy = false,
                status = "ERROR",
                errorMessage = e.message
            )
            _healthUpdates.emit(errorStatus)
            Result.failure(e)
        }
    }

    /**
     * Perform service-specific health check
     */
    protected abstract suspend fun performHealthCheck(): Result<Unit>

    /**
     * Get cached response if available and not expired
     */
    protected fun <T> getCachedResponse(key: String): T? {
        if (!config.enableCaching) return null

        val cached = responseCache[key] ?: return null
        if (System.currentTimeMillis() - cached.timestamp > config.cacheTimeout) {
            responseCache.remove(key)
            return null
        }
        @Suppress("UNCHECKED_CAST")
        return cached.data as? T
    }

    /**
     * Cache response data
     */
    protected fun <T> cacheResponse(key: String, data: T) {
        if (config.enableCaching) {
            @Suppress("UNCHECKED_CAST")
            responseCache[key] = CachedResponse(data as Any, System.currentTimeMillis())
        }
    }

    /**
     * Execute HTTP request with retry logic
     */
    protected suspend fun <T> executeWithRetry(
        operation: suspend () -> Response<T>
    ): Result<T> = withContext(Dispatchers.IO) {
        var lastException: Exception? = null

        repeat(config.retryCount + 1) { attempt ->
            try {
                val response = operation()

                return@withContext if (response.isSuccessful) {
                    Result.success(response.body() ?: throw Exception("Empty response body"))
                } else {
                    Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                lastException = e

                // Don't retry on certain errors
                if (e is IllegalArgumentException || e.message?.contains("auth", ignoreCase = true) == true) {
                    return@withContext Result.failure(e)
                }

                // Wait before retry (exponential backoff)
                if (attempt < config.retryCount) {
                    delay(config.retryDelay * (attempt + 1))
                }
            }
        }

        Result.failure(lastException ?: Exception("Unknown error"))
    }

    /**
     * Execute HTTP request without response body
     */
    protected suspend fun executeRequest(
        operation: suspend () -> Response<Unit>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        executeWithRetry {
            operation().let { response ->
                if (response.isSuccessful) {
                    Response.success(Unit, response.raw())
                } else {
                    response
                }
            }
        }
    }

    /**
     * Make authenticated HTTP request
     */
    protected fun Request.Builder.authenticate(authMethod: AuthenticationMethod): Request.Builder {
        return when (authMethod) {
            is AuthenticationMethod.ApiKey -> header(authMethod.headerName, authMethod.key)
            is AuthenticationMethod.BearerToken -> header("Authorization", "Bearer ${authMethod.token}")
            is AuthenticationMethod.BasicAuth -> {
                val credentials = "${authMethod.username}:${authMethod.password}"
                val encoded = java.util.Base64.getEncoder().encodeToString(credentials.toByteArray())
                header("Authorization", "Basic $encoded")
            }
            is AuthenticationMethod.CustomHeader -> header(authMethod.headerName, authMethod.headerValue)
            is AuthenticationMethod.OAuth2 -> this // OAuth2 handled separately
            is AuthenticationMethod.NoAuth -> this
        }
    }

    /**
     * Get authentication method from profile
     */
    protected fun getAuthMethod(): AuthenticationMethod {
        return when {
            profile.username != null && profile.password != null ->
                AuthenticationMethod.BasicAuth(profile.username!!, profile.password!!)
            else -> AuthenticationMethod.NoAuth
        }
    }

    /**
     * Log operation if logging is enabled
     */
    protected fun logOperation(operation: String, details: String? = null) {
        if (config.enableLogging) {
            println("[${serviceType}] $operation ${details ?: ""}")
        }
    }

    /**
     * Validate URL format
     */
    protected fun validateUrl(url: String): Boolean {
        return try {
            java.net.URL(url)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun cleanup() {
        coroutineScope.cancel()
        responseCache.clear()
        httpClient.dispatcher.executorService.shutdown()
        httpClient.connectionPool.evictAll()
    }

    /**
     * Cached response wrapper
     */
    private data class CachedResponse<T>(
        val data: T,
        val timestamp: Long
    )
}

/**
 * Factory for creating connectors
 */
interface ConnectorFactory {
    fun createConnector(profile: ServerProfile): Connector?
    fun getSupportedServiceTypes(): Set<ServiceType>
    fun getCapabilities(serviceType: ServiceType): ConnectorCapabilities
}