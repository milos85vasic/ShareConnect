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


package com.shareconnect.profilesync.adapters

import com.shareconnect.profilesync.models.ProfileData

/**
 * Adapter for uTorrentConnect Server
 * Note: This uses a simplified representation to avoid dependency on uTorrentConnect module
 */
object UTorrentConnectProfileAdapter {

    data class Server(
        val id: String,
        val name: String,
        val host: String,
        val port: Int,
        val useAuthentication: Boolean,
        val username: String?,
        val password: String?,
        val rpcUrl: String,
        val useHttps: Boolean,
        val trustSelfSignedSslCert: Boolean
    )

    fun toProfileData(server: Server, sourceApp: String): ProfileData {
        return ProfileData(
            id = server.id,
            name = server.name,
            host = server.host,
            port = server.port,
            isDefault = false,  // Will be set separately
            serviceType = ProfileData.TYPE_TORRENT,
            torrentClientType = ProfileData.TORRENT_CLIENT_UTORRENT,
            username = if (server.useAuthentication) server.username else null,
            password = if (server.useAuthentication) server.password else null,
            sourceApp = sourceApp,
            version = 1,
            lastModified = System.currentTimeMillis(),
            rpcUrl = server.rpcUrl,
            useHttps = server.useHttps,
            trustSelfSignedCert = server.trustSelfSignedSslCert
        )
    }

    fun fromProfileData(profileData: ProfileData): Server {
        // Only convert uTorrent profiles
        require(profileData.isUTorrentProfile()) {
            "ProfileData must be a uTorrent profile"
        }

        val hasAuth = profileData.username != null && profileData.password != null

        return Server(
            id = profileData.id,
            name = profileData.name,
            host = profileData.host,
            port = profileData.port,
            useAuthentication = hasAuth,
            username = profileData.username,
            password = profileData.password,
            rpcUrl = profileData.rpcUrl ?: "gui",
            useHttps = profileData.useHttps,
            trustSelfSignedSslCert = profileData.trustSelfSignedCert
        )
    }

    /**
     * Check if a ProfileData is eligible for uTorrentConnect (must be uTorrent type)
     */
    fun isEligibleForUTorrentConnect(profileData: ProfileData): Boolean {
        return profileData.isUTorrentProfile()
    }
}
