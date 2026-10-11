package com.tiagohs.cinemahistory.shared.data

import com.tiagohs.cinemahistory.shared.model.AnoDoPremio
import com.tiagohs.cinemahistory.shared.model.Bloco
import com.tiagohs.cinemahistory.shared.model.Capitulo
import com.tiagohs.cinemahistory.shared.model.CitacaoDoInicio
import com.tiagohs.cinemahistory.shared.model.CitacaoEra
import com.tiagohs.cinemahistory.shared.model.DestaqueDoAno
import com.tiagohs.cinemahistory.shared.model.Diretor
import com.tiagohs.cinemahistory.shared.model.Era
import com.tiagohs.cinemahistory.shared.model.GrupoDeReferencias
import com.tiagohs.cinemahistory.shared.model.Idioma
import com.tiagohs.cinemahistory.shared.model.IndicadosDoAno
import com.tiagohs.cinemahistory.shared.model.ItemDaLinhaDoTempo
import com.tiagohs.cinemahistory.shared.model.ItemDoInicio
import com.tiagohs.cinemahistory.shared.model.ItemDoSumario
import com.tiagohs.cinemahistory.shared.model.ListaDe1001
import com.tiagohs.cinemahistory.shared.model.PaginaDaLinhaDoTempo
import com.tiagohs.cinemahistory.shared.model.PeriodoDaBiografia
import com.tiagohs.cinemahistory.shared.model.PessoaEspecial
import com.tiagohs.cinemahistory.shared.model.Premio
import com.tiagohs.cinemahistory.shared.model.RedeSocial
import com.tiagohs.cinemahistory.shared.model.Referencia
import com.tiagohs.cinemahistory.shared.model.Resultado
import com.tiagohs.cinemahistory.shared.model.TermoDoGlossario
import com.tiagohs.cinemahistory.shared.model.TipoDeIndicado
import com.tiagohs.cinemahistory.shared.model.VideoDaPessoa
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray

/**
 * Todo o conteúdo local do app (os mesmos JSON do Android), por idioma. Cada leitura devolve [Resultado]:
 * arquivo ausente ou inválido vira [Resultado.Erro] com o caminho, nunca exceção (UC-48).
 * Caminhos e regras espelham LocalFiles/LocalRoutes do Android.
 */
class RepositorioDeConteudo(private val fonte: FonteDeConteudo) {

    private fun <T> ler(idioma: Idioma, caminho: String, converter: (JsonElement) -> T): Resultado<T> {
        val texto = fonte.ler(idioma, caminho) ?: return Resultado.Erro("Conteúdo não encontrado: ${idioma.pasta}/$caminho")
        return try {
            Resultado.Sucesso(converter(lerJson(texto)))
        } catch (e: Exception) {
            Resultado.Erro("Conteúdo inválido em ${idioma.pasta}/$caminho: ${e.message}")
        }
    }

    // UC-05 · eras e citações do Início (maintopics.json mistura os dois)
    fun eras(idioma: Idioma): Resultado<List<Era>> = ler(idioma, "maintopics.json") { raiz ->
        raiz.objetos().filter { it.texto("layout_type") != "quote" && it.texto("main_topic_type") == "history_cinema" }.map { era(it) }
    }

    fun citacoesDoInicio(idioma: Idioma): Resultado<List<CitacaoDoInicio>> = ler(idioma, "maintopics.json") { raiz ->
        raiz.objetos().filter { it.texto("layout_type") == "quote" }
            .map { CitacaoDoInicio(it.inteiro("id") ?: 0, it.textoOu("quote"), it.textoOu("author")) }
    }

    // UC-04 · cartões de seção do Início
    fun itensDoInicio(idioma: Idioma): Resultado<List<ItemDoInicio>> = ler(idioma, "homecontent.json") { raiz ->
        raiz.objetos().mapNotNull { o -> Conversores.imagem(o.objeto("image"))?.let { ItemDoInicio(o.textoOu("main_topic_type"), it, o.logico("dark_mode") ?: false) } }
    }

    // UC-08 · sumário de uma era
    fun sumario(idioma: Idioma, era: Int): Resultado<List<ItemDoSumario>> = ler(idioma, "history_sumarios/hmt_sumarios_$era.json") { raiz ->
        raiz.objetos().map { o ->
            ItemDoSumario(o.inteiro("id") ?: 0, o.textoOu("title"), o.textoOu("description"), requireNotNull(Conversores.imagem(o.objeto("image"))) { "sumário sem imagem" })
        }
    }

    // UC-09 · capítulo
    fun capitulo(idioma: Idioma, era: Int, numero: Int): Resultado<Capitulo> =
        ler(idioma, "pages/main_$era/main_${era}_page_$numero.json") { raiz ->
            val o = raiz as JsonObject
            Capitulo(era, o.inteiro("number") ?: numero, Conversores.blocos(o.lista("content_list")))
        }

    // UC-35 · glossário
    fun glossario(idioma: Idioma): Resultado<List<TermoDoGlossario>> = ler(idioma, "glossary.json") { raiz ->
        raiz.objetos().map { TermoDoGlossario(it.textoOu("name"), Conversores.blocos(it.lista("content_list"))) }
    }

    // UC-36 · referências
    fun referencias(idioma: Idioma): Resultado<List<GrupoDeReferencias>> = ler(idioma, "references.json") { raiz ->
        raiz.objetos().map { g ->
            GrupoDeReferencias(g.textoOu("name"), g.lista("references").map { r ->
                if (r.texto("type") == "text") Referencia.Texto(r.textoOu("text"))
                else Referencia.Midia(
                    titulo = r.textoOu("title"), subtitulo = r.textoOu("subtitle"), descricao = r.textoOu("description"),
                    imagem = Conversores.imagem(r.objeto("image")), tipoDeMidia = r.textoOu("mediaType"),
                    link = r.textoOu("link"), textoDoBotao = r.texto("buttonText"),
                )
            })
        }
    }

    // UC-34 · linha do tempo
    fun paginasDaLinhaDoTempo(idioma: Idioma): Resultado<List<Int>> = ler(idioma, "timelines/timeline_list.json") { raiz ->
        raiz.jsonArray.map { (it as JsonPrimitive).int }
    }

    fun linhaDoTempo(idioma: Idioma, id: Int): Resultado<PaginaDaLinhaDoTempo> = ler(idioma, "timelines/timeline_$id.json") { raiz ->
        val o = raiz as JsonObject
        PaginaDaLinhaDoTempo(o.inteiro("id") ?: id, o.textoOu("color"), o.textoOu("title_text_color"), o.lista("timeline_list").map { i ->
            when (i.texto("type")) {
                "title" -> ItemDaLinhaDoTempo.Titulo(i.inteiro("id") ?: 0, i.textoOu("title"), i.textoOu("page_title"), i.texto("next"), i.texto("previous"), i.logico("coming_soon") ?: false)
                "footer" -> ItemDaLinhaDoTempo.Rodape(i.texto("next"), i.texto("previous"))
                else -> ItemDaLinhaDoTempo.Acontecimento(
                    ano = i.textoOu("year"), titulo = i.texto("title"), descricao = i.textoOu("description"),
                    imagem = Conversores.imagem(i.objeto("image")), imagemTransparente = i.logico("image_transparent") ?: false,
                    informacaoDaImagem = i.objeto("image_info")?.let { Conversores.informacao(it) },
                )
            }
        })
    }

    // UC-31 a UC-33 · premiações
    fun premios(idioma: Idioma): Resultado<List<Premio>> = ler(idioma, "awards.json") { raiz ->
        raiz.objetos().map { o ->
            Premio(
                id = o.inteiro("id") ?: 0, nome = o.textoOu("name"), pais = o.texto("country"), apresentadoPor = o.texto("presented_by"),
                primeiraEdicao = o.texto("first_awarded_date"), idDosIndicados = o.inteiro("nominees_id") ?: 0,
                logo = requireNotNull(Conversores.imagem(o.objeto("logo"))) { "prêmio sem logo" },
                imagem = requireNotNull(Conversores.imagem(o.objeto("image"))) { "prêmio sem imagem" },
                redes = o.lista("social_list").map { RedeSocial(it.textoOu("type"), it.texto("link")) },
            )
        }
    }

    /** Texto "Sobre" de um prêmio. O arquivo do prêmio 6 tem nome diferente no Android (history_6.json). */
    fun historiaDoPremio(idioma: Idioma, id: Int): Resultado<List<Bloco>> {
        val caminho = if (fonte.ler(idioma, "awards/history/history$id.json") != null) "awards/history/history$id.json" else "awards/history/history_$id.json"
        return ler(idioma, caminho) { Conversores.blocos(it.objetos()) }
    }

    fun anosDoPremio(idioma: Idioma, idDosIndicados: Int): Resultado<List<AnoDoPremio>> = ler(idioma, "awards/nominees/$idDosIndicados/index.json") { raiz ->
        raiz.objetos().map { o ->
            AnoDoPremio(o.textoOu("year"), o.inteiro("categories") ?: 0, o.inteiro("nominees") ?: 0, o.objeto("highlight")?.let { d ->
                DestaqueDoAno(d.textoOu("category"), if (d.texto("type") == "person") TipoDeIndicado.PESSOA else TipoDeIndicado.FILME,
                    d.longo("id") ?: 0, d.textoOu("name"), d.texto("image_path"), d.texto("director"), d.texto("backdrop_path"))
            })
        }
    }

    fun indicadosDoAno(idioma: Idioma, idDosIndicados: Int, ano: String): Resultado<IndicadosDoAno> =
        ler(idioma, "awards/nominees/$idDosIndicados/$ano.json") { raiz ->
            val o = raiz as JsonObject
            IndicadosDoAno(o.texto("year") ?: ano, Conversores.blocos(o.lista("content")).filterIsInstance<Bloco.Indicados>())
        }

    // UC-28 · diretores
    fun diretores(idioma: Idioma): Resultado<List<Diretor>> = ler(idioma, "directorsmaintopics.json") { raiz ->
        raiz.objetos().map { o ->
            Diretor(
                idDaPessoa = o.longo("person_id") ?: 0, nome = o.textoOu("title"),
                imagem = requireNotNull(Conversores.imagem(o.objeto("image"))) { "diretor sem imagem" },
                anos = o.texto("years"), pais = o.texto("country"), descricao = o.texto("description"),
                epoca = o.texto("era"), regiao = o.texto("region"), emAlta = o.inteiro("trending") ?: 0,
            )
        }
    }

    // UC-29 · página especial do diretor
    fun pessoasEspeciais(idioma: Idioma): Resultado<List<PessoaEspecial>> = ler(idioma, "specials/persons.json") { raiz ->
        raiz.objetos().map { o ->
            PessoaEspecial(
                id = o.longo("id") ?: 0, nome = o.textoOu("name"), nomePersonalizado = o.texto("custom_name"),
                imagemDeDestaque = o.texto("highlight_image"), citacao = o.texto("quote"),
                biografia = o.lista("profile").map { PeriodoDaBiografia(it.textoOu("years"), it.textoOu("content")) },
                videos = o.lista("videos").map { VideoDaPessoa(it.textoOu("name"), it.textoOu("source"), it.textoOu("type"), it.textoOu("key")) },
            )
        }
    }

    // UC-24 · listas dos 1001 Filmes (o arquivo intercala citações, como o maintopics.json)
    fun listasDe1001(idioma: Idioma): Resultado<List<ListaDe1001>> = ler(idioma, "milmoviesmaintopics.json") { raiz ->
        raiz.objetos().filter { it.texto("layout_type") != "quote" }.map { o ->
            ListaDe1001(
                id = o.inteiro("id") ?: 0, idDaLista = o.textoOu("list_id"), titulo = o.textoOu("title"),
                imagem = requireNotNull(Conversores.imagem(o.objeto("image"))) { "lista sem imagem" },
                corDoTitulo = o.texto("title_color"), corDeFundo = o.texto("background_color"),
            )
        }
    }

    fun citacoesDe1001(idioma: Idioma): Resultado<List<CitacaoDoInicio>> = ler(idioma, "milmoviesmaintopics.json") { raiz ->
        raiz.objetos().filter { it.texto("layout_type") == "quote" }
            .map { CitacaoDoInicio(it.inteiro("id") ?: 0, it.textoOu("quote"), it.textoOu("author")) }
    }

    private fun era(o: JsonObject): Era {
        val q = o.objeto("quote")
        return Era(
            id = o.inteiro("id") ?: 0,
            titulo = o.textoOu("title"),
            subtitulo = o.textoOu("subtitle"),
            descricao = o.textoOu("description"),
            cor = o.textoOu("color"),
            nova = o.logico("is_new") ?: false,
            bloqueada = o.logico("blocked") ?: false,
            imagem = Conversores.imagem(o.objeto("image")),
            imagemDeApresentacao = Conversores.imagem(o.objeto("presentation_image")),
            layout = o.textoOu("layout_type"),
            citacao = q?.texto("quote")?.let { CitacaoEra(it, q.textoOu("author")) },
        )
    }
}
