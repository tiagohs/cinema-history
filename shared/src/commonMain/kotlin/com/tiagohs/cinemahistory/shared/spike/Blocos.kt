package com.tiagohs.cinemahistory.shared.spike

/** Os 16 tipos do enum de conteúdo do Android (UC-09). O Swift decide a renderização pelo [tipo]. */
enum class BlocoTipo {
    TEXT, GIF, VIDEO, SLIDE, IMAGE, AUDIO_STREAM, QUOTE, BLOCK_SPECIAL, LINK_SCREEN,
    MOVIE_LIST, PERSON_LIST, RECOMENDATIONS, AWARDS_NOMINEES, MOVIE_LIST_SPECIAL, TWITTER, ESSAY,
}

sealed class Bloco(val tipo: BlocoTipo) {
    class Texto(val html: String) : Bloco(BlocoTipo.TEXT)
    class Gif(val url: String) : Bloco(BlocoTipo.GIF)
    class Video(val youtubeId: String, val titulo: String) : Bloco(BlocoTipo.VIDEO)
    class Slide(val imagens: List<String>) : Bloco(BlocoTipo.SLIDE)
    class Imagem(val url: String, val legenda: String?) : Bloco(BlocoTipo.IMAGE)
    class Audio(val url: String) : Bloco(BlocoTipo.AUDIO_STREAM)
    class Citacao(val texto: String, val autor: String) : Bloco(BlocoTipo.QUOTE)
    class Especial(val titulo: String, val descricao: String) : Bloco(BlocoTipo.BLOCK_SPECIAL)
    class LinkTela(val destino: String) : Bloco(BlocoTipo.LINK_SCREEN)
    class ListaFilmes(val ids: List<Int>) : Bloco(BlocoTipo.MOVIE_LIST)
    class ListaPessoas(val ids: List<Int>) : Bloco(BlocoTipo.PERSON_LIST)
    class Recomendacoes(val ids: List<Int>) : Bloco(BlocoTipo.RECOMENDATIONS)
    class Indicados(val premio: String) : Bloco(BlocoTipo.AWARDS_NOMINEES)
    class ListaEspecial(val titulo: String) : Bloco(BlocoTipo.MOVIE_LIST_SPECIAL)
    class Twitter(val url: String) : Bloco(BlocoTipo.TWITTER)
    class Ensaio(val titulo: String) : Bloco(BlocoTipo.ESSAY)
}

fun blocosDeExemplo(): List<Bloco> = listOf(
    Bloco.Texto("<p>texto</p>"), Bloco.Gif("g"), Bloco.Video("abc", "v"), Bloco.Slide(listOf("a", "b")),
    Bloco.Imagem("i", null), Bloco.Audio("a"), Bloco.Citacao("c", "autor"), Bloco.Especial("t", "d"),
    Bloco.LinkTela("timeline"), Bloco.ListaFilmes(listOf(1, 2)), Bloco.ListaPessoas(listOf(3)),
    Bloco.Recomendacoes(listOf(4)), Bloco.Indicados("oscar"), Bloco.ListaEspecial("x"),
    Bloco.Twitter("t"), Bloco.Ensaio("e"),
)
