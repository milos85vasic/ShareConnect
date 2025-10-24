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