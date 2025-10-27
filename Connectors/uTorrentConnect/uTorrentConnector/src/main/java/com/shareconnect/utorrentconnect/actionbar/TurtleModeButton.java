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


package com.shareconnect.utorrentconnect.actionbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.utils.CheatSheet;

public class TurtleModeButton extends ImageButton implements View.OnClickListener {

    private boolean isEnabled;
    private int enabledRes;
    private int disabledRes;
    private OnEnableChangedListener enableListener;

    public TurtleModeButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TurtleModeButton, 0, 0);

        try {
            enabledRes = a.getResourceId(R.styleable.TurtleModeButton_src_enabled, 0);
            disabledRes = a.getResourceId(R.styleable.TurtleModeButton_src_disabled, 0);
            isEnabled = a.getBoolean(R.styleable.TurtleModeButton_enabled, false);
        } finally {
            a.recycle();
        }

        updateImage();

        setOnClickListener(this);

        CheatSheet.setup(this, R.string.tooltip_turtle_mode);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        if (enableListener != null) {
            enableListener.onEnableChanged(isEnabled);
        }
        updateImage();
        invalidate();
        requestLayout();
    }

    public void setEnableChangedListener(OnEnableChangedListener listener) {
        enableListener = listener;
    }

    @Override
    public void onClick(View v) {
        setEnabled(!isEnabled);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (l != this) {
            throw new UnsupportedOperationException("Use setEnableChangedListener(OnEnableChangedListener) instead");
        }
        super.setOnClickListener(l);
    }

    private void updateImage() {
        setImageResource(isEnabled ? enabledRes : disabledRes);
    }

    public interface OnEnableChangedListener {
        void onEnableChanged(boolean isEnabled);
    }
}
