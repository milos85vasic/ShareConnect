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

import com.shareconnect.utorrentconnect.R
import com.shareconnect.utorrentconnect.e2e.utils.assertViewWithIdDisplayed
import com.shareconnect.utorrentconnect.e2e.utils.clickId
import com.shareconnect.utorrentconnect.e2e.utils.waitForCondition

class WelcomeRobot {

    fun assertAddServerButtonDisplayed() {
        waitForCondition("Add server button displayed") {
            assertViewWithIdDisplayed(R.id.add_server_button)
        }
    }

    fun clickAddServerButton() {
        clickId(R.id.add_server_button)
    }

    companion object {
        fun welcome(func: WelcomeRobot.() -> Unit): WelcomeRobot {
            assertViewWithIdDisplayed(R.id.welcome_text)
            return WelcomeRobot().apply(func)
        }
    }
}
