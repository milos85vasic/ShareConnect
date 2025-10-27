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


package com.shareconnect.jdownloaderconnect.data.dao

import androidx.room.*
import com.shareconnect.jdownloaderconnect.data.model.JDownloaderAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM jdownloader_accounts")
    fun getAllAccounts(): Flow<List<JDownloaderAccount>>

    @Query("SELECT * FROM jdownloader_accounts WHERE isActive = 1")
    fun getActiveAccount(): Flow<JDownloaderAccount?>

    @Query("SELECT * FROM jdownloader_accounts WHERE id = :id")
    suspend fun getAccountById(id: String): JDownloaderAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: JDownloaderAccount)

    @Update
    suspend fun updateAccount(account: JDownloaderAccount)

    @Delete
    suspend fun deleteAccount(account: JDownloaderAccount)

    @Query("UPDATE jdownloader_accounts SET isActive = 0")
    suspend fun deactivateAllAccounts()

    @Query("UPDATE jdownloader_accounts SET isActive = 1 WHERE id = :id")
    suspend fun activateAccount(id: String)
}