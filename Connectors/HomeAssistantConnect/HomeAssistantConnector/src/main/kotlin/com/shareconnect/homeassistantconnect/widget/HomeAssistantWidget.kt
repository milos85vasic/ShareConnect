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


package com.shareconnect.homeassistantconnect.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.shareconnect.homeassistantconnect.R

/**
 * Home Assistant Widget
 * Displays entity status summary (lights, switches, sensors)
 */
class HomeAssistantWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Load widget data
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
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Home Assistant",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.onSurface
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Connection Status
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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
                        )
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                Text(
                    text = if (data.isConnected) "Connected" else "Disconnected",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = if (data.isConnected) {
                            ColorProvider(android.graphics.Color.parseColor("#4CAF50"))
                        } else {
                            ColorProvider(android.graphics.Color.parseColor("#F44336"))
                        }
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Entity Stats
            EntityStatRow(
                label = "Lights",
                on = data.lightsOn,
                total = data.totalLights,
                color = ColorProvider(android.graphics.Color.parseColor("#FFC107"))
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            EntityStatRow(
                label = "Switches",
                on = data.switchesOn,
                total = data.totalSwitches,
                color = ColorProvider(android.graphics.Color.parseColor("#4CAF50"))
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            EntityStatRow(
                label = "Sensors",
                on = null,
                total = data.totalSensors,
                color = ColorProvider(android.graphics.Color.parseColor("#9C27B0"))
            )

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Last Update
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
    private fun EntityStatRow(
        label: String,
        on: Int?,
        total: Int,
        color: ColorProvider
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = GlanceModifier
                    .size(12.dp)
                    .background(color)
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = GlanceTheme.colors.onSurface
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            Text(
                text = if (on != null) "$on/$total" else total.toString(),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = GlanceTheme.colors.onSurface
                )
            )
        }
    }

    private suspend fun loadWidgetData(context: Context): WidgetData {
        // In a real implementation, this would load data from:
        // - HomeAssistantWebSocketClient
        // - Local database cache
        // - SharedPreferences

        // For now, return mock data
        return WidgetData(
            isConnected = true,
            lightsOn = 3,
            totalLights = 8,
            switchesOn = 2,
            totalSwitches = 4,
            totalSensors = 12,
            lastUpdate = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date())
        )
    }
}

/**
 * Widget data model
 */
data class WidgetData(
    val isConnected: Boolean,
    val lightsOn: Int,
    val totalLights: Int,
    val switchesOn: Int,
    val totalSwitches: Int,
    val totalSensors: Int,
    val lastUpdate: String
)
