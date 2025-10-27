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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.AccountViewModel
import com.shareconnect.jdownloaderconnect.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    accountViewModel: AccountViewModel
) {
    val accounts by accountViewModel.accounts.collectAsState()
    val activeAccount by accountViewModel.activeAccount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Account Settings
            SettingsSection(title = "Account Settings") {
                SettingsItem(
                    icon = Icons.Default.AccountCircle,
                    title = "Active Account",
                    subtitle = activeAccount?.email ?: "No account connected",
                    onClick = {}
                )
                
                SettingsItem(
                    icon = Icons.Default.Devices,
                    title = "Connected Devices",
                    subtitle = "Manage JDownloader devices",
                    onClick = {}
                )
                
                SettingsItem(
                    icon = Icons.Default.Security,
                    title = "Security",
                    subtitle = "Account security settings",
                    onClick = {}
                )
            }

            // Download Settings
            SettingsSection(title = "Download Settings") {
                SettingsItem(
                    icon = Icons.Default.Download,
                    title = "Download Directory",
                    subtitle = "Set default download location",
                    onClick = {}
                )
                
                SettingsItem(
                    icon = Icons.Default.Folder,
                    title = "Package Settings",
                    subtitle = "Configure package behavior",
                    onClick = {}
                )
                
                SettingsItem(
                    icon = Icons.Default.Speed,
                    title = "Speed Limits",
                    subtitle = "Set download/upload limits",
                    onClick = {}
                )
            }

            // Connection Settings
            SettingsSection(title = "Connection Settings") {
                SettingsItem(
                    icon = Icons.Default.Wifi,
                    title = "Connection Limits",
                    subtitle = "Configure connection settings",
                    onClick = {}
                )
                
                SettingsItem(
                    icon = Icons.Default.Cloud,
                    title = "Proxy Settings",
                    subtitle = "Configure proxy connections",
                    onClick = {}
                )
                
                SettingsItem(
                    icon = Icons.Default.Sync,
                    title = "Sync Interval",
                    subtitle = "Set data sync frequency",
                    onClick = {}
                )
            }

            // Application Settings
            SettingsSection(title = "Application Settings") {
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "Theme",
                    subtitle = "Change app appearance",
                    onClick = {}
                )
                
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "Language",
                    subtitle = "Change app language",
                    onClick = {}
                )
                
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Configure notifications",
                    onClick = {}
                )
                
                SettingsItem(
                    icon = Icons.Default.Storage,
                    title = "Storage",
                    subtitle = "Manage app storage",
                    onClick = {}
                )
            }

            // About Section
            SettingsSection(title = "About") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "About JDownloader Connect",
                    subtitle = "Version 1.0.0",
                    onClick = {}
                )
                
                SettingsItem(
                    icon = Icons.Default.Help,
                    title = "Help & Support",
                    subtitle = "Get help and documentation",
                    onClick = {}
                )
                
                SettingsItem(
                    icon = Icons.Default.BugReport,
                    title = "Report Issue",
                    subtitle = "Report bugs and issues",
                    onClick = {}
                )
            }

            // Danger Zone
            SettingsSection(title = "Danger Zone") {
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Clear All Data",
                    subtitle = "Delete all app data and accounts",
                    onClick = {},
                    isDestructive = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    ListItem(
        headlineContent = { 
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDestructive) MaterialTheme.colorScheme.error 
                       else MaterialTheme.colorScheme.onSurface
            )
        },
        supportingContent = { 
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDestructive) MaterialTheme.colorScheme.error 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isDestructive) MaterialTheme.colorScheme.error 
                       else MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = if (isDestructive) MaterialTheme.colorScheme.error 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
    
    if (!isDestructive) {
        Divider(modifier = Modifier.padding(start = 72.dp))
    }
}