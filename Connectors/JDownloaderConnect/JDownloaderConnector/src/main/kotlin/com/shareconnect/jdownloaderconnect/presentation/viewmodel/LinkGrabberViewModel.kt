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


package com.shareconnect.jdownloaderconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.jdownloaderconnect.data.repository.JDownloaderRepository
import com.shareconnect.jdownloaderconnect.domain.model.LinkGrabberPackage
import com.shareconnect.jdownloaderconnect.domain.model.LinkAvailability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LinkGrabberViewModel constructor(
    private val repository: JDownloaderRepository
) : ViewModel() {

    private val _linkGrabberPackages = MutableStateFlow<List<LinkGrabberPackage>>(emptyList())
    val linkGrabberPackages: StateFlow<List<LinkGrabberPackage>> = _linkGrabberPackages.asStateFlow()

    private val _selectedPackage = MutableStateFlow<LinkGrabberPackage?>(null)
    val selectedPackage: StateFlow<LinkGrabberPackage?> = _selectedPackage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadLinkGrabber()
    }

    private fun loadLinkGrabber() {
        viewModelScope.launch {
            repository.getAllLinkGrabberPackages().collectLatest { packages ->
                _linkGrabberPackages.value = packages
            }
        }
    }

    fun refreshLinkGrabber(sessionToken: String, deviceId: String) {
        viewModelScope.launch {
            _isRefreshing.value = true
            _errorMessage.value = null
            try {
                repository.refreshLinkGrabber(sessionToken, deviceId)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to refresh link grabber: ${e.message}"
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun addLinksToLinkGrabber(
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
                repository.addLinksToLinkGrabber(
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

    fun moveToDownloadList(
        sessionToken: String,
        deviceId: String,
        linkIds: List<String>? = null,
        packageIds: List<String>? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.moveToDownloadList(sessionToken, deviceId, linkIds, packageIds)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to move to download list: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectPackage(pkg: LinkGrabberPackage?) {
        _selectedPackage.value = pkg
    }

    fun getFilteredPackages(availability: LinkAvailability?): List<LinkGrabberPackage> {
        return if (availability == null) {
            _linkGrabberPackages.value
        } else {
            _linkGrabberPackages.value.filter { pkg ->
                pkg.links.any { it.availability == availability }
            }
        }
    }

    fun getLinkGrabberStats(): LinkGrabberStats {
        val packages = _linkGrabberPackages.value
        val allLinks = packages.flatMap { it.links }
        return LinkGrabberStats(
            totalPackages = packages.size,
            totalLinks = allLinks.size,
            onlineLinks = allLinks.count { it.availability == LinkAvailability.ONLINE },
            offlineLinks = allLinks.count { it.availability == LinkAvailability.OFFLINE },
            unknownLinks = allLinks.count { it.availability == LinkAvailability.UNKNOWN },
            totalBytes = allLinks.sumOf { it.bytesTotal },
            hosterCount = packages.sumOf { it.hosterCount }
        )
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

data class LinkGrabberStats(
    val totalPackages: Int,
    val totalLinks: Int,
    val onlineLinks: Int,
    val offlineLinks: Int,
    val unknownLinks: Int,
    val totalBytes: Long,
    val hosterCount: Int
)