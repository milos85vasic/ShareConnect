package com.shareconnect.matrixconnect

import android.app.Application
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
 * MatrixConnect Application class
 *
 * Initializes all ShareConnect sync managers for cross-app synchronization:
 * - ThemeSync: UI theme preferences
 * - ProfileSync: Matrix server profiles
 * - HistorySync: Message history
 * - RSSSync: RSS feed subscriptions
 * - BookmarkSync: Bookmarked conversations
 * - PreferencesSync: App preferences
 * - LanguageSync: Language settings
 * - TorrentSharingSync: Shared torrent data
 */
class MatrixConnectApplication : Application() {

    companion object {
        private const val APP_ID = "com.shareconnect.matrixconnect"
        private const val APP_NAME = "MatrixConnect"
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

        // Initialize all sync managers
        themeSyncManager = ThemeSyncManager.getInstance(
            context = applicationContext,
            appId = APP_ID,
            appName = APP_NAME,
            appVersion = APP_VERSION
        )

        profileSyncManager = ProfileSyncManager.getInstance(
            context = applicationContext,
            appId = APP_ID,
            appName = APP_NAME,
            appVersion = APP_VERSION
        )

        historySyncManager = HistorySyncManager.getInstance(
            context = applicationContext,
            appId = APP_ID,
            appName = APP_NAME,
            appVersion = APP_VERSION
        )

        rssSyncManager = RssSyncManager.getInstance(
            context = applicationContext,
            appId = APP_ID,
            appName = APP_NAME,
            appVersion = APP_VERSION
        )

        bookmarkSyncManager = BookmarkSyncManager.getInstance(
            context = applicationContext,
            appId = APP_ID,
            appName = APP_NAME,
            appVersion = APP_VERSION
        )

        preferencesSyncManager = PreferencesSyncManager.getInstance(
            context = applicationContext,
            appId = APP_ID,
            appName = APP_NAME,
            appVersion = APP_VERSION
        )

        languageSyncManager = LanguageSyncManager.getInstance(
            context = applicationContext,
            appId = APP_ID,
            appName = APP_NAME,
            appVersion = APP_VERSION
        )

        torrentSharingSyncManager = TorrentSharingSyncManager.getInstance(
            context = applicationContext,
            appId = APP_ID,
            appName = APP_NAME,
            appVersion = APP_VERSION
        )

        // Start all sync managers in background
        applicationScope.launch {
            themeSyncManager.startSync()
            profileSyncManager.startSync()
            historySyncManager.startSync()
            rssSyncManager.startSync()
            bookmarkSyncManager.startSync()
            preferencesSyncManager.startSync()
            languageSyncManager.startSync()
            torrentSharingSyncManager.startSync()
        }
    }
}
