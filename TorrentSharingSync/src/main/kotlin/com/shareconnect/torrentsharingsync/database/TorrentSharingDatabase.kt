package com.shareconnect.torrentsharingsync.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shareconnect.torrentsharingsync.models.TorrentSharingData

/**
 * Room database for torrent sharing preferences synchronization
 */
@Database(
    entities = [TorrentSharingData::class],
    version = 1,
    exportSchema = false
)
abstract class TorrentSharingDatabase : RoomDatabase() {

    abstract fun torrentSharingDao(): TorrentSharingDao

    companion object {
        @Volatile
        private var INSTANCE: TorrentSharingDatabase? = null

        fun getInstance(context: Context): TorrentSharingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TorrentSharingDatabase::class.java,
                    "torrent_sharing_sync_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
