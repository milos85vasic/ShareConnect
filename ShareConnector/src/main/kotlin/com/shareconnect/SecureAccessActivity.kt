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
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.redelf.commons.logging.Console
import com.shareconnect.utils.SecureStorage
import java.util.concurrent.Executor

class SecureAccessActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "secure_access_prefs"
        private const val KEY_SECURE_ACCESS_ENABLED = "secure_access_enabled"
        private const val KEY_AUTH_METHOD = "auth_method" // "pin", "password", "biometric"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_PASSWORD_HASH = "password_hash"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_ALIAS = "secure_access_key"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"

        fun isSecureAccessEnabled(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_SECURE_ACCESS_ENABLED, false)
        }

        fun setSecureAccessEnabled(context: Context, enabled: Boolean) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_SECURE_ACCESS_ENABLED, enabled).apply()
        }

        fun setAuthMethod(context: Context, method: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_AUTH_METHOD, method).apply()
        }

        fun getAuthMethod(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_AUTH_METHOD, null)
        }


    }

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: PromptInfo
    private lateinit var executor: Executor

    private var editTextPin: TextInputEditText? = null
    private var editTextPassword: TextInputEditText? = null
    private var buttonAuthenticate: MaterialButton? = null
    private var buttonUseBiometric: MaterialButton? = null
    private var buttonForgotPassword: MaterialButton? = null

    private var currentAuthMethod: String? = null
    private var biometricSupported = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if secure access is enabled
        if (!isSecureAccessEnabled(this)) {
            // Secure access not enabled, go directly to main activity
            startMainActivity()
            return
        }

        currentAuthMethod = getAuthMethod(this)

        // Check biometric support
        biometricSupported = checkBiometricSupport()

        setContentView(R.layout.activity_secure_access)

        initializeViews()
        setupBiometricPrompt()
        setupAuthMethod()
    }

    private fun initializeViews() {
        editTextPin = findViewById(R.id.editTextPin)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonAuthenticate = findViewById(R.id.buttonAuthenticate)
        buttonUseBiometric = findViewById(R.id.buttonUseBiometric)
        buttonForgotPassword = findViewById(R.id.buttonForgotPassword)

        buttonAuthenticate?.setOnClickListener {
            authenticate()
        }

        buttonUseBiometric?.setOnClickListener {
            authenticateWithBiometric()
        }

        buttonForgotPassword?.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun setupBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Console.debug("Biometric authentication error: $errString")
                    Toast.makeText(this@SecureAccessActivity,
                        getString(R.string.biometric_auth_error, errString),
                        Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Console.debug("Biometric authentication succeeded")
                    Toast.makeText(this@SecureAccessActivity,
                        R.string.biometric_auth_success,
                        Toast.LENGTH_SHORT).show()
                    startMainActivity()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Console.debug("Biometric authentication failed")
                    Toast.makeText(this@SecureAccessActivity,
                        R.string.biometric_auth_failed,
                        Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_prompt_title))
            .setSubtitle(getString(R.string.biometric_prompt_subtitle))
            .setAllowedAuthenticators(Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText(getString(R.string.cancel))
            .build()
    }

    private fun setupAuthMethod() {
        when (currentAuthMethod) {
            "pin" -> {
                editTextPin?.visibility = View.VISIBLE
                editTextPassword?.visibility = View.GONE
                buttonAuthenticate?.text = getString(R.string.authenticate_with_pin)
                editTextPin?.requestFocus()
            }
            "password" -> {
                editTextPin?.visibility = View.GONE
                editTextPassword?.visibility = View.VISIBLE
                buttonAuthenticate?.text = getString(R.string.authenticate_with_password)
                editTextPassword?.requestFocus()
            }
            "biometric" -> {
                editTextPin?.visibility = View.GONE
                editTextPassword?.visibility = View.GONE
                buttonAuthenticate?.visibility = View.GONE
                buttonUseBiometric?.visibility = View.VISIBLE

                if (biometricSupported) {
                    // Auto-prompt for biometric if available
                    Handler(Looper.getMainLooper()).postDelayed({
                        authenticateWithBiometric()
                    }, 500)
                } else {
                    Toast.makeText(this, R.string.biometric_not_available, Toast.LENGTH_LONG).show()
                    finish()
                }
            }
            else -> {
                // Default to PIN
                editTextPin?.visibility = View.VISIBLE
                editTextPassword?.visibility = View.GONE
                buttonAuthenticate?.text = getString(R.string.authenticate_with_pin)
                editTextPin?.requestFocus()
            }
        }

        // Show biometric button if supported and not in biometric-only mode
        if (biometricSupported && currentAuthMethod != "biometric") {
            buttonUseBiometric?.visibility = View.VISIBLE
        } else {
            buttonUseBiometric?.visibility = View.GONE
        }
    }

    private fun authenticate() {
        when (currentAuthMethod) {
            "pin" -> authenticateWithPin()
            "password" -> authenticateWithPassword()
        }
    }

    private fun authenticateWithPin() {
        val pin = editTextPin?.text?.toString() ?: ""

        if (pin.length < 4) {
            Toast.makeText(this, R.string.pin_too_short, Toast.LENGTH_SHORT).show()
            return
        }

        val storedPinHash = getStoredPinHash()
        if (storedPinHash == null) {
            Toast.makeText(this, R.string.secure_access_not_configured, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val pinHash = hashPin(pin)
        if (pinHash == storedPinHash) {
            Toast.makeText(this, R.string.authentication_successful, Toast.LENGTH_SHORT).show()
            startMainActivity()
        } else {
            Toast.makeText(this, R.string.invalid_pin, Toast.LENGTH_SHORT).show()
            editTextPin?.text?.clear()
        }
    }

    private fun authenticateWithPassword() {
        val password = editTextPassword?.text?.toString() ?: ""

        if (password.isEmpty()) {
            Toast.makeText(this, R.string.password_required, Toast.LENGTH_SHORT).show()
            return
        }

        val storedPasswordHash = getStoredPasswordHash()
        if (storedPasswordHash == null) {
            Toast.makeText(this, R.string.secure_access_not_configured, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val passwordHash = hashPassword(password)
        if (passwordHash == storedPasswordHash) {
            Toast.makeText(this, R.string.authentication_successful, Toast.LENGTH_SHORT).show()
            startMainActivity()
        } else {
            Toast.makeText(this, R.string.invalid_password, Toast.LENGTH_SHORT).show()
            editTextPassword?.text?.clear()
        }
    }

    private fun authenticateWithBiometric() {
        if (!biometricSupported) {
            Toast.makeText(this, R.string.biometric_not_available, Toast.LENGTH_SHORT).show()
            return
        }

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            Console.debug("Biometric authentication failed: ${e.message}")
            Toast.makeText(this, R.string.biometric_auth_error_generic, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkBiometricSupport(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return when (biometricManager.canAuthenticate(Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> false
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> false
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false
            else -> false
        }
    }

    private fun showForgotPasswordDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(R.string.forgot_password_title)
            .setMessage(R.string.forgot_password_message)
            .setPositiveButton(R.string.go_to_settings) { _, _ ->
                val intent = Intent(this, SettingsActivity::class.java)
                intent.putExtra("show_secure_access_settings", true)
                startActivity(intent)
                finish()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun getStoredPinHash(): String? {
        return SecureStorage.getStoredPinHash(this)
    }

    private fun getStoredPasswordHash(): String? {
        return SecureStorage.getStoredPasswordHash(this)
    }

    private fun hashPin(pin: String): String {
        return SecureStorage.hashString(pin + "pin_salt")
    }

    private fun hashPassword(password: String): String {
        return SecureStorage.hashString(password + "password_salt")
    }

    override fun onBackPressed() {
        // Prevent back navigation when secure access is enabled
        Toast.makeText(this, R.string.secure_access_required, Toast.LENGTH_SHORT).show()
    }
}