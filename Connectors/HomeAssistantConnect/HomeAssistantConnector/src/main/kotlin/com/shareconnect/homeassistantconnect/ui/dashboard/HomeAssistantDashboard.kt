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
    val connected by client.connected.collectAsState()
    val entities by client.entities.collectAsState()
    val availableServices by client.availableServices.collectAsState()

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
        val entitiesByDomain = entities.groupBy { it.entityId.substringBefore('.') }

        entitiesByDomain.forEach { (domain, domainEntities) ->
            item {
                DomainCard(
                    domain = domain,
                    entities = domainEntities
                )
            }
        }

        // Services Available
        if (availableServices.isNotEmpty()) {
            item {
                DashboardCard(
                    title = "Available Services",
                    icon = Icons.Default.Settings
                ) {
                    Text(
                        text = "${availableServices.size} services available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
    entities: List<StateChangedMessage.EntityState>,
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
    entity: StateChangedMessage.EntityState,
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
                text = entity.attributes["friendly_name"] ?: entity.entityId,
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

private fun buildQuickStats(entities: List<StateChangedMessage.EntityState>): List<QuickStat> {
    val stats = mutableListOf<QuickStat>()

    // Total entities
    stats.add(
        QuickStat(
            label = "Total Entities",
            value = entities.size.toString(),
            icon = Icons.Default.Home,
            iconColor = Color(0xFF2196F3)
        )
    )

    // Lights
    val lights = entities.filter { it.entityId.startsWith("light.") }
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
    val switches = entities.filter { it.entityId.startsWith("switch.") }
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
    val sensors = entities.filter { it.entityId.startsWith("sensor.") }
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

private fun getEntityStatus(entity: StateChangedMessage.EntityState): DashboardStatus {
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

private fun getEntityStateColor(entity: StateChangedMessage.EntityState): Color {
    return when (entity.state.lowercase()) {
        "on", "home", "open", "unlocked", "playing" -> Color(0xFF4CAF50)
        "off", "away", "closed", "locked", "paused" -> Color(0xFF9E9E9E)
        "unavailable", "unknown" -> Color(0xFFF44336)
        else -> Color(0xFF2196F3)
    }
}
