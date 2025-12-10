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


package com.shareconnect.homeassistantconnect.ui.dashboard

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
import com.shareconnect.homeassistantconnect.data.websocket.*
import kotlinx.coroutines.flow.StateFlow

/**
 * HomeAssistant Dashboard
 * Real-time dashboard showing entities, states, and connection status
 */
@Composable
fun HomeAssistantDashboard(
    client: HomeAssistantWebSocketClient,
    modifier: Modifier = Modifier
) {
    val authenticationState by client.authenticationState.collectAsState()
    val entities by client.entities.collectAsState()
    val connected = authenticationState is HomeAssistantWebSocketClient.AuthenticationState.Authenticated

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

        // Quick Stats
        item {
            DashboardCard(
                title = "Quick Stats",
                icon = Icons.Default.Dashboard
            ) {
                QuickStatsGrid(
                    stats = buildQuickStats(entities),
                    columns = 2
                )
            }
        }

        // Entities by Domain
        val entitiesByDomain = entities.values.groupBy { it.entityId.substringBefore('.') }

        entitiesByDomain.forEach { (domain, domainEntities) ->
            item {
                DomainCard(
                    domain = domain,
                    entities = domainEntities
                )
            }
        }

        // Empty State
        if (entities.isEmpty() && connected) {
            item {
                DashboardEmptyState(
                    icon = Icons.Default.Home,
                    title = "No Entities",
                    description = "No entities found in Home Assistant"
                )
            }
        }
    }
}

@Composable
private fun DomainCard(
    domain: String,
    entities: List<EventMessage.EntityState>,
    modifier: Modifier = Modifier
) {
    val icon = getDomainIcon(domain)
    val displayName = getDomainDisplayName(domain)

    DashboardCard(
        title = displayName,
        icon = icon,
        modifier = modifier,
        action = {
            Text(
                text = "${entities.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            entities.forEach { entity ->
                EntityItem(entity = entity)
            }
        }
    }
}

@Composable
private fun EntityItem(
    entity: EventMessage.EntityState,
    modifier: Modifier = Modifier
) {
    val status = getEntityStatus(entity)
    val stateColor = getEntityStateColor(entity)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entity.attributes?.get("friendly_name") as? String ?: entity.entityId,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = entity.entityId,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        StatusBadge(
            text = entity.state,
            status = status
        )
    }
}

private fun buildQuickStats(entities: Map<String, EventMessage.EntityState>): List<QuickStat> {
    val stats = mutableListOf<QuickStat>()
    val entityList = entities.values.toList()

    // Total entities
    stats.add(
        QuickStat(
            label = "Total Entities",
            value = entityList.size.toString(),
            icon = Icons.Default.Home,
            iconColor = Color(0xFF2196F3)
        )
    )

    // Lights
    val lights = entityList.filter { it.entityId.startsWith("light.") }
    val lightsOn = lights.count { it.state == "on" }
    stats.add(
        QuickStat(
            label = "Lights On",
            value = "$lightsOn/${lights.size}",
            icon = Icons.Default.Lightbulb,
            iconColor = Color(0xFFFFC107)
        )
    )

    // Switches
    val switches = entityList.filter { it.entityId.startsWith("switch.") }
    val switchesOn = switches.count { it.state == "on" }
    stats.add(
        QuickStat(
            label = "Switches On",
            value = "$switchesOn/${switches.size}",
            icon = Icons.Default.ToggleOn,
            iconColor = Color(0xFF4CAF50)
        )
    )

    // Sensors
    val sensors = entityList.filter { it.entityId.startsWith("sensor.") }
    stats.add(
        QuickStat(
            label = "Sensors",
            value = sensors.size.toString(),
            icon = Icons.Default.Sensors,
            iconColor = Color(0xFF9C27B0)
        )
    )

    return stats
}

private fun getDomainIcon(domain: String) = when (domain) {
    "light" -> Icons.Default.Lightbulb
    "switch" -> Icons.Default.ToggleOn
    "sensor" -> Icons.Default.Sensors
    "binary_sensor" -> Icons.Default.Sensors
    "climate" -> Icons.Default.Thermostat
    "cover" -> Icons.Default.Window
    "lock" -> Icons.Default.Lock
    "media_player" -> Icons.Default.PlayArrow
    "camera" -> Icons.Default.Videocam
    "automation" -> Icons.Default.Autorenew
    "script" -> Icons.Default.Code
    "scene" -> Icons.Default.Lightbulb
    else -> Icons.Default.DeviceHub
}

private fun getDomainDisplayName(domain: String) = when (domain) {
    "light" -> "Lights"
    "switch" -> "Switches"
    "sensor" -> "Sensors"
    "binary_sensor" -> "Binary Sensors"
    "climate" -> "Climate"
    "cover" -> "Covers"
    "lock" -> "Locks"
    "media_player" -> "Media Players"
    "camera" -> "Cameras"
    "automation" -> "Automations"
    "script" -> "Scripts"
    "scene" -> "Scenes"
    else -> domain.replaceFirstChar { it.uppercase() }
}

private fun getEntityStatus(entity: EventMessage.EntityState): DashboardStatus {
    return when (entity.state.lowercase()) {
        "on", "home", "open", "unlocked", "playing", "active" -> DashboardStatus.SUCCESS
        "off", "away", "closed", "locked", "paused", "idle" -> DashboardStatus.NEUTRAL
        "unavailable", "unknown" -> DashboardStatus.ERROR
        "warning" -> DashboardStatus.WARNING
        else -> {
            // Check for numeric values (sensors)
            entity.state.toDoubleOrNull()?.let {
                DashboardStatus.INFO
            } ?: DashboardStatus.NEUTRAL
        }
    }
}

private fun getEntityStateColor(entity: EventMessage.EntityState): Color {
    return when (entity.state.lowercase()) {
        "on", "home", "open", "unlocked", "playing" -> Color(0xFF4CAF50)
        "off", "away", "closed", "locked", "paused" -> Color(0xFF9E9E9E)
        "unavailable", "unknown" -> Color(0xFFF44336)
        else -> Color(0xFF2196F3)
    }
}
