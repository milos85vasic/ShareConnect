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


package com.shareconnect.designsystem.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Connector Notification Manager
 * Centralized notification system for all ShareConnect connectors
 */
class ConnectorNotificationManager(private val context: Context) {

    companion object {
        // Channel IDs
        private const val CHANNEL_ALARMS = "connector_alarms"
        private const val CHANNEL_EVENTS = "connector_events"
        private const val CHANNEL_STATUS = "connector_status"
        private const val CHANNEL_UPDATES = "connector_updates"

        // Notification IDs base
        private const val NOTIFICATION_ID_ALARM = 1000
        private const val NOTIFICATION_ID_EVENT = 2000
        private const val NOTIFICATION_ID_STATUS = 3000
        private const val NOTIFICATION_ID_UPDATE = 4000
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannels()
    }

    /**
     * Create notification channels (Android 8.0+)
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ALARMS,
                    "Alarms & Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Critical alarms and alerts from connected services"
                    enableVibration(true)
                    enableLights(true)
                },
                NotificationChannel(
                    CHANNEL_EVENTS,
                    "Events",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Events and state changes from connected services"
                },
                NotificationChannel(
                    CHANNEL_STATUS,
                    "Connection Status",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Connection status updates"
                },
                NotificationChannel(
                    CHANNEL_UPDATES,
                    "Widget Updates",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Widget and data update notifications"
                }
            )

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            channels.forEach { manager.createNotificationChannel(it) }
        }
    }

    /**
     * Show critical alarm notification
     */
    fun showAlarmNotification(
        title: String,
        message: String,
        connectorName: String,
        actionIntent: Intent? = null
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ALARMS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .apply {
                actionIntent?.let {
                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        it,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    setContentIntent(pendingIntent)
                }
                setSubText(connectorName)
            }
            .build()

        notificationManager.notify(NOTIFICATION_ID_ALARM + connectorName.hashCode(), notification)
    }

    /**
     * Show event notification
     */
    fun showEventNotification(
        title: String,
        message: String,
        connectorName: String,
        icon: Int = android.R.drawable.ic_dialog_info,
        actionIntent: Intent? = null
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_EVENTS)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setAutoCancel(true)
            .apply {
                actionIntent?.let {
                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        it,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    setContentIntent(pendingIntent)
                }
                setSubText(connectorName)
            }
            .build()

        notificationManager.notify(NOTIFICATION_ID_EVENT + message.hashCode(), notification)
    }

    /**
     * Show connection status notification
     */
    fun showStatusNotification(
        title: String,
        message: String,
        connectorName: String,
        isConnected: Boolean
    ) {
        val icon = if (isConnected) {
            android.R.drawable.presence_online
        } else {
            android.R.drawable.presence_offline
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_STATUS)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .setSubText(connectorName)
            .build()

        notificationManager.notify(NOTIFICATION_ID_STATUS + connectorName.hashCode(), notification)
    }

    /**
     * Show grouped notifications (multiple items)
     */
    fun showGroupedNotifications(
        groupKey: String,
        summaryTitle: String,
        summaryText: String,
        notifications: List<NotificationData>
    ) {
        // Create individual notifications
        notifications.forEach { data ->
            val notification = NotificationCompat.Builder(context, CHANNEL_EVENTS)
                .setSmallIcon(data.icon)
                .setContentTitle(data.title)
                .setContentText(data.message)
                .setGroup(groupKey)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(data.id, notification)
        }

        // Create summary notification
        val summaryNotification = NotificationCompat.Builder(context, CHANNEL_EVENTS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(summaryTitle)
            .setContentText(summaryText)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setSummaryText(summaryText)
                    .also { style ->
                        notifications.forEach { data ->
                            style.addLine("${data.title}: ${data.message}")
                        }
                    }
            )
            .setGroup(groupKey)
            .setGroupSummary(true)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(groupKey.hashCode(), summaryNotification)
    }

    /**
     * Cancel notification by ID
     */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    /**
     * Cancel all notifications for a connector
     */
    fun cancelAllForConnector(connectorName: String) {
        val baseIds = listOf(
            NOTIFICATION_ID_ALARM,
            NOTIFICATION_ID_EVENT,
            NOTIFICATION_ID_STATUS,
            NOTIFICATION_ID_UPDATE
        )
        baseIds.forEach { baseId ->
            notificationManager.cancel(baseId + connectorName.hashCode())
        }
    }

    /**
     * Cancel all notifications
     */
    fun cancelAll() {
        notificationManager.cancelAll()
    }
}

/**
 * Notification data model
 */
data class NotificationData(
    val id: Int,
    val title: String,
    val message: String,
    val icon: Int = android.R.drawable.ic_dialog_info
)

/**
 * Notification builder extensions
 */

/**
 * Build alarm notification for Netdata
 */
fun ConnectorNotificationManager.showNetdataAlarm(
    alarmName: String,
    severity: String,
    value: String,
    threshold: String
) {
    val title = when (severity.uppercase()) {
        "CRITICAL" -> "🔴 Critical Alarm: $alarmName"
        "WARNING" -> "⚠️ Warning: $alarmName"
        else -> "Alarm: $alarmName"
    }

    val message = "Value: $value (Threshold: $threshold)"

    showAlarmNotification(
        title = title,
        message = message,
        connectorName = "Netdata"
    )
}

/**
 * Build event notification for Portainer
 */
fun ConnectorNotificationManager.showPortainerEvent(
    eventType: String,
    containerName: String,
    image: String? = null
) {
    val title = when (eventType.lowercase()) {
        "start" -> "Container Started"
        "stop" -> "Container Stopped"
        "die" -> "Container Died"
        "destroy" -> "Container Destroyed"
        else -> "Container Event: $eventType"
    }

    val message = buildString {
        append(containerName)
        image?.let { append(" ($it)") }
    }

    showEventNotification(
        title = title,
        message = message,
        connectorName = "Portainer"
    )
}

/**
 * Build now playing notification for Jellyfin
 */
fun ConnectorNotificationManager.showJellyfinNowPlaying(
    title: String,
    seriesName: String? = null,
    userName: String
) {
    val notificationTitle = "Now Playing"
    val message = buildString {
        append(title)
        seriesName?.let { append(" - $it") }
        append(" (User: $userName)")
    }

    showEventNotification(
        title = notificationTitle,
        message = message,
        connectorName = "Jellyfin"
    )
}

/**
 * Build state change notification for HomeAssistant
 */
fun ConnectorNotificationManager.showHomeAssistantStateChange(
    entityName: String,
    oldState: String,
    newState: String
) {
    val title = "State Changed: $entityName"
    val message = "$oldState → $newState"

    showEventNotification(
        title = title,
        message = message,
        connectorName = "Home Assistant"
    )
}
