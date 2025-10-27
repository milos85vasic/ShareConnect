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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shareconnect.jdownloaderconnect.domain.model.LinkAvailability
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.AccountViewModel
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.LinkGrabberViewModel
import com.shareconnect.jdownloaderconnect.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkGrabberScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    linkGrabberViewModel: LinkGrabberViewModel,
    accountViewModel: AccountViewModel
) {
    val linkGrabberPackages by linkGrabberViewModel.linkGrabberPackages.collectAsState()
    val selectedPackage by linkGrabberViewModel.selectedPackage.collectAsState()
    val isLoading by linkGrabberViewModel.isLoading.collectAsState()
    val isRefreshing by linkGrabberViewModel.isRefreshing.collectAsState()
    val errorMessage by linkGrabberViewModel.errorMessage.collectAsState()
    val activeAccount by accountViewModel.activeAccount.collectAsState()
    val connectionState by accountViewModel.connectionState.collectAsState()

    var selectedFilter by remember { mutableStateOf<LinkAvailability?>(null) }
    var showAddLinksDialog by remember { mutableStateOf(false) }

    val filteredPackages = linkGrabberViewModel.getFilteredPackages(selectedFilter)
    val linkGrabberStats = linkGrabberViewModel.getLinkGrabberStats()

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            linkGrabberViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Link Grabber") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        connectionState?.let { connection ->
                            activeAccount?.let { account ->
                                linkGrabberViewModel.refreshLinkGrabber(connection.sessionToken, account.deviceId)
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
            // Link Grabber Stats
            LinkGrabberStatsCard(stats = linkGrabberStats)

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips
            FilterChipsRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                filters = listOf(
                    null to "All",
                    LinkAvailability.ONLINE to "Online",
                    LinkAvailability.OFFLINE to "Offline",
                    LinkAvailability.UNKNOWN to "Unknown"
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Link Grabber Packages List
            if (filteredPackages.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Link,
                    title = "No Links",
                    description = "Add links to the Link Grabber"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredPackages) { linkGrabberPackage ->
                        LinkGrabberPackageCard(
                            linkGrabberPackage = linkGrabberPackage,
                            isSelected = linkGrabberPackage.uuid == selectedPackage?.uuid,
                            onSelect = { linkGrabberViewModel.selectPackage(linkGrabberPackage) },
                            onMoveToDownloads = { 
                                connectionState?.let { connection ->
                                    activeAccount?.let { account ->
                                        linkGrabberViewModel.moveToDownloadList(
                                            connection.sessionToken,
                                            account.deviceId,
                                            packageIds = listOf(linkGrabberPackage.uuid)
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
                            linkGrabberViewModel.addLinksToLinkGrabber(
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
                onDismiss = { linkGrabberViewModel.clearError() }
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
private fun LinkGrabberStatsCard(stats: com.shareconnect.jdownloaderconnect.presentation.viewmodel.LinkGrabberStats) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Link Grabber Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("Packages", stats.totalPackages.toString())
                StatItem("Links", stats.totalLinks.toString())
                StatItem("Online", stats.onlineLinks.toString())
                StatItem("Hosters", stats.hosterCount.toString())
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Link availability breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AvailabilityIndicator("Online", stats.onlineLinks, MaterialTheme.colorScheme.primary)
                AvailabilityIndicator("Offline", stats.offlineLinks, MaterialTheme.colorScheme.error)
                AvailabilityIndicator("Unknown", stats.unknownLinks, MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
private fun AvailabilityIndicator(label: String, count: Int, color: androidx.compose.ui.graphics.Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, androidx.compose.foundation.shape.CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$count",
            style = MaterialTheme.typography.bodySmall,
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
    selectedFilter: LinkAvailability?,
    onFilterSelected: (LinkAvailability?) -> Unit,
    filters: List<Pair<LinkAvailability?, String>>
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