package com.tiagohs.cinema_history.presentation.views

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator

/**
 * Pôster que cresce levemente ao tocar ou receber foco (D-pad/teclado), como nas fileiras dos
 * apps de streaming. Uma única instância é compartilhada por todos os cartões (sem alocação por item);
 * só mexe em scale/translationZ (propriedades do RenderNode, sem novo layout).
 */
class PressScale(
    private val scale: Float = 1.06f,
    private val liftPx: Float = 0f
) : View.OnTouchListener, View.OnFocusChangeListener {

    var enabled: Boolean = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (!enabled) return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> grow(v)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (!v.isFocused) shrink(v)
        }
        return false
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (!enabled) return
        if (hasFocus) grow(v) else shrink(v)
    }

    private fun grow(v: View) {
        v.animate()
            .scaleX(scale)
            .scaleY(scale)
            .translationZ(liftPx)
            .setDuration(140)
            .setInterpolator(DECELERATE)
            .start()
    }

    private fun shrink(v: View) {
        v.animate()
            .scaleX(1f)
            .scaleY(1f)
            .translationZ(0f)
            .setDuration(220)
            .setInterpolator(OVERSHOOT)
            .start()
    }

    companion object {
        private val DECELERATE = DecelerateInterpolator()
        private val OVERSHOOT = OvershootInterpolator(1.6f)

        /** Volta o cartão ao estado normal (ao reciclar o ViewHolder). */
        fun reset(v: View) {
            v.animate().cancel()
            v.scaleX = 1f
            v.scaleY = 1f
            v.translationZ = 0f
        }
    }
}
