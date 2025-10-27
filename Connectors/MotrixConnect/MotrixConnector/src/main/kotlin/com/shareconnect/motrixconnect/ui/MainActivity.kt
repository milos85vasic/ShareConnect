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


package com.shareconnect.motrixconnect.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            MaterialTheme {
                MotrixConnectApp()
            }
        }
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
        builder.setMessage("Please enter your PIN to access MotrixConnect")

        val input = android.widget.EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("Unlock") { _: android.content.DialogInterface, _: Int ->
            val pin = input.text.toString()
            authenticateWithPin(pin)
        }

        builder.setNegativeButton("Cancel") { _: android.content.DialogInterface, _: Int ->
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
                        // Authentication successful, show main view
                        setupMainView()
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

@Composable
fun MotrixConnectApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MotrixConnect",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Download Manager Integration",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Connect to Motrix download manager to manage and sync downloads across all ShareConnect apps.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
