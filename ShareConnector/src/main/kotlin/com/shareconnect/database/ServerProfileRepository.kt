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
import com.shareconnect.ServerProfile
import com.shareconnect.SCApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.UUID

class ServerProfileRepository(context: Context) {
    private val database: HistoryDatabase = (context.applicationContext as SCApplication).database
    private val serverProfileDao: ServerProfileDao = database.serverProfileDao()

    fun getAllProfiles(): List<ServerProfile> = runBlocking {
        withContext(Dispatchers.IO) {
            serverProfileDao.getAllProfiles().map { entityToServerProfile(it) }
        }
    }

    fun getProfileById(id: String): ServerProfile? = runBlocking {
        withContext(Dispatchers.IO) {
            serverProfileDao.getProfileById(id)?.let { entityToServerProfile(it) }
        }
    }

    fun getDefaultProfile(): ServerProfile? = runBlocking {
        withContext(Dispatchers.IO) {
            serverProfileDao.getDefaultProfile()?.let { entityToServerProfile(it) }
        }
    }

    fun getProfilesByServiceType(serviceType: String): List<ServerProfile> = runBlocking {
        withContext(Dispatchers.IO) {
            serverProfileDao.getProfilesByServiceType(serviceType).map { entityToServerProfile(it) }
        }
    }

    fun addProfile(profile: ServerProfile) = runBlocking {
        withContext(Dispatchers.IO) {
            if (profile.id.isNullOrEmpty()) {
                profile.id = UUID.randomUUID().toString()
            }
            serverProfileDao.insert(serverProfileToEntity(profile))
        }
    }

    fun updateProfile(profile: ServerProfile) = runBlocking {
        withContext(Dispatchers.IO) {
            serverProfileDao.update(serverProfileToEntity(profile))
        }
    }

    fun deleteProfile(profile: ServerProfile) = runBlocking {
        withContext(Dispatchers.IO) {
            serverProfileDao.delete(serverProfileToEntity(profile))
        }
    }

    fun setDefaultProfile(profileId: String) = runBlocking {
        withContext(Dispatchers.IO) {
            serverProfileDao.clearAllDefaults()
            serverProfileDao.setDefaultProfile(profileId)
        }
    }

    fun clearDefaultProfile() = runBlocking {
        withContext(Dispatchers.IO) {
            serverProfileDao.clearAllDefaults()
        }
    }

    fun hasProfiles(): Boolean = runBlocking {
        withContext(Dispatchers.IO) {
            serverProfileDao.getAllProfiles().isNotEmpty()
        }
    }

    private fun entityToServerProfile(entity: ServerProfileEntity): ServerProfile {
        return ServerProfile(
            entity.id,
            entity.name,
            entity.url,
            entity.port,
            entity.isDefault,
            entity.serviceType,
            entity.torrentClientType,
            entity.username,
            entity.password
        )
    }

    private fun serverProfileToEntity(profile: ServerProfile): ServerProfileEntity {
        return ServerProfileEntity(
            profile.id ?: UUID.randomUUID().toString(),
            profile.name,
            profile.url,
            profile.port,
            profile.isDefault,
            profile.serviceType,
            profile.torrentClientType,
            profile.username,
            profile.password
        )
    }
}