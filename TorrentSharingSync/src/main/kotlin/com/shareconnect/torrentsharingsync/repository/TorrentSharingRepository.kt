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


package com.shareconnect.torrentsharingsync.repository

import com.shareconnect.torrentsharingsync.database.TorrentSharingDao
import com.shareconnect.torrentsharingsync.models.TorrentSharingData
import kotlinx.coroutines.flow.Flow

/**
 * Repository for torrent sharing preferences data access
 */
class TorrentSharingRepository(private val torrentSharingDao: TorrentSharingDao) {

    /**
     * Get the current torrent sharing preferences
     */
    suspend fun getTorrentSharingPrefs(): TorrentSharingData? {
        return torrentSharingDao.getTorrentSharingPrefs()
    }

    /**
     * Get torrent sharing preferences as Flow for reactive updates
     */
    fun getTorrentSharingPrefsFlow(): Flow<TorrentSharingData?> {
        return torrentSharingDao.getTorrentSharingPrefsFlow()
    }

    /**
     * Set the torrent sharing preferences
     */
    suspend fun setTorrentSharingPrefs(prefs: TorrentSharingData) {
        torrentSharingDao.insertTorrentSharingPrefs(prefs)
    }

    /**
     * Update the torrent sharing preferences
     */
    suspend fun updateTorrentSharingPrefs(prefs: TorrentSharingData) {
        torrentSharingDao.updateTorrentSharingPrefs(prefs)
    }

    /**
     * Delete the torrent sharing preferences
     */
    suspend fun deleteTorrentSharingPrefs() {
        torrentSharingDao.deleteTorrentSharingPrefs()
    }

    /**
     * Get or create default torrent sharing preferences
     */
    suspend fun getOrCreateDefault(): TorrentSharingData {
        return getTorrentSharingPrefs() ?: TorrentSharingData.createDefault().also {
            setTorrentSharingPrefs(it)
        }
    }
}
