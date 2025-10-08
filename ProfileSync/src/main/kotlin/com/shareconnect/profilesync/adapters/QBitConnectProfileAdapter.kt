package com.shareconnect.profilesync.adapters

import com.shareconnect.profilesync.models.ProfileData

/**
 * Adapter for qBitConnect ServerConfig
 * Note: This uses a simplified representation to avoid dependency on qBitConnect module
 */
object QBitConnectProfileAdapter {

    data class ServerConfig(
        val id: Int,
        val name: String?,
        val url: String,
        val username: String?,
        val password: String?
    )

    fun toProfileData(serverConfig: ServerConfig, sourceApp: String): ProfileData {
        // Parse URL to extract host and port
        val url = serverConfig.url.replace(Regex("^https?://"), "")
        val parts = url.split(":")
        val host = parts[0]
        val port = parts.getOrNull(1)?.toIntOrNull() ?: 8080

        val useHttps = serverConfig.url.startsWith("https://")

        return ProfileData(
            id = "${sourceApp}_${serverConfig.id}",
            name = serverConfig.name ?: "qBittorrent Server",
            host = host,
            port = port,
            isDefault = false,  // Will be set separately
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_QBITTORRENT,
            username = serverConfig.username,
            password = serverConfig.password,
            sourceApp = sourceApp,
            version = 1,
            lastModified = System.currentTimeMillis(),
            useHttps = useHttps
        )
    }

    fun fromProfileData(profileData: ProfileData): ServerConfig {
        // Only convert qBittorrent profiles
        require(profileData.isQBitTorrentProfile()) {
            "ProfileData must be a qBittorrent profile"
        }

        val protocol = if (profileData.useHttps) "https" else "http"
        val url = "$protocol://${profileData.host}:${profileData.port}"

        // Extract numeric ID from the profile ID
        val id = profileData.id.substringAfterLast("_").toIntOrNull() ?: profileData.id.hashCode()

        return ServerConfig(
            id = id,
            name = profileData.name,
            url = url,
            username = profileData.username,
            password = profileData.password
        )
    }

    /**
     * Check if a ProfileData is eligible for qBitConnect (must be qBittorrent type)
     */
    fun isEligibleForQBitConnect(profileData: ProfileData): Boolean {
        return profileData.isQBitTorrentProfile()
    }
}
