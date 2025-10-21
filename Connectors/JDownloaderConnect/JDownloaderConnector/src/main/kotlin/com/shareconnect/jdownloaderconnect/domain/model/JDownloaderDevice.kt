package com.shareconnect.jdownloaderconnect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class JDownloaderDevice(
    val id: String,
    val name: String,
    val type: String,
    val status: String,
    val version: String,
    val isConnected: Boolean = false
)

@Serializable
data class DeviceConnection(
    val deviceId: String,
    val sessionToken: String,
    val serverEncryptionToken: String,
    val deviceEncryptionToken: String,
    val isActive: Boolean = true
)