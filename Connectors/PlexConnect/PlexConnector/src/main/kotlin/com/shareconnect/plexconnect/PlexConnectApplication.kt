package com.shareconnect.plexconnect

import android.app.Application
import com.shareconnect.plexconnect.data.database.PlexDatabase
import com.shareconnect.plexconnect.data.repository.PlexServerRepository
import com.shareconnect.plexconnect.sync.PlexSyncManager
import androidx.room.Room

class PlexConnectApplication : Application() {

    lateinit var plexSyncManager: PlexSyncManager
        private set

    // Database and repositories
    lateinit var plexDatabase: PlexDatabase
        private set

    lateinit var plexServerRepository: PlexServerRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize database
        plexDatabase = Room.databaseBuilder(
            applicationContext,
            PlexDatabase::class.java,
            PlexDatabase.DATABASE_NAME
        ).build()

        // Initialize repositories
        plexServerRepository = PlexServerRepository(
            plexDatabase.plexServerDao(),
            com.shareconnect.plexconnect.data.api.PlexApiClient()
        )

        // Initialize sync manager
        try {
            plexSyncManager = PlexSyncManager.getInstance(this, "PlexConnect", "PlexConnect", "1.0.0", plexServerRepository)
        } catch (e: Exception) {
            // Handle initialization errors
            e.printStackTrace()
        }
    }
}