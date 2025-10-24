package com.shareconnect.plexconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.plexconnect.data.repository.PlexServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val serverRepository: PlexServerRepository
) : ViewModel() {

    private val _hasServers = MutableStateFlow(false)
    val hasServers: StateFlow<Boolean> = _hasServers.asStateFlow()

    init {
        checkExistingServers()
    }

    private fun checkExistingServers() {
        viewModelScope.launch {
            val serverCount = serverRepository.getServerCount()
            _hasServers.value = serverCount > 0
        }
    }
}