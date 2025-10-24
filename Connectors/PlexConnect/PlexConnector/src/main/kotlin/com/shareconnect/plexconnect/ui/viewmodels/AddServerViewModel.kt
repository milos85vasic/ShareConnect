package com.shareconnect.plexconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.plexconnect.data.model.PlexServer
import com.shareconnect.plexconnect.data.repository.PlexServerRepository
import com.shareconnect.plexconnect.service.PlexAuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddServerViewModel(
    private val serverRepository: PlexServerRepository,
    private val authService: PlexAuthService
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()

    private val _requiresAuth = MutableStateFlow(false)
    val requiresAuth: StateFlow<Boolean> = _requiresAuth.asStateFlow()

    private var pendingServer: PlexServer? = null

    fun testServerConnection(name: String, address: String, port: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _requiresAuth.value = false

            try {
                val server = PlexServer(
                    name = name,
                    address = address,
                    port = port,
                    isLocal = true // Assume local for now, can be detected later
                )

                // Test connection to server
                val testResult = serverRepository.testServerConnection(server)
                if (testResult.isSuccess) {
                    val serverInfo = testResult.getOrNull()
                    // Server is accessible without authentication
                    // For now, assume it doesn't require auth if we can connect
                    // In the future, we might want to check specific endpoints
                    serverRepository.addServer(server)
                    _success.value = true
                } else {
                    val error = testResult.exceptionOrNull()
                    // Check if it's an authentication error (401)
                    if (error?.message?.contains("401") == true ||
                        error?.message?.contains("Unauthorized") == true) {
                        // Server requires authentication
                        pendingServer = server
                        _requiresAuth.value = true
                    } else {
                        _error.value = "Failed to connect to server: ${error?.message}"
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to test server connection"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeServerAdditionWithAuth() {
        val server = pendingServer ?: return
        val authState = authService.authState.value

        if (authState is PlexAuthService.AuthState.Authenticated) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val authenticatedServer = server.copy(token = authState.token)
                    serverRepository.addServer(authenticatedServer)
                    _success.value = true
                    pendingServer = null
                    _requiresAuth.value = false
                } catch (e: Exception) {
                    _error.value = e.message ?: "Failed to save authenticated server"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun cancelServerAddition() {
        pendingServer = null
        _requiresAuth.value = false
        _error.value = null
        _isLoading.value = false
    }

    fun clearError() {
        _error.value = null
    }

    fun reset() {
        pendingServer = null
        _requiresAuth.value = false
        _error.value = null
        _success.value = false
        _isLoading.value = false
    }
}