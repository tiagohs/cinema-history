package com.tiagohs.cinemahistory.shared.model

/** Estado que toda tela recebe do núcleo (UC-48): a UI nunca monta regra de carregamento ou erro por conta própria. */
sealed interface Resultado<out T> {
    data object Carregando : Resultado<Nothing>
    data class Sucesso<T>(val valor: T) : Resultado<T>
    data class Erro(val mensagem: String) : Resultado<Nothing>
}
