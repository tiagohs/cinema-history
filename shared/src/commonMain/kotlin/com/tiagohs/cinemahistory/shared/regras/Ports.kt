package com.tiagohs.cinemahistory.shared.regras

/**
 * Preferências guardadas no aparelho (UserDefaults no iOS). Implementada em Swift; nos testes, em memória.
 * Sem tipos opcionais de número (regra 1 da ponte): cada leitura recebe o valor padrão.
 */
interface Preferencias {
    fun texto(chave: String): String?
    fun gravarTexto(chave: String, valor: String?)
    fun longo(chave: String, padrao: Long): Long
    fun gravarLongo(chave: String, valor: Long)
    fun logico(chave: String, padrao: Boolean): Boolean
    fun gravarLogico(chave: String, valor: Boolean)
    fun remover(chave: String)
}

/** Relógio do aparelho: instante atual e o dia no fuso local (para limites diários). */
interface Relogio {
    fun agoraMs(): Long

    /** Identificador do dia local, igual ao Android: ano * 1000 + dia do ano. */
    fun diaLocal(): Int
}

/** Preferências em memória (testes e pré-visualizações). */
class PreferenciasEmMemoria : Preferencias {
    private val valores = mutableMapOf<String, Any?>()
    override fun texto(chave: String): String? = valores[chave] as? String
    override fun gravarTexto(chave: String, valor: String?) { valores[chave] = valor }
    override fun longo(chave: String, padrao: Long): Long = valores[chave] as? Long ?: padrao
    override fun gravarLongo(chave: String, valor: Long) { valores[chave] = valor }
    override fun logico(chave: String, padrao: Boolean): Boolean = valores[chave] as? Boolean ?: padrao
    override fun gravarLogico(chave: String, valor: Boolean) { valores[chave] = valor }
    override fun remover(chave: String) { valores.remove(chave) }
}
