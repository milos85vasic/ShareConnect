package com.shareconnect.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.shareconnect.ServerProfile
import com.shareconnect.torrentsharingsync.TorrentSharingSyncManager
import kotlinx.coroutines.runBlocking
import java.io.File

/**
 * Helper class for managing torrent app integrations (qBitConnect and TransmissionConnect)
 *
 * Features:
 * - Detects if torrent apps are installed
 * - Creates sharing intents for magnet links and torrent files
 * - Provides app store links for installation
 * - Manages "don't ask again" preferences
 */
object TorrentAppHelper {

    // Package names
    const val PACKAGE_QBITCONNECT = "com.shareconnect.qbitconnect"
    const val PACKAGE_QBITCONNECT_DEBUG = "com.shareconnect.qbitconnect.debug"
    const val PACKAGE_TRANSMISSIONCONNECT = "com.shareconnect.transmissionconnect"

    // App Store URLs
    const val PLAY_STORE_QBITCONNECT = "market://details?id=$PACKAGE_QBITCONNECT"
    const val PLAY_STORE_TRANSMISSIONCONNECT = "market://details?id=$PACKAGE_TRANSMISSIONCONNECT"
    const val WEB_PLAY_STORE_QBITCONNECT = "https://play.google.com/store/apps/details?id=$PACKAGE_QBITCONNECT"
    const val WEB_PLAY_STORE_TRANSMISSIONCONNECT = "https://play.google.com/store/apps/details?id=$PACKAGE_TRANSMISSIONCONNECT"

    // Torrent content types
    const val MIME_TYPE_MAGNET = "application/x-bittorrent-magnet"
    const val MIME_TYPE_TORRENT_FILE = "application/x-bittorrent"

    // Sync manager instance
    private var syncManager: TorrentSharingSyncManager? = null

    /**
     * Initialize the sync manager
     */
    fun initialize(context: Context, appId: String, appName: String, appVersion: String) {
        if (syncManager == null) {
            syncManager = TorrentSharingSyncManager.getInstance(context, appId, appName, appVersion)
        }
    }

    /**
     * Get sync manager instance
     */
    private fun getSyncManager(context: Context): TorrentSharingSyncManager {
        return syncManager ?: throw IllegalStateException(
            "TorrentAppHelper not initialized. Call initialize() first."
        )
    }

    /**
     * Check if qBitConnect is installed
     */
    fun isQBitConnectInstalled(context: Context): Boolean {
        return isAppInstalled(context, PACKAGE_QBITCONNECT) ||
                isAppInstalled(context, PACKAGE_QBITCONNECT_DEBUG)
    }

    /**
     * Check if TransmissionConnect is installed
     */
    fun isTransmissionConnectInstalled(context: Context): Boolean {
        return isAppInstalled(context, PACKAGE_TRANSMISSIONCONNECT)
    }

    /**
     * Check if any torrent app is installed
     */
    fun hasAnyTorrentAppInstalled(context: Context): Boolean {
        return isQBitConnectInstalled(context) || isTransmissionConnectInstalled(context)
    }

    /**
     * Get installed torrent apps for a given profile
     */
    fun getInstalledAppForProfile(context: Context, profile: ServerProfile): String? {
        if (!profile.isTorrent()) return null

        return when (profile.torrentClientType) {
            ServerProfile.TORRENT_CLIENT_QBITTORRENT ->
                if (isQBitConnectInstalled(context)) PACKAGE_QBITCONNECT else null
            ServerProfile.TORRENT_CLIENT_TRANSMISSION ->
                if (isTransmissionConnectInstalled(context)) PACKAGE_TRANSMISSIONCONNECT else null
            else -> null
        }
    }

    /**
     * Get the appropriate app package for a torrent client type
     */
    fun getAppPackageForClientType(clientType: String?): String? {
        return when (clientType) {
            ServerProfile.TORRENT_CLIENT_QBITTORRENT -> PACKAGE_QBITCONNECT
            ServerProfile.TORRENT_CLIENT_TRANSMISSION -> PACKAGE_TRANSMISSIONCONNECT
            else -> null
        }
    }

    /**
     * Check if direct sharing is enabled in settings
     */
    fun isDirectSharingEnabled(context: Context): Boolean {
        return runBlocking {
            getSyncManager(context).getOrCreateDefault().directSharingEnabled
        }
    }

    /**
     * Set direct sharing enabled/disabled
     */
    fun setDirectSharingEnabled(context: Context, enabled: Boolean) {
        runBlocking {
            getSyncManager(context).setDirectSharingEnabled(enabled)
        }
    }

    /**
     * Check if "don't ask again" is set for qBitConnect installation
     */
    fun shouldAskQBitConnectInstall(context: Context): Boolean {
        return runBlocking {
            !getSyncManager(context).getOrCreateDefault().dontAskQBitConnect
        }
    }

    /**
     * Check if "don't ask again" is set for TransmissionConnect installation
     */
    fun shouldAskTransmissionConnectInstall(context: Context): Boolean {
        return runBlocking {
            !getSyncManager(context).getOrCreateDefault().dontAskTransmissionConnect
        }
    }

    /**
     * Set "don't ask again" for qBitConnect installation
     */
    fun setDontAskQBitConnect(context: Context, dontAsk: Boolean) {
        runBlocking {
            getSyncManager(context).setDontAskQBitConnect(dontAsk)
        }
    }

    /**
     * Set "don't ask again" for TransmissionConnect installation
     */
    fun setDontAskTransmissionConnect(context: Context, dontAsk: Boolean) {
        runBlocking {
            getSyncManager(context).setDontAskTransmissionConnect(dontAsk)
        }
    }

    /**
     * Reset all "don't ask again" preferences
     */
    fun resetDontAskPreferences(context: Context) {
        runBlocking {
            getSyncManager(context).resetDontAskPreferences()
        }
    }

    /**
     * Create intent to share magnet link directly to torrent app
     */
    fun createMagnetShareIntent(context: Context, magnetLink: String, targetPackage: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(magnetLink)
            setPackage(if (targetPackage == PACKAGE_QBITCONNECT && isAppInstalled(context, PACKAGE_QBITCONNECT_DEBUG)) {
                PACKAGE_QBITCONNECT_DEBUG
            } else {
                targetPackage
            })
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return intent
    }

    /**
     * Create intent to share torrent file directly to torrent app
     */
    fun createTorrentFileShareIntent(context: Context, torrentFile: File, targetPackage: String): Intent {
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", torrentFile)
        } else {
            Uri.fromFile(torrentFile)
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, MIME_TYPE_TORRENT_FILE)
            setPackage(if (targetPackage == PACKAGE_QBITCONNECT && isAppInstalled(context, PACKAGE_QBITCONNECT_DEBUG)) {
                PACKAGE_QBITCONNECT_DEBUG
            } else {
                targetPackage
            })
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        return intent
    }

    /**
     * Create intent to open app in Play Store
     */
    fun createPlayStoreIntent(context: Context, targetPackage: String): Intent {
        val storeUrl = when (targetPackage) {
            PACKAGE_QBITCONNECT, PACKAGE_QBITCONNECT_DEBUG -> PLAY_STORE_QBITCONNECT
            PACKAGE_TRANSMISSIONCONNECT -> PLAY_STORE_TRANSMISSIONCONNECT
            else -> return Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=torrent"))
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // Fallback to web Play Store if Play Store app not installed
        if (intent.resolveActivity(context.packageManager) == null) {
            val webUrl = when (targetPackage) {
                PACKAGE_QBITCONNECT, PACKAGE_QBITCONNECT_DEBUG -> WEB_PLAY_STORE_QBITCONNECT
                PACKAGE_TRANSMISSIONCONNECT -> WEB_PLAY_STORE_TRANSMISSIONCONNECT
                else -> "https://play.google.com"
            }
            return Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
        }

        return intent
    }

    /**
     * Get app name for display
     */
    fun getAppName(targetPackage: String): String {
        return when (targetPackage) {
            PACKAGE_QBITCONNECT, PACKAGE_QBITCONNECT_DEBUG -> "qBitConnect"
            PACKAGE_TRANSMISSIONCONNECT -> "TransmissionConnect"
            else -> "Torrent App"
        }
    }

    /**
     * Check if a URL is a magnet link
     */
    fun isMagnetLink(url: String?): Boolean {
        return url?.trim()?.startsWith("magnet:", ignoreCase = true) == true
    }

    /**
     * Check if a URL is a torrent file
     */
    fun isTorrentFile(url: String?): Boolean {
        return url?.trim()?.endsWith(".torrent", ignoreCase = true) == true
    }

    /**
     * Check if content is torrent-related
     */
    fun isTorrentContent(url: String?): Boolean {
        return isMagnetLink(url) || isTorrentFile(url)
    }

    /**
     * Determine which app to suggest based on profile type
     */
    fun getSuggestedAppForProfile(profile: ServerProfile): String? {
        if (!profile.isTorrent()) return null

        return when (profile.torrentClientType) {
            ServerProfile.TORRENT_CLIENT_QBITTORRENT -> PACKAGE_QBITCONNECT
            ServerProfile.TORRENT_CLIENT_TRANSMISSION -> PACKAGE_TRANSMISSIONCONNECT
            ServerProfile.TORRENT_CLIENTUTORRENT -> null // No specific app for uTorrent yet
            else -> null
        }
    }

    /**
     * Check if app installation should be suggested for profile
     */
    fun shouldSuggestAppInstallation(context: Context, profile: ServerProfile): Boolean {
        val suggestedApp = getSuggestedAppForProfile(profile) ?: return false
        val isInstalled = isAppInstalled(context, suggestedApp)

        if (isInstalled) return false

        return when (suggestedApp) {
            PACKAGE_QBITCONNECT, PACKAGE_QBITCONNECT_DEBUG -> shouldAskQBitConnectInstall(context)
            PACKAGE_TRANSMISSIONCONNECT -> shouldAskTransmissionConnectInstall(context)
            else -> false
        }
    }

    /**
     * Check if an app is installed
     */
    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(packageName, 0)
            }
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Data class for torrent sharing result
     */
    data class SharingResult(
        val success: Boolean,
        val appPackage: String? = null,
        val appName: String? = null,
        val needsInstallation: Boolean = false,
        val message: String? = null
    )

    /**
     * Attempt to share torrent content directly to appropriate app
     *
     * @return SharingResult with details about the sharing attempt
     */
    fun attemptDirectShare(
        context: Context,
        content: String,
        profile: ServerProfile
    ): SharingResult {
        if (!isDirectSharingEnabled(context)) {
            return SharingResult(
                success = false,
                message = "Direct sharing is disabled in settings"
            )
        }

        if (!isTorrentContent(content)) {
            return SharingResult(
                success = false,
                message = "Content is not a torrent magnet link or file"
            )
        }

        val targetPackage = getInstalledAppForProfile(context, profile)
            ?: return SharingResult(
                success = false,
                needsInstallation = true,
                appPackage = getSuggestedAppForProfile(profile),
                appName = getSuggestedAppForProfile(profile)?.let { getAppName(it) },
                message = "Target app not installed"
            )

        return try {
            val intent = if (isMagnetLink(content)) {
                createMagnetShareIntent(context, content, targetPackage)
            } else {
                // For torrent files, content would be a file path
                val file = File(content)
                if (!file.exists()) {
                    return SharingResult(
                        success = false,
                        message = "Torrent file not found"
                    )
                }
                createTorrentFileShareIntent(context, file, targetPackage)
            }

            context.startActivity(intent)

            SharingResult(
                success = true,
                appPackage = targetPackage,
                appName = getAppName(targetPackage),
                message = "Shared to ${getAppName(targetPackage)}"
            )
        } catch (e: Exception) {
            SharingResult(
                success = false,
                message = "Failed to share: ${e.message}"
            )
        }
    }
}
