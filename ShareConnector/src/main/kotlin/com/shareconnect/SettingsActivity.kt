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
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.ListPreference
import androidx.preference.SwitchPreferenceCompat
import androidx.preference.Preference as AndroidPreference
import com.redelf.commons.logging.Console
import com.shareconnect.languagesync.utils.LocaleHelper
import com.shareconnect.utils.SecureStorage
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
        try {
            Console.debug("SettingsActivity.onCreate() - Starting")

            // Apply current theme before calling super.onCreate()
            themeManager = ThemeManager.getInstance(this)
            Console.debug("SettingsActivity.onCreate() - Applying theme")
            themeManager!!.applyTheme(this)

            super.onCreate(savedInstanceState)
            Console.debug("SettingsActivity.onCreate() - super.onCreate() completed")

            // Enable edge-to-edge display
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT

            // Check if this is the first run
            isFirstRun = intent.getBooleanExtra("first_run", false)
            Console.debug("SettingsActivity.onCreate() - isFirstRun: $isFirstRun")

            Console.debug("SettingsActivity.onCreate() - Setting content view")
            setContentView(R.layout.settings_activity)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
            }
            Console.debug("SettingsActivity.onCreate() - Completed")
        } catch (e: Exception) {
            Console.debug("SettingsActivity.onCreate() - Exception: ${e.message}")
            e.printStackTrace()
            // Fallback: finish activity
            finish()
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

             val profilesPreference = findPreference<AndroidPreference>("server_profiles")
             if (profilesPreference != null) {
                 profilesPreference.onPreferenceClickListener = AndroidPreference.OnPreferenceClickListener {
                     val intent = Intent(context, ProfilesActivity::class.java)
                     startActivity(intent)
                     true
                 }
             }

             val languagePreference = findPreference<AndroidPreference>("language_selection")
             if (languagePreference != null) {
                 languagePreference.onPreferenceClickListener = AndroidPreference.OnPreferenceClickListener {
                     // Call the parent activity's method to start language selection
                     (activity as? SettingsActivity)?.startLanguageSelection()
                     true
                 }
             }

              val themePreference = findPreference<AndroidPreference>("theme_selection")
              if (themePreference != null) {
                  themePreference.onPreferenceClickListener = AndroidPreference.OnPreferenceClickListener {
                      // Call the parent activity's method to start theme selection
                      (activity as? SettingsActivity)?.startThemeSelection()
                      true
                  }
              }

              val themeCreatorPreference = findPreference<AndroidPreference>("theme_creator")
              if (themeCreatorPreference != null) {
                  themeCreatorPreference.onPreferenceClickListener = AndroidPreference.OnPreferenceClickListener {
                      // Start theme creator activity
                      val intent = android.content.Intent(context, com.shareconnect.ThemeCreatorActivity::class.java)
                      startActivity(intent)
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

             val resetPromptsPreference = findPreference<AndroidPreference>("reset_torrent_prompts")
             if (resetPromptsPreference != null) {
                 resetPromptsPreference.onPreferenceClickListener = AndroidPreference.OnPreferenceClickListener {
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

             // Secure access preferences
             val secureAccessPreference = findPreference<SwitchPreferenceCompat>("secure_access_enabled")
             if (secureAccessPreference != null) {
                 secureAccessPreference.isChecked = SecureAccessActivity.isSecureAccessEnabled(requireContext())

                 secureAccessPreference.setOnPreferenceChangeListener { _, newValue ->
                     val enabled = newValue as Boolean
                     SecureAccessActivity.setSecureAccessEnabled(requireContext(), enabled)

                     if (enabled) {
                         // Show setup dialog
                         (activity as? SettingsActivity)?.showSecureAccessSetupDialog()
                     } else {
                         // Clear secure data
                         SecureStorage.clearSecureData(requireContext())
                         SecureAccessActivity.setAuthMethod(requireContext(), "")
                     }
                     true
                  }
              }

              val authMethodPreference = findPreference<ListPreference>("auth_method")
              if (authMethodPreference != null) {
                  val currentMethod = SecureAccessActivity.getAuthMethod(requireContext())
                  authMethodPreference.value = currentMethod

                  authMethodPreference.setOnPreferenceChangeListener { _, newValue ->
                      val method = newValue as String
                      SecureAccessActivity.setAuthMethod(requireContext(), method)

                      // Show setup dialog for the selected method
                      (activity as? SettingsActivity)?.showSecureAccessSetupDialog(method)
                      true
                  }
              }

              val biometricPreference = findPreference<SwitchPreferenceCompat>("biometric_enabled")
              if (biometricPreference != null) {
                  biometricPreference.isChecked = SecureStorage.isBiometricEnabled(requireContext())

                  biometricPreference.setOnPreferenceChangeListener { _, newValue ->
                      val enabled = newValue as Boolean
                      SecureStorage.setBiometricEnabled(requireContext(), enabled)
                      true
                  }
              }

              val changePinPreference = findPreference<AndroidPreference>("change_pin")
              if (changePinPreference != null) {
                  changePinPreference.onPreferenceClickListener = AndroidPreference.OnPreferenceClickListener {
                      (activity as? SettingsActivity)?.showPinSetupDialog()
                      true
                  }
              }

              val changePasswordPreference = findPreference<AndroidPreference>("change_password")
              if (changePasswordPreference != null) {
                  changePasswordPreference.onPreferenceClickListener = AndroidPreference.OnPreferenceClickListener {
                      (activity as? SettingsActivity)?.showPasswordSetupDialog()
                      true
                  }
              }
         }
     }

     private fun showSecureAccessSetupDialog(method: String = "pin") {
        val dialogView = layoutInflater.inflate(R.layout.dialog_secure_access_setup, null)
        val editTextPin = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextPin)
        val editTextPassword = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextPassword)
        val editTextConfirmPin = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextConfirmPin)
        val editTextConfirmPassword = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextConfirmPassword)

        when (method) {
            "pin" -> {
                editTextPin.visibility = android.view.View.VISIBLE
                editTextPassword.visibility = android.view.View.GONE
                editTextConfirmPin.visibility = android.view.View.VISIBLE
                editTextConfirmPassword.visibility = android.view.View.GONE
            }
            "password" -> {
                editTextPin.visibility = android.view.View.GONE
                editTextPassword.visibility = android.view.View.VISIBLE
                editTextConfirmPin.visibility = android.view.View.GONE
                editTextConfirmPassword.visibility = android.view.View.VISIBLE
            }
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.setup_secure_access))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                when (method) {
                    "pin" -> {
                        val pin = editTextPin.text.toString()
                        val confirmPin = editTextConfirmPin.text.toString()

                        if (pin.length < 4) {
                            android.widget.Toast.makeText(this, R.string.pin_too_short, android.widget.Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }

                        if (pin != confirmPin) {
                            android.widget.Toast.makeText(this, R.string.pins_do_not_match, android.widget.Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }

                        SecureStorage.storePinHash(this, SecureStorage.hashString(pin + "pin_salt"))
                        SecureAccessActivity.setAuthMethod(this, "pin")
                        android.widget.Toast.makeText(this, R.string.pin_saved, android.widget.Toast.LENGTH_SHORT).show()
                    }
                    "password" -> {
                        val password = editTextPassword.text.toString()
                        val confirmPassword = editTextConfirmPassword.text.toString()

                        if (password.isEmpty()) {
                            android.widget.Toast.makeText(this, R.string.password_required, android.widget.Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }

                        if (password != confirmPassword) {
                            android.widget.Toast.makeText(this, R.string.passwords_do_not_match, android.widget.Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }

                        SecureStorage.storePasswordHash(this, SecureStorage.hashString(password + "password_salt"))
                        SecureAccessActivity.setAuthMethod(this, "password")
                        android.widget.Toast.makeText(this, R.string.password_saved, android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showPinSetupDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_pin_setup, null)
        val editTextCurrentPin = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextCurrentPin)
        val editTextNewPin = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextNewPin)
        val editTextConfirmNewPin = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextConfirmNewPin)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.change_pin))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val currentPin = editTextCurrentPin.text.toString()
                val newPin = editTextNewPin.text.toString()
                val confirmNewPin = editTextConfirmNewPin.text.toString()

                // Verify current PIN
                val storedPinHash = SecureStorage.getStoredPinHash(this)
                val currentPinHash = SecureStorage.hashString(currentPin + "pin_salt")

                if (storedPinHash != currentPinHash) {
                    android.widget.Toast.makeText(this, R.string.current_pin_incorrect, android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPin.length < 4) {
                    android.widget.Toast.makeText(this, R.string.pin_too_short, android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPin != confirmNewPin) {
                    android.widget.Toast.makeText(this, R.string.pins_do_not_match, android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                SecureStorage.storePinHash(this, SecureStorage.hashString(newPin + "pin_salt"))
                android.widget.Toast.makeText(this, R.string.pin_changed, android.widget.Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showPasswordSetupDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_password_setup, null)
        val editTextCurrentPassword = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextCurrentPassword)
        val editTextNewPassword = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextNewPassword)
        val editTextConfirmNewPassword = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextConfirmNewPassword)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.change_password))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val currentPassword = editTextCurrentPassword.text.toString()
                val newPassword = editTextNewPassword.text.toString()
                val confirmNewPassword = editTextConfirmNewPassword.text.toString()

                // Verify current password
                val storedPasswordHash = SecureStorage.getStoredPasswordHash(this)
                val currentPasswordHash = SecureStorage.hashString(currentPassword + "password_salt")

                if (storedPasswordHash != currentPasswordHash) {
                    android.widget.Toast.makeText(this, R.string.current_password_incorrect, android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword.isEmpty()) {
                    android.widget.Toast.makeText(this, R.string.password_required, android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword != confirmNewPassword) {
                    android.widget.Toast.makeText(this, R.string.passwords_do_not_match, android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                SecureStorage.storePasswordHash(this, SecureStorage.hashString(newPassword + "password_salt"))
                android.widget.Toast.makeText(this, R.string.password_changed, android.widget.Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    companion object {
        private const val THEME_SELECTION_REQUEST = 1001
        private const val LANGUAGE_SELECTION_REQUEST = 1002
    }
}