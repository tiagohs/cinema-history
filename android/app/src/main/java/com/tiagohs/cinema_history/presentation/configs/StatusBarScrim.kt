package com.tiagohs.cinema_history.presentation.configs

import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * Nas telas que desenham atrás da status bar (imagem no topo), o CoordinatorLayout com
 * fitsSystemWindows pintava a área da status bar com colorPrimaryDark (preto), escondendo a imagem.
 * No Android 15+ (edge-to-edge obrigatório) isso virou uma faixa sólida. Aqui ela vira um véu
 * translúcido, como a status bar translúcida antiga.
 */
object StatusBarScrim {
    const val COLOR = 0x33000000

    fun apply(root: View?) {
        when (root) {
            is CoordinatorLayout -> root.setStatusBarBackgroundColor(COLOR)
        }
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) apply(root.getChildAt(i))
        }
    }
}
