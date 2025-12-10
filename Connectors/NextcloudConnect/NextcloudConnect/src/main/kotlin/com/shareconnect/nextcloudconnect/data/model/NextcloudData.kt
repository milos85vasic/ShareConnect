package com.shareconnect.nextcloudconnect.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NextcloudData(
    val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null
)
