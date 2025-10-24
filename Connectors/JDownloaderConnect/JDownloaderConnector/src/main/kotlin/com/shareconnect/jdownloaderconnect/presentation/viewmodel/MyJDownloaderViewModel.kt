package com.shareconnect.jdownloaderconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.jdownloaderconnect.data.repository.MyJDownloaderRepository
import com.shareconnect.jdownloaderconnect.domain.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MyJDownloaderViewModel(
    private val myJDownloaderRepository: MyJDownloaderRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<MyJDownloaderUiState>(MyJDownloaderUiState.Loading)
    val uiState: StateFlow<MyJDownloaderUiState> = _uiState.asStateFlow()

    // Selected instance for detailed view
    private val _selectedInstance = MutableStateFlow<JDownloaderInstance?>(null)
    val selectedInstance: StateFlow<JDownloaderInstance?> = _selectedInstance.asStateFlow()

    // Dashboard data for selected instance
    private val _dashboardData = MutableStateFlow<InstanceDashboard?>(null)
    val dashboardData: StateFlow<InstanceDashboard?> = _dashboardData.asStateFlow()

    // Real-time updates
    val instanceUpdates = myJDownloaderRepository.instanceUpdates

    init {
        loadInstances()
        observeInstanceUpdates()
    }

    private fun loadInstances() {
        viewModelScope.launch {
            _uiState.value = MyJDownloaderUiState.Loading

            myJDownloaderRepository.getInstances()
                .onSuccess { instances ->
                    _uiState.value = MyJDownloaderUiState.Success(instances)
                    // Auto-select first online instance
                    instances.firstOrNull { it.isOnline }?.let { selectInstance(it.id) }
                }
                .onFailure { error ->
                    _uiState.value = MyJDownloaderUiState.Error(error.message ?: "Failed to load instances")
                }
        }
    }

    fun selectInstance(instanceId: String) {
        viewModelScope.launch {
            val instance = myJDownloaderRepository.instances.value.find { it.id == instanceId }
            _selectedInstance.value = instance

            if (instance != null) {
                loadDashboardData(instanceId)
            }
        }
    }

    private fun loadDashboardData(instanceId: String) {
        viewModelScope.launch {
            myJDownloaderRepository.getInstanceDashboard(instanceId)
                .onSuccess { dashboard ->
                    _dashboardData.value = dashboard
                }
                .onFailure { error ->
                    // Handle error - could emit a separate error state for dashboard
                    _dashboardData.value = null
                }
        }
    }

    fun controlInstance(instanceId: String, action: InstanceAction) {
        viewModelScope.launch {
            val repoAction = when (action) {
                InstanceAction.START -> com.shareconnect.jdownloaderconnect.data.repository.InstanceAction.START
                InstanceAction.STOP -> com.shareconnect.jdownloaderconnect.data.repository.InstanceAction.STOP
                InstanceAction.PAUSE -> com.shareconnect.jdownloaderconnect.data.repository.InstanceAction.PAUSE
            }
            myJDownloaderRepository.controlInstance(instanceId, repoAction)
                .onFailure { error ->
                    // Handle error - could emit error state
                    error.printStackTrace()
                }
        }
    }

    fun refreshInstances() {
        loadInstances()
    }

    fun refreshDashboard() {
        _selectedInstance.value?.id?.let { loadDashboardData(it) }
    }

    private fun observeInstanceUpdates() {
        viewModelScope.launch {
            instanceUpdates.collect { update ->
                // Update UI state when instances change
                when (update.type) {
                    UpdateType.STATUS_CHANGED,
                    UpdateType.SPEED_UPDATED,
                    UpdateType.INSTANCE_CONNECTED,
                    UpdateType.INSTANCE_DISCONNECTED -> {
                        // Refresh current instance data if it's the selected one
                        if (update.instanceId == _selectedInstance.value?.id) {
                            loadDashboardData(update.instanceId)
                        }
                    }
                    else -> {
                        // Handle other update types if needed
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        myJDownloaderRepository.cleanup()
    }
}

// UI State
sealed class MyJDownloaderUiState {
    object Loading : MyJDownloaderUiState()
    data class Success(val instances: List<JDownloaderInstance>) : MyJDownloaderUiState()
    data class Error(val message: String) : MyJDownloaderUiState()
}

// Instance control actions
enum class InstanceAction {
    START, STOP, PAUSE
}