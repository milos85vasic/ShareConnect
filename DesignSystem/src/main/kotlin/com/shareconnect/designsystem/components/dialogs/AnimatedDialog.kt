package com.shareconnect.designsystem.components.dialogs

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import com.shareconnect.designsystem.R
import com.shareconnect.designsystem.components.DesignSystemComponent

/**
 * Animated dialog component with entrance/exit animations and modern design.
 * Supports different dialog types and customizable content.
 */
class AnimatedDialog private constructor(
    context: Context,
    private val builder: Builder
) : Dialog(context, R.style.AnimatedDialogTheme) {

    enum class DialogType {
        INFO,
        SUCCESS,
        WARNING,
        ERROR,
        CONFIRMATION
    }

    enum class AnimationType {
        SCALE,
        SLIDE_UP,
        SLIDE_DOWN,
        FADE
    }

    private var dialogView: View? = null
    private var currentAnimator: AnimatorSet? = null
    private var animationDuration: Long = 300

    init {
        setupDialog()
        createContentView()
    }

    private fun setupDialog() {
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.CENTER)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

            // Set dim amount for backdrop
            setDimAmount(0.5f)
        }

        setCancelable(builder.cancelable)
        setCanceledOnTouchOutside(builder.cancelable)
    }

    private fun createContentView() {
        val container = FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(32, 32, 32, 32)
        }

        // Create dialog content card
        val contentCard = createContentCard()
        container.addView(contentCard)

        dialogView = container
        setContentView(container)
    }

    private fun createContentCard(): View {
        return FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 16
                marginEnd = 16
            }

            // Set background with rounded corners
            background = createRoundedBackground()
            elevation = 8f

            // Add content
            addView(createDialogContent())
        }
    }

    private fun createRoundedBackground(): android.graphics.drawable.Drawable {
        return android.graphics.drawable.GradientDrawable().apply {
            cornerRadius = 16f
            setColor(getColorFromTheme(R.attr.dsColorSurface))
        }
    }

    private fun createDialogContent(): View {
        return FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(24, 24, 24, 24)

            // Add icon if specified
            builder.iconRes?.let { iconRes ->
                addView(createIconView(iconRes))
            }

            // Add title
            builder.title?.let { title ->
                addView(createTitleView(title))
            }

            // Add message
            builder.message?.let { message ->
                addView(createMessageView(message))
            }

            // Add buttons
            addView(createButtonContainer())
        }
    }

    private fun createIconView(iconRes: Int): View {
        return android.widget.ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(48, 48).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = 0
                bottomMargin = 16
            }
            setImageResource(iconRes)
            setColorFilter(getIconColor())
        }
    }

    private fun createTitleView(title: String): View {
        return TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = if (builder.iconRes != null) 0 else 0
                bottomMargin = 8
            }
            text = title
            textSize = 20f
            setTextColor(getColorFromTheme(R.attr.dsColorOnSurface))
            gravity = Gravity.CENTER_HORIZONTAL
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
    }

    private fun createMessageView(message: String): View {
        return TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
                bottomMargin = 24
            }
            text = message
            textSize = 16f
            setTextColor(getColorFromTheme(R.attr.dsColorOnSurfaceVariant))
            gravity = Gravity.CENTER_HORIZONTAL
            setLineSpacing(4f, 1.2f)
        }
    }

    private fun createButtonContainer(): View {
        return android.widget.LinearLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM
            }
            orientation = android.widget.LinearLayout.HORIZONTAL
            gravity = Gravity.END

            // Add negative button if specified
            builder.negativeButtonText?.let { text ->
                addView(createButton(text, false))
            }

            // Add positive button
            builder.positiveButtonText?.let { text ->
                if (builder.negativeButtonText != null) {
                    // Add space between buttons
                    addView(android.view.View(context).apply {
                        layoutParams = android.widget.LinearLayout.LayoutParams(16, 0)
                    })
                }
                addView(createButton(text, true))
            }
        }
    }

    private fun createButton(text: String, isPositive: Boolean): View {
        return android.widget.Button(context).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(
                0,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            this.text = text
            textSize = 16f
            isAllCaps = true
            setTextColor(if (isPositive) Color.WHITE else getColorFromTheme(R.attr.dsColorPrimary))
            background = createButtonBackground(isPositive)
            setPadding(24, 12, 24, 12)

            setOnClickListener {
                if (isPositive) {
                    builder.positiveButtonClickListener?.invoke(this@AnimatedDialog)
                } else {
                    builder.negativeButtonClickListener?.invoke(this@AnimatedDialog)
                }
                dismissWithAnimation()
            }
        }
    }

    private fun createButtonBackground(isPositive: Boolean): android.graphics.drawable.Drawable {
        return android.graphics.drawable.GradientDrawable().apply {
            cornerRadius = 8f
            if (isPositive) {
                setColor(getColorFromTheme(R.attr.dsColorPrimary))
            } else {
                setColor(Color.TRANSPARENT)
            }
        }
    }

    private fun getIconColor(): Int {
        return when (builder.dialogType) {
            DialogType.SUCCESS -> getColorFromTheme(R.attr.dsColorSuccess)
            DialogType.WARNING -> getColorFromTheme(R.attr.dsColorWarning)
            DialogType.ERROR -> getColorFromTheme(R.attr.dsColorError)
            else -> getColorFromTheme(R.attr.dsColorPrimary)
        }
    }

    private fun getColorFromTheme(attr: Int): Int {
        val typedValue = android.util.TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    override fun show() {
        super.show()
        animateEntrance()
    }

    override fun dismiss() {
        animateExit {
            super.dismiss()
        }
    }

    private fun dismissWithAnimation() {
        animateExit {
            super.dismiss()
        }
    }

    private fun animateEntrance() {
        dialogView?.let { view ->
            when (builder.animationType) {
                AnimationType.SCALE -> animateScaleEntrance(view)
                AnimationType.SLIDE_UP -> animateSlideUpEntrance(view)
                AnimationType.SLIDE_DOWN -> animateSlideDownEntrance(view)
                AnimationType.FADE -> animateFadeEntrance(view)
            }
        }
    }

    private fun animateExit(onComplete: () -> Unit) {
        dialogView?.let { view ->
            when (builder.animationType) {
                AnimationType.SCALE -> animateScaleExit(view, onComplete)
                AnimationType.SLIDE_UP -> animateSlideUpExit(view, onComplete)
                AnimationType.SLIDE_DOWN -> animateSlideDownExit(view, onComplete)
                AnimationType.FADE -> animateFadeExit(view, onComplete)
            }
        } ?: onComplete()
    }

    private fun animateScaleEntrance(view: View) {
        view.scaleX = 0.8f
        view.scaleY = 0.8f
        view.alpha = 0f

        val scaleXAnim = ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1f)
        val scaleYAnim = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1f)
        val alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

        AnimatorSet().apply {
            playTogether(scaleXAnim, scaleYAnim, alphaAnim)
            duration = animationDuration
            start()
            currentAnimator = this
        }
    }

    private fun animateScaleExit(view: View, onComplete: () -> Unit) {
        val scaleXAnim = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.8f)
        val scaleYAnim = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.8f)
        val alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)

        AnimatorSet().apply {
            playTogether(scaleXAnim, scaleYAnim, alphaAnim)
            duration = animationDuration
            addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onComplete()
                }
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            start()
            currentAnimator = this
        }
    }

    private fun animateSlideDownEntrance(view: View) {
        view.translationY = -100f
        view.alpha = 0f

        val translateAnim = ObjectAnimator.ofFloat(view, "translationY", -100f, 0f)
        val alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

        AnimatorSet().apply {
            playTogether(translateAnim, alphaAnim)
            duration = animationDuration
            start()
            currentAnimator = this
        }
    }

    private fun animateSlideDownExit(view: View, onComplete: () -> Unit) {
        val translateAnim = ObjectAnimator.ofFloat(view, "translationY", 0f, 100f)
        val alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)

        AnimatorSet().apply {
            playTogether(translateAnim, alphaAnim)
            duration = animationDuration
            addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onComplete()
                }
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            start()
            currentAnimator = this
        }
    }

    private fun animateSlideUpEntrance(view: View) {
        view.translationY = 100f
        view.alpha = 0f

        val translateAnim = ObjectAnimator.ofFloat(view, "translationY", 100f, 0f)
        val alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

        AnimatorSet().apply {
            playTogether(translateAnim, alphaAnim)
            duration = animationDuration
            start()
            currentAnimator = this
        }
    }

    private fun animateSlideUpExit(view: View, onComplete: () -> Unit) {
        val translateAnim = ObjectAnimator.ofFloat(view, "translationY", 0f, -100f)
        val alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)

        AnimatorSet().apply {
            playTogether(translateAnim, alphaAnim)
            duration = animationDuration
            addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onComplete()
                }
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            start()
            currentAnimator = this
        }
    }

    private fun animateFadeEntrance(view: View) {
        view.alpha = 0f

        val alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

        AnimatorSet().apply {
            play(alphaAnim)
            duration = animationDuration
            start()
            currentAnimator = this
        }
    }

    private fun animateFadeExit(view: View, onComplete: () -> Unit) {
        val alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)

        AnimatorSet().apply {
            play(alphaAnim)
            duration = animationDuration
            addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onComplete()
                }
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            start()
            currentAnimator = this
        }
    }



    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        currentAnimator?.cancel()
    }

    class Builder(private val context: Context) {
        var dialogType: DialogType = DialogType.INFO
        var animationType: AnimationType = AnimationType.SCALE
        var title: String? = null
        var message: String? = null
        var iconRes: Int? = null
        var positiveButtonText: String? = null
        var negativeButtonText: String? = null
        var positiveButtonClickListener: ((AnimatedDialog) -> Unit)? = null
        var negativeButtonClickListener: ((AnimatedDialog) -> Unit)? = null
        var cancelable: Boolean = true

        fun setType(type: DialogType) = apply { dialogType = type }
        fun setAnimationType(type: AnimationType) = apply { animationType = type }
        fun setTitle(title: String) = apply { this.title = title }
        fun setTitle(@StringRes resId: Int) = apply { this.title = context.getString(resId) }
        fun setMessage(message: String) = apply { this.message = message }
        fun setMessage(@StringRes resId: Int) = apply { this.message = context.getString(resId) }
        fun setIcon(@androidx.annotation.DrawableRes iconRes: Int) = apply { this.iconRes = iconRes }
        fun setPositiveButton(text: String, listener: ((AnimatedDialog) -> Unit)? = null) = apply {
            positiveButtonText = text
            positiveButtonClickListener = listener
        }
        fun setPositiveButton(@StringRes resId: Int, listener: ((AnimatedDialog) -> Unit)? = null) = apply {
            positiveButtonText = context.getString(resId)
            positiveButtonClickListener = listener
        }
        fun setNegativeButton(text: String, listener: ((AnimatedDialog) -> Unit)? = null) = apply {
            negativeButtonText = text
            negativeButtonClickListener = listener
        }
        fun setNegativeButton(@StringRes resId: Int, listener: ((AnimatedDialog) -> Unit)? = null) = apply {
            negativeButtonText = context.getString(resId)
            negativeButtonClickListener = listener
        }
        fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }

        fun build() = AnimatedDialog(context, this)
        fun show() = build().apply { show() }
    }
}