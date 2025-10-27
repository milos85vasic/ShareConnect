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


package com.shareconnect.designsystem.components.buttons

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.core.content.res.ResourcesCompat
import com.shareconnect.designsystem.R
import com.shareconnect.designsystem.components.BaseDesignSystemView

/**
 * Animated button component with ripple effects, state animations, and modern design.
 * Supports different button styles and interaction states.
 */
class AnimatedButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseDesignSystemView(context, attrs, defStyleAttr) {

    enum class ButtonStyle {
        PRIMARY,
        SECONDARY,
        OUTLINED,
        TEXT
    }

    enum class ButtonSize {
        SMALL,
        MEDIUM,
        LARGE
    }

    // Button properties
    var buttonStyle: ButtonStyle = ButtonStyle.PRIMARY
        set(value) {
            field = value
            updateButtonColors()
            invalidate()
        }

    var buttonSize: ButtonSize = ButtonSize.MEDIUM
        set(value) {
            field = value
            updateDimensions()
            requestLayout()
        }

    var text: String = ""
        set(value) {
            field = value
            invalidate()
        }

    var isLoading: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    // Colors
    @ColorInt private var backgroundColor: Int = Color.BLUE
    @ColorInt private var textColor: Int = Color.WHITE
    @ColorInt private var rippleColor: Int = Color.WHITE
    @ColorInt private var borderColor: Int = Color.TRANSPARENT

    // Dimensions
    private var buttonHeight: Float = 44f
    private var buttonPaddingHorizontal: Float = 16f
    private var cornerRadius: Float = 8f
    private var borderWidth: Float = 0f

    // Animation properties
    private var rippleRadius: Float = 0f
    private var rippleAlpha: Int = 0
    private var scaleFactor: Float = 1f
    private var pressScale: Float = 0.95f

    // Paints
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val ripplePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 16f
    }

    // Animation
    private var currentAnimator: AnimatorSet? = null
    private var touchX: Float = 0f
    private var touchY: Float = 0f

    init {
        parseAttributes(attrs)
        updateButtonColors()
        updateDimensions()
        isClickable = true
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimatedButton)
        try {
            buttonStyle = ButtonStyle.values()[typedArray.getInt(R.styleable.AnimatedButton_dsButtonStyle, 0)]
            buttonSize = ButtonSize.values()[typedArray.getInt(R.styleable.AnimatedButton_dsButtonSize, 1)]
            text = typedArray.getString(R.styleable.AnimatedButton_dsText) ?: ""
            isLoading = typedArray.getBoolean(R.styleable.AnimatedButton_dsIsLoading, false)
        } finally {
            typedArray.recycle()
        }
    }

    override fun applyDesignSystemTheme() {
        updateButtonColors()
    }

    private fun updateButtonColors() {
        when (buttonStyle) {
            ButtonStyle.PRIMARY -> {
                backgroundColor = getColorFromTheme(R.attr.dsColorPrimary)
                textColor = getColorFromTheme(R.attr.dsColorOnPrimary)
                rippleColor = textColor
                borderColor = Color.TRANSPARENT
            }
            ButtonStyle.SECONDARY -> {
                backgroundColor = getColorFromTheme(R.attr.dsColorSecondaryContainer)
                textColor = getColorFromTheme(R.attr.dsColorOnSecondaryContainer)
                rippleColor = textColor
                borderColor = Color.TRANSPARENT
            }
            ButtonStyle.OUTLINED -> {
                backgroundColor = Color.TRANSPARENT
                textColor = getColorFromTheme(R.attr.dsColorPrimary)
                rippleColor = textColor
                borderColor = textColor
            }
            ButtonStyle.TEXT -> {
                backgroundColor = Color.TRANSPARENT
                textColor = getColorFromTheme(R.attr.dsColorPrimary)
                rippleColor = textColor
                borderColor = Color.TRANSPARENT
            }
        }
    }

    private fun getColorFromTheme(attr: Int): Int {
        val typedValue = android.util.TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    private fun updateDimensions() {
        when (buttonSize) {
            ButtonSize.SMALL -> {
                buttonHeight = resources.getDimension(R.dimen.ds_button_height_small)
                buttonPaddingHorizontal = resources.getDimension(R.dimen.ds_button_padding_horizontal_small)
                textPaint.textSize = resources.getDimension(R.dimen.ds_text_label_large)
            }
            ButtonSize.MEDIUM -> {
                buttonHeight = resources.getDimension(R.dimen.ds_button_height_medium)
                buttonPaddingHorizontal = resources.getDimension(R.dimen.ds_button_padding_horizontal_medium)
                textPaint.textSize = resources.getDimension(R.dimen.ds_text_label_large)
            }
            ButtonSize.LARGE -> {
                buttonHeight = resources.getDimension(R.dimen.ds_button_height_large)
                buttonPaddingHorizontal = resources.getDimension(R.dimen.ds_button_padding_horizontal_large)
                textPaint.textSize = resources.getDimension(R.dimen.ds_text_title_medium)
            }
        }
        cornerRadius = resources.getDimension(R.dimen.ds_radius_medium)
        borderWidth = if (buttonStyle == ButtonStyle.OUTLINED) 2f else 0f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val textWidth = textPaint.measureText(text)
        val desiredWidth = (textWidth + 2 * buttonPaddingHorizontal).toInt()
        val desiredHeight = buttonHeight.toInt()

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())

        // Draw background
        backgroundPaint.color = backgroundColor
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, backgroundPaint)

        // Draw border
        if (borderWidth > 0) {
            borderPaint.color = borderColor
            borderPaint.strokeWidth = borderWidth
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)
        }

        // Draw ripple effect
        if (rippleRadius > 0 && rippleAlpha > 0) {
            ripplePaint.color = rippleColor
            ripplePaint.alpha = rippleAlpha
            canvas.drawCircle(touchX, touchY, rippleRadius, ripplePaint)
        }

        // Draw text
        textPaint.color = textColor
        val textY = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
        canvas.drawText(text, width / 2f, textY, textPaint)

        // Draw loading indicator if needed
        if (isLoading) {
            drawLoadingIndicator(canvas)
        }
    }

    private fun drawLoadingIndicator(canvas: Canvas) {
        // Simple loading dots animation
        val dotRadius = 4f
        val dotSpacing = 12f
        val centerY = height / 2f
        val startX = width / 2f - dotSpacing

        for (i in 0..2) {
            val alpha = ((System.currentTimeMillis() / 200 + i) % 3 * 85).toInt().coerceIn(0, 255)
            textPaint.alpha = alpha
            canvas.drawCircle(startX + i * dotSpacing, centerY, dotRadius, textPaint)
        }
        textPaint.alpha = 255
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled || !areAnimationsEnabled()) {
            return super.onTouchEvent(event)
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                touchY = event.y
                startPressAnimation()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                startReleaseAnimation()
                if (event.action == MotionEvent.ACTION_UP && isClickable) {
                    performClick()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun startPressAnimation() {
        currentAnimator?.cancel()

        val scaleAnim = ObjectAnimator.ofFloat(this, "scaleFactor", scaleFactor, pressScale)
        val rippleAnim = ObjectAnimator.ofFloat(this, "rippleRadius", 0f, width.toFloat())
        val alphaAnim = ObjectAnimator.ofInt(this, "rippleAlpha", 0, 100)

        AnimatorSet().apply {
            playTogether(scaleAnim, rippleAnim, alphaAnim)
            duration = 150
            start()
            currentAnimator = this
        }
    }

    private fun startReleaseAnimation() {
        currentAnimator?.cancel()

        val scaleAnim = ObjectAnimator.ofFloat(this, "scaleFactor", scaleFactor, 1f)
        val alphaAnim = ObjectAnimator.ofInt(this, "rippleAlpha", rippleAlpha, 0)

        AnimatorSet().apply {
            playTogether(scaleAnim, alphaAnim)
            duration = 200
            start()
            currentAnimator = this
        }
    }

    // Property setters for animations
    private fun setScaleFactor(scale: Float) {
        scaleFactor = scale
        scaleX = scale
        scaleY = scale
    }

    private fun setRippleRadius(radius: Float) {
        rippleRadius = radius
        invalidate()
    }

    private fun setRippleAlpha(alpha: Int) {
        rippleAlpha = alpha
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        currentAnimator?.cancel()
    }
}