package com.shareconnect.wireguardconnect.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WireGuardData(
    val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null
)
