package com.shareconnect.syncthingconnect.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SyncthingData(
    val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null
)
