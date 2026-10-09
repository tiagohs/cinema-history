package com.tiagohs.cinema_history.presentation.fragments

import com.tiagohs.cinema_history.presentation.adapters.awards.AwardActions
import com.tiagohs.entities.awards.AwardYearSummary
import com.tiagohs.entities.awards.NomineeResult

/**
 * Contrato entre a AwardActivity (hero, presenter, navegação) e as abas.
 * Só o ano aberto é carregado; a aba recebe o resultado por [AwardYearListener].
 */
interface AwardScreenHost {
    val awardActions: AwardActions
    fun selectedYear(): String?
    fun selectYear(summary: AwardYearSummary)
    fun retrySelectedYear()

    /** Registra a aba; o host repassa na hora o estado atual (carregando, carregado ou erro). */
    fun setYearListener(listener: AwardYearListener?)
}

interface AwardYearListener {
    fun onYearLoading(year: String)
    fun onYearLoaded(year: String, result: NomineeResult)
    fun onYearFailed(year: String)
}
