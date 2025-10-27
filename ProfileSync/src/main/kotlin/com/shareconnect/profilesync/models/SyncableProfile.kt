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


package com.shareconnect.profilesync.models

import digital.vasic.asinka.models.SyncableObject

class SyncableProfile(
    private var profileData: ProfileData
) : SyncableObject {

    override val objectId: String
        get() = profileData.id

    override val objectType: String
        get() = ProfileData.OBJECT_TYPE

    override val version: Int
        get() = profileData.version

    fun getProfileData(): ProfileData = profileData

    override fun toFieldMap(): Map<String, Any?> {
        return mapOf(
            "id" to profileData.id,
            "name" to profileData.name,
            "host" to profileData.host,
            "port" to profileData.port,
            "isDefault" to profileData.isDefault,
            "serviceType" to profileData.serviceType,
            "torrentClientType" to profileData.torrentClientType,
            "username" to profileData.username,
            "password" to profileData.password,
            "sourceApp" to profileData.sourceApp,
            "version" to profileData.version,
            "lastModified" to profileData.lastModified,
            "rpcUrl" to profileData.rpcUrl,
            "useHttps" to profileData.useHttps,
            "trustSelfSignedCert" to profileData.trustSelfSignedCert
        )
    }

    override fun fromFieldMap(fields: Map<String, Any?>) {
        // Handle version field - could be Int or Long from field map
        val versionValue = when (val v = fields["version"]) {
            is Int -> v
            is Long -> v.toInt()
            else -> profileData.version
        }

        profileData = ProfileData(
            id = fields["id"] as? String ?: profileData.id,
            name = fields["name"] as? String ?: profileData.name,
            host = fields["host"] as? String ?: profileData.host,
            port = (fields["port"] as? Long)?.toInt() ?: (fields["port"] as? Int) ?: profileData.port,
            isDefault = fields["isDefault"] as? Boolean ?: profileData.isDefault,
            serviceType = fields["serviceType"] as? String ?: profileData.serviceType,
            torrentClientType = fields["torrentClientType"] as? String,
            username = fields["username"] as? String,
            password = fields["password"] as? String,
            sourceApp = fields["sourceApp"] as? String ?: profileData.sourceApp,
            version = versionValue,
            lastModified = fields["lastModified"] as? Long ?: profileData.lastModified,
            rpcUrl = fields["rpcUrl"] as? String,
            useHttps = fields["useHttps"] as? Boolean ?: profileData.useHttps,
            trustSelfSignedCert = fields["trustSelfSignedCert"] as? Boolean ?: profileData.trustSelfSignedCert
        )
    }

    companion object {
        fun fromProfileData(profileData: ProfileData): SyncableProfile {
            return SyncableProfile(profileData)
        }
    }
}
