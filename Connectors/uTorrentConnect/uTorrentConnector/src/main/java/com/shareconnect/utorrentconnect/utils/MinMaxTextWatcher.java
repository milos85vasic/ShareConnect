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


package com.shareconnect.utorrentconnect.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class MinMaxTextWatcher implements TextWatcher {

    private static final String TAG = MinMaxTextWatcher.class.getSimpleName();

    private int min, max;
    private String minStr, maxStr;
    private boolean selfChange = false;

    public MinMaxTextWatcher(int min, int max) {
        this.min = min;
        this.max = max;
        minStr = String.valueOf(min);
        maxStr = String.valueOf(max);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (selfChange) return;
        selfChange = true;
        try {
            int value = Integer.parseInt(s.toString());
            if(value < min) {
                s.replace(0, s.length(), minStr, 0, minStr.length());
            } else if(value > max) {
                s.replace(0, s.length(), maxStr, 0, maxStr.length());
            }
        } catch (NumberFormatException ex) {
            Log.e(TAG, "Can't parse number '" + s + "'");
        } finally {
            selfChange = false;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
