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


package com.shareconnect.seafileconnect.ui

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope

import com.shareconnect.designsystem.compose.theme.DesignSystemTheme
import com.shareconnect.qrscanner.QRScannerManager
import com.shareconnect.seafileconnect.SeafileConnectApplication
import digital.vasic.security.access.access.SecurityAccessManager
import digital.vasic.security.access.access.AccessMethod
import com.shareconnect.themesync.ThemeSyncManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * MainActivity for SeafileConnect
 * 
 * Provides access to Seafile server functionality with:
 * - Library browsing
 * - File management
 * - Upload/download capabilities
 * - Search functionality
 * - Encrypted library support
 */
class MainActivity : ComponentActivity() {

    private lateinit var securityAccessManager: SecurityAccessManager
    private lateinit var themeSyncManager: ThemeSyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = application as SeafileConnectApplication
        themeSyncManager = application.getThemeSyncManager()
        securityAccessManager = SecurityAccessManager.getInstance(this)

        // Check if security access is required BEFORE showing main UI
        if (isSecurityAccessRequired()) {
            Log.d("MainActivity", "Security access required, launching authentication")
            launchSecurityAccess()
            return
        }

        showMainUI()
    }

    override fun onResume() {
        super.onResume()

        // Check security access when app comes back to foreground
        if (isSecurityAccessRequired()) {
            Log.d("MainActivity", "Security access required on resume")
            launchSecurityAccess()
            return
        }
    }

    private fun showMainUI() {
        setContent {
            val themeData by themeSyncManager.observeDefaultTheme().collectAsState(initial = null)

            if (themeData != null) {
                DesignSystemTheme(themeData = themeData!!) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SeafileConnectScreen()
                    }
                }
            } else {
                // Fallback to default theme while loading
                DesignSystemTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SeafileConnectScreen()
            }
        }
    }

    /**
     * Check if security access is required
     */
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

    /**
     * Launch security access authentication
     */
    private fun launchSecurityAccess() {
        try {
            showSecurityAccessDialog()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error launching security access", e)
            // If security access fails, continue with normal flow
            showMainUI()
        }
    }

    /**
     * Show security access authentication dialog
     */
    private fun showSecurityAccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Security Access Required")
        builder.setMessage("Please enter your PIN to access the application")

        val input = EditText(this)
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

    /**
     * Authenticate with PIN using SecurityAccessManager
     */
    private fun authenticateWithPin(pin: String) {
        lifecycleScope.launch {
            try {
                val result = securityAccessManager.authenticate(AccessMethod.PIN, pin)
                when (result) {
                    is SecurityAccessManager.AuthenticationResult.Success -> {
                        // Authentication successful, continue with normal flow
                        showMainUI()
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeafileConnectScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Libraries", "Files", "Search", "Settings")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SeafileConnect") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = { Text(title.first().toString()) },
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> LibrariesTab()
                1 -> FilesTab()
                2 -> SearchTab()
                3 -> SettingsTab()
            }
        }
    }
}

@Composable
fun LibrariesTab() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            "Libraries - Browse and manage Seafile libraries",
            modifier = Modifier.padding(MaterialTheme.typography.bodyLarge.fontSize.value.toInt().dp)
        )
    }
}

@Composable
fun FilesTab() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showAddOptionsDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddOptionsDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                "Files - Browse files and directories\nTap + to add files via QR code or manual entry",
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    if (showAddOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showAddOptionsDialog = false },
            title = { Text("Add Files") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            showAddOptionsDialog = false
                            // TODO: Implement manual file addition
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Files Manually")
                    }
                    TextButton(
                        onClick = {
                            showAddOptionsDialog = false
                            scope.launch {
                                val qrResult = QRScannerManager(context).scanQRCode()
                                if (qrResult != null) {
                                    // TODO: Process scanned URL for file upload
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Scan QR Code")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddOptionsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SearchTab() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            "Search - Search across all libraries",
            modifier = Modifier.padding(MaterialTheme.typography.bodyLarge.fontSize.value.toInt().dp)
        )
    }
}

@Composable
fun SettingsTab() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showQRDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Settings - Configure SeafileConnect",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { showQRDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Scan QR Code for Configuration")
        }
    }

    if (showQRDialog) {
        AlertDialog(
            onDismissRequest = { showQRDialog = false },
            title = { Text("Scan Configuration QR") },
            text = { Text("Scan a QR code containing Seafile server configuration or settings.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showQRDialog = false
                        scope.launch {
                            val qrResult = QRScannerManager(context).scanQRCode()
                            if (qrResult != null) {
                                // TODO: Process scanned configuration
                            }
                        }
                    }
                ) {
                    Text("Scan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQRDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
