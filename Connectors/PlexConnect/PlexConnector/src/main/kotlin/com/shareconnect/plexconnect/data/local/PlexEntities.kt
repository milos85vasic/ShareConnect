package com.shareconnect.plexconnect.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shareconnect.plexconnect.data.model.MediaType
import java.time.Instant

/**
 * Entity for caching Plex server information
 */
@Entity(tableName = "plex_servers")
data class PlexServerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val secure: Boolean = true,
    val lastSyncTime: Instant = Instant.now()
)

/**
 * Entity for caching Plex library information
 */
@Entity(tableName = "plex_libraries")
data class PlexLibraryEntity(
    @PrimaryKey val id: String,
    val serverId: String,
    val title: String,
    val type: String,
    val lastSyncTime: Instant = Instant.now()
)

/**
 * Entity for caching Plex media items
 */
@Entity(tableName = "plex_media_items")
data class PlexMediaItemEntity(
    @PrimaryKey val id: String,
    val libraryId: String,
    val title: String,
    val type: String,
    val year: Int? = null,
    val summary: String? = null,
    val thumbnailUrl: String? = null,
    val lastPlayedTime: Instant? = null,
    val isWatched: Boolean = false,
    val lastSyncTime: Instant = Instant.now()
)

/**
 * Type converters for Room database to handle non-primitive types
 */
object PlexTypeConverters {
    @androidx.room.TypeConverter
    fun fromInstant(instant: Instant?): Long? = instant?.toEpochMilli()

    @androidx.room.TypeConverter
    fun toInstant(epochMilli: Long?): Instant? = 
        epochMilli?.let { Instant.ofEpochMilli(it) }
}