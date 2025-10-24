package com.shareconnect.plexconnect.data.database

import androidx.room.TypeConverter
import com.shareconnect.plexconnect.data.model.LibraryType
import com.shareconnect.plexconnect.data.model.MediaType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PlexTypeConverters {

    private val json = Json { ignoreUnknownKeys = true }

    // LibraryType converters
    @TypeConverter
    fun fromLibraryType(type: LibraryType): String = type.value

    @TypeConverter
    fun toLibraryType(value: String): LibraryType = LibraryType.fromString(value)

    // MediaType converters
    @TypeConverter
    fun fromMediaType(type: MediaType): String = type.value

    @TypeConverter
    fun toMediaType(value: String): MediaType = MediaType.fromString(value)

    // List<String> converters
    @TypeConverter
    fun fromStringList(list: List<String>): String = json.encodeToString(list)

    @TypeConverter
    fun toStringList(value: String): List<String> = json.decodeFromString(value)
}