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


package com.shareconnect.utorrentconnect.e2e

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.shareconnect.utorrentconnect.MainActivity
import com.shareconnect.utorrentconnect.e2e.robots.AddServerRobot.Companion.addServer
import com.shareconnect.utorrentconnect.e2e.robots.TorrentDetailsRobot.Companion.torrentDetails
import com.shareconnect.utorrentconnect.e2e.robots.TorrentListRobot.Companion.torrentList
import com.shareconnect.utorrentconnect.e2e.robots.WelcomeRobot.Companion.welcome
import net.yupol.transmissionremote.mockserver.MockServer
import net.yupol.transmissionremote.mockserver.model.Torrent
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TorrentDetailsTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<MainActivity>()

    @get:Rule
    val grantNotificationsPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    private val server = MockServer()

    @Before
    fun setup() {
        server.start()
        server.addTorrent(
            name = "openSUSE-Leap-15.4-NET-x86_64-Build243.2-Media.iso",
            size = Torrent.Size(
                totalSize = 181403648L
            )
        )
        server.addTorrent(
            name = "ubuntu-22.04.1-desktop-amd64.iso",
            size = Torrent.Size(
                totalSize = 3826831360L
            )
        )
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun torrent_details_test() {
        welcome {
            clickAddServerButton()
        }
        addServer {
            enterServerName("Test server")
            enterHostName(server.hostName)
            enterPort(server.port)
            clickOk()
        }
        torrentList {
            clickTorrentAtPosition(0)
        }
        torrentDetails {
            assertInfoPageOpen()
            scrollToFilesPage()
            scrollToTrackersPage()
            scrollToPeersPage()
            scrollToOptionsPage()
            exit()
        }
        torrentList {
            clickTorrentAtPosition(1)
        }
        torrentDetails {
            assertOptionsPageOpen()
        }
    }
}
