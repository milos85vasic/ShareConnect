package com.shareconnect.wireguardconnect.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import digital.vasic.security.access.access.SecurityAccessManager
import digital.vasic.security.access.data.AccessMethod
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Main activity for WireGuardConnect.
 * Handles security access and app initialization.
 */
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val ONBOARDING_PREFS = "onboarding_prefs"
        private const val ONBOARDING_COMPLETED = "onboarding_completed"
    }

    private lateinit var securityAccessManager: SecurityAccessManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "MainActivity onCreate")

        securityAccessManager = SecurityAccessManager.getInstance(this)

        // Check if security access is required BEFORE onboarding
        if (isSecurityAccessRequired()) {
            launchSecurityAccess()
            return
        }

        // Check if onboarding is needed BEFORE setting up UI
        if (isOnboardingNeeded()) {
            // Finish this activity immediately - onboarding will be launched from App
            Log.d(TAG, "Onboarding needed, finishing MainActivity")
            finish()
            return
        }

        setupMainUI()
    }

    override fun onResume() {
        super.onResume()

        // Check security access when app comes back to foreground
        if (isSecurityAccessRequired()) {
            launchSecurityAccess()
            return
        }
    }

    /**
     * Checks if onboarding has been completed.
     */
    private fun isOnboardingNeeded(): Boolean {
        val prefs = getSharedPreferences(ONBOARDING_PREFS, MODE_PRIVATE)
        val completed = prefs.getBoolean(ONBOARDING_COMPLETED, false)
        Log.d(TAG, "Onboarding completed: $completed")
        return !completed
    }

    /**
     * Checks if security access is required.
     */
    private fun isSecurityAccessRequired(): Boolean {
        return try {
            runBlocking {
                val required = securityAccessManager.isAccessRequired()
                Log.d(TAG, "Security access required: $required")
                required
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking security access requirement", e)
            false // Default to no security if there's an error
        }
    }

    /**
     * Launches security access flow.
     */
    private fun launchSecurityAccess() {
        try {
            Log.d(TAG, "Launching security access")
            showSecurityAccessDialog()
        } catch (e: Exception) {
            Log.e(TAG, "Error launching security access", e)
            // If security access fails, continue with normal flow
            setupMainUI()
        }
    }

    /**
     * Shows security access dialog.
     */
    private fun showSecurityAccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Security Access Required")
        builder.setMessage("Please enter your PIN to access WireGuardConnect")

        val input = android.widget.EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("Unlock") { dialog, _ ->
            val pin = input.text.toString()
            authenticateWithPin(pin)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
            finish() // Close app if user cancels
        }

        builder.setCancelable(false)
        builder.show()
    }

    /**
     * Authenticates user with PIN.
     */
    private fun authenticateWithPin(pin: String) {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Authenticating with PIN")
                val result = securityAccessManager.authenticate(AccessMethod.PIN, pin)
                when (result) {
                    is SecurityAccessManager.AuthenticationResult.Success -> {
                        Log.d(TAG, "Authentication successful")
                        setupMainUI()
                    }
                    else -> {
                        Log.w(TAG, "Authentication failed")
                        Toast.makeText(
                            this@MainActivity,
                            "Authentication failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                        showSecurityAccessDialog()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during PIN authentication", e)
                Toast.makeText(
                    this@MainActivity,
                    "Authentication error. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
                showSecurityAccessDialog()
            }
        }
    }

    /**
     * Sets up main UI.
     */
    private fun setupMainUI() {
        Log.d(TAG, "Setting up main UI")
        setContent {
            App()
        }
    }
}
