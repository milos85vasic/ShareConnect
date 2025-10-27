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
import com.shareconnect.utorrentconnect.e2e.utils.assertViewWithIdHasText
import com.shareconnect.utorrentconnect.e2e.utils.assertViewWithTextDisplayed

class TorrentDetailsInfoPageRobot {

    fun assertTotalSizeText(text: String) {
        assertViewWithIdHasText(R.id.total_size_text, text)
    }

    fun assertLocation(text: String) {
        assertViewWithIdHasText(R.id.location_text, text)
    }

    fun assertPrivacy(text: String) {
        assertViewWithIdHasText(R.id.privacy_text, text)
    }

    fun assertCreator(text: String) {
        assertViewWithIdHasText(R.id.creator_text, text)
    }

    fun assertCreatedOn(text: String) {
        assertViewWithIdHasText(R.id.created_on_text, text)
    }

    fun assertComment(text: String) {
        assertViewWithIdHasText(R.id.comment_text, text)
    }

    fun assertMagnet(text: String) {
        assertViewWithIdHasText(R.id.magnet_text, text)
    }

    fun assertHave(text: String) {
        assertViewWithIdHasText(R.id.have_text, text)
    }

    fun assertAvailable(text: String) {
        assertViewWithIdHasText(R.id.available_text, text)
    }

    fun assertDownloaded(text: String) {
        assertViewWithIdHasText(R.id.downloaded_text, text)
    }

    fun assertUploaded(text: String) {
        assertViewWithIdHasText(R.id.uploaded_text, text)
    }

    fun assertAverageSpeed(text: String) {
        assertViewWithIdHasText(R.id.average_speed_text, text)
    }

    fun assertAdded(text: String) {
        assertViewWithIdHasText(R.id.added_text, text)
    }

    fun assertLastActivity(text: String) {
        assertViewWithIdHasText(R.id.last_activity_text, text)
    }

    fun assertDownloading(text: String) {
        assertViewWithIdHasText(R.id.downloading_text, text)
    }

    fun assertSeeding(text: String) {
        assertViewWithIdHasText(R.id.seeding_text, text)
    }

    companion object {
        fun torrentDetailsInfoPage(
            func: TorrentDetailsInfoPageRobot.() -> Unit
        ): TorrentDetailsInfoPageRobot {
            assertViewWithTextDisplayed("Info")
            assertViewWithTextDisplayed("Torrent Information")
            assertViewWithTextDisplayed("Transfer")
            assertViewWithTextDisplayed("Dates")
            assertViewWithTextDisplayed("Time Elapsed")
            return TorrentDetailsInfoPageRobot().apply(func)
        }
    }
}
