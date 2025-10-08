package com.shareconnect.profilesync.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shareconnect.profilesync.models.ProfileData

@Database(entities = [ProfileData::class], version = 1, exportSchema = false)
abstract class ProfileDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: ProfileDatabase? = null

        fun getInstance(context: Context): ProfileDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProfileDatabase::class.java,
                    "synced_profiles_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getInstanceForTesting(context: Context): ProfileDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                ProfileDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}
