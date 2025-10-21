package com.shareconnect.jdownloaderconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.jdownloaderconnect.data.model.JDownloaderAccount
import com.shareconnect.jdownloaderconnect.data.repository.JDownloaderRepository
import com.shareconnect.jdownloaderconnect.domain.model.DeviceConnection
import com.shareconnect.jdownloaderconnect.domain.model.JDownloaderDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AccountViewModel constructor(
    private val repository: JDownloaderRepository
) : ViewModel() {

    private val _accounts = MutableStateFlow<List<JDownloaderAccount>>(emptyList())
    val accounts: StateFlow<List<JDownloaderAccount>> = _accounts.asStateFlow()

    private val _activeAccount = MutableStateFlow<JDownloaderAccount?>(null)
    val activeAccount: StateFlow<JDownloaderAccount?> = _activeAccount.asStateFlow()

    private val _devices = MutableStateFlow<List<JDownloaderDevice>>(emptyList())
    val devices: StateFlow<List<JDownloaderDevice>> = _devices.asStateFlow()

    private val _connectionState = MutableStateFlow<DeviceConnection?>(null)
    val connectionState: StateFlow<DeviceConnection?> = _connectionState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadAccounts()
        loadActiveAccount()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            repository.getAllAccounts().collectLatest { accounts ->
                _accounts.value = accounts
            }
        }
    }

    private fun loadActiveAccount() {
        viewModelScope.launch {
            repository.getActiveAccount().collectLatest { account ->
                _activeAccount.value = account
                if (account != null) {
                    loadDevices()
                }
            }
        }
    }

    fun connectAccount(email: String, password: String, deviceName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val connection = repository.connectAccount(email, password, deviceName)
                _connectionState.value = connection
                
                // Save account
                val account = JDownloaderAccount(
                    id = "${email}_${deviceName}",
                    email = email,
                    password = password,
                    deviceName = deviceName,
                    deviceId = connection.deviceId,
                    isActive = true
                )
                repository.setActiveAccount(account)
                
                // Load devices
                loadDevices()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to connect: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun disconnectAccount() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                connectionState.value?.let { connection ->
                    repository.disconnectAccount(connection.sessionToken)
                }
                _connectionState.value = null
                _devices.value = emptyList()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to disconnect: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadDevices() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                connectionState.value?.let { connection ->
                    val devices = repository.getDevices(connection.sessionToken)
                    _devices.value = devices
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load devices: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setActiveAccount(account: JDownloaderAccount) {
        viewModelScope.launch {
            repository.setActiveAccount(account)
        }
    }

    fun deleteAccount(account: JDownloaderAccount) {
        viewModelScope.launch {
            repository.deleteAccount(account)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}