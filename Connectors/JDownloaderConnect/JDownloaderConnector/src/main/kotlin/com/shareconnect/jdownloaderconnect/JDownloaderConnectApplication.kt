/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


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
        // Battery-aware sync scheduling
        scheduleBatteryAwareSync()

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

    private fun scheduleBatteryAwareSync() {
        applicationScope.launch {
            while (true) {
                delay(300000) // Check every 5 minutes
                if (isBatteryAwareSyncAllowed()) {
                    performBatteryAwareSync()
                }
            }
        }
    }

    private fun isBatteryAwareSyncAllowed(): Boolean {
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val isCharging = batteryManager.isCharging
        val isWifi = connectivityManager.activeNetwork?.let { network ->
            connectivityManager.getNetworkCapabilities(network)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } ?: false

        return isCharging && isWifi
    }

    private fun performBatteryAwareSync() {
        // Only sync when charging on WiFi to minimize battery drain
        // This prevents unnecessary battery usage during normal usage
        applicationScope.launch {
            try {
                // Access sync managers lazily to trigger initialization if needed
                val themeManager = themeSyncManager
                val profileManager = profileSyncManager
                // Add more managers as needed for background sync
            } catch (e: Exception) {
                // Silently handle sync errors during battery-aware sync
                e.printStackTrace()
            }
        }
    }
