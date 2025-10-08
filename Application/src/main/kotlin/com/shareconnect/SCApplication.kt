package com.shareconnect

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        initializeDatabase()
        migrateProfilesToDatabase()
        initializeThemeSync()
        initializeProfileSync()
        initializeHistorySync()
        initializeRSSSync()
        initializeBookmarkSync()
        initializePreferencesSync()
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
            preferencesSyncManager.start()
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