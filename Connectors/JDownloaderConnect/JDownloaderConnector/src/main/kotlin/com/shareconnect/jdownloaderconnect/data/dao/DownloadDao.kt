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