package com.tiagohs.cinemahistory.shared.bridge

import kotlinx.coroutines.Job

/**
 * Alça que o Swift guarda para cancelar um trabalho do núcleo (regra 2 da ponte).
 * Necessária porque cancelar a Task do Swift não cancela a corrotina na exportação padrão (medido no contrato S4a).
 * No Swift, o helper `aguardar` liga esta alça ao cancelamento da Task.
 */
class Cancelavel(private val job: Job) {
    val ativo: Boolean get() = job.isActive
    fun cancelar() = job.cancel()
}
