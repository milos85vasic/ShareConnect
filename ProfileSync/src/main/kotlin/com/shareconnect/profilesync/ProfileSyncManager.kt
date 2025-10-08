package com.shareconnect.profilesync

import android.content.Context
import android.util.Log
import com.shareconnect.profilesync.database.ProfileDatabase
import com.shareconnect.profilesync.models.ProfileData
import com.shareconnect.profilesync.models.SyncableProfile
import com.shareconnect.profilesync.repository.ProfileRepository
import digital.vasic.asinka.AsinkaClient
import digital.vasic.asinka.AsinkaConfig
import digital.vasic.asinka.models.FieldSchema
import digital.vasic.asinka.models.FieldType
import digital.vasic.asinka.models.ObjectSchema
import digital.vasic.asinka.sync.SyncChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ProfileSyncManager private constructor(
    private val context: Context,
    private val appIdentifier: String,
    private val asinkaClient: AsinkaClient,
    private val repository: ProfileRepository,
    private val clientTypeFilter: String? = null  // null = all profiles (ShareConnect), "qbittorrent" = qBit only, "transmission" = Transmission only
) {
    private val TAG = "ProfileSyncManager"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _profileChangeFlow = MutableSharedFlow<ProfileData>(replay = 0, extraBufferCapacity = 10)
    val profileChangeFlow: SharedFlow<ProfileData> = _profileChangeFlow.asSharedFlow()

    private var isStarted = false

    /**
     * Initialize and start the sync manager
     */
    suspend fun start() {
        if (isStarted) {
            Log.w(TAG, "ProfileSyncManager already started")
            return
        }

        Log.d(TAG, "Starting ProfileSyncManager for $appIdentifier with filter: $clientTypeFilter")

        // Start Asinka client
        asinkaClient.start()

        // Load existing profiles and register them with Asinka
        syncLocalProfilesToAsinka()

        // Observe Asinka sync changes
        scope.launch {
            asinkaClient.syncManager.observeAllChanges().collect { change ->
                when (change) {
                    is SyncChange.Updated -> {
                        val syncableProfile = change.obj as? SyncableProfile
                        syncableProfile?.let {
                            handleReceivedProfile(it)
                        }
                    }
                    is SyncChange.Deleted -> {
                        handleDeletedProfile(change.objectId)
                    }
                }
            }
        }

        isStarted = true
        Log.d(TAG, "ProfileSyncManager started successfully")
    }

    /**
     * Stop the sync manager
     */
    suspend fun stop() {
        if (!isStarted) {
            return
        }

        asinkaClient.stop()
        isStarted = false
        Log.d(TAG, "ProfileSyncManager stopped")
    }

    /**
     * Get all profiles (filtered by client type if applicable)
     */
    fun getAllProfiles(): Flow<List<ProfileData>> {
        return when (clientTypeFilter) {
            ProfileData.TORRENT_CLIENT_QBITTORRENT -> repository.getQBitConnectProfiles()
            ProfileData.TORRENT_CLIENT_TRANSMISSION -> repository.getTransmissionConnectProfiles()
            else -> repository.getAllProfiles()  // ShareConnect gets all profiles
        }
    }

    /**
     * Get profiles by service type
     */
    suspend fun getProfilesByServiceType(serviceType: String): List<ProfileData> {
        val profiles = repository.getProfilesByServiceType(serviceType)
        return filterProfilesForApp(profiles)
    }

    /**
     * Get a profile by ID
     */
    suspend fun getProfileById(profileId: String): ProfileData? {
        val profile = repository.getProfileById(profileId)
        return if (profile != null && isProfileRelevantForApp(profile)) {
            profile
        } else {
            null
        }
    }

    /**
     * Observe a profile by ID
     */
    fun observeProfileById(profileId: String): Flow<ProfileData?> {
        return repository.observeProfileById(profileId)
    }

    /**
     * Get the default profile
     */
    suspend fun getDefaultProfile(): ProfileData? {
        val profile = repository.getDefaultProfile()
        return if (profile != null && isProfileRelevantForApp(profile)) {
            profile
        } else {
            null
        }
    }

    /**
     * Observe the default profile
     */
    fun observeDefaultProfile(): Flow<ProfileData?> {
        return repository.observeDefaultProfile()
    }

    /**
     * Add or update a profile
     */
    suspend fun addOrUpdateProfile(profile: ProfileData) {
        // Only sync profiles relevant to this app
        if (!isProfileRelevantForApp(profile)) {
            Log.w(TAG, "Profile ${profile.id} not relevant for this app, skipping")
            return
        }

        // Save to local database
        repository.insertProfile(profile)

        // Sync to other apps via Asinka
        val syncableProfile = SyncableProfile.fromProfileData(profile)
        asinkaClient.syncManager.registerObject(syncableProfile)

        // Emit change event
        _profileChangeFlow.emit(profile)

        Log.d(TAG, "Profile added/updated: ${profile.name} (${profile.id})")
    }

    /**
     * Delete a profile
     */
    suspend fun deleteProfile(profileId: String) {
        // Delete from local database
        repository.deleteProfile(profileId)

        // Notify other apps via Asinka
        asinkaClient.syncManager.deleteObject(profileId)

        Log.d(TAG, "Profile deleted: $profileId")
    }

    /**
     * Set a profile as default
     */
    suspend fun setDefaultProfile(profileId: String) {
        val profile = repository.getProfileById(profileId)
        if (profile == null) {
            Log.w(TAG, "Cannot set default: profile $profileId not found")
            return
        }

        if (!isProfileRelevantForApp(profile)) {
            Log.w(TAG, "Cannot set default: profile $profileId not relevant for this app")
            return
        }

        // Update in database
        repository.setDefaultProfile(profileId)

        // Get the updated profile and sync
        val updatedProfile = repository.getProfileById(profileId)
        if (updatedProfile != null) {
            val updatedWithVersion = updatedProfile.copy(
                version = updatedProfile.version + 1,
                lastModified = System.currentTimeMillis()
            )
            repository.updateProfile(updatedWithVersion)

            val syncableProfile = SyncableProfile.fromProfileData(updatedWithVersion)
            asinkaClient.syncManager.updateObject(
                updatedWithVersion.id,
                syncableProfile.toFieldMap()
            )
            _profileChangeFlow.emit(updatedWithVersion)
        }

        Log.d(TAG, "Default profile set: $profileId")
    }

    /**
     * Get the count of profiles (filtered)
     */
    suspend fun getProfileCount(): Int {
        return when (clientTypeFilter) {
            ProfileData.TORRENT_CLIENT_QBITTORRENT -> repository.getProfileCountByClientType(ProfileData.TORRENT_CLIENT_QBITTORRENT)
            ProfileData.TORRENT_CLIENT_TRANSMISSION -> repository.getProfileCountByClientType(ProfileData.TORRENT_CLIENT_TRANSMISSION)
            else -> repository.getProfileCount()
        }
    }

    /**
     * Sync all local profiles to Asinka
     */
    private suspend fun syncLocalProfilesToAsinka() {
        try {
            val profiles = repository.getAllProfilesSync()
            val relevantProfiles = filterProfilesForApp(profiles)

            Log.d(TAG, "Syncing ${relevantProfiles.size} local profiles to Asinka")

            for (profile in relevantProfiles) {
                val syncableProfile = SyncableProfile.fromProfileData(profile)
                asinkaClient.syncManager.registerObject(syncableProfile)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing local profiles to Asinka", e)
        }
    }

    /**
     * Handle a profile received from another app via Asinka
     */
    private suspend fun handleReceivedProfile(syncableProfile: SyncableProfile) {
        try {
            val profile = syncableProfile.getProfileData()

            // Filter based on app type
            if (!isProfileRelevantForApp(profile)) {
                Log.d(TAG, "Ignoring profile ${profile.id} - not relevant for this app (filter: $clientTypeFilter)")
                return
            }

            Log.d(TAG, "Received profile from ${profile.sourceApp}: ${profile.name} (${profile.id})")

            // Check if we already have this profile
            val existingProfile = repository.getProfileById(profile.id)

            if (existingProfile == null) {
                // New profile - save it
                repository.insertProfile(profile)
                _profileChangeFlow.emit(profile)
                Log.d(TAG, "Saved new profile: ${profile.name}")
            } else {
                // Existing profile - update if version is newer
                if (profile.version > existingProfile.version || profile.lastModified > existingProfile.lastModified) {
                    repository.updateProfile(profile)
                    _profileChangeFlow.emit(profile)
                    Log.d(TAG, "Updated profile: ${profile.name}")
                } else {
                    Log.d(TAG, "Ignoring older version of profile: ${profile.name}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling received profile", e)
        }
    }

    /**
     * Handle a profile deletion received from another app
     */
    private suspend fun handleDeletedProfile(profileId: String) {
        try {
            Log.d(TAG, "Received delete notification for profile: $profileId")
            repository.deleteProfile(profileId)
        } catch (e: Exception) {
            Log.e(TAG, "Error handling deleted profile", e)
        }
    }

    /**
     * Check if a profile is relevant for this app based on the client type filter
     */
    private fun isProfileRelevantForApp(profile: ProfileData): Boolean {
        return when (clientTypeFilter) {
            ProfileData.TORRENT_CLIENT_QBITTORRENT -> profile.isQBitTorrentProfile()
            ProfileData.TORRENT_CLIENT_TRANSMISSION -> profile.isTransmissionProfile()
            else -> true  // ShareConnect accepts all profiles
        }
    }

    /**
     * Filter a list of profiles based on app relevance
     */
    private fun filterProfilesForApp(profiles: List<ProfileData>): List<ProfileData> {
        return when (clientTypeFilter) {
            ProfileData.TORRENT_CLIENT_QBITTORRENT -> repository.filterForQBitConnect(profiles)
            ProfileData.TORRENT_CLIENT_TRANSMISSION -> repository.filterForTransmissionConnect(profiles)
            else -> profiles  // ShareConnect gets all profiles
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ProfileSyncManager? = null

        /**
         * Get or create the singleton instance
         */
        fun getInstance(
            context: Context,
            appId: String,
            appName: String,
            appVersion: String,
            clientTypeFilter: String? = null
        ): ProfileSyncManager {
            return INSTANCE ?: synchronized(this) {
                val profileSchema = ObjectSchema(
                    objectType = ProfileData.OBJECT_TYPE,
                    version = "1",
                    fields = listOf(
                        FieldSchema("id", FieldType.STRING),
                        FieldSchema("name", FieldType.STRING),
                        FieldSchema("host", FieldType.STRING),
                        FieldSchema("port", FieldType.INT),
                        FieldSchema("isDefault", FieldType.BOOLEAN),
                        FieldSchema("serviceType", FieldType.STRING),
                        FieldSchema("torrentClientType", FieldType.STRING),
                        FieldSchema("username", FieldType.STRING),
                        FieldSchema("password", FieldType.STRING),
                        FieldSchema("sourceApp", FieldType.STRING),
                        FieldSchema("version", FieldType.INT),
                        FieldSchema("lastModified", FieldType.LONG),
                        FieldSchema("rpcUrl", FieldType.STRING),
                        FieldSchema("useHttps", FieldType.BOOLEAN),
                        FieldSchema("trustSelfSignedCert", FieldType.BOOLEAN)
                    )
                )

                val asinkaConfig = AsinkaConfig(
                    appId = appId,
                    appName = appName,
                    appVersion = appVersion,
                    exposedSchemas = listOf(profileSchema),
                    capabilities = mapOf("profile_sync" to "1.0")
                )

                val asinkaClient = AsinkaClient.create(context, asinkaConfig)
                val database = ProfileDatabase.getInstance(context)
                val repository = ProfileRepository(database.profileDao())

                val instance = ProfileSyncManager(
                    context = context.applicationContext,
                    appIdentifier = appId,
                    asinkaClient = asinkaClient,
                    repository = repository,
                    clientTypeFilter = clientTypeFilter
                )
                INSTANCE = instance
                instance
            }
        }

        /**
         * Reset the singleton instance (for testing)
         */
        fun resetInstance() {
            INSTANCE = null
        }
    }
}
