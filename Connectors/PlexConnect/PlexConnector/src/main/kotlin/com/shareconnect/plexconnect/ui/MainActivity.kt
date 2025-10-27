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


package com.shareconnect.plexconnect.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.shareconnect.languagesync.utils.LocaleHelper
import digital.vasic.security.access.access.SecurityAccessManager
import digital.vasic.security.access.data.AccessMethod
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private lateinit var securityAccessManager: SecurityAccessManager

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SecurityAccessManager first
        securityAccessManager = SecurityAccessManager.getInstance(this)

        // Check if security access is required BEFORE setting up UI
        if (isSecurityAccessRequired()) {
            launchSecurityAccess()
            return
        }

        // Check if onboarding is needed
        if (isOnboardingNeeded()) {
            // Launch onboarding activity
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Show main UI
        setupMainView()
    }

    override fun onResume() {
        super.onResume()

        // Check security access on resume (when coming back from background)
        if (isSecurityAccessRequired()) {
            launchSecurityAccess()
        }
    }

    private fun setupMainView() {
        setContent {
            App()
        }
    }

    private fun isOnboardingNeeded(): Boolean {
        val prefs = getSharedPreferences("plex_onboarding_prefs", MODE_PRIVATE)
        return !prefs.getBoolean("onboarding_completed", false)
    }

    private fun isSecurityAccessRequired(): Boolean {
        return try {
            runBlocking {
                securityAccessManager.isAccessRequired()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error checking security access requirement", e)
            false // Default to no security if there's an error
        }
    }

    private fun launchSecurityAccess() {
        try {
            showSecurityAccessDialog()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error launching security access", e)
            // If security access fails, continue with normal flow
            setupMainView()
        }
    }

    private fun showSecurityAccessDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Security Access Required")
        builder.setMessage("Please enter your PIN to access PlexConnect")

        val input = android.widget.EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
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

    private fun authenticateWithPin(pin: String) {
        lifecycleScope.launch {
            try {
                val result = securityAccessManager.authenticate(AccessMethod.PIN, pin)
                when (result) {
                    is SecurityAccessManager.AuthenticationResult.Success -> {
                        // Authentication successful, check onboarding or show main view
                        if (isOnboardingNeeded()) {
                            val intent = Intent(this@MainActivity, OnboardingActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            setupMainView()
                        }
                    }
                    else -> {
                        // Authentication failed, show error and retry
                        Toast.makeText(this@MainActivity, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
                        showSecurityAccessDialog()
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during PIN authentication", e)
                Toast.makeText(this@MainActivity, "Authentication error. Please try again.", Toast.LENGTH_SHORT).show()
                showSecurityAccessDialog()
            }
        }
    }
}