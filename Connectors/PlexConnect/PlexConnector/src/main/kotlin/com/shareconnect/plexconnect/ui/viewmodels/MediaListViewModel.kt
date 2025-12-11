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
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.data.model.PlexServer
import com.shareconnect.plexconnect.data.repository.PlexLibraryRepository
import com.shareconnect.plexconnect.data.repository.PlexMediaRepository
import com.shareconnect.plexconnect.data.repository.PlexServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MediaListViewModel(
    private val serverRepository: PlexServerRepository,
    private val libraryRepository: PlexLibraryRepository,
    private val mediaRepository: PlexMediaRepository,
    private val serverId: Long,
    private val libraryKey: String
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _server = MutableStateFlow<PlexServer?>(null)
    val server: StateFlow<PlexServer?> = _server.asStateFlow()

    private val _library = MutableStateFlow<PlexLibrary?>(null)
    val library: StateFlow<PlexLibrary?> = _library.asStateFlow()

    private val _mediaItems = MutableStateFlow<List<PlexMediaItem>>(emptyList())
    val mediaItems: StateFlow<List<PlexMediaItem>> = _mediaItems.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var currentOffset = 0
    private val pageSize = 50
    private var hasMoreItems = true
    private val maxItemsInMemory = 500 // Limit to prevent excessive memory usage

    init {
        loadServerAndLibrary()
    }

    private fun loadServerAndLibrary() {
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

                // Load library info
                val libraryResult = libraryRepository.getLibraryByKey(libraryKey)
                if (libraryResult == null) {
                    _error.value = "Library not found"
                    return@launch
                }
                _library.value = libraryResult

                // Load initial media items
                loadMediaItems(refresh = false)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMediaItems(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _isLoading.value = true
                currentOffset = 0
                hasMoreItems = true
            } else if (_isLoadingMore.value || !hasMoreItems) {
                return@launch
            }

            if (!refresh) {
                _isLoadingMore.value = true
            }

            _error.value = null

            try {
                val currentServer = _server.value
                val currentLibrary = _library.value

                if (currentServer == null || currentLibrary == null) {
                    _error.value = "Server or library not available"
                    return@launch
                }

                val refreshResult = mediaRepository.refreshLibraryItems(
                    currentServer,
                    currentLibrary,
                    limit = pageSize,
                    offset = currentOffset
                )

                refreshResult.fold(
                    onSuccess = { newItems ->
                        val currentItems = _mediaItems.value
                        val combinedItems = if (refresh) {
                            newItems
                        } else {
                            currentItems + newItems
                        }

                        // Limit memory usage by keeping only the most recent items
                        val limitedItems = if (combinedItems.size > maxItemsInMemory) {
                            combinedItems.drop(maxItemsInMemory - combinedItems.size)
                        } else {
                            combinedItems
                        }

                        _mediaItems.value = limitedItems

                        currentOffset += newItems.size
                        hasMoreItems = newItems.size >= pageSize && limitedItems.size <= maxItemsInMemory
                    },
                    onFailure = { error ->
                        _error.value = "Failed to load media items: ${error.message}"
                        hasMoreItems = false
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load media items"
                hasMoreItems = false
            } finally {
                _isLoading.value = false
                _isLoadingMore.value = false
            }
        }
    }

    fun loadMoreItems() {
        if (hasMoreItems && !_isLoadingMore.value) {
            loadMediaItems(refresh = false)
        }
    }

    fun clearError() {
        _error.value = null
    }
}