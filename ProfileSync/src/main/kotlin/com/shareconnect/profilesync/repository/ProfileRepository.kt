package com.shareconnect.profilesync.repository

import com.shareconnect.profilesync.database.ProfileDao
import com.shareconnect.profilesync.models.ProfileData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepository(private val profileDao: ProfileDao) {

    /**
     * Get all profiles as a Flow
     */
    fun getAllProfiles(): Flow<List<ProfileData>> {
        return profileDao.getAllProfiles()
    }

    /**
     * Get all profiles synchronously (for sync operations)
     */
    suspend fun getAllProfilesSync(): List<ProfileData> {
        return profileDao.getAllProfilesSync()
    }

    /**
     * Get profiles filtered by torrent client type
     * This is the core filtering mechanism for qBitConnect and TransmissionConnect
     */
    fun getProfilesByClientType(clientType: String): Flow<List<ProfileData>> {
        return profileDao.observeTorrentProfilesByClientType(clientType)
    }

    /**
     * Get profiles filtered by torrent client type (synchronous)
     */
    suspend fun getProfilesByClientTypeSync(clientType: String): List<ProfileData> {
        return profileDao.getProfilesByTorrentClientType(clientType)
    }

    /**
     * Get profiles filtered by service type
     */
    suspend fun getProfilesByServiceType(serviceType: String): List<ProfileData> {
        return profileDao.getProfilesByServiceType(serviceType)
    }

    /**
     * Get profiles filtered by source app
     */
    suspend fun getProfilesBySourceApp(sourceApp: String): List<ProfileData> {
        return profileDao.getProfilesBySourceApp(sourceApp)
    }

    /**
     * Get a specific profile by ID
     */
    suspend fun getProfileById(profileId: String): ProfileData? {
        return profileDao.getProfileById(profileId)
    }

    /**
     * Observe a specific profile by ID
     */
    fun observeProfileById(profileId: String): Flow<ProfileData?> {
        return profileDao.observeProfileById(profileId)
    }

    /**
     * Get the current default profile
     */
    suspend fun getDefaultProfile(): ProfileData? {
        return profileDao.getDefaultProfile()
    }

    /**
     * Observe the default profile
     */
    fun observeDefaultProfile(): Flow<ProfileData?> {
        return profileDao.observeDefaultProfile()
    }

    /**
     * Insert or update a profile
     */
    suspend fun insertProfile(profile: ProfileData): Long {
        return profileDao.insert(profile)
    }

    /**
     * Insert or update multiple profiles
     */
    suspend fun insertProfiles(profiles: List<ProfileData>) {
        profileDao.insertAll(profiles)
    }

    /**
     * Update an existing profile
     */
    suspend fun updateProfile(profile: ProfileData) {
        profileDao.update(profile)
    }

    /**
     * Set a profile as the default
     * This clears all other default flags first
     */
    suspend fun setDefaultProfile(profileId: String) {
        profileDao.clearDefaultProfiles()
        profileDao.setDefaultProfile(profileId)
    }

    /**
     * Delete a specific profile
     */
    suspend fun deleteProfile(profileId: String) {
        profileDao.deleteProfile(profileId)
    }

    /**
     * Delete all profiles
     */
    suspend fun deleteAllProfiles() {
        profileDao.deleteAllProfiles()
    }

    /**
     * Get the total count of profiles
     */
    suspend fun getProfileCount(): Int {
        return profileDao.getProfileCount()
    }

    /**
     * Get the count of profiles for a specific client type
     */
    suspend fun getProfileCountByClientType(clientType: String): Int {
        return profileDao.getProfileCountByClientType(clientType)
    }

    /**
     * Check if a profile is relevant for qBitConnect
     */
    fun isRelevantForQBitConnect(profile: ProfileData): Boolean {
        return profile.isQBitTorrentProfile()
    }

    /**
     * Check if a profile is relevant for TransmissionConnect
     */
    fun isRelevantForTransmissionConnect(profile: ProfileData): Boolean {
        return profile.isTransmissionProfile()
    }

    /**
     * Filter profiles for qBitConnect (only qBittorrent profiles)
     */
    fun filterForQBitConnect(profiles: List<ProfileData>): List<ProfileData> {
        return profiles.filter { it.isQBitTorrentProfile() }
    }

    /**
     * Filter profiles for TransmissionConnect (only Transmission profiles)
     */
    fun filterForTransmissionConnect(profiles: List<ProfileData>): List<ProfileData> {
        return profiles.filter { it.isTransmissionProfile() }
    }

    /**
     * Get profiles relevant for qBitConnect as a Flow
     */
    fun getQBitConnectProfiles(): Flow<List<ProfileData>> {
        return getAllProfiles().map { profiles ->
            filterForQBitConnect(profiles)
        }
    }

    /**
     * Get profiles relevant for TransmissionConnect as a Flow
     */
    fun getTransmissionConnectProfiles(): Flow<List<ProfileData>> {
        return getAllProfiles().map { profiles ->
            filterForTransmissionConnect(profiles)
        }
    }
}
