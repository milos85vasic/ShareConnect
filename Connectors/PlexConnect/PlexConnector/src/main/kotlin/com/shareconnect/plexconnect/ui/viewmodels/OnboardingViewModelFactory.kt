package com.shareconnect.plexconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shareconnect.plexconnect.data.repository.PlexServerRepository

class OnboardingViewModelFactory(
    private val serverRepository: PlexServerRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            return OnboardingViewModel(serverRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}