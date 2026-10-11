package com.tiagohs.cinemahistory.shared

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Garante as regras da ponte Kotlin↔Swift na fachada (`bridge`), que é o que o Swift enxerga.
 * Ver "Regras da ponte" no plano do iOS.
 */
class RegrasDaPonteTest {
    private val fachada: List<File> = generateSequence(File("").absoluteFile) { it.parentFile }
        .map { File(it, "shared/src/commonMain/kotlin/com/tiagohs/cinemahistory/shared/bridge") }
        .first { it.isDirectory }
        .walkTopDown().filter { it.extension == "kt" }.toList()

    private fun linhasPublicas(): List<Pair<String, String>> = fachada.flatMap { arquivo ->
        arquivo.readLines().filterNot { it.trimStart().startsWith("private") || it.trimStart().startsWith("internal") }
            .map { arquivo.name to it }
    }

    @Test
    fun `a fachada existe`() {
        assertTrue(fachada.isNotEmpty())
    }

    @Test
    fun `regra 1 - nada de sealed generico, Int opcional ou colecao de numeros na fachada`() {
        val problemas = linhasPublicas().filter { (_, l) ->
            Regex("""sealed\s+(class|interface)\s+\w+<""").containsMatchIn(l) || Regex(""":\s*Int\?""").containsMatchIn(l) ||
                Regex("""(List|Set|Map)<[^>]*\b(Int|Long|Double|Float|Boolean)\b""").containsMatchIn(l)
        }
        assertTrue(problemas.isEmpty(), "Fora da regra 1: $problemas")
    }

    @Test
    fun `regra 4 - funcao que pode lancar declara Throws`() {
        val problemas = fachada.flatMap { arquivo ->
            val linhas = arquivo.readLines()
            linhas.withIndex().filter { (i, l) ->
                val publica = !l.trimStart().startsWith("private") && !l.trimStart().startsWith("internal")
                publica && (Regex("""\bsuspend\s+fun\b""").containsMatchIn(l) || Regex("""\bfun\b.*=\s*throw\b""").containsMatchIn(l)) &&
                    linhas.getOrNull(i - 1)?.contains("@Throws") != true
            }.map { "${arquivo.name}:${it.index + 1}" }
        }
        assertTrue(problemas.isEmpty(), "Fora da regra 4 (falta @Throws): $problemas")
    }
}
