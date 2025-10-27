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


package com.shareconnect.utorrentconnect.preferences

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.shareconnect.utorrentconnect.R
import com.shareconnect.utorrentconnect.uTorrentRemote

class NotificationPreferencesFragment : PreferenceFragmentCompat() {

    private val notificationsEnabledKey by lazy {
        getString(R.string.torrent_finished_notification_enabled_key)
    }
    private val soundPreferenceKey by lazy {
        getString(R.string.torrent_finished_notification_sound_and_vibrate_key)
    }
    private val application by lazy {
        requireContext().applicationContext as uTorrentRemote
    }
    private val preferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }
    private var rootKey: String? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val preferencesListener = OnSharedPreferenceChangeListener { _, key ->
        if (key == notificationsEnabledKey && preferences.getBoolean(notificationsEnabledKey, false)) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            openNotificationsSettings()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        this.rootKey = rootKey
        setPreferencesFromResource(R.xml.notification_preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (preference.key) {
            soundPreferenceKey -> {
                openNotificationChannelSettings()
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            preferences.registerOnSharedPreferenceChangeListener(preferencesListener)

            val isPermissionDenied = ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_DENIED
            if (preferences.getBoolean(notificationsEnabledKey, false) && isPermissionDenied) {
                application.isNotificationEnabled = false
                setPreferencesFromResource(R.xml.notification_preferences, rootKey)
            }
        }
    }

    override fun onStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            preferences.unregisterOnSharedPreferenceChangeListener(preferencesListener)
        }
        super.onStop()
    }

    private fun openNotificationsSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent, null)
        }
    }

    private fun openNotificationChannelSettings() {
        val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_CHANNEL_ID, uTorrentRemote.NOTIFICATION_CHANNEL_ID)
            .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent, null)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotificationPreferencesFragment()
    }
}
