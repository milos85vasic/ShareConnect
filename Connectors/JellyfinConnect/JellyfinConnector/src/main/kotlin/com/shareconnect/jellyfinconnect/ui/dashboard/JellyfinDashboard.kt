package com.shareconnect.jellyfinconnect.ui.dashboard

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
import com.shareconnect.jellyfinconnect.data.websocket.*
import kotlinx.coroutines.flow.StateFlow

/**
 * Jellyfin Dashboard
 * Real-time dashboard showing now playing, active sessions, and library stats
 */
@Composable
fun JellyfinDashboard(
    client: JellyfinWebSocketClient,
    modifier: Modifier = Modifier
) {
    val connected by client.connected.collectAsState()
    val sessions by client.sessions.collectAsState()
    val nowPlaying by client.nowPlaying.collectAsState()

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

        // Now Playing
        nowPlaying?.let { playing ->
            item {
                NowPlayingCard(nowPlaying = playing)
            }
        }

        // Quick Stats
        item {
            DashboardCard(
                title = "Quick Stats",
                icon = Icons.Default.Dashboard
            ) {
                QuickStatsGrid(
                    stats = buildQuickStats(sessions),
                    columns = 2
                )
            }
        }

        // Active Sessions
        if (sessions.isNotEmpty()) {
            item {
                DashboardCard(
                    title = "Active Sessions",
                    icon = Icons.Default.Group,
                    action = {
                        Text(
                            text = "${sessions.size}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        sessions.forEach { session ->
                            SessionItem(session = session)
                        }
                    }
                }
            }
        }

        // Empty State
        if (sessions.isEmpty() && connected) {
            item {
                DashboardEmptyState(
                    icon = Icons.Default.PlayCircle,
                    title = "No Active Sessions",
                    description = "No one is currently watching or listening"
                )
            }
        }
    }
}

@Composable
private fun NowPlayingCard(
    nowPlaying: SessionsMessage.NowPlayingItem,
    modifier: Modifier = Modifier
) {
    DashboardCard(
        title = "Now Playing",
        icon = Icons.Default.PlayCircle,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Title
            Text(
                text = nowPlaying.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            // Type
            StatusBadge(
                text = nowPlaying.type,
                status = when (nowPlaying.type.lowercase()) {
                    "movie" -> DashboardStatus.INFO
                    "episode" -> DashboardStatus.SUCCESS
                    "audio" -> DashboardStatus.WARNING
                    else -> DashboardStatus.NEUTRAL
                }
            )

            // Series info (if episode)
            nowPlaying.seriesName?.let { series ->
                InfoRow(
                    icon = Icons.Default.Tv,
                    label = "Series",
                    value = series
                )
            }

            // Season/Episode info
            if (nowPlaying.parentIndexNumber != null && nowPlaying.indexNumber != null) {
                InfoRow(
                    icon = Icons.Default.Numbers,
                    label = "Episode",
                    value = "S${nowPlaying.parentIndexNumber}E${nowPlaying.indexNumber}"
                )
            }

            // Year
            nowPlaying.productionYear?.let { year ->
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Year",
                    value = year.toString()
                )
            }

            // Runtime
            nowPlaying.runTimeTicks?.let { ticks ->
                val minutes = (ticks / 10000000 / 60).toInt()
                InfoRow(
                    icon = Icons.Default.Timer,
                    label = "Runtime",
                    value = formatDuration(minutes)
                )
            }
        }
    }
}

@Composable
private fun SessionItem(
    session: SessionsMessage.SessionInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // User
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = session.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            }

            // Now Playing
            session.nowPlayingItem?.let { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadge(
                        text = item.type,
                        status = DashboardStatus.INFO
                    )
                }
            }

            // Play State
            session.playState?.let { playState ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Play status
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (playState.isPaused) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (playState.isPaused) Color(0xFFFFC107) else Color(0xFF4CAF50)
                        )
                        Text(
                            text = if (playState.isPaused) "Paused" else "Playing",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Progress
                    playState.positionTicks?.let { position ->
                        session.nowPlayingItem?.runTimeTicks?.let { total ->
                            val progress = (position.toFloat() / total.toFloat() * 100).toInt()
                            Text(
                                text = "$progress%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Progress Bar
                session.nowPlayingItem?.runTimeTicks?.let { total ->
                    playState.positionTicks?.let { position ->
                        val progress = position.toFloat() / total.toFloat()
                        LabeledProgressIndicator(
                            label = "Progress",
                            progress = progress,
                            showPercentage = false
                        )
                    }
                }
            }
        }
    }
}

private fun buildQuickStats(sessions: List<SessionsMessage.SessionInfo>): List<QuickStat> {
    val stats = mutableListOf<QuickStat>()

    // Active Sessions
    stats.add(
        QuickStat(
            label = "Active Sessions",
            value = sessions.size.toString(),
            icon = Icons.Default.Group,
            iconColor = Color(0xFF2196F3)
        )
    )

    // Playing count
    val playing = sessions.count { session ->
        session.playState?.isPaused == false && session.nowPlayingItem != null
    }
    stats.add(
        QuickStat(
            label = "Playing Now",
            value = playing.toString(),
            icon = Icons.Default.PlayArrow,
            iconColor = Color(0xFF4CAF50)
        )
    )

    // Movies being watched
    val movies = sessions.count { it.nowPlayingItem?.type == "Movie" }
    stats.add(
        QuickStat(
            label = "Movies",
            value = movies.toString(),
            icon = Icons.Default.Movie,
            iconColor = Color(0xFFF44336)
        )
    )

    // Episodes being watched
    val episodes = sessions.count { it.nowPlayingItem?.type == "Episode" }
    stats.add(
        QuickStat(
            label = "Episodes",
            value = episodes.toString(),
            icon = Icons.Default.Tv,
            iconColor = Color(0xFF9C27B0)
        )
    )

    return stats
}

private fun formatDuration(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) {
        "${hours}h ${mins}m"
    } else {
        "${mins}m"
    }
}
