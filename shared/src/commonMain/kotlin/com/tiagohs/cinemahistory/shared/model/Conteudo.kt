package com.tiagohs.cinemahistory.shared.model

/** Um capítulo (página do Android) de uma era: blocos na ordem do JSON (o índice é o source_index do áudio). */
data class Capitulo(val era: Int, val numero: Int, val blocos: List<Bloco>)

/** Item do sumário de uma era (UC-08): o [id] é o número do capítulo. */
data class ItemDoSumario(val id: Int, val titulo: String, val descricao: String, val imagem: Imagem)

/** Cartão de seção do Início (UC-04/05): tipo da seção e imagem de capa. */
data class ItemDoInicio(val secao: String, val imagem: Imagem, val escuro: Boolean)

/** Citação dos cartões do Início (UC-06): fixa, vem do maintopics.json. */
data class CitacaoDoInicio(val id: Int, val texto: String, val autor: String)

data class TermoDoGlossario(val nome: String, val blocos: List<Bloco>)

sealed class Referencia {
    class Midia(
        val titulo: String,
        val subtitulo: String,
        val descricao: String,
        val imagem: Imagem?,
        val tipoDeMidia: String,
        val link: String,
        val textoDoBotao: String?,
    ) : Referencia()

    class Texto(val html: String) : Referencia()
}

data class GrupoDeReferencias(val nome: String, val referencias: List<Referencia>)

sealed class ItemDaLinhaDoTempo {
    class Titulo(val id: Int, val titulo: String, val tituloDaPagina: String, val proxima: String?, val anterior: String?, val emBreve: Boolean) : ItemDaLinhaDoTempo()
    class Acontecimento(
        val ano: String,
        val titulo: String?,
        val descricao: String,
        val imagem: Imagem?,
        val imagemTransparente: Boolean,
        val informacaoDaImagem: Informacao?,
    ) : ItemDaLinhaDoTempo()

    class Rodape(val proxima: String?, val anterior: String?) : ItemDaLinhaDoTempo()
}

data class PaginaDaLinhaDoTempo(val id: Int, val cor: String, val corDoTitulo: String, val itens: List<ItemDaLinhaDoTempo>)

data class RedeSocial(val tipo: String, val link: String?)

/** Prêmio (UC-31): [idDosIndicados] aponta a pasta awards/nominees/<id>; 0 quando o prêmio não tem indicados. */
data class Premio(
    val id: Int,
    val nome: String,
    val pais: String?,
    val apresentadoPor: String?,
    val primeiraEdicao: String?,
    val idDosIndicados: Int,
    val logo: Imagem,
    val imagem: Imagem,
    val redes: List<RedeSocial>,
)

data class DestaqueDoAno(val categoria: String, val tipo: TipoDeIndicado, val id: Long, val nome: String, val imagem: String?, val diretor: String?, val fundo: String?)

/** Linha do índice de anos de um prêmio. */
data class AnoDoPremio(val ano: String, val categorias: Int, val indicados: Int, val destaque: DestaqueDoAno?)

data class IndicadosDoAno(val ano: String, val categorias: List<Bloco.Indicados>)

/** Diretor da seção Mestres das Telas (UC-28). */
data class Diretor(
    val idDaPessoa: Long,
    val nome: String,
    val imagem: Imagem,
    val anos: String?,
    val pais: String?,
    val descricao: String?,
    val epoca: String?,
    val regiao: String?,
    val emAlta: Int,
)

/** Uma lista dos 1001 Filmes (década ou recorte). */
data class ListaDe1001(val id: Int, val idDaLista: String, val titulo: String, val imagem: Imagem, val corDoTitulo: String?, val corDeFundo: String?)

data class PeriodoDaBiografia(val anos: String, val texto: String)

data class VideoDaPessoa(val nome: String, val fonte: String, val tipo: String, val chave: String)

/** Página especial de diretor (UC-29): dados locais que complementam o TMDB. */
data class PessoaEspecial(
    val id: Long,
    val nome: String,
    val nomePersonalizado: String?,
    val imagemDeDestaque: String?,
    val citacao: String?,
    val biografia: List<PeriodoDaBiografia>,
    val videos: List<VideoDaPessoa>,
)
