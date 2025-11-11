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


package com.shareconnect.wireguardconnect

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

class WireGuardConnectApplication : Application() {
    private lateinit var themeSyncManager: ThemeSyncManager

    override fun onCreate() {
        super.onCreate()
        val id = BuildConfig.APP_ID
        themeSyncManager = ThemeSyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        ProfileSyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        HistorySyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        RSSSyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        BookmarkSyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        PreferencesSyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        LanguageSyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        TorrentSharingSyncManager.getInstance(this, id, BuildConfig.APP_NAME, BuildConfig.APP_VERSION)
        CoroutineScope(SupervisorJob()).launch { themeSyncManager.start() }
    }

    fun getThemeSyncManager() = themeSyncManager
}
