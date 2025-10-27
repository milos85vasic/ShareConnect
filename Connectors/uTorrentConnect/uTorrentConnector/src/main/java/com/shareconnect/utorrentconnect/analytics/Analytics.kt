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


package com.shareconnect.utorrentconnect.analytics

import com.google.firebase.analytics.FirebaseAnalytics.Event
import com.google.firebase.analytics.FirebaseAnalytics.Param

class Analytics constructor(
    private val analyticsProvider: AnalyticsProvider
) {
    fun logScreenView(screenName: String, screenClass: Class<*>) {
        analyticsProvider.logEvent(
            name = Event.SCREEN_VIEW,
            Param.SCREEN_NAME to screenName,
            Param.SCREEN_CLASS to screenClass.simpleName
        )
    }

    fun setTorrentsCount(count: Int) {
        analyticsProvider.setUserProperty(PROPERTY_TORRENTS_COUNT, TorrentCount.fromCount(count).value)
    }

    fun logStartupTimeSLI(timeMillis: Long) {
        analyticsProvider.logEvent(
            name = EVENT_SLI_STARTUP_TIME,
            PROPERTY_STARTUP_TIME_MILLIS to timeMillis
        )
    }

    companion object {
        private const val PROPERTY_TORRENTS_COUNT = "torrents_count"
        private const val PROPERTY_STARTUP_TIME_MILLIS = "startup_time_millis"

        private const val EVENT_SLI_STARTUP_TIME = "sli_startup_time"
    }
}
