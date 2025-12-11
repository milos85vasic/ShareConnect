package com.shareconnect.plexconnect.di

import android.content.Context
import androidx.room.Room
import com.shareconnect.plexconnect.data.api.PlexApiClient
import com.shareconnect.plexconnect.data.api.PlexApiService
import com.shareconnect.plexconnect.data.database.PlexDatabase
import com.shareconnect.plexconnect.data.database.Migration1To2
import com.shareconnect.plexconnect.data.repository.PlexRepository
import com.shareconnect.plexconnect.data.repository.PlexRepositoryImpl
import com.shareconnect.plexconnect.ui.PlexViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Dependency Injection module for Plex Connector
 */
val plexModule = module {
    // OkHttpClient
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl("https://plex.tv/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Plex API Service
    single {
        get<Retrofit>().create(PlexApiService::class.java)
    }

    // Plex API Client
    single {
        PlexApiClient()
    }

    // Room Database
    single {
        Room.databaseBuilder(
            androidContext(),
            PlexDatabase::class.java,
            PlexDatabase.DATABASE_NAME
        )
        .addMigrations(Migration1To2)
        .fallbackToDestructiveMigration() // Be careful with this in production
        .build()
    }

    // DAOs
    single { get<PlexDatabase>().plexServerDao() }
    single { get<PlexDatabase>().plexLibraryDao() }
    single { get<PlexDatabase>().plexMediaItemDao() }
    single { get<PlexDatabase>().semanticEmbeddingDao() }

    // Repository
    // single<PlexRepository> {
    //     PlexRepositoryImpl(
    //         apiClient = get(),
    //         serverDao = get(),
    //         libraryDao = get(),
    //         mediaItemDao = get()
    //     )
    // }

    // ViewModel
    // viewModel {
    //     PlexViewModel(
    //         plexRepository = get()
    //     )
    // }
}