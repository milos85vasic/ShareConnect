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


package com.shareconnect.plexconnect.di

import android.content.Context
import com.shareconnect.plexconnect.data.api.PlexApiClient
import com.shareconnect.plexconnect.data.database.PlexDatabase
import com.shareconnect.plexconnect.data.repository.PlexLibraryRepository
import com.shareconnect.plexconnect.data.repository.PlexMediaRepository
import com.shareconnect.plexconnect.data.repository.PlexServerRepository
import com.shareconnect.plexconnect.service.PlexAuthService
import androidx.room.Room

object DependencyContainer {
    private lateinit var applicationContext: Context

    // Database
    val plexDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            PlexDatabase::class.java,
            PlexDatabase.DATABASE_NAME
        ).build()
    }

    // API Client
    val plexApiClient by lazy { PlexApiClient() }

    // Services
    val plexAuthService by lazy { PlexAuthService(plexApiClient) }

    // Repositories
    val plexServerRepository by lazy {
        PlexServerRepository(
            plexDatabase.plexServerDao(),
            plexApiClient
        )
    }

    val plexLibraryRepository by lazy {
        PlexLibraryRepository(
            plexDatabase.plexLibraryDao(),
            plexApiClient
        )
    }

    val plexMediaRepository: PlexMediaRepository by lazy {
        PlexMediaRepository(
            applicationContext,
            plexDatabase.plexMediaItemDao(),
            plexApiClient
        )
    }

    fun init(context: Context) {
        if (!::applicationContext.isInitialized) {
            applicationContext = context.applicationContext
        }
    }
}