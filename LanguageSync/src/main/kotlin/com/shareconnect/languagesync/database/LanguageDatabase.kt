package com.shareconnect.languagesync.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shareconnect.languagesync.models.LanguageData

/**
 * Room database for language preference synchronization
 */
@Database(
    entities = [LanguageData::class],
    version = 1,
    exportSchema = false
)
abstract class LanguageDatabase : RoomDatabase() {

    abstract fun languageDao(): LanguageDao

    companion object {
        @Volatile
        private var INSTANCE: LanguageDatabase? = null

        fun getInstance(context: Context): LanguageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LanguageDatabase::class.java,
                    "language_sync_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
