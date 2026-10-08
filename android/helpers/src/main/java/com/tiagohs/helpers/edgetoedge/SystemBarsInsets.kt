package com.tiagohs.helpers.edgetoedge

import android.app.Activity
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * A partir do targetSdk 35 o Android força o modo edge-to-edge: as barras de sistema
 * ficam transparentes e o conteúdo é desenhado atrás delas.
 *
 * Este helper reproduz o comportamento antigo do app:
 *  - Telas com `android:windowTranslucentStatus` continuam desenhando atrás da status bar
 *    (o layout já foi pensado para isso, com fitsSystemWindows).
 *  - As demais telas recebem um espaçamento no topo pintado com a cor da status bar.
 *  - A barra de navegação recebe sempre um espaçamento pintado com [navigationBarColor].
 */
object SystemBarsInsets {

    private val TAG_KEY = "system_bars_background".hashCode()

    fun apply(activity: Activity, statusBarColor: Int? = null, navigationBarColor: Int = 0xFF000000.toInt()) {
        val content = activity.findViewById<ViewGroup>(android.R.id.content) ?: return
        val drawsBehindStatusBar = activity.themeBoolean(android.R.attr.windowTranslucentStatus)
        val resolvedStatusColor = statusBarColor
            ?: activity.themeColor(androidx.appcompat.R.attr.colorPrimaryDark)
            ?: 0xFF000000.toInt()

        val background = SystemBarsBackgroundDrawable(
            base = activity.window.decorView.background,
            statusBarColor = if (drawsBehindStatusBar) 0 else resolvedStatusColor,
            navigationBarColor = navigationBarColor
        )
        content.background = background
        content.setTag(TAG_KEY, background)

        WindowCompat.getInsetsController(activity.window, activity.window.decorView)
            .isAppearanceLightNavigationBars = false

        ViewCompat.setOnApplyWindowInsetsListener(content) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val top = if (drawsBehindStatusBar) 0 else bars.top
            val bottom = maxOf(bars.bottom, ime.bottom)

            view.updatePadding(left = bars.left, top = top, right = bars.right, bottom = bottom)
            background.topInset = top
            background.bottomInset = bars.bottom
            background.invalidateSelf()

            // Repassa para os filhos apenas o inset do topo quando a tela desenha atrás da status bar.
            WindowInsetsCompat.Builder(insets)
                .setInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
                    Insets.of(0, if (drawsBehindStatusBar) bars.top else 0, 0, 0)
                )
                .build()
        }
        ViewCompat.requestApplyInsets(content)
    }

    /** Atualiza a cor da área da status bar (usado por setStatusBarColor). */
    fun updateStatusBarColor(activity: Activity, color: Int) {
        val content = activity.findViewById<ViewGroup>(android.R.id.content) ?: return
        val background = content.getTag(TAG_KEY) as? SystemBarsBackgroundDrawable ?: return
        background.statusBarColor = color
        background.invalidateSelf()
    }

    private fun Activity.themeBoolean(attr: Int): Boolean {
        val a = theme.obtainStyledAttributes(intArrayOf(attr))
        return try { a.getBoolean(0, false) } finally { a.recycle() }
    }

    private fun Activity.themeColor(attr: Int): Int? {
        val value = TypedValue()
        return if (theme.resolveAttribute(attr, value, true)) value.data else null
    }
}

internal class SystemBarsBackgroundDrawable(
    private val base: Drawable?,
    var statusBarColor: Int,
    navigationBarColor: Int
) : Drawable() {

    var topInset = 0
    var bottomInset = 0

    private val statusPaint = Paint()
    private val navPaint = Paint().apply { color = navigationBarColor }

    override fun draw(canvas: Canvas) {
        val b = bounds
        base?.let {
            it.bounds = b
            it.draw(canvas)
        }
        if (topInset > 0 && statusBarColor != 0) {
            statusPaint.color = statusBarColor
            canvas.drawRect(b.left.toFloat(), b.top.toFloat(), b.right.toFloat(), (b.top + topInset).toFloat(), statusPaint)
        }
        if (bottomInset > 0) {
            canvas.drawRect(b.left.toFloat(), (b.bottom - bottomInset).toFloat(), b.right.toFloat(), b.bottom.toFloat(), navPaint)
        }
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
