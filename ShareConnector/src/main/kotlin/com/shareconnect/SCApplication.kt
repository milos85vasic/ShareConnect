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
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.redelf.commons.application.BaseApplication
import com.shareconnect.database.HistoryDatabase
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.historysync.HistorySyncManager
import com.shareconnect.rsssync.RSSSyncManager
import com.shareconnect.bookmarksync.BookmarkSyncManager
import com.shareconnect.preferencessync.PreferencesSyncManager
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.languagesync.utils.LocaleHelper
import com.shareconnect.torrentsharingsync.TorrentSharingSyncManager
import com.shareconnect.utils.TorrentAppHelper
import com.shareconnect.onboarding.viewmodel.OnboardingViewModel
import com.shareconnect.themesync.utils.ThemeApplier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SCApplication : BaseApplication() {

    lateinit var database: HistoryDatabase
        private set

    lateinit var themeSyncManager: ThemeSyncManager
        private set

    lateinit var profileSyncManager: ProfileSyncManager
        private set

    lateinit var historySyncManager: HistorySyncManager
        private set

    lateinit var rssSyncManager: RSSSyncManager
        private set

    lateinit var bookmarkSyncManager: BookmarkSyncManager
        private set

    lateinit var preferencesSyncManager: PreferencesSyncManager
        private set

    lateinit var languageSyncManager: LanguageSyncManager
        private set

    lateinit var torrentSharingSyncManager: TorrentSharingSyncManager
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun attachBaseContext(base: Context) {
        // Set gRPC system property before any gRPC classes are loaded
        System.setProperty("io.grpc.internal.DisableGlobalInterceptors", "true")
        // Disable Netty native transports to prevent epoll issues on Android
        System.setProperty("io.grpc.netty.shaded.io.netty.transport.noNative", "true")
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    override fun onCreate() {
        super.onCreate()
        initializeDatabase()
        migrateProfilesToDatabase()
        initializeLanguageSync()
        initializeTorrentSharingSync()
        initializeThemeSync()
        initializeProfileSync()
        initializeHistorySync()
        initializeRSSSync()
        initializeBookmarkSync()
        initializePreferencesSync()
        observeLanguageChanges()

        // Apply saved preferences if onboarding is completed
        applySavedPreferencesIfNeeded()

        // Check if onboarding is needed
        checkAndLaunchOnboardingIfNeeded()
    }

    private fun observeLanguageChanges() {
        applicationScope.launch {
            languageSyncManager.languageChangeFlow.collect { languageData ->
                // Persist language change so it applies on next app start
                LocaleHelper.persistLanguage(this@SCApplication, languageData.languageCode)
            }
        }
    }

    private fun applySavedPreferencesIfNeeded() {
        // Check if onboarding has been completed
        val prefs = getSharedPreferences("onboarding_prefs", MODE_PRIVATE)
        val onboardingCompleted = prefs.getBoolean("onboarding_completed", false)

        if (onboardingCompleted) {
            // Apply saved theme and language preferences
            applicationScope.launch {
                try {
                    // Apply saved theme
                    val defaultTheme = themeSyncManager.getDefaultTheme()
                    defaultTheme?.let { theme ->
                        ThemeApplier.applyDarkMode(theme)
                    }

                    // Apply saved language - get current language from sync manager
                    val currentLanguage = languageSyncManager.getOrCreateDefault()
                    // Note: Language application requires an Activity context, so we skip it here
                    // It will be applied when the first Activity starts
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun initializeThemeSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        themeSyncManager = ThemeSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0"
        )

        // Start theme sync in background
        applicationScope.launch {
            delay(100) // Small delay to avoid port conflicts
            themeSyncManager.start()
        }
    }

    private fun initializeProfileSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        profileSyncManager = ProfileSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0",
            clientTypeFilter = null  // ShareConnect syncs all profiles
        )

        // Start profile sync in background
        applicationScope.launch {
            delay(200) // Small delay to avoid port conflicts
            profileSyncManager.start()
        }
    }

    private fun initializeHistorySync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        historySyncManager = HistorySyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0"
        )

        // Start history sync in background
        applicationScope.launch {
            delay(300) // Small delay to avoid port conflicts
            historySyncManager.start()
        }
    }

    private fun initializeRSSSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        rssSyncManager = RSSSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0",
            clientTypeFilter = null  // ShareConnect syncs all RSS feeds
        )

        // Start RSS sync in background
        applicationScope.launch {
            delay(400) // Small delay to avoid port conflicts
            rssSyncManager.start()
        }
    }

    private fun initializeBookmarkSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        bookmarkSyncManager = BookmarkSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0"
        )

        // Start bookmark sync in background
        applicationScope.launch {
            delay(500) // Small delay to avoid port conflicts
            bookmarkSyncManager.start()
        }
    }

    private fun initializePreferencesSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        preferencesSyncManager = PreferencesSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0"
        )

        // Start preferences sync in background
        applicationScope.launch {
            delay(600) // Small delay to avoid port conflicts
            preferencesSyncManager.start()
        }
    }

    private fun initializeLanguageSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        languageSyncManager = LanguageSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0"
        )

        // Start language sync in background
        applicationScope.launch {
            delay(700) // Small delay to avoid port conflicts
            languageSyncManager.start()
        }
    }

    private fun initializeTorrentSharingSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        torrentSharingSyncManager = TorrentSharingSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0"
        )

        // Initialize TorrentAppHelper with sync manager
        TorrentAppHelper.initialize(
            context = this,
            appId = packageName,
            appName = getString(R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0"
        )

        // Start torrent sharing sync in background
        applicationScope.launch {
            delay(800) // Small delay to avoid port conflicts
            torrentSharingSyncManager.start()
        }
    }

    private fun initializeDatabase() {
        // Initialize database without encryption for now
        // SQLCipher will be configured separately through the toolkit
        database = Room.databaseBuilder(
            applicationContext,
            HistoryDatabase::class.java,
            "history_database"
        )
            .addMigrations(MIGRATION_2_3)
            .allowMainThreadQueries() // For simplicity
            .fallbackToDestructiveMigration() // For development
            .build()
    }

    private fun migrateProfilesToDatabase() {
        // Migrate existing profiles from SharedPreferences to Room database
        val sharedPrefs = getSharedPreferences("MeTubeSharePrefs", MODE_PRIVATE)
        val profilesJson = sharedPrefs.getString("profiles", null)

        if (profilesJson != null && !sharedPrefs.getBoolean("profiles_migrated", false)) {
            try {
                val profileManager = ProfileManager(this)
                // ProfileManager will handle the migration internally
                sharedPrefs.edit().putBoolean("profiles_migrated", true).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun firebaseEnabled() = isProduction()
    override fun firebaseAnalyticsEnabled() = isProduction()

    override fun isProduction(): Boolean {

        return resources.getBoolean(R.bool.is_production)
    }

    override fun takeSalt(): String {

        return getString(R.string.app_name)
    }

    private fun checkAndLaunchOnboardingIfNeeded() {
        // Check if onboarding has been completed
        val prefs = getSharedPreferences("onboarding_prefs", MODE_PRIVATE)
        val onboardingCompleted = prefs.getBoolean("onboarding_completed", false)

        if (!onboardingCompleted) {
            // Check if user has any existing data that would indicate they've used the app before
            applicationScope.launch {
            val hasExistingProfiles = runCatching<Boolean> {
                val profileManager = ProfileManager(this@SCApplication)
                profileManager.hasProfiles()
            }.getOrDefault(false)

            // If no existing profiles, launch onboarding
            if (!hasExistingProfiles) {
                launchOnboarding()
            }
            }
        }
    }

    private fun launchOnboarding() {
        // Launch onboarding activity
        val intent = android.content.Intent(this, ShareConnectOnboardingActivity::class.java)
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create server_profiles table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS server_profiles (
                        id TEXT PRIMARY KEY NOT NULL,
                        name TEXT,
                        url TEXT,
                        port INTEGER NOT NULL DEFAULT 0,
                        isDefault INTEGER NOT NULL DEFAULT 0,
                        serviceType TEXT,
                        torrentClientType TEXT,
                        username TEXT,
                        password TEXT
                    )
                """.trimIndent())
            }
        }
    }
}