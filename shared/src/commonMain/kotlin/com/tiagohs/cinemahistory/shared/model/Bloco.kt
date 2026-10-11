package com.tiagohs.cinemahistory.shared.model

/** Os 16 tipos do enum de conteúdo do Android, mais [DESCONHECIDO] para um tipo novo que o app ainda não conhece. */
enum class TipoDeBloco(val chave: String) {
    TEXT("text"), GIF("gif"), VIDEO("video"), SLIDE("slide"), IMAGE("image"), AUDIO_STREAM("audio_stream"),
    QUOTE("quote"), BLOCK_SPECIAL("block_special"), LINK_SCREEN("link_screen"), MOVIE_LIST("movie_list"),
    PERSON_LIST("person_list"), RECOMENDATIONS("recomendations"), AWARDS_NOMINEES("awards_nominees"),
    MOVIE_LIST_SPECIAL("movie_list_special"), TWITTER("twitter"), ESSAY("essay"), DESCONHECIDO("");

    companion object {
        fun daChave(chave: String?): TipoDeBloco = entries.firstOrNull { it.chave == chave && it != DESCONHECIDO } ?: DESCONHECIDO
    }
}

/**
 * Um bloco do conteúdo (capítulo, glossário, histórico e indicados dos prêmios).
 * Regra 6 da ponte: o Swift escolhe a tela pelo [tipo]; um teste garante que todo tipo tem tela.
 */
sealed class Bloco(val tipo: TipoDeBloco) {
    class Texto(val html: String, val titulo: String?, val creditos: String?) : Bloco(TipoDeBloco.TEXT)
    class Gif(val url: String, val tipoDaImagem: TipoDeImagem, val miniatura: Imagem, val informacao: Informacao) : Bloco(TipoDeBloco.GIF)
    class Video(val youtubeId: String, val altura: Int, val informacao: Informacao) : Bloco(TipoDeBloco.VIDEO)
    class Slide(val imagens: List<Imagem>, val altura: Int, val informacao: Informacao) : Bloco(TipoDeBloco.SLIDE)
    class ImagemComLegenda(val imagem: Imagem, val altura: Int, val informacao: Informacao) : Bloco(TipoDeBloco.IMAGE)
    class Audio(val caminho: String, val imagem: Imagem, val informacao: Informacao) : Bloco(TipoDeBloco.AUDIO_STREAM)
    class Citacao(val texto: String, val autor: String, val corDaAspa: String?) : Bloco(TipoDeBloco.QUOTE)
    class Especial(val titulo: String?, val descricao: String, val creditos: String?, val imagem: Imagem?, val clique: Clique?) : Bloco(TipoDeBloco.BLOCK_SPECIAL)
    class LinkDeTela(val titulo: String?, val subtitulo: String?, val descricao: String, val imagem: Imagem?, val clique: Clique?) : Bloco(TipoDeBloco.LINK_SCREEN)
    class ListaDeFilmes(val filmes: List<FilmeResumo>) : Bloco(TipoDeBloco.MOVIE_LIST)
    class ListaDePessoas(val titulo: String?, val pessoas: List<PessoaResumo>) : Bloco(TipoDeBloco.PERSON_LIST)
    class Recomendacoes(val itens: List<Recomendacao>) : Bloco(TipoDeBloco.RECOMENDATIONS)
    class Indicados(val categoria: String?, val ano: String?, val indicados: List<Indicado>) : Bloco(TipoDeBloco.AWARDS_NOMINEES)
    class ListaEspecialDeFilmes(val filmes: List<FilmeResumo>, val informacao: Informacao) : Bloco(TipoDeBloco.MOVIE_LIST_SPECIAL)
    class Twitter(val url: String, val html: String, val informacao: Informacao) : Bloco(TipoDeBloco.TWITTER)
    class Ensaio(
        val titulo: String,
        val descricao: String,
        val youtubeId: String,
        val canal: CanalDeEnsaio?,
        val filme: FilmeResumo?,
        val pessoa: PessoaResumo?,
    ) : Bloco(TipoDeBloco.ESSAY)

    /** Tipo que o app não conhece (conteúdo mais novo que o app): a tela simplesmente pula. */
    class Desconhecido(val chave: String) : Bloco(TipoDeBloco.DESCONHECIDO)
}
