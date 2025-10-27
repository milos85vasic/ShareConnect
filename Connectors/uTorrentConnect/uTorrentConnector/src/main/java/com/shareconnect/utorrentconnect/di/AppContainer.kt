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