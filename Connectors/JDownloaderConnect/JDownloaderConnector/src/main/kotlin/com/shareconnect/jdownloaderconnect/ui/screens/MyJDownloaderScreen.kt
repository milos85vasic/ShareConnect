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

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shareconnect.jdownloaderconnect.domain.model.*
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.MyJDownloaderViewModel
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.MyJDownloaderUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyJDownloaderScreen(
    onNavigateBack: () -> Unit,
    viewModel: MyJDownloaderViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedInstance by viewModel.selectedInstance.collectAsState()
    val dashboardData by viewModel.dashboardData.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My JDownloader") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshInstances() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                is MyJDownloaderUiState.Loading -> {
                    LoadingScreen()
                }
                is MyJDownloaderUiState.Success -> {
                    val instances = (uiState as MyJDownloaderUiState.Success).instances
                    MyJDownloaderContent(
                        instances = instances,
                        selectedInstance = selectedInstance,
                        dashboardData = dashboardData,
                        onInstanceSelected = { viewModel.selectInstance(it) },
                        onControlInstance = { instanceId, action ->
                            viewModel.controlInstance(instanceId, action)
                        },
                        onRefresh = { viewModel.refreshDashboard() }
                    )
                }
                is MyJDownloaderUiState.Error -> {
                    ErrorScreen(
                        message = (uiState as MyJDownloaderUiState.Error).message,
                        onRetry = { viewModel.refreshInstances() }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()

            Text(
                text = "Loading JDownloader instances...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

@Composable
private fun MyJDownloaderContent(
    instances: List<JDownloaderInstance>,
    selectedInstance: JDownloaderInstance?,
    dashboardData: InstanceDashboard?,
    onInstanceSelected: (String) -> Unit,
    onControlInstance: (String, com.shareconnect.jdownloaderconnect.presentation.viewmodel.InstanceAction) -> Unit,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Instances overview
        item {
            Text(
                text = "JDownloader Instances",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(instances) { instance ->
            InstanceCard(
                instance = instance,
                isSelected = instance.id == selectedInstance?.id,
                onClick = { onInstanceSelected(instance.id) },
                        onControl = { action -> onControlInstance(instance.id, com.shareconnect.jdownloaderconnect.presentation.viewmodel.InstanceAction.valueOf(action.name)) }
            )
        }

        // Selected instance dashboard
        selectedInstance?.let { instance ->
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${instance.name} Dashboard",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Status overview
            item {
                StatusOverviewCard(instance = instance)
            }

            // Recent activity
            dashboardData?.let { data ->
                item {
                    RecentActivityCard(activities = data.recentActivity)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InstanceCard(
    instance: JDownloaderInstance,
    isSelected: Boolean,
    onClick: () -> Unit,
    onControl: (com.shareconnect.jdownloaderconnect.presentation.viewmodel.InstanceAction) -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(300)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = instance.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "v${instance.version}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status indicator with animation
                StatusIndicator(status = instance.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Instance stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatItem(
                    label = "Active",
                    value = instance.activeDownloads.toString(),
                    icon = Icons.Default.Download
                )
                StatItem(
                    label = "Speed",
                    value = formatSpeed(instance.currentSpeed),
                    icon = Icons.Default.Speed
                )
                StatItem(
                    label = "Uptime",
                    value = formatUptime(instance.uptime),
                    icon = Icons.Default.Schedule
                )
            }

            // Control buttons
            if (isSelected) {
                Spacer(modifier = Modifier.height(12.dp))
                ControlButtons(
                    status = instance.status,
                    onControl = onControl
                )
            }
        }
    }
}

@Composable
private fun StatusIndicator(status: InstanceStatus) {
    val (color, icon) = when (status) {
        InstanceStatus.RUNNING -> MaterialTheme.colorScheme.primary to Icons.Default.PlayArrow
        InstanceStatus.PAUSED -> MaterialTheme.colorScheme.tertiary to Icons.Default.Pause
        InstanceStatus.STOPPED -> MaterialTheme.colorScheme.error to Icons.Default.Stop
        InstanceStatus.ERROR -> MaterialTheme.colorScheme.error to Icons.Default.Error
        InstanceStatus.OFFLINE -> MaterialTheme.colorScheme.outline to Icons.Default.WifiOff
        InstanceStatus.CONNECTING -> MaterialTheme.colorScheme.secondary to Icons.Default.Sync
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = status.getDisplayName(),
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

@Composable
private fun StatItem(label: String, value: String, icon: ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ControlButtons(
    status: InstanceStatus,
    onControl: (com.shareconnect.jdownloaderconnect.presentation.viewmodel.InstanceAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (status) {
            InstanceStatus.RUNNING -> {
                OutlinedButton(
                    onClick = { onControl(com.shareconnect.jdownloaderconnect.presentation.viewmodel.InstanceAction.PAUSE) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pause")
                }
                OutlinedButton(
                    onClick = { onControl(com.shareconnect.jdownloaderconnect.presentation.viewmodel.InstanceAction.STOP) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Stop")
                }
            }
            InstanceStatus.PAUSED, InstanceStatus.STOPPED -> {
                Button(
                    onClick = { onControl(com.shareconnect.jdownloaderconnect.presentation.viewmodel.InstanceAction.START) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start")
                }
            }
            else -> {
                // No controls for other states
            }
        }
    }
}

@Composable
private fun StatusOverviewCard(instance: JDownloaderInstance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Status Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatusMetric(
                    label = "Status",
                    value = instance.status.getDisplayName(),
                    color = when (instance.status) {
                        InstanceStatus.RUNNING -> MaterialTheme.colorScheme.primary
                        InstanceStatus.PAUSED -> MaterialTheme.colorScheme.tertiary
                        InstanceStatus.ERROR -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.outline
                    }
                )
                StatusMetric(
                    label = "Active Downloads",
                    value = instance.activeDownloads.toString(),
                    color = MaterialTheme.colorScheme.primary
                )
                StatusMetric(
                    label = "Current Speed",
                    value = formatSpeed(instance.currentSpeed),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            instance.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusMetric(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun RecentActivityCard(activities: List<DownloadActivity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (activities.isEmpty()) {
                Text(
                    text = "No recent activity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 32.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                activities.forEach { activity ->
                    ActivityItem(activity = activity)
                    if (activity != activities.last()) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityItem(activity: DownloadActivity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status icon
        val (statusIcon, statusColor) = when (activity.status) {
            DownloadStatus.DOWNLOADING -> Icons.Default.Download to MaterialTheme.colorScheme.primary
            DownloadStatus.PAUSED -> Icons.Default.Pause to MaterialTheme.colorScheme.tertiary
            DownloadStatus.FINISHED -> Icons.Default.CheckCircle to MaterialTheme.colorScheme.primary
            DownloadStatus.FAILED -> Icons.Default.Error to MaterialTheme.colorScheme.error
            else -> Icons.Default.Download to MaterialTheme.colorScheme.outline
        }

        Icon(
            statusIcon,
            contentDescription = null,
            tint = statusColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.filename,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${(activity.progress * 100).roundToInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = formatSpeed(activity.speed),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                if (activity.eta > 0) {
                    Text(
                        text = formatEta(activity.eta),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}



// Utility functions
private fun formatSpeed(bytesPerSecond: Long): String {
    return when {
        bytesPerSecond >= 1024 * 1024 * 1024 -> "%.1f GB/s".format(bytesPerSecond / (1024.0 * 1024.0 * 1024.0))
        bytesPerSecond >= 1024 * 1024 -> "%.1f MB/s".format(bytesPerSecond / (1024.0 * 1024.0))
        bytesPerSecond >= 1024 -> "%.1f KB/s".format(bytesPerSecond / 1024.0)
        else -> "$bytesPerSecond B/s"
    }
}

private fun formatUptime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return "${hours}h ${minutes}m"
}

private fun formatEta(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m ${remainingSeconds}s"
        else -> "${remainingSeconds}s"
    }
}