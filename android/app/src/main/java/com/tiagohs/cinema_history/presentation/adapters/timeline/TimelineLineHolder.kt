package com.tiagohs.cinema_history.presentation.adapters.timeline

import android.view.View

/**
 * ViewHolder com um trecho da linha vertical que "se desenha" conforme o scroll.
 * O preenchimento é só um scaleY (pivot no topo): sem novo layout por frame.
 */
interface TimelineLineHolder {
    val lineFill: View
    var lastLineProgress: Float

    fun setLineProgress(progress: Float) {
        if (progress == lastLineProgress) return
        lastLineProgress = progress
        lineFill.scaleY = progress
    }
}
