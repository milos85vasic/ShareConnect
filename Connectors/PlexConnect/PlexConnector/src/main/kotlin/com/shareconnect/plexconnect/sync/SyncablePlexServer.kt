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


package com.shareconnect.plexconnect.sync

import com.shareconnect.plexconnect.data.model.PlexServer
import digital.vasic.asinka.models.SyncableObject

class SyncablePlexServer(
    private var server: PlexServer
) : SyncableObject {

    override val objectId: String
        get() = server.id.toString()

    override val objectType: String
        get() = OBJECT_TYPE

    override val version: Int
        get() = server.updatedAt.toInt()

    override fun toFieldMap(): Map<String, Any?> {
        return mapOf(
            "id" to server.id,
            "name" to server.name,
            "address" to server.address,
            "port" to server.port,
            "isLocal" to server.isLocal,
            "isOwned" to server.isOwned,
            "ownerId" to server.ownerId,
            "machineIdentifier" to server.machineIdentifier,
            "version" to server.version,
            "createdAt" to server.createdAt,
            "updatedAt" to server.updatedAt
        )
    }

    override fun fromFieldMap(fields: Map<String, Any?>) {
        server = server.copy(
            id = fields["id"] as? Long ?: server.id,
            name = fields["name"] as? String ?: server.name,
            address = fields["address"] as? String ?: server.address,
            port = fields["port"] as? Int ?: server.port,
            isLocal = fields["isLocal"] as? Boolean ?: server.isLocal,
            isOwned = fields["isOwned"] as? Boolean ?: server.isOwned,
            ownerId = fields["ownerId"] as? Long ?: server.ownerId,
            machineIdentifier = fields["machineIdentifier"] as? String ?: server.machineIdentifier,
            version = fields["version"] as? String ?: server.version,
            createdAt = fields["createdAt"] as? Long ?: server.createdAt,
            updatedAt = fields["updatedAt"] as? Long ?: server.updatedAt
        )
    }

    fun getPlexServer(): PlexServer = server

    companion object {
        const val OBJECT_TYPE = "plex_server"

        fun fromPlexServer(server: PlexServer): SyncablePlexServer {
            return SyncablePlexServer(server)
        }
    }
}