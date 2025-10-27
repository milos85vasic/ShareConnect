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


package com.shareconnect.utorrentconnect.torrentlist;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import androidx.appcompat.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.shareconnect.utorrentconnect.R;

public class PlayPauseButton extends AppCompatImageButton {


    private static final long ANIMATION_DURATION = 150;

    private boolean isPaused;
    private PlayPauseDrawable drawable;
    private Animator animator;

    public PlayPauseButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        int backgroundColor, foregroundColor, borderColor;
        TypedArray customAttrs = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlayPauseButton, 0, 0);
        try {
            backgroundColor = customAttrs.getColor(R.styleable.PlayPauseButton_background_color, Color.WHITE);
            foregroundColor = customAttrs.getColor(R.styleable.PlayPauseButton_foreground_color, Color.GRAY);
            borderColor = customAttrs.getColor(R.styleable.PlayPauseButton_border_color, Color.DKGRAY);
        } finally {
            customAttrs.recycle();
        }

        drawable = new PlayPauseDrawable(backgroundColor, foregroundColor, borderColor);
        drawable.setCallback(this);
        setImageDrawable(drawable);
        setBackgroundDrawable(null);
        setPadding(0, 0, 0, 0);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        drawable.setArmed(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        drawable.setArmed(false);
                        break;
                }
                return false;
            }
        });

        setPaused(true);
    }

    public void setPaused(boolean isPaused) {
        if (this.isPaused == isPaused) return;

        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }

        this.isPaused = isPaused;
        drawable.setPaused(isPaused);
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void toggle() {
        isPaused = !isPaused;

        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        animator = drawable.getAnimator(isPaused);
        animator.setDuration(ANIMATION_DURATION);
        animator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        drawable.setBounds(0, 0, w, h);
    }
}
