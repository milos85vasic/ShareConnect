package com.shareconnect.plexconnect.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shareconnect.plexconnect.data.database.dao.PlexLibraryDao
import com.shareconnect.plexconnect.data.database.dao.PlexMediaItemDao
import com.shareconnect.plexconnect.data.database.dao.PlexServerDao
import com.shareconnect.plexconnect.data.model.PlexLibrary
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.data.model.PlexServer

@Database(
    entities = [
        PlexServer::class,
        PlexLibrary::class,
        PlexMediaItem::class
    ],
    version = 1,
    exportSchema = true
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