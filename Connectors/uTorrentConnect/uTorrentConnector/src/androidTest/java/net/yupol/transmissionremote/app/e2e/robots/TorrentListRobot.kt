package com.shareconnect.utorrentconnect.e2e.robots

import com.shareconnect.utorrentconnect.R
import com.shareconnect.utorrentconnect.e2e.utils.assertViewWithIdDisplayed
import com.shareconnect.utorrentconnect.e2e.utils.assertViewWithIdHasText
import com.shareconnect.utorrentconnect.e2e.utils.clickItemAtPosition
import com.shareconnect.utorrentconnect.e2e.utils.recyclerViewWithIdHasItemCount
import com.shareconnect.utorrentconnect.e2e.utils.waitForCondition

class TorrentListRobot {

    fun assertEmptyTorrentList() {
        waitForCondition("Torrent list is empty") {
            assertViewWithIdHasText(R.id.torrent_list_empty_text, "No torrents")
        }
    }

    fun assertTorrentCount(expectedCount: Int) {
        waitForCondition("Torrent count is $expectedCount") {
            recyclerViewWithIdHasItemCount(R.id.torrent_list_recycler_view, expectedCount)
        }
    }

    fun clickTorrentAtPosition(position: Int) {
        clickItemAtPosition(R.id.torrent_list_recycler_view, position)
    }

    companion object {
        fun torrentList(func: TorrentListRobot.() -> Unit): TorrentListRobot {
            assertViewWithIdDisplayed(R.id.torrent_list_recycler_view)
            return TorrentListRobot().apply(func)
        }
    }
}
