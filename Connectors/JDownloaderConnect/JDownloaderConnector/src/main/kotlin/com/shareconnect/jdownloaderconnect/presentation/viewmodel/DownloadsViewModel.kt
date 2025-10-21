package com.shareconnect.jdownloaderconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.jdownloaderconnect.data.repository.JDownloaderRepository
import com.shareconnect.jdownloaderconnect.domain.model.DownloadPackage
import com.shareconnect.jdownloaderconnect.domain.model.DownloadStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DownloadsViewModel constructor(
    private val repository: JDownloaderRepository
) : ViewModel() {

    private val _downloadPackages = MutableStateFlow<List<DownloadPackage>>(emptyList())
    val downloadPackages: StateFlow<List<DownloadPackage>> = _downloadPackages.asStateFlow()

    private val _selectedPackage = MutableStateFlow<DownloadPackage?>(null)
    val selectedPackage: StateFlow<DownloadPackage?> = _selectedPackage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadDownloads()
    }

    private fun loadDownloads() {
        viewModelScope.launch {
            repository.getAllDownloadPackages().collectLatest { packages ->
                _downloadPackages.value = packages
            }
        }
    }

    fun refreshDownloads(sessionToken: String, deviceId: String) {
        viewModelScope.launch {
            _isRefreshing.value = true
            _errorMessage.value = null
            try {
                repository.refreshDownloads(sessionToken, deviceId)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to refresh downloads: ${e.message}"
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun addLinks(
        sessionToken: String,
        deviceId: String,
        links: List<String>,
        packageName: String? = null,
        destinationFolder: String? = null,
        extractAfterDownload: Boolean = false,
        autoStart: Boolean = false
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.addLinksToDownloads(
                    sessionToken = sessionToken,
                    deviceId = deviceId,
                    links = links,
                    packageName = packageName,
                    destinationFolder = destinationFolder,
                    extractAfterDownload = extractAfterDownload,
                    autoStart = autoStart
                )
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add links: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startDownloads(
        sessionToken: String,
        deviceId: String,
        linkIds: List<String>? = null,
        packageIds: List<String>? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.startDownloads(sessionToken, deviceId, linkIds, packageIds)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to start downloads: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun stopDownloads(
        sessionToken: String,
        deviceId: String,
        linkIds: List<String>? = null,
        packageIds: List<String>? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.stopDownloads(sessionToken, deviceId, linkIds, packageIds)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to stop downloads: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeDownloads(
        sessionToken: String,
        deviceId: String,
        linkIds: List<String>? = null,
        packageIds: List<String>? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.removeDownloads(sessionToken, deviceId, linkIds, packageIds)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to remove downloads: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectPackage(pkg: DownloadPackage?) {
        _selectedPackage.value = pkg
    }

    fun getFilteredPackages(status: DownloadStatus?): List<DownloadPackage> {
        return if (status == null) {
            _downloadPackages.value
        } else {
            _downloadPackages.value.filter { it.status == status }
        }
    }

    fun getDownloadStats(): DownloadStats {
        val packages = _downloadPackages.value
        return DownloadStats(
            totalPackages = packages.size,
            downloading = packages.count { it.status == DownloadStatus.DOWNLOADING },
            queued = packages.count { it.status == DownloadStatus.QUEUED },
            paused = packages.count { it.status == DownloadStatus.PAUSED },
            finished = packages.count { it.status == DownloadStatus.FINISHED },
            failed = packages.count { it.status == DownloadStatus.FAILED },
            totalBytes = packages.sumOf { it.bytesTotal },
            loadedBytes = packages.sumOf { it.bytesLoaded }
        )
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

data class DownloadStats(
    val totalPackages: Int,
    val downloading: Int,
    val queued: Int,
    val paused: Int,
    val finished: Int,
    val failed: Int,
    val totalBytes: Long,
    val loadedBytes: Long
)