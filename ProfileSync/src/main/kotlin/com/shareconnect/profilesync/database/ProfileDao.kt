package com.shareconnect.profilesync.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shareconnect.profilesync.models.ProfileData
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM synced_profiles ORDER BY name ASC")
    fun getAllProfiles(): Flow<List<ProfileData>>

    @Query("SELECT * FROM synced_profiles ORDER BY name ASC")
    suspend fun getAllProfilesSync(): List<ProfileData>

    @Query("SELECT * FROM synced_profiles WHERE id = :profileId")
    suspend fun getProfileById(profileId: String): ProfileData?

    @Query("SELECT * FROM synced_profiles WHERE id = :profileId")
    fun observeProfileById(profileId: String): Flow<ProfileData?>

    @Query("SELECT * FROM synced_profiles WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultProfile(): ProfileData?

    @Query("SELECT * FROM synced_profiles WHERE isDefault = 1 LIMIT 1")
    fun observeDefaultProfile(): Flow<ProfileData?>

    @Query("SELECT * FROM synced_profiles WHERE serviceType = :serviceType")
    suspend fun getProfilesByServiceType(serviceType: String): List<ProfileData>

    @Query("SELECT * FROM synced_profiles WHERE torrentClientType = :clientType")
    suspend fun getProfilesByTorrentClientType(clientType: String): List<ProfileData>

    @Query("SELECT * FROM synced_profiles WHERE sourceApp = :sourceApp")
    suspend fun getProfilesBySourceApp(sourceApp: String): List<ProfileData>

    @Query("SELECT * FROM synced_profiles WHERE serviceType = 'torrent' AND torrentClientType = :clientType")
    fun observeTorrentProfilesByClientType(clientType: String): Flow<List<ProfileData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProfileData): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<ProfileData>)

    @Update
    suspend fun update(profile: ProfileData)

    @Query("UPDATE synced_profiles SET isDefault = 0")
    suspend fun clearDefaultProfiles()

    @Query("UPDATE synced_profiles SET isDefault = 1 WHERE id = :profileId")
    suspend fun setDefaultProfile(profileId: String)

    @Query("DELETE FROM synced_profiles WHERE id = :profileId")
    suspend fun deleteProfile(profileId: String)

    @Query("DELETE FROM synced_profiles")
    suspend fun deleteAllProfiles()

    @Query("SELECT COUNT(*) FROM synced_profiles")
    suspend fun getProfileCount(): Int

    @Query("SELECT COUNT(*) FROM synced_profiles WHERE serviceType = 'torrent' AND torrentClientType = :clientType")
    suspend fun getProfileCountByClientType(clientType: String): Int
}
