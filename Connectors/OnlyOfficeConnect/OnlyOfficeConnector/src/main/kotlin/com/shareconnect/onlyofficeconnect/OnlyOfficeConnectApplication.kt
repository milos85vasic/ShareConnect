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


package com.shareconnect.onlyofficeconnect

import android.app.Application
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.historysync.HistorySyncManager
import com.shareconnect.rsssync.RSSSyncManager
import com.shareconnect.bookmarksync.BookmarkSyncManager
import com.shareconnect.preferencessync.PreferencesSyncManager
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.torrentsharingsync.TorrentSharingSyncManager
import kotlinx.coroutines.*

class OnlyOfficeConnectApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        val id = BuildConfig.APP_ID
        val name = BuildConfig.APP_NAME
        val version = BuildConfig.APP_VERSION

        // Initialize all sync managers
        val themeSyncManager = ThemeSyncManager.getInstance(this, id, name, version)
        val profileSyncManager = ProfileSyncManager.getInstance(this, id, name, version)
        val historySyncManager = HistorySyncManager.getInstance(this, id, name, version)
        val rssSyncManager = RSSSyncManager.getInstance(this, id, name, version)
        val bookmarkSyncManager = BookmarkSyncManager.getInstance(this, id, name, version)
        val preferencesSyncManager = PreferencesSyncManager.getInstance(this, id, name, version)
        val languageSyncManager = LanguageSyncManager.getInstance(this, id, name, version)
        val torrentSharingSyncManager = TorrentSharingSyncManager.getInstance(this, id, name, version)

        // Start sync managers with delays to avoid port conflicts
        applicationScope.launch { delay(100); themeSyncManager.start() }
        applicationScope.launch { delay(200); profileSyncManager.start() }
        applicationScope.launch { delay(300); historySyncManager.start() }
        applicationScope.launch { delay(400); rssSyncManager.start() }
        applicationScope.launch { delay(500); bookmarkSyncManager.start() }
        applicationScope.launch { delay(600); preferencesSyncManager.start() }
        applicationScope.launch { delay(700); languageSyncManager.start() }
        applicationScope.launch { delay(800); torrentSharingSyncManager.start() }
    }
}
