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
import com.shareconnect.jdownloaderconnect.domain.model.LinkAvailability

@Composable
fun LinkGrabberPackageCard(
    linkGrabberPackage: com.shareconnect.jdownloaderconnect.domain.model.LinkGrabberPackage,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onMoveToDownloads: () -> Unit
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
                    text = linkGrabberPackage.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = { showActions = !showActions }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                }
            }
            
            // Link Statistics
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${linkGrabberPackage.links.size} links",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${linkGrabberPackage.hosterCount} hosters",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Text(
                    text = formatBytes(linkGrabberPackage.bytesTotal),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Actions Menu
            if (showActions) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onMoveToDownloads()
                            showActions = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "To Downloads", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("To Downloads", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
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