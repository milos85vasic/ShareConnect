package com.shareconnect.jdownloaderconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "server_profiles")
@Serializable
data class ServerProfile(
    @PrimaryKey
    val id: String,
    val name: String,
    val serverUrl: String,
    val username: String? = null,
    val password: String? = null,
    val isDefault: Boolean = false,
    val serviceType: ServiceType = ServiceType.JDOWNLOADER,
    val connectionTimeout: Int = 30,
    val readTimeout: Int = 60,
    val enabled: Boolean = true,
    val lastConnected: Long? = null
)

@Serializable
enum class ServiceType {
    JDOWNLOADER, QBITTORRENT, TRANSMISSION, UTORRENT
}