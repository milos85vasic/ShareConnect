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


package com.shareconnect.netdataconnect.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

/**
 * Netdata Widget
 * Displays system health and resource metrics
 */
class NetdataWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetData = loadWidgetData(context)

        provideContent {
            GlanceTheme {
                WidgetContent(widgetData)
            }
        }
    }

    @Composable
    private fun WidgetContent(data: WidgetData) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Netdata",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onSurface
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Health Status
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(8.dp)
                        .background(getHealthColor(data.healthStatus))
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                Text(
                    text = data.healthStatus,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = getHealthColor(data.healthStatus)
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Metrics
            MetricRow("CPU", data.cpuUsage, getMetricColor(data.cpuUsage))
            Spacer(modifier = GlanceModifier.height(6.dp))

            MetricRow("RAM", data.ramUsage, getMetricColor(data.ramUsage))
            Spacer(modifier = GlanceModifier.height(6.dp))

            MetricRow("Disk", data.diskUsage, getMetricColor(data.diskUsage))

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Alarms
            if (data.criticalAlarms > 0 || data.warningAlarms > 0) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    if (data.criticalAlarms > 0) {
                        AlarmBadge("Critical", data.criticalAlarms, true)
                        Spacer(modifier = GlanceModifier.width(8.dp))
                    }
                    if (data.warningAlarms > 0) {
                        AlarmBadge("Warning", data.warningAlarms, false)
                    }
                }
                Spacer(modifier = GlanceModifier.height(8.dp))
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            Text(
                text = "Updated: ${data.lastUpdate}",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = GlanceTheme.colors.onSurface
                )
            )
        }
    }

    @Composable
    private fun MetricRow(label: String, value: Int, color: ColorProvider) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onSurface
                ),
                modifier = GlanceModifier.width(50.dp)
            )

            // Progress bar background
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .height(8.dp)
                    .background(GlanceTheme.colors.surfaceVariant)
            ) {
                // Progress bar fill
                Box(
                    modifier = GlanceModifier
                        .fillMaxHeight()
                        .width((value * 1.5).dp) // Approximate percentage
                        .background(color)
                )
            }

            Spacer(modifier = GlanceModifier.width(8.dp))

            Text(
                text = "$value%",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = color
                ),
                modifier = GlanceModifier.width(35.dp)
            )
        }
    }

    @Composable
    private fun AlarmBadge(label: String, count: Int, isCritical: Boolean) {
        Row(
            modifier = GlanceModifier
                .background(
                    if (isCritical) {
                        ColorProvider(android.graphics.Color.parseColor("#FFCDD2"))
                    } else {
                        ColorProvider(android.graphics.Color.parseColor("#FFF9C4"))
                    }
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$count $label",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isCritical) {
                        ColorProvider(android.graphics.Color.parseColor("#D32F2F"))
                    } else {
                        ColorProvider(android.graphics.Color.parseColor("#F57F17"))
                    }
                )
            )
        }
    }

    private fun getHealthColor(status: String): ColorProvider {
        return when (status) {
            "Healthy" -> ColorProvider(android.graphics.Color.parseColor("#4CAF50"))
            "Warning" -> ColorProvider(android.graphics.Color.parseColor("#FFC107"))
            "Critical" -> ColorProvider(android.graphics.Color.parseColor("#F44336"))
            else -> ColorProvider(android.graphics.Color.parseColor("#9E9E9E"))
        }
    }

    private fun getMetricColor(percentage: Int): ColorProvider {
        return when {
            percentage >= 90 -> ColorProvider(android.graphics.Color.parseColor("#F44336"))
            percentage >= 70 -> ColorProvider(android.graphics.Color.parseColor("#FFC107"))
            else -> ColorProvider(android.graphics.Color.parseColor("#4CAF50"))
        }
    }

    private suspend fun loadWidgetData(context: Context): WidgetData {
        // Mock data - production would fetch from NetdataRealtimeClient
        return WidgetData(
            healthStatus = "Healthy",
            cpuUsage = 45,
            ramUsage = 68,
            diskUsage = 52,
            criticalAlarms = 0,
            warningAlarms = 0,
            lastUpdate = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date())
        )
    }
}

data class WidgetData(
    val healthStatus: String,
    val cpuUsage: Int,
    val ramUsage: Int,
    val diskUsage: Int,
    val criticalAlarms: Int,
    val warningAlarms: Int,
    val lastUpdate: String
)
