package com.tiagohs.helpers.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.*
import com.tiagohs.entities.enums.AnimationType

object AnimationUtils {

    fun createAnimationFromType(type: AnimationType, duration: Int): Animation {
        return when (type) {
            AnimationType.SHAKE_VERTICAL -> createShakeAnimation(duration)
            AnimationType.BLINK_VERTICAL -> createBlinkAnimation(duration)
        }
    }

    fun createShakeAnimation(duration: Int): Animation {
        val animation = TranslateAnimation(0f, 0f, -30f, 30f)
        animation.duration = duration.toLong()
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.REVERSE

        return animation
    }

    fun createBlinkAnimation(duration: Int): Animation {
        val animation = AlphaAnimation(0.7f, 1.0f)
        animation.duration = duration.toLong()
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.REVERSE

        return animation
    }

    fun createFadeInAnimation(duration: Int, startOffset: Long = 0, onFinished: (() -> Unit)? = null): Animation {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = duration.toLong()
        fadeIn.startOffset = startOffset
        fadeIn.fillAfter = true

        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                onFinished?.invoke()
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })

        return fadeIn
    }

    fun createFadeOutAnimation(duration: Int): Animation {
        val fadeIn = AlphaAnimation(1f, 0f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = duration.toLong()

        return fadeIn
    }

    fun createScaleAnimation(scaleTo: Float, duration: Long, v: View, onFinished: (() -> Unit)? = null) {
        v.animate()
            .scaleXBy(scaleTo)
            .scaleYBy(scaleTo)
            .setDuration(duration)
            .withEndAction {
                v.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(duration)
                    .withEndAction {
                        onFinished?.invoke()
                    }
                    .start()
            }
            .start()
    }

    fun createShowCircularReveal(view: View, onFinished: (() -> Unit)? = null) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val cx = (view.left + view.right) / 2
            val cy = (view.top + view.bottom) / 2
            val finalRadius = Math.max(view.width, view.height)

            val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius.toFloat())
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    onFinished?.invoke()
                }

                override fun onAnimationCancel(animation: Animator?) {
                    onFinished?.invoke()
                }

                override fun onAnimationStart(animation: Animator?) {

                }

            })
            anim.start()
        } else
            view.visibility = View.VISIBLE
    }

    fun createShowCircularReveal(view: View, listener: Animator.AnimatorListener) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val cx = (view.left + view.right) / 2
            val cy = (view.top + view.bottom) / 2
            val finalRadius = Math.max(view.width, view.height)

            val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius.toFloat())
            anim.addListener(listener)
            anim.start()
        } else
            view.visibility = View.VISIBLE
    }

    fun createHideCircularReveal(view: View) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val cx = (view.left + view.right) / 2
            val cy = (view.top + view.bottom) / 2
            val startRadius = Math.max(view.width, view.height)

            val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, startRadius.toFloat(), 0f)
            anim.start()
        } else
            view.visibility = View.GONE
    }

    fun createHideCircularReveal(view: View, listener: Animator.AnimatorListener) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val cx = (view.left + view.right) / 2
            val cy = (view.top + view.bottom) / 2
            val startRadius = Math.max(view.width, view.height)

            val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, startRadius.toFloat(), 0f)
            anim.addListener(listener)
            anim.start()
        } else
            view.visibility = View.GONE
    }

    fun createScaleUpAnimation(
        view: View,
        fromX: Float,
        toX: Float,
        fromY: Float,
        toY: Float,
        pivotX: Float,
        pivotY: Float,
        duration: Int,
        startOffset: Long = 0,
        onFinished: (() -> Unit)? = null
    ) {
        val scale = ScaleAnimation(
            fromX,
            toX,
            fromY,
            toY,
            Animation.RELATIVE_TO_SELF,
            pivotX,
            Animation.RELATIVE_TO_SELF,
            pivotY)

        scale.duration = duration.toLong()     // animation duration in milliseconds
        scale.fillAfter = true    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        scale.interpolator = AccelerateInterpolator(2f)
        scale.startOffset = startOffset
        scale.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                onFinished?.invoke()
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
        view.startAnimation(scale)

    }

    fun createPulseAnimation(view: View, toX: Float = 1.2f, toY: Float = 1.2f) {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, "scaleY", 1f, toY, 1f, toY, 1f),
            ObjectAnimator.ofFloat(view, "scaleX", 1f, toX, 1f, toX, 1f))

        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.duration = 600
        animatorSet.start()
    }
}