package com.shareconnect.plexconnect.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PlexAuthRequest(
    val user: PlexUserCredentials,
    val password: String? = null // For PIN-based auth if needed
)

@Serializable
data class PlexUserCredentials(
    val login: String,
    val password: String
)

@Serializable
data class PlexAuthResponse(
    val user: PlexUser? = null,
    val error: String? = null
)

@Serializable
data class PlexUser(
    val id: Long? = null,
    val uuid: String? = null,
    val email: String? = null,
    val joined_at: String? = null,
    val username: String? = null,
    val title: String? = null,
    val thumb: String? = null,
    val hasPassword: Boolean = false,
    val authToken: String? = null,
    val authentication_token: String? = null,
    val subscription: PlexSubscription? = null,
    val roles: PlexRoles? = null,
    val entitlements: List<String> = emptyList(),
    val confirmedAt: String? = null,
    val forumId: Long? = null
)

@Serializable
data class PlexSubscription(
    val active: Boolean = false,
    val status: String? = null,
    val plan: String? = null,
    val features: List<String> = emptyList()
)

@Serializable
data class PlexRoles(
    val roles: List<String> = emptyList()
)

@Serializable
data class PlexPinRequest(
    val strong: Boolean = true,
    val product: String = "PlexConnect",
    val clientIdentifier: String
)

@Serializable
data class PlexPinResponse(
    val id: Long,
    val code: String,
    val product: String,
    val trusted: Boolean = false,
    val clientIdentifier: String,
    val location: PlexLocation? = null,
    val expiresIn: Long,
    val createdAt: String,
    val expiresAt: String,
    val authToken: String? = null,
    val newRegistration: String? = null
)

@Serializable
data class PlexLocation(
    val code: String,
    val country: String,
    val city: String,
    val subdivisions: String,
    val coordinates: String
)