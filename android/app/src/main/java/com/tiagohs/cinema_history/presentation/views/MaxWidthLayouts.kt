package com.tiagohs.cinema_history.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.tiagohs.cinema_history.R

/**
 * Containers com largura máxima (como `max-width` no CSS), para telas grandes.
 *
 * O próprio container nunca mede mais que `app:ls_maxWidth`; quem o centraliza é o pai
 * (`android:layout_gravity="center_horizontal"` em FrameLayout/ScrollView/LinearLayout, ou
 * constraints dos dois lados no ConstraintLayout). Com `ls_maxWidth = 0` (valor do celular,
 * ver values/dimens_large_screen.xml) não muda nada.
 */
private fun readMaxWidth(context: Context, attrs: AttributeSet?): Int {
    if (attrs == null) return 0
    val a = context.obtainStyledAttributes(attrs, R.styleable.MaxWidthLayout)
    return try {
        a.getDimensionPixelSize(R.styleable.MaxWidthLayout_ls_maxWidth, 0)
    } finally {
        a.recycle()
    }
}

private fun clampWidthSpec(widthMeasureSpec: Int, maxWidth: Int): Int {
    if (maxWidth <= 0) return widthMeasureSpec
    val mode = View.MeasureSpec.getMode(widthMeasureSpec)
    val size = View.MeasureSpec.getSize(widthMeasureSpec)
    return when (mode) {
        View.MeasureSpec.UNSPECIFIED -> View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST)
        else -> if (size > maxWidth) View.MeasureSpec.makeMeasureSpec(maxWidth, mode) else widthMeasureSpec
    }
}

class MaxWidthFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var maxContentWidth: Int = readMaxWidth(context, attrs)
        set(value) {
            field = value
            requestLayout()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(clampWidthSpec(widthMeasureSpec, maxContentWidth), heightMeasureSpec)
    }
}

class MaxWidthLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var maxContentWidth: Int = readMaxWidth(context, attrs)
        set(value) {
            field = value
            requestLayout()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(clampWidthSpec(widthMeasureSpec, maxContentWidth), heightMeasureSpec)
    }
}
