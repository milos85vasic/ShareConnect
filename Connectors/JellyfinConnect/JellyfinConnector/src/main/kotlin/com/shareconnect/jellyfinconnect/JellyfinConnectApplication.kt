package com.shareconnect.jellyfinconnect

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class JellyfinConnectApplication : Application() {

    // Lazy initialization for better startup performance
    val themeSyncManager: ThemeSyncManager by lazy {
        initializeThemeSync()
    }

    val profileSyncManager: ProfileSyncManager by lazy {
        initializeProfileSync()
    }

    val historySyncManager: HistorySyncManager by lazy {
        initializeHistorySync()
    }

    val rssSyncManager: RSSSyncManager by lazy {
        initializeRSSSync()
    }

    val bookmarkSyncManager: BookmarkSyncManager by lazy {
        initializeBookmarkSync()
    }

    val preferencesSyncManager: PreferencesSyncManager by lazy {
        initializePreferencesSync()
    }

    val languageSyncManager: LanguageSyncManager by lazy {
        initializeLanguageSync()
    }

    val torrentSharingSyncManager: TorrentSharingSyncManager by lazy {
        initializeTorrentSharingSync()
    }

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

        // Observe language changes (lazy initialization will happen when accessed)
        observeLanguageChanges()
    }

    private fun observeLanguageChanges() {
        applicationScope.launch {
            languageSyncManager.languageChangeFlow.collect { languageData ->
                // Persist language change so it applies on next app start
                LocaleHelper.persistLanguage(this@JellyfinConnectApplication, languageData.languageCode)
            }
        }
    }

    private fun initializeThemeSync(): ThemeSyncManager {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val manager = ThemeSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "JellyfinConnect",
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        // Start theme sync in background
        applicationScope.launch {
            delay(100) // Small delay to avoid port conflicts
            manager.start()
        }

        return manager
    }

    private fun initializeProfileSync(): ProfileSyncManager {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val manager = ProfileSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "JellyfinConnect",
            appVersion = packageInfo.versionName ?: "1.0.0",
            clientTypeFilter = "MEDIA_SERVER"  // JellyfinConnect only syncs media server profiles
        )

        // Start profile sync in background
        applicationScope.launch {
            delay(200) // Small delay to avoid port conflicts
            manager.start()
        }

        return manager
    }

    private fun initializeHistorySync(): HistorySyncManager {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val manager = HistorySyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "JellyfinConnect",
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        // Start history sync in background
        applicationScope.launch {
            delay(300) // Small delay to avoid port conflicts
            manager.start()
        }

        return manager
    }

    private fun initializeRSSSync(): RSSSyncManager {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val manager = RSSSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "JellyfinConnect",
            appVersion = packageInfo.versionName ?: "1.0.0",
            clientTypeFilter = "MEDIA_SERVER"  // JellyfinConnect syncs media server RSS feeds
        )

        // Start RSS sync in background
        applicationScope.launch {
            delay(400) // Small delay to avoid port conflicts
            manager.start()
        }

        return manager
    }

    private fun initializeBookmarkSync(): BookmarkSyncManager {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val manager = BookmarkSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "JellyfinConnect",
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        // Start bookmark sync in background
        applicationScope.launch {
            delay(500) // Small delay to avoid port conflicts
            manager.start()
        }

        return manager
    }

    private fun initializePreferencesSync(): PreferencesSyncManager {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val manager = PreferencesSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "JellyfinConnect",
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        // Start preferences sync in background
        applicationScope.launch {
            delay(600) // Small delay to avoid port conflicts
            manager.start()
        }

        return manager
    }

    private fun initializeLanguageSync(): LanguageSyncManager {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val manager = LanguageSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "JellyfinConnect",
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        // Start language sync in background
        applicationScope.launch {
            delay(700) // Small delay to avoid port conflicts
            manager.start()
        }

        return manager
    }

    private fun initializeTorrentSharingSync(): TorrentSharingSyncManager {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val manager = TorrentSharingSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "JellyfinConnect",
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        // Start torrent sharing sync in background
        applicationScope.launch {
            delay(800) // Small delay to avoid port conflicts
            manager.start()
        }

        return manager
    }
}
