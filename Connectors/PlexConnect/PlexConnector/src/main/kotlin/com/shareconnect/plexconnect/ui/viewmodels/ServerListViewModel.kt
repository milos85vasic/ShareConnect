package com.shareconnect.plexconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.plexconnect.data.model.PlexServer
import com.shareconnect.plexconnect.data.repository.PlexServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ServerListViewModel(
    private val serverRepository: PlexServerRepository
) : ViewModel() {

    private val _servers = MutableStateFlow<List<PlexServer>>(emptyList())
    val servers: StateFlow<List<PlexServer>> = _servers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadServers()
    }

    fun loadServers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            serverRepository.getAllServers()
                .catch { e ->
                    _error.value = e.message ?: "Failed to load servers"
                    _isLoading.value = false
                }
                .collect { serverList ->
                    _servers.value = serverList
                    _isLoading.value = false
                }
        }
    }

    fun deleteServer(server: PlexServer) {
        viewModelScope.launch {
            try {
                serverRepository.deleteServer(server)
                // Reload servers after deletion
                loadServers()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete server"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}