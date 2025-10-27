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


package com.shareconnect.seafileconnect

import android.app.Application
import android.util.Log
import com.shareconnect.bookmarksync.BookmarkSyncManager
import com.shareconnect.historysync.HistorySyncManager
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.preferencessync.PreferencesSyncManager
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.rsssync.RssSyncManager
import com.shareconnect.seafileconnect.BuildConfig
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.torrentsharingsync.TorrentSharingSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * SeafileConnect Application
 * 
 * Initializes all Asinka sync managers for real-time data synchronization
 * across all ShareConnect applications.
 */
class SeafileConnectApplication : Application() {

    companion object {
        private const val TAG = "SeafileConnectApp"
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Asinka Sync Managers
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
        
        Log.d(TAG, "SeafileConnect ${BuildConfig.APP_VERSION} starting...")
        
        initializeSyncManagers()
        
        Log.d(TAG, "SeafileConnect initialization complete")
    }

    /**
     * Initialize all Asinka sync managers
     */
    private fun initializeSyncManagers() {
        val appId = BuildConfig.APP_ID
        val appName = BuildConfig.APP_NAME
        val appVersion = BuildConfig.APP_VERSION

        try {
            // Initialize Theme Sync (port 8890)
            themeSyncManager = ThemeSyncManager.getInstance(
                context = applicationContext,
                appId = appId,
                appName = appName,
                appVersion = appVersion
            )
            applicationScope.launch {
                themeSyncManager.startSync()
                Log.d(TAG, "ThemeSyncManager started")
            }

            // Initialize Profile Sync (port 8900)
            profileSyncManager = ProfileSyncManager.getInstance(
                context = applicationContext,
                appId = appId,
                appName = appName,
                appVersion = appVersion
            )
            applicationScope.launch {
                profileSyncManager.startSync()
                Log.d(TAG, "ProfileSyncManager started")
            }

            // Initialize History Sync (port 8910)
            historySyncManager = HistorySyncManager.getInstance(
                context = applicationContext,
                appId = appId,
                appName = appName,
                appVersion = appVersion
            )
            applicationScope.launch {
                historySyncManager.startSync()
                Log.d(TAG, "HistorySyncManager started")
            }

            // Initialize RSS Sync (port 8920)
            rssSyncManager = RssSyncManager.getInstance(
                context = applicationContext,
                appId = appId,
                appName = appName,
                appVersion = appVersion
            )
            applicationScope.launch {
                rssSyncManager.startSync()
                Log.d(TAG, "RssSyncManager started")
            }

            // Initialize Bookmark Sync (port 8930)
            bookmarkSyncManager = BookmarkSyncManager.getInstance(
                context = applicationContext,
                appId = appId,
                appName = appName,
                appVersion = appVersion
            )
            applicationScope.launch {
                bookmarkSyncManager.startSync()
                Log.d(TAG, "BookmarkSyncManager started")
            }

            // Initialize Preferences Sync (port 8940)
            preferencesSyncManager = PreferencesSyncManager.getInstance(
                context = applicationContext,
                appId = appId,
                appName = appName,
                appVersion = appVersion
            )
            applicationScope.launch {
                preferencesSyncManager.startSync()
                Log.d(TAG, "PreferencesSyncManager started")
            }

            // Initialize Language Sync (port 8950)
            languageSyncManager = LanguageSyncManager.getInstance(
                context = applicationContext,
                appId = appId,
                appName = appName,
                appVersion = appVersion
            )
            applicationScope.launch {
                languageSyncManager.startSync()
                Log.d(TAG, "LanguageSyncManager started")
            }

            // Initialize Torrent Sharing Sync (port 8960)
            torrentSharingSyncManager = TorrentSharingSyncManager.getInstance(
                context = applicationContext,
                appId = appId,
                appName = appName,
                appVersion = appVersion
            )
            applicationScope.launch {
                torrentSharingSyncManager.startSync()
                Log.d(TAG, "TorrentSharingSyncManager started")
            }

            Log.d(TAG, "All sync managers initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize sync managers", e)
        }
    }

    /**
     * Get ThemeSyncManager instance
     */
    fun getThemeSyncManager(): ThemeSyncManager = themeSyncManager

    /**
     * Get ProfileSyncManager instance
     */
    fun getProfileSyncManager(): ProfileSyncManager = profileSyncManager

    /**
     * Get HistorySyncManager instance
     */
    fun getHistorySyncManager(): HistorySyncManager = historySyncManager

    /**
     * Get RssSyncManager instance
     */
    fun getRssSyncManager(): RssSyncManager = rssSyncManager

    /**
     * Get BookmarkSyncManager instance
     */
    fun getBookmarkSyncManager(): BookmarkSyncManager = bookmarkSyncManager

    /**
     * Get PreferencesSyncManager instance
     */
    fun getPreferencesSyncManager(): PreferencesSyncManager = preferencesSyncManager

    /**
     * Get LanguageSyncManager instance
     */
    fun getLanguageSyncManager(): LanguageSyncManager = languageSyncManager

    /**
     * Get TorrentSharingSyncManager instance
     */
    fun getTorrentSharingSyncManager(): TorrentSharingSyncManager = torrentSharingSyncManager
}
