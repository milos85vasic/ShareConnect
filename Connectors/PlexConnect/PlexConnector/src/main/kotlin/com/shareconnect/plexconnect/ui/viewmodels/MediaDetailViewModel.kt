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
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.data.model.PlexServer
import com.shareconnect.plexconnect.data.repository.PlexMediaRepository
import com.shareconnect.plexconnect.data.repository.PlexServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MediaDetailViewModel(
    private val serverRepository: PlexServerRepository,
    private val mediaRepository: PlexMediaRepository,
    private val serverId: Long,
    private val ratingKey: String
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _server = MutableStateFlow<PlexServer?>(null)
    val server: StateFlow<PlexServer?> = _server.asStateFlow()

    private val _mediaItem = MutableStateFlow<PlexMediaItem?>(null)
    val mediaItem: StateFlow<PlexMediaItem?> = _mediaItem.asStateFlow()

    private val _children = MutableStateFlow<List<PlexMediaItem>>(emptyList())
    val children: StateFlow<List<PlexMediaItem>> = _children.asStateFlow()

    init {
        loadMediaDetails()
    }

    fun loadMediaDetails() {
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

                // Load media item details
                val mediaResult = mediaRepository.getMediaItemDetails(serverResult, ratingKey)
                mediaResult.fold(
                    onSuccess = { mediaItem ->
                        _mediaItem.value = mediaItem

                        // If this is a container (show, season, album), load children
                        if (mediaItem != null && isContainerType(mediaItem.type.value)) {
                            loadChildren()
                        }
                    },
                    onFailure = { error ->
                        _error.value = "Failed to load media details: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load media details"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadChildren() {
        viewModelScope.launch {
            try {
                val currentServer = _server.value ?: return@launch

                val childrenResult = mediaRepository.getMediaChildren(currentServer, ratingKey)
                childrenResult.fold(
                    onSuccess = { children ->
                        _children.value = children
                    },
                    onFailure = { error ->
                        // Children loading failure is not critical, just log it
                        println("Failed to load children: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                println("Error loading children: ${e.message}")
            }
        }
    }

    fun markAsPlayed() {
        viewModelScope.launch {
            try {
                val currentServer = _server.value ?: return@launch
                val currentMediaItem = _mediaItem.value ?: return@launch

                val result = mediaRepository.markAsPlayed(currentServer, currentMediaItem)
                result.fold(
                    onSuccess = {
                        // Update local state
                        _mediaItem.value = currentMediaItem.copy(
                            viewCount = currentMediaItem.viewCount + 1,
                            lastViewedAt = System.currentTimeMillis()
                        )
                    },
                    onFailure = { error ->
                        _error.value = "Failed to mark as played: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to mark as played"
            }
        }
    }

    fun markAsUnplayed() {
        viewModelScope.launch {
            try {
                val currentServer = _server.value ?: return@launch
                val currentMediaItem = _mediaItem.value ?: return@launch

                val result = mediaRepository.markAsUnplayed(currentServer, currentMediaItem)
                result.fold(
                    onSuccess = {
                        // Update local state
                        _mediaItem.value = currentMediaItem.copy(
                            viewCount = 0,
                            lastViewedAt = null
                        )
                    },
                    onFailure = { error ->
                        _error.value = "Failed to mark as unplayed: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to mark as unplayed"
            }
        }
    }

    fun updateProgress(progressMs: Long) {
        viewModelScope.launch {
            try {
                val currentServer = _server.value ?: return@launch
                val currentMediaItem = _mediaItem.value ?: return@launch

                val result = mediaRepository.updateProgress(currentServer, currentMediaItem, progressMs)
                result.fold(
                    onSuccess = {
                        // Update local state
                        _mediaItem.value = currentMediaItem.copy(
                            viewOffset = progressMs,
                            lastViewedAt = System.currentTimeMillis()
                        )
                    },
                    onFailure = { error ->
                        _error.value = "Failed to update progress: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update progress"
            }
        }
    }

    private fun isContainerType(type: String): Boolean {
        return type in listOf("show", "season", "album", "artist")
    }

    fun clearError() {
        _error.value = null
    }
}