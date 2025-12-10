package com.shareconnect.plexconnect.data.error

import android.util.Log
import com.shareconnect.plexconnect.data.model.PlexServerInfo
import java.io.IOException
import java.net.UnknownHostException

/**
 * Comprehensive error handling and classification for Plex-related operations
 */
sealed class PlexError(message: String) : Exception(message) {
    /**
     * Network-related errors
     */
    sealed class NetworkError(message: String) : PlexError(message) {
        class ConnectionError(message: String) : NetworkError(message)
        class TimeoutError(message: String) : NetworkError(message)
        class DNSError(message: String) : NetworkError(message)
    }

    /**
     * Authentication-related errors
     */
    sealed class AuthenticationError(message: String) : PlexError(message) {
        class InvalidCredentials(message: String) : AuthenticationError(message)
        class TokenExpired(message: String) : AuthenticationError(message)
        class InsufficientPermissions(message: String) : AuthenticationError(message)
    }

    /**
     * Server-related errors
     */
    sealed class ServerError(message: String) : PlexError(message) {
        class ServerUnavailable(message: String) : ServerError(message)
        class UnsupportedServerVersion(message: String) : ServerError(message)
    }

    /**
     * Media-related errors
     */
    sealed class MediaError(message: String) : PlexError(message) {
        class MediaNotFound(message: String) : MediaError(message)
        class UnsupportedMediaType(message: String) : MediaError(message)
    }

    /**
     * Generic system errors
     */
    class SystemError(message: String) : PlexError(message)
}

/**
 * Error handling and classification utility
 */
object PlexErrorHandler {
    private const val TAG = "PlexErrorHandler"

    /**
     * Classify and handle different types of errors
     */
    fun handleError(error: Throwable): PlexError {
        Log.e(TAG, "Error occurred: ${error.message}", error)
        
        return when (error) {
            // Network-related errors
            is UnknownHostException -> PlexError.NetworkError.DNSError("Unable to resolve server hostname")
            is IOException -> PlexError.NetworkError.ConnectionError(error.message ?: "Network connection failed")
            
            // Authentication errors
            is SecurityException -> PlexError.AuthenticationError.InvalidCredentials("Authentication failed")
            
            // Server errors
            is IllegalStateException -> PlexError.ServerError.ServerUnavailable("Server is not responding")
            
            // Media-related errors
            is NoSuchElementException -> PlexError.MediaError.MediaNotFound("Requested media not found")
            
            // Default fallback
            else -> PlexError.SystemError(error.message ?: "Unknown error occurred")
        }
    }

    /**
     * Validate server compatibility
     */
    fun validateServerCompatibility(serverInfo: PlexServerInfo): Boolean {
        return try {
            // Example version compatibility check
            val majorVersion = serverInfo.version?.split(".")?.firstOrNull()?.toIntOrNull() ?: 0
            
            when {
                majorVersion < 1 -> {
                    Log.w(TAG, "Unsupported Plex server version")
                    throw PlexError.ServerError.UnsupportedServerVersion("Server version too low")
                }
                majorVersion > 2 -> {
                    Log.w(TAG, "Untested Plex server version")
                    true // Allow but log warning
                }
                else -> true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Server version validation failed", e)
            false
        }
    }

    /**
     * Retry mechanism with exponential backoff
     */
    suspend fun <T> retryWithBackoff(
        times: Int = 3,
        initialDelay: Long = 1000L,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                Log.w(TAG, "Attempt ${attempt + 1} failed: ${e.message}")
                // Exponential backoff
                Thread.sleep(currentDelay)
                currentDelay *= 2
            }
        }
        // Final attempt
        return block()
    }
}

/**
 * Extension function for easy error logging
 */
fun Throwable.logPlexError() {
    PlexErrorHandler.handleError(this)
}