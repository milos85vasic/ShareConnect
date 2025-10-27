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
