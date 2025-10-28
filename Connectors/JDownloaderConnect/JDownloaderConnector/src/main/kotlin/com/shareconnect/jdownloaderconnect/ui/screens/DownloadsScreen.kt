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


package com.shareconnect.jdownloaderconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shareconnect.jdownloaderconnect.domain.model.DownloadStatus
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.AccountViewModel
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.DownloadsViewModel
import com.shareconnect.jdownloaderconnect.ui.components.*
import com.shareconnect.qrscanner.QRScannerManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    downloadsViewModel: DownloadsViewModel,
    accountViewModel: AccountViewModel
) {
    val downloadPackages by downloadsViewModel.downloadPackages.collectAsState()
    val selectedPackage by downloadsViewModel.selectedPackage.collectAsState()
    val isLoading by downloadsViewModel.isLoading.collectAsState()
    val isRefreshing by downloadsViewModel.isRefreshing.collectAsState()
    val errorMessage by downloadsViewModel.errorMessage.collectAsState()
    val activeAccount by accountViewModel.activeAccount.collectAsState()
    val connectionState by accountViewModel.connectionState.collectAsState()

    var selectedFilter by remember { mutableStateOf<DownloadStatus?>(null) }
    var showAddLinksDialog by remember { mutableStateOf(false) }
    var showAddOptionsDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val filteredPackages = downloadsViewModel.getFilteredPackages(selectedFilter)
    val downloadStats = downloadsViewModel.getDownloadStats()

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            downloadsViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Downloads") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        connectionState?.let { connection ->
                            activeAccount?.let { account ->
                                downloadsViewModel.refreshDownloads(connection.sessionToken, account.deviceId)
                            }
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onNavigateToAccounts) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Accounts")
                    }
                }
            )
        },
        floatingActionButton = {
            if (activeAccount != null && connectionState != null) {
                FloatingActionButton(onClick = { showAddOptionsDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Content")
                }
            }
        },
                            onStop = { 
                                connectionState?.let { connection ->
                                    activeAccount?.let { account ->
                                        downloadsViewModel.stopDownloads(
                                            connection.sessionToken,
                                            account.deviceId,
                                            packageIds = listOf(downloadPackage.uuid)
                                        )
                                    }
                                }
                            },
                            onRemove = { 
                                connectionState?.let { connection ->
                                    activeAccount?.let { account ->
                                        downloadsViewModel.removeDownloads(
                                            connection.sessionToken,
                                            account.deviceId,
                                            packageIds = listOf(downloadPackage.uuid)
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Add Links Dialog
        if (showAddOptionsDialog) {
            AlertDialog(
                onDismissRequest = { showAddOptionsDialog = false },
                title = { Text("Add Content") },
                text = {
                    Column {
                        TextButton(
                            onClick = {
                                showAddOptionsDialog = false
                                showAddLinksDialog = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add Links Manually")
                        }
                        TextButton(
                            onClick = {
                                showAddOptionsDialog = false
                                coroutineScope.launch {
                                    val qrResult = QRScannerManager(context).scanQRCode()
                                    if (qrResult != null) {
                                        processScannedUrl(qrResult, downloadsViewModel, connectionState, activeAccount)
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
                },
                properties = androidx.compose.ui.window.DialogProperties()
            )
        }

        if (showAddLinksDialog) {
            AddLinksDialog(
                onDismiss = { showAddLinksDialog = false },
                onLinksAdded = { links ->
                    connectionState?.let { connection ->
                        activeAccount?.let { account ->
                            downloadsViewModel.addLinks(links, connection.sessionToken, account.deviceId)
                        }
                    }
                    showAddLinksDialog = false
                }
            )
        }
                    }
                    showAddLinksDialog = false
                }
            )
        }

        // Error Dialog
        errorMessage?.let { message ->
            ErrorDialog(
                message = message,
                onDismiss = { downloadsViewModel.clearError() }
            )
        }

        // Loading Indicator
        if (isLoading || isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun DownloadStatsCard(stats: com.shareconnect.jdownloaderconnect.presentation.viewmodel.DownloadStats) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Download Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("Total", stats.totalPackages.toString())
                StatItem("Downloading", stats.downloading.toString())
                StatItem("Queued", stats.queued.toString())
                StatItem("Finished", stats.finished.toString())
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress bar for overall download progress
            if (stats.totalBytes > 0) {
                val progress = if (stats.totalBytes > 0) stats.loadedBytes.toFloat() / stats.totalBytes else 0f
                Column {
                    Text(
                        text = "Overall Progress: ${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun FilterChipsRow(
    selectedFilter: DownloadStatus?,
    onFilterSelected: (DownloadStatus?) -> Unit,
    filters: List<Pair<DownloadStatus?, String>>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (filter, label) ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(label) }
            )
        }
    }
}

private fun processScannedUrl(
    url: String,
    downloadsViewModel: DownloadsViewModel,
    connectionState: com.shareconnect.jdownloaderconnect.domain.model.ConnectionState?,
    activeAccount: com.shareconnect.jdownloaderconnect.data.model.JDownloaderAccount?
) {
    if (isValidUrl(url)) {
        // Add the scanned URL directly to downloads
        connectionState?.let { connection ->
            activeAccount?.let { account ->
                downloadsViewModel.addLinks(listOf(url), connection.sessionToken, account.deviceId)
            }
        }
    }
}

private fun isValidUrl(url: String?): Boolean {
    if (url == null) return false
    return url.startsWith("http://") || url.startsWith("https://")
}