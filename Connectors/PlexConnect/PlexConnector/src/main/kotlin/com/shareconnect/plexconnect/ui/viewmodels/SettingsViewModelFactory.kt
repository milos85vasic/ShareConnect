package com.shareconnect.plexconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shareconnect.plexconnect.data.repository.PlexServerRepository

class SettingsViewModelFactory(
    private val serverRepository: PlexServerRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(serverRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}