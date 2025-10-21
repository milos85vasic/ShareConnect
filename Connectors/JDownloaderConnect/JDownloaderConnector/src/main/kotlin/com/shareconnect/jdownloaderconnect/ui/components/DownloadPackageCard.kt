package com.shareconnect.jdownloaderconnect.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shareconnect.jdownloaderconnect.domain.model.DownloadStatus

@Composable
fun DownloadPackageCard(
    downloadPackage: com.shareconnect.jdownloaderconnect.domain.model.DownloadPackage,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onRemove: () -> Unit
) {
    var showActions by remember { mutableStateOf(false) }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                           else MaterialTheme.colorScheme.surface
        ),
        onClick = onSelect
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = downloadPackage.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = { showActions = !showActions }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                }
            }
            
            // Status and Progress
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(status = downloadPackage.status)
                
                Text(
                    text = formatBytes(downloadPackage.bytesLoaded) + " / " + formatBytes(downloadPackage.bytesTotal),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Progress Bar
            if (downloadPackage.bytesTotal > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                val progress = downloadPackage.bytesLoaded.toFloat() / downloadPackage.bytesTotal
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Speed and ETA
            if (downloadPackage.status == DownloadStatus.DOWNLOADING) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Speed: ${formatBytes(downloadPackage.speed)}/s",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "ETA: ${formatTime(downloadPackage.eta)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            // Actions Menu
            if (showActions) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (downloadPackage.status) {
                        DownloadStatus.DOWNLOADING -> {
                            Button(
                                onClick = {
                                    onStop()
                                    showActions = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Pause, contentDescription = "Stop", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Stop", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        DownloadStatus.PAUSED, DownloadStatus.QUEUED -> {
                            Button(
                                onClick = {
                                    onStart()
                                    showActions = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Start", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Start", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        else -> {}
                    }
                    
                    Button(
                        onClick = {
                            onRemove()
                            showActions = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Remove", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: DownloadStatus) {
    val (text, color) = when (status) {
        DownloadStatus.DOWNLOADING -> "Downloading" to MaterialTheme.colorScheme.primary
        DownloadStatus.QUEUED -> "Queued" to MaterialTheme.colorScheme.secondary
        DownloadStatus.PAUSED -> "Paused" to MaterialTheme.colorScheme.outline
        DownloadStatus.FINISHED -> "Finished" to MaterialTheme.colorScheme.tertiary
        DownloadStatus.FAILED -> "Failed" to MaterialTheme.colorScheme.error
        DownloadStatus.ABORTED -> "Aborted" to MaterialTheme.colorScheme.error
        DownloadStatus.SKIPPED -> "Skipped" to MaterialTheme.colorScheme.outline
        DownloadStatus.UNKNOWN -> "Unknown" to MaterialTheme.colorScheme.outline
    }
    
    AssistChip(
        onClick = {},
        label = { Text(text, style = MaterialTheme.typography.labelSmall) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.2f),
            labelColor = color
        )
    )
}

private fun formatBytes(bytes: Long): String {
    val units = listOf("B", "KB", "MB", "GB", "TB")
    var size = bytes.toDouble()
    var unitIndex = 0
    
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    
    return "%.2f %s".format(size, units[unitIndex])
}

private fun formatTime(seconds: Long): String {
    return when {
        seconds < 60 -> "${seconds}s"
        seconds < 3600 -> "${seconds / 60}m"
        else -> "${seconds / 3600}h ${(seconds % 3600) / 60}m"
    }
}