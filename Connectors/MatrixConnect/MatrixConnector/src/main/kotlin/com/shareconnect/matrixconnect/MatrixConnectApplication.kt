package com.shareconnect.matrixconnect

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

class MatrixConnectApplication : Application() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var themeSyncManager: ThemeSyncManager
    private lateinit var profileSyncManager: ProfileSyncManager

    override fun onCreate() {
        super.onCreate()
        Log.d("MatrixConnect", "Starting ${BuildConfig.APP_VERSION}")
        initializeSyncManagers()
    }

    private fun initializeSyncManagers() {
        val id = BuildConfig.APP_ID
        val name = BuildConfig.APP_NAME
        val version = BuildConfig.APP_VERSION
        
        themeSyncManager = ThemeSyncManager.getInstance(this, id, name, version)
        profileSyncManager = ProfileSyncManager.getInstance(this, id, name, version)
        val historySyncManager = HistorySyncManager.getInstance(this, id, name, version)
        val rssSyncManager = RssSyncManager.getInstance(this, id, name, version)
        val bookmarkSyncManager = BookmarkSyncManager.getInstance(this, id, name, version)
        val preferencesSyncManager = PreferencesSyncManager.getInstance(this, id, name, version)
        val languageSyncManager = LanguageSyncManager.getInstance(this, id, name, version)
        val torrentSharingSyncManager = TorrentSharingSyncManager.getInstance(this, id, name, version)
        
        scope.launch { themeSyncManager.startSync() }
        scope.launch { profileSyncManager.startSync() }
        scope.launch { historySyncManager.startSync() }
        scope.launch { rssSyncManager.startSync() }
        scope.launch { bookmarkSyncManager.startSync() }
        scope.launch { preferencesSyncManager.startSync() }
        scope.launch { languageSyncManager.startSync() }
        scope.launch { torrentSharingSyncManager.startSync() }
    }

    fun getThemeSyncManager() = themeSyncManager
}
