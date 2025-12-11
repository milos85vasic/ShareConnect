package com.shareconnect.plexconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.plexconnect.nlp.NlpModelManager
import com.shareconnect.plexconnect.cache.EmbeddingCacheManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

// Use the monitor's metrics class
typealias MonitorNlpPerformanceMetrics = com.shareconnect.plexconnect.monitoring.NlpPerformanceMonitor.NlpPerformanceMetrics

data class NlpPerformanceMetrics(
    val averageInferenceTime: Float = 0f,
    val cacheHitRate: Float = 0f,
    val totalRequests: Int = 0,
    val errorRate: Float = 0f,
    val memoryUsage: Long = 0L
)

data class NlpUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val metrics: NlpPerformanceMetrics = NlpPerformanceMetrics(),
    val recentActivity: List<String> = emptyList(),
    val isMonitoring: Boolean = false
)

class NlpPerformanceDashboardViewModel(
    private val nlpModelManager: NlpModelManager,
    private val embeddingCacheManager: EmbeddingCacheManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NlpUiState())
    val uiState: StateFlow<NlpUiState> = _uiState.asStateFlow()
    
    private val _cacheStatistics = MutableStateFlow<Map<String, Any>>(emptyMap())
    val cacheStatistics: StateFlow<Map<String, Any>> = _cacheStatistics.asStateFlow()
    
    fun startMonitoring() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isMonitoring = true)
            collectMetrics()
        }
    }
    
    fun stopMonitoring() {
        _uiState.value = _uiState.value.copy(isMonitoring = false)
    }
    
    fun refreshMetrics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val metrics = collectPerformanceMetrics()
                val cacheStats = getCacheStatistics()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    metrics = metrics,
                    error = null
                )
                
                _cacheStatistics.value = cacheStats
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to refresh metrics: ${e.message}"
                )
            }
        }
    }
    
    private suspend fun collectMetrics() {
        while (_uiState.value.isMonitoring) {
            val metrics = collectPerformanceMetrics()
            _uiState.value = _uiState.value.copy(metrics = metrics)
            
            val activity = "Metrics updated at ${Instant.now()}"
            _uiState.value = _uiState.value.copy(
                recentActivity = listOf(activity) + _uiState.value.recentActivity.take(10)
            )
            
            kotlinx.coroutines.delay(5000) // Update every 5 seconds
        }
    }
    
    private suspend fun collectPerformanceMetrics(): NlpPerformanceMetrics {
        val cacheStats = embeddingCacheManager.getCacheStatistics()
        return NlpPerformanceMetrics(
            averageInferenceTime = 0.150f, // Placeholder
            cacheHitRate = cacheStats.totalHitRate,
            totalRequests = cacheStats.totalRequests.toInt(),
            errorRate = 0.02f, // Placeholder
            memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        )
    }
    
    private suspend fun getCacheStatistics(): Map<String, Any> {
        val stats = embeddingCacheManager.getCacheStatistics()
        return mapOf(
            "totalRequests" to stats.totalRequests,
            "memoryHits" to stats.memoryHits,
            "databaseHits" to stats.databaseHits,
            "computeOperations" to stats.computeOperations,
            "memoryHitRate" to stats.memoryHitRate,
            "databaseHitRate" to stats.databaseHitRate,
            "computeRate" to stats.computeRate,
            "totalHitRate" to stats.totalHitRate,
            "memoryCacheSize" to stats.memoryCacheSize,
            "averageEmbeddingSize" to stats.averageEmbeddingSize
        )
    }
}

class NlpPerformanceDashboardViewModelFactory(
    private val nlpModelManager: NlpModelManager,
    private val embeddingCacheManager: EmbeddingCacheManager
) : androidx.lifecycle.ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NlpPerformanceDashboardViewModel::class.java)) {
            return NlpPerformanceDashboardViewModel(nlpModelManager, embeddingCacheManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}