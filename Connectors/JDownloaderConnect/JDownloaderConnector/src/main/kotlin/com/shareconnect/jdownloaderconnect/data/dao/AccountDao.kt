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