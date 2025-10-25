package com.shareconnect.jellyfinconnect.data.models

import com.google.gson.annotations.SerializedName

/**
 * Jellyfin authentication response
 */
data class JellyfinAuthResponse(
    @SerializedName("User") val user: JellyfinUser?,
    @SerializedName("SessionInfo") val sessionInfo: JellyfinSession?,
    @SerializedName("AccessToken") val accessToken: String,
    @SerializedName("ServerId") val serverId: String
)

/**
 * Jellyfin user information
 */
data class JellyfinUser(
    @SerializedName("Name") val name: String,
    @SerializedName("ServerId") val serverId: String,
    @SerializedName("Id") val id: String,
    @SerializedName("HasPassword") val hasPassword: Boolean,
    @SerializedName("HasConfiguredPassword") val hasConfiguredPassword: Boolean,
    @SerializedName("HasConfiguredEasyPassword") val hasConfiguredEasyPassword: Boolean,
    @SerializedName("EnableAutoLogin") val enableAutoLogin: Boolean? = null,
    @SerializedName("LastLoginDate") val lastLoginDate: String? = null,
    @SerializedName("LastActivityDate") val lastActivityDate: String? = null
)

/**
 * Jellyfin session information
 */
data class JellyfinSession(
    @SerializedName("Id") val id: String,
    @SerializedName("UserId") val userId: String,
    @SerializedName("UserName") val userName: String,
    @SerializedName("Client") val client: String,
    @SerializedName("LastActivityDate") val lastActivityDate: String,
    @SerializedName("DeviceName") val deviceName: String,
    @SerializedName("DeviceId") val deviceId: String,
    @SerializedName("ApplicationVersion") val applicationVersion: String
)

/**
 * Jellyfin server information
 */
data class JellyfinServerInfo(
    @SerializedName("LocalAddress") val localAddress: String? = null,
    @SerializedName("ServerName") val serverName: String,
    @SerializedName("Version") val version: String,
    @SerializedName("ProductName") val productName: String? = null,
    @SerializedName("OperatingSystem") val operatingSystem: String? = null,
    @SerializedName("Id") val id: String,
    @SerializedName("StartupWizardCompleted") val startupWizardCompleted: Boolean? = null
)

/**
 * Jellyfin library/view
 */
data class JellyfinLibrary(
    @SerializedName("Name") val name: String,
    @SerializedName("ServerId") val serverId: String,
    @SerializedName("Id") val id: String,
    @SerializedName("CollectionType") val collectionType: String? = null,
    @SerializedName("ImageTags") val imageTags: Map<String, String>? = null,
    @SerializedName("PrimaryImageAspectRatio") val primaryImageAspectRatio: Double? = null
)

/**
 * Jellyfin item query result
 */
data class JellyfinItemsResult(
    @SerializedName("Items") val items: List<JellyfinItem>,
    @SerializedName("TotalRecordCount") val totalRecordCount: Int,
    @SerializedName("StartIndex") val startIndex: Int
)

/**
 * Jellyfin media item
 */
data class JellyfinItem(
    @SerializedName("Name") val name: String,
    @SerializedName("ServerId") val serverId: String,
    @SerializedName("Id") val id: String,
    @SerializedName("Type") val type: String,
    @SerializedName("CollectionType") val collectionType: String? = null,
    @SerializedName("ProductionYear") val productionYear: Int? = null,
    @SerializedName("RunTimeTicks") val runTimeTicks: Long? = null,
    @SerializedName("PremiereDate") val premiereDate: String? = null,
    @SerializedName("OfficialRating") val officialRating: String? = null,
    @SerializedName("Overview") val overview: String? = null,
    @SerializedName("ImageTags") val imageTags: Map<String, String>? = null,
    @SerializedName("BackdropImageTags") val backdropImageTags: List<String>? = null,
    @SerializedName("UserData") val userData: JellyfinUserData? = null,
    @SerializedName("MediaType") val mediaType: String? = null,
    @SerializedName("IndexNumber") val indexNumber: Int? = null,
    @SerializedName("ParentIndexNumber") val parentIndexNumber: Int? = null,
    @SerializedName("SeriesId") val seriesId: String? = null,
    @SerializedName("SeasonId") val seasonId: String? = null,
    @SerializedName("SeriesName") val seriesName: String? = null
)

/**
 * Jellyfin user-specific data for an item
 */
data class JellyfinUserData(
    @SerializedName("PlaybackPositionTicks") val playbackPositionTicks: Long? = null,
    @SerializedName("PlayCount") val playCount: Int? = null,
    @SerializedName("IsFavorite") val isFavorite: Boolean? = null,
    @SerializedName("Played") val played: Boolean? = null,
    @SerializedName("Key") val key: String? = null,
    @SerializedName("UnplayedItemCount") val unplayedItemCount: Int? = null
)

/**
 * Jellyfin authentication request
 */
data class JellyfinAuthRequest(
    @SerializedName("Username") val username: String,
    @SerializedName("Pw") val pw: String
)

/**
 * Jellyfin playback progress report
 */
data class JellyfinPlaybackProgress(
    @SerializedName("ItemId") val itemId: String,
    @SerializedName("PositionTicks") val positionTicks: Long,
    @SerializedName("IsPaused") val isPaused: Boolean = false,
    @SerializedName("IsMuted") val isMuted: Boolean = false,
    @SerializedName("VolumeLevel") val volumeLevel: Int = 100
)

/**
 * Jellyfin search hint result
 */
data class JellyfinSearchResult(
    @SerializedName("SearchHints") val searchHints: List<JellyfinSearchHint>,
    @SerializedName("TotalRecordCount") val totalRecordCount: Int
)

/**
 * Jellyfin search hint
 */
data class JellyfinSearchHint(
    @SerializedName("ItemId") val itemId: String,
    @SerializedName("Id") val id: String,
    @SerializedName("Name") val name: String,
    @SerializedName("Type") val type: String,
    @SerializedName("MediaType") val mediaType: String? = null,
    @SerializedName("ProductionYear") val productionYear: Int? = null,
    @SerializedName("MatchedTerm") val matchedTerm: String? = null
)
