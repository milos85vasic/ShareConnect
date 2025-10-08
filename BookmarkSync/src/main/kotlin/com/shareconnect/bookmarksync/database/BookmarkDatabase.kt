package com.shareconnect.bookmarksync.database

import android.content.Context
import androidx.room.*
import com.shareconnect.bookmarksync.models.BookmarkData

@Database(entities = [BookmarkData::class], version = 1, exportSchema = false)
abstract class BookmarkDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        @Volatile
        private var INSTANCE: BookmarkDatabase? = null

        fun getInstance(context: Context): BookmarkDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    BookmarkDatabase::class.java,
                    "synced_bookmarks_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
