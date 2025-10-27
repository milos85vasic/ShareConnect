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


package com.shareconnect

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

/**
 * Utility class for showing confirmation and error dialogs with proper localization
 */
object DialogUtils {

    /**
     * Show a confirmation dialog with Yes/No options
     * @param context The context to use
     * @param title The dialog title resource ID
     * @param message The dialog message resource ID
     * @param positiveListener The listener for positive button click
     * @param negativeListener The listener for negative button click (optional)
     */
    fun showConfirmDialog(
        context: Context, title: Int, message: Int,
        positiveListener: DialogInterface.OnClickListener,
        negativeListener: DialogInterface.OnClickListener?
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.yes, positiveListener)
        builder.setNegativeButton(
            R.string.no
        ) { dialog, which -> dialog.dismiss() }
        builder.setCancelable(true)
        builder.show()
    }

    /**
     * Show a confirmation dialog with OK/Cancel options
     * @param context The context to use
     * @param title The dialog title resource ID
     * @param message The dialog message resource ID
     * @param positiveListener The listener for positive button click
     * @param negativeListener The listener for negative button click (optional)
     */
    fun showOkCancelDialog(
        context: Context, title: Int, message: Int,
        positiveListener: DialogInterface.OnClickListener,
        negativeListener: DialogInterface.OnClickListener?
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.ok, positiveListener)
        builder.setNegativeButton(
            R.string.cancel
        ) { dialog, which -> dialog.dismiss() }
        builder.setCancelable(true)
        builder.show()
    }

    /**
     * Show an error dialog with OK button
     * @param context The context to use
     * @param title The dialog title resource ID
     * @param message The dialog message resource ID
     */
    fun showErrorDialog(context: Context, title: Int, message: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
        builder.setCancelable(true)
        builder.show()
    }

    /**
     * Show an error dialog with a custom message
     * @param context The context to use
     * @param title The dialog title resource ID
     * @param message The custom error message
     */
    fun showErrorDialog(context: Context, title: Int, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
        builder.setCancelable(true)
        builder.show()
    }
}