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


package com.shareconnect.utorrentconnect.server;

import android.text.InputFilter;
import android.text.Spanned;

public class PortNumberFilter implements InputFilter {

    private static PortNumberFilter instance;

    public static PortNumberFilter instance() {
        if (instance == null) {
            synchronized (PortNumberFilter.class) {
                if (instance == null) {
                    instance = new PortNumberFilter();
                }
            }
        }
        return instance;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (source.length() == 0)
            return null;
        String result = resultingText(source, start, end, dest, dstart, dend);
        if (result.startsWith("0"))
            return "";
        try {
            Integer port = Integer.parseInt(result);
            if (port > 0xFFFF)
                return dest.subSequence(dstart, dend);
        } catch (NumberFormatException e) {
            return "";
        }
        return null;
    }

    private String resultingText(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder b = new StringBuilder();
        b.append(dest.subSequence(0, dstart));
        b.append(source.subSequence(start, end));
        b.append(dest.subSequence(dend, dest.length()));
        return b.toString();
    }
}
