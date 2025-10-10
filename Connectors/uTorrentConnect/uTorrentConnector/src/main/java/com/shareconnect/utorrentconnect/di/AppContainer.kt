package com.shareconnect.utorrentconnect.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.shareconnect.utorrentconnect.analytics.Analytics
import com.shareconnect.utorrentconnect.analytics.AnalyticsProvider
import com.shareconnect.utorrentconnect.analytics.FirebaseAnalyticsProvider
import com.shareconnect.utorrentconnect.FeatureManager
import com.shareconnect.utorrentconnect.logging.Logger
import com.shareconnect.utorrentconnect.preferences.PreferencesRepository

class AppContainer(private val context: Context) {

    // Firebase dependencies
    val crashlytics: FirebaseCrashlytics by lazy {
        FirebaseCrashlytics.getInstance()
    }

    val remoteConfig by lazy {
        FirebaseRemoteConfig.getInstance()
    }

    val firebaseAnalytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(context)
    }

    // DataStore
    val dataStore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("user_preferences")
        }
    }

    // Analytics
    val firebaseAnalyticsProvider: FirebaseAnalyticsProvider by lazy {
        FirebaseAnalyticsProvider(firebaseAnalytics)
    }

    val analyticsProvider: AnalyticsProvider by lazy {
        firebaseAnalyticsProvider
    }

    val analytics: Analytics by lazy {
        Analytics(analyticsProvider)
    }

    // Logger
    val logger: Logger by lazy {
        Logger(crashlytics)
    }

    // Feature Manager
    val featureManager: FeatureManager by lazy {
        FeatureManager(remoteConfig, crashlytics)
    }

    // Preferences Repository
    val preferencesRepository: PreferencesRepository by lazy {
        PreferencesRepository(dataStore)
    }
}