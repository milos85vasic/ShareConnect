package com.shareconnect.duplicaticonnect

import android.app.Application
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.historysync.HistorySyncManager
import com.shareconnect.rsssync.RssSyncManager
import com.shareconnect.bookmarksync.BookmarkSyncManager
import com.shareconnect.preferencessync.PreferencesSyncManager
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.torrentsharingsync.TorrentSharingSyncManager
import kotlinx.coroutines.*

class DuplicatiConnectApplication : Application() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var themeSyncManager: ThemeSyncManager

    override fun onCreate() {
        super.onCreate()
        val id = BuildConfig.APP_ID
        
        themeSyncManager = ThemeSyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        ProfileSyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        HistorySyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        RssSyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        BookmarkSyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        PreferencesSyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        LanguageSyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        TorrentSharingSyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        
        scope.launch { themeSyncManager.startSync() }
    }

    fun getThemeSyncManager() = themeSyncManager
}
