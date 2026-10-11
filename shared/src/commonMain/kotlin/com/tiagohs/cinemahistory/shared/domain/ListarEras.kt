package com.tiagohs.cinemahistory.shared.domain

import com.tiagohs.cinemahistory.shared.data.ContentSource
import com.tiagohs.cinemahistory.shared.data.MainTopicDto
import com.tiagohs.cinemahistory.shared.data.paraEra
import com.tiagohs.cinemahistory.shared.model.Era
import com.tiagohs.cinemahistory.shared.model.Idioma
import com.tiagohs.cinemahistory.shared.model.Resultado
import kotlinx.serialization.json.Json

/** UC-05 · Explorar uma era: devolve as eras na ordem do JSON, no idioma pedido. */
class ListarEras(private val fonte: ContentSource) {

    operator fun invoke(idioma: Idioma): Resultado<List<Era>> {
        val texto = fonte.lerTexto("${idioma.pasta}/maintopics.json")
            ?: return Resultado.Erro("Conteúdo de ${idioma.pasta} não encontrado")
        return try {
            val eras = json.decodeFromString<List<MainTopicDto>>(texto).mapNotNull { it.paraEra() }
            Resultado.Sucesso(eras)
        } catch (e: Exception) {
            Resultado.Erro("Conteúdo de ${idioma.pasta} inválido: ${e.message}")
        }
    }

    private companion object {
        val json = Json { ignoreUnknownKeys = true }
    }
}
