package com.shareconnect.wireguardconnect

import android.app.Application
import android.util.Log
import com.shareconnect.bookmarksync.BookmarkSyncManager
import com.shareconnect.historysync.HistorySyncManager
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.preferencessync.PreferencesSyncManager
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.rsssync.RssSyncManager
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.torrentsharingsync.TorrentSharingSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Application class for WireGuardConnect.
 * Initializes all sync managers for cross-app data synchronization.
 */
class WireGuardConnectApplication : Application() {

    companion object {
        private const val TAG = "WireGuardConnectApp"
        private const val APP_ID = "wireguardconnect"
        private const val APP_NAME = "WireGuardConnect"
        private const val APP_VERSION = "1.0.0"

        lateinit var themeSyncManager: ThemeSyncManager
            private set

        lateinit var profileSyncManager: ProfileSyncManager
            private set

        lateinit var historySyncManager: HistorySyncManager
            private set

        lateinit var rssSyncManager: RssSyncManager
            private set

        lateinit var bookmarkSyncManager: BookmarkSyncManager
            private set

        lateinit var preferencesSyncManager: PreferencesSyncManager
            private set

        lateinit var languageSyncManager: LanguageSyncManager
            private set

        lateinit var torrentSharingSyncManager: TorrentSharingSyncManager
            private set
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "Initializing WireGuardConnect application")

        // Initialize all sync managers
        initializeSyncManagers()

        // Start synchronization in background
        applicationScope.launch {
            startAllSyncManagers()
        }

        Log.d(TAG, "WireGuardConnect application initialized successfully")
    }

    private fun initializeSyncManagers() {
        try {
            themeSyncManager = ThemeSyncManager.getInstance(
                context = applicationContext,
                appId = APP_ID,
                appName = APP_NAME,
                appVersion = APP_VERSION
            )
            Log.d(TAG, "ThemeSyncManager initialized")

            profileSyncManager = ProfileSyncManager.getInstance(
                context = applicationContext,
                appId = APP_ID,
                appName = APP_NAME,
                appVersion = APP_VERSION
            )
            Log.d(TAG, "ProfileSyncManager initialized")

            historySyncManager = HistorySyncManager.getInstance(
                context = applicationContext,
                appId = APP_ID,
                appName = APP_NAME,
                appVersion = APP_VERSION
            )
            Log.d(TAG, "HistorySyncManager initialized")

            rssSyncManager = RssSyncManager.getInstance(
                context = applicationContext,
                appId = APP_ID,
                appName = APP_NAME,
                appVersion = APP_VERSION
            )
            Log.d(TAG, "RssSyncManager initialized")

            bookmarkSyncManager = BookmarkSyncManager.getInstance(
                context = applicationContext,
                appId = APP_ID,
                appName = APP_NAME,
                appVersion = APP_VERSION
            )
            Log.d(TAG, "BookmarkSyncManager initialized")

            preferencesSyncManager = PreferencesSyncManager.getInstance(
                context = applicationContext,
                appId = APP_ID,
                appName = APP_NAME,
                appVersion = APP_VERSION
            )
            Log.d(TAG, "PreferencesSyncManager initialized")

            languageSyncManager = LanguageSyncManager.getInstance(
                context = applicationContext,
                appId = APP_ID,
                appName = APP_NAME,
                appVersion = APP_VERSION
            )
            Log.d(TAG, "LanguageSyncManager initialized")

            torrentSharingSyncManager = TorrentSharingSyncManager.getInstance(
                context = applicationContext,
                appId = APP_ID,
                appName = APP_NAME,
                appVersion = APP_VERSION
            )
            Log.d(TAG, "TorrentSharingSyncManager initialized")

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing sync managers", e)
        }
    }

    private suspend fun startAllSyncManagers() {
        try {
            Log.d(TAG, "Starting all sync managers")

            themeSyncManager.startSync()
            Log.d(TAG, "ThemeSyncManager started")

            profileSyncManager.startSync()
            Log.d(TAG, "ProfileSyncManager started")

            historySyncManager.startSync()
            Log.d(TAG, "HistorySyncManager started")

            rssSyncManager.startSync()
            Log.d(TAG, "RssSyncManager started")

            bookmarkSyncManager.startSync()
            Log.d(TAG, "BookmarkSyncManager started")

            preferencesSyncManager.startSync()
            Log.d(TAG, "PreferencesSyncManager started")

            languageSyncManager.startSync()
            Log.d(TAG, "LanguageSyncManager started")

            torrentSharingSyncManager.startSync()
            Log.d(TAG, "TorrentSharingSyncManager started")

            Log.d(TAG, "All sync managers started successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Error starting sync managers", e)
        }
    }
}
