package com.shareconnect

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.redelf.commons.logging.Console
import com.shareconnect.languagesync.utils.LocaleHelper
import com.shareconnect.utils.TorrentAppHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private var themeManager: ThemeManager? = null
    private var isFirstRun = false

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply current theme before calling super.onCreate()
        themeManager = ThemeManager.getInstance(this)
        themeManager!!.applyTheme(this)

        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        // Check if this is the first run
        isFirstRun = intent.getBooleanExtra("first_run", false)

        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (isFirstRun) {
            // If this is the first run, we need to check if profiles were created
            val profileManager = ProfileManager(this)
            if (profileManager.hasProfiles()) {
                // Profiles were created, start MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()
            } else {
                // No profiles created, just finish and let the app close
                finishAffinity() // This will finish all activities and properly close the app
            }
        } else {
            // Normal back behavior
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // If theme was changed, recreate this activity to apply the new theme
        if (requestCode == THEME_SELECTION_REQUEST && resultCode == RESULT_OK) {
            Console.debug(getString(R.string.log_theme_change_detected))
            // Simply recreate the current activity instead of starting a new one
            recreate()
        }
        // If language was changed, recreate this activity to apply the new language
        if (requestCode == LANGUAGE_SELECTION_REQUEST && resultCode == RESULT_OK) {
            Console.debug("Language changed")
            // Simply recreate the current activity instead of starting a new one
            recreate()
        }
    }

    fun startLanguageSelection() {
        val intent = Intent(this, LanguageSelectionActivity::class.java)
        startActivityForResult(intent, LANGUAGE_SELECTION_REQUEST)
    }

    fun startThemeSelection() {
        val intent = Intent(this, ThemeSelectionActivity::class.java)
        startActivityForResult(intent, THEME_SELECTION_REQUEST)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val profilesPreference = findPreference<Preference>("server_profiles")
            if (profilesPreference != null) {
                profilesPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val intent = Intent(context, ProfilesActivity::class.java)
                    startActivity(intent)
                    true
                }
            }

            val languagePreference = findPreference<Preference>("language_selection")
            if (languagePreference != null) {
                languagePreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    // Call the parent activity's method to start language selection
                    (activity as? SettingsActivity)?.startLanguageSelection()
                    true
                }
            }

            val themePreference = findPreference<Preference>("theme_selection")
            if (themePreference != null) {
                themePreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    // Call the parent activity's method to start theme selection
                    (activity as? SettingsActivity)?.startThemeSelection()
                    true
                }
            }

            // Torrent sharing preferences
            val directSharingPreference = findPreference<SwitchPreferenceCompat>("direct_torrent_sharing_enabled")
            if (directSharingPreference != null) {
                // Initialize with current value
                CoroutineScope(Dispatchers.Main).launch {
                    val isEnabled = TorrentAppHelper.isDirectSharingEnabled(requireContext())
                    directSharingPreference.isChecked = isEnabled
                }

                directSharingPreference.setOnPreferenceChangeListener { _, newValue ->
                    val enabled = newValue as Boolean
                    TorrentAppHelper.setDirectSharingEnabled(requireContext(), enabled)
                    true
                }
            }

            val resetPromptsPreference = findPreference<Preference>("reset_torrent_prompts")
            if (resetPromptsPreference != null) {
                resetPromptsPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    TorrentAppHelper.resetDontAskPreferences(requireContext())
                    // Show confirmation
                    android.widget.Toast.makeText(
                        context,
                        getString(R.string.torrent_prompts_reset),
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    true
                }
            }
        }
    }

    companion object {
        private const val THEME_SELECTION_REQUEST = 1001
        private const val LANGUAGE_SELECTION_REQUEST = 1002
    }
}