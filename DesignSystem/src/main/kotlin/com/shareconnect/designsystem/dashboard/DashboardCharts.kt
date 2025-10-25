package com.shareconnect.designsystem.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.shape.Shape

/**
 * Dashboard chart components using Vico
 * Provides time-series visualizations for metrics
 */

/**
 * Line chart for time-series data
 *
 * @param title Chart title
 * @param data List of data points (time, value)
 * @param modifier Modifier
 * @param lineColor Color of the line
 * @param showPoints Whether to show data points
 * @param yAxisLabel Label for Y axis
 * @param xAxisLabel Label for X axis
 */
@Composable
fun TimeSeriesLineChart(
    title: String,
    data: List<Pair<Long, Double>>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    showPoints: Boolean = true,
    yAxisLabel: String? = null,
    xAxisLabel: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Chart
        if (data.isNotEmpty()) {
            val modelProducer = remember { CartesianChartModelProducer() }

            // Update model with data
            modelProducer.runTransaction {
                lineSeries {
                    series(data.map { it.second })
                }
            }

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lines = listOf(
                            rememberLineComponent(
                                color = lineColor.toArgb(),
                                thickness = 2.dp,
                                shape = Shape.rounded(allPercent = 25)
                            )
                        )
                    ),
                    startAxis = rememberStartAxis(
                        title = yAxisLabel
                    ),
                    bottomAxis = rememberBottomAxis(
                        title = xAxisLabel
                    )
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        } else {
            Text(
                text = "No data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        }
    }
}

/**
 * Multi-line chart for comparing multiple metrics
 *
 * @param title Chart title
 * @param series List of series (name, color, data points)
 * @param modifier Modifier
 * @param yAxisLabel Label for Y axis
 * @param xAxisLabel Label for X axis
 */
@Composable
fun MultiLineChart(
    title: String,
    series: List<ChartSeries>,
    modifier: Modifier = Modifier,
    yAxisLabel: String? = null,
    xAxisLabel: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            series.forEach { chartSeries ->
                LegendItem(
                    label = chartSeries.name,
                    color = chartSeries.color
                )
            }
        }

        // Chart
        if (series.isNotEmpty() && series.all { it.data.isNotEmpty() }) {
            val modelProducer = remember { CartesianChartModelProducer() }

            // Update model with multiple series
            modelProducer.runTransaction {
                lineSeries {
                    series.forEach { chartSeries ->
                        series(chartSeries.data.map { it.second })
                    }
                }
            }

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lines = series.map { chartSeries ->
                            rememberLineComponent(
                                color = chartSeries.color.toArgb(),
                                thickness = 2.dp,
                                shape = Shape.rounded(allPercent = 25)
                            )
                        }
                    ),
                    startAxis = rememberStartAxis(
                        title = yAxisLabel
                    ),
                    bottomAxis = rememberBottomAxis(
                        title = xAxisLabel
                    )
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        } else {
            Text(
                text = "No data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        }
    }
}

/**
 * Area chart for showing filled regions
 *
 * @param title Chart title
 * @param data List of data points (time, value)
 * @param modifier Modifier
 * @param fillColor Fill color
 * @param lineColor Line color
 * @param yAxisLabel Label for Y axis
 * @param xAxisLabel Label for X axis
 */
@Composable
fun AreaChart(
    title: String,
    data: List<Pair<Long, Double>>,
    modifier: Modifier = Modifier,
    fillColor: Color = MaterialTheme.colorScheme.primaryContainer,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    yAxisLabel: String? = null,
    xAxisLabel: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Chart
        if (data.isNotEmpty()) {
            val modelProducer = remember { CartesianChartModelProducer() }

            // Update model with data
            modelProducer.runTransaction {
                lineSeries {
                    series(data.map { it.second })
                }
            }

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lines = listOf(
                            rememberLineComponent(
                                color = lineColor.toArgb(),
                                thickness = 2.dp,
                                shape = Shape.rounded(allPercent = 25)
                            )
                        )
                    ),
                    startAxis = rememberStartAxis(
                        title = yAxisLabel
                    ),
                    bottomAxis = rememberBottomAxis(
                        title = xAxisLabel
                    )
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        } else {
            Text(
                text = "No data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        }
    }
}

/**
 * Real-time chart with automatic scrolling
 * Optimized for streaming data
 *
 * @param title Chart title
 * @param data List of recent data points (max 60)
 * @param modifier Modifier
 * @param lineColor Line color
 * @param maxDataPoints Maximum number of data points to display
 * @param yAxisLabel Label for Y axis
 */
@Composable
fun RealtimeChart(
    title: String,
    data: List<Pair<Long, Double>>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    maxDataPoints: Int = 60,
    yAxisLabel: String? = null
) {
    val recentData = remember(data) {
        data.takeLast(maxDataPoints)
    }

    TimeSeriesLineChart(
        title = title,
        data = recentData,
        modifier = modifier,
        lineColor = lineColor,
        showPoints = false,
        yAxisLabel = yAxisLabel,
        xAxisLabel = "Time"
    )
}

/**
 * Sparkline chart - minimal inline chart
 *
 * @param data List of data points
 * @param modifier Modifier
 * @param lineColor Line color
 */
@Composable
fun SparklineChart(
    data: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    if (data.isNotEmpty()) {
        val modelProducer = remember { CartesianChartModelProducer() }

        // Update model with data
        modelProducer.runTransaction {
            lineSeries {
                series(data)
            }
        }

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lines = listOf(
                        rememberLineComponent(
                            color = lineColor.toArgb(),
                            thickness = 1.dp
                        )
                    )
                )
            ),
            modelProducer = modelProducer,
            modifier = modifier
                .fillMaxWidth()
                .height(40.dp)
        )
    }
}

/**
 * Legend item for multi-line charts
 */
@Composable
private fun LegendItem(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = androidx.compose.foundation.shape.CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Data classes

/**
 * Chart series data
 */
data class ChartSeries(
    val name: String,
    val color: Color,
    val data: List<Pair<Long, Double>>
)

/**
 * Chart data point
 */
data class ChartDataPoint(
    val timestamp: Long,
    val value: Double,
    val label: String? = null
)
