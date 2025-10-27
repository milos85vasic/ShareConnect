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


package com.shareconnect.designsystem.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes

/**
 * Base interface for all Design System components.
 * Provides common functionality and ensures consistency across components.
 */
interface DesignSystemComponent {

    /**
     * Get the component's context for resource access
     */
    val componentContext: Context

    /**
     * Apply design system theme attributes to the component
     */
    fun applyDesignSystemTheme()

    /**
     * Enable or disable animations for this component
     */
    fun setAnimationsEnabled(enabled: Boolean)

    /**
     * Check if animations are currently enabled
     */
    fun areAnimationsEnabled(): Boolean
}

/**
 * Base class for custom views that implement the Design System
 */
abstract class BaseDesignSystemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes), DesignSystemComponent {

    override val componentContext: Context = context

    private var animationsEnabled = true

    init {
        applyDesignSystemTheme()
    }

    override fun setAnimationsEnabled(enabled: Boolean) {
        animationsEnabled = enabled
    }

    override fun areAnimationsEnabled(): Boolean = animationsEnabled
}