package com.shareconnect.portainerconnect.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shareconnect.designsystem.dashboard.*
import com.shareconnect.portainerconnect.data.events.*
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

/**
 * Portainer Dashboard
 * Real-time dashboard showing container status, events, and Docker resources
 */
@Composable
fun PortainerDashboard(
    client: PortainerEventsClient,
    containers: List<ContainerInfo> = emptyList(),
    images: List<ImageInfo> = emptyList(),
    modifier: Modifier = Modifier
) {
    val connected by client.connected.collectAsState()
    val latestContainerEvent by client.latestContainerEvent.collectAsState()
    val latestImageEvent by client.latestImageEvent.collectAsState()

    // Track recent events (last 10)
    val recentEvents = remember { mutableStateListOf<DockerEventMessage>() }

    // Subscribe to events
    LaunchedEffect(Unit) {
        client.subscribeToContainerEvents { event ->
            recentEvents.add(0, event)
            if (recentEvents.size > 10) {
                recentEvents.removeAt(recentEvents.size - 1)
            }
        }
        client.subscribeToImageEvents { event ->
            recentEvents.add(0, event)
            if (recentEvents.size > 10) {
                recentEvents.removeAt(recentEvents.size - 1)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Connection Status
        item {
            DashboardCard(
                title = "Connection",
                icon = Icons.Default.Link
            ) {
                ConnectionStatusIndicator(
                    isConnected = connected,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Quick Stats
        item {
            DashboardCard(
                title = "Quick Stats",
                icon = Icons.Default.Dashboard
            ) {
                QuickStatsGrid(
                    stats = buildQuickStats(containers, images),
                    columns = 2
                )
            }
        }

        // Container Status
        if (containers.isNotEmpty()) {
            item {
                ContainerStatusCard(containers = containers)
            }
        }

        // Recent Events
        if (recentEvents.isNotEmpty()) {
            item {
                DashboardCard(
                    title = "Recent Events",
                    icon = Icons.Default.History,
                    action = {
                        Text(
                            text = "${recentEvents.size}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recentEvents.forEach { event ->
                            EventItem(event = event)
                        }
                    }
                }
            }
        }

        // Images
        if (images.isNotEmpty()) {
            item {
                ImagesCard(images = images)
            }
        }

        // Empty State
        if (containers.isEmpty() && connected) {
            item {
                DashboardEmptyState(
                    icon = Icons.Default.Inbox,
                    title = "No Containers",
                    description = "No containers found on this Docker host"
                )
            }
        }
    }
}

@Composable
private fun ContainerStatusCard(
    containers: List<ContainerInfo>,
    modifier: Modifier = Modifier
) {
    val running = containers.count { it.state == "running" }
    val stopped = containers.count { it.state == "exited" || it.state == "stopped" }
    val paused = containers.count { it.state == "paused" }

    DashboardCard(
        title = "Containers",
        icon = Icons.Default.Widgets,
        modifier = modifier,
        action = {
            Text(
                text = "${containers.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (running > 0) {
                    StatusBadge(
                        text = "$running Running",
                        status = DashboardStatus.SUCCESS
                    )
                }
                if (stopped > 0) {
                    StatusBadge(
                        text = "$stopped Stopped",
                        status = DashboardStatus.NEUTRAL
                    )
                }
                if (paused > 0) {
                    StatusBadge(
                        text = "$paused Paused",
                        status = DashboardStatus.WARNING
                    )
                }
            }

            Divider()

            // Container List
            containers.forEach { container ->
                ContainerItem(container = container)
            }
        }
    }
}

@Composable
private fun ContainerItem(
    container: ContainerInfo,
    modifier: Modifier = Modifier
) {
    val status = getContainerStatus(container.state)
    val icon = getContainerIcon(container.state)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = getContainerColor(container.state)
                    )
                    Text(
                        text = container.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                }
                Text(
                    text = container.image,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            StatusBadge(
                text = container.state,
                status = status
            )
        }
    }
}

@Composable
private fun EventItem(
    event: DockerEventMessage,
    modifier: Modifier = Modifier
) {
    val (icon, color, title, description) = when (event) {
        is ContainerEventMessage -> {
            val icon = when (event.eventType) {
                "start" -> Icons.Default.PlayArrow
                "stop", "die" -> Icons.Default.Stop
                "create" -> Icons.Default.Add
                "destroy" -> Icons.Default.Delete
                "pause" -> Icons.Default.Pause
                "unpause" -> Icons.Default.PlayArrow
                "restart" -> Icons.Default.Refresh
                else -> Icons.Default.Info
            }
            val color = when (event.eventType) {
                "start", "unpause" -> Color(0xFF4CAF50)
                "stop", "die", "pause" -> Color(0xFFFFC107)
                "destroy" -> Color(0xFFF44336)
                else -> Color(0xFF2196F3)
            }
            Tuple4(
                icon,
                color,
                "Container ${event.eventType}",
                event.containerName
            )
        }
        is ImageEventMessage -> {
            val icon = when (event.eventType) {
                "pull" -> Icons.Default.Download
                "push" -> Icons.Default.Upload
                "delete" -> Icons.Default.Delete
                "tag" -> Icons.Default.Label
                else -> Icons.Default.Image
            }
            Tuple4(
                icon,
                Color(0xFF9C27B0),
                "Image ${event.eventType}",
                event.imageName ?: event.imageId
            )
        }
        is NetworkEventMessage -> {
            Tuple4(
                Icons.Default.NetworkCheck,
                Color(0xFF2196F3),
                "Network ${event.eventType}",
                event.networkName
            )
        }
        is VolumeEventMessage -> {
            Tuple4(
                Icons.Default.Storage,
                Color(0xFF607D8B),
                "Volume ${event.eventType}",
                event.volumeName
            )
        }
        else -> {
            Tuple4(
                Icons.Default.Info,
                Color(0xFF9E9E9E),
                "Event",
                event.type
            )
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = color
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = formatEventTime(event.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ImagesCard(
    images: List<ImageInfo>,
    modifier: Modifier = Modifier
) {
    DashboardCard(
        title = "Images",
        icon = Icons.Default.Image,
        modifier = modifier,
        action = {
            Text(
                text = "${images.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            images.take(5).forEach { image ->
                ImageItem(image = image)
            }

            if (images.size > 5) {
                Text(
                    text = "... and ${images.size - 5} more",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ImageItem(
    image: ImageInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = image.repository,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tag: ${image.tag}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = formatSize(image.size),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun buildQuickStats(
    containers: List<ContainerInfo>,
    images: List<ImageInfo>
): List<QuickStat> {
    val stats = mutableListOf<QuickStat>()

    // Running Containers
    val running = containers.count { it.state == "running" }
    stats.add(
        QuickStat(
            label = "Running",
            value = running.toString(),
            icon = Icons.Default.PlayArrow,
            iconColor = Color(0xFF4CAF50)
        )
    )

    // Stopped Containers
    val stopped = containers.count { it.state == "exited" || it.state == "stopped" }
    stats.add(
        QuickStat(
            label = "Stopped",
            value = stopped.toString(),
            icon = Icons.Default.Stop,
            iconColor = Color(0xFF9E9E9E)
        )
    )

    // Total Images
    stats.add(
        QuickStat(
            label = "Images",
            value = images.size.toString(),
            icon = Icons.Default.Image,
            iconColor = Color(0xFF9C27B0)
        )
    )

    // Total Containers
    stats.add(
        QuickStat(
            label = "Total Containers",
            value = containers.size.toString(),
            icon = Icons.Default.Widgets,
            iconColor = Color(0xFF2196F3)
        )
    )

    return stats
}

private fun getContainerStatus(state: String): DashboardStatus = when (state.lowercase()) {
    "running" -> DashboardStatus.SUCCESS
    "paused" -> DashboardStatus.WARNING
    "exited", "stopped" -> DashboardStatus.NEUTRAL
    "dead", "removing" -> DashboardStatus.ERROR
    else -> DashboardStatus.INFO
}

private fun getContainerIcon(state: String) = when (state.lowercase()) {
    "running" -> Icons.Default.PlayArrow
    "paused" -> Icons.Default.Pause
    "exited", "stopped" -> Icons.Default.Stop
    else -> Icons.Default.Circle
}

private fun getContainerColor(state: String): Color = when (state.lowercase()) {
    "running" -> Color(0xFF4CAF50)
    "paused" -> Color(0xFFFFC107)
    "exited", "stopped" -> Color(0xFF9E9E9E)
    else -> Color(0xFFF44336)
}

private fun formatEventTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        else -> {
            val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

private fun formatSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0

    return when {
        gb >= 1 -> "%.1f GB".format(gb)
        mb >= 1 -> "%.1f MB".format(mb)
        kb >= 1 -> "%.1f KB".format(kb)
        else -> "$bytes B"
    }
}

// Helper data classes
data class ContainerInfo(
    val id: String,
    val name: String,
    val image: String,
    val state: String
)

data class ImageInfo(
    val id: String,
    val repository: String,
    val tag: String,
    val size: Long
)

// Tuple helper for event info
private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
