package com.shareconnect.qa.ai.models

import kotlinx.serialization.Serializable

/**
 * Test profile data model for AI QA system
 */
@Serializable
data class TestProfile(
    val id: String,
    val name: String,
    val url: String,
    val port: Int,
    val isDefault: Boolean = false,
    val serviceType: ServiceType,
    val torrentClientType: TorrentClientType? = null,
    val authentication: Authentication? = null,
    val isEdgeCase: Boolean = false,
    val edgeCaseType: String? = null,
    val description: String = ""
)

@Serializable
enum class ServiceType {
    METUBE,
    YTDL,
    TORRENT,
    JDOWNLOADER
}

@Serializable
enum class TorrentClientType {
    QBITTORRENT,
    TRANSMISSION,
    UTORRENT
}

@Serializable
data class Authentication(
    val username: String,
    val password: String
)

/**
 * Test scenario combining multiple profiles
 */
@Serializable
data class ProfileTestScenario(
    val id: String,
    val name: String,
    val description: String,
    val profiles: List<TestProfile>,
    val expectedBehavior: String,
    val testType: ProfileTestType
)

@Serializable
enum class ProfileTestType {
    CREATE,
    EDIT,
    DELETE,
    SET_DEFAULT,
    MULTI_PROFILE,
    VALIDATION,
    EDGE_CASE
}
