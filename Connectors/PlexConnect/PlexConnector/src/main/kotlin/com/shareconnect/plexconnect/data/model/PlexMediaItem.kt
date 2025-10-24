package com.shareconnect.plexconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "plex_media_items")
data class PlexMediaItem(
    @PrimaryKey
    val ratingKey: String,
    val key: String,
    val guid: String? = null,
    val librarySectionTitle: String? = null,
    val librarySectionID: Long? = null,
    val librarySectionKey: String? = null,
    val type: MediaType,
    val title: String,
    val titleSort: String? = null,
    val originalTitle: String? = null,
    val summary: String? = null,
    val rating: Float? = null,
    val audienceRating: Float? = null,
    val year: Int? = null,
    val tagline: String? = null,
    val thumb: String? = null,
    val art: String? = null,
    val duration: Long? = null, // in milliseconds
    val originallyAvailableAt: String? = null, // ISO date string
    val addedAt: Long? = null,
    val updatedAt: Long? = null,
    val audienceRatingImage: String? = null,
    val chapterSource: String? = null,
    val primaryExtraKey: String? = null,
    val ratingImage: String? = null,
    val viewCount: Int = 0,
    val lastViewedAt: Long? = null,
    val viewOffset: Long = 0, // resume position in milliseconds
    val skipCount: Int = 0,
    val lastSkippedAt: Long? = null,
    val studio: String? = null,
    val contentRating: String? = null,
    val directors: List<String> = emptyList(),
    val writers: List<String> = emptyList(),
    val actors: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val collections: List<String> = emptyList(),
    val labels: List<String> = emptyList(),
    val serverId: Long
) {
    val isWatched: Boolean
        get() = viewCount > 0

    val isPartiallyWatched: Boolean
        get() = viewOffset > 0 && !isWatched

    val progressPercentage: Float
        get() = if (duration != null && duration > 0) {
            (viewOffset.toFloat() / duration.toFloat()) * 100f
        } else 0f
}

enum class MediaType(val value: String) {
    MOVIE("movie"),
    EPISODE("episode"),
    SEASON("season"),
    SHOW("show"),
    TRACK("track"),
    ALBUM("album"),
    ARTIST("artist"),
    PHOTO("photo"),
    CLIP("clip"),
    UNKNOWN("unknown");

    companion object {
        fun fromString(value: String): MediaType {
            return entries.find { it.value == value } ?: UNKNOWN
        }
    }
}