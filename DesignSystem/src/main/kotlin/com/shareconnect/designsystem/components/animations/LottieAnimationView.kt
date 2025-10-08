package com.shareconnect.designsystem.components.animations

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.RawRes
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.shareconnect.designsystem.R
import com.shareconnect.designsystem.components.DesignSystemComponent

/**
 * Wrapper component for Lottie animations with design system integration.
 * Provides easy configuration and consistent animation behavior.
 */
class LottieAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), DesignSystemComponent {

    override val componentContext: Context = context

    private val lottieView: LottieAnimationView = LottieAnimationView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    // Animation properties
    var animationFile: String? = null
        set(value) {
            field = value
            value?.let { setAnimation(it) }
        }

    var autoPlay: Boolean = false
        set(value) {
            field = value
            if (value) playAnimation()
        }

    var loop: Boolean = false
        set(value) {
            field = value
            lottieView.repeatCount = if (value) LottieDrawable.INFINITE else 0
        }

    var animationSpeed: Float = 1f
        set(value) {
            field = value
            lottieView.speed = value
        }

    init {
        addView(lottieView)
        parseAttributes(attrs)
        setupAnimation()
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LottieAnimationView)
        try {
            animationFile = typedArray.getString(R.styleable.LottieAnimationView_animationFile)
            autoPlay = typedArray.getBoolean(R.styleable.LottieAnimationView_autoPlay, false)
            loop = typedArray.getBoolean(R.styleable.LottieAnimationView_loop, false)
            animationSpeed = typedArray.getFloat(R.styleable.LottieAnimationView_animationSpeed, 1f)
        } finally {
            typedArray.recycle()
        }
    }

    override fun applyDesignSystemTheme() {
        // Lottie animations are typically self-contained
    }

    private fun setupAnimation() {
        lottieView.apply {
            repeatCount = if (loop) LottieDrawable.INFINITE else 0
            speed = animationSpeed
        }

        animationFile?.let { setAnimation(it) }

        if (autoPlay) {
            playAnimation()
        }
    }

    /**
     * Set animation from assets folder
     */
    fun setAnimation(assetName: String) {
        lottieView.setAnimation(assetName)
    }

    /**
     * Set animation from raw resource
     */
    fun setAnimation(@RawRes resId: Int) {
        lottieView.setAnimation(resId)
    }

    /**
     * Play the animation
     */
    fun playAnimation() {
        if (areAnimationsEnabled()) {
            lottieView.playAnimation()
        }
    }

    /**
     * Pause the animation
     */
    fun pauseAnimation() {
        lottieView.pauseAnimation()
    }

    /**
     * Stop the animation
     */
    fun stopAnimation() {
        lottieView.cancelAnimation()
    }

    /**
     * Check if animation is currently playing
     */
    fun isAnimating(): Boolean = lottieView.isAnimating

    /**
     * Set animation progress (0.0 to 1.0)
     */
    fun setProgress(progress: Float) {
        lottieView.progress = progress
    }

    /**
     * Get current animation progress
     */
    fun getProgress(): Float = lottieView.progress

    /**
     * Set completion listener
     */
    fun setOnAnimationEndListener(listener: () -> Unit) {
        lottieView.addAnimatorListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {}
            override fun onAnimationEnd(animation: android.animation.Animator) {
                listener()
            }
            override fun onAnimationCancel(animation: android.animation.Animator) {}
            override fun onAnimationRepeat(animation: android.animation.Animator) {}
        })
    }

    override fun setAnimationsEnabled(enabled: Boolean) {
        if (!enabled && lottieView.isAnimating) {
            lottieView.pauseAnimation()
        }
    }

    override fun areAnimationsEnabled(): Boolean = true

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lottieView.cancelAnimation()
    }
}