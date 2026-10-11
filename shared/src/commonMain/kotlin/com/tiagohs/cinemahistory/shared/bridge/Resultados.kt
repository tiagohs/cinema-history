package com.tiagohs.cinemahistory.shared.bridge

import com.tiagohs.cinemahistory.shared.model.AnoDoPremio
import com.tiagohs.cinemahistory.shared.model.Bloco
import com.tiagohs.cinemahistory.shared.model.CitacaoDoInicio
import com.tiagohs.cinemahistory.shared.model.Diretor
import com.tiagohs.cinemahistory.shared.model.GrupoDeReferencias
import com.tiagohs.cinemahistory.shared.model.IndicadosDoAno
import com.tiagohs.cinemahistory.shared.model.ItemDoInicio
import com.tiagohs.cinemahistory.shared.model.ItemDoSumario
import com.tiagohs.cinemahistory.shared.model.ListaDe1001
import com.tiagohs.cinemahistory.shared.model.PaginaDaLinhaDoTempo
import com.tiagohs.cinemahistory.shared.model.PessoaEspecial
import com.tiagohs.cinemahistory.shared.model.Premio
import com.tiagohs.cinemahistory.shared.model.Resultado
import com.tiagohs.cinemahistory.shared.model.TermoDoGlossario

/*
 * Resultados concretos para o Swift (regra 1 da ponte): valor ou erro, sem genéricos nem sealed.
 * Exatamente um dos dois é não nulo.
 */

class CitacoesResultado(val citacoes: List<CitacaoDoInicio>?, val erro: String?)
class ItensDoInicioResultado(val itens: List<ItemDoInicio>?, val erro: String?)
class SumarioResultado(val itens: List<ItemDoSumario>?, val erro: String?)
class BlocosResultado(val blocos: List<Bloco>?, val erro: String?)
class GlossarioResultado(val termos: List<TermoDoGlossario>?, val erro: String?)
class ReferenciasResultado(val grupos: List<GrupoDeReferencias>?, val erro: String?)
class LinhaDoTempoResultado(val pagina: PaginaDaLinhaDoTempo?, val erro: String?)
class LinhaDoTempoCompletaResultado(val paginas: List<PaginaDaLinhaDoTempo>?, val erro: String?)
class PremiosResultado(val premios: List<Premio>?, val erro: String?)
class AnosDoPremioResultado(val anos: List<AnoDoPremio>?, val erro: String?)
class IndicadosResultado(val ano: IndicadosDoAno?, val erro: String?)
class DiretoresResultado(val diretores: List<Diretor>?, val erro: String?)
class ListasDe1001Resultado(val listas: List<ListaDe1001>?, val erro: String?)
class PessoasEspeciaisResultado(val pessoas: List<PessoaEspecial>?, val erro: String?)

internal inline fun <T, R> Resultado<T>.para(criar: (T?, String?) -> R): R = when (this) {
    is Resultado.Sucesso -> criar(valor, null)
    is Resultado.Erro -> criar(null, mensagem)
    Resultado.Carregando -> criar(null, "carregando")
}
