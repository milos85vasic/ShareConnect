package com.shareconnect.plexconnect.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.plexconnect.data.model.PlexLibrarySection
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.data.model.PlexMediaFilter
import com.shareconnect.plexconnect.data.model.PlexServerInfo
import com.shareconnect.plexconnect.data.repository.PlexRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel for managing Plex-related UI state and interactions
 * @param plexRepository Repository for Plex data operations
 */
class PlexViewModel(
    private val plexRepository: PlexRepository
) : ViewModel() {

    // Mutable state flows for internal state management
    private val _serverInfo = MutableStateFlow<PlexServerInfo?>(null)
    val serverInfo: StateFlow<PlexServerInfo?> = _serverInfo.asStateFlow()

    private val _libraries = MutableStateFlow<List<PlexLibrarySection>>(emptyList())
    val libraries: StateFlow<List<PlexLibrarySection>> = _libraries.asStateFlow()

    private val _mediaItems = MutableStateFlow<List<PlexMediaItem>>(emptyList())
    val mediaItems: StateFlow<List<PlexMediaItem>> = _mediaItems.asStateFlow()

    private val _searchResults = MutableStateFlow<List<PlexMediaItem>>(emptyList())
    val searchResults: StateFlow<List<PlexMediaItem>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Fetch server information
     * @param serverUrl URL of the Plex server
     * @param token Authentication token
     */
    fun fetchServerInfo(serverUrl: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                plexRepository.getServerInfo(serverUrl, token)
                    .collect { serverInfo ->
                        _serverInfo.value = serverInfo
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Failed to fetch server info: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Retrieve libraries for a Plex server
     * @param serverUrl URL of the Plex server
     * @param token Authentication token
     */
    fun fetchLibraries(serverUrl: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                plexRepository.getLibraries(serverUrl, token)
                    .collect { libraryList ->
                        _libraries.value = libraryList
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Failed to fetch libraries: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Fetch media items from a specific library
     * @param serverUrl URL of the Plex server
     * @param sectionKey Library section key
     * @param token Authentication token
     * @param limit Maximum number of items to retrieve
     * @param offset Pagination offset
     */
    fun fetchLibraryItems(
        serverUrl: String,
        sectionKey: String,
        token: String,
        limit: Int = 50,
        offset: Int = 0,
        filter: PlexMediaFilter? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                plexRepository.getLibraryItems(serverUrl, sectionKey, token, limit, offset, filter)
                    .collect { items ->
                        _mediaItems.value = items
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Failed to fetch library items: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Create preset filters for common media browsing scenarios
     */
    fun createPresetFilter(preset: MediaFilterPreset): PlexMediaFilter {
        return when (preset) {
            MediaFilterPreset.RECENTLY_ADDED -> PlexMediaFilter(
                sortBy = PlexMediaFilter.SortOption.DATE_ADDED,
                sortOrder = PlexMediaFilter.SortOrder.DESCENDING
            )
            MediaFilterPreset.UNWATCHED -> PlexMediaFilter(
                watchStatus = PlexMediaFilter.WatchStatus.UNWATCHED
            )
            MediaFilterPreset.THIS_YEAR -> PlexMediaFilter(
                year = IntRange(
                    LocalDate.now().year,
                    LocalDate.now().year
                )
            )
        }
    }

    /**
     * Enum for preset filter configurations
     */
    enum class MediaFilterPreset {
        RECENTLY_ADDED,
        UNWATCHED,
        THIS_YEAR
    }

    /**
     * Search for media across the Plex server
     * @param serverUrl URL of the Plex server
     * @param query Search query
     * @param token Authentication token
     * @param limit Maximum number of results
     */
    fun searchMedia(
        serverUrl: String,
        query: String,
        token: String,
        limit: Int = 50
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                plexRepository.searchMedia(serverUrl, query, token, limit)
                    .collect { results ->
                        _searchResults.value = results
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Mark a media item as played
     * @param serverUrl URL of the Plex server
     * @param key Unique identifier of the media item
     * @param token Authentication token
     */
    fun markAsPlayed(serverUrl: String, key: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                plexRepository.markAsPlayed(serverUrl, key, token)
                _isLoading.value = false
                // Optionally, refresh media items or search results
            } catch (e: Exception) {
                _error.value = "Failed to mark as played: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Synchronize Plex server data
     * @param serverUrl URL of the Plex server
     * @param token Authentication token
     */
    fun syncPlexData(serverUrl: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                plexRepository.sync(serverUrl, token)
                // Refresh data after sync
                fetchServerInfo(serverUrl, token)
                fetchLibraries(serverUrl, token)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Sync failed: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear any existing error state
     */
    fun clearError() {
        _error.value = null
    }
}