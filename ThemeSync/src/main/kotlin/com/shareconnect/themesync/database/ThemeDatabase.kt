package com.shareconnect.themesync.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shareconnect.themesync.models.ThemeData

@Database(entities = [ThemeData::class], version = 1, exportSchema = false)
abstract class ThemeDatabase : RoomDatabase() {
    abstract fun themeDao(): ThemeDao

    companion object {
        @Volatile
        private var INSTANCE: ThemeDatabase? = null

        fun getInstance(context: Context): ThemeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ThemeDatabase::class.java,
                    "theme_sync_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getInMemoryInstance(context: Context): ThemeDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                ThemeDatabase::class.java
            ).build()
        }
    }
}
