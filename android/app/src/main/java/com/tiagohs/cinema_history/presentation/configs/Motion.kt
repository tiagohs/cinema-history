package com.tiagohs.cinema_history.presentation.configs

import android.content.Context
import android.provider.Settings

/**
 * Respeita a opção do sistema "Remover animações" (Configurações > Acessibilidade) e a escala de
 * animação 0 das opções do desenvolvedor: nesses casos as telas não fazem animações nem efeitos
 * ligados ao scroll (parallax, linha que se desenha, entradas escalonadas).
 */
object Motion {

    fun enabled(context: Context?): Boolean {
        context ?: return true
        val scale = try {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        } catch (e: Exception) {
            1f
        }
        return scale != 0f
    }
}
