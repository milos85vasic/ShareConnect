package com.shareconnect.plexconnect.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shareconnect.plexconnect.cache.EmbeddingCacheManager
import com.shareconnect.plexconnect.nlp.NlpModelManager
import com.shareconnect.plexconnect.monitoring.NlpPerformanceMonitor
import com.shareconnect.plexconnect.ui.viewmodels.NlpPerformanceDashboardViewModel
import com.shareconnect.plexconnect.ui.viewmodels.NlpPerformanceDashboardViewModelFactory
import org.koin.androidx.compose.koinViewModel
import kotlin.math.max
import kotlin.math.min

// Extension function to convert UI metrics to monitor metrics
private fun com.shareconnect.plexconnect.ui.viewmodels.NlpPerformanceMetrics.toMonitorMetrics(): NlpPerformanceMonitor.NlpPerformanceMetrics {
    return NlpPerformanceMonitor.NlpPerformanceMetrics(
        totalEmbeddings = this.totalRequests.toLong(),
        totalSimilarities = 0L, // Not tracked in UI metrics
        totalRecommendations = 0L, // Not tracked in UI metrics
        totalErrors = 0L, // Use 0 since errorCount doesn't exist in UI metrics
        averageEmbeddingTimeMs = this.averageInferenceTime.toLong(),
        averageSimilarityTimeMs = 0L, // Not tracked
        averageRecommendationTimeMs = 0L // Not tracked
    )
}

/**
 * Performance dashboard for NLP system
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NlpPerformanceDashboardScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NlpPerformanceDashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cacheStats by viewModel.cacheStatistics.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.startMonitoring()
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top Bar
        TopAppBar(
            title = { Text("NLP Performance") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(
                    onClick = { viewModel.refreshMetrics() },
                    enabled = !uiState.isLoading
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = if (uiState.isLoading) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        )
        
        // Main Content
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading performance metrics...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.error!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.refreshMetrics() }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Overview Section
                    item {
                        OverviewSection(
                            metrics = uiState.metrics.toMonitorMetrics(),
                            isMonitoring = uiState.isMonitoring
                        )
                    }
                    
                    // Performance Metrics
                    item {
                        PerformanceMetricsSection(
                            metrics = uiState.metrics.toMonitorMetrics()
                        )
                    }
                    
                    // Cache Statistics
                    item {
                        CacheStatisticsSection(
                            cacheStats = cacheStats
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewSection(
    metrics: NlpPerformanceMonitor.NlpPerformanceMetrics,
    isMonitoring: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Performance Overview",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isMonitoring) "Monitoring" else "Idle",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isMonitoring) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                
                Column {
                    Text(
                        text = "Total Requests",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${metrics.totalEmbeddings}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Column {
                    Text(
                        text = "Cache Hit Rate",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${String.format("%.1f", (metrics.cacheStats.values.firstOrNull()?.hitRate ?: 0f) * 100)}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = if ((metrics.cacheStats.values.firstOrNull()?.hitRate ?: 0f) > 0.7f) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PerformanceMetricsSection(
    metrics: NlpPerformanceMonitor.NlpPerformanceMetrics
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Performance Metrics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Inference Time
            PerformanceMetricRow(
                label = "Average Inference Time",
                value = "${metrics.averageEmbeddingTimeMs}ms"
            )
            
            // Memory Usage
            PerformanceMetricRow(
                label = "Memory Usage",
                value = "${metrics.memoryStats.values.firstOrNull()?.usedMemoryMB ?: 0L}MB"
            )
            
            // Error Rate
            val errorRate = if (metrics.totalEmbeddings > 0) {
                metrics.totalErrors.toFloat() / metrics.totalEmbeddings
            } else 0f
            PerformanceMetricRow(
                label = "Error Rate",
                value = "${String.format("%.2f", errorRate * 100)}%",
                color = if (errorRate < 0.01f) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

@Composable
private fun PerformanceMetricRow(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}

@Composable
private fun CacheStatisticsSection(
    cacheStats: Map<String, Any>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Cache Statistics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Simplified display of cache stats
            cacheStats.entries.forEach { (key, value) ->
                PerformanceMetricRow(
                    label = key,
                    value = value.toString()
                )
            }
        }
    }
}

@Composable
private fun PerformanceChart(
    data: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val maxValue = data.maxOrNull() ?: 0f
        val minValue = data.minOrNull() ?: 0f
        val range = if (maxValue - minValue > 0.1f) maxValue - minValue else 0.1f
        
        // Draw grid lines
        for (i in 0..4) {
            val y = canvasHeight * i / 4
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        
        // Draw data line
        if (data.isNotEmpty()) {
            val stepX = canvasWidth / (data.size - 1)
            val points = data.mapIndexed { index, value ->
                val x = index * stepX
                val y = canvasHeight - (value - minValue) / range * canvasHeight
                Offset(x, y)
            }
            
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = color,
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 2.dp.toPx()
                )
            }
            
            // Draw points
            points.forEach { point ->
                drawCircle(
                    color = color,
                    radius = 4.dp.toPx(),
                    center = point
                )
            }
        }
    }
}