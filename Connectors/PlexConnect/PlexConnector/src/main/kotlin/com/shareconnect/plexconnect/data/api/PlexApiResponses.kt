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


package com.shareconnect.plexconnect.data.api

import com.shareconnect.plexconnect.data.model.*
import kotlinx.serialization.Serializable

@Serializable
data class PlexServerInfo(
    val machineIdentifier: String? = null,
    val version: String? = null,
    val name: String? = null,
    val host: String? = null,
    val address: String? = null,
    val port: Int? = null,
    val scheme: String? = null,
    val localAddresses: String? = null,
    val owned: Int? = null,
    val synced: Int? = null
)

@Serializable
data class PlexLibraryResponse(
    val size: Int? = null,
    val allowSync: Boolean? = null,
    val identifier: String? = null,
    val mediaContainer: PlexMediaContainer<PlexLibrary>? = null
)

@Serializable
data class PlexMediaResponse(
    val size: Int? = null,
    val allowSync: Boolean? = null,
    val identifier: String? = null,
    val librarySectionTitle: String? = null,
    val librarySectionID: Int? = null,
    val librarySectionUUID: String? = null,
    val mediaContainer: PlexMediaContainer<PlexMediaItem>? = null
)

@Serializable
data class PlexSearchResponse(
    val size: Int? = null,
    val allowSync: Boolean? = null,
    val identifier: String? = null,
    val mediaContainer: PlexMediaContainer<PlexMediaItem>? = null
)

@Serializable
data class PlexMediaContainer<T>(
    val size: Int = 0,
    val totalSize: Int? = null,
    val offset: Int = 0,
    val identifier: String? = null,
    val librarySectionTitle: String? = null,
    val librarySectionID: Int? = null,
    val librarySectionUUID: String? = null,
    val media: List<T> = emptyList(),
    val Metadata: List<T> = emptyList(),
    val Directory: List<T> = emptyList()
)