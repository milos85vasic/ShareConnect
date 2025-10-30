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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.shareconnect.qrscanner.QRScannerManager
import kotlinx.coroutines.launch

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

    // private lateinit var securityAccessManager: SecurityAccessManager
    // private lateinit var themeSyncManager: ThemeSyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // val application = application as SeafileConnectApplication
        // themeSyncManager = application.getThemeSyncManager()
        // securityAccessManager = SecurityAccessManager.getInstance(this)

        showMainUI()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun showMainUI() {
        setContent {
            MaterialTheme {
                val navItems = listOf("Libraries", "Files", "Search", "Settings")
                var selectedTab by remember { mutableStateOf(0) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            navItems.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    icon = { Text(item.first().toString()) },
                                    label = { Text(item) },
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index }
                                )
                            }
                        }
                    }
                ) { padding ->
                    Box(
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
        }
    }










}


@Composable
fun LibrariesTab() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Libraries - Browse and manage Seafile libraries",
            style = MaterialTheme.typography.bodyLarge
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
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Files - Browse files and directories\nTap + to add files via QR code or manual entry",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Search - Search across all libraries",
            style = MaterialTheme.typography.bodyLarge
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
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
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
