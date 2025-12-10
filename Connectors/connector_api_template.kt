package com.shareconnect.connectortemplate.data.api

import kotlinx.coroutines.flow.Flow
import retrofit2.http.*
import retrofit2.Response

/**
 * Base interface for connector API services
 * @param T The type of data being retrieved/managed
 * @param ID The type of identifier used for resources
 */
interface BaseConnectorService<T, ID> {
    /**
     * Retrieve all resources
     */
    @GET("/")
    suspend fun getAll(): Response<List<T>>

    /**
     * Retrieve a specific resource by ID
     * @param id Unique identifier for the resource
     */
    @GET("/{id}")
    suspend fun getById(@Path("id") id: ID): Response<T>

    /**
     * Create a new resource
     * @param resource The resource to create
     */
    @POST("/")
    suspend fun create(@Body resource: T): Response<T>

    /**
     * Update an existing resource
     * @param id Unique identifier for the resource
     * @param resource Updated resource data
     */
    @PUT("/{id}")
    suspend fun update(@Path("id") id: ID, @Body resource: T): Response<T>

    /**
     * Delete a resource
     * @param id Unique identifier for the resource to delete
     */
    @DELETE("/{id}")
    suspend fun delete(@Path("id") id: ID): Response<Unit>
}

/**
 * Base repository interface for managing connector resources
 * @param T The type of data being managed
 * @param ID The type of identifier used for resources
 */
interface BaseConnectorRepository<T, ID> {
    /**
     * Retrieve all resources from remote and local sources
     * @return Flow of resources
     */
    fun getAll(): Flow<List<T>>

    /**
     * Retrieve a specific resource by ID
     * @param id Unique identifier for the resource
     * @return Resource if found, null otherwise
     */
    suspend fun getById(id: ID): T?

    /**
     * Create a new resource
     * @param resource The resource to create
     * @return Created resource
     */
    suspend fun create(resource: T): T

    /**
     * Update an existing resource
     * @param resource Updated resource data
     * @return Updated resource
     */
    suspend fun update(resource: T): T

    /**
     * Delete a resource
     * @param id Unique identifier for the resource to delete
     */
    suspend fun delete(id: ID)

    /**
     * Sync resources between local and remote sources
     */
    suspend fun sync()
}

/**
 * Result wrapper for API operations
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Exception) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

/**
 * Configuration for connector API client
 * @param baseUrl Base URL for the API
 * @param apiKey Optional API key for authentication
 * @param timeout Connection timeout in milliseconds
 */
data class ConnectorApiConfig(
    val baseUrl: String,
    val apiKey: String? = null,
    val timeout: Long = 30000
)

/**
 * Authentication interceptor for API requests
 */
interface AuthInterceptor {
    /**
     * Add authentication details to request
     * @param baseRequest Original request
     * @return Authenticated request
     */
    fun authenticate(baseRequest: Any): Any
}

/**
 * Exception types for connector API
 */
sealed class ConnectorApiException(message: String) : Exception(message) {
    class NetworkException(message: String) : ConnectorApiException(message)
    class AuthenticationException(message: String) : ConnectorApiException(message)
    class ResourceNotFoundException(message: String) : ConnectorApiException(message)
    class ValidationException(message: String) : ConnectorApiException(message)
}