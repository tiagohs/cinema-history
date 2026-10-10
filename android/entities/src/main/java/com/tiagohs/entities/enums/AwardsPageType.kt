package com.tiagohs.entities.enums

import com.tiagohs.entities.R
import java.io.Serializable

/**
 * Abas da tela de prêmios, na ordem em que aparecem.
 * A primeira aba é a de indicados/vencedores; o histórico ("Sobre") vem em seguida.
 */
enum class AwardsPageType(
    val screenName: Int
): Serializable {
    NOMINEES(R.string.award_nominees),
    HISTORY(R.string.award_history);
}
