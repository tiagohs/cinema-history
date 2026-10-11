package com.tiagohs.cinemahistory.shared.model

/** De onde vem a imagem: embutida no app (nome do recurso), URL comum ou Firebase Storage. */
enum class TipoDeImagem { LOCAL, ONLINE, ONLINE_FIREBASE }

/**
 * Imagem do conteúdo. Medidas em pontos; 0 = não informado (regra 1 da ponte: nada de Int? para o Swift).
 * [escala] é o scale_type do Android (center_crop, center_inside, fit_center…), ou "" quando ausente.
 */
data class Imagem(
    val tipo: TipoDeImagem,
    val url: String,
    val descricao: String?,
    val largura: Int,
    val altura: Int,
    val escala: String,
    val animacao: String?,
)

/** Legenda de mídia (imagem, vídeo, GIF, slide): título, texto e fonte. */
data class Informacao(val titulo: String?, val texto: String?, val fonte: String?)

enum class TelaDoClique { LINHA_DO_TEMPO, LINK_ONLINE, DESCONHECIDA }

data class ParametroDoClique(val chave: String?, val valor: String)

data class Clique(val tela: TelaDoClique, val textoDoBotao: String?, val parametros: List<ParametroDoClique>) {
    fun parametro(chave: String): String? = parametros.firstOrNull { it.chave == chave }?.valor
}

/** Filme como aparece nas listas do conteúdo (dados do TMDB guardados no JSON). Séries usam name/first_air_date. */
data class FilmeResumo(
    val id: Long,
    val titulo: String,
    val tituloOriginal: String?,
    val dataDeLancamento: String?,
    val poster: String?,
    val fundo: String?,
    val sinopse: String?,
    val nota: Double,
) {
    /** Ano de lançamento ("1915"), ou "" sem data. */
    val ano: String get() = dataDeLancamento?.take(4)?.takeIf { it.length == 4 } ?: ""
}

data class PessoaResumo(val id: Long, val nome: String, val foto: String?, val departamento: String?)

data class Recomendacao(val titulo: String?, val subtitulo: String?, val descricao: String?, val link: String?)

enum class TipoDeIndicado { FILME, PESSOA }

/** Indicado a um prêmio. Para pessoa, [filme] é o filme pelo qual foi indicada. */
data class Indicado(
    val tipo: TipoDeIndicado,
    val id: Long,
    val nome: String?,
    val imagem: String?,
    val vencedor: Boolean,
    val filme: Indicado?,
    val departamento: String?,
    val pais: String?,
    val diretor: String?,
    val fundo: String?,
)

/** Canal de vídeo-ensaio (chave do JSON do Android) com nome, endereço e imagem. */
data class CanalDeEnsaio(val chave: String, val nome: String, val url: String, val imagem: String)
