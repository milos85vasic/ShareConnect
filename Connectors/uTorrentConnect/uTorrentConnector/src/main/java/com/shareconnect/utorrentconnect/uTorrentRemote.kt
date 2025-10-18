package com.shareconnect.utorrentconnect

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.UiModeManager
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Build
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.evernote.android.job.JobManager
import com.shareconnect.utorrentconnect.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.shareconnect.utorrentconnect.analytics.Analytics
import com.shareconnect.utorrentconnect.di.AppContainer
import com.shareconnect.utorrentconnect.FeatureManager
import com.shareconnect.utorrentconnect.logging.Logger
import com.shareconnect.utorrentconnect.filtering.Filter
import com.shareconnect.utorrentconnect.filtering.Filters
import com.shareconnect.utorrentconnect.model.json.Torrent
import com.shareconnect.utorrentconnect.notifications.BackgroundUpdateJob
import com.shareconnect.utorrentconnect.notifications.BackgroundUpdater
import com.shareconnect.utorrentconnect.preferences.PreferencesRepository
import com.shareconnect.utorrentconnect.server.Server
import com.shareconnect.utorrentconnect.sorting.SortOrder
import com.shareconnect.utorrentconnect.sorting.SortedBy
import com.shareconnect.utorrentconnect.theme.NightMode
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.profilesync.models.ProfileData
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.historysync.HistorySyncManager
import com.shareconnect.rsssync.RSSSyncManager
import com.shareconnect.rsssync.models.RSSFeedData
import com.shareconnect.bookmarksync.BookmarkSyncManager
import com.shareconnect.preferencessync.PreferencesSyncManager
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.languagesync.utils.LocaleHelper
import com.shareconnect.torrentsharingsync.TorrentSharingSyncManager
import java.util.LinkedList
import java.util.Objects
import java.util.WeakHashMap
import java.util.stream.Collectors

class uTorrentRemote : Application(), OnSharedPreferenceChangeListener {
    private val servers: MutableList<Server?> = LinkedList()
    private var activeServer: Server? = null
    private val activeServerListeners: MutableList<OnActiveServerChangedListener> = LinkedList()
    private val serverListListeners: MutableList<OnServerListChangedListener> = LinkedList()
    var isSpeedLimitEnabled = false
        set(isEnabled) {
            field = isEnabled
            speedLimitsCache[activeServer] = isEnabled
            for (l in speedLimitChangedListeners) {
                l.speedLimitEnabledChanged(isSpeedLimitEnabled)
            }
        }
    private val speedLimitChangedListeners: MutableList<OnSpeedLimitChangedListener> = LinkedList()
    var torrents: Collection<Torrent> = emptyList()
        set(torrents) {
            field = torrents
            for (listener in torrentsUpdatedListeners) {
                listener.torrentsUpdated(torrents.toMutableList())
            }
        }
    private val torrentsUpdatedListeners: MutableList<OnTorrentsUpdatedListener> = LinkedList()
    private var activeFilter = Filters.ALL
    private val filterSelectedListeners: MutableList<OnFilterSelectedListener> = LinkedList()
    var sortedBy = SortedBy.NAME
        private set
    var sortOrder = SortOrder.ASCENDING
        private set
    private val sortingChangedListeners: MutableList<OnSortingChangedListener> = LinkedList()
    @JvmField
    var defaultDownloadDir: String? = null
    private val speedLimitsCache: MutableMap<Server?, Boolean> = WeakHashMap()
    private lateinit var sharedPreferences: SharedPreferences
    @JvmField
    val appStartTimestampMillis = System.currentTimeMillis()
    @JvmField
    var appStartupTimeReported = false

    private lateinit var appContainer: AppContainer
    lateinit var analytics: Analytics
    lateinit var featureManager: FeatureManager
    lateinit var preferencesRepository: PreferencesRepository
    lateinit var logger: Logger
    lateinit var themeSyncManager: ThemeSyncManager
    lateinit var profileSyncManager: ProfileSyncManager
    lateinit var historySyncManager: HistorySyncManager
    lateinit var rssSyncManager: RSSSyncManager
    lateinit var bookmarkSyncManager: BookmarkSyncManager
    lateinit var preferencesSyncManager: PreferencesSyncManager
    lateinit var languageSyncManager: LanguageSyncManager
    lateinit var torrentSharingSyncManager: TorrentSharingSyncManager

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    override fun onCreate() {
        super.onCreate()
        // Disable Netty native transports to prevent epoll issues on Android
        System.setProperty("io.grpc.netty.shaded.io.netty.transport.noNative", "true")
        appContainer = AppContainer(this)
        analytics = appContainer.analytics
        featureManager = appContainer.featureManager
        preferencesRepository = appContainer.preferencesRepository
        logger = appContainer.logger
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            preferencesRepository.getNightMode().collect { nightMode ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                    val mode = when (nightMode) {
                        NightMode.OFF -> UiModeManager.MODE_NIGHT_NO
                        NightMode.ON -> UiModeManager.MODE_NIGHT_YES
                        NightMode.AUTO -> UiModeManager.MODE_NIGHT_AUTO
                    }
                    uiModeManager.setApplicationNightMode(mode)
                } else {
                    val mode = when (nightMode) {
                        NightMode.OFF -> AppCompatDelegate.MODE_NIGHT_NO
                        NightMode.ON -> AppCompatDelegate.MODE_NIGHT_YES
                        NightMode.AUTO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }
                    AppCompatDelegate.setDefaultNightMode(mode)
                }
            }
        }
        instance = this
        restore()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        JobManager.create(this)
            .addJobCreator(BackgroundUpdateJob.Creator())
        if (isNotificationEnabled) {
            BackgroundUpdater.start(this)
        }
        createNotificationChannel()
        initializeLanguageSync()
        initializeTorrentSharingSync()
        initializeThemeSync()
        initializeProfileSync()
        initializeHistorySync()
        initializeRSSSync()
        initializeBookmarkSync()
        initializePreferencesSync()
        observeLanguageChanges()

        // Check if onboarding is needed
        checkAndLaunchOnboardingIfNeeded()
    }

    private fun observeLanguageChanges() {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            languageSyncManager.languageChangeFlow.collect { languageData ->
                // Persist language change so it applies on next app start
                LocaleHelper.persistLanguage(this@uTorrentRemote, languageData.languageCode)
            }
        }
    }

    private fun initializeThemeSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        themeSyncManager = ThemeSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        ProcessLifecycleOwner.get().lifecycleScope.launch {
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
            appVersion = packageInfo.versionName ?: "1.0.0",
            clientTypeFilter = ProfileData.TORRENT_CLIENT_UTORRENT  // Only sync uTorrent profiles
        )

        ProcessLifecycleOwner.get().lifecycleScope.launch {
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
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        ProcessLifecycleOwner.get().lifecycleScope.launch {
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
            appVersion = packageInfo.versionName ?: "1.0.0",
            clientTypeFilter = RSSFeedData.TORRENT_CLIENT_UTORRENT  // Only sync uTorrent RSS feeds
        )

        ProcessLifecycleOwner.get().lifecycleScope.launch {
            rssSyncManager.start()
        }
    }

    private fun initializeBookmarkSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        bookmarkSyncManager = BookmarkSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        ProcessLifecycleOwner.get().lifecycleScope.launch {
            bookmarkSyncManager.start()
        }
    }

    private fun initializePreferencesSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        preferencesSyncManager = PreferencesSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        ProcessLifecycleOwner.get().lifecycleScope.launch {
            preferencesSyncManager.start()
        }
    }

    private fun initializeLanguageSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        languageSyncManager = LanguageSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        ProcessLifecycleOwner.get().lifecycleScope.launch {
            languageSyncManager.start()
        }
    }

    private fun initializeTorrentSharingSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        torrentSharingSyncManager = TorrentSharingSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        ProcessLifecycleOwner.get().lifecycleScope.launch {
            delay(700) // Small delay to avoid port conflicts
            torrentSharingSyncManager.start()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key == getString(R.string.torrent_finished_notification_enabled_key)) {
            if (isNotificationEnabled) {
                BackgroundUpdater.start(this)
            } else {
                BackgroundUpdater.stop(this)
                for (server in servers) {
                    server!!.lastUpdateDate = 0
                }
                persistServers()
            }
        } else if (key == getString(R.string.background_update_only_unmetered_wifi_key)) {
            BackgroundUpdater.restart(this)
        }
    }

    fun getServers(): List<Server?> {
        return servers
    }

    fun getServerById(id: String): Server? {
        for (server in servers) {
            if (id == server!!.id) return server
        }
        return null
    }

    fun addServer(server: Server?) {
        servers.add(server)
        persistServers()
        for (l in serverListListeners) {
            l.serverAdded(server)
        }
        if (isNotificationEnabled) {
            BackgroundUpdater.start(this)
        }
    }

    fun removeServer(server: Server) {
        servers.remove(server)
        if (server == getActiveServer()) {
            setActiveServer(if (servers.isNotEmpty()) servers[0] else null)
        }
        persistServers()
        for (l in serverListListeners) {
            l.serverRemoved(server)
        }
    }

    fun updateServer(server: Server?) {
        persistServers()
        for (l in serverListListeners) {
            l.serverUpdated(server)
        }
    }

    fun getActiveServer(): Server? {
        return activeServer
    }

    fun setActiveServer(server: Server?) {
        activeServer = server
        persistActiveServer()
        fireActiveServerChangedEvent()
        isSpeedLimitEnabled = speedLimitsCache.getOrDefault(server, false)
    }

    fun addOnActiveServerChangedListener(listener: OnActiveServerChangedListener) {
        if (!activeServerListeners.contains(listener)) {
            activeServerListeners.add(listener)
        }
    }

    fun removeOnActiveServerChangedListener(listener: OnActiveServerChangedListener) {
        activeServerListeners.remove(listener)
    }

    private fun fireActiveServerChangedEvent() {
        for (listener in activeServerListeners) {
            listener.serverChanged(activeServer)
        }
    }

    val updateInterval: Int
        get() = sharedPreferences.getString(
            getString(R.string.update_interval_key),
            getString(R.string.update_interval_default_value)
        )!!.toInt()
    var isNotificationEnabled: Boolean
        get() = sharedPreferences.getBoolean(
            getString(R.string.torrent_finished_notification_enabled_key),
            java.lang.Boolean.parseBoolean(getString(R.string.torrent_finished_notification_enabled_default_value))
        )
        set(isEnabled) {
            sharedPreferences.edit()
                .putBoolean(getString(R.string.torrent_finished_notification_enabled_key), isEnabled)
                .apply()
        }
    val isFreeSpaceCheckDisabled: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.disable_free_space_check_key), false)

    fun addOnSpeedLimitEnabledChangedListener(listener: OnSpeedLimitChangedListener) {
        if (!speedLimitChangedListeners.contains(listener)) {
            speedLimitChangedListeners.add(listener)
        }
    }

    fun removeOnSpeedLimitEnabledChangedListener(listener: OnSpeedLimitChangedListener) {
        speedLimitChangedListeners.remove(listener)
    }

    fun addTorrentsUpdatedListener(listener: OnTorrentsUpdatedListener) {
        if (!torrentsUpdatedListeners.contains(listener)) {
            torrentsUpdatedListeners.add(listener)
        }
    }

    fun removeTorrentsUpdatedListener(listener: OnTorrentsUpdatedListener) {
        torrentsUpdatedListeners.remove(listener)
    }

    fun setActiveFilter(activeFilter: Filter) {
        this.activeFilter = activeFilter
        for (listener in filterSelectedListeners) {
            listener.filterSelected(activeFilter)
        }
    }

    fun getActiveFilter(): Filter {
        return activeFilter
    }

    fun addOnFilterSetListener(listener: OnFilterSelectedListener) {
        if (!filterSelectedListeners.contains(listener)) {
            filterSelectedListeners.add(listener)
        }
    }

    fun removeOnFilterSelectedListener(listener: OnFilterSelectedListener) {
        filterSelectedListeners.remove(listener)
    }

    fun setSorting(sortedBy: SortedBy, sortOrder: SortOrder) {
        this.sortedBy = sortedBy
        this.sortOrder = sortOrder
        val comparator = sortComparator
        for (listener in sortingChangedListeners) {
            listener.onSortingChanged(comparator)
        }
    }

    val sortComparator: Comparator<Torrent>
        get() = sortOrder.comparator(sortedBy.comparator)

    fun addOnSortingChangedListeners(listener: OnSortingChangedListener) {
        if (!sortingChangedListeners.contains(listener)) {
            sortingChangedListeners.add(listener)
        }
    }

    fun removeOnSortingChangedListener(listener: OnSortingChangedListener) {
        sortingChangedListeners.remove(listener)
    }

    fun persist() {
        persistServers()
        persistFilter()
        persistSorting()
    }

    private fun restore() {
        restoreServers()
        restoreFilter()
        restoreSorting()
    }

    fun persistServers() {
        val serversInJson = servers.stream().map { obj: Server? -> obj!!.toJson() }
            .collect(Collectors.toSet())
        val sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        val editor = sp.edit()
        editor.putStringSet(KEY_SERVERS, serversInJson)
        editor.apply()
    }

    private fun restoreServers() {
        val sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        val serversInJson = sp.getStringSet(KEY_SERVERS, emptySet())
        servers.addAll(
            serversInJson!!.stream().map { jsonObj: String? -> Server.fromJson(jsonObj) }
                .filter { obj: Server? -> Objects.nonNull(obj) }.collect(Collectors.toList())
        )
        val activeServerInJson = sp.getString(KEY_ACTIVE_SERVER, null)
        if (activeServerInJson != null) {
            val persistedActiveServer = Server.fromJson(activeServerInJson)
            // active server should point to object in all servers list
            activeServer = servers.stream()
                .filter { server: Server? -> server == persistedActiveServer }
                .findFirst()
                .orElse(null)
            fireActiveServerChangedEvent()
        } else {
            activeServer = null
        }
        if (activeServer == null && servers.isNotEmpty()) {
            activeServer = servers[0]
        }
    }

    private fun persistActiveServer() {
        val serverInJson = if (activeServer != null) activeServer!!.toJson() else null
        val sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(KEY_ACTIVE_SERVER, serverInJson)
        editor.apply()
    }

    private fun persistFilter() {
        val sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt(KEY_FILTER, listOf(*allFilters).indexOf(activeFilter))
        editor.apply()
    }

    private fun restoreFilter() {
        val sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        val filterIdx = sp.getInt(KEY_FILTER, 0)
        activeFilter = allFilters[filterIdx]
    }

    private fun persistSorting() {
        val sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt(KEY_SORTED_BY, sortedBy.ordinal)
        editor.putInt(KEY_SORT_ORDER, sortOrder.ordinal)
        editor.apply()
    }

    private fun restoreSorting() {
        val sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        sortedBy = SortedBy.values()[sp.getInt(KEY_SORTED_BY, 0)]
        sortOrder = SortOrder.values()[sp.getInt(
            KEY_SORT_ORDER,
            0
        )]
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun interface OnActiveServerChangedListener {
        fun serverChanged(newServer: Server?)
    }

    interface OnServerListChangedListener {
        fun serverAdded(server: Server?)
        fun serverRemoved(server: Server?)
        fun serverUpdated(server: Server?)
    }

    interface OnSpeedLimitChangedListener {
        fun speedLimitEnabledChanged(isEnabled: Boolean)
    }

    interface OnTorrentsUpdatedListener {
        fun torrentsUpdated(torrents: MutableCollection<Torrent>)
    }

    interface OnFilterSelectedListener {
        fun filterSelected(filter: Filter?)
    }

    interface OnSortingChangedListener {
        fun onSortingChanged(comparator: Comparator<Torrent>?)
    }

    private fun checkAndLaunchOnboardingIfNeeded() {
        // Check if onboarding has been completed
        val prefs = getSharedPreferences("onboarding_prefs", MODE_PRIVATE)
        val onboardingCompleted = prefs.getBoolean("onboarding_completed", false)

        if (!onboardingCompleted) {
            // For simplicity, always launch onboarding if not completed
            // In a more complex implementation, we could check for existing data
            launchOnboarding()
        }
    }

    private fun launchOnboarding() {
        // Launch onboarding activity
        val intent = android.content.Intent(this, uTorrentConnectOnboardingActivity::class.java)
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    companion object {
        private const val SHARED_PREFS_NAME = "transmission_remote_shared_prefs"
        private const val KEY_SERVERS = "key_servers"
        private const val KEY_ACTIVE_SERVER = "key_active_server"
        private const val KEY_FILTER = "key_filter"
        private const val KEY_SORTED_BY = "key_sorted_by"
        private const val KEY_SORT_ORDER = "key_sort_order"
        const val NOTIFICATION_CHANNEL_ID = "finished_torrents_notification_channel_id"
        val allFilters = arrayOf(
            Filters.ALL,
            Filters.ACTIVE,
            Filters.DOWNLOADING,
            Filters.SEEDING,
            Filters.PAUSED,
            Filters.DOWNLOAD_COMPLETED,
            Filters.FINISHED
        )
        @JvmStatic
        lateinit var instance: uTorrentRemote
            private set

        @JvmStatic
        fun getApplication(context: Context): uTorrentRemote {
            return context.applicationContext as uTorrentRemote
        }
    }
}
