package com.shareconnect.preferencessync.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "synced_preferences")
data class PreferencesData(
    @PrimaryKey val id: String,
    val category: String, // "download", "bandwidth", "notification", "ui", "connection", "update", "advanced"
    val key: String,
    val value: String?, // Stored as JSON for complex values
    val type: String, // "string", "int", "long", "boolean", "json"
    val description: String? = null,
    val sourceApp: String,
    val version: Int = 1,
    val lastModified: Long = System.currentTimeMillis()
) {
    companion object {
        const val OBJECT_TYPE = "preferences"

        // Categories
        const val CATEGORY_DOWNLOAD = "download"
        const val CATEGORY_BANDWIDTH = "bandwidth"
        const val CATEGORY_NOTIFICATION = "notification"
        const val CATEGORY_UI = "ui"
        const val CATEGORY_CONNECTION = "connection"
        const val CATEGORY_UPDATE = "update"
        const val CATEGORY_ADVANCED = "advanced"

        // Types
        const val TYPE_STRING = "string"
        const val TYPE_INT = "int"
        const val TYPE_LONG = "long"
        const val TYPE_BOOLEAN = "boolean"
        const val TYPE_JSON = "json"

        // Common preference keys
        // Download preferences
        const val KEY_DEFAULT_DOWNLOAD_PATH = "default_download_path"
        const val KEY_CREATE_SUBDIRECTORIES = "create_subdirectories"
        const val KEY_SUBDIRECTORY_PATTERN = "subdirectory_pattern"

        // Bandwidth preferences
        const val KEY_GLOBAL_DOWNLOAD_LIMIT = "global_download_limit"
        const val KEY_GLOBAL_UPLOAD_LIMIT = "global_upload_limit"
        const val KEY_ALTERNATIVE_DOWNLOAD_LIMIT = "alternative_download_limit"
        const val KEY_ALTERNATIVE_UPLOAD_LIMIT = "alternative_upload_limit"
        const val KEY_SCHEDULE_ENABLED = "schedule_enabled"
        const val KEY_SCHEDULE_FROM = "schedule_from"
        const val KEY_SCHEDULE_TO = "schedule_to"
        const val KEY_SCHEDULE_DAYS = "schedule_days"

        // Notification preferences
        const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val KEY_NOTIFY_ON_DOWNLOAD_COMPLETE = "notify_on_download_complete"
        const val KEY_NOTIFY_ON_DOWNLOAD_ERROR = "notify_on_download_error"
        const val KEY_NOTIFY_ON_TORRENT_COMPLETE = "notify_on_torrent_complete"
        const val KEY_NOTIFICATION_SOUND = "notification_sound"
        const val KEY_NOTIFICATION_VIBRATE = "notification_vibrate"

        // UI preferences
        const val KEY_THEME = "theme"
        const val KEY_SORT_ORDER = "sort_order"
        const val KEY_VIEW_MODE = "view_mode"
        const val KEY_SHOW_HIDDEN_FILES = "show_hidden_files"
        const val KEY_LANGUAGE = "language"

        // Connection preferences
        const val KEY_CONNECTION_TIMEOUT = "connection_timeout"
        const val KEY_MAX_RETRIES = "max_retries"
        const val KEY_RETRY_DELAY = "retry_delay"
        const val KEY_USE_HTTPS = "use_https"
        const val KEY_VERIFY_SSL = "verify_ssl"

        // Update preferences
        const val KEY_AUTO_REFRESH_ENABLED = "auto_refresh_enabled"
        const val KEY_REFRESH_INTERVAL = "refresh_interval"
        const val KEY_CHECK_FOR_UPDATES = "check_for_updates"

        // Advanced preferences
        const val KEY_ENABLE_LOGGING = "enable_logging"
        const val KEY_LOG_LEVEL = "log_level"
        const val KEY_MAX_CONCURRENT_DOWNLOADS = "max_concurrent_downloads"
        const val KEY_ENABLE_EXPERIMENTAL_FEATURES = "enable_experimental_features"
    }

    fun getStringValue(): String? = if (type == TYPE_STRING) value else null
    fun getIntValue(): Int? = if (type == TYPE_INT) value?.toIntOrNull() else null
    fun getLongValue(): Long? = if (type == TYPE_LONG) value?.toLongOrNull() else null
    fun getBooleanValue(): Boolean? = if (type == TYPE_BOOLEAN) value?.toBooleanStrictOrNull() else null
    fun getJsonValue(): String? = if (type == TYPE_JSON) value else null
}
