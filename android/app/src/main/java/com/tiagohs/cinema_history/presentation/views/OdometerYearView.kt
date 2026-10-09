package com.tiagohs.cinema_history.presentation.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.tiagohs.cinema_history.R

/**
 * Ano grande (4 dígitos) desenhado direto no Canvas. Ao trocar de ano, só os dígitos que mudaram
 * "rolam" como num odômetro (para cima avançando no tempo, para baixo voltando).
 *
 * Feito para ser atualizado durante o scroll: tamanho fixo (nunca pede novo layout), sem alocação
 * ao trocar o ano ou ao desenhar, e um único ValueAnimator reaproveitado.
 */
class OdometerYearView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = context.resources.getDimension(R.dimen.timeline_year_watermark_size)
        typeface = try { ResourcesCompat.getFont(context, R.font.oswald_bold) } catch (e: Exception) { null }
        // Preto no tema claro, claro no escuro (é uma marca d'água com alpha baixo).
        color = ContextCompat.getColor(context, R.color.daynight_text_primary)
    }

    private val digitWidth: Float
    private val lineHeight: Float
    private val baseline: Float

    private val current = IntArray(DIGITS) { -1 }
    private val previous = IntArray(DIGITS) { -1 }
    private var year = NO_YEAR
    private var direction = 1
    private var progress = 1f

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 560
        interpolator = DecelerateInterpolator(1.6f)
        addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }
    }

    init {
        var widest = 0f
        for (i in 0..9) widest = maxOf(widest, paint.measureText(DIGIT_CHARS, i, 1))
        digitWidth = widest * 0.92f
        val metrics = paint.fontMetrics
        lineHeight = metrics.descent - metrics.ascent
        baseline = -metrics.ascent
    }

    var textColor: Int
        get() = paint.color
        set(value) {
            if (paint.color == value) return
            paint.color = value
            invalidate()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = (digitWidth * DIGITS).toInt() + paddingLeft + paddingRight
        val height = lineHeight.toInt() + paddingTop + paddingBottom
        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec))
    }

    /** Mostra [newYear] (ex.: 1895). Sem animação quando [animate] é falso ou é o primeiro ano. */
    fun setYear(newYear: Int, animate: Boolean) {
        if (newYear == year) return
        direction = if (newYear >= year) 1 else -1
        val first = year == NO_YEAR
        year = newYear

        var value = newYear
        for (i in DIGITS - 1 downTo 0) {
            previous[i] = current[i]
            current[i] = value % 10
            value /= 10
        }

        animator.cancel()
        if (!animate || first) {
            progress = 1f
            invalidate()
        } else {
            progress = 0f
            animator.start()
        }
        contentDescription = newYear.toString()
    }

    override fun onDraw(canvas: Canvas) {
        val top = paddingTop.toFloat()
        val clipBottom = top + lineHeight
        for (i in 0 until DIGITS) {
            val digit = current[i]
            if (digit < 0) continue
            val centerX = paddingLeft + digitWidth * (i + 0.5f)
            val old = previous[i]

            if (progress >= 1f || old == digit || old < 0) {
                canvas.drawText(DIGIT_CHARS, digit, 1, centerX, top + baseline, paint)
                continue
            }

            // Dígitos da direita começam antes (como um contador mecânico).
            val delay = (DIGITS - 1 - i) * STAGGER
            val local = ((progress - delay) / (1f - delay)).coerceIn(0f, 1f)
            val shift = local * lineHeight * direction

            canvas.save()
            canvas.clipRect(centerX - digitWidth, top, centerX + digitWidth, clipBottom)
            canvas.drawText(DIGIT_CHARS, old, 1, centerX, top + baseline - shift, paint)
            canvas.drawText(DIGIT_CHARS, digit, 1, centerX, top + baseline - shift + lineHeight * direction, paint)
            canvas.restore()
        }
    }

    override fun onDetachedFromWindow() {
        animator.cancel()
        progress = 1f
        super.onDetachedFromWindow()
    }

    companion object {
        private const val DIGITS = 4
        private const val NO_YEAR = Int.MIN_VALUE
        private const val STAGGER = 0.08f
        private val DIGIT_CHARS = "0123456789".toCharArray()
    }
}
