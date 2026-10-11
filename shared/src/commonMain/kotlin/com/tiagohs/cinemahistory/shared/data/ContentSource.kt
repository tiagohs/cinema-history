package com.tiagohs.cinemahistory.shared.data

/**
 * De onde vem o conteúdo local (a pasta `local/` do Android, embutida no app).
 * No iOS é implementada em Swift sobre o Bundle; nos testes, sobre o sistema de arquivos.
 */
interface ContentSource {
    /** Lê o texto de [caminho] (ex.: "pt/maintopics.json"), ou null se não existir. */
    fun lerTexto(caminho: String): String?
}
