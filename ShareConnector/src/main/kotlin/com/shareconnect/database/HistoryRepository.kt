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


package com.shareconnect.database

import android.content.Context
import androidx.room.Room
import com.shareconnect.R

class HistoryRepository(context: Context, testDao: HistoryItemDao? = null) {
    private val appContext = context.applicationContext
    private lateinit var database: HistoryDatabase
    private lateinit var historyItemDao: HistoryItemDao

    init {
        if (testDao != null) {
            // Use test DAO
            historyItemDao = testDao
        } else {
            // Initialize the database with SQLCipher encryption
            database = Room.databaseBuilder(
                appContext,
                HistoryDatabase::class.java, appContext.getString(R.string.db_history_database)
            )
                .allowMainThreadQueries() // For simplicity, allow main thread queries
                .fallbackToDestructiveMigration()
                .build()
            historyItemDao = database.historyItemDao()
        }
    }

    // Insert a new history item
    fun insertHistoryItem(item: HistoryItem) {
        historyItemDao.insert(item)
    }

    // Get all history items
    val allHistoryItems: List<HistoryItem>
        get() = historyItemDao.getAllHistoryItems()

    // Get history items by service provider
    fun getHistoryItemsByServiceProvider(serviceProvider: String): List<HistoryItem> {
        return historyItemDao.getHistoryItemsByServiceProvider(serviceProvider)
    }

    // Get history items by type
    fun getHistoryItemsByType(type: String): List<HistoryItem> {
        return historyItemDao.getHistoryItemsByType(type)
    }

    // Get history items by service type
    fun getHistoryItemsByServiceType(serviceType: String): List<HistoryItem> {
        return historyItemDao.getHistoryItemsByServiceType(serviceType)
    }

    // Get all service providers
    val allServiceProviders: List<String>
        get() = historyItemDao.getAllServiceProviders()

    // Get all types
    val allTypes: List<String>
        get() = historyItemDao.getAllTypes()

    // Get all service types
    val allServiceTypes: List<String>
        get() = historyItemDao.getAllServiceTypes()

    // Delete a specific history item
    fun deleteHistoryItem(item: HistoryItem) {
        historyItemDao.delete(item)
    }

    // Delete all history items
    fun deleteAllHistoryItems() {
        historyItemDao.deleteAll()
    }

    // Delete history items by service provider
    fun deleteHistoryItemsByServiceProvider(serviceProvider: String) {
        historyItemDao.deleteByServiceProvider(serviceProvider)
    }

    // Delete history items by type
    fun deleteHistoryItemsByType(type: String) {
        historyItemDao.deleteByType(type)
    }

    // Delete history items by service type
    fun deleteHistoryItemsByServiceType(serviceType: String) {
        historyItemDao.deleteByServiceType(serviceType)
    }
}