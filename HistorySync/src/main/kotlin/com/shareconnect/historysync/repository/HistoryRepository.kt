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


package com.shareconnect.historysync.repository

import com.shareconnect.historysync.database.HistoryDao
import com.shareconnect.historysync.models.HistoryData
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {

    fun getAllHistory(): Flow<List<HistoryData>> {
        return historyDao.getAllHistory()
    }

    suspend fun getAllHistorySync(): List<HistoryData> {
        return historyDao.getAllHistorySync()
    }

    suspend fun getHistoryById(historyId: String): HistoryData? {
        return historyDao.getHistoryById(historyId)
    }

    suspend fun getHistoryByUrl(url: String): HistoryData? {
        return historyDao.getHistoryByUrl(url)
    }

    suspend fun getHistoryByServiceType(serviceType: String): List<HistoryData> {
        return historyDao.getHistoryByServiceType(serviceType)
    }

    fun getTorrentHistoryByClientType(clientType: String): Flow<List<HistoryData>> {
        return historyDao.observeTorrentHistoryByClientType(clientType)
    }

    suspend fun getHistoryByType(type: String): List<HistoryData> {
        return historyDao.getHistoryByType(type)
    }

    suspend fun getHistoryByCategory(category: String): List<HistoryData> {
        return historyDao.getHistoryByCategory(category)
    }

    suspend fun getHistoryBySourceApp(sourceApp: String): List<HistoryData> {
        return historyDao.getHistoryBySourceApp(sourceApp)
    }

    suspend fun getSuccessfulHistory(): List<HistoryData> {
        return historyDao.getSuccessfulHistory()
    }

    suspend fun getFailedHistory(): List<HistoryData> {
        return historyDao.getFailedHistory()
    }

    suspend fun getHistoryByTimeRange(startTime: Long, endTime: Long): List<HistoryData> {
        return historyDao.getHistoryByTimeRange(startTime, endTime)
    }

    suspend fun searchHistory(query: String): List<HistoryData> {
        val searchQuery = "%$query%"
        return historyDao.searchHistory(searchQuery)
    }

    suspend fun getAllCategories(): List<String> {
        return historyDao.getAllCategories()
    }

    suspend fun getAllServiceProviders(): List<String> {
        return historyDao.getAllServiceProviders()
    }

    suspend fun insertHistory(history: HistoryData): Long {
        return historyDao.insert(history)
    }

    suspend fun insertAllHistory(historyList: List<HistoryData>) {
        historyDao.insertAll(historyList)
    }

    suspend fun updateHistory(history: HistoryData) {
        historyDao.update(history)
    }

    suspend fun deleteHistory(historyId: String) {
        historyDao.deleteHistory(historyId)
    }

    suspend fun deleteAllHistory() {
        historyDao.deleteAllHistory()
    }

    suspend fun deleteHistoryOlderThan(timestamp: Long) {
        historyDao.deleteHistoryOlderThan(timestamp)
    }

    suspend fun getHistoryCount(): Int {
        return historyDao.getHistoryCount()
    }

    suspend fun getHistoryCountByClientType(clientType: String): Int {
        return historyDao.getHistoryCountByClientType(clientType)
    }

    suspend fun getSuccessfulHistoryCount(): Int {
        return historyDao.getSuccessfulHistoryCount()
    }

    suspend fun deleteHistoryByServiceProvider(serviceProvider: String) {
        historyDao.deleteHistoryByServiceProvider(serviceProvider)
    }

    suspend fun deleteHistoryByType(type: String) {
        historyDao.deleteHistoryByType(type)
    }

    suspend fun deleteHistoryByServiceType(serviceType: String) {
        historyDao.deleteHistoryByServiceType(serviceType)
    }

    /**
     * Filter history for qBitConnect (only qBittorrent torrents and general media)
     */
    fun filterForQBitConnect(historyList: List<HistoryData>): List<HistoryData> {
        return historyList.filter {
            it.isMediaHistory() || it.isQBitTorrentHistory()
        }
    }

    /**
     * Filter history for TransmissionConnect (only Transmission torrents and general media)
     */
    fun filterForTransmissionConnect(historyList: List<HistoryData>): List<HistoryData> {
        return historyList.filter {
            it.isMediaHistory() || it.isTransmissionHistory()
        }
    }
}
