package com.shareconnect.jdownloaderconnect.data.dao

import androidx.room.*
import com.shareconnect.jdownloaderconnect.data.model.LinkGrabberLink
import com.shareconnect.jdownloaderconnect.data.model.LinkGrabberPackage
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkGrabberDao {

    // Package operations
    @Query("SELECT * FROM linkgrabber_packages ORDER BY addedDate DESC")
    fun getAllPackages(): Flow<List<LinkGrabberPackage>>

    @Query("SELECT * FROM linkgrabber_packages WHERE uuid = :uuid")
    suspend fun getPackageById(uuid: String): LinkGrabberPackage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(linkGrabberPackage: LinkGrabberPackage)

    @Update
    suspend fun updatePackage(linkGrabberPackage: LinkGrabberPackage)

    @Delete
    suspend fun deletePackage(linkGrabberPackage: LinkGrabberPackage)

    @Query("DELETE FROM linkgrabber_packages WHERE uuid = :uuid")
    suspend fun deletePackageById(uuid: String)

    // Link operations
    @Query("SELECT * FROM linkgrabber_links WHERE packageUuid = :packageUuid ORDER BY addedDate DESC")
    fun getLinksByPackage(packageUuid: String): Flow<List<LinkGrabberLink>>

    @Query("SELECT * FROM linkgrabber_links WHERE uuid = :uuid")
    suspend fun getLinkById(uuid: String): LinkGrabberLink?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: LinkGrabberLink)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinks(links: List<LinkGrabberLink>)

    @Update
    suspend fun updateLink(link: LinkGrabberLink)

    @Delete
    suspend fun deleteLink(link: LinkGrabberLink)

    @Query("DELETE FROM linkgrabber_links WHERE packageUuid = :packageUuid")
    suspend fun deleteLinksByPackage(packageUuid: String)

    // Combined operations
    @Transaction
    suspend fun insertPackageWithLinks(linkGrabberPackage: LinkGrabberPackage, links: List<LinkGrabberLink>) {
        insertPackage(linkGrabberPackage)
        insertLinks(links)
    }

    @Transaction
    suspend fun deletePackageWithLinks(packageUuid: String) {
        deletePackageById(packageUuid)
        deleteLinksByPackage(packageUuid)
    }
}