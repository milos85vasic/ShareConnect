package com.shareconnect.plexconnect.data.model

/**
 * Comprehensive media filtering options for Plex media library
 */
data class PlexMediaFilter(
    val type: MediaType? = null,
    val year: IntRange? = null,
    val genre: List<String>? = null,
    val rating: RatingFilter? = null,
    val duration: DurationFilter? = null,
    val watchStatus: WatchStatus? = null,
    val sortBy: SortOption? = null,
    val sortOrder: SortOrder = SortOrder.DESCENDING
) {
    /**
     * Enum for media watch status
     */
    enum class WatchStatus {
        WATCHED,
        UNWATCHED,
        IN_PROGRESS
    }

    /**
     * Enum for sorting options
     */
    enum class SortOption {
        TITLE,
        YEAR,
        DATE_ADDED,
        LAST_WATCHED,
        RATING
    }

    /**
     * Enum for sorting order
     */
    enum class SortOrder {
        ASCENDING,
        DESCENDING
    }

    /**
     * Rating filter configuration
     * @param minRating Minimum rating threshold
     * @param maxRating Maximum rating threshold
     */
    data class RatingFilter(
        val minRating: Double? = null,
        val maxRating: Double? = null
    )

    /**
     * Duration filter configuration
     * @param minDuration Minimum duration in minutes
     * @param maxDuration Maximum duration in minutes
     */
    data class DurationFilter(
        val minDuration: Int? = null,
        val maxDuration: Int? = null
    )
}