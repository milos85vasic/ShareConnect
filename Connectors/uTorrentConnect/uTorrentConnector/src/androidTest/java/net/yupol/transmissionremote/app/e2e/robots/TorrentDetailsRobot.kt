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


package com.shareconnect.utorrentconnect.e2e.robots

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.ViewPagerActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.shareconnect.utorrentconnect.R
import com.shareconnect.utorrentconnect.e2e.utils.assertViewWithIdDisplayed
import com.shareconnect.utorrentconnect.e2e.utils.assertViewWithTextDisplayed

class TorrentDetailsRobot {

    fun assertInfoPageOpen() {
        assertViewWithTextDisplayed("Info")
    }

    fun assertFilesPageOpen() {
        assertViewWithTextDisplayed("Files")
    }

    fun assertTrackersPageOpen() {
        assertViewWithTextDisplayed("Trackers")
    }

    fun assertPeersPageOpen() {
        assertViewWithTextDisplayed("Peers")
    }

    fun assertOptionsPageOpen() {
        assertViewWithTextDisplayed("Options")
    }

    fun scrollToInfoPage() {
        scrollToPage(0)
        assertInfoPageOpen()
    }

    fun scrollToFilesPage() {
        scrollToPage(1)
        assertFilesPageOpen()
    }

    fun scrollToTrackersPage() {
        scrollToPage(2)
        assertTrackersPageOpen()
    }

    fun scrollToPeersPage() {
        scrollToPage(3)
        assertPeersPageOpen()
    }

    fun scrollToOptionsPage() {
        scrollToPage(4)
        assertOptionsPageOpen()
    }

    fun exit() {
        Espresso.pressBack()
    }

    private fun scrollToPage(page: Int) {
        onView(withId(R.id.pager)).perform(ViewPagerActions.scrollToPage(page))
    }

    companion object {
        fun torrentDetails(func: TorrentDetailsRobot.() -> Unit): TorrentDetailsRobot {
            assertViewWithIdDisplayed(R.id.pager_title_strip)
            return TorrentDetailsRobot().apply(func)
        }
    }
}
