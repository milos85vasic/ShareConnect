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
