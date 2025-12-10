package com.shareconnect.matrixconnect.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MatrixData(
    val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null
)
