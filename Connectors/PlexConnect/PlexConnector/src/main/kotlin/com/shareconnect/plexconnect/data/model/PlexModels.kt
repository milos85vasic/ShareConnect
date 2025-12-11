package com.shareconnect.plexconnect.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PlexServerInfo(
    val machineIdentifier: String? = null,
    val version: String? = null,
    val myPlexUsername: String? = null,
    val platform: String? = null,
    val platformVersion: String? = null
)

@Serializable
data class PlexLibrarySection(
    val key: String,
    val title: String,
    val type: LibraryType
)

@Serializable
data class PlexLibraryResponse(
    val size: Int? = null,
    val allowSync: Boolean? = null,
    val sections: List<PlexLibrarySection>? = null
)

@Serializable
data class PlexMediaResponse(
    val size: Int? = null,
    val totalSize: Int? = null,
    val offset: Int? = null,
    val items: List<PlexMediaItemDto>? = null
)

@Serializable
data class PlexMediaItemDto(
    val ratingKey: String? = null,
    val key: String? = null,
    val guid: String? = null,
    val studio: String? = null,
    val type: String? = null,
    val title: String? = null,
    val titleSort: String? = null,
    val summary: String? = null,
    val index: Int? = null,
    val year: Int? = null,
    val duration: Long? = null,
    val librarySectionTitle: String? = null,
    val librarySectionID: Long? = null
)

@Serializable
data class PlexSearchResponse(
    val size: Int? = null,
    val totalSize: Int? = null,
    val offset: Int? = null,
    val items: List<PlexMediaItemDto>? = null
)