package com.tiagohs.cinemahistory.shared

import com.tiagohs.cinemahistory.shared.data.FonteDeConteudo
import com.tiagohs.cinemahistory.shared.data.RepositorioDeConteudo
import com.tiagohs.cinemahistory.shared.model.Bloco
import com.tiagohs.cinemahistory.shared.model.Idioma
import com.tiagohs.cinemahistory.shared.model.Resultado
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Lê TODO o conteúdo real do app (o mesmo do Android) nos três idiomas e confere contra o JSON cru:
 * nenhum arquivo falha, nenhum bloco vira "desconhecido", e as traduções têm a mesma estrutura do português
 * (mesmo número de capítulos e a mesma sequência de blocos — o áudio e a posição de leitura dependem disso).
 */
class ConteudoCompletoTest {
    private val conteudo = ConteudoDoAndroid()
    private val repo = RepositorioDeConteudo(FonteDeConteudo(conteudo))

    private fun <T> ok(r: Resultado<T>, contexto: String): T = when (r) {
        is Resultado.Sucesso -> r.valor
        is Resultado.Erro -> fail("$contexto: ${r.mensagem}")
        Resultado.Carregando -> fail("$contexto: carregando")
    }

    private fun cru(idioma: Idioma, caminho: String) = Json.parseToJsonElement(File(conteudo.raiz, "${idioma.pasta}/$caminho").readText())

    private fun paginasDaEra(idioma: Idioma, era: Int): Int =
        File(conteudo.raiz, "${idioma.pasta}/pages/main_$era").listFiles { f -> f.name.endsWith(".json") }?.size ?: 0

    private fun tiposCrus(lista: JsonArray): List<String> = lista.map { it.jsonObject["type"]!!.jsonPrimitive.content }

    @Test
    fun `Inicio - eras, citacoes e cartoes em todos os idiomas`() {
        for (idioma in Idioma.entries) {
            assertEquals(8, ok(repo.eras(idioma), "eras $idioma").size)
            assertEquals(2, ok(repo.citacoesDoInicio(idioma), "citações $idioma").size)
            assertEquals(cru(idioma, "homecontent.json").jsonArray.size, ok(repo.itensDoInicio(idioma), "início $idioma").size)
        }
    }

    @Test
    fun `todos os capitulos de todas as eras abrem sem bloco desconhecido`() {
        for (idioma in Idioma.entries) for (era in 1..8) {
            val sumario = ok(repo.sumario(idioma, era), "sumário $idioma/$era")
            assertEquals(paginasDaEra(idioma, era), sumario.size, "sumário x páginas em $idioma/era $era")
            for (item in sumario) {
                val cap = ok(repo.capitulo(idioma, era, item.id), "capítulo $idioma/$era/${item.id}")
                val crus = cru(idioma, "pages/main_$era/main_${era}_page_${item.id}.json").jsonObject["content_list"]!!.jsonArray
                assertEquals(crus.size, cap.blocos.size, "blocos em $idioma/$era/${item.id}")
                val desconhecidos = cap.blocos.filterIsInstance<Bloco.Desconhecido>()
                assertTrue(desconhecidos.isEmpty(), "desconhecidos em $idioma/$era/${item.id}: ${desconhecidos.map { it.chave }}")
                assertEquals(tiposCrus(crus), cap.blocos.map { it.tipo.chave }, "tipos em $idioma/$era/${item.id}")
            }
        }
    }

    @Test
    fun `traducoes tem a mesma estrutura do portugues`() {
        val diferencas = mutableListOf<String>()
        for (idioma in listOf(Idioma.EN, Idioma.ES)) for (era in 1..8) {
            val pt = ok(repo.sumario(Idioma.PT, era), "sumário pt/$era")
            val outro = ok(repo.sumario(idioma, era), "sumário $idioma/$era")
            if (pt.map { it.id } != outro.map { it.id }) diferencas += "$idioma era $era: sumário ${pt.map { it.id }} x ${outro.map { it.id }}"
            for (item in pt) {
                val a = ok(repo.capitulo(Idioma.PT, era, item.id), "pt").blocos.map { it.tipo }
                val b = (repo.capitulo(idioma, era, item.id) as? Resultado.Sucesso)?.valor?.blocos?.map { it.tipo }
                if (a != b) diferencas += "$idioma era $era capítulo ${item.id}: ${a.size} blocos em pt x ${b?.size} em ${idioma.pasta}"
            }
        }
        assertTrue(diferencas.isEmpty(), "Estrutura diferente do português:\n" + diferencas.joinToString("\n"))
    }

    @Test
    fun `glossario, referencias e linha do tempo`() {
        for (idioma in Idioma.entries) {
            val termos = ok(repo.glossario(idioma), "glossário $idioma")
            assertEquals(cru(idioma, "glossary.json").jsonArray.size, termos.size)
            assertTrue(termos.flatMap { it.blocos }.none { it is Bloco.Desconhecido }, "glossário $idioma")
            assertTrue(ok(repo.referencias(idioma), "referências $idioma").isNotEmpty())
            val paginas = ok(repo.paginasDaLinhaDoTempo(idioma), "linha do tempo $idioma")
            assertEquals((1..8).toList(), paginas)
            for (id in paginas) {
                val pagina = ok(repo.linhaDoTempo(idioma, id), "linha do tempo $idioma/$id")
                assertEquals(cru(idioma, "timelines/timeline_$id.json").jsonObject["timeline_list"]!!.jsonArray.size, pagina.itens.size)
            }
        }
    }

    @Test
    fun `premiacoes - todos os premios, historias, anos e indicados`() {
        for (idioma in Idioma.entries) {
            val premios = ok(repo.premios(idioma), "prêmios $idioma")
            assertEquals(7, premios.size, "prêmios $idioma")
            for (p in premios) {
                assertTrue(ok(repo.historiaDoPremio(idioma, p.id), "história ${p.id} $idioma").isNotEmpty())
                if (p.idDosIndicados == 0) continue
                val anos = ok(repo.anosDoPremio(idioma, p.idDosIndicados), "anos ${p.idDosIndicados} $idioma")
                val arquivos = File(conteudo.raiz, "${idioma.pasta}/awards/nominees/${p.idDosIndicados}")
                    .listFiles { f -> f.name != "index.json" }!!.map { it.nameWithoutExtension }.toSet()
                assertEquals(arquivos, anos.map { it.ano }.toSet(), "índice x arquivos do prêmio ${p.idDosIndicados} em $idioma")
                for (ano in anos) {
                    val ind = ok(repo.indicadosDoAno(idioma, p.idDosIndicados, ano.ano), "indicados ${p.idDosIndicados}/${ano.ano} $idioma")
                    assertEquals(ano.categorias, ind.categorias.size, "categorias ${p.idDosIndicados}/${ano.ano} $idioma")
                    assertEquals(ano.indicados, ind.categorias.sumOf { it.indicados.size }, "indicados ${p.idDosIndicados}/${ano.ano} $idioma")
                }
            }
        }
    }

    @Test
    fun `diretores, 1001 filmes e paginas especiais`() {
        for (idioma in Idioma.entries) {
            assertEquals(cru(idioma, "directorsmaintopics.json").jsonArray.size, ok(repo.diretores(idioma), "diretores $idioma").size)
            val crus1001 = cru(idioma, "milmoviesmaintopics.json").jsonArray.map { it.jsonObject }
            val citacoes = crus1001.count { it["layout_type"]?.jsonPrimitive?.content == "quote" }
            assertEquals(crus1001.size - citacoes, ok(repo.listasDe1001(idioma), "1001 $idioma").size)
            assertEquals(citacoes, ok(repo.citacoesDe1001(idioma), "citações 1001 $idioma").size)
            val especiais = ok(repo.pessoasEspeciais(idioma), "especiais $idioma")
            assertEquals(cru(idioma, "specials/persons.json").jsonArray.size, especiais.size)
            assertTrue(especiais.all { it.biografia.isNotEmpty() }, "biografia vazia em $idioma")
        }
    }
}
