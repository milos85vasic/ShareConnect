package com.shareconnect.syncthingconnect

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

class SyncthingConnectApplication : Application() {
    companion object {
        private const val TAG = "SyncthingConnectApp"
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private lateinit var themeSyncManager: ThemeSyncManager
    private lateinit var profileSyncManager: ProfileSyncManager
    private lateinit var historySyncManager: HistorySyncManager
    private lateinit var rssSyncManager: RssSyncManager
    private lateinit var bookmarkSyncManager: BookmarkSyncManager
    private lateinit var preferencesSyncManager: PreferencesSyncManager
    private lateinit var languageSyncManager: LanguageSyncManager
    private lateinit var torrentSharingSyncManager: TorrentSharingSyncManager

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "SyncthingConnect ${BuildConfig.APP_VERSION} starting...")
        initializeSyncManagers()
    }

    private fun initializeSyncManagers() {
        val appId = BuildConfig.APP_ID
        val appName = BuildConfig.APP_NAME
        val appVersion = BuildConfig.APP_VERSION

        try {
            themeSyncManager = ThemeSyncManager.getInstance(applicationContext, appId, appName, appVersion)
            applicationScope.launch { themeSyncManager.startSync() }

            profileSyncManager = ProfileSyncManager.getInstance(applicationContext, appId, appName, appVersion)
            applicationScope.launch { profileSyncManager.startSync() }

            historySyncManager = HistorySyncManager.getInstance(applicationContext, appId, appName, appVersion)
            applicationScope.launch { historySyncManager.startSync() }

            rssSyncManager = RssSyncManager.getInstance(applicationContext, appId, appName, appVersion)
            applicationScope.launch { rssSyncManager.startSync() }

            bookmarkSyncManager = BookmarkSyncManager.getInstance(applicationContext, appId, appName, appVersion)
            applicationScope.launch { bookmarkSyncManager.startSync() }

            preferencesSyncManager = PreferencesSyncManager.getInstance(applicationContext, appId, appName, appVersion)
            applicationScope.launch { preferencesSyncManager.startSync() }

            languageSyncManager = LanguageSyncManager.getInstance(applicationContext, appId, appName, appVersion)
            applicationScope.launch { languageSyncManager.startSync() }

            torrentSharingSyncManager = TorrentSharingSyncManager.getInstance(applicationContext, appId, appName, appVersion)
            applicationScope.launch { torrentSharingSyncManager.startSync() }

            Log.d(TAG, "All sync managers initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize sync managers", e)
        }
    }

    fun getThemeSyncManager(): ThemeSyncManager = themeSyncManager
    fun getProfileSyncManager(): ProfileSyncManager = profileSyncManager
    fun getHistorySyncManager(): HistorySyncManager = historySyncManager
}
