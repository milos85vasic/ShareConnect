package com.shareconnect.plexconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shareconnect.plexconnect.data.repository.PlexServerRepository
import com.shareconnect.plexconnect.service.PlexAuthService

class AddServerViewModelFactory(
    private val serverRepository: PlexServerRepository,
    private val authService: PlexAuthService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddServerViewModel::class.java)) {
            return AddServerViewModel(serverRepository, authService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}