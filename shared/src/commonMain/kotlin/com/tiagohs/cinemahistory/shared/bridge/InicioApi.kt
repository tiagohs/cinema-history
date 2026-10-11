package com.tiagohs.cinemahistory.shared.bridge

import com.tiagohs.cinemahistory.shared.data.ContentSource
import com.tiagohs.cinemahistory.shared.domain.ListarEras
import com.tiagohs.cinemahistory.shared.model.Era
import com.tiagohs.cinemahistory.shared.model.Idioma
import com.tiagohs.cinemahistory.shared.model.Resultado

/** Resultado concreto para o Swift: sem genéricos nem sealed (exportação padrão do Kotlin/Native). */
class ErasResultado(val eras: List<Era>?, val erro: String?)

/** Fachada da área Início exposta ao Swift (uma classe por área). */
class InicioApi(fonte: ContentSource) {
    private val listarEras = ListarEras(fonte)

    fun eras(idioma: Idioma): ErasResultado = when (val r = listarEras(idioma)) {
        is Resultado.Sucesso -> ErasResultado(r.valor, null)
        is Resultado.Erro -> ErasResultado(null, r.mensagem)
        Resultado.Carregando -> ErasResultado(null, null)
    }

    fun idiomaDoAparelho(codigo: String): Idioma = Idioma.doAparelho(codigo)
}
