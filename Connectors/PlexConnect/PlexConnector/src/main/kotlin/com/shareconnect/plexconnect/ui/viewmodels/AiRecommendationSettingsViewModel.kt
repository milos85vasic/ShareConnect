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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.plexconnect.config.NlpConfig
import com.shareconnect.plexconnect.ui.screens.RecommendationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * ViewModel for AI recommendation settings
 */
class AiRecommendationSettingsViewModel(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(AiRecommendationSettingsUiState())
    val uiState: StateFlow<AiRecommendationSettingsUiState> = _uiState.asStateFlow()
    
    init {
        observeSettings()
    }
    
    fun loadSettings() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val preferences = dataStore.data.first()
                
                val settings = AiRecommendationSettings(
                    enableAiRecommendations = preferences[KEY_ENABLE_AI_RECOMMENDATIONS] ?: true,
                    similarityThreshold = preferences[KEY_SIMILARITY_THRESHOLD] ?: NlpConfig.DEFAULT_SIMILARITY_THRESHOLD,
                    recommendationCount = preferences[KEY_RECOMMENDATION_COUNT] ?: NlpConfig.DEFAULT_MAX_RECOMMENDATIONS,
                    languagePreference = preferences[KEY_LANGUAGE_PREFERENCE] ?: NlpConfig.DEFAULT_LANGUAGE,
                    enableCrossLingual = preferences[KEY_ENABLE_CROSS_LINGUAL] ?: true,
                    enabledRecommendationTypes = preferences[KEY_ENABLED_RECOMMENDATION_TYPES] 
                        ?: setOf("SEMANTIC", "PERSONALIZED"),
                    cacheSize = preferences[KEY_CACHE_SIZE] ?: NlpConfig.EMBEDDING_CACHE_SIZE,
                    enablePerformanceMonitoring = preferences[KEY_ENABLE_PERFORMANCE_MONITORING] ?: NlpConfig.ENABLE_PERFORMANCE_LOGGING,
                    debugMode = preferences[KEY_DEBUG_MODE] ?: false
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    enableAiRecommendations = settings.enableAiRecommendations,
                    similarityThreshold = settings.similarityThreshold,
                    recommendationCount = settings.recommendationCount,
                    languagePreference = settings.languagePreference,
                    enableCrossLingual = settings.enableCrossLingual,
                    enabledRecommendationTypes = settings.enabledRecommendationTypes.mapNotNull { type ->
                        try {
                            RecommendationType.valueOf(type)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    }.toSet(),
                    cacheSize = settings.cacheSize,
                    enablePerformanceMonitoring = settings.enablePerformanceMonitoring,
                    debugMode = settings.debugMode
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load settings: ${e.message}"
                )
            }
        }
    }
    
    fun setEnableAiRecommendations(enabled: Boolean) {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[KEY_ENABLE_AI_RECOMMENDATIONS] = enabled
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to save AI recommendation setting: ${e.message}"
                )
            }
        }
    }
    
    fun setSimilarityThreshold(threshold: Double) {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[KEY_SIMILARITY_THRESHOLD] = threshold
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to save similarity threshold: ${e.message}"
                )
            }
        }
    }
    
    fun setRecommendationCount(count: Int) {
        viewModelScope.launch {
            try {
                val validCount = count.coerceIn(
                    NlpConfig.MIN_RECOMMENDATIONS,
                    NlpConfig.MAX_RECOMMENDATIONS
                )
                dataStore.edit { preferences ->
                    preferences[KEY_RECOMMENDATION_COUNT] = validCount
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to save recommendation count: ${e.message}"
                )
            }
        }
    }
    
    fun setLanguagePreference(language: String) {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[KEY_LANGUAGE_PREFERENCE] = language
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to save language preference: ${e.message}"
                )
            }
        }
    }
    
    fun setEnableCrossLingual(enabled: Boolean) {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[KEY_ENABLE_CROSS_LINGUAL] = enabled
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to save cross-lingual setting: ${e.message}"
                )
            }
        }
    }
    
    fun setEnabledRecommendationTypes(types: Set<RecommendationType>) {
        viewModelScope.launch {
            try {
                val typeStrings = types.map { it.name }.toSet()
                dataStore.edit { preferences ->
                    preferences[KEY_ENABLED_RECOMMENDATION_TYPES] = typeStrings
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to save recommendation types: ${e.message}"
                )
            }
        }
    }
    
    fun setCacheSize(size: Int) {
        viewModelScope.launch {
            try {
                val validSize = size.coerceIn(100, 2000)
                dataStore.edit { preferences ->
                    preferences[KEY_CACHE_SIZE] = validSize
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to save cache size: ${e.message}"
                )
            }
        }
    }
    
    fun setEnablePerformanceMonitoring(enabled: Boolean) {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[KEY_ENABLE_PERFORMANCE_MONITORING] = enabled
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to save performance monitoring setting: ${e.message}"
                )
            }
        }
    }
    
    fun setDebugMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[KEY_DEBUG_MODE] = enabled
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to save debug mode: ${e.message}"
                )
            }
        }
    }
    
    fun resetToDefaults() {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences.clear()
                    // Set default values
                    preferences[KEY_ENABLE_AI_RECOMMENDATIONS] = true
                    preferences[KEY_SIMILARITY_THRESHOLD] = NlpConfig.DEFAULT_SIMILARITY_THRESHOLD
                    preferences[KEY_RECOMMENDATION_COUNT] = NlpConfig.DEFAULT_MAX_RECOMMENDATIONS
                    preferences[KEY_LANGUAGE_PREFERENCE] = NlpConfig.DEFAULT_LANGUAGE
                    preferences[KEY_ENABLE_CROSS_LINGUAL] = true
                    preferences[KEY_ENABLED_RECOMMENDATION_TYPES] = setOf("SEMANTIC", "PERSONALIZED")
                    preferences[KEY_CACHE_SIZE] = NlpConfig.EMBEDDING_CACHE_SIZE
                    preferences[KEY_ENABLE_PERFORMANCE_MONITORING] = NlpConfig.ENABLE_PERFORMANCE_LOGGING
                    preferences[KEY_DEBUG_MODE] = false
                }
                
                _uiState.value = AiRecommendationSettingsUiState()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to reset settings: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    private fun observeSettings() {
        // Observe changes from DataStore
        viewModelScope.launch {
            dataStore.data.map { preferences ->
                AiRecommendationSettings(
                    enableAiRecommendations = preferences[KEY_ENABLE_AI_RECOMMENDATIONS] ?: true,
                    similarityThreshold = preferences[KEY_SIMILARITY_THRESHOLD] ?: NlpConfig.DEFAULT_SIMILARITY_THRESHOLD,
                    recommendationCount = preferences[KEY_RECOMMENDATION_COUNT] ?: NlpConfig.DEFAULT_MAX_RECOMMENDATIONS,
                    languagePreference = preferences[KEY_LANGUAGE_PREFERENCE] ?: NlpConfig.DEFAULT_LANGUAGE,
                    enableCrossLingual = preferences[KEY_ENABLE_CROSS_LINGUAL] ?: true,
                    enabledRecommendationTypes = preferences[KEY_ENABLED_RECOMMENDATION_TYPES] 
                        ?: setOf("SEMANTIC", "PERSONALIZED"),
                    cacheSize = preferences[KEY_CACHE_SIZE] ?: NlpConfig.EMBEDDING_CACHE_SIZE,
                    enablePerformanceMonitoring = preferences[KEY_ENABLE_PERFORMANCE_MONITORING] ?: NlpConfig.ENABLE_PERFORMANCE_LOGGING,
                    debugMode = preferences[KEY_DEBUG_MODE] ?: false
                )
            }.collect { settings ->
                _uiState.value = _uiState.value.copy(
                    enableAiRecommendations = settings.enableAiRecommendations,
                    similarityThreshold = settings.similarityThreshold,
                    recommendationCount = settings.recommendationCount,
                    languagePreference = settings.languagePreference,
                    enableCrossLingual = settings.enableCrossLingual,
                    enabledRecommendationTypes = settings.enabledRecommendationTypes.mapNotNull { type ->
                        try {
                            RecommendationType.valueOf(type)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    }.toSet(),
                    cacheSize = settings.cacheSize,
                    enablePerformanceMonitoring = settings.enablePerformanceMonitoring,
                    debugMode = settings.debugMode
                )
            }
        }
    }
    
    // DataStore keys
    companion object {
        private val KEY_ENABLE_AI_RECOMMENDATIONS = booleanPreferencesKey(NlpConfig.PREF_ENABLE_AI_RECOMMENDATIONS)
        private val KEY_SIMILARITY_THRESHOLD = doublePreferencesKey(NlpConfig.PREF_SIMILARITY_THRESHOLD)
        private val KEY_RECOMMENDATION_COUNT = intPreferencesKey(NlpConfig.PREF_RECOMMENDATION_COUNT)
        private val KEY_LANGUAGE_PREFERENCE = stringPreferencesKey(NlpConfig.PREF_LANGUAGE_PREFERENCE)
        private val KEY_ENABLE_CROSS_LINGUAL = booleanPreferencesKey(NlpConfig.PREF_ENABLE_CROSS_LINGUAL)
        private val KEY_ENABLED_RECOMMENDATION_TYPES = stringSetPreferencesKey("enabled_recommendation_types")
        private val KEY_CACHE_SIZE = intPreferencesKey("cache_size")
        private val KEY_ENABLE_PERFORMANCE_MONITORING = booleanPreferencesKey("enable_performance_monitoring")
        private val KEY_DEBUG_MODE = booleanPreferencesKey("debug_mode")
    }
    
    data class AiRecommendationSettings(
        val enableAiRecommendations: Boolean,
        val similarityThreshold: Double,
        val recommendationCount: Int,
        val languagePreference: String,
        val enableCrossLingual: Boolean,
        val enabledRecommendationTypes: Set<String>,
        val cacheSize: Int,
        val enablePerformanceMonitoring: Boolean,
        val debugMode: Boolean
    )
    
    data class AiRecommendationSettingsUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val enableAiRecommendations: Boolean = true,
        val similarityThreshold: Double = NlpConfig.DEFAULT_SIMILARITY_THRESHOLD,
        val recommendationCount: Int = NlpConfig.DEFAULT_MAX_RECOMMENDATIONS,
        val languagePreference: String = NlpConfig.DEFAULT_LANGUAGE,
        val enableCrossLingual: Boolean = true,
        val enabledRecommendationTypes: Set<RecommendationType> = setOf(RecommendationType.SEMANTIC, RecommendationType.PERSONALIZED),
        val cacheSize: Int = NlpConfig.EMBEDDING_CACHE_SIZE,
        val enablePerformanceMonitoring: Boolean = NlpConfig.ENABLE_PERFORMANCE_LOGGING,
        val debugMode: Boolean = false
    )
}