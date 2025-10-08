package com.shareconnect.historysync.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shareconnect.historysync.models.HistoryData

@Database(entities = [HistoryData::class], version = 1, exportSchema = false)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        fun getInstance(context: Context): HistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDatabase::class.java,
                    "synced_history_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getInstanceForTesting(context: Context): HistoryDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                HistoryDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}
