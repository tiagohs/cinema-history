package com.tiagohs.cinemahistory.shared.spike

import kotlinx.coroutines.Job

/** Alça que o Swift guarda para cancelar um trabalho do núcleo (padrão para cancelamento sem plugin). */
class Cancelavel internal constructor(private val job: Job) {
    val ativo: Boolean get() = job.isActive
    fun cancelar() = job.cancel()
}
