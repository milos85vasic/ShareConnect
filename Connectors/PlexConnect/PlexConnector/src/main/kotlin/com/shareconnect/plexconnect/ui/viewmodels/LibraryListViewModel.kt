/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.plexconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.plexconnect.data.model.PlexLibrary
import com.shareconnect.plexconnect.data.model.PlexServer
import com.shareconnect.plexconnect.data.repository.PlexLibraryRepository
import com.shareconnect.plexconnect.data.repository.PlexServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LibraryListViewModel(
    private val serverRepository: PlexServerRepository,
    private val libraryRepository: PlexLibraryRepository,
    private val serverId: Long
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _server = MutableStateFlow<PlexServer?>(null)
    val server: StateFlow<PlexServer?> = _server.asStateFlow()

    private val _libraries = MutableStateFlow<List<PlexLibrary>>(emptyList())
    val libraries: StateFlow<List<PlexLibrary>> = _libraries.asStateFlow()

    init {
        loadServerAndLibraries()
    }

    private fun loadServerAndLibraries() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Load server info
                val serverResult = serverRepository.getServerById(serverId)
                if (serverResult == null) {
                    _error.value = "Server not found"
                    return@launch
                }
                _server.value = serverResult

                // Load libraries for this server
                libraryRepository.getLibrariesForServer(serverId).collectLatest { libraries ->
                    _libraries.value = libraries

                    // If no libraries cached, try to refresh from server
                    if (libraries.isEmpty()) {
                        refreshLibraries()
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load libraries"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshLibraries() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val currentServer = _server.value
                if (currentServer == null) {
                    _error.value = "Server not available"
                    return@launch
                }

                val refreshResult = libraryRepository.refreshLibrariesForServer(currentServer)
                refreshResult.fold(
                    onSuccess = { refreshedLibraries ->
                        // Libraries are already updated in the database and flow
                        // The collectLatest will pick up the changes
                    },
                    onFailure = { error ->
                        _error.value = "Failed to refresh libraries: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to refresh libraries"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}