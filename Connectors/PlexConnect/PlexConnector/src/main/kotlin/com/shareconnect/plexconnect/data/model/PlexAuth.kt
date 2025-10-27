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