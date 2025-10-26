package com.shareconnect.duplicaticonnect

import android.app.Application
import android.content.Context
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.historysync.HistorySyncManager
import com.shareconnect.rsssync.RSSSyncManager
import com.shareconnect.bookmarksync.BookmarkSyncManager
import com.shareconnect.preferencessync.PreferencesSyncManager
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.languagesync.utils.LocaleHelper
import com.shareconnect.torrentsharingsync.TorrentSharingSyncManager
import kotlinx.coroutines.*

class DuplicatiConnectApplication : Application() {

    lateinit var themeSyncManager: ThemeSyncManager
        private set

    lateinit var profileSyncManager: ProfileSyncManager
        private set

    lateinit var historySyncManager: HistorySyncManager
        private set

    lateinit var rssSyncManager: RSSSyncManager
        private set

    lateinit var bookmarkSyncManager: BookmarkSyncManager
        private set

    lateinit var preferencesSyncManager: PreferencesSyncManager
        private set

    lateinit var languageSyncManager: LanguageSyncManager
        private set

    lateinit var torrentSharingSyncManager: TorrentSharingSyncManager
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun attachBaseContext(base: Context) {
        // Set gRPC system property before any gRPC classes are loaded
        System.setProperty("io.grpc.internal.DisableGlobalInterceptors", "true")
        // Disable Netty native transports to prevent epoll issues on Android
        System.setProperty("io.grpc.netty.shaded.io.netty.transport.noNative", "true")
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize all sync managers
        initializeLanguageSync()
        initializeTorrentSharingSync()
        initializeThemeSync()
        initializeProfileSync()
        initializeHistorySync()
        initializeRSSSync()
        initializeBookmarkSync()
        initializePreferencesSync()

        // Observe language changes
        observeLanguageChanges()
    }

    private fun observeLanguageChanges() {
        applicationScope.launch {
            languageSyncManager.languageChangeFlow.collect { languageData ->
                // Persist language change so it applies on next app start
                LocaleHelper.persistLanguage(this@DuplicatiConnectApplication, languageData.languageCode)
            }
        }
    }

    private fun initializeThemeSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        themeSyncManager = ThemeSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "DuplicatiConnect",
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        // Start theme sync in background
        applicationScope.launch {
            delay(100) // Small delay to avoid port conflicts
            themeSyncManager.start()
        }
    }

    private fun initializeProfileSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        profileSyncManager = ProfileSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "DuplicatiConnect",
            appVersion = packageInfo.versionName ?: "1.0.0",
            clientTypeFilter = "BACKUP"  // DuplicatiConnect only syncs backup profiles
        )

        // Start profile sync in background
        applicationScope.launch {
            delay(200) // Small delay to avoid port conflicts
            profileSyncManager.start()
        }
    }

    private fun initializeHistorySync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        historySyncManager = HistorySyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "DuplicatiConnect",
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        // Start history sync in background
        applicationScope.launch {
            delay(300) // Small delay to avoid port conflicts
            historySyncManager.start()
        }
    }

    private fun initializeRSSSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        rssSyncManager = RSSSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "DuplicatiConnect",
            appVersion = packageInfo.versionName ?: "1.0.0",
            clientTypeFilter = "BACKUP"  // DuplicatiConnect syncs backup-related RSS feeds
        )

        // Start RSS sync in background
        applicationScope.launch {
            delay(400) // Small delay to avoid port conflicts
            rssSyncManager.start()
        }
    }

    private fun initializeBookmarkSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        bookmarkSyncManager = BookmarkSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "DuplicatiConnect",
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        // Start bookmark sync in background
        applicationScope.launch {
            delay(500) // Small delay to avoid port conflicts
            bookmarkSyncManager.start()
        }
    }

    private fun initializePreferencesSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        preferencesSyncManager = PreferencesSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "DuplicatiConnect",
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        // Start preferences sync in background
        applicationScope.launch {
            delay(600) // Small delay to avoid port conflicts
            preferencesSyncManager.start()
        }
    }

    private fun initializeLanguageSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        languageSyncManager = LanguageSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "DuplicatiConnect",
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        // Start language sync in background
        applicationScope.launch {
            delay(700) // Small delay to avoid port conflicts
            languageSyncManager.start()
        }
    }

    private fun initializeTorrentSharingSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        torrentSharingSyncManager = TorrentSharingSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "DuplicatiConnect",
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        // Start torrent sharing sync in background
        applicationScope.launch {
            delay(800) // Small delay to avoid port conflicts
            torrentSharingSyncManager.start()
        }
    }
}
