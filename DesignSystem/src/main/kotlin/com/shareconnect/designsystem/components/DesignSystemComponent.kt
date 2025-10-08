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