/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


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
