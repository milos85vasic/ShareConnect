package com.shareconnect.torrentsharingsync.database

import androidx.room.*
import com.shareconnect.torrentsharingsync.models.TorrentSharingData
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for torrent sharing preferences
 */
@Dao
interface TorrentSharingDao {

    @Query("SELECT * FROM synced_torrent_sharing_prefs WHERE id = 'torrent_sharing_prefs' LIMIT 1")
    suspend fun getTorrentSharingPrefs(): TorrentSharingData?

    @Query("SELECT * FROM synced_torrent_sharing_prefs WHERE id = 'torrent_sharing_prefs' LIMIT 1")
    fun getTorrentSharingPrefsFlow(): Flow<TorrentSharingData?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTorrentSharingPrefs(prefs: TorrentSharingData)

    @Update
    suspend fun updateTorrentSharingPrefs(prefs: TorrentSharingData)

    @Query("DELETE FROM synced_torrent_sharing_prefs WHERE id = 'torrent_sharing_prefs'")
    suspend fun deleteTorrentSharingPrefs()

    @Query("DELETE FROM synced_torrent_sharing_prefs")
    suspend fun deleteAll()
}
