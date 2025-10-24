package com.shareconnect.plexconnect.data.api

import com.shareconnect.plexconnect.data.model.*
import kotlinx.serialization.Serializable

@Serializable
data class PlexServerInfo(
    val machineIdentifier: String? = null,
    val version: String? = null,
    val name: String? = null,
    val host: String? = null,
    val address: String? = null,
    val port: Int? = null,
    val scheme: String? = null,
    val localAddresses: String? = null,
    val owned: Int? = null,
    val synced: Int? = null
)

@Serializable
data class PlexLibraryResponse(
    val size: Int? = null,
    val allowSync: Boolean? = null,
    val identifier: String? = null,
    val mediaContainer: PlexMediaContainer<PlexLibrary>? = null
)

@Serializable
data class PlexMediaResponse(
    val size: Int? = null,
    val allowSync: Boolean? = null,
    val identifier: String? = null,
    val librarySectionTitle: String? = null,
    val librarySectionID: Int? = null,
    val librarySectionUUID: String? = null,
    val mediaContainer: PlexMediaContainer<PlexMediaItem>? = null
)

@Serializable
data class PlexSearchResponse(
    val size: Int? = null,
    val allowSync: Boolean? = null,
    val identifier: String? = null,
    val mediaContainer: PlexMediaContainer<PlexMediaItem>? = null
)

@Serializable
data class PlexMediaContainer<T>(
    val size: Int = 0,
    val totalSize: Int? = null,
    val offset: Int = 0,
    val identifier: String? = null,
    val librarySectionTitle: String? = null,
    val librarySectionID: Int? = null,
    val librarySectionUUID: String? = null,
    val Metadata: List<T> = emptyList(),
    val Directory: List<T> = emptyList()
)