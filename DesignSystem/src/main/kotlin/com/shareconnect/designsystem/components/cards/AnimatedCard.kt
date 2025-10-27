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


package com.shareconnect.designsystem.components.cards

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import com.shareconnect.designsystem.R
import com.shareconnect.designsystem.components.DesignSystemComponent

/**
 * Animated card component with elevation, shadows, and hover effects.
 * Supports different card styles and interaction states.
 */
class AnimatedCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), DesignSystemComponent {

    override val componentContext: Context = context

    enum class CardStyle {
        ELEVATED,
        OUTLINED,
        FILLED
    }

    // Card properties
    var cardStyle: CardStyle = CardStyle.ELEVATED
        set(value) {
            field = value
            updateCardAppearance()
            invalidate()
        }

    var cardElevation: Float = 4f
        set(value) {
            field = value
            updateElevation()
        }

    var cardCornerRadius: Float = 12f
        set(value) {
            field = value
            invalidate()
        }

    // Colors
    @ColorInt private var backgroundColor: Int = Color.WHITE
    @ColorInt private var strokeColor: Int = Color.LTGRAY
    @ColorInt private var shadowColor: Int = Color.BLACK

    // Dimensions
    private var strokeWidth: Float = 1f
    private var shadowRadius: Float = 8f

    // Animation properties
    private var currentElevation: Float = cardElevation
    private var hoverElevation: Float = cardElevation * 1.5f
    private var scaleFactor: Float = 1f
    private var hoverScale: Float = 1.02f

    // Paints
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        setShadowLayer(shadowRadius, 0f, 2f, shadowColor)
    }

    // Animation
    private var currentAnimator: AnimatorSet? = null
    private var isHovered = false

    init {
        parseAttributes(attrs)
        updateCardAppearance()
        updateElevation()
        setWillNotDraw(false)
        isClickable = true
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimatedCard)
        try {
            cardStyle = CardStyle.values()[typedArray.getInt(R.styleable.AnimatedCard_dsCardStyle, 0)]
            cardElevation = typedArray.getDimension(R.styleable.AnimatedCard_dsCardElevation, cardElevation)
            cardCornerRadius = typedArray.getDimension(R.styleable.AnimatedCard_dsCardCornerRadius, cardCornerRadius)
            strokeWidth = typedArray.getDimension(R.styleable.AnimatedCard_dsCardStrokeWidth, strokeWidth)
        } finally {
            typedArray.recycle()
        }
    }

    override fun applyDesignSystemTheme() {
        updateCardAppearance()
    }

    private fun updateCardAppearance() {
        when (cardStyle) {
            CardStyle.ELEVATED -> {
                backgroundColor = getColorFromTheme(R.attr.dsColorSurfaceContainerLow)
                strokeColor = Color.TRANSPARENT
                strokeWidth = 0f
                shadowRadius = cardElevation / 2f
            }
            CardStyle.OUTLINED -> {
                backgroundColor = getColorFromTheme(R.attr.dsColorSurface)
                strokeColor = getColorFromTheme(R.attr.dsColorSurfaceVariant)
                strokeWidth = 1f
                shadowRadius = 0f
            }
            CardStyle.FILLED -> {
                backgroundColor = getColorFromTheme(R.attr.dsColorSurfaceContainerHighest)
                strokeColor = Color.TRANSPARENT
                strokeWidth = 0f
                shadowRadius = 0f
            }
        }
        updateElevation()
        invalidate()
    }

    private fun getColorFromTheme(attr: Int): Int {
        val typedValue = android.util.TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    private fun updateElevation() {
        elevation = currentElevation
        shadowPaint.setShadowLayer(shadowRadius, 0f, currentElevation / 4f, shadowColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val rect = RectF(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            (width - paddingRight).toFloat(),
            (height - paddingBottom).toFloat()
        )

        // Draw shadow for elevated cards
        if (cardStyle == CardStyle.ELEVATED && shadowRadius > 0) {
            shadowPaint.color = backgroundColor
            canvas.drawRoundRect(rect, cardCornerRadius, cardCornerRadius, shadowPaint)
        }

        // Draw background
        backgroundPaint.color = backgroundColor
        canvas.drawRoundRect(rect, cardCornerRadius, cardCornerRadius, backgroundPaint)

        // Draw stroke
        if (strokeWidth > 0) {
            strokePaint.color = strokeColor
            strokePaint.strokeWidth = strokeWidth
            canvas.drawRoundRect(rect, cardCornerRadius, cardCornerRadius, strokePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled || !areAnimationsEnabled()) {
            return super.onTouchEvent(event)
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startHoverAnimation()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                startUnhoverAnimation()
                if (event.action == MotionEvent.ACTION_UP && isClickable) {
                    performClick()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun startHoverAnimation() {
        if (isHovered) return
        isHovered = true

        currentAnimator?.cancel()

        val elevationAnim = ObjectAnimator.ofFloat(this, "currentElevation", currentElevation, hoverElevation)
        val scaleAnim = ObjectAnimator.ofFloat(this, "scaleFactor", scaleFactor, hoverScale)

        AnimatorSet().apply {
            playTogether(elevationAnim, scaleAnim)
            duration = 150
            start()
            currentAnimator = this
        }
    }

    private fun startUnhoverAnimation() {
        if (!isHovered) return
        isHovered = false

        currentAnimator?.cancel()

        val elevationAnim = ObjectAnimator.ofFloat(this, "currentElevation", currentElevation, cardElevation)
        val scaleAnim = ObjectAnimator.ofFloat(this, "scaleFactor", scaleFactor, 1f)

        AnimatorSet().apply {
            playTogether(elevationAnim, scaleAnim)
            duration = 200
            start()
            currentAnimator = this
        }
    }

    // Property setters for animations
    private fun setCurrentElevation(elevation: Float) {
        currentElevation = elevation
        updateElevation()
    }

    private fun setScaleFactor(scale: Float) {
        scaleFactor = scale
        scaleX = scale
        scaleY = scale
    }

    override fun setAnimationsEnabled(enabled: Boolean) {
        // Store animation state if needed
    }

    override fun areAnimationsEnabled(): Boolean = true

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        currentAnimator?.cancel()
    }
}