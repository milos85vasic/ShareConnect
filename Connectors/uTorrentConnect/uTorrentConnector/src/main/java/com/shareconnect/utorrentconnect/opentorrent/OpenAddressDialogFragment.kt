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
import android.content.ClipDescription
import android.content.ClipboardManager
import android.os.Bundle
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.shareconnect.utorrentconnect.R

class OpenAddressDialogFragment : DialogFragment() {

    private val listener: OnOpenMagnetListener
        get() = checkNotNull(activity as? OnOpenMagnetListener) {
            "Activity must implement ${OnOpenMagnetListener::class.java.simpleName}"
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.open_address_dialog, null)
        val addressText = view.findViewById<TextInputEditText>(R.id.address_text)
        builder.setView(view)
            .setTitle(R.string.address_of_torrent_file)
            .setPositiveButton(R.string.open) { _, _ -> listener.onOpenMagnet(addressText.text.toString()) }
            .setNegativeButton(android.R.string.cancel, null)
        val clipboard = getSystemService(requireContext(), ClipboardManager::class.java)
        if (clipboard != null) {
            val clipData = clipboard.primaryClip
            if (clipData != null && clipData.description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                val text = clipData.getItemAt(0).text
                if (text != null) {
                    addressText.setText(text)
                    addressText.setSelection(0, addressText.text?.length ?: 0)
                }
            }
        }
        val dialog = builder.create()
        addressText.addTextChangedListener { s ->
            val openButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            openButton.isEnabled = !s.isNullOrBlank()
        }
        dialog.setOnShowListener {
            val openButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            openButton.isEnabled = !addressText.text.isNullOrBlank()
        }
        return dialog
    }

    interface OnOpenMagnetListener {
        fun onOpenMagnet(uri: String?)
    }
}
