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
import com.shareconnect.plexconnect.data.repository.PlexMediaRepository
import com.shareconnect.plexconnect.service.PlexAiRecommendationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for AI-powered media recommendations
 * Manages state for enhanced media items, similarity analysis, and cross-lingual recommendations
 */
class PlexAiRecommendationViewModel(
    private val mediaRepository: PlexMediaRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(PlexAiRecommendationUiState())
    val uiState: StateFlow<PlexAiRecommendationUiState> = _uiState.asStateFlow()

    // Enhanced media items for current view
    private val _enhancedMediaItems = MutableStateFlow<List<PlexAiRecommendationService.EnhancedMediaItem>>(emptyList())
    val enhancedMediaItems: StateFlow<List<PlexAiRecommendationService.EnhancedMediaItem>> = _enhancedMediaItems.asStateFlow()

    // Similar media recommendations
    private val _similarMedia = MutableStateFlow<List<PlexAiRecommendationService.SimilarMediaResult>>(emptyList())
    val similarMedia: StateFlow<List<PlexAiRecommendationService.SimilarMediaResult>> = _similarMedia.asStateFlow()

    // Cross-lingual recommendations
    private val _crossLingualRecommendations = MutableStateFlow<List<PlexAiRecommendationService.EnhancedMediaItem>>(emptyList())
    val crossLingualRecommendations: StateFlow<List<PlexAiRecommendationService.EnhancedMediaItem>> = _crossLingualRecommendations.asStateFlow()

    // History-based recommendations
    private val _historyBasedRecommendations = MutableStateFlow<List<PlexAiRecommendationService.SimilarMediaResult>>(emptyList())
    val historyBasedRecommendations: StateFlow<List<PlexAiRecommendationService.SimilarMediaResult>> = _historyBasedRecommendations.asStateFlow()

    init {
        loadEnhancedMediaItems()
    }

    /**
     * Load enhanced media items for all media
     */
    fun loadEnhancedMediaItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                mediaRepository.getEnhancedMediaItems().collect { enhancedItems ->
                    _enhancedMediaItems.value = enhancedItems
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load enhanced media items: ${e.message}"
                )
            }
        }
    }

    /**
     * Load enhanced media items for a specific server
     */
    fun loadEnhancedMediaItemsForServer(serverId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                mediaRepository.getEnhancedMediaItemsForServer(serverId).collect { enhancedItems ->
                    _enhancedMediaItems.value = enhancedItems
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load enhanced media items for server: ${e.message}"
                )
            }
        }
    }

    /**
     * Load enhanced media items for a specific library
     */
    fun loadEnhancedMediaItemsForLibrary(libraryId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                mediaRepository.getEnhancedMediaItemsForLibrary(libraryId).collect { enhancedItems ->
                    _enhancedMediaItems.value = enhancedItems
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load enhanced media items for library: ${e.message}"
                )
            }
        }
    }

    /**
     * Find similar media items
     */
    fun findSimilarMedia(targetItem: PlexMediaItem, threshold: Double = 0.7) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val results = mediaRepository.findSimilarMedia(targetItem, threshold)
                _similarMedia.value = results
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to find similar media: ${e.message}"
                )
            }
        }
    }

    /**
     * Load cross-lingual recommendations
     */
    fun loadCrossLingualRecommendations(targetLanguage: String = "en") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val recommendations = mediaRepository.getCrossLingualRecommendations(
                    targetLanguage = targetLanguage,
                    limit = PlexAiRecommendationService.MAX_RECOMMENDATIONS
                )
                _crossLingualRecommendations.value = recommendations
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load cross-lingual recommendations: ${e.message}"
                )
            }
        }
    }

    /**
     * Load history-based recommendations
     */
    fun loadHistoryBasedRecommendations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val recommendations = mediaRepository.getRecommendationsBasedOnHistory(
                    limit = PlexAiRecommendationService.MAX_RECOMMENDATIONS
                )
                _historyBasedRecommendations.value = recommendations
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load history-based recommendations: ${e.message}"
                )
            }
        }
    }

    /**
     * Clear current error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Refresh all recommendation data
     */
    fun refreshAll() {
        loadEnhancedMediaItems()
        loadHistoryBasedRecommendations()
        loadCrossLingualRecommendations()
    }

    /**
     * Update similarity threshold and recompute similar media
     */
    fun updateSimilarityThreshold(threshold: Double) {
        _uiState.value = _uiState.value.copy(similarityThreshold = threshold)
        // If we have a current target item, recompute similar media
        // This would require tracking the current target item in state
    }
}

/**
 * UI state for AI recommendations
 */
data class PlexAiRecommendationUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val similarityThreshold: Double = PlexAiRecommendationService.DEFAULT_SIMILARITY_THRESHOLD,
    val selectedLanguage: String = "en",
    val maxRecommendations: Int = PlexAiRecommendationService.MAX_RECOMMENDATIONS
)