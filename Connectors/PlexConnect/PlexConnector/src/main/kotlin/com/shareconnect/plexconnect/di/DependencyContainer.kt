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