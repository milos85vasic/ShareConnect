package com.shareconnect.plexconnect.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PlexData(
    val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null
)
