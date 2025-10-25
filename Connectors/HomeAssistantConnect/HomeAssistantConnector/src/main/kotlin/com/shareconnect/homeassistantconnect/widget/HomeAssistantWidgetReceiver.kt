package com.shareconnect.homeassistantconnect.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Home Assistant Widget Receiver
 * Handles widget lifecycle and updates
 */
class HomeAssistantWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HomeAssistantWidget()
}
