package com.shareconnect.jellyfinconnect.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JellyfinData(
    val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null
)
