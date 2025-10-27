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


package com.shareconnect.plexconnect

import android.app.Application
import android.content.Context
import com.shareconnect.plexconnect.data.database.PlexDatabase
import com.shareconnect.plexconnect.data.repository.PlexServerRepository
import com.shareconnect.plexconnect.di.DependencyContainer
import com.shareconnect.plexconnect.sync.PlexSyncManager
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.historysync.HistorySyncManager
import com.shareconnect.rsssync.RSSSyncManager
import com.shareconnect.bookmarksync.BookmarkSyncManager
import com.shareconnect.preferencessync.PreferencesSyncManager
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.languagesync.utils.LocaleHelper
import com.shareconnect.torrentsharingsync.TorrentSharingSyncManager
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlexConnectApplication : Application() {

    lateinit var plexSyncManager: PlexSyncManager
        private set

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

        // Initialize manual dependency injection
        DependencyContainer.init(this)

        // Initialize all sync managers
        initializeLanguageSync()
        initializeTorrentSharingSync()
        initializeThemeSync()
        initializeProfileSync()
        initializeHistorySync()
        initializeRSSSync()
        initializeBookmarkSync()
        initializePreferencesSync()
        initializePlexSync()

        // Observe language changes
        observeLanguageChanges()
    }

    private fun observeLanguageChanges() {
        applicationScope.launch {
            languageSyncManager.languageChangeFlow.collect { languageData ->
                // Persist language change so it applies on next app start
                LocaleHelper.persistLanguage(this@PlexConnectApplication, languageData.languageCode)
            }
        }
    }

    private fun initializeThemeSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        themeSyncManager = ThemeSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = "PlexConnect",
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
            appName = "PlexConnect",
            appVersion = packageInfo.versionName ?: "1.0.0",
            clientTypeFilter = "MEDIA_SERVER"  // PlexConnect only syncs media server profiles
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
            appName = "PlexConnect",
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
            appName = "PlexConnect",
            appVersion = packageInfo.versionName ?: "1.0.0",
            clientTypeFilter = "MEDIA_SERVER"  // PlexConnect syncs media server RSS feeds
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
            appName = "PlexConnect",
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
            appName = "PlexConnect",
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
            appName = "PlexConnect",
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
            appName = "PlexConnect",
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        // Start torrent sharing sync in background
        applicationScope.launch {
            delay(800) // Small delay to avoid port conflicts
            torrentSharingSyncManager.start()
        }
    }

    private fun initializePlexSync() {
        // Initialize Plex sync manager
        try {
            plexSyncManager = PlexSyncManager.getInstance(
                this,
                packageName,
                "PlexConnect",
                "1.0.0",
                DependencyContainer.plexServerRepository
            )

            // Start Plex sync in background
            applicationScope.launch {
                delay(900) // Small delay to avoid port conflicts
                plexSyncManager.start()
            }
        } catch (e: Exception) {
            // Handle initialization errors
            e.printStackTrace()
        }
    }
}