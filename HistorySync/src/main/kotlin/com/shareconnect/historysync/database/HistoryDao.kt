package com.shareconnect.historysync.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shareconnect.historysync.models.HistoryData
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM synced_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryData>>

    @Query("SELECT * FROM synced_history ORDER BY timestamp DESC")
    suspend fun getAllHistorySync(): List<HistoryData>

    @Query("SELECT * FROM synced_history WHERE id = :historyId")
    suspend fun getHistoryById(historyId: String): HistoryData?

    @Query("SELECT * FROM synced_history WHERE serviceType = :serviceType ORDER BY timestamp DESC")
    suspend fun getHistoryByServiceType(serviceType: String): List<HistoryData>

    @Query("SELECT * FROM synced_history WHERE serviceType = 'torrent' AND torrentClientType = :clientType ORDER BY timestamp DESC")
    fun observeTorrentHistoryByClientType(clientType: String): Flow<List<HistoryData>>

    @Query("SELECT * FROM synced_history WHERE type = :type ORDER BY timestamp DESC")
    suspend fun getHistoryByType(type: String): List<HistoryData>

    @Query("SELECT * FROM synced_history WHERE category = :category ORDER BY timestamp DESC")
    suspend fun getHistoryByCategory(category: String): List<HistoryData>

    @Query("SELECT * FROM synced_history WHERE sourceApp = :sourceApp ORDER BY timestamp DESC")
    suspend fun getHistoryBySourceApp(sourceApp: String): List<HistoryData>

    @Query("SELECT * FROM synced_history WHERE isSentSuccessfully = 1 ORDER BY timestamp DESC")
    suspend fun getSuccessfulHistory(): List<HistoryData>

    @Query("SELECT * FROM synced_history WHERE isSentSuccessfully = 0 ORDER BY timestamp DESC")
    suspend fun getFailedHistory(): List<HistoryData>

    @Query("SELECT * FROM synced_history WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    suspend fun getHistoryByTimeRange(startTime: Long, endTime: Long): List<HistoryData>

    @Query("SELECT * FROM synced_history WHERE url LIKE :searchQuery OR title LIKE :searchQuery OR description LIKE :searchQuery ORDER BY timestamp DESC")
    suspend fun searchHistory(searchQuery: String): List<HistoryData>

    @Query("SELECT DISTINCT category FROM synced_history WHERE category IS NOT NULL ORDER BY category ASC")
    suspend fun getAllCategories(): List<String>

    @Query("SELECT DISTINCT serviceProvider FROM synced_history WHERE serviceProvider IS NOT NULL ORDER BY serviceProvider ASC")
    suspend fun getAllServiceProviders(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: HistoryData): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(historyList: List<HistoryData>)

    @Update
    suspend fun update(history: HistoryData)

    @Query("DELETE FROM synced_history WHERE id = :historyId")
    suspend fun deleteHistory(historyId: String)

    @Query("DELETE FROM synced_history")
    suspend fun deleteAllHistory()

    @Query("DELETE FROM synced_history WHERE timestamp < :timestamp")
    suspend fun deleteHistoryOlderThan(timestamp: Long)

    @Query("SELECT COUNT(*) FROM synced_history")
    suspend fun getHistoryCount(): Int

    @Query("SELECT COUNT(*) FROM synced_history WHERE serviceType = 'torrent' AND torrentClientType = :clientType")
    suspend fun getHistoryCountByClientType(clientType: String): Int

    @Query("SELECT COUNT(*) FROM synced_history WHERE isSentSuccessfully = 1")
    suspend fun getSuccessfulHistoryCount(): Int

    @Query("DELETE FROM synced_history WHERE serviceProvider = :serviceProvider")
    suspend fun deleteHistoryByServiceProvider(serviceProvider: String)

    @Query("DELETE FROM synced_history WHERE type = :type")
    suspend fun deleteHistoryByType(type: String)

    @Query("DELETE FROM synced_history WHERE serviceType = :serviceType")
    suspend fun deleteHistoryByServiceType(serviceType: String)
}
