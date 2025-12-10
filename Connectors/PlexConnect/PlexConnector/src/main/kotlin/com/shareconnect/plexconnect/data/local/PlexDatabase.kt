package com.shareconnect.plexconnect.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room database for Plex media caching
 */
@Database(
    entities = [
        PlexServerEntity::class, 
        PlexLibraryEntity::class, 
        PlexMediaItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(PlexTypeConverters::class)
abstract class PlexDatabase : RoomDatabase() {
    abstract fun plexServerDao(): PlexServerDao
    abstract fun plexLibraryDao(): PlexLibraryDao
    abstract fun plexMediaItemDao(): PlexMediaItemDao

    companion object {
        const val DATABASE_NAME = "plex_database"
    }
}