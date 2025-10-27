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
import com.shareconnect.utorrentconnect.analytics.Analytics
import com.shareconnect.utorrentconnect.analytics.AnalyticsProvider
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class AnalyticsTest {

    private val analyticsProvider = mock<AnalyticsProvider>()
    private val analytics = Analytics(analyticsProvider)

    @Test
    fun `test logScreenView`() {
        // given
        class MainScreen

        // when
        analytics.logScreenView("Main", MainScreen::class.java)

        // then
        verify(analyticsProvider).logEvent(
            Event.SCREEN_VIEW,
            Param.SCREEN_NAME to "Main",
            Param.SCREEN_CLASS to "MainScreen"
        )
    }

    @Test
    fun `test setTorrentsCount`() {
        // when
        analytics.setTorrentsCount(42)

        // then
        verify(analyticsProvider).setUserProperty("torrents_count", "41-50")
    }

    @Test
    fun `test logStartupTimeSLI`() {
        // when
        analytics.logStartupTimeSLI(345L)

        // then
        verify(analyticsProvider).logEvent(
            name = "sli_startup_time",
            "startup_time_millis" to 345L
        )
    }
}
