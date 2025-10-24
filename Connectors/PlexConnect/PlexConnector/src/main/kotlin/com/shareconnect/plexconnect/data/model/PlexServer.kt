package com.shareconnect.plexconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "plex_servers")
data class PlexServer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String, // IP address or hostname
    val port: Int = 32400,
    val token: String? = null,
    val isLocal: Boolean = true,
    val isOwned: Boolean = false,
    val ownerId: Long? = null,
    val machineIdentifier: String? = null,
    val version: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val baseUrl: String
        get() = "http://$address:$port"

    val isAuthenticated: Boolean
        get() = !token.isNullOrBlank()
}