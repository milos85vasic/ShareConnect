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
