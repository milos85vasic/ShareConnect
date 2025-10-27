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


package com.shareconnect.designsystem.components.fabs

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.shareconnect.designsystem.R
import com.shareconnect.designsystem.components.DesignSystemComponent

/**
 * Animated Floating Action Button with expand/collapse animations and modern design.
 * Supports different sizes and states with smooth transitions.
 */
class AnimatedFAB @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr), DesignSystemComponent {

    override val componentContext: Context = context

    enum class FABSize {
        SMALL,
        MEDIUM,
        LARGE,
        EXTENDED
    }

    enum class FABState {
        NORMAL,
        EXPANDED,
        LOADING
    }

    // FAB properties
    var fabSize: FABSize = FABSize.MEDIUM
        set(value) {
            field = value
            updateDimensions()
            requestLayout()
        }

    var fabState: FABState = FABState.NORMAL
        set(value) {
            field = value
            updateState()
            invalidate()
        }

    var extendedText: String = ""
        set(value) {
            field = value
            if (fabSize == FABSize.EXTENDED) {
                invalidate()
            }
        }

    // Colors
    @ColorInt private var backgroundColor: Int = Color.BLUE
    @ColorInt private var iconColor: Int = Color.WHITE
    @ColorInt private var rippleColor: Int = Color.WHITE

    // Dimensions
    private var fabSizePx: Float = 56f
    private var cornerRadius: Float = 28f
    private var iconSize: Float = 24f
    private var extendedPaddingHorizontal: Float = 20f

    // Animation properties
    private var scaleFactor: Float = 1f
    private var rotationAngle: Float = 0f
    private var loadingProgress: Float = 0f

    // Paints
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val ripplePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val loadingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 16f
        textAlign = Paint.Align.LEFT
    }

    // Animation
    private var currentAnimator: AnimatorSet? = null

    init {
        parseAttributes(attrs)
        setupFAB()
        updateDimensions()
        updateColors()
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimatedFAB)
        try {
            fabSize = FABSize.values()[typedArray.getInt(R.styleable.AnimatedFAB_dsFabSize, 1)]
            fabState = FABState.values()[typedArray.getInt(R.styleable.AnimatedFAB_dsFabState, 0)]
            extendedText = typedArray.getString(R.styleable.AnimatedFAB_dsExtendedText) ?: ""
        } finally {
            typedArray.recycle()
        }
    }

    private fun setupFAB() {
        setBackgroundColor(Color.TRANSPARENT)
        scaleType = ScaleType.CENTER_INSIDE
        isClickable = true
    }

    override fun applyDesignSystemTheme() {
        backgroundColor = getColorFromTheme(R.attr.dsColorPrimaryContainer)
        iconColor = getColorFromTheme(R.attr.dsColorOnPrimaryContainer)
        rippleColor = iconColor
        loadingPaint.color = iconColor
        textPaint.color = iconColor
        updateColors()
        invalidate()
    }

    private fun getColorFromTheme(attr: Int): Int {
        val typedValue = android.util.TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    private fun updateDimensions() {
        when (fabSize) {
            FABSize.SMALL -> {
                fabSizePx = resources.getDimension(R.dimen.ds_fab_size_small)
                iconSize = 20f
            }
            FABSize.MEDIUM -> {
                fabSizePx = resources.getDimension(R.dimen.ds_fab_size_medium)
                iconSize = 24f
            }
            FABSize.LARGE -> {
                fabSizePx = resources.getDimension(R.dimen.ds_fab_size_large)
                iconSize = 28f
            }
            FABSize.EXTENDED -> {
                fabSizePx = resources.getDimension(R.dimen.ds_fab_size_extended_height)
                iconSize = 24f
            }
        }
        cornerRadius = fabSizePx / 2f
    }

    private fun updateColors() {
        backgroundPaint.color = backgroundColor
        ripplePaint.color = rippleColor
        setColorFilter(iconColor)
    }

    private fun updateState() {
        when (fabState) {
            FABState.NORMAL -> {
                rotationAngle = 0f
                loadingProgress = 0f
            }
            FABState.EXPANDED -> {
                // Handle expanded state if needed
            }
            FABState.LOADING -> {
                startLoadingAnimation()
            }
        }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = fabSizePx.toInt()

        val width = if (fabSize == FABSize.EXTENDED && extendedText.isNotEmpty()) {
            val textWidth = textPaint.measureText(extendedText)
            val iconSpace = if (drawable != null) iconSize + 12f else 0f
            (extendedPaddingHorizontal * 2 + iconSpace + textWidth).toInt()
        } else {
            height
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())

        // Draw background
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, backgroundPaint)

        // Draw loading indicator
        if (fabState == FABState.LOADING) {
            drawLoadingIndicator(canvas)
        }

        // Draw extended text if applicable
        if (fabSize == FABSize.EXTENDED && extendedText.isNotEmpty()) {
            drawExtendedText(canvas)
        }
    }

    private fun drawLoadingIndicator(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = Math.min(width, height) / 2f - 8f

        // Draw loading arc
        val oval = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        val sweepAngle = loadingProgress * 360f
        canvas.drawArc(oval, -90f, sweepAngle, false, loadingPaint)
    }

    private fun drawExtendedText(canvas: Canvas) {
        val iconSpace = if (drawable != null) iconSize + 12f else 0f
        val textX = extendedPaddingHorizontal + iconSpace
        val textY = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f

        canvas.drawText(extendedText, textX, textY, textPaint)
    }

    private fun startLoadingAnimation() {
        currentAnimator?.cancel()

        val progressAnim = ObjectAnimator.ofFloat(this, "loadingProgress", 0f, 1f).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }

        AnimatorSet().apply {
            play(progressAnim)
            duration = 1000
            start()
            currentAnimator = this
        }
    }

    /**
     * Expand the FAB with animation
     */
    fun expand() {
        if (fabState == FABState.EXPANDED) return

        currentAnimator?.cancel()

        val scaleAnim = ObjectAnimator.ofFloat(this, "scaleFactor", scaleFactor, 1.1f)
        val rotationAnim = ObjectAnimator.ofFloat(this, "rotationAngle", rotationAngle, 45f)

        AnimatorSet().apply {
            playTogether(scaleAnim, rotationAnim)
            duration = 200
            addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    fabState = FABState.EXPANDED
                }
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            start()
            currentAnimator = this
        }
    }

    /**
     * Collapse the FAB with animation
     */
    fun collapse() {
        if (fabState == FABState.NORMAL) return

        currentAnimator?.cancel()

        val scaleAnim = ObjectAnimator.ofFloat(this, "scaleFactor", scaleFactor, 1f)
        val rotationAnim = ObjectAnimator.ofFloat(this, "rotationAngle", rotationAngle, 0f)

        AnimatorSet().apply {
            playTogether(scaleAnim, rotationAnim)
            duration = 200
            addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    fabState = FABState.NORMAL
                }
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            start()
            currentAnimator = this
        }
    }

    /**
     * Show loading state
     */
    fun showLoading() {
        fabState = FABState.LOADING
    }

    /**
     * Hide loading state
     */
    fun hideLoading() {
        fabState = FABState.NORMAL
    }

    // Property setters for animations
    private fun setScaleFactor(scale: Float) {
        scaleFactor = scale
        scaleX = scale
        scaleY = scale
    }

    private fun setRotationAngle(angle: Float) {
        rotationAngle = angle
        rotation = angle
    }

    private fun setLoadingProgress(progress: Float) {
        loadingProgress = progress
        invalidate()
    }

    override fun setAnimationsEnabled(enabled: Boolean) {
        if (!enabled && currentAnimator?.isRunning == true) {
            currentAnimator?.cancel()
        }
    }

    override fun areAnimationsEnabled(): Boolean = true

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        currentAnimator?.cancel()
    }
}