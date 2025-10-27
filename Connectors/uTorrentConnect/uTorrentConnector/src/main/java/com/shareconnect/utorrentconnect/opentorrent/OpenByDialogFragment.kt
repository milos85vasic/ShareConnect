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


package com.shareconnect.utorrentconnect.opentorrent

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.shareconnect.utorrentconnect.R

class OpenByDialogFragment : DialogFragment() {

    private val listener: OnOpenTorrentSelectedListener
        get() = checkNotNull(activity as? OnOpenTorrentSelectedListener) {
            "Activity must implement ${OnOpenTorrentSelectedListener::class.java.simpleName}"
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.open_torrent)
            .setItems(R.array.open_torrent_by_entries) { _, which ->
                if (which == 0) { // by file
                    listener.onOpenTorrentByFile()
                } else if (which == 1) { // by address
                    listener.onOpenTorrentByAddress()
                }
            }
        return builder.create()
    }

    interface OnOpenTorrentSelectedListener {
        fun onOpenTorrentByFile()
        fun onOpenTorrentByAddress()
    }
}
