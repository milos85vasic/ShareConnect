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


package com.shareconnect.jdownloaderconnect.data.repository

import com.shareconnect.jdownloaderconnect.data.dao.AccountDao
import com.shareconnect.jdownloaderconnect.data.dao.DownloadDao
import com.shareconnect.jdownloaderconnect.data.dao.LinkGrabberDao
import com.shareconnect.jdownloaderconnect.data.model.*
import com.shareconnect.jdownloaderconnect.domain.model.*
import com.shareconnect.jdownloaderconnect.network.api.MyJDownloaderApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JDownloaderRepository constructor(
    private val accountDao: AccountDao,
    private val downloadDao: DownloadDao,
    private val linkGrabberDao: LinkGrabberDao,
    private val api: MyJDownloaderApi
) {

    // Account Management
    suspend fun saveAccount(account: JDownloaderAccount) = accountDao.insertAccount(account)
    suspend fun deleteAccount(account: JDownloaderAccount) = accountDao.deleteAccount(account)
    fun getActiveAccount(): Flow<JDownloaderAccount?> = accountDao.getActiveAccount()
    fun getAllAccounts(): Flow<List<JDownloaderAccount>> = accountDao.getAllAccounts()
    suspend fun setActiveAccount(account: JDownloaderAccount) {
        accountDao.deactivateAllAccounts()
        accountDao.insertAccount(account.copy(isActive = true))
    }

    // Device Management
    suspend fun connectAccount(email: String, password: String, deviceName: String): DeviceConnection {
        // TODO: Implement actual API connection
        return DeviceConnection(
            deviceId = "mock_device_id",
            sessionToken = "mock_session_token",
            serverEncryptionToken = "mock_server_token",
            deviceEncryptionToken = "mock_device_token"
        )
    }

    suspend fun disconnectAccount(sessionToken: String) {
        // TODO: Implement actual API disconnection
    }

    suspend fun getDevices(sessionToken: String): List<JDownloaderDevice> {
        // TODO: Implement actual API call
        return listOf(
            JDownloaderDevice(
                id = "mock_device_id",
                name = "Mock Device",
                type = "DESKTOP",
                status = "ONLINE",
                version = "2.0.0",
                isConnected = true
            )
        )
    }

    // Download Management
    fun getAllDownloadPackages(): Flow<List<com.shareconnect.jdownloaderconnect.domain.model.DownloadPackage>> = downloadDao.getAllPackages()
        .map { packages -> packages.map { it.toDomain() } }

    suspend fun getDownloadPackage(packageId: String): com.shareconnect.jdownloaderconnect.domain.model.DownloadPackage? = 
        downloadDao.getPackageById(packageId)?.toDomain()

    suspend fun refreshDownloads(sessionToken: String, deviceId: String) {
        // TODO: Implement actual API call
    }

    suspend fun addLinksToDownloads(
        sessionToken: String,
        deviceId: String,
        links: List<String>,
        packageName: String? = null,
        destinationFolder: String? = null,
        extractAfterDownload: Boolean = false,
        autoStart: Boolean = false
    ) {
        // TODO: Implement actual API call
    }

    suspend fun startDownloads(
        sessionToken: String,
        deviceId: String,
        linkIds: List<String>? = null,
        packageIds: List<String>? = null
    ) {
        // TODO: Implement actual API call
    }

    suspend fun stopDownloads(
        sessionToken: String,
        deviceId: String,
        linkIds: List<String>? = null,
        packageIds: List<String>? = null
    ) {
        // TODO: Implement actual API call
    }

    suspend fun removeDownloads(
        sessionToken: String,
        deviceId: String,
        linkIds: List<String>? = null,
        packageIds: List<String>? = null
    ) {
        // TODO: Implement actual API call
    }

    // Link Grabber Management
    fun getAllLinkGrabberPackages(): Flow<List<com.shareconnect.jdownloaderconnect.domain.model.LinkGrabberPackage>> = linkGrabberDao.getAllPackages()
        .map { packages -> packages.map { it.toDomain() } }

    suspend fun refreshLinkGrabber(sessionToken: String, deviceId: String) {
        // TODO: Implement actual API call
    }

    suspend fun addLinksToLinkGrabber(
        sessionToken: String,
        deviceId: String,
        links: List<String>,
        packageName: String? = null,
        destinationFolder: String? = null,
        extractAfterDownload: Boolean = false,
        autoStart: Boolean = false
    ) {
        // TODO: Implement actual API call
    }

    suspend fun moveToDownloadList(
        sessionToken: String,
        deviceId: String,
        linkIds: List<String>? = null,
        packageIds: List<String>? = null
    ) {
        // TODO: Implement actual API call
    }

    // System Information
    suspend fun getSystemInfo(sessionToken: String, deviceId: String) {
        // TODO: Implement actual API call
    }

    // Account Hosters
    suspend fun getHostAccounts(sessionToken: String, deviceId: String): List<String> {
        // TODO: Implement actual API call
        return emptyList()
    }

    // Extension functions for domain mapping
    private fun com.shareconnect.jdownloaderconnect.data.model.DownloadPackage.toDomain(): com.shareconnect.jdownloaderconnect.domain.model.DownloadPackage {
        return com.shareconnect.jdownloaderconnect.domain.model.DownloadPackage(
            uuid = this.uuid,
            name = this.name,
            bytesTotal = this.bytesTotal,
            bytesLoaded = this.bytesLoaded,
            status = com.shareconnect.jdownloaderconnect.domain.model.DownloadStatus.valueOf(this.status.name),
            speed = 0,
            eta = 0,
            addedDate = this.addedDate,
            finishedDate = this.finishedDate
        )
    }

    private fun com.shareconnect.jdownloaderconnect.data.model.LinkGrabberPackage.toDomain(): com.shareconnect.jdownloaderconnect.domain.model.LinkGrabberPackage {
        return com.shareconnect.jdownloaderconnect.domain.model.LinkGrabberPackage(
            uuid = this.uuid,
            name = this.name,
            bytesTotal = this.bytesTotal,
            hosterCount = 0,
            enabled = true
        )
    }
}