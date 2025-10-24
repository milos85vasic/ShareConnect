package com.shareconnect.plexconnect

import android.app.Application
import android.content.Context
import com.shareconnect.plexconnect.data.database.PlexDatabase
import com.shareconnect.plexconnect.data.repository.PlexServerRepository
import com.shareconnect.plexconnect.di.DependencyContainer
import com.shareconnect.plexconnect.sync.PlexSyncManager
import androidx.room.Room

class PlexConnectApplication : Application() {

    lateinit var plexSyncManager: PlexSyncManager
        private set

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize manual dependency injection
        DependencyContainer.init(this)

        // Initialize Plex sync manager
        try {
            plexSyncManager = PlexSyncManager.getInstance(
                this,
                "PlexConnect",
                "PlexConnect",
                "1.0.0",
                DependencyContainer.plexServerRepository
            )
        } catch (e: Exception) {
            // Handle initialization errors
            e.printStackTrace()
        }
    }
}