package com.shareconnect.jellyfinconnect.widget

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
 * Jellyfin Widget
 * Displays now playing and active sessions
 */
class JellyfinWidget : GlanceAppWidget() {

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
                text = "Jellyfin",
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
                        )
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                Text(
                    text = if (data.isConnected) "Connected" else "Disconnected",
                    style = TextStyle(fontSize = 12.sp)
                )
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Now Playing
            if (data.nowPlaying != null) {
                Text(
                    text = "Now Playing",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.primary
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = data.nowPlaying,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            } else {
                Text(
                    text = "No active playback",
                    style = TextStyle(fontSize = 12.sp)
                )
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Stats
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                StatItem("Sessions", data.activeSessions)
                Spacer(modifier = GlanceModifier.width(16.dp))
                StatItem("Playing", data.playing)
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            Text(
                text = "Updated: ${data.lastUpdate}",
                style = TextStyle(fontSize = 10.sp)
            )
        }
    }

    @Composable
    private fun StatItem(label: String, value: Int) {
        Column {
            Text(
                text = value.toString(),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.primary
                )
            )
            Text(
                text = label,
                style = TextStyle(fontSize = 10.sp)
            )
        }
    }

    private suspend fun loadWidgetData(context: Context): WidgetData {
        return WidgetData(
            isConnected = true,
            nowPlaying = "Breaking Bad S05E14",
            activeSessions = 2,
            playing = 1,
            lastUpdate = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date())
        )
    }
}

data class WidgetData(
    val isConnected: Boolean,
    val nowPlaying: String?,
    val activeSessions: Int,
    val playing: Int,
    val lastUpdate: String
)
