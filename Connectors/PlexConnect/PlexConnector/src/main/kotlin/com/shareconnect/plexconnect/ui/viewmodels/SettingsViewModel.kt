package com.shareconnect.plexconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.plexconnect.data.model.PlexServer
import com.shareconnect.plexconnect.data.repository.PlexServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val serverRepository: PlexServerRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _servers = MutableStateFlow<List<PlexServer>>(emptyList())
    val servers: StateFlow<List<PlexServer>> = _servers.asStateFlow()

    init {
        loadServers()
    }

    private fun loadServers() {
        viewModelScope.launch {
            serverRepository.getAllServers().collectLatest { servers ->
                _servers.value = servers
            }
        }
    }

    fun deleteServer(server: PlexServer) {
        viewModelScope.launch {
            try {
                serverRepository.deleteServer(server)
                // The flow will automatically update the UI
            } catch (e: Exception) {
                // Handle error - could add error state if needed
                e.printStackTrace()
            }
        }
    }
}