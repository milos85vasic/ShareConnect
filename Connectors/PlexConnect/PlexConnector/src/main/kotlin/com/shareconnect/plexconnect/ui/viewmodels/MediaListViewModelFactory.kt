package com.shareconnect.plexconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shareconnect.plexconnect.data.repository.PlexLibraryRepository
import com.shareconnect.plexconnect.data.repository.PlexMediaRepository
import com.shareconnect.plexconnect.data.repository.PlexServerRepository

class MediaListViewModelFactory(
    private val serverRepository: PlexServerRepository,
    private val libraryRepository: PlexLibraryRepository,
    private val mediaRepository: PlexMediaRepository,
    private val serverId: Long,
    private val libraryKey: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaListViewModel::class.java)) {
            return MediaListViewModel(
                serverRepository,
                libraryRepository,
                mediaRepository,
                serverId,
                libraryKey
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}