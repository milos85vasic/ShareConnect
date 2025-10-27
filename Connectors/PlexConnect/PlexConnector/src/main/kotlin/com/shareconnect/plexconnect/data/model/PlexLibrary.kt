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

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "plex_libraries")
data class PlexLibrary(
    @PrimaryKey
    val key: String,
    val title: String,
    val type: LibraryType,
    val serverId: Long,
    val agent: String? = null,
    val scanner: String? = null,
    val language: String? = null,
    val uuid: String? = null,
    val updatedAt: Long? = null,
    val createdAt: Long? = null,
    val scannedAt: Long? = null,
    val content: Boolean = true,
    val directory: Boolean = true,
    val contentChangedAt: Long? = null,
    val hidden: Int = 0,
    val art: String = "",
    val composite: String = "",
    val thumb: String = ""
)

enum class LibraryType(val value: String) {
    MOVIE("movie"),
    SHOW("show"),
    MUSIC("artist"),
    PHOTO("photo"),
    UNKNOWN("unknown");

    companion object {
        fun fromString(value: String): LibraryType {
            return entries.find { it.value == value } ?: UNKNOWN
        }
    }
}