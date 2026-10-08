package com.tiagohs.uicomponents.alertsnack

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.ContentViewCallback
import com.tiagohs.uicomponents.R
import com.tiagohs.uicomponents.databinding.LayoutSnackbarBaseAlertBinding

class AlertSnackBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    val binding = LayoutSnackbarBaseAlertBinding.inflate(LayoutInflater.from(context), this)

    init {
        clipToPadding = false
    }

    override fun animateContentIn(delay: Int, duration: Int) {

    }

    override fun animateContentOut(delay: Int, duration: Int) {
    }
}