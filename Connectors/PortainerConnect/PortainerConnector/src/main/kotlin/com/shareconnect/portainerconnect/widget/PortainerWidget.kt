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


package com.shareconnect.portainerconnect.widget

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
 * Portainer Widget
 * Displays Docker container status and recent events
 */
class PortainerWidget : GlanceAppWidget() {

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
                text = "Portainer",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onSurface
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Connection Status
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(8.dp)
                        .background(
                            if (data.isConnected) {
                                ColorProvider(android.graphics.Color.parseColor("#4CAF50"))
                            } else {
                                ColorProvider(android.graphics.Color.parseColor("#F44336"))
                            }
                        ),
                    content = {}
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                Text(
                    text = if (data.isConnected) "Connected" else "Disconnected",
                    style = TextStyle(fontSize = 12.sp)
                )
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Container Stats
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                ContainerStatBox(
                    label = "Running",
                    count = data.runningContainers,
                    color = ColorProvider(android.graphics.Color.parseColor("#4CAF50"))
                )
                Spacer(modifier = GlanceModifier.width(12.dp))
                ContainerStatBox(
                    label = "Stopped",
                    count = data.stoppedContainers,
                    color = ColorProvider(android.graphics.Color.parseColor("#9E9E9E"))
                )
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Images and Total
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    Box(
                        modifier = GlanceModifier
                            .size(10.dp)
                            .background(ColorProvider(android.graphics.Color.parseColor("#9C27B0"))),
                        content = {}
                    )
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = "Images: ${data.imagesCount}",
                        style = TextStyle(fontSize = 12.sp)
                    )
                }
                Text(
                    text = "Total: ${data.totalContainers}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Recent Events
            if (data.recentEvents.isNotEmpty()) {
                Text(
                    text = "Recent Events",
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.primary
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                data.recentEvents.take(2).forEach { event ->
                    EventRow(event)
                    Spacer(modifier = GlanceModifier.height(2.dp))
                }
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
    private fun ContainerStatBox(label: String, count: Int, color: ColorProvider) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 10.sp,
                    color = GlanceTheme.colors.onSurface
                )
            )
        }
    }

    @Composable
    private fun EventRow(event: ContainerEvent) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = GlanceModifier
                    .size(6.dp)
                    .background(getEventColor(event.type)),
                content = {}
            )
            Spacer(modifier = GlanceModifier.width(6.dp))
            Text(
                text = "${event.type}: ${event.containerName}",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = GlanceTheme.colors.onSurface
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            Text(
                text = event.time,
                style = TextStyle(
                    fontSize = 9.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
        }
    }

    private fun getEventColor(eventType: String): ColorProvider {
        return when (eventType.lowercase()) {
            "start" -> ColorProvider(android.graphics.Color.parseColor("#4CAF50"))
            "stop", "die" -> ColorProvider(android.graphics.Color.parseColor("#FFC107"))
            "destroy" -> ColorProvider(android.graphics.Color.parseColor("#F44336"))
            else -> ColorProvider(android.graphics.Color.parseColor("#2196F3"))
        }
    }

    private suspend fun loadWidgetData(context: Context): WidgetData {
        // Mock data - production would fetch from PortainerEventsClient
        return WidgetData(
            isConnected = true,
            runningContainers = 5,
            stoppedContainers = 2,
            imagesCount = 12,
            totalContainers = 7,
            recentEvents = listOf(
                ContainerEvent("start", "nginx", "1m"),
                ContainerEvent("stop", "redis", "5m")
            ),
            lastUpdate = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date())
        )
    }
}

data class WidgetData(
    val isConnected: Boolean,
    val runningContainers: Int,
    val stoppedContainers: Int,
    val imagesCount: Int,
    val totalContainers: Int,
    val recentEvents: List<ContainerEvent>,
    val lastUpdate: String
)

data class ContainerEvent(
    val type: String,
    val containerName: String,
    val time: String
)
