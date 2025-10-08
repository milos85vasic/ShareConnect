package com.shareconnect.preferencessync.database

import android.content.Context
import androidx.room.*
import com.shareconnect.preferencessync.models.PreferencesData

@Database(entities = [PreferencesData::class], version = 1, exportSchema = false)
abstract class PreferencesDatabase : RoomDatabase() {
    abstract fun preferencesDao(): PreferencesDao

    companion object {
        @Volatile
        private var INSTANCE: PreferencesDatabase? = null

        fun getInstance(context: Context): PreferencesDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    PreferencesDatabase::class.java,
                    "synced_preferences_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
