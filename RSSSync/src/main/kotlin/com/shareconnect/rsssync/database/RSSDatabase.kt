package com.shareconnect.rsssync.database

import android.content.Context
import androidx.room.*
import com.shareconnect.rsssync.models.RSSFeedData

@Database(entities = [RSSFeedData::class], version = 1, exportSchema = false)
abstract class RSSDatabase : RoomDatabase() {
    abstract fun rssDao(): RSSDao

    companion object {
        @Volatile
        private var INSTANCE: RSSDatabase? = null

        fun getInstance(context: Context): RSSDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    RSSDatabase::class.java,
                    "synced_rss_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
