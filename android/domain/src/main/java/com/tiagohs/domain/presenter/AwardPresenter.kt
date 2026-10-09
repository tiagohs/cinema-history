package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.IPresenter
import com.tiagohs.domain.views.AwardView
import com.tiagohs.entities.main_topics.AwardMainTopic

interface AwardPresenter : IPresenter<AwardView> {
    /** Carrega o índice de anos e o histórico ("Sobre") do prêmio. */
    fun fetchAwardsNominees(awardMainTopic: AwardMainTopic?)

    /** Carrega (em background) só o conteúdo do ano pedido. */
    fun fetchAwardYear(awardId: Int, year: String)
}
