package com.tiagohs.cinemahistory.shared.bridge

import com.tiagohs.cinemahistory.shared.data.ContentSource
import com.tiagohs.cinemahistory.shared.data.FonteDeConteudo
import com.tiagohs.cinemahistory.shared.data.RepositorioDeConteudo
import com.tiagohs.cinemahistory.shared.model.Capitulo
import com.tiagohs.cinemahistory.shared.model.Idioma
import com.tiagohs.cinemahistory.shared.model.Resultado
import com.tiagohs.cinemahistory.shared.regras.DestinoDoLink
import com.tiagohs.cinemahistory.shared.regras.Links
import com.tiagohs.cinemahistory.shared.regras.PoliticaDeAnuncios
import com.tiagohs.cinemahistory.shared.regras.Preferencias
import com.tiagohs.cinemahistory.shared.regras.Progresso
import com.tiagohs.cinemahistory.shared.regras.Relogio
import com.tiagohs.cinemahistory.shared.remoto.CacheDoConteudoRemoto
import com.tiagohs.cinemahistory.shared.remoto.ResumoDaSincronizacao
import com.tiagohs.cinemahistory.shared.remoto.SincronizadorDeConteudo
import com.tiagohs.cinemahistory.shared.remoto.criarClienteHttp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath

/**
 * Ponto de entrada do núcleo para o app iOS: cria uma vez (no início do app) e entrega as fachadas de cada área.
 * [embutido] lê a pasta `local` do app; [pastaDoCache] é onde o conteúdo baixado do site fica guardado.
 */
class Nucleo(
    embutido: ContentSource,
    pastaDoCache: String,
    preferencias: Preferencias,
    relogio: Relogio,
    versaoDoApp: Long,
) {
    private val escopo = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val fonte = FonteDeConteudo(embutido, CacheDoConteudoRemoto(pastaDoCache))
    private val repositorio = RepositorioDeConteudo(fonte)

    val inicio = InicioApi(repositorio)
    val historia = HistoriaApi(repositorio, escopo)
    val explorar = ExplorarApi(repositorio)
    val progresso = Progresso(preferencias)
    val anuncios = PoliticaDeAnuncios(preferencias, relogio)
    val sincronizacao = SincronizacaoApi(
        SincronizadorDeConteudo(criarClienteHttp(), pastaDoCache.toPath(), embutido, versaoDoApp),
        relogio,
        escopo,
    )

    /** UC-10 · destino de um link do texto. */
    fun destinoDoLink(endereco: String): DestinoDoLink = Links.destino(endereco)

    /** UC-43 · idioma do conteúdo: escolha do app, depois os idiomas do aparelho; nenhum → inglês. */
    fun idioma(escolhaDoApp: String?, preferidosDoAparelho: List<String>): Idioma = Idioma.resolver(escolhaDoApp, preferidosDoAparelho)
}

/** UC-08, UC-09 · História: sumário (leve) e capítulo (pesado, fora da thread principal, com alça de cancelamento). */
class HistoriaApi internal constructor(private val repositorio: RepositorioDeConteudo, private val escopo: CoroutineScope) {

    fun sumario(idioma: Idioma, era: Int): SumarioResultado = repositorio.sumario(idioma, era).para(::SumarioResultado)

    /** A maior página tem ~1 MB e leva ~35 ms: roda em segundo plano (regra 2). */
    fun carregarCapitulo(idioma: Idioma, era: Int, numero: Int, aoTerminar: (Capitulo) -> Unit, aoFalhar: (String) -> Unit): Cancelavel =
        Cancelavel(escopo.launch {
            when (val r = repositorio.capitulo(idioma, era, numero)) {
                is Resultado.Sucesso -> aoTerminar(r.valor)
                is Resultado.Erro -> aoFalhar(r.mensagem)
                Resultado.Carregando -> Unit
            }
        })
}

/** UC-24, UC-28, UC-29, UC-31 a UC-36 · Explorar. Arquivos pequenos: leitura direta. */
class ExplorarApi internal constructor(private val repositorio: RepositorioDeConteudo) {
    fun glossario(idioma: Idioma) = repositorio.glossario(idioma).para(::GlossarioResultado)
    fun referencias(idioma: Idioma) = repositorio.referencias(idioma).para(::ReferenciasResultado)
    /** Todas as páginas da linha do tempo, na ordem (8 arquivos pequenos). */
    fun linhaDoTempoCompleta(idioma: Idioma): LinhaDoTempoCompletaResultado = when (val ids = repositorio.paginasDaLinhaDoTempo(idioma)) {
        is Resultado.Sucesso -> {
            val paginas = ids.valor.map { repositorio.linhaDoTempo(idioma, it) }
            val erro = paginas.filterIsInstance<Resultado.Erro>().firstOrNull()
            if (erro != null) LinhaDoTempoCompletaResultado(null, erro.mensagem)
            else LinhaDoTempoCompletaResultado(paginas.map { (it as Resultado.Sucesso).valor }, null)
        }
        is Resultado.Erro -> LinhaDoTempoCompletaResultado(null, ids.mensagem)
        Resultado.Carregando -> LinhaDoTempoCompletaResultado(null, "carregando")
    }
    fun linhaDoTempo(idioma: Idioma, id: Int) = repositorio.linhaDoTempo(idioma, id).para(::LinhaDoTempoResultado)
    fun premios(idioma: Idioma) = repositorio.premios(idioma).para(::PremiosResultado)
    fun historiaDoPremio(idioma: Idioma, id: Int) = repositorio.historiaDoPremio(idioma, id).para(::BlocosResultado)
    fun anosDoPremio(idioma: Idioma, idDosIndicados: Int) = repositorio.anosDoPremio(idioma, idDosIndicados).para(::AnosDoPremioResultado)
    fun indicadosDoAno(idioma: Idioma, idDosIndicados: Int, ano: String) = repositorio.indicadosDoAno(idioma, idDosIndicados, ano).para(::IndicadosResultado)
    fun diretores(idioma: Idioma) = repositorio.diretores(idioma).para(::DiretoresResultado)
    fun pessoasEspeciais(idioma: Idioma) = repositorio.pessoasEspeciais(idioma).para(::PessoasEspeciaisResultado)
    fun listasDe1001(idioma: Idioma) = repositorio.listasDe1001(idioma).para(::ListasDe1001Resultado)
    fun citacoesDe1001(idioma: Idioma) = repositorio.citacoesDe1001(idioma).para(::CitacoesResultado)
}

/** UC-33, UC-44 · conteúdo atualizável pelo site (regra 2: alça de cancelamento). */
class SincronizacaoApi internal constructor(
    private val sincronizador: SincronizadorDeConteudo,
    private val relogio: Relogio,
    private val escopo: CoroutineScope,
) {
    /** Sem rede: apaga o cache se o app mudou de versão. Chamar ao abrir o app. */
    fun preparar() = sincronizador.preparar()

    fun sincronizar(idioma: Idioma, forcar: Boolean, aoTerminar: (ResumoDaSincronizacao) -> Unit, aoFalhar: (String) -> Unit): Cancelavel =
        Cancelavel(escopo.launch {
            try {
                aoTerminar(sincronizador.sincronizar(idioma, relogio.agoraMs(), forcar))
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                aoFalhar(e.message ?: "falha na sincronização")
            }
        })
}
