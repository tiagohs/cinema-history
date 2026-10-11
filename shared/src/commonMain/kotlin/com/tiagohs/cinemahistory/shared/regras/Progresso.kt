package com.tiagohs.cinemahistory.shared.regras

/** Posição salva do áudio de um capítulo (UC-23). */
data class PosicaoSalvaDoAudio(val faixa: String, val ms: Long)

/**
 * Progresso e preferências do leitor (UC-02, UC-04, UC-07, UC-14, UC-23, UC-47), guardados no aparelho.
 * Valores ausentes usam padrões em vez de opcionais numéricos (regra 1 da ponte).
 */
class Progresso(private val prefs: Preferencias) {

    // UC-02 · onboarding só na primeira abertura
    var onboardingConcluido: Boolean
        get() = prefs.logico("app.onboarding_concluido", false)
        set(v) = prefs.gravarLogico("app.onboarding_concluido", v)

    /** UC-07 · idioma escolhido no app (pt, en, es) ou null para seguir o aparelho. */
    var idiomaEscolhido: String?
        get() = prefs.texto("app.idioma")
        set(v) = prefs.gravarTexto("app.idioma", v)

    /** "sistema", "claro" ou "escuro". */
    var tema: String
        get() = prefs.texto("app.tema") ?: "sistema"
        set(v) = prefs.gravarTexto("app.tema", v)

    // UC-14 · capítulos lidos por era
    fun marcarLido(era: Int, capitulo: Int) {
        prefs.gravarTexto(chaveLidos(era), (capitulosLidos(era) + capitulo).sorted().joinToString(","))
    }

    /** Quantos capítulos da era já foram lidos (para o "3 de 9" do sumário). */
    fun quantidadeDeLidos(era: Int): Int = capitulosLidos(era).size

    internal fun capitulosLidos(era: Int): Set<Int> =
        prefs.texto(chaveLidos(era))?.split(',')?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()

    fun estaLido(era: Int, capitulo: Int): Boolean = capitulo in capitulosLidos(era)

    // UC-04 · "Continue lendo" = última era e capítulo abertos (0 = nenhum)
    fun registrarLeitura(era: Int, capitulo: Int) {
        prefs.gravarLongo("leitura.ultima_era", era.toLong())
        prefs.gravarLongo("leitura.ultimo_capitulo.$era", capitulo.toLong())
    }

    val ultimaEra: Int get() = prefs.longo("leitura.ultima_era", 0).toInt()

    fun ultimoCapitulo(era: Int): Int = prefs.longo("leitura.ultimo_capitulo.$era", 0).toInt()

    /** Posição de rolagem do capítulo, em pontos (0 = topo). */
    fun salvarPosicaoDeLeitura(era: Int, capitulo: Int, posicao: Long) = prefs.gravarLongo("leitura.posicao.$era.$capitulo", posicao)

    fun posicaoDeLeitura(era: Int, capitulo: Int): Long = prefs.longo("leitura.posicao.$era.$capitulo", 0)

    // UC-23 · áudio
    fun salvarPosicaoDoAudio(chave: ChaveDoCapitulo, faixa: String, ms: Long) = prefs.gravarTexto("audio.posicao.${chave.id}", "$faixa|$ms")

    fun posicaoDoAudio(chave: ChaveDoCapitulo): PosicaoSalvaDoAudio? {
        val (faixa, ms) = prefs.texto("audio.posicao.${chave.id}")?.split('|')?.takeIf { it.size == 2 } ?: return null
        return PosicaoSalvaDoAudio(faixa, ms.toLongOrNull() ?: return null)
    }

    fun apagarPosicaoDoAudio(chave: ChaveDoCapitulo) = prefs.remover("audio.posicao.${chave.id}")

    var velocidadeDoAudio: Double
        get() = prefs.texto("audio.velocidade")?.toDoubleOrNull()?.takeIf { it in ConfigDoAudio.VELOCIDADES } ?: 1.0
        set(v) = prefs.gravarTexto("audio.velocidade", v.toString())

    /** UC-20 · ouvir a era inteira. */
    var modoEra: Boolean
        get() = prefs.logico("audio.modo_era", false)
        set(v) = prefs.gravarLogico("audio.modo_era", v)

    private fun chaveLidos(era: Int) = "leitura.lidos.$era"
}
