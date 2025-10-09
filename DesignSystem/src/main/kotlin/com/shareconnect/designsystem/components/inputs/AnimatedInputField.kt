package com.shareconnect.designsystem.components.inputs

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.shareconnect.designsystem.R
import com.shareconnect.designsystem.components.DesignSystemComponent

/**
 * Animated input field component with validation states, floating labels, and modern design.
 * Supports different input types and validation feedback.
 */
class AnimatedInputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr), DesignSystemComponent, TextWatcher {

    override val componentContext: Context = context

    enum class ValidationState {
        NONE,
        VALID,
        INVALID,
        WARNING
    }

    // Input properties
    var hintText: String = ""
        set(value) {
            field = value
            invalidate()
        }

    var helperText: String = ""
        set(value) {
            field = value
            invalidate()
        }

    var validationState: ValidationState = ValidationState.NONE
        set(value) {
            field = value
            updateValidationAppearance()
            invalidate()
        }

    var errorMessage: String = ""
        set(value) {
            field = value
            if (validationState == ValidationState.INVALID) {
                invalidate()
            }
        }

    // Colors
    @ColorInt private var borderColor: Int = Color.LTGRAY
    @ColorInt private var focusedBorderColor: Int = Color.BLUE
    @ColorInt private var errorBorderColor: Int = Color.RED
    @ColorInt private var validBorderColor: Int = Color.GREEN
    @ColorInt private var warningBorderColor: Int = Color.YELLOW
    @ColorInt private var hintTextColor: Int = Color.GRAY
    @ColorInt private var helperTextColor: Int = Color.GRAY

    // Dimensions
    private var borderWidth: Float = 2f
    private var cornerRadius: Float = 8f
    private var paddingHorizontal: Float = 16f
    private var paddingVertical: Float = 16f

    // Animation properties
    private var borderAlpha: Int = 255
    private var labelOffset: Float = 0f
    private var labelScale: Float = 1f
    private var focusedLabelOffset: Float = -24f
    private var focusedLabelScale: Float = 0.8f

    // Paints
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = borderWidth
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val hintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 16f
        color = hintTextColor
    }

    private val helperPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 14f
        color = helperTextColor
    }

    // Animation
    private var currentAnimator: AnimatorSet? = null
    private var isFocused = false

    init {
        parseAttributes(attrs)
        setupInputField()
        updateValidationAppearance()
        addTextChangedListener(this)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimatedInputField)
        try {
            hintText = typedArray.getString(R.styleable.AnimatedInputField_hintText) ?: ""
            helperText = typedArray.getString(R.styleable.AnimatedInputField_helperText) ?: ""
        } finally {
            typedArray.recycle()
        }
    }

    private fun setupInputField() {
        setPadding(
            paddingHorizontal.toInt(),
            (paddingVertical + 24f).toInt(), // Extra padding for floating label
            paddingHorizontal.toInt(),
            paddingVertical.toInt()
        )
        setBackgroundColor(Color.TRANSPARENT)
        hint = null // We handle hint ourselves
    }

    override fun applyDesignSystemTheme() {
        borderColor = getColorFromTheme(R.attr.dsColorSurfaceVariant)
        focusedBorderColor = getColorFromTheme(R.attr.dsColorPrimary)
        errorBorderColor = getColorFromTheme(R.attr.dsColorError)
        validBorderColor = getColorFromTheme(R.attr.dsColorSuccess)
        warningBorderColor = getColorFromTheme(R.attr.dsColorWarning)
        hintTextColor = getColorFromTheme(R.attr.dsColorOnSurfaceVariant)
        helperTextColor = getColorFromTheme(R.attr.dsColorOnSurfaceVariant)

        backgroundPaint.color = getColorFromTheme(R.attr.dsColorSurface)
        updateValidationAppearance()
        invalidate()
    }

    private fun getColorFromTheme(attr: Int): Int {
        val typedValue = android.util.TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    private fun updateValidationAppearance() {
        borderPaint.color = when (validationState) {
            ValidationState.NONE -> if (isFocused) focusedBorderColor else borderColor
            ValidationState.VALID -> validBorderColor
            ValidationState.INVALID -> errorBorderColor
            ValidationState.WARNING -> warningBorderColor
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val rect = RectF(
            borderWidth / 2,
            borderWidth / 2,
            width - borderWidth / 2,
            height - borderWidth / 2
        )

        // Draw background
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, backgroundPaint)

        // Draw border
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)

        // Draw floating hint label
        if (hintText.isNotEmpty()) {
            val labelX = paddingHorizontal
            val labelY = paddingVertical + hintPaint.textSize + labelOffset

            canvas.save()
            canvas.translate(labelX, labelY)
            canvas.scale(labelScale, labelScale)
            canvas.drawText(hintText, 0f, 0f, hintPaint)
            canvas.restore()
        }

        // Draw helper text
        val displayText = when (validationState) {
            ValidationState.INVALID -> errorMessage
            else -> helperText
        }

        if (displayText.isNotEmpty()) {
            val helperY = height + helperPaint.textSize + 8f
            canvas.drawText(displayText, paddingHorizontal, helperY, helperPaint)
        }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: android.graphics.Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        isFocused = focused
        animateLabel()
        updateValidationAppearance()
        invalidate()
    }

    private fun animateLabel() {
        currentAnimator?.cancel()

        val shouldFloat = isFocused || text?.isNotEmpty() == true

        val offsetAnim = ObjectAnimator.ofFloat(
            this,
            "labelOffset",
            labelOffset,
            if (shouldFloat) focusedLabelOffset else 0f
        )

        val scaleAnim = ObjectAnimator.ofFloat(
            this,
            "labelScale",
            labelScale,
            if (shouldFloat) focusedLabelScale else 1f
        )

        AnimatorSet().apply {
            playTogether(offsetAnim, scaleAnim)
            duration = 200
            start()
            currentAnimator = this
        }
    }

    // TextWatcher implementation
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        animateLabel()
    }

    override fun afterTextChanged(s: Editable?) {}

    // Property setters for animations
    private fun setLabelOffset(offset: Float) {
        labelOffset = offset
        invalidate()
    }

    private fun setLabelScale(scale: Float) {
        labelScale = scale
        invalidate()
    }

    /**
     * Set validation state with optional error message
     */
    fun setValidationState(state: ValidationState, errorMessage: String = "") {
        validationState = state
        this.errorMessage = errorMessage
    }

    /**
     * Clear validation state
     */
    fun clearValidation() {
        validationState = ValidationState.NONE
        errorMessage = ""
    }

    override fun setAnimationsEnabled(enabled: Boolean) {
        // Store animation state if needed
    }

    override fun areAnimationsEnabled(): Boolean = true

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        currentAnimator?.cancel()
        removeTextChangedListener(this)
    }
}