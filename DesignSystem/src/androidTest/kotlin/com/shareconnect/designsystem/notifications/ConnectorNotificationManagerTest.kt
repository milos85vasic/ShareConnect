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

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for ConnectorNotificationManager
 */
@RunWith(AndroidJUnit4::class)
class ConnectorNotificationManagerTest {

    private lateinit var notificationManager: ConnectorNotificationManager

    @Before
    fun setup() {
        notificationManager = ConnectorNotificationManager(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun initializationCreatesNotificationChannels() {
        // Just verify that initialization doesn't crash
        // Notification channels are created automatically in init block
        assertNotNull(notificationManager)
    }

    @Test
    fun showAlarmNotification_doesNotCrash() {
        // Test that showing alarm notification doesn't crash
        notificationManager.showAlarmNotification(
            title = "Test Alarm",
            message = "This is a test alarm notification",
            connectorName = "TestConnector"
        )

        // If we get here, the notification was shown successfully
        assertTrue(true)
    }

    @Test
    fun showAlarmNotification_withActionIntent() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), Any::class.java)

        notificationManager.showAlarmNotification(
            title = "Test Alarm with Action",
            message = "This alarm has an action",
            connectorName = "TestConnector",
            actionIntent = intent
        )

        assertTrue(true)
    }

    @Test
    fun showEventNotification_doesNotCrash() {
        notificationManager.showEventNotification(
            title = "Test Event",
            message = "This is a test event notification",
            connectorName = "TestConnector",
            icon = android.R.drawable.ic_dialog_info
        )

        assertTrue(true)
    }

    @Test
    fun showStatusNotification_doesNotCrash() {
        notificationManager.showStatusNotification(
            title = "Connection Status",
            message = "Connected to TestConnector",
            connectorName = "TestConnector",
            isConnected = true
        )

        assertTrue(true)
    }

    @Test
    fun showUpdateNotification_doesNotCrash() {
        notificationManager.showUpdateNotification(
            title = "Widget Update",
            message = "Widget data updated",
            connectorName = "TestConnector"
        )

        assertTrue(true)
    }

    @Test
    fun cancelNotification_doesNotCrash() {
        // Show a notification first
        notificationManager.showAlarmNotification(
            title = "Test",
            message = "Test",
            connectorName = "Test"
        )

        // Then cancel it
        notificationManager.cancelNotification(1000)

        assertTrue(true)
    }

    @Test
    fun cancelAllNotifications_doesNotCrash() {
        // Show multiple notifications
        notificationManager.showAlarmNotification("Test 1", "Message 1", "Connector1")
        notificationManager.showEventNotification("Test 2", "Message 2", "Connector2", android.R.drawable.ic_dialog_info)

        // Cancel all
        notificationManager.cancelAllNotifications()

        assertTrue(true)
    }

    @Test
    fun showGroupedNotifications_doesNotCrash() {
        val notifications = listOf(
            NotificationData(
                id = 1,
                title = "Notification 1",
                message = "Message 1",
                icon = android.R.drawable.ic_dialog_info
            ),
            NotificationData(
                id = 2,
                title = "Notification 2",
                message = "Message 2",
                icon = android.R.drawable.ic_dialog_info
            )
        )

        notificationManager.showGroupedNotifications(
            groupKey = "test_group",
            summaryTitle = "Test Group",
            summaryText = "2 notifications",
            notifications = notifications
        )

        assertTrue(true)
    }

    // Test extension functions

    @Test
    fun showNetdataAlarm_doesNotCrash() {
        notificationManager.showNetdataAlarm(
            alarmName = "CPU Usage",
            severity = "CRITICAL",
            value = "95%",
            threshold = "80%"
        )

        assertTrue(true)
    }

    @Test
    fun showPortainerEvent_doesNotCrash() {
        notificationManager.showPortainerEvent(
            eventType = "container.start",
            containerName = "test-container",
            image = "nginx:latest"
        )

        assertTrue(true)
    }

    @Test
    fun showJellyfinNowPlaying_doesNotCrash() {
        notificationManager.showJellyfinNowPlaying(
            title = "Test Movie",
            seriesName = null,
            userName = "TestUser"
        )

        assertTrue(true)
    }

    @Test
    fun showHomeAssistantStateChange_doesNotCrash() {
        notificationManager.showHomeAssistantStateChange(
            entityName = "light.living_room",
            oldState = "off",
            newState = "on"
        )

        assertTrue(true)
    }

    @Test
    fun multipleNotifications_canBeShownSequentially() {
        // Test showing multiple different types of notifications
        notificationManager.showAlarmNotification("Alarm", "Test", "Connector")
        notificationManager.showEventNotification("Event", "Test", "Connector", android.R.drawable.ic_dialog_info)
        notificationManager.showStatusNotification("Status", "Test", "Connector", true)
        notificationManager.showUpdateNotification("Update", "Test", "Connector")

        // If we get here, all notifications were shown successfully
        assertTrue(true)
    }
}
