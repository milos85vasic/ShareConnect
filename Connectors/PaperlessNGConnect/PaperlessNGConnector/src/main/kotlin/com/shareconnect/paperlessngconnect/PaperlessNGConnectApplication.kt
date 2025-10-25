package com.shareconnect.paperlessngconnect

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

class PaperlessNGConnectApplication : Application() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var themeSyncManager: ThemeSyncManager

    override fun onCreate() {
        super.onCreate()
        val id = BuildConfig.APP_ID
        val name = BuildConfig.APP_NAME
        val version = BuildConfig.APP_VERSION
        
        themeSyncManager = ThemeSyncManager.getInstance(this, id, name, version)
        ProfileSyncManager.getInstance(this, id, name, version)
        HistorySyncManager.getInstance(this, id, name, version)
        RssSyncManager.getInstance(this, id, name, version)
        BookmarkSyncManager.getInstance(this, id, name, version)
        PreferencesSyncManager.getInstance(this, id, name, version)
        LanguageSyncManager.getInstance(this, id, name, version)
        TorrentSharingSyncManager.getInstance(this, id, name, version)
        
        scope.launch { themeSyncManager.startSync() }
    }

    fun getThemeSyncManager() = themeSyncManager
}
