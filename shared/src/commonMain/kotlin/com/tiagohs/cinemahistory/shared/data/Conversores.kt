package com.tiagohs.cinemahistory.shared.data

import com.tiagohs.cinemahistory.shared.model.Bloco
import com.tiagohs.cinemahistory.shared.model.CanalDeEnsaio
import com.tiagohs.cinemahistory.shared.model.Clique
import com.tiagohs.cinemahistory.shared.model.FilmeResumo
import com.tiagohs.cinemahistory.shared.model.Imagem
import com.tiagohs.cinemahistory.shared.model.Indicado
import com.tiagohs.cinemahistory.shared.model.Informacao
import com.tiagohs.cinemahistory.shared.model.ParametroDoClique
import com.tiagohs.cinemahistory.shared.model.PessoaResumo
import com.tiagohs.cinemahistory.shared.model.Recomendacao
import com.tiagohs.cinemahistory.shared.model.TelaDoClique
import com.tiagohs.cinemahistory.shared.model.TipoDeBloco
import com.tiagohs.cinemahistory.shared.model.TipoDeImagem
import com.tiagohs.cinemahistory.shared.model.TipoDeIndicado
import kotlinx.serialization.json.JsonObject

/** Conversão do JSON do conteúdo (formato do Android) para o modelo do núcleo. Espelha os deserializers do Android. */
internal object Conversores {

    fun imagem(o: JsonObject?): Imagem? {
        o ?: return null
        val url = o.texto("url") ?: return null
        val estilo = o.objeto("style")
        val redimensionar = estilo?.objeto("resize")
        return Imagem(
            tipo = when (o.texto("image_type")) {
                "local" -> TipoDeImagem.LOCAL
                "online_firebase" -> TipoDeImagem.ONLINE_FIREBASE
                else -> TipoDeImagem.ONLINE // padrão do Android
            },
            url = url,
            descricao = o.texto("content_description"),
            largura = redimensionar?.inteiro("width") ?: estilo?.inteiro("width") ?: 0,
            altura = redimensionar?.inteiro("height") ?: estilo?.inteiro("height") ?: 0,
            escala = estilo?.texto("scale_type").orEmpty(),
            animacao = o.objeto("animation")?.texto("type"),
        )
    }

    fun informacao(o: JsonObject?): Informacao =
        Informacao(titulo = o?.texto("contentTitle"), texto = o?.texto("contentText"), fonte = o?.texto("source"))

    fun clique(o: JsonObject?): Clique? {
        o ?: return null
        return Clique(
            tela = when (o.texto("screen")) {
                "timeline" -> TelaDoClique.LINHA_DO_TEMPO
                "link_online" -> TelaDoClique.LINK_ONLINE
                else -> TelaDoClique.DESCONHECIDA
            },
            textoDoBotao = o.texto("button_text"),
            parametros = o.lista("parameters").mapNotNull { p -> p.texto("value")?.let { ParametroDoClique(p.texto("key"), it) } },
        )
    }

    fun filme(o: JsonObject): FilmeResumo? {
        val id = o.longo("id") ?: return null
        return FilmeResumo(
            id = id,
            titulo = o.texto("title") ?: o.texto("name") ?: o.texto("original_title") ?: o.textoOu("original_name"),
            tituloOriginal = o.texto("original_title") ?: o.texto("original_name"),
            dataDeLancamento = o.texto("release_date") ?: o.texto("first_air_date"),
            poster = o.texto("poster_path"),
            fundo = o.texto("backdrop_path"),
            sinopse = o.texto("overview"),
            nota = o.decimal("vote_average") ?: 0.0,
        )
    }

    fun pessoa(o: JsonObject): PessoaResumo? {
        val id = o.longo("id") ?: return null
        return PessoaResumo(id = id, nome = o.textoOu("name"), foto = o.texto("profile_path"), departamento = o.texto("known_for_department"))
    }

    fun indicado(o: JsonObject): Indicado = Indicado(
        tipo = if (o.texto("type") == "person") TipoDeIndicado.PESSOA else TipoDeIndicado.FILME,
        id = o.longo("id") ?: 0,
        nome = o.texto("name"),
        imagem = o.texto("image_path"),
        vencedor = o.logico("winner") ?: false,
        filme = o.objeto("movie")?.let { indicado(it) },
        departamento = o.texto("department"),
        pais = o.texto("country"),
        diretor = o.texto("director"),
        fundo = o.texto("backdrop_path"),
    )

    fun bloco(o: JsonObject): Bloco {
        val chave = o.texto("type")
        return when (TipoDeBloco.daChave(chave)) {
            TipoDeBloco.TEXT -> Bloco.Texto(o.textoOu("content_text"), o.texto("content_title"), o.texto("content_credits"))
            TipoDeBloco.GIF -> {
                val gif = o.objeto("gif_image")
                val miniatura = imagem(gif?.objeto("thumbnail"))
                if (gif?.texto("url") == null || miniatura == null) Bloco.Desconhecido(chave.orEmpty())
                else Bloco.Gif(
                    url = gif.textoOu("url"),
                    tipoDaImagem = imagem(gif)?.tipo ?: TipoDeImagem.ONLINE,
                    miniatura = miniatura,
                    informacao = informacao(o.objeto("information")),
                )
            }
            TipoDeBloco.VIDEO -> Bloco.Video(o.textoOu("video_id"), o.inteiro("height") ?: 0, informacao(o.objeto("information")))
            TipoDeBloco.SLIDE -> Bloco.Slide(o.lista("images").mapNotNull { imagem(it) }, o.inteiro("height") ?: 0, informacao(o.objeto("information")))
            TipoDeBloco.IMAGE -> imagem(o.objeto("image"))?.let { Bloco.ImagemComLegenda(it, o.inteiro("height") ?: 0, informacao(o.objeto("information"))) }
                ?: Bloco.Desconhecido(chave.orEmpty())
            TipoDeBloco.AUDIO_STREAM -> imagem(o.objeto("image"))?.let { Bloco.Audio(o.textoOu("path"), it, informacao(o.objeto("information"))) }
                ?: Bloco.Desconhecido(chave.orEmpty())
            TipoDeBloco.QUOTE -> {
                val q = o.objeto("quote")
                Bloco.Citacao(q?.textoOu("quote").orEmpty(), q?.textoOu("author").orEmpty(), o.texto("quote_mark_color"))
            }
            TipoDeBloco.BLOCK_SPECIAL -> Bloco.Especial(o.texto("title"), o.textoOu("description"), o.texto("credits"), imagem(o.objeto("image")), clique(o.objeto("click")))
            TipoDeBloco.LINK_SCREEN -> Bloco.LinkDeTela(o.texto("title"), o.texto("subtitle"), o.textoOu("description"), imagem(o.objeto("image")), clique(o.objeto("click")))
            TipoDeBloco.MOVIE_LIST -> Bloco.ListaDeFilmes(o.lista("movies").mapNotNull { filme(it) })
            TipoDeBloco.PERSON_LIST -> Bloco.ListaDePessoas(o.texto("title"), o.lista("persons").mapNotNull { pessoa(it) })
            TipoDeBloco.RECOMENDATIONS -> Bloco.Recomendacoes(o.lista("list").map { Recomendacao(it.texto("title"), it.texto("subtitle"), it.texto("description"), it.texto("link")) })
            TipoDeBloco.AWARDS_NOMINEES -> Bloco.Indicados(o.texto("name"), o.texto("year"), o.lista("nominee_list").map { indicado(it) })
            TipoDeBloco.MOVIE_LIST_SPECIAL -> Bloco.ListaEspecialDeFilmes(o.lista("movies").mapNotNull { filme(it) }, informacao(o.objeto("information")))
            TipoDeBloco.TWITTER -> Bloco.Twitter(o.textoOu("twitter_url"), o.textoOu("twitter_html"), informacao(o.objeto("information")))
            TipoDeBloco.ESSAY -> Bloco.Ensaio(
                titulo = o.textoOu("title"),
                descricao = o.textoOu("description"),
                youtubeId = o.textoOu("video_id"),
                canal = o.texto("channel")?.let { c -> CANAIS.firstOrNull { it.chave == c } },
                filme = o.objeto("movie")?.let { filme(it) },
                pessoa = o.objeto("person")?.let { pessoa(it) },
            )
            TipoDeBloco.DESCONHECIDO -> Bloco.Desconhecido(chave.orEmpty())
        }
    }

    fun blocos(lista: List<JsonObject>): List<Bloco> = lista.map { bloco(it) }

    /** Canais de vídeo-ensaio (EssayChannel do Android; o nome do "fandor" lá está errado como "Insider"). */
    val CANAIS: List<CanalDeEnsaio> = listOf(
    CanalDeEnsaio("lessons_from_the_screenplay", "Lessons from the Screenplay", "https://www.youtube.com/c/LessonsfromtheScreenplay", "https://website-cb5.pages.dev/cinema-history/media/channels/lessons_from_the_screenplay.webp"),
    CanalDeEnsaio("allerix_films", "Allerix Films", "https://www.youtube.com/channel/UCe8JT_-iNjMC2ZhiEQB57Gg", "https://website-cb5.pages.dev/cinema-history/media/channels/allerix_films.webp"),
    CanalDeEnsaio("alpha_alpaca_pack", "Alpha-Alpaca-Pack", "https://www.youtube.com/channel/UC3dtRhnPOJz4JrTZA7kj85g", "https://website-cb5.pages.dev/cinema-history/media/channels/alpha_alpaca_pack.webp"),
    CanalDeEnsaio("now_you_see_it", "Now You See It", "https://www.youtube.com/channel/UCWTFGPpNQ0Ms6afXhaWDiRw", "https://website-cb5.pages.dev/cinema-history/media/channels/now_you_see_it.webp"),
    CanalDeEnsaio("the_midas_touch", "The Midas Touch", "https://www.youtube.com/channel/UCdHXNk-O8aXWBKmm9Mr3law", "https://website-cb5.pages.dev/cinema-history/media/channels/the_midas_touch.webp"),
    CanalDeEnsaio("the_new_york_times", "The New York Times", "https://www.youtube.com/channel/UCqnbDFdCpuN8CMEg0VuEBqA", "https://website-cb5.pages.dev/cinema-history/media/channels/the_new_york_times.webp"),
    CanalDeEnsaio("wisecrack", "Wisecrack", "https://www.youtube.com/channel/UC6-ymYjG0SU0jUWnWh9ZzEQ", "https://website-cb5.pages.dev/cinema-history/media/channels/wisecrack.webp"),
    CanalDeEnsaio("studio_binder", "StudioBinder", "https://www.youtube.com/channel/UCUFoQUaVRt3MVFxqwPUMLCQ", "https://website-cb5.pages.dev/cinema-history/media/channels/studio_binder.webp"),
    CanalDeEnsaio("fames_focus", "Fames Focus", "https://www.youtube.com/channel/UCfzuJecdM0uhX7HfICewt3Q", "https://website-cb5.pages.dev/cinema-history/media/channels/fames_focus.webp"),
    CanalDeEnsaio("cosmavoid", "Cosmavoid", "https://www.youtube.com/channel/UCcjI2G98kWgw7a-JjWt6kZw", "https://website-cb5.pages.dev/cinema-history/media/channels/cosmavoid.webp"),
    CanalDeEnsaio("insider", "Insider", "https://www.youtube.com/channel/UCHJuQZuzapBh-CuhRYxIZrg", "https://website-cb5.pages.dev/cinema-history/media/channels/insider.webp"),
    CanalDeEnsaio("fandor", "Fandor", "https://www.youtube.com/channel/UCkeBOIrsgk0EyJwg-hHs7MA", "https://website-cb5.pages.dev/cinema-history/media/channels/fandor.webp"),
    CanalDeEnsaio("just_write", "Just Write", "https://www.youtube.com/user/mythicalsage", "https://website-cb5.pages.dev/cinema-history/media/channels/just_write.webp"),
    CanalDeEnsaio("kaptainkristian", "kaptainkristian", "https://www.youtube.com/channel/UCuPgdqQKpq4T4zeqmTelnFg", "https://website-cb5.pages.dev/cinema-history/media/channels/kaptainkristian.webp"),
    CanalDeEnsaio("channel_awesome", "Channel Awesome", "https://www.youtube.com/channel/UCiH828EtgQjTyNIMH6YiOSw", "https://website-cb5.pages.dev/cinema-history/media/channels/channel_awesome.webp"),
    CanalDeEnsaio("alex_day", "Alex Day", "https://www.youtube.com/channel/UCM6hs_4aB32MgKQA96cdo-g", "https://website-cb5.pages.dev/cinema-history/media/channels/alex_day.webp"),
    CanalDeEnsaio("rossatron", "Rossatron", "https://www.youtube.com/channel/UCxUR9wLuzgsvA6mpgKtiqGw", "https://website-cb5.pages.dev/cinema-history/media/channels/rossatron.webp"),
    CanalDeEnsaio("like_stories_of_old", "Like Stories of Old", "https://www.youtube.com/channel/UCs7nPQIEba0T3tGOWWsZpJQ", "https://website-cb5.pages.dev/cinema-history/media/channels/like_stories_of_old.webp"),
    CanalDeEnsaio("storyStreet", "StoryStreet", "https://www.youtube.com/channel/UC_1tSdT5U1Y2AL6kBFXw8Vw", "https://website-cb5.pages.dev/cinema-history/media/channels/storyStreet.webp"),
    CanalDeEnsaio("variety", "Variety", "https://www.youtube.com/channel/UCgRQHK8Ttr1j9xCEpCAlgbQ", "https://website-cb5.pages.dev/cinema-history/media/channels/variety.webp"),
    CanalDeEnsaio("vox", "Vox", "https://www.youtube.com/channel/UCLXo7UDZvByw2ixzpQCufnA", "https://website-cb5.pages.dev/cinema-history/media/channels/vox.webp"),
    CanalDeEnsaio("the_take", "The Take", "https://www.youtube.com/channel/UCVjsbqKtxkLt7bal4NWRjJQ", "https://website-cb5.pages.dev/cinema-history/media/channels/the_take.webp"),
    CanalDeEnsaio("wow_such_gaming", "Wow Such Gaming", "https://www.youtube.com/channel/UCugqjlk-tkE_uZnG0tDbQyg", "https://website-cb5.pages.dev/cinema-history/media/channels/wow_such_gaming.webp"),
    CanalDeEnsaio("the_discarded_image", "The Discarded Image", "https://www.youtube.com/channel/UCJKLIcSts_4XpVbORwzLZVw", "https://website-cb5.pages.dev/cinema-history/media/channels/the_discarded_image.webp"),
    CanalDeEnsaio("netflix_film_club", "Netflix Film Club", "https://www.youtube.com/channel/UC_UJdqZuFhRXfeYXYrhwgJA", "https://website-cb5.pages.dev/cinema-history/media/channels/netflix_film_club.webp"),
    CanalDeEnsaio("every_frame_is_a_painting", "Every Frame a Painting", "https://www.youtube.com/channel/UCjFqcJQXGZ6T6sxyFB-5i6A", "https://website-cb5.pages.dev/cinema-history/media/channels/every_frame_is_a_painting.webp"),
    )
}
