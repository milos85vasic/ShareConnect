package com.shareconnect.jdownloaderconnect

import android.app.Application
import androidx.room.Room
import com.shareconnect.jdownloaderconnect.data.database.JDownloaderDatabase
import com.shareconnect.jdownloaderconnect.sync.JDownloaderSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class JDownloaderConnectApplication : Application() {

    companion object {
        lateinit var database: JDownloaderDatabase
            private set
        
        lateinit var syncManager: JDownloaderSyncManager
            private set
    }
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        
        // Initialize database
        database = Room.databaseBuilder(
            applicationContext,
            JDownloaderDatabase::class.java,
            "jdownloader_database"
        ).fallbackToDestructiveMigration()
         .build()
        
        // Initialize sync manager
        syncManager = JDownloaderSyncManager.getInstance(
            context = applicationContext,
            appId = "jdownloaderconnect",
            appName = "JDownloaderConnect",
            appVersion = "1.0.0"
        )
        
        // Start synchronization in background
        applicationScope.launch {
            syncManager.startSync()
        }
    }
}