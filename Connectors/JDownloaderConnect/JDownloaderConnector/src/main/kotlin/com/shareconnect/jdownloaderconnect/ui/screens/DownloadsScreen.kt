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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shareconnect.jdownloaderconnect.domain.model.DownloadStatus
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.AccountViewModel
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.DownloadsViewModel
import com.shareconnect.jdownloaderconnect.ui.components.*

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
                FloatingActionButton(onClick = { showAddLinksDialog = true }) {
                    Icon(Icons.Default.AddLink, contentDescription = "Add Links")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Download Stats
            DownloadStatsCard(stats = downloadStats)

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips
            FilterChipsRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                filters = listOf(
                    null to "All",
                    DownloadStatus.DOWNLOADING to "Downloading",
                    DownloadStatus.QUEUED to "Queued",
                    DownloadStatus.PAUSED to "Paused",
                    DownloadStatus.FINISHED to "Finished"
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Downloads List
            if (filteredPackages.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Download,
                    title = "No Downloads",
                    description = "Add links to start downloading"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredPackages) { downloadPackage ->
                        DownloadPackageCard(
                            downloadPackage = downloadPackage,
                            isSelected = downloadPackage.uuid == selectedPackage?.uuid,
                            onSelect = { downloadsViewModel.selectPackage(downloadPackage) },
                            onStart = { 
                                connectionState?.let { connection ->
                                    activeAccount?.let { account ->
                                        downloadsViewModel.startDownloads(
                                            connection.sessionToken,
                                            account.deviceId,
                                            packageIds = listOf(downloadPackage.uuid)
                                        )
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
        if (showAddLinksDialog) {
            AddLinksDialog(
                onDismiss = { showAddLinksDialog = false },
                onAddLinks = { links, packageName, destinationFolder, extractAfterDownload, autoStart ->
                    connectionState?.let { connection ->
                        activeAccount?.let { account ->
                            downloadsViewModel.addLinks(
                                connection.sessionToken,
                                account.deviceId,
                                links,
                                packageName,
                                destinationFolder,
                                extractAfterDownload,
                                autoStart
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