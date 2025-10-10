package com.shareconnect.utorrentconnect.e2e.robots

import com.shareconnect.utorrentconnect.R
import com.shareconnect.utorrentconnect.e2e.utils.assertViewWithIdDisplayed
import com.shareconnect.utorrentconnect.e2e.utils.assertViewWithIdExists
import com.shareconnect.utorrentconnect.e2e.utils.assertViewWithIdHidden
import com.shareconnect.utorrentconnect.e2e.utils.waitForCondition

class ProgressRobot {

    fun assertProgressbarDisplayed() {
        waitForCondition("Progressbar displayed") {
            assertViewWithIdDisplayed(R.id.progressbar)
        }
    }

    fun assertProgressbarHidden() {
        waitForCondition("Progressbar hidden") {
            assertViewWithIdHidden(R.id.progressbar)
        }
    }

    companion object {
        fun progress(func: ProgressRobot.() -> Unit): ProgressRobot {
            assertViewWithIdExists(R.id.progressbar)
            return ProgressRobot().apply(func)
        }
    }
}
