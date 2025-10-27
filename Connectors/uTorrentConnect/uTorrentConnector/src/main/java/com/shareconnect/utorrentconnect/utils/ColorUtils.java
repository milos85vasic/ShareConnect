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

import android.content.Context;
import android.content.res.ColorStateList;
import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import android.util.TypedValue;

public class ColorUtils {

    @ColorInt public static int resolveColor(Context context, int colorAttr, int defaultResId) {
        TypedValue typedValue = new TypedValue();
        boolean resolved = context.getTheme().resolveAttribute(colorAttr, typedValue, true);
        if (resolved) {
            if (typedValue.type == TypedValue.TYPE_STRING) {
                ColorStateList stateList = ContextCompat.getColorStateList(context, typedValue.resourceId);
                if (stateList != null) return stateList.getDefaultColor();
            } else {
                return typedValue.data;
            }
        }

        return ContextCompat.getColor(context, defaultResId);
    }
}
