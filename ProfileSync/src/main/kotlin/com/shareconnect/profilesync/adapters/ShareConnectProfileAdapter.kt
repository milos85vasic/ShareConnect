package com.shareconnect.profilesync.adapters

import com.shareconnect.profilesync.models.ProfileData

/**
 * Adapter for ShareConnect ServerProfile
 * Note: This uses a simplified representation to avoid dependency on ShareConnect module
 */
object ShareConnectProfileAdapter {

    data class ServerProfile(
        val id: String?,
        val name: String?,
        val url: String?,
        val port: Int,
        val isDefault: Boolean,
        val serviceType: String?,
        val torrentClientType: String?,
        val username: String?,
        val password: String?
    )

    fun toProfileData(serverProfile: ServerProfile, sourceApp: String): ProfileData {
        return ProfileData(
            id = serverProfile.id ?: java.util.UUID.randomUUID().toString(),
            name = serverProfile.name ?: "Unnamed",
            host = serverProfile.url ?: "localhost",
            port = serverProfile.port,
            isDefault = serverProfile.isDefault,
            serviceType = serverProfile.serviceType ?: ProfileData.TYPE_METUBE,
            torrentClientType = serverProfile.torrentClientType,
            username = serverProfile.username,
            password = serverProfile.password,
            sourceApp = sourceApp,
            version = 1,
            lastModified = System.currentTimeMillis()
        )
    }

    fun fromProfileData(profileData: ProfileData): ServerProfile {
        return ServerProfile(
            id = profileData.id,
            name = profileData.name,
            url = profileData.host,
            port = profileData.port,
            isDefault = profileData.isDefault,
            serviceType = profileData.serviceType,
            torrentClientType = profileData.torrentClientType,
            username = profileData.username,
            password = profileData.password
        )
    }
}
