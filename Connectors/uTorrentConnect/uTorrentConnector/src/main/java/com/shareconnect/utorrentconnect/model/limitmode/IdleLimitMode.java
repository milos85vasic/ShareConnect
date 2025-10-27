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


package com.shareconnect.utorrentconnect.model.limitmode;

import com.shareconnect.utorrentconnect.R;

public enum IdleLimitMode implements LimitMode {
    GLOBAL_SETTINGS(0, R.string.global_settings),
    STOP_WHEN_INACTIVE(1, R.string.stop_when_inactive),
    UNLIMITED(2, R.string.unlimited);

    private int value;
    private int textRes;

    IdleLimitMode(int value, int textRes) {
        this.value = value;
        this.textRes = textRes;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public int getTextRes() {
        return textRes;
    }

    public static IdleLimitMode fromValue(int value) {
        for (IdleLimitMode mode : values()) {
            if (mode.value == value)
                return mode;
        }
        return null;
    }
}
