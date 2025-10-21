package com.shareconnect.jdownloaderconnect.data.dao

import androidx.room.*
import com.shareconnect.jdownloaderconnect.data.model.DownloadLink
import com.shareconnect.jdownloaderconnect.data.model.DownloadPackage
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {

    // Package operations
    @Query("SELECT * FROM download_packages ORDER BY addedDate DESC")
    fun getAllPackages(): Flow<List<DownloadPackage>>

    @Query("SELECT * FROM download_packages WHERE uuid = :uuid")
    suspend fun getPackageById(uuid: String): DownloadPackage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(downloadPackage: DownloadPackage)

    @Update
    suspend fun updatePackage(downloadPackage: DownloadPackage)

    @Delete
    suspend fun deletePackage(downloadPackage: DownloadPackage)

    @Query("DELETE FROM download_packages WHERE uuid = :uuid")
    suspend fun deletePackageById(uuid: String)

    // Link operations
    @Query("SELECT * FROM download_links WHERE packageUuid = :packageUuid ORDER BY addedDate DESC")
    fun getLinksByPackage(packageUuid: String): Flow<List<DownloadLink>>

    @Query("SELECT * FROM download_links WHERE uuid = :uuid")
    suspend fun getLinkById(uuid: String): DownloadLink?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: DownloadLink)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinks(links: List<DownloadLink>)

    @Update
    suspend fun updateLink(link: DownloadLink)

    @Delete
    suspend fun deleteLink(link: DownloadLink)

    @Query("DELETE FROM download_links WHERE packageUuid = :packageUuid")
    suspend fun deleteLinksByPackage(packageUuid: String)

    // Combined operations
    @Transaction
    suspend fun insertPackageWithLinks(downloadPackage: DownloadPackage, links: List<DownloadLink>) {
        insertPackage(downloadPackage)
        insertLinks(links)
    }

    @Transaction
    suspend fun deletePackageWithLinks(packageUuid: String) {
        deletePackageById(packageUuid)
        deleteLinksByPackage(packageUuid)
    }
}