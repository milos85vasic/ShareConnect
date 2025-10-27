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


package com.shareconnect

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shareconnect.database.ServerProfileRepository
import java.util.UUID

class ProfileManager private constructor(
    private val context: Context,
    private val isTestMode: Boolean = false
) {
    private lateinit var repository: ServerProfileRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gson: Gson

    // Primary constructor for normal usage
    constructor(context: Context) : this(context, false)

    // Secondary constructor for testing with mocked dependencies
    constructor(context: Context, repository: ServerProfileRepository, sharedPreferences: SharedPreferences) : this(context, true) {
        this.repository = repository
        this.sharedPreferences = sharedPreferences
        this.gson = Gson()
        // Migration is skipped in test mode
    }

    init {
        if (!isTestMode) {
            repository = ServerProfileRepository(context)
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            gson = Gson()

            // Migrate from SharedPreferences to Room if needed
            migrateIfNeeded()
        }
    }

    private fun migrateIfNeeded() {
        if (!sharedPreferences.getBoolean("migrated_to_room", false)) {
            val profilesJson = sharedPreferences.getString(KEY_PROFILES, null)
            if (profilesJson != null) {
                val listType = object : TypeToken<List<ServerProfile>>() {}.type
                val oldProfiles: List<ServerProfile> = gson.fromJson(profilesJson, listType)

                // Migrate each profile to Room database
                oldProfiles.forEach { profile ->
                    if (profile.id.isNullOrEmpty()) {
                        profile.id = UUID.randomUUID().toString()
                    }
                    repository.addProfile(profile)
                }

                // Mark as migrated
                sharedPreferences.edit()
                    .putBoolean("migrated_to_room", true)
                    .remove(KEY_PROFILES) // Clean up old data
                    .apply()
            } else {
                // No old profiles, just mark as migrated
                sharedPreferences.edit()
                    .putBoolean("migrated_to_room", true)
                    .apply()
            }
        }
    }

    val profiles: List<ServerProfile>
        get() = repository.getAllProfiles()

    fun saveProfiles(profiles: List<ServerProfile>) {
        // This method is kept for backward compatibility but now uses Room
        // Clear all existing profiles and add new ones
        // Note: In a production app, this would be more sophisticated
        profiles.forEach { profile ->
            if (repository.getProfileById(profile.id ?: "") != null) {
                repository.updateProfile(profile)
            } else {
                repository.addProfile(profile)
            }
        }
    }

    fun defaultProfile(): ServerProfile? {
        val defaultProfile = repository.getDefaultProfile()
        if (defaultProfile != null) {
            return defaultProfile
        }

        // Fallback: check SharedPreferences for backward compatibility
        val defaultProfileId = sharedPreferences.getString(KEY_DEFAULT_PROFILE, null)
        if (defaultProfileId != null) {
            val profile = repository.getProfileById(defaultProfileId)
            if (profile != null) {
                // Migrate the default setting to Room
                repository.setDefaultProfile(defaultProfileId)
                sharedPreferences.edit().remove(KEY_DEFAULT_PROFILE).apply()
                return profile
            }
        }

        // If no default profile is set, return the first profile if available
        val profiles = profiles
        return if (profiles.isNotEmpty()) {
            profiles[0]
        } else null
    }

    fun setDefaultProfile(profile: ServerProfile) {
        profile.id?.let { repository.setDefaultProfile(it) }
    }

    fun clearDefaultProfile() {
        repository.clearDefaultProfile()
    }

    fun addProfile(profile: ServerProfile) {
        if (profile.id.isNullOrEmpty()) {
            profile.id = UUID.randomUUID().toString()
        }

        // Set default service type if not set
        if (profile.serviceType.isNullOrEmpty()) {
            profile.serviceType = ServerProfile.TYPE_METUBE
        }

        repository.addProfile(profile)
    }

    fun updateProfile(profile: ServerProfile) {
        repository.updateProfile(profile)
    }

    fun deleteProfile(profile: ServerProfile) {
        repository.deleteProfile(profile)
    }

    fun hasProfiles(): Boolean {
        return repository.hasProfiles()
    }

    /**
     * Get profiles filtered by service type
     */
    fun getProfilesByServiceType(serviceType: String): List<ServerProfile> {
        return repository.getProfilesByServiceType(serviceType)
    }

    /**
     * Get all unique service types from existing profiles
     */
    fun allServiceTypes(): List<String> {
        val allProfiles = profiles
        val serviceTypes: MutableList<String> = ArrayList()

        for (profile in allProfiles) {
            val serviceType = profile.serviceType
            if (serviceType != null && !serviceTypes.contains(serviceType)) {
                serviceTypes.add(serviceType)
            }
        }

        return serviceTypes
    }

    /**
     * Get all torrent client types from existing profiles
     */
    fun allTorrentClientTypes(): List<String> {
        val allProfiles = profiles
        val clientTypes: MutableList<String> = ArrayList()

        for (profile in allProfiles) {
            if (ServerProfile.TYPE_TORRENT == profile.serviceType) {
                val clientType = profile.torrentClientType
                if (clientType != null && !clientTypes.contains(clientType)) {
                    clientTypes.add(clientType)
                }
            }
        }

        return clientTypes
    }

    companion object {
        private const val PREFS_NAME = "MeTubeSharePrefs"
        private const val KEY_PROFILES = "profiles"
        private const val KEY_DEFAULT_PROFILE = "default_profile"
    }
}