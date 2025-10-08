package com.shareconnect.activities

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.R
import com.shareconnect.SettingsActivity
import com.shareconnect.utils.TorrentAppHelper
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest {

    private lateinit var context: android.content.Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Initialize TorrentAppHelper
        TorrentAppHelper.initialize(context, "test_app", "Test App", "1.0.0")
    }

    @After
    fun tearDown() {
        // Reset preferences
        TorrentAppHelper.resetDontAskPreferences(context)
        TorrentAppHelper.setDirectSharingEnabled(context, true)
    }

    @Test
    fun testDirectSharingPreference_initializesWithCorrectValue() {
        // Given - direct sharing is enabled by default
        TorrentAppHelper.setDirectSharingEnabled(context, true)

        // When
        val scenario = launchFragmentInContainer<SettingsActivity.SettingsFragment>()

        scenario.onFragment { fragment ->
            val preference = fragment.findPreference<SwitchPreferenceCompat>("direct_torrent_sharing_enabled")
            assertTrue("Direct sharing preference should be checked", preference?.isChecked == true)
        }
    }

    @Test
    fun testDirectSharingPreference_updatesWhenChanged() {
        // Given - direct sharing is disabled
        TorrentAppHelper.setDirectSharingEnabled(context, false)

        // When
        val scenario = launchFragmentInContainer<SettingsActivity.SettingsFragment>()

        scenario.onFragment { fragment ->
            val preference = fragment.findPreference<SwitchPreferenceCompat>("direct_torrent_sharing_enabled")

            // Initially should be false
            assertFalse("Direct sharing preference should be unchecked", preference?.isChecked == true)

            // Change to true
            preference?.isChecked = true

            // Verify the change was persisted
            runBlocking {
                assertTrue("Direct sharing should be enabled",
                    TorrentAppHelper.isDirectSharingEnabled(context))
            }
        }
    }

    @Test
    fun testResetPromptsPreference_resetsDontAskPreferences() {
        // Given - set some don't ask preferences
        TorrentAppHelper.setDontAskQBitConnect(context, true)
        TorrentAppHelper.setDontAskTransmissionConnect(context, true)

        // Verify they are set
        runBlocking {
            assertFalse("Should not ask qBitConnect initially",
                TorrentAppHelper.shouldAskQBitConnectInstall(context))
            assertFalse("Should not ask TransmissionConnect initially",
                TorrentAppHelper.shouldAskTransmissionConnectInstall(context))
        }

        // When
        val scenario = launchFragmentInContainer<SettingsActivity.SettingsFragment>()

        scenario.onFragment { fragment ->
            val preference = fragment.findPreference<Preference>("reset_torrent_prompts")

            // Click the reset preference
            preference?.onPreferenceClickListener?.onPreferenceClick(preference)

            // Then - verify preferences are reset
            runBlocking {
                assertTrue("Should ask qBitConnect after reset",
                    TorrentAppHelper.shouldAskQBitConnectInstall(context))
                assertTrue("Should ask TransmissionConnect after reset",
                    TorrentAppHelper.shouldAskTransmissionConnectInstall(context))
            }
        }
    }

    @Test
    fun testProfilesPreference_opensProfilesActivity() {
        // When
        val scenario = launchFragmentInContainer<SettingsActivity.SettingsFragment>()

        scenario.onFragment { fragment ->
            val preference = fragment.findPreference<Preference>("server_profiles")

            // Click the profiles preference
            val result = preference?.onPreferenceClickListener?.onPreferenceClick(preference)

            // Should return true (handled)
            assertTrue("Profiles preference click should be handled", result == true)
        }
    }

    @Test
    fun testLanguagePreference_opensLanguageSelection() {
        // When
        val scenario = launchFragmentInContainer<SettingsActivity.SettingsFragment>()

        scenario.onFragment { fragment ->
            val preference = fragment.findPreference<Preference>("language_selection")

            // Click the language preference
            val result = preference?.onPreferenceClickListener?.onPreferenceClick(preference)

            // Should return true (handled)
            assertTrue("Language preference click should be handled", result == true)
        }
    }

    @Test
    fun testThemePreference_opensThemeSelection() {
        // When
        val scenario = launchFragmentInContainer<SettingsActivity.SettingsFragment>()

        scenario.onFragment { fragment ->
            val preference = fragment.findPreference<Preference>("theme_selection")

            // Click the theme preference
            val result = preference?.onPreferenceClickListener?.onPreferenceClick(preference)

            // Should return true (handled)
            assertTrue("Theme preference click should be handled", result == true)
        }
    }
}