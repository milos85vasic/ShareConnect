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
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.AccountViewModel
import com.shareconnect.jdownloaderconnect.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun AccountsScreen(
    onNavigateToDownloads: () -> Unit,
    onNavigateToLinkGrabber: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: AccountViewModel
) {
    val accounts by viewModel.accounts.collectAsState()
    val activeAccount by viewModel.activeAccount.collectAsState()
    val devices by viewModel.devices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showAddAccountDialog by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("JDownloader Connect") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddAccountDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Account")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Connection Status
            ConnectionStatusCard(
                activeAccount = activeAccount,
                devices = devices,
                isLoading = isLoading,
                onDisconnect = { viewModel.disconnectAccount() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionCard(
                    title = "Downloads",
                    icon = Icons.Default.Download,
                    onClick = onNavigateToDownloads,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Link Grabber",
                    icon = Icons.Default.Link,
                    onClick = onNavigateToLinkGrabber,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Accounts List
            Text(
                text = "Accounts",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (accounts.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.AccountCircle,
                    title = "No Accounts",
                    description = "Add your first JDownloader account to get started"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(accounts) { account ->
                        AccountCard(
                            account = account,
                            isActive = account.id == activeAccount?.id,
                            onSetActive = { viewModel.setActiveAccount(account) },
                            onDelete = { viewModel.deleteAccount(account) }
                        )
                    }
                }
            }
        }

        // Add Account Dialog
        if (showAddAccountDialog) {
            AddAccountDialog(
                onDismiss = { showAddAccountDialog = false },
                onConnect = { email, password, deviceName ->
                    viewModel.connectAccount(email, password, deviceName)
                    showAddAccountDialog = false
                }
            )
        }

        // Error Dialog
        errorMessage?.let { message ->
            ErrorDialog(
                message = message,
                onDismiss = { viewModel.clearError() }
            )
        }
    }
}

@Composable
private fun ConnectionStatusCard(
    activeAccount: com.shareconnect.jdownloaderconnect.data.model.JDownloaderAccount?,
    devices: List<com.shareconnect.jdownloaderconnect.domain.model.JDownloaderDevice>,
    isLoading: Boolean,
    onDisconnect: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Connection Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (activeAccount != null) "Connected" else "Not Connected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (activeAccount != null) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.error
                    )
                }

                if (activeAccount != null && !isLoading) {
                    Button(onClick = onDisconnect) {
                        Text("Disconnect")
                    }
                }
            }

            if (activeAccount != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Account: ${activeAccount.email}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Device: ${activeAccount.deviceName}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                if (devices.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Connected Devices: ${devices.size}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}