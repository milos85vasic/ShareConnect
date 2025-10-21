package com.shareconnect.jdownloaderconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "jdownloader_accounts")
@Serializable
data class JDownloaderAccount(
    @PrimaryKey
    val id: String,
    val email: String,
    val password: String,
    val deviceName: String,
    val deviceId: String,
    val isActive: Boolean = false,
    val lastSync: Long = System.currentTimeMillis(),
    val serverUrl: String = "https://api.jdownloader.org",
    val appKey: String = "ShareConnect_JDownloaderConnect"
)