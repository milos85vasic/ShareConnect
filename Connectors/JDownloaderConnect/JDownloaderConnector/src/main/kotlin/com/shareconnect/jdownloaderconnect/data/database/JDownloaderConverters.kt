package com.shareconnect.jdownloaderconnect.data.database

import androidx.room.TypeConverter
import com.shareconnect.jdownloaderconnect.data.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JDownloaderConverters {

    @TypeConverter
    fun fromDownloadPriority(priority: DownloadPriority): String = priority.name

    @TypeConverter
    fun toDownloadPriority(priority: String): DownloadPriority = DownloadPriority.valueOf(priority)

    @TypeConverter
    fun fromDownloadStatus(status: DownloadStatus): String = status.name

    @TypeConverter
    fun toDownloadStatus(status: String): DownloadStatus = DownloadStatus.valueOf(status)

    @TypeConverter
    fun fromLinkAvailability(availability: LinkAvailability): String = availability.name

    @TypeConverter
    fun toLinkAvailability(availability: String): LinkAvailability = LinkAvailability.valueOf(availability)

    @TypeConverter
    fun fromExtractorStatus(status: ExtractorStatus?): String? = status?.name

    @TypeConverter
    fun toExtractorStatus(status: String?): ExtractorStatus? = status?.let { ExtractorStatus.valueOf(it) }

    @TypeConverter
    fun fromStringList(list: List<String>): String = Json.encodeToString(list)

    @TypeConverter
    fun toStringList(json: String): List<String> = Json.decodeFromString(json)
}