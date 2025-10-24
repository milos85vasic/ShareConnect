package com.shareconnect.plexconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shareconnect.plexconnect.data.repository.PlexLibraryRepository
import com.shareconnect.plexconnect.data.repository.PlexServerRepository

class LibraryListViewModelFactory(
    private val serverRepository: PlexServerRepository,
    private val libraryRepository: PlexLibraryRepository,
    private val serverId: Long
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryListViewModel::class.java)) {
            return LibraryListViewModel(serverRepository, libraryRepository, serverId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}