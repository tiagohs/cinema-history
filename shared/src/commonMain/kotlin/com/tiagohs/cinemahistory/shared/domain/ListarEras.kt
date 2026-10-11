package com.tiagohs.cinemahistory.shared.domain

import com.tiagohs.cinemahistory.shared.data.ContentSource
import com.tiagohs.cinemahistory.shared.data.FonteDeConteudo
import com.tiagohs.cinemahistory.shared.data.RepositorioDeConteudo
import com.tiagohs.cinemahistory.shared.model.Era
import com.tiagohs.cinemahistory.shared.model.Idioma
import com.tiagohs.cinemahistory.shared.model.Resultado

/** UC-05 · Explorar uma era: devolve as eras na ordem do JSON, no idioma pedido. */
class ListarEras(private val repositorio: RepositorioDeConteudo) {
    constructor(fonte: ContentSource) : this(RepositorioDeConteudo(FonteDeConteudo(fonte)))

    operator fun invoke(idioma: Idioma): Resultado<List<Era>> = repositorio.eras(idioma)
}
