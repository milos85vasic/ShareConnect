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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shareconnect.designsystem.dashboard.*
import com.shareconnect.netdataconnect.data.realtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * Netdata Charts Screen
 * Real-time charts showing system metrics with time-series visualization
 */
@Composable
fun NetdataChartsScreen(
    client: NetdataRealtimeClient,
    modifier: Modifier = Modifier
) {
    val connected by client.connected.collectAsState()
    val latestMetrics by client.latestMetrics.collectAsState()

    // Store historical data for charts (last 60 points)
    val cpuHistory = remember { mutableStateListOf<Pair<Long, Double>>() }
    val ramHistory = remember { mutableStateListOf<Pair<Long, Double>>() }
    val diskHistory = remember { mutableStateListOf<Pair<Long, Double>>() }
    val networkInHistory = remember { mutableStateListOf<Pair<Long, Double>>() }
    val networkOutHistory = remember { mutableStateListOf<Pair<Long, Double>>() }

    // Update historical data when new metrics arrive
    LaunchedEffect(latestMetrics) {
        latestMetrics?.let { metrics ->
            val timestamp = System.currentTimeMillis()

            // CPU
            metrics.charts["system.cpu"]?.let { cpuChart ->
                cpuChart.dimensions["user"]?.let { userCpu ->
                    cpuChart.dimensions["system"]?.let { systemCpu ->
                        val totalCpu = userCpu + systemCpu
                        cpuHistory.add(timestamp to totalCpu)
                        if (cpuHistory.size > 60) cpuHistory.removeAt(0)
                    }
                }
            }

            // RAM
            metrics.charts["system.ram"]?.let { ramChart ->
                ramChart.dimensions["used"]?.let { used ->
                    ramChart.dimensions["free"]?.let { free ->
                        val total = used + free
                        val percentage = if (total > 0) (used / total) * 100 else 0.0
                        ramHistory.add(timestamp to percentage)
                        if (ramHistory.size > 60) ramHistory.removeAt(0)
                    }
                }
            }

            // Disk
            metrics.charts["disk_space._"]?.let { diskChart ->
                diskChart.dimensions["used"]?.let { used ->
                    diskChart.dimensions["avail"]?.let { avail ->
                        val total = used + avail
                        val percentage = if (total > 0) (used / total) * 100 else 0.0
                        diskHistory.add(timestamp to percentage)
                        if (diskHistory.size > 60) diskHistory.removeAt(0)
                    }
                }
            }

            // Network
            metrics.charts["system.net"]?.let { netChart ->
                netChart.dimensions["received"]?.let { received ->
                    networkInHistory.add(timestamp to received)
                    if (networkInHistory.size > 60) networkInHistory.removeAt(0)
                }
                netChart.dimensions["sent"]?.let { sent ->
                    networkOutHistory.add(timestamp to sent)
                    if (networkOutHistory.size > 60) networkOutHistory.removeAt(0)
                }
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

        // CPU Chart
        if (cpuHistory.isNotEmpty()) {
            item {
                DashboardCard(
                    title = "CPU Usage Over Time",
                    icon = Icons.Default.Memory
                ) {
                    RealtimeChart(
                        title = "",
                        data = cpuHistory.toList(),
                        lineColor = Color(0xFF2196F3),
                        yAxisLabel = "Usage (%)"
                    )

                    // Current value
                    cpuHistory.lastOrNull()?.second?.let { currentCpu ->
                        Spacer(modifier = Modifier.height(8.dp))
                        MetricItem(
                            label = "Current CPU",
                            value = "${currentCpu.toInt()}%",
                            icon = Icons.Default.Speed,
                            valueColor = getCpuColor(currentCpu.toInt())
                        )
                    }
                }
            }
        }

        // RAM Chart
        if (ramHistory.isNotEmpty()) {
            item {
                DashboardCard(
                    title = "Memory Usage Over Time",
                    icon = Icons.Default.Storage
                ) {
                    RealtimeChart(
                        title = "",
                        data = ramHistory.toList(),
                        lineColor = Color(0xFF4CAF50),
                        yAxisLabel = "Usage (%)"
                    )

                    // Current value
                    ramHistory.lastOrNull()?.second?.let { currentRam ->
                        Spacer(modifier = Modifier.height(8.dp))
                        MetricItem(
                            label = "Current RAM",
                            value = "${currentRam.toInt()}%",
                            icon = Icons.Default.Memory,
                            valueColor = getRamColor(currentRam.toInt())
                        )
                    }
                }
            }
        }

        // Network Chart (Combined In/Out)
        if (networkInHistory.isNotEmpty() && networkOutHistory.isNotEmpty()) {
            item {
                DashboardCard(
                    title = "Network Activity",
                    icon = Icons.Default.NetworkCheck
                ) {
                    MultiLineChart(
                        title = "",
                        series = listOf(
                            ChartSeries(
                                name = "Download",
                                color = Color(0xFF9C27B0),
                                data = networkInHistory.toList()
                            ),
                            ChartSeries(
                                name = "Upload",
                                color = Color(0xFFFFC107),
                                data = networkOutHistory.toList()
                            )
                        ),
                        yAxisLabel = "Speed (KB/s)"
                    )

                    // Current values
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        networkInHistory.lastOrNull()?.second?.let { currentIn ->
                            Column {
                                MetricItem(
                                    label = "Download",
                                    value = formatNetworkSpeed(currentIn),
                                    icon = Icons.Default.Download,
                                    valueColor = Color(0xFF9C27B0)
                                )
                            }
                        }
                        networkOutHistory.lastOrNull()?.second?.let { currentOut ->
                            Column {
                                MetricItem(
                                    label = "Upload",
                                    value = formatNetworkSpeed(currentOut),
                                    icon = Icons.Default.Upload,
                                    valueColor = Color(0xFFFFC107)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Disk Chart
        if (diskHistory.isNotEmpty()) {
            item {
                DashboardCard(
                    title = "Disk Usage Over Time",
                    icon = Icons.Default.Sd
                ) {
                    RealtimeChart(
                        title = "",
                        data = diskHistory.toList(),
                        lineColor = Color(0xFFF44336),
                        yAxisLabel = "Usage (%)"
                    )

                    // Current value
                    diskHistory.lastOrNull()?.second?.let { currentDisk ->
                        Spacer(modifier = Modifier.height(8.dp))
                        MetricItem(
                            label = "Current Disk",
                            value = "${currentDisk.toInt()}%",
                            icon = Icons.Default.Sd,
                            valueColor = getDiskColor(currentDisk.toInt())
                        )
                    }
                }
            }
        }

        // Loading state
        if (cpuHistory.isEmpty() && connected) {
            item {
                DashboardLoadingState(
                    message = "Collecting metrics data..."
                )
            }
        }
    }
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

private fun getDiskColor(percentage: Int): Color = when {
    percentage >= 90 -> Color(0xFFF44336)
    percentage >= 80 -> Color(0xFFFFC107)
    else -> Color(0xFF4CAF50)
}

private fun formatNetworkSpeed(bytesPerSec: Double): String {
    val kb = bytesPerSec / 1024
    val mb = kb / 1024

    return when {
        mb >= 1 -> "%.2f MB/s".format(mb)
        kb >= 1 -> "%.1f KB/s".format(kb)
        else -> "%.0f B/s".format(bytesPerSec)
    }
}
