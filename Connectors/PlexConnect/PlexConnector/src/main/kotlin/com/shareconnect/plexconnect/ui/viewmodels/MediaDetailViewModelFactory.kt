package com.shareconnect.plexconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shareconnect.plexconnect.data.repository.PlexMediaRepository
import com.shareconnect.plexconnect.data.repository.PlexServerRepository

class MediaDetailViewModelFactory(
    private val serverRepository: PlexServerRepository,
    private val mediaRepository: PlexMediaRepository,
    private val serverId: Long,
    private val ratingKey: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaDetailViewModel::class.java)) {
            return MediaDetailViewModel(
                serverRepository,
                mediaRepository,
                serverId,
                ratingKey
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}