package com.shareconnect.portainerconnect.data.api

import android.util.Log
import com.shareconnect.portainerconnect.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Portainer API client for Docker container management
 * Based on Portainer REST API v2
 *
 * @param serverUrl Base URL of the Portainer server (e.g., "http://192.168.1.100:9000")
 * @param portainerApiService Optional service for dependency injection (testing)
 */
class PortainerApiClient(
    private val serverUrl: String,
    portainerApiService: PortainerApiService? = null
) {

    private val tag = "PortainerApiClient"

    /**
     * Generate the Authorization header value
     */
    fun getAuthHeader(token: String): String = "Bearer $token"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ensureTrailingSlash(serverUrl))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val service: PortainerApiService = portainerApiService ?: retrofit.create(PortainerApiService::class.java)

    private fun ensureTrailingSlash(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }

    // Authentication methods

    /**
     * Authenticate with username and password
     */
    suspend fun authenticate(username: String, password: String): Result<PortainerAuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = PortainerAuthRequest(username = username, password = password)
            val response = service.authenticate(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Authentication failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error authenticating", e)
            Result.failure(e)
        }
    }

    /**
     * Get Portainer status
     */
    suspend fun getStatus(): Result<PortainerStatus> = withContext(Dispatchers.IO) {
        try {
            val response = service.getStatus()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get status failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting status", e)
            Result.failure(e)
        }
    }

    // Endpoint methods

    /**
     * Get all endpoints
     */
    suspend fun getEndpoints(token: String): Result<List<PortainerEndpoint>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader(token)
            val response = service.getEndpoints(authHeader)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get endpoints failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting endpoints", e)
            Result.failure(e)
        }
    }

    // Container management methods

    /**
     * Get containers for an endpoint
     */
    suspend fun getContainers(endpointId: Int, token: String, all: Boolean = true): Result<List<PortainerContainer>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader(token)
            val response = service.getContainers(endpointId, all, authHeader)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get containers failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting containers", e)
            Result.failure(e)
        }
    }

    /**
     * Start a container
     */
    suspend fun startContainer(endpointId: Int, containerId: String, token: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader(token)
            val response = service.startContainer(endpointId, containerId, authHeader)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Start container failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error starting container", e)
            Result.failure(e)
        }
    }

    /**
     * Stop a container
     */
    suspend fun stopContainer(endpointId: Int, containerId: String, token: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader(token)
            val response = service.stopContainer(endpointId, containerId, authHeader)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Stop container failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error stopping container", e)
            Result.failure(e)
        }
    }

    /**
     * Restart a container
     */
    suspend fun restartContainer(endpointId: Int, containerId: String, token: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader(token)
            val response = service.restartContainer(endpointId, containerId, authHeader)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Restart container failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error restarting container", e)
            Result.failure(e)
        }
    }

    /**
     * Pause a container
     */
    suspend fun pauseContainer(endpointId: Int, containerId: String, token: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader(token)
            val response = service.pauseContainer(endpointId, containerId, authHeader)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Pause container failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error pausing container", e)
            Result.failure(e)
        }
    }

    /**
     * Unpause a container
     */
    suspend fun unpauseContainer(endpointId: Int, containerId: String, token: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader(token)
            val response = service.unpauseContainer(endpointId, containerId, authHeader)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Unpause container failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error unpausing container", e)
            Result.failure(e)
        }
    }

    /**
     * Remove a container
     */
    suspend fun removeContainer(endpointId: Int, containerId: String, token: String, force: Boolean = false): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader(token)
            val response = service.removeContainer(endpointId, containerId, force, authHeader)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Remove container failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error removing container", e)
            Result.failure(e)
        }
    }

    // Resource methods

    /**
     * Get images for an endpoint
     */
    suspend fun getImages(endpointId: Int, token: String): Result<List<PortainerImage>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader(token)
            val response = service.getImages(endpointId, authHeader)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get images failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting images", e)
            Result.failure(e)
        }
    }

    /**
     * Get volumes for an endpoint
     */
    suspend fun getVolumes(endpointId: Int, token: String): Result<List<PortainerVolume>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader(token)
            val response = service.getVolumes(endpointId, authHeader)
            if (response.isSuccessful) {
                val volumes = response.body()?.volumes ?: emptyList()
                Result.success(volumes)
            } else {
                Result.failure(Exception("Get volumes failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting volumes", e)
            Result.failure(e)
        }
    }

    /**
     * Get networks for an endpoint
     */
    suspend fun getNetworks(endpointId: Int, token: String): Result<List<PortainerNetwork>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader(token)
            val response = service.getNetworks(endpointId, authHeader)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get networks failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting networks", e)
            Result.failure(e)
        }
    }

    /**
     * Get container stats
     */
    suspend fun getContainerStats(endpointId: Int, containerId: String, token: String): Result<PortainerContainerStats> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader(token)
            val response = service.getContainerStats(endpointId, containerId, stream = false, authHeader = authHeader)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get container stats failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting container stats", e)
            Result.failure(e)
        }
    }
}
