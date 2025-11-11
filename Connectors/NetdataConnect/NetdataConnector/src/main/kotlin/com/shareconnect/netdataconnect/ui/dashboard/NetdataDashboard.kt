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


package com.shareconnect.netdataconnect.ui.dashboard

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
import com.shareconnect.netdataconnect.data.realtime.*
import kotlinx.coroutines.flow.StateFlow

/**
 * Netdata Dashboard
 * Real-time dashboard showing system metrics, alarms, and health status
 */
@Composable
fun NetdataDashboard(
    client: NetdataRealtimeClient,
    modifier: Modifier = Modifier
) {
    val connected by client.connected.collectAsState()
    val healthStatus by client.healthStatus.collectAsState()
    val alarms by client.alarms.collectAsState()
    val latestMetrics by client.latestMetrics.collectAsState()

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

        // Health Status
        healthStatus?.let { health ->
            item {
                HealthStatusCard(health = health)
            }
        }

        // Quick Stats
        latestMetrics?.let { metrics ->
            item {
                DashboardCard(
                    title = "Quick Stats",
                    icon = Icons.Default.Dashboard
                ) {
                    QuickStatsGrid(
                        stats = buildQuickStats(metrics),
                        columns = 2
                    )
                }
            }
        }

        // Alarms
        if (alarms.isNotEmpty()) {
            item {
                AlarmsCard(alarms = alarms)
            }
        }

        // Key Metrics
        latestMetrics?.let { metrics ->
            item {
                KeyMetricsCard(metrics = metrics)
            }
        }

        // Empty State
        if (healthStatus == null && connected) {
            item {
                DashboardLoadingState(
                    message = "Loading metrics..."
                )
            }
        }
    }
}

@Composable
private fun HealthStatusCard(
    health: HealthStatusUpdateMessage,
    modifier: Modifier = Modifier
) {
    val status = when {
        health.critical > 0 -> DashboardStatus.ERROR
        health.warning > 0 -> DashboardStatus.WARNING
        else -> DashboardStatus.SUCCESS
    }

    val statusText = when (status) {
        DashboardStatus.ERROR -> "Critical"
        DashboardStatus.WARNING -> "Warning"
        DashboardStatus.SUCCESS -> "Healthy"
        else -> "Unknown"
    }

    DashboardCard(
        title = "Health Status",
        icon = Icons.Default.MonitorHeart,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Overall Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "Overall Status",
                    style = MaterialTheme.typography.bodyMedium
                )
                StatusBadge(
                    text = statusText,
                    status = status
                )
            }

            Divider()

            // Alarm Counts
            if (health.critical > 0) {
                MetricItem(
                    label = "Critical Alarms",
                    value = health.critical.toString(),
                    icon = Icons.Default.Error,
                    valueColor = MaterialTheme.colorScheme.error
                )
            }

            if (health.warning > 0) {
                MetricItem(
                    label = "Warning Alarms",
                    value = health.warning.toString(),
                    icon = Icons.Default.Warning,
                    valueColor = Color(0xFFFFC107)
                )
            }

            if (health.critical == 0 && health.warning == 0) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "All systems operational",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@Composable
private fun AlarmsCard(
    alarms: List<AlarmUpdateMessage.AlarmStatus>,
    modifier: Modifier = Modifier
) {
    val critical = alarms.filter { it.status == "CRITICAL" }
    val warning = alarms.filter { it.status == "WARNING" }

    DashboardCard(
        title = "Active Alarms",
        icon = Icons.Default.Notifications,
        modifier = modifier,
        action = {
            Text(
                text = "${alarms.size}",
                style = MaterialTheme.typography.labelLarge,
                color = if (critical.isNotEmpty()) {
                    MaterialTheme.colorScheme.error
                } else {
                    Color(0xFFFFC107)
                }
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Critical Alarms
            critical.forEach { alarm ->
                AlarmItem(alarm = alarm, isCritical = true)
            }

            // Warning Alarms
            warning.forEach { alarm ->
                AlarmItem(alarm = alarm, isCritical = false)
            }
        }
    }
}

@Composable
private fun AlarmItem(
    alarm: AlarmUpdateMessage.AlarmStatus,
    isCritical: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCritical) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            } else {
                Color(0xFFFFF9C4)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = alarm.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(
                    text = alarm.status,
                    status = if (isCritical) DashboardStatus.ERROR else DashboardStatus.WARNING
                )
            }

            alarm.info?.let { info ->
                Text(
                    text = info,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Value
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Value:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${alarm.value} ${alarm.units ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun KeyMetricsCard(
    metrics: MetricsUpdateMessage,
    modifier: Modifier = Modifier
) {
    DashboardCard(
        title = "Key Metrics",
        icon = Icons.Default.Speed,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // CPU
            metrics.charts["system.cpu"]?.let { cpuChart ->
                cpuChart.dimensions["user"]?.let { userCpu ->
                    cpuChart.dimensions["system"]?.let { systemCpu ->
                        val totalCpu = userCpu + systemCpu
                        LabeledProgressIndicator(
                            label = "CPU Usage",
                            progress = (totalCpu.toFloat() / 100f).coerceIn(0f, 1f)
                        )
                    }
                }
            }

            // RAM
            metrics.charts["system.ram"]?.let { ramChart ->
                ramChart.dimensions["used"]?.let { used ->
                    ramChart.dimensions["free"]?.let { free ->
                        val total = used + free
                        val progress = if (total > 0) (used / total).toFloat() else 0f
                        LabeledProgressIndicator(
                            label = "RAM Usage",
                            progress = progress
                        )
                    }
                }
            }

            // Disk
            metrics.charts["disk_space._"]?.let { diskChart ->
                diskChart.dimensions["used"]?.let { used ->
                    diskChart.dimensions["avail"]?.let { avail ->
                        val total = used + avail
                        val progress = if (total > 0) (used / total).toFloat() else 0f
                        LabeledProgressIndicator(
                            label = "Disk Usage",
                            progress = progress
                        )
                    }
                }
            }

            // Network
            metrics.charts["system.net"]?.let { netChart ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    netChart.dimensions["received"]?.let { received ->
                        MetricItem(
                            label = "Network In",
                            value = formatBytes(received),
                            icon = Icons.Default.Download
                        )
                    }
                    netChart.dimensions["sent"]?.let { sent ->
                        MetricItem(
                            label = "Network Out",
                            value = formatBytes(sent),
                            icon = Icons.Default.Upload
                        )
                    }
                }
            }
        }
    }
}

private fun buildQuickStats(metrics: MetricsUpdateMessage): List<QuickStat> {
    val stats = mutableListOf<QuickStat>()

    // CPU Usage
    metrics.charts["system.cpu"]?.let { cpuChart ->
        cpuChart.dimensions["user"]?.let { userCpu ->
            cpuChart.dimensions["system"]?.let { systemCpu ->
                val totalCpu = (userCpu + systemCpu).toInt()
                stats.add(
                    QuickStat(
                        label = "CPU Usage",
                        value = "$totalCpu%",
                        icon = Icons.Default.Memory,
                        iconColor = getCpuColor(totalCpu)
                    )
                )
            }
        }
    }

    // RAM Usage
    metrics.charts["system.ram"]?.let { ramChart ->
        ramChart.dimensions["used"]?.let { used ->
            ramChart.dimensions["free"]?.let { free ->
                val total = used + free
                val percentage = if (total > 0) ((used / total) * 100).toInt() else 0
                stats.add(
                    QuickStat(
                        label = "RAM Usage",
                        value = "$percentage%",
                        icon = Icons.Default.Storage,
                        iconColor = getRamColor(percentage)
                    )
                )
            }
        }
    }

    // Active Connections
    metrics.charts["system.net"]?.dimensions?.size?.let { connections ->
        stats.add(
            QuickStat(
                label = "Network",
                value = "Active",
                icon = Icons.Default.NetworkCheck,
                iconColor = Color(0xFF4CAF50)
            )
        )
    }

    // Charts Monitored
    stats.add(
        QuickStat(
            label = "Charts",
            value = metrics.charts.size.toString(),
            icon = Icons.Default.ShowChart,
            iconColor = Color(0xFF2196F3)
        )
    )

    return stats
}

private fun getCpuColor(percentage: Int): Color = when {
    percentage >= 90 -> Color(0xFFF44336)
    percentage >= 70 -> Color(0xFFFFC107)
    else -> Color(0xFF4CAF50)
}

private fun getRamColor(percentage: Int): Color = when {
    percentage >= 90 -> Color(0xFFF44336)
    percentage >= 70 -> Color(0xFFFFC107)
    else -> Color(0xFF4CAF50)
}

private fun formatBytes(bytes: Double): String {
    val kb = bytes / 1024
    val mb = kb / 1024
    val gb = mb / 1024

    return when {
        gb >= 1 -> "%.2f GB/s".format(gb)
        mb >= 1 -> "%.2f MB/s".format(mb)
        kb >= 1 -> "%.2f KB/s".format(kb)
        else -> "%.0f B/s".format(bytes)
    }
}
