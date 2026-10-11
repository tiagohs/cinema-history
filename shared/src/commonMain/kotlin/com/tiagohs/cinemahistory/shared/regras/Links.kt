package com.tiagohs.cinemahistory.shared.regras

/** Para onde leva um link do texto (UC-10). */
sealed class DestinoDoLink {
    class Filme(val id: Long) : DestinoDoLink()
    class Pessoa(val id: Long) : DestinoDoLink()
    class Online(val url: String) : DestinoDoLink()

    /** Link quebrado no conteúdo (ex.: id vazio): o texto aparece sem link, sem derrubar o app. */
    class Invalido(val original: String) : DestinoDoLink()
}

/**
 * Os links internos do conteúdo têm o formato do Android: `https://_{'type': 'screen', 'id': 11523, 'screen_type': 'person'}`
 * ou `https://_{'type': 'online', 'url': '…'}`. O JSON é "frouxo" (chaves sem aspas, valores sem aspas), então a leitura
 * é por expressões tolerantes, como o Gson leniente do Android — mas link quebrado vira [DestinoDoLink.Invalido] em vez de exceção.
 */
object Links {
    private const val PREFIXO = "https://_"
    private val tipo = Regex("""['"]?type['"]?\s*:\s*['"]?([a-z_]+)""")
    private val id = Regex("""['"]?id['"]?\s*:\s*['"]?(\d+)['"]?\s*[,}]""")
    private val tela = Regex("""['"]?screen_type['"]?\s*:\s*['"]?([a-z_]+)""")
    private val url = Regex("""['"]?url['"]?\s*:\s*['"]([^'"]+)['"]""")
    private val href = Regex("""href\s*=\s*"(https://_\{[^"]*)"""")

    fun ehInterno(endereco: String): Boolean = endereco.startsWith(PREFIXO)

    fun destino(endereco: String): DestinoDoLink {
        if (!ehInterno(endereco)) return DestinoDoLink.Online(endereco)
        val corpo = endereco.removePrefix(PREFIXO)
        return when (tipo.find(corpo)?.groupValues?.get(1)) {
            "online" -> url.find(corpo)?.groupValues?.get(1)?.let { DestinoDoLink.Online(it) } ?: DestinoDoLink.Invalido(endereco)
            "screen" -> {
                val numero = id.find(corpo)?.groupValues?.get(1)?.toLongOrNull()
                when {
                    numero == null -> DestinoDoLink.Invalido(endereco)
                    tela.find(corpo)?.groupValues?.get(1) == "person" -> DestinoDoLink.Pessoa(numero)
                    tela.find(corpo)?.groupValues?.get(1) == "movie" -> DestinoDoLink.Filme(numero)
                    else -> DestinoDoLink.Invalido(endereco)
                }
            }
            else -> DestinoDoLink.Invalido(endereco)
        }
    }

    /** Todos os links internos de um trecho de HTML do conteúdo. */
    fun linksDoHtml(html: String): List<String> = href.findAll(html).map { it.groupValues[1] }.toList()
}
